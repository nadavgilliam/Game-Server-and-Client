package bgu.spl.Assignment3;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GameRoom.
 * @author lazardg
 *
 */

public class GameRoom {
	
	private String roomName;
	private boolean isActive = false;
	private Game game;
	private ConcurrentLinkedQueue<Player> _players = new ConcurrentLinkedQueue<Player>(); // players in room. Concurrent so that multiple players can join at once with no problem
	
	public GameRoom(String roomName, Player p) {
		this.roomName = roomName;
		this._players.add(p);
	}
	
	public Object[] getPlayers(){
		return this._players.toArray();
	}
	
	/**
	 * Add player temp to room
	 * @param temp
	 * @return
	 */
	
	public boolean addPlayer(Player temp) {
		if(isActive)
			return false;
		this._players.add(temp);
		return true;
	}
	/**
	 * delete player p from room
	 */

	public void deletePlayer(Player p) {
		this._players.remove(p);
		if(this._players.isEmpty())
			GameManager.getInstance().get_Rooms().remove(this.roomName);
	}

	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * send a msg to all players in the room
	 * @param msg
	 */

	public void msgRoom(String msg) { 
		synchronized (this._players) { // we will lock the players queue since we are turning it into an array which is not thread safe and multiple players might send messages at once or leave room while a message is being sent
			Command commandAns = new Command(msg);
			Object [] players = this._players.toArray();
			for(int i = 0; i < this._players.size(); i++) {
				try {
					((Player) players[i]).get_myCallback().sendMessage(commandAns);
				} catch (IOException e) {
					
				}
			}
		}
	}

	public void StartGame(int j) { // start the jth game on the game list
		synchronized (this) { // Can cause trouble if clients leave and join room while command is being sent
			this.isActive = true;
		}
		
		try {
			this.game = GameManager.getInstance().getAllGames().get(j).newInstance();
		} catch (InstantiationException e) {
			
		} catch (IllegalAccessException e) {
			
		}
		game.StartGame(this._players.size(), this.roomName);
	}

	public void TXTRESP(String parameter, String name) {
		this.game.TXTRESP(parameter, name);
	}

	public void SELECETRESP(int parameter, String name) {
		this.game.SELECTRESP(parameter, name);
	}
	
	public int getPlayerAmount() {
		return this._players.size();
	}
	
	/**
	 * clean room at the end of a game
	 */

	public void clean() {
		this.game = null;
		this.isActive = false;
		
	}
}
