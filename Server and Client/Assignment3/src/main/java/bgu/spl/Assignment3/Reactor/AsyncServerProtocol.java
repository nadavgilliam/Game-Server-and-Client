package bgu.spl.Assignment3.Reactor;

import bgu.spl.Assignment3.ProtocolCallback;
import bgu.spl.Assignment3.ServerProtocol;

public interface AsyncServerProtocol<T> extends ServerProtocol<T> {
	
	void processMessage (T msg, ProtocolCallback<T> callback);
	
	boolean isEnd (T msg);
	
	boolean shouldClose();
	
	void connectionTerminated();

}
