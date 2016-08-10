
public class Packet015DeclineTrade extends Packet {
	
	private String username, otherPlayerName;

	public Packet015DeclineTrade(byte[] data) {
		super(015);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.otherPlayerName = dataArray[1];
	}
	
	public Packet015DeclineTrade(String username, String otherPlayerName) {
		super(015);
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
		return ("015" + this.username + "," + this.otherPlayerName).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getOtherPlayerName() {
		return this.otherPlayerName;
	}

}
