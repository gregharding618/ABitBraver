import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class MiniMap implements Serializable {
	
	private static final long serialVersionUID = 374363949310313295L;
	
	public double x, y, range = 150; //150 in all directions around the player
	public Level level;
	public Player player;
	
	public double mapX, mapY;
	
	public final double size = 10;
	
	public MiniMap(Level level, Player player) {
		this.level = level;
		this.player = player;
		this.x = 89.75;
		this.y = 89.75;
		this.mapX = player.getMapX();
		this.mapY = player.getMapY();
	}
	
	public void update() {
		this.mapX = this.player.getMapX();
		this.mapY = this.player.getMapY();
	}
	
	public void render() {
		//Background
		StdDraw.setPenColor(new Color(0, 0, 0, 194));
		StdDraw.filledSquare(this.x, this.y, this.size);
		////////////////////////
		
		//Render ground
		for (int xx = 0; xx < this.level.tiles.length; xx++) {
			for (int yy = 0; yy < this.level.tiles[xx].length; yy++) {
				double minimapX = this.mapX - this.level.tiles[xx][yy].mapX; //X offset relative to minimap center
				double minimapY = this.mapY - this.level.tiles[xx][yy].mapY; //Y offset relative to minimap center
				
				double xxx = this.x - (minimapX / (this.range / this.size)); //Proper x coordinate on minimap
				double yyy = this.y - (minimapY / (this.range / this.size)); //Proper y coordinate on minimap
					
				this.level.tiles[xx][yy].miniMapRender(this, xxx, yyy, 0.51); //range 150, 0.51
			}
		}
		////////////////////////
		
		//Render tile overlays
		for (TileOverlay to : this.level.tileOverlays) {
			if (to instanceof TileOverlay_Path) {
				for (ArrayList<Double> coords : ((TileOverlay_Path)to).coordinates) {
					double minimapX = this.mapX - coords.get(0); //X offset relative to minimap center
					double minimapY = this.mapY - coords.get(1); //Y offset relative to minimap center
					
					if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
						double xx = this.x - (minimapX / (this.range / this.size)); //Proper x coordinate on minimap
						double yy = this.y - (minimapY / (this.range / this.size)); //Proper y coordinate on minimap
						
						to.minimapRender(xx, yy, ((TileOverlay_Path) to).imageSeparationDistance / (this.size * 2));
					}
				}
			} else {
				System.out.println("ERROR: Unimplemented tile overlay render for minimap being called for " + to.getClass());
			}
		}
		////////////////////////
		
		//Render dropped items
		for (DroppedItem item : new ArrayList<>(this.level.getDroppedItems())) {
			double minimapX = this.mapX - item.mapX; //X offset relative to minimap center
			double minimapY = this.mapY - item.mapY; //Y offset relative to minimap center
			
			if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
				double xx = this.x - (minimapX / (this.range / this.size)); //Proper x coordinate on minimap
				double yy = this.y - (minimapY / (this.range / this.size)); //Proper y coordinate on minimap
				
				item.miniMapRender(xx, yy, 0.33); //range 150, 0.33
			}
		}
		////////////////////////
		
		//Render entities
		for (Entity e : new ArrayList<>(this.level.getEntities())) {
			double minimapX = this.mapX - e.getMapX(); //X offset relative to minimap center
			double minimapY = this.mapY - e.getMapY(); //Y offset relative to minimap center
			
			double xx = this.x - (minimapX / (this.range / this.size)); //Proper x coordinate on minimap
			double yy = this.y - (minimapY / (this.range / this.size)); //Proper y coordinate on minimap
				
			if (e instanceof LevelObject && ((LevelObject)e).isWall) {
				e.miniMapRender(this, xx, yy, this.size);
					
			} else if (e instanceof LevelConnector) {
				if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
					e.miniMapRender(this, xx, yy, 1);
				}
					
			} else {
				if (!(minimapX < -this.range || minimapX > this.range || minimapY < -this.range || minimapY > this.range)) {
					e.miniMapRender(this, xx, yy, 0.33); //range 150, 0.33
				}
			}
		}
		////////////////////////
		
		//Border
		StdDraw.setPenColor(new Color(177, 177, 190));
		StdDraw.setPenRadius(0.008);
		StdDraw.square(this.x, this.y, this.size);
		StdDraw.setPenRadius();
		////////////////////////
	}

}
