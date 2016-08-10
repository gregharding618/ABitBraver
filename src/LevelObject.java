import java.io.Serializable;

public abstract class LevelObject extends Entity implements Serializable {
	
	private static final long serialVersionUID = -8985505639506226614L;
	
	public String imageFile;
	public boolean isWall, isLightSource, isVertical = false, isHorizontal = false, shouldRender = false;
	public double trueHeight; //Since maxY is always shorter than the object, use this for shouldRender checks
	public int lightStrength; //1 is weakest, 255 is strongest
	public double lightRange;
	
	public void update() {
		if (Game.player.level.equals(this.level)) {
			setRenderCoordinates();
			if (!(this.x + this.maxX < 0) && !(this.x - this.minX > 100) && !(this.y + this.trueHeight < 0) && !(this.y - this.minY > 100)) {
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
		StdDraw.setPenColor(StdDraw.WHITE);
		
		//Render white box on minimap
		double halfWidth = ((this.minX / (size + (this.level.tileSize * 3.5))) + (this.maxX / (size + (this.level.tileSize * 3.5))));
		double halfHeight = ((this.minY / (size + (this.level.tileSize * 3.5))) + (this.maxY / (size + (this.level.tileSize * 3.5))));

		if (minimap != null) {
			if (x - halfWidth < minimap.x - size && y - halfHeight < minimap.y - size) {
				if (x + halfWidth > minimap.x - size && y + halfHeight > minimap.y - size) {
					double[] xCoords, yCoords;
				
					xCoords = new double[]{minimap.x - size, minimap.x - size, x + halfWidth, x + halfWidth};
					yCoords = new double[]{y + halfHeight, minimap.y - size, minimap.y - size, y + halfHeight};
				
					StdDraw.polygon(xCoords, yCoords);
				}
			}
		
			else if (x - halfWidth < minimap.x - size) {
				if (x + halfWidth > minimap.x - size) {
					double[] xCoords, yCoords;
				
					xCoords = new double[]{minimap.x - size, minimap.x - size, x + halfWidth, x + halfWidth};
					yCoords = new double[]{y + halfHeight, y - halfHeight, y - halfHeight, y + halfHeight};
				
					StdDraw.polygon(xCoords, yCoords);
				}
			}
		
			else if (y - halfHeight < minimap.y - size) {
				if (y + halfHeight > minimap.y - size) {
					double[] xCoords, yCoords;
				
					xCoords = new double[]{x + halfWidth, x - halfWidth, x - halfWidth, x + halfWidth};
					yCoords = new double[]{y + halfHeight, y + halfHeight, minimap.y - size, minimap.y - size};
				
					StdDraw.polygon(xCoords, yCoords);
				}
			}
			
			else {
				StdDraw.rectangle(x, y, halfWidth, halfHeight);
			}
		}
		
		else {
			StdDraw.rectangle(x, y, halfWidth * (size / 7), halfHeight * (size / 7));
		}
		/////////////////////////////////////
	}

	protected void setRenderCoordinates() {
		this.x = Game.player.x + (this.getMapX() - Game.player.getMapX());
		this.y = Game.player.y + (this.getMapY() - Game.player.getMapY());
	}
}
