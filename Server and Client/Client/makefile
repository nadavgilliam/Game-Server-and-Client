CFLAGS:=-c -Wall -Weffc++ -g
LDFLAGS:=-lboost_system -lboost_locale -lboost_thread

all: bin/encoder.o BlufferClient
	g++ -o bin/Client bin/encoder.o bin/connectionHandler.o bin/Client.o $(LDFLAGS) 

BlufferClient: bin/connectionHandler.o bin/Client.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp
	
bin/encoder.o: encoder/encoder.cpp
	g++ $(CFLAGS) -o bin/encoder.o encoder/encoder.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
