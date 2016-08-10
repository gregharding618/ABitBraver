import java.awt.Canvas;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Game extends Canvas implements Runnable {
	
	public World world; //Will be a list of worlds eventually
	public static PlayerMP player;
	
	public boolean debugmode;
	public static boolean savingWorld = false, savingPlayer = false;
	public static boolean gamePaused = false;
	
	public static GameClient socketClient;
	public static GameServer socketServer;
	
	public WindowHandler windowHandler;
	
	private static boolean running = false;
	
	private static int frames = 0;
	private static long lastTime = System.currentTimeMillis();
	
	private static Random random = new Random();
	
	public Game(boolean debugmode) {
		this.debugmode = debugmode;
		this.windowHandler = new WindowHandler(this);
		
		/*
		//Load world from file
		World loadedWorld = null;
		File worldpath = new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/worlds/World_Start.txt");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(worldpath));
			loadedWorld = (World) in.readObject();
			in.close(); 
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		world = loadedWorld;
		world.setDebugMode(debugmode);*/
	}
	
	private void update() {
		this.world.update();
		
		//Make sure last button pressed is reset if the click is not used
		if (System.currentTimeMillis() - Mouse.timeClicked > 90) {
			Mouse.timeClicked = 0;
			Mouse.lastButtonPressed = -1;
		}
		/////////////////////////
	}
	
	private void render() {
		StdDraw.clear(StdDraw.BLACK);
		
		this.world.render();
		
		StdDraw.show(1);
	}
	
	public synchronized void start() {
		new Thread(this).start();
		
		String username = "";
		
		if (JOptionPane.showConfirmDialog(this, "Do you want to run the server?") == 0) {
			socketServer = new GameServer(this);
			socketServer.start();
			socketClient = new GameClient(this, "localhost");
			socketClient.connectedToServer = true;
			socketClient.start();
			running = true;
				
			username = JOptionPane.showInputDialog("Enter a username");
			if (username == null || username.equals("")) username = Integer.toString(random.nextInt(9999999));
		} else {
			username = JOptionPane.showInputDialog("Enter a username");
			if (username == null || username.equals("")) username = Integer.toString(random.nextInt(9999999));
			
			boolean goodIP = false;
			boolean goodUsername = false;
			
			while (!goodIP) {
				String ip = JOptionPane.showInputDialog("Enter the IP address of the server you want to connect to:");
				if (isIP(ip)) {
					socketClient = new GameClient(this, ip);
					socketClient.start();
					
					long startTime = System.currentTimeMillis();
					long currentTime = startTime;
					
					Packet012PingServer pingServerPacket = new Packet012PingServer(username);
					pingServerPacket.writeData(socketClient);
					
					StdDraw.setPenColor(StdDraw.BLACK);
					StdDraw.setFont(new Font("Arial", Font.BOLD, 56));
					
					while (currentTime < startTime + 20000) {
						StdDraw.clear();
						if (socketClient.connectedToServer) {
							goodIP = true;
							break;
						}
						
						if (currentTime == startTime + 7500 || currentTime == startTime + 13500) {
							Packet012PingServer nextPingServerPacket = new Packet012PingServer(username);
							nextPingServerPacket.writeData(socketClient);
						}
						
						currentTime = System.currentTimeMillis();
						StdDraw.text(50, 57, "Pinging server...");
						StdDraw.text(50, 50, ((currentTime - startTime) / 1000) + " seconds / 20");
						StdDraw.show(1);
					}
					
					StdDraw.clear();
					StdDraw.text(50, 57, "Pinging server...");
					StdDraw.text(50, 50, ((currentTime - startTime) / 1000) + " seconds / 20");
					StdDraw.text(50, 24, "Server not found,");
					StdDraw.text(50, 17,  "try another IP address.");
					StdDraw.show(1);
				}
			}
			
			running = true;
			StdDraw.clear();
		}
		
		createPlayer(username);

		this.world = new World(player);
		
		Packet000Login loginPacket = new Packet000Login(player.name, this.world.startLevel.name, 25, 50, this.world.time.getTimeOfDay(), this.world.weather.getCurrentWeather());
		if (socketServer != null) {
			socketServer.addConnection((PlayerMP)player, loginPacket);
		}
		loginPacket.writeData(socketClient);
	}
	
	private void createPlayer(String username) {
		//Check if player already exists
        File actual = new File(".");
        for(File f : actual.listFiles()) {
            if (f.toString().equals(".\\" + username + ".txt")) {
            	player = Main.loadPlayer(username);
            	return;
            }
        }
        //////////////////////////
		
		player = new PlayerMP(username, true, this.debugmode, 25, 50, new Item_Equipment_EmptySpace(), 
				new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), 
				new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), 
				new Item_Equipment_EmptySpace(), null, -1);
	}

	public synchronized void stop() {
		running = false;
	}
	
	public static boolean isIP(String text) {
		if (text.equalsIgnoreCase("localhost")) return true;
		
	    Pattern p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	    Matcher m = p.matcher(text);
	    return m.find();
	}


	@Override
	public void run() {
		while (true) {
			while (running && this.world != null && this.world.currentPlayer != null) {
				if (savingWorld) {
					Player tempPlayer = Game.player;
					tempPlayer.ec.saveMap();
					savingWorld = false;
				} else if (savingPlayer) {
					Main.savePlayer();
					savingPlayer = false;
				} else {
					updateFPS();
					update();
					if (gamePaused) {
						PauseMenu.update();
						PauseMenu.render();
					} else {
						render();
					}
				}
			
				//Deletes all picture files generated by the game
				//Temporary pictures are generated to ensure they scale to the window size.
				//NOTE: you will receive an error after closing the game due to this method, disregard the error.
				//NOTE: occasionaly pictures will not resize properly if you restart the game and use different window sizes quickly.
				//		Restart the game until pictures are scaled properly
				try {
					Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
						public void run() {
							File actual = new File(".");
							for (File f : actual.listFiles()) {
								if (f.toString().length() >= 10 && f.toString().substring(f.toString().length() - 10, f.toString().length() - 4).equals("Scaled")) {
									f.delete();
								}
							
								else if (f.toString().length() >= 11 && f.toString().substring(f.toString().length() - 11, f.toString().length() - 4).equals("Clipped")) {
									f.delete();
								}
							}
						}
					}, "Shutdown-thread"));
				} catch (Exception e) {
				
				}
				/////////////////////////
			}
		}
	}

	private void updateFPS() {
		//Update FPS
		long currentTime = System.currentTimeMillis();
		frames++;
		if (currentTime - lastTime < 10000 && currentTime - lastTime >= 1000) {
			StdDraw.frame.setTitle("A Bit Braver - " + frames + " FPS");
			frames = 0;
			lastTime = System.currentTimeMillis();
		}
		/////////////////////////
	}

}
