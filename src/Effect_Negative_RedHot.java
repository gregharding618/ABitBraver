import java.io.Serializable;

public class Effect_Negative_RedHot extends Effect_Negative implements Serializable {
	
	public Effect_Negative_RedHot(Entity_Character owner, Entity_Character target) {
		this.owner = owner;
		this.target = target;
		this.imageFile = "redHot.png";
		
		this.owner.activeEffectsToAdd.add(this);
		this.target.negativeEffectsToAdd.add(this);
		
		this.timeUntilRemoval = 1000;
		this.repeatTimerDelay = 200;
	}

	@Override
	public void update() {
		if (this.repeatTimer <= 0) {
			this.repeatTimer = this.repeatTimerDelay;
			
			this.target.currentHealth -= 2;
			this.target.level.overheadDamageToAdd.add(new OverheadDamage(this.target.level, this.owner, this.target, 2));
		}
	}

}
