import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

public class Button implements Serializable {
	
	public double x, y, width, height;
	public int fontSize;
	public Color backgroundColor, textColor;
	public String text;
	public boolean isSelected = false, isHighlighted = false;
	
	private Player player;
	private double fontYadjust;
	
	public Button(Player player, double x, double y, double width, double height, String text, int fontSize, double fontYadjust, Color backgroundColor, Color textColor) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.fontSize = fontSize;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.player = player;
		this.fontYadjust = fontYadjust;
	}
	
	public void update() {
		if (StdDraw.mouseX() >= this.x - this.width && StdDraw.mouseX() <= this.x + this.width && StdDraw.mouseY() >= this.y - this.height && StdDraw.mouseY() <= this.y + this.height) {
			this.isHighlighted = true;
			
			if (Mouse.lastButtonPressed == 1 && this.player.clickDelay == 0) {
				Mouse.lastButtonPressed = -1;
				this.player.clickDelay = this.player.clickDelayAmount;
				this.isSelected = true;
			}
		} else {
			this.isHighlighted = false;
		}
	}
	
	public void render() {
		//Background, border and shadow
		StdDraw.setPenColor(this.backgroundColor);
		if (this.isSelected) {
			StdDraw.filledRectangle(this.x + 0.25, this.y - 0.25, this.width, this.height);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(0.005);
			StdDraw.line(((this.x + 0.25) - this.width) + 0.125, ((this.y - 0.25) - this.height) + 0.25, ((this.x + 0.25) - this.width) + 0.125, ((this.y - 0.25) + this.height) - 0.25);
			StdDraw.line(((this.x + 0.25) - this.width) + 0.125, ((this.y - 0.25) + this.height) - 0.125, ((this.x + 0.25) + this.width) - 0.25, ((this.y - 0.25) + this.height) - 0.125);
			StdDraw.setPenRadius();
			StdDraw.rectangle(this.x + 0.25, this.y - 0.25, this.width, this.height);
		} else {
			StdDraw.filledRectangle(this.x, this.y, this.width, this.height);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(0.005);
			StdDraw.line((this.x + this.width) + 0.25, (this.y + this.height) - 0.25, (this.x + this.width) + 0.25, (this.y - this.height) - 0.25);
			StdDraw.line((this.x + this.width) + 0.25, (this.y - this.height) - 0.25, (this.x - this.width) + 0.25, (this.y - this.height) - 0.25);
			StdDraw.setPenRadius();
			StdDraw.rectangle(this.x, this.y, this.width, this.height);
		}
		//////////////////
		
		//Text
		StdDraw.setFont(new Font("Arial", Font.BOLD, this.fontSize));
		if (this.isSelected) {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(this.x + 0.55, this.y  + this.fontYadjust - 0.4, text);
			StdDraw.setPenColor(StdDraw.CYAN);
			StdDraw.text(this.x + 0.25, this.y  + this.fontYadjust - 0.25, text);
		} else {
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(this.x + 0.3, this.y + this.fontYadjust - 0.15, text);
			if (this.isHighlighted) StdDraw.setPenColor(StdDraw.CYAN);
			else StdDraw.setPenColor(this.textColor);
			StdDraw.text(this.x, this.y + this.fontYadjust, text);
		}
		//////////////////
	}

}
