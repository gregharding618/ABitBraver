import java.awt.Color;
import java.io.Serializable;

public abstract class Tile implements Serializable {
	
	private static final long serialVersionUID = 8751174992294306210L;
	
	public Level level;
	public double x, y, mapX, mapY, size;
	public String filename;
	public boolean shouldRender = false, walkable, causesDamage = false;
	public Color miniMapColor;
	
	public void update() {
		if (this.level.world.currentPlayer.level.equals(this.level)) {
			setRenderCoordinates();
			if (!(this.x + this.size < 0) && !(this.x - this.size > 100) && !(this.y + this.size < 0) && !(this.y - this.size > 100)) {
				this.shouldRender = true;
			} else {
				this.shouldRender = false;
			}
		} else {
			this.shouldRender = false;
		}
	}

	public void render() {
		StdDraw.picture(this.x, this.y, this.filename);
	}
	
	@SuppressWarnings("unused")
	public void miniMapRender(MiniMap minimap, double x, double y, double size) {
		if (minimap == null) {
			StdDraw.setPenColor(this.miniMapColor);
			StdDraw.filledSquare(x, y, size);
			return;
		}
		
		if (x + size <= minimap.x - minimap.size || x - size >= minimap.x + minimap.size || y + size <= minimap.y - minimap.size || y - size >= minimap.y + minimap.size) {
			return;
		}
		
		StdDraw.setPenColor(this.miniMapColor);
		
		if (x - size < minimap.x - minimap.size && y - size < minimap.y - minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{minimap.x - minimap.size, minimap.x - minimap.size, x + size, x + size};
			yCoords = new double[]{y + size, minimap.y - minimap.size, minimap.y - minimap.size, y + size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
		}
		
		else if (x + size > minimap.x + minimap.size && y - size < minimap.y - minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{x - size, x - size, minimap.x + minimap.size, minimap.x + minimap.size};
			yCoords = new double[]{y + size, minimap.y - minimap.size, minimap.y - minimap.size, y + size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
		}
		
		else if (x + size > minimap.x + minimap.size && y + size > minimap.y + minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{x - size, x - size, minimap.x + minimap.size, minimap.x + minimap.size};
			yCoords = new double[]{minimap.y + minimap.size, y - size, y - size, minimap.y + minimap.size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
		}
		
		else if (x - size < minimap.x - minimap.size && y + size > minimap.y + minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{minimap.x - minimap.size, minimap.x - minimap.size, x + size, x + size};
			yCoords = new double[]{minimap.y + minimap.size, y - size, y - size, minimap.y + minimap.size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
		}
		
		else if (x - size < minimap.x - minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{minimap.x - minimap.size, minimap.x - minimap.size, x + size, x + size};
			yCoords = new double[]{y + size, y - size, y - size, y + size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
			
		} 
		
		else if (x + size > minimap.x + minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{x - size, x - size, minimap.x + minimap.size, minimap.x + minimap.size};
			yCoords = new double[]{y + size, y - size, y - size, y + size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
			
		} 
		
		else if (y - size < minimap.y - minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{x - size, x - size, x + size, x + size};
			yCoords = new double[]{y + size, minimap.y - minimap.size, minimap.y - minimap.size, y + size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
			
		} 
		
		else if (y + size > minimap.y + minimap.size) {
			double[] xCoords, yCoords;
			
			xCoords = new double[]{x - size, x - size, x + size, x + size};
			yCoords = new double[]{minimap.y + minimap.size, y - size, y - size, minimap.y + minimap.size};
			
			StdDraw.filledPolygon(xCoords, yCoords);
			
		} 
		
		else {
			StdDraw.filledSquare(x, y, size);
		}
	}

	private void setRenderCoordinates() {
		this.x = Game.player.x + (this.mapX - Game.player.getMapX());
		this.y = Game.player.y + (this.mapY - Game.player.getMapY());
	}
	
	private void causeDamage() {
		
	}

}
