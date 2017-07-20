package bgu.spl.Assignment3;


/**
 * The GameManger is a Singleton used to manage the games on the server.
 */

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
	
	private List<Class <? extends Game>> allGames = new LinkedList<Class <? extends Game>>(); // Will list all games on server
	private List <String> allGameString = new LinkedList <String>(); // String list that rep the games
	private ConcurrentHashMap<String, Integer> _nameDatabase = new ConcurrentHashMap <String, Integer>(); // Will hold the name database and (0/1) if the name is taken
	private ConcurrentHashMap<String, GameRoom> _Rooms = new ConcurrentHashMap <String, GameRoom>(); // Will hold the room name with corresponding Room object
	
	public List<Class <? extends Game>> getAllGames() {
		return allGames;
	}
	public List<String> getAllGameString() {
		return allGameString;
	}
	public ConcurrentHashMap<String, Integer> get_nameDatabase() {
		return _nameDatabase;
	}
	public ConcurrentHashMap<String, GameRoom> get_Rooms() {
		return _Rooms;
	}
	private static class GameManagerHolder {
		private static GameManager instance = new GameManager();
	}
	private GameManager() {
		
	}
	public static GameManager getInstance() {
		return GameManagerHolder.instance;
	}
	
	/**
	 * Appends all game names to string
	 * @return final string
	 */
	
	public String printGames() {
		StringBuffer ans = new StringBuffer();
		for(int i = 0; i < this.allGameString.size()-1; i++) {
			ans.append(this.allGameString.get(i) + ", ");
		}
		ans.append(this.allGameString.get(this.allGameString.size()-1));
		return ans.toString();
	}
	
	/**
	 * Deletes player p from database and will delete room in the case where p is the only player left in the room
	 * @param p - player
	 */
	
	public void deletePlayer(Player p) {
		this._nameDatabase.remove(p.getName()); // remove from name database
		if(p.getRoomName() != null)
			this._Rooms.get(p.getRoomName()).deletePlayer(p); // delete room if final player
	}
	
	/**
	 * leaves current room p is  in
	 * @param p - player
	 */
	
	public void leaveRoom(Player p) {
		this._Rooms.get(p.getRoomName()).deletePlayer(p);
	}
	/**
	 * sends a msg to all players in roomName
	 * @param roomName - name of room to send messages to players
	 * @param msg - msg to be sent
	 */
	public void msgRoom(String roomName, String msg) {
		this._Rooms.get(roomName).msgRoom(msg);
	}
	
	/**
	 * get a class game from list of games available on server
	 * @param gameName - String
	 * @return - the type of game asked to be played
	 */
	
	private int getGame(String gameName) {
		for(int i = 0; i < this.allGames.size(); i++) {
			if(this.allGameString.get(i).equals(gameName)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Starts the game in room
	 * @param game - String game name
	 * @param room - String room name
	 */
	
	public void StartGame(String game, String room) {
		this._Rooms.get(room).StartGame(this.getGame(game));
	}
	/**
	 * Adds games to gameList
	 * @param string
	 */
	
	public void loadGameList() {
		this.allGames.add((Class<? extends Game>) BLUFFER.class);
		this.allGameString.add("BLUFFER");
	}
		

}
