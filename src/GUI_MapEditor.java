import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import java.awt.BorderLayout;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JSplitPane;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JTree;
import javax.swing.JCheckBox;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

public class GUI_MapEditor implements ActionListener, Serializable {

	public JFrame frame;
	public Player_Sandbox player;
	
	private EditorController ec;
	private int updates = 0;
	private Color selectionColor = StdDraw.WHITE;
	
	private JPanel panel;
	
	//Selecting variables
	public JCheckBox cb_isSelecting;
	public JComboBox comboBox_setSelection;
	
	public Tile selectedTile;
	public Entity selectedEntity;
	public LevelObject selectedObject;
	public LevelConnector selectedLevelConnector;
	public DroppedItem selectedDroppedItem;
	
	private JLabel yourSelection;
	private JLabel selectionImage;
	private JLabel replacementImage;
	//////////////////////
	
	//TILE SELECTION variables
	private JLabel lbl_tileReplacement;
	private JComboBox tileReplacement;
	
	private JButton replaceTileButton;
	//////////////////////
	
	//Placing variables
	public JCheckBox cb_isPlacing;
	
	private JLabel lbl_tiles;
	public JComboBox comboBox_Tiles;
	
	private JLabel lbl_NPC;
	public JComboBox comboBox_NPC;
	
	private JLabel lbl_item;
	public JComboBox comboBox_items;
	
	private JLabel lbl_weapon;
	public JComboBox comboBox_weapon;
	
	private JLabel lbl_armor;
	public JComboBox comboBox_armor;
	//////////////////////

	/**
	 * Create the application.
	 */
	public GUI_MapEditor() {
		initialize();
		frame.setVisible(true);
				
		//createEditorGUI();
	}
	
	public void update() {
		//Update selection color flash
		this.updates++;
		if (this.updates > 50) {
			this.updates = 0;
			if (this.selectionColor.equals(StdDraw.WHITE)) this.selectionColor = StdDraw.PINK;
			else this.selectionColor = StdDraw.WHITE;
		}
		////////////////////////////
		
		//If user is selecting things to change in-game
		if (this.cb_isSelecting.isSelected()) {
			this.selectionImage.setVisible(true);
			this.replacementImage.setVisible(true);
			
			//Reset current selection if Tile becomes selected
			if (this.comboBox_setSelection.getSelectedItem().equals("Tile")) {
				if (this.selectedEntity != null || this.selectedObject != null || this.selectedLevelConnector != null || this.selectedDroppedItem != null) {
					this.selectionImage.setIcon(null);
					this.selectionImage.setText(null);
					this.selectionImage.revalidate();
					this.selectionImage.repaint();
					this.replacementImage.setIcon(null);
					this.replacementImage.setText(null);
					this.replacementImage.revalidate();
					this.replacementImage.repaint();
					this.lbl_tileReplacement.setVisible(false);
					this.tileReplacement.setVisible(false);
					this.replaceTileButton.setVisible(false);
					this.panel.validate();
					this.panel.repaint();
				}
				
				this.selectedEntity = null;
				this.selectedObject = null;
				this.selectedLevelConnector = null;
				this.selectedDroppedItem = null;
				
				if (this.selectedTile != null) {
					this.lbl_tileReplacement.setVisible(true);
					this.tileReplacement.setVisible(true);
					
					if (!this.tileReplacement.getSelectedItem().equals("- None -")) {
						this.replaceTileButton.setVisible(true);
					}
				}
			}
			////////////////////////////
			
			//Reset current selection if Entity becomes selected
			else if (this.comboBox_setSelection.getSelectedItem().equals("Entity")) {
				if (this.selectedTile != null || this.selectedObject != null || this.selectedLevelConnector != null || this.selectedDroppedItem != null) {
					this.selectionImage.setIcon(null);
					this.selectionImage.setText(null);
					this.selectionImage.revalidate();
					this.selectionImage.repaint();
					this.replacementImage.setIcon(null);
					this.replacementImage.setText(null);
					this.replacementImage.revalidate();
					this.replacementImage.repaint();
					this.lbl_tileReplacement.setVisible(false);
					this.tileReplacement.setVisible(false);
					this.replaceTileButton.setVisible(false);
					this.panel.validate();
					this.panel.repaint();
				}
				
				this.selectedTile = null;
				this.selectedObject = null;
				this.selectedLevelConnector = null;
				this.selectedDroppedItem = null;
			}
			////////////////////////////
		}
		////////////////////////////
		
		//If user is choosing things in the gui to place in-game
		else if (this.cb_isPlacing.isSelected()) {
			this.selectionImage.setVisible(false);
			this.replacementImage.setVisible(false);
		}
		////////////////////////////
	}
	
