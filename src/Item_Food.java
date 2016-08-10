import java.io.Serializable;

public class Item_Food extends Item implements Serializable {
	
	public int healAmount, meleeBoost, archeryBoost, magicBoost, maxHitBoost, accuracyBoost, defenseBoost;
	
	public Item_Food(String name, String iconImageFile, boolean stackable, int amount, int value, int healAmount, int meleeBoost, int archeryBoost, int magicBoost, int maxHitBoost, int accuracyBoost, int defenseBoost) {
		this.name = name;
		this.iconImageFile = iconImageFile;
		this.stackable = stackable;
		this.amount = amount;
		if (!this.stackable && amount > 1) this.amount = 1;
		this.value = value;
		this.healAmount = healAmount;
		this.meleeBoost = meleeBoost;
		this.archeryBoost = archeryBoost;
		this.magicBoost = magicBoost;
		this.maxHitBoost = maxHitBoost;
		this.accuracyBoost = accuracyBoost;
		this.defenseBoost = defenseBoost;
	}

	@Override
	public void render(double x, double y) {
		StdDraw.picture(x, y, this.iconImageFile);
	}

}
