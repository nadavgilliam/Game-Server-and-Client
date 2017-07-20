package bgu.spl.Assignment3;

import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.Assignment3.Reactor.TBGProtocol4Reactor;

public class TBGProtocolFactory {

	public TBGProtocolFactory() {
		
	}
	
	public TBGProtocol4Reactor createR(){
		return new TBGProtocol4Reactor();
	}
	
	public TBGProtocol create() {
		return new TBGProtocol();
	}

}