	public void render() {
		if (this.selectedTile != null) {
			//Render in game
			StdDraw.setPenColor(this.selectionColor);
			StdDraw.setPenRadius(0.0058);
			StdDraw.square(this.selectedTile.x, this.selectedTile.y, this.selectedTile.level.tileSize);
			StdDraw.setPenRadius();
			////////////////////////////
			
			//Render selection in GUI
			this.selectionImage.setText(null);
			this.selectionImage.setIcon(new ImageIcon(this.getClass().getResource(this.selectedTile.filename)));
			this.selectionImage.revalidate();
			this.selectionImage.repaint();
			////////////////////////////
			
			//Render replacement tile chosen in GUI
			if (this.tileReplacement.getSelectedItem().equals("Blank")) {
				this.replacementImage.setText(null);
				this.replacementImage.setIcon(new ImageIcon(this.getClass().getResource("blank_tile.png")));
				this.replacementImage.revalidate();
				this.replacementImage.repaint();
			}
			
			else if (this.tileReplacement.getSelectedItem().equals("Grass")) {
				this.replacementImage.setText(null);
				this.replacementImage.setIcon(new ImageIcon(this.getClass().getResource("grass.png")));
				this.replacementImage.revalidate();
				this.replacementImage.repaint();
			}
			////////////////////////////
			
			//Update panel
			this.panel.revalidate();
			this.panel.repaint();
			////////////////////////////
		}
		
		else if (this.selectedEntity != null) {
			double[] xCoords, yCoords;
			
			if (Mouse.lastButtonPressed == 1) {
				Mouse.lastButtonPressed = -1;
				
				//this.clickDelay = this.clickDelayAmount;
				double xx = this.player.getMapX() - (this.player.x  - StdDraw.mouseX());
				double yy  = this.player.getMapY() - (this.player.y  - StdDraw.mouseY());
				Main.game.world.currentPlayer.level.getEntitiesToAdd().add((new Entity_NPC_Test(Main.game.world.currentPlayer.level, true, true, "Enemy", "Enemy", xx, yy, xx + 25, yy + 26, xx - 10, yy - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace())));
			}
			
			//Render in game
			StdDraw.setPenColor(this.selectionColor);
			StdDraw.setPenRadius(0.0058);
			xCoords = new double[]{this.selectedEntity.x - this.selectedEntity.minX, this.selectedEntity.x - this.selectedEntity.minX, this.selectedEntity.x + this.selectedEntity.maxX, this.selectedEntity.x + this.selectedEntity.maxX};
			yCoords = new double[]{this.selectedEntity.y + this.selectedEntity.maxY * 2, this.selectedEntity.y - this.selectedEntity.minY, this.selectedEntity.y - this.selectedEntity.minY, this.selectedEntity.y + this.selectedEntity.maxY * 2};
			StdDraw.polygon(xCoords, yCoords);
			StdDraw.setPenRadius();
			//selectedEntity = new Entity_NPC_Test(Main.currentLevel, true, true, "Enemy", "Enemy", 22, 29, 22 + 25, 29 + 26, 22 - 10, 29 - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace());
			//Main.currentLevel.entitiesToAdd.add(new Entity_NPC_Test(Main.currentLevel, true, true, "Enemy", "Enemy", 22, 29, 22 + 25, 29 + 26, 22 - 10, 29 - 12, new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace(), new Item_Equipment_EmptySpace()));
			////////////////////////////
			
			//Render in GUI
			this.selectionImage.setIcon(null);
			this.selectionImage.setFont(new Font("Arial", Font.BOLD, 14));
			this.selectionImage.setText(this.selectedEntity.name);
			this.selectionImage.revalidate();
			this.selectionImage.repaint();
			this.panel.revalidate();
			this.panel.repaint();
			////////////////////////////
		}
	}

