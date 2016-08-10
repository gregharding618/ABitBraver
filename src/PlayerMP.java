import java.net.InetAddress;

public class PlayerMP extends Player {
	
	public InetAddress ipAddress;
	public int port;
	
	public PlayerMP(String name, boolean hasInput, boolean debugmode, double mapX, double mapY, Item_Equipment helmet, Item_Equipment chest, Item_Equipment legs, Item_Equipment boots, Item_Equipment gloves, Item_Equipment shield, Item_Equipment weapon, Item_Equipment ammo, InetAddress ipAddress, int port) {
		super(name, hasInput, debugmode, mapX, mapY, helmet, chest, legs, boots, gloves, shield, weapon, ammo);
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	@Override
	public void update() {
		super.update();
		
		//Move packet
		//if (this.isMoving) {
		//	Packet002Move packet = new Packet002Move(this.name, this.mapX, this.mapY, this.movingDir);
		//	packet.writeData(Game.socketClient);
		//}
		/////////////////////////
	}

}
