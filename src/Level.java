import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public abstract class Level implements Serializable {
	
	private static final long serialVersionUID = 676484830365553677L;
	
	public World world;
	
	public double xMax, yMax;
	public String name;
	
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList<Entity> entitiesToAdd = new ArrayList<Entity>();
	private ArrayList<Entity> entitiesToRemove = new ArrayList<Entity>();
	
	private ArrayList<DroppedItem> droppedItems = new ArrayList<DroppedItem>();
	private ArrayList<DroppedItem> droppedItemsToAdd = new ArrayList<DroppedItem>();
	private ArrayList<DroppedItem> droppedItemsToRemove = new ArrayList<DroppedItem>();
	
	public ArrayList<OverheadDamage> overheadDamage = new ArrayList<OverheadDamage>();
	public ArrayList<OverheadDamage> overheadDamageToAdd = new ArrayList<OverheadDamage>();
	public ArrayList<OverheadDamage> overheadDamageToRemove = new ArrayList<OverheadDamage>();
	
	public ArrayList<TileOverlay> tileOverlays = new ArrayList<TileOverlay>();
	public ArrayList<TileOverlay> tileOverlaysToAdd = new ArrayList<TileOverlay>();
	public ArrayList<TileOverlay> tileOverlaysToRemove = new ArrayList<TileOverlay>();
	
	public ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	
	public Tile[][] tiles;
	
	public double tileSize = 7; //128x128 pixel tile - the larger this number, less lag; 7.11108 at 900 canvasSize works, 7 seems to function for any size (possibly due to rules of 8-bit numbers)
	
	public boolean firstRender = false;
	public boolean isOutside;
	
	public void update() {
		for (Tile[] tArray : this.tiles) {
        	for (Tile t : tArray) {
            	t.update();
            }
		}
		
		for (TileOverlay to : this.tileOverlays) to.update();
		this.tileOverlays.removeAll(this.tileOverlaysToRemove);
		this.tileOverlaysToRemove.clear();
		this.tileOverlays.addAll(this.tileOverlaysToAdd);
		this.tileOverlaysToAdd.clear();
		
		for (DroppedItem i : this.droppedItems) i.update();
		this.droppedItems.removeAll(this.droppedItemsToRemove);
		this.droppedItemsToRemove.clear();
		this.droppedItems.addAll(this.droppedItemsToAdd);
		this.droppedItemsToAdd.clear();

		//Ensures that entities behind another entity will render behind the entity in front of them
		Collections.sort(getEntities());
		/////////////////////////////////
		
		for (Entity e : new ArrayList<>(getEntities())) {
			if (e instanceof Entity_Character && ((Entity_Character)e).currentHealth <= 0) {
				((Entity_Character)e).death();
			}
			
			else {
				if (this.world.currentPlayer != null && this.world.currentPlayer.levelTransitionDelay == 0 && this.world.currentPlayer.level.equals(this) && e instanceof LevelConnector) {
					((LevelConnector)e).destination.update();
				}
				if (e.level != null) e.checkIfStuck();
				if (e instanceof Entity_Character) ((Entity_Character)e).updateStats();
				e.checkTimers();
				e.update();
				if (e instanceof Entity_Character) {
					((Entity_Character)e).updateAbilities();
					((Entity_Character)e).updateKnockback();
					((Entity_Character)e).updateEffects();
				}
				if (e instanceof Entity_NPC) ((Entity_NPC)e).updateMinimapColor();
			}
		}
		
		getEntities().removeAll(getEntitiesToRemove());
		getEntitiesToRemove().clear();
		
		for (Entity e : new ArrayList<>(getEntitiesToAdd())) {
			if (e instanceof Entity_Character) {
				if (System.currentTimeMillis() - ((Entity_Character)e).respawnTimer >= 30000) {
					getEntities().add(e);
					((Entity_Character)e).respawnTimer = 0;
				}
			} else {
				if (!getEntities().contains(e)) getEntities().add(e);
			}
		}
		this.getEntitiesToAdd().clear();
		
		for (OverheadDamage d : this.overheadDamage) d.update();
		this.overheadDamage.removeAll(this.overheadDamageToRemove);
		this.overheadDamageToRemove.clear();
		this.overheadDamage.addAll(this.overheadDamageToAdd);
		this.overheadDamageToAdd.clear();
		
		if (this.firstRender) {
			this.world.currentPlayer.checkCollision();
		}
		
		for ( Waypoint w : waypoints ){
			w.update();
		}
	}
	
	public void render() {
		
		for ( Waypoint w : waypoints ){
			w.render();
		}
		
		//Prevents slow loading when using tiles
		if (this.firstRender) {
			this.firstRender = false;
			StdDraw.setFont(new Font("Arial", Font.BOLD, 34));
			StdDraw.text(50, 57, "Loading...");
			StdDraw.text(50, 50, "If this message doesn't go away after 5 seconds,");
			StdDraw.text(50, 43, "the game crashed.");
			StdDraw.show(1);
		}
		/////////////////////////////////
		
		//Render tiles
		for (Tile[] tArray : this.tiles) {
        	for (Tile t : tArray) {
            	if (t.shouldRender) t.render();
            }
		}
		/////////////////////////////////
		
		for (TileOverlay to : this.tileOverlays) if (to.shouldRender) to.render();
		
		for (DroppedItem i : this.droppedItems) if (i.shouldRender) i.render();
		
		for (Entity e : new ArrayList<>(this.entities)) {
			
			if (e instanceof Entity_NPC) {
				e.render();
			}
				
			else if (e instanceof LevelObject) {
				if (((LevelObject)e).shouldRender) e.render();
			}
			
			else if (e instanceof Projectile) {
				if (((Projectile)e).shouldRender) e.render();
			}
			
			else if (e instanceof LevelConnector) {
				if (((LevelConnector)e).shouldRender) e.render();
			}
			
			else {
				e.render();
			}
		}
		
		for (OverheadDamage d : this.overheadDamage) if (d.shouldRender) d.render();
		
		if (this.world.currentPlayer.debugmode) {
			this.world.currentPlayer.editorGUI.render();
		}
	}
	
	public void setLevelConnectors() {
		if (this instanceof Level_Test) {
			Level destination = null;
			
			for (Level l : this.world.levels) {
				//if (l instanceof Level_Test2) destination = l;
				if (l instanceof Sandbox) { 
					destination = l;
					//Main.player = new Player_Sandbox("Debugger", 50, 50, 4, 5);
				}
			}
			if (destination != null) {
				LevelConnector connector = new LevelConnector(this, destination, "helmet_gold.png", 1, 32, 295, 22, 2, 5);
				this.getEntitiesToAdd().add(connector);
			}
		}
		
		else if (this instanceof Level_Test_BrickHouse) {
			Level destination = null;
			
			for (Level l : this.world.levels) {
				if (l instanceof Level_Test) destination = l;
			}
			if (destination != null) {
				//House exit - put this code inside Level_Test where it makes sense, instead of being in a random place
				//this.entitiesToAdd.add(new LevelConnector(this, destination, "olddoor.png", 18, -2, 18, 5, 5, 8));
				//////////////////////////////
			}
		}
	}
	/*
	public void saveLevel(){
		String mapName = JOptionPane.showInputDialog("Map name:");
		JOptionPane.showMessageDialog(null, mapName + " saved");
		String dir = System.getProperty("user.dir").concat("\\maps");
		File npcFile = new File(dir);
		File tileFile = new File(dir);
		
		try{
			FileOutputStream fileOut = new FileOutputStream("Tile.txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			/*
			for(int xx = 0; xx < tiles.length; xx++){
				for(int yy = 0; yy <tiles[xx].length; yy++){
					out.writeObject(tiles[xx][yy]);
				}
			}
			
			out.writeObject(this);
			out.close();
			fileOut.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try {
			if (!npcFile.exists())	npcFile.createNewFile();
			//ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
			FileOutputStream fileOut = new FileOutputStream("NPC.txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			for (Entity e : entities){
				out.writeObject(e);
			}
			
			out.close();
			fileOut.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public Level_Test loadLevel(){
		JFileChooser chooser = new JFileChooser();
		File selectedFile = null;
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}
		
		Level_Test loadedLevel = null;
		String filepath = "";
		try {
		    filepath = selectedFile.getCanonicalPath();
		} catch(IOException e) {
			e.printStackTrace();
		}
		try{
			FileInputStream fileIn = new FileInputStream(filepath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			
			loadedLevel = (Level_Test) in.readObject();
		}catch(IOException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		
		return (loadedLevel);
	}
	/*public void saveLevel() {
		String mapName = JOptionPane.showInputDialog("Map name:");
		JOptionPane.showMessageDialog(null, mapName + " saved");
		String dir = System.getProperty("user.dir").concat("\\maps");
		File tileFile = new File(dir);
		File npcFile = new File(dir);

		try {
			if (!tileFile.exists()) tileFile.createNewFile();
			
			FileWriter fw = new FileWriter(mapName + ".txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			// Save the tiles into the file, 0 is grass, 1 is blank
			for(int xx = 0; xx < tiles.length; xx++){
				for(int yy = 0; yy <tiles[xx].length; yy++){
					if 		(tiles[xx][yy].filename == "grass.png") 		bw.write('0');
					else if (tiles[xx][yy].filename == "blank_tile.png")  	bw.write('1');
					else if (tiles[xx][yy].filename == "grassSpikes.png")	bw.write('2');
					else if (tiles[xx][yy].filename == "mud_tile.png")		bw.write('3');
				}
			}
		
			
			bw.close();
			System.out.println("Save Successful...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			if (!npcFile.exists())	npcFile.createNewFile();
			//ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
			FileOutputStream fileOut = new FileOutputStream("NPC.txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			for (Entity e : entities){
				out.writeObject(e);
			}
			
			out.close();
			fileOut.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadLevel() {
		JFileChooser chooser = new JFileChooser();
		File selectedFile = null;
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}
		
		Level loadedLevel = null;
		String filepath = "";
		try {
		    filepath = selectedFile.getCanonicalPath();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		Level openedLevel = new Level_Test2(player, "Saved Level", 1200, 1200 );
		try {
			Scanner s = new Scanner(new BufferedReader(new FileReader(filepath)));
			// Load the appropriate tiles into the array
			String str = "";
			while (s.hasNext()) {
				str = str.concat(s.next()); 
			}
			// The array that stores the integer character for determining the tile type
			char[] sequence = str.toCharArray();
			int count = 0;
			//////////////////////////////////////
			// Loads the tiles that were saved in the file and renders them onto the map
			// This was the algorithm taken from Level_Test and now properly creates the map
			for (int xx = 0; xx < tiles.length; xx++) {
				for (int yy = 0; yy < tiles[xx].length; yy++, count++) {
					// If it is a grass tile
					if 			(sequence[count] == '0') {
						if (xx == 0 && yy == 0) tiles[xx][yy] = new Tile_Grass(this, xx, yy);
						else if (xx == 0) 		tiles[xx][yy] = new Tile_Grass(this, xx, this.tileSize * 2 * yy);
						else if (yy == 0) 		tiles[xx][yy] = new Tile_Grass(this, this.tileSize * 2 * xx, yy);
				        else 					tiles[xx][yy] = new Tile_Grass(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
					}
					// If it is a blank tile
					else if		(sequence[count] == '1') {
						if (xx == 0 && yy == 0) tiles[xx][yy] = new Tile_Blank(this, xx, yy);
						else if (xx == 0) 		tiles[xx][yy] = new Tile_Blank(this, xx, this.tileSize * 2 * yy);
						else if (yy == 0) 		tiles[xx][yy] = new Tile_Blank(this, this.tileSize * 2 * xx, yy);
				        else 					tiles[xx][yy] = new Tile_Blank(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
					}
					else if		(sequence[count] == '2') {
						if (xx == 0 && yy == 0) tiles[xx][yy] = new Tile_Spikes(this, xx, yy);
						else if (xx == 0) 		tiles[xx][yy] = new Tile_Spikes(this, xx, this.tileSize * 2 * yy);
						else if (yy == 0) 		tiles[xx][yy] = new Tile_Spikes(this, this.tileSize * 2 * xx, yy);
				        else 					tiles[xx][yy] = new Tile_Spikes(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
					}
					else if		(sequence[count] == '3') {
						if (xx == 0 && yy == 0) tiles[xx][yy] = new Tile_Mud(this, xx, yy);
						else if (xx == 0) 		tiles[xx][yy] = new Tile_Mud(this, xx, this.tileSize * 2 * yy);
						else if (yy == 0) 		tiles[xx][yy] = new Tile_Mud(this, this.tileSize * 2 * xx, yy);
				        else 					tiles[xx][yy] = new Tile_Mud(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
					}
				}
			}
			/////////////////////////////////////
			openedLevel.tiles = tiles;
			System.out.println("Load Successful...");
		} catch(Exception e) {
			e.printStackTrace();
		}
			
		// Change current level to the loaded level
		Main.currentLevel = openedLevel;
		player.minimap = new MiniMap(openedLevel, player);
		player.minimap.update();
		//////////////////////////////////////
	}*/
	
	public synchronized ArrayList<Entity> getEntities() {
		return this.entities;
	}
	
	public synchronized ArrayList<Entity> getEntitiesToAdd() {
		return this.entitiesToAdd;
	}
	
	public synchronized ArrayList<Entity> getEntitiesToRemove() {
		return this.entitiesToRemove;
	}

	public void removePlayerMP(String username) {
		for (Entity e : new ArrayList<>(getEntities())) {
			if (e instanceof PlayerMP && ((PlayerMP)e).name.equals(username)) {
				getEntitiesToRemove().add(e);
				break;
			}
		}
	}
	
	public synchronized ArrayList<DroppedItem> getDroppedItems() {
		return this.droppedItems;
	}
	
	public synchronized ArrayList<DroppedItem> getDroppedItemsToAdd() {
		return this.droppedItemsToAdd;
	}
	
	public synchronized ArrayList<DroppedItem> getDroppedItemsToRemove() {
		return this.droppedItemsToRemove;
	}
}
