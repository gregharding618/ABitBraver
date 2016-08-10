import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

public class Ability_RapidHeal extends Ability implements Serializable {
	
	public Ability_RapidHeal() {
		this.iconImageFile = "rapidheal.png";
		this.isMelee = false;
		this.isArchery = false;
		this.isMagic = true;
		this.cooldownAmount = 1310;
		this.energyCost = 302;
	}

	@Override
	public void update() {
		if (this.isActive && this.cooldown == 0) {
			if (this.owner.energy < this.energyCost) {
				this.isActive = false;
				return;
			}
			
			for (Entity_Character e : this.targets) new Effect_Positive_RapidHeal(this.owner, e);
			
			this.targets.clear();
			this.isActive = false;
			this.cooldown = this.cooldownAmount;
			this.owner.energy -= this.energyCost;
			if (this.owner.energy < 0) this.owner.energy = 0;
			if (this.owner instanceof Player) ((Player)this.owner).energyToReduce += this.energyCost;
		}
		
		if (this.cooldown > 0) this.cooldown--;
	}
	
	@Override
	public void renderDescription(double x, double y) {
		double width = 10;
		double height = 20;
		
		//Window
		StdDraw.setPenColor(new Color(88, 237, 219));
		StdDraw.filledRectangle(x, y - height - 2, width, height);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.rectangle(x, y - height - 2, width, height);
		///////////////////
		
		//Text
		StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
		StdDraw.text(x, y - 4.5, "Rapid Heal");
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
		StdDraw.text(x, y - 7.5, "Using this ability");
		StdDraw.text(x, y - 10, "on a target will heal");
		StdDraw.text(x, y - 12.5, "the target for 12.5%");
		StdDraw.text(x, y - 15, "of their max health");
		StdDraw.text(x, y - 17.5, "over time.");
		
		StdDraw.text(x, y - 21.5, "The user will be healed");
		StdDraw.text(x, y - 24, "for 20% of their max");
		StdDraw.text(x, y - 26.5, "health over time.");
		StdDraw.text(x, y - 29, "Using the ability on");
		StdDraw.text(x, y - 31.5, "yourself will only");
		StdDraw.text(x, y - 34, "grant the 20% healing.");
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
		StdDraw.text(x - 4, y - 38, "Cooldown:");
		StdDraw.text(x - 3, y - 40.5, "Energy cost:");
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
		StdDraw.text(x + 6.5, y - 38, this.cooldownAmount + "");
		StdDraw.text(x + 6.5, y - 40.5, this.energyCost + "");
		///////////////////
	}

}
