import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
//import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class Main implements ActionListener, Serializable {
	
	public Main() {
		setCanvasSizeWindow();
	}

	public static Game game;
	
	public static double scale;
	public static double xRatio;
	
	public static boolean debugmode = false;
	public static String mapName;
	
	private static boolean setCanvasSize = false, badCanvasSizeInput = false, addedError = false;
	private static int canvasSize = 900, maxCanvasSize = 900;
	
	private static JFrame frame;
	private static JFrame editorFrame;
	private static JTextField input;
	private static JTextArea textArea;
	private static JComboBox editorcombo = new JComboBox();
	
	public static void main(String[] args) {
		//Checks to make sure scaled pictures from previous run are deleted
        File actual = new File(".");
        for(File f : actual.listFiles()) {
            if (f.toString().length() >= 10 && f.toString().substring(f.toString().length() - 10, f.toString().length() - 4).equals("Scaled")) {
            	f.delete();
            	
            } else if (f.toString().length() >= 11 && f.toString().substring(f.toString().length() - 11, f.toString().length() - 4).equals("Clipped")) {
				f.delete();
			}
        }
        //////////////////////////
        
		Main main = new Main();
		
		while (!setCanvasSize) {
			if (badCanvasSizeInput && !addedError) {
				addedError = true;
				textArea.setEditable(true);
		        textArea.append("\n\n\n\nERROR: Bad input received. Please try again.");
				textArea.setEditable(false);
			}
		}
		
		//Determine screen size and set x to y ratio
		Rectangle screen = getScreenBounds(new Window(null));
		xRatio = (double) screen.width / (double) screen.height;
        //////////////////////////
		
		StdDraw.setCanvasSize(canvasSize, canvasSize);
		StdDraw.setXscale(0, 100);
		StdDraw.setYscale(0, 100);
		
		scale = canvasSize / (double) maxCanvasSize;
		
		//Create game
		game = new Game(debugmode);
		game.start();
        //////////////////////////
	}
	
	private void setCanvasSizeWindow() {
		//Text area
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
		}
		frame = new JFrame("Set Window Size");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        textArea = new JTextArea(15, 50);
        textArea.setWrapStyleWord(true);
        textArea.append("Enter the desired size of the game window below (Betweeen 1 and " + maxCanvasSize + "). If the window is too large or \n"
        		+ "too small, restart the game and adjust your entry as needed. Clicking start without entering anything\n"
        		+ "will default the window size at " + maxCanvasSize + ".\n"
        		+ "NOTE: Pictures will NOT scale to the window size if window size entered is greater than the default\n"
        		+ "window size above. If you need a window size greater than " + maxCanvasSize + " then change the \"maxCanvasSize\"\n"
        		+ "variable as needed.");
        textArea.setEditable(false);
        textArea.setFont(Font.getFont(Font.SANS_SERIF));
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ///////////////////
        
        //Input area
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        input = new JTextField(6);
        
        // If play mode is selected
        JButton button_play = new JButton( new AbstractAction("Play") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				alwaysActionPerformed();
			}	
        });
        
        // If debug mode is selected
        JButton button_debug = new JButton( new AbstractAction("Debug") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {		
				debugmode = true;
				alwaysActionPerformed();	
			}	
        });
        
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel.add(scroller);
        inputpanel.add(input);
        inputpanel.add(button_play);
        inputpanel.add(button_debug);
        button_debug.addActionListener(this);
        button_play.addActionListener(this);
        panel.add(inputpanel);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        input.requestFocus();      
	}

	// The original actionPerformed method 
	// put here to reduce code duplication.
	private void alwaysActionPerformed() {
		if (input.getText().equals("")) {
	    	setCanvasSize = true;
	    	frame.dispose();
    	} else {
    		try {
    			canvasSize = Integer.parseInt(input.getText());
    			setCanvasSize = true;
    			frame.dispose();
    		} catch (Exception error) {
    			badCanvasSizeInput = true;
    		}
    	}
	}
	/////////////////////////////

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void saveWorld() {
		//600String mapName = JOptionPane.showInputDialog("Map name:");
		
		
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save world to a directory...");
		int returnVal = chooser.showSaveDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File fileSaveLocation = chooser.getCurrentDirectory();
			File fileToSave = chooser.getSelectedFile();
			
			try {
				FileOutputStream fileOut = new FileOutputStream(fileSaveLocation + "/" + fileToSave.getName() + ".txt");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(game.world);
				out.close();
				fileOut.close();
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				JOptionPane.showMessageDialog(null, fileToSave.getName() + " saved");
			}
	    }
		
		//String dir = System.getProperty("user.dir").concat("\\maps");
	}
	
	public static World loadWorld() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Load world from a directory...");
		File selectedFile = null;
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}
		
		World loadedWorld = null;
		String filepath = "";
		try {
		    filepath = selectedFile.getCanonicalPath();
		} catch(IOException e) {
			e.printStackTrace();
		}
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath));
			loadedWorld = (World) in.readObject();
			in.close(); 
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return (loadedWorld);
	}
	
	public static void savePlayer() {
		File fileToSave = new File(Game.player.name);
			
		try {
			FileOutputStream fileOut = new FileOutputStream(fileToSave + ".txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(Game.player);
			out.close();
			fileOut.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static PlayerMP loadPlayer(String username) {
		PlayerMP loadedPlayer = null;
		String filepath = ".\\" + username + ".txt";
		
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath));
			loadedPlayer = (PlayerMP) in.readObject();
			in.close(); 
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return (loadedPlayer);
	}
	
	public static Rectangle getScreenBounds(Window wnd) {
	    Rectangle sb;
	    Insets si = getScreenInsets(wnd);

	    if (wnd == null) { 
	        sb = GraphicsEnvironment
	           .getLocalGraphicsEnvironment()
	           .getDefaultScreenDevice()
	           .getDefaultConfiguration()
	           .getBounds(); 
	    }
	    else { 
	        sb = wnd
	           .getGraphicsConfiguration()
	           .getBounds(); 
	    }

	    sb.x     +=si.left;
	    sb.y     +=si.top;
	    sb.width -=si.left+si.right;
	    sb.height-=si.top+si.bottom;
	    return sb;
	}

	public static Insets getScreenInsets(Window wnd) {
	    Insets                              si;

	    if (wnd == null) { 
	        si = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment
	           .getLocalGraphicsEnvironment()
	           .getDefaultScreenDevice()
	           .getDefaultConfiguration()); 
	    }
	    else { 
	        si = wnd.getToolkit().getScreenInsets(wnd.getGraphicsConfiguration()); 
	    }
	    return si;
	}
}
