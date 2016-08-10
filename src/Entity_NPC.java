import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Entity_NPC extends Entity_Character implements Comparable<Entity>, Serializable {
	
	protected boolean shouldRender = false, aggressive = false;
	protected double aggressiveRange = 10;
	
	public boolean canTalk = false;
	
	public double maxXWalk, minXWalk, maxYWalk, minYWalk, moveX = 0, moveY = 0;
	
	public ArrayList<Dialogue> dialogues;
	
	public abstract List<DroppedItem> dropItems();
	
	public abstract Dialogue dialogue(int index, ArrayList<String> dialog);
	public abstract Dialogue dialogue(int index);
	//public abstract Dialogue dialogue(String message);

	@Override
	public void update() {
		if (this.level.world.currentPlayer.level.equals(this.level)) {
			setRenderCoordinates();
			if (!(this.x + this.maxX < 0) && !(this.x - this.minX > 100) && !(this.y + this.maxY * 2 < 0) && !(this.y - this.minY > 100)) {
				this.shouldRender = true;
			} else {
				this.shouldRender = false;
			}
		} else {
			this.shouldRender = false;
		}
		
		if (Game.socketServer != null) {
			if (this.aggressive) {
				checkForEnemies();
			}
		
			if (this.canAttack) {
				checkForAttack();
			}
		}
		
		handlePackets();
	}
	
	protected void handlePackets() {
		if (Game.socketServer != null) {
			
			//Move packet
			if (this.moveX != 0 || this.moveY != 0) {
				//Packet002Move packet = new Packet002Move(this.name, getMapX(), getMapY(), this.movingDir);
				//packet.writeData(Game.socketServer);
			}
			//////////////////////////
			
		}
	}
	
	private void checkForAttack() {
		if (this.attackDelayTimer <= 0 && this.lastTarget != null) {

			if (this.lastTarget.level.equals(this.level) && GameUtilities.getDistance(getMapX(), getMapY(), this.lastTarget.getMapX(), this.lastTarget.getMapY()) <= this.range) {
				attack(this.lastTarget);
			}
		}
	}

	private void checkForEnemies() {
		if (this.canAttack && this.lastTarget == null) {
			for (Entity e : this.level.getEntities()) {
				if (!e.equals(this) && e instanceof Entity_Character && ((Entity_Character)e).canBeAttacked && !this.level.getEntitiesToRemove().contains(e) && !((Entity_Character)e).faction.equals(this.faction)) {

					if (GameUtilities.getDistance(getMapX(), getMapY(), e.getMapX(), e.getMapY()) <= this.aggressiveRange) {
						this.lastTarget = (Entity_Character)e;
						this.combatTimer = this.combatDelay;
					}
				}
			}
		}
	}

	protected void setRenderCoordinates() {
		this.x = Game.player.x + (getMapX() - Game.player.getMapX());
		this.y = Game.player.y + (getMapY() - Game.player.getMapY());
	}
	
	protected void moveWithinWalkZone(double chance) {
		if (!this.canMove) {
			this.moveX = 0;
			this.moveY = 0;
			return;
		}
		
		//If random number says entity can move
		if (this.moveX == 0 && this.moveY == 0 && ThreadLocalRandom.current().nextDouble(0, 100) <= chance) {
			//Random x within set walk zone
			if (random.nextBoolean()) {
				if (getMapX() + this.maxX <= this.checkpointMapX + this.maxXWalk) this.moveX = ThreadLocalRandom.current().nextDouble(0, (this.checkpointMapX + this.maxXWalk) - getMapX());
			} else {
				if (getMapX() - this.minX >= this.checkpointMapX - this.minXWalk) this.moveX = ThreadLocalRandom.current().nextDouble(0, getMapX() + (this.checkpointMapX - this.minXWalk)) * -1;
			}
			////////////////////////
			
			//Random y within set walk zone
			if (random.nextBoolean()) {
				if (getMapY() + (this.maxY * 2) <= this.checkpointMapY + this.maxYWalk) this.moveY = ThreadLocalRandom.current().nextDouble(0, (this.checkpointMapY + this.maxYWalk) - getMapY());
			} else {
				if (getMapY() - this.minY >= this.checkpointMapY - this.minYWalk) this.moveY = ThreadLocalRandom.current().nextDouble(0, getMapY() + (this.checkpointMapY - this.minYWalk)) * -1;
			}
			////////////////////////
		}
		////////////////////////
		
		//Check for collisions
		if (this.moveX > 0) {
			if (!willCollide(getMapX() + this.maxX + this.speed, getMapY())) {
				moveTo(getMapX() + this.speed, getMapY(), getMovingDir());
				this.moveX -= this.speed;
				if (this.moveX < 0) {
					moveTo(getMapX() + this.moveX, getMapY(), getMovingDir());
					this.moveX = 0;
				}
			} else {
				this.moveX = 0;
			}
		}
		
		else if (this.moveX < 0) {
			if (!willCollide(getMapX() - this.minX - this.speed, getMapY())) {
				moveTo(getMapX() - this.speed, getMapY(), getMovingDir());
				this.moveX += this.speed;
				if (this.moveX > 0) {
					moveTo(getMapX() - this.moveX, getMapY(), getMovingDir());
					this.moveX = 0;
				}
			} else {
				this.moveX = 0;
			}
		}
		
		if (this.moveY > 0) {
			if (!willCollide(getMapX(), getMapY() + this.maxY + this.speed)) {
				moveTo(getMapX(), getMapY() + this.speed, getMovingDir());
				this.moveY -= this.speed;
				if (this.moveY < 0) {
					moveTo(getMapX(), getMapY() + this.moveY, getMovingDir());
					this.moveY = 0;
				}
			} else {
				this.moveY = 0;
			}
		}
		
		else if (this.moveY < 0) {
			if (!willCollide(getMapX(), getMapY() - this.minY - this.speed)) {
				moveTo(getMapX(), getMapY() - this.speed, getMovingDir());
				this.moveY += this.speed;
				if (this.moveY > 0) {
					moveTo(getMapX(), getMapY() - this.moveY, getMovingDir());
					this.moveY = 0;
				}
			} else {
				this.moveY = 0;
			}
		}
		////////////////////////
		
		//If entity's body is outside of walk zone
		if (getMapX() + this.maxX > this.maxXWalk) {
			moveTo(this.maxXWalk - this.maxX, getMapY(), getMovingDir());
			this.moveX = 0;
		}
		
		else if (getMapX() - this.minX < this.minXWalk) {
			moveTo(this.minXWalk + this.minX, getMapY(), getMovingDir());
			this.moveX = 0;
		}
		
		if (getMapY() + this.maxY > this.maxYWalk) {
			moveTo(getMapX(), this.maxYWalk - this.maxY, getMovingDir());
			this.moveY = 0;
		}
		
		else if (getMapY() - this.minY < this.minYWalk) {
			moveTo(getMapX(), this.minYWalk + this.minY, getMovingDir());
			this.moveY = 0;
		}
		////////////////////////
	}
	
	public abstract void setDialogueList(ArrayList<String> dialog);
}
