import java.io.Serializable;

public class Item_Equipment_Test extends Item_Equipment implements Serializable {
	
	public Item_Equipment_Test(String iconImageFile, String entityImageFile, String name, boolean stackable, int amount, boolean isMelee, boolean isArchery, boolean isMagic, int slot, int value, double range, int speed, double accuracy, int maxHit, int armor, int meleeBoost, int archeryBoost, int magicBoost) {
		super(iconImageFile, entityImageFile, name, stackable, amount, isMelee, isArchery, isMagic, slot, value, range, speed, accuracy, maxHit, armor, meleeBoost, archeryBoost, magicBoost);
	}

	@Override
	public void render(double x, double y) {
		StdDraw.picture(x, y, "helmet_gold.png");
	}

}
