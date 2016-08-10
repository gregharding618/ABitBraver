import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

//import javafx.animation.Animation;

public class Player extends Entity_Character implements Comparable<Entity>, Serializable {
	
	private static final long serialVersionUID = -2108304789188507108L;
	public static final int MAXIMUM_TRADE_DISTANCE = 12;
	
	public EditorController ec;
	public GUI_MapEditor_V2 editorGUI;
	
	public int buttonDelay = 0, clickDelay = 0, levelTransitionDelay = 0;
	public boolean inventoryOpen = false, mapOpen = false;
	public boolean debugmode;
	public boolean hasInput;
	public boolean showHealth = true, showMinimap = true, showChat = true, showAbilities = true, showEbar = true;
	public int clickDelayAmount = 16;
	public int showLocationNameTimer = 0;
	public transient int[] clickCoodinates;
	public final int locationNameTimerAmount = 350;
	public final int buttonDelayAmount = 28;
	public final int levelTransitionDelayAmount = 1000;
	public double meleeXpPercent = 0, archeryXpPercent = 0, magicXpPercent = 0, defenseXpPercent = 0;
	public int energyToReduce = 0;
	public int energyRed = 89, energyGreen = 222, energyBlue = 77;
	public transient Dialogue currentDialogue;
	public transient Shop currentShop;
	public transient Trade currentTrade;
	
	protected int dialogueIndex = 0;
	protected transient Entity_Character examinedEntity;
	protected boolean showControls = false;
	protected transient LevelConnector nearbyLevelConnector;
	protected transient DroppedItem nearbyItem;
	protected transient PlayerMP nearbyPlayer;
	protected MiniMap minimap;
	protected LevelMap levelMap;
	protected Quests quests;
	
	private int meleeLevelUpTimer = 0, archeryLevelUpTimer = 0, magicLevelUpTimer = 0, defenseLevelUpTimer = 0, levelUpDelayAmount = 400;
	private int selectedAbilityGlow = 245;
	private boolean abilityGlowDown = true;
	private static final DecimalFormat df = new DecimalFormat("#.##");
	private transient String chat = "";
	private boolean typing = false;
	private boolean canStartTrade = false;
	private transient String playerToTrade = "";
	
	public Player(String name, boolean hasInput, boolean debugmode, double mapX, double mapY, Item_Equipment helmet, Item_Equipment chest, Item_Equipment legs, Item_Equipment boots, Item_Equipment gloves, Item_Equipment shield, Item_Equipment weapon, Item_Equipment ammo) {
		//Debugging?
		this.debugmode = debugmode;
		//////////////////////
		
		//Location in level
		setMapX(mapX);
		setMapY(mapY);
		//////////////////////
		
		//hasInput - true = player, false = multiplayer copy
		this.hasInput = hasInput;
		//////////////////////
		
		//Collision box
		this.maxX = 4;
		this.minX = 4;
		this.maxY = 2;
		this.minY = 4;
		//////////////////////
		
		//Save checkpoint location
		this.checkpointMapX = mapX;
		this.checkpointMapY = mapY;
		//////////////////////
		
		//Quest info
		this.quests = new Quests(this);
		//////////////////////
		
		this.speed = 0.55;
		this.maxHealth = 100;
		this.currentHealth = this.maxHealth;
		this.faction = "Player";
		this.name = name;
		this.canAttack = true;
		this.canBeAttacked = true;
		this.inventory = new Inventory(this, 50);
		if (this.hasInput) this.miniMapColor = StdDraw.WHITE;
		else this.miniMapColor = StdDraw.CYAN;
		
		//Worn equipment
		this.helmet = helmet;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		this.gloves = gloves;
		this.shield = shield;
		this.weapon = weapon;
		this.ammo = ammo;
		//////////////////////
		
		//Animations Test
		this.animation = new Animations(this, "Hand Axe");
		////////////////////////////////////////////////////
		
		if (this.debugmode) {
			this.ec = new EditorController(this);
			this.editorGUI = new GUI_MapEditor_V2(this);
			this.isSolid = false;
		}
	}

	@Override
	public void update() {
		if (this.hasInput) {
			//Update editorGUI
			if (this.editorGUI != null) this.editorGUI.update();
			/////////////////////
			
			// Animations
			if (this.isInWeaponAnimation == true || this.isInWalkLeftAnimation == true) this.animation.updateAnimation();
			////////////////////////////
			
			//Update hidden and shown gui objects
			updateGUIshownStatus();
			////////////////////////////

			if (this.checkpointLevel == null) {
				this.checkpointLevel = this.level;
			}
			
			if (this.levelTransitionDelay > 0) {
				updateLevelTransition();
			}

			else if (this.currentShop != null) {
				this.canAttack = false;
				this.inventoryOpen = false;
				this.mapOpen = false;
				this.currentDialogue = null;
				this.currentShop.update();
			}

			else if (this.currentDialogue != null) {
				this.canAttack = false;
				this.inventoryOpen = false;
				this.mapOpen = false;
				this.currentShop = null;
				this.currentDialogue.update();
				checkOtherInput();
			}

			else if (this.inventoryOpen) {
				this.canAttack = false;
				this.mapOpen = false;
				this.currentDialogue = null;
				this.currentShop = null;
				this.inventory.update();
			}

			else if (this.mapOpen) {
				this.canAttack = false;
				this.inventoryOpen = false;
				this.currentDialogue = null;
				this.currentShop = null;
				if (this.levelMap != null) this.levelMap.update();
			}
			
			else if (this.typing) {
				this.canAttack = false;
				checkChatInput();
			}
			
			else if (this.currentTrade != null) {
				this.canAttack = false;
				this.currentTrade.update();
			}

			else {
				move();
				updateScreenPosition();
				checkLevelConnectors();
				checkNearbyItems();
				checkNearbyPlayers();
				checkOtherInput();
			}

			updateLighting();

			updateLevels();

			// Minimap and level map stuff
			if (this.level.firstRender) {
				this.minimap = new MiniMap(this.level, this);
				this.levelMap = new LevelMap(this);
			}

			if (this.minimap != null) this.minimap.update();
			/////////////////////
		}
	}

	private void updateGUIshownStatus() {
		if (PauseMenu.checkBoxes[0] == 1) this.showHealth = true;
		else this.showHealth = false;
		
		if (PauseMenu.checkBoxes[1] == 1) this.showMinimap = true;
		else this.showMinimap = false;
		
		if (PauseMenu.checkBoxes[2] == 1) this.showChat = true;
		else this.showChat = false;
		
		if (PauseMenu.checkBoxes[3] == 1) this.showAbilities = true;
		else this.showAbilities = false;
		
		if (PauseMenu.checkBoxes[4] == 1) this.showEbar = true;
		else this.showEbar = false;
	}

	private void checkChatInput() {
		//Enter - submit text
		if (StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
			this.typing = false;
			if (!(this.chat.equals("") || this.chat.substring(0, 2).equals("::"))) {
				Packet005Chat packet = new Packet005Chat(this.name + ": " + this.chat);
				if (Game.socketServer != null ) packet.writeData(Game.socketServer);
				else packet.writeData(Game.socketClient);
			}
			this.chat = "";
			this.buttonDelay = this.buttonDelayAmount;
		}
		/////////////////////
		
		//Backspace - remove character
		else if (StdDraw.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
			if (this.buttonDelay == 0) {
				if (this.chat.length() <= 1) this.chat = "";
				else this.chat = this.chat.substring(0, this.chat.length() - 1);
				this.buttonDelay = this.buttonDelayAmount / 2;
			}
			StdDraw.keysTyped.clear();
		}
		/////////////////////
		
		//Any other legal characters to put into chat
		else {
			if (this.chat.length() >= 160) return;
			
			if (StdDraw.hasNextKeyTyped()) {
				char c = StdDraw.nextKeyTyped();
			
				this.chat += c;
			}
		}
		/////////////////////
	}

	private void updateLevelTransition() {
		this.inventoryOpen = false;
		this.canAttack = false;
			
		if (this.levelTransitionDelay < this.levelTransitionDelayAmount / 2) {
			this.canAttack = true;
				
			if (this.levelTransitionDelay > (this.levelTransitionDelayAmount / 2 - 10)) {
				updateScreenPosition();
			}
		}
			
		else if (this.nearbyLevelConnector != null && !this.level.equals(this.nearbyLevelConnector.destination) && this.levelTransitionDelay <= this.levelTransitionDelayAmount / 2) {
			changeLevel(this.nearbyLevelConnector);
			this.nearbyLevelConnector = null;
		}
	}

	private void updateLighting() {
		int lightToUse = this.level.world.time.getLightLevel();
		
		for (Entity e : this.level.getEntities()) {
			if (e instanceof LevelObject && ((LevelObject)e).isLightSource) {
				double distance = GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY());
				
				if (distance <= ((LevelObject)e).lightRange) {
					int thisLight = (int) (this.level.world.time.getLightLevel() - ((LevelObject)e).lightStrength * (((LevelObject)e).lightRange - distance));
					if (thisLight < lightToUse) lightToUse = thisLight;
				}
			}
		}
		
