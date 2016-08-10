import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class Effect_Positive_BloodLeech extends Effect_Positive implements Serializable {
	
	public int damageDealt = -1;
	
	public Effect_Positive_BloodLeech(Entity_Character owner, int timeUntilRemoval) {
		this.owner = owner;
		this.imageFile = "bloodleech.png";
		
		this.owner.activeEffectsToAdd.add(this);
		this.owner.positiveEffectsToAdd.add(this);
		
		this.timeUntilRemoval = timeUntilRemoval;
	}

	@Override
	public void update() {
		if (this.damageDealt >= 0 && ThreadLocalRandom.current().nextDouble(0, 100) <= 15) {
			int heal = (int) (this.damageDealt * 0.1);
			if (this.damageDealt > 0 && heal <= 0) heal = 1;
			this.owner.currentHealth += heal;
			if (this.owner.currentHealth > this.owner.maxHealth) this.owner.currentHealth = this.owner.maxHealth;
			
			this.damageDealt = -1;
		}
	}

}
