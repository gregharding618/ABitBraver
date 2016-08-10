import java.util.ArrayList;

public class Packet026RemoveDroppedItem extends Packet {
	
	private String levelName, itemType;
	private double mapX, mapY;
	private ArrayList<String> attributes = new ArrayList<String>();

	public Packet026RemoveDroppedItem(byte[] data) {
		super(026);
		String[] dataArray = readData(data).split(",");
		this.levelName = dataArray[0];
		this.itemType = dataArray[1];
		this.mapX = Double.parseDouble(dataArray[2]);
		this.mapY = Double.parseDouble(dataArray[3]);
		for (int i = 4; i < dataArray.length; i++) {
			this.attributes.add(dataArray[i]);
		}
	}
	
	public Packet026RemoveDroppedItem(String levelName, String itemType, double mapX, double mapY, ArrayList<String> attributes) {
		super(026);
		this.levelName = levelName;
		this.itemType = itemType;
		this.mapX = mapX;
		this.mapY = mapY;
		this.attributes = attributes;
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
		String data = "026" + this.levelName + "," + this.itemType + "," + this.mapX + "," + this.mapY + ",";
		
		for (int i = 0; i < this.attributes.size(); i++) {
			if (i == this.attributes.size() - 1) data += this.attributes.get(i);
			else data += this.attributes.get(i) + ",";
		}
		
		return data.getBytes();
	}
	
	public String getLevelName() {
		return this.levelName;
	}
	
	public String getItemType() {
		return this.itemType;
	}
	
	public double getMapX() {
		return this.mapX;
	}
	
	public double getMapY() {
		return this.mapY;
	}
	
	public ArrayList<String> getAttributes() {
		return this.attributes;
	}

}
