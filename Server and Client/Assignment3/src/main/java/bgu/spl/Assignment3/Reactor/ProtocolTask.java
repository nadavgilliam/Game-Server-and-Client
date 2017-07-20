package bgu.spl.Assignment3.Reactor;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import bgu.spl.Assignment3.Command;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask<T> implements Runnable {

	private final TBGProtocol4Reactor _protocol;
	private final MessageTokenizer<Command> _tokenizer;
	private final ConnectionHandlerReactor<T> _handler;

	public ProtocolTask(final TBGProtocol4Reactor protocol, final MessageTokenizer<Command> tokenizer, final ConnectionHandlerReactor<T> h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
      // go over all complete messages and process them.
      while (_tokenizer.hasMessage()) {
         Command msg = _tokenizer.nextMessage();
         this._protocol.processMessage(msg, callback -> {
        	 if (callback != null) {
                 try {
                    ByteBuffer bytes = _tokenizer.getBytesForMessage(callback);
                    this._handler.addOutData(bytes);
                 } catch (CharacterCodingException e) { e.printStackTrace(); }
              } 
          });
      }
	}

	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}
}
