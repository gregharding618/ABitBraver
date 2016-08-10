import java.io.Serializable;

public class LevelObject_TreeBase1 extends LevelObject implements Serializable {
	
	public LevelObject_TreeBase1(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "tree_base1.png";
		this.isSolid = true;
		this.isWall = true;
		this.isLightSource = false;
		setMapX(mapX);
		setMapY(mapY);
		
		//Set collision box
		this.minX = 3;
		this.maxX = 3;
		this.minY = 3;
		this.maxY = 3;
		this.trueHeight = 3; //Since maxY is always shorter than the object, use this for shouldRender checks
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
