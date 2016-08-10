import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class OverheadDamage implements Serializable {
	
	public World world;
	public Level level;
	public double x, y, mapX, mapY, speed;
	public Entity_Character attacker, target;
	public boolean shouldRender = false;
	
	private int timeLeft = 135, damage;
	
	private double myXoffset, myYoffset, minimumSpeed = 0.065;
	private static double xOffset = 0.75, yOffset = 0.75;
	private Color colorToUse;
	
	public OverheadDamage(Level level, Entity_Character attacker, Entity_Character target, int damage) {
		this.level = level;
		this.mapX = target.getMapX();
		this.mapY = target.getMapY() + target.maxY;
		this.attacker = attacker;
		this.target = target;
		this.damage = damage;
		
		this.speed = 2.575;
		if (xOffset > 0) xOffset = ThreadLocalRandom.current().nextDouble(0, 0.65) * -1;
		else xOffset = ThreadLocalRandom.current().nextDouble(0, 0.65);
		
		yOffset = ThreadLocalRandom.current().nextDouble(0.6, 1);
		
		this.myXoffset = xOffset;
		this.myYoffset = yOffset;
		
		if (this.attacker == null) this.colorToUse = StdDraw.RED;
		
		else if (this.attacker instanceof Player) {
			this.colorToUse = StdDraw.GREEN;
		}
	
		else if (this.attacker.faction.equals(Game.player.faction)) {
			this.colorToUse = StdDraw.YELLOW;
		}
	
		else {
			this.colorToUse = StdDraw.RED;
		}
		
		Packet011OverheadDamage overheadDamagePacket = new Packet011OverheadDamage(this.level.name, this.mapX, this.mapY, this.getRed(), this.getGreen(), this.getBlue(), this.getDamage());
		overheadDamagePacket.writeData(Game.socketClient);
	}
	
	public OverheadDamage(Level level, Entity_Character target, int damage) {
		this.level = level;
		this.mapX = target.getMapX();
		this.mapY = target.getMapY() + target.maxY;
		this.target = target;
		this.damage = damage;
		
		this.speed = 2.575;
		if (xOffset > 0) xOffset = ThreadLocalRandom.current().nextDouble(0, 0.65) * -1;
		else xOffset = ThreadLocalRandom.current().nextDouble(0, 0.65);
		
		yOffset = ThreadLocalRandom.current().nextDouble(0.6, 1);
		
		this.myXoffset = xOffset;
		this.myYoffset = yOffset;
		
		if (this.attacker == null) this.colorToUse = StdDraw.RED;
		
		else if (this.attacker instanceof Player) {
			this.colorToUse = StdDraw.GREEN;
		}
	
		else if (this.attacker.faction.equals(Game.player.faction)) {
			this.colorToUse = StdDraw.YELLOW;
		}
	
		else {
			this.colorToUse = StdDraw.RED;
		}
		
		Packet011OverheadDamage overheadDamagePacket = new Packet011OverheadDamage(this.level.name, this.mapX, this.mapY, this.getRed(), this.getGreen(), this.getBlue(), this.getDamage());
		overheadDamagePacket.writeData(Game.socketClient);
	}
	
	public OverheadDamage(Level level, double mapX, double mapY, int red, int green, int blue, int damage) {
		this.level = level;
		this.mapX = mapX;
		this.mapY = mapY;
		this.colorToUse = new Color(red, green, blue);
		this.damage = damage;
		
		this.speed = 2.575;
		if (xOffset > 0) xOffset = ThreadLocalRandom.current().nextDouble(0, 0.65) * -1;
		else xOffset = ThreadLocalRandom.current().nextDouble(0, 0.65);
		
		yOffset = ThreadLocalRandom.current().nextDouble(0.6, 1);
		
		this.myXoffset = xOffset;
		this.myYoffset = yOffset;
	}

	public void update() {
		this.timeLeft--;
		if (this.speed > this.minimumSpeed) this.speed -= 1 / this.speed;
		if (this.speed < this.minimumSpeed) this.speed = this.minimumSpeed;
		move();
		
		if (Game.player.level.equals(this.level)) {
		if (((this.mapX < Game.player.getMapX() && this.mapX >= Game.player.getMapX() - 101) || (this.mapX > Game.player.getMapX() && this.mapX <= Game.player.getMapX() + 101) || this.mapX == Game.player.getMapX()) &&
				((this.mapY < Game.player.getMapY() && this.mapY >= Game.player.getMapY() - 101) || (this.mapY > Game.player.getMapY() && this.mapY <= Game.player.getMapY() + 101) || this.mapY == Game.player.getMapY())) {
				this.shouldRender = true;
				setRenderCoordinates();
			} else {
				this.shouldRender = false;
			}
		} else {
			this.shouldRender = false;
		}
		
		if (this.timeLeft <= 0) {
			this.level.overheadDamageToRemove.add(this);
		}
	}

	public void render() {
		if (this.shouldRender) {
			StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(this.x + 0.14, this.y - 0.12, this.damage + "");
			
			StdDraw.setPenColor(this.colorToUse);
			
			StdDraw.text(this.x, this.y, this.damage + "");
		}
	}

	public void move() {
		this.mapX += this.speed * this.myXoffset;
		this.mapY += this.speed * this.myYoffset;
	}
	
	protected void setRenderCoordinates() {
		this.x = Game.player.x + (this.mapX - Game.player.getMapX());
		this.y = Game.player.y + (this.mapY - Game.player.getMapY());
	}
	
	public int getRed() {
		return this.colorToUse.getRed();
	}
	
	public int getGreen() {
		return this.colorToUse.getGreen();
	}
	
	public int getBlue() {
		return this.colorToUse.getBlue();
	}

	public int getDamage() {
		return this.damage;
	}

}
