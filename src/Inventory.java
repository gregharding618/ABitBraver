import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Inventory implements Serializable {
	
	public Entity owner;
	public int size;
	public boolean isFull = false;
	
	public Item[] items;
	public transient Item selectedItem;
	
	private ScrollBar scrollBar, selectedItemScrollBar;
	
	private Button sortAllButton, sortEquipmentButton, sortFoodButton, sortValueButton;
	private Button inventoryButton, equipmentButton;
	private Button deselectItemButton, deselectEquipmentButton, unequipButton, dropButton, areYouSureDropYesButton, areYouSureDropNoButton;
	private Button equipButton, eatButton;
	
	private boolean showInventory = true, showEquipment = false;
	private transient boolean areYouSureDrop = false;
	
	private int selectedEquipmentSlot = 0; //0 = none, 1 = head, 2 = chest, 3 = legs, 4 = boots, 5 = gloves, 6 = shield, 7 = weapon
	private int inventoryFullTimer = 0, inventoryFullDelayAmount = 300;
	
	private static final DecimalFormat df = new DecimalFormat("#.##");
	private static final DecimalFormat percent = new DecimalFormat("#");
	
	public Inventory(Entity owner, int size) {
		this.owner = owner;
		this.size = size;
		items = new Item[this.size];
		
		if (this.owner instanceof Player) {
			for (int i = 0; i < this.size; i++) this.items[i] = new Item_Equipment("helmet_gold.png", "", "Test Helmet " + (i + 1), false, 1, true, false, false, 1, i, 3, 5, 5, 1, 5, 1, 5, 9);
			this.items[2] = new Item_Food("Jewbies", "jewbies.png", true, 28, 400, 2, 0, 414, 294, 0, 19, 87);
			this.items[3] = new Item_Money(265);
			this.items[4] = new Item_Equipment("bow.png", "", "Test Bow", false, 1, false, true, false, 7, 36, 49, 49, 25, 49, 49, 31, 49, 49);
			this.items[5] = new Item_Equipment("dagger.png", "", "Test Weapon2", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47);
			
			//Inventory tab
			this.scrollBar = new ScrollBar(58, 75, 85, 5, 1.25, 10, true, new Color(165, 165, 187));
			
			this.sortAllButton = new Button(((Player)this.owner), 20, 95.5, 2.85, 1.85, "All", 24, -0.25, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.sortAllButton.isSelected = true;
			
			this.sortEquipmentButton = new Button(((Player)this.owner), 29.5, 95.5, 5.2, 1.85, "Equipment", 16, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			
			this.sortFoodButton = new Button(((Player)this.owner), 39, 95.5, 3, 1.85, "Food", 16, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			
			this.sortValueButton = new Button(((Player)this.owner), 46.5, 95.5, 3.2, 1.85, "Value", 16, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			/////////////////////////
			
			//Menu tab buttons
			this.inventoryButton = new Button(((Player)this.owner), 20, 2.5, 5.6, 1.75, "Inventory", 19, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.inventoryButton.isSelected = true;
			this.equipmentButton = new Button(((Player)this.owner), 35, 2.5, 6.25, 1.75, "Equipment", 19, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			/////////////////////////
			
			//Selected item buttons
			this.dropButton = new Button(((Player)this.owner), 70, 87.15, 3.5, 1.6, "Drop", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.deselectItemButton = new Button(((Player)this.owner), 90, 87.15, 4.8, 1.6, "Deselect", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.equipButton = new Button(((Player)this.owner), 80, 94, 3.5, 1.6, "Equip", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.areYouSureDropYesButton = new Button(((Player)this.owner), 40, 44, 5, 3, "YES", 24, 0, StdDraw.GRAY, StdDraw.GREEN);
			this.areYouSureDropNoButton = new Button(((Player)this.owner), 60, 44, 5, 3, "NO", 24, 0, StdDraw.GRAY, StdDraw.RED);
			this.eatButton = new Button(((Player)this.owner), 80, 94, 3.25, 1.6, "Eat", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			/////////////////////////
			
			//Selected equipment buttons
			this.deselectEquipmentButton = new Button(((Player)this.owner), 58, 90, 4.8, 1.6, "Deselect", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			this.unequipButton = new Button(((Player)this.owner), 42, 90, 4.8, 1.6, "Unequip", 17, 0, StdDraw.LIGHT_GRAY, StdDraw.GREEN);
			/////////////////////////
			
			//Selected item scroll bar
			this.selectedItemScrollBar = new ScrollBar(97.5, 73, 83, 5, 1.25, 10, true, new Color(165, 165, 187));
			/////////////////////////
			
			this.isFull = checkIfFull();
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
		checkForZeroAmounts();
		this.isFull = checkIfFull();
		
		if (this.inventoryFullTimer > 0) this.inventoryFullTimer--;
		
		if (((Player)this.owner) instanceof Player) {
			checkInput();
			if (this.areYouSureDrop) updateAreYouSureDropButtons();
			if (this.showInventory && !this.areYouSureDrop && !this.scrollBar.isMoving && !this.selectedItemScrollBar.isMoving) updateSortButtons();
			if (this.selectedItem != null && !this.areYouSureDrop && !this.scrollBar.isMoving && !this.selectedItemScrollBar.isMoving) updateSelectedItemButtons();
		} else {
			
		}
	}

	public boolean checkIfFull() {
		for (Item i : this.items) {
			if (i instanceof Item_Equipment_EmptySpace) return false;
		}
		
		return true;
	}
	
	public int emptySlots() {
		int slots = 0;
		
		for (Item item : this.items) {
			if (item instanceof Item_Equipment_EmptySpace) slots++;
		}
		
		return slots;
	}

	private void updateAreYouSureDropButtons() {
		this.areYouSureDropYesButton.update();
		if (this.areYouSureDropYesButton.isSelected) {
			this.areYouSureDropYesButton.isSelected = false;
			deleteItem(this.selectedItem, 1);

			ArrayList<String> attributes = GameUtilities.createAttributes(this.selectedItem, 1);
			
			Packet025AddDroppedItem packet = new Packet025AddDroppedItem(this.owner.level.name, GameUtilities.itemTypeToString(this.selectedItem), this.owner.getMapX(), this.owner.getMapY(), attributes);
			packet.writeData(Game.socketClient);
			
			this.selectedItem = null;
			this.areYouSureDrop = false;
			((Player)this.owner).clickDelay = ((Player)this.owner).clickDelayAmount;
		}

		this.areYouSureDropNoButton.update();
		if (this.areYouSureDropNoButton.isSelected) {
			this.areYouSureDropNoButton.isSelected = false;
			this.areYouSureDrop = false;
			((Player)this.owner).clickDelay = ((Player)this.owner).clickDelayAmount;
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
				deleteItem(this.selectedItem, 1);
				((Player)this.owner).equipItem((Item_Equipment)this.selectedItem);
				this.selectedItem = null;
			}
		}
		
		else if (this.selectedItem instanceof Item_Food) {
			this.eatButton.update();
			if (this.eatButton.isSelected && (((Player)this.owner)).clickDelay == 0) {
				this.eatButton.isSelected = false;
				(((Player)this.owner)).clickDelay = (((Player)this.owner)).clickDelayAmount;
				
				(((Player)this.owner)).eat((Item_Food)this.selectedItem);
				
				if (this.selectedItem.stackable) {
					this.selectedItem.amount--;
					if (this.selectedItem.amount <= 0) {
						deleteItem(this.selectedItem, 1);
						this.selectedItem = null;
					}
				}
				else {
					deleteItem(this.selectedItem, 1);
					this.selectedItem = null;
				}
			}
		}
	}
	
	public void addItem(Item item) {
		if (item.stackable) {
			for (int i = 0; i < this.items.length; i++) {
				if (!this.items[i].stackable) continue;
				
				if (item instanceof Item_Money && this.items[i] instanceof Item_Money) {
					this.items[i].amount += item.amount;
					return;
				}
				
				else if (item instanceof Item_Food && this.items[i] instanceof Item_Food) {
					if (item.name.equals(this.items[i].name) &&
						item.iconImageFile.equals(this.items[i].iconImageFile) &&
						item.value == this.items[i].value &&
						((Item_Food)item).healAmount == ((Item_Food)this.items[i]).healAmount &&
						((Item_Food)item).meleeBoost == ((Item_Food)this.items[i]).meleeBoost &&
						((Item_Food)item).archeryBoost == ((Item_Food)this.items[i]).archeryBoost &&
						((Item_Food)item).magicBoost == ((Item_Food)this.items[i]).magicBoost &&
						((Item_Food)item).maxHitBoost == ((Item_Food)this.items[i]).maxHitBoost &&
						((Item_Food)item).accuracyBoost == ((Item_Food)this.items[i]).accuracyBoost &&
						((Item_Food)item).defenseBoost == ((Item_Food)this.items[i]).defenseBoost) {
						
						this.items[i].amount += item.amount;
						return;
					}
				}
				
				else if (item instanceof Item_Equipment && this.items[i] instanceof Item_Equipment) {
					if (item.iconImageFile.equals(this.items[i].iconImageFile) &&
						((Item_Equipment)item).entityImageFile.equals(((Item_Equipment)this.items[i]).entityImageFile) &&
						item.name.equals(this.items[i].name) &&
						((Item_Equipment)item).isMelee == ((Item_Equipment)this.items[i]).isMelee &&
						((Item_Equipment)item).isArchery == ((Item_Equipment)this.items[i]).isArchery &&
						((Item_Equipment)item).isMagic == ((Item_Equipment)this.items[i]).isMagic &&
						item.value == this.items[i].value &&
						((Item_Equipment)item).slot == ((Item_Equipment)this.items[i]).slot &&
						((Item_Equipment)item).range == ((Item_Equipment)this.items[i]).range &&
						((Item_Equipment)item).speed == ((Item_Equipment)this.items[i]).speed &&
						((Item_Equipment)item).accuracy == ((Item_Equipment)this.items[i]).accuracy &&
						((Item_Equipment)item).maxHit == ((Item_Equipment)this.items[i]).maxHit &&
						((Item_Equipment)item).armor == ((Item_Equipment)this.items[i]).armor &&
						((Item_Equipment)item).meleeBoost == ((Item_Equipment)this.items[i]).meleeBoost &&
						((Item_Equipment)item).archeryBoost == ((Item_Equipment)this.items[i]).archeryBoost &&
						((Item_Equipment)item).magicBoost == ((Item_Equipment)this.items[i]).magicBoost) {
						
						if (((Item_Equipment)item).slot == 7) {
							boolean sameAbilities = true;
							for (int ii = 0; ii < ((Item_Equipment)item).abilities.length; ii++) {
								if (!((Item_Equipment)item).abilities[ii].equals(((Item_Equipment)this.items[i]).abilities[ii])) {
									sameAbilities = false;
									break;
								}
							}
							
							if (sameAbilities) {
								this.items[i].amount += item.amount;
								return;
							}
							
						} else {
							this.items[i].amount += item.amount;
							return;
						}
					}
				}
			}
		}
		
		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i] instanceof Item_Equipment_EmptySpace) {
				this.items[i] = item;
				return;
			}
		}
	}
	
	public void deleteItem(Item item, int amount) {
		if (item.stackable) {
			for (int i = 0; i < this.items.length; i++) {
				if (this.items[i].equals(item)) {
					this.items[i].amount -= amount;
					if (this.items[i].amount <= 0) this.items[i] = new Item_Equipment_EmptySpace();
					return;
				}
			}
		}
		
		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i].equals(item)) {
				this.items[i] = new Item_Equipment_EmptySpace();
				return;
			}
		}
	}

	private void renderEquipment() {
		double[] xCoords, yCoords;
		
		//Background
		StdDraw.setPenColor(new Color(0, 0, 0, 199));
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
		
		//Energy bar
		double reducingEnergyPosition = (((Player)this.owner)).energyToReduce * 0.965;
		if ((((Player)this.owner)).energyToReduce > 0) (((Player)this.owner)).energyToReduce -= (((Player)this.owner)).energyToReduce * 0.035;
		if ((((Player)this.owner)).energyToReduce < 0) (((Player)this.owner)).energyToReduce = 0;
	
		if ((((Player)this.owner)).energyToReduce >= 33) {
			(((Player)this.owner)).energyRed += 5;
			(((Player)this.owner)).energyGreen++;
			(((Player)this.owner)).energyBlue += 5;
		
			if ((((Player)this.owner)).energyRed > 253) (((Player)this.owner)).energyRed = 253;
			if ((((Player)this.owner)).energyGreen > 249) (((Player)this.owner)).energyGreen = 249;
			if ((((Player)this.owner)).energyBlue > 253) (((Player)this.owner)).energyBlue = 253;
		} else {
			(((Player)this.owner)).energyRed -= 2;
			(((Player)this.owner)).energyGreen--;
			(((Player)this.owner)).energyBlue -= 2;
		
			if ((((Player)this.owner)).energyRed < 89) (((Player)this.owner)).energyRed = 89;
			if ((((Player)this.owner)).energyGreen < 222) (((Player)this.owner)).energyGreen = 222;
			if ((((Player)this.owner)).energyBlue < 77) (((Player)this.owner)).energyBlue = 77;
		}
	
		StdDraw.setPenColor(new Color((((Player)this.owner)).energyRed, (((Player)this.owner)).energyGreen, (((Player)this.owner)).energyBlue));
		xCoords = new double[]{45, 45, 45 + (48 * (((((Player)this.owner)).energy + reducingEnergyPosition) / ((((Player)this.owner)).maxEnergy + 0.0))), 45 + (48 * (((((Player)this.owner)).energy + reducingEnergyPosition) / ((((Player)this.owner)).maxEnergy + 0.0)))};
		yCoords = new double[]{10.33, 8.33, 8.33, 10.33};
		StdDraw.filledPolygon(xCoords, yCoords);
	
		StdDraw.setPenColor(new Color(70, 31, 62));
		StdDraw.setPenRadius(0.0075);
		StdDraw.rectangle(69, 9.33, 24, 1);
		StdDraw.setPenRadius();
		
		StdDraw.setPenColor(new Color(89, 222, 77));
		StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
		StdDraw.text(69, 13, "Energy");
		////////////////////
		
		//Selected equipment name
		if (this.selectedEquipmentSlot > 0) {
			String itemName = "";
						
			//Head
			if (this.selectedEquipmentSlot == 1) {
				itemName = ((Player)this.owner).helmet.name;
			}
			////////////////////
			
			//Chest
			else if (this.selectedEquipmentSlot == 2) {
				itemName = ((Player)this.owner).chest.name;
			}
			////////////////////
			
			//Legs
			else if (this.selectedEquipmentSlot == 3) {
				itemName = ((Player)this.owner).legs.name;
			}
			////////////////////
			
			//Feet
			else if (this.selectedEquipmentSlot == 4) {
				itemName = ((Player)this.owner).boots.name;
			}
			////////////////////
			
			//Hands
			else if (this.selectedEquipmentSlot == 5) {
				itemName = ((Player)this.owner).gloves.name;
			}
			////////////////////
			
			//Shield
			else if (this.selectedEquipmentSlot == 6) {
				itemName = ((Player)this.owner).shield.name;
			}
			////////////////////
			
			//Weapon
			else if (this.selectedEquipmentSlot == 7) {
				itemName = ((Player)this.owner).weapon.name;
			}
			////////////////////
			
			//Ammo
			else if (this.selectedEquipmentSlot == 8) {
				itemName = ((Player)this.owner).ammo.name;
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
		StdDraw.setPenColor(new Color(255, 255, 255, 236));
		StdDraw.setPenRadius(0.004);
		StdDraw.line(30, 74, 30, 14);
		StdDraw.line(30, 54, 10, 54);
		StdDraw.line(30, 54, 10, 34);
		StdDraw.line(30, 54, 50, 34);
		StdDraw.line(30, 54, 10, 74);
		StdDraw.setPenRadius();
		////////////////////
		
		//Head
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 1) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(30, 74, 6);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).helmet instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 82, ((Player)this.owner).helmet.name);
		else StdDraw.text(30, 82, "Head");
		StdDraw.square(30, 74, 6);
		((Player)this.owner).helmet.render(30, 74);
		////////////////////
		
		//Chest
		if (this.selectedEquipmentSlot == 2) StdDraw.setPenColor(new Color(87, 102, 88));
		else StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(30, 54, 6);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).chest instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 62, ((Player)this.owner).chest.name);
		else StdDraw.text(30, 62, "Chest");
		StdDraw.square(30, 54, 6);
		((Player)this.owner).chest.render(30, 54);
		////////////////////
		
		//Legs
		if (this.selectedEquipmentSlot == 3) StdDraw.setPenColor(new Color(87, 102, 88));
		else StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(30, 34, 6);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).legs instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 42, ((Player)this.owner).legs.name);
		else StdDraw.text(30, 42, "Legs");
		StdDraw.square(30, 34, 6);
		((Player)this.owner).legs.render(30, 34);
		////////////////////
		
		//Feet
		if (this.selectedEquipmentSlot == 4) StdDraw.setPenColor(new Color(87, 102, 88));
		else StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(30, 14, 6);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).boots instanceof Item_Equipment_EmptySpace)) StdDraw.text(30, 22, ((Player)this.owner).boots.name);
		else StdDraw.text(30, 22, "Feet");
		StdDraw.square(30, 14, 6);
		((Player)this.owner).boots.render(30, 14);
		////////////////////
		
		//Hands
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 5) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(10, 54, 6);
		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).gloves instanceof Item_Equipment_EmptySpace)) StdDraw.text(10, 62, ((Player)this.owner).gloves.name);
		else StdDraw.text(10, 62, "Hands");
		StdDraw.square(10, 54, 6);
		((Player)this.owner).gloves.render(30, 54);
		////////////////////
		
		//Shield
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 6) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(50, 34, 6);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).shield instanceof Item_Equipment_EmptySpace)) {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(50, 42, ((Player)this.owner).shield.name);
		}
		else {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(50, 42, "Shield");
		}
		StdDraw.square(50, 34, 6);
		((Player)this.owner).shield.render(50, 34);
		////////////////////
		
		//Weapon
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 7) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(10, 34, 6);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).weapon instanceof Item_Equipment_EmptySpace)) {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(10, 42, ((Player)this.owner).weapon.name);
		}
		else {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(10, 42, "Weapon");
		}
		StdDraw.square(10, 34, 6);
		((Player)this.owner).weapon.render(10, 34);
		////////////////////
		
		//Ammo
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.selectedEquipmentSlot == 8) StdDraw.setPenColor(new Color(87, 102, 88));
		StdDraw.filledSquare(10, 74, 6);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 23));
		if (!(((Player)this.owner).ammo instanceof Item_Equipment_EmptySpace)) {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(10, 82, ((Player)this.owner).ammo.name);
		}
		else {
			StdDraw.setPenColor(StdDraw.GRAY);
			StdDraw.text(10, 82, "Ammo");
		}
		StdDraw.square(10, 74, 6);
		((Player)this.owner).ammo.render(10, 34);
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
		StdDraw.text(89, 76, ((Player)this.owner).overallLevel + "");
		
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.text(78, 72, "Melee:");
		StdDraw.text(89, 72, ((Player)this.owner).melee + "");
		
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.text(78, 68, "Archery:");
		StdDraw.text(89, 68, ((Player)this.owner).archery + "");
		
		StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.text(78, 64, "Magic:");
		StdDraw.text(89, 64, ((Player)this.owner).magic + "");
		
		StdDraw.setPenColor(StdDraw.CYAN);
		StdDraw.text(78, 60, "Defense:");
		StdDraw.text(89, 60, ((Player)this.owner).defense + "");
		
		StdDraw.setPenColor(StdDraw.PINK);
		StdDraw.text(78, 56, "Health:");
		StdDraw.text(89, 56, ((Player)this.owner).currentHealth + " / " + ((Player)this.owner).maxHealth);
		//////////////////////
		
		//Level progress bars
		
		//Overall
		StdDraw.setPenColor(StdDraw.BLACK);
		
		int nextLevel = ((Player)this.owner).overallLevel + 1;
		double currentLevel = (((Player)this.owner).defense / 2.0) + ((((Player)this.owner).melee + ((Player)this.owner).archery + ((Player)this.owner).magic) / 2.25) + (((Player)this.owner).maxHealth / 30.0);
		StdDraw.setPenColor(StdDraw.ORANGE);
		xCoords = new double[]{48, 48, 48 + (((1 - (nextLevel - currentLevel)) * 100) / 4.15), 48 + (((1 - (nextLevel - currentLevel)) * 100) / 4.15)}; //(this.currentHealth / 4.2) + 1, (this.currentHealth / 4.2) + 1    works for 100 max health but nothing else
		yCoords = new double[]{77.5, 74.5, 74.5, 77.5};
		StdDraw.filledPolygon(xCoords, yCoords);
		
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.008);
		StdDraw.rectangle(60, 76, 12, 1.5);
		StdDraw.setPenRadius();
		//////////////////////
		
		//Melee
		StdDraw.setPenColor(StdDraw.BLACK);
		
		StdDraw.setPenColor(StdDraw.RED);
		xCoords = new double[]{48, 48, 48 + ((((Player)this.owner)).meleeXpPercent / 4.15), 48 + ((((Player)this.owner)).meleeXpPercent / 4.15)}; //(this.currentHealth / 4.2) + 1, (this.currentHealth / 4.2) + 1    works for 100 max health but nothing else
		yCoords = new double[]{73.5, 70.5, 70.5, 73.5};
		StdDraw.filledPolygon(xCoords, yCoords);
		
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.008);
		StdDraw.rectangle(60, 72, 12, 1.5);
		StdDraw.setPenRadius();
		//////////////////////
		
		//Archery
		StdDraw.setPenColor(StdDraw.BLACK);
		
		StdDraw.setPenColor(new Color(130, 176, 18));
		xCoords = new double[]{48, 48, 48 + ((((Player)this.owner)).archeryXpPercent / 4.15), 48 + ((((Player)this.owner)).archeryXpPercent / 4.15)}; //(this.currentHealth / 4.2) + 1, (this.currentHealth / 4.2) + 1    works for 100 max health but nothing else
		yCoords = new double[]{69.5, 66.5, 66.5, 69.5};
		StdDraw.filledPolygon(xCoords, yCoords);
		
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.008);
		StdDraw.rectangle(60, 68, 12, 1.5);
		StdDraw.setPenRadius();
		//////////////////////
		
		//Magic
		StdDraw.setPenColor(StdDraw.BLACK);
		
		StdDraw.setPenColor(StdDraw.BLUE);
		xCoords = new double[]{48, 48, 48 + ((((Player)this.owner)).magicXpPercent / 4.15), 48 + ((((Player)this.owner)).magicXpPercent / 4.15)}; //(this.currentHealth / 4.2) + 1, (this.currentHealth / 4.2) + 1    works for 100 max health but nothing else
		yCoords = new double[]{65.5, 62.5, 62.5, 65.5};
		StdDraw.filledPolygon(xCoords, yCoords);
		
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.008);
		StdDraw.rectangle(60, 64, 12, 1.5);
		StdDraw.setPenRadius();
		//////////////////////
		
		//Defense
		StdDraw.setPenColor(StdDraw.BLACK);
		
		StdDraw.setPenColor(StdDraw.CYAN);
		xCoords = new double[]{48, 48, 48 + ((((Player)this.owner)).defenseXpPercent / 4.15), 48 + ((((Player)this.owner)).defenseXpPercent / 4.15)}; //(this.currentHealth / 4.2) + 1, (this.currentHealth / 4.2) + 1    works for 100 max health but nothing else
		yCoords = new double[]{61.5, 58.5, 58.5, 61.5};
		StdDraw.filledPolygon(xCoords, yCoords);
		
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.008);
		StdDraw.rectangle(60, 60, 12, 1.5);
		StdDraw.setPenRadius();
		//////////////////////
		
		//////////////////////
		
		//Stats
		StdDraw.setPenColor(new Color(151, 181, 201));
		StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
		StdDraw.text(85, 46, "Stats");
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 22));
		StdDraw.setPenColor(StdDraw.WHITE);
		
		StdDraw.text(78, 42, "Armor:");
		StdDraw.text(89, 42, ((Player)this.owner).armor + "");
		
		StdDraw.text(78, 38, "Accuracy:");
		StdDraw.text(89, 38, df.format(((Player)this.owner).accuracy));
		
		StdDraw.text(78, 34, "Strength:");
		StdDraw.text(89, 34, ((Player)this.owner).maxHit + "");
		
		StdDraw.text(78, 30, "Melee:");
		StdDraw.text(89, 30, ((Player)this.owner).meleeBoost + "");
		
		StdDraw.text(78, 26, "Archery:");
		StdDraw.text(89, 26, ((Player)this.owner).archeryBoost + "");
		
		StdDraw.text(78, 22, "Magic:");
		StdDraw.text(89, 22, ((Player)this.owner).magicBoost + "");
		//////////////////////
		
		//Boosts
		StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
		
		if (((Player)this.owner).tempDefenseBoost > 0) {
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(95, 60, "+" + ((Player)this.owner).tempDefenseBoost);
		}
		
		else if (((Player)this.owner).tempDefenseBoost < 0) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.text(95, 60, "" + ((Player)this.owner).tempDefenseBoost);
		}
		
		if (((Player)this.owner).tempAccuracyBoost > 0) {
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(95, 38, "+" + ((Player)this.owner).tempAccuracyBoost);
		}
		
		else if (((Player)this.owner).tempAccuracyBoost < 0) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.text(95, 38, "" + ((Player)this.owner).tempAccuracyBoost);
		}
		
		if (((Player)this.owner).tempMaxHitBoost > 0) {
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(95, 34, "+" + ((Player)this.owner).tempMaxHitBoost);
		}
		
		else if (((Player)this.owner).tempMaxHitBoost < 0) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.text(95, 34, "" + ((Player)this.owner).tempMaxHitBoost);
		}
		
		if (((Player)this.owner).tempMeleeBoost > 0) {
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(95, 30, "+" + ((Player)this.owner).tempMeleeBoost);
		}
		
		else if (((Player)this.owner).tempMeleeBoost < 0) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.text(95, 30, "" + ((Player)this.owner).tempMeleeBoost);
		}
		
		if (((Player)this.owner).tempArcheryBoost > 0) {
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(95, 26, "+" + ((Player)this.owner).tempArcheryBoost);
		}
		
		else if (((Player)this.owner).tempArcheryBoost < 0) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.text(95, 26, "" + ((Player)this.owner).tempArcheryBoost);
		}
		
		if (((Player)this.owner).tempMagicBoost > 0) {
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(95, 22, "+" + ((Player)this.owner).tempMagicBoost);
		}
		
		else if (((Player)this.owner).tempMagicBoost < 0) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.text(95, 22, "" + ((Player)this.owner).tempMagicBoost);
		}
		//////////////////////
		////////////////////////////////////////
	}
	
	private void renderInventory() {
		//Background
		StdDraw.setPenColor(new Color(0, 0, 0, 170));
		StdDraw.filledRectangle(30, 50, 30, 48);
		////////////////////
		
		//Organize list
		moveEmptySpacesToBottom();
		////////////////////
			
		//Item list
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
					if (item.stackable) StdDraw.text(30, 78.5 - (i * 10), item.name + " (" + item.amount + ")");
					else StdDraw.text(30, 78.5 - (i * 10), item.name);
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
								if (item.stackable) StdDraw.text(30, yy, item.name + " (" + item.amount + ")");
								else StdDraw.text(30, yy, item.name);
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
					if (equipmentItem.stackable) StdDraw.text(30, 78.5 - (ii * 10), equipmentItem.name + " (" + equipmentItem.amount + ")");
					else StdDraw.text(30, 78.5 - (ii * 10), equipmentItem.name);
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
							if (equipmentItem.stackable) StdDraw.text(30, yy, equipmentItem.name + " (" + equipmentItem.amount + ")");
							else StdDraw.text(30, yy, equipmentItem.name);
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
					if (foodItem.stackable) StdDraw.text(30, 78.5 - (ii * 10), foodItem.name + " (" + foodItem.amount + ")");
					else StdDraw.text(30, 78.5 - (ii * 10), foodItem.name);
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
							if (foodItem.stackable) StdDraw.text(30, yy, foodItem.name + " (" + foodItem.amount + ")");
							else StdDraw.text(30, yy, foodItem.name);
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
					if (valueItem.stackable) StdDraw.text(30, 78.5 - (ii * 10), valueItem.name + " (" + valueItem.amount + ")");
					else StdDraw.text(30, 78.5 - (ii * 10), valueItem.name);
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
								if (valueItem.stackable) StdDraw.text(30, yy, valueItem.name + " (" + valueItem.amount + ")");
								else StdDraw.text(30, yy, valueItem.name);
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
	//////////////////////////////////////////
		
		//Selected item info
		if (this.selectedItem != null && !this.showEquipment) {
			
			//Backgrond
			StdDraw.setPenColor(new Color(0, 0, 0, 170));
			StdDraw.filledRectangle(80, 49, 20, 49);
			////////////////////
			
			//If selected item is equipment
			if (this.selectedItem instanceof Item_Equipment) renderSelectedEquipmentInfo();
			////////////////////
			
			//If selected item is food
			else if (this.selectedItem instanceof Item_Food) renderSelectedFoodInfo();
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
			else if (this.selectedItem instanceof Item_Food) this.eatButton.render();
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
	
	private void renderSelectedFoodInfo() {
		double scrollPercentToTop = (this.selectedItemScrollBar.y - this.selectedItemScrollBar.min - this.selectedItemScrollBar.height) / (this.selectedItemScrollBar.max - (this.selectedItemScrollBar.height * 2) - this.selectedItemScrollBar.min); //0.##

		//Item name
		if (81 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.setPenColor(new Color(189, 144, 132));
			StdDraw.filledRectangle(77.5, 81.25 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
			if (this.selectedItem.stackable) StdDraw.text(77.5, 81 + (76 * (1 - scrollPercentToTop)), this.selectedItem.name + " (" + this.selectedItem.amount + ")");
			else StdDraw.text(77.5, 81 + (76 * (1 - scrollPercentToTop)), this.selectedItem.name);
		}
		///////////////////

		//Stat labels
		double yy = 76;
		for (int i = 0; i < 7; i++) {
			if (i == 0) {
				if (((Item_Food)this.selectedItem).healAmount != 0) {
					if (yy + (76 * (1 - scrollPercentToTop)) < 88) {
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
						StdDraw.text(70, yy + (76 * (1 - scrollPercentToTop)), "Heal:");
						StdDraw.setPenColor(StdDraw.CYAN);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
						StdDraw.text(86, yy + (76 * (1 - scrollPercentToTop)), ((Item_Food)this.selectedItem).healAmount + "");
					}
					yy -= 5;
				}
			}
				
			else if (i == 1) {
				if (((Item_Food)this.selectedItem).accuracyBoost != 0) {
					if (yy + (76 * (1 - scrollPercentToTop)) < 88) {
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
						StdDraw.text(70, yy + (76 * (1 - scrollPercentToTop)), "Accuracy boost:");
						StdDraw.setPenColor(StdDraw.CYAN);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
						StdDraw.text(86, yy + (76 * (1 - scrollPercentToTop)), ((Item_Food)this.selectedItem).accuracyBoost + "");
					}
					yy -= 5;
				}
			}
			
			else if (i == 2) {
				if (((Item_Food)this.selectedItem).maxHitBoost != 0) {
					if (yy + (76 * (1 - scrollPercentToTop)) < 88) {
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
						StdDraw.text(70, yy + (76 * (1 - scrollPercentToTop)), "Strength boost:");
						StdDraw.setPenColor(StdDraw.CYAN);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
						StdDraw.text(86, yy + (76 * (1 - scrollPercentToTop)), ((Item_Food)this.selectedItem).maxHitBoost + "");
					}
					yy -= 5;
				}
			}
			
			else if (i == 3) {
				if (((Item_Food)this.selectedItem).meleeBoost != 0) {
					if (yy + (76 * (1 - scrollPercentToTop)) < 88) {
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
						StdDraw.text(70, yy + (76 * (1 - scrollPercentToTop)), "Melee boost:");
						StdDraw.setPenColor(StdDraw.CYAN);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
						StdDraw.text(86, yy + (76 * (1 - scrollPercentToTop)), ((Item_Food)this.selectedItem).meleeBoost + "");
					}
					yy -= 5;
				}
			}
			
			else if (i == 4) {
				if (((Item_Food)this.selectedItem).archeryBoost != 0) {
					if (yy + (76 * (1 - scrollPercentToTop)) < 88) {
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
						StdDraw.text(70, yy + (76 * (1 - scrollPercentToTop)), "Archery boost:");
						StdDraw.setPenColor(StdDraw.CYAN);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
						StdDraw.text(86, yy + (76 * (1 - scrollPercentToTop)), ((Item_Food)this.selectedItem).archeryBoost + "");
					}
					yy -= 5;
				}
			}
			
			else if (i == 5) {
				if (((Item_Food)this.selectedItem).magicBoost != 0) {
					if (yy + (76 * (1 - scrollPercentToTop)) < 88) {
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
						StdDraw.text(70, yy + (76 * (1 - scrollPercentToTop)), "Magic boost:");
						StdDraw.setPenColor(StdDraw.CYAN);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
						StdDraw.text(86, yy + (76 * (1 - scrollPercentToTop)), ((Item_Food)this.selectedItem).magicBoost + "");
					}
					yy -= 5;
				}
			}
			
			else if (i == 6) {
				if (((Item_Food)this.selectedItem).defenseBoost != 0) {
					if (yy + (76 * (1 - scrollPercentToTop)) < 88) {
						StdDraw.setPenColor(StdDraw.WHITE);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
						StdDraw.text(70, yy + (76 * (1 - scrollPercentToTop)), "Defense boost:");
						StdDraw.setPenColor(StdDraw.CYAN);
						StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
						StdDraw.text(86, yy + (76 * (1 - scrollPercentToTop)), ((Item_Food)this.selectedItem).defenseBoost + "");
					}
					yy -= 5;
				}
			}
		}
		///////////////////
	
		//Current Boosts
		if (yy - 5 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.setPenColor(new Color(189, 144, 132));
			StdDraw.filledRectangle(77.5, yy - 4.75 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
			StdDraw.text(77.5, yy - 5 + (76 * (1 - scrollPercentToTop)), "Current Boosts");
		}
			
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
			
		if (yy - 10 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 10 + (76 * (1 - scrollPercentToTop)), "Health:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 10 + (76 * (1 - scrollPercentToTop)), ((Player)this.owner).currentHealth + "/" + ((Player)this.owner).maxHealth);
		}
		
		if (yy - 15 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 15 + (76 * (1 - scrollPercentToTop)), "Accuracy boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 15 + (76 * (1 - scrollPercentToTop)), ((Player)this.owner).tempAccuracyBoost + "");
		}
		
		if (yy - 20 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 20 + (76 * (1 - scrollPercentToTop)), "Strength boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 20 + (76 * (1 - scrollPercentToTop)), ((Player)this.owner).tempMaxHitBoost + "");
		}
		
		if (yy - 25 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 25 + (76 * (1 - scrollPercentToTop)), "Melee boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 25 + (76 * (1 - scrollPercentToTop)), ((Player)this.owner).tempMeleeBoost + "");
		}
		
		if (yy - 30 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 30 + (76 * (1 - scrollPercentToTop)), "Archery boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 30 + (76 * (1 - scrollPercentToTop)), ((Player)this.owner).tempArcheryBoost + "");
		}
		
		if (yy - 35 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 35 + (76 * (1 - scrollPercentToTop)), "Magic boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 35 + (76 * (1 - scrollPercentToTop)), ((Player)this.owner).tempMagicBoost + "");
		}
		
		if (yy - 40 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 40 + (76 * (1 - scrollPercentToTop)), "Defense boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 40 + (76 * (1 - scrollPercentToTop)), ((Player)this.owner).tempDefenseBoost + "");
		}
		///////////////////
		
		//Difference comparisons
		StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
		
		//Heal
		if (yy - 10 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).healAmount < 0) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).healAmount + ")");
			}
			
			else if (((Player)this.owner).maxHealth - ((Player)this.owner).currentHealth > ((Item_Food)this.selectedItem).healAmount) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).healAmount + ")");
			}
			
			else if (((Player)this.owner).maxHealth - ((Player)this.owner).currentHealth < ((Item_Food)this.selectedItem).healAmount && ((Player)this.owner).maxHealth - ((Player)this.owner).currentHealth > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).healAmount - (((Player)this.owner).maxHealth - ((Player)this.owner).currentHealth)) + ")");
			}
			
			else if (((Player)this.owner).maxHealth == ((Player)this.owner).currentHealth || ((Item_Food)this.selectedItem).healAmount == 0) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Accuracy
		if (yy - 15 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).accuracyBoost < 0) {
				if (((Item_Food)this.selectedItem).accuracyBoost >= ((Player)this.owner).tempAccuracyBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.owner).tempAccuracyBoost >= 0) StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).accuracyBoost + ")");
					else StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).accuracyBoost - ((Player)this.owner).tempAccuracyBoost) + ")");
				}
			}
			
			else if (((Player)this.owner).tempAccuracyBoost < ((Item_Food)this.selectedItem).accuracyBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.owner).tempAccuracyBoost < 0) StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).accuracyBoost + ")");
				else StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).accuracyBoost - ((Player)this.owner).tempAccuracyBoost) + ")");
			}
			
			else if (((Player)this.owner).tempAccuracyBoost >= ((Item_Food)this.selectedItem).accuracyBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Max hit
		if (yy - 20 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).maxHitBoost < 0) {
				if (((Item_Food)this.selectedItem).maxHitBoost >= ((Player)this.owner).tempMaxHitBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.owner).tempMaxHitBoost >= 0) StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).maxHitBoost + ")");
					else StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).maxHitBoost - ((Player)this.owner).tempMaxHitBoost) + ")");
				}
			}
			
			else if (((Player)this.owner).tempMaxHitBoost < ((Item_Food)this.selectedItem).maxHitBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.owner).tempMaxHitBoost < 0) StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).maxHitBoost + ")");
				else StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).maxHitBoost - ((Player)this.owner).tempMaxHitBoost) + ")");
			}
			
			else if (((Player)this.owner).tempMaxHitBoost >= ((Item_Food)this.selectedItem).maxHitBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Melee
		if (yy - 25 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).meleeBoost < 0) {
				if (((Item_Food)this.selectedItem).meleeBoost >= ((Player)this.owner).tempMeleeBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.owner).tempMeleeBoost >= 0) StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).meleeBoost + ")");
					else StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).meleeBoost - ((Player)this.owner).tempMeleeBoost) + ")");
				}
			}
			
			else if (((Player)this.owner).tempMeleeBoost < ((Item_Food)this.selectedItem).meleeBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.owner).tempMeleeBoost < 0) StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).meleeBoost + ")");
				else StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).meleeBoost - ((Player)this.owner).tempMeleeBoost) + ")");
			}
			
			else if (((Player)this.owner).tempMeleeBoost >= ((Item_Food)this.selectedItem).meleeBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Archery
		if (yy - 30 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).archeryBoost < 0) {
				if (((Item_Food)this.selectedItem).archeryBoost >= ((Player)this.owner).tempArcheryBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.owner).tempArcheryBoost >= 0) StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).archeryBoost + ")");
					else StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).archeryBoost - ((Player)this.owner).tempArcheryBoost) + ")");
				}
			}
			
			else if (((Player)this.owner).tempArcheryBoost < ((Item_Food)this.selectedItem).archeryBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.owner).tempArcheryBoost < 0) StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).archeryBoost + ")");
				else StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).archeryBoost - ((Player)this.owner).tempArcheryBoost) + ")");
			}
			
			else if (((Player)this.owner).tempArcheryBoost >= ((Item_Food)this.selectedItem).archeryBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Magic
		if (yy - 35 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).magicBoost < 0) {
				if (((Item_Food)this.selectedItem).magicBoost >= ((Player)this.owner).tempMagicBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.owner).tempMagicBoost >= 0) StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).magicBoost + ")");
					else StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).magicBoost - ((Player)this.owner).tempMagicBoost) + ")");
				}
			}
			
			else if (((Player)this.owner).tempMagicBoost < ((Item_Food)this.selectedItem).magicBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.owner).tempMagicBoost < 0) StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).magicBoost + ")");
				else StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).magicBoost - ((Player)this.owner).tempMagicBoost) + ")");
			}
			
			else if (((Player)this.owner).tempMagicBoost >= ((Item_Food)this.selectedItem).magicBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Defense
		if (yy - 40 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).defenseBoost < 0) {
				if (((Item_Food)this.selectedItem).defenseBoost >= ((Player)this.owner).tempDefenseBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.owner).tempDefenseBoost >= 0) StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).defenseBoost + ")");
					else StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).defenseBoost - ((Player)this.owner).tempDefenseBoost) + ")");
				}
			}
			
			else if (((Player)this.owner).tempDefenseBoost < ((Item_Food)this.selectedItem).defenseBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.owner).tempDefenseBoost < 0) StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).defenseBoost + ")");
				else StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).defenseBoost - ((Player)this.owner).tempDefenseBoost) + ")");
			}
			
			else if (((Player)this.owner).tempDefenseBoost >= ((Item_Food)this.selectedItem).defenseBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Scroll bar border
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(this.selectedItemScrollBar.x, 40, 2.5, 45);
		///////////////////

		//Scroll bar crevice
		StdDraw.setPenColor(new Color(0, 0, 0, 170));
		StdDraw.filledRectangle(this.selectedItemScrollBar.x, (this.selectedItemScrollBar.max + this.selectedItemScrollBar.min) / 2, this.selectedItemScrollBar.width, this.selectedItemScrollBar.max - ((this.selectedItemScrollBar.max + this.selectedItemScrollBar.min) / 2));
		///////////////////
		
		//Scroll bar
		this.selectedItemScrollBar.render();
		///////////////////
	}

	private void renderSelectedEquipmentInfo() {
		double scrollPercentToTop = (this.selectedItemScrollBar.y - this.selectedItemScrollBar.min - this.selectedItemScrollBar.height) / (this.selectedItemScrollBar.max - (this.selectedItemScrollBar.height * 2) - this.selectedItemScrollBar.min); //0.##

		//Item name
		if (81 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.setPenColor(new Color(189, 144, 132));
			StdDraw.filledRectangle(77.5, 81.25 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
			if (this.selectedItem.stackable) StdDraw.text(77.5, 81 + (76 * (1 - scrollPercentToTop)), this.selectedItem.name + " (" + this.selectedItem.amount + ")");
			else StdDraw.text(77.5, 81 + (76 * (1 - scrollPercentToTop)), this.selectedItem.name);
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
		StdDraw.filledRectangle(this.selectedItemScrollBar.x, 40, 2.5, 45);
		///////////////////
		
		//Scroll bar crevice
		StdDraw.setPenColor(new Color(0, 0, 0, 170));
		StdDraw.filledRectangle(this.selectedItemScrollBar.x, (this.selectedItemScrollBar.max + this.selectedItemScrollBar.min) / 2, this.selectedItemScrollBar.width, this.selectedItemScrollBar.max - ((this.selectedItemScrollBar.max + this.selectedItemScrollBar.min) / 2));
		///////////////////
		
		//Scroll bar
		this.selectedItemScrollBar.render();
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
		
		if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(80, 76 + (76 * (1 - scrollPercentToTop)), df.format(((Item_Equipment)this.selectedItem).accuracy));
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
			if ((((Player)this.owner)).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).helmet.accuracy)) + ")");
			}
			
			else if ((((Player)this.owner)).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).helmet.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.owner).helmet.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).helmet.speed) + ")");
			}
			
			else if (((Player)this.owner).helmet.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).helmet.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.owner).helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).helmet.maxHit) + ")");
			}
			
			else if (((Player)this.owner).helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).helmet.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.owner).helmet.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).helmet.armor) + ")");
			}
			
			else if (((Player)this.owner).helmet.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).helmet.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.owner).helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).helmet.meleeBoost) + ")");
			}
			
			else if (((Player)this.owner).helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).helmet.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.owner).helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).helmet.archeryBoost) + ")");
			}
			
			else if (((Player)this.owner).helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).helmet.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.owner).helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).helmet.magicBoost) + ")");
			}
			
			else if (((Player)this.owner).helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).helmet.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
		}
		///////////////////
		
		//Chest
		if (((Item_Equipment)this.selectedItem).slot == 2) {
			
			//Accuracy
			if (((Player)this.owner).chest.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).chest.accuracy)) + ")");
			}
			
			else if (((Player)this.owner).chest.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).chest.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.owner).chest.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).chest.speed) + ")");
			}
			
			else if (((Player)this.owner).chest.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).chest.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.owner).chest.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).chest.maxHit) + ")");
			}
			
			else if (((Player)this.owner).chest.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).chest.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.owner).chest.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).chest.armor) + ")");
			}
			
			else if (((Player)this.owner).chest.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).chest.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.owner).chest.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).chest.meleeBoost) + ")");
			}
			
			else if (((Player)this.owner).chest.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).chest.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.owner).chest.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).chest.archeryBoost) + ")");
			}
			
			else if (((Player)this.owner).chest.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).chest.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.owner).chest.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).chest.magicBoost) + ")");
			}
			
			else if (((Player)this.owner).chest.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).chest.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
		}
		///////////////////
		
		//Legs
		if (((Item_Equipment)this.selectedItem).slot == 3) {
			
			//Accuracy
			if (((Player)this.owner).legs.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).legs.accuracy)) + ")");
			}
			
			else if (((Player)this.owner).legs.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).legs.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.owner).legs.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).legs.speed) + ")");
			}
			
			else if (((Player)this.owner).legs.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).legs.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.owner).legs.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).legs.maxHit) + ")");
			}
			
			else if (((Player)this.owner).legs.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).legs.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.owner).legs.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).legs.armor) + ")");
			}
			
			else if (((Player)this.owner).legs.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).legs.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.owner).legs.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).legs.meleeBoost) + ")");
			}
			
			else if (((Player)this.owner).legs.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).legs.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.owner).legs.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).legs.archeryBoost) + ")");
			}
			
			else if (((Player)this.owner).legs.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).legs.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.owner).legs.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).legs.magicBoost) + ")");
			}
			
			else if (((Player)this.owner).legs.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).legs.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
		}
		///////////////////
		
		//Boots
		if (((Item_Equipment)this.selectedItem).slot == 4) {
			
			//Accuracy
			if (((Player)this.owner).boots.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).boots.accuracy)) + ")");
			}
			
			else if (((Player)this.owner).boots.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).boots.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.owner).boots.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).boots.speed) + ")");
			}
			
			else if (((Player)this.owner).boots.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).boots.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.owner).boots.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).boots.maxHit) + ")");
			}
			
			else if (((Player)this.owner).boots.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).boots.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.owner).boots.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).boots.armor) + ")");
			}
			
			else if (((Player)this.owner).boots.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).boots.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.owner).boots.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).boots.meleeBoost) + ")");
			}
			
			else if (((Player)this.owner).boots.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).boots.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.owner).boots.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).boots.archeryBoost) + ")");
			}
			
			else if (((Player)this.owner).boots.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).boots.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.owner).boots.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).boots.magicBoost) + ")");
			}
			
			else if (((Player)this.owner).boots.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).boots.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
		}
		///////////////////
		
		//Gloves
		if (((Item_Equipment)this.selectedItem).slot == 5) {
			
			//Accuracy
			if (((Player)this.owner).gloves.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).gloves.accuracy)) + ")");
			}
			
			else if (((Player)this.owner).gloves.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).gloves.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.owner).gloves.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).gloves.speed) + ")");
			}
			
			else if (((Player)this.owner).gloves.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).gloves.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.owner).gloves.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).gloves.maxHit) + ")");
			}
			
			else if (((Player)this.owner).gloves.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).gloves.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.owner).gloves.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).gloves.armor) + ")");
			}
			
			else if (((Player)this.owner).gloves.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).gloves.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.owner).gloves.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).gloves.meleeBoost) + ")");
			}
			
			else if (((Player)this.owner).gloves.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).gloves.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.owner).gloves.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).gloves.archeryBoost) + ")");
			}
			
			else if (((Player)this.owner).gloves.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).gloves.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.owner).gloves.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).gloves.magicBoost) + ")");
			}
			
			else if (((Player)this.owner).gloves.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).gloves.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
		}
		///////////////////
		
		//Shield
		if (((Item_Equipment)this.selectedItem).slot == 6) {
			
			//Accuracy
			if (((Player)this.owner).shield.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).shield.accuracy)) + ")");
			}
			
			else if (((Player)this.owner).shield.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).shield.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.owner).shield.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).shield.speed) + ")");
			}
			
			else if (((Player)this.owner).shield.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).shield.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.owner).shield.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).shield.maxHit) + ")");
			}
			
			else if (((Player)this.owner).shield.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).shield.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.owner).shield.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).shield.armor) + ")");
			}
			
			else if (((Player)this.owner).shield.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).shield.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.owner).shield.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).shield.meleeBoost) + ")");
			}
			
			else if (((Player)this.owner).shield.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).shield.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.owner).shield.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).shield.archeryBoost) + ")");
			}
			
			else if (((Player)this.owner).shield.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).shield.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.owner).shield.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).shield.magicBoost) + ")");
			}
			
			else if (((Player)this.owner).shield.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).shield.magicBoost) + ")");
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
			if (((Player)this.owner).weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).weapon.accuracy)) + ")");
			}
			
			else if (((Player)this.owner).weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.owner).weapon.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.owner).weapon.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).weapon.speed) + ")");
			}
			
			else if (((Player)this.owner).weapon.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.owner).weapon.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.owner).weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).weapon.maxHit) + ")");
			}
			
			else if (((Player)this.owner).weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.owner).weapon.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.owner).weapon.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).weapon.armor) + ")");
			}
			
			else if (((Player)this.owner).weapon.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.owner).weapon.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.owner).weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).weapon.meleeBoost) + ")");
			}
			
			else if (((Player)this.owner).weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.owner).weapon.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.owner).weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).weapon.archeryBoost) + ")");
			}
			
			else if (((Player)this.owner).weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.owner).weapon.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.owner).weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).weapon.magicBoost) + ")");
			}
			
			else if (((Player)this.owner).weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.owner).weapon.magicBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Type
			StdDraw.setFont(new Font("Arial", Font.BOLD, 27));
			
			if (((Item_Equipment)this.selectedItem).isMelee) {
				StdDraw.setPenColor(StdDraw.RED);
				if (41 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(77.5, 41 + (76 * (1 - scrollPercentToTop)), "MELEE");
			}
			
			else if (((Item_Equipment)this.selectedItem).isArchery) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (41 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(77.5, 41 + (76 * (1 - scrollPercentToTop)), "ARCHERY");
			}
			
			else if (((Item_Equipment)this.selectedItem).isMagic) {
				StdDraw.setPenColor(StdDraw.BLUE);
				if (41 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(77.5, 41 + (76 * (1 - scrollPercentToTop)), "MAGIC");
			}
			///////////////////
		}
		///////////////////
		//////////////////////////////////////////////////////////////////
		
		//Abilities
		Ability describeAbility = null;
		
		if (((Item_Equipment) this.selectedItem).slot == 7) {
			if (((Item_Equipment) this.selectedItem).abilities[0] != null) {
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[0].renderIcon(69, 26 + (76 * (1 - scrollPercentToTop)));
				if (StdDraw.mouseY() < 85 && StdDraw.mouseX() <= 75 && StdDraw.mouseX() >= 63 && StdDraw.mouseY() <= 32 + (76 * (1 - scrollPercentToTop)) && StdDraw.mouseY() >= 20 + (76 * (1 - scrollPercentToTop))) describeAbility = ((Item_Equipment) this.selectedItem).abilities[0];
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(69, 26 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[1] != null) {
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[1] .renderIcon(86, 26 + (76 * (1 - scrollPercentToTop)));
				if (StdDraw.mouseY() < 85 && StdDraw.mouseX() <= 92 && StdDraw.mouseX() >= 80 && StdDraw.mouseY() <= 32 + (76 * (1 - scrollPercentToTop)) && StdDraw.mouseY() >= 20 + (76 * (1 - scrollPercentToTop))) describeAbility = ((Item_Equipment) this.selectedItem).abilities[1];
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (26 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(86, 26 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[2] != null) {
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[2].renderIcon(69, 12 + (76 * (1 - scrollPercentToTop)));
				if (StdDraw.mouseY() < 85 && StdDraw.mouseX() <= 75 && StdDraw.mouseX() >= 63 && StdDraw.mouseY() <= 18 + (76 * (1 - scrollPercentToTop)) && StdDraw.mouseY() >= 6 + (76 * (1 - scrollPercentToTop))) describeAbility = ((Item_Equipment) this.selectedItem).abilities[2];
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(69, 12 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[3] != null) {
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[3].renderIcon(86, 12 + (76 * (1 - scrollPercentToTop)));
				if (StdDraw.mouseY() < 85 && StdDraw.mouseX() <= 92 && StdDraw.mouseX() >= 80 && StdDraw.mouseY() <= 18 + (76 * (1 - scrollPercentToTop)) && StdDraw.mouseY() >= 6 + (76 * (1 - scrollPercentToTop))) describeAbility = ((Item_Equipment) this.selectedItem).abilities[3];
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (12 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(86, 12 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[4] != null) {
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[4].renderIcon(69, -2 + (76 * (1 - scrollPercentToTop)));
				if (StdDraw.mouseY() < 85 && StdDraw.mouseX() <= 75 && StdDraw.mouseX() >= 63 && StdDraw.mouseY() <= 4 + (76 * (1 - scrollPercentToTop)) && StdDraw.mouseY() >= -8 + (76 * (1 - scrollPercentToTop))) describeAbility = ((Item_Equipment) this.selectedItem).abilities[4];
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(69, -2 + (76 * (1 - scrollPercentToTop)), 6);
			}

			if (((Item_Equipment) this.selectedItem).abilities[5] != null) {
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) ((Item_Equipment) this.selectedItem).abilities[5].renderIcon(86, -2 + (76 * (1 - scrollPercentToTop)));
				if (StdDraw.mouseY() < 85 && StdDraw.mouseX() <= 92 && StdDraw.mouseX() >= 80 && StdDraw.mouseY() <= 4 + (76 * (1 - scrollPercentToTop)) && StdDraw.mouseY() >= -8 + (76 * (1 - scrollPercentToTop))) describeAbility = ((Item_Equipment) this.selectedItem).abilities[5];
			} else {
				StdDraw.setPenColor(StdDraw.GRAY);
				if (-2 + (76 * (1 - scrollPercentToTop)) < 91) StdDraw.square(86, -2 + (76 * (1 - scrollPercentToTop)), 6);
			}
		}
		
		//Render ability description window
		if (describeAbility != null) {
			describeAbility.renderDescription(StdDraw.mouseX(), StdDraw.mouseY());
		}
		///////////////////
		
		///////////////////
	}

	private void updateSortButtons() {
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
	
	public void moveEmptySpacesToBottom() {
		for (int i = 0; i < this.size; i++) {
			if (this.items[i] == null || this.items[i] instanceof Item_Equipment_EmptySpace) {
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
		if ((((Player)this.owner)).buttonDelay == 0 && (StdDraw.isKeyPressed(KeyEvent.VK_I) || StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE))) {
			(((Player)this.owner)).inventoryOpen = false;
			this.areYouSureDrop = false;
			this.selectedItem = null;
			(((Player)this.owner)).buttonDelay = (((Player)this.owner)).buttonDelayAmount;
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
			this.areYouSureDrop = false;
		}
		/////////////////////
		
		//Update inventory menu if open
		if (this.showInventory) {
			
			//Update scroll bar
			if (!this.selectedItemScrollBar.isMoving) this.scrollBar.update();
			/////////////////////
			
			//Update selected item
			if (StdDraw.mousePressed() && !this.scrollBar.isMoving && !this.selectedItemScrollBar.isMoving && !this.areYouSureDrop && (((Player)this.owner)).clickDelay == 0) checkForSelectedItem();
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
						temp = ((Player)this.owner).helmet;
						((Player)this.owner).helmet = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Chest
					else if (this.selectedEquipmentSlot == 2) {
						temp = ((Player)this.owner).chest;
						((Player)this.owner).chest = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Legs
					else if (this.selectedEquipmentSlot == 3) {
						temp = ((Player)this.owner).legs;
						((Player)this.owner).legs = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Feet
					else if (this.selectedEquipmentSlot == 4) {
						temp = ((Player)this.owner).boots;
						((Player)this.owner).boots = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Hands
					else if (this.selectedEquipmentSlot == 5) {
						temp = ((Player)this.owner).gloves;
						((Player)this.owner).gloves = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Shield
					else if (this.selectedEquipmentSlot == 6) {
						temp = ((Player)this.owner).shield;
						((Player)this.owner).shield = new Item_Equipment_EmptySpace();
					}
					/////////////////////
					
					//Weapon
					else if (this.selectedEquipmentSlot == 7) {
						temp = ((Player)this.owner).weapon;
						((Player)this.owner).weapon = new Item_Equipment_EmptySpace();
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

	private void checkForSelectedEquipment() {
		if (Mouse.lastButtonPressed == 1) {
			//Head
			if (!(((Player)this.owner).helmet instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 80 && StdDraw.mouseY() >= 68) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 1;
			}
			///////////////////////
			
			//Chest
			else if (!(((Player)this.owner).chest instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 60 && StdDraw.mouseY() >= 48) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 2;
			}
			///////////////////////
			
			//Legs
			else if (!(((Player)this.owner).legs instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 40 && StdDraw.mouseY() >= 28) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 3;
			}
			///////////////////////
			
			//Feet
			else if (!(((Player)this.owner).boots instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 24 && StdDraw.mouseX() <= 36 && StdDraw.mouseY() <= 20 && StdDraw.mouseY() >= 8) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 4;
			}
			///////////////////////
			
			//Hands
			else if (!(((Player)this.owner).gloves instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 4 && StdDraw.mouseX() <= 16 && StdDraw.mouseY() <= 60 && StdDraw.mouseY() >= 48) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 5;
			}
			///////////////////////
			
			//Shield
			else if (!(((Player)this.owner).shield instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 44 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= 40 && StdDraw.mouseY() >= 28) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 6;
			}
			///////////////////////
			
			//Weapon
			else if (!(((Player)this.owner).weapon instanceof Item_Equipment_EmptySpace) && StdDraw.mouseX() >= 4 && StdDraw.mouseX() <= 16 && StdDraw.mouseY() <= 40 && StdDraw.mouseY() >= 28) {
				Mouse.lastButtonPressed = -1;
				this.selectedEquipmentSlot = 7;
			}
			///////////////////////
		}
	}

	private void updateSelectedItem() {
		//Update scroll bar
		if (!this.scrollBar.isMoving) this.selectedItemScrollBar.update();
		/////////////////////
	}

	private void checkForSelectedItem() {
		//If all items are shown
		if (this.sortAllButton.isSelected) {
			for (int i = 0; i < this.items.length; i++) {
				Item item = this.items[i];
				if (this.size <= 7) {
					if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= (78.5 - (i * 10)) + 5 && StdDraw.mouseY() >= (78.5 - (i * 10)) - 5) {
						this.selectedItem = item;
						break;
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
									break;
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
						break;
					}
				} else {
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / equipmentSize;
	
					if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
						double yy = ((equipmentSize * 10) - (scrollPercentToTop * ((equipmentSize * 10) - 78.5))) - (ii * 10);
		
						if (yy < 88.5 && yy > 0) {
							if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
								this.selectedItem = equipmentItem;
								break;
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
						break;
					}
				} else {
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / foodSize;

					if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
						double yy = ((foodSize * 10) - (scrollPercentToTop * ((foodSize * 10) - 78.5))) - (ii * 10);
						
						if (yy < 88.5 && yy > 0) {
							if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
								this.selectedItem = foodItem;
								break;
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
						break;
					}
				} else {
					double scrollPercentToTop = (this.scrollBar.y - this.scrollBar.min - this.scrollBar.height) / (this.scrollBar.max - (this.scrollBar.height * 2) - this.scrollBar.min); //0.##
					double percentToInvLimit = (ii + 0.0) / this.size;
	
					if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
						double yy = ((this.size * 10) - (scrollPercentToTop * ((this.size * 10) - 78.5))) - (ii * 10);
		
						if (yy < 88.5 && yy > 0) {
							if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
								this.selectedItem = valueItem;
								break;
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

	public void checkForZeroAmounts() {
		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i].amount <= 0) this.items[i] = new Item_Equipment_EmptySpace();
		}
	}

}
