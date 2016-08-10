import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.lang.reflect.*;

public class Inventory_Sandbox implements Serializable {

	public Entity owner;
	public int size;
	public boolean isFull = false;
	
	public Item[] items;
	public Item selectedItem;
	
	public Entity[] entities;
	public Entity selectedEntity;
	
	private ScrollBar scrollBar, selectedEquipmentScrollBar;
	
	private Button sortAllButton, sortEquipmentButton, sortFoodButton, sortValueButton;
	private Button inventoryButton, equipmentButton;
	private Button deselectItemButton, deselectEquipmentButton, unequipButton, dropButton, areYouSureDropYesButton, areYouSureDropNoButton;
	private Button equipButton;
	
	private boolean showInventory = true, showEquipment = false;
	private boolean areYouSureDrop = false;
	
	private int selectedEquipmentSlot = 0; //0 = none, 1 = head, 2 = chest, 3 = legs, 4 = boots, 5 = gloves, 6 = shield, 7 = weapon
	private int inventoryFullTimer = 0, inventoryFullDelayAmount = 300;
	
	public Inventory_Sandbox(Entity owner, int size) {
		String directory;
		File file = new File(".");
		directory = file.getAbsolutePath();
		
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				String filename = fileList[i].getName();
			
				if (filename.startsWith("Entity_NPC_")) {
					filename = fileList[i].getName();
					filename = filename.replace(".java","");
					Class c = Class.forName(filename);
					Object o = c.newInstance();
					Object[] parameters = c.getTypeParameters();
					int parameterSize = parameters.length;
					
					//this.entities[0] = new Entity_NPC_Test(((Player_Sandbox)this.owner).level, true, true, "Enemy", "Enemy", 22, 29, 22 + 25, 29 + 26, 22 - 10, 29 - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace());
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.owner = owner;
		this.size = size;
		items = new Item[this.size];

		for (int i = 0; i < this.size; i++) this.items[i] = new Item_Equipment("", "", "Entity " + (i + 1), false, 1, true, false, false, i, 3, 5, 5, 1, 5, 1, 5, 9, 3);
		this.items[3] = new Item_Food("Jewbies", "jewbies.png", true, 28, 400, 2, 312, 414, 294, 10, 5, 87);

		if (((Player_Sandbox)this.owner) instanceof Player_Sandbox) {
			this.scrollBar = new ScrollBar(78, 75, 85, 5, 1.25, 10, true, new Color(165, 165, 187));
			
			//this.sortAllButton = new Button((Player_Sandbox)((Player_Sandbox)this.owner), 40, 95.5, 2.85, 1.85, "All", 24, -0.25, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.sortAllButton.isSelected = true;
			
			//this.sortEquipmentButton = new Button((Player_Sandbox)((Player_Sandbox)this.owner), 49.5, 95.5, 5.2, 1.85, "Equipment", 16, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			
			//this.sortFoodButton = new Button((Player_Sandbox)((Player_Sandbox)this.owner), 59, 95.5, 3, 1.85, "Food", 16, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			
			//this.sortValueButton = new Button((Player_Sandbox)((Player_Sandbox)this.owner), 66.5, 95.5, 3.2, 1.85, "Value", 16, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			
			//Menu tab buttons
			this.inventoryButton = new Button((Player)((Player_Sandbox)this.owner), 20, 2.5, 5.6, 1.75, "Inventory", 19, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.inventoryButton.isSelected = true;
			this.equipmentButton = new Button((Player)((Player_Sandbox)this.owner), 35, 2.5, 6.25, 1.75, "Equipment", 19, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			/////////////////////////
			
			//Selected item buttons
			this.dropButton = new Button((Player)((Player_Sandbox)this.owner), 70, 87.15, 3.5, 1.6, "Drop", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.deselectItemButton = new Button((Player)((Player_Sandbox)this.owner), 90, 87.15, 4.8, 1.6, "Deselect", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.equipButton = new Button((Player)((Player_Sandbox)this.owner), 80, 94, 3.5, 1.6, "Equip", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.areYouSureDropYesButton = new Button((Player)((Player_Sandbox)this.owner), 40, 44, 5, 3, "YES", 24, 0, StdDraw.GRAY, StdDraw.GREEN);
			this.areYouSureDropNoButton = new Button((Player)((Player_Sandbox)this.owner), 60, 44, 5, 3, "NO", 24, 0, StdDraw.GRAY, StdDraw.RED);
			/////////////////////////
			
			//Selected equipment buttons
			this.deselectEquipmentButton = new Button((Player)((Player_Sandbox)this.owner), 58, 90, 4.8, 1.6, "Deselect", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.unequipButton = new Button((Player)((Player_Sandbox)this.owner), 42, 90, 4.8, 1.6, "Unequip", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			/////////////////////////
			
			//Selected item scroll bar
			this.selectedEquipmentScrollBar = new ScrollBar(97.5, 73, 83, 5, 1.25, 10, true, new Color(165, 165, 187));
			/////////////////////////
		}
		
		//TEST REMOVE LATER
		/*this.items[0] = new Item_Equipment_Test("Test Helmet", 3, 5, 5, 1, 5, 1, 5, 9);
		this.items[1] = new Item_Equipment_Test("Test Helmet", 3, 5, 5, 1, 5, 1, 5, 9);
		this.items[2] = new Item_Equipment_Test("Test Helmet", 3, 5, 5, 1, 5, 1, 5, 9);
		this.items[3] = new Item_Equipment_Test("Test Helmet", 3, 5, 5, 1, 5, 1, 5, 9);
		this.items[4] = new Item_Equipment_Test("Test Helmet", 3, 5, 5, 1, 5, 1, 5, 9);
		this.items[5] = new Item_Equipment_Test("Test Helmet", 3, 5, 5, 1, 5, 1, 5, 9);
		this.items[6] = new Item_Equipment_Test("Test Helmet", 3, 5, 5, 1, 5, 1, 5, 9);*/
		/////////////////////////////////////////
	}
	
	public void update() {
		this.isFull = checkIfFull();
		
		if (this.inventoryFullTimer > 0) this.inventoryFullTimer--;
		
		if (((Player_Sandbox)this.owner) instanceof Player_Sandbox) {
			checkInput();
			if (this.areYouSureDrop) updateAreYouSureDropButtons();
			if (this.selectedItem != null && !this.areYouSureDrop && !this.scrollBar.isMoving && !this.selectedEquipmentScrollBar.isMoving) updateSelectedItemButtons();
		} else {
			
		}
	}

	public void render() {
		if (this.showInventory) renderInventory();
		else if (this.showEquipment) renderEquipment();
		
		//Menu tab buttons
		this.inventoryButton.render();
		this.equipmentButton.render();
		/////////////////////////
	}
	
	private void renderEquipment() {
		//Background
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(50, 50, 50, 48);
		////////////////////
		
		//Top banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(50, 91.25, 50, 6.5);
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.rectangle(50, 91.5, 49.8, 6.5);
		////////////////////
		
		//Inventory full text
		if (this.inventoryFullTimer > 0) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 38));
			StdDraw.text(82, 91.25, "Inventory full!");
		}
		////////////////////

		//Bottom banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(50, 2.25, 50, 2.275);
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.rectangle(50, 2.25, 49.8, 2.275);
		////////////////////
		
		//Selected equipment name
		if (this.selectedEquipmentSlot > 0) {
			String itemName = "";
						
			//Head
			if (this.selectedEquipmentSlot == 1) {
				itemName = ((Player_Sandbox)this.owner).helmet.name;
			}
			////////////////////
			
			//Chest
			else if (this.selectedEquipmentSlot == 2) {
				itemName = ((Player_Sandbox)this.owner).chest.name;
			}
			////////////////////
			
			//Legs
			else if (this.selectedEquipmentSlot == 3) {
				itemName = ((Player_Sandbox)this.owner).legs.name;
			}
			////////////////////
			
			//Feet
			else if (this.selectedEquipmentSlot == 4) {
				itemName = ((Player_Sandbox)this.owner).boots.name;
			}
			////////////////////
			
			//Hands
			else if (this.selectedEquipmentSlot == 5) {
				itemName = ((Player_Sandbox)this.owner).gloves.name;
			}
			////////////////////
			
			//Shield
			else if (this.selectedEquipmentSlot == 6) {
				itemName = ((Player_Sandbox)this.owner).shield.name;
			}
			////////////////////
			
			//Weapon
			else if (this.selectedEquipmentSlot == 7) {
				itemName = ((Player_Sandbox)this.owner).weapon.name;
			}
			////////////////////
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 32));
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.text(50, 95, itemName);
		}
		////////////////////
		
		//Buttons
		if (this.selectedEquipmentSlot > 0) {
			this.deselectEquipmentButton.render();
			this.unequipButton.render();
		}
		////////////////////
		
		
		
		
		
		//Equipment side
		
		//Lines
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.setPenRadius(0.004);
		StdDraw.line(30, 74, 30, 14);
		StdDraw.line(30, 54, 10, 54);
		StdDraw.line(30, 54, 10, 34);
		StdDraw.line(30, 54, 50, 34);
		StdDraw.setPenRadius();
		////////////////////
		
		//Head
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 1) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(30, 74, 6);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player_Sandbox)this.owner).helmet instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 82, ((Player_Sandbox)this.owner).helmet.name);
		else StdDraw.text(30, 82, "Head");
		StdDraw.square(30, 74, 6);
		((Player_Sandbox)this.owner).helmet.render(30, 74);
		////////////////////
		
