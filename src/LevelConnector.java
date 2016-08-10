import java.io.Serializable;

public class LevelConnector extends Entity implements Serializable {
	
	public double spawnX, spawnY, size, actionSize;
	public boolean shouldRender = false;
	public String imageFile;
	
	public Level level, destination;
	
	public LevelConnector(Level level, Level destination, String imageFile, double mapX, double mapY, double spawnX, double spawnY, double size, double actionSize) {
		this.level = level;
		this.destination = destination;
		this.imageFile = imageFile;
		setMapX(mapX);
		setMapY(mapY);
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.size = size;
		this.actionSize = actionSize;
		
		this.miniMapColor = StdDraw.ORANGE;
	}
	
	public void update() {
		if (Game.player.level.equals(this.level)) {
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
		StdDraw.picture(this.x, this.y, this.imageFile);
	}
	
	@Override
	public void miniMapRender(MiniMap minimap, double x, double y, double size) {
		StdDraw.setPenColor(this.miniMapColor);
		
		double[] xCoords, yCoords;
		
		xCoords = new double[]{x - (0.25 * size), x + (0.25 * size), x + (0.25 * size), x + (0.5 * size), x, x - (0.5 * size), x - (0.25 * size)};
		yCoords = new double[]{y - (0.5 * size), y - (0.5 * size), y, y, y + (0.5 * size), y, y};
		
		StdDraw.filledPolygon(xCoords, yCoords);
	}
	
	protected void setRenderCoordinates() {
		this.x = Game.player.x + (this.getMapX() - Game.player.getMapX());
		this.y = Game.player.y + (this.getMapY() - Game.player.getMapY());
	}

	@Override
	public void move() {
		
	}

	@Override
	public void checkTimers() {
		
	}

}
