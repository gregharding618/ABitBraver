import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
//import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class GUI_MapEditor_V2 implements Serializable {

	private static final long serialVersionUID = -953057869957957700L;
	
	public JFrame frame;
	public JFrame newGameFrame;
	
	private JPanel panel;
	public JPanel panel_parameters;
	
	public Player debugger;
	
	private int updates = 0;
	private Color selectionColor = StdDraw.WHITE;
	
	public Tile selectedTile;
	public Entity selectedEntity;
	public Entity_NPC selectedNPC;
	public LevelObject selectedObject;
	public LevelConnector selectedLevelConnector;
	public DroppedItem selectedDroppedItem;
	
	public ArrayList<String> dialogues = new ArrayList<String>();
	public int dialogueCount = 0;
	
	public boolean entityIsSelected = false;
	public boolean isAggressive = false;
	public boolean canAttack = false;
	public boolean canBeAttacked = false;
	public boolean canSpeak = false;
	
	public JRadioButton rb_tile;
	public JRadioButton rb_npc;
	public JRadioButton rb_levelObjects;
	public JRadioButton	par_rb_waypoint;
	
	public JComboBox cb_selection;
	public JComboBox par_co_faction;
	public JComboBox par_co_npcType;
	
	private JCheckBox cb_freeze;
	
	public JTextArea par_ta_dialogue;
	
	public JMenuItem mb_save;
	public JMenuItem mb_open;
	public JMenuItem mb_new;
	
	public JCheckBox par_cb_canAttack;
	public JCheckBox par_cb_canBeAttacked;
	public JCheckBox par_cb_aggressive;
	public JCheckBox par_cb_canSpeak;
	
	public JTextField par_tb_name;
	public JTextField par_tb_maxHealth;
	public JTextField par_tb_currentHealth;
	public JTextField par_tb_minXwalk;
	public JTextField par_tb_maxXwalk;
	public JTextField par_tb_minYwalk;
	public JTextField par_tb_maxYwalk;
	public JTextField par_tb_speed;
	
	private JLabel par_lbl_maxHealth;
	private JLabel par_lbl_currentHealth;
	private JLabel par_lbl_minXwalk;
	private JLabel par_lbl_maxXwalk;
	private JLabel par_lbl_minYwalk;
	private JLabel par_lbl_maxYwalk;
	private JLabel lbl_speed;
	
	private JComboBox par_co_helmetCombo;
	private JComboBox par_co_chestCombo;
	private JComboBox par_co_bootCombo;
	private JComboBox par_co_gloveCombo;
	private JComboBox par_co_shieldCombo;
	private JComboBox par_co_weaponCombo;
	
	private JLabel par_lbl_helmet;
	private JLabel par_lbl_chest;
	private JLabel par_lbl_legs;
	private JLabel par_lbl_boots;
	private JLabel par_lbl_gloves;
	private JLabel par_lbl_shield;
	private JLabel par_lbl_weapon;
	
	private JButton par_btn_addDialogue;
	private JButton par_btn_removeDialogue;
	private JRadioButton rb_levelConnector;
	private JPanel panel_levelConnector;
	private JTextField textField;
	private JTextField lc_tf_path;
	private JButton lc_btn_browse;
	private JLabel par_lbl_dialogueCount;
	public JRadioButton par_rb_waypoints;

	

	/**
	 * Create the application.
	 */
	public GUI_MapEditor_V2(Player p) {
		this.debugger = p;
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Main Panel
		this.panel = new JPanel();
		this.frame.getContentPane().add(this.panel);
		this.panel.setLayout(null);
		///////////////////////////
		
		
		//setCustomCursor();
		
		cb_selection = new JComboBox();
		cb_selection.setModel(new DefaultComboBoxModel(new String[] {"- None -"}));
		cb_selection.setBounds(326, 8, 109, 20);
		panel.add(cb_selection);
		
		rb_tile = new JRadioButton("Tiles");
		rb_tile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rb_npc.setSelected(false);
				rb_levelObjects.setSelected(false);
				rb_levelConnector.setSelected(false);
				cb_selection.setModel(new DefaultComboBoxModel(debugger.ec.getTiles()));
				panel_parameters.setVisible(false);
				panel_levelConnector.setVisible(false);
				selectedEntity = null;
				selectedNPC = null;
			}
		});
		rb_tile.setBounds(441, 7, 133, 23);
		panel.add(rb_tile);
		
		rb_npc = new JRadioButton("NPC");
		rb_npc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rb_tile.setSelected(false);
				rb_levelObjects.setSelected(false);
				rb_levelConnector.setSelected(false);
				cb_selection.setModel(new DefaultComboBoxModel(debugger.ec.getNPCs()));
				panel_parameters.setVisible(true);
				panel_levelConnector.setVisible(false);
			}
		});
		rb_npc.setBounds(442, 33, 132, 23);
		panel.add(rb_npc);
		
		rb_levelObjects = new JRadioButton("Level Objects");
		rb_levelObjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rb_tile.setSelected(false);
				rb_npc.setSelected(false);
				rb_levelConnector.setSelected(false);
				rb_levelObjects.setSelected(true);
				cb_selection.setModel(new DefaultComboBoxModel(debugger.ec.getLevelObjects()));
				panel_parameters.setVisible(false);
				panel_levelConnector.setVisible(false);
				selectedEntity = null;
				selectedNPC = null;
				
			}
		});
		rb_levelObjects.setBounds(442, 59, 132, 23);
		panel.add(rb_levelObjects);
				
		rb_levelConnector = new JRadioButton("Level Connector");
		rb_levelConnector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rb_tile.setSelected(false);
				rb_npc.setSelected(false);
				rb_levelObjects.setSelected(false);
				cb_selection.setModel(new DefaultComboBoxModel(debugger.ec.getConnector()));
				panel_parameters.setVisible(false);
				panel_levelConnector.setVisible(true);
				selectedEntity = null;
				selectedNPC = null;
			}
		});
		rb_levelConnector.setBounds(442, 88, 132, 23);
		panel.add(rb_levelConnector);
		
		JMenuBar menuBar_1 = new JMenuBar();
		frame.setJMenuBar(menuBar_1);
		
		JMenu mnFile = new JMenu("File");
		menuBar_1.add(mnFile);
		
		mb_new = new JMenuItem("New");
		mb_new.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewGameFrame();
			}
		});
		mnFile.add(mb_new);
		
		mb_open = new JMenuItem("Open");
		mb_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debugger.ec.loadMap();
			}
		});
		mnFile.add(mb_open);
		
		mb_save = new JMenuItem("Save");
		mb_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//debugger.ec.saveMap();
				Main.game.savingWorld = true;
			}
		});
		mnFile.add(mb_save);
		
		JMenuItem mb_close = new JMenuItem("Close");
		mb_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mb_close);
		
		
			////////////// Parameter Panel \\\\\\\\\\\\\\\\\\\
			this.panel_parameters = new JPanel();
			panel_parameters.setBackground(Color.LIGHT_GRAY);
			panel_parameters.setBounds(10, 172, 564, 357);
			panel.add(panel_parameters);
			this.panel_parameters.setLayout(null);
			this.panel_parameters.setVisible(false);
			
			par_cb_canAttack = new JCheckBox("Can Attack");
			par_cb_canAttack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (par_cb_canAttack.isSelected()) 	canAttack = true;
					else								canAttack = false;
				}
			});
			par_cb_canAttack.setToolTipText("Can the entity attack?");
			par_cb_canAttack.setBounds(443, 64, 111, 23);
			panel_parameters.add(par_cb_canAttack);
			
			par_cb_canBeAttacked = new JCheckBox("Can Be Attacked");
			par_cb_canBeAttacked.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (par_cb_canBeAttacked.isSelected())	canBeAttacked = true;
					else									canBeAttacked = false;
				}
			});
			par_cb_canBeAttacked.setToolTipText("Can the entity be attacked?");
			par_cb_canBeAttacked.setBounds(443, 90, 111, 23);
			panel_parameters.add(par_cb_canBeAttacked);
			
			par_co_faction = new JComboBox();
			par_co_faction.setModel(new DefaultComboBoxModel(debugger.ec.getFactions()));
			par_co_faction.setToolTipText("The faction this entity belongs to...");
			par_co_faction.setBounds(443, 37, 111, 20);
			panel_parameters.add(par_co_faction);
			
			par_tb_name = new JTextField();
			par_tb_name.setToolTipText("The name of the entity...");
			par_tb_name.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_name.setText("- name -");
			par_tb_name.setBounds(10, 8, 140, 20);
			panel_parameters.add(par_tb_name);
			par_tb_name.setColumns(10);
			
			par_tb_maxHealth = new JTextField();
			par_tb_maxHealth.setToolTipText("The amount of health this entity can have...");
			par_tb_maxHealth.setText("0");
			par_tb_maxHealth.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_maxHealth.setColumns(10);
			par_tb_maxHealth.setBounds(101, 37, 49, 20);
			panel_parameters.add(par_tb_maxHealth);
			
			par_tb_currentHealth = new JTextField();
			par_tb_currentHealth.setToolTipText("The health of the entity");
			par_tb_currentHealth.setText("0");
			par_tb_currentHealth.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_currentHealth.setColumns(10);
			par_tb_currentHealth.setBounds(101, 65, 49, 20);
			panel_parameters.add(par_tb_currentHealth);
			
			par_tb_minXwalk = new JTextField();
			par_tb_minXwalk.setToolTipText("The minimum walk speed in X direction");
			par_tb_minXwalk.setText("0");
			par_tb_minXwalk.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_minXwalk.setColumns(10);
			par_tb_minXwalk.setBounds(101, 91, 49, 20);
			panel_parameters.add(par_tb_minXwalk);
			
			par_cb_aggressive = new JCheckBox("Aggressive");
			par_cb_aggressive.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (par_cb_aggressive.isSelected())	isAggressive = true;
					else								isAggressive = false;
				}
			});
			par_cb_aggressive.setToolTipText("Is the entity aggressive?");
			par_cb_aggressive.setBounds(443, 116, 111, 23);
			panel_parameters.add(par_cb_aggressive);
			
			par_cb_canSpeak = new JCheckBox("Can Speak");
			par_cb_canSpeak.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (par_cb_canSpeak.isSelected())	canSpeak = true;
					else								canSpeak = false;
				}
			});
			par_cb_canSpeak.setToolTipText("Can the entity talk?");
			par_cb_canSpeak.setBounds(443, 142, 111, 23);
			panel_parameters.add(par_cb_canSpeak);
			
			par_tb_maxXwalk = new JTextField();
			par_tb_maxXwalk.setToolTipText("The fastest entity can move in X direction");
			par_tb_maxXwalk.setText("0");
			par_tb_maxXwalk.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_maxXwalk.setColumns(10);
			par_tb_maxXwalk.setBounds(101, 117, 49, 20);
			panel_parameters.add(par_tb_maxXwalk);
			
			par_tb_minYwalk = new JTextField();
			par_tb_minYwalk.setToolTipText("The minimum speed in the Y direction");
			par_tb_minYwalk.setText("0");
			par_tb_minYwalk.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_minYwalk.setColumns(10);
			par_tb_minYwalk.setBounds(101, 143, 49, 20);
			panel_parameters.add(par_tb_minYwalk);
			
			par_tb_maxYwalk = new JTextField();
			par_tb_maxYwalk.setToolTipText("The max Y speed");
			par_tb_maxYwalk.setText("0");
			par_tb_maxYwalk.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_maxYwalk.setColumns(10);
			par_tb_maxYwalk.setBounds(101, 172, 49, 20);
			panel_parameters.add(par_tb_maxYwalk);
			
			par_tb_speed = new JTextField();
			par_tb_speed.setToolTipText("The overall speed...");
			par_tb_speed.setText("0");
			par_tb_speed.setHorizontalAlignment(SwingConstants.CENTER);
			par_tb_speed.setColumns(10);
			par_tb_speed.setBounds(196, 8, 49, 20);
			panel_parameters.add(par_tb_speed);
			
			par_ta_dialogue = new JTextArea();
			par_ta_dialogue.setText("- Dialogue -");
			par_ta_dialogue.setBounds(10, 203, 284, 141);
			panel_parameters.add(par_ta_dialogue);
				
			par_co_npcType = new JComboBox();
			par_co_npcType.setModel(new DefaultComboBoxModel(new String[] {"- NPC Character -"}));
			par_co_npcType.setToolTipText("The faction this entity belongs to...");
			par_co_npcType.setBounds(443, 8, 111, 20);
			panel_parameters.add(par_co_npcType);
			
			par_lbl_maxHealth = new JLabel("Max Health");
			par_lbl_maxHealth.setBounds(10, 37, 81, 20);
			panel_parameters.add(par_lbl_maxHealth);
			
			par_lbl_currentHealth = new JLabel("Current Health");
			par_lbl_currentHealth.setBounds(10, 64, 96, 20);
			panel_parameters.add(par_lbl_currentHealth);
			
			par_lbl_minXwalk = new JLabel("Min X-Walk");
			par_lbl_minXwalk.setBounds(10, 90, 81, 20);
			panel_parameters.add(par_lbl_minXwalk);
			
			par_lbl_maxXwalk = new JLabel("Max X-Walk");
			par_lbl_maxXwalk.setBounds(10, 116, 81, 20);
			panel_parameters.add(par_lbl_maxXwalk);
			
			par_lbl_minYwalk = new JLabel("Min Y-Walk");
			par_lbl_minYwalk.setBounds(10, 142, 81, 20);
			panel_parameters.add(par_lbl_minYwalk);
			
			par_lbl_maxYwalk = new JLabel("Max Y-Walk");
			par_lbl_maxYwalk.setBounds(10, 172, 81, 20);
			panel_parameters.add(par_lbl_maxYwalk);
			
			lbl_speed = new JLabel("Speed");
			lbl_speed.setBounds(160, 8, 79, 20);
			panel_parameters.add(lbl_speed);
			
			JButton par_btn_changeAttributes = new JButton("Accept");
			par_btn_changeAttributes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					debugger.ec.editNPCinfo(selectedNPC);
				}
			});
			par_btn_changeAttributes.setBounds(465, 172, 89, 23);
			panel_parameters.add(par_btn_changeAttributes);
			
			/*
			 * Add Dialogue Message
			 */
			par_btn_addDialogue = new JButton("Add Msg");
			par_btn_addDialogue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selectedEntity != null){
						String message = par_ta_dialogue.getText();
						debugger.ec.addDialogueToList(message, (Entity_NPC_DialogueTest)selectedNPC);
						par_ta_dialogue.setText("");
						par_lbl_dialogueCount.setText(Integer.toString(debugger.ec.dialogueIndex));
					}
				}
			});
			par_btn_addDialogue.setBounds(304, 215, 84, 23);
			panel_parameters.add(par_btn_addDialogue);
				
			/*
			 * Remove Dialogue Message
			 */
			par_btn_removeDialogue = new JButton("Remove");
			par_btn_removeDialogue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (selectedEntity != null){
						par_ta_dialogue.setText( debugger.ec.removedialogueFromList((Entity_NPC_DialogueTest)selectedNPC) );
						par_lbl_dialogueCount.setText(Integer.toString(debugger.ec.dialogueIndex + 1));
					}
				}
			});
			par_btn_removeDialogue.setBounds(304, 249, 84, 23);
			panel_parameters.add(par_btn_removeDialogue);
			
			/*
			 * Left Arrow Button
			 */
			JButton par_btn_dialogueLeft = new JButton("\u25C4");
			par_btn_dialogueLeft.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selectedEntity != null){
						par_ta_dialogue.setText( debugger.ec.moveLeftMsg((Entity_NPC_DialogueTest)selectedNPC) );
						par_lbl_dialogueCount.setText(Integer.toString(debugger.ec.dialogueIndex + 1));
					}
				}
			});
			par_btn_dialogueLeft.setBounds(297, 281, 63, 23);
			panel_parameters.add(par_btn_dialogueLeft);
			
			
			/*
			 * Right Arrow Button
			 */
			JButton par_btn_dialogueRight = new JButton("\u25BA");
			par_btn_dialogueRight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selectedEntity != null){
						par_ta_dialogue.setText( debugger.ec.moveRightMsg((Entity_NPC_DialogueTest)selectedNPC));
						par_lbl_dialogueCount.setText(Integer.toString(debugger.ec.dialogueIndex + 1));
				
					}
				}
			});
			par_btn_dialogueRight.setBounds(358, 281, 63, 23);
			panel_parameters.add(par_btn_dialogueRight);
			
			par_co_helmetCombo = new JComboBox();
			par_co_helmetCombo.setModel(new DefaultComboBoxModel(debugger.ec.getHelmet()));
			par_co_helmetCombo.setToolTipText("The faction this entity belongs to...");
			par_co_helmetCombo.setBounds(322, 8, 111, 20);
			panel_parameters.add(par_co_helmetCombo);
			
			par_co_chestCombo = new JComboBox();
			par_co_chestCombo.setModel(new DefaultComboBoxModel(debugger.ec.getChest()));
			par_co_chestCombo.setToolTipText("The faction this entity belongs to...");
			par_co_chestCombo.setBounds(322, 37, 111, 20);
			panel_parameters.add(par_co_chestCombo);
			
			JComboBox par_co_legCombo = new JComboBox();
			par_co_legCombo.setModel(new DefaultComboBoxModel(debugger.ec.getLegs()));
			par_co_legCombo.setToolTipText("The faction this entity belongs to...");
			par_co_legCombo.setBounds(322, 65, 111, 20);
			panel_parameters.add(par_co_legCombo);
			
			par_co_bootCombo = new JComboBox();
			par_co_bootCombo.setModel(new DefaultComboBoxModel(debugger.ec.getBoots()));
			par_co_npcType.setModel(new DefaultComboBoxModel(new String[] {"- NPC Character -"}));
			par_co_bootCombo.setToolTipText("The faction this entity belongs to...");
			par_co_bootCombo.setBounds(322, 91, 111, 20);
			panel_parameters.add(par_co_bootCombo);
			
			par_co_gloveCombo = new JComboBox();
			par_co_gloveCombo.setModel(new DefaultComboBoxModel(debugger.ec.getGloves()));
			par_co_gloveCombo.setToolTipText("The faction this entity belongs to...");
			par_co_gloveCombo.setBounds(322, 117, 111, 20);
			panel_parameters.add(par_co_gloveCombo);
			
			par_co_shieldCombo = new JComboBox();
			par_co_shieldCombo.setModel(new DefaultComboBoxModel(debugger.ec.getShields()));
			par_co_shieldCombo.setToolTipText("The faction this entity belongs to...");
			par_co_shieldCombo.setBounds(322, 143, 111, 20);
			panel_parameters.add(par_co_shieldCombo);
			
			par_co_weaponCombo = new JComboBox();
			par_co_weaponCombo.setModel(new DefaultComboBoxModel(debugger.ec.getWeapons()));
			par_co_weaponCombo.setToolTipText("The faction this entity belongs to...");
			par_co_weaponCombo.setBounds(322, 172, 111, 20);
			panel_parameters.add(par_co_weaponCombo);
			
			par_lbl_helmet = new JLabel("Helmet");
			par_lbl_helmet.setBounds(274, 8, 79, 20);
			panel_parameters.add(par_lbl_helmet);
			
			par_lbl_chest = new JLabel("Chest");
			par_lbl_chest.setBounds(274, 37, 79, 20);
			panel_parameters.add(par_lbl_chest);
			
			par_lbl_legs = new JLabel("Legs");
			par_lbl_legs.setHorizontalAlignment(SwingConstants.LEFT);
			par_lbl_legs.setBounds(274, 65, 79, 20);
			panel_parameters.add(par_lbl_legs);
			
			par_lbl_boots = new JLabel("Boots");
			par_lbl_boots.setBounds(274, 91, 79, 20);
			panel_parameters.add(par_lbl_boots);
			
			par_lbl_gloves = new JLabel("Gloves");
			par_lbl_gloves.setBounds(274, 117, 79, 20);
			panel_parameters.add(par_lbl_gloves);
			
			par_lbl_shield = new JLabel("Shield");
			par_lbl_shield.setBounds(274, 143, 79, 20);
			panel_parameters.add(par_lbl_shield);
			
			par_lbl_weapon = new JLabel("Weapon");
			par_lbl_weapon.setBounds(274, 172, 79, 20);
			panel_parameters.add(par_lbl_weapon);
			
			cb_freeze = new JCheckBox("Freeze NPC");
			cb_freeze.setBounds(457, 204, 97, 23);
			panel_parameters.add(cb_freeze);
			
			par_lbl_dialogueCount = new JLabel("0");
			par_lbl_dialogueCount.setHorizontalAlignment(SwingConstants.CENTER);
			par_lbl_dialogueCount.setBounds(304, 315, 39, 23);
			panel_parameters.add(par_lbl_dialogueCount);
			
			par_rb_waypoints = new JRadioButton("Waypoint");
			par_rb_waypoints.setBounds(457, 230, 97, 23);
			panel_parameters.add(par_rb_waypoints);
			

			cb_freeze.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if 		(cb_freeze.isSelected())	debugger.ec.haltNPC();
					else 								debugger.ec.activateNPC();
				}
			});
			
			
			
			
			///////////// End Parameter Panel \\\\\\\\\\\\\\\\
			
			
			//////////////Level Connector Panel \\\\\\\\\\\\\\\\\\\
			/*
			 * The Panel containing the items for level connector operations
			 */
			panel_levelConnector = new JPanel();
			panel_levelConnector.setBackground(Color.LIGHT_GRAY);
			panel_levelConnector.setBounds(10, 118, 564, 43);
			this.panel_levelConnector.setLayout(null);
			this.panel_levelConnector.setVisible(false);
			panel.add(panel_levelConnector);
			
			textField = new JTextField();
			panel_levelConnector.add(textField);
			textField.setColumns(10);
			
			/*
			 * The text field containing the path
			 */
			lc_tf_path = new JTextField();
			lc_tf_path.setBounds(10, 11, 440, 20);
			panel_levelConnector.add(lc_tf_path);
			lc_tf_path.setColumns(10);
			lc_tf_path.setEditable(false);
			
			/*
			 * Browse button for choosing level for file connector
			 */
			lc_btn_browse = new JButton("Browse");
			lc_btn_browse.setBounds(460, 10, 94, 23);
			panel_levelConnector.add(lc_btn_browse);
			
			
			///////////// End Level Connector Panel \\\\\\\\\\\\\\\\
			
	//******************END MAIN PANEL************************
				
	
	
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
		
		
		//When user begins selecting radio buttons
		if (rb_tile.isSelected()) {
			this.selectedEntity = null;
			this.selectedDroppedItem = null;
			this.selectedLevelConnector = null;
			this.selectedNPC = null;
			this.selectedObject = null;
		}
		
		else if (rb_npc.isSelected()) {
			this.selectedDroppedItem = null;
			this.selectedLevelConnector = null;
			this.selectedTile = null;
			this.selectedObject = null;
		}
		
		else if (this.rb_levelObjects.isSelected()) {
			this.selectedDroppedItem = null;
			this.selectedLevelConnector = null;
			this.selectedNPC = null;
			this.selectedTile = null;
		}
		///////////////////////////
		
		if (this.selectedEntity != null && this.par_rb_waypoints.isSelected()){
			if (Mouse.lastButtonPressed == 1 && this.debugger.clickDelay == 0) {
				Mouse.lastButtonPressed = -1;
				double xx = this.debugger.getMapX() - (this.debugger.x  - StdDraw.mouseX());
				double yy  = this.debugger.getMapY() - (this.debugger.y  - StdDraw.mouseY());
				this.debugger.ec.addWaypointForSelectedEntity(this.debugger.level, (Entity_Character)this.selectedEntity, xx, yy);
			}
		}
		
		//Placing entity
		if (this.selectedEntity != null) {
			if (cb_selection.getSelectedItem().equals("NPC Test")) {
				if (Mouse.lastButtonPressed == 1 && this.debugger.clickDelay == 0) {
					Mouse.lastButtonPressed = -1;
					this.debugger.clickDelay = this.debugger.clickDelayAmount;
					double xx = this.debugger.getMapX() - (this.debugger.x  - StdDraw.mouseX());
					double yy  = this.debugger.getMapY() - (this.debugger.y  - StdDraw.mouseY());
				}
			}
			//If you click on the NPC again, after selecting, cause it to be deselected
			if (this.entityIsSelected == true) {
				if (Mouse.lastButtonPressed == 1 && this.debugger.clickDelay == 0) {
					Mouse.lastButtonPressed = -1;
					this.entityIsSelected = false;
				}
				this.panel_parameters.setVisible(true);		// Display the panel if selected
			}
			// Handle events if the NPC is deslected or radio button is deselected
			if (rb_npc.isSelected() == false || this.entityIsSelected == false) {
				this.entityIsSelected = false;
			}
		}
		///////////////////////////
		
		//Placing tile
		if (this.selectedTile != null) {
			if (Mouse.lastButtonPressed == 1 && this.debugger.clickDelay == 0){
				Mouse.lastButtonPressed = -1;
				this.debugger.clickDelay = this.debugger.clickDelayAmount;
				for (int yy = 0; yy < this.debugger.level.tiles.length; yy++) {
					for (int xx = 0; xx < this.debugger.level.tiles[yy].length; xx++) {
						if (this.debugger.level.tiles[xx][yy].equals(this.selectedTile)) {
							debugger.ec.addTile((String)this.cb_selection.getSelectedItem(), xx, yy);
							this.selectedTile = null;
						}
					}
				}
			}
		}
		///////////////////////////
		else {
			String comboSelection = (String)this.cb_selection.getSelectedItem();
			if (Mouse.lastButtonPressed == 1 && this.debugger.clickDelay == 0) {
				//Mouse.lastButtonPressed = -1; - WAS CAUSING TILES TO NOT BE ABLE TO BE SELECTED
			
				double xx = this.debugger.getMapX() - (this.debugger.x - StdDraw.mouseX());
				double yy  = this.debugger.getMapY() - (this.debugger.y - StdDraw.mouseY());
			
				switch(comboSelection) {
				
				// Level Objects
				case "Brick House 1":
				case "Horiz. Brick Wall":
				case "Vert. Brick Wall":
				case "Candle":
				case "Rock 1":
				case "Rock 2":
				case "Rock Wall":
				case "Tree Base 1":
				case "Tree Trunk 1":
				case "Tree Top 1":
					debugger.ec.addObject(comboSelection, xx, yy);
					break;
				
				// NPC's 
				case "NPC Test":
				case "NPC Dialogue Test":
				case "NPC Shop Owner":
					debugger.ec.addNPC(comboSelection, xx, yy);
				
				default:
					break;
				}
			}
		}
	}
	
	public void render() {
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////FOR ANYTHING SELECTED/////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (this.selectedEntity != null) {
				
				double[] xCoords, yCoords;
					
				//Render in game
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				xCoords = new double[]{this.selectedEntity.x - this.selectedEntity.minX, this.selectedEntity.x - this.selectedEntity.minX, this.selectedEntity.x + this.selectedEntity.maxX, this.selectedEntity.x + this.selectedEntity.maxX};
				yCoords = new double[]{this.selectedEntity.y + this.selectedEntity.maxY * 2, this.selectedEntity.y - this.selectedEntity.minY, this.selectedEntity.y - this.selectedEntity.minY, this.selectedEntity.y + this.selectedEntity.maxY * 2};
				StdDraw.polygon(xCoords, yCoords);
				StdDraw.setPenRadius();
			}
		
			else if (this.selectedTile != null) {
				//Render in game
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				StdDraw.square(this.selectedTile.x, this.selectedTile.y, this.selectedTile.level.tileSize);
				StdDraw.setPenRadius();
				////////////////////////////
			}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////FOR ANYTHING NEW//////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			//Level Objects
			else if (this.cb_selection.getSelectedItem().equals("Brick House 1")) {
				//Render outline
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY(), 28.5, 24.2);
				StdDraw.setPenRadius();
				////////////////////////////
			}
			
			else if (this.cb_selection.getSelectedItem().equals("Horiz. Brick Wall")) {
				//Render outline
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY(), 28, 14.5);
				StdDraw.setPenRadius();
				////////////////////////////
			}
			
			else if (this.cb_selection.getSelectedItem().equals("Vert. Brick Wall")) {
				//Render outline
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY(), 14.5, 28);
				StdDraw.setPenRadius();
				////////////////////////////
			}
			
			else if (this.cb_selection.getSelectedItem().equals("Rock 1")   	|| 
					 this.cb_selection.getSelectedItem().equals("Rock 2")    	|| 
					 this.cb_selection.getSelectedItem().equals("Rock Wall") 	||
					 this.cb_selection.getSelectedItem().equals("Tree Base 1" )) {
				//Render outline
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY(), 7.2, 7.2);
				StdDraw.setPenRadius();
				////////////////////////////
			}
			
			else if (this.cb_selection.getSelectedItem().equals("Tree Trunk 1")) {
				//Render outline
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY(), 3.5, 3.5);
				StdDraw.setPenRadius();
				////////////////////////////
			}
			
			else if (this.cb_selection.getSelectedItem().equals("Tree Top 1")) {
				//Render outline
				StdDraw.setPenColor(this.selectionColor);
				StdDraw.setPenRadius(0.0058);
				StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY(), 14.2, 14.2);
				StdDraw.setPenRadius();
				////////////////////////////
			}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	}
	
	/**
	 * Set the information of the selected NPC into all of the text fields,
	 * check boxes, etc so that you can view or alter them
	 * 
	 * @param selectedNPC
	 */
	public void setSelectedEntityInfo(Entity_NPC selectedNPC) {
		par_cb_canAttack.setSelected(selectedNPC.canAttack);
		par_cb_canBeAttacked.setSelected(selectedNPC.canBeAttacked);
		par_cb_aggressive.setSelected(selectedNPC.aggressive);
		par_cb_canSpeak.setSelected(selectedNPC.canTalk);
		
		par_tb_name.setText(selectedNPC.name);
		par_tb_maxHealth.setText(String.valueOf(selectedNPC.maxHealth));
		par_tb_currentHealth.setText(String.valueOf(selectedNPC.currentHealth));
		par_tb_minXwalk.setText(String.valueOf(selectedNPC.minXWalk));
		par_tb_maxXwalk.setText(String.valueOf(selectedNPC.maxXWalk));
		par_tb_minYwalk.setText(String.valueOf(selectedNPC.minYWalk));
		par_tb_maxYwalk.setText(String.valueOf(selectedNPC.maxYWalk));
		par_tb_speed.setText(String.valueOf(selectedNPC.speed));
		
		par_co_faction.setSelectedItem(selectedNPC.faction);
	}
	
	/**
	 * Set everything within the GUI back to its original state.
	 */
	public void resetComponents(){
		
		/*
		 * Panels
		 */
		panel_parameters.setVisible(false);
		panel_levelConnector.setVisible(false);
		
		/*
		 * Main panel 
		 */
		rb_tile.setSelected(false);
		rb_npc.setSelected(false);
		rb_levelObjects.setSelected(false);
		rb_levelConnector.setSelected(false);
		
		cb_freeze.setSelected(false);
		cb_selection.setSelectedIndex(0);
		
		/*
		 * Parameter panel 
		 */
		par_co_faction.setSelectedIndex(0);
		par_co_npcType.setSelectedIndex(0);
		par_co_helmetCombo.setSelectedIndex(0);
		par_co_chestCombo.setSelectedIndex(0);
		par_co_bootCombo.setSelectedIndex(0);
		par_co_gloveCombo.setSelectedIndex(0);
		par_co_shieldCombo.setSelectedIndex(0);
		par_co_weaponCombo.setSelectedIndex(0);
		
		par_cb_canAttack.setSelected(false);
		par_cb_canBeAttacked.setSelected(false);
		par_cb_aggressive.setSelected(false);
		par_cb_canSpeak.setSelected(false);
		
		par_ta_dialogue.setText("");
		par_tb_name.setText("");
		par_tb_maxHealth.setText("");
		par_tb_currentHealth.setText("");
		par_tb_minXwalk.setText("");
		par_tb_maxXwalk.setText("");
		par_tb_minYwalk.setText("");
		par_tb_maxYwalk.setText("");
		par_tb_speed.setText("");
		
		/*
		 *	Level Connector Panel 
		 */
		lc_tf_path.setText("");
		
	}
	
	/**
	 * The frame to display when creating a new map. Will contain
	 * a few features for setting up the map layout.
	 */
	public void createNewGameFrame(){
		newGameFrame = new JFrame();			// Let's generate a new frame
		newGameFrame.setBounds(100, 100, 500, 250);
		newGameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		newGameFrame.setVisible(true);
		
		//Begin panel creation on frame
		
			JPanel newGamePanel = new JPanel();
			newGamePanel = new JPanel();
			newGamePanel.setVisible(true);
			newGameFrame.getContentPane().add(newGamePanel);
			newGamePanel.setLayout(null);
			
			//Begin component creation on panel

				final JTextField ngp_tf_mapLength = new JTextField("100");
				final JTextField ngp_tf_mapWidth = new JTextField("100");

				final JComboBox ngp_co_mapTerrain = new JComboBox();
				ngp_co_mapTerrain.setModel(new DefaultComboBoxModel(debugger.ec.getTiles()));
				
				JLabel ngp_label_mapWidth = new JLabel("Width");
				JLabel ngp_label_mapLength = new JLabel("Length");
				
				JButton acceptNewMap = new JButton("Accept");
				acceptNewMap.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int xx = Integer.parseInt(ngp_tf_mapWidth.getText());
						int yy = Integer.parseInt(ngp_tf_mapLength.getText());
						String terrain = (String) ngp_co_mapTerrain.getSelectedItem();
						debugger.ec.newMap(xx, yy, terrain);
						
						newGameFrame.dispose();
					}
				});
				
				ngp_tf_mapLength.setBounds(150, 8, 109, 20);
				ngp_tf_mapWidth.setBounds(150, 28, 109, 20);
				ngp_co_mapTerrain.setBounds(326, 8, 109, 20);
				ngp_label_mapWidth.setBounds(20, 28, 109, 20);
				ngp_label_mapLength.setBounds(20, 8, 109, 20);
				acceptNewMap.setBounds(20, 58, 80, 20);
				
				newGamePanel.add(ngp_tf_mapLength);
				newGamePanel.add(ngp_tf_mapWidth);
				newGamePanel.add(ngp_co_mapTerrain);
				newGamePanel.add(ngp_label_mapWidth);
				newGamePanel.add(ngp_label_mapLength);
				newGamePanel.add(acceptNewMap);
				
			//End component creation on panel
			
		//End panel creation frame
	}
	
	
	/**
	 * Set up the custom cursor for the map editor
	 */
	/*private void setCustomCursor() {
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
	    ////////////////////////////
	    
	    frame.setCursor(cursor);
	    StdDraw.frame.setCursor(cursor);
	}*/
}
