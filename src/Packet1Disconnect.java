
public class Packet1Disconnect extends Packet {
	
	private String username;

	public Packet1Disconnect(byte[] data) {
		super(1);
		this.username = readData(data);
	}
	
	public Packet1Disconnect(String username) {
		super(1);
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
		return ("1" + this.username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

}
