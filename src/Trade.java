import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Trade {
	
	public PlayerMP player, otherPlayer;
	
	public boolean playerAccepted = false, otherPlayerAccepted = false;
	
	private List<Item> playerItems = new ArrayList<Item>();
	private List<Item> otherPlayerItems = new ArrayList<Item>();
	
	private Item selectedItem;
	private String amount = "";
	
	private Button acceptButton, declineButton, addItemButton, cancelAddItemButton, addSelectedItemButton, deselectItemButton, 
	removeSelectedItemButton, unacceptButton, okAddAmountButton, okRemoveAmountButton;
	
	private ScrollBar inventoryScrollBar, selectedItemScrollBar;
	
	private boolean addingItem = false, enteringAddAmount = false, enteringRemoveAmount = false;
	
	private int notEnoughInventorySpaceTimer = 0;
	private final int notEnoughInventorySpaceTimerAmount = 200;
	
	private static DecimalFormat df = new DecimalFormat("#.##");
	
	public Trade(PlayerMP player, PlayerMP otherPlayer) {
		this.player = player;
		this.otherPlayer = otherPlayer;
		
		this.acceptButton = new Button(this.player, 30, 12.7, 7, 1.75, "Accept", 16, -0.1, new Color(109, 66, 154), StdDraw.GREEN);
		this.addItemButton = new Button(this.player, 50, 12.7, 7, 1.75, "Add Item", 16, -0.1, new Color(109, 66, 154), new Color(88, 161, 197));
		this.declineButton = new Button(this.player, 70, 12.7, 7, 1.75, "Decline", 16, -0.1, new Color(109, 66, 154), new Color(202, 9, 58));
		this.cancelAddItemButton = new Button(this.player, 30, 2.5, 6, 1.5, "Cancel", 16, -0.1, new Color(109, 66, 154), new Color(75, 187, 193));
		this.addSelectedItemButton = new Button(this.player, 80, 94, 6, 1.5, "ADD", 16, -0.1, new Color(109, 66, 154), new Color(75, 187, 193));
		this.deselectItemButton = new Button(this.player, 80, 89.5, 6, 1.5, "DESELECT", 16, -0.1, new Color(109, 66, 154), new Color(75, 187, 193));
		this.removeSelectedItemButton = new Button(this.player, 80, 94, 6, 1.5, "REMOVE", 16, -0.1, new Color(109, 66, 154), new Color(75, 187, 193));
		this.unacceptButton = new Button(this.player, 30, 12.7, 7, 1.75, "Unaccept", 16, -0.1, new Color(109, 66, 154), StdDraw.GREEN);
		this.okAddAmountButton = new Button(this.player, 50, 43, 7, 1.75, "OK", 16, -0.1, new Color(109, 66, 154), StdDraw.GREEN);
		this.okRemoveAmountButton = new Button(this.player, 50, 43, 7, 1.75, "OK", 16, -0.1, new Color(109, 66, 154), StdDraw.GREEN);
		
		this.inventoryScrollBar = new ScrollBar(58, 75, 85, 5, 1.25, 10, true, new Color(165, 165, 187));
		this.selectedItemScrollBar = new ScrollBar(97.5, 73, 83, 5, 1.25, 10, true, new Color(165, 165, 187));
	}
	
	public void update() {
		if (this.notEnoughInventorySpaceTimer > 0) this.notEnoughInventorySpaceTimer--;
		
		if (!this.addingItem) {
			if (!this.enteringRemoveAmount) updateButtons();
			
			if (this.selectedItem != null && !getPlayerItems().contains(this.selectedItem) && !getOtherPlayerItems().contains(this.selectedItem)) {
				this.selectedItem = null;
			}
		} else {
				updateAddItemButtons();
				
			if (!this.enteringAddAmount && !this.enteringRemoveAmount) {
				updateInventory();
				checkForSelectedItem();
			}
		}
		
		if (this.selectedItem != null && !this.enteringAddAmount) {
			this.selectedItemScrollBar.update();
			
			if (!this.addingItem && getOtherPlayerItems().contains(this.selectedItem)) {
				this.deselectItemButton.update();
				if (this.deselectItemButton.isSelected) {
					this.deselectItemButton.isSelected = false;
					this.selectedItem = null;
				}
			}
			
			else if (!this.addingItem && getPlayerItems().contains(this.selectedItem)) {
				this.removeSelectedItemButton.update();
				if (this.removeSelectedItemButton.isSelected) {
					this.removeSelectedItemButton.isSelected = false;
					
					if (this.selectedItem.stackable) {
						this.enteringRemoveAmount = true;
					} else {
					
						ArrayList<String> attributes = GameUtilities.createAttributes(this.selectedItem, 1);
					
						this.playerAccepted = false;
						this.otherPlayerAccepted = false;
						
						Packet023UnacceptTrade unacceptPacket = new Packet023UnacceptTrade(this.player.name, this.otherPlayer.name);
						unacceptPacket.writeData(Game.socketClient);
						
						Packet020RemoveItemFromTrade packet = new Packet020RemoveItemFromTrade(this.player.name, this.otherPlayer.name, GameUtilities.itemTypeToString(this.selectedItem), attributes);
						packet.writeData(Game.socketClient);
				
						this.selectedItem = null;
					}
				}
			
				this.deselectItemButton.update();
				if (this.deselectItemButton.isSelected) {
					this.deselectItemButton.isSelected = false;
					this.selectedItem = null;
				}
			}
			
			if (this.enteringRemoveAmount) {
				this.okRemoveAmountButton.update();
				if (this.okRemoveAmountButton.isSelected) {
					this.okRemoveAmountButton.isSelected = false;
					
					if (!this.amount.equals("")) {
						this.enteringRemoveAmount = false;
					
						ArrayList<String> attributes = GameUtilities.createAttributes(this.selectedItem, Integer.parseInt(this.amount));
					
						this.playerAccepted = false;
						this.otherPlayerAccepted = false;
					
						Packet023UnacceptTrade unacceptPacket = new Packet023UnacceptTrade(this.player.name, this.otherPlayer.name);
						unacceptPacket.writeData(Game.socketClient);
					
						Packet020RemoveItemFromTrade packet = new Packet020RemoveItemFromTrade(this.player.name, this.otherPlayer.name, GameUtilities.itemTypeToString(this.selectedItem), attributes);
						packet.writeData(Game.socketClient);
			
						this.selectedItem = null;
						this.amount = "";
					}
				}
				
				else if (StdDraw.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
					StdDraw.keysTyped.clear();
					if (this.amount.length() > 1) {
						this.amount = this.amount.substring(0, this.amount.length() - 1);
					} else {
						this.amount = "";
					}
				}
				
				else if (StdDraw.hasNextKeyTyped()) {
					char c = StdDraw.nextKeyTyped();
					
					if (Character.isDigit(c)) {
						this.amount += c;
						if (this.amount.equals("0")) this.amount = "";
						if (!this.amount.equals("") && Integer.parseInt(this.amount) > this.selectedItem.amount) this.amount = Integer.toString(this.selectedItem.amount);
					}
				}
			}
		}
	}

	public void render() {
		//Background
		StdDraw.setPenColor(new Color(0, 0, 0, 212));
		StdDraw.filledSquare(50, 50, 40);
		//////////////////////////
		
		//Offered items
		int increments = 0;
		double xx = 16.5, yy = 72;
		for (Item item : new ArrayList<>(getPlayerItems())) {
			item.render(xx, yy);
			if (this.selectedItem != null && this.selectedItem.equals(item)) {
				StdDraw.setPenColor(new Color(56, 159, 222));
				StdDraw.setPenRadius(0.008);
				StdDraw.square(xx, yy, 5);
				StdDraw.setPenRadius();
			}
			
			if (!this.addingItem && Mouse.lastButtonPressed == 1 && this.player.clickDelay == 0 && StdDraw.mouseX() >= xx - 3 && StdDraw.mouseX() <= xx + 3 && StdDraw.mouseY() >= yy - 3 && StdDraw.mouseY() <= yy + 3) {
				Mouse.lastButtonPressed = -1;
				this.player.clickDelay = this.player.clickDelayAmount;
				this.selectedItem = item;
			}
			
			xx += 12;
			increments++;
			if (increments == 3) {
				increments = 0;
				xx = 16.5;
				yy -= 12;
			}
		}
		
		xx = 58;
		yy = 72;
		increments = 0;
		for (Item item : new ArrayList<>(getOtherPlayerItems())) {
			item.render(xx, yy);
			if (this.selectedItem != null && this.selectedItem.equals(item)) {
				StdDraw.setPenColor(new Color(56, 159, 222));
				StdDraw.setPenRadius(0.008);
				StdDraw.square(xx, yy, 5);
				StdDraw.setPenRadius();
			}
			
			if (!this.addingItem && Mouse.lastButtonPressed == 1 && this.player.clickDelay == 0 && StdDraw.mouseX() >= xx - 3 && StdDraw.mouseX() <= xx + 3 && StdDraw.mouseY() >= yy - 3 && StdDraw.mouseY() <= yy + 3) {
				Mouse.lastButtonPressed = -1;
				this.player.clickDelay = this.player.clickDelayAmount;
				this.selectedItem = item;
			}
			
			xx += 12;
			increments++;
			if (increments == 3) {
				increments = 0;
				xx = 58;
				yy -= 12;
			}
		}
		//////////////////////////
		
		//Divider
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(50, 50, 1.5, 40);
		//////////////////////////
		
		//Other player accepted trade
		if (this.otherPlayerAccepted) {
			StdDraw.setPenColor(StdDraw.DARK_GRAY);
			StdDraw.filledRectangle(50, 93, 14, 3.5);
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.rectangle(50, 93, 14, 3.5);
			
			StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 22));
			StdDraw.text(50, 94.5, this.otherPlayer.name);
			StdDraw.text(50, 91.5, "has accepted.");
		}
		//////////////////////////
		
		//Top banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(50, 85, 40, 5);
		
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 32));
		StdDraw.text(50, 87, "Trading with " + this.otherPlayer.name);
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 28));
		StdDraw.text(30, 82.25, "Your items");
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 28));
		StdDraw.text(70, 82.25, "Their items");
		//////////////////////////
		
		//Bottom banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(50, 12.5, 40, 2.5);
		
		if (!this.playerAccepted) this.acceptButton.render();
		else this.unacceptButton.render();
		this.addItemButton.render();
		this.declineButton.render();
		//////////////////////////
		
		//Border
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.square(50, 50, 40);
		//////////////////////////
		
		if (this.addingItem) {
			renderInventory();
		}
		
		//Selected item info
		if (this.selectedItem != null) {
			
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
			StdDraw.filledRectangle(80.05, 91.25, 20, 6.5);
			
			if (this.addingItem) this.addSelectedItemButton.render();
			else {
				if (this.getPlayerItems().contains(this.selectedItem)) {
					this.removeSelectedItemButton.render();
				}
			}
			this.deselectItemButton.render();
			////////////////////
			
			//Borders
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.rectangle(80.05, 91.5, 20, 6.5);
			StdDraw.line(60.05, 85, 60.05, -0.5);
			//StdDraw.line(60, 98, 100, 98);
			//StdDraw.line(50, 0.1, 100, 0.1);
			//StdDraw.line(99.9, 98, 99.9, 0.1);
			//StdDraw.line(60, 84.75, 100, 84.75);
			////////////////////
		}
		
		//Not enough inventory space message
		if (this.notEnoughInventorySpaceTimer > 0) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(50, 50, 19, 12);
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.setPenRadius(0.014);
			StdDraw.rectangle(50, 50, 19, 12);
			StdDraw.setPenRadius(0);
		
			StdDraw.setFont(new Font("Arial", Font.BOLD, 26));
			StdDraw.text(50, 53.5, "Not enough");
			StdDraw.text(50, 46.5, "inventory space!");
		}
		////////////////////
		
		//"Enter amount" box
		if (this.enteringAddAmount) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(50, 50, 16, 10);
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.setPenRadius(0.0088);
			StdDraw.rectangle(50, 50, 16, 10);
			StdDraw.setPenRadius();
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
			StdDraw.text(50, 54.5, "How many?");
			
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 21));
			StdDraw.text(50, 50, this.amount);
			
			this.okAddAmountButton.render();
		}
		
		else if (this.enteringRemoveAmount) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(50, 50, 16, 10);
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.setPenRadius(0.0088);
			StdDraw.rectangle(50, 50, 16, 10);
			StdDraw.setPenRadius();
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
			StdDraw.text(50, 54.5, "How many?");
			
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 21));
			StdDraw.text(50, 50, this.amount);
			
			this.okRemoveAmountButton.render();
		}
		////////////////////
		
		//////////////////////////////////////////
	}

	private void updateButtons() {
		if (!this.playerAccepted) {
			this.acceptButton.update();
			if (this.acceptButton.isSelected) {
				this.acceptButton.isSelected = false;
				
				if (this.player.inventory.emptySlots() >= this.getOtherPlayerItems().size()) {
					Packet021AcceptTradeRequest packet = new Packet021AcceptTradeRequest(this.player.name, this.otherPlayer.name);
					packet.writeData(Game.socketClient);
				} else {
					this.notEnoughInventorySpaceTimer = this.notEnoughInventorySpaceTimerAmount;
				}
			}
		} else {
			this.unacceptButton.update();
			if (this.unacceptButton.isSelected) {
				this.unacceptButton.isSelected = false;
				
				this.playerAccepted = false;
				this.otherPlayerAccepted = false;
				
				Packet023UnacceptTrade packet = new Packet023UnacceptTrade(this.player.name, this.otherPlayer.name);
				packet.writeData(Game.socketClient);
			}
		}
	
		this.addItemButton.update();
		if (this.addItemButton.isSelected) {
			this.addItemButton.isSelected = false;
			this.selectedItem = null;
			this.addingItem = true;
		}
	
		this.declineButton.update();
		if (this.declineButton.isSelected) {
			this.declineButton.isSelected = false;
		
			Packet015DeclineTrade declinePacket = new Packet015DeclineTrade(this.player.name, this.otherPlayer.name);
			declinePacket.writeData(Game.socketClient);
		}
	}
	
	private void updateAddItemButtons() {
		this.deselectItemButton.update();
		if (this.deselectItemButton.isSelected) {
			this.deselectItemButton.isSelected = false;
			this.selectedItem = null;
		}
		
		this.cancelAddItemButton.update();
		if (this.cancelAddItemButton.isSelected) {
			this.cancelAddItemButton.isSelected = false;
			this.selectedItem = null;
			this.addingItem = false;
		}
		
		if (this.selectedItem != null && !this.enteringAddAmount) {
			this.addSelectedItemButton.update();
			if (this.addSelectedItemButton.isSelected) {
				if (this.selectedItem.stackable) {
					if (getPlayerItems().size() < 15) {
						this.addSelectedItemButton.isSelected = false;
						this.enteringAddAmount = true;
						StdDraw.keysTyped.clear();
					}
				} else {
					this.addSelectedItemButton.isSelected = false;
					if (getPlayerItems().size() < 15) {
				
						this.player.inventory.deleteItem(this.selectedItem, 1);
					
						ArrayList<String> attributes = GameUtilities.createAttributes(this.selectedItem, 1);
					
						this.playerAccepted = false;
						this.otherPlayerAccepted = false;
						
						Packet023UnacceptTrade unacceptPacket = new Packet023UnacceptTrade(this.player.name, this.otherPlayer.name);
						unacceptPacket.writeData(Game.socketClient);
						
						Packet017AddItemToTrade packet = new Packet017AddItemToTrade(this.player.name, this.otherPlayer.name, GameUtilities.itemTypeToString(this.selectedItem), attributes);
						packet.writeData(Game.socketClient);
				
						this.selectedItem = null;
					}
				}
			}
		}
		
		if (this.enteringAddAmount) {
			
			this.okAddAmountButton.update();
			if (this.okAddAmountButton.isSelected) {
				this.okAddAmountButton.isSelected = false;
				
				if (!this.amount.equals("")) {
					this.enteringAddAmount = false;
				
					this.player.inventory.deleteItem(this.selectedItem, Integer.parseInt(this.amount));
				
					ArrayList<String> attributes = GameUtilities.createAttributes(this.selectedItem, Integer.parseInt(this.amount));
			
					this.playerAccepted = false;
					this.otherPlayerAccepted = false;
					
					Packet023UnacceptTrade unacceptPacket = new Packet023UnacceptTrade(this.player.name, this.otherPlayer.name);
					unacceptPacket.writeData(Game.socketClient);
				
					Packet017AddItemToTrade packet = new Packet017AddItemToTrade(this.player.name, this.otherPlayer.name, GameUtilities.itemTypeToString(this.selectedItem), attributes);
					packet.writeData(Game.socketClient);
		
					this.selectedItem = null;
					this.amount = "";
				}
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
				StdDraw.keysTyped.clear();
				if (this.amount.length() > 1) {
					this.amount = this.amount.substring(0, this.amount.length() - 1);
				} else {
					this.amount = "";
				}
			}
			
			else if (StdDraw.hasNextKeyTyped()) {
				char c = StdDraw.nextKeyTyped();
				
				if (Character.isDigit(c)) {
					this.amount += c;
					if (this.amount.equals("0")) this.amount = "";
					if (!this.amount.equals("") && Integer.parseInt(this.amount) > this.selectedItem.amount) this.amount = Integer.toString(this.selectedItem.amount);
				}
			}
		}
	}
	
	private void updateInventory() {
		//Scrollbar
		this.inventoryScrollBar.update();
		//////////////////////////
	}
	
	private void renderInventory() {
		//Background
		StdDraw.setPenColor(new Color(0, 0, 0, 170));
		StdDraw.filledRectangle(30, 50, 30, 48);
		////////////////////
			
		//Item list
		for (int i = 0; i < this.player.inventory.items.length; i++) {
			Item item = this.player.inventory.items[i];
			double scrollPercentToTop = (this.inventoryScrollBar.y - this.inventoryScrollBar.min - this.inventoryScrollBar.height) / (this.inventoryScrollBar.max - (this.inventoryScrollBar.height * 2) - this.inventoryScrollBar.min); //0.##
			double percentToInvLimit = (i + 0.0) / this.player.inventory.size;
			double yy = ((this.player.inventory.size * 10) - (scrollPercentToTop * ((this.player.inventory.size * 10) - 78.5))) - (i * 10);
				
			if (this.player.inventory.size <= 7) {
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
		////////////////////
		
		//Top banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(30.05, 91.25, 29.96, 6.5);
		
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 26));
		StdDraw.text(30.05, 93, "Select an item to add");
		StdDraw.text(30.05, 90, "to your offer");
		////////////////////
		
		//Bottom banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(30.05, 2.25, 29.96, 2.275);
		StdDraw.filledSquare(59.42, 4.5, 0.58);
		
		this.cancelAddItemButton.render();
		////////////////////
		
		//Border
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.rectangle(30, 49, 30, 48.95);
		////////////////////
		
		//Scroll bar
		this.inventoryScrollBar.render();
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
	}
	
	private void renderSelectedFoodInfo() {
		double scrollPercentToTop = (this.selectedItemScrollBar.y - this.selectedItemScrollBar.min - this.selectedItemScrollBar.height) / (this.selectedItemScrollBar.max - (this.selectedItemScrollBar.height * 2) - this.selectedItemScrollBar.min); //0.##

		//Item name
		if (81 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.setPenColor(new Color(189, 144, 132));
			StdDraw.filledRectangle(78.18, 81.25 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
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
			StdDraw.text(83, yy - 10 + (76 * (1 - scrollPercentToTop)), ((Player)this.player).currentHealth + "/" + ((Player)this.player).maxHealth);
		}
		
		if (yy - 15 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 15 + (76 * (1 - scrollPercentToTop)), "Accuracy boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 15 + (76 * (1 - scrollPercentToTop)), ((Player)this.player).tempAccuracyBoost + "");
		}
		
		if (yy - 20 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 20 + (76 * (1 - scrollPercentToTop)), "Strength boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 20 + (76 * (1 - scrollPercentToTop)), ((Player)this.player).tempMaxHitBoost + "");
		}
		
		if (yy - 25 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 25 + (76 * (1 - scrollPercentToTop)), "Melee boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 25 + (76 * (1 - scrollPercentToTop)), ((Player)this.player).tempMeleeBoost + "");
		}
		
		if (yy - 30 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 30 + (76 * (1 - scrollPercentToTop)), "Archery boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 30 + (76 * (1 - scrollPercentToTop)), ((Player)this.player).tempArcheryBoost + "");
		}
		
		if (yy - 35 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 35 + (76 * (1 - scrollPercentToTop)), "Magic boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 35 + (76 * (1 - scrollPercentToTop)), ((Player)this.player).tempMagicBoost + "");
		}
		
		if (yy - 40 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 40 + (76 * (1 - scrollPercentToTop)), "Defense boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 40 + (76 * (1 - scrollPercentToTop)), ((Player)this.player).tempDefenseBoost + "");
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
			
			else if (((Player)this.player).maxHealth - ((Player)this.player).currentHealth > ((Item_Food)this.selectedItem).healAmount) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).healAmount + ")");
			}
			
			else if (((Player)this.player).maxHealth - ((Player)this.player).currentHealth < ((Item_Food)this.selectedItem).healAmount && ((Player)this.player).maxHealth - ((Player)this.player).currentHealth > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).healAmount - (((Player)this.player).maxHealth - ((Player)this.player).currentHealth)) + ")");
			}
			
			else if (((Player)this.player).maxHealth == ((Player)this.player).currentHealth || ((Item_Food)this.selectedItem).healAmount == 0) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Accuracy
		if (yy - 15 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).accuracyBoost < 0) {
				if (((Item_Food)this.selectedItem).accuracyBoost >= ((Player)this.player).tempAccuracyBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.player).tempAccuracyBoost >= 0) StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).accuracyBoost + ")");
					else StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).accuracyBoost - ((Player)this.player).tempAccuracyBoost) + ")");
				}
			}
			
			else if (((Player)this.player).tempAccuracyBoost < ((Item_Food)this.selectedItem).accuracyBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.player).tempAccuracyBoost < 0) StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).accuracyBoost + ")");
				else StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).accuracyBoost - ((Player)this.player).tempAccuracyBoost) + ")");
			}
			
			else if (((Player)this.player).tempAccuracyBoost >= ((Item_Food)this.selectedItem).accuracyBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Max hit
		if (yy - 20 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).maxHitBoost < 0) {
				if (((Item_Food)this.selectedItem).maxHitBoost >= ((Player)this.player).tempMaxHitBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.player).tempMaxHitBoost >= 0) StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).maxHitBoost + ")");
					else StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).maxHitBoost - ((Player)this.player).tempMaxHitBoost) + ")");
				}
			}
			
			else if (((Player)this.player).tempMaxHitBoost < ((Item_Food)this.selectedItem).maxHitBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.player).tempMaxHitBoost < 0) StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).maxHitBoost + ")");
				else StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).maxHitBoost - ((Player)this.player).tempMaxHitBoost) + ")");
			}
			
			else if (((Player)this.player).tempMaxHitBoost >= ((Item_Food)this.selectedItem).maxHitBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Melee
		if (yy - 25 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).meleeBoost < 0) {
				if (((Item_Food)this.selectedItem).meleeBoost >= ((Player)this.player).tempMeleeBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.player).tempMeleeBoost >= 0) StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).meleeBoost + ")");
					else StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).meleeBoost - ((Player)this.player).tempMeleeBoost) + ")");
				}
			}
			
			else if (((Player)this.player).tempMeleeBoost < ((Item_Food)this.selectedItem).meleeBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.player).tempMeleeBoost < 0) StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).meleeBoost + ")");
				else StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).meleeBoost - ((Player)this.player).tempMeleeBoost) + ")");
			}
			
			else if (((Player)this.player).tempMeleeBoost >= ((Item_Food)this.selectedItem).meleeBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Archery
		if (yy - 30 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).archeryBoost < 0) {
				if (((Item_Food)this.selectedItem).archeryBoost >= ((Player)this.player).tempArcheryBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.player).tempArcheryBoost >= 0) StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).archeryBoost + ")");
					else StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).archeryBoost - ((Player)this.player).tempArcheryBoost) + ")");
				}
			}
			
			else if (((Player)this.player).tempArcheryBoost < ((Item_Food)this.selectedItem).archeryBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.player).tempArcheryBoost < 0) StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).archeryBoost + ")");
				else StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).archeryBoost - ((Player)this.player).tempArcheryBoost) + ")");
			}
			
			else if (((Player)this.player).tempArcheryBoost >= ((Item_Food)this.selectedItem).archeryBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Magic
		if (yy - 35 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).magicBoost < 0) {
				if (((Item_Food)this.selectedItem).magicBoost >= ((Player)this.player).tempMagicBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.player).tempMagicBoost >= 0) StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).magicBoost + ")");
					else StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).magicBoost - ((Player)this.player).tempMagicBoost) + ")");
				}
			}
			
			else if (((Player)this.player).tempMagicBoost < ((Item_Food)this.selectedItem).magicBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.player).tempMagicBoost < 0) StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).magicBoost + ")");
				else StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).magicBoost - ((Player)this.player).tempMagicBoost) + ")");
			}
			
			else if (((Player)this.player).tempMagicBoost >= ((Item_Food)this.selectedItem).magicBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Defense
		if (yy - 40 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).defenseBoost < 0) {
				if (((Item_Food)this.selectedItem).defenseBoost >= ((Player)this.player).tempDefenseBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (((Player)this.player).tempDefenseBoost >= 0) StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).defenseBoost + ")");
					else StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).defenseBoost - ((Player)this.player).tempDefenseBoost) + ")");
				}
			}
			
			else if (((Player)this.player).tempDefenseBoost < ((Item_Food)this.selectedItem).defenseBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (((Player)this.player).tempDefenseBoost < 0) StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).defenseBoost + ")");
				else StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).defenseBoost - ((Player)this.player).tempDefenseBoost) + ")");
			}
			
			else if (((Player)this.player).tempDefenseBoost >= ((Item_Food)this.selectedItem).defenseBoost) {
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
			StdDraw.filledRectangle(78.18, 81.25 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
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
				StdDraw.filledRectangle(78.18, 36.25 + (76 * (1 - scrollPercentToTop)), 18, 2.5);
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
			if ((((Player)this.player)).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).helmet.accuracy)) + ")");
			}
			
			else if ((((Player)this.player)).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).helmet.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.player).helmet.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).helmet.speed) + ")");
			}
			
			else if (((Player)this.player).helmet.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).helmet.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.player).helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).helmet.maxHit) + ")");
			}
			
			else if (((Player)this.player).helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).helmet.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.player).helmet.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).helmet.armor) + ")");
			}
			
			else if (((Player)this.player).helmet.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).helmet.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.player).helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).helmet.meleeBoost) + ")");
			}
			
			else if (((Player)this.player).helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).helmet.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.player).helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).helmet.archeryBoost) + ")");
			}
			
			else if (((Player)this.player).helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).helmet.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.player).helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).helmet.magicBoost) + ")");
			}
			
			else if (((Player)this.player).helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).helmet.magicBoost) + ")");
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
			if (((Player)this.player).chest.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).chest.accuracy)) + ")");
			}
			
			else if (((Player)this.player).chest.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).chest.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.player).chest.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).chest.speed) + ")");
			}
			
			else if (((Player)this.player).chest.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).chest.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.player).chest.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).chest.maxHit) + ")");
			}
			
			else if (((Player)this.player).chest.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).chest.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.player).chest.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).chest.armor) + ")");
			}
			
			else if (((Player)this.player).chest.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).chest.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.player).chest.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).chest.meleeBoost) + ")");
			}
			
			else if (((Player)this.player).chest.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).chest.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.player).chest.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).chest.archeryBoost) + ")");
			}
			
			else if (((Player)this.player).chest.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).chest.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.player).chest.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).chest.magicBoost) + ")");
			}
			
			else if (((Player)this.player).chest.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).chest.magicBoost) + ")");
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
			if (((Player)this.player).legs.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).legs.accuracy)) + ")");
			}
			
			else if (((Player)this.player).legs.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).legs.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.player).legs.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).legs.speed) + ")");
			}
			
			else if (((Player)this.player).legs.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).legs.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.player).legs.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).legs.maxHit) + ")");
			}
			
			else if (((Player)this.player).legs.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).legs.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.player).legs.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).legs.armor) + ")");
			}
			
			else if (((Player)this.player).legs.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).legs.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.player).legs.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).legs.meleeBoost) + ")");
			}
			
			else if (((Player)this.player).legs.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).legs.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.player).legs.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).legs.archeryBoost) + ")");
			}
			
			else if (((Player)this.player).legs.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).legs.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.player).legs.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).legs.magicBoost) + ")");
			}
			
			else if (((Player)this.player).legs.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).legs.magicBoost) + ")");
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
			if (((Player)this.player).boots.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).boots.accuracy)) + ")");
			}
			
			else if (((Player)this.player).boots.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).boots.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.player).boots.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).boots.speed) + ")");
			}
			
			else if (((Player)this.player).boots.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).boots.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.player).boots.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).boots.maxHit) + ")");
			}
			
			else if (((Player)this.player).boots.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).boots.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.player).boots.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).boots.armor) + ")");
			}
			
			else if (((Player)this.player).boots.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).boots.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.player).boots.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).boots.meleeBoost) + ")");
			}
			
			else if (((Player)this.player).boots.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).boots.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.player).boots.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).boots.archeryBoost) + ")");
			}
			
			else if (((Player)this.player).boots.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).boots.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.player).boots.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).boots.magicBoost) + ")");
			}
			
			else if (((Player)this.player).boots.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).boots.magicBoost) + ")");
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
			if (((Player)this.player).gloves.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).gloves.accuracy)) + ")");
			}
			
			else if (((Player)this.player).gloves.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).gloves.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.player).gloves.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).gloves.speed) + ")");
			}
			
			else if (((Player)this.player).gloves.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).gloves.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.player).gloves.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).gloves.maxHit) + ")");
			}
			
			else if (((Player)this.player).gloves.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).gloves.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.player).gloves.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).gloves.armor) + ")");
			}
			
			else if (((Player)this.player).gloves.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).gloves.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.player).gloves.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).gloves.meleeBoost) + ")");
			}
			
			else if (((Player)this.player).gloves.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).gloves.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.player).gloves.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).gloves.archeryBoost) + ")");
			}
			
			else if (((Player)this.player).gloves.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).gloves.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.player).gloves.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).gloves.magicBoost) + ")");
			}
			
			else if (((Player)this.player).gloves.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).gloves.magicBoost) + ")");
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
			if (((Player)this.player).shield.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).shield.accuracy)) + ")");
			}
			
			else if (((Player)this.player).shield.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).shield.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.player).shield.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).shield.speed) + ")");
			}
			
			else if (((Player)this.player).shield.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).shield.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.player).shield.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).shield.maxHit) + ")");
			}
			
			else if (((Player)this.player).shield.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).shield.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.player).shield.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).shield.armor) + ")");
			}
			
			else if (((Player)this.player).shield.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).shield.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.player).shield.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).shield.meleeBoost) + ")");
			}
			
			else if (((Player)this.player).shield.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).shield.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.player).shield.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).shield.archeryBoost) + ")");
			}
			
			else if (((Player)this.player).shield.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).shield.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.player).shield.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).shield.magicBoost) + ")");
			}
			
			else if (((Player)this.player).shield.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).shield.magicBoost) + ")");
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
			if (((Player)this.player).weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).weapon.accuracy)) + ")");
			}
			
			else if (((Player)this.player).weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - ((Player)this.player).weapon.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (((Player)this.player).weapon.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).weapon.speed) + ")");
			}
			
			else if (((Player)this.player).weapon.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - ((Player)this.player).weapon.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (((Player)this.player).weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).weapon.maxHit) + ")");
			}
			
			else if (((Player)this.player).weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - ((Player)this.player).weapon.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (((Player)this.player).weapon.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).weapon.armor) + ")");
			}
			
			else if (((Player)this.player).weapon.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - ((Player)this.player).weapon.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (((Player)this.player).weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).weapon.meleeBoost) + ")");
			}
			
			else if (((Player)this.player).weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - ((Player)this.player).weapon.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (((Player)this.player).weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).weapon.archeryBoost) + ")");
			}
			
			else if (((Player)this.player).weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - ((Player)this.player).weapon.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (((Player)this.player).weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).weapon.magicBoost) + ")");
			}
			
			else if (((Player)this.player).weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - ((Player)this.player).weapon.magicBoost) + ")");
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
	
	private void checkForSelectedItem() {
		if (!(Mouse.lastButtonPressed == 1 && this.player.clickDelay == 0)) return;
		
		for (int i = 0; i < this.player.inventory.items.length; i++) {
			Item item = this.player.inventory.items[i];
			if (this.player.inventory.size <= 7) {
				if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= (78.5 - (i * 10)) + 5 && StdDraw.mouseY() >= (78.5 - (i * 10)) - 5) {
					this.selectedItem = item;
					Mouse.lastButtonPressed = -1;
					this.player.clickDelay = this.player.clickDelayAmount;
					break;
				}
			} else {
				if (!(item instanceof Item_Equipment_EmptySpace)) {
					double scrollPercentToTop = (this.inventoryScrollBar.y - this.inventoryScrollBar.min - this.inventoryScrollBar.height) / (this.inventoryScrollBar.max - (this.inventoryScrollBar.height * 2) - this.inventoryScrollBar.min); //0.##
					double percentToInvLimit = (i + 0.0) / this.player.inventory.size;
			
					if ((percentToInvLimit * 100) + 100 >= (scrollPercentToTop * 100) && (percentToInvLimit * 100) - 100 <= (scrollPercentToTop * 100)) {
						double yy = ((this.player.inventory.size * 10) - (scrollPercentToTop * ((this.player.inventory.size * 10) - 78.5))) - (i * 10);
				
						if (yy < 88.5 && yy > 0) {
							if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 56 && StdDraw.mouseY() <= yy + 5 && StdDraw.mouseY() >= yy - 5) {
								this.selectedItem = item;
								Mouse.lastButtonPressed = -1;
								this.player.clickDelay = this.player.clickDelayAmount;
								break;
							}
						}
					}
				}
			}
		}
		////////////////////
	}

	public synchronized void addPlayerItem(Item item) {
		this.playerItems.add(item);
	}
	
	public synchronized void addOtherPlayerItem(Item item) {
		this.otherPlayerItems.add(item);
	}
	
	public synchronized void removePlayerItem(Item item) {
		this.playerItems.remove(item);
	}
	
	public synchronized void removeOtherPlayerItem(Item item) {
		this.otherPlayerItems.remove(item);
	}
	
	public synchronized List<Item> getPlayerItems() {
		return this.playerItems;
	}
	
	public synchronized List<Item> getOtherPlayerItems() {
		return this.otherPlayerItems;
	}

}
