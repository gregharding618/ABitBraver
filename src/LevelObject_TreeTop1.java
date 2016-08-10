import java.io.Serializable;

public class LevelObject_TreeTop1 extends LevelObject implements Serializable {
	
	public LevelObject_TreeTop1(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "tree_top1.png";
		this.isSolid = false;
		this.isWall = false;
		this.isLightSource = false;
		setMapX(mapX);
		setMapY(mapY);
		
		//Set collision box
		this.minX = 3;
		this.maxX = 6;
		this.minY = 3;
		this.maxY = 6;
		this.trueHeight = 0; //Since maxY is always shorter than the object, use this for shouldRender checks
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
