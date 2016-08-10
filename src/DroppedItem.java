import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class DroppedItem implements Serializable {
	
	public Level level;
	public double x, y, mapX, mapY, actionSize = 7; //Action size is the maximum distance the player can be to pick it up
	public Item item;
	public boolean shouldRender = false;
	
	private Color miniMapColor = StdDraw.MAGENTA;
	
	public long timeCreated = System.currentTimeMillis();

	public DroppedItem(Level level, Item item, double mapX, double mapY) {
		this.level = level;
		this.item = item;
		this.mapX = mapX;
		this.mapY = mapY;
	}
	
	public void update() {
		//Delete item after 60 seconds
		long currentTime = System.currentTimeMillis();
		if (Game.socketServer != null && currentTime - this.timeCreated >= 60000) {
			
			ArrayList<String> attributes = new ArrayList<String>();
			
			if (this.item instanceof Item_Money) {
				attributes.add(Integer.toString(this.item.amount));
				
				Packet026RemoveDroppedItem packet = new Packet026RemoveDroppedItem(this.level.name, "money", this.mapX, this.mapY, attributes);
				packet.writeData(Game.socketServer);
			}
			
			else if (this.item instanceof Item_Food) {
				attributes.add(this.item.name);
				attributes.add(this.item.iconImageFile);
				attributes.add(Boolean.toString(this.item.stackable));
				attributes.add(Integer.toString(this.item.amount));
				attributes.add(Integer.toString(this.item.value));
				attributes.add(Integer.toString(((Item_Food) this.item).healAmount));
				attributes.add(Integer.toString(((Item_Food) this.item).meleeBoost));
				attributes.add(Integer.toString(((Item_Food) this.item).archeryBoost));
				attributes.add(Integer.toString(((Item_Food) this.item).magicBoost));
				attributes.add(Integer.toString(((Item_Food) this.item).maxHitBoost));
				attributes.add(Integer.toString(((Item_Food) this.item).accuracyBoost));
				attributes.add(Integer.toString(((Item_Food) this.item).defenseBoost));
				
				Packet026RemoveDroppedItem packet = new Packet026RemoveDroppedItem(this.level.name, "food", this.mapX, this.mapY, attributes);
				packet.writeData(Game.socketServer);
			}
			
			else if (this.item instanceof Item_Equipment) {
				attributes.add(this.item.iconImageFile);
				attributes.add(((Item_Equipment) this.item).entityImageFile);
				attributes.add(this.item.name);
				attributes.add(Boolean.toString(this.item.stackable));
				attributes.add(Integer.toString(this.item.amount));
				attributes.add(Boolean.toString(((Item_Equipment) this.item).isMelee));
				attributes.add(Boolean.toString(((Item_Equipment) this.item).isArchery));
				attributes.add(Boolean.toString(((Item_Equipment) this.item).isMagic));
				attributes.add(Integer.toString(((Item_Equipment) this.item).slot));
				attributes.add(Integer.toString(this.item.value));
				attributes.add(Double.toString(((Item_Equipment) this.item).range));
				attributes.add(Integer.toString(((Item_Equipment) this.item).speed));
				attributes.add(Double.toString(((Item_Equipment) this.item).accuracy));
				attributes.add(Integer.toString(((Item_Equipment) this.item).maxHit));
				attributes.add(Integer.toString(((Item_Equipment) this.item).armor));
				attributes.add(Integer.toString(((Item_Equipment) this.item).meleeBoost));
				attributes.add(Integer.toString(((Item_Equipment) this.item).archeryBoost));
				attributes.add(Integer.toString(((Item_Equipment) this.item).magicBoost));
				for (Ability a : ((Item_Equipment)this.item).abilities) {
					if (a == null) continue;
					attributes.add(a.getClass().toString());
				}
				
				Packet026RemoveDroppedItem packet = new Packet026RemoveDroppedItem(this.level.name, "equipment", this.mapX, this.mapY, attributes);
				packet.writeData(Game.socketServer);
			}
		}
		else {
			if (this.level.world.currentPlayer.level.equals(this.level)) {
				setRenderCoordinates();
				if (!(this.x + this.actionSize < 0) && !(this.x - this.actionSize > 100) && !(this.y + this.actionSize < 0) && !(this.y - this.actionSize > 100)) {
					this.shouldRender = true;
				} else {
					this.shouldRender = false;
				}
			} else {
				this.shouldRender = false;
			}
		}
	}
	
	public void render() {
		this.item.render(this.x, this.y);
	}
	
	public void miniMapRender(double x, double y, double size) {
		StdDraw.setPenColor(this.miniMapColor);
		
		double[] xCoords, yCoords;

		xCoords = new double[]{x, x - size, x + size};
		yCoords = new double[]{y + size, y - size, y - size};
		StdDraw.filledPolygon(xCoords, yCoords);
	}

	private void setRenderCoordinates() {
		this.x = Game.player.x + (this.mapX - Game.player.getMapX());
		this.y = Game.player.y + (this.mapY - Game.player.getMapY());
	}
}
