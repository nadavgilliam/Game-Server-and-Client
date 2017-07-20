package bgu.spl.Assignment3;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TPC Server
 * The main thread exists here.
 */

public class TPCServer implements Runnable{
	
	//fields
	private ServerSocket _serverSocket;
	private int _listeningPort;
	private TBGProtocolFactory _protocolFactory;
	private TokenizerFactory _tokenFactory;
	
	public TPCServer(int port, TBGProtocolFactory f, TokenizerFactory tf) {
		this._serverSocket = null;
		this._listeningPort = port;
		this._protocolFactory = f;
		this._tokenFactory = tf;
		
	}

	public static void main(String[] args) {
		GameManager.getInstance().loadGameList();
		int port = Integer.decode(args[0]).intValue();
		TPCServer server = new TPCServer(port, new TBGProtocolFactory(), new TokenizerFactory());
		Thread t1 = new Thread(server);
		t1.start();
		try {
			t1.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Server stopped");
		}
	}

	public void run() {
		try {
			_serverSocket = new ServerSocket(_listeningPort);//open the server to clients on the specified port
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + _listeningPort);
		}
		while (true) // infinite loop the continues to try and accept connections from outer clients
		{ 
			try {
				Socket client = this._serverSocket.accept(); // blocking function that waits until a client trys to connect to server
				System.out.println("someone is here");
				TBGProtocol p = this._protocolFactory.create(); // create a protocol from the factory for each client 
				Tokenizer t = this._tokenFactory.create(new BufferedReader (new InputStreamReader(client.getInputStream(),"UTF-8")),new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8"), true), '\n'); // create a tokenizer for each client connected. add the delimeter decided "\n"
				ConnectionHandlerTPC newClient = new ConnectionHandlerTPC(client, p, t); // create a new connection handler for the client. In the TPC server, each connection handler is a thread of its own. The connection handler will be instantiated with a Socket, protocol and tokenizer
				new Thread(newClient).start(); // start the connection handler thread
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + _listeningPort);
			}
		}
	}
}
