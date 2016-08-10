import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditorController implements Serializable {

	private static final long serialVersionUID = -9179968217926777084L;
	
	//Debugger using this controller
	public Player debugger;
	
	private String[] selectionOptions;
	private String[] tileReplacementOptions;
	private String[] tiles;
	private String[] items;
	private String[] NPCs;
	private String[] MISC;
	private String[] entity;
	private String[] faction;
	private String[] levelConnector;
	
	// Equipment
	private String[] weapon;
	private String[] shield;
	private String[] glove;
	private String[] boot;
	private String[] helmet;
	private String[] leg;
	private String[] chest;
	
	// Level Objects
	private String[] levelObjects;
	
	// Shop Owner Stocks
	private Item[] stock_basic;
	private Item[] stock_intermediate;
	private Item[] stock_advanced;
	private Item[] stock_food;
	
	// Dialogue
	public int dialogueIndex;
	
	//Constructor
	public EditorController(Player p) {	
		this.debugger = p;
		
		selectionOptions 	= new String[] {"- None -", "Tile", "Entity", "Object", "Level Connector", "Dropped Item"};
		tileReplacementOptions 	= new String[] {"- None -", "Blank", "Grass"};
		tiles   			= new String[] {"- None -", "Grass", "Blank", "Mud", "Briarchase Wood Floor", "Wood Floor", "Spikes", "Water"};
		items   			= new String[] {"- None -", "Helmet", "Armor", "Gloves", "Food", "Potion"};
		NPCs    			= new String[] {"- None -", "NPC Test", "NPC Dialogue Test", "NPC Shop Owner"};
		MISC    			= new String[] {"- None -", "MISC1", "MISC2", "MISC3"};
		entity  			= new String[] {"- None -", "Entity1", "Entity2"};
		weapon	 			= new String[] {"- None -", "Sword", "Axe", "Warhammer"};
		shield   			= new String[] {"- None -", "Helmet", "Chest"};
		faction				= new String[] {"- None -", "Friendly", "Enemy"};
		levelConnector		= new String[] {" -None -", "Level Connector"};
		
		weapon 				= new String[] {"- None -", "Test Weapon"};
		shield				= new String[] {"- None -", "Test Shield"};
		glove				= new String[] {"- None -", "Test Glove"};
		boot 				= new String[] {"- None -", "Test Boot"};
		helmet				= new String[] {"- None -", "Test Helmet"};
		leg					= new String[] {"- None -", "Test Leg"};
		chest 				= new String[] {"- None -", "Test Chest"};
		
		levelObjects		= new String[] {"- None -", "Brick House 1", "Horiz. Brick Wall", "Vert. Brick Wall", "Candle", "Rock 1", "Rock 2", "Rock Wall",
											"Tree Base 1", "Tree Trunk 1", "Tree Top 1"};
		
		//stock_basic			= new Item[]   {new Item_Equipment("Dagger")
		//stock_intermediate;
		//stock_advanced;
		//stock_food;
		
				dialogueIndex = 0;
		
		/**public Item_Equipment(
		 * String iconImageFile, 
		 * String entityImageFile, 
		 * String name, 
		 * boolean stackable, 
		 * int amount, 
		 * boolean isMelee, 
		 * boolean isArchery, 
		 * boolean isMagic, 
		 * int slot, 
		 * int value, 
		 * double range, 
		 * int speed, 
		 * double accuracy, 
		 * int maxHit, 
		 * int armor, 
		 * int meleeBoost, 
		 * int archeryBoost, 
		 * int magicBoost) {
		*
		*/
		
	}
	
	/**
	 * Add a way point to the selected NPC pathway
	 * @param xx
	 * @param yy
	 * @param e
	 */
	public void addWaypointForSelectedEntity(Level level, Entity_Character e, double xx, double yy){
		//e.waypoint_path.addWaypoint(level, xx, yy);
		Main.game.world.currentPlayer.level.waypoints.add(new Waypoint (level, e, xx, yy, 0));
	}
	
	//Mouse was clicked, now figure out what the player clicked on
	public void checkSelectedObject(double[] coordinates) {
		double[] levelCoordinates = coordinates;
		
		if (this.debugger.editorGUI.rb_tile.isSelected()) {
			boolean tileSelected = false;
			for (Tile[] tArray : debugger.level.tiles) {
				for (Tile t : tArray) {
					if (levelCoordinates[0] <= t.mapX + debugger.level.tileSize && 
						levelCoordinates[0] >= t.mapX - debugger.level.tileSize &&
						levelCoordinates[1] <= t.mapY + debugger.level.tileSize && 
						levelCoordinates[1] >= t.mapY - debugger.level.tileSize) {
						
						if (this.debugger.editorGUI.selectedTile == null || !this.debugger.editorGUI.selectedTile.equals(t)) {
							this.debugger.editorGUI.selectedTile = t;
							//Mouse.lastButtonPressed = -1;
						}
						tileSelected = true;
						if (tileSelected) break;
					}
				}
				if (tileSelected) break;
			}
		}
		else if (this.debugger.editorGUI.rb_npc.isSelected()) {
			
			for (Entity e : debugger.level.getEntities()) {
				if (!(e instanceof Entity_NPC)) continue;
				
				if (levelCoordinates[0] <= e.getMapX() + e.maxX && 
						levelCoordinates[0] >= e.getMapX() - e.minX &&
						levelCoordinates[1] <= e.getMapY() + e.maxY * 2 && 
						levelCoordinates[1] >= e.getMapY() - e.minY) {
					
					this.debugger.editorGUI.selectedEntity = e;
					this.debugger.editorGUI.selectedNPC = (Entity_NPC)e;
					this.debugger.editorGUI.entityIsSelected = true;
					debugger.editorGUI.setSelectedEntityInfo((Entity_NPC)e);
					
					break;
				}
			}
		}
	}
	
	//Save the current created map
	public void saveMap() {
		this.debugger.editorGUI.selectedEntity = null;
		this.debugger.editorGUI.selectedTile = null;
		this.debugger.editorGUI.selectedNPC = null;
		Main.saveWorld();
	}
	
	//Load a previous map
	public void loadMap() {
		World sb = (World) Main.loadWorld();								// The serialized level from the file is pointed to by sb
		this.debugger.editorGUI.frame.dispose();								// Dispose of previous editor GUI
		
		Main.game.world = sb;
		
		//Main.game.world.currentPlayer.level = sb;													// Set mains current level to the loaded level
		//Main.game.world.player.level = sb;												// Set debuggers level to loaded level
		//Main.game.world.levels.add(sb);													// Add the level to the levels list
		//Main.game.world.player = (Player_Sandbox)sb.player;								// Set the loaded debugger as the current debugger
		//Main.game.world.currentPlayer.level.entities.add(Main.game.world.player);							// Add it to the entities list
		//Main.game.world.player.ec = sb.player.ec;										// Set the editor controller to the loaded editor controller
		//Main.game.world.player.editorGUI = sb.player.editorGUI;							// Set the GUI to the loaded GUI

		this.debugger = sb.currentPlayer;								// Set this classes debugger to the loaded debugger
		
		this.debugger.ec = new EditorController(this.debugger);					// Set this debuggers editor controller to the loaded one
		this.debugger.editorGUI = new GUI_MapEditor_V2(this.debugger);			// Set this debuggers map editor GUI to the loaded one
		
		// Some of this code may be useless. I will optimize it later.
	}
	
	//Create new map
	public void newMap(double xx, double yy, String tileType) {
		World world = new World(this.debugger);
		Main.game.world = world;
		world.currentPlayer.level = new Sandbox(world, tileType, "New Level", xx, yy);
		world.currentPlayer.level.firstRender = true;
	}
	
	//Halt NPC's
	public void haltNPC() {
		for (Entity e : Main.game.world.currentPlayer.level.getEntities()) {
			if (e instanceof Entity_Character) ((Entity_Character)e).isFrozen = true;
		}
	}
	
	//Activate NPC's
	public void activateNPC() {
		for (Entity e : Main.game.world.currentPlayer.level.getEntities()) {
			if (e instanceof Entity_Character) ((Entity_Character)e).isFrozen = false;
		}
	}
	
	//Add Dialogue
	public void addDialogueToList(String msg, Entity_NPC_DialogueTest entity) {
		Dialogue d = new Dialogue(entity, msg);
		try {
			if ( dialogueIndex <= entity.dialogues.size() - 1 ) {
				entity.dialogues.set(dialogueIndex, d);
			}
			else {
				entity.dialogues.add(d);
				dialogueIndex++;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Remove Dialogue
	public String removedialogueFromList(Entity_NPC_DialogueTest entity) {
		try {
			if 		( entity.dialogues.isEmpty() ){
				dialogueIndex = 0;
				return ("");
			}
			else if ( entity.dialogues.size() == 1 )	{
				dialogueIndex = 0;
				entity.dialogues.clear();
				return ( "" );
			}
			else {
				entity.dialogues.remove( dialogueIndex );
				if ( dialogueIndex <= 0){
					if 		( entity.dialogues.isEmpty() ){
						dialogueIndex = 0;
						return ("");
					}else{
					return ( "" );
					}
				}
				else{
					dialogueIndex--;
					return ( "" );
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("List empty");
		}
		return ( null );
		
	}
	
	//Right Arrow
	public String moveRightMsg(Entity_NPC_DialogueTest entity) {
		try {
			if ( dialogueIndex >= entity.dialogues.size() - 1 && !entity.dialogues.isEmpty()) {
				dialogueIndex = entity.dialogues.size() - 1;
				return ( entity.dialogues.get(entity.dialogues.size() - 1).text );
			}
			else if ( entity.dialogues.isEmpty() ){
				return ("");
			}			
			else {
				dialogueIndex++;
				return ( entity.dialogues.get(dialogueIndex).text );
			}
		} catch (Exception e) {
			e.printStackTrace();
			return("null");
		}
	}
	
	//Left Arrow
	public String moveLeftMsg(Entity_NPC_DialogueTest entity) {
		try {
			dialogueIndex--;
			if ( dialogueIndex < 0 && !entity.dialogues.isEmpty() ) {
				dialogueIndex = 0;
				return ( entity.dialogues.get( 0 ).text );
			}
			else if ( entity.dialogues.isEmpty() ){
				return ("");
			}		
			else{
				return ( entity.dialogues.get(dialogueIndex).text );
			}
		} catch(Exception e) {
			e.printStackTrace();
			return("null");
		}
	}
	
	//Add tile
	public void addTile(String tile, int xx, int yy) {
		switch (tile) {
		case "Blank":
			this.debugger.level.tiles[xx][yy] = new Tile_Blank(this.debugger.level.tiles[xx][yy].level, this.debugger.level.tiles[xx][yy].mapX, this.debugger.level.tiles[xx][yy].mapY);
			this.debugger.editorGUI.selectedTile = this.debugger.level.tiles[xx][yy];
			break;
		case "Grass":
			this.debugger.level.tiles[xx][yy] = new Tile_Grass(this.debugger.level.tiles[xx][yy].level, this.debugger.level.tiles[xx][yy].mapX, this.debugger.level.tiles[xx][yy].mapY);
			this.debugger.editorGUI.selectedTile = this.debugger.level.tiles[xx][yy];
			break;
		case "Mud":
			this.debugger.level.tiles[xx][yy] = new Tile_Mud(this.debugger.level.tiles[xx][yy].level, this.debugger.level.tiles[xx][yy].mapX, this.debugger.level.tiles[xx][yy].mapY);
			this.debugger.editorGUI.selectedTile = this.debugger.level.tiles[xx][yy];
			break;
		case "Spikes":
			this.debugger.level.tiles[xx][yy] = new Tile_Spikes(this.debugger.level.tiles[xx][yy].level, this.debugger.level.tiles[xx][yy].mapX, this.debugger.level.tiles[xx][yy].mapY);
			this.debugger.editorGUI.selectedTile = this.debugger.level.tiles[xx][yy];
			break;
		case "Wood Floor":
			this.debugger.level.tiles[xx][yy] = new Tile_WoodFloor(this.debugger.level.tiles[xx][yy].level, this.debugger.level.tiles[xx][yy].mapX, this.debugger.level.tiles[xx][yy].mapY);
			this.debugger.editorGUI.selectedTile = this.debugger.level.tiles[xx][yy];
			break;
		case "Briarchase Wood Floor":
			this.debugger.level.tiles[xx][yy] = new Tile_Briarchase_Woodfloor(this.debugger.level.tiles[xx][yy].level, this.debugger.level.tiles[xx][yy].mapX, this.debugger.level.tiles[xx][yy].mapY);
			this.debugger.editorGUI.selectedTile = this.debugger.level.tiles[xx][yy];
			break;
		case "Water":
			this.debugger.level.tiles[xx][yy] = new Tile_Animated(this.debugger.level.tiles[xx][yy].level, this.debugger.level.tiles[xx][yy].mapX, this.debugger.level.tiles[xx][yy].mapY, "water_1.png", "water_2.png", "water_3.png");
			this.debugger.editorGUI.selectedTile = this.debugger.level.tiles[xx][yy];
			break;
		}
	}
	
	//Add object
	public void addObject(String object, double xx, double yy) {
		LevelObject objectToAdd = null;
		
		switch (object) {
		case "Rock 1":
			objectToAdd = new LevelObject_Rock1(Main.game.world.currentPlayer.level, xx, yy);
			break;
		case "Rock 2":
			objectToAdd = new LevelObject_Rock1(Main.game.world.currentPlayer.level, xx, yy);
			objectToAdd.imageFile = "rock_2.png";
			break;
		case "Rock Wall":
			objectToAdd = new LevelObject_RockWall(Main.game.world.currentPlayer.level, xx, yy);
			objectToAdd.imageFile = "rock_wall.png";
			break;
		case "Tree Base 1":
			objectToAdd = new LevelObject_TreeBase1(Main.game.world.currentPlayer.level, xx, yy);
			break;
		case "Tree Trunk 1":
			objectToAdd = new LevelObject_TreeTrunk1(Main.game.world.currentPlayer.level, xx, yy);
			break;
		case "Tree Top 1":
			objectToAdd = new LevelObject_TreeTop1(Main.game.world.currentPlayer.level, xx, yy);
			break;
		case "Brick House 1":
			objectToAdd = new LevelObject_BrickHouse(Main.game.world.currentPlayer.level, xx, yy);
			break;
		case "Horiz. Brick Wall":
			objectToAdd = new LevelObject_BrickWall_Horizontal(Main.game.world.currentPlayer.level, xx, yy);
			break;
		case "Vert. Brick Wall":
			objectToAdd = new LevelObject_BrickWall_Vertical(Main.game.world.currentPlayer.level, xx, yy);
			break;
		case "Candle":
			objectToAdd = new LevelObject_Candle(Main.game.world.currentPlayer.level, xx, yy);
			break;
		}
		Main.game.world.currentPlayer.level.getEntitiesToAdd().add(objectToAdd);
	}
	
	//Add NPC
	public void addNPC(String npcType, double xx, double yy) {
		/*String name					= this.debugger.editorGUI.par_tb_name.getText();
		//String npcType				= (String)this.debugger.editorGUI.par_co_npcType.getSelectedItem();
		String faction				= (String)this.debugger.editorGUI.par_co_faction.getSelectedItem();
		String dialogue				= this.debugger.editorGUI.par_ta_dialogue.getText();
		double maxXWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_maxXwalk.getText());
		double maxYWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_maxYwalk.getText());
		double minXWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_minXwalk.getText());
		double minYWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_minYwalk.getText());
		double speed				= Double.parseDouble(this.debugger.editorGUI.par_tb_speed.getText());
		double maxHealth			= Double.parseDouble(this.debugger.editorGUI.par_tb_maxHealth.getText());
		double currentHealth		= Double.parseDouble(this.debugger.editorGUI.par_tb_currentHealth.getText());
		boolean aggressive 			= this.debugger.editorGUI.par_cb_aggressive.isSelected();
		boolean canAttack 			= this.debugger.editorGUI.par_cb_canAttack.isSelected();
		boolean canBeAttacked 		= this.debugger.editorGUI.par_cb_canBeAttacked.isSelected();
		boolean canTalk				= this.debugger.editorGUI.canSpeak;
	*/
		
		switch ( npcType ) {
		
		case "NPC Test":
			Main.game.world.currentPlayer.level.getEntitiesToAdd().add(new Entity_NPC_Test(Main.game.world.currentPlayer.level, true, true, "Enemy", "Enemy", xx, yy, xx + 25, yy + 26, xx - 10, yy - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
			break;
		case "NPC Dialogue Test":
			Main.game.world.currentPlayer.level.getEntitiesToAdd().add(new Entity_NPC_DialogueTest(Main.game.world.currentPlayer.level, "Friendly", "Speaker", xx, yy, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
			break;
		case "NPC Shop Owner":
			Main.game.world.currentPlayer.level.getEntitiesToAdd().add(new Entity_NPC_ShopOwner(Main.game.world.currentPlayer.level, "Shop Owner", xx, yy, 0, 0, 0, 0, null));
			break;
		}
	}
	
	/**
	 * Parse all of the information gathered from the NPC parameter panel
	*/
	public void editNPCinfo(Entity_NPC e) {
		String name					= this.debugger.editorGUI.par_tb_name.getText();
		String npcType				= (String)this.debugger.editorGUI.par_co_npcType.getSelectedItem();
		String faction				= (String)this.debugger.editorGUI.par_co_faction.getSelectedItem();
		String dialogue				= this.debugger.editorGUI.par_ta_dialogue.getText();
		double maxXWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_maxXwalk.getText());
		double maxYWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_maxYwalk.getText());
		double minXWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_minXwalk.getText());
		double minYWalk				= Double.parseDouble(this.debugger.editorGUI.par_tb_minYwalk.getText());
		double speed				= Double.parseDouble(this.debugger.editorGUI.par_tb_speed.getText());
		double maxHealth			= Double.parseDouble(this.debugger.editorGUI.par_tb_maxHealth.getText());
		double currentHealth		= Double.parseDouble(this.debugger.editorGUI.par_tb_currentHealth.getText());
		boolean aggressive 			= this.debugger.editorGUI.par_cb_aggressive.isSelected();
		boolean canAttack 			= this.debugger.editorGUI.par_cb_canAttack.isSelected();
		boolean canBeAttacked 		= this.debugger.editorGUI.par_cb_canBeAttacked.isSelected();	
		boolean canTalk				= this.debugger.editorGUI.par_cb_canSpeak.isSelected();
		
		e.name = name;
		e.faction = faction;
		e.maxXWalk = maxXWalk;
		e.maxYWalk = maxYWalk;
		e.minXWalk = minXWalk;
		e.maxXWalk = maxXWalk;
		e.speed = speed;
		e.maxHealth = (int)maxHealth;
		e.currentHealth = (int)currentHealth;
		e.aggressive = aggressive;
		e.canAttack = canAttack;
		e.canBeAttacked = canBeAttacked;
		e.canTalk = canTalk;
		
		if ( e.canTalk ) {
			double xx = e.getMapX();
			double yy = e.getMapY();
			e = new Entity_NPC_DialogueTest(Main.game.world.currentPlayer.level, faction, name, xx, yy, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()); 
			//e.setDialogueList(dialogues);
			//dialogues.clear();
			//e.dialogue(dialogue);
		}	
	}
	
	/**
	 * Returns the list of Levels that exist within the World given.
	 * 
	 * @param world
	 * @return List<Level>
	 */
	public List<Level> getLevels(World world) {
		return world.levels;
	}
	
	
	
	//Get methods............................................ 
	public String[] getSelectionOptions()		{	return (  selectionOptions  ); }
	public String[] getTileReplacementOptions()	{	return (  tileReplacementOptions  ); }
	public String[] getTiles()					{	return (  tiles   ); }
	public String[] getItems()					{	return (  items   ); }
	public String[] getNPCs()					{	return (  NPCs    ); }
	public String[] getMISC()					{	return (  MISC    ); }
	public String[] getEntities()				{	return (  entity  ); }
	public String[] getFactions()				{	return (  faction ); }
	public String[] getConnector()				{	return (  levelConnector ); }
	
	public String[] getWeapons()				{	return (  weapon  ); }
	public String[] getShields()				{	return (  shield  ); }
	public String[] getBoots()					{	return (  boot    ); }
	public String[] getGloves()					{	return (  glove   ); }
	public String[] getChest()					{	return (  chest   ); }
	public String[] getLegs()					{	return (  leg    ); }
	public String[] getHelmet()					{	return (  helmet    ); }
	
	public String[] getLevelObjects()			{	return (  levelObjects); }
	
	
	
	//.......................................................
}
