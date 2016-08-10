import java.io.Serializable;
import java.util.ArrayList;

public class Waypoint_Path implements Serializable {

	public ArrayList<Waypoint> path;		// The list of Waypoint's that will serve as a path
	private Entity_Character entity;					// The entity to follow the pathway 
	private boolean isLooped;				// If the path will be an endless loop for the entity
	private int waypointCount;				// The count for identification of each waypoint
	
	private boolean q1 = false, q2 = false, q3 = false, q4 = false, 
					front = false, back = false, up = false, down = false;
	private boolean reachedDestination = false;
	
	private Waypoint lastWaypoint;
	
	/**
	 * Constructor
	 */
	public Waypoint_Path(Entity_Character e) {
		path = new ArrayList<Waypoint>();
		isLooped = false;
		waypointCount = 0;
		this.entity = e;
	}
	
	/**
	 * Add a waypoint to the path list. Increment the count for identification purposes.
	 * 
	 * @param xx 
	 * @param yy
	 */
	public void addWaypoint(Level level, Entity_Character entity, double xx, double yy) {
		Waypoint new_waypoint = new Waypoint(level, entity, xx, yy, waypointCount);
		path.add(new_waypoint);
		
		waypointCount++;	
		lastWaypoint = new_waypoint;
	}
	
	/**
	 * Remove a waypoint from the list by scanning for the similar ID's
	 * 
	 * @param w
	 */
	public void removeWaypoint(Waypoint w) {
		for (Waypoint waypoint : path) {
			if ( w.getID().equals(waypoint.getID())) {
				path.remove(waypoint);
			}
		}
	}
	
	/**
	 * Returns the array list containing the path
	 * @return
	 */
	public ArrayList getPath() {
		return ( path );
	}
	
	public void followPath() {
		while ( reachedDestination == false ) {
			boolean nextLocation = locateTarget();
			
			if ( this.entity.lastTarget.getMapX() == this.entity.getMapX() && this.entity.lastTarget.getMapY() == this.entity.getMapY()) {
				reachedDestination = true;
			} else {
				this.entity.lastTarget = this.path.get(waypointCount);
				
				if ( q1) {
					this.entity.moveTo(this.entity.getMapX() + 1, this.entity.getMapY() + 1, this.entity.getMovingDir());
				}
				else if ( q2 ) {
					this.entity.moveTo(this.entity.getMapX() - 1, this.entity.getMapY() + 1, this.entity.getMovingDir());
				}
				else if ( q3 ) {
					this.entity.moveTo(this.entity.getMapX() - 1, this.entity.getMapY() - 1, this.entity.getMovingDir());
				}
				else if ( q4 ) {
					this.entity.moveTo(this.entity.getMapX() + 1, this.entity.getMapY() - 1, this.entity.getMovingDir());
				}
				else if ( up ) {
					this.entity.moveTo(this.entity.getMapX(), this.entity.getMapY() + 1, this.entity.getMovingDir());
				}
				else if ( down ) {
					this.entity.moveTo(this.entity.getMapX(), this.entity.getMapY() - 1, this.entity.getMovingDir());
				}
				else if ( front ) {
					this.entity.moveTo(this.entity.getMapX() + 1, this.entity.getMapY(), this.entity.getMovingDir());
				}
				else if ( back ) {
					this.entity.moveTo(this.entity.getMapX() - 1, this.entity.getMapY(), this.entity.getMovingDir());
				}
			}
		}
	}
	
	private boolean locateTarget() {
		if (this.entity.lastTarget.getMapX() > this.entity.getMapX() && this.entity.lastTarget.getMapY() > this.entity.getMapY()) {
			q1 = true;
			q2 = false;
			q3 = false;
			q4 = false;
			up = false;
			down = false;
			front = false;
			back = false;
			
			return ( q1 );
		}
		else if (this.entity.lastTarget.getMapX() < this.entity.getMapX() && this.entity.lastTarget.getMapY() > this.entity.getMapY()) {
			q1 = false;
			q2 = true;
			q3 = false;
			q4 = false;
			up = false;
			down = false;
			front = false;
			back = false;
			
			return ( q2 );
		}
		else if (this.entity.lastTarget.getMapX() < this.entity.getMapX() && this.entity.lastTarget.getMapY() < this.entity.getMapY()) {
			q1 = false;
			q2 = false;
			q3 = true;
			q4 = false;
			up = false;
			down = false;
			front = false;
			back = false;
			
			return ( q3 );
		}
		else if (this.entity.lastTarget.getMapX() > this.entity.getMapX() && this.entity.lastTarget.getMapY() < this.entity.getMapY()) {
			q1 = false;
			q2 = false;
			q3 = false;
			q4 = true;
			up = false;
			down = false;
			front = false;
			back = false;
			
			return ( q4 );
		}
		else if (this.entity.lastTarget.getMapX() == this.entity.getMapX() && this.entity.lastTarget.getMapY() > this.entity.getMapY()) {
			q1 = false;
			q2 = false;
			q3 = false;
			q4 = false;
			up = true;
			down = false;
			front = false;
			back = false;
			
			return ( up );
		}
		else if (this.entity.lastTarget.getMapX() == this.entity.getMapX() && this.entity.lastTarget.getMapY() < this.entity.getMapY()) {
			q1 = false;
			q2 = false;
			q3 = false;
			q4 = false;
			up = false;
			down = true;
			front = false;
			back = false;
			
			return ( down );
		}
		else if (this.entity.lastTarget.getMapX() > this.entity.getMapX() && this.entity.lastTarget.getMapY() == this.entity.getMapY()) {
			q1 = false;
			q2 = false;
			q3 = false;
			q4 = false;
			up = false;
			down = false;
			front = true;
			back = false;
			
			return ( front );
		}
		else if(this.entity.lastTarget.getMapX() < this.entity.getMapX() && this.entity.lastTarget.getMapY() == this.entity.getMapY()) {
			q1 = false;
			q2 = false;
			q3 = false;
			q4 = false;
			up = false;
			down = false;
			front = false;
			back = true;
			
			return ( back );
		}
		else {
			return ( true );
		}
		
	}
	
}
