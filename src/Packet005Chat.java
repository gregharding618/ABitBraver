
public class Packet005Chat extends Packet {
	
	private String text;

	public Packet005Chat(byte[] data) {
		super(005);
		this.text = readData(data);
	}
	
	public Packet005Chat(String text) {
		super(005);
		this.text = text;
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
		return ("005" + this.text).getBytes();
	}
	
	public String getText() {
		return this.text;
	}

}
