import java.util.ArrayList;

public class Packet017AddItemToTrade extends Packet {
	
	private String 	username, 
					otherPlayerName,
					itemType;
	private ArrayList<String> attributes = new ArrayList<String>();

	public Packet017AddItemToTrade(byte[] data) {
		super(017);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.otherPlayerName = dataArray[1];
		this.itemType = dataArray[2];
		for (int i = 3; i < dataArray.length; i++) {
			this.attributes.add(dataArray[i]);
		}
	}
	
	public Packet017AddItemToTrade(String username, String otherPlayerName, String itemType, ArrayList<String> attributes) {
		super(017);
		this.username = username;
		this.otherPlayerName = otherPlayerName;
		this.itemType = itemType;
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
		String data = "017" + this.username + "," + this.otherPlayerName + "," + this.itemType + ",";
		
		for (int i = 0; i < this.attributes.size(); i++) {
			if (i == this.attributes.size() - 1) data += this.attributes.get(i);
			else data += this.attributes.get(i) + ",";
		}
		
		return data.getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getOtherPlayerName() {
		return this.otherPlayerName;
	}
	
	public String getItemType() {
		return this.itemType;
	}
	
	public ArrayList<String> getAttributes() {
		return this.attributes;
	}

}
