import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Weather implements Serializable {
	
	public World world;
	
	private int currentWeather; //0 = clear, 1 = slightly cloudy, 2 = cloudy, 3 = rain, 4 = lightning
	
	private int currentWeatherTimer;
	private final int minimumWeatherTime; //12500
	private final int maximumWeatherTime; //70000
	
	public int lightningTimer = 0;
	
	private List<ArrayList<Double>> clouds = new ArrayList<ArrayList<Double>>();
	private List<ArrayList<Double>> rain = new ArrayList<ArrayList<Double>>();
	private List<ArrayList<Double>> rainSplashes = new ArrayList<ArrayList<Double>>();
	
	private Random random = new Random();
	
	public Player player;
	
	public Weather(World world, int currentWeather, int currentWeatherTimer, int minimumWeatherTime, int maximumWeatherTime) {
		this.world = world;
		this.currentWeather = currentWeather;
		this.currentWeatherTimer = currentWeatherTimer;
		this.minimumWeatherTime = minimumWeatherTime;
		this.maximumWeatherTime = maximumWeatherTime;
	}
	
	public void update() {
		if (this.player == null) {
			this.player = this.world.currentPlayer;
		}
		
		updateTimers();
		
		if (this.world.debugmode) {
			currentWeather = 0;
		}
		
		else if (Game.socketServer != null && Game.player.port > -1 && currentWeatherTimer == 0) {
			setWeather();
		}
		
		updateWeather();
	}

	public void render() {
		//Clear - no code needed
		////////////////////
		
		//Slightly cloudy
		if (this.currentWeather == 1) {
			if (this.world.time.getLightLevel() < 31) StdDraw.setPenColor(new Color(0, 0, 0, 31));
			else {
				int alpha = 62 - this.world.time.getLightLevel();
				if (alpha < 0) alpha = 0;
				StdDraw.setPenColor(new Color(0, 0, 0, alpha));
			}
			
			for (ArrayList<Double> list : this.clouds) {
				double[] renderCoords = setRenderCoordinates(list.get(0), list.get(1));
				if (renderCoords[0] + 5 > -1 && renderCoords[0] - 5 < 101 && renderCoords[1] + 5 > -1 && renderCoords[1] - 5 < 101) {
					//StdDraw.filledEllipse(renderCoords[0], renderCoords[1], 5, 2.5);
					StdDraw.picture(renderCoords[0], renderCoords[1], "Cloud.png");
				}
			}
		}
		////////////////////
		
		//Cloudy
		else if (this.currentWeather == 2) {
			if (this.world.time.getLightLevel() < 48) StdDraw.setPenColor(new Color(0, 0, 0, 48));
			else {
				int alpha = 96 - this.world.time.getLightLevel();
				if (alpha < 0) alpha = 0;
				StdDraw.setPenColor(new Color(0, 0, 0, alpha));
			}
			StdDraw.filledSquare(50, 50, 51);
		}
		////////////////////
		
		//Rain
		else if (this.currentWeather == 3) {
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.setPenRadius(0.0035);
			for (ArrayList<Double> list : rain) {
				double[] renderCoords = setRenderCoordinates(list.get(0), list.get(1));

				if (renderCoords[0] + 0.15 > -1 && renderCoords[0] - 0.15 < 101 && renderCoords[1] + 0.5 > -1 && renderCoords[1] - 0.5 < 101) {
					StdDraw.line(renderCoords[0] - 0.15, renderCoords[1] - 0.5, renderCoords[0] + 0.15, renderCoords[1] + 0.5);
				}
			}
			StdDraw.setPenRadius();
			
			for (ArrayList<Double> list : this.rainSplashes) {
				double[] renderCoords = setRenderCoordinates(list.get(0), list.get(1));

				if (renderCoords[0] + 0.2 > -1 && renderCoords[0] - 0.2 < 101 && renderCoords[1] + 0.4 > -1 && renderCoords[1] - 0.4 < 101) {
					StdDraw.line(renderCoords[0] + (1 - list.get(2)), renderCoords[1] + (1 - list.get(2)), renderCoords[0] + 0.2 + (1 - list.get(2)), renderCoords[1] + 0.4 + (1 - list.get(2)));
					StdDraw.line(renderCoords[0] - (1 - list.get(2)), renderCoords[1] + (1 - list.get(2)), renderCoords[0] - 0.2 - (1 - list.get(2)), renderCoords[1] + 0.4 + (1 - list.get(2)));
				}
			}
			
			if (this.world.time.getLightLevel() < 48) StdDraw.setPenColor(new Color(0, 0, 0, 48));
			else {
				int alpha = 96 - this.world.time.getLightLevel();
				if (alpha < 0) alpha = 0;
				StdDraw.setPenColor(new Color(0, 0, 0, alpha));
			}
			StdDraw.filledSquare(50, 50, 51);
		}
		////////////////////
		
		//Lightning
		else if (this.currentWeather == 4) {
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.setPenRadius(0.0035);
			for (ArrayList<Double> list : rain) {
				double[] renderCoords = setRenderCoordinates(list.get(0), list.get(1));

				if (renderCoords[0] + 0.15 > -1 && renderCoords[0] - 0.15 < 101 && renderCoords[1] + 0.5 > -1 && renderCoords[1] - 0.5 < 101) {
					StdDraw.line(renderCoords[0] - 0.15, renderCoords[1] - 0.5, renderCoords[0] + 0.15, renderCoords[1] + 0.5);
				}
			}
			StdDraw.setPenRadius();
			
			for (ArrayList<Double> list : this.rainSplashes) {
				double[] renderCoords = setRenderCoordinates(list.get(0), list.get(1));

				if (renderCoords[0] + 0.2 > -1 && renderCoords[0] - 0.2 < 101 && renderCoords[1] + 0.4 > -1 && renderCoords[1] - 0.4 < 101) {
					StdDraw.line(renderCoords[0] + (1 - list.get(2)), renderCoords[1] + (1 - list.get(2)), renderCoords[0] + 0.2 + (1 - list.get(2)), renderCoords[1] + 0.4 + (1 - list.get(2)));
					StdDraw.line(renderCoords[0] - (1 - list.get(2)), renderCoords[1] + (1 - list.get(2)), renderCoords[0] - 0.2 - (1 - list.get(2)), renderCoords[1] + 0.4 + (1 - list.get(2)));
				}
			}
			
			if (this.world.time.getLightLevel() < 48) StdDraw.setPenColor(new Color(0, 0, 0, 48));
			else {
				int alpha = 96 - this.world.time.getLightLevel();
				if (alpha < 0) alpha = 0;
				StdDraw.setPenColor(new Color(0, 0, 0, alpha));
			}
			
			StdDraw.filledSquare(50, 50, 51);
		}
		////////////////////
	}
	
	private void setWeather() {
		if (this.currentWeather == 3 || this.currentWeather == 4) {
			if (this.random.nextInt(5) == 1) {
				this.clouds.clear();
				this.rain.clear();
				this.currentWeather = 2;
			} else if (this.random.nextInt(4) == 1) {
				this.rain.clear();
				this.currentWeather = 1;
			} else {
				this.clouds.clear();
				this.rain.clear();
				this.currentWeather = 0;
			}
		} else {
			if (this.random.nextInt(10) == 1) {
				this.clouds.clear();
				this.currentWeather = 3;
			} else if (this.random.nextInt(11) == 1) {
				this.clouds.clear();
				this.currentWeather = 4;
			} else if (this.random.nextInt(5) == 1) {
				this.rain.clear();
				this.currentWeather = 1;
			} else if (this.random.nextInt(4) == 1) {
				this.clouds.clear();
				this.rain.clear();
				this.currentWeather = 2;
			} else {
				this.clouds.clear();
				this.rain.clear();
				this.currentWeather = 0;
			}
		}
		
		this.rainSplashes.clear();
		setWeatherTimer();
		
		Packet007Weather packet = new Packet007Weather(this.currentWeather);
		packet.writeData(Game.socketServer);
	}

	private double[] setRenderCoordinates(double mapX, double mapY) {
		double[] coords = new double[2];
		
		coords[0] = this.player.x + (mapX - this.player.getMapX());
		coords[1] = this.player.y + (mapY - this.player.getMapY());
		
		return coords;
	}

	private void updateWeather() {
		//Slightly cloudy
		if (this.currentWeather == 1) {
			if (this.random.nextInt(178) == 1) {
				ArrayList<Double> cloud = new ArrayList<Double>();
				double mapX = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.xMax);
				double mapY = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.yMax);
				while ((mapX > this.player.getMapX() + 54 || mapX < this.player.getMapX() - 54) && (mapY > this.player.getMapY() + 52 || mapY < this.player.getMapY() - 52)) {
					mapX = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.xMax);
					mapY = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.yMax);
				}
				cloud.add(mapX);
				cloud.add(mapY);
				this.clouds.add(cloud);
			}
			
			List<ArrayList<Double>> cloudsToRemove = new ArrayList<ArrayList<Double>>();
			for (ArrayList<Double> list : this.clouds) {
				if (list.get(0) < -8) cloudsToRemove.add(list);
				else list.set(0, list.get(0) - 0.0261);
			}
			this.clouds.removeAll(cloudsToRemove);
		}
		////////////////////
		
		//Rain
		if (this.currentWeather == 3) {
			for (int i = 0; i < (this.world.currentPlayer.level.xMax + this.world.currentPlayer.level.yMax) / 200; i++) {
				if (this.random.nextInt(2) == 1) {
					ArrayList<Double> rainDrop = new ArrayList<Double>();
					double mapX = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.xMax + 55);
					double mapY = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.yMax + 55);
					while ((mapX > this.player.getMapX() + 51 || mapX < this.player.getMapX() - 51) && (mapY > this.player.getMapY() + 51 || mapY < this.player.getMapY() - 51)) {
						mapX = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.xMax + 55);
						mapY = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.yMax + 55);
					}
					rainDrop.add(mapX);
					rainDrop.add(mapY);
					this.rain.add(rainDrop);
				}
			}
			
			List<ArrayList<Double>> rainDropToRemove = new ArrayList<ArrayList<Double>>();
			for (ArrayList<Double> list : this.rain) {
				if (list.get(0) < -2 || list.get(1) < -2) rainDropToRemove.add(list);
				
				else if (this.random.nextInt(50) == 1) {
					rainDropToRemove.add(list);
					ArrayList<Double> rainSplash = new ArrayList<Double>();
					rainSplash.add(list.get(0));
					rainSplash.add(list.get(1));
					rainSplash.add(1.0);
					this.rainSplashes.add(rainSplash);
				}
				
 				else {
					list.set(0, list.get(0) - 0.1255);
					list.set(1, list.get(1) - 1.056);
				}
			}
			this.rain.removeAll(rainDropToRemove);
			
			List<ArrayList<Double>> rainSplashesToRemove = new ArrayList<ArrayList<Double>>();
			for (ArrayList<Double> list : this.rainSplashes) {
				if (list.get(2) <= 0) rainSplashesToRemove.add(list);
				else list.set(2, list.get(2) - 0.04);
			}
			this.rainSplashes.removeAll(rainSplashesToRemove);
		}
		////////////////////
		
		//Lightning
		if (this.currentWeather == 4) {
			if (this.lightningTimer == 0) {
				if (this.random.nextInt(222) == 1) {
					this.lightningTimer = 30;
				}
			}
			
			for (int i = 0; i < (this.world.currentPlayer.level.xMax + this.world.currentPlayer.level.yMax) / 225; i++) {
				if (this.random.nextInt(2) == 1) {
					ArrayList<Double> rainDrop = new ArrayList<Double>();
					double mapX = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.xMax + 55);
					double mapY = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.yMax + 55);
					while ((mapX > this.player.getMapX() + 51 || mapX < this.player.getMapX() - 51) && (mapY > this.player.getMapY() + 51 || mapY < this.player.getMapY() - 51)) {
						mapX = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.xMax + 55);
						mapY = ThreadLocalRandom.current().nextDouble(0, this.world.currentPlayer.level.yMax + 55);
					}
					rainDrop.add(mapX);
					rainDrop.add(mapY);
					this.rain.add(rainDrop);
				}
			}
			
			List<ArrayList<Double>> rainDropToRemove = new ArrayList<ArrayList<Double>>();
			for (ArrayList<Double> list : this.rain) {
				if (list.get(0) < -2 || list.get(1) < -2) rainDropToRemove.add(list);
				
				else if (this.random.nextInt(50) == 1) {
					rainDropToRemove.add(list);
					ArrayList<Double> rainSplash = new ArrayList<Double>();
					rainSplash.add(list.get(0));
					rainSplash.add(list.get(1));
					rainSplash.add(1.0);
					this.rainSplashes.add(rainSplash);
				}
				
 				else {
					list.set(0, list.get(0) - 0.1255);
					list.set(1, list.get(1) - 1.056);
				}
			}
			this.rain.removeAll(rainDropToRemove);
			
			List<ArrayList<Double>> rainSplashesToRemove = new ArrayList<ArrayList<Double>>();
			for (ArrayList<Double> list : this.rainSplashes) {
				if (list.get(2) <= 0) rainSplashesToRemove.add(list);
				else list.set(2, list.get(2) - 0.04);
			}
			this.rainSplashes.removeAll(rainSplashesToRemove);
		}
		////////////////////
	}
	
	private void setWeatherTimer() {
		this.currentWeatherTimer = this.random.nextInt(this.maximumWeatherTime + 1);
		if (this.currentWeatherTimer < this.minimumWeatherTime) this.currentWeatherTimer = this.minimumWeatherTime;
	}
	
	private void updateTimers() {
		if (Game.socketServer != null && this.currentWeatherTimer > 0) this.currentWeatherTimer--;
		if (this.lightningTimer > 0) this.lightningTimer--;
	}

	public int getCurrentWeather() {
		return this.currentWeather;
	}

	public void setCurrentWeather(int currentWeather) {
		this.currentWeather = currentWeather;
	}

}
