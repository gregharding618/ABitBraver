import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;

public class LevelMap implements Serializable {
	
	public double x, y, mapX, mapY, range;
	
	public Level level;
	public Player player;
	
	private double zoom;
	private double maxZoom = 0.25;
	private double minZoom = 5;
	private boolean showControls = false;
	
	public LevelMap(Player player) {
		this.player = player;
		this.level = player.level;
		
		this.zoom = this.minZoom;
		
		this.x = 50;
		this.y = 50;
		this.mapX = player.getMapX();
		this.mapY = player.getMapY();
		
		this.range = (this.level.xMax > this.level.yMax) ? this.level.xMax : this.level.yMax;
	}
	
	public void update() {
		this.range = (this.level.xMax > this.level.yMax) ? this.level.xMax : this.level.yMax;
		
		checkInput();
	}

	public void render() {
		//Background
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledSquare(50, 50, 51);
		////////////////////////
		
		//Render ground
		double playerX = 0, playerY = 0;
		double limitToUse = (this.level.xMax > this.level.yMax) ? this.level.xMax : this.level.yMax;
		boolean foundPlayer = false;
		
		for (int xx = 0; xx < this.level.tiles.length; xx++) {
			for (int yy = 0; yy < this.level.tiles[xx].length; yy++) {
				double minimapX = (this.mapX - this.level.tiles[xx][yy].mapX) / this.zoom; //X offset relative to minimap center
				double minimapY = (this.mapY - this.level.tiles[xx][yy].mapY) / this.zoom; //Y offset relative to minimap center
				
				double xxx = this.x - (minimapX / (this.range / 100)); //Proper x coordinate on minimap
				double yyy = this.y - (minimapY / (this.range / 100)); //Proper y coordinate on minimap
					
				if (xxx > -7 && xxx < 107 && yyy > -7 && yyy < 107) this.level.tiles[xx][yy].miniMapRender(null, xxx, yyy, (720 / limitToUse) / this.zoom);
					
				//If in debug mode, move player to selected tile on map
				if (this.player instanceof Player_Sandbox) {
					if (StdDraw.mousePressed()) {
						Mouse.lastButtonPressed = -1;
							
						if (StdDraw.mouseX() >= xxx - (720 / limitToUse) / this.zoom &&
							StdDraw.mouseX() <= xxx + (720 / limitToUse) / this.zoom &&
							StdDraw.mouseY() >= yyy - (720 / limitToUse) / this.zoom &&
							StdDraw.mouseY() <= yyy + (720 / limitToUse) / this.zoom) {
								
							this.player.moveTo(this.level.tiles[xx][yy].mapX, this.level.tiles[xx][yy].mapY, this.player.getMovingDir());
							this.player.checkCollision();
							playerX = xxx;
							playerY = yyy;
							foundPlayer = true;
						}
					}
				}
				////////////////////////
					
				//Find player
				if (!foundPlayer) {
					if (this.player.getMapX() >= this.level.tiles[xx][yy].mapX - this.level.tileSize &&
						this.player.getMapX() <= this.level.tiles[xx][yy].mapX + this.level.tileSize &&
						this.player.getMapY() >= this.level.tiles[xx][yy].mapY - this.level.tileSize &&
						this.player.getMapY() <= this.level.tiles[xx][yy].mapY + this.level.tileSize) {
							
						playerX = xxx;
						playerY = yyy;
						foundPlayer = true;
					}
				}
				////////////////////////
			}
		}
		////////////////////////
		
		//Render tile overlays
		for (TileOverlay to : this.level.tileOverlays) {
			if (to instanceof TileOverlay_Path) {
				for (ArrayList<Double> coords : ((TileOverlay_Path)to).coordinates) {
					double minimapX = (this.mapX - coords.get(0)) / this.zoom; //X offset relative to minimap center
					double minimapY = (this.mapY - coords.get(1)) / this.zoom; //Y offset relative to minimap center
					
					if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
						double xx = this.x - (minimapX / (this.range / 100)); //Proper x coordinate on minimap
						double yy = this.y - (minimapY / (this.range / 100)); //Proper y coordinate on minimap
						
						to.minimapRender(xx, yy, ((((TileOverlay_Path) to).imageSeparationDistance * ((TileOverlay_Path) to).imageSeparationDistance * 10) / limitToUse) / this.zoom);
					}
				}
			} else {
				System.out.println("ERROR: Unimplemented tile overlay render for level map being called for " + to.getClass());
			}
		}
		////////////////////////
		
