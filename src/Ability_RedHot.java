import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

public class Ability_RedHot extends Ability implements Serializable {
	
	public Ability_RedHot() {
		this.iconImageFile = "redHot.png";
		this.isMelee = false;
		this.isArchery = false;
		this.isMagic = true;
		this.cooldownAmount = 850;
		this.energyCost = 276;
	}
	
	@Override
	public void update() {
		if (this.isActive && this.cooldown == 0) {
			if (this.owner.energy < this.energyCost) {
				this.isActive = false;
				return;
			}
			
			for (Entity_Character e : this.targets) new Effect_Negative_RedHot(this.owner, e);
			
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
		double height = 12;
		
		//Window
		StdDraw.setPenColor(new Color(88, 237, 219));
		StdDraw.filledRectangle(x, y - height - 2, width, height);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.rectangle(x, y - height - 2, width, height);
		///////////////////
		
		//Text
		StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
		StdDraw.text(x, y - 4.5, "Red Hot");
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
		StdDraw.text(x, y - 7.5, "When attacking, if this");
		StdDraw.text(x, y - 10, "ability is selected,");
		StdDraw.text(x, y - 12.5, "your target will burn");
		StdDraw.text(x, y - 15, "for 2 damage over time");
		StdDraw.text(x, y - 17.5, "up to 10 damage.");
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
		StdDraw.text(x - 4, y - 22, "Cooldown:");
		StdDraw.text(x - 3, y - 24.5, "Energy cost:");
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
		StdDraw.text(x + 6.5, y - 22, this.cooldownAmount + "");
		StdDraw.text(x + 6.5, y - 24.5, this.energyCost + "");
		///////////////////
	}

}
