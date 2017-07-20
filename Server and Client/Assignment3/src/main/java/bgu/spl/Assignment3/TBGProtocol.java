package bgu.spl.Assignment3;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class TBGProtocol implements ServerProtocol<Command> {
	
	protected boolean quit = false; // added another boolean field that won't allow client to quit unless authorized in the protocol first
	protected boolean inRoom = false; // in a room
	protected boolean loggedIn = false; // logged in
	protected Player _me; // Player class representing the client
	protected boolean QnA = false; // A field that will determine the state of a player in the game, if he can select a response or fool the opponent 

	public TBGProtocol(){
		
	}
	
	/**
	 * Main function in the protocol, will take care of all cases
	 */
	
	public void processMessage(Command msg, ProtocolCallback<Command> callback) {
		String ans;
		Command commandAns;
		if(!validCommand(msg.getCommand())) {//first we need to check if the command given by client is supported by our server
			commandAns = new Command ("SYSMSG " + msg.getCommand() + " UNIDENTIFIED");
			try {
				callback.sendMessage(commandAns);
				return;
			} catch (IOException e) {
				
			}
		}
		if(msg.getCommand().equals("QUIT")) {//a client can always quit the server at any given time,except while he is in the middle of a game. so of the valid commands, it must be addressed first
			Quit(callback);
			return;
		}
		if (!loggedIn) {//first must check that user is already logged in so he can use the servers games
			logIN(msg, callback);
			return;
		}
		else {//user already logged in with a nickname. Our server wont allow any commands until logged in
			if(msg.getCommand().equals("LISTGAMES")){ // allowed to see all available games on server
				this.ListGames();
				return;
			}
			if(!inRoom) {//at this stage the client is logged in but is not inside a room so only the commands JOIN,LISTGAMES are valid in this state
				if(msg.getCommand().equals("JOIN")) { // Allowed to join from this state
					this.Join(msg.getParameter());
					return;
				}
				else { // All other commands at this state are invalid
					commandAns = new Command("SYSMSG " + msg.getCommand() + " REJECTED Please give valid command \n");
					try {
						callback.sendMessage(commandAns);
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else {// client is inside a room 
				if(msg.getCommand().equals("MSG")) { // Allowed to message all other clients in same room
					this.msgRoom("USRMSG " + this._me.getName() + ": " + msg.getParameter());
					return;
				}
				if(!GameManager.getInstance().get_Rooms().get(_me.getRoomName()).isActive()) {//the client is in a room where a game IS NOT underway
					if(msg.getCommand().equals("JOIN")) {
						this.Join(msg.getParameter());
						return;
					}
					if(msg.getCommand().equals("STARTGAME")){
						this.StartGame(msg.getParameter(), this._me.getRoomName());
						return;
					}
					
				}
				else { // the client is in a room where game IS underway. Must write commands that are compatible with the game and nothing else.
					if(msg.getCommand().equals("TXTRESP") && !this.QnA) {
						this.QnA = true;
						this.TXTRESP(msg.getParameter());
						return;
					}
					if(msg.getCommand().equals("SELECTRESP") && QnA) { // SELECTRESP
						int newParameter = -1;
						try {
							newParameter = Integer.parseInt(msg.getParameter());
							if(GameManager.getInstance().get_Rooms().get(_me.getRoomName()).getPlayerAmount() + 1 <= newParameter) {
								commandAns = new Command ("SYSMSG SELECTRESP REJECTED Please send valid answer");
								System.out.println("User sent invalid response");
								try {
									this._me.get_myCallback().sendMessage(commandAns);
									return;
								} catch (IOException f) {
										
								}
							}
						}
						catch (NumberFormatException e) {
							commandAns = new Command ("SYSMSG SELECTRESP REJECTED Please send valid answer");
							System.out.println("User sent invalid response");
							try {
								this._me.get_myCallback().sendMessage(commandAns);
								return;
							} catch (IOException f) {
									
							}
						}
						this.QnA = false;
						this.SELECTRESP(newParameter);
						return;
					}
					else {
						commandAns = new Command ("SYSMSG " + msg.getCommand() + " REJECTED Please send valid command");
						System.out.println("User sent invalid response");
						try {
							this._me.get_myCallback().sendMessage(commandAns);
							return;
						} catch (IOException f) {
								
						}
					}
				}
			}
		}
			
		
	}
	private void SELECTRESP(int parameter) {
		Command commandAns = new Command ("SYSMSG SELECTRESP ACCEPTED");
		try {
			this._me.get_myCallback().sendMessage(commandAns);
		} catch (IOException e) {
				
		}
		GameManager.getInstance().get_Rooms().get(_me.getRoomName()).SELECETRESP(parameter, this._me.getName());
	}

	private void TXTRESP(String parameter) {
		Command commandAns = new Command ("SYSMSG TEXTRESP ACCEPTED");
		try {
			this._me.get_myCallback().sendMessage(commandAns);
		} catch (IOException e) {
				
		}
		GameManager.getInstance().get_Rooms().get(_me.getRoomName()).TXTRESP(parameter, this._me.getName());
	}

	private void StartGame(String game, String room) {
		Command commandAns = null;
		boolean found = false;
		for(int i = 0; i < GameManager.getInstance().getAllGameString().size(); i ++) {
			if (GameManager.getInstance().getAllGameString().get(i).equals(game)) { // We will make sure that the game exists on the server
				this.msgRoom("SYSMSG STARTGAME ACCEPTED");
				GameManager.getInstance().StartGame(game, room);
				found = true;
				return;
			}
		}
		if(!found) // In case the game does no exist
			commandAns = new Command("SYSMSG STARTGAME REJECTED Game not on server.");
		try {
			this._me.get_myCallback().sendMessage(commandAns);
		} catch (IOException e) {
				
		}
	}

	private void msgRoom(String msg) { // MSG the clients in the room
		GameManager.getInstance().msgRoom(this._me.getRoomName(), msg);
	}

	private void Quit(ProtocolCallback<Command> callback) { // QUIT
		Command commandAns;
		if(!this.loggedIn) { // Not logged in -> just leave the server
			commandAns = new Command("SYSMSG QUIT ACCEPTED"); // Accept Quit command
			this.quit = true;
			try {
				callback.sendMessage(commandAns);
				return;
			} catch (IOException e) {
				
			}
		}
		else { // Client is logged in
			if(this.inRoom) { // In a room
				synchronized (GameManager.getInstance().get_Rooms().get(_me.getRoomName())) { // we are avoiding the case in which the player thinks that the game is not active, and once skipping the "if" statement, a game will start and he will be allowed to quit
					if(GameManager.getInstance().get_Rooms().get(_me.getRoomName()).isActive()) {
						commandAns = new Command ("SYSMSG QUIT REJECTED cannot quit in the middle of a game!!");
					}
					else { // in a room but not playing a game
						GameManager.getInstance().deletePlayer(_me);
						commandAns = new Command("SYSMSG QUIT ACCEPTED");
						this.quit = true;
					}
				}
			}
			else { // Not in a room , quit will be accepted
				GameManager.getInstance().deletePlayer(_me);
				commandAns = new Command("SYSMSG QUIT ACCEPTED");
				this.quit = true;
			}
			try {
				callback.sendMessage(commandAns);
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void logIN(Command msg, ProtocolCallback<Command> callback) {
		String ans;
		Command commandAns;
		if (!msg.getCommand().equals("NICK")){ //user not logged in and his message wasn't NICK
			ans = "SYSMSG " + msg.getCommand() + " REJECTED Please give nickname \n";
			System.out.println("SYSMSG " + msg.getCommand() + " REJECTED Please give nickname");
			commandAns = new Command(ans);
		try {
				callback.sendMessage(commandAns);
			} catch (IOException e) {
				
			}
		}	
		else{ // msg command received was NICK
			if (GameManager.getInstance().get_nameDatabase().containsKey(msg.getParameter())){ // check if name already exists in nameDatabase
				ans = "SYSMSG " + msg.getCommand() + " REJECTED nickname already exists \n";
				commandAns = new Command(ans);
				try {
					callback.sendMessage(commandAns);
				} catch (IOException e) {
					//TODO
				}
			}
			else{ // name does not exist in Database
				GameManager.getInstance().get_nameDatabase().put(msg.getParameter(), 1);
				this.loggedIn = true;
				ans = "SYSMSG " + msg.getCommand() + " ACCEPTED \n";
				commandAns = new Command(ans);
				this._me = new Player(msg.getParameter(), this, callback);
				try {
					callback.sendMessage(commandAns);
				} catch (IOException e) {
					//TODO
					}
				}
			}
		}
		
	

	private boolean validCommand(String command) { // Will check to make sure among ALL possible commands
		if(command.equals("NICK") | command.equals("JOIN") | command.equals("MSG") | command.equals("LISTGAMES") | command.equals("STARTGAME") | command.equals("TXTRESP") | command.equals("SELECTRESP") | command.equals("QUIT"))
			return true;
		return false;
	}

	private void ListGames() { // List all games on Server
		String games =  GameManager.getInstance().printGames();
		Command commandAns = new Command("SYSMSG " + games);
		try {
			this._me.get_myCallback().sendMessage(commandAns);
		} catch (IOException e) {
			
		}
	}

	private void Join(String parameter) {
		Command commandAns;
		if(inRoom) { // First we leave the room we are in
			GameManager.getInstance().leaveRoom(this._me);
		}
		if(GameManager.getInstance().get_Rooms().containsKey(parameter)) { // Room already exists
			if(GameManager.getInstance().get_Rooms().get(parameter).addPlayer(_me)){ // Room is not gaming, we can join
				commandAns = new Command("SYSMSG JOIN ACCEPTED \n");
				this.inRoom = true;
				this._me.setRoomName(parameter);
				try {
					this._me.get_myCallback().sendMessage(commandAns);
				} catch (IOException e) {
					
				}
			}
			else { // room is in the middle of a game
				commandAns = new Command("SYSMSG JOIN REJECTED game in progress, you are not in any room. \n"); // Maybe we need to be in a room if switch fails??
				try {
					this._me.get_myCallback().sendMessage(commandAns);
				} catch (IOException e) {
					
				}
			}
		}
		else { // Room doesnt exist - will create it
			GameManager.getInstance().get_Rooms().put(parameter, new GameRoom(parameter, _me));
			commandAns = new Command("SYSMSG JOIN ACCEPTED created new room. \n");
			this.inRoom = true;
			this._me.setRoomName(parameter);
			try {
				this._me.get_myCallback().sendMessage(commandAns);
			} catch (IOException e) {
				
			}
		}	
	}

	public boolean isEnd(Command msg) { 
		if (msg.getCommand().equals("QUIT") && this.quit) // Quit was first accepted by protocol
			return true;
		else 
			return false;
	}

	

}
