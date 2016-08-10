
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GameClient extends Thread {
	
	public static final int PORT = 1331;
	
	public boolean connectedToServer = false;
	
	private InetAddress ipAddress;
	private DatagramSocket socket;
	private Game game;

	public GameClient(Game game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
					" has joined the game.");
			PlayerMP player = new PlayerMP(((Packet000Login)packet).getUsername(), false, false, ((Packet000Login)packet).getMapX(), ((Packet000Login)packet).getMapY(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), address, port);
			for (Level level : this.game.world.levels) {
				if (level.name.equals(((Packet000Login)packet).getLevelName())) {
					player.level = level;
					break;
				}
			}
			player.level.getEntitiesToAdd().add(player);
			if (Game.socketServer == null) {
				this.game.world.time.setTimeOfDay(((Packet000Login)packet).getTime());
				this.game.world.weather.setCurrentWeather(((Packet000Login)packet).getWeather());
			}
			break;
		case DISCONNECT:
			packet = new Packet001Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "]" + ((Packet001Disconnect)packet).getUsername() + 
					" left the game.");
			Level l = null;
			for (Level level : this.game.world.levels) {
				for (Entity e : new ArrayList<>(level.getEntities())) {
					if (e instanceof PlayerMP && e.name.equals(((Packet001Disconnect)packet).getUsername())) {
						l = level;
						break;
					}
				}
				if (l != null) break;
			}
			l.removePlayerMP(((Packet001Disconnect)packet).getUsername());
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
			setServer((Packet012PingServer)packet);
			break;
		case TRADEREQUEST:
			packet = new Packet013TradeRequest(data);
			addTradeRequest((Packet013TradeRequest)packet);
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

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, PORT);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleMove(Packet002Move packet) {
		if (Game.socketServer != null) return;
		
		boolean foundEntity = false;
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e.name == null) continue;
				
				if (e.name.equals(packet.getName())) {
					e.setMapX(packet.getMapX());
					e.setMapY(packet.getMapY());
					e.setMovingDir(packet.getMovingDir());
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
				if (Game.socketServer != null) {
					for (Entity e: new ArrayList<>(levels.getEntities())) {
						if (e.name != null && e.name.equals(packet.getName())) return;
					}
				}
				for (Entity e : new ArrayList<>(levels.getEntities())) {
					if (e.name == null) continue;
				
					if (e.name.equals(packet.getName())) {
						e.level.getEntitiesToRemove().add(e);
						e.level = level;
						level.getEntitiesToAdd().add(e);
						e.setMapX(packet.getMapX());
						e.setMapY(packet.getMapY());
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
					foundEntity = true;
					break;
				}
			}
			if (foundEntity) break;
		}
	}

	private void updateChat(Packet005Chat packet) {
		this.game.world.getChatMessagesToAdd().add(packet.getText());
	}
	
	private void updateTime(Packet006Time packet) {
		if (Game.socketServer == null) this.game.world.time.setTimeOfDay(packet.getTime());
	}
	
	private void updateWeather(Packet007Weather packet) {
		if (Game.socketServer == null) this.game.world.weather.setCurrentWeather(packet.getCurrentWeather());
	}
	
	private void updatePlayerLevels(Packet010PlayerLevels packet) {
		for (Level level : this.game.world.levels) {
			for (Entity player : new ArrayList<>(level.getEntities())) {
				if (!(player instanceof PlayerMP)) continue;
				
				if (player.name.equals(packet.getName())) {
					((PlayerMP)player).overallLevel = packet.getOverallLevel();
					((PlayerMP)player).melee = packet.getMeleeLevel();
					((PlayerMP)player).archery = packet.getArcheryLevel();
					((PlayerMP)player).magic = packet.getMagicLevel();
					((PlayerMP)player).defense = packet.getDefenseLevel();
					return;
				}
			}
		}
	}
	
	private void addOverheadDamage(Packet011OverheadDamage packet) {
		Level level = null;
		for (Level l : this.game.world.levels) {
			if (packet.getLevelName().equals(l.name)) {
				level = l;
				break;
			}
		}
		
		level.overheadDamageToAdd.add(new OverheadDamage(level, packet.getMapX(), packet.getMapY(), packet.getRed(), packet.getGreen(), packet.getBlue(), packet.getDamage()));
	}
	
	private void setServer(Packet012PingServer packet) {
		this.connectedToServer = true;
	}
	
	private void addTradeRequest(Packet013TradeRequest packet) {
		if (Game.player.name.equals(packet.getOtherPlayerName())) {
			this.game.world.getChatMessagesToAdd().add("::" + packet.getUsername() + " would like to trade with you.");
		}
	}
	
	private void startTrade(Packet014StartTrade packet) {
		PlayerMP player = null, otherPlayer = null;
		Level level = null;
		
		for (Level l : this.game.world.levels) {
			for (Entity e : new ArrayList<>(l.getEntities())) {
				if (e instanceof PlayerMP && e.name.equals(packet.getUsername())) {
					player = (PlayerMP)e;
					level = l;
					break;
				}
			}
			if (player != null) break;
		}
		
		for (Entity e : new ArrayList<>(level.getEntities())) {
			if (e instanceof PlayerMP && e.name.equals(packet.getOtherPlayerName())) {
				otherPlayer = (PlayerMP)e;
				break;
			}
		}
		
		if (player != null && otherPlayer != null) {
			player.currentTrade = new Trade(player, otherPlayer);
			otherPlayer.currentTrade = new Trade(otherPlayer, player);
			
			if (Game.player.equals(player)) {
				for (String s : new ArrayList<>(this.game.world.getChatMessages())) {
					if (s.length() >= otherPlayer.name.length() + 2 && s.subSequence(0, otherPlayer.name.length() + 2).equals("::" + otherPlayer.name)) {
						this.game.world.getChatMessagesToRemove().add(s);
						break;
					}
				}
			}
			
			else if (Game.player.equals(otherPlayer)) {
				for (String s : new ArrayList<>(this.game.world.getChatMessages())) {
					if (s.length() >= player.name.length() + 2 && s.subSequence(0, player.name.length() + 2).equals("::" + player.name)) {
						this.game.world.getChatMessagesToRemove().add(s);
						break;
					}
				}
			}
		}
	}
	
	private void declineTrade(Packet015DeclineTrade packet) {
		PlayerMP player = null, otherPlayer = null;
		Level level = null;
		
		for (Level l : this.game.world.levels) {
			for (Entity e : new ArrayList<>(l.getEntities())) {
				if (e instanceof PlayerMP && e.name.equals(packet.getUsername())) {
					player = (PlayerMP)e;
					level = l;
					break;
				}
			}
			if (player != null) break;
		}
		
		for (Entity e : new ArrayList<>(level.getEntities())) {
			if (e instanceof PlayerMP && e.name.equals(packet.getOtherPlayerName())) {
				otherPlayer = (PlayerMP)e;
				break;
			}
		}
		
		if (player != null && otherPlayer != null) {
			if (Game.player.equals(player) || Game.player.equals(otherPlayer)) {
				for (Item item : new ArrayList<>(Game.player.currentTrade.getPlayerItems())) {
					Game.player.inventory.addItem(item);
				}
			}
			
			player.currentTrade = null;
			otherPlayer.currentTrade = null;
		}
	}
	
	private void endTrade(Packet016EndTrade packet) {
		if (Game.player.name.equals(packet.getUsername())) {
			for (Item item : new ArrayList<>(Game.player.currentTrade.getPlayerItems())) {
				Game.player.inventory.addItem(item);
			}
			Game.player.currentTrade = null;
		}
	}
	
	private void addItemToTrade(Packet017AddItemToTrade packet) {
		Item item = null;
		PlayerMP player = null, otherPlayer = null;
		
		//Create item
		if (packet.getItemType().equalsIgnoreCase("money")) {
			item = new Item_Money(Integer.parseInt(packet.getAttributes().get(0)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("food")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Food(attributes.get(0), attributes.get(1), Boolean.parseBoolean(attributes.get(2)), 
					Integer.parseInt(attributes.get(3)), Integer.parseInt(attributes.get(4)), Integer.parseInt(attributes.get(5)),
					Integer.parseInt(attributes.get(6)), Integer.parseInt(attributes.get(7)), Integer.parseInt(attributes.get(8)), 
					Integer.parseInt(attributes.get(9)), Integer.parseInt(attributes.get(10)), Integer.parseInt(attributes.get(11)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("equipment")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Equipment(attributes.get(0), attributes.get(1), attributes.get(2), Boolean.parseBoolean(attributes.get(3)),
					Integer.parseInt(attributes.get(4)), Boolean.parseBoolean(attributes.get(5)), Boolean.parseBoolean(attributes.get(6)),
					Boolean.parseBoolean(attributes.get(7)), Integer.parseInt(attributes.get(8)), Integer.parseInt(attributes.get(9)), 
					Double.parseDouble(attributes.get(10)), Integer.parseInt(attributes.get(11)), Double.parseDouble(attributes.get(12)), 
					Integer.parseInt(attributes.get(13)), Integer.parseInt(attributes.get(14)), Integer.parseInt(attributes.get(15)), 
					Integer.parseInt(attributes.get(16)), Integer.parseInt(attributes.get(17)));
			
			((Item_Equipment)item).abilities = new Ability[6];
			
			if (attributes.size() > 18) {
				int abilityIndex = 0;
				for (int i = 18; i < attributes.size(); i++) {
					if (attributes.get(i).equalsIgnoreCase("class ability_bloodleech")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BloodLeech();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_bonecrusher")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BoneCrusher();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_rapidheal")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RapidHeal();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_redhot")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RedHot();
						abilityIndex++;
					}
				}
			}
		}
		
		if (item == null) return;
		///////////////////////
		
		//Find players
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e instanceof PlayerMP) {
					
					if (e.name.equals(packet.getUsername())) player = (PlayerMP)e;
					
					else if (e.name.equals(packet.getOtherPlayerName())) otherPlayer = (PlayerMP)e;
					
				} else continue;
				
				if (player != null && otherPlayer != null) break;
			}
			if (player != null && otherPlayer != null) break;
		}
		
		if (player == null || otherPlayer == null) return;
		///////////////////////
		
		//Add item to trade
		player.currentTrade.addPlayerItem(item);
		otherPlayer.currentTrade.addOtherPlayerItem(item);
		player.currentTrade.playerAccepted = false;
		player.currentTrade.otherPlayerAccepted = false;
		otherPlayer.currentTrade.playerAccepted = false;
		otherPlayer.currentTrade.otherPlayerAccepted = false;
		///////////////////////
	}
	
	private void removeItemFromTrade(Packet020RemoveItemFromTrade packet) {
		Item item = null;
		PlayerMP player = null, otherPlayer = null;
		
		//Create item
		if (packet.getItemType().equalsIgnoreCase("money")) {
			item = new Item_Money(Integer.parseInt(packet.getAttributes().get(0)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("food")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Food(attributes.get(0), attributes.get(1), Boolean.parseBoolean(attributes.get(2)), 
					Integer.parseInt(attributes.get(3)), Integer.parseInt(attributes.get(4)), Integer.parseInt(attributes.get(5)),
					Integer.parseInt(attributes.get(6)), Integer.parseInt(attributes.get(7)), Integer.parseInt(attributes.get(8)), 
					Integer.parseInt(attributes.get(9)), Integer.parseInt(attributes.get(10)), Integer.parseInt(attributes.get(11)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("equipment")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Equipment(attributes.get(0), attributes.get(1), attributes.get(2), Boolean.parseBoolean(attributes.get(3)),
					Integer.parseInt(attributes.get(4)), Boolean.parseBoolean(attributes.get(5)), Boolean.parseBoolean(attributes.get(6)),
					Boolean.parseBoolean(attributes.get(7)), Integer.parseInt(attributes.get(8)), Integer.parseInt(attributes.get(9)), 
					Double.parseDouble(attributes.get(10)), Integer.parseInt(attributes.get(11)), Double.parseDouble(attributes.get(12)), 
					Integer.parseInt(attributes.get(13)), Integer.parseInt(attributes.get(14)), Integer.parseInt(attributes.get(15)), 
					Integer.parseInt(attributes.get(16)), Integer.parseInt(attributes.get(17)));
			
			((Item_Equipment)item).abilities = new Ability[6];
			
			if (attributes.size() > 18) {
				int abilityIndex = 0;
				for (int i = 18; i < attributes.size(); i++) {
					if (attributes.get(i).equalsIgnoreCase("class ability_bloodleech")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BloodLeech();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_bonecrusher")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BoneCrusher();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_rapidheal")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RapidHeal();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_redhot")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RedHot();
						abilityIndex++;
					}
				}
			}
		}
		
		if (item == null) return;
		///////////////////////
		
		//Find players
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e instanceof PlayerMP) {
					
					if (e.name.equals(packet.getUsername())) player = (PlayerMP)e;
					
					else if (e.name.equals(packet.getOtherPlayerName())) otherPlayer = (PlayerMP)e;
					
				} else continue;
				
				if (player != null && otherPlayer != null) break;
			}
			if (player != null && otherPlayer != null) break;
		}
		
		if (player == null || otherPlayer == null) return;
		///////////////////////
		
		//Remove item from trade
		for (Item i : new ArrayList<>(player.currentTrade.getPlayerItems())) {
			if (item instanceof Item_Money) {
				if (i instanceof Item_Money) {
					otherPlayer.currentTrade.removeOtherPlayerItem(i);
					i.amount -= item.amount;
					if (i.amount <= 0) {
						player.currentTrade.removePlayerItem(i);
					} else {
						otherPlayer.currentTrade.addOtherPlayerItem(i);
					}
					player.inventory.addItem(item);
					player.currentTrade.playerAccepted = false;
					player.currentTrade.otherPlayerAccepted = false;
					otherPlayer.currentTrade.playerAccepted = false;
					otherPlayer.currentTrade.otherPlayerAccepted = false;
					break;
				}
			}
			
			else if (item instanceof Item_Food) {
				if (i instanceof Item_Food && i.name.equals(item.name) && i.iconImageFile.equals(item.iconImageFile) &&
					i.stackable == item.stackable && i.value == item.value &&
					((Item_Food)i).healAmount == ((Item_Food)item).healAmount && ((Item_Food)i).meleeBoost == ((Item_Food)item).meleeBoost &&
					((Item_Food)i).archeryBoost == ((Item_Food)item).archeryBoost && ((Item_Food)i).magicBoost == ((Item_Food)item).magicBoost &&
					((Item_Food)i).maxHitBoost == ((Item_Food)item).maxHitBoost && ((Item_Food)i).accuracyBoost == ((Item_Food)item).accuracyBoost &&
					((Item_Food)i).defenseBoost == ((Item_Food)item).defenseBoost) {
					
					if (item.stackable) {
						otherPlayer.currentTrade.removeOtherPlayerItem(i);
						i.amount -= item.amount;
						if (i.amount <= 0) {
							player.currentTrade.removePlayerItem(i);
						} else {
							otherPlayer.currentTrade.addOtherPlayerItem(i);
						}
						player.inventory.addItem(item);
						player.currentTrade.playerAccepted = false;
						player.currentTrade.otherPlayerAccepted = false;
						otherPlayer.currentTrade.playerAccepted = false;
						otherPlayer.currentTrade.otherPlayerAccepted = false;
						break;
					} else {
						player.currentTrade.removePlayerItem(i);
						player.inventory.addItem(i);
						otherPlayer.currentTrade.removeOtherPlayerItem(i);
						player.currentTrade.playerAccepted = false;
						player.currentTrade.otherPlayerAccepted = false;
						otherPlayer.currentTrade.playerAccepted = false;
						otherPlayer.currentTrade.otherPlayerAccepted = false;
						break;
					}
				}
			}
			
			else if (item instanceof Item_Equipment) {
				if (i instanceof Item_Equipment && i.iconImageFile.equals(item.iconImageFile) && ((Item_Equipment)i).entityImageFile.equals(((Item_Equipment)item).entityImageFile) &&
					i.name.equals(item.name) && i.stackable == item.stackable && 
					((Item_Equipment)i).isMelee == ((Item_Equipment)item).isMelee && ((Item_Equipment)i).isArchery == ((Item_Equipment)item).isArchery &&
					((Item_Equipment)i).isMagic == ((Item_Equipment)item).isMagic && ((Item_Equipment)i).slot == ((Item_Equipment)item).slot &&
					i.value == item.value && ((Item_Equipment)i).range == ((Item_Equipment)item).range &&
					((Item_Equipment)i).speed == ((Item_Equipment)item).speed && ((Item_Equipment)i).accuracy == ((Item_Equipment)item).accuracy &&
					((Item_Equipment)i).maxHit == ((Item_Equipment)item).maxHit && ((Item_Equipment)i).armor == ((Item_Equipment)item).armor &&
					((Item_Equipment)i).meleeBoost == ((Item_Equipment)item).meleeBoost && ((Item_Equipment)i).archeryBoost == ((Item_Equipment)item).archeryBoost &&
					((Item_Equipment)i).magicBoost == ((Item_Equipment)item).magicBoost) {
					
					boolean shouldContinue = false;
					for (int index = 0; index < ((Item_Equipment)i).abilities.length; index++) {
						if (((Item_Equipment)i).abilities[index] == null && ((Item_Equipment)item).abilities[index] != null ||
							((Item_Equipment)i).abilities[index] != null && ((Item_Equipment)item).abilities[index] == null) {
							shouldContinue = true;
							break;
						}
						
						if (((Item_Equipment)i).abilities[index] != null && ((Item_Equipment)item).abilities[index] != null &&
							!(((Item_Equipment)i).abilities[index].getClass().toString().equals(((Item_Equipment)item).abilities[index].getClass().toString()))) {
							shouldContinue = true;
							break;
						}
					}
					
					if (shouldContinue) continue;
					
					if (item.stackable) {
						otherPlayer.currentTrade.removeOtherPlayerItem(i);
						i.amount -= item.amount;
						if (i.amount <= 0) {
							player.currentTrade.removePlayerItem(i);
						} else {
							otherPlayer.currentTrade.addOtherPlayerItem(i);
						}
						player.inventory.addItem(item);
						player.currentTrade.playerAccepted = false;
						player.currentTrade.otherPlayerAccepted = false;
						otherPlayer.currentTrade.playerAccepted = false;
						otherPlayer.currentTrade.otherPlayerAccepted = false;
						break;
					} else {
						player.currentTrade.removePlayerItem(i);
						player.inventory.addItem(i);
						otherPlayer.currentTrade.removeOtherPlayerItem(i);
						player.currentTrade.playerAccepted = false;
						player.currentTrade.otherPlayerAccepted = false;
						otherPlayer.currentTrade.playerAccepted = false;
						otherPlayer.currentTrade.otherPlayerAccepted = false;
						break;
					}
				}
			}
		}
		///////////////////////
	}
	
	private void sendAcceptTradeRequest(Packet021AcceptTradeRequest packet) {
		PlayerMP player = null, otherPlayer = null;
		
		//Find players
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e instanceof PlayerMP) {
					
					if (e.name.equals(packet.getUsername())) player = (PlayerMP)e;
					
					else if (e.name.equals(packet.getOtherPlayerName())) otherPlayer = (PlayerMP)e;
					
				} else continue;
				
				if (player != null && otherPlayer != null) break;
			}
			if (player != null && otherPlayer != null) break;
		}
		
		if (player == null || otherPlayer == null) return;
		///////////////////////
		
		//Set accept request
		player.currentTrade.playerAccepted = true;
		otherPlayer.currentTrade.otherPlayerAccepted = true;
		///////////////////////
		
		//If both players have accepted, finish trade
		if (player.currentTrade.playerAccepted && player.currentTrade.otherPlayerAccepted) {
			Packet022FinishTrade finishTradePacket = new Packet022FinishTrade(player.name, otherPlayer.name);
			finishTradePacket.writeData(this);
		}
		///////////////////////
	}

	private void finishTrade(Packet022FinishTrade packet) {
		PlayerMP player = null, otherPlayer = null;
		
		//Find players
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e instanceof PlayerMP) {
					
					if (e.name.equals(packet.getUsername())) player = (PlayerMP)e;
					
					else if (e.name.equals(packet.getOtherPlayerName())) otherPlayer = (PlayerMP)e;
					
				} else continue;
				
				if (player != null && otherPlayer != null) break;
			}
			if (player != null && otherPlayer != null) break;
		}
		
		if (player == null || otherPlayer == null) return;
		///////////////////////
		
		if (Game.player.equals(player) || Game.player.equals(otherPlayer)) {
			for (Item item : new ArrayList<>(Game.player.currentTrade.getOtherPlayerItems())) {
				Game.player.inventory.addItem(item);
			}
			Game.player.currentTrade.getOtherPlayerItems().clear();
			Game.player.currentTrade.getPlayerItems().clear();
		}
		
		Game.player.currentTrade.playerAccepted = false;
		Game.player.currentTrade.otherPlayerAccepted = false;
	}
	
	private void unacceptTrade(Packet023UnacceptTrade packet) {
		PlayerMP player = null, otherPlayer = null;
		
		//Find players
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e instanceof PlayerMP) {
					
					if (e.name.equals(packet.getUsername())) player = (PlayerMP)e;
					
					else if (e.name.equals(packet.getOtherPlayerName())) otherPlayer = (PlayerMP)e;
					
				} else continue;
				
				if (player != null && otherPlayer != null) break;
			}
			if (player != null && otherPlayer != null) break;
		}
		
		if (player == null || otherPlayer == null) return;
		///////////////////////
		
		//Set to unaccepted
		player.currentTrade.playerAccepted = false;
		otherPlayer.currentTrade.otherPlayerAccepted = false;
		///////////////////////
	}
	
	private void setTarget(Packet024SetLastTarget packet) {
		Entity_Character entity = null, target = null;
		
		//Find entity and target
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e instanceof Entity_Character) {
					if (e.name.equals(packet.getEntityName())) entity = (Entity_Character)e;
					else if (e.name.equals(packet.getTargetName())) target = (Entity_Character)e;
					
					if (entity != null && target != null) break;
				}
			}
			if (entity != null && target != null) break;
		}
		///////////////////////
		
		//Set target
		if (entity != null && target != null) {
			entity.lastTarget = target;
			entity.isInCombat = true;
			entity.combatTimer = packet.getCombatTimer();
		}
		///////////////////////
	}
	
	private void addDroppedItem(Packet025AddDroppedItem packet) {
		Level level = null;
		Item item = null;
		
		//Find level
		for (Level l : this.game.world.levels) {
			if (l.name.equals(packet.getLevelName())) {
				level = l;
				break;
			}
		}
		///////////////////////
		
		//Create item
		if (packet.getItemType().equalsIgnoreCase("money")) {
			item = new Item_Money(Integer.parseInt(packet.getAttributes().get(0)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("food")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Food(attributes.get(0), attributes.get(1), Boolean.parseBoolean(attributes.get(2)), 
					Integer.parseInt(attributes.get(3)), Integer.parseInt(attributes.get(4)), Integer.parseInt(attributes.get(5)),
					Integer.parseInt(attributes.get(6)), Integer.parseInt(attributes.get(7)), Integer.parseInt(attributes.get(8)), 
					Integer.parseInt(attributes.get(9)), Integer.parseInt(attributes.get(10)), Integer.parseInt(attributes.get(11)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("equipment")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Equipment(attributes.get(0), attributes.get(1), attributes.get(2), Boolean.parseBoolean(attributes.get(3)),
					Integer.parseInt(attributes.get(4)), Boolean.parseBoolean(attributes.get(5)), Boolean.parseBoolean(attributes.get(6)),
					Boolean.parseBoolean(attributes.get(7)), Integer.parseInt(attributes.get(8)), Integer.parseInt(attributes.get(9)), 
					Double.parseDouble(attributes.get(10)), Integer.parseInt(attributes.get(11)), Double.parseDouble(attributes.get(12)), 
					Integer.parseInt(attributes.get(13)), Integer.parseInt(attributes.get(14)), Integer.parseInt(attributes.get(15)), 
					Integer.parseInt(attributes.get(16)), Integer.parseInt(attributes.get(17)));
			
			((Item_Equipment)item).abilities = new Ability[6];
			
			if (attributes.size() > 18) {
				int abilityIndex = 0;
				for (int i = 18; i < attributes.size(); i++) {
					if (attributes.get(i).equalsIgnoreCase("class ability_bloodleech")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BloodLeech();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_bonecrusher")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BoneCrusher();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_rapidheal")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RapidHeal();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_redhot")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RedHot();
						abilityIndex++;
					}
				}
			}
		}
		
		if (item == null) return;
		///////////////////////
		
		//Add dropped item to level
		level.getDroppedItemsToAdd().add(new DroppedItem(level, item, packet.getMapX(), packet.getMapY()));
		///////////////////////
	}
	
	private void removeDroppedItem(Packet026RemoveDroppedItem packet) {
		Level level = null;
		Item item = null;
		
		//Find level
		for (Level l : this.game.world.levels) {
			if (l.name.equals(packet.getLevelName())) {
				level = l;
				break;
			}
		}
		///////////////////////
		
		//Create item
		if (packet.getItemType().equalsIgnoreCase("money")) {
			item = new Item_Money(Integer.parseInt(packet.getAttributes().get(0)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("food")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Food(attributes.get(0), attributes.get(1), Boolean.parseBoolean(attributes.get(2)), 
					Integer.parseInt(attributes.get(3)), Integer.parseInt(attributes.get(4)), Integer.parseInt(attributes.get(5)),
					Integer.parseInt(attributes.get(6)), Integer.parseInt(attributes.get(7)), Integer.parseInt(attributes.get(8)), 
					Integer.parseInt(attributes.get(9)), Integer.parseInt(attributes.get(10)), Integer.parseInt(attributes.get(11)));
		}
		
		else if (packet.getItemType().equalsIgnoreCase("equipment")) {
			ArrayList<String> attributes = packet.getAttributes();
			item = new Item_Equipment(attributes.get(0), attributes.get(1), attributes.get(2), Boolean.parseBoolean(attributes.get(3)),
					Integer.parseInt(attributes.get(4)), Boolean.parseBoolean(attributes.get(5)), Boolean.parseBoolean(attributes.get(6)),
					Boolean.parseBoolean(attributes.get(7)), Integer.parseInt(attributes.get(8)), Integer.parseInt(attributes.get(9)), 
					Double.parseDouble(attributes.get(10)), Integer.parseInt(attributes.get(11)), Double.parseDouble(attributes.get(12)), 
					Integer.parseInt(attributes.get(13)), Integer.parseInt(attributes.get(14)), Integer.parseInt(attributes.get(15)), 
					Integer.parseInt(attributes.get(16)), Integer.parseInt(attributes.get(17)));
			
			((Item_Equipment)item).abilities = new Ability[6];
			
			if (attributes.size() > 18) {
				int abilityIndex = 0;
				for (int i = 18; i < attributes.size(); i++) {
					if (attributes.get(i).equalsIgnoreCase("class ability_bloodleech")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BloodLeech();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_bonecrusher")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_BoneCrusher();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_rapidheal")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RapidHeal();
						abilityIndex++;
					}
					
					else if (attributes.get(i).equalsIgnoreCase("class ability_redhot")) {
						((Item_Equipment)item).abilities[abilityIndex] = new Ability_RedHot();
						abilityIndex++;
					}
				}
			}
		}
		
		if (item == null) return;
		///////////////////////
		
		//Remove dropped item from level
		for (DroppedItem i : new ArrayList<>(level.getDroppedItems())) {
			if (packet.getItemType().equalsIgnoreCase("money")) {
				if (i.item instanceof Item_Money) {
					if (packet.getMapX() == i.mapX && packet.getMapY() == i.mapY &&
						i.item.amount == Integer.parseInt(packet.getAttributes().get(0))) {
						level.getDroppedItemsToRemove().add(i);
						break;
					}
				}
			}
			
			else if (packet.getItemType().equalsIgnoreCase("food")) {
				if (i.item instanceof Item_Food && i.item.name.equals(item.name) && i.item.iconImageFile.equals(item.iconImageFile) &&
					i.item.stackable == item.stackable && i.item.amount == item.amount && i.item.value == item.value &&
					((Item_Food)i.item).healAmount == ((Item_Food)item).healAmount && ((Item_Food)i.item).meleeBoost == ((Item_Food)item).meleeBoost &&
					((Item_Food)i.item).archeryBoost == ((Item_Food)item).archeryBoost && ((Item_Food)i.item).magicBoost == ((Item_Food)item).magicBoost &&
					((Item_Food)i.item).maxHitBoost == ((Item_Food)item).maxHitBoost && ((Item_Food)i.item).accuracyBoost == ((Item_Food)item).accuracyBoost &&
					((Item_Food)i.item).defenseBoost == ((Item_Food)item).defenseBoost) {
					
					level.getDroppedItemsToRemove().add(i);
					break;
				}
			}
			
			else if (packet.getItemType().equalsIgnoreCase("equipment")) {
				if (i.item instanceof Item_Equipment && i.item.iconImageFile.equals(item.iconImageFile) && ((Item_Equipment)i.item).entityImageFile.equals(((Item_Equipment)item).entityImageFile) &&
					i.item.name.equals(item.name) && i.item.stackable == item.stackable && i.item.amount == item.amount && 
					((Item_Equipment)i.item).isMelee == ((Item_Equipment)item).isMelee && ((Item_Equipment)i.item).isArchery == ((Item_Equipment)item).isArchery &&
					((Item_Equipment)i.item).isMagic == ((Item_Equipment)item).isMagic && ((Item_Equipment)i.item).slot == ((Item_Equipment)item).slot &&
					i.item.value == item.value && ((Item_Equipment)i.item).range == ((Item_Equipment)item).range &&
					((Item_Equipment)i.item).speed == ((Item_Equipment)item).speed && ((Item_Equipment)i.item).accuracy == ((Item_Equipment)item).accuracy &&
					((Item_Equipment)i.item).maxHit == ((Item_Equipment)item).maxHit && ((Item_Equipment)i.item).armor == ((Item_Equipment)item).armor &&
					((Item_Equipment)i.item).meleeBoost == ((Item_Equipment)item).meleeBoost && ((Item_Equipment)i.item).archeryBoost == ((Item_Equipment)item).archeryBoost &&
					((Item_Equipment)i.item).magicBoost == ((Item_Equipment)item).magicBoost) {
					
					boolean shouldContinue = false;
					for (int index = 0; index < ((Item_Equipment)i.item).abilities.length; index++) {
						if (((Item_Equipment)i.item).abilities[index] == null && ((Item_Equipment)item).abilities[index] != null ||
							((Item_Equipment)i.item).abilities[index] != null && ((Item_Equipment)item).abilities[index] == null) {
							shouldContinue = true;
							break;
						}
						
						if (((Item_Equipment)i.item).abilities[index] != null && ((Item_Equipment)item).abilities[index] != null &&
							!(((Item_Equipment)i.item).abilities[index].getClass().toString().equals(((Item_Equipment)item).abilities[index].getClass().toString()))) {
							shouldContinue = true;
							break;
						}
					}
					
					if (shouldContinue) continue;
					
					level.getDroppedItemsToRemove().add(i);
					break;
				}
			}
		}
		///////////////////////
	}
	
	private void addProjectile(Packet027AddProjectile packet) {
		Level level = null;
		Entity_Character owner = null;
		
		//Find level
		for (Level l : this.game.world.levels) {
			if (l.name.equals(packet.getLevelName())) {
				level = l;
				break;
			}
		}
		///////////////////////
		
		//Find owner
		for (Entity e : new ArrayList<>(level.getEntities())) {
			if (e.name == null || !(e instanceof Entity_Character)) continue;
			
			if (e.name.equals(packet.getOwnerName())) {
				owner = (Entity_Character)e;
				break;
			}
		}
		///////////////////////
		
		//Create and add projectile
		level.getEntitiesToAdd().add(new Projectile(level, owner, packet.getImageFile(), packet.getMapX(), packet.getMapY(), packet.getSize(), packet.getSpeed(), packet.getRange(), packet.getMovingDir()));
		///////////////////////
	}
	
	private void updateStats(Packet030Stats packet) {
		if (packet.getName().equals(Game.player.name)) return;
		
		Entity_Character entity = null;
		
		//Find entity
		for (Level level : this.game.world.levels) {
			for (Entity e : new ArrayList<>(level.getEntities())) {
				if (e.name == null || !(e instanceof Entity_Character)) continue;
				
				if (e.name.equals(packet.getName())) {
					entity = (Entity_Character)e;
					break;
				}
			}
			if (entity != null) break;
		}
		///////////////////////
		
		if (entity == null) return;
		
		//Update entity's stats
		entity.accuracy = packet.getAccuracy();
		entity.maxHit = packet.getMaxHit();
		entity.armor = packet.getArmor();
		entity.meleeBoost = packet.getMeleeBoost();
		entity.archeryBoost = packet.getArcheryBoost();
		entity.magicBoost = packet.getMagicBoost();
		entity.range = packet.getRange();
		entity.tempMeleeBoost = packet.getTempMeleeBoost();
		entity.tempArcheryBoost = packet.getTempArcheryBoost();
		entity.tempMagicBoost = packet.getTempMagicBoost();
		entity.tempDefenseBoost = packet.getTempDefenseBoost();
		entity.tempAccuracyBoost = packet.getTempAccuracyBoost();
		entity.tempMaxHitBoost = packet.getTempMaxHitBoost();
		///////////////////////
	}
}
