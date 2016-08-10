import java.awt.Color;

public class Tile_Animated extends Tile {
	
	private String imageFile1, imageFile2, imageFile3;
	private int imageChange = 0;
	
	public Tile_Animated(Level level, double mapX, double mapY, String imageFile1, String imageFile2, String imageFile3) {
		this.level = level;
		this.miniMapColor = new Color(0, 0, 200);
		this.walkable = true;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = level.tileSize;
		this.imageFile1 = imageFile1;
		this.imageFile2 = imageFile2;
		this.imageFile3 = imageFile3;
		this.filename = imageFile1;
	}
	
	@Override
	public void update() {
		super.update();
		
		
	}
	
	@Override
	public void render() {
		if (this.imageChange == 0) this.imageChange = 300;
		else this.imageChange--;
		
		if (this.imageChange >= 200) {
			StdDraw.picture(this.x, this.y, this.imageFile1);
		} 
		else if (this.imageChange > 100 && this.imageChange < 200 ) {
			StdDraw.picture(this.x, this.y, this.imageFile2);
		}
		else {
			StdDraw.picture(this.x, this.y, this.imageFile3);
		}
	}
}
