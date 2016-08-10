import java.io.Serializable;

public class LevelObject_BrickWall_Horizontal extends LevelObject implements Serializable {
	
	public LevelObject_BrickWall_Horizontal(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "brick_wall_horizontal.png";
		this.isSolid = true;
		this.isWall = true;
		this.isLightSource = false;
		this.isHorizontal = true;
		setMapX(mapX);
		setMapY(mapY);
		
		//Set collision box
		this.minX = 28.5;
		this.maxX = 28.5;
		this.minY = 6;
		this.maxY = 0;
		this.trueHeight = 13; //Since maxY is always shorter than the object, use this for shouldRender checks
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
