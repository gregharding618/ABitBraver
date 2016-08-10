//import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class LevelObject_BrickWall extends LevelObject implements Serializable {
	
	public LevelObject_BrickWall(Level level, double mapX, double mapY) {
		this.level = level;
		this.imageFile = "brick_wall.png";
		this.isSolid = true;
		this.isWall = true;
		this.isLightSource = false;
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
