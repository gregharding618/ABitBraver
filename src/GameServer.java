
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {
	
	public static final int PORT = 1331;
	
	private DatagramSocket socket;
	private Game game;
	private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();

	public GameServer(Game game) {
		this.game = game;
		try {
			this.socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized List<PlayerMP> getConnectedPlayer() {
		return this.connectedPlayers;
	}
	
	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				this.socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 3));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet000Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "]" + ((Packet000Login)packet).getUsername() + 
					" has connected.....");
			PlayerMP player = new PlayerMP(((Packet000Login)packet).getUsername(), false, false, 25, 50, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), address, port);
			player.level = this.game.world.startLevel;
			this.addConnection(player, (Packet000Login)packet);
			break;
		case DISCONNECT:
			packet = new Packet001Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "]" + ((Packet001Disconnect)packet).getUsername() + 
					" disconnecting.....");
			this.removeConnection((Packet001Disconnect)packet);
			break;
		case MOVE:
			packet = new Packet002Move(data);
			handleMove((Packet002Move)packet);
			break;
		case CHANGELEVEL:
			packet = new Packet003ChangeLevel(data);
			changeLevel((Packet003ChangeLevel)packet);
			break;
		case HEALTH:
			packet = new Packet004Health(data);
			updateHealth((Packet004Health)packet);
			break;
		case CHAT:
			packet = new Packet005Chat(data);
			updateChat((Packet005Chat)packet);
			break;
		case TIME:
			packet = new Packet006Time(data);
			updateTime((Packet006Time)packet);
			break;
		case WEATHER:
			packet = new Packet007Weather(data);
			updateWeather((Packet007Weather)packet);
			break;
		case PLAYERLEVELS:
			packet = new Packet010PlayerLevels(data);
			updatePlayerLevels((Packet010PlayerLevels)packet);
			break;
		case OVERHEADDAMAGE:
			packet = new Packet011OverheadDamage(data);
			addOverheadDamage((Packet011OverheadDamage)packet);
			break;
		case PINGSERVER:
			packet = new Packet012PingServer(data);
			pingClient((Packet012PingServer)packet, address, port);
			break;
		case TRADEREQUEST:
			packet = new Packet013TradeRequest(data);
			sendTradeRequest((Packet013TradeRequest)packet);
			break;
		case STARTTRADE:
			packet = new Packet014StartTrade(data);
			startTrade((Packet014StartTrade)packet);
			break;
		case DECLINETRADE:
			packet = new Packet015DeclineTrade(data);
			declineTrade((Packet015DeclineTrade)packet);
			break;
		case ENDTRADE:
			packet = new Packet016EndTrade(data);
			endTrade((Packet016EndTrade)packet);
			break;
		case ADDITEMTOTRADE:
			packet = new Packet017AddItemToTrade(data);
			addItemToTrade((Packet017AddItemToTrade)packet);
			break;
		case REMOVEITEMFROMTRADE:
			packet = new Packet020RemoveItemFromTrade(data);
			removeItemFromTrade((Packet020RemoveItemFromTrade)packet);
			break;
		case ACCEPTTRADEREQUEST:
			packet = new Packet021AcceptTradeRequest(data);
			sendAcceptTradeRequest((Packet021AcceptTradeRequest)packet);
			break;
		case FINISHTRADE:
			packet = new Packet022FinishTrade(data);
			finishTrade((Packet022FinishTrade)packet);
			break;
		case UNACCEPTTRADE:
			packet = new Packet023UnacceptTrade(data);
			unacceptTrade((Packet023UnacceptTrade)packet);
			break;
		case SETLASTTARGET:
			packet = new Packet024SetLastTarget(data);
			setTarget((Packet024SetLastTarget)packet);
			break;
		case ADDDROPPEDITEM:
			packet = new Packet025AddDroppedItem(data);
			addDroppedItem((Packet025AddDroppedItem)packet);
			break;
		case REMOVEDROPPEDITEM:
			packet = new Packet026RemoveDroppedItem(data);
			removeDroppedItem((Packet026RemoveDroppedItem)packet);
			break;
		case ADDPROJECTILE:
			packet = new Packet027AddProjectile(data);
			addProjectile((Packet027AddProjectile)packet);
			break;
		case STATS:
			packet = new Packet030Stats(data);
			updateStats((Packet030Stats)packet);
			break;
		}
	}

	public void addConnection(PlayerMP player, Packet000Login packet) {
		boolean alreadyConnected = false;
		for (PlayerMP p : this.connectedPlayers) {
			if (player.name.equalsIgnoreCase(p.name)) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				
				if (p.port == -1) {
					p.port = player.port;
				}
				
				alreadyConnected = true;
			} else {
				sendData(packet.getData(), p.ipAddress, p.port);
				
				Packet000Login packetNew = new Packet000Login(p.name, p.level.name, p.getMapX(), p.getMapY(), this.game.world.time.getTimeOfDay(), this.game.world.weather.getCurrentWeather());
				sendData(packetNew.getData(), player.ipAddress, player.port);
			}
		}
		
		if (!alreadyConnected) {
			this.connectedPlayers.add(player);
		}
	}

	public void removeConnection(Packet001Disconnect packet) {
		this.connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
		packet.writeData(this);
	}
	
	public PlayerMP getPlayerMP(String name) {
		for (PlayerMP player : this.connectedPlayers) {
			if (player.name.equals(name)) {
				return player;
			}
		}
		
		return null;
	}
	
	public int getPlayerMPIndex(String name) {
		int index = 0;
		for (PlayerMP player : this.connectedPlayers) {
			if (player.name.equals(name)) {
				break;
			}
			index++;
		}
		
		return index;
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (PlayerMP p : this.connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}
	
	private void handleMove(Packet002Move packet) {
		boolean foundEntity = false;
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e.name == null) continue;
				
				if (e.name.equals(packet.getName())) {
					e.setMapX(packet.getMapX());
					e.setMapY(packet.getMapY());
					e.setMovingDir(packet.getMovingDir());
					packet.writeData(this);
					foundEntity = true;
					break;
				}
			}
			if (foundEntity) break;
		}
	}
	
	private void changeLevel(Packet003ChangeLevel packet) {
		Level level = null;
		for (Level l : this.game.world.levels) {
			if (l.name.equals(packet.getLevelName())) {
				level = l;
				break;
			}
		}
		
		if (level != null) {
			boolean foundEntity = false;
			for (Level levels : this.game.world.levels) {
				for (Entity e : new ArrayList<>(levels.getEntities())) {
					if (e.name == null) continue;
				
					if (e.name.equals(packet.getName())) {
						e.level.getEntitiesToRemove().add(e);
						e.level = level;
						level.getEntitiesToAdd().add(e);
						e.setMapX(packet.getMapX());
						e.setMapY(packet.getMapY());
						packet.writeData(this);
						foundEntity = true;
						break;
					}
				}
				if (foundEntity) break;
			}
		}
	}

	private void updateHealth(Packet004Health packet) {
		boolean foundEntity = false;
		for (Level levels : this.game.world.levels) {
			for (Entity e : new ArrayList<>(levels.getEntities())) {
				if (e.name == null) continue;
				
				if (e.name.equals(packet.getName())) {
					((Entity_Character)e).currentHealth = packet.getCurrentHealth();
					((Entity_Character)e).maxHealth = packet.getMaxHealth();
					packet.writeData(this);
					foundEntity = true;
					break;
				}
			}
			if (foundEntity) break;
		}
	}

	private void updateChat(Packet005Chat packet) {
		packet.writeData(this);
	}
	
	private void updateTime(Packet006Time packet) {
		packet.writeData(this);
	}
	
	private void updateWeather(Packet007Weather packet) {
		packet.writeData(this);
	}
	
	private void updatePlayerLevels(Packet010PlayerLevels packet) {
		for (PlayerMP player : this.connectedPlayers) {
			if (player.name.equals(packet.getName())) {
				player.overallLevel = packet.getOverallLevel();
				player.melee = packet.getMeleeLevel();
				player.archery = packet.getArcheryLevel();
				player.magic = packet.getMagicLevel();
				player.defense = packet.getDefenseLevel();
				packet.writeData(this);
				break;
			}
		}
	}
	
	private void addOverheadDamage(Packet011OverheadDamage packet) {
		packet.writeData(this);
	}
	
	private void pingClient(Packet012PingServer packet, InetAddress address, int port) {
		packet.writeData(this, address, port);
	}

	public void sendDataToClient(byte[] data, InetAddress address, int port) {
		sendData(data, address, port);
	}
	
	private void sendTradeRequest(Packet013TradeRequest packet) {
		packet.writeData(this);
	}
	
	private void startTrade(Packet014StartTrade packet) {
		packet.writeData(this);
	}
	
	private void declineTrade(Packet015DeclineTrade packet) {
		packet.writeData(this);
	}
	
	private void endTrade(Packet016EndTrade packet) {
		packet.writeData(this);
	}
	
	private void addItemToTrade(Packet017AddItemToTrade packet) {
		packet.writeData(this);
	}
	
	private void removeItemFromTrade(Packet020RemoveItemFromTrade packet) {
		packet.writeData(this);
	}
	
	private void sendAcceptTradeRequest(Packet021AcceptTradeRequest packet) {
		packet.writeData(this);
	}
	
	private void finishTrade(Packet022FinishTrade packet) {
		packet.writeData(this);
	}
	
	private void unacceptTrade(Packet023UnacceptTrade packet) {
		packet.writeData(this);
	}
	
	private void setTarget(Packet024SetLastTarget packet) {
		packet.writeData(this);
	}
	
	private void addDroppedItem(Packet025AddDroppedItem packet) {
		packet.writeData(this);
	}
	
	private void removeDroppedItem(Packet026RemoveDroppedItem packet) {
		packet.writeData(this);
	}
	
	private void addProjectile(Packet027AddProjectile packet) {
		packet.writeData(this);
	}
	
	private void updateStats(Packet030Stats packet) {
		packet.writeData(this);
	}
}
