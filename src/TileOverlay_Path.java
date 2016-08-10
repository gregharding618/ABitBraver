import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TileOverlay_Path extends TileOverlay implements Serializable {
	
	public double imageSeparationDistance;
	
	public List<ArrayList<Double>> coordinates = new ArrayList<ArrayList<Double>>();
	
	private final boolean multipleImages;
	
	private String verticalImagefile, horizontalImageFile, downToRightTurnImagefile, upToRightTurnImagefile, downToLeftTurnImagefile, upToLeftTurnImagefile;
	
	public TileOverlay_Path(Level level, String imageFile, Color minimapColor, double[] xCoords, double[] yCoords, double imageSeparationDistance) {
		this.level = level;
		this.imageFile = imageFile;
		this.mapXCoords = xCoords;
		this.mapYCoords = yCoords;
		this.xCoords = new double[this.mapXCoords.length];
		this.yCoords = new double[this.mapYCoords.length];
		this.imageSeparationDistance = imageSeparationDistance;
		this.miniMapColor = minimapColor;
		this.multipleImages = false;
		
		if (xCoords.length != yCoords.length){
			System.out.println("ERROR: unequal amount of x and y coordinates for tile overlay!");
			System.out.println("Overlay image name: " + this.imageFile);
		}
	}
	
	public TileOverlay_Path(World world, Level level, String generalImageFile, String verticalImagefile, String horizontalImageFile, String downToRightTurnImagefile, String upToRightTurnImagefile, String downToLeftTurnImagefile, String upToLeftTurnImagefile, Color minimapColor, double[] xCoords, double[] yCoords, double imageSeparationDistance) {
		this.world = world;
		this.level = level;
		this.imageFile = generalImageFile;
		this.verticalImagefile = verticalImagefile;
		this.horizontalImageFile = horizontalImageFile;
		this.downToRightTurnImagefile = downToRightTurnImagefile;
		this.downToLeftTurnImagefile = downToLeftTurnImagefile;
		this.upToRightTurnImagefile = upToRightTurnImagefile;
		this.upToLeftTurnImagefile = upToLeftTurnImagefile;
		this.mapXCoords = xCoords;
		this.mapYCoords = yCoords;
		this.xCoords = new double[this.mapXCoords.length];
		this.yCoords = new double[this.mapYCoords.length];
		this.imageSeparationDistance = imageSeparationDistance;
		this.miniMapColor = minimapColor;
		this.multipleImages = true;
		
		if (xCoords.length != yCoords.length){
			System.out.println("ERROR: unequal amount of x and y coordinates for tile overlay!");
			System.out.println("Overlay image name: " + this.imageFile);
		}
	}

	@Override
	public void render() {
		boolean addCoordinates = this.coordinates.isEmpty();
		
		if (!this.multipleImages) {
			double lastX = this.xCoords[0];
			double lastY = this.yCoords[0];
			double lastMapX = this.mapXCoords[0];
			double lastMapY = this.mapYCoords[0];
			
			StdDraw.picture(lastX, lastY, this.imageFile);
			
			if (addCoordinates) {
				ArrayList<Double> coords = new ArrayList<Double>();
				coords.add(lastMapX);
				coords.add(lastMapY);
				this.coordinates.add(coords);
			}
			
			for (int i = 1; i < this.xCoords.length; i++) {
				if (lastX < this.xCoords[i]) {
					while (lastX + this.imageSeparationDistance < this.xCoords[i]) {
						StdDraw.picture(lastX + this.imageSeparationDistance, this.yCoords[i], this.imageFile);
						if (addCoordinates) {
							lastMapX += this.imageSeparationDistance;
							ArrayList<Double> coords = new ArrayList<Double>();
							coords.add(lastMapX);
							coords.add(lastMapY);
							this.coordinates.add(coords);
						}
						lastX += this.imageSeparationDistance;
					}
				}
					
				else if (lastX > this.xCoords[i]) {
					while (lastX - this.imageSeparationDistance > this.xCoords[i]) {
						StdDraw.picture(lastX - this.imageSeparationDistance, this.yCoords[i], this.imageFile);
						if (addCoordinates) {
							lastMapX -= this.imageSeparationDistance;
							ArrayList<Double> coords = new ArrayList<Double>();
							coords.add(lastMapX);
							coords.add(lastMapY);
							this.coordinates.add(coords);
						}
						lastX -= this.imageSeparationDistance;
					}
				}

				else if (lastY < this.yCoords[i]) {
					while (lastY + this.imageSeparationDistance < this.yCoords[i]) {
						StdDraw.picture(this.xCoords[i], lastY + this.imageSeparationDistance, this.imageFile);
						if (addCoordinates) {
							lastMapY += this.imageSeparationDistance;
							ArrayList<Double> coords = new ArrayList<Double>();
							coords.add(lastMapX);
							coords.add(lastMapY);
							this.coordinates.add(coords);
						}
						lastY += this.imageSeparationDistance;
					}
				}

				else if (lastY > this.yCoords[i]) {
					while (lastY - this.imageSeparationDistance > this.yCoords[i]) {
						StdDraw.picture(this.xCoords[i], lastY - this.imageSeparationDistance, this.imageFile);
						if (addCoordinates) {
							lastMapY -= this.imageSeparationDistance;
							ArrayList<Double> coords = new ArrayList<Double>();
							coords.add(lastMapX);
							coords.add(lastMapY);
							this.coordinates.add(coords);
						}
						lastY -= this.imageSeparationDistance;
					}
				}

				if (addCoordinates) {
					lastMapX = this.mapXCoords[i];
					lastMapY = this.mapYCoords[i];
					ArrayList<Double> coords = new ArrayList<Double>();
					coords.add(lastMapX);
					coords.add(lastMapY);
					this.coordinates.add(coords);
				}

				lastX = this.xCoords[i];
				lastY = this.yCoords[i];
				StdDraw.picture(lastX, lastY, this.imageFile);
			}
		}

		else {

		}
		
	}

	@Override
	public void minimapRender(double x, double y, double size) {
		StdDraw.setPenColor(this.miniMapColor);
		StdDraw.filledSquare(x, y, size);
	}

}
