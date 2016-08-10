import java.io.Serializable;

public class Effect_Negative_BoneCrusher extends Effect_Negative implements Serializable {
	
	private boolean effectApplied = false;

	public Effect_Negative_BoneCrusher(Entity_Character owner, Entity_Character target) {
		this.owner = owner;
		this.target = target;
		this.imageFile = "bonecrusher.png";
		
		this.owner.activeEffectsToAdd.add(this);
		this.target.negativeEffectsToAdd.add(this);
		
		this.timeUntilRemoval = 18350;
	}

	@Override
	public void update() {
		if (!this.effectApplied) {
			this.effectApplied = true;
			
			this.target.tempDefenseBoost -= 3;
		}
	}

}
