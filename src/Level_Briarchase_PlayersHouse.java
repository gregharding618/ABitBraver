import java.io.Serializable;

public class Level_Briarchase_PlayersHouse extends Level implements Serializable {
	
	public Level_Briarchase_PlayersHouse(World world, String name) {
		this.world = world;
		//this.entities.add(this.world.currentPlayer);
		this.name = name;
		this.xMax = 45;
		this.yMax = 45;
		this.isOutside = false;
		
		this.tiles = new Tile[(int) ((this.xMax / (this.tileSize * 2)) + 2)][(int) ((this.yMax / (this.tileSize * 2)) + 2)];
		for (int xx = 0; xx < this.tiles.length; xx++) {
			for (int yy = 0; yy < this.tiles[xx].length; yy++) {
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
		}
		
		//Objects
		this.getEntitiesToAdd().add(new LevelObject_Candle(this, 50, 4));
		/////////////////////
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
