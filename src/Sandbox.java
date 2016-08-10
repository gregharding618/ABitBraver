import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Sandbox extends Level implements Serializable {
	
	private static final long serialVersionUID = -2741102015871252825L;
	
	public Sandbox(World world, String tileType, String name, double xMax, double yMax) {	
		this.world = world;
		this.getEntities().add(this.world.currentPlayer);
		this.name = name;
		this.xMax = xMax;
		this.yMax = yMax;
		this.isOutside = true;
				
		this.tiles = new Tile[(int) ((this.xMax / (this.tileSize * 2)) + 2)][(int) ((this.yMax / (this.tileSize * 2)) + 2)];
		determineInitialTileType(tileType);
	}
	
	private void determineInitialTileType(String tileType){
		int xx = 0;
		int yy = 0;
		Tile tile = null;

		for (xx = 0; xx < this.tiles.length; xx++) {
			for (yy = 0; yy < this.tiles[xx].length; yy++) {
				
				if (tileType.equals("Grass")) {	
					if (xx == 0 && yy == 0) {
						this.tiles[xx][yy] = new Tile_Grass(this, xx, yy);
					}

					else if (xx == 0) {
			        	this.tiles[xx][yy] = new Tile_Grass(this, xx, this.tileSize * 2 * yy);  
					}

					else if (yy == 0) {
						this.tiles[xx][yy] = new Tile_Grass(this, this.tileSize * 2 * xx, yy);  
					}

			        else this.tiles[xx][yy] = new Tile_Grass(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				else if (tileType.equals("Mud")){
					if (xx == 0 && yy == 0) {
						this.tiles[xx][yy] = new Tile_Mud(this, xx, yy);
					}

					else if (xx == 0) {
			        	this.tiles[xx][yy] = new Tile_Mud(this, xx, this.tileSize * 2 * yy);  
					}

					else if (yy == 0) {
						this.tiles[xx][yy] = new Tile_Mud(this, this.tileSize * 2 * xx, yy);  
					}

			        else this.tiles[xx][yy] = new Tile_Mud(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				else if (tileType.equals("Blank")){
					if (xx == 0 && yy == 0) {
						this.tiles[xx][yy] = new Tile_Blank(this, xx, yy);
					}

					else if (xx == 0) {
			        	this.tiles[xx][yy] = new Tile_Blank(this, xx, this.tileSize * 2 * yy);  
					}

					else if (yy == 0) {
						this.tiles[xx][yy] = new Tile_Blank(this, this.tileSize * 2 * xx, yy);  
					}

			        else this.tiles[xx][yy] = new Tile_Blank(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				else if (tileType.equals("Briarchase Wood Floor")){
					if (xx == 0 && yy == 0) {
						this.tiles[xx][yy] = new Tile_Briarchase_Woodfloor(this, xx, yy);
					}

					else if (xx == 0) {
			        	this.tiles[xx][yy] = new Tile_Briarchase_Woodfloor(this, xx, this.tileSize * 2 * yy);  
					}

					else if (yy == 0) {
						this.tiles[xx][yy] = new Tile_Briarchase_Woodfloor(this, this.tileSize * 2 * xx, yy);  
					}

			        else this.tiles[xx][yy] = new Tile_Briarchase_Woodfloor(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				else if (tileType.equals("Wood Floor")){
					if (xx == 0 && yy == 0) {
						this.tiles[xx][yy] = new Tile_WoodFloor(this, xx, yy);
					}

					else if (xx == 0) {
			        	this.tiles[xx][yy] = new Tile_WoodFloor(this, xx, this.tileSize * 2 * yy);  
					}

					else if (yy == 0) {
						this.tiles[xx][yy] = new Tile_WoodFloor(this, this.tileSize * 2 * xx, yy);  
					}

			        else this.tiles[xx][yy] = new Tile_WoodFloor(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				else if (tileType.equals("Spikes")){
					if (xx == 0 && yy == 0) {
						this.tiles[xx][yy] = new Tile_Spikes(this, xx, yy);
					}

					else if (xx == 0) {
			        	this.tiles[xx][yy] = new Tile_Spikes(this, xx, this.tileSize * 2 * yy);  
					}

					else if (yy == 0) {
						this.tiles[xx][yy] = new Tile_Spikes(this, this.tileSize * 2 * xx, yy);  
					}

			        else this.tiles[xx][yy] = new Tile_Spikes(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				else {
					if (xx == 0 && yy == 0) {
						this.tiles[xx][yy] = new Tile_Grass(this, xx, yy);
					}

					else if (xx == 0) {
			        	this.tiles[xx][yy] = new Tile_Grass(this, xx, this.tileSize * 2 * yy);  
					}

					else if (yy == 0) {
						this.tiles[xx][yy] = new Tile_Grass(this, this.tileSize * 2 * xx, yy);  
					}

			        else this.tiles[xx][yy] = new Tile_Grass(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
			}
		}
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void render() {
		super.render();
	}
}
