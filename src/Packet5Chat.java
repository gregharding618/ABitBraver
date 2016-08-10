
public class Packet5Chat extends Packet {
	
	private String text;

	public Packet5Chat(byte[] data) {
		super(5);
		this.text = readData(data);
	}
	
	public Packet5Chat(String text) {
		super(5);
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
		return ("5" + this.text).getBytes();
	}
	
	public String getText() {
		return this.text;
	}

}
