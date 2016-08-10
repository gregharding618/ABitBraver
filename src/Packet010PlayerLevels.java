
public class Packet010PlayerLevels extends Packet {
	
	private String name;
	private int overallLevel, meleeLevel, archeryLevel, magicLevel, defenseLevel;

	public Packet010PlayerLevels(byte[] data) {
		super(010);
		String[] dataArray = readData(data).split(",");
		this.name = dataArray[0];
		this.overallLevel = Integer.parseInt(dataArray[1]);
		this.meleeLevel = Integer.parseInt(dataArray[2]);
		this.archeryLevel = Integer.parseInt(dataArray[3]);
		this.magicLevel = Integer.parseInt(dataArray[4]);
		this.defenseLevel = Integer.parseInt(dataArray[5]);
	}
	
	public Packet010PlayerLevels(String name, int overallLevel, int meleeLevel, int archeryLevel, int magicLevel, int defenseLevel) {
		super(010);
		this.name = name;
		this.overallLevel = overallLevel;
		this.meleeLevel = meleeLevel;
		this.archeryLevel = archeryLevel;
		this.magicLevel = magicLevel;
		this.defenseLevel = defenseLevel;
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
		return ("010" + this.name + "," + this.overallLevel + "," + this.meleeLevel + "," + this.archeryLevel + "," + this.magicLevel + "," + this.defenseLevel).getBytes();
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getOverallLevel() {
		return this.overallLevel;
	}
	
	public int getMeleeLevel() {
		return this.meleeLevel;
	}
	
	public int getArcheryLevel() {
		return this.archeryLevel;
	}
	
	public int getMagicLevel() {
		return this.magicLevel;
	}
	
	public int getDefenseLevel() {
		return this.defenseLevel;
	}

}
