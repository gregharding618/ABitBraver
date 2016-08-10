
public class Packet4Health extends Packet {
	
	private String name;
	private int currentHealth, maxHealth;

	public Packet4Health(byte[] data) {
		super(4);
		String[] dataArray = readData(data).split(",");
		this.name = dataArray[0];
		this.currentHealth = Integer.parseInt(dataArray[1]);
		this.maxHealth = Integer.parseInt(dataArray[2]);
	}
	
	public Packet4Health(String name, int currentHealth, int maxHealth) {
		super(4);
		this.name = name;
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
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
		return ("4" + this.name + "," + this.currentHealth + "," + this.maxHealth).getBytes();
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getCurrentHealth() {
		return this.currentHealth;
	}
	
	public int getMaxHealth() {
		return this.maxHealth;
	}

}
