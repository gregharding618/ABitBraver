
public class Packet024SetLastTarget extends Packet {
	
	private String entityName, targetName;
	private int combatTimer;

	public Packet024SetLastTarget(byte[] data) {
		super(024);
		String[] dataArray = readData(data).split(",");
		this.entityName = dataArray[0];
		this.targetName = dataArray[1];
		this.combatTimer = Integer.parseInt(dataArray[2]);
	}
	
	public Packet024SetLastTarget(String entityName, String targetName, int combatTimer) {
		super(024);
		this.entityName = entityName;
		this.targetName = targetName;
		this.combatTimer = combatTimer;
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
		return ("024" + this.entityName + "," + this.targetName + "," + this.combatTimer).getBytes();
	}
	
	public String getEntityName() {
		return this.entityName;
	}
	
	public String getTargetName() {
		return this.targetName;
	}

	public int getCombatTimer() {
		return this.combatTimer;
	}
}
