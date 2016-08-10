import java.awt.Color;
import java.io.Serializable;

public class Tile_Grass extends Tile implements Serializable {
	
	public Tile_Grass(Level level, double mapX, double mapY) {
		this.level = level;
		this.miniMapColor = new Color(0, 104, 10);
		this.walkable = true;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = level.tileSize;
		this.filename = "grass.png";
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
