
public class Packet007Weather extends Packet {
	
	private int currentWeather;

	public Packet007Weather(byte[] data) {
		super(007);
		this.currentWeather = Integer.parseInt(readData(data));
	}
	
	public Packet007Weather(int currentWeather) {
		super(007);
		this.currentWeather = currentWeather;
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
		return ("007" + this.currentWeather).getBytes();
	}
	
	public int getCurrentWeather() {
		return this.currentWeather;
	}

}
