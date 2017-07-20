package bgu.spl.Assignment3;

/**
 * Player class will wrap around each client joined in the server
 * @author lazardg
 *
 */

public class Player {
	
	private String name;
	private String roomName = null;
	private TBGProtocol _myProtocol; // protocol per client
	private ProtocolCallback _myCallback;
	
	public Player(String name, TBGProtocol p, ProtocolCallback cb) {
		this.name = name;
		this._myCallback = cb;
		this._myProtocol = p;
	}

	public String getName() {
		return name;
	}

	public TBGProtocol get_myProtocol() {
		return _myProtocol;
	}

	public ProtocolCallback get_myCallback() {
		return _myCallback;
	}
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}


}
