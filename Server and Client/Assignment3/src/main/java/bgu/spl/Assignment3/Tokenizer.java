package bgu.spl.Assignment3;

import java.io.*;

/**
 * Tokenizer
 * @author lazardg
 *
 */

public class Tokenizer {
	
	private BufferedReader  _isr; // Reads from input stream
	private PrintWriter _osw; // writes on output stream
	private char delimiter;
	boolean isClosed;
	
	public Tokenizer(BufferedReader r, PrintWriter w, char d) {
		this._isr = r;
		this._osw = w;
		this.delimiter = d;
		this.isClosed = false;
	}
	
	/**
	 * will read the next message from input stream
	 * @return
	 * @throws IOException
	 */
	
	public String nextToken () throws IOException {
		if (isClosed)  // connection is closed
			throw new IOException("connection is closed");
		String ans;
		try {
			int c;
			StringBuilder sb = new StringBuilder();
			while ((c = _isr.read()) != -1){ // read from string. Blocking method. -1 will represent closed connection
				 if (c == delimiter) // if we reached the delimiter, leave the while and return the string
	                    break;
	                else
	                    sb.append((char) c);
			}
	        ans = sb.toString();
	    } 
		catch (IOException e) {
	            isClosed = true;
	            throw new IOException("Connection is dead");
	    }
	    return ans;
			
		
	}
	
	/**
	 *  will send a message back to the client through the output stream
	 * @param msg
	 * @throws IOException
	 */
	
	public void sendMsg(String msg) throws IOException{
		this._osw.println(msg);
	}
	
	public void close(){ 
		try {
			this._isr.close(); // close input
			this._osw.close(); // close output
		} catch (IOException e) {
			
		}
		
		
	}
}
