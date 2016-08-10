
public class Packet6Time extends Packet {
	
	private int time;

	public Packet6Time(byte[] data) {
		super(6);
		this.time = Integer.parseInt(readData(data));
	}
	
	public Packet6Time(int time) {
		super(6);
		this.time = time;
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
		return ("6" + this.time).getBytes();
	}
	
	public int getTime() {
		return this.time;
	}

}
