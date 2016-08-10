import java.io.Serializable;

public class LevelObject_Rock1 extends LevelObject implements Serializable {
	
	public LevelObject_Rock1(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "rock_1.png";
		this.isSolid = true;
		this.isWall = true;
		this.isLightSource = false;
		setMapX(mapX);
		setMapY(mapY);
		
		//Set collision box
		this.minX = 6;
		this.maxX = 10;
		this.minY = 6;
		this.maxY = 10;
		this.trueHeight = 10; //Since maxY is always shorter than the object, use this for shouldRender checks
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
