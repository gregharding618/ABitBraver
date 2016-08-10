import java.awt.Color;
import java.io.Serializable;

public class ScrollBar implements Serializable {
	
	public double x, y, max, min, width, height;
	public boolean vertical, isMoving = false;
	public Color color;
	
	public ScrollBar(double x, double y, double max, double min, double width, double height, boolean vertical, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.vertical = vertical;
		this.color = color;
		this.max = max;
		this.min = min;
	}
	
	public void update() {
		if (this.isMoving) {
			Mouse.lastButtonPressed = 1;
			if (StdDraw.mousePressed()) {
				if (this.vertical) {
					this.y = StdDraw.mouseY();
					if (this.y + this.height > this.max) this.y = this.max - this.height;
					else if (this.y - this.height < this.min) this.y = this.min + this.height;
				} else {
					this.x = StdDraw.mouseX();
					if (this.x + this.width > this.max) this.x = this.max - this.width;
					else if (this.x - this.width < this.min) this.x = this.min + this.width;
				}
			} else {
				Mouse.lastButtonPressed = -1;
				this.isMoving = false;
			}
		}
		
		else if (Mouse.lastButtonPressed == 1 && StdDraw.mouseX() >= this.x - this.width && StdDraw.mouseY() <= this.y + this.height && StdDraw.mouseX() <= this.x + this.width && StdDraw.mouseY() >= this.y - this.height) {
			if (StdDraw.mousePressed()) {
				if (this.vertical) {
					this.y = StdDraw.mouseY();
					if (this.y + this.height > this.max) this.y = this.max - this.height;
					else if (this.y - this.height < this.min) this.y = this.min + this.height;
					this.isMoving = true;
				} else {
					this.x = StdDraw.mouseX();
					if (this.x + this.width > this.max) this.x = this.max - this.width;
					else if (this.x - this.width < this.min) this.x = this.min + this.width;
					this.isMoving = true;
				}
			} else {
				Mouse.lastButtonPressed = -1;
				this.isMoving = false;
			}
		}
	}
	
	public void render() {
		StdDraw.setPenColor(this.color);
		StdDraw.filledRectangle(this.x, this.y, this.width, this.height);
		
		StdDraw.setPenColor(StdDraw.BLACK);
		if (this.vertical) {
			StdDraw.line(this.x - (this.width / 3), this.y + 0.5, this.x + this.width / 3, this.y + 0.5);
			StdDraw.line(this.x - (this.width / 2), this.y, this.x + this.width / 2, this.y);
			StdDraw.line(this.x - (this.width / 3), this.y - 0.65, this.x + this.width / 3, this.y - 0.65);
		} else {
			StdDraw.line(this.x + 0.5, this.y - (this.height / 3), this.x + 0.5, this.y + (this.height / 3));
			StdDraw.line(this.x, this.y - (this.height / 2), this.x, this.y + (this.height / 2));
			StdDraw.line(this.x - 0.65, this.y - (this.height / 3), this.x - 0.65, this.y + (this.height / 3));
		}
	}

}
