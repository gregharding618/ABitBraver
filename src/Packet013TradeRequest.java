
public class Packet013TradeRequest extends Packet {
	
	private String username, otherPlayerName;

	public Packet013TradeRequest(byte[] data) {
		super(013);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.otherPlayerName = dataArray[1];
	}
	
	public Packet013TradeRequest(String username, String otherPlayerName) {
		super(013);
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
		return ("013" + this.username + "," + this.otherPlayerName).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getOtherPlayerName() {
		return this.otherPlayerName;
	}

}
