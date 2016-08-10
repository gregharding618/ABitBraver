import java.util.ArrayList;

public class Packet027AddProjectile extends Packet {
	
	private String levelName, ownerName, imageFile;
	private double mapX, mapY, size, speed, range;
	private int movingDir;

	public Packet027AddProjectile(byte[] data) {
		super(027);
		String[] dataArray = readData(data).split(",");
		this.levelName = dataArray[0];
		this.ownerName = dataArray[1];
		this.imageFile = dataArray[2];
		this.mapX = Double.parseDouble(dataArray[3]);
		this.mapY = Double.parseDouble(dataArray[4]);
		this.size = Double.parseDouble(dataArray[5]);
		this.speed = Double.parseDouble(dataArray[6]);
		this.range = Double.parseDouble(dataArray[7]);
		this.movingDir = Integer.parseInt(dataArray[8]);
	}
	
	public Packet027AddProjectile(String levelName, String ownerName, String imageFile, double mapX, double mapY, double size, double speed, double range, int movingDir) {
		super(027);
		this.levelName = levelName;
		this.ownerName = ownerName;
		this.imageFile = imageFile;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = size;
		this.speed = speed;
		this.range = range;
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
		return ("027" + this.levelName + "," + this.ownerName + "," + this.imageFile + "," + this.mapX + "," + this.mapY + "," + this.size + "," + this.speed + "," + this.range + "," + this.movingDir).getBytes();
	}
	
	public String getLevelName() {
		return this.levelName;
	}
	
	public String getOwnerName() {
		return this.ownerName;
	}
	
	public String getImageFile() {
		return this.imageFile;
	}
	
	public double getMapX() {
		return this.mapX;
	}
	
	public double getMapY() {
		return this.mapY;
	}
	
	public double getSize() {
		return this.size;
	}
	
	public double getSpeed() {
		return this.speed;
	}
	
	public double getRange() {
		return this.range;
	}
	
	public int getMovingDir() {
		return this.movingDir;
	}

}
