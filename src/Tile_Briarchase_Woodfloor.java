import java.awt.Color;
import java.io.Serializable;

public class Tile_Briarchase_Woodfloor extends Tile implements Serializable {
	
	public Tile_Briarchase_Woodfloor(Level level, double mapX, double mapY) {
		this.level = level;
		this.miniMapColor = new Color(222, 184, 135);
		this.walkable = true;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = level.tileSize;
		this.filename = "briarchase_woodfloor.png";
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
