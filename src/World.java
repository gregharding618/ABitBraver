import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

public class World implements Serializable {
	
	private static final long serialVersionUID = 6985159114960070497L;
	
	public final Level startLevel;
	public List<Level> levels = new ArrayList<Level>();
	
	public Player normalPlayer;
	public Player currentPlayer;
	
	public boolean debugmode;
	
	public Time time;
	public Weather weather;
	
	private List<String> chatMessages = new ArrayList<String>();
	private List<String> chatMessagesToAdd = new ArrayList<String>();
	private List<String> chatMessagesToRemove = new ArrayList<String>();
	
	public World(Player player) {
		this.currentPlayer = player;
		
		this.time = new Time(this, 65000, (int) (65000 * 0.21), 0.15, 0.21, 0.65, 0.71);
		this.weather = new Weather(this, 0, 0, 12500, 70000);
		
		//this.normalPlayer = new Player("Greg", true, 25, 50, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace());
		//this.debugger = new Player_Sandbox("Debugger", true, 4, 5);
		//if (inDebugMode) {
		//	this.currentPlayer = this.debugger;
		//} else {
		//	this.currentPlayer = this.normalPlayer;
		//}
		
		for (Level l : this.levels) l.setLevelConnectors();
		
		//Briarchase start house
		Level start = new Level_Briarchase_PlayersHouse(this, "my house");
		if (this.currentPlayer.level == null) {
			this.currentPlayer.level = start;
			this.currentPlayer.level.getEntitiesToAdd().add(this.currentPlayer);
		}
		this.startLevel = start;
		this.levels.add(start);
		/////////////////////
		
		this.currentPlayer.minimap = new MiniMap(this.currentPlayer.level, this.currentPlayer);
		this.currentPlayer.levelMap = new LevelMap(this.currentPlayer);
		this.debugmode = false;
		
		//Rest of the game world
		generateAllLevels(start);
		/////////////////////
		
		player.level.firstRender = true;
	}
	
	public void update() {
		if (this.debugmode == true) {
			if (this.currentPlayer.editorGUI != null) this.currentPlayer.editorGUI.update();
		}
		
		//Update and clean chat memory
		this.chatMessages.removeAll(this.chatMessagesToRemove);
		this.chatMessagesToRemove.clear();
		this.chatMessages.addAll(this.chatMessagesToAdd);
		this.chatMessagesToAdd.clear();
		while (this.chatMessages.size() > 40) {
			this.chatMessages.remove(0);
		}
		/////////////////////////
		
		//Update weather
		this.weather.update();
		/////////////////////////
			
		//Update time and light information
		this.time.update();
		/////////////////////////
				
		this.currentPlayer.level.update();
	}
	
	public void render() {
		this.currentPlayer.level.render();
		
		//Render weather
		if (this.currentPlayer.level.isOutside) {
			this.weather.render();
		}
		/////////////////////////
		
		//Light level
		this.time.normalizeLightLevel();
		if (this.currentPlayer.level.isOutside && this.weather.lightningTimer >= 26) {
			StdDraw.setPenColor(new Color(255, 255, 255, 112));
			StdDraw.filledSquare(50, 50, 51);
		} else if (this.currentPlayer.level.isOutside && this.weather.lightningTimer <= 20 && this.weather.lightningTimer >= 16) {
			if (ThreadLocalRandom.current().nextDouble(0, 100) <= 28) {
				StdDraw.setPenColor(new Color(255, 255, 255, 99));
				StdDraw.filledSquare(50, 50, 51);
			}
		} else {
			StdDraw.setPenColor(new Color(0, 0, 0, this.time.getLightLevel()));
			StdDraw.filledSquare(50, 50, 51);
		}
		/////////////////////////
		
		//Ensures nothing renders over the player's GUI
		this.currentPlayer.renderGuiObjects();
		/////////////////////////////////
	}
	
	//Will be fixed later
	public void setDebugMode(boolean inDebugMode) {
		/*this.debugmode = inDebugMode;
		
		if (inDebugMode) {
			this.currentPlayer = this.debugger;
			this.currentPlayer.ec = new EditorController(this.debugger);
			this.currentPlayer.editorGUI = new GUI_MapEditor_V2(this.debugger);
		} else {
			this.currentPlayer = this.normalPlayer;
		}
		
		for (Level level : this.levels) {
			level.entities.remove(this.normalPlayer);
			level.entities.remove(this.debugger);
			level.entities.add(this.currentPlayer);
		}
		
		this.currentPlayer.level = this.currentPlayer.level;
		this.currentPlayer.minimap = new MiniMap(this.currentPlayer.level, this.currentPlayer);
		this.currentPlayer.levelMap = new LevelMap(this.currentPlayer);
		
		this.weather.player = this.currentPlayer;*/
	}
	
	private void generateAllLevels(Level startLevel) {
		//Create briarchase and house door (inside and outside)
		Level briarchase = new Level_Briarchase(this, "Briarchase");
		briarchase.getEntitiesToAdd().add(new LevelConnector(briarchase, startLevel, "olddoor.png", 65, 69, (startLevel.xMax / 2) + startLevel.tileSize, 5, 2, 8));
		this.levels.add(briarchase);
		startLevel.getEntitiesToAdd().add(new LevelConnector(startLevel, briarchase, "olddoor.png", (startLevel.xMax / 2) + startLevel.tileSize, -2, 65, 65, 2, 8));
		///////////////////////
	}
	
	public synchronized List<String> getChatMessages() {
		return this.chatMessages;
	}
	
	public synchronized List<String> getChatMessagesToAdd() {
		return this.chatMessagesToAdd;
	}

	public synchronized List<String> getChatMessagesToRemove() {
		return this.chatMessagesToRemove;
	}

}
