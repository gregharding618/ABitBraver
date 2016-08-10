import java.io.Serializable;

public abstract class Effect_Positive extends Effect implements Serializable {
	
	public void checkTimers() {
		if (this.timeUntilRemoval > 0) this.timeUntilRemoval--;
		
		if (this.timeUntilRemoval <= 0) {
			if (this.owner != null) this.owner.activeEffectsToRemove.add(this);
			if (this.target != null) this.target.positiveEffectsToRemove.add(this);
		}
		
		if (this.repeatTimer <= 0) this.repeatTimer = this.repeatTimerDelay;
		
		else this.repeatTimer--;
	}

}
