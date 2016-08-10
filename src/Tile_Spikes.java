import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tile_Spikes extends Tile implements Serializable {
	
	private List<Entity> tempImmuneEntities = new ArrayList<Entity>();
	private List<Integer> immunityTimers = new ArrayList<Integer>();

	public Tile_Spikes(Level level, double mapX, double mapY){
		this.level = level;
		this.miniMapColor = new Color(17, 104, 70);
		this.walkable = true;
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = level.tileSize;
		this.filename = "grassSpikes.png";
	}
	
	@Override
	public void update() {
		super.update();
		
		//Damage entities standing on this tile
		for (Entity entity : this.level.getEntities()) {
			if (!(entity instanceof Entity_Character)) continue;
			if (this.tempImmuneEntities.contains(entity)) continue;
			
			if (((Entity_Character)entity).canBeAttacked && entity.getMapX() >= this.mapX - this.size && entity.getMapX() <= this.mapX + this.size && 
				entity.getMapY() - entity.minY >= this.mapY - this.size && entity.getMapY() <= this.mapY + this.size) {

				((Entity_Character)entity).currentHealth--;
				this.level.overheadDamageToAdd.add(new OverheadDamage(this.level, ((Entity_Character)entity), 1));
				this.tempImmuneEntities.add(entity);
				this.immunityTimers.add(100);
			}
		}
		////////////////////////
		
		//Update immune entities and their timers
		for (int i = 0; i < this.immunityTimers.size(); i++) {
			int timer = this.immunityTimers.get(i);
			if (timer > 0) this.immunityTimers.set(i, timer - 1);
			if (this.immunityTimers.get(i) <= 0) {
				this.immunityTimers.remove(i);
				this.tempImmuneEntities.remove(i);
			}
		}
		////////////////////////
	}
	
	@Override
	public void render() {
		super.render();
	}
}
