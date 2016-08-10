
public class Packet0Login extends Packet {
	
	private String username;
	private String levelName;
	private double mapX, mapY;
	private int time, weather;

	public Packet0Login(byte[] data) {
		super(0);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.levelName = dataArray[1];
		this.mapX = Double.parseDouble(dataArray[2]);
		this.mapY = Double.parseDouble(dataArray[3]);
		this.time = Integer.parseInt(dataArray[4]);
		this.weather = Integer.parseInt(dataArray[5]);
	}
	
	public Packet0Login(String username, String levelName, double mapX, double mapY, int time, int weather) {
		super(0);
		this.username = username;
		this.levelName = levelName;
		this.mapX = mapX;
		this.mapY = mapY;
		this.time = time;
		this.weather = weather;
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
		return ("0" + this.username + "," + this.levelName + "," + this.mapX + "," + this.mapY + "," + this.time + "," + this.weather).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getLevelName() {
		return this.levelName;
	}

	public double getMapX() {
		return this.mapX;
	}
	
	public double getMapY() {
		return this.mapY;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public int getWeather() {
		return this.weather;
	}
}