		//Chest
		if (this.selectedEquipmentSlot == 2) StdDraw.setPenColor(new Color(87, 102, 88));
		else StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(30, 54, 6);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(30, 62, 3, 1);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player_Sandbox)this.owner).chest instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 62, ((Player_Sandbox)this.owner).chest.name);
		else StdDraw.text(30, 62, "Chest");
		StdDraw.square(30, 54, 6);
		((Player_Sandbox)this.owner).chest.render(30, 54);
		////////////////////
		
		//Legs
		if (this.selectedEquipmentSlot == 3) StdDraw.setPenColor(new Color(87, 102, 88));
		else StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(30, 34, 6);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(30, 42, 3, 1);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player_Sandbox)this.owner).legs instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 42, ((Player_Sandbox)this.owner).legs.name);
		else StdDraw.text(30, 42, "Legs");
		StdDraw.square(30, 34, 6);
		((Player_Sandbox)this.owner).legs.render(30, 34);
		////////////////////
		
		//Feet
		if (this.selectedEquipmentSlot == 4) StdDraw.setPenColor(new Color(87, 102, 88));
		else StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(30, 14, 6);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(30, 22, 3, 1);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player_Sandbox)this.owner).boots instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 22, ((Player_Sandbox)this.owner).boots.name);
		else StdDraw.text(30, 22, "Feet");
		StdDraw.square(30, 14, 6);
		((Player_Sandbox)this.owner).boots.render(30, 14);
		////////////////////
		
		//Hands
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 5) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(10, 54, 6);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player_Sandbox)this.owner).gloves instanceof Item_Equipment_EmptySpace)) StdDraw.text(10, 62, ((Player_Sandbox)this.owner).gloves.name);
		else StdDraw.text(10, 62, "Hands");
		StdDraw.square(10, 54, 6);
		((Player_Sandbox)this.owner).gloves.render(30, 54);
		////////////////////
		
		//Shield
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 6) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(50, 34, 6);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player_Sandbox)this.owner).shield instanceof Item_Equipment_EmptySpace)) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledSquare(42, 42.3, 1.25);
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(50, 42, ((Player_Sandbox)this.owner).shield.name);
		}
		else {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(50, 42, "Shield");
		}
		StdDraw.square(50, 34, 6);
		((Player_Sandbox)this.owner).shield.render(50, 34);
		////////////////////
		
		//Weapon
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 7) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(10, 34, 6);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player_Sandbox)this.owner).weapon instanceof Item_Equipment_EmptySpace)) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledSquare(18, 42.3, 1.25);
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(10, 42, ((Player_Sandbox)this.owner).weapon.name);
		}
		else {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(10, 42, "Weapon");
		}
		StdDraw.square(10, 34, 6);
		((Player_Sandbox)this.owner).weapon.render(10, 34);
		////////////////////
		
		////////////////////////////////////////
		
		
		
		
		
		//Levels and stats side
		
		//Levels
		StdDraw.setPenColor(new Color(151, 181, 201));
		StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
		StdDraw.text(85, 82, "Levels");
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 22));
		
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.text(78, 76, "Overall:");
		StdDraw.text(92, 76, ((Player_Sandbox)this.owner).overallLevel + "");
		
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.text(78, 72, "Melee:");
		StdDraw.text(92, 72, ((Player_Sandbox)this.owner).melee + "");
		
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.text(78, 68, "Archery:");
		StdDraw.text(92, 68, ((Player_Sandbox)this.owner).archery + "");
		
		StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.text(78, 64, "Magic:");
		StdDraw.text(92, 64, ((Player_Sandbox)this.owner).magic + "");
		
		StdDraw.setPenColor(StdDraw.CYAN);
		StdDraw.text(78, 60, "Defense:");
		StdDraw.text(92, 60, ((Player_Sandbox)this.owner).defense + "");
		
		StdDraw.setPenColor(StdDraw.PINK);
		StdDraw.text(78, 56, "Health:");
		StdDraw.text(92, 56, ((Player_Sandbox)this.owner).currentHealth + " / " + ((Player_Sandbox)this.owner).maxHealth);
		//////////////////////
		
		//Stats
		StdDraw.setPenColor(new Color(151, 181, 201));
		StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
		StdDraw.text(85, 46, "Stats");
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 22));
		StdDraw.setPenColor(StdDraw.WHITE);
		
		StdDraw.text(78, 42, "Armor:");
		StdDraw.text(92, 42, ((Player_Sandbox)this.owner).armor + "");
		
		StdDraw.text(78, 38, "Accuracy:");
		StdDraw.text(92, 38, ((Player_Sandbox)this.owner).accuracy + "");
		
		StdDraw.text(78, 34, "Strength:");
		StdDraw.text(92, 34, ((Player_Sandbox)this.owner).maxHit + "");
		
		StdDraw.text(78, 30, "Melee Bonus:");
		StdDraw.text(92, 30, ((Player_Sandbox)this.owner).meleeBoost + "");
		
		StdDraw.text(78, 26, "Archery Bonus:");
		StdDraw.text(92, 26, ((Player_Sandbox)this.owner).archeryBoost + "");
		
		StdDraw.text(78, 22, "Magic Bonus:");
		StdDraw.text(92, 22, ((Player_Sandbox)this.owner).magicBoost + "");
		//////////////////////
		////////////////////////////////////////
	}
	
	private void renderInventory() {
		boolean renderList = true;
		//Background
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(30, 50, 30, 48);
		////////////////////
		
		//Organize list
		moveEmptySpacesToBottom();
		////////////////////
			
		//Don't render anything if the inventory size is somehow 0 or first item is an empty space
		if (this.items.length == 0 || this.items[0] instanceof Item_Equipment_EmptySpace) renderList = false;
		//////////////////////////////
			
		//Item list
		if (renderList) {
			//Show all items
			if (this.sortAllButton.isSelected) {
				for (int i = 0; i < this.items.length; i++) {
					Item item = this.items[i];
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (i + 0.0) / this.size;
					double yy = ((this.size * 10) - (scrollPercentToTop * ((this.size * 10) - 78.5))) - (i * 10);
				
					if (this.size <= 7) {
						item.render(5, 78.5 - (i * 10));
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
						StdDraw.text(30, 78.5 - (i * 10), item.name);
					} else {
						if (!(item instanceof Item_Equipment_EmptySpace)) {
							if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
								if (yy < 88.5 && yy > 0) {
									if (this.selectedItem != null && this.selectedItem.equals(item)) {
										StdDraw.setPenColor(new Color(202, 224, 204));
										StdDraw.filledRectangle(28, yy, 28, 5);
										StdDraw.setPenColor(StdDraw.BLACK);
									}
									else StdDraw.setPenColor(StdDraw.WHITE);
									StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
									StdDraw.text(30, yy, item.name);
									item.render(5, yy);
								}
							}
						} else {
							StdDraw.setPenColor(new Color(37, 25, 25));
							StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
							StdDraw.text(30, yy, "Empty");
						}
					}
				}
			}
			////////////////////
			
			//Show only worn items
			else if (this.sortEquipmentButton.isSelected) {
				Item[] equipment = new Item[this.size];
				int equipmentSize = 0;
				
				for (int ii = 0; ii < this.size; ii++) {
					if (this.items[ii] instanceof Item_Equipment && !(this.items[ii] instanceof Item_Equipment_EmptySpace)) {
						equipment[equipmentSize] = this.items[ii];
						equipmentSize++;
					}
				}
				
				for (int ii = 0; ii < equipmentSize; ii++) {
					if (equipment.length == 0) break;
					Item equipmentItem = equipment[ii];
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / equipmentSize;
					double yy = ((equipmentSize * 10) - (scrollPercentToTop * ((equipmentSize * 10) - 78.5))) - (ii * 10);
				
					if (equipmentSize <= 7) {
						if (this.selectedItem != null && this.selectedItem.equals(equipmentItem)) {
							StdDraw.setPenColor(new Color(202, 224, 204));
							StdDraw.filledRectangle(28, 78.5 - (ii * 10), 28, 5);
							StdDraw.setPenColor(StdDraw.BLACK);
						}
						else StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
						StdDraw.text(30, 78.5 - (ii * 10), equipmentItem.name);
						equipmentItem.render(5, 78.5 - (ii * 10));
					} else {
						if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
							if (yy < 88.5 && yy > 0) {
								if (this.selectedItem != null && this.selectedItem.equals(equipmentItem)) {
									StdDraw.setPenColor(new Color(202, 224, 204));
									StdDraw.filledRectangle(28, yy, 28, 5);
									StdDraw.setPenColor(StdDraw.BLACK);
								}
								else StdDraw.setPenColor(StdDraw.WHITE);
								StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
								StdDraw.text(30, yy, equipmentItem.name);
								equipmentItem.render(5, yy);
							}
						}
					}
				}
			}
			////////////////////
			
			//Show only edible items
			else if (this.sortFoodButton.isSelected) {
				Item[] food = new Item[this.size];
				int foodSize = 0;
				
				for (int ii = 0; ii < this.size; ii++) {
					if (this.items[ii] instanceof Item_Food) {
						food[foodSize] = this.items[ii];
						foodSize++;
					}
				}
				
				for (int ii = 0; ii < foodSize; ii++) {
					if (food.length == 0) break;
					Item foodItem = food[ii];
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / foodSize;
					double yy = ((foodSize * 10) - (scrollPercentToTop * ((foodSize * 10) - 78.5))) - (ii * 10);
				
					if (foodSize <= 7) {
						if (this.selectedItem != null && this.selectedItem.equals(foodItem)) {
							StdDraw.setPenColor(new Color(202, 224, 204));
							StdDraw.filledRectangle(28, 78.5 - (ii * 10), 28, 5);
							StdDraw.setPenColor(StdDraw.BLACK);
						}
						else StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
						StdDraw.text(30, 78.5 - (ii * 10), foodItem.name);
						foodItem.render(5, 78.5 - (ii * 10));
					} else {
						if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
			
							if (yy < 88.5 && yy > 0) {
								if (this.selectedItem != null && this.selectedItem.equals(foodItem)) {
									StdDraw.setPenColor(new Color(202, 224, 204));
									StdDraw.filledRectangle(28, yy, 28, 5);
									StdDraw.setPenColor(StdDraw.BLACK);
								}
								else StdDraw.setPenColor(StdDraw.WHITE);
								StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
								StdDraw.text(30, yy, foodItem.name);
								foodItem.render(5, yy);
							}
						}
					}
				}
			}
			////////////////////
			
			//Organize items by value (Top is most valueable)
			else if (this.sortValueButton.isSelected) {
				Item[] value = this.items.clone();

				Arrays.sort(value);
				
				for (int ii = 0; ii < this.size; ii++) {
					if (value.length == 0) break;
					Item valueItem = value[ii];
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / this.size;
					double yy = ((this.size * 10) - (scrollPercentToTop * ((this.size * 10) - 78.5))) - (ii * 10);
				
					if (this.size <= 7) {
						if (this.selectedItem != null && this.selectedItem.equals(valueItem)) {
							StdDraw.setPenColor(new Color(202, 224, 204));
							StdDraw.filledRectangle(28, 78.5 - (ii * 10), 28, 5);
							StdDraw.setPenColor(StdDraw.BLACK);
						}
						else StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
						StdDraw.text(30, 78.5 - (ii * 10), valueItem.name);
						valueItem.render(5, 78.5 - (ii * 10));
					} else {
						if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
							if (yy < 88.5 && yy > 0) {
								if (!(valueItem instanceof Item_Equipment_EmptySpace)) {
									if (this.selectedItem != null && this.selectedItem.equals(valueItem)) {
										StdDraw.setPenColor(new Color(202, 224, 204));
										StdDraw.filledRectangle(28, yy, 28, 5);
										StdDraw.setPenColor(StdDraw.BLACK);
									}
									else StdDraw.setPenColor(StdDraw.WHITE);
									StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
									StdDraw.text(30, yy, valueItem.name);
									valueItem.render(5, yy);
								} else {
									StdDraw.setPenColor(new Color(37, 25, 25));
									StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
									StdDraw.text(30, yy, "Empty");
								}
							}
						}
					}
				}
			}
			////////////////////
		}
		//////////////////////////////////////////
		
		//Selected item info
		if (this.selectedItem != null) {
			
			//Backgrond
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(80, 49, 20, 49);
			////////////////////
			
			//If selected item is equipment
			if (this.selectedItem instanceof Item_Equipment) renderSelectedEquipmentInfo();
			////////////////////
			
			//Top banner
			StdDraw.setPenColor(StdDraw.DARK_GRAY);
			StdDraw.filledRectangle(80.05, 91.25, 29.96, 6.5);
			////////////////////
			
			//Borders
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.line(60, 98, 100, 98);
			StdDraw.line(50, 0.1, 100, 0.1);
			StdDraw.line(99.9, 98, 99.9, 0.1);
			StdDraw.line(60, 84.75, 100, 84.75);
			////////////////////
			
			//Buttons
			this.dropButton.render();
			this.deselectItemButton.render();
			if (this.selectedItem instanceof Item_Equipment) this.equipButton.render();
			////////////////////
		}
		//////////////////////////////////////////
		
		//Top banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(30.05, 91.25, 29.96, 6.5);
		////////////////////
		
		//Bottom banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(30.05, 2.25, 29.96, 2.275);
		StdDraw.filledSquare(59.42, 4.5, 0.58);
		////////////////////
		
		//Border
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.rectangle(30, 49, 30, 48.95);
		////////////////////
		
		//Scroll bar
		this.scrollBar.render();
		////////////////////
		
		//Scroll bar border
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.0175);
		StdDraw.rectangle(58, 45, 1.45, 40);
		StdDraw.setPenRadius();
		////////////////////
		
		//Item list border
		StdDraw.setPenColor(StdDraw.YELLOW);
		//StdDraw.line(20, 84.75, 80, 84.75);
		StdDraw.rectangle(28.1, 44.6, 28, 40.1);
		////////////////////
		
		//"Sort by:" text
		StdDraw.setPenColor(new Color(123, 193, 204));
		StdDraw.setFont(new Font("Arial", Font.BOLD, 32));
		StdDraw.text(8, 91, "Sort by:");
		////////////////////
		
		//Buttons
		this.sortAllButton.render();
		this.sortEquipmentButton.render();
		this.sortFoodButton.render();
		this.sortValueButton.render();
		////////////////////
		
		//Are you sure you want to drop the selected item
		if (this.areYouSureDrop) {
			//Background and border
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(50, 50, 21, 12);
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.setPenRadius(0.017);
			StdDraw.rectangle(50, 50, 21, 12);
			StdDraw.setPenRadius();
			////////////////////
			
			//Text
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
			StdDraw.text(50, 58, "Are you sure you want to drop");
			StdDraw.text(50, 54, this.selectedItem.name + "?");
			////////////////////
			
			//Yes and no buttons
			this.areYouSureDropYesButton.render();
			this.areYouSureDropNoButton.render();
			////////////////////
		}
		////////////////////
	}
	
	private void renderSelectedEquipmentInfo() {
		double scrollPercentToTop = (this.selectedEquipmentScrollBar.y - this.selectedEquipmentScrollBar.min - this.selectedEquipmentScrollBar.height) / (this.selectedEquipmentScrollBar.max - (this.selectedEquipmentScrollBar.height * 2) - this.selectedEquipmentScrollBar.min); //0.##

		//Item name
		if (81 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.setPenColor(new Color(189, 144, 132));
			StdDraw.filledRectangle(77.5, 81.25 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
			StdDraw.text(77.5, 81 + (76 * (1 - scrollPercentToTop)), this.selectedItem.name);
		}
		///////////////////
		
		//"Abilities" text
		if (((Item_Equipment)this.selectedItem).slot == 7) {
			if (36 + (76 * (1 - scrollPercentToTop)) < 88) {
				StdDraw.setPenColor(new Color(189, 144, 132));
				StdDraw.filledRectangle(77.5, 36.25 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
				StdDraw.setPenColor(StdDraw.ORANGE);
				StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
				StdDraw.text(77.5, 36 + (76 * (1 - scrollPercentToTop)), "Abilities");
			}
		}
		///////////////////
		
		//Scroll bar border
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(this.selectedEquipmentScrollBar.x, 40, 2.5, 45);
		///////////////////
		
		//Scroll bar crevice
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(this.selectedEquipmentScrollBar.x, (this.selectedEquipmentScrollBar.max + this.selectedEquipmentScrollBar.min) / 2, this.selectedEquipmentScrollBar.width, this.selectedEquipmentScrollBar.max - ((this.selectedEquipmentScrollBar.max + this.selectedEquipmentScrollBar.min) / 2));
		///////////////////
		
		//Scroll bar
		this.selectedEquipmentScrollBar.render();
		///////////////////

		//Stat labels
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
		
		if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(65.75, 76 + (76 * (1 - scrollPercentToTop)), "Accuracy:");
		if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(67.2, 71 + (76 * (1 - scrollPercentToTop)), "Speed:");
		if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(66.15, 66 + (76 * (1 - scrollPercentToTop)), "Strength:");
		if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(67.2, 61 + (76 * (1 - scrollPercentToTop)), "Armor:");
		if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(67.3, 56 + (76 * (1 - scrollPercentToTop)), "Melee:");
		if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(66.4, 51 + (76 * (1 - scrollPercentToTop)), "Archery:");
		if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(67.45, 46 + (76 * (1 - scrollPercentToTop)), "Magic:");
		///////////////////
		
		//Item's stats
		StdDraw.setPenColor(StdDraw.CYAN);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
		
		if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 76 + (76 * (1 - scrollPercentToTop)), ((Item_Equipment)this.selectedItem).accuracy + "");
		if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 71 + (76 * (1 - scrollPercentToTop)), ((Item_Equipment)this.selectedItem).speed + "");
		if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 66 + (76 * (1 - scrollPercentToTop)), ((Item_Equipment)this.selectedItem).maxHit + "");
		if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 61 + (76 * (1 - scrollPercentToTop)), ((Item_Equipment)this.selectedItem).armor + "");
		if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 56 + (76 * (1 - scrollPercentToTop)), ((Item_Equipment)this.selectedItem).meleeBoost + "");
		if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 51 + (76 * (1 - scrollPercentToTop)), ((Item_Equipment)this.selectedItem).archeryBoost + "");
		if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 46 + (76 * (1 - scrollPercentToTop)), ((Item_Equipment)this.selectedItem).magicBoost + "");
		///////////////////
		
		//Difference between current worn item
		StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
		
		//Helmet
		if (((Item_Equipment)this.selectedItem).slot == 1) {
			
			//Accuracy
			if (((Player_Sandbox)this.owner).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).accuracy - ((Player_Sandbox)this.owner).helmet.accuracy) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).accuracy - ((Player_Sandbox)this.owner).helmet.accuracy) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player_Sandbox)this.owner).helmet.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player_Sandbox)this.owner).helmet.speed) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).helmet.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player_Sandbox)this.owner).helmet.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player_Sandbox)this.owner).helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player_Sandbox)this.owner).helmet.maxHit) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player_Sandbox)this.owner).helmet.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player_Sandbox)this.owner).helmet.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player_Sandbox)this.owner).helmet.armor) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).helmet.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player_Sandbox)this.owner).helmet.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player_Sandbox)this.owner).helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player_Sandbox)this.owner).helmet.meleeBoost) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player_Sandbox)this.owner).helmet.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player_Sandbox)this.owner).helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player_Sandbox)this.owner).helmet.archeryBoost) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player_Sandbox)this.owner).helmet.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player_Sandbox)this.owner).helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player_Sandbox)this.owner).helmet.magicBoost) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player_Sandbox)this.owner).helmet.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
		}
		///////////////////
		
		//Weapon
		if (((Item_Equipment)this.selectedItem).slot == 7) {
			
			//Accuracy
			if (((Player_Sandbox)this.owner).weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).accuracy - ((Player_Sandbox)this.owner).weapon.accuracy) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).accuracy - ((Player_Sandbox)this.owner).weapon.accuracy) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player_Sandbox)this.owner).weapon.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player_Sandbox)this.owner).weapon.speed) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).weapon.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player_Sandbox)this.owner).weapon.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player_Sandbox)this.owner).weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player_Sandbox)this.owner).weapon.maxHit) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player_Sandbox)this.owner).weapon.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player_Sandbox)this.owner).weapon.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player_Sandbox)this.owner).weapon.armor) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).weapon.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player_Sandbox)this.owner).weapon.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player_Sandbox)this.owner).weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player_Sandbox)this.owner).weapon.meleeBoost) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player_Sandbox)this.owner).weapon.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player_Sandbox)this.owner).weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player_Sandbox)this.owner).weapon.archeryBoost) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player_Sandbox)this.owner).weapon.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player_Sandbox)this.owner).weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player_Sandbox)this.owner).weapon.magicBoost) + ")");
			}
			
			else if (((Player_Sandbox)this.owner).weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player_Sandbox)this.owner).weapon.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
		}
		///////////////////
		//////////////////////////////////////////////////////////////////
		
		//Abilities
		if (((Item_Equipment) this.selectedItem).slot == 7) {
			if (((Item_Equipment) this.selectedItem).abilities[0] != null) {
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[0].renderIcon(69, 26 + (76 * (1 - scrollPercentToTop)));
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(69, 26 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[1] != null) {
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[1] .renderIcon(86, 26 + (76 * (1 - scrollPercentToTop)));
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(86, 26 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[2] != null) {
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[2].renderIcon(69, 12 + (76 * (1 - scrollPercentToTop)));
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(69, 12 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[3] != null) {
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[3].renderIcon(86, 12 + (76 * (1 - scrollPercentToTop)));
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(86, 12 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[4] != null) {
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[4].renderIcon(69, -2 + (76 * (1 - scrollPercentToTop)));
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(69, -2 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[5] != null) {
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[5].renderIcon(86, -2 + (76 * (1 - scrollPercentToTop)));
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(86, -2 + (76 * (1 - scrollPercentToTop)), 6);
			}
		}
		///////////////////
	}
	
	private boolean checkIfFull() {
		for (Item i : this.items) {
			if (i instanceof Item_Equipment_EmptySpace) return false;
		}
		
		return true;
	}

	private void updateAreYouSureDropButtons() {
		this.areYouSureDropYesButton.update();
		if (this.areYouSureDropYesButton.isSelected) {
			this.areYouSureDropYesButton.isSelected = false;
			deleteItem(this.selectedItem);
			((Player_Sandbox)this.owner).level.getDroppedItemsToAdd().add(new DroppedItem(((Player_Sandbox)this.owner).level, this.selectedItem, ((Player_Sandbox)this.owner).getMapX(), ((Player_Sandbox)this.owner).getMapY()));
			this.selectedItem = null;
			this.areYouSureDrop = false;
			((Player) ((Player_Sandbox)this.owner)).clickDelay = ((Player) ((Player_Sandbox)this.owner)).clickDelayAmount;
		}

		this.areYouSureDropNoButton.update();
		if (this.areYouSureDropNoButton.isSelected) {
			this.areYouSureDropNoButton.isSelected = false;
			this.areYouSureDrop = false;
			((Player) ((Player_Sandbox)this.owner)).clickDelay = ((Player) ((Player_Sandbox)this.owner)).clickDelayAmount;
		}
	}
	
	private void updateButtons() {
		this.sortAllButton.update();
			if (this.sortAllButton.isSelected) {
				setButtonsToUnselected(this.sortAllButton);
			}
			
		this.sortEquipmentButton.update();
			if (this.sortEquipmentButton.isSelected) {
				setButtonsToUnselected(this.sortEquipmentButton);
			}
			
		this.sortFoodButton.update();
			if (this.sortFoodButton.isSelected) {
				setButtonsToUnselected(this.sortFoodButton);
			}
			
		this.sortValueButton.update();
			if (this.sortValueButton.isSelected) {
				setButtonsToUnselected(this.sortValueButton);
			}
	}
	
	private void setButtonsToUnselected(Button exception) {
		if (!exception.equals(this.sortAllButton)) this.sortAllButton.isSelected = false;
		if (!exception.equals(this.sortEquipmentButton)) this.sortEquipmentButton.isSelected = false;
		if (!exception.equals(this.sortFoodButton)) this.sortFoodButton.isSelected = false;
		if (!exception.equals(this.sortValueButton)) this.sortValueButton.isSelected = false;
	}
	
	private void moveEmptySpacesToBottom() {
		for (int i = 0; i < this.size; i++) {
			if (this.items[i] instanceof Item_Equipment_EmptySpace) {
				for (int ii = i; ii < this.size; ii++) {
					if (!(this.items[ii] instanceof Item_Equipment_EmptySpace)) {
						Item temp = this.items[i];
						this.items[i] = this.items[ii];
						this.items[ii] = temp;
						return;
					}
				}
				return;
			}
		}
	}
	
	private void checkInput() {
		//I or ESC - Close inventory
		if (((Player)((Player_Sandbox)this.owner)).buttonDelay == 0 && (StdDraw.isKeyPressed(KeyEvent.VK_I) || StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE))) {
			((Player)((Player_Sandbox)this.owner)).inventoryOpen = false;
			((Player)((Player_Sandbox)this.owner)).buttonDelay = ((Player)((Player_Sandbox)this.owner)).buttonDelayAmount;
		}
		/////////////////////
		
		//Inventory and equipment buttons
		this.inventoryButton.update();
		if (this.inventoryButton.isSelected) {
			this.equipmentButton.isSelected = false;
			this.showEquipment = false;
			this.showInventory = true;
			this.selectedEquipmentSlot = 0;
		}
		
		this.equipmentButton.update();
		if (this.equipmentButton.isSelected) {
			this.inventoryButton.isSelected = false;
			this.showEquipment = true;
			this.showInventory = false;
			this.selectedItem = null;
		}
		/////////////////////
		
		//Update inventory menu if open
		if (this.showInventory) {
			
			//Update scroll bar
			if (!this.selectedEquipmentScrollBar.isMoving) this.scrollBar.update();
			/////////////////////
			
			//Update selected item
			if (StdDraw.mousePressed() && !this.scrollBar.isMoving && !this.selectedEquipmentScrollBar.isMoving && !this.areYouSureDrop && ((Player)((Player_Sandbox)this.owner)).clickDelay == 0) checkForSelectedItem();
			if (this.selectedItem != null) updateSelectedItem();
			/////////////////////
		}
		////////////////////////////////////////////////
		
		//Update equipment menu if open
		else if (this.showEquipment) {
			checkForSelectedEquipment();
			
			this.deselectEquipmentButton.update();
			if (this.deselectEquipmentButton.isSelected) {
				this.deselectEquipmentButton.isSelected = false;
				this.selectedEquipmentSlot = 0;
			}
			
			this.unequipButton.update();
			if (this.unequipButton.isSelected) {
				this.unequipButton.isSelected = false;
				if (!this.isFull) {
					Item temp = null;
				
					//Head
					if (this.selectedEquipmentSlot == 1) {
						temp = ((Player_Sandbox)this.owner).helmet;
						((Player_Sandbox)this.owner).helmet = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Chest
					else if (this.selectedEquipmentSlot == 2) {
						temp = ((Player_Sandbox)this.owner).chest;
						((Player_Sandbox)this.owner).chest = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Legs
					else if (this.selectedEquipmentSlot == 3) {
						temp = ((Player_Sandbox)this.owner).legs;
						((Player_Sandbox)this.owner).legs = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Feet
					else if (this.selectedEquipmentSlot == 4) {
						temp = ((Player_Sandbox)this.owner).boots;
						((Player_Sandbox)this.owner).boots = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Hands
					else if (this.selectedEquipmentSlot == 5) {
						temp = ((Player_Sandbox)this.owner).gloves;
						((Player_Sandbox)this.owner).gloves = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Shield
					else if (this.selectedEquipmentSlot == 6) {
						temp = ((Player_Sandbox)this.owner).shield;
						((Player_Sandbox)this.owner).shield = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Weapon
					else if (this.selectedEquipmentSlot == 7) {
						temp = ((Player_Sandbox)this.owner).weapon;
						((Player_Sandbox)this.owner).weapon = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					addItem(temp);
					this.selectedEquipmentSlot = 0;
				} else {
					this.inventoryFullTimer = this.inventoryFullDelayAmount;
				}
			}
		}
		////////////////////////////////////////////////
	}
	
	private void checkForSelectedItem() {
		//If all items are shown
		if (this.sortAllButton.isSelected) {
			for (int i = 0; i < this.items.length; i++) {
				Item item = this.items[i];
				if (this.size <= 7) {
					if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= (78.5 - (i * 10)) + 5 && StdDraw.mouseY() >= (78.5 - (i * 10)) - 5) {
						this.selectedItem = item;
					}
				} else {
					if (!(item instanceof Item_Equipment_EmptySpace)) {
						double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
						double percentToInvLimit = (i + 0.0) / this.size;
			
						if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
							double yy = ((this.size * 10) - (scrollPercentToTop * ((this.size * 10) - 78.5))) - (i * 10);
				
							if (yy < 88.5 && yy > 0) {
								if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
									this.selectedItem = item;
								}
							}
						}
					}
				}
			}
		}
		////////////////////
		
		//If items are organized by equipment
		else if (this.sortEquipmentButton.isSelected) {
			Item[] equipment = new Item[this.size];
			int equipmentSize = 0;
			
			for (int ii = 0; ii < this.size; ii++) {
				if (this.items[ii] instanceof Item_Equipment && !(this.items[ii] instanceof Item_Equipment_EmptySpace)) {
					equipment[equipmentSize] = this.items[ii];
					equipmentSize++;
				}
			}
			
			for (int ii = 0; ii < equipmentSize; ii++) {
				if (equipment.length == 0) break;
				Item equipmentItem = equipment[ii];
			
				if (equipmentSize <= 7) {
					if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= (78.5 - (ii * 10)) + 5 && StdDraw.mouseY() >= (78.5 - (ii * 10)) - 5) {
						this.selectedItem = equipmentItem;
					}
				} else {
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / equipmentSize;
	
					if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
						double yy = ((equipmentSize * 10) - (scrollPercentToTop * ((equipmentSize * 10) - 78.5))) - (ii * 10);
		
						if (yy < 88.5 && yy > 0) {
							if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
								this.selectedItem = equipmentItem;
							}
						}
					}
				}
			}
		}
		////////////////////
		
		//If items are organized by consumables
		else if (this.sortFoodButton.isSelected) {
			Item[] food = new Item[this.size];
			int foodSize = 0;
			
			for (int ii = 0; ii < this.size; ii++) {
				if (this.items[ii] instanceof Item_Food) {
					food[foodSize] = this.items[ii];
					foodSize++;
				}
			}
			
			for (int ii = 0; ii < foodSize; ii++) {
				if (food.length == 0) break;
				Item foodItem = food[ii];

				if (foodSize <= 7) {
					if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= (78.5 - (ii * 10)) + 5 && StdDraw.mouseY() >= (78.5 - (ii * 10)) - 5) {
						this.selectedItem = foodItem;
					}
				} else {
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / foodSize;

					if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
						double yy = ((foodSize * 10) - (scrollPercentToTop * ((foodSize * 10) - 78.5))) - (ii * 10);
						
						if (yy < 88.5 && yy > 0) {
							if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
								this.selectedItem = foodItem;
							}
						}
					}
				}
			}
		}
		////////////////////
		
		//If items are organized by value
		else if (this.sortValueButton.isSelected) {
			Item[] value = this.items.clone();

			Arrays.sort(value);
			
			for (int ii = 0; ii < this.size; ii++) {
				if (value.length == 0) break;
				Item valueItem = value[ii];
			
				if (this.size <= 7) {
					if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= (78.5 - (ii * 10)) + 5 && StdDraw.mouseY() >= (78.5 - (ii * 10)) - 5) {
						this.selectedItem = valueItem;
					}
				} else {
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / this.size;
	
					if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
						double yy = ((this.size * 10) - (scrollPercentToTop * ((this.size * 10) - 78.5))) - (ii * 10);
		
						if (yy < 88.5 && yy > 0) {
							if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
								this.selectedItem = valueItem;
							}
						}
					}
				}
			}
		}
		////////////////////
		
		//Make sure selected item is not an empty space
		if (this.selectedItem instanceof Item_Equipment_EmptySpace) this.selectedItem = null;
		////////////////////
	}
	
	private void checkForSelectedEquipment() {
		if (Mouse.lastButtonPressed == 1) {
			//Head
			if (!(((Player_Sandbox)this.owner).helmet instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 80 && StdDraw.mouseY() >= 68) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 1;
			}
			///////////////////////
			
			//Chest
			else if (!(((Player_Sandbox)this.owner).chest instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 60 && StdDraw.mouseY() >= 48) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 2;
			}
			///////////////////////
			
			//Legs
			else if (!(((Player_Sandbox)this.owner).legs instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 40 && StdDraw.mouseY() >= 28) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 3;
			}
			///////////////////////
			
			//Feet
			else if (!(((Player_Sandbox)this.owner).boots instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 20 && StdDraw.mouseY() >= 8) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 4;
			}
			///////////////////////
			
			//Hands
			else if (!(((Player_Sandbox)this.owner).gloves instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 4 && StdDraw.mouseX() <= 16 && StdDraw.mouseY() <= 60 && StdDraw.mouseY() >= 48) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 5;
			}
			///////////////////////
			
			//Shield
			else if (!(((Player_Sandbox)this.owner).shield instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 44 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= 40 && StdDraw.mouseY() >= 28) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 6;
			}
			///////////////////////
			
			//Weapon
			else if (!(((Player_Sandbox)this.owner).weapon instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 4 && StdDraw.mouseX() <= 16 && StdDraw.mouseY() <= 40 && StdDraw.mouseY() >= 28) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 7;
			}
			///////////////////////
		}
	}
	
	private void updateSelectedItemButtons() {
		this.deselectItemButton.update();
		if (this.deselectItemButton.isSelected) {
			this.deselectItemButton.isSelected = false;
			this.selectedItem = null;
		}
		
		this.dropButton.update();
		if (this.dropButton.isSelected) {
			this.dropButton.isSelected = false;
			this.areYouSureDrop = true;
		}
		
		if (this.selectedItem instanceof Item_Equipment) {
			this.equipButton.update();
			if (this.equipButton.isSelected) {
				this.equipButton.isSelected = false;
				((Player_Sandbox)this.owner).equipItem((Item_Equipment)this.selectedItem);
				deleteItem(this.selectedItem);
				this.selectedItem = null;
			}
		}
	}
	
	private void updateSelectedItem() {
		//Update scroll bar
		if (!this.scrollBar.isMoving) this.selectedEquipmentScrollBar.update();
		/////////////////////
	}
	
	public void addItem(Item item) {
		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i] instanceof Item_Equipment_EmptySpace) {
				this.items[i] = item;
				return;
			}
		}
	}
	
	public void deleteItem(Item item) {
		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i].equals(item)) {
				this.items[i] = new Item_Equipment_EmptySpace();
				return;
			}
		}
	}
}
