
public class Packet001Disconnect extends Packet {
	
	private String username;

	public Packet001Disconnect(byte[] data) {
		super(001);
		this.username = readData(data);
	}
	
	public Packet001Disconnect(String username) {
		super(001);
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
		return ("001" + this.username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

}
