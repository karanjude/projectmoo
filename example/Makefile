webserver: webserver.o uv/uv.a http_parser/http_parser.o
	g++ -o webserver webserver.o uv/uv.a http_parser/http_parser.o

http_parser/http_parser.o:
	$(MAKE) -C http_parser http_parser.o

uv/uv.a:
	$(MAKE) -C uv

webserver.o:
	g++ -c -Iuv/include -Iuv/test webserver.cc

clean:
	rm webserver webserver.o