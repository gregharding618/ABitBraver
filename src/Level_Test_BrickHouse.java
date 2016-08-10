import java.awt.Color;
import java.io.Serializable;

public class Level_Test_BrickHouse extends Level implements Serializable {
	
	public Level_Test_BrickHouse(World world, String name) {
		this.world = world;
		//this.entities.add(this.world.currentPlayer);
		this.name = name;
		this.xMax = 48;
		this.yMax = 48;
		this.isOutside = false;
		
		Item[] stock = new Item[]{new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47)};
		this.getEntitiesToAdd().add(new Entity_NPC_ShopOwner(this, "Shop guy", 20, 35, 10 + 100, 10 + 100, 10 - 100, 10 - 100, stock));

		this.tiles = new Tile[4][4];
		for (int xx = 0; xx < this.tiles.length; xx++) {
			for (int yy = 0; yy < this.tiles[xx].length; yy++) {
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
