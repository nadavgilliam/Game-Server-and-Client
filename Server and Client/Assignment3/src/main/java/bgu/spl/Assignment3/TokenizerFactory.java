package bgu.spl.Assignment3;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class TokenizerFactory {

	public TokenizerFactory() {
		
	}
	
	public Tokenizer create(BufferedReader r, PrintWriter w, char d){
		return new Tokenizer(r,w,d);
		
	}
		
}