		this.level.world.time.setLightLevel(lightToUse);
	}

	private void updateScreenPosition() {
		if (this.level.xMax > 101) {
			if (this.getMapX() < 50) this.x = this.getMapX();
			else if (this.getMapX() > this.level.xMax - 50) this.x = 100 - ((this.level.xMax + (this.level.tileSize * 2)) - this.getMapX());
			else this.x = 50;
		} else {
			this.x = 50;
		}

		if (this.level.yMax > 101) {
			if (this.getMapY() < 50) this.y = this.getMapY();
			else if (this.getMapY() > this.level.yMax - 50) this.y = 100 - ((this.level.yMax + (this.level.tileSize * 2)) - this.getMapY());
			else this.y = 50;
		} else {
			this.y = 50;
		}
	}

	public void updateLevels() {
		boolean sendPackets = false;
		
		while (this.meleeXpPercent >= 100) {
			this.meleeXpPercent = Math.abs(100 - this.meleeXpPercent);
			this.melee++;
			this.maxHealth++;
			this.currentHealth++;
			this.meleeLevelUpTimer = this.levelUpDelayAmount;
			sendPackets = true;
		}
		
		while (this.archeryXpPercent >= 100) {
			this.archeryXpPercent = Math.abs(100 - this.archeryXpPercent);
			this.archery++;
			this.maxHealth++;
			this.currentHealth++;
			this.archeryLevelUpTimer = this.levelUpDelayAmount;
			sendPackets = true;
		}
		
		while (this.magicXpPercent >= 100) {
			this.magicXpPercent = Math.abs(100 - this.magicXpPercent);
			this.magic++;
			this.maxHealth++;
			this.currentHealth++;
			this.magicLevelUpTimer = this.levelUpDelayAmount;
			sendPackets = true;
		}
		
		while (this.defenseXpPercent >= 100) {
			this.defenseXpPercent = Math.abs(100 - this.defenseXpPercent);
			this.defense++;
			this.maxHealth++;
			this.currentHealth++;
			this.defenseLevelUpTimer = this.levelUpDelayAmount;
			sendPackets = true;
		}
		
		if (sendPackets) {
			Packet004Health healthPacket = new Packet004Health(this.name, this.currentHealth, this.maxHealth);
			healthPacket.writeData(Game.socketClient);
			
			Packet010PlayerLevels levelsPacket = new Packet010PlayerLevels(this.name, this.overallLevel, this.melee, this.archery, this.magic, this.defense);
			levelsPacket.writeData(Game.socketClient);
		}
	}

	@Override
	public void render() {
		//Handles players that are not the input player
		if (!this.hasInput) {
			if (this.level.equals(this.level.world.currentPlayer.level)) {
				Player realPlayer = this.level.world.currentPlayer;
			
				//Set x and y relative to actual player
				this.x = realPlayer.x - (realPlayer.getMapX() - this.getMapX());
				this.y = realPlayer.y - (realPlayer.getMapY() - this.getMapY());
				if (this.x < -8 || this.x > 108 || this.y < -8 || this.y > 108) return;
				//////////////////
				
				//Render username
				StdDraw.setFont(new Font("Arial", Font.PLAIN, 23));
				if (StdDraw.mouseX() >= this.x - this.minX && StdDraw.mouseX() <= this.x + this.maxX &&
					StdDraw.mouseY() >= this.y - this.minY && StdDraw.mouseY() <= this.y + (this.maxY * 2)) {
					
					StdDraw.setPenColor(new Color(255, 255, 255));
				} else {
					StdDraw.setPenColor(new Color(255, 255, 255, 85));
				}
				
				StdDraw.text(this.x, (this.y + (this.maxY * 2)) + 4, this.name);
				//////////////////
			} else return;
		}
		
		// Animations Test
		if (isInWeaponAnimation == true){
			this.animation.renderWeaponAnimation();
			
		}
		if (isInWalkLeftAnimation == true){
			this.animation.renderEntityWalkAnimation();
		}
		
		else {
			StdDraw.picture(this.x - 5, this.y, this.animation.imageArr[4]);
			// this.animation.render();
		}
		/////////////////////////////////

		// StdDraw.setPenColor(StdDraw.BLUE);
		// StdDraw.filledRectangle(this.x, this.y, this.maxX, this.maxY *
		// 2);
		StdDraw.picture(this.x, this.y, "player_template.png");

		// Body part positions
		double[] xCoords, yCoords;

		// Head
		StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.filledCircle(this.x, this.y + 4.5, 1);
		//////////////////

		// Body
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledRectangle(this.x, this.y + 0.5, 1.75, 3);
		StdDraw.filledRectangle(this.x, this.y + 3, 2.75, 0.5);
		//////////////////

		// Arm 1
		StdDraw.setPenColor(StdDraw.GREEN);
		xCoords = new double[] { this.x - 2.7, this.x - 2.4, this.x - 4.7, this.x - 5 };
		yCoords = new double[] { this.y + 3.5, this.y + 2.5, this.y - 0.5, this.y + 0.25 };

		StdDraw.filledPolygon(xCoords, yCoords);
		//////////////////

		// Arm 2
		StdDraw.setPenColor(StdDraw.GREEN);
		xCoords = new double[] { this.x + 2.7, this.x + 2.4, this.x + 4.7, this.x + 5 };
		yCoords = new double[] { this.y + 3.5, this.y + 2.5, this.y - 0.5, this.y + 0.25 };

		StdDraw.filledPolygon(xCoords, yCoords);
		//////////////////

		// Leg 1
		StdDraw.setPenColor(StdDraw.MAGENTA);
		xCoords = new double[] { this.x - 1.75, this.x - 2.5, this.x - 3.5, this.x - 3.5, this.x - 2, this.x - 0.25 };
		yCoords = new double[] { this.y - 2.5, this.y - 6, this.y - 6, this.y - 7, this.y - 7, this.y - 2.5 };

		StdDraw.filledPolygon(xCoords, yCoords);
		//////////////////

		// Leg 2
		StdDraw.setPenColor(StdDraw.MAGENTA);
		xCoords = new double[] { this.x + 1.75, this.x + 2.5, this.x + 3.5, this.x + 3.5, this.x + 2, this.x + 0.25 };
		yCoords = new double[] { this.y - 2.5, this.y - 6, this.y - 6, this.y - 7, this.y - 7, this.y - 2.5 };

		StdDraw.filledPolygon(xCoords, yCoords);
		//////////////////
	}
	
	public void renderGuiObjects() {
		if (!this.hasInput) return;
		
		//Examined entity info
		renderExaminedEntityInfo();
		//////////////////
		
		//Location name
		renderLocationName();
		//////////////////
		
		if (this.showHealth) {
			//Health bar
			renderHealthBar();
			
			//Percentages to next level
			renderPercentagesToNextLevel();
			
			//Controls
			renderControls();
		}
		
		//Sun position information
		if (this.showMinimap) renderTimeAndWeatherInfo();
		//////////////////
		
		//Minimap
		if (this.showMinimap && this.minimap != null) this.minimap.render();
		//////////////////
		
		//Chat box
		if (this.showChat) renderChatBox();
		//////////////////
		
		//Last target's health bar
		renderTargetHealth();
		//////////////////
		
		//Positive and negative effects active on the player
		renderPlayerEffects();
		//////////////////
		
		//Abilities
		if (this.showAbilities) renderAbilityBar();
		//////////////////
		
		//Energy bar
		if (this.showAbilities) renderEnergyBar();
		//////////////////
		
		//Attack square
		renderAttackSquare();
		//////////////////
		
		//E action bar
		if (this.showEbar) renderEActionBar();
		//////////////////
		
		//Shop
		if (this.currentShop != null) {
			this.currentShop.render();
		}
		//////////////////
		
		//Dialogue
		if (this.currentDialogue != null) {
			this.currentDialogue.render();
		}
		//////////////////
		
		//Inventory
		if (this.inventoryOpen) this.inventory.render();
		//////////////////
		
		//Level map
		if (this.mapOpen) this.levelMap.render();
		//////////////////
		
		if (this.currentTrade != null) renderTrade();
		
		//Level transition animation
		renderLevelTransitionAnimation();
		//////////////////
		
		//Level up messages
		renderLevelUpMessage();
		//////////////////
	}

	private void renderTrade() {
		this.currentTrade.render();
	}

	private void renderTimeAndWeatherInfo() {
		double[] xCoords, yCoords;
		
		//Background
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(89.75, 77.5, 10, 2);
		////
		
		//Time until next phase
		double max = 0;
		double min = 0;
		
		//DAY
		if (this.level.world.time.getSunPosition() == 0) {
			max = this.level.world.time.getDaylength() * this.level.world.time.getSunsetStart() - this.level.world.time.getDaylength() * this.level.world.time.getSunriseEnd();
			min = this.level.world.time.getDaylength() * this.level.world.time.getSunriseEnd();
		}
		
		//SUNSET
		else if (this.level.world.time.getSunPosition() == 1) {
			max = this.level.world.time.getDaylength() * this.level.world.time.getSunsetEnd() - this.level.world.time.getDaylength() * this.level.world.time.getSunsetStart();
			min = this.level.world.time.getDaylength() * this.level.world.time.getSunsetStart();
		}
		
		//NIGHT
		else if (this.level.world.time.getSunPosition() == 2) {
			max = (this.level.world.time.getDaylength() + (this.level.world.time.getDaylength() * this.level.world.time.getSunriseStart())) - (this.level.world.time.getDaylength() * this.level.world.time.getSunsetEnd());
			min = this.level.world.time.getDaylength() * this.level.world.time.getSunsetEnd();
		}
		
		//SUNRISE
		else if (this.level.world.time.getSunPosition() == 3) {
			max = this.level.world.time.getDaylength() * this.level.world.time.getSunriseEnd() - this.level.world.time.getDaylength() * this.level.world.time.getSunriseStart();
			min = this.level.world.time.getDaylength() * this.level.world.time.getSunriseStart();
		}
		
		//Create angle of completion around circle and convert to radians
		double degrees = 360 * ((this.level.world.time.getTimeOfDay() - min) / max);
		if (this.level.world.time.getSunPosition() == 2) {
			double newFactor = this.level.world.time.getTimeOfDay();
			
			if (this.level.world.time.getTimeOfDay() <= this.level.world.time.getDaylength() * this.level.world.time.getSunriseStart()) {
				newFactor = this.level.world.time.getTimeOfDay() + this.level.world.time.getDaylength();
			}
			degrees = 360 * (((newFactor) - min) / max);
		}
		double radians = Math.toRadians(degrees);
		////
		
		//Create circle
		if (degrees >= 0 && degrees <= 90) {
			xCoords = new double[]{96.75, 96.75 + (Math.sin(radians) * 2), 96.75 + (Math.sin(radians) * 2), 96.75};
			yCoords = new double[]{79.6, 79.6, 77.6 + (Math.cos(radians) * 2), 77.6};
		}
		
		else if (degrees > 90 && degrees <= 180) {
			xCoords = new double[]{96.75, 98.75, 98.75, 98.75, 96.75 + (Math.sin(radians) * 2), 96.75};
			yCoords = new double[]{79.6, 79.6, 75.6, 77.6 + (Math.cos(radians) * 2), 77.6 + (Math.cos(radians) * 2), 77.6};
		}
		
		else if (degrees > 180 && degrees <= 270) {
			xCoords = new double[]{96.75, 98.75, 98.75, 96.75, 96.75 + (Math.sin(radians) * 2), 96.75 + (Math.sin(radians) * 2), 96.75};
			yCoords = new double[]{79.6, 79.6, 75.6, 75.6, 75.6, 77.6 + (Math.cos(radians) * 2), 77.6};
		}
		
		else {
			xCoords = new double[]{96.75, 98.75, 98.75, 94.75, 94.75, 96.75 + (Math.sin(radians) * 2), 96.75};
			yCoords = new double[]{79.6, 79.6, 75.6, 75.6, 77.6 + (Math.cos(radians) * 2), 77.6 + (Math.cos(radians) * 2), 77.6};
		}
		
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledPolygon(xCoords, yCoords);
		////
		
		StdDraw.setPenRadius(0.01);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.circle(96.75, 77.6, 2);
		StdDraw.setPenRadius(0.0071);
		StdDraw.circle(96.75, 77.6, 2.1);
		StdDraw.filledSquare(98.75, 79.2, 0.56);
		StdDraw.filledSquare(94.75, 79.2, 0.56);
		StdDraw.filledSquare(98.75, 76, 0.56);
		StdDraw.filledSquare(94.75, 76, 0.56);
		
		StdDraw.setPenColor(new Color(177, 177, 190));
		StdDraw.setPenRadius(0.0032);
		StdDraw.circle(96.75, 77.6, 2);
		/////////
		
		//Weather icon
		renderWeatherIcon();
		/////////
		
		//Day phase Text
		int alpha = (int) (this.level.world.time.getTimeOfDay() - min);
		if (this.level.world.time.getSunPosition() != 2) {
			if (alpha > 255) alpha = 255;
			if ((max + min) - this.level.world.time.getTimeOfDay() < 255) alpha = (int) ((max + min) - (this.level.world.time.getTimeOfDay()));
			if (alpha < 0) alpha = 0;
		} else {
			if (this.level.world.time.getTimeOfDay() <= this.level.world.time.getDaylength() * 0.14) {
				alpha = (int) ((this.level.world.time.getTimeOfDay() + this.level.world.time.getDaylength()) - min);
				if (alpha > 255) alpha = 255;
				if ((max + min) - (this.level.world.time.getTimeOfDay() + this.level.world.time.getDaylength()) < 255) alpha = (int) ((max + min) - (this.level.world.time.getTimeOfDay() + this.level.world.time.getDaylength()));
				if (alpha < 0) alpha = 0;
			} else {
				if (alpha > 255) alpha = 255;
				if ((max + min) - this.level.world.time.getTimeOfDay() < 255) alpha = (int) ((max + min) - (this.level.world.time.getTimeOfDay()));
				if (alpha < 0) alpha = 0;
			}
		}
		StdDraw.setPenColor(new Color(255, 255, 0, alpha));
		StdDraw.setFont(new Font("Arial", Font.BOLD, 22));
		String position = "";
		
		if (this.level.world.time.getSunPosition() == 0) position = "Day";
		else if (this.level.world.time.getSunPosition() == 1) position = "Sunset";
		else if (this.level.world.time.getSunPosition() == 2) position = "Night";
		else if (this.level.world.time.getSunPosition() == 3) position = "Sunrise";
		
		StdDraw.text(89.65, 77.5, position);
		////
		
		//Border
		StdDraw.setPenColor(new Color(177, 177, 190));
		StdDraw.setPenRadius(0.008);
		StdDraw.line(79.75, 79.5, 79.75, 75.5);
		StdDraw.line(99.75, 79.5, 99.75, 75.5);
		StdDraw.line(79.75, 75.5, 99.75, 75.5);
		StdDraw.setPenRadius();
		////
		//////////////////
	}

	private void renderChatBox() {
		//Background
		StdDraw.setPenColor(new Color(0, 0, 0, 173));
		StdDraw.filledRectangle(89.75, 51.2, 10.2, 24);
		//////////////////
		
		//Unsubmitted text
		if (this.typing) {
			StdDraw.setPenColor(new Color(0, 0, 0, 173));
			StdDraw.filledRectangle(89.75, 20.7, 10.2, 6);
			
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 12));
			if (this.chat.length() < 20) StdDraw.textLeft(79.75, 25.5, this.chat);
			else {
				double yy = 25.5;
				String message = "";
				for (int i = 0; i < this.chat.length(); i++) {
					if (i != 0 && i % 20 == 0) {
						StdDraw.textLeft(79.75, yy, message);
						yy -= 1.2;
						message = "";
					}
					message += this.chat.charAt(i);
					if (i == this.chat.length() - 1) StdDraw.textLeft(79.75, yy, message);
				}
			}
		}
		//////////////////
		
		//Previous messages
		this.canStartTrade = false;
		double previousHighestY = 26.8;
		for (int i = this.level.world.getChatMessages().size() - 1; i >= 0; i--) {
			String message = this.level.world.getChatMessages().get(i);
			double yy = Math.ceil(previousHighestY + 1.2);
			
			if (message.length() < 20) {
				if (message.length() >= 2 && message.substring(0, 2).equals("::")) {
					if (this.currentTrade == null && this.currentShop == null && this.currentDialogue == null && !this.inventoryOpen && !this.mapOpen && StdDraw.mouseY() <= yy + 0.65 && StdDraw.mouseY() >= yy - 0.5 && StdDraw.mouseX() >= 79.55 && StdDraw.mouseX() <= 100) {
						StdDraw.setPenColor(StdDraw.CYAN);
						this.canStartTrade = true;
						this.playerToTrade = "";
						
						int index = 2;
						while (!(message.charAt(index) == ' ')) {
							this.playerToTrade += message.charAt(index);
							index++;
						}
					}
					else {
						StdDraw.setPenColor(new Color(1, 70, 238));
					}
					StdDraw.setFont(new Font("Arial", Font.PLAIN, 11));
					message = message.substring(2, message.length());
				}
				else if (message.length() >= this.name.length() && message.substring(0, this.name.length()).equals(this.name)) {
					StdDraw.setPenColor(StdDraw.GREEN);
					StdDraw.setFont(new Font("Arial", Font.BOLD, 11));
				}
				else {
					StdDraw.setPenColor(StdDraw.ORANGE);
					StdDraw.setFont(new Font("Arial", Font.PLAIN, 11));
				}
				if (yy < 73.61) StdDraw.textLeft(79.75, yy, message);
				previousHighestY = yy;
			}
			
			else {
				double previousYY = yy;
				yy += Math.ceil(message.length() / 20.0) * 1.2;
				previousHighestY = yy;
				
				if (message.length() >= 2 && message.substring(0, 2).equals("::")) {
					if (this.currentTrade == null && this.currentShop == null && this.currentDialogue == null && !this.inventoryOpen && !this.mapOpen && StdDraw.mouseY() <= previousHighestY + 0.65 && StdDraw.mouseY() >= (previousYY + 1.2) - 0.5 && StdDraw.mouseX() >= 79.55 && StdDraw.mouseX() <= 100) {
						StdDraw.setPenColor(StdDraw.CYAN);
						this.canStartTrade = true;
						this.playerToTrade = "";
						
						int index = 2;
						while (!(message.charAt(index) == ' ')) {
							this.playerToTrade += message.charAt(index);
							index++;
						}
					}
					else {
						StdDraw.setPenColor(new Color(1, 70, 238));
					}
					StdDraw.setFont(new Font("Arial", Font.PLAIN, 11));
					message = message.substring(2, message.length());
				}
				else if (message.length() >= this.name.length() && message.substring(0, this.name.length()).equals(this.name)) {
					StdDraw.setPenColor(StdDraw.GREEN);
					StdDraw.setFont(new Font("Arial", Font.BOLD, 11));
				}
				else {
					StdDraw.setPenColor(StdDraw.ORANGE);
					StdDraw.setFont(new Font("Arial", Font.PLAIN, 11));
				}
				String messagePiece = "";
			
				for (int index = 0; index < message.length(); index++) {
					if (index != 0 && index % 20 == 0) {
						if (yy < 73.61) StdDraw.textLeft(79.75, yy, messagePiece);
						yy -= 1.2;
						messagePiece = "";
					}
					messagePiece += message.charAt(index);
					if (index == message.length() - 1 && yy < 73.61) StdDraw.textLeft(79.75, yy, messagePiece);
				}
			}
		}
		//////////////////
	}
	
	private int findNextSpace(int start) {
		int index = start;
		
		for (int i = start; i < this.chat.length(); i++) {
			if (this.chat.charAt(i) == ' ') {
				index = i;
				break;
			}
		}
		
		if (index > this.chat.length()) return this.chat.length() + 1;
		else if (index == start && this.chat.charAt(index) != ' ') return this.chat.length() + 1;
		
		else return index;
	}

	private void renderWeatherIcon() {
		if (this.level.world.weather.getCurrentWeather() == 0) StdDraw.picture(82.75, 77.72, "Sunny.png");
		else if (this.level.world.weather.getCurrentWeather() == 1) StdDraw.picture(82.75, 77.72, "Slightly_cloudy.png");
		else if (this.level.world.weather.getCurrentWeather() == 2) StdDraw.picture(82.75, 77.72, "Cloudy.png");
		else if (this.level.world.weather.getCurrentWeather() == 3) StdDraw.picture(82.75, 77.72, "Rain.png");
		else if (this.level.world.weather.getCurrentWeather() == 4) StdDraw.picture(82.75, 77.72, "Lightning.png");
		
		StdDraw.setPenColor(new Color(177, 177, 190));
		StdDraw.setPenRadius(0.0032);
		StdDraw.circle(82.75, 77.6, 2);
	}

	private void renderPercentagesToNextLevel() {
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(5, 88, 1.5);
		StdDraw.filledSquare(10, 88, 1.5);
		StdDraw.filledSquare(15, 88, 1.5);
		StdDraw.filledSquare(20, 88, 1.5);
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 13));
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.text(5, 87.5, ((int) (this.meleeXpPercent)) + "%");
		StdDraw.setPenColor(new Color(130, 176, 18));
		StdDraw.text(10, 87.5, ((int) (this.archeryXpPercent)) + "%");
		StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.text(15, 87.5, ((int) (this.magicXpPercent)) + "%");
		StdDraw.setPenColor(StdDraw.CYAN);
		StdDraw.text(20, 87.5, ((int) (this.defenseXpPercent)) + "%");
	}

	private void renderHealthBar() {
		double[] xCoords, yCoords;
		StdDraw.setPenColor(new Color(1, 1, 1, 119));
		if (!this.showControls) {
			StdDraw.filledRectangle(13, 90, 14, 10);
		} else {
			StdDraw.filledRectangle(13, 87, 14, 26);
		}
		
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 29));
		StdDraw.text(13, 97, this.name);
		
		StdDraw.setPenColor(StdDraw.RED);
		xCoords = new double[]{1, 1, (this.currentHealth / (this.maxHealth * 0.042)) + 1, (this.currentHealth / (this.maxHealth * 0.042)) + 1}; //(this.currentHealth / 4.2) + 1, (this.currentHealth / 4.2) + 1    works for 100 max health but nothing else
		yCoords = new double[]{94.5, 89.5, 89.5, 94.5};
		StdDraw.filledPolygon(xCoords, yCoords);
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
		StdDraw.text(13, 91.75, this.currentHealth + " / " + this.maxHealth);
		
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(0.0125);
		StdDraw.rectangle(13, 92, 12, 2.5);
		StdDraw.setPenRadius();
	}

	private void renderLocationName() {
		if (this.showLocationNameTimer > 0 && this.levelTransitionDelay == 0) {
			this.showLocationNameTimer--;
			
			StdDraw.setPenColor(new Color(1, 1, 1, 192));
			
			double yy = 101.5;
			
			if (this.showLocationNameTimer > this.locationNameTimerAmount - 30) {
				yy -= (this.locationNameTimerAmount - this.showLocationNameTimer) * 0.15;
			}
			
			else yy -= 4.5;
			
			if (yy < 97) yy = 97;
			
			if (this.showLocationNameTimer < 30) {
				yy += (30 - this.showLocationNameTimer) * 0.15;
			}
			StdDraw.filledRectangle(50, yy, 17, 3);
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.rectangle(50, yy, 17, 3);
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 26));
			
			StdDraw.text(50, yy - 0.25, toTitleCase(this.level.name));
		}
	}

	private void renderControls() {
		if (!this.inventoryOpen && !this.mapOpen) {
			if (this.showControls) {
				StdDraw.setPenColor(new Color(223, 223, 231));
				StdDraw.setFont(new Font("Arial", Font.BOLD, 22));
				StdDraw.text(13.5, 84, "[Ctrl] to hide controls");
				StdDraw.setFont(new Font("Arial", Font.PLAIN, 19));
				StdDraw.text(12.9, 81, "[ WASD ] Move character");
				StdDraw.text(13.5, 78, "[ I ] Open / Close inventory");
				StdDraw.text(9.3, 75, "[ E ] Action button");
				StdDraw.text(8.5, 72, "[SPACE] Attack");
				StdDraw.text(12.6, 69, "[Right click] - show / hide");
				StdDraw.text(10, 66, "selected entity info");
				StdDraw.text(14.6, 63, "[ M ]  Open / Close level map");
			} else {
				StdDraw.setPenColor(new Color(223, 223, 231));
				StdDraw.setFont(new Font("Arial", Font.BOLD, 22));
				StdDraw.text(13.5, 84, "[Ctrl] to show controls");
			}
		}
	}

	private void renderTargetHealth() {
		if (this.lastTarget != null) {
			double yy = 0;
			
			if (this.showLocationNameTimer > this.locationNameTimerAmount - 30) {
				yy += (this.locationNameTimerAmount - this.showLocationNameTimer) * 0.18;
			}
			
			else yy += 5.25;
			
			if (yy > 5.25) yy = 5.25;
			
			if (this.showLocationNameTimer < 30) {
				yy -= (30 - this.showLocationNameTimer) * 0.18;
			}
			
			double[] xCoords, yCoords;
			
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 29));
			StdDraw.text(50, 97 - yy, this.lastTarget.name);
			
			StdDraw.setPenColor(StdDraw.RED);
			xCoords = new double[]{62, 62, 61 - (this.lastTarget.currentHealth / (this.lastTarget.maxHealth * 0.042)) + 1, 61 - (this.lastTarget.currentHealth / (this.lastTarget.maxHealth * 0.042)) + 1};
			yCoords = new double[]{94.5 - yy, 89.5 - yy, 89.5 - yy, 94.5 - yy};
			StdDraw.filledPolygon(xCoords, yCoords);
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
			StdDraw.text(50, 91.75 - yy, this.lastTarget.currentHealth + " / " + this.lastTarget.maxHealth);
			
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(0.0125);
			StdDraw.rectangle(50, 92 - yy, 12, 2.5);
			StdDraw.setPenRadius();
		}
	}

	private void renderPlayerEffects() {
		if (!this.inventoryOpen && !this.mapOpen && this.currentShop == null) {
			double xx = 3, yy = 15;
			
			if (!this.positiveEffects.isEmpty() || !this.negativeEffects.isEmpty()) {
				StdDraw.setPenColor(new Color(0, 0, 0, 150));
				StdDraw.filledRectangle(12, 9, 12, 11);
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
				StdDraw.text(8, 18, "Positive effects:");
				StdDraw.setPenColor(new Color(240, 45, 45));
				StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
				StdDraw.text(8, 9, "Negative effects:"); 
			}
			
			for (Effect_Positive ep : this.positiveEffects) {
				StdDraw.picture(xx, yy, ep.imageFile, 2.5, 2.5);
				xx += 3;
				if (xx > 21) {
					xx = 3;
					yy -= 3;
				}
				
				if (yy < 12) break;
			}
			
			xx = 3;
			yy = 6;
			
			for (Effect_Negative en : this.negativeEffects) {
				StdDraw.picture(xx, yy, en.imageFile, 2.5, 2.5);
				xx += 3;
				if (xx > 21) {
					xx = 3;
					yy -= 3;
				}
				
				if (yy < 3) break;
			}
		}
	}

	private void renderAbilityBar() {
		if (!this.inventoryOpen && !this.mapOpen && this.currentShop == null) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(50, 10.1, 24, 4);
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.rectangle(50, 10.1, 24, 4);
			
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.square(30, 10.1, 3.75);
			StdDraw.square(38, 10.1, 3.75);
			StdDraw.square(46, 10.1, 3.75);
			StdDraw.square(54, 10.1, 3.75);
			StdDraw.square(62, 10.1, 3.75);
			StdDraw.square(70, 10.1, 3.75);
			
			double xx = 30;
			for (int i = 0; i < this.weapon.abilities.length; i++) {
				if (this.weapon.abilities[i] != null) {
					this.weapon.abilities[i].renderIcon(xx, 10.1, 7);
					if (this.selectedAbility == i + 1) {
						StdDraw.setPenColor(17, 99, this.selectedAbilityGlow);
						if (this.abilityGlowDown) {
							this.selectedAbilityGlow -= 3;
							
							if (this.selectedAbilityGlow < 4) this.abilityGlowDown = false;
						} else {
							this.selectedAbilityGlow += 3;
							
							if (this.selectedAbilityGlow > 245) this.abilityGlowDown = true;
						}
						StdDraw.setPenRadius(0.008 + (this.selectedAbilityGlow / 42500.0));
						StdDraw.square(xx, 10.1, 3.5);
						StdDraw.setPenRadius();
					}
					xx += 8;
				}
				
				else break;
			}
		}
	}

	private void renderEnergyBar() {
		if (!this.inventoryOpen && !this.mapOpen && this.currentShop == null) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(50, 15.33, 23.95, 0.95);
			
			double reducingEnergyPosition = this.energyToReduce * 0.965;
			double[] xCoords, yCoords;
			
			if (this.energyToReduce > 0) this.energyToReduce -= this.energyToReduce * 0.035;
			if (this.energyToReduce < 0) this.energyToReduce = 0;
		
			if (this.energyToReduce >= 33) {
				this.energyRed += 5;
				this.energyGreen++;
				this.energyBlue += 5;
			
				if (this.energyRed > 253) this.energyRed = 253;
				if (this.energyGreen > 249) this.energyGreen = 249;
				if (this.energyBlue > 253) this.energyBlue = 253;
			} else {
				this.energyRed -= 2;
				this.energyGreen--;
				this.energyBlue -= 2;
			
				if (this.energyRed < 89) this.energyRed = 89;
				if (this.energyGreen < 222) this.energyGreen = 222;
				if (this.energyBlue < 77) this.energyBlue = 77;
			}
		
			StdDraw.setPenColor(new Color(this.energyRed, this.energyGreen, this.energyBlue));
			xCoords = new double[]{26, 26, 26 + (48 * ((this.energy + reducingEnergyPosition) / (this.maxEnergy + 0.0))), 26 + (48 * ((this.energy + reducingEnergyPosition) / (this.maxEnergy + 0.0)))};
			yCoords = new double[]{16.33, 14.33, 14.33, 16.33};
			StdDraw.filledPolygon(xCoords, yCoords);
		
			StdDraw.setPenColor(new Color(70, 31, 62));
			StdDraw.setPenRadius(0.0075);
			StdDraw.rectangle(50, 15.33, 24, 1);
			StdDraw.setPenRadius();
		}
	}

	private void renderAttackSquare() {
		if (!this.inventoryOpen && !this.mapOpen && this.currentShop == null) {
			StdDraw.setPenColor(new Color(0, 0, 0, 190));
			StdDraw.filledSquare(88, 8, 6);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(88, 1, 6, 0.98);
		
			if (!(this.weapon instanceof Item_Equipment_EmptySpace)) StdDraw.picture(88, 8, this.weapon.iconImageFile);
			else {
				StdDraw.setPenColor(StdDraw.DARK_GRAY);
				StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
				StdDraw.text(88, 7.75, "Empty");
			}
		
			double min = 0;
			double max = MAX_ATTACK_DELAY - this.attackDelay;
			double degrees = 360 * ((this.attackDelayTimer - min) / max);
			double radians = Math.toRadians(degrees);
			double[] xCoords, yCoords;
		
			if (this.attackDelayTimer > 0) {
				if (degrees >= 0 && degrees <= 90) {
					xCoords = new double[]{88, 88 + (Math.sin(radians) * 6), 88 + (Math.sin(radians) * 6), 88};
					yCoords = new double[]{14, 14, 8 + (Math.cos(radians) * 6), 8};
				}
		
				else if (degrees > 90 && degrees <= 180) {
					xCoords = new double[]{88, 94, 94, 94, 88 + (Math.sin(radians) * 6), 88};
					yCoords = new double[]{14, 14, 2, 8 + (Math.cos(radians) * 6), 8 + (Math.cos(radians) * 6), 8};
				}
		
				else if (degrees > 180 && degrees <= 270) {
					xCoords = new double[]{88, 94, 94, 88, 88 + (Math.sin(radians) * 6), 88 + (Math.sin(radians) * 6), 88};
					yCoords = new double[]{14, 14, 2, 2, 2, 8 + (Math.cos(radians) * 6), 8};
				}
		
				else {
					xCoords = new double[]{88, 94, 94, 82, 82, 88 + (Math.sin(radians) * 6), 88};
					yCoords = new double[]{14, 14, 2, 2, 8 + (Math.cos(radians) * 6), 8 + (Math.cos(radians) * 6), 8};
				}
				StdDraw.setPenColor(new Color(this.attackDelayTimer * 2, (MAX_ATTACK_DELAY - this.attackDelayTimer) * 2, 0));
				StdDraw.filledPolygon(xCoords, yCoords);
			}
			
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.square(88, 8, 6);
			StdDraw.rectangle(88, 1, 6, 0.98);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 15));
			StdDraw.text(88, 0.8, "Spacebar");
		}
	}

	private void renderEActionBar() {
		if (!this.inventoryOpen && !this.mapOpen && this.currentShop == null) {
			StdDraw.setPenColor(new Color(0, 0, 0, 190));
			StdDraw.filledRectangle(50, 3.1, 22, 3);
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.rectangle(50, 3.1, 22, 3);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
			StdDraw.text(31, 3, "E:");

			String message = "";
			
			for (Entity e : this.level.getEntities()) {
				if (e instanceof Entity_NPC && ((Entity_NPC)e).canTalk) {

					if (GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY()) <= 10) {
						message = "Talk to " + e.name;
						break;
					}
				}
			}
			
			if (message.equals("") && this.nearbyLevelConnector != null) {
				message = "Go to " + this.nearbyLevelConnector.destination.name;
			}
			
			else if (this.nearbyItem != null) {
				if (this.inventory.isFull) {
					message = "Inventory full!";
				} else {
					message = "Pick up " + this.nearbyItem.item.name;
				}
			}
			
			StdDraw.text(52, 3, message);
		}
	}

	private void renderLevelUpMessage() {
		if (this.meleeLevelUpTimer > 0) {
			StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.text(50, 84, "Level up! Your Melee level is now " + this.melee);
		}
		
		else if (this.archeryLevelUpTimer > 0) {
			StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.text(50, 84, "Level up! Your Archery level is now " + this.archery);
		}
		
		else if (this.magicLevelUpTimer > 0) {
			StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.text(50, 84, "Level up! Your Magic level is now " + this.magic);
		}
		
		else if (this.defenseLevelUpTimer > 0) {
			StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.text(50, 84, "Level up! Your Defense level is now " + this.defense);
		}
	}

	private void renderLevelTransitionAnimation() {
		if (this.levelTransitionDelay > 0) {
			StdDraw.setPenColor(StdDraw.BLACK);
			if (this.levelTransitionDelayAmount / 2 < this.levelTransitionDelay) {
				StdDraw.filledSquare(50, 50, 101 - (this.levelTransitionDelay / 10.0));
				StdDraw.setPenColor(StdDraw.YELLOW);
				StdDraw.square(50, 50, 101 - (this.levelTransitionDelay / 10.0));
			} else {
				StdDraw.filledSquare(50, 50, (this.levelTransitionDelay / 10.0));
				StdDraw.setPenColor(StdDraw.YELLOW);
				StdDraw.square(50, 50, (this.levelTransitionDelay / 10.0));
			}
		}
	}

	private void renderExaminedEntityInfo() {
		if (this.examinedEntity != null) {
			double windowHeight = 12;
			double textHeightMod = 0;
			if (!this.examinedEntity.positiveEffects.isEmpty()) {
				//windowHeight += 4;
				//textHeightMod += 4;
				windowHeight += ((int) (this.examinedEntity.positiveEffects.size() / 3)) + 3.5;
				textHeightMod += ((int) (this.examinedEntity.positiveEffects.size() / 3)) + 3.5;
			}
			if (!this.examinedEntity.negativeEffects.isEmpty()) {
				//windowHeight += 4;
				//textHeightMod += 4;
				windowHeight += ((int) (this.examinedEntity.negativeEffects.size() / 3)) + 3.5;
				textHeightMod += ((int) (this.examinedEntity.negativeEffects.size() / 3)) + 3.5;
			}
			
			StdDraw.setPenColor(new Color(61, 248, 212));
			StdDraw.filledRectangle(StdDraw.mouseX(), StdDraw.mouseY() - 14.5 - textHeightMod, 9, windowHeight);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY() - 14.5 - textHeightMod, 9, windowHeight);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
			StdDraw.text(StdDraw.mouseX(), StdDraw.mouseY() - 4.5, this.examinedEntity.name);
			StdDraw.text(StdDraw.mouseX(), StdDraw.mouseY() - 7, "Level " + this.examinedEntity.overallLevel);
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
			
			StdDraw.text(StdDraw.mouseX() - 5.9, StdDraw.mouseY() - 9.5, "Health:");
			StdDraw.text(StdDraw.mouseX() + 4, StdDraw.mouseY() - 9.5, this.examinedEntity.maxHealth + "");
			
			StdDraw.text(StdDraw.mouseX() - 4.75, StdDraw.mouseY() - 12, "Accuracy:");
			StdDraw.text(StdDraw.mouseX() + 4, StdDraw.mouseY() - 12, df.format(this.examinedEntity.accuracy));
			if (this.examinedEntity.tempAccuracyBoost > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 12, " + " + this.examinedEntity.tempAccuracyBoost);
			}
			else if (this.examinedEntity.tempAccuracyBoost < 0) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 12, " - " + (Math.abs(this.examinedEntity.tempAccuracyBoost)));
			}
			
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(StdDraw.mouseX() - 5.1, StdDraw.mouseY() - 14.5, "Strength:");
			StdDraw.text(StdDraw.mouseX() + 4, StdDraw.mouseY() - 14.5, this.examinedEntity.maxHit + "");
			if (this.examinedEntity.tempMaxHitBoost > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 14.5, " + "  + this.examinedEntity.tempMaxHitBoost);
			}
			else if (this.examinedEntity.tempMaxHitBoost < 0) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 14.5, " - " + (Math.abs(this.examinedEntity.tempMaxHitBoost)));
			}
			
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(StdDraw.mouseX() - 5.1, StdDraw.mouseY() - 17, "Defense:");
			StdDraw.text(StdDraw.mouseX() + 4, StdDraw.mouseY() - 17, this.examinedEntity.defense + "");
			if (this.examinedEntity.tempDefenseBoost > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 17, " + "  + this.examinedEntity.tempDefenseBoost);
			}
			else if (this.examinedEntity.tempDefenseBoost < 0) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 17, " - " + (Math.abs(this.examinedEntity.tempDefenseBoost)));
			}
			
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(StdDraw.mouseX() - 6, StdDraw.mouseY() - 19.5, "Melee:");
			StdDraw.text(StdDraw.mouseX() + 4, StdDraw.mouseY() - 19.5, this.examinedEntity.melee + "");
			if (this.examinedEntity.tempMeleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 19.5, " + "  + this.examinedEntity.tempMeleeBoost);
			}
			else if (this.examinedEntity.tempMeleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 19.5, " - " + (Math.abs(this.examinedEntity.tempMeleeBoost)));
			}
			
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(StdDraw.mouseX() - 5.4, StdDraw.mouseY() - 22, "Archery:");
			StdDraw.text(StdDraw.mouseX() + 4, StdDraw.mouseY() - 22, this.examinedEntity.archery + "");
			if (this.examinedEntity.tempArcheryBoost > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 22, " + "  + this.examinedEntity.tempArcheryBoost);
			}
			else if (this.examinedEntity.tempArcheryBoost < 0) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 22, " - " + (Math.abs(this.examinedEntity.tempArcheryBoost)));
			}
			
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(StdDraw.mouseX() - 6, StdDraw.mouseY() - 24.5, "Magic:");
			StdDraw.text(StdDraw.mouseX() + 4, StdDraw.mouseY() - 24.5, this.examinedEntity.magic + "");
			if (this.examinedEntity.tempMagicBoost > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 24.5, " + "  + this.examinedEntity.tempMagicBoost);
			}
			else if (this.examinedEntity.tempMagicBoost < 0) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.text(StdDraw.mouseX() + 6, StdDraw.mouseY() - 24.5, " - " + (Math.abs(this.examinedEntity.tempMagicBoost)));
			}
			
			//Active effects on the examined entity
			double xx = StdDraw.mouseX() - 6;
			double yy = StdDraw.mouseY() - 27;
			int count = 1;
			
			StdDraw.setPenColor(StdDraw.BLACK);
			
			if (!this.examinedEntity.positiveEffects.isEmpty()) {
				StdDraw.text(StdDraw.mouseX() - 2.5, yy, "Positive effects:");
				yy -= 2.75;
				for (Effect_Positive ep : this.examinedEntity.positiveEffects) {
					if (count > 3) {
						count = 1;
						xx = StdDraw.mouseX() - 6;
						yy -= 4.25;
					}
					ep.renderIcon(xx, yy, 3.5);
					xx += 4.5;
					count++;
				}
				yy -= 4.25;
			}
			
			if (!this.examinedEntity.negativeEffects.isEmpty()) {
				xx = StdDraw.mouseX() - 6;
				count = 1;
				StdDraw.text(StdDraw.mouseX() - 2.1, yy, "Negative effects:");
				yy -= 2.75;
				for (Effect_Negative en : this.examinedEntity.negativeEffects) {
					if (count > 3) {
						count = 1;
						xx = StdDraw.mouseX() - 6;
						yy -= 4.25;
					}
					en.renderIcon(xx, yy, 3.5);
					xx += 4.5;
					count++;
				}
			}
			//////////////////
		}
	}

	private static String toTitleCase(String givenString) {
		if (givenString == null || givenString.equals("")) return "";
		
	    String[] arr = givenString.split(" ");
	    StringBuffer sb = new StringBuffer();

	    for (int i = 0; i < arr.length; i++) {
	        sb.append(Character.toUpperCase(arr[i].charAt(0)))
	            .append(arr[i].substring(1)).append(" ");
	    }          
	    return sb.toString().trim();
	}
	
	@Override
	public void move() {
		if (!this.canMove) return;
		
		if (StdDraw.isKeyPressed(KeyEvent.VK_SHIFT) && this.isMoving && this.energy >= 0.5) {
			this.speed *= 4;
			this.energy -= 0.5;
		}
		
		if (StdDraw.isKeyPressed(KeyEvent.VK_W) && StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			if (!willCollide(getMapX() + this.speed / 2, getMapY() + this.speed / 2)) {
				moveTo(getMapX() + (this.speed / 2), getMapY() + (this.speed / 2), 2);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX() + this.speed, getMapY())) {
				moveTo(getMapX() + this.speed, getMapY(), 3);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX(), getMapY() + this.speed)) {
				moveTo(getMapX(), getMapY() + this.speed, 1);
				this.isMoving = true;
			}
			
			else this.isMoving = false;
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_W) && StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			this.isInWalkLeftAnimation = true;
			if (!willCollide(getMapX() - this.speed / 2, getMapY() + this.speed / 2)) {
				moveTo(getMapX() - (this.speed / 2), getMapY() + (this.speed / 2), 8);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX() - this.speed, getMapY())) {
				moveTo(getMapX() - this.speed, getMapY(), 7);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX(), getMapY() + this.speed)) {
				moveTo(getMapX(), getMapY() + this.speed, 1);
				this.isMoving = true;
			}
			
			else this.isMoving = false;
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_S) && StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			if (!willCollide(getMapX() + this.speed / 2, getMapY() - this.speed / 2)) {
				moveTo(getMapX() + (this.speed / 2), getMapY() - (this.speed / 2), 4);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX() + this.speed, getMapY())) {
				moveTo(getMapX() + this.speed, getMapY(), 3);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX(), getMapY() - this.speed)) {
				moveTo(getMapX(), getMapY() - this.speed, 5);
				this.isMoving = true;
			}
			
			else this.isMoving = false;
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_S) && StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			if (!willCollide(getMapX() - this.speed / 2, getMapY() - this.speed / 2)) {
				moveTo(getMapX() - (this.speed / 2), getMapY() - (this.speed / 2), 6);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX() - this.speed, getMapY())) {
				moveTo(getMapX() - this.speed, getMapY(), 7);
				this.isMoving = true;
			}
			
			else if (!willCollide(getMapX(), getMapY() - this.speed)) {
				moveTo(getMapX(), getMapY() - this.speed, 5);
				this.isMoving = true;
			}
			
			else this.isMoving = false;
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_W)) {
			if (!willCollide(getMapX(), getMapY() + this.speed)) {
				moveTo(getMapX(), getMapY() + this.speed, 1);
				this.isMoving = true;
			} else this.isMoving = false;
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_S)) {
			if (!willCollide(getMapX(), getMapY() - this.speed)) {
				moveTo(getMapX(), getMapY() - this.speed, 5);
				this.isMoving = true;
			} else this.isMoving = false;
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			if (!willCollide(getMapX() + this.speed, getMapY())) {
				moveTo(getMapX() + this.speed, getMapY(), 3);
				this.isMoving = true;
			} else this.isMoving = false;
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			if (!willCollide(getMapX() - this.speed, getMapY())) {
				moveTo(getMapX() - this.speed, getMapY(), 7);
				this.isMoving = true;
			} else this.isMoving = false;
		}
		
		else this.isMoving = false;
		
		this.speed = this.defaultSpeed;
	}
	
	protected void checkOtherInput() {
		//T - Chat
		if (this.showChat && StdDraw.isKeyPressed(KeyEvent.VK_T) && this.buttonDelay == 0 && this.levelTransitionDelay == 0) {
			StdDraw.keysTyped.clear();
			this.typing = true;
			this.buttonDelay = this.buttonDelayAmount;
		}
		///////////////////
		
		//E - Action button

		if (StdDraw.isKeyPressed(KeyEvent.VK_E) && this.buttonDelay == 0 && this.levelTransitionDelay == 0) {
			
			//During dialogue
			if (this.currentDialogue != null) {
				this.dialogueIndex++;
				if (this.dialogueIndex >= this.currentDialogue.speaker.dialogues.size()){
					currentDialogue = null;
				}
				else if (this.currentDialogue.finishedWriting) this.currentDialogue = ((Entity_NPC)this.currentDialogue.speaker).dialogue(this.dialogueIndex);
				if (this.currentDialogue == null) {
					this.dialogueIndex = 0;
					this.canAttack = true;
				}
				this.buttonDelay = this.buttonDelayAmount;
			///////////////////
				
			} else {
			
				boolean actionFound = false;

				// Starting Dialogue
				for (Entity e : this.level.getEntities()) {
					if (e instanceof Entity_NPC && ((Entity_NPC) e).canTalk) {

						if (GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY()) <= 10) {
							this.currentDialogue = ((Entity_NPC) e).dialogue(this.dialogueIndex);
							this.buttonDelay = this.buttonDelayAmount;
							this.canAttack = false;
							actionFound = true;
							break;
						}
					}
				}
				///////////////////
		
				// Nearby level connector
				if (!actionFound && this.nearbyLevelConnector != null) {
					this.levelTransitionDelay = this.levelTransitionDelayAmount;
					this.buttonDelay = this.buttonDelayAmount;
					actionFound = true;
				}
				///////////////////

				// Nearby dropped item
				if (!actionFound && this.nearbyItem != null && !this.inventory.isFull) {
					for (Item item : this.inventory.items) {
						if (item instanceof Item_Equipment_EmptySpace) {

							ArrayList<String> attributes = GameUtilities.createAttributes(this.nearbyItem.item, this.nearbyItem.item.amount);
							
							Packet026RemoveDroppedItem packet = new Packet026RemoveDroppedItem(this.level.name, GameUtilities.itemTypeToString(this.nearbyItem.item), this.nearbyItem.mapX, this.nearbyItem.mapY, attributes);
							packet.writeData(Game.socketClient);

							this.inventory.addItem(this.nearbyItem.item);
							this.nearbyItem = null;
							actionFound = true;
							break;
						}
					}
					this.buttonDelay = this.buttonDelayAmount;
				}
				///////////////////
				
				// Nearby player
				if (!actionFound && this.nearbyPlayer != null) {
					
					Packet013TradeRequest tradeRequestPacket = new Packet013TradeRequest(this.name, this.nearbyPlayer.name);
					tradeRequestPacket.writeData(Game.socketClient);
					
					this.level.world.getChatMessagesToAdd().add("Trade request to " + this.nearbyPlayer.name + " sent.");
					
					actionFound = true;
					this.buttonDelay = this.buttonDelayAmount;
				}
				///////////////////
			}
			///////////////////
		}
		
		///////////////////////////////////
		
		//Spacebar - Attack; Speed up dialogue
		if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
			
			//Animations
			this.isInWeaponAnimation = true;
			///////////////////////////////
			
			//Speed up dialogue
			if (this.currentDialogue != null) {
				this.currentDialogue.writingSpeed = 0;
			}
			///////////////////
			
			//Attack
			else if (this.attackDelayTimer == 0) {
				Entity_Character target = null;
				
				if (this.weapon.isArchery) {
					Packet027AddProjectile projectilePacket = new Packet027AddProjectile(this.level.name, this.name, "arrow_test.png", this.getMapX(), this.getMapY(), 0.25, 0.75, 32, getMovingDir());
					projectilePacket.writeData(Game.socketClient);
					
					this.attackDelayTimer = this.attackDelay;
				}
				
				else {
					for (Entity e : this.level.getEntities()) {
						if (e instanceof Entity_Character && ((Entity_Character)e).canBeAttacked && !e.equals(this) && !this.level.getEntitiesToRemove().contains(e)) {
							//Above player
							if (this.getMapY() + this.maxY + this.range >= e.getMapY() - e.minY && ((this.getMapX() > e.getMapX() && this.getMapX() - this.minX <= e.getMapX() + e.maxX) || (this.getMapX() < e.getMapX() && this.getMapX() + this.maxX >= e.getMapX() - e.minX) || (this.getMapX() == e.getMapX()))) {
								if (target == null) {

									if (GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY()) < this.range) target = (Entity_Character)e;
								}
								else {
									double distanceFromCurrentTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), target.getMapX(), target.getMapY());
									double distanceFromPossibleTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY());
									
									if (distanceFromPossibleTarget < distanceFromCurrentTarget) target = (Entity_Character)e;
								}
							}
							///////////
							
							//Below player
							else if (this.getMapY() - this.minY - this.range <= e.getMapY() + e.maxY && ((this.getMapX() > e.getMapX() && this.getMapX() - this.minX <= e.getMapX() + e.maxX) || (this.getMapX() < e.getMapX() && this.getMapX() + this.maxX >= e.getMapX() - e.minX) || (this.getMapX() == e.getMapX()))) {
								if (target == null) {

									if (GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY()) < this.range) target = (Entity_Character)e;
								}
								else {
									double distanceFromCurrentTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), target.getMapX(), target.getMapY());
									double distanceFromPossibleTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY());
									
									if (distanceFromPossibleTarget < distanceFromCurrentTarget) target = (Entity_Character)e;
								}
							}
							///////////
							
							//Left of player
							else if (this.getMapX() - this.minX - this.range <= e.getMapX() + e.maxX && ((this.getMapY() > e.getMapY() && this.getMapY() - this.minY <= e.getMapY() + e.maxY) || (this.getMapY() < e.getMapY() && this.getMapY() + this.maxY >= e.getMapY() - e.minY) || (this.getMapY() == e.getMapY()))) {
								if (target == null) {

									if (GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY()) < this.range) target = (Entity_Character)e;
								}
								else {
									double distanceFromCurrentTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), target.getMapX(), target.getMapY());
									double distanceFromPossibleTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY());
									
									if (distanceFromPossibleTarget < distanceFromCurrentTarget) target = (Entity_Character)e;
								}
							}
							///////////
							
							//Right of player
							else if (this.getMapX() + this.maxX + this.range >= e.getMapX() - e.minX && ((this.getMapY() > e.getMapY() && this.getMapY() - this.minY <= e.getMapY() + e.maxY) || (this.getMapY() < e.getMapY() && this.getMapY() + this.maxY >= e.getMapY() - e.minY) || (this.getMapY() == e.getMapY()))) {
								if (target == null) {

									if (GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY()) < this.range) target = (Entity_Character)e;
								}
								else {
									double distanceFromCurrentTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), target.getMapX(), target.getMapY());
									double distanceFromPossibleTarget = GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY());
									
									if (distanceFromPossibleTarget < distanceFromCurrentTarget) target = (Entity_Character)e;
								}
							}
							///////////
						}
					}
					
					if (target != null) attack(target);
				}
			}
			///////////////////
		} else if (this.currentDialogue != null) this.currentDialogue.writingSpeed = 2;
		///////////////////

		//I - Open inventory
		if (this.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_I)) {
			if (!this.inventoryOpen) {
				this.inventoryOpen = true;
				this.canAttack = false;
				this.buttonDelay = this.buttonDelayAmount;
				this.examinedEntity = null;
			}
			
			else {
				this.inventoryOpen = false;
				this.canAttack = true;
			}
		}
		///////////////////
		
		//ESC - Close inventory; Close dialogue; Pause menu
		if (this.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE)) {
			//During dialogue
			if (this.currentDialogue != null) {
				this.currentDialogue = null;
				this.dialogueIndex = 0;
				this.canAttack = true;
				this.buttonDelay = this.buttonDelayAmount;
			}
			///////////////////
			
			//Pause menu
			else {
				if (!Main.game.gamePaused) {
					Main.game.gamePaused = true;
					PauseMenu.highlightedSelection = 1;
				}
				else Main.game.gamePaused = false;
				this.buttonDelay = this.buttonDelayAmount * 2;
			}
			///////////////////
		}
		///////////////////
		
		//Ctrl - show controls
		if (this.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_CONTROL)) {
			if (!this.showControls) {
				this.showControls = true;
			} else {
				this.showControls = false;
			}
			this.buttonDelay = this.buttonDelayAmount;
		}
		///////////////////
		
		//M - Open map
		if (this.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_M)) {
			if (!this.mapOpen) {
				this.mapOpen = true;
				this.canAttack = false;
				this.buttonDelay = this.buttonDelayAmount;
				this.examinedEntity = null;
				this.levelMap.mapX = this.getMapX();
				this.levelMap.mapY = this.getMapY();
			}
			
			else {
				this.mapOpen = false;
				this.canAttack = true;
			}
		}
		///////////////////
		
		//Right click (3) - Check entity info
		if (Mouse.lastButtonPressed == 3 && this.clickDelay == 0) {
			Mouse.lastButtonPressed = -1;
			
			if (this.examinedEntity == null) {
				for (Entity e : this.level.getEntities()) {
					if (!this.level.getEntitiesToRemove().contains(e) && !e.equals(this) && e instanceof Entity_Character) {
						if (StdDraw.mouseX() >= e.x - e.minX && StdDraw.mouseY() <= e.y + (e.maxY * 2) && StdDraw.mouseX() <= e.x + e.maxX && StdDraw.mouseY() >= e.y - e.minY) {
							this.examinedEntity = (Entity_Character)e;
							break;
						} else {
							this.examinedEntity = null;
						}
					}
				}
			} else {
				this.examinedEntity = null;
			}
		}
		
		///////////////////
		if (this.debugmode) {
			/*if (this.editorGUI.par_rb_waypoint.isSelected() && (Mouse.lastButtonPressed == 1 && this.clickDelay == 0)){
				
			}*/
			
			if (Mouse.lastButtonPressed == 1 && this.clickDelay == 0) {
				this.clickDelay = this.clickDelayAmount;
				double[] levelCoordinates = getClickCoordinatesOnMap();
				this.ec.checkSelectedObject(levelCoordinates);
				Mouse.lastButtonPressed = -1;
			}
			
		}
		
		
		//Left click (1) - Select or deselect abilities; use selected ability on entity
		double xx = 30;
		
		//Starting a trade
		if (this.currentTrade == null && !this.inventoryOpen && !this.mapOpen && this.currentDialogue == null && this.currentShop == null && Mouse.lastButtonPressed == 1 && this.clickDelay == 0 && this.canStartTrade) {
			Mouse.lastButtonPressed = -1;
			this.canStartTrade = false;
			this.clickDelay = this.clickDelayAmount;
			
			PlayerMP tradeTargetPlayer = null;
			for (Entity e : new ArrayList<>(this.level.getEntities())) {
				if (e instanceof PlayerMP && e.name.equals(this.playerToTrade)) {
					tradeTargetPlayer = (PlayerMP)e;
					break;
				}
			}
			
			if (tradeTargetPlayer != null && GameUtilities.getDistance(this.getMapX(), this.getMapY(), tradeTargetPlayer.getMapX(), tradeTargetPlayer.getMapY()) <= MAXIMUM_TRADE_DISTANCE) {
				Packet014StartTrade startTradePacket = new Packet014StartTrade(tradeTargetPlayer.name, this.name);
				startTradePacket.writeData(Game.socketClient);
			}
		}
		///////////////////
		
		//Selecting/ deselecting abilities
		for (int i = 0; i < this.weapon.abilities.length; i++) {
			if (this.weapon.abilities[i] != null) {
				if (this.weapon.abilities[i].cooldown > 0) {
					xx += 8;
					continue;
				}
				
				if (Mouse.lastButtonPressed == 1 && StdDraw.mouseX() >= xx - 3.5 && StdDraw.mouseX() <= xx + 3.5 && StdDraw.mouseY() >= 10.1 - 3.5 && StdDraw.mouseY() <= 10.1 + 3.5) {
					if (this.selectedAbility != i + 1) this.selectedAbility = i + 1;
					else this.selectedAbility = 0;
					Mouse.lastButtonPressed = -1;
					
					//Blood leech abilitys
					if (this.selectedAbility > 0 && this.weapon.abilities[this.selectedAbility - 1] instanceof Ability_BloodLeech) {
						if (this.energy >= this.weapon.abilities[this.selectedAbility - 1].energyCost) {
							this.weapon.abilities[this.selectedAbility - 1].owner = this;
							this.weapon.abilities[this.selectedAbility - 1].isActive = true;
							this.selectedAbility = 0;
						}
					}
					///////////////////
					break;
				}
				xx += 8;
			}
		}
		///////////////////
		
		//Rapid heal ability
		if (Mouse.lastButtonPressed == 1 && this.selectedAbility > 0 && this.weapon.abilities[this.selectedAbility - 1] instanceof Ability_RapidHeal) {	
			for (Entity e : this.level.getEntities()) {
				if (!this.level.getEntitiesToRemove().contains(e) && e instanceof Entity_Character) {
					if (StdDraw.mouseX() >= e.x - e.minX && StdDraw.mouseX() <= e.x + e.maxX &&
					StdDraw.mouseY() >= e.y - e.minY && StdDraw.mouseY() <= e.y + e.maxY * 2) {
						
						Mouse.lastButtonPressed = -1;
						
						if (this.energy >= this.weapon.abilities[this.selectedAbility - 1].energyCost) {
							this.weapon.abilities[this.selectedAbility - 1].owner = this;
							this.weapon.abilities[this.selectedAbility - 1].targets.add((Entity_Character)e);
							this.weapon.abilities[this.selectedAbility - 1].isActive = true;
							this.selectedAbility = 0;
						}
						break;
					}
				}
			}
		}
		///////////////////
		
		
		
		
		
		////////////////////////////////////////////////////////////////////////
	}

	private void checkLevelConnectors() {
		for (Entity lc : this.level.getEntities()) {
			if (lc instanceof LevelConnector && lc.getMapX() + ((LevelConnector)lc).actionSize >= this.getMapX() - this.minX && lc.getMapX() - ((LevelConnector)lc).actionSize <= this.getMapX() + this.maxX && lc.getMapY() + ((LevelConnector)lc).actionSize >= this.getMapY() - this.minY && lc.getMapY() - ((LevelConnector)lc).actionSize <= this.getMapY() + this.maxY) {
				this.nearbyLevelConnector = (LevelConnector)lc;
				this.nearbyPlayer = null;
				return;
			} else {
				this.nearbyLevelConnector = null;
			}
		}
	}
	
	private void checkNearbyItems() {
		for (DroppedItem item : new ArrayList<>(this.level.getDroppedItems())) {
			if (this.level.getDroppedItemsToRemove().contains(item)) continue;

			if (GameUtilities.getDistance(this.x, this.y, item.x, item.y) <= item.actionSize) {
				this.nearbyItem = item;
				this.nearbyPlayer = null;
				return;
			} else {
				this.nearbyItem = null;
			}
		}
	}
	
	private void checkNearbyPlayers() {
		PlayerMP nearestPlayer = null;
		double nearestPlayerDistance = 0;
		double maxDistance = MAXIMUM_TRADE_DISTANCE;
		this.nearbyPlayer = null;
		
		for (Entity e : new ArrayList<>(this.level.getEntities())) {
			if (e instanceof PlayerMP && !e.equals(this)) {
				if (nearestPlayer == null && GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY()) <= maxDistance) {
					nearestPlayer = (PlayerMP)e;
					nearestPlayerDistance = GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY());
				}
				
				else if (nearestPlayer != null) {
					double possibleNearestPlayerDistance = GameUtilities.getDistance(this.getMapX(), this.getMapY(), e.getMapX(), e.getMapY());
					if (possibleNearestPlayerDistance < nearestPlayerDistance) {
						nearestPlayer = (PlayerMP)e;
						nearestPlayerDistance = possibleNearestPlayerDistance;
					}
				}
			}
		}
		
		if (nearestPlayer != null) this.nearbyPlayer = nearestPlayer;
	}
	
	private double[] getClickCoordinatesOnMap() {
		double[] coordinates = new double[]{this.getMapX() - (this.x - StdDraw.mouseX()), this.getMapY() - (this.y - StdDraw.mouseY())};
		
		return coordinates;
	}

	@Override
	public void checkTimers() {
		if (this.attackDelayTimer > 0) this.attackDelayTimer--;
		if (this.attackDelayTimer < 0) this.attackDelayTimer = 0;
		if (this.buttonDelay > 0) this.buttonDelay--;
		if (this.clickDelay > 0) this.clickDelay--;
		if (this.levelTransitionDelay > 0) {
			this.levelTransitionDelay -= 5;
			if (this.levelTransitionDelay < 0) this.levelTransitionDelay = 0;
		}
		
		if (this.combatTimer > 0) {
			this.combatTimer--;
			if (this.combatTimer == 0) {
				this.isInCombat = false;
				this.lastTarget = null;
			}
		}
		
		if (this.meleeLevelUpTimer > 0) {
			this.meleeLevelUpTimer--;
		}
		
		else if (this.archeryLevelUpTimer > 0) {
			this.archeryLevelUpTimer--;
		}
		
		else if (this.magicLevelUpTimer > 0) {
			this.magicLevelUpTimer--;
		}
		
		else if (this.defenseLevelUpTimer > 0) {
			this.defenseLevelUpTimer--;
		}
	}

}
