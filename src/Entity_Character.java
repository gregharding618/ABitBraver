import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Entity_Character extends Entity implements Serializable {

	private static final long serialVersionUID = 6534068059585422556L;
	
	public static final int MAX_ATTACK_DELAY = 80;
	
	public int 	currentHealth, 
				maxHealth, 
				maxHit = 1, 
				armor = 0, 
				meleeBoost = 0, 
				archeryBoost = 0, 
				magicBoost = 0, 
				tempAccuracyBoost = 0,
				tempMaxHitBoost = 0, 
				tempMeleeBoost = 0, 
				tempArcheryBoost = 0, 
				tempMagicBoost = 0,
				tempDefenseBoost = 0, 
				attackDelay = 0, 
				attackDelayTimer = 0, 
				combatTimer = 0;
	
	public double 	accuracy = 1, 
					energy = 1000, 
					maxEnergy = 1000;
	
	public Item_Equipment 	helmet, 
							chest, 
							legs, 
							boots, 
							gloves, 
							weapon, 
							shield, 
							ammo;
	
	public Entity_Character lastTarget;
		
	public long respawnTimer = 0;
	
	protected int combatDelay = 1500;
	
	public long tempMeleeBoostTimer = 0, 
				tempArcheryBoostTimer = 0, 
				tempMagicBoostTimer = 0, 
				tempDefenseBoostTimer = 0, 
				tempAccuracyBoostTimer = 0, 
				tempMaxHitBoostTimer = 0;
	
	//Entity levels
	public int 	overallLevel = 1, 
				defense = 1, 
				melee = 1, 
				archery = 1, 
				magic = 1;
	
	protected boolean updatePathFind = true;
	protected List<Double> pathfindX = new ArrayList<Double>();
	protected List<Double> pathfindY = new ArrayList<Double>();
	protected double destinationX = 0, destinationY = 0;
	
	public boolean canAttack = false, canBeAttacked = false, isInCombat = false;
	public String faction = "";
	public int selectedAbility = 0; //0 = none, 1 - 6 are selected abilities
	public double knockbackX = 0, knockbackY = 0;
	public boolean canMove = true;
	public boolean isFrozen = false;
	
	private int pathFindUpdates = 0;
	private List<ArrayList<Integer>> badCoordinates = new ArrayList<ArrayList<Integer>>();
	protected List<ArrayList<Integer>> previousCoordinates = new ArrayList<ArrayList<Integer>>();

	public List<Effect> activeEffects = new ArrayList<Effect>();
	public List<Effect> activeEffectsToAdd = new ArrayList<Effect>();
	public List<Effect> activeEffectsToRemove = new ArrayList<Effect>();
	
	public List<Effect_Positive> positiveEffects = new ArrayList<Effect_Positive>();
	public List<Effect_Positive> positiveEffectsToAdd = new ArrayList<Effect_Positive>();
	public List<Effect_Positive> positiveEffectsToRemove = new ArrayList<Effect_Positive>();
	
	public List<Effect_Negative> negativeEffects = new ArrayList<Effect_Negative>();
	public List<Effect_Negative> negativeEffectsToAdd = new ArrayList<Effect_Negative>();
	public List<Effect_Negative> negativeEffectsToRemove = new ArrayList<Effect_Negative>();
	
	public void updateMinimapColor() {
		if (this.level.world.currentPlayer.level.equals(this.level)) {
			this.miniMapColor = StdDraw.RED; 
			if (this.faction.equals(this.level.world.currentPlayer.faction)) this.miniMapColor = StdDraw.YELLOW;
			if (!this.canAttack || !this.canBeAttacked) this.miniMapColor = StdDraw.GREEN;
		}
	}
	
	public void moveToLocation(double xx, double yy){
		this.pathFind(xx, yy, 10);
	}
	
	protected boolean pathFindV2(double destX, double destY, double leeway, double distanceLimit) {
		this.badCoordinates.clear();
		this.previousCoordinates.clear();
		this.pathfindX.clear();
		this.pathfindY.clear();
		
		//If within acceptable range of destination coordinates, pathfinding not needed
		if (((getMapX() <= destX && getMapX() + leeway >= destX) || (getMapX() >= destX && getMapX() - leeway <= destX)) && 
			((getMapY() <= destY && getMapY() + leeway >= destY) || (getMapY() >= destY && getMapY() - leeway <= destY))) {
			
			return true;
		}
		//////////////////////
		
		//Add the current coordinates as "bad" coordinates on the "grid"
		ArrayList<Integer> badCoords = new ArrayList<Integer>();
		badCoords.add(0);
		badCoords.add(0);
		this.badCoordinates.add(badCoords);
		//////////////////////
		
		//Add the current coordinates to the "grid"
		ArrayList<Integer> previousCoords = new ArrayList<Integer>();
		previousCoords.add(0);
		previousCoords.add(0);
		this.previousCoordinates.add(previousCoords);
		//////////////////////
		
		double currentMapX = getMapX();
		double currentMapY = getMapY();
		int currentGridX = 0;
		int currentGridY = 0;
		int maxChecks = (int) (distanceLimit / this.speed);
		int checks = 0;
		List<ArrayList<Integer>> checkList = new ArrayList<ArrayList<Integer>>();
		boolean searching = true;
		
		while (searching) {
			//If within acceptable range of destination coordinates, pathfinding is completed
			if (((currentMapX <= destX && currentMapX + leeway >= destX) || (currentMapX >= destX && currentMapX - leeway <= destX)) && 
				((currentMapY <= destY && currentMapY + leeway >= destY) || (currentMapY >= destY && currentMapY - leeway <= destY))) {
				
				//Create walk pattern for the entity
				int lastX = 0, lastY = 0;
				for (ArrayList<Integer> list : this.previousCoordinates) {
					if (list.get(0) == 0 && list.get(1) == 0) continue;
					
					if (list.get(0) > lastX) this.pathfindX.add(this.speed);
					else if (list.get(0) < lastX) this.pathfindX.add(-this.speed);
					else this.pathfindX.add(0.0);
					
					if (list.get(1) > lastY) this.pathfindY.add(this.speed);
					else if (list.get(1) < lastY) this.pathfindY.add(-this.speed);
					else this.pathfindY.add(0.0);
					
					lastX = list.get(0);
					lastY = list.get(1);
				}
				//////////////////////
				
				this.destinationX = destX;
				this.destinationY = destY;
				return true;
			}
			//////////////////////
			
			//Set up check sequence; 1 = up, 2 = right, 3 = down, 4 = left
			int[] bestCheckSequence = null;
			ArrayList<Integer> checkSequence = new ArrayList<Integer>();
			
			if (checkList.isEmpty()) {
				bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
				
				for (int i : bestCheckSequence) {
					checkSequence.add(i);
				}
				checkList.add(checkSequence);
			}
			/////////////////////////
			
			//Use current check sequence
			for (int i : checkList.get(checkList.size() - 1)) {
				boolean addCoordinates = true;
				for (ArrayList<Integer> plist : this.previousCoordinates) {
					if (plist.get(0) == currentGridX && plist.get(1) == currentGridY) {
						addCoordinates = false;
						break;
					}
				}
					
				if (addCoordinates) {
					ArrayList<Integer> coords = new ArrayList<Integer>();
					coords.add(currentGridX);
					coords.add(currentGridY);
					this.previousCoordinates.add(coords);
				}
				
				//If within acceptable range of destination coordinates, pathfinding is completed
				if (((currentMapX <= destX && currentMapX + leeway >= destX) || (currentMapX >= destX && currentMapX - leeway <= destX)) && 
					((currentMapY <= destY && currentMapY + leeway >= destY) || (currentMapY >= destY && currentMapY - leeway <= destY))) {
					
					//Create walk pattern for the entity
					int lastX = 0, lastY = 0;
					for (ArrayList<Integer> list : this.previousCoordinates) {
						if (list.get(0) == 0 && list.get(1) == 0) continue;
						
						if (list.get(0) > lastX) this.pathfindX.add(this.speed);
						else if (list.get(0) < lastX) this.pathfindX.add(-this.speed);
						else this.pathfindX.add(0.0);
						
						if (list.get(1) > lastY) this.pathfindY.add(this.speed);
						else if (list.get(1) < lastY) this.pathfindY.add(-this.speed);
						else this.pathfindY.add(0.0);
						
						lastX = list.get(0);
						lastY = list.get(1);
					}
					//////////////////////
					
					this.destinationX = destX;
					this.destinationY = destY;
					return true;
				}
				//////////////////////
				
				checkList.get(checkList.size() - 1).remove(0);
				if (checkList.get(checkList.size() - 1).isEmpty()) checkList.remove(checkList.size() - 1);
				
				boolean nextCheck = false;
				
				switch (i) {
				
				//Up
				case 1:
					if (!willCollide(currentMapX, currentMapY + this.speed)) {

						for (ArrayList<Integer> list : this.badCoordinates) {
							if (list.get(0) == currentGridX && list.get(1) == currentGridY + 1) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;

						for (ArrayList<Integer> list : this.previousCoordinates) {
							if (list.get(0) == currentGridX && list.get(1) == currentGridY + 1) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;
						
						if (currentMapY < destY && currentMapY + this.speed > destY && ((destX > currentMapX && !willCollide(currentMapX + this.speed, currentMapY + (destY - currentMapY))) || (destX < currentMapX && !willCollide(currentMapX - this.speed, currentMapY + (destY - currentMapY))))) {
							currentMapY += destY - currentMapY;
							currentGridY++;
							
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
							
						} else {
							currentMapY += this.speed;
							currentGridY++;
							
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
						}
					} else {
						ArrayList<Integer> badUp = new ArrayList<Integer>();
						badUp.add(currentGridX);
						badUp.add(currentGridY + 1);
						this.badCoordinates.add(badUp);
					}
					break;
					
				//Right
				case 2:
					if (!willCollide(currentMapX + this.speed, currentMapY)) {

						for (ArrayList<Integer> list : this.badCoordinates) {
							if (list.get(0) == currentGridX + 1 && list.get(1) == currentGridY) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;

						for (ArrayList<Integer> list : this.previousCoordinates) {
							if (list.get(0) == currentGridX + 1 && list.get(1) == currentGridY) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;
							
						if (currentMapX < destX && currentMapX + this.speed > destX && ((destY > currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY + this.speed)) || (destY < currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY - this.speed)))) {
							currentMapX += destX - currentMapX;
							currentGridX++;
								
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
								
						} else {
							currentMapX += this.speed;
							currentGridX++;
								
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
						}
					} else {
						ArrayList<Integer> badRight = new ArrayList<Integer>();
						badRight.add(currentGridX + 1);
						badRight.add(currentGridY);
						this.badCoordinates.add(badRight);
					}
					break;
					
				//Down
				case 3:
					if (!willCollide(currentMapX, currentMapY - this.speed)) {

						for (ArrayList<Integer> list : this.badCoordinates) {
							if (list.get(0) == currentGridX && list.get(1) == currentGridY - 1) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;

						for (ArrayList<Integer> list : this.previousCoordinates) {
							if (list.get(0) == currentGridX && list.get(1) == currentGridY - 1) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;
						
						if (currentMapX < destX && currentMapX + this.speed > destX && ((destY > currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY + this.speed)) || (destY < currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY - this.speed)))) {
							currentMapY += destY - currentMapY;
							currentGridY--;
							
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
							
						} else {
							currentMapY -= this.speed;
							currentGridY--;
							
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
						}
					} else {
						ArrayList<Integer> badDown = new ArrayList<Integer>();
						badDown.add(currentGridX);
						badDown.add(currentGridY - 1);
						this.badCoordinates.add(badDown);
					}
					break;
					
				//Left
				case 4:
					if (!willCollide(currentMapX - this.speed, currentMapY)) {
						
						for (ArrayList<Integer> list : this.badCoordinates) {
							if (list.get(0) == currentGridX - 1 && list.get(1) == currentGridY) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;
						
						for (ArrayList<Integer> list : this.previousCoordinates) {
							if (list.get(0) == currentGridX - 1 && list.get(1) == currentGridY) {
								nextCheck = true;
								break;
							}
						}
						if (nextCheck) continue;
						
						if (currentMapX > destX && currentMapX + this.speed < destX && ((destY > currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY + this.speed)) || (destY < currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY - this.speed)))) {
							currentMapX += destX - currentMapX;
							currentGridX--;
							
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
							
						} else {
							currentMapX -= this.speed;
							currentGridX--;
							
							bestCheckSequence = createCheckSequence(destX, destY, currentMapX, currentMapY);
							checkSequence = new ArrayList<Integer>();
							for (int index : bestCheckSequence) {
								checkSequence.add(index);
							}
							checkList.add(checkSequence);
						}
					} else {
						ArrayList<Integer> badLeft = new ArrayList<Integer>();
						badLeft.add(currentGridX - 1);
						badLeft.add(currentGridY);
						this.badCoordinates.add(badLeft);
					}
					break;
				
				}
				
			}
			/////////////////////////
			
			if (currentGridX == 0 && currentGridY == 0) searching = false;
			
			else {
				ArrayList<Integer> badCoord = new ArrayList<Integer>();
				badCoord.add(currentGridX);
				badCoord.add(currentGridY);
				this.badCoordinates.add(badCoord);

				double prevGridX = currentGridX;
				double prevGridY = currentGridY;
				currentGridX = this.previousCoordinates.get(this.previousCoordinates.size() - 1).get(0);
				currentGridY = this.previousCoordinates.get(this.previousCoordinates.size() - 1).get(1);

				if (prevGridX < currentGridX) currentMapX += this.speed;
				else if (prevGridX > currentGridX) currentMapX -= this.speed;
				else if (prevGridY < currentGridY) currentMapY += this.speed;
				else if (prevGridY > currentGridY) currentMapY -= this.speed;

				this.previousCoordinates.remove(this.previousCoordinates.size() - 1);
			}
		}

		return false;
	}
	
	private int[] createCheckSequence(double destX, double destY, double currentMapX, double currentMapY) {
		int[] sequence = null;
		
		if (destX > currentMapX && destY > currentMapY) sequence = new int[]{1, 2, 3, 4};
		else if (destX > currentMapX && destY < currentMapY) sequence = new int[]{3, 2, 4, 1};
		else if (destX < currentMapX && destY < currentMapY) sequence = new int[]{3, 4, 1, 2};
		else if (destX < currentMapX && destY > currentMapY) sequence = new int[]{1, 4, 2, 3};
		else if (destY > currentMapY) sequence = new int[]{1, 2, 4, 3};
		else if (destX > currentMapX) sequence = new int[]{2, 1, 3, 4};
		else if (destY < currentMapY) sequence = new int[]{3, 2, 4, 1};
		else if (destX < currentMapX) sequence = new int[]{4, 1, 3, 2};
		
		return sequence;
	}
	
	protected boolean pathFind(double destX, double destY, double leeway) {
		this.badCoordinates.clear();
		this.previousCoordinates.clear();
		this.pathfindX.clear();
		this.pathfindY.clear();
		
		//If within acceptable range of destination coordinates, pathfinding not needed
		if (((getMapX() <= destX && getMapX() + leeway >= destX) || (getMapX() >= destX && getMapX() - leeway <= destX)) && 
			((getMapY() <= destY && getMapY() + leeway >= destY) || (getMapY() >= destY && getMapY() - leeway <= destY))) {
			return true;
		}
		//////////////////////
		
		//Add the current coordinates as "bad" coordinates on the "grid"
		ArrayList<Integer> badCoords = new ArrayList<Integer>();
		badCoords.add(0);
		badCoords.add(0);
		this.badCoordinates.add(badCoords);
		//////////////////////
		
		//Add the current coordinates to the "grid"
		ArrayList<Integer> previousCoords = new ArrayList<Integer>();
		previousCoords.add(0);
		previousCoords.add(0);
		this.previousCoordinates.add(previousCoords);
		//////////////////////
				
		//Set up check sequence; 1 = up, 2 = right, 3 = down, 4 = left
		int[] bestCheckSequence = null;
		
		if (destX > getMapX() && destY > getMapY()) bestCheckSequence = new int[]{1, 2, 3, 4};
		else if (destX > getMapX() && destY < getMapY()) bestCheckSequence = new int[]{3, 2, 4, 1};
		else if (destX < getMapX() && destY < getMapY()) bestCheckSequence = new int[]{3, 4, 1, 2};
		else if (destX < getMapX() && destY > getMapY()) bestCheckSequence = new int[]{1, 4, 2, 3};
		else if (destY > getMapY()) bestCheckSequence = new int[]{1, 2, 4, 3};
		else if (destX > getMapX()) bestCheckSequence = new int[]{2, 1, 3, 4};
		else if (destY < getMapY()) bestCheckSequence = new int[]{3, 2, 4, 1};
		else if (destX < getMapX()) bestCheckSequence = new int[]{4, 1, 3, 2};
		/////////////////////////
		
		//Use check sequence
		for (int i : bestCheckSequence) {
			boolean cancelCheck = false;
			this.pathFindUpdates = 0;
			
			switch (i) {
			
			//Up
			case 1:
				if (!willCollide(getMapX(), getMapY() + this.speed)) {
					
					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == 0 && list.get(1) == 1) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (pathFind(destX, destY, getMapX(), getMapY() + this.speed, 0, 1, leeway)) {
						return true;
					}
				} else {
					ArrayList<Integer> badUp = new ArrayList<Integer>();
					badUp.add(0);
					badUp.add(1);
					this.badCoordinates.add(badUp);
				}
				break;
				
			//Right
			case 2:
				if (!willCollide(getMapX() + this.speed, getMapY())) {
					
					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == 1 && list.get(1) == 0) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (pathFind(destX, destY, getMapX() + this.speed, getMapY(), 1, 0, leeway)) {
						return true;
					}
				} else {
					ArrayList<Integer> badRight = new ArrayList<Integer>();
					badRight.add(1);
					badRight.add(0);
					this.badCoordinates.add(badRight);
				}
				break;
				
			//Down
			case 3:
				if (!willCollide(getMapX(), getMapY() - this.speed)) {
					
					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == -1 && list.get(1) == 0) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (pathFind(destX, destY, getMapX(), getMapY() - this.speed, 0, -1, leeway)) {
						return true;
					}
				} else {
					ArrayList<Integer> badDown = new ArrayList<Integer>();
					badDown.add(0);
					badDown.add(-1);
					this.badCoordinates.add(badDown);
				}
				break;
				
			//Left
			case 4:
				if (!willCollide(getMapX() - this.speed, getMapY())) {
					
					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == -1 && list.get(1) == 0) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (pathFind(destX, destY, getMapX() - this.speed, getMapY(), -1, 0, leeway)) {
						return true;
					}
				} else {
					ArrayList<Integer> badLeft = new ArrayList<Integer>();
					badLeft.add(-1);
					badLeft.add(0);
					this.badCoordinates.add(badLeft);
				}
				break;
			
			}
		}
		/////////////////////////

		return false;
	}
	
	protected boolean pathFind(double destX, double destY, double currentMapX, double currentMapY, int currentGridX, int currentGridY, double leeway) {
		//Add the current coordinates to the "grid"
		ArrayList<Integer> previousCoords = new ArrayList<Integer>();
		previousCoords.add(currentGridX);
		previousCoords.add(currentGridY);
		this.previousCoordinates.add(previousCoords);
		//////////////////////
		
		//If within acceptable range of destination coordinates, pathfinding is completed
		if (((currentMapX <= destX && currentMapX + leeway >= destX) || (currentMapX >= destX && currentMapX - leeway <= destX)) && 
			((currentMapY <= destY && currentMapY + leeway >= destY) || (currentMapY >= destY && currentMapY - leeway <= destY))) {
			
			//Create walk pattern for the entity
			int lastX = 0, lastY = 0;
			for (ArrayList<Integer> list : this.previousCoordinates) {
				if (list.get(0) == 0 && list.get(1) == 0) continue;
				
				if (list.get(0) > lastX) this.pathfindX.add(this.speed);
				else if (list.get(0) < lastX) this.pathfindX.add(-this.speed);
				else this.pathfindX.add(0.0);
				
				if (list.get(1) > lastY) this.pathfindY.add(this.speed);
				else if (list.get(1) < lastY) this.pathfindY.add(-this.speed);
				else this.pathfindY.add(0.0);
				
				lastX = list.get(0);
				lastY = list.get(1);
			}
			//////////////////////
			
			this.destinationX = destX;
			this.destinationY = destY;
			return true;
		}
		//////////////////////
		
		//If path finding has advanced more than (570 * this.speed) distance without
		//finding the destination, cancel this path
		if (this.pathFindUpdates > 570) return false;
		this.pathFindUpdates++;
		//////////////////////
				
		//Set up check sequence; 1 = up, 2 = right, 3 = down, 4 = left
		int[] bestCheckSequence = null;
		
		if (destX > currentMapX && destY > currentMapY) bestCheckSequence = new int[]{1, 2, 3, 4};
		else if (destX > currentMapX && destY < currentMapY) bestCheckSequence = new int[]{3, 2, 4, 1};
		else if (destX < currentMapX && destY < currentMapY) bestCheckSequence = new int[]{3, 4, 1, 2};
		else if (destX < currentMapX && destY > currentMapY) bestCheckSequence = new int[]{1, 4, 2, 3};
		else if (destY > currentMapY) bestCheckSequence = new int[]{1, 2, 4, 3};
		else if (destX > currentMapX) bestCheckSequence = new int[]{2, 1, 3, 4};
		else if (destY < currentMapY) bestCheckSequence = new int[]{3, 2, 4, 1};
		else if (destX < currentMapX) bestCheckSequence = new int[]{4, 1, 3, 2};
		/////////////////////////
		
		//Use check sequence
		for (int i : bestCheckSequence) {
			if (this.pathFindUpdates > 570) {
				this.previousCoordinates.remove(previousCoords);
				ArrayList<Integer> badCoords = new ArrayList<Integer>();
				badCoords.add(currentGridX);
				badCoords.add(currentGridY);
				this.badCoordinates.add(badCoords);
				return false;
			}
			
			boolean cancelCheck = false;
			
			switch (i) {
			
			//Up
			case 1:
				if (!willCollide(currentMapX, currentMapY + this.speed)) {

					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == currentGridX && list.get(1) == currentGridY + 1) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;

					for (ArrayList<Integer> list : this.previousCoordinates) {
						if (list.get(0) == currentGridX && list.get(1) == currentGridY + 1) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (currentMapY < destY && currentMapY + this.speed > destY && ((destX > currentMapX && !willCollide(currentMapX + this.speed, currentMapY + (destY - currentMapY))) || (destX < currentMapX && !willCollide(currentMapX - this.speed, currentMapY + (destY - currentMapY))))) {
						if (pathFind(destX, destY, currentMapX, currentMapY + (destY - currentMapY), currentGridX, currentGridY + 1, leeway)) {
							return true;
						}
					} else {
						if (pathFind(destX, destY, currentMapX, currentMapY + this.speed, currentGridX, currentGridY + 1, leeway)) {
							return true;
						}
					}
				} else {
					ArrayList<Integer> badUp = new ArrayList<Integer>();
					badUp.add(currentGridX);
					badUp.add(currentGridY + 1);
					this.badCoordinates.add(badUp);
				}
				break;
				
			//Right
			case 2:
				if (!willCollide(currentMapX + this.speed, currentMapY)) {
					
					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == currentGridX + 1 && list.get(1) == currentGridY) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					for (ArrayList<Integer> list : this.previousCoordinates) {
						if (list.get(0) == currentGridX + 1 && list.get(1) == currentGridY) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (currentMapX < destX && currentMapX + this.speed > destX && ((destY > currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY + this.speed)) || (destY < currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY - this.speed)))) {
						if (pathFind(destX, destY, currentMapX + (destX - currentMapX), currentMapY, currentGridX, currentGridY + 1, leeway)) {
							return true;
						}
					} else {
						if (pathFind(destX, destY, currentMapX + this.speed, currentMapY, currentGridX + 1, currentGridY, leeway)) {
							return true;
						}
					}
				} else {
					ArrayList<Integer> badRight = new ArrayList<Integer>();
					badRight.add(currentGridX + 1);
					badRight.add(currentGridY);
					this.badCoordinates.add(badRight);
				}
				break;
				
			//Down
			case 3:
				if (!willCollide(currentMapX, currentMapY - this.speed)) {
					
					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == currentGridX && list.get(1) == currentGridY - 1) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					for (ArrayList<Integer> list : this.previousCoordinates) {
						if (list.get(0) == currentGridX && list.get(1) == currentGridY - 1) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (currentMapY > destY && currentMapY - this.speed < destY && ((destX > currentMapX && !willCollide(currentMapX + this.speed, currentMapY + (destY - currentMapY))) || (destX < currentMapX && !willCollide(currentMapX - this.speed, currentMapY + (destY - currentMapY))))) {
						if (pathFind(destX, destY, currentMapX, currentMapY + (destY - currentMapY), currentGridX, currentGridY + 1, leeway)) {
							return true;
						}
					} else {
						if (pathFind(destX, destY, currentMapX, currentMapY - this.speed, currentGridX, currentGridY - 1, leeway)) {
							return true;
						}
					}
				} else {
					ArrayList<Integer> badDown = new ArrayList<Integer>();
					badDown.add(currentGridX);
					badDown.add(currentGridY - 1);
					this.badCoordinates.add(badDown);
				}
				break;
				
			//Left
			case 4:
				if (!willCollide(currentMapX - this.speed, currentMapY)) {
					
					for (ArrayList<Integer> list : this.badCoordinates) {
						if (list.get(0) == currentGridX - 1 && list.get(1) == currentGridY) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					for (ArrayList<Integer> list : this.previousCoordinates) {
						if (list.get(0) == currentGridX - 1 && list.get(1) == currentGridY) {
							cancelCheck = true;
							break;
						}
					}
					if (cancelCheck) break;
					
					if (currentMapX > destX && currentMapX + this.speed < destX && ((destY > currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY + this.speed)) || (destY < currentMapY && !willCollide(currentMapX + (destX - currentMapX), currentMapY - this.speed)))) {
						if (pathFind(destX, destY, currentMapX + (destX - currentMapX), currentMapY, currentGridX, currentGridY + 1, leeway)) {
							return true;
						}
					} else {
						if (pathFind(destX, destY, currentMapX - this.speed, currentMapY, currentGridX - 1, currentGridY, leeway)) {
							return true;
						}
					}
				} else {
					ArrayList<Integer> badLeft = new ArrayList<Integer>();
					badLeft.add(currentGridX - 1);
					badLeft.add(currentGridY);
					this.badCoordinates.add(badLeft);
				}
				break;
			
			}
			
		}
		/////////////////////////

		this.previousCoordinates.remove(previousCoords);
		ArrayList<Integer> badCoords = new ArrayList<Integer>();
		badCoords.add(currentGridX);
		badCoords.add(currentGridY);
		this.badCoordinates.add(badCoords);
		this.pathFindUpdates--;
		return false;
	}
	
	public void equipItem(Item_Equipment item) {
		Item temp = null;
		
		//Helmet
		if (item.slot == 1) {
			temp = this.helmet;
			this.helmet = item;
		}
		////////////////////////
		
		//Chest
		if (item.slot == 2) {
			temp = this.chest;
			this.chest = item;
		}
		////////////////////////
		
		//Legs
		if (item.slot == 3) {
			temp = this.legs;
			this.legs = item;
		}
		////////////////////////
		
		//Boots
		if (item.slot == 4) {
			temp = this.boots;
			this.boots = item;
		}
		////////////////////////
		
		//Gloves
		if (item.slot == 5) {
			temp = this.gloves;
			this.gloves = item;
		}
		////////////////////////
		
		//Shield
		if (item.slot == 6) {
			temp = this.shield;
			this.shield = item;
		}
		////////////////////////
		
		//Weapon
		else if (item.slot == 7) {
			temp = this.weapon;
			this.weapon = item;
		}
		////////////////////////
		
		//Ammo
		else if (item.slot == 8) {
			temp = this.ammo;
			this.ammo = item;
		}
		////////////////////////
		
		this.inventory.addItem(temp);
		////////////////////////
	}
	
	public void changeLevel(LevelConnector lc) {
		//this.level.entitiesToRemove.add(this);
		
		//this.level = lc.destination;
		
		//this.level.entitiesToAdd.add(this);
		
		moveTo(lc.spawnX, lc.spawnY, getMovingDir());
		
		if (this instanceof Player) {
			if (((Player)this).hasInput) {
				// Change minimap level to the current level
				((Player)this).minimap = new MiniMap(lc.destination, (Player)this);
				((Player)this).minimap.update();
				//////////////////////////////////////
			
				// Change level map level to the current level
				((Player)this).levelMap = new LevelMap((Player)this);
				((Player)this).levelMap.level = lc.destination;
				((Player)this).levelMap.update();
				//////////////////////////////////////
				
				//Update location name display timer
				((Player)this).showLocationNameTimer = ((Player)this).locationNameTimerAmount;
				//////////////////////////////////////
				
				Packet003ChangeLevel packet = new Packet003ChangeLevel(this.name, lc.destination.name, getMapX(), getMapY());
				packet.writeData(Game.socketClient);
			}
		} else if (Game.socketServer != null) {
			Packet003ChangeLevel packet = new Packet003ChangeLevel(this.name, lc.destination.name, getMapX(), getMapY());
			packet.writeData(Game.socketServer);
		}
		
		//checkCollision();
	}
	
	public void changeLevel(Level level, double mapX, double mapY) {
		//this.level.entitiesToRemove.add(this);
		
		//this.level = level;
		
		//this.level.entitiesToAdd.add(this);
		
		moveTo(mapX, mapY, getMovingDir());
		
		if (this instanceof Player) {
			if (((Player)this).hasInput) {
				// Change minimap level to the current level
				((Player)this).minimap = new MiniMap(level, (Player)this);
				((Player)this).minimap.update();
				//////////////////////////////////////
			
				// Change level map level to the current level
				((Player)this).levelMap = new LevelMap((Player)this);
				((Player)this).levelMap.level = level;
				((Player)this).levelMap.update();
				//////////////////////////////////////
				
				//Update location name display timer
				((Player)this).showLocationNameTimer = ((Player)this).locationNameTimerAmount;
				//////////////////////////////////////
				
				Packet003ChangeLevel packet = new Packet003ChangeLevel(this.name, level.name, getMapX(), getMapY());
				packet.writeData(Game.socketClient);
			}
		} else if (Game.socketServer != null) {
			Packet003ChangeLevel packet = new Packet003ChangeLevel(this.name, level.name, getMapX(), getMapY());
			packet.writeData(Game.socketServer);
		}
		//checkCollision();
	}
	
	public void death() {
		this.energy = this.maxEnergy;
		
		//Any entities that have the dying entity as their last target will no longer have a last target
		for (Entity e : new ArrayList<>(this.level.getEntities())) {
			if (!e.equals(this) && e instanceof Entity_Character) {
				if (((Entity_Character)e).lastTarget != null && ((Entity_Character)e).lastTarget.equals(this)) ((Entity_Character)e).lastTarget = null;
			}
		}
		/////////////////////////////
		
		//Set all temporary boosts to 0
		this.tempMeleeBoost = 0;
		this.tempArcheryBoost = 0;
		this.tempMagicBoost = 0;
		this.tempDefenseBoost = 0;
		this.tempAccuracyBoost = 0;
		this.tempMaxHitBoost = 0;
		/////////////////////////////
		
		//Entity is no longer in combat
		this.combatTimer = 0;
		/////////////////////////////
		
		//Get rid of this entities last target
		this.lastTarget = null;
		/////////////////////////////
		
		//No knockback into next life
		this.knockbackX = 0;
		this.knockbackY = 0;
		this.canMove = true;
		/////////////////////////////
		
		//Clear all positive and negative effects
		this.positiveEffects.clear();
		this.positiveEffectsToAdd.clear();
		this.positiveEffectsToRemove.clear();
		this.negativeEffects.clear();
		this.negativeEffectsToAdd.clear();
		this.negativeEffectsToRemove.clear();
		/////////////////////////////
		
		//If entity is not a player, set respawn for 30 seconds
		if (this instanceof Entity_NPC) {
			this.respawnTimer = System.currentTimeMillis();
			//this.level.getDroppedItemsToAdd().addAll(((Entity_NPC)this).dropItems());
		}
		/////////////////////////////
		
		//Get rid of dialogue
		else if (this instanceof Player) {
			((Player)this).currentDialogue = null;
			((Player)this).currentShop = null;
			((Player)this).examinedEntity = null;
			((Player)this).dialogueIndex = 0;
			((Player)this).energyToReduce = 0;
		}
		/////////////////////////////
		
		this.currentHealth = this.maxHealth;
		Packet004Health healthPacket = new Packet004Health(this.name, this.currentHealth, this.maxHealth);
		healthPacket.writeData(Game.socketClient);
		
		//Set coordinates to respawn coordinates
		changeLevel(this.checkpointLevel, this.checkpointMapX, this.checkpointMapY);
		/////////////////////////////
	}
	
	public void updateEffects() {
		for (Effect_Positive ep : this.positiveEffects) {
			ep.update();
			ep.checkTimers();
		}
		this.positiveEffects.removeAll(this.positiveEffectsToRemove);
		this.positiveEffectsToRemove.clear();
		this.positiveEffects.addAll(this.positiveEffectsToAdd);
		this.positiveEffectsToAdd.clear();
		
		for (Effect_Negative en : this.negativeEffects) {
			en.update();
			en.checkTimers();
		}
		this.negativeEffects.removeAll(this.negativeEffectsToRemove);
		this.negativeEffectsToRemove.clear();
		this.negativeEffects.addAll(this.negativeEffectsToAdd);
		this.negativeEffectsToAdd.clear();
	}
	
	public void updateKnockback() {
		double knockbackXSpeed = Math.abs(this.knockbackX / 8.0);
		double knockbackYSpeed = Math.abs(this.knockbackY / 8.0);
		
		if (this.knockbackX < 1 && this.knockbackX > -1) knockbackXSpeed = Math.abs(this.knockbackX);
		if (this.knockbackY < 1 && this.knockbackY > -1) knockbackYSpeed = Math.abs(this.knockbackY);
		
		this.canMove = true;
		
		if (this.knockbackX > 0) {
			if (!willCollide(getMapX() + knockbackXSpeed, getMapY())) {
				moveTo(getMapX() + knockbackXSpeed, getMapY(), getMovingDir());
				this.knockbackX -= knockbackXSpeed;
				this.canMove = false;
				
				if (this.knockbackX < 0) {
					moveTo(getMapX() + this.knockbackX, getMapY(), getMovingDir());
					this.knockbackX = 0;
					this.canMove = true;
				}
			} else {
				this.knockbackX = 0;
			}
		}
		
		else if (this.knockbackX < 0) {
			if (!willCollide(getMapX() - knockbackXSpeed, getMapY())) {
				moveTo(getMapX() - knockbackXSpeed, getMapY(), getMovingDir());
				this.knockbackX += knockbackXSpeed;
				this.canMove = false;
				
				if (this.knockbackX > 0){
					moveTo(getMapX() + this.knockbackX, getMapY(), getMovingDir());
					this.knockbackX = 0;
					this.canMove = true;
				}
			} else {
				this.knockbackX = 0;
			}
		}

		
		
		if (this.knockbackY > 0) {
			if (!willCollide(getMapX(), getMapY() + knockbackYSpeed)) {
				moveTo(getMapX(), getMapY() + knockbackYSpeed, getMovingDir());
				this.knockbackY -= knockbackYSpeed;
				this.canMove = false;
				
				if (this.knockbackY < 0) {
					moveTo(getMapX(), getMapY() + this.knockbackY, getMovingDir());
					this.knockbackY = 0;
					if (this.knockbackX == 0) this.canMove = true;
				}
			} else {
				this.knockbackY = 0;
			}
		}
		
		else if (this.knockbackY < 0) {
			if (!willCollide(getMapX(), getMapY() - knockbackYSpeed)) {
				moveTo(getMapX(), getMapY() - knockbackYSpeed, getMovingDir());
				this.knockbackY += knockbackYSpeed;
				this.canMove = false;
				
				if (this.knockbackY > 0) {
					moveTo(getMapX(), getMapY() + this.knockbackY, getMovingDir());
					this.knockbackY = 0;
					if (this.knockbackX == 0) this.canMove = true;
				}
			} else {
				this.knockbackY = 0;
			}
		}
		
		if (this.knockbackX == 0 && this.knockbackY == 0) this.canMove = true;
	}
	
	public void updateStats() {
		this.overallLevel = (int) ((this.defense / 2.0) + ((this.melee + this.archery + this.magic) / 2.25) + (this.maxHealth / 30.0));
		
		if (!(this instanceof PlayerMP && ((PlayerMP)this).hasInput) && !(Game.socketServer != null && !(this instanceof PlayerMP))) return;
		
		this.accuracy = this.overallLevel / 3.0;
		
		this.accuracy += this.helmet.accuracy + this.chest.accuracy + this.legs.accuracy + this.boots.accuracy + this.gloves.accuracy + this.weapon.accuracy + this.shield.accuracy + this.ammo.accuracy;
		this.maxHit = this.helmet.maxHit + this.chest.maxHit + this.legs.maxHit + this.boots.maxHit + this.gloves.maxHit + this.weapon.maxHit + this.shield.maxHit + this.ammo.maxHit;
		this.armor = this.helmet.armor + this.chest.armor + this.legs.armor + this.boots.armor + this.gloves.armor + this.weapon.armor + this.shield.armor + this.ammo.armor;
		this.meleeBoost = this.helmet.meleeBoost + this.chest.meleeBoost + this.legs.meleeBoost + this.boots.meleeBoost + this.gloves.meleeBoost + this.weapon.meleeBoost + this.shield.meleeBoost + this.ammo.meleeBoost;
		this.archeryBoost = this.helmet.archeryBoost + this.chest.archeryBoost + this.legs.archeryBoost + this.boots.archeryBoost + this.gloves.archeryBoost + this.weapon.archeryBoost + this.shield.archeryBoost + this.ammo.archeryBoost;
		this.magicBoost = this.helmet.magicBoost + this.chest.magicBoost + this.legs.magicBoost + this.boots.magicBoost + this.gloves.magicBoost + this.weapon.magicBoost + this.shield.magicBoost + this.ammo.magicBoost;
		this.attackDelay = this.helmet.speed + this.chest.speed + this.legs.speed + this.boots.speed + this.gloves.speed + this.weapon.speed + this.shield.speed + this.ammo.speed;
		this.range = this.helmet.range + this.chest.range + this.legs.range + this.boots.range + this.gloves.range + this.weapon.range + this.shield.range + this.ammo.range;
		
		//TEMPORAY
		this.range = 12;
		/////////////////
		
		this.energy += 0.1;
		if (this.energy > this.maxEnergy) this.energy = this.maxEnergy;
		
		if (this.maxHit <= 0) this.maxHit = 1;
		
		if (this.tempMeleeBoost != 0) {
			this.tempMeleeBoostTimer = System.currentTimeMillis() - this.tempMeleeBoostTimer;
			if (this.tempMeleeBoostTimer < 100000 && this.tempMeleeBoostTimer > 30000) {
				this.tempMeleeBoostTimer = System.currentTimeMillis();
				if (this.tempMeleeBoost > 0) this.tempMeleeBoost--;
				else this.tempMeleeBoost++;
			}
		}
		
		if (this.tempArcheryBoost != 0) {
			this.tempArcheryBoostTimer = System.currentTimeMillis() - this.tempArcheryBoostTimer;
			if (this.tempArcheryBoostTimer < 100000 && this.tempArcheryBoostTimer > 30000) {
				this.tempArcheryBoostTimer = System.currentTimeMillis();
				if (this.tempArcheryBoost > 0) this.tempArcheryBoost--;
				else this.tempArcheryBoost++;
			}
		}
		
		if (this.tempMagicBoost != 0) {
			this.tempMagicBoostTimer = System.currentTimeMillis() - this.tempMagicBoostTimer;
			if (this.tempMagicBoostTimer < 100000 && this.tempMagicBoostTimer > 30000) {
				this.tempMagicBoostTimer = System.currentTimeMillis();
				if (this.tempMagicBoost > 0) this.tempMagicBoost--;
				else this.tempMagicBoost++;
			}
		}
		
		if (this.tempDefenseBoost != 0) {
			this.tempDefenseBoostTimer = System.currentTimeMillis() - this.tempDefenseBoostTimer;
			if (this.tempDefenseBoostTimer < 100000 && this.tempDefenseBoostTimer > 30000) {
				this.tempDefenseBoostTimer = System.currentTimeMillis();
				if (this.tempDefenseBoost > 0) this.tempDefenseBoost--;
				else this.tempDefenseBoost++;
			}
		}
		
		if (this.tempAccuracyBoost != 0) {
			this.tempAccuracyBoostTimer = System.currentTimeMillis() - this.tempAccuracyBoostTimer;
			if (this.tempAccuracyBoostTimer < 100000 && this.tempAccuracyBoostTimer > 30000) {
				this.tempAccuracyBoostTimer = System.currentTimeMillis();
				if (this.tempAccuracyBoost > 0) this.tempAccuracyBoost--;
				else this.tempAccuracyBoost++;
			}
		}
		
		if (this.tempMaxHitBoost != 0) {
			this.tempMaxHitBoostTimer = System.currentTimeMillis() - this.tempMaxHitBoostTimer;
			if (this.tempMaxHitBoostTimer < 100000 && this.tempMaxHitBoostTimer > 30000) {
				this.tempMaxHitBoostTimer = System.currentTimeMillis();
				if (this.tempMaxHitBoost > 0) this.tempMaxHitBoost--;
				else this.tempMaxHitBoost++;
			}
		}

		if (this instanceof PlayerMP && ((PlayerMP)this).hasInput) {
			Packet030Stats statsPacket = new Packet030Stats(this.name, this.accuracy, this.maxHit, this.armor, this.meleeBoost, this.archeryBoost, this.magicBoost, this.range, this.tempMeleeBoost, this.tempArcheryBoost, this.tempMagicBoost, this.tempDefenseBoost, this.tempAccuracyBoost, this.tempMaxHitBoost);
			statsPacket.writeData(Game.socketClient);
		}
			
		else if (Game.socketServer != null && !(this instanceof PlayerMP)) {
			Packet030Stats statsPacket = new Packet030Stats(this.name, this.accuracy, this.maxHit, this.armor, this.meleeBoost, this.archeryBoost, this.magicBoost, this.range, this.tempMeleeBoost, this.tempArcheryBoost, this.tempMagicBoost, this.tempDefenseBoost, this.tempAccuracyBoost, this.tempMaxHitBoost);
			statsPacket.writeData(Game.socketServer);
		}
	}
	
	public void attack(Entity_Character target) {
		int damage = calculateDamage(target);
				
		target.currentHealth -= damage;
		Packet004Health healthPacket = new Packet004Health(target.name, target.currentHealth, target.maxHealth);
		healthPacket.writeData(Game.socketClient);
		
		OverheadDamage overheadDamage = new OverheadDamage(this.level, this, target, damage);
		
		if (this.weapon.isMelee) this.attackDelayTimer = MAX_ATTACK_DELAY - this.attackDelay;
		
		this.isInCombat = true;
		this.combatTimer = this.combatDelay;
		target.isInCombat = true;
		target.combatTimer = target.combatDelay;
		
		this.lastTarget = target;
		target.lastTarget = this;
		Packet024SetLastTarget targetPacket1 = new Packet024SetLastTarget(this.name, target.name, this.combatTimer);
		Packet024SetLastTarget targetPacket2 = new Packet024SetLastTarget(target.name, this.name, target.combatTimer);
		targetPacket1.writeData(Game.socketClient);
		targetPacket2.writeData(Game.socketClient);
		
		calculateKnockback(target, damage);
		
		if (this instanceof Player) {
			if (damage > 0) {
				if (this.weapon.isMelee) ((Player)this).meleeXpPercent += (2 / this.melee) + (damage / this.melee);
				else if (this.weapon.isArchery) ((Player)this).archeryXpPercent += (2 / this.archery) + (damage / this.archery);
				else if (this.weapon.isMagic) ((Player)this).magicXpPercent += (2 / this.magic) + (damage / this.magic);
			}
		}
		
		if (target instanceof Player) {
			if (damage > 0) {
				((Player)target).defenseXpPercent += (2 / target.defense) + (damage / target.defense);
			}
		}
		
		//Apply BloodLeech, if the ability is active
		for (Effect_Positive ep : this.positiveEffects) {
			if (ep instanceof Effect_Positive_BloodLeech) {
				((Effect_Positive_BloodLeech)ep).damageDealt = damage;
				break;
			}
		}
		//////////////
		
		//Check effects that should be added
		for (int i = 0; i < this.weapon.abilities.length; i++) {
			if (this.weapon.abilities[i] != null) {
				if (this.weapon.abilities[i].cooldown > 0) continue;
				
				//RedHot
				if (this.weapon.abilities[i] instanceof Ability_RedHot) {
					if (this.selectedAbility == i + 1) {
						if (this.energy >= this.weapon.abilities[i].energyCost) {
							this.weapon.abilities[i].owner = this;
							this.weapon.abilities[i].targets.add(target);
							this.weapon.abilities[i].isActive = true;
							this.selectedAbility = 0;
						}
					}
				}
				//////////////
				
				//BoneCrusher
				else if (this.weapon.abilities[i] instanceof Ability_BoneCrusher) {
					if (this.selectedAbility == i + 1) {
						if (this.energy >= this.weapon.abilities[i].energyCost) {
							this.weapon.abilities[i].owner = this;
							this.weapon.abilities[i].targets.add(target);
							this.weapon.abilities[i].isActive = true;
							this.selectedAbility = 0;
						}
						this.weapon.abilities[i].owner = this;
						this.weapon.abilities[i].targets.add(target);
						this.weapon.abilities[i].isActive = true;
						this.selectedAbility = 0;
					}
				}
				//////////////
				
				
			}
		}
		////////////////////////////////////
	}
	
	public void attack(Entity_Character target, Projectile projectile) {
		int damage = calculateDamage(target);
				
		target.currentHealth -= damage;
		Packet004Health healthPacket = new Packet004Health(target.name, target.currentHealth, target.maxHealth);
		healthPacket.writeData(Game.socketClient);
		
		OverheadDamage overheadDamage = new OverheadDamage(this.level, this, target, damage);
		
		if (this.weapon.isMelee) this.attackDelayTimer = 50 - this.attackDelay;
		
		this.isInCombat = true;
		this.combatTimer = this.combatDelay;
		target.isInCombat = true;
		target.combatTimer = target.combatDelay;
		
		this.lastTarget = target;
		target.lastTarget = this;
		
		calculateKnockback(target, projectile, damage);
		
		if (this instanceof Player) {
			if (damage > 0) {
				if (this.weapon.isMelee) ((Player)this).meleeXpPercent += (2 / this.melee) + (damage / this.melee);
				else if (this.weapon.isArchery) ((Player)this).archeryXpPercent += (2 / this.archery) + (damage / this.archery);
				else if (this.weapon.isMagic) ((Player)this).magicXpPercent += (2 / this.magic) + (damage / this.magic);
			}
		}
		
		if (target instanceof Player) {
			if (damage > 0) {
				((Player)target).defenseXpPercent += (2 / target.defense) + (damage / target.defense);
			}
		}
		
		//Check effects that should be added
		for (int i = 0; i < this.weapon.abilities.length; i++) {
			if (this.weapon.abilities[i] != null) {
				if (this.weapon.abilities[i].cooldown > 0) continue;
				
				//RedHot
				if (this.weapon.abilities[i] instanceof Ability_RedHot) {
					if (this.selectedAbility == i + 1) {
						if (this.energy >= this.weapon.abilities[i].energyCost) {
							this.weapon.abilities[i].owner = this;
							this.weapon.abilities[i].targets.add(target);
							this.weapon.abilities[i].isActive = true;
							this.selectedAbility = 0;
						}
					}
				}
				//////////////
				
				//BoneCrusher
				else if (this.weapon.abilities[i] instanceof Ability_BoneCrusher) {
					if (this.selectedAbility == i + 1) {
						if (this.energy >= this.weapon.abilities[i].energyCost) {
							this.weapon.abilities[i].owner = this;
							this.weapon.abilities[i].targets.add(target);
							this.weapon.abilities[i].isActive = true;
							this.selectedAbility = 0;
						}
					}
				}
				//////////////
				
				
			}
		}
		////////////////////////////////////
	}
	
	private void calculateKnockback(Entity_Character target, int damage) {
		if (damage == 0 || target.knockbackX != 0 || target.knockbackY != 0) return;
		
		double bottomFactor = 0.01; //Always greater than 0
		double topFactor = 0.4; //Always greater than bottom factor and less than 1
		
		if (getMovingDir() == 1) {
			target.knockbackY = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor));
		}
		
		else if (getMovingDir() == 2) {
			target.knockbackY = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
			target.knockbackX = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
		}
		
		else if (getMovingDir() == 3) {
			target.knockbackX = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor));
		}
		
		else if (getMovingDir() == 4) {
			target.knockbackY = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
			target.knockbackX = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
		}
		
		else if (getMovingDir() == 5) {
			target.knockbackY = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor)));
		}
		
		else if (getMovingDir() == 6) {
			target.knockbackY = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
			target.knockbackX = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
		}
		
		else if (getMovingDir() == 7) {
			target.knockbackX = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor)));
		}
		
		else if (getMovingDir() == 8) {
			target.knockbackY = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
			target.knockbackX = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
		}
	}
	
	private void calculateKnockback(Entity_Character target, Projectile projectile, int damage) {
		if (damage == 0 || target.knockbackX != 0 || target.knockbackY != 0) return;
		
		double bottomFactor = 0.01; //Always greater than 0
		double topFactor = 0.4; //Always greater than bottom factor and less than 1
		
		if (projectile.getMovingDir() == 1) {
			target.knockbackY = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor));
		}
		
		else if (projectile.getMovingDir() == 2) {
			target.knockbackY = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
			target.knockbackX = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
		}
		
		else if (projectile.getMovingDir() == 3) {
			target.knockbackX = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor));
		}
		
		else if (projectile.getMovingDir() == 4) {
			target.knockbackY = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
			target.knockbackX = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
		}
		
		else if (projectile.getMovingDir() == 5) {
			target.knockbackY = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor)));
		}
		
		else if (projectile.getMovingDir() == 6) {
			target.knockbackY = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
			target.knockbackX = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
		}
		
		else if (projectile.getMovingDir() == 7) {
			target.knockbackX = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor, topFactor)));
		}
		
		else if (projectile.getMovingDir() == 8) {
			target.knockbackY = damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2));
			target.knockbackX = -(damage * (ThreadLocalRandom.current().nextDouble(bottomFactor / 2, topFactor / 2)));
		}
	}
	
	public void updateAbilities() {
		if (this.weapon.abilities != null) {
			for (Ability a : this.weapon.abilities) if (a != null) {
				a.owner = this;
				a.update();
			}
		}
		
		if (this.inventory != null) {
			for (Item item : this.inventory.items) {
				if (item instanceof Item_Equipment && ((Item_Equipment)item).slot == 7) {
					if (((Item_Equipment)item).abilities != null) {
						for (Ability a : ((Item_Equipment)item).abilities) if (a != null) {
							a.owner = this;
							a.update();
						}
					}
				}
			}
		}
	}
	
	public void eat(Item_Food food) {
		this.currentHealth += food.healAmount;
		if (this.currentHealth > this.maxHealth) this.currentHealth = this.maxHealth;
		
		if (food.meleeBoost > 0 && food.meleeBoost > this.tempMeleeBoost) {
			if (this.tempMeleeBoost >= 0) this.tempMeleeBoost = food.meleeBoost;
			else this.tempMeleeBoost += food.meleeBoost;
		}
		else if (food.meleeBoost < 0 && food.meleeBoost < this.tempMeleeBoost) {
			if (this.tempMeleeBoost <= 0) this.tempMeleeBoost = food.meleeBoost;
			else this.tempMeleeBoost += food.meleeBoost;
		}
		
		if (food.archeryBoost > 0 && food.archeryBoost > this.tempArcheryBoost) {
			if (this.tempArcheryBoost >= 0) this.tempArcheryBoost = food.archeryBoost;
			else this.tempArcheryBoost += food.archeryBoost;
		}
		else if (food.archeryBoost < 0 && food.archeryBoost < this.tempArcheryBoost) {
			if (this.tempArcheryBoost <= 0) this.tempArcheryBoost = food.archeryBoost;
			else this.tempArcheryBoost += food.archeryBoost;
		}
		
		if (food.magicBoost > 0 && food.magicBoost > this.tempMagicBoost) {
			if (this.tempMagicBoost >= 0) this.tempMagicBoost = food.magicBoost;
			else this.tempMagicBoost += food.magicBoost;
		}
		else if (food.magicBoost < 0 && food.magicBoost < this.tempMagicBoost) {
			if (this.tempMagicBoost <= 0) this.tempMagicBoost = food.magicBoost;
			else this.tempMagicBoost += food.magicBoost;
		}
		
		if (food.defenseBoost > 0 && food.defenseBoost > this.tempDefenseBoost) {
			if (this.tempDefenseBoost >= 0) this.tempDefenseBoost = food.defenseBoost;
			else this.tempDefenseBoost += food.defenseBoost;
		}
		else if (food.defenseBoost < 0 && food.defenseBoost < this.tempDefenseBoost) {
			if (this.tempDefenseBoost <= 0) this.tempDefenseBoost = food.defenseBoost;
			else this.tempDefenseBoost += food.defenseBoost;
		}
		
		if (food.accuracyBoost > 0 && food.accuracyBoost > this.tempAccuracyBoost) {
			if (this.tempAccuracyBoost >= 0) this.tempAccuracyBoost = food.accuracyBoost;
			else this.tempAccuracyBoost += food.accuracyBoost;
		}
		else if (food.accuracyBoost < 0 && food.accuracyBoost < this.tempAccuracyBoost) {
			if (this.tempAccuracyBoost <= 0) this.tempAccuracyBoost = food.accuracyBoost;
			else this.tempAccuracyBoost += food.accuracyBoost;
		}
		
		if (food.maxHitBoost > 0 && food.maxHitBoost > this.tempMaxHitBoost) {
			if (this.tempMaxHitBoost >= 0) this.tempMaxHitBoost = food.maxHitBoost;
			else this.tempMaxHitBoost += food.maxHitBoost;
		}
		else if (food.maxHitBoost < 0 && food.maxHitBoost < this.tempMaxHitBoost) {
			if (this.tempMaxHitBoost <= 0) this.tempMaxHitBoost = food.maxHitBoost;
			else this.tempMaxHitBoost += food.maxHitBoost;
		}
	}
	
	private int calculateDamage(Entity_Character target) {
		int damage = 0;
		int boostToUse = 0;
		int levelToUse = 0;
		int targetBoostToUse = 0;
		int targetLevelToUse = 0;
			
		if (this.weapon.isMelee) {
			boostToUse = this.meleeBoost + this.tempMeleeBoost;
			levelToUse = this.melee;
			targetBoostToUse = target.meleeBoost + target.tempMeleeBoost;
			targetLevelToUse = target.melee;
		}
		else if (this.weapon.isArchery) {
			boostToUse = this.archeryBoost + this.tempArcheryBoost;
			levelToUse = this.archery;
			targetBoostToUse = target.archeryBoost + target.tempArcheryBoost;
			targetLevelToUse = target.archery;
		}
		else if (this.weapon.isMagic) {
			boostToUse = this.magicBoost + this.tempMagicBoost;
			levelToUse = this.magic;
			targetBoostToUse = target.magicBoost + target.tempMagicBoost;
			targetLevelToUse = target.magic;
		}
			
		if (ThreadLocalRandom.current().nextDouble(0, 100) <= this.accuracy + this.tempAccuracyBoost + (Math.ceil((levelToUse * 0.45)) - (targetLevelToUse * 0.05))) {
			damage = random.nextInt(this.maxHit + this.tempMaxHitBoost) + 1;
			if (damage < this.maxHit + this.tempMaxHitBoost) {
				damage += (int) ((ThreadLocalRandom.current().nextDouble(0, (boostToUse / 10) + 0.001)) - (ThreadLocalRandom.current().nextDouble(0, (targetBoostToUse / 10) + 0.001)));
			}
		}
		
		if (target.defense + target.tempDefenseBoost < 0) damage -= (int) ThreadLocalRandom.current().nextDouble(0, ((target.armor / 10) + 0.001));
		else damage -= (int) ThreadLocalRandom.current().nextDouble((target.defense + target.tempDefenseBoost) * 0.2, (target.defense + target.tempDefenseBoost) * 0.2 + ((target.armor / 10) + 0.001));

		if (damage < 0) damage = 0;
		else if (damage > this.maxHit + this.tempMaxHitBoost) damage = this.maxHit + this.tempMaxHitBoost;
		
		if (damage > target.currentHealth) damage = target.currentHealth;
		
		return damage;
	}
}
