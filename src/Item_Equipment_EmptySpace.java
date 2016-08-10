import java.io.Serializable;

public class Item_Equipment_EmptySpace extends Item_Equipment implements Serializable {
	
	public Item_Equipment_EmptySpace() {
		super("", "", "", false, 1, true, false, false, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public void render(double x, double y) {
		
	}

}
