import java.awt.Color;
import java.io.Serializable;

public class Tile_Mud extends Tile implements Serializable {
	
	public Tile_Mud(Level level, double mapX, double mapY) {
		this.level = level;
		this.miniMapColor = new Color(142, 96, 0);
		this.walkable = true;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = level.tileSize;
		this.filename = "mud_tile.png";
	}
	
	@Override
	public void update() {
		super.update();
		
		//Slow down entities
		for (Entity entity : this.level.getEntities()) {
			if (entity.getMapX() >= this.mapX - this.size && entity.getMapX() <= this.mapX + this.size && 
				entity.getMapY() - entity.minY >= this.mapY - this.size && entity.getMapY() <= this.mapY + this.size) {
				
				entity.speed /= 2;
			}
		}
		//////////////////////
	}
	
	@Override
	public void render() {
		super.render();
	}

}
