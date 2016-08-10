
public class Packet002Move extends Packet {
	
	private String name;
	private double mapX, mapY;
	private int movingDir;

	public Packet002Move(byte[] data) {
		super(002);
		String[] dataArray = readData(data).split(",");
		this.name = dataArray[0];
		this.mapX = Double.parseDouble(dataArray[1]);
		this.mapY = Double.parseDouble(dataArray[2]);
		this.movingDir = Integer.parseInt(dataArray[3]);
	}
	
	public Packet002Move(String name, double mapX, double mapY, int movingDir) {
		super(002);
		this.name = name;
		this.mapX = mapX;
		this.mapY = mapY;
		this.movingDir = movingDir;
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
		return ("002" + this.name + "," + this.mapX + "," + this.mapY + "," + this.movingDir).getBytes();
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getMapX() {
		return this.mapX;
	}
	
	public double getMapY() {
		return this.mapY;
	}
	
	public int getMovingDir() {
		return this.movingDir;
	}

}
