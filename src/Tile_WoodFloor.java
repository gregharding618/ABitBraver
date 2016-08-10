import java.awt.Color;
import java.io.Serializable;

public class Tile_WoodFloor extends Tile implements Serializable {
	
	public Tile_WoodFloor(Level level, double mapX, double mapY) {
		this.level = level;
		this.miniMapColor = new Color(165, 42, 42);
		this.walkable = true;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = level.tileSize;
		this.filename = "woodfloor.png";
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
