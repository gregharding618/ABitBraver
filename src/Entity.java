import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Entity implements Comparable<Entity>, Serializable {
	
	private static final long serialVersionUID = 1205572717065018341L;
	
	public Level level;
	public double x, y, maxX, minX, maxY, minY, speed, defaultSpeed = 0.55, range = 12;
	public Level checkpointLevel;
	public double checkpointMapX, checkpointMapY;
	public boolean isMoving = false, isSolid = true;
	
	public Waypoint_Path waypoint_path;
	public boolean isOnWaypoint = false;
	public int waypointIndex = 0;
	
	// Animations test
	public Animations animation;
	public boolean isInWeaponAnimation = false;
	public boolean isInWalkLeftAnimation = false;
	public boolean isInWalkRightAnimation = false;
	public boolean isInWalkUpAnimation = false;
	public boolean isInWalkDownAnimation = false;
	//////////////////////////////////////
	
	public String name;
	
	public Inventory inventory;
	
	public Color miniMapColor;
	
	public abstract void update();
	public abstract void render();
	public abstract void move();
	public abstract void checkTimers();
	
	protected static Random random = new Random();
	
	private double mapX = -999, mapY = -999;
	private int movingDir = 5;
	
	public void checkIfStuck() {
		if (!(this instanceof Entity_Character)) return;
		
		if (willCollide(this.mapX, this.mapY)) {
			boolean up = true;
			boolean down = true;
			boolean left = true;
			boolean right = true;
			
			Entity entity = willCollideWithEntity(this.mapX, this.mapY);
			if (entity != null) {
				while (willCollide(this.mapX, this.mapY)) {
					if (up) {
						if (!willCollide(this.mapX, this.mapY + 0.1, entity)) this.mapY += 0.1;
						else up = false;
					}

					else if (down) {
						if (!willCollide(this.mapX, this.mapY - 0.1, entity)) this.mapY -= 0.1;
						else down = false;
					}

					else if (left) {
						if (!willCollide(this.mapX + 0.1, this.mapY, entity)) this.mapX += 0.1;
						else left = false;
					}

					else if (right) {
						if (!willCollide(this.mapX - 0.1, this.mapY, entity)) this.mapX -= 0.1;
						else right = false;
					}
					
					else {
						this.mapX = this.checkpointMapX;
						this.mapY = this.checkpointMapY;
						return;
					}
				}
			}
			
			Tile tile = willCollideWithTile(this.mapX, this.mapY);
			if (tile != null) {
				while (willCollide(this.mapX, this.mapY)) {
					if (up) {
						if (!willCollide(this.mapX, this.mapY + 0.1, tile)) this.mapY += 0.1;
						else up = false;
					}

					else if (down) {
						if (!willCollide(this.mapX, this.mapY - 0.1, tile)) this.mapY -= 0.1;
						else down = false;
					}

					else if (left) {
						if (!willCollide(this.mapX + 0.1, this.mapY, tile)) this.mapX += 0.1;
						else left = false;
					}

					else if (right) {
						if (!willCollide(this.mapX - 0.1, this.mapY, tile)) this.mapX -= 0.1;
						else right = false;
					}
					
					else {
						this.mapX = this.checkpointMapX;
						this.mapY = this.checkpointMapY;
						return;
					}
				}
			}
		}
	}
	
	protected Entity willCollideWithEntity(double futureX, double futureY) {
		for (Entity e : new ArrayList<>(this.level.getEntities())) {
			if (!(e instanceof Projectile) && e.isSolid && !e.equals(this) && !this.level.getEntitiesToRemove().contains(e)) {
				if (futureX - this.minX < e.mapX + e.maxX &&
					futureX + this.maxX > e.mapX - e.minX &&
					futureY - this.minY < e.mapY + e.maxY &&
					futureY + this.maxY > e.mapY - e.minY) {
					
					return e;
				}
			}
		}
		
		return null;
	}
	
	protected Tile willCollideWithTile(double futureX, double futureY) {
		for (int xx = 0; xx < this.level.tiles.length; xx++) {
			for (int yy = 0; yy < this.level.tiles[xx].length; yy++) {
				Tile tile = this.level.tiles[xx][yy];
				if (tile.walkable) continue;
				
				if (futureX - this.minX < tile.mapX + tile.size &&
					futureX + this.maxX > tile.mapX - tile.size &&
					futureY - this.minY < tile.mapY + tile.size &&
					futureY + this.maxY > tile.mapY - tile.size) {
					
					return tile;
				}
			}
		}
		
		return null;
	}
	
	protected boolean willCollide(double futureX, double futureY) {
		if (this.isSolid) {
			//Level borders
			if (futureX - this.minX < 0) {
				return true;
			}
			else if (futureX + this.maxX > this.level.xMax + (this.level.tileSize * 2)) {
				return true;
			}
		
			if (futureY - this.minY < 0) {
				return true;
			}
		
			else if (futureY + this.maxY * 2 > this.level.yMax + (this.level.tileSize * 2)) {
				return true;
			}
			///////////////////////
			
			//Entities
			for (Entity e : new ArrayList<>(this.level.getEntities())) {
				if (!(e instanceof Projectile) && e.isSolid && !e.equals(this) && !this.name.equals(e.name) && !this.level.getEntitiesToRemove().contains(e)) {
					if (futureX - this.minX < e.mapX + e.maxX &&
						futureX + this.maxX > e.mapX - e.minX &&
						futureY - this.minY < e.mapY + e.maxY &&
						futureY + this.maxY > e.mapY - e.minY) {
						
						return true;
					}
				}
			}
			///////////////////////
			
			//Non-walkable tiles
			for (int xx = 0; xx < this.level.tiles.length; xx++) {
				for (int yy = 0; yy < this.level.tiles[xx].length; yy++) {
					Tile tile = this.level.tiles[xx][yy];
					if (tile.walkable) continue;
					
					if (futureX - this.minX < tile.mapX + tile.size &&
						futureX + this.maxX > tile.mapX - tile.size &&
						futureY - this.minY < tile.mapY + tile.size &&
						futureY + this.maxY > tile.mapY - tile.size) {
						
						return true;
					}
				}
			}
			///////////////////////
		}
		
		return false;
	}
	
	protected boolean willCollide(double futureX, double futureY, Entity entityToExclude) {
		if (this.isSolid) {
			//Level borders
			if (futureX - this.minX < 0) {
				return true;
			}
		
			else if (futureX + this.maxX > this.level.xMax) {
				return true;
			}
		
			if (futureY - this.minY < 0) {
				return true;
			}
		
			else if (futureY + this.maxY * 2 > this.level.yMax) {
				return true;
			}
			///////////////////////
			
			//Entities
			for (Entity e : new ArrayList<>(this.level.getEntities())) {
				if (!e.equals(entityToExclude) && !(e instanceof Projectile) && e.isSolid && !e.equals(this) && !this.level.getEntitiesToRemove().contains(e)) {
					if (futureX - this.minX < e.mapX + e.maxX &&
						futureX + this.maxX > e.mapX - e.minX &&
						futureY - this.minY < e.mapY + e.maxY &&
						futureY + this.maxY > e.mapY - e.minY) {
						
						return true;
					}
				}
			}
			///////////////////////
			
			//Non-walkable tiles
			for (int xx = 0; xx < this.level.tiles.length; xx++) {
				for (int yy = 0; yy < this.level.tiles[xx].length; yy++) {
					Tile tile = this.level.tiles[xx][yy];
					if (tile.walkable) continue;
					
					if (futureX - this.minX < tile.mapX + tile.size &&
						futureX + this.maxX > tile.mapX - tile.size &&
						futureY - this.minY < tile.mapY + tile.size &&
						futureY + this.maxY > tile.mapY - tile.size) {
						
						return true;
					}
				}
			}
			///////////////////////
		}
		
		return false;
	}
	
	protected boolean willCollide(double futureX, double futureY, Tile tileToExclude) {
		if (this.isSolid) {
			//Level borders
			if (futureX - this.minX < 0) {
				return true;
			}
		
			else if (futureX + this.maxX > this.level.xMax) {
				return true;
			}
		
			if (futureY - this.minY < 0) {
				return true;
			}
		
			else if (futureY + this.maxY * 2 > this.level.yMax) {
				return true;
			}
			///////////////////////
			
			//Entities
			for (Entity e : this.level.getEntities()) {
				if (!(e instanceof Projectile) && e.isSolid && !e.equals(this) && !this.level.getEntitiesToRemove().contains(e)) {
					if (futureX - this.minX < e.mapX + e.maxX &&
						futureX + this.maxX > e.mapX - e.minX &&
						futureY - this.minY < e.mapY + e.maxY &&
						futureY + this.maxY > e.mapY - e.minY) {
						
						return true;
					}
				}
			}
			///////////////////////
			
			//Non-walkable tiles
			for (int xx = 0; xx < this.level.tiles.length; xx++) {
				for (int yy = 0; yy < this.level.tiles[xx].length; yy++) {
					Tile tile = this.level.tiles[xx][yy];
					if (tile.walkable || tile.equals(tileToExclude)) continue;
					
					if (futureX - this.minX < tile.mapX + tile.size &&
						futureX + this.maxX > tile.mapX - tile.size &&
						futureY - this.minY < tile.mapY + tile.size &&
						futureY + this.maxY > tile.mapY - tile.size) {
						
						return true;
					}
				}
			}
			///////////////////////
		}
		
		return false;
	}
	
	protected void checkCollision() {
		//Level borders
		if (this.isSolid) {
			if (this.mapX - this.minX < 0) {
				this.mapX = this.minX;
				this.isMoving = false;
			}
		
			else if (this.mapX + this.maxX > this.level.xMax) {
				this.mapX = this.level.xMax - this.maxX;
				this.isMoving = false;
			}
		
			if (this.mapY - this.minY < 0) {
				this.mapY = this.minY;
				this.isMoving = false;
			}
		
			else if (this.mapY + this.maxY * 2 > this.level.yMax) {
				this.mapY = this.level.yMax - this.maxY * 2;
				this.isMoving = false;
			}
		////////////////////////
		
			//Entities
			for (Entity e : new ArrayList<>(this.level.getEntities())) {
				if (e.isSolid && !e.equals(this) && !this.level.getEntitiesToRemove().contains(e)) {
					//Right of collided entity
					if (((this.movingDir == 6 && ((this.mapX - e.mapX) / (this.mapY - e.mapY) > (this.mapY - e.mapY) / (this.mapX - e.mapX))) || this.movingDir == 7 || (this.movingDir == 8 && ((this.mapX - e.mapX) / (e.mapY - this.mapY) > (e.mapY - this.mapY) / (this.mapX - e.mapX)))) && ((this.mapY < e.mapY && this.mapY > e.mapY - e.minY - this.maxY) || (this.mapY > e.mapY && this.mapY < e.mapY + e.maxY + this.minY)) && this.mapX > e.mapX && this.mapX - this.minX <= e.mapX + e.maxX && ((this.mapY > e.mapY && this.mapY - this.minY <= e.mapY + e.maxY) || (this.mapY < e.mapY && this.mapY + this.maxY >= e.mapY - e.minY) || (this.mapY == e.mapY))) {
						this.mapX = e.mapX + e.maxX + this.minX;
						this.isMoving = false;
					}
				
					//Left of collided entity
					else if (((this.movingDir == 2 && ((e.mapX - this.mapX) / (e.mapY - this.mapY) > (e.mapY - this.mapY) / (e.mapX - this.mapX))) || this.movingDir == 3 || (this.movingDir == 4 && ((e.mapX - this.mapX) / (this.mapY - e.mapY) > (this.mapY - e.mapY) / (e.mapX - this.mapX)))) && ((this.mapY < e.mapY && this.mapY > e.mapY - e.minY - this.maxY) || (this.mapY > e.mapY && this.mapY < e.mapY + e.maxY + this.minY)) && this.mapX < e.mapX && this.mapX + this.maxX >= e.mapX - e.minX && ((this.mapY > e.mapY && this.mapY - this.minY <= e.mapY + e.maxY) || (this.mapY < e.mapY && this.mapY + this.maxY >= e.mapY - e.minY) || (this.mapY == e.mapY))) {
						this.mapX = e.mapX - e.minX - this.maxX;
						this.isMoving = false;
					}
				
					//Top of collided entity
					else if ((this.movingDir == 4 || this.movingDir == 5 || this.movingDir == 6) && ((this.mapX < e.mapX && this.mapX > e.mapX - e.minX - this.maxX) || (this.mapX > e.mapX && this.mapX < e.mapX + e.maxX + this.minX)) && this.mapY > e.mapY && this.mapY - this.minY <= e.mapY + e.maxY && ((this.mapX > e.mapX && this.mapX - this.minX <= e.mapX + e.maxX) || (this.mapX < e.mapX && this.mapX + this.maxX >= e.mapX - e.minX) || (this.mapX == e.mapX))) {
						this.mapY = e.mapY + e.maxY + this.minY;
						this.isMoving = false;
					}
				
					//Bottom of collided entity
					else if ((this.movingDir == 8 || this.movingDir == 1 || this.movingDir == 2) && ((this.mapX < e.mapX && this.mapX > e.mapX - e.minX - this.maxX) || (this.mapX > e.mapX && this.mapX < e.mapX + e.maxX + this.minX)) && this.mapY < e.mapY && this.mapY + this.maxY >= e.mapY - e.minY && ((this.mapX > e.mapX && this.mapX - this.minX <= e.mapX + e.maxX) || (this.mapX < e.mapX && this.mapX + this.maxX >= e.mapX - e.minX) || (this.mapX == e.mapX))) {
						this.mapY = e.mapY - e.minY - this.maxY;
						this.isMoving = false;
					}
				}
			}
			////////////////////////
			
			//Non-walkable tiles
			for (int xx = 0; xx < this.level.tiles.length; xx++) {
				for (int yy = 0; yy < this.level.tiles[xx].length; yy++) {
					Tile tile = this.level.tiles[xx][yy];
					if (tile.walkable) continue;
					
					if (this.mapX - this.minX < tile.mapX + tile.size ||
						this.mapX + this.maxX > tile.mapX - tile.size ||
						this.mapY - this.minY < tile.mapY + tile.size ||
						this.mapY + this.maxY > tile.mapY - tile.size) {
						
						if (!willCollide(this.mapX, (this.mapY + this.maxY) - tile.size)) this.mapY = (this.mapY + this.maxY) - tile.size;
						else if (!willCollide(this.mapX, (this.mapY - this.minY) + tile.size)) this.mapY = (this.mapY - this.minY) + tile.size;
						else if (!willCollide((this.mapX + this.maxX) - tile.size, this.mapY)) this.mapX = (this.mapX + this.maxX) - tile.size;
						else if (!willCollide((this.mapX - this.minX) + tile.size, this.mapY)) this.mapX = (this.mapX - this.minX) + tile.size;
						else {
							this.mapX = this.checkpointMapX;
							this.mapY = this.checkpointMapY;
						}
					}
				}
			}
			////////////////////////
		}
		
		if (this instanceof Entity_NPC && !this.isMoving) {
			((Entity_NPC)this).moveX = 0;
			((Entity_NPC)this).moveY = 0;
		}
	}
	
	public void miniMapRender(MiniMap minimap, double x, double y, double size) {
		StdDraw.setPenColor(this.miniMapColor);
		StdDraw.filledCircle(x, y, size);
	}
	
	public int compareTo(Entity e) {
		if (this.mapY > e.mapY) return -1;
		else if (this.mapY == e.mapY) return 0;
		else return 1;
	}
	
	public void moveTo(double mapX, double mapY, int movingDir) {
		if (this instanceof PlayerMP && ((PlayerMP)this).hasInput) {
			this.mapX = mapX;
			this.mapY = mapY;
			this.movingDir = movingDir;
			
			Packet002Move packet = new Packet002Move(this.name, this.mapX, this.mapY, this.movingDir);
			packet.writeData(Game.socketClient);
		}
		
		else if (Game.socketServer != null) {
			this.mapX = mapX;
			this.mapY = mapY;
			this.movingDir = movingDir;
			
			Packet002Move packet = new Packet002Move(this.name, this.mapX, this.mapY, this.movingDir);
			packet.writeData(Game.socketServer);
		}
	}
	
	public double getMapX() {
		return this.mapX;
	}
	
	public double getMapY() {
		return this.mapY;
	}
	
	public int getMovingDir() {
		return this.movingDir;
	}
	
	public void setMapX(double mapX) {
		this.mapX = mapX;
	}
	
	public void setMapY(double mapY) {
		this.mapY = mapY;
	}
	
	public void setMovingDir(int movingDir) {
		this.movingDir = movingDir;
	}
}
