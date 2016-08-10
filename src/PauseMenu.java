import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class PauseMenu implements Serializable {
	
	public static int highlightedSelection = 1; //0 = none, 1 = resume, 2 = options, 3 = quit, 4 = back (in options menu)
	public static int[] checkBoxes = new int[]{1, 1, 1, 1, 1}; //Health, minimap, textchat, abilities, e-action bar
	
	protected static int pauseguyUpdates = 0;
	
	private static boolean showOptions = false, closeGame = false;
	
	public static void update() {
		if (closeGame && !Game.savingPlayer) System.exit(0);
		
		checkInput();
		
		pauseguyUpdates++;
		if (pauseguyUpdates > 250) pauseguyUpdates = 0;
	}

	public static void render() {
		if (showOptions) {
			StdDraw.clear(StdDraw.BLACK);
			
			//"Options" text
			StdDraw.setFont(new Font("Arial", Font.BOLD, 38));
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.text(50, 93, "Options");
			///////////////////
			
			StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
			
			//Username and Health
			if (checkBoxes[0] == 1) {
				StdDraw.picture(10, 85, "filled_checkbox.png");
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.picture(10, 85, "empty_checkbox.png");
				StdDraw.setPenColor(new Color(255, 255, 255, 155));
			}
			StdDraw.textLeft(13, 84.75, "Show username and health");
			///////////////////
			
			//Minimap
			if (checkBoxes[1] == 1) {
				StdDraw.picture(10, 78, "filled_checkbox.png");
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.picture(10, 78, "empty_checkbox.png");
				StdDraw.setPenColor(new Color(255, 255, 255, 155));
			}
			StdDraw.textLeft(13, 77.75, "Show minimap");
			///////////////////
			
			//Textchat
			if (checkBoxes[2] == 1) {
				StdDraw.picture(10, 71, "filled_checkbox.png");
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.picture(10, 71, "empty_checkbox.png");
				StdDraw.setPenColor(new Color(255, 255, 255, 155));
			}
			StdDraw.textLeft(13, 70.75, "Show chat window");
			///////////////////
			
			//Abilities and energy
			if (checkBoxes[3] == 1) {
				StdDraw.picture(10, 64, "filled_checkbox.png");
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.picture(10, 64, "empty_checkbox.png");
				StdDraw.setPenColor(new Color(255, 255, 255, 155));
			}
			StdDraw.textLeft(13, 63.75, "Show ability and energy bar");
			///////////////////
			
			//E-action bar
			if (checkBoxes[4] == 1) {
				StdDraw.picture(10, 57, "filled_checkbox.png");
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.picture(10, 57, "empty_checkbox.png");
				StdDraw.setPenColor(new Color(255, 255, 255, 155));
			}
			StdDraw.textLeft(13, 56.75, "Show e-action bar");
			///////////////////
			
			//"Back" text
			if (highlightedSelection == 4) {
				StdDraw.setFont(new Font("Arial", Font.BOLD, 48));
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.setFont(new Font("Arial", Font.PLAIN, 38));
				StdDraw.setPenColor(StdDraw.WHITE);
			}
			StdDraw.text(50, 8, "Back");
			///////////////////
		}
		
		else {
			StdDraw.clear(StdDraw.BLACK);

			// Resume text
			if (highlightedSelection == 1) {
				StdDraw.setFont(new Font("Arial", Font.BOLD, 58));
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.setFont(new Font("Arial", Font.PLAIN, 48));
				StdDraw.setPenColor(StdDraw.GRAY);
			}

			StdDraw.text(50, 75, "Resume");
			///////////////////

			// Options text
			if (highlightedSelection == 2) {
				StdDraw.setFont(new Font("Arial", Font.BOLD, 58));
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.setFont(new Font("Arial", Font.PLAIN, 48));
				StdDraw.setPenColor(StdDraw.GRAY);
			}

			StdDraw.text(50, 50, "Options");
			///////////////////

			// Quit text
			if (highlightedSelection == 3) {
				StdDraw.setFont(new Font("Arial", Font.BOLD, 58));
				StdDraw.setPenColor(StdDraw.WHITE);
			} else {
				StdDraw.setFont(new Font("Arial", Font.PLAIN, 48));
				StdDraw.setPenColor(StdDraw.GRAY);
			}

			StdDraw.text(50, 25, "Quit");
			///////////////////

			// Pause guy
			if (pauseguyUpdates < 125) {
				StdDraw.picture(15, 15, "pauseguy_1.png");
			} else {
				StdDraw.picture(15, 15, "pauseguy_2.png");
			}
			///////////////////
		}
		
		StdDraw.show(1);
	}
	
	private static void checkInput() {
		
		if (showOptions) {
			// Esc - close menu
			if (Game.player.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE)) {
				Game.gamePaused = false;
				Game.player.buttonDelay = Game.player.buttonDelayAmount;
			}
			///////////////////
			
			// Left click - make selection
			else if (Mouse.lastButtonPressed == 1 && Game.player.clickDelay == 0) {
				if (StdDraw.mouseX() >= 8 && StdDraw.mouseX() <= 11.77777777 && StdDraw.mouseY() >= 83.22222222 && StdDraw.mouseY() <= 87) {
					Mouse.lastButtonPressed = -1;
					Game.player.clickDelay = Game.player.clickDelayAmount;
					if (checkBoxes[0] == 1) checkBoxes[0] = 0;
					else checkBoxes[0] = 1;
				}
				
				else if (StdDraw.mouseX() >= 8 && StdDraw.mouseX() <= 11.77777777 && StdDraw.mouseY() >= 76.22222222 && StdDraw.mouseY() <= 80) {
					Mouse.lastButtonPressed = -1;
					Game.player.clickDelay = Game.player.clickDelayAmount;
					if (checkBoxes[1] == 1) checkBoxes[1] = 0;
					else checkBoxes[1] = 1;
				}
				
				else if (StdDraw.mouseX() >= 8 && StdDraw.mouseX() <= 11.77777777 && StdDraw.mouseY() >= 69.22222222 && StdDraw.mouseY() <= 73) {
					Mouse.lastButtonPressed = -1;
					Game.player.clickDelay = Game.player.clickDelayAmount;
					if (checkBoxes[2] == 1) checkBoxes[2] = 0;
					else checkBoxes[2] = 1;
				}
				
				else if (StdDraw.mouseX() >= 8 && StdDraw.mouseX() <= 11.77777777 && StdDraw.mouseY() >= 62.22222222 && StdDraw.mouseY() <= 66) {
					Mouse.lastButtonPressed = -1;
					Game.player.clickDelay = Game.player.clickDelayAmount;
					if (checkBoxes[3] == 1) checkBoxes[3] = 0;
					else checkBoxes[3] = 1;
				}
				
				else if (StdDraw.mouseX() >= 8 && StdDraw.mouseX() <= 11.77777777 && StdDraw.mouseY() >= 55.22222222 && StdDraw.mouseY() <= 59) {
					Mouse.lastButtonPressed = -1;
					Game.player.clickDelay = Game.player.clickDelayAmount;
					if (checkBoxes[4] == 1) checkBoxes[4] = 0;
					else checkBoxes[4] = 1;
				}
				
				else if (highlightedSelection == 4) {
					Mouse.lastButtonPressed = -1;
					handleSelection(highlightedSelection);
				}
			}
			///////////////////
			
			// Track mouse movement
			if (StdDraw.mouseX() >= 0 && StdDraw.mouseX() <= 100 && StdDraw.mouseY() >= 0 && StdDraw.mouseY() <= 12) {
				highlightedSelection = 4;
			} else {
				highlightedSelection = 0;
			}
			///////////////////
		} else {

			// Esc - close menu
			if (Game.player.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_ESCAPE)) {
				Game.gamePaused = false;
				Game.player.buttonDelay = Game.player.buttonDelayAmount;
			}
			///////////////////

			// Enter - make selection
			else if (Main.game.world.currentPlayer.buttonDelay == 0 && StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
				Main.game.world.currentPlayer.buttonDelay = Main.game.world.currentPlayer.buttonDelayAmount;
				handleSelection(highlightedSelection);
			}
			///////////////////

			// Left click - make selection
			else if (Mouse.lastButtonPressed == 1 && Game.player.clickDelay == 0) {
				Mouse.lastButtonPressed = -1;
				Game.player.clickDelay = Game.player.clickDelayAmount;
				handleSelection(highlightedSelection);
			}
			///////////////////

			// Track mouse movement
			if (StdDraw.mouseY() > 65 && StdDraw.mouseY() < 85) highlightedSelection = 1;
			else if (StdDraw.mouseY() > 40 && StdDraw.mouseY() < 60) highlightedSelection = 2;
			else if (StdDraw.mouseY() > 15 && StdDraw.mouseY() < 35) highlightedSelection = 3;
			else highlightedSelection = 0;
			///////////////////
		}
	}

	private static void handleSelection(int selection) {
		//Resume
		if (selection == 1) {
			showOptions = false;
			Game.gamePaused = false;
		}
		///////////////////
		
		//Options
		else if (selection == 2) {
			showOptions = true;
		}
		///////////////////
		
		//Quit
		else if (selection == 3) {
			try {
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					public void run() {
						File actual = new File(".");
						for(File f : actual.listFiles()) {
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
			
			//Game.savingPlayer = true;
			
			Packet001Disconnect packet = new Packet001Disconnect(Game.player.name);
			packet.writeData(Game.socketClient);
			
			closeGame = true;
		}
		///////////////////
		
		//Back (options menu)
		else if (selection == 4) {
			showOptions = false;
		}
		///////////////////
	}

}
