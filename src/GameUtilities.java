import java.util.ArrayList;

public class GameUtilities {
	
	/**
	 * Returns the absolute distance between the first and second pair of coordinates.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
	}
	
	/**
	 * Returns an ArrayList of Strings in the order necessary to send information about items
	 * throught packets.
	 * 
	 * @param item
	 * @return ArrayList
	 */
	public static ArrayList<String> createAttributes(Item item, int amount) {
		
		ArrayList<String> attributes = new ArrayList<String>();
		
		if (item instanceof Item_Money) {
			attributes.add(Integer.toString(amount));
		}
		
		else if (item instanceof Item_Food) {
			attributes.add(item.name);
			attributes.add(item.iconImageFile);
			attributes.add(Boolean.toString(item.stackable));
			attributes.add(Integer.toString(amount));
			attributes.add(Integer.toString(item.value));
			attributes.add(Integer.toString(((Item_Food) item).healAmount));
			attributes.add(Integer.toString(((Item_Food) item).meleeBoost));
			attributes.add(Integer.toString(((Item_Food) item).archeryBoost));
			attributes.add(Integer.toString(((Item_Food) item).magicBoost));
			attributes.add(Integer.toString(((Item_Food) item).maxHitBoost));
			attributes.add(Integer.toString(((Item_Food) item).accuracyBoost));
			attributes.add(Integer.toString(((Item_Food) item).defenseBoost));
		}
		
		else if (item instanceof Item_Equipment) {
			attributes.add(item.iconImageFile);
			attributes.add(((Item_Equipment) item).entityImageFile);
			attributes.add(item.name);
			attributes.add(Boolean.toString(item.stackable));
			attributes.add(Integer.toString(amount));
			attributes.add(Boolean.toString(((Item_Equipment) item).isMelee));
			attributes.add(Boolean.toString(((Item_Equipment) item).isArchery));
			attributes.add(Boolean.toString(((Item_Equipment) item).isMagic));
			attributes.add(Integer.toString(((Item_Equipment) item).slot));
			attributes.add(Integer.toString(item.value));
			attributes.add(Double.toString(((Item_Equipment) item).range));
			attributes.add(Integer.toString(((Item_Equipment) item).speed));
			attributes.add(Double.toString(((Item_Equipment) item).accuracy));
			attributes.add(Integer.toString(((Item_Equipment) item).maxHit));
			attributes.add(Integer.toString(((Item_Equipment) item).armor));
			attributes.add(Integer.toString(((Item_Equipment) item).meleeBoost));
			attributes.add(Integer.toString(((Item_Equipment) item).archeryBoost));
			attributes.add(Integer.toString(((Item_Equipment) item).magicBoost));
			for (Ability a : ((Item_Equipment)item).abilities) {
				if (a == null) continue;
				attributes.add(a.getClass().toString());
			}
		}
		
		return attributes;
	}
	
	/**
	 * Returns the item type of the item given in a format necessary for packets regarding items.
	 * 
	 * @param item
	 * @return String
	 */
	public static String itemTypeToString(Item item) {
		
		if (item instanceof Item_Money) {
			return "money";
		}
		
		else if (item instanceof Item_Food) {
			return "food";
		}
		
		else if (item instanceof Item_Equipment) {
			return "equipment";
		}
		
		return "invalid item type";
	}

}
