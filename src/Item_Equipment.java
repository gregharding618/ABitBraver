import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Item_Equipment extends Item implements Serializable {
	
	public int speed = 0, maxHit = 0, armor = 0, meleeBoost = 0, archeryBoost = 0, magicBoost = 0;
	public double accuracy = 0;
	//For speed, it'll be     x - speed,     x being Entity_Characters MAX_ATTACK_DELAY variable
	//The higher speed is, the faster the entity can attack.
	
	//"Strength" (mentioned in-game) and "maxHit" variable are synonamous
	
	public boolean isMelee, isArchery, isMagic;
	
	public int slot; //0 = any slot (used only by Item_Equipment_EmptySpace), 1 = helmet, 2 = chest, 3 = legs, 4 = boots, 5 = gloves, 6 = shield, 7 = weapon, 8 = ammo
	public double range = 0;
	public String entityImageFile;
	
	public Ability[] abilities = new Ability[6];
	
	private static Random random = new Random();
	private static final int NUM_MELEE_ABILITIES = 2;
	private static final int NUM_ARCHERY_ABILITIES = 0;
	private static final int NUM_MAGIC_ABILITIES = 2;
	
	public Item_Equipment(String iconImageFile, String entityImageFile, String name, boolean stackable, int amount, boolean isMelee, boolean isArchery, boolean isMagic, int slot, int value, double range, int speed, double accuracy, int maxHit, int armor, int meleeBoost, int archeryBoost, int magicBoost) {
		this.iconImageFile = iconImageFile;
		this.entityImageFile = entityImageFile;
		this.name = name;
		this.stackable = stackable;
		this.amount = amount;
		if (!this.stackable && amount > 1) this.amount = 1;
		this.isMelee = isMelee;
		this.isArchery = isArchery;
		this.isMagic = isMagic;
		this.value = value;
		this.slot = slot;
		this.range = range;
		this.speed = speed;
		this.accuracy = accuracy;
		this.maxHit = maxHit;
		this.armor = armor;
		this.meleeBoost = meleeBoost;
		this.archeryBoost = archeryBoost;
		this.magicBoost = magicBoost;
		
		if (this.slot == 7) {
			assignAbilities();
		}
	}

	@Override
	public void render(double x, double y) {
		StdDraw.picture(x, y, this.iconImageFile);
	}
	
	public void renderOnEntity(Entity e) {
		
	}
	
	private void assignAbilities() {
		if (this.isMelee && !(this instanceof Item_Equipment_EmptySpace)) {
			int numberOfAbilities = random.nextInt((int) ((this.meleeBoost / 15.0) + ThreadLocalRandom.current().nextDouble(0, (maxHit + (this.accuracy / 30.0)) / 27.0)));
			if (numberOfAbilities > 6) numberOfAbilities = 6;
			
			for (int i = 0; i < numberOfAbilities; i++) {
				this.abilities[i] = assignAbility(random.nextInt(NUM_MELEE_ABILITIES));
			}
		}
	
		else if (this.isArchery) {
			int numberOfAbilities = random.nextInt((int) ((this.archeryBoost / 15.0) + ThreadLocalRandom.current().nextDouble(0, (maxHit + (this.accuracy / 30.0)) / 27.0)));if (numberOfAbilities > 6) numberOfAbilities = 6;
			if (numberOfAbilities > 6) numberOfAbilities = 6;
			
			for (int i = 0; i < numberOfAbilities; i++) {
				//this.abilities[i] = assignAbility(random.nextInt(NUM_ARCHERY_ABILITIES));
			}
		}
	
		else if (this.isMagic) {
			int numberOfAbilities = random.nextInt((int) ((this.magicBoost / 15.0) + ThreadLocalRandom.current().nextDouble(0, (maxHit + (this.accuracy / 30.0)) / 27.0)));
			if (numberOfAbilities > 6) numberOfAbilities = 6;
			
			for (int i = 0; i < numberOfAbilities; i++) {
				this.abilities[i] = assignAbility(random.nextInt(NUM_MAGIC_ABILITIES));
			}
		}
	}

	private Ability assignAbility(int ability) {
		Ability a = null;
		
		if (this.isMelee) {
			switch (ability) {
		
			case 0:
				a = new Ability_BoneCrusher();
				break;
				
			case 1:
				a = new Ability_BloodLeech();
				break;
		
			}
		}
		
		else if (this.isArchery) {
			switch (ability) {
		
			}
		}
		
		else if (this.isMagic) {
			switch (ability) {
		
			case 0:
				a = new Ability_RapidHeal();
				break;
				
			case 1:
				a = new Ability_RedHot();
				break;
		
			}
		}
		
		//Check to make sure the weapon doesn't already have this ability
		
		//Will be added when each weapon type has at least 6 abilities
		
		//////////////////////
		
		return a;
	}

}
