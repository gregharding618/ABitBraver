import java.io.Serializable;

public class Projectile extends Entity implements Serializable {
	
	public Entity_Character owner;
	public double size;
	public String imageFile; //This class assumes arrows, in their pictures, are always facing UP/NORTH
	public boolean shouldRender = false;
	
	private double distanceTraveled = 0;
	
	private static int nameNumber = 0;
	
	public Projectile(Level level, Entity_Character owner, String imageFile, double mapX, double mapY, double size, double speed, double range, int movingDir) {
		this.level = level;
		this.owner = owner;
		this.imageFile = imageFile;
		setMapX(mapX);
		setMapY(mapY);
		this.size = size;
		this.speed = speed;
		this.range = range;
		setMovingDir(movingDir);
		this.isSolid = false;
		
		this.name = "projectile" + nameNumber;
		nameNumber++;
		if (nameNumber == Integer.MAX_VALUE) nameNumber = 0;
	}
	
	public void update() {
		move();
		
		//Delete projectile if it travels beyond it's range
		if (this.distanceTraveled > this.range) this.level.getEntitiesToRemove().add(this);
		else {
			
			checkForHit();
			
			if (Game.player.level.equals(this.level)) {
				setRenderCoordinates();
				if (!(this.x + this.size < 0) && !(this.x - this.size > 100) && !(this.y + this.size * 2 < 0) && !(this.y - this.size > 100)) {
					this.shouldRender = true;
				} else {
					this.shouldRender = false;
				}
			} else {
				this.shouldRender = false;
			}
		}
	}
	
	private void checkForHit() {
		Entity target = willHit(this.getMapX(), this.getMapY());
		if (target != null) {
			if (Game.socketServer != null && target instanceof Entity_Character && ((Entity_Character)target).canBeAttacked) this.owner.attack(((Entity_Character)target), this);
			this.level.getEntitiesToRemove().add(this);
		}
	}

	public void render() {
		if (getMovingDir() == 1) StdDraw.picture(this.x, this.y, this.imageFile);
		else if (getMovingDir() == 2) StdDraw.picture(this.x, this.y, this.imageFile, 315);
		else if (getMovingDir() == 3) StdDraw.picture(this.x, this.y, this.imageFile, 270);
		else if (getMovingDir() == 4) StdDraw.picture(this.x, this.y, this.imageFile, 225);
		else if (getMovingDir() == 5) StdDraw.picture(this.x, this.y, this.imageFile, 180);
		else if (getMovingDir() == 6) StdDraw.picture(this.x, this.y, this.imageFile, 135);
		else if (getMovingDir() == 7) StdDraw.picture(this.x, this.y, this.imageFile, 90);
		else if (getMovingDir() == 8) StdDraw.picture(this.x, this.y, this.imageFile, 45);
	}
	
	@Override
	public void miniMapRender(MiniMap minimap, double x, double y, double size) {
		
	}
	
	@Override
	public void move() {
		if (getMovingDir() == 1) {
			moveTo(getMapX(), getMapY() + this.speed, getMovingDir());
		}
		
		else if (getMovingDir() == 2) {
			moveTo(getMapX() + (this.speed / 2), getMapY() + (this.speed / 2), getMovingDir());
		}
		
		else if (getMovingDir() == 3) {
			moveTo(getMapX() + this.speed, getMapY(), getMovingDir());
		}
		
		else if (getMovingDir() == 4) {
			moveTo(getMapX() + (this.speed / 2), getMapY() - (this.speed / 2), getMovingDir());
		}
		
		else if (getMovingDir() == 5) {
			moveTo(getMapX(), getMapY() - this.speed, getMovingDir());
		}
		
		else if (getMovingDir() == 6) {
			moveTo(getMapX() - (this.speed / 2), getMapY() - (this.speed / 2), getMovingDir());
		}
		
		else if (getMovingDir() == 7) {
			moveTo(getMapX() - this.speed, getMapY(), getMovingDir());
		}
		
		else if (getMovingDir() == 8) {
			moveTo(getMapX() - (this.speed / 2), getMapY() + (this.speed / 2), getMovingDir());
		}
		
		this.distanceTraveled += this.speed;
	}
	
	private Entity willHit(double futureX, double futureY) {
		// Level borders
		if (futureX - this.size < 0) {
			this.level.getEntitiesToRemove().add(this);
			return null;
		}

		else if (futureX + this.size > this.level.xMax) {
			this.level.getEntitiesToRemove().add(this);
			return null;
		}

		if (futureY - this.size < 0) {
			this.level.getEntitiesToRemove().add(this);
			return null;
		}

		else if (futureY + this.size > this.level.yMax) {
			this.level.getEntitiesToRemove().add(this);
			return null;
		}
		///////////////////////

		// Entities
		for (Entity e : this.level.getEntities()) {
			if (e.isSolid && !e.equals(this) && !e.equals(this.owner) && !this.level.getEntitiesToRemove().contains(e)) {
				if (futureX - this.size < e.getMapX() + e.maxX && 
					futureX + this.size > e.getMapX() - e.minX && 
					futureY - this.size < e.getMapY() + e.maxY * 2 && 
					futureY + this.size > e.getMapY() - e.minY) {

					return e;
				}
			}
		}
		///////////////////////

		return null;
	}
	
	protected void setRenderCoordinates() {
		this.x = Game.player.x + (this.getMapX() - Game.player.getMapX());
		this.y = Game.player.y + (this.getMapY() - Game.player.getMapY());
	}

	@Override
	public void checkTimers() {
		
	}
}