		//Render objects
		for (Entity e : this.level.getEntities()) {
			if (!(e instanceof LevelObject)) continue;
			
			double minimapX = (this.mapX - e.getMapX()) / this.zoom; //X offset relative to minimap center
			double minimapY = (this.mapY - e.getMapY()) / this.zoom; //Y offset relative to minimap center
		
			if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
				double xx = this.x - (minimapX / (this.range / 100)); //Proper x coordinate on minimap
				double yy = this.y - (minimapY / (this.range / 100)); //Proper y coordinate on minimap
			
				e.miniMapRender(null, xx, yy, (((this.level.xMax + this.level.yMax) * this.level.tileSize) / limitToUse) / this.zoom);
			}
		}
		////////////////////////
		
		//Render level connectors
		for (Entity e : this.level.getEntities()) {
			if (!(e instanceof LevelConnector)) continue;
			
			double minimapX = (this.mapX - e.getMapX()) / this.zoom; //X offset relative to minimap center
			double minimapY = (this.mapY - e.getMapY()) / this.zoom; //Y offset relative to minimap center
		
			if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
				double xx = this.x - (minimapX / (this.range / 100)); //Proper x coordinate on minimap
				double yy = this.y - (minimapY / (this.range / 100)); //Proper y coordinate on minimap
			
				e.miniMapRender(null, xx, yy, (1000 / limitToUse) / this.zoom);
			}
		}
		////////////////////////
		
		//Render dropped items if in debug mode
		if (this.player instanceof Player_Sandbox) {
			for (DroppedItem item : new ArrayList<>(this.level.getDroppedItems())) {
				double minimapX = (this.mapX - item.mapX) / this.zoom; //X offset relative to minimap center
				double minimapY = (this.mapY - item.mapY) / this.zoom; //Y offset relative to minimap center
			
				if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
					double xx = this.x - (minimapX / (this.range / 100)); //Proper x coordinate on minimap
					double yy = this.y - (minimapY / (this.range / 100)); //Proper y coordinate on minimap
				
					item.miniMapRender(xx, yy, (400 / limitToUse) / this.zoom);
				}
			}
		}
		////////////////////////
		
		//Render entities if in debug mode
		if (this.player instanceof Player_Sandbox) {
			for (Entity e : this.level.getEntities()) {
				if (e instanceof Player) continue;
				
				double minimapX = (this.mapX - e.getMapX()) / this.zoom; //X offset relative to minimap center
				double minimapY = (this.mapY - e.getMapY()) / this.zoom; //Y offset relative to minimap center
			
				if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
					double xx = this.x - (minimapX / (this.range / 100)); //Proper x coordinate on minimap
					double yy = this.y - (minimapY / (this.range / 100)); //Proper y coordinate on minimap
				
					e.miniMapRender(null, xx, yy, (455 / limitToUse) / this.zoom);
				}
			}
		}
		////////////////////////
		
		//Render player
		this.player.miniMapRender(null, playerX, playerY, (455 / limitToUse) / this.zoom);
		////////////////////////
		
		//Render controls
		StdDraw.setPenColor(StdDraw.MAGENTA);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
		if (this.showControls) {
			StdDraw.text(13, 97, "[SPACE] to hide controls");
			StdDraw.text(14, 94, "[M] or [ESC] to close map");
			StdDraw.text(13, 91, "[ WASD ] to move view");
			StdDraw.text(13, 88, "[E] to zoom out");
			StdDraw.text(13, 85, "[Q] to zoom in");
			StdDraw.text(13, 82, "IN DEBUG MODE:");
			StdDraw.text(13, 79, "Left-click to move player");
		} else {
			StdDraw.text(13, 97, "[SPACE] to show controls");
		}
		////////////////////////
	}
	
	private void checkInput() {
		//M or ESC - Close map
		if (this.player.buttonDelay == 0 && (StdDraw.isKeyPressed(KeyEvent.VK_M) || StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE))) {
			this.player.mapOpen = false;
			this.player.canAttack = true;
			this.player.buttonDelay = this.player.buttonDelayAmount;
		}
		////////////////////////
		
		//Space - Controls
		else if (this.player.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
			if (this.showControls) this.showControls = false;
			else this.showControls = true;
			this.player.buttonDelay = this.player.buttonDelayAmount;
		}
		////////////////////////
		
		//WASD - move view of map
		if (StdDraw.isKeyPressed(KeyEvent.VK_W)) {
			this.mapY += ((this.level.yMax / this.level.tileSize) / 8) / ((this.minZoom - Math.ceil(this.zoom) + 1));
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_S)) {
			this.mapY -= ((this.level.yMax / this.level.tileSize) / 8) / ((this.minZoom - Math.ceil(this.zoom) + 1));
		}
		
		if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			this.mapX += ((this.level.xMax / this.level.tileSize) / 8) / ((this.minZoom - Math.ceil(this.zoom) + 1));
		}
		
		else if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			this.mapX -= ((this.level.xMax / this.level.tileSize) / 8) / ((this.minZoom - Math.ceil(this.zoom) + 1));
		}
		////////////////////////
		
		//E and Q - zoom out and in
		if (this.player.buttonDelay == 0) {
			if (StdDraw.isKeyPressed(KeyEvent.VK_E)) {
				if (this.zoom < this.minZoom) {
					this.zoom += 0.25;
					this.player.buttonDelay = this.player.buttonDelayAmount;
				}
			}
		
			else if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
				if (this.zoom > this.maxZoom) {
					this.zoom -= 0.25;
					this.player.buttonDelay = this.player.buttonDelayAmount;
				}
			}
		}
		////////////////////////
	}

}
