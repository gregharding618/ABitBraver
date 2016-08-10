
public class Packet016EndTrade extends Packet {
	
	private String username;

	public Packet016EndTrade(byte[] data) {
		super(016);
		this.username = readData(data);
	}
	
	public Packet016EndTrade(String username) {
		super(016);
		this.username = username;
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
		return ("016" + this.username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
}
