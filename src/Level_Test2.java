import java.awt.Color;
import java.io.Serializable;

public class Level_Test2 extends Level implements Serializable {
	
	private static final long serialVersionUID = 676484830365553677L;
	
	public Level_Test2(World world, String name, double xMax, double yMax) {
		this.world = world;
		//this.entities.add(this.world.currentPlayer);
		this.name = name;
		this.xMax = xMax;
		this.yMax = yMax;
		this.isOutside = true;
		
		this.getEntitiesToAdd().add(new Entity_NPC_Test(this, true, true, "Enemy", "Enemy", 22, 29, 22 + 25, 29 + 26, 22 - 10, 29 - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
		
		this.tiles = new Tile[(int) ((this.xMax / (this.tileSize * 2)) + 2)][(int) ((this.yMax / (this.tileSize * 2)) + 2)];
		for (int xx = 0; xx < this.tiles.length; xx++) {
			for (int yy = 0; yy < this.tiles[xx].length; yy++) {
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

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void render() {
		//StdDraw.setPenColor(new Color(199, 188, 188));
		//StdDraw.filledRectangle(this.xMax / 2, this.yMax / 2, this.xMax, this.yMax);
		
		super.render();
	}

}
