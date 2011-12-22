webserver: webserver.o uv/uv.a
	gcc -o webserver uv/uv.a  webserver.o

uv/uv.a:
	$(MAKE) -C uv

webserver.o:
	gcc -c -Iuv/include -Iuv/test webserver.c	

clean:
	rm webserver webserver.o