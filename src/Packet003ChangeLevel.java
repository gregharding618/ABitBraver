
public class Packet003ChangeLevel extends Packet {
	
	private String name, levelName;
	private double mapX, mapY;

	public Packet003ChangeLevel(byte[] data) {
		super(003);
		String[] dataArray = readData(data).split(",");
		this.name = dataArray[0];
		this.levelName = dataArray[1];
		this.mapX = Double.parseDouble(dataArray[2]);
		this.mapY = Double.parseDouble(dataArray[3]);
	}
	
	public Packet003ChangeLevel(String name, String levelName, double mapX, double mapY) {
		super(003);
		this.name = name;
		this.levelName = levelName;
		this.mapX = mapX;
		this.mapY = mapY;
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
		return ("003" + this.name + "," + this.levelName + "," + this.mapX + "," + this.mapY).getBytes();
	}
	
	public String getName() {
		return this.name;
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
}
