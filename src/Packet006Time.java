
public class Packet006Time extends Packet {
	
	private int time;

	public Packet006Time(byte[] data) {
		super(006);
		this.time = Integer.parseInt(readData(data));
	}
	
	public Packet006Time(int time) {
		super(006);
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
		return ("006" + this.time).getBytes();
	}
	
	public int getTime() {
		return this.time;
	}

}
