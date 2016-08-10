import java.io.Serializable;

public class Effect_Positive_RapidHeal extends Effect_Positive implements Serializable {
	
	public Effect_Positive_RapidHeal(Entity_Character owner, Entity_Character target) {
		this.owner = owner;
		this.target = target;
		this.imageFile = "rapidheal.png";
		
		this.owner.activeEffectsToAdd.add(this);
		this.target.positiveEffectsToAdd.add(this);
		
		this.timeUntilRemoval = 361;
		this.repeatTimerDelay = 90;
	}

	@Override
	public void update() {
		if (this.repeatTimer <= 0) {
			this.repeatTimer = this.repeatTimerDelay;
			
			this.owner.currentHealth += this.owner.maxHealth * 0.04;
			if (this.owner.currentHealth > this.owner.maxHealth) this.owner.currentHealth = this.owner.maxHealth;
			
			if (!this.target.equals(this.owner)) {
				this.target.currentHealth += this.target.maxHealth * 0.025;
				if (this.target.currentHealth > this.target.maxHealth) this.target.currentHealth = this.target.maxHealth;
			}
		}
	}

}
