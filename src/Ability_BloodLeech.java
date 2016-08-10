import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

public class Ability_BloodLeech extends Ability implements Serializable {
	
	public Ability_BloodLeech() {
		this.iconImageFile = "bloodleech.png";
		this.isMelee = true;
		this.isArchery = false;
		this.isMagic = false;
		this.cooldownAmount = 1875;
		this.energyCost = 334;
	}

	@Override
	public void update() {
		if (this.isActive && this.cooldown == 0) {
			if (this.owner.energy < this.energyCost) {
				this.isActive = false;
				return;
			}
			
			this.isActive = false;
			this.cooldown = this.cooldownAmount;
			this.owner.energy -= this.energyCost;
			if (this.owner.energy < 0) this.owner.energy = 0;
			if (this.owner instanceof Player) ((Player)this.owner).energyToReduce += this.energyCost;
			
			new Effect_Positive_BloodLeech(this.owner, this.cooldown);
		}
		
		if (this.cooldown > 0) this.cooldown--;
	}

	@Override
	public void renderDescription(double x, double y) {
		double width = 10;
		double height = 15.5;
		
		//Window
		StdDraw.setPenColor(new Color(88, 237, 219));
		StdDraw.filledRectangle(x, y - height - 2, width, height);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.rectangle(x, y - height - 2, width, height);
		///////////////////
		
		//Text
		StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
		StdDraw.text(x, y - 4.5, "Blood Leech");
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
		StdDraw.text(x, y - 7.5, "While active, the");
		StdDraw.text(x, y - 10, "user has a 15% ");
		StdDraw.text(x, y - 12.5, "chance to be healed");
		StdDraw.text(x, y - 15, "when dealing damage.");
		StdDraw.text(x, y - 17.5, "The user will be");
		StdDraw.text(x, y - 20, "healed for 10%");
		StdDraw.text(x, y - 22.5, "of the damage dealt.");
		
		StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
		StdDraw.text(x - 4, y - 28, "Cooldown:");
		StdDraw.text(x - 3, y - 30.5, "Energy cost:");
		
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
		StdDraw.text(x + 6.5, y - 28, this.cooldownAmount + "");
		StdDraw.text(x + 6.5, y - 30.5, this.energyCost + "");
		///////////////////
	}

}
