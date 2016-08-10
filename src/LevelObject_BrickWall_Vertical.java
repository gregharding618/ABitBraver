import java.io.Serializable;

public class LevelObject_BrickWall_Vertical extends LevelObject implements Serializable {
	
	public LevelObject_BrickWall_Vertical(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "brick_wall_vertical.png";
		this.isSolid = true;
		this.isWall = true;
		this.isLightSource = false;
		this.isVertical = true;
		setMapX(mapX);
		setMapY(mapY);
		
		//Set collision box
		this.minX = 2;
		this.maxX = 2;
		this.minY = 6;
		this.maxY = 6;
		this.trueHeight = 6; //Since maxY is always shorter than the object, use this for shouldRender checks
		///////////////////////////////////////////
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void move() {
		
	}

	@Override
	public void checkTimers() {
		
	}

}

