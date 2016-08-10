import java.io.Serializable;
import java.util.ArrayList;

public class Waypoint extends Entity_Character implements Serializable {
	
	public boolean shouldRender = false;

	private String ID;
	private final String waypoint_photo = "waypoint_marker.png";
	private Entity_Character entity;
	private int waypointCount;				// The count for identification of each waypoint
	
	private boolean q1 = false, q2 = false, q3 = false, q4 = false, 
					front = false, back = false, up = false, down = false;
	
	
	// A* Path Finding
	private int closedListIndex = 0;
	private ArrayList<PathNode> openList = new ArrayList<>();
	private ArrayList<PathNode> closedList = new ArrayList<>();
	private PathNode firstNode;
	
	private double[] waypointCoordinates = new double[2];
	private double[] entityCoordinates = new double[2];
	
	private boolean reachedDestination = false;
	
	private PathNode currentNode;
	//////////////////
	
	
	public Waypoint(Level level, Entity_Character entity, double mapX, double mapY, int ID_number) {
		this.level = level;
		setMapX(mapX);
		setMapY(mapY);
		this.ID = ( "Waypoint " + ID_number );
		
		this.isSolid = false;
		this.minX = 0;
		this.maxX = 0;
		this.minY = 0;
		this.maxY = 0;
		
		this.entity = entity;
		
		double distance = ( this.entity.speed - this.getMapX() ) + ( this.entity.speed - this.getMapY() );
		firstNode = new PathNode(0, distance, this.entity.getMapX(), this.entity.getMapY());
		currentNode = firstNode;
		closedList.add(firstNode);		
	
	}

	public String getID() {
		return ( this.ID );
	}

	@Override
	public void render() {
		StdDraw.picture(this.x, this.y, this.waypoint_photo);	
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		if (Game.player.level.equals(this.level)) {
			setRenderCoordinates();
			if (!(this.x + this.maxX < 0) && !(this.x - this.minX > 100) && !(this.y + this.maxY * 2 < 0) && !(this.y - this.minY > 100)) {
				this.shouldRender = true;
			} else {
				this.shouldRender = false;
			}
		} else {
			this.shouldRender = false;
		}
		if ( !Main.game.world.currentPlayer.level.waypoints.isEmpty() ) {
			//this.entity.isInCombat = false;
			followPath();
		}
	}
	
	private void setRenderCoordinates() {
		this.x = Game.player.x + (this.getMapX() - Game.player.getMapX());
		this.y = Game.player.y + (this.getMapY() - Game.player.getMapY());
	}

	@Override
	public void checkTimers() {
		// TODO Auto-generated method stub
		
	}
	
	public void followPath() {
		currentNode = calculateSurroundingNodes();
		
	}
		
	private PathNode calculateSurroundingNodes() {
		double tempGcost = 0;
		double tempHcost = 0;
		double tempMapX = 0;
		double tempMapY = 0;
		double x = 0;
		double y = 0;
		
		// G Cost : Distance from starting node
		// H Cost : Distance from ending node
		// F Cost : G Cost + H Cost
		
		// Top Left Node
		x = ( this.entity.getMapX() - this.entity.speed );
		y = ( this.entity.getMapY() + this.entity.speed );
		tempGcost = x + y;
		
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
		
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		// Left Node
		x = ( this.entity.getMapX() - this.entity.speed );
		y = 0;
		tempGcost = x;
		
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
				
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		// Bottom Left Node
		x = ( this.entity.getMapX() - this.entity.speed );
		y = ( this.entity.getMapY() - this.entity.speed );
		tempGcost = x + y;
						
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
						
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		
		// Bottom Node
		x = 0;
		y = ( this.entity.getMapY() - this.entity.speed );
		tempGcost = x + y;
				
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
								
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		// Bottom Right Node
		x = ( this.entity.getMapX() + this.entity.speed );
		y = ( this.entity.getMapY() - this.entity.speed );
		tempGcost = x + y;
				
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
										
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		// Right Node
		x = ( this.getMapX() + this.entity.speed );
		y = 0;
		tempGcost = x + y;
				
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
												
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		
		// Top Right Node
		x = ( this.getMapX() + this.entity.speed );
		y = ( this.getMapY() + this.entity.speed );
		tempGcost = x + y;
			
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
														
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		
		// Top Node
		x = 0;
		y = ( this.getMapY() + this.entity.speed );
		tempGcost = x + y;
				
		tempMapX = x;
		tempMapY = y;
		
		x = x + ( this.getMapX() );
		y = y + ( this.getMapY() );
		tempHcost = x + y;
																
		openList.add( new PathNode( tempGcost, tempHcost, tempMapX, tempMapY ));
		//////////////////////////////////////////////////////
		
		// Find out which node has the smallest F-cost and set that as the
		// most efficient path to travel. Adds to the closed list that contains
		// all of the nodes that will make up the pathway.
		PathNode currentSmallest = openList.get(1);
		for ( PathNode pn : openList ) {
			if (currentSmallest.Fcost > pn.Fcost ) {
				currentSmallest = pn;
			}
		}
		closedList.add(currentSmallest);
		openList.clear();
		
		return ( currentSmallest );
	}
}
