import java.io.Serializable;

public abstract class Item implements Comparable<Item>, Serializable {
	
	private static final long serialVersionUID = -5504110900322894109L;
	
	public String name;
	public int value;
	public String iconImageFile;
	public boolean stackable;
	public int amount = 1;
	
	public abstract void render(double x, double y);

	public int compareTo(Item i) {
		if (this.value > i.value) return -1;

		return 1;
	}
	
	public static Item clone(Item item) {
		
		if (item instanceof Item_Equipment) {
			Item_Equipment newItem = new Item_Equipment(item.iconImageFile, ((Item_Equipment) item).entityImageFile, item.name, item.stackable, item.amount, ((Item_Equipment) item).isMelee, ((Item_Equipment) item).isArchery, ((Item_Equipment) item).isMagic, ((Item_Equipment) item).slot, item.value, ((Item_Equipment) item).accuracy, ((Item_Equipment) item).speed, ((Item_Equipment) item).accuracy, ((Item_Equipment) item).maxHit, ((Item_Equipment) item).armor, ((Item_Equipment) item).meleeBoost, ((Item_Equipment) item).archeryBoost, ((Item_Equipment) item).magicBoost);
			
			newItem.abilities = ((Item_Equipment)item).abilities;
			
			return newItem;
		}
		
		else if (item instanceof Item_Food) {
			Item_Food newItem = new Item_Food(item.name, item.iconImageFile, item.stackable, item.amount, item.value, ((Item_Food) item).healAmount, ((Item_Food) item).meleeBoost, ((Item_Food) item).archeryBoost, ((Item_Food) item).magicBoost, ((Item_Food) item).maxHitBoost, ((Item_Food) item).accuracyBoost, ((Item_Food) item).defenseBoost);
			
			return newItem;
		}
		
		return null;
	}
}
