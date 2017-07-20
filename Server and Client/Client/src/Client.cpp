#include <stdlib.h>
#include <boost/locale.hpp>
#include "connectionHandler.h"
#include <boost/thread.hpp>

bool shouldEnd = false;
bool shouldEndForSure = false;

void Sender (ConnectionHandler* connectionHandler){
	const short bufsize = 1024;
        char buf[bufsize];
        while(!shouldEndForSure){
	  if(!shouldEnd){
       		std::cin.getline(buf, bufsize);
		std::string line(buf);
        	if ((!connectionHandler->sendLine(line))) {
		  std::cout << "Disconnected. Exiting...\n" << std::endl;
		  break;
        	}
		if (line == "QUIT") {
		  shouldEnd = true;
		}	
	  }
    	}
}

void Reciver (ConnectionHandler* connectionHandler){
        std::string answer;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
    while (1){
        
        if (!connectionHandler->getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        
		int len=answer.length();
		// A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
		// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        answer.resize(len-1);
        std::cout << "Recived from server: " << answer << std::endl;
	
	if (answer == "SYSMSG QUIT REJECTED cannot quit in the middle of a game!!") {
            shouldEnd = false;
        }
        if (answer == "SYSMSG QUIT ACCEPTED") {
            std::cout << "Exiting...\n" << std::endl;
	    shouldEndForSure = true;
            break;
        }
        
    }
}


int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);


    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
	
	boost::thread sender(Sender, &connectionHandler);
	boost::thread reciver(Reciver, &connectionHandler);
	//From here we will see the rest of the ehco client implementation:
  	 
        

    

    sender.join();
    reciver.join();
    
   // connectionHandler.close();

    return 0;
}



