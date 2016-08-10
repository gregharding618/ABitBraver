import java.io.Serializable;

public class LevelObject_Candle extends LevelObject implements Serializable {
	
	private String imageFile2 = "candle_2.png";
	private int imageChange = 0;
	
	public LevelObject_Candle(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "candle_1.png";
		this.isSolid = true;
		this.isWall = false;
		this.isLightSource = true;
		this.lightStrength = 2;
		this.lightRange = 23;
		setMapX(mapX);
		setMapY(mapY);
		
		//Set collision box
		this.minX = 0.18;
		this.maxX = 0.18;
		this.minY = 0.18;
		this.maxY = 0.18;
		this.trueHeight = 2; //Since maxY is always shorter than the object, use this for shouldRender checks
		///////////////////////////////////////////
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void render() {
		if (this.imageChange == 0) this.imageChange = 100;
		else this.imageChange--;
		
		if (this.imageChange >= 50) {
			StdDraw.picture(this.x, this.y, this.imageFile);
		} else {
			StdDraw.picture(this.x, this.y, this.imageFile2);
		}
	}

	@Override
	public void move() {
		
	}

	@Override
	public void checkTimers() {
		
	}

}
