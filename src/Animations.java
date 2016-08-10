import java.io.Serializable;


public class Animations implements Serializable
{
	
	private int imageWeaponChange = 0;
	private int imageWalkChange = 0;
	public String[] imageArr;
	public String[] playerImages;
	private int index = 0;
	private Entity entity;
	
	private boolean isWalking = false;
	
	public Animations (Entity entity, String item){
		this.entity = entity;
		this.playerImages = new String[] { "player_model_standing_1.png", "player_model_standing_2.png", "player_model_standing_3.png", "player_model_standing_4.png"};
		
		switch (item){
		case "Hand Axe":
			this.imageWeaponChange = 35;
			imageArr = new String[] {   "handaxe_game_1.png", "handaxe_game_2.png", "handaxe_game_3.png", "handaxe_game_4.png", "handaxe_game_5.png",
										"handaxe_game_6.png", "handaxe_game_7.png", "handaxe_game_8.png"
						};
			break;
		
		}
	}
	
	// YOUR CODE THAT YOU STARTED WITH
	public static double[][] Animation_Human_Legs_Walk(Entity entity, double[] xCoords, double[] yCoords, int leftLegIndex1, int leftLegIndex2, int rightLegIndex1, int rightLegIndex2) {
		
		
		
		double[][] newModel = new double[][]{xCoords, yCoords};
		return newModel;
	}
	/////////////////////////////////////////////////////////////////

	
	public void updateAnimation() {
		this.imageWeaponChange--;
		
		if ( this.isWalking == true) this.imageWalkChange--;
	}
	
	public void renderEntityWalkAnimation(){
		
		if (entity.isMoving == false){
			this.isWalking = false;
			this.index = 0;
			this.imageWalkChange = 0;
			StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
		}
		else{
			this.isWalking = true;
			if			(this.imageWalkChange >= 30){
				this.index = 0;
				StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
			}
			else if		(this.imageWalkChange >= 25 && this.imageWalkChange < 30){
				this.index = 1;
				StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
			}
			else if		(this.imageWalkChange >= 20 && this.imageWalkChange < 25){
				this.index = 2;
				StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
			}
			else if		(this.imageWalkChange >= 15 && this.imageWalkChange < 20){
				this.index = 3;
				StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
			}
			else if		(this.imageWalkChange >= 10 && this.imageWalkChange < 15){
				this.index = 2;
				StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
			}
			else if		(this.imageWalkChange >= 5 && this.imageWalkChange < 10){
				this.index = 1;
				StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
			}
			else if		(this.imageWalkChange >= 0 && this.imageWalkChange < 15){
				this.index = 0;
				StdDraw.picture(entity.x, entity.y, this.playerImages[index]);
			}
		}
	}
	
	public void renderWeaponAnimation() {
		
		if 			(this.imageWeaponChange >= 35){
			this.index = 0;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else if		(this.imageWeaponChange >= 30 && this.imageWeaponChange < 35){
			this.index = 1;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else if		(this.imageWeaponChange >= 25 && this.imageWeaponChange < 30){
			this.index = 2;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else if		(this.imageWeaponChange >= 20 && this.imageWeaponChange < 25){
			this.index = 3;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else if		(this.imageWeaponChange >= 15 && this.imageWeaponChange < 20){
			this.index = 4;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else if		(this.imageWeaponChange >= 10 && this.imageWeaponChange < 15){
			this.index = 5;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else if		(this.imageWeaponChange >= 5 && this.imageWeaponChange < 10){
			this.index = 6;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else if		(this.imageWeaponChange >= 0 && this.imageWeaponChange < 5){
			this.index = 7;
			StdDraw.picture(entity.x - 7, entity.y, this.imageArr[index]);
		}
		else{
			this.entity.isInWeaponAnimation = false;
			this.imageWeaponChange = 35;
			
			return;
		}
		
	}

	
}


