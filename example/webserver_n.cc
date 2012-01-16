#include <cstdio>
#include <iostream>

#include <cstdio>
#include <cassert>
#include <cstdlib>
#include <sstream>
#include <fstream>

#include "uv.h"
#include "http_parser/http_parser.h"

using namespace std;

#define RESPONSE \
  "HTTP/1.1 200 OK\r\n" \
  "Content-Type: text/html\r\n" \
  "Content-Length: 12\r\n" \
  "\r\n" \
  "Hello World\n"

typedef struct {
  uv_write_t req;
  uv_buf_t buf;
}write_req_t;

typedef struct {
  uv_tcp_t handle;
  http_parser parser;
  write_req_t wr;
  string *file_name;
}client_t;

static uv_buf_t resbuf;
bool server_closed = false;
static http_parser_settings settings;

static uv_loop_t* loop;
static uv_tcp_t server;
static uv_handle_t* server_handle_g;
static int served;


void on_close(uv_handle_t* handle){
  client_t* client = (client_t*) handle->data;
  if(client->file_name)
    delete client->file_name;
  free(client);
  cerr << endl << "client disconnected";
}

void on_server_close(uv_handle_t* handle){
  cout << endl << "server shutdown";
}

void after_shutdown(uv_shutdown_t* req, int status){
  cout << endl << "after shutdown";
}

void on_read(uv_stream_t* client_handle, ssize_t nread, uv_buf_t buf){
  uv_shutdown_t* req;
  client_t* client = (client_t*) client_handle->data;

   if(nread < 0){
    uv_err_t err = uv_last_error(loop);
    if(err.code == UV_EOF){
      uv_close((uv_handle_t*)client_handle, on_close);
      //req = (uv_shutdown_t*) malloc(sizeof(req));
      //uv_shutdown(req, client_handle, after_shutdown);
    }else{
      fprintf(stderr, "read error: %s", uv_strerror(err));
    }
   }else{
     if(server_closed)
       return;


    string GET_STRING = "GET /?file=";
    string HTTP_STRING = " HTTP";
    string s = buf.base;

    //cout << endl << s;

    client->file_name = NULL;

    int pos = s.find(GET_STRING);
    if(pos != string::npos){
      int http_pos = s.find(HTTP_STRING, pos);
      if(http_pos != string::npos){
	string file_name = s.substr(pos+GET_STRING.length(), http_pos-(pos+GET_STRING.length()));
	client->file_name = new string(file_name);
	cout << endl << "got string :" << *(client->file_name);
      }
    }

    size_t parsed = http_parser_execute(&client->parser, &settings, buf.base, nread);

    if(parsed < nread){
      fprintf(stderr, "\nparse error");
      uv_close((uv_handle_t*)&client->handle, on_close);
    }
   }

   free(buf.base);
}

uv_buf_t on_alloc(uv_handle_t* client_handle, size_t suggested_size){
  return uv_buf_init((char*)malloc(suggested_size), suggested_size);
}

void on_connect(uv_stream_t* server_handle, int status){
  assert(server_handle == (uv_stream_t*)&server);

  if(status){
    fprintf(stderr, "Connect error %s", uv_err_name(uv_last_error(loop)));
  }
  cerr << endl << "Connection established";

  client_t* client = (client_t*) malloc(sizeof(client_t));
  uv_tcp_t* client_handle = (uv_tcp_t*)&client->handle;
  int r = uv_tcp_init(loop, client_handle);
  client_handle->data = client;

  r = uv_accept(server_handle, (uv_stream_t*)&client->handle);
  http_parser_init(&client->parser, HTTP_REQUEST);
  client->parser.data = client;

  r = uv_read_start((uv_stream_t*)&client->handle, on_alloc, on_read);
}

void after_write(uv_write_t* req, int status){
  write_req_t* wr;

  if(status){
    uv_err_t err = uv_last_error(loop);
    fprintf(stderr, "uv_write erro: %s\n", uv_strerror(err));
  }

  client_t* client = (client_t*)req->data;
  char* base = client->wr.buf.base;
  //if(base)
  //  free(base);

  uv_close((uv_handle_t*)&client->handle, on_close);
}

string get_file_content(const string& file_name){
  string line;
  ifstream file;
  ostringstream content; 
  file.open(file_name.c_str());
  if(file.is_open()){
    while(!file.eof()){
      file >> line;
      content << line;
    }
  }
  file.close();
  return content.str();
}

string make_http_response(const string& file_name){
  string content = "hello world";

  string header = 
    "HTTP/1.1 200 OK\r\n"			\
    "Content-Type: text/html\r\n"		\
    "Content-Length: ";

  ostringstream ss;
  ss << header << content.length() << "\r\n\r\n" << content;
  return ss.str();
}

int on_headers_complete(http_parser* parser){
  printf("\nGot an http message ");
  client_t* client = (client_t*)parser->data;

  string http_response = make_http_response(*(client->file_name));
  char* response = (char*)malloc(sizeof(char) * http_response.length());
  strcpy(response, http_response.data());
  
  client->wr.buf = uv_buf_init(response, http_response.length());
  client->wr.req.data = client;
  served++;
  fprintf(stderr, "served :%d\n", served);

  int r = uv_write(&client->wr.req, (uv_stream_t*)&client->handle, &client->wr.buf, 1, after_write);
  if(r){
    fprintf(stderr, "\n write error");
    uv_close((uv_handle_t*)&client->handle, on_close);
  }
  return 1;
}

int main(int argc, char** argv){
  loop = uv_default_loop();
  settings.on_headers_complete = on_headers_complete;
  
  resbuf.base = (char*)RESPONSE;
  resbuf.len = sizeof(RESPONSE);


  server_handle_g = (uv_handle_t*)&server;
  int r = uv_tcp_init(loop, &server);
  if(r){
    fprintf(stderr, "Socket error connection \n");
    return 1;
  }

  struct sockaddr_in address = uv_ip4_addr("0.0.0.0", 8000);
  /* bind the server */
  r = uv_tcp_bind(&server, address);
  if(r){
    fprintf(stderr, "bind:\n");
    return -1;
  }

  r = uv_listen((uv_stream_t*)server_handle_g, SOMAXCONN, on_connect);
  if(r){
    fprintf(stderr, "Listen error: %s\n", uv_err_name(uv_last_error(loop)));
    return -1;
  }
  uv_run(loop);
  return 0;
}
