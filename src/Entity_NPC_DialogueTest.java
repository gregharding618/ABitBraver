import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entity_NPC_DialogueTest extends Entity_NPC implements Serializable {
	
	public Entity_NPC_DialogueTest(Level level, String faction, String name, double mapX, double mapY, Item_Equipment helmet, Item_Equipment chest, Item_Equipment legs, Item_Equipment boots, Item_Equipment gloves, Item_Equipment shield, Item_Equipment weapon, Item_Equipment ammo) {
		this.level = level;
		setMapX(mapX);
		setMapX(mapY);
		this.maxXWalk = mapX;
		this.minXWalk = mapX;
		this.maxYWalk = mapY;
		this.minYWalk = mapY;
		
		//Save checkpoint location
		this.checkpointLevel = level;
		this.checkpointMapX = mapX;
		this.checkpointMapY = mapY;
		//////////////////////
		
		//Dialogue
		this.canTalk = true;
		//////////////////////
		
		this.maxX = 4.5;
		this.minX = 4.5;
		this.maxY = 2.25;
		this.minY = 4.5;
		
		this.speed = 0;
		this.maxHealth = 10;
		this.currentHealth = this.maxHealth;
		this.faction = faction;
		this.name = name;
		this.canAttack = false;
		this.canBeAttacked = false;
		this.aggressive = false;
		
		this.miniMapColor = StdDraw.RED;
		if (this.faction.equals(this.level.world.currentPlayer.faction)) this.miniMapColor = StdDraw.YELLOW;
		if (!this.canAttack || !this.canBeAttacked) this.miniMapColor = StdDraw.GREEN;

		this.helmet = helmet;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		this.gloves = gloves;
		this.shield = shield;
		this.weapon = weapon;
		this.ammo = ammo;
		
		this.attackDelay = 155;
		
		dialogues = new ArrayList<Dialogue>();
	}

	@Override
	public List<DroppedItem> dropItems() {

		return null;
	}

	@Override
	public Dialogue dialogue(int index) {	
		return ( dialogues.get(index) );
	}
		
	public void addDialogue(Dialogue dialogue){
		dialogues.add(dialogue);
	}
/*
	@Override
	public Dialogue dialogue(int index) {
		Dialogue text = null;
		
		switch (index) {
		
		case 0:
			text = new Dialogue(this, "Hi my name is Albert, who are you?");
			break;
			
		case 1:
			text = new Dialogue(this, "Normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text normal text");
			break;
		}
		
		return text;
	}
	*/
	public void setDialogueList(ArrayList<String> dialog){
		//dialogues = dialog;
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
	public Dialogue dialogue(int index, ArrayList<String> dialog) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
