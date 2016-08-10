import java.io.Serializable;

public class Time implements Serializable {
	
	public World world;
	
	private int lightLevel = 0; //0 = max brightness, 255 = max darkness
	
	private int sunPosition = 0; //0 = day, 1 = sunset, 2 = night, 3 = sunrise
	
	private final double sunriseStart;
	private final double sunriseEnd;
	private final double sunsetStart;
	private final double sunsetEnd;
	
	private final int DAY_LENGTH;
	private int timeOfDay;
	private int ticks = 0;
	
	public Time(World world, int dayLength, int timeOfDay, double sunriseStart, double sunriseEnd, double sunsetStart, double sunsetEnd) {
		this.world = world;
		this.DAY_LENGTH = dayLength;
		this.timeOfDay = timeOfDay;
		this.sunriseStart = sunriseStart;
		this.sunriseEnd = sunriseEnd;
		this.sunsetStart = sunsetStart;
		this.sunsetEnd = sunsetEnd;
	}
	
	public void update() {
		if (Game.socketServer != null) {
			this.ticks++;
			if (this.ticks >= 250) {
				this.ticks = 0;
				Packet006Time packet = new Packet006Time(this.timeOfDay);
				packet.writeData(Game.socketServer);
			}
		}
		
		updateLightLevel();
		
		//Update game time
		this.timeOfDay++;
		if (this.timeOfDay > this.DAY_LENGTH) this.timeOfDay = 0;
		/////////////////////////
	}
	
	private void updateLightLevel() {
		if (this.world.debugmode) {
			this.lightLevel = 0;
			this.sunPosition = 0;
			this.timeOfDay = (int) ((this.DAY_LENGTH * this.sunriseEnd) + 280);
			return;
		}
		
		if (this.world.currentPlayer.level.isOutside) {
		
			this.lightLevel = 205; //Max natural outside darkness
			for (double percent = this.sunriseStart; percent <= this.sunriseEnd; percent += ((this.sunriseEnd - this.sunriseStart) / 205)) {
				if (this.timeOfDay >= this.DAY_LENGTH * percent) this.lightLevel--;
				else break;
				
				if (this.lightLevel <= 0) break;
			}
			if (this.timeOfDay > this.DAY_LENGTH * this.sunsetStart) {
				for (double percent = this.sunsetStart; percent <= this.sunsetEnd; percent += ((this.sunsetEnd - this.sunsetStart) / 205)) {
					if (this.timeOfDay >= this.DAY_LENGTH * percent) this.lightLevel++;
					else break;
					
					if (this.lightLevel >= 205) break;
				}
			}
			
		} else {
			this.lightLevel = 238; //Max natural inside darkness
		}
		
		//Update sun position
		if (this.timeOfDay >= this.DAY_LENGTH * this.sunsetStart && this.timeOfDay <= this.DAY_LENGTH * this.sunsetEnd) {
			this.sunPosition = 1;
		}
		
		else if (this.timeOfDay >= this.DAY_LENGTH * this.sunriseStart && this.timeOfDay <= this.DAY_LENGTH * this.sunriseEnd) {
			this.sunPosition = 3;
		}
		
		else if (this.timeOfDay > this.DAY_LENGTH * this.sunsetEnd || this.timeOfDay < this.DAY_LENGTH * this.sunriseStart) {
			this.sunPosition = 2;
		}
		
		else this.sunPosition = 0;
		/////////////////////////
	}
	
	public void normalizeLightLevel() {
		if (this.lightLevel < 0) this.lightLevel = 0;
		else if (this.lightLevel > 255) this.lightLevel = 255;
	}
	
	public int getLightLevel() {
		return this.lightLevel;
	}
	
	public int getDaylength() {
		return this.DAY_LENGTH;
	}
	
	public int getTimeOfDay() {
		return this.timeOfDay;
	}
	
	public int getSunPosition() {
		return this.sunPosition;
	}
	
	public double getSunriseStart() {
		return this.sunriseStart;
	}
	
	public double getSunriseEnd() {
		return this.sunriseEnd;
	}
	
	public double getSunsetStart() {
		return this.sunsetStart;
	}
	
	public double getSunsetEnd() {
		return this.sunsetEnd;
	}
	
	public void setTimeOfDay(int time) {
		this.timeOfDay = time;
	}
	
	public void setLightLevel(int light) {
		this.lightLevel = light;
	}

}
