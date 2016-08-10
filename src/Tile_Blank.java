import java.awt.Color;
import java.io.Serializable;

public class Tile_Blank extends Tile implements Serializable {
	
	public Tile_Blank(Level level, double mapX, double mapY) {
		this.level = level;
		this.miniMapColor = StdDraw.BLACK;
		this.walkable = false;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = level.tileSize;
		this.filename = "blank_tile.png";
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void render() {
		super.render();
	}

}
