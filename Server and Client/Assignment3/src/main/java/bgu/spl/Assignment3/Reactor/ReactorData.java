package bgu.spl.Assignment3.Reactor;

import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;

import bgu.spl.Assignment3.TBGProtocolFactory;

/**
 * a simple data structure that hold information about the reactor, including getter methods
 */
public class ReactorData<T> {

    private final ExecutorService _executor;
    private final Selector _selector;
    private final TBGProtocolFactory _protocolMaker;
    private final TokenizerFactory<T> _tokenizerMaker;
    
    public ExecutorService getExecutor() {
        return _executor;
    }

    public Selector getSelector() {
        return _selector;
    }

	public ReactorData(ExecutorService _executor, Selector _selector, TBGProtocolFactory protocol, TokenizerFactory<T> tokenizer) {
		this._executor = _executor;
		this._selector = _selector;
		this._protocolMaker = protocol;
		this._tokenizerMaker = tokenizer;
	}

	public TBGProtocolFactory getProtocolMaker() {
		return _protocolMaker;
	}

	public TokenizerFactory<T> getTokenizerMaker() {
		return _tokenizerMaker;
	}

}
