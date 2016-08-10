import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entity_NPC_ShopOwner extends Entity_NPC implements Serializable {
	
	Button yes;
	Button no;
	
	public Entity_NPC_ShopOwner(Level level, String name, double mapX, double mapY, double maxXWalk, double maxYWalk, double minXWalk, double minYWalk, Item[] inventory) {
		this.level = level;
		setMapX(mapX);
		setMapX(mapY);
		this.maxXWalk = maxXWalk;
		this.minXWalk = minXWalk;
		this.maxYWalk = maxYWalk;
		this.minYWalk = minYWalk;
		
		this.inventory = new Inventory(this, inventory.length);
		this.inventory.items = inventory;
		
		//Save checkpoint location
		this.checkpointLevel = level;
		this.checkpointMapX = mapX;
		this.checkpointMapY = mapY;
		//////////////////////
		
		this.canTalk = true;
		
		this.maxX = 4.5;
		this.minX = 4.5;
		this.maxY = 2.25;
		this.minY = 4.5;
		
		this.speed = 0.35;
		this.maxHealth = 1;
		this.currentHealth = this.maxHealth;
		this.faction = "Player";
		this.name = name;
		this.canAttack = false;
		this.canBeAttacked = false;
		
		this.miniMapColor = StdDraw.GREEN;

		this.helmet = new Item_Equipment_EmptySpace();
		this.chest = new Item_Equipment_EmptySpace();
		this.legs = new Item_Equipment_EmptySpace();
		this.boots = new Item_Equipment_EmptySpace();
		this.gloves = new Item_Equipment_EmptySpace();
		this.shield = new Item_Equipment_EmptySpace();
		this.weapon = new Item_Equipment_EmptySpace();
		this.ammo = new Item_Equipment_EmptySpace();
		
		//Shop buttons
		this.yes = new Button(this.level.world.currentPlayer, 0, 0, 3, 2, "Yes", 18, -0.1, new Color(45, 43, 58), StdDraw.GREEN);
		this.no = new Button(this.level.world.currentPlayer, 0, 0, 3, 2, "No", 18, -0.1, new Color(45, 43, 58), StdDraw.RED);
		/////////////
	}

	@Override
	public void update() {
		super.update();
		
		if (this.yes.isSelected) {
			this.yes.isSelected = false;
			this.level.world.currentPlayer.currentShop = new Shop(this, this.level.world.currentPlayer);
			this.level.world.currentPlayer.dialogueIndex = 0;
			this.level.world.currentPlayer.currentDialogue = null;
		}
		if (this.no.isSelected) {
			this.no.isSelected = false;
			this.level.world.currentPlayer.dialogueIndex = 0;
			this.level.world.currentPlayer.currentDialogue = null;
		}
	}

	@Override
	public void render() {
		StdDraw.picture(this.x, this.y, "dialogue_test.png");
	}

	@Override
	public void move() {
		
	}

	@Override
	public void checkTimers() {
		
	}

	@Override
	public List<DroppedItem> dropItems() {
		
		return null;
	}

	@Override
	public Dialogue dialogue(int index) {
		Dialogue text = null;
		
		switch (index) {
		
		case 0:
			text = new Dialogue(this, "Want to see my shop?");
			text.addButton(this.yes);
			text.addButton(this.no);
			break;
			
		}
		
		return text;
	}

	@Override
	public void setDialogueList(ArrayList<String> dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dialogue dialogue(int index, ArrayList<String> dialog) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
