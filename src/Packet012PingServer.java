import java.net.InetAddress;

public class Packet012PingServer extends Packet {
	
	private String username;

	public Packet012PingServer(byte[] data) {
		super(012);
		this.username = readData(data);
	}
	
	public Packet012PingServer(String username) {
		super(012);
		this.username = username;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server, InetAddress address, int port) {
		server.sendDataToClient(getData(), address, port);
	}

	@Override
	public byte[] getData() {
		return ("012" + this.username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

}
