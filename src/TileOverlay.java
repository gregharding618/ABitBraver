import java.awt.Color;
import java.io.Serializable;

public abstract class TileOverlay implements Serializable {
	
	public World world;
	public Level level;
	public double[] xCoords, yCoords;
	public double[] mapXCoords, mapYCoords;
	public boolean shouldRender = false;
	public String imageFile;
	public Color miniMapColor;
	
	public void update() {
		this.shouldRender = false;
		if (Game.player.level.equals(this.level)) {
			setRenderCoordinates();
			for (int i = 0; i < this.xCoords.length; i++) {
				if (this.xCoords[i] > -1 && this.xCoords[i] < 101 && this.yCoords[i] > -1 && this.yCoords[i] < 101) {
					this.shouldRender = true;
					break;
				}
			}
		}
	}
	
	public abstract void render();
	public abstract void minimapRender(double x, double y, double size);
	
	private void setRenderCoordinates() {
		for (int x = 0; x < this.xCoords.length; x++) {
			this.xCoords[x] = Game.player.x + (this.mapXCoords[x] - Game.player.getMapX());
		}
		
		for (int y = 0; y < this.yCoords.length; y++) {
			this.yCoords[y] = Game.player.y + (this.mapYCoords[y] - Game.player.getMapY());
		}
	}

}
