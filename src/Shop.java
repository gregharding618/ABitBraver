import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Shop implements Serializable {
	
	public Entity_NPC_ShopOwner shopOwner;
	public Player player;
	
	private int playersWealth = 0;
	private Item selectedItem;
	private ScrollBar scrollbar, selectedItemScrollBar, inventoryScrollBar;
	private DecimalFormat df = new DecimalFormat("#.##");
	private Button buyButton;
	private Button sellButton;
	private Button buySelectedItemButton;
	private Button sellSelectedItemButton;
	private Button confirmAmountButton;
	
	private int inventoryFullMessageTimer = 0;
	private int notEnoughMoneyMessageTimer = 0;
	
	private boolean showAmountInputWindow = false;
	private String amount = "";
	private int amountTickFlash = 0;
	
	public Shop(Entity_NPC_ShopOwner shopOwner, Player player) {
		this.shopOwner = shopOwner;
		this.player = player;
		
		this.buyButton = new Button(this.player, 7, 3.5, 3, 2, "Buy", 16, -0.1, StdDraw.GRAY, StdDraw.GREEN);
		this.buyButton.isSelected = true;
		this.sellButton = new Button(this.player, 43, 3.5, 3, 2, "Sell", 16, -0.1, StdDraw.GRAY, StdDraw.GREEN);
		this.buySelectedItemButton = new Button(this.player, 80, 89, 3, 2, "BUY", 16, -0.2, new Color(64, 78, 105), StdDraw.GREEN);
		this.sellSelectedItemButton = new Button(this.player, 80, 88, 3, 2, "SELL", 16, -0.2, new Color(64, 78, 105), StdDraw.GREEN);
		this.confirmAmountButton = new Button(this.player, 50, 41.5, 4.25, 2, "Enter", 16, -0.2, new Color(63, 88, 123), StdDraw.GREEN);
		
		this.scrollbar = new ScrollBar(47, 82, 90, 10, 1.25, 8, true, new Color(186, 121, 188));
		this.selectedItemScrollBar = new ScrollBar(97.5, 73, 83, 5, 1.25, 10, true, new Color(186, 121, 188));
		this.inventoryScrollBar = new ScrollBar(58, 75, 85, 5, 1.25, 10, true, new Color(186, 121, 188));
	}
	
	public void update() {
		this.player.inventory.checkForZeroAmounts();
		this.player.inventory.isFull = this.player.inventory.checkIfFull();
		
		for (Item item : this.player.inventory.items) {
			if (item instanceof Item_Money) {
				this.playersWealth = item.amount;
				break;
			}
		}
		
		updateTimers();
		
		if (this.buyButton.isSelected) this.scrollbar.update();
		else if (this.sellButton.isSelected) this.inventoryScrollBar.update();
		
		if (this.selectedItem != null) {
			this.selectedItemScrollBar.update();
			
			if (this.buyButton.isSelected) {
				if (this.inventoryFullMessageTimer == 0 && this.notEnoughMoneyMessageTimer == 0) {
					this.buySelectedItemButton.update();
					if (this.buySelectedItemButton.isSelected) {
						
						boolean confirmBuy = false;
						if (this.selectedItem.stackable) {
							this.showAmountInputWindow = true;
							confirmBuy = checkAmountInput();
						} else confirmBuy = true;

						if (confirmBuy) {
							this.buySelectedItemButton.isSelected = false;
							this.showAmountInputWindow = false;
							// If player has enough money
							boolean hasEnoughMoney = false;
							if (this.selectedItem.stackable) hasEnoughMoney = this.playersWealth >= this.selectedItem.value * Integer.parseInt(this.amount);
							else hasEnoughMoney = this.playersWealth >= this.selectedItem.value;
							
							if (hasEnoughMoney) {

								// If player's inventory is not full
								boolean isFull = true;
								if (this.selectedItem.stackable) {
									for (Item item : this.player.inventory.items) {
										if (item.name.equals(this.selectedItem.name)) {
											isFull = false;
											break;
										}
									}
								} else isFull = this.player.inventory.isFull;
								
								if (!isFull) {

									// Subtract item's value from player's money
									for (Item item : this.player.inventory.items) {
										if (item instanceof Item_Money) {
											if (this.selectedItem.stackable) {
												item.amount -= Integer.parseInt(this.amount) * this.selectedItem.value;
												this.playersWealth = item.amount;
											} else {
												item.amount -= this.selectedItem.value;
												this.playersWealth = item.amount;
											}
											break;
										}
									}
									////////////

									// Add item to player's inventory
									if (this.selectedItem.stackable) {
										boolean added = false;
										for (Item item : this.player.inventory.items) {
											if (item.name.equals(this.selectedItem.name)) {
												item.amount += Integer.parseInt(this.amount);
												added = true;
												break;
											}
										}
										
										if (!added) {
											Item newItem = Item.clone(this.selectedItem);
											this.player.inventory.addItem(newItem);
											for (Item item : this.player.inventory.items) {
												if (item.equals(newItem)) {
													item.amount -= item.amount - Integer.parseInt(this.amount);
													break;
												}
											}
										}
									}
									else this.player.inventory.addItem(Item.clone(this.selectedItem));
									////////////

									//Reduce amount of item in shop, remove item from shop if needed
									if (this.selectedItem.stackable) {
										this.selectedItem.amount -= Integer.parseInt(this.amount);
									}
									if (this.selectedItem.amount == 0 || !this.selectedItem.stackable) {
										for (int i = 0; i < this.shopOwner.inventory.items.length; i++) {
											if (this.shopOwner.inventory.items[i].equals(this.selectedItem)) {
												Item[] oldInventory = new Item[this.shopOwner.inventory.items.length];
												System.arraycopy(this.shopOwner.inventory.items, 0, oldInventory, 0, this.shopOwner.inventory.items.length);
												this.shopOwner.inventory = new Inventory(this.shopOwner, oldInventory.length - 1);
												boolean usePlusOne = false;
												
												for (int ii = 0; ii < this.shopOwner.inventory.size; ii++) {
													if (usePlusOne && ii + 1 == oldInventory.length) break;
													
													if (oldInventory[ii].equals(this.selectedItem)) {
														this.shopOwner.inventory.items[ii] = oldInventory[ii + 1];
														usePlusOne = true;
														continue;
													}
													else if (usePlusOne) {
														this.shopOwner.inventory.items[ii] = oldInventory[ii + 1];
													}
													else this.shopOwner.inventory.items[ii] = oldInventory[ii];
												}
												break;
											}
										}
									}
									////////////
									
									this.selectedItem = null;
									this.amount = "";
								}
								////////////

								// If player's inventory is full
								else {
									this.inventoryFullMessageTimer = 500;
								}
								/////////////
							}
							////////////////////

							// If player doesn't have enough money
							else {
								this.notEnoughMoneyMessageTimer = 500;
							}
							////////////////////
						}
					}
				}
			}
			
			else if (this.sellButton.isSelected) {
				this.sellSelectedItemButton.update();
				if (this.sellSelectedItemButton.isSelected) {				
					//If there's no room to add money to player's inventory
					boolean playerHasRoomForMoney = false;
					
					for (Item item : this.player.inventory.items) {
						if (item instanceof Item_Money) {
							playerHasRoomForMoney = true;
							break;
						}
					}
					
					if (!playerHasRoomForMoney) {
						for (Item item : this.player.inventory.items) {
							if (item instanceof Item_Equipment_EmptySpace) {
								playerHasRoomForMoney = true;
								break;
							}
						}
					}
					
					if (!playerHasRoomForMoney) {
						this.inventoryFullMessageTimer = 500;
						this.sellSelectedItemButton.isSelected = false;
					}
					////////////////////
					
					else {
						boolean confirmSell = false;
						if (this.selectedItem.stackable) {
							this.showAmountInputWindow = true;
							confirmSell = checkAmountInput();
						} else confirmSell = true;
						
						if (confirmSell) {
							this.sellSelectedItemButton.isSelected = false;
							this.showAmountInputWindow = false;
							boolean moneyAdded = false;
								
							for (Item item : this.player.inventory.items) {
								if (item instanceof Item_Money) {
									if (this.selectedItem.stackable) item.amount += this.selectedItem.value * Integer.parseInt(this.amount);
									else item.amount += this.selectedItem.value;
									if (this.selectedItem.stackable) {
										boolean addedItemToShop = false;
										for (Item itemToAdd : this.shopOwner.inventory.items) {
											if (itemToAdd.name.equals(this.selectedItem.name)) {
												itemToAdd.amount += Integer.parseInt(this.amount);
												addedItemToShop = true;
												break;
											}
										}
										if (!addedItemToShop) {
											Item[] oldInventory = this.shopOwner.inventory.items;
											this.shopOwner.inventory = new Inventory(this.shopOwner, oldInventory.length + 1);
											for (int ii = 0; ii < this.shopOwner.inventory.items.length; ii++) {
												if (ii == this.shopOwner.inventory.items.length - 1) {
													if (this.selectedItem.stackable) {
														this.shopOwner.inventory.items[ii] = Item.clone(this.selectedItem);
														this.shopOwner.inventory.items[ii].amount = Integer.parseInt(this.amount);
													}
													else this.shopOwner.inventory.items[ii] = this.selectedItem;
												}
												else this.shopOwner.inventory.items[ii] = oldInventory[ii];
											}
										}
									} else {
										Item[] oldInventory = this.shopOwner.inventory.items;
										this.shopOwner.inventory = new Inventory(this.shopOwner, oldInventory.length + 1);
										for (int ii = 0; ii < this.shopOwner.inventory.items.length; ii++) {
											if (ii == this.shopOwner.inventory.items.length - 1) {
												if (this.selectedItem.stackable) {
													this.shopOwner.inventory.items[ii] = Item.clone(this.selectedItem);
													this.shopOwner.inventory.items[ii].amount = Integer.parseInt(this.amount);
												}
												else this.shopOwner.inventory.items[ii] = this.selectedItem;
											}
											else this.shopOwner.inventory.items[ii] = oldInventory[ii];
										}
									}
									moneyAdded = true;
											
									if (this.selectedItem.stackable) {
										this.selectedItem.amount -= Integer.parseInt(this.amount);
									}
									else {
										for (int i = 0; i < this.player.inventory.items.length; i++) {
											if (this.player.inventory.items[i].equals(this.selectedItem)) {
												this.player.inventory.items[i] = new Item_Equipment_EmptySpace();
												break;
											}
										}
									}
											
									this.selectedItem = null;
									this.amount = "";
									break;
								}
							}
								
							if (!moneyAdded) {
								for (Item item : this.player.inventory.items) {
									if (item instanceof Item_Equipment_EmptySpace) {
										item = new Item_Money(this.selectedItem.value);
										if (this.selectedItem.stackable) {
											boolean addedItemToShop = false;
											for (int ii = 0; ii < this.shopOwner.inventory.items.length; ii++) {
												if (this.shopOwner.inventory.items[ii].name.equals(this.selectedItem.name)) {
													this.shopOwner.inventory.items[ii].amount += Integer.parseInt(this.amount);
													addedItemToShop = true;
													break;
												}
											}
											if (!addedItemToShop) {
												Item[] oldInventory = this.shopOwner.inventory.items;
												this.shopOwner.inventory = new Inventory(this.shopOwner, oldInventory.length + 1);
												for (int ii = 0; ii < this.shopOwner.inventory.items.length; ii++) {
													if (ii == this.shopOwner.inventory.items.length - 1) {
														if (this.selectedItem.stackable) {
															this.shopOwner.inventory.items[ii] = Item.clone(this.selectedItem);
															this.shopOwner.inventory.items[ii].amount = Integer.parseInt(this.amount);
														}
														else this.shopOwner.inventory.items[ii] = this.selectedItem;
													}
													else this.shopOwner.inventory.items[ii] = oldInventory[ii];
												}
											}
										}
										moneyAdded = true;
												
										if (this.selectedItem.stackable) {
											this.selectedItem.amount -= Integer.parseInt(this.amount);
										}
										else {
											for (int i = 0; i < this.player.inventory.items.length; i++) {
												if (this.player.inventory.items[i].equals(this.selectedItem)) {
													this.player.inventory.items[i] = new Item_Equipment_EmptySpace();
													break;
												}
											}
										}
												
										this.selectedItem = null;
										this.amount = "";
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		
		this.buyButton.update();
		if (this.buyButton.isSelected) {
			this.sellButton.isSelected = false;
			this.buyButton.x = 7;
			this.buyButton.y = 3.5;
			this.buyButton.width = 3;
			this.buyButton.height = 2;
			this.buyButton.fontSize = 16;
			this.sellButton.x = 43;
			this.sellButton.y = 3.5;
			this.sellButton.width = 3;
			this.sellButton.height = 2;
			this.sellButton.fontSize = 16;
			
			if (this.selectedItem != null) {
				boolean cancelSelectedItem = true;
				
				for (Item item : this.shopOwner.inventory.items) {
					if (item.equals(this.selectedItem)) {
						cancelSelectedItem = false;
						break;
					}
				}
				
				if (cancelSelectedItem) this.selectedItem = null;
			}
		}
		
		this.sellButton.update();
		if (this.sellButton.isSelected) {
			this.buyButton.isSelected = false;
			this.buyButton.x = 7;
			this.buyButton.y = 2.5;
			this.buyButton.width = 3;
			this.buyButton.height = 1.5;
			this.buyButton.fontSize = 14;
			this.sellButton.x = 43;
			this.sellButton.y = 2.5;
			this.sellButton.width = 3;
			this.sellButton.height = 1.5;
			this.sellButton.fontSize = 14;
			
			if (this.selectedItem != null) {
				boolean cancelSelectedItem = true;
				
				for (Item item : this.player.inventory.items) {
					if (item.equals(this.selectedItem)) {
						cancelSelectedItem = false;
						break;
					}
				}
				
				if (cancelSelectedItem) this.selectedItem = null;
			}
		}
		
		checkInput();
	}

	private boolean checkAmountInput() {
		this.confirmAmountButton.update();
		if (this.confirmAmountButton.isSelected) {
			this.confirmAmountButton.isSelected = false;
			
			if (!this.amount.equals("")) return true;
		}
		
		if (this.player.buttonDelay == 0) {
			//Enter - confirm amount
			if (StdDraw.isKeyPressed(KeyEvent.VK_ENTER) && !this.amount.equals("")) {
				this.player.buttonDelay = this.player.buttonDelayAmount;
				return true;
			}
			////////////////
			
			//Backspace
			else if (StdDraw.isKeyPressed(KeyEvent.VK_BACK_SPACE) && !this.amount.equals("")) {
				if (this.amount.length() == 1) this.amount = "";
				else this.amount = this.amount.substring(0, this.amount.length() - 1);
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			////////////////
			
			//Any numbers
			else if (StdDraw.isKeyPressed(KeyEvent.VK_0) && !this.amount.equals("")) {
				this.amount += "0";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_1)) {
				this.amount += "1";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_2)) {
				this.amount += "2";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_3)) {
				this.amount += "3";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_4)) {
				this.amount += "4";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_5)) {
				this.amount += "5";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_6)) {
				this.amount += "6";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_7)) {
				this.amount += "7";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_8)) {
				this.amount += "8";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			
			else if (StdDraw.isKeyPressed(KeyEvent.VK_9)) {
				this.amount += "9";
				this.player.buttonDelay = this.player.buttonDelayAmount;
			}
			////////////////
		}
		
		if (!this.amount.equals("") && Integer.parseInt(this.amount) > this.selectedItem.amount) this.amount = this.selectedItem.amount + "";
		
		return false;
	}

	public void render() {
		if (this.buyButton.isSelected) renderShopInterface();
		else if (this.sellButton.isSelected) renderPlayerInventoryInterface();
		
		if (this.showAmountInputWindow) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(50, 50, 22, 15);
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.setPenRadius(0.009);
			StdDraw.rectangle(50, 50, 22, 15);
			StdDraw.setPenRadius();
			
			if (this.amountTickFlash > 0) this.amountTickFlash--;
			else this.amountTickFlash = 100;
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.text(50, 60, "How many?");
			
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 25));
			if (this.buyButton.isSelected) {
				if (!this.amount.equals("") && this.selectedItem.value * Integer.parseInt(this.amount) > this.playersWealth) StdDraw.setPenColor(StdDraw.RED);
				else StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.amountTickFlash >= 50) StdDraw.text(50, 46, this.amount + "|");
				else StdDraw.text(50, 46, this.amount + " ");
			}
			
			else if (this.sellButton.isSelected) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.amountTickFlash >= 50) StdDraw.text(50, 46, this.amount + "|");
				else StdDraw.text(50, 46, this.amount + " ");
			}
			
			this.confirmAmountButton.render();
		}
	}

	private void renderShopInterface() {
		//Background
		StdDraw.setPenColor(new Color(1, 1, 1, 178));
		StdDraw.filledRectangle(25, 50, 25, 50);
		////////////////////
		
		//Scrollbar
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(47, 50, 3, 50);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(47, 50, this.scrollbar.width, 40);
		this.scrollbar.render();
		////////////////////
		
		//Items
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 18));
		
		double xx = 8;
		double yy = 85;
		int index = 0;
		
		if (this.shopOwner.inventory.size > 12) {
			double scrollPercentToTop = (this.scrollbar.y - this.scrollbar.min - this.scrollbar.height) / (this.scrollbar.max - (this.scrollbar.height * 2) - this.scrollbar.min); //0.##
			int indents = (int) (Math.ceil(this.shopOwner.inventory.size / 3.0));
			indents += (int) ((indents) * 14);
			
			for (Item item : this.shopOwner.inventory.items) {
				if (index != 0 && index % 3 == 0) {
					xx = 8;
					yy -= 14;
				}

				double yAdjust = yy + ((indents) * (1 - scrollPercentToTop));
				item.render(xx, yAdjust);
				if (item.value > this.playersWealth) StdDraw.setPenColor(StdDraw.RED);
				else StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(xx, yAdjust - 6, "$" + item.value);
				if (this.selectedItem != null && this.selectedItem.equals(item)) {
					StdDraw.setPenColor(StdDraw.CYAN);
					StdDraw.setPenRadius(0.0083);
					StdDraw.rectangle(xx, yAdjust - 2, 6, 7);
					StdDraw.setPenRadius();
				}
			
				index++;
				xx += 14;
			}
		}
		
		else {
			for (Item item : this.shopOwner.inventory.items) {
				if (index == 3) {
					index = 0;
					xx = 8;
					yy -= 14;
				}
			
				item.render(xx, yy);
				if (item.value > this.playersWealth) StdDraw.setPenColor(StdDraw.RED);
				else StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(xx, yy - 6, "$" + item.value);
			
				index++;
				xx += 14;
			}
		}
		////////////////////
		
		//Selected item info
		if (this.selectedItem != null) {
			
			//Backgrond
			StdDraw.setPenColor(new Color(1, 1, 1, 178));
			StdDraw.filledRectangle(80, 50, 20.5, 50);
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.rectangle(80, 50, 20.5, 50);
			////////////////////
			
			//If selected item is equipment
			if (this.selectedItem instanceof Item_Equipment) renderSelectedEquipmentInfo();
			////////////////////
			
			//If selected item is food
			else if (this.selectedItem instanceof Item_Food) renderSelectedFoodInfo();
			////////////////////
			
			//Top banner
			StdDraw.setPenColor(StdDraw.DARK_GRAY);
			StdDraw.filledRectangle(80, 93, 20.4, 8);
			////////////////////
			
			//Price
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(80, 96, 12, 2.75);
			StdDraw.setPenColor(new Color(67, 69, 102));
			StdDraw.setPenRadius(0.0048);
			StdDraw.rectangle(80, 96, 12, 2.75);
			StdDraw.setPenRadius();
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 35));
			if (this.selectedItem.value > this.playersWealth) StdDraw.setPenColor(StdDraw.RED);
			else StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(80, 95.5, "$" + this.selectedItem.value);
			////////////////////
			
			//Buttons
			this.buySelectedItemButton.render();
			////////////////////
		}
		//////////////////////////////////////////
		
		//Top banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(25, 97.5, 25, 5);
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 28));
		StdDraw.text(25, 96, this.shopOwner.name + "'s shop");
		////////////////////
		
		//Bottom banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(25, 2.5, 25, 5);
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 25));
		StdDraw.text(25, 3.5, "You have: $" + this.playersWealth);
		this.buyButton.render();
		this.sellButton.render();
		////////////////////
		
		//Border
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.rectangle(25, 50, 25, 50);
		////////////////////
		
		//Not enough money message
		if (this.notEnoughMoneyMessageTimer > 0) {
			StdDraw.setPenColor(new Color(33, 27, 26));
			StdDraw.filledRectangle(50, 50, 24, 5);
			if ((this.notEnoughMoneyMessageTimer > 475 && this.notEnoughMoneyMessageTimer < 490) || (this.notEnoughMoneyMessageTimer > 425 && this.notEnoughMoneyMessageTimer < 450)) StdDraw.setPenColor(StdDraw.CYAN);
			else StdDraw.setPenColor(StdDraw.RED);
			StdDraw.setPenRadius(0.0085);
			StdDraw.rectangle(50, 50, 24, 5);
			StdDraw.setPenRadius();
			
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 27));
			StdDraw.text(50, 50, "You don't have enough money!");
		}
		////////////////////
		
		//Inventory full message
		else if (this.inventoryFullMessageTimer > 0) {
			StdDraw.setPenColor(new Color(33, 27, 26));
			StdDraw.filledRectangle(50, 50, 24, 5);
			if ((this.inventoryFullMessageTimer > 475 && this.inventoryFullMessageTimer < 490) || (this.inventoryFullMessageTimer > 425 && this.inventoryFullMessageTimer < 450)) StdDraw.setPenColor(StdDraw.CYAN);
			else StdDraw.setPenColor(StdDraw.RED);
			StdDraw.setPenRadius(0.0085);
			StdDraw.rectangle(50, 50, 24, 5);
			StdDraw.setPenRadius();
			
			StdDraw.setPenColor(StdDraw.ORANGE);
			StdDraw.setFont(new Font("Arial", Font.BOLD, 27));
			StdDraw.text(50, 50, "Your inventory is full!");
		}
		////////////////////
	}
	
	private void renderPlayerInventoryInterface() {
		//Background
		StdDraw.setPenColor(new Color(1, 1, 1, 178));
		StdDraw.filledRectangle(30, 50, 30, 48);
		////////////////////
		
		//Organize list
		this.player.inventory.moveEmptySpacesToBottom();
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
		//////////////////////////////////////////
		
		//Selected item info
		if (this.selectedItem != null) {
			
			//Backgrond
			StdDraw.setPenColor(new Color(1, 1, 1, 178));
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
			
			//Price
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.filledRectangle(80, 94, 12, 2.75);
			StdDraw.setPenColor(new Color(67, 69, 102));
			StdDraw.setPenRadius(0.0048);
			StdDraw.rectangle(80, 94, 12, 2.75);
			StdDraw.setPenRadius();
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 35));
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.text(80, 93.5, "$" + this.selectedItem.value);
			////////////////////
			
			//Sell button
			this.sellSelectedItemButton.render();
			////////////////////
		}
		//////////////////////////////////////////
		
		//Top banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(30.05, 91.25, 29.96, 6.5);
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
		StdDraw.text(30, 91, "Your Inventory");
		////////////////////
		
		//Bottom banner
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.filledRectangle(30.05, 2.25, 29.96, 2.275);
		StdDraw.filledSquare(59.42, 4.5, 0.58);
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 22));
		StdDraw.text(25, 2.25, "You have: $" + this.playersWealth);
		this.buyButton.render();
		this.sellButton.render();
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
			StdDraw.text(83, yy - 10 + (76 * (1 - scrollPercentToTop)), this.player.currentHealth + "/" + this.player.maxHealth);
		}
		
		if (yy - 15 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 15 + (76 * (1 - scrollPercentToTop)), "Accuracy boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 15 + (76 * (1 - scrollPercentToTop)), this.player.tempAccuracyBoost + "");
		}
		
		if (yy - 20 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 20 + (76 * (1 - scrollPercentToTop)), "Strength boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 20 + (76 * (1 - scrollPercentToTop)), this.player.tempMaxHitBoost + "");
		}
		
		if (yy - 25 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 25 + (76 * (1 - scrollPercentToTop)), "Melee boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 25 + (76 * (1 - scrollPercentToTop)), this.player.tempMeleeBoost + "");
		}
		
		if (yy - 30 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 30 + (76 * (1 - scrollPercentToTop)), "Archery boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 30 + (76 * (1 - scrollPercentToTop)), this.player.tempArcheryBoost + "");
		}
		
		if (yy - 35 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 35 + (76 * (1 - scrollPercentToTop)), "Magic boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 35 + (76 * (1 - scrollPercentToTop)), this.player.tempMagicBoost + "");
		}
		
		if (yy - 40 + (76 * (1 - scrollPercentToTop)) < 88) {
			StdDraw.text(70, yy - 40 + (76 * (1 - scrollPercentToTop)), "Defense boost:");
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(83, yy - 40 + (76 * (1 - scrollPercentToTop)), this.player.tempDefenseBoost + "");
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
			
			else if (this.player.maxHealth - this.player.currentHealth > ((Item_Food)this.selectedItem).healAmount) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).healAmount + ")");
			}
			
			else if (this.player.maxHealth - this.player.currentHealth < ((Item_Food)this.selectedItem).healAmount && this.player.maxHealth - this.player.currentHealth > 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).healAmount - (this.player.maxHealth - this.player.currentHealth)) + ")");
			}
			
			else if (this.player.maxHealth == this.player.currentHealth || ((Item_Food)this.selectedItem).healAmount == 0) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 10 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Accuracy
		if (yy - 15 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).accuracyBoost < 0) {
				if (((Item_Food)this.selectedItem).accuracyBoost >= this.player.tempAccuracyBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (this.player.tempAccuracyBoost >= 0) StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).accuracyBoost + ")");
					else StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).accuracyBoost - this.player.tempAccuracyBoost) + ")");
				}
			}
			
			else if (this.player.tempAccuracyBoost < ((Item_Food)this.selectedItem).accuracyBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.player.tempAccuracyBoost < 0) StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).accuracyBoost + ")");
				else StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).accuracyBoost - this.player.tempAccuracyBoost) + ")");
			}
			
			else if (this.player.tempAccuracyBoost >= ((Item_Food)this.selectedItem).accuracyBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 15 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Max hit
		if (yy - 20 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).maxHitBoost < 0) {
				if (((Item_Food)this.selectedItem).maxHitBoost >= this.player.tempMaxHitBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (this.player.tempMaxHitBoost >= 0) StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).maxHitBoost + ")");
					else StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).maxHitBoost - this.player.tempMaxHitBoost) + ")");
				}
			}
			
			else if (this.player.tempMaxHitBoost < ((Item_Food)this.selectedItem).maxHitBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.player.tempMaxHitBoost < 0) StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).maxHitBoost + ")");
				else StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).maxHitBoost - this.player.tempMaxHitBoost) + ")");
			}
			
			else if (this.player.tempMaxHitBoost >= ((Item_Food)this.selectedItem).maxHitBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 20 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Melee
		if (yy - 25 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).meleeBoost < 0) {
				if (((Item_Food)this.selectedItem).meleeBoost >= this.player.tempMeleeBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (this.player.tempMeleeBoost >= 0) StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).meleeBoost + ")");
					else StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).meleeBoost - this.player.tempMeleeBoost) + ")");
				}
			}
			
			else if (this.player.tempMeleeBoost < ((Item_Food)this.selectedItem).meleeBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.player.tempMeleeBoost < 0) StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).meleeBoost + ")");
				else StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).meleeBoost - this.player.tempMeleeBoost) + ")");
			}
			
			else if (this.player.tempMeleeBoost >= ((Item_Food)this.selectedItem).meleeBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 25 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Archery
		if (yy - 30 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).archeryBoost < 0) {
				if (((Item_Food)this.selectedItem).archeryBoost >= this.player.tempArcheryBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (this.player.tempArcheryBoost >= 0) StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).archeryBoost + ")");
					else StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).archeryBoost - this.player.tempArcheryBoost) + ")");
				}
			}
			
			else if (this.player.tempArcheryBoost < ((Item_Food)this.selectedItem).archeryBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.player.tempArcheryBoost < 0) StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).archeryBoost + ")");
				else StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).archeryBoost - this.player.tempArcheryBoost) + ")");
			}
			
			else if (this.player.tempArcheryBoost >= ((Item_Food)this.selectedItem).archeryBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 30 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Magic
		if (yy - 35 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).magicBoost < 0) {
				if (((Item_Food)this.selectedItem).magicBoost >= this.player.tempMagicBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (this.player.tempMagicBoost >= 0) StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).magicBoost + ")");
					else StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).magicBoost - this.player.tempMagicBoost) + ")");
				}
			}
			
			else if (this.player.tempMagicBoost < ((Item_Food)this.selectedItem).magicBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.player.tempMagicBoost < 0) StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).magicBoost + ")");
				else StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).magicBoost - this.player.tempMagicBoost) + ")");
			}
			
			else if (this.player.tempMagicBoost >= ((Item_Food)this.selectedItem).magicBoost) {
				StdDraw.setPenColor(StdDraw.WHITE);
				StdDraw.text(90.75, yy - 35 + (76 * (1 - scrollPercentToTop)), "(+0)");
			}
		}
		///////////////////
		
		//Defense
		if (yy - 40 + (76 * (1 - scrollPercentToTop)) < 88) {
			if (((Item_Food)this.selectedItem).defenseBoost < 0) {
				if (((Item_Food)this.selectedItem).defenseBoost >= this.player.tempDefenseBoost) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+0)");
				}
				
				else {
					StdDraw.setPenColor(StdDraw.RED);
					if (this.player.tempDefenseBoost >= 0) StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(" + ((Item_Food)this.selectedItem).defenseBoost + ")");
					else StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Food)this.selectedItem).defenseBoost - this.player.tempDefenseBoost) + ")");
				}
			}
			
			else if (this.player.tempDefenseBoost < ((Item_Food)this.selectedItem).defenseBoost) {
				StdDraw.setPenColor(StdDraw.GREEN);
				
				if (this.player.tempDefenseBoost < 0) StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+" + ((Item_Food)this.selectedItem).defenseBoost + ")");
				else StdDraw.text(90.75, yy - 40 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Food)this.selectedItem).defenseBoost - this.player.tempDefenseBoost) + ")");
			}
			
			else if (this.player.tempDefenseBoost >= ((Item_Food)this.selectedItem).defenseBoost) {
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
		StdDraw.setPenColor(StdDraw.BLACK);
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
		StdDraw.setPenColor(StdDraw.BLACK);
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
			if ((this.player).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.helmet.accuracy)) + ")");
			}
			
			else if ((this.player).helmet.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.helmet.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (this.player.helmet.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - this.player.helmet.speed) + ")");
			}
			
			else if (this.player.helmet.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - this.player.helmet.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (this.player.helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - this.player.helmet.maxHit) + ")");
			}
			
			else if (this.player.helmet.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - this.player.helmet.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (this.player.helmet.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - this.player.helmet.armor) + ")");
			}
			
			else if (this.player.helmet.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - this.player.helmet.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (this.player.helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.helmet.meleeBoost) + ")");
			}
			
			else if (this.player.helmet.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.helmet.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (this.player.helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.helmet.archeryBoost) + ")");
			}
			
			else if (this.player.helmet.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.helmet.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (this.player.helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.helmet.magicBoost) + ")");
			}
			
			else if (this.player.helmet.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.helmet.magicBoost) + ")");
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
			if (this.player.chest.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.chest.accuracy)) + ")");
			}
			
			else if (this.player.chest.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.chest.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (this.player.chest.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - this.player.chest.speed) + ")");
			}
			
			else if (this.player.chest.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - this.player.chest.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (this.player.chest.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - this.player.chest.maxHit) + ")");
			}
			
			else if (this.player.chest.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - this.player.chest.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (this.player.chest.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - this.player.chest.armor) + ")");
			}
			
			else if (this.player.chest.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - this.player.chest.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (this.player.chest.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.chest.meleeBoost) + ")");
			}
			
			else if (this.player.chest.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.chest.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (this.player.chest.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.chest.archeryBoost) + ")");
			}
			
			else if (this.player.chest.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.chest.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (this.player.chest.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.chest.magicBoost) + ")");
			}
			
			else if (this.player.chest.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.chest.magicBoost) + ")");
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
			if (this.player.legs.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.legs.accuracy)) + ")");
			}
			
			else if (this.player.legs.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.legs.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (this.player.legs.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - this.player.legs.speed) + ")");
			}
			
			else if (this.player.legs.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - this.player.legs.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (this.player.legs.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - this.player.legs.maxHit) + ")");
			}
			
			else if (this.player.legs.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - this.player.legs.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (this.player.legs.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - this.player.legs.armor) + ")");
			}
			
			else if (this.player.legs.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - this.player.legs.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (this.player.legs.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.legs.meleeBoost) + ")");
			}
			
			else if (this.player.legs.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.legs.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (this.player.legs.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.legs.archeryBoost) + ")");
			}
			
			else if (this.player.legs.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.legs.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (this.player.legs.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.legs.magicBoost) + ")");
			}
			
			else if (this.player.legs.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.legs.magicBoost) + ")");
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
			if (this.player.boots.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.boots.accuracy)) + ")");
			}
			
			else if (this.player.boots.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.boots.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (this.player.boots.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - this.player.boots.speed) + ")");
			}
			
			else if (this.player.boots.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - this.player.boots.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (this.player.boots.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - this.player.boots.maxHit) + ")");
			}
			
			else if (this.player.boots.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - this.player.boots.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (this.player.boots.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - this.player.boots.armor) + ")");
			}
			
			else if (this.player.boots.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - this.player.boots.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (this.player.boots.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.boots.meleeBoost) + ")");
			}
			
			else if (this.player.boots.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.boots.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (this.player.boots.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.boots.archeryBoost) + ")");
			}
			
			else if (this.player.boots.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.boots.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (this.player.boots.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.boots.magicBoost) + ")");
			}
			
			else if (this.player.boots.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.boots.magicBoost) + ")");
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
			if (this.player.gloves.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.gloves.accuracy)) + ")");
			}
			
			else if (this.player.gloves.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.gloves.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (this.player.gloves.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - this.player.gloves.speed) + ")");
			}
			
			else if (this.player.gloves.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - this.player.gloves.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (this.player.gloves.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - this.player.gloves.maxHit) + ")");
			}
			
			else if (this.player.gloves.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - this.player.gloves.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (this.player.gloves.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - this.player.gloves.armor) + ")");
			}
			
			else if (this.player.gloves.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - this.player.gloves.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (this.player.gloves.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.gloves.meleeBoost) + ")");
			}
			
			else if (this.player.gloves.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.gloves.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (this.player.gloves.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.gloves.archeryBoost) + ")");
			}
			
			else if (this.player.gloves.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.gloves.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (this.player.gloves.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.gloves.magicBoost) + ")");
			}
			
			else if (this.player.gloves.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.gloves.magicBoost) + ")");
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
			if (this.player.shield.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.shield.accuracy)) + ")");
			}
			
			else if (this.player.shield.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.shield.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (this.player.shield.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - this.player.shield.speed) + ")");
			}
			
			else if (this.player.shield.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - this.player.shield.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (this.player.shield.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - this.player.shield.maxHit) + ")");
			}
			
			else if (this.player.shield.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - this.player.shield.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (this.player.shield.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - this.player.shield.armor) + ")");
			}
			
			else if (this.player.shield.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - this.player.shield.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (this.player.shield.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.shield.meleeBoost) + ")");
			}
			
			else if (this.player.shield.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.shield.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (this.player.shield.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.shield.archeryBoost) + ")");
			}
			
			else if (this.player.shield.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.shield.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (this.player.shield.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.shield.magicBoost) + ")");
			}
			
			else if (this.player.shield.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.shield.magicBoost) + ")");
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
			if (this.player.weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(+" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.weapon.accuracy)) + ")");
			}
			
			else if (this.player.weapon.accuracy - ((Item_Equipment)this.selectedItem).accuracy > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(" + df.format((((Item_Equipment)this.selectedItem).accuracy - this.player.weapon.accuracy)) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (76 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 76 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Speed
			if (this.player.weapon.speed - ((Item_Equipment)this.selectedItem).speed < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).speed - this.player.weapon.speed) + ")");
			}
			
			else if (this.player.weapon.speed - ((Item_Equipment)this.selectedItem).speed > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).speed - this.player.weapon.speed) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (71 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 71 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Max hit (strength)
			if (this.player.weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).maxHit - this.player.weapon.maxHit) + ")");
			}
			
			else if (this.player.weapon.maxHit - ((Item_Equipment)this.selectedItem).maxHit > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).maxHit - this.player.weapon.maxHit) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (66 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 66 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Armor
			if (this.player.weapon.armor - ((Item_Equipment)this.selectedItem).armor < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).armor - this.player.weapon.armor) + ")");
			}
			
			else if (this.player.weapon.armor - ((Item_Equipment)this.selectedItem).armor > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).armor - this.player.weapon.armor) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (61 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 61 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Melee boost
			if (this.player.weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.weapon.meleeBoost) + ")");
			}
			
			else if (this.player.weapon.meleeBoost - ((Item_Equipment)this.selectedItem).meleeBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).meleeBoost - this.player.weapon.meleeBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (56 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 56 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Archery boost
			if (this.player.weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.weapon.archeryBoost) + ")");
			}
			
			else if (this.player.weapon.archeryBoost - ((Item_Equipment)this.selectedItem).archeryBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).archeryBoost - this.player.weapon.archeryBoost) + ")");
			}
			
			else {
				StdDraw.setPenColor(StdDraw.WHITE);
				if (51 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 51 + (76 * (1 - scrollPercentToTop)), "(=)");
			}
			///////////////////
			
			//Magic boost
			if (this.player.weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost < 0) {
				StdDraw.setPenColor(StdDraw.GREEN);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(+" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.weapon.magicBoost) + ")");
			}
			
			else if (this.player.weapon.magicBoost - ((Item_Equipment)this.selectedItem).magicBoost > 0) {
				StdDraw.setPenColor(StdDraw.RED);
				if (46 + (76 * (1 - scrollPercentToTop)) < 88) StdDraw.text(90, 46 + (76 * (1 - scrollPercentToTop)), "(" + (((Item_Equipment)this.selectedItem).magicBoost - this.player.weapon.magicBoost) + ")");
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

	private void checkInput() {
		//ESC - close shop
		if (this.player.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE)) {
			this.player.currentShop = null;
			this.player.canAttack = true;
			this.player.buttonDelay = this.player.buttonDelayAmount * 2;
		}
		////////////////////
		
		//Left click (1) - selecting item
		if (this.buyButton.isSelected && !this.scrollbar.isMoving && !this.selectedItemScrollBar.isMoving) {
			if (Mouse.lastButtonPressed == 1 && this.player.clickDelay == 0) {
				double xx = 8;
				double yy = 85;
				int index = 0;
		
				if (this.shopOwner.inventory.size > 12) {
					double scrollPercentToTop = (this.scrollbar.y - this.scrollbar.min - this.scrollbar.height) / (this.scrollbar.max - (this.scrollbar.height * 2) - this.scrollbar.min); //0.##
					int indents = (int) (Math.ceil(this.shopOwner.inventory.size / 3.0));
					indents += (int) ((indents) * 14);
			
					for (Item item : this.shopOwner.inventory.items) {
						if (index != 0 && index % 3 == 0) {
							xx = 8;
							yy -= 14;
						}
					
						double yAdjust = yy + ((indents) * (1 - scrollPercentToTop));

						if (StdDraw.mouseX() >= xx - 6 && StdDraw.mouseX() <= xx + 6 && StdDraw.mouseY() >= yAdjust - 9 && StdDraw.mouseY() <= yAdjust + 5) {
							if (this.selectedItem == null || !this.selectedItem.equals(item)) {
								this.selectedItem = item;
								Mouse.lastButtonPressed = -1;
								this.player.clickDelay = this.player.clickDelayAmount;
								this.selectedItemScrollBar.y = this.selectedItemScrollBar.max - this.selectedItemScrollBar.height;
								break;
							} else {
								this.selectedItem = null;
								Mouse.lastButtonPressed = -1;
								this.player.clickDelay = this.player.clickDelayAmount;
								break;
							}
						}
			
						index++;
						xx += 14;
					}
				}
		
				else {
					for (Item item : this.shopOwner.inventory.items) {
						if (index == 3) {
							index = 0;
							xx = 8;
							yy -= 14;
						}
			
						if (StdDraw.mouseX() >= xx - 6 && StdDraw.mouseX() <= xx + 6 && StdDraw.mouseY() >= yy - 9 && StdDraw.mouseY() <= yy + 5) {
							if (this.selectedItem == null || !this.selectedItem.equals(item)) {
								this.selectedItem = item;
								Mouse.lastButtonPressed = -1;
								this.player.clickDelay = this.player.clickDelayAmount;
								break;
							} else {
								this.selectedItem = null;
								Mouse.lastButtonPressed = -1;
								this.player.clickDelay = this.player.clickDelayAmount;
								break;
							}
						}
			
						index++;
						xx += 14;
					}
				}
			}
		}
		
		else if (this.sellButton.isSelected && !this.inventoryScrollBar.isMoving && !this.selectedItemScrollBar.isMoving) {
			if (Mouse.lastButtonPressed == 1 && this.player.clickDelay == 0) {
				for (int i = 0; i < this.player.inventory.items.length; i++) {
					Item item = this.player.inventory.items[i];
					if (item instanceof Item_Money) continue;
					
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
									}
								}
							}
						}
					}
				}
			}
		}
		////////////////////
	}

	private void updateTimers() {
		if (this.notEnoughMoneyMessageTimer > 0) this.notEnoughMoneyMessageTimer--;
		if (this.inventoryFullMessageTimer > 0) this.inventoryFullMessageTimer--;
	}
	
}
