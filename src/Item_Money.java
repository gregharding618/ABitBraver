import java.io.Serializable;

public class Item_Money extends Item implements Serializable {

	public Item_Money(int amount) {
		this.name = "Money";
		this.iconImageFile = "money_pic.png";
		this.stackable = true;
		this.amount = amount;
		this.value = 1;
	}

	@Override
	public void render(double x, double y) {
		StdDraw.picture(x, y, this.iconImageFile);
	}

}
