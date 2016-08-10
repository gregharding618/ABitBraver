
public class Packet10PositiveEffectAdd extends Packet {
	
	private String sender, owner, target, effectName;
	private int timeUntilRemoval;

	public Packet10PositiveEffectAdd(byte[] data) {
		super(10);
		String[] dataArray = readData(data).split(",");
		this.sender = dataArray[0];
		this.owner = dataArray[1];
		this.target = dataArray[2];
		this.effectName = dataArray[3];
		this.timeUntilRemoval = Integer.parseInt(dataArray[4]);
	}
	
	public Packet10PositiveEffectAdd(String sender, String owner, String target, String effectName, int timeUntilRemoval) {
		super(10);
		this.sender = sender;
		this.owner = owner;
		this.target = target;
		this.effectName = effectName;
		this.timeUntilRemoval = timeUntilRemoval;
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
		return ("10" + this.sender + "," + this.owner + "," + this.target + "," + this.effectName + "," + this.timeUntilRemoval).getBytes();
	}
	
	public String getSender() {
		return this.sender;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public String getTarget() {
		return this.target;
	}
	
	public String getEffectName() {
		return this.effectName;
	}

	public int getTimeUntilRemoval() {
		return this.timeUntilRemoval;
	}
}
