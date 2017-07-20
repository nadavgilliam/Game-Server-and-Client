package bgu.spl.Assignment3;

/**
 * An interface that all games on server must implement
 */

import java.util.concurrent.ConcurrentLinkedQueue;

public interface Game {
	
	public String getName();
	
	public void StartGame(int num, String roomName);
	
	public void TXTRESP(String resp, String name);

	public void SELECTRESP(int parameter, String name);
	
}
