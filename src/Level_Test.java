import java.awt.Color;
import java.io.Serializable;

public class Level_Test extends Level implements Serializable {
	
	private static final long serialVersionUID = 676484830365553677L;
	
	public Level_Test(World world, String name, double xMax, double yMax) {
		this.world = world;
		//this.entities.add(this.world.currentPlayer);
		this.name = name;
		this.xMax = xMax;
		this.yMax = yMax;
		this.isOutside = true;
		
		//this.entitiesToAdd.add(new Entity_NPC_Test(this, true, true, "Enemy", "Enemy", 22, 29, 22 + 25, 29 + 26, 22 - 10, 29 - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
		this.getEntitiesToAdd().add(new Entity_NPC_Test(this, true, true, "Player", "Friendly", 22, 36, 22 + 25, 36 + 26, 22 - 10, 36 - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
		this.getEntitiesToAdd().add(new Entity_NPC_Test(this, false, false, "NPC", "NPC", 37, 29, 37 + 25, 29 + 26, 37 - 10, 29 - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
		this.getEntitiesToAdd().add(new Entity_NPC_DialogueTest(this, "this guy", "Albert", 76, 39, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
		
		this.getEntitiesToAdd().add(new LevelObject_BrickWall_Horizontal(this, 39, 95));
		this.getEntitiesToAdd().add(new LevelObject_BrickWall_Vertical(this, 65.5, 105));
		this.getEntitiesToAdd().add(new LevelObject_BrickHouse(this, 114, 62));

		//House door and inside house with door
		Level brickHouse = new Level_Test_BrickHouse(this.world, "old brick house");
		this.getEntitiesToAdd().add(new LevelConnector(this, brickHouse, "olddoor.png", 114, 47.2, 18, 5, 5, 12));
		this.world.levels.add(brickHouse);
		brickHouse.getEntitiesToAdd().add(new LevelConnector(brickHouse, this, "olddoor.png", 18, -2, 114, 43, 5, 12));
		//////////////////////////////
		
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
				
				//TEMPORARY DO NOT REUSE
				else if (yy == 15) {
					this.tiles[xx][yy] = new Tile_Spikes(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				
				else if (yy == 16) {
					this.tiles[xx][yy] = new Tile_Mud(this, this.tileSize * 2 * xx, this.tileSize * 2 * yy);
				}
				///////////////////////////////////////

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
		//StdDraw.setPenColor(new Color(176, 176, 176));
		//StdDraw.filledRectangle(this.xMax / 2, this.yMax / 2, this.xMax, this.yMax);
		
		super.render();
	}

}
