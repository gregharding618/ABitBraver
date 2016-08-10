
public class Packet021AcceptTradeRequest extends Packet {
	
	private String username, otherPlayerName;

	public Packet021AcceptTradeRequest(byte[] data) {
		super(021);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.otherPlayerName = dataArray[1];
	}
	
	public Packet021AcceptTradeRequest(String username, String otherPlayerName) {
		super(021);
		this.username = username;
		this.otherPlayerName = otherPlayerName;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("021" + this.username + "," + this.otherPlayerName).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getOtherPlayerName() {
		return this.otherPlayerName;
	}

}
