
public class Packet030Stats extends Packet {
	
	private String 	name;
	
	private int maxHit, 
				armor, 
				meleeBoost, 
				archeryBoost,
				magicBoost, 
				tempMeleeBoost, 
				tempArcheryBoost, 
				tempMagicBoost, 
				tempDefenseBoost, 
				tempAccuracyBoost, 
				tempMaxHitBoost;
	
	private double 	accuracy, 
					range;

	public Packet030Stats(byte[] data) {
		super(030);
		String[] dataArray = readData(data).split(",");
		this.name = dataArray[0];
		this.accuracy = Double.parseDouble(dataArray[1]);
		this.maxHit = Integer.parseInt(dataArray[2]);
		this.armor = Integer.parseInt(dataArray[3]);
		this.meleeBoost = Integer.parseInt(dataArray[4]);
		this.archeryBoost = Integer.parseInt(dataArray[5]);
		this.magicBoost = Integer.parseInt(dataArray[6]);
		this.range = Double.parseDouble(dataArray[7]);
		this.tempMeleeBoost = Integer.parseInt(dataArray[8]);
		this.tempArcheryBoost = Integer.parseInt(dataArray[9]);
		this.tempMagicBoost = Integer.parseInt(dataArray[10]);
		this.tempDefenseBoost = Integer.parseInt(dataArray[11]);
		this.tempAccuracyBoost = Integer.parseInt(dataArray[12]);
		this.tempMaxHitBoost = Integer.parseInt(dataArray[13]);
	}
	
	public Packet030Stats(String name, double accuracy, int maxHit, int armor, int meleeBoost, int archeryBoost, int magicBoost, double range, int tempMeleeBoost, int tempArcheryBoost, int tempMagicBoost, int tempDefenseBoost, int tempAccuracyBoost, int tempMaxHitBoost) {
		super(030);
		this.name = name;
		this.accuracy = accuracy;
		this.maxHit = maxHit;
		this.armor = armor;
		this.meleeBoost = meleeBoost;
		this.archeryBoost = archeryBoost;
		this.magicBoost = magicBoost;
		this.range = range;
		this.tempMeleeBoost = tempMeleeBoost;
		this.tempArcheryBoost = tempArcheryBoost;
		this.tempMagicBoost = tempMagicBoost;
		this.tempDefenseBoost = tempDefenseBoost;
		this.tempAccuracyBoost = tempAccuracyBoost;
		this.tempMaxHitBoost = tempMaxHitBoost;
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
		return ("030" + this.name + "," + this.accuracy + "," + this.maxHit + "," + this.armor + "," + this.meleeBoost + "," + this.archeryBoost + "," + this.magicBoost + "," + this.range + "," + this.tempMeleeBoost + "," + this.tempArcheryBoost + "," + this.tempMagicBoost + "," + this.tempDefenseBoost + "," + this.tempAccuracyBoost + "," + this.tempMaxHitBoost).getBytes();
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getAccuracy() {
		return this.accuracy;
	}
	
	public int getMaxHit() {
		return this.maxHit;
	}
	
	public int getArmor() {
		return this.armor;
	}
	
	public int getMeleeBoost() {
		return this.meleeBoost;
	}
	
	public int getArcheryBoost() {
		return this.archeryBoost;
	}
	
	public int getMagicBoost() {
		return this.magicBoost;
	}
	
	public double getRange() {
		return this.range;
	}
	
	public int getTempMeleeBoost() {
		return this.tempMeleeBoost;
	}
	
	public int getTempArcheryBoost() {
		return this.tempArcheryBoost;
	}
	
	public int getTempMagicBoost() {
		return this.tempMagicBoost;
	}
	
	public int getTempDefenseBoost() {
		return this.tempDefenseBoost;
	}
	
	public int getTempAccuracyBoost() {
		return this.tempAccuracyBoost;
	}
	
	public int getTempMaxHitBoost() {
		return this.tempMaxHitBoost;
	}

}
