package bgu.spl.Assignment3.Reactor;

public interface TokenizerFactory<T> {
   MessageTokenizer<T> create();
}
