
public abstract class Packet {
	
	public static enum PacketTypes {
		INVALID				(-01), 
		LOGIN				(000), 
		DISCONNECT			(001), 
		MOVE				(002), 
		CHANGELEVEL			(003), 
		HEALTH				(004), 
		CHAT				(005), 
		TIME				(006), 
		WEATHER				(007), 
		PLAYERLEVELS		(010), 
		OVERHEADDAMAGE		(011), 
		PINGSERVER			(012),
		TRADEREQUEST		(013),
		STARTTRADE			(014),
		DECLINETRADE		(015),
		ENDTRADE			(016),
		ADDITEMTOTRADE		(017),
		REMOVEITEMFROMTRADE	(020),
		ACCEPTTRADEREQUEST	(021),
		FINISHTRADE			(022),
		UNACCEPTTRADE		(023),
		SETLASTTARGET		(024),
		ADDDROPPEDITEM		(025),
		REMOVEDROPPEDITEM	(026),
		ADDPROJECTILE		(027),
		STATS				(030);
		
		private int packetId;
		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}
		
		public int getId() {
			return packetId;
		}
	}

	public byte packetId;
	
	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}
	
	public abstract void writeData(GameClient client);
	
	public abstract void writeData(GameServer server);
	
	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(3);
	}
	
	public abstract byte[] getData();
	
	public static PacketTypes lookupPacket(String packetId) {
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch (NumberFormatException e) {
			return PacketTypes.INVALID;
		}
	}
	
	public static PacketTypes lookupPacket(int id) {
		for (PacketTypes p : PacketTypes.values()) {
			if (Integer.toOctalString(p.getId()).equals(Integer.toString(id))) {
				 return p;
			}
		}
		return PacketTypes.INVALID;
	}
}
