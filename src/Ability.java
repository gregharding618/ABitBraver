import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Ability implements Serializable {
	
	private static final long serialVersionUID = -4118481255722035959L;
	
	public Entity_Character owner;
	public List<Entity_Character> targets = new ArrayList<Entity_Character>();
	
	public boolean isMelee, isArchery, isMagic;
	public boolean isActive = false;
	public String iconImageFile;
	public int cooldown = 0;
	public int cooldownAmount;
	public int energyCost;
	public Player player;
	
	private boolean justBecameAvailable = false;
	private int availableRed = 85, availableGreen = 85, availableBlue = 85;
	private boolean availableGlowUp1 = true;
	private boolean availableGlowUp2 = true, availableGlowUp2Start = false;

	public abstract void update();
	public abstract void renderDescription(double x, double y);
	
	public void renderIcon(double x, double y) {
		StdDraw.picture(x, y, this.iconImageFile);
	}

	public void renderIcon(double x, double y, double constraint) {
		if (this.cooldown <= 0 && this.owner.energy >= this.energyCost) {
			StdDraw.picture(x, y, this.iconImageFile, constraint, constraint);
			
			if (this.justBecameAvailable) {
				StdDraw.setPenColor(new Color(this.availableRed, this.availableGreen, this.availableBlue));
				StdDraw.setPenRadius(0.0075 + (this.availableBlue / 100000.0));
				StdDraw.square(x, y, 2.8 + (this.availableBlue / 275.0));
				StdDraw.setPenRadius();
				
				if (this.availableGlowUp1) {
					this.availableRed += 2;
					this.availableGreen += 3;
					this.availableBlue += 4;
					
					if (this.availableBlue >= 250) this.availableGlowUp1 = false;
					
				} else if (!this.availableGlowUp1 && this.availableGlowUp2) {
					if (this.availableGlowUp2Start) {
						this.availableRed += 2;
						this.availableGreen += 3;
						this.availableBlue += 4;
						
						if (this.availableBlue >= 250) this.availableGlowUp2 = false;
					} else {
						this.availableRed -= 3;
						this.availableGreen -= 4;
						this.availableBlue -= 5;
					
						if (this.availableBlue <= 150) this.availableGlowUp2Start = true;
					}
				} else {
					this.availableRed -= 2;
					this.availableGreen -= 3;
					this.availableBlue -= 4;
					
					if (this.availableBlue <= 84) {
						this.justBecameAvailable = false;
						this.availableRed = 85;
						this.availableGreen = 85;
						this.availableBlue = 85;
						this.availableGlowUp1 = true;
						this.availableGlowUp2 = true;
						this.availableGlowUp2Start = false;
					}
				}
			}
		}
		else {
			StdDraw.picture(x, y, this.iconImageFile.substring(0, this.iconImageFile.length() - 4) + "_cooldown.png", constraint, constraint);
			StdDraw.setPenColor(StdDraw.RED);
			
			double[] xCoords, yCoords;

			xCoords = new double[]{x - 3.5, x + 3.5, x + 3.5, x - 3.5};
			yCoords = new double[]{y - 3.6, y - 3.6, (y - 3.6) + (7 * (this.cooldown / (this.cooldownAmount + 0.0))), (y - 3.6) + (7 * (this.cooldown / (this.cooldownAmount + 0.0)))};
			
			StdDraw.filledPolygon(xCoords, yCoords);
			this.justBecameAvailable = true;
		}
	}

}
