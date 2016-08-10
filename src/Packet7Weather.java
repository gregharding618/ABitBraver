
public class Packet7Weather extends Packet {
	
	private int currentWeather;

	public Packet7Weather(byte[] data) {
		super(7);
		this.currentWeather = Integer.parseInt(readData(data));
	}
	
	public Packet7Weather(int currentWeather) {
		super(7);
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
		return ("7" + this.currentWeather).getBytes();
	}
	
	public int getCurrentWeather() {
		return this.currentWeather;
	}

}
