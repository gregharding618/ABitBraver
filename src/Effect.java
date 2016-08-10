import java.io.Serializable;

public abstract class Effect implements Serializable {
	
	public Entity_Character owner;
	public Entity_Character target;
	public String imageFile;
	
	//NOTE:
	//These timers are measured in TICKS, aka UPDATES. Using real time would
	//not be smart, since each update takes a different amount of time and
	//the effect could be triggered 3 times in 1 update if using real time,
	//for example, depending on the player's computer speed
	public int timeUntilRemoval = 0; //Time until effect should be removed from the entity
	public int repeatTimer = 0;		 //Time until effect modifys the entity again
	public int repeatTimerDelay = 0; //Delay between the effect's repeated modification to the entity
	
	public abstract void update();
	public abstract void checkTimers();

	public void renderIcon(double x, double y) {
		StdDraw.picture(x, y, this.imageFile);
	}

	public void renderIcon(double x, double y, double constraint) {
		StdDraw.picture(x, y, this.imageFile, constraint, constraint);
	}
}
