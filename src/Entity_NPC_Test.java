import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Entity_NPC_Test extends Entity_NPC implements Comparable<Entity>, Serializable {
	
	public Entity_NPC_Test(Level level, boolean canAttack, boolean canBeAttacked, String faction, String name, double mapX, double mapY, double maxXWalk, double maxYWalk, double minXWalk, double minYWalk, Item_Equipment helmet, Item_Equipment chest, Item_Equipment legs, Item_Equipment boots, Item_Equipment gloves, Item_Equipment shield, Item_Equipment weapon, Item_Equipment ammo) {
		this.level = level;
		setMapX(mapX);
		setMapY(mapY);
		this.maxXWalk = maxXWalk;
		this.minXWalk = minXWalk;
		this.maxYWalk = maxYWalk;
		this.minYWalk = minYWalk;
		
		//Save checkpoint location
		this.checkpointLevel = level;
		this.checkpointMapX = mapX;
		this.checkpointMapY = mapY;
		//////////////////////
		
		this.maxX = 4.5;
		this.minX = 4.5;
		this.maxY = 2.25;
		this.minY = 4.5;
		
		this.speed = 0.385;
		this.maxHealth = 1004;
		this.currentHealth = this.maxHealth;
		this.faction = faction;
		this.name = name;
		this.canAttack = canAttack;
		this.canBeAttacked = canBeAttacked;
		this.aggressive = true;
		
		this.miniMapColor = StdDraw.RED;

		this.helmet = helmet;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		this.gloves = gloves;
		this.shield = shield;
		this.weapon = weapon;
		this.ammo = ammo;
		
		this.attackDelay = 155;
		
		this.waypoint_path = new Waypoint_Path(this);
	}

	@Override
	public void update() {
		super.update();
		if (Game.socketServer != null) {
			if (this.isFrozen == true) return;
			if (this.canMove) move();
			else {
				this.moveX = 0;
				this.moveY = 0;
			}
		}
	}

	@Override
	public void render() {
		if (this.shouldRender) {
			StdDraw.setPenColor(this.miniMapColor);
			StdDraw.filledSquare(this.x, this.y, this.maxX);
		}
		
		if (!this.pathfindX.isEmpty()) {
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.setPenRadius(0.0083);
			
			double lastScreenX = this.x, lastScreenY = this.y;
			for (int i = 0; i < this.pathfindX.size(); i++) {
					
				StdDraw.line(lastScreenX, lastScreenY, lastScreenX + this.pathfindX.get(i), lastScreenY + this.pathfindY.get(i));
				
				lastScreenX += this.pathfindX.get(i);
				lastScreenY += this.pathfindY.get(i);
			}
			
			StdDraw.setPenRadius();
		}
	}

	@Override
	public void move() {
		if (this.isOnWaypoint == true) {
			
		}
		
		if (this.lastTarget == null) {
			this.updatePathFind = true;
			moveWithinWalkZone(0.5);
		} else {
			//Follow last target
			
			//Follow target to their current level
			if ((this.lastTarget instanceof Entity_NPC && !this.level.getEntities().contains(this.lastTarget)) || (this.lastTarget instanceof Player && this.level.world.currentPlayer == null)) {
				for (Entity e : this.level.getEntities()) {
					if (e instanceof LevelConnector) {
						if ((this.lastTarget instanceof Entity_NPC && ((LevelConnector)e).destination.getEntities().contains(this.lastTarget)) || (this.lastTarget instanceof Player && ((LevelConnector)e).destination.world.currentPlayer != null)) {

							if (GameUtilities.getDistance(getMapX(), getMapY(), e.getMapX(), e.getMapY()) <= ((LevelConnector)e).actionSize) {
								changeLevel((LevelConnector)e);
							}
							
							else {
								if (e.getMapX() > getMapX()) this.moveX += this.speed;
								else if (e.getMapX() < getMapX()) this.moveX -= this.speed;
								if (e.getMapY() > getMapY()) this.moveY += this.speed;
								else if (e.getMapY() < getMapY()) this.moveY -= this.speed;
							}
							
							break;
						}
					}
				}
			}
			////////////
			
			else {
				/*
				if (this.lastTarget.getMapX() - this.lastTarget.minX > getMapX() + this.maxX) this.moveX += this.speed;
				else if (this.lastTarget.getMapX() + this.lastTarget.maxX < getMapX() - this.minX) this.moveX -= this.speed;
				if (this.lastTarget.getMapY() - this.lastTarget.minY > getMapY() + (this.maxY * 2)) this.moveY += this.speed;
				else if (this.lastTarget.getMapY() + this.lastTarget.maxY < getMapY() - this.minY) this.moveY -= this.speed;
				*/
				
				//Recalculate path if target moved too much since last path calculation
				if (GameUtilities.getDistance(this.lastTarget.getMapX(), this.lastTarget.getMapY(), this.destinationX, this.destinationY) > 95) this.updatePathFind = true;
				////////////////////////
				
				if (this.canMove && ((this.pathfindX.isEmpty() && this.pathfindY.isEmpty()) || this.updatePathFind)) {
					if (pathFind(this.lastTarget.getMapX(), this.lastTarget.getMapY(), 10)) {
						this.updatePathFind = false;
						if (!this.pathfindX.isEmpty() && !this.pathfindY.isEmpty()) {
							this.moveX = this.pathfindX.get(0);
							this.pathfindX.remove(0);
							this.moveY = this.pathfindY.get(0);
							this.pathfindY.remove(0);
						}
					} else {
						this.lastTarget = null;
						this.updatePathFind = true;
					}
				}
				
				if (this.moveX == 0 && this.moveY == 0 && !this.pathfindX.isEmpty() && !this.pathfindY.isEmpty()) {
					this.moveX = this.pathfindX.get(0);
					this.pathfindX.remove(0);
					this.moveY = this.pathfindY.get(0);
					this.pathfindY.remove(0);
				}
				
				handlePackets();
			}
			
			if (!this.canMove) {
				this.moveX = 0;
				this.moveY = 0;
				this.pathfindX.clear();
				this.pathfindY.clear();
			}
			
			//Check for collisions and move
			if (this.moveX > 0) {
				if (!willCollide(getMapX() + this.speed, getMapY())) {
					moveTo(getMapX() + this.speed, getMapY(), getMovingDir());
					this.moveX -= this.speed;
					if (this.moveX < 0) {
						moveTo(getMapX() + this.moveX, getMapY(), getMovingDir());
						this.moveX = 0;
					}
				} else {
					this.moveX = 0;
					this.pathfindX.clear();
					this.pathfindY.clear();
				}
			}
			
			else if (this.moveX < 0) {
				if (!willCollide(getMapX() - this.speed, getMapY())) {
					moveTo(getMapX() - this.speed, getMapY(), getMovingDir());
					this.moveX += this.speed;
					if (this.moveX > 0) {
						moveTo(getMapX() - this.moveX, getMapY(), getMovingDir());
						this.moveX = 0;
					}
				} else {
					this.moveX = 0;
					this.pathfindX.clear();
					this.pathfindY.clear();
				}
			}
			
			if (this.moveY > 0) {
				if (!willCollide(getMapX(), getMapY() + this.speed)) {
					moveTo(getMapX(), getMapY() + this.speed, getMovingDir());
					this.moveY -= this.speed;
					if (this.moveY < 0) {
						moveTo(getMapX(), getMapY() + this.moveY, getMovingDir());
						this.moveY = 0;
					}
				} else {
					this.moveY = 0;
					this.pathfindX.clear();
					this.pathfindY.clear();
				}
			}
			
			else if (this.moveY < 0) {
				if (!willCollide(getMapX(), getMapY() - this.speed)) {
					moveTo(getMapX(), getMapY() - this.speed, getMovingDir());
					this.moveY += this.speed;
					if (this.moveY > 0) {
						moveTo(getMapX(), getMapY() - this.moveY, getMovingDir());
						this.moveY = 0;
					}
				} else {
					this.moveY = 0;
					this.pathfindX.clear();
					this.pathfindY.clear();
				}
			}
			///////////////////////////
			///////////////////////////
		}
		
		if (this.moveX != 0 || this.moveY != 0) {
			if (this.moveX > 0 && this.moveY > 0) setMovingDir(2);
			else if (this.moveX > 0 && this.moveY < 0) setMovingDir(4);
			else if (this.moveX < 0 && this.moveY < 0) setMovingDir(6);
			else if (this.moveX < 0 && this.moveY > 0) setMovingDir(8);
			else if (this.moveX > 0) setMovingDir(3);
			else if (this.moveX < 0) setMovingDir(7);
			else if (this.moveY > 0) setMovingDir(1);
			else if (this.moveY < 0) setMovingDir(5);
			this.isMoving = true;
		} else {
			this.isMoving = false;
		}
	}

	@Override
	public void checkTimers() {
		if (this.attackDelayTimer > 0) this.attackDelayTimer--;
		
		if (this.combatTimer > 0) {
			this.combatTimer--;
			if (this.combatTimer == 0) {
				this.isInCombat = false;
				this.lastTarget = null;
			}
		}
	}

	@Override
	public List<DroppedItem> dropItems() {
		List<DroppedItem> items = new ArrayList<DroppedItem>();
		
		if (ThreadLocalRandom.current().nextBoolean()) items.add(new DroppedItem(this.level, new Item_Food("Jewbies" + random.nextInt(2800), "jewbies.png", true, random.nextInt(2800), random.nextInt(2800), random.nextInt(2800), random.nextInt(2800), random.nextInt(2800), random.nextInt(2800), random.nextInt(2800), random.nextInt(2800), random.nextInt(2800)), getMapX(), getMapY()));
		
		return items;
	}

	@Override
	public Dialogue dialogue(int index, ArrayList<String> dialog) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public void setDialogueList(ArrayList<String> dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dialogue dialogue(int index) {
		// TODO Auto-generated method stub
		return null;
	}
}
