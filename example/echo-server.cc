#include <cstdio>
#include <iostream>

#include <cstdio>
#include <cassert>
#include <cstdlib>

#include "uv.h"
#include "http_parser/http_parser.h"

using namespace std;

typedef struct {
  uv_write_t req;
  uv_buf_t buf;
} write_req_t;

bool server_closed = false;

static uv_loop_t* loop;
static uv_tcp_t server;
static uv_handle_t* server_handle_g;

void on_close(uv_handle_t* handle){
  free(handle);
  cerr << endl << "client disconnected";
}

void on_server_close(uv_handle_t* handle){
  //free(handle);
  cout << endl << "server shutdown";
}

void after_write(uv_write_t* req, int status){
  write_req_t* wr;

  if(status){
    uv_err_t err = uv_last_error(loop);
    fprintf(stderr, "uv_write erro: %s\n", uv_strerror(err));
  }

  wr = (write_req_t*) req;

  //uv_close((uv_handle_t*)wr->req.data, on_close);

  free(wr->buf.base);
  free(wr);
}

void after_shutdown(uv_shutdown_t* req, int status){
  //uv_close((uv_handle_t*)req->data, on_close);
  free(req);
}

void on_read(uv_stream_t* client_handle, ssize_t nread, uv_buf_t buf){
  uv_shutdown_t* req;
  write_req_t *wr;

   if(nread < 0){
    uv_err_t err = uv_last_error(loop);
    if(err.code == UV_EOF){
      free(buf.base);
      // close
      //req = (uv_shutdown_t*) malloc(sizeof(req));
      //req->data = client_handle;
      //uv_shutdown(req, client_handle, after_shutdown);
    }else{
      fprintf(stderr, "read error: %s", uv_strerror(err));
    }
   }else{
    if(server_closed)
      return;
    if(!server_closed){
      wr = (write_req_t*) malloc(sizeof(write_req_t));
      wr->buf = uv_buf_init(buf.base, nread);
      wr->req.data = client_handle;

      uv_write(&wr->req, client_handle, &wr->buf, 1, after_write);
      
    }
   }
}

uv_buf_t on_alloc(uv_handle_t* client_handle, size_t suggested_size) {
  return uv_buf_init((char*)malloc(suggested_size), suggested_size);
}

void on_connect(uv_stream_t* server_handle, int status){
  assert(server_handle == (uv_stream_t*)&server);

  if(status){
    fprintf(stderr, "Connect error %s", uv_err_name(uv_last_error(loop)));
  }
  cerr << endl << "Connection established";

  uv_stream_t * client_handle = (uv_stream_t*) malloc(sizeof(uv_tcp_t));
  int r = uv_tcp_init(loop, (uv_tcp_t*)client_handle);
  client_handle->data = server_handle;

  r = uv_accept(server_handle, client_handle);
  r = uv_read_start(client_handle, on_alloc, on_read);
}

int main(int argc, char** argv){
  loop = uv_default_loop();

  cerr << endl << "Hello world";

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
