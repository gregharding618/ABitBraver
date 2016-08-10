import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.Serializable;

public class Player_Sandbox extends Player implements Comparable<Entity>, Serializable {
	
	private static final long serialVersionUID = -2175494992188993694L;
	
	public Player_Sandbox(String name, boolean hasInput, double mapX, double mapY) {
		super(name, hasInput, true, mapX, mapY, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace());

		//Collision box
		this.maxX = 4;
		this.minX = 4;
		this.maxY = 2;
		this.minY = 4;
		//////////////////////
			
		this.speed = 0.55;
		this.maxHealth = 100;
		this.currentHealth = this.maxHealth;
		this.faction = "Debugger";
		this.name = name;
		this.attackDelay = 55;
		this.canAttack = true;
		this.canBeAttacked = false;
		//this.inventory_sandbox = new Inventory_Sandbox(this, 50);
		this.inventory = new Inventory(this, 50);
		//this.editorGUI = editorGUI;
		//this.editorGUI.player = this;
		this.clickDelayAmount = 0;
		this.clickDelay = 0;
		this.isSolid = false;
	}
	
	@Override
	public void update() {
		if (this.hasInput) {
			if (this.ec == null) {
				this.ec = new EditorController(this);
			}
		
			if (this.editorGUI == null) {
				this.editorGUI = new GUI_MapEditor_V2(this);
			}
		
			super.update();
		
			//Update editorGUI
			this.editorGUI.update();
			/////////////////////
		}
	}

	@Override
	public void render() {
		super.render();
	}
	
	public void renderGuiObjects() {
		super.renderGuiObjects();
	}
		
	@Override
	public void move() {
		super.move();
	}
	
	@Override
	protected void checkOtherInput() {
		if (StdDraw.isKeyPressed(KeyEvent.VK_E) && this.buttonDelay == 0 && this.levelTransitionDelay == 0) {
			
			//During dialogue
			if (this.currentDialogue != null) {
				
				
				if (this.currentDialogue.finishedWriting) this.currentDialogue = ((Entity_NPC)this.currentDialogue.speaker).dialogue(this.dialogueIndex);
				this.dialogueIndex++;
				if (this.dialogueIndex >= this.currentDialogue.speaker.dialogues.size() - 1)	this.currentDialogue = null;
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
		
				// Level connector
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
							//this.level.droppedItemsToRemove.add(this.nearbyItem);
							this.inventory.addItem(this.nearbyItem.item);
							this.nearbyItem = null;
							actionFound = true;
							break;
						}
					}
					this.buttonDelay = this.buttonDelayAmount;
				}
				///////////////////
			}
			///////////////////
		}
		
		///////////////////////////////////
		
		//Spacebar - Attack
		if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
			if (this.attackDelayTimer == 0) {
				for (Entity e : this.level.getEntities()) {
					if (e instanceof Entity_Character && ((Entity_Character)e).canBeAttacked && !e.equals(this) && !this.level.getEntitiesToRemove().contains(e)) {
						//Above player
						if (this.getMapY() + this.maxY + this.range >= e.getMapY() - e.minY && ((this.getMapX() > e.getMapX() && this.getMapX() - this.minX <= e.getMapX() + e.maxX) || (this.getMapX() < e.getMapX() && this.getMapX() + this.maxX >= e.getMapX() - e.minX) || (this.getMapX() == e.getMapX()))) {
							attack((Entity_Character)e);
						}
					}
				}
			}
		}
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
		
		//ESC - Close inventory
		if (this.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE)) {
			if (!this.showControls) {
				this.showControls = true;
			} else {
				this.showControls = false;
			}
			this.buttonDelay = this.buttonDelayAmount;
		}
		///////////////////
		
		//Right click (3) - Check entity info
		if (Mouse.lastButtonPressed == 3 && this.clickDelay == 0) {
			Mouse.lastButtonPressed = -1;
			
			if (this.examinedEntity == null) {
				for (Entity e : this.level.getEntities()) {
					if (!this.level.getEntitiesToRemove().contains(e) && e instanceof Entity_NPC && ((Entity_NPC)e).shouldRender) {
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
		if (Mouse.lastButtonPressed == 1 && this.clickDelay == 0) {
			this.clickDelay = this.clickDelayAmount;
			double[] levelCoordinates = getClickCoordinatesOnMap();
			this.ec.checkSelectedObject(levelCoordinates);
			
		}
	}
	
	private double[] getClickCoordinatesOnMap() {
		double[] coordinates = new double[]{this.getMapX() - (this.x - StdDraw.mouseX()), this.getMapY() - (this.y - StdDraw.mouseY())};
		
		return coordinates;
	}

	@Override
	public void checkTimers() {
		if (this.attackDelayTimer > 0) this.attackDelayTimer--;
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
	}

}