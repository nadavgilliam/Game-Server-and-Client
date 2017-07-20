package bgu.spl.Assignment3;

import java.io.*;
import java.net.*;

/**
 * The connection handler for the TPC server.
 *
 */

public class ConnectionHandlerTPC implements Runnable {

	//fields
	Socket _client;
	TBGProtocol _protocol;
	Tokenizer _tokenizer;
	
	
	public ConnectionHandlerTPC(Socket s, TBGProtocol p, Tokenizer t) {
		this._client = s;
		this._protocol = p;
		this._tokenizer = t;
		
	}

	public void run() {
		
		String msg = null;
		
		while (true){ // infinite loop. 
			try {
				msg = this._tokenizer.nextToken(); // A blocking function that will receive a FULL MESSAGE from the tokenizer
			} catch (IOException e) {
				
			}
			Command c = new Command(msg); // wrap the message in Command class and send it to be processed by the protocol
			this._protocol.processMessage(c, callback -> { // on complete...
				String ans = callback.unite();
				this._tokenizer.sendMsg(ans); // send back to the tokenizer to send back to client
			});
			
			if (this._protocol.isEnd(c)) // if quit is authorized
			{
				break;
			}
		}
		this._tokenizer.close(); // close tokenizer
		try {
			this._client.close(); // close socket
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
