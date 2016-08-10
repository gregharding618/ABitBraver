import java.io.Serializable;

public class LevelObject_BrickHouse extends LevelObject implements Serializable {
	
	public LevelObject_BrickHouse(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "house_noalpha.png";
		this.isSolid = true;
		this.isWall = true;
		this.isLightSource = false;
		setMapX(mapX);
		setMapY(mapY);
		
		//Set collision box
		this.minX = 28.5;
		this.maxX = 28.5;
		this.minY = 16;
		this.maxY = 21;
		this.trueHeight = 26; //Since maxY is always shorter than the object, use this for shouldRender checks
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