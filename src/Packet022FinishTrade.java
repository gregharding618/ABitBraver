
public class Packet022FinishTrade extends Packet {
	
	private String username, otherPlayerName;

	public Packet022FinishTrade(byte[] data) {
		super(022);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.otherPlayerName = dataArray[1];
	}
	
	public Packet022FinishTrade(String username, String otherPlayerName) {
		super(022);
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
		return ("022" + this.username + "," + this.otherPlayerName).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getOtherPlayerName() {
		return this.otherPlayerName;
	}
	
}
