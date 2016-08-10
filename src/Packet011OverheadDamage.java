
public class Packet011OverheadDamage extends Packet {
	
	private String levelName;
	private double mapX, mapY;
	private int red, green, blue, damage;

	public Packet011OverheadDamage(byte[] data) {
		super(011);
		String[] dataArray = readData(data).split(",");
		this.levelName = dataArray[0];
		this.mapX = Double.parseDouble(dataArray[1]);
		this.mapY = Double.parseDouble(dataArray[2]);
		this.red = Integer.parseInt(dataArray[3]);
		this.green = Integer.parseInt(dataArray[4]);
		this.blue = Integer.parseInt(dataArray[5]);
		this.damage = Integer.parseInt(dataArray[6]);
	}
	
	public Packet011OverheadDamage(String levelName, double mapX, double mapY, int red, int green, int blue, int damage) {
		super(011);
		this.levelName = levelName;
		this.mapX = mapX;
		this.mapY = mapY;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.damage = damage;
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
		return ("011" + this.levelName + "," + this.mapX + "," + this.mapY + "," + this.red + "," + this.green + "," + this.blue + "," + this.damage).getBytes();
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
	
	public int getRed() {
		return this.red;
	}
	
	public int getGreen() {
		return this.green;
	}
	
	public int getBlue() {
		return this.blue;
	}
	
	public int getDamage() {
		return this.damage;
	}

}
