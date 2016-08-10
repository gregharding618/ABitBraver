
public class Packet014StartTrade extends Packet {
	
	private String username, otherPlayerName;

	public Packet014StartTrade(byte[] data) {
		super(014);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.otherPlayerName = dataArray[1];
	}
	
	public Packet014StartTrade(String username, String otherPlayerName) {
		super(014);
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
		return ("014" + this.username + "," + this.otherPlayerName).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getOtherPlayerName() {
		return this.otherPlayerName;
	}

}
