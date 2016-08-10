import java.awt.Color;
import java.io.Serializable;

public class Level_Briarchase extends Level implements Serializable {
	
	public Level_Briarchase(World world, String name) {
		this.world = world;
		//this.entities.add(this.world.currentPlayer);
		this.name = name;
		this.xMax = 350;
		this.yMax = 350;
		this.isOutside = true;
		
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
		
		//Tile overlays
		this.tileOverlaysToAdd.add(new TileOverlay_Path(this, "dirtpath.png", new Color(87, 59, 12), new double[]{65, 65, 148}, new double[]{61, 44, 44}, 6));
		/////////////////
		
		//Objects
		this.getEntitiesToAdd().add(new LevelObject_BrickHouse(this, 65, 83.75));
		/////////////////
		
		//NPCs
		Item[] stock = new Item[]{new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment("dagger.png", "", "Dagger", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47)};
		this.getEntitiesToAdd().add(new Entity_NPC_ShopOwner(this, "Shop guy", 20, 35, 10 + 100, 10 + 100, 10 - 100, 10 - 100, stock));
		
		this.getEntitiesToAdd().add(new Entity_NPC_Test(this, true, true, "Player", "Test walker", 52, 35, 68, 55, 11, 11, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment("dagger.png", "", "Test Weapon2", false, 1, true, false, false, 7, 16, 47, 25, 31.23, 47, 21, 34, 23, 47), new Item_Equipment_EmptySpace()));
		/////////////////
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