	/**
	 * Launch the application.
	 */
	public void createEditorGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_MapEditor window = new GUI_MapEditor();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.ec = new EditorController(this.player);
		
		this.frame = new JFrame();
		this.frame.setBounds(100, 100, 700, 800);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setCustomCursor();
	    
		JMenuBar menuBar = new JMenuBar();
		this.frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//ec.newMap();
			}
		});
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ec.loadMap();
			}
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ec.saveMap();
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmClose);
		this.frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		this.panel = new JPanel();
		this.frame.getContentPane().add(this.panel);
		this.panel.setLayout(null);
		Font defaultFont = this.panel.getFont();
		
		
		
		//Selecting area
		this.cb_isSelecting = new JCheckBox(" Selecting");
		this.cb_isSelecting.setFont(new Font("Rockwell", Font.PLAIN, 26));
		this.cb_isSelecting.setBounds(25, 25, 150, 35);
		this.panel.add(this.cb_isSelecting);
		this.cb_isSelecting.setSelected(true);
		this.cb_isSelecting.addActionListener(this);
		
		this.yourSelection = new JLabel("Your selection:");
		this.yourSelection.setFont(new Font("Rockwell", Font.PLAIN, 16));
		this.yourSelection.setBounds(25, 40, 200, 100);
		this.panel.add(this.yourSelection);
		
		this.comboBox_setSelection = new JComboBox(ec.getSelectionOptions());
		this.comboBox_setSelection.setBounds(25, 110, 160, 20);
		this.panel.add(this.comboBox_setSelection);
		
		this.selectionImage = new JLabel();
		this.selectionImage.setBounds(50, 150, 128, 128);
		this.panel.add(this.selectionImage);
		
		this.lbl_tileReplacement = new JLabel("Replace with:");
		this.lbl_tileReplacement.setFont(new Font("Rockwell", Font.PLAIN, 16));
		this.lbl_tileReplacement.setBounds(25, 300, 200, 20);
		this.lbl_tileReplacement.setVisible(false);
		this.panel.add(this.lbl_tileReplacement);
		
		this.tileReplacement = new JComboBox(ec.getTileReplacementOptions());
		this.tileReplacement.setBounds(25, 325, 160, 20);
		this.tileReplacement.setVisible(false);
		this.panel.add(this.tileReplacement);
		
		this.replacementImage = new JLabel();
		this.replacementImage.setBounds(50, 375, 128, 128);
		this.panel.add(this.replacementImage);
		
		this.replaceTileButton = new JButton("Replace Tile");
		this.replaceTileButton.setFont(new Font("Rockwell", Font.BOLD, 14));
		this.replaceTileButton.setBounds(50, 555, 135, 75);
		this.replaceTileButton.addActionListener(this);
		this.replaceTileButton.setVisible(false);
		this.panel.add(this.replaceTileButton);
		//////////////////////////
		
		
		
		//Placing area
		this.cb_isPlacing = new JCheckBox(" Placing");
		this.cb_isPlacing.setFont(new Font("Rockwell", Font.PLAIN, 26));
		this.cb_isPlacing.setBounds(450, 25, 150, 35);
		this.panel.add(this.cb_isPlacing);
		this.cb_isPlacing.addActionListener(this);
		
		this.lbl_tiles = new JLabel("Tiles");
		this.lbl_tiles.setFont(new Font("Rockwell", Font.PLAIN, 16));
		this.lbl_tiles.setBounds(444, 71, 46, 14);
		this.panel.add(this.lbl_tiles);
		
		this.comboBox_Tiles = new JComboBox(ec.getTiles());
		this.comboBox_Tiles.setBounds(500, 70, 160, 20);
		this.panel.add(this.comboBox_Tiles);
		
		this.lbl_NPC = new JLabel("NPC");
		this.lbl_NPC.setFont(new Font("Rockwell", Font.PLAIN, 16));
		this.lbl_NPC.setBounds(444, 101, 46, 14);
		this.panel.add(this.lbl_NPC);
		
		this.comboBox_NPC = new JComboBox(ec.getNPCs());
		this.comboBox_NPC.setBounds(500, 100, 160, 20);
		this.panel.add(this.comboBox_NPC);
		
		this.lbl_item = new JLabel("Items");
		this.lbl_item.setFont(new Font("Rockwell", Font.PLAIN, 16));
		this.lbl_item.setBounds(444, 131, 46, 14);
		this.panel.add(this.lbl_item);
		
		this.comboBox_items = new JComboBox(ec.getItems());
		this.comboBox_items.setBounds(500, 130, 160, 20);
		this.panel.add(this.comboBox_items);
		
		this.lbl_weapon = new JLabel("Weapons");
		this.lbl_weapon.setFont(new Font("Rockwell", Font.PLAIN, 16));
		this.lbl_weapon.setBounds(415, 161, 69, 20);
		this.panel.add(this.lbl_weapon);
		
		this.comboBox_weapon = new JComboBox(ec.getWeapons());
		this.comboBox_weapon.setBounds(500, 160, 160, 20);
		this.panel.add(this.comboBox_weapon);
		
		this.lbl_armor = new JLabel("Armor");
		this.lbl_armor.setFont(new Font("Rockwell", Font.PLAIN, 16));
		this.lbl_armor.setBounds(442, 191, 48, 14);
		this.panel.add(this.lbl_armor);
		
		//this.comboBox_armor = new JComboBox(ec.getArmor());
		this.comboBox_armor.setBounds(500, 190, 160, 20);
		this.panel.add(this.comboBox_armor);
		///////////////////////////////
		
		Canvas canvas_picture = new Canvas();
		canvas_picture.setBounds(488, 280, 81, 75);
		this.panel.add(canvas_picture);
		
		final JCheckBox cb_activeNPC = new JCheckBox("Active NPC");
		cb_activeNPC.setSelected(true);
		cb_activeNPC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if   (cb_activeNPC.isSelected()) ec.activateNPC();
				else 							 ec.haltNPC();
			}
		});
		cb_activeNPC.setBounds(563, 217, 97, 23);
		panel.add(cb_activeNPC);
		
	}

	private void setCustomCursor() {
		//Blue circle with red crosshair cursor
		Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension dim = kit.getBestCursorSize(48, 48);
	    BufferedImage buffered =  GraphicsUtilities.createCompatibleTranslucentImage(dim.width, dim.height);
	    Shape circle = new Ellipse2D.Float(0, 0, dim.width - 1, dim.height - 1);
	    Graphics2D g = buffered.createGraphics();
	    g.setColor(Color.BLUE);
	    g.draw(circle);
	    g.setColor(Color.RED);
	    int centerX = (dim.width - 1) /2;
	    int centerY = (dim.height - 1) / 2;
	    g.drawLine(centerX, 0, centerX, dim.height - 1);
	    g.drawLine(0, centerY, dim.height - 1, centerY);
	    g.dispose();
	    Cursor cursor = kit.createCustomCursor(buffered, new Point(centerX, centerY), "myCursor");
	    /////////////////////////////
	    
	    /*
		//Setting cursor to pointer.png
	    Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension dim = kit.getBestCursorSize(48, 48);
	    BufferedImage buffered = GraphicsUtilities.createCompatibleTranslucentImage(dim.width, dim.height);
	    try {
			buffered = ImageIO.read(this.getClass().getResource("pointer.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    Graphics2D g = buffered.createGraphics();
	    int centerX = (dim.width - 1) /2;
	    int centerY = (dim.height - 1) / 2;
	    g.dispose();
	    Cursor cursor = kit.createCustomCursor(buffered, new Point(centerX, centerY), "myCursor");
	    frame.setCursor(cursor);
	    StdDraw.frame.setCursor(cursor);
	    /////////////////////////////
	    */
	    
	    frame.setCursor(cursor);
	    StdDraw.frame.setCursor(cursor);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.cb_isSelecting)) {
			if (this.cb_isSelecting.isSelected()) {
				this.cb_isPlacing.setSelected(false);
				
				this.comboBox_Tiles.setSelectedIndex(0);
				this.comboBox_NPC.setSelectedIndex(0);
				this.comboBox_items.setSelectedIndex(0);
				this.comboBox_weapon.setSelectedIndex(0);
				this.comboBox_armor.setSelectedIndex(0);
			} else {
				this.cb_isPlacing.setSelected(true);
				
				this.comboBox_setSelection.setSelectedIndex(0);
				
				this.selectionImage.setIcon(null);
				this.selectionImage.setText(null);
				this.selectionImage.revalidate();
				this.selectionImage.repaint();
				this.replacementImage.setIcon(null);
				this.replacementImage.setText(null);
				this.replacementImage.revalidate();
				this.replacementImage.repaint();
				this.lbl_tileReplacement.setVisible(false);
				this.tileReplacement.setVisible(false);
				this.replaceTileButton.setVisible(false);
				this.panel.validate();
				this.panel.repaint();
			
				this.selectedEntity = null;
				this.selectedObject = null;
				this.selectedLevelConnector = null;
				this.selectedDroppedItem = null;
			}
		}
		
		else if (event.getSource().equals(this.cb_isPlacing)) {
			if (this.cb_isPlacing.isSelected()) {
				this.cb_isSelecting.setSelected(false);
				
				this.comboBox_setSelection.setSelectedIndex(0);
				
				this.selectionImage.setIcon(null);
				this.selectionImage.setText(null);
				this.selectionImage.revalidate();
				this.selectionImage.repaint();
				this.replacementImage.setIcon(null);
				this.replacementImage.setText(null);
				this.replacementImage.revalidate();
				this.replacementImage.repaint();
				this.lbl_tileReplacement.setVisible(false);
				this.tileReplacement.setVisible(false);
				this.replaceTileButton.setVisible(false);
				this.panel.validate();
				this.panel.repaint();
			
				this.selectedEntity = null;
				this.selectedObject = null;
				this.selectedLevelConnector = null;
				this.selectedDroppedItem = null;
			} else {
				this.cb_isSelecting.setSelected(true);
				
				this.comboBox_Tiles.setSelectedIndex(0);
				this.comboBox_NPC.setSelectedIndex(0);
				this.comboBox_items.setSelectedIndex(0);
				this.comboBox_weapon.setSelectedIndex(0);
				this.comboBox_armor.setSelectedIndex(0);
			}
		}
		
		else if (event.getSource().equals(this.replaceTileButton)) {
			for (int yy = 0; yy < this.player.level.tiles.length; yy++) {
				for (int xx = 0; xx < this.player.level.tiles[yy].length; xx++) {
					if (this.player.level.tiles[xx][yy].equals(this.selectedTile)) {
						if (this.tileReplacement.getSelectedItem().equals("Blank")) {
							this.player.level.tiles[xx][yy] = new Tile_Blank(this.player.level.tiles[xx][yy].level, this.player.level.tiles[xx][yy].mapX, this.player.level.tiles[xx][yy].mapY);
							this.selectedTile = this.player.level.tiles[xx][yy];
							this.selectionImage.setIcon(new ImageIcon(this.getClass().getResource(this.selectedTile.filename)));
							this.selectionImage.revalidate();
							this.selectionImage.repaint();
							this.replacementImage.revalidate();
							this.replacementImage.repaint();
							this.panel.validate();
							this.panel.repaint();
							return;
						}
						
						else if (this.tileReplacement.getSelectedItem().equals("Grass")) {
							this.player.level.tiles[xx][yy] = new Tile_Grass(this.player.level, this.player.level.tiles[xx][yy].mapX, this.player.level.tiles[xx][yy].mapY);
							this.selectedTile = this.player.level.tiles[xx][yy];
							this.selectionImage.setIcon(new ImageIcon(this.getClass().getResource(this.selectedTile.filename)));
							this.selectionImage.revalidate();
							this.selectionImage.repaint();
							this.replacementImage.revalidate();
							this.replacementImage.repaint();
							this.panel.validate();
							this.panel.repaint();
							return;
						}
					}
				}
			}
		}
	}
}
