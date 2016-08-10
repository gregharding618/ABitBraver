import java.awt.Color;
import java.awt.Font;
import java.awt.Scrollbar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dialogue implements Serializable {
	
	public Entity_NPC speaker;
	public String text;
	public boolean finishedWriting = false;
	
	public List<Button> buttons = new ArrayList<Button>();
	
	protected int writingSpeed = 2;
	protected int writingTimer = 0;
	protected int currentIndex = 0;
	
	protected ScrollBar scrollbar;
	
	public Dialogue(Entity_NPC speaker, String text) {
		this.speaker = speaker;
		this.text = text;
	}
	
	public void update() {
		if (this.currentIndex < this.text.length()) {
			if (this.writingTimer >= this.writingSpeed) {
				this.writingTimer = 0;
				this.currentIndex++;
			} else {
				this.writingTimer++;
			}
		} else {
			this.finishedWriting = true;
		}
		
		if (this.scrollbar != null) {
			this.scrollbar.update();
		}
		
		for (Button button : this.buttons) {
			button.update();
		}
	}
	
	public void render() {
		StdDraw.picture(50, 11.5, "textbox_background.png");
		
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.setFont(new Font("Arial", Font.PLAIN, 22));
		
		double y = 16;
		double scrollPercentToTop = 1;
		
		//Adjust y-coordinate if scroll bar is needed
		if (this.scrollbar != null) {
			scrollPercentToTop = (this.scrollbar.y - this.scrollbar.min - this.scrollbar.height) / (this.scrollbar.max - (this.scrollbar.height * 2) - this.scrollbar.min); //0.##
		}
		///////////////////
		
		int nextSpace = findNextSpace(40);
		int previousIndex = 0;
		int indents = (int) (((this.text.length() / 40.0) + 1) + (this.buttons.size() / 2));
		
		boolean doneWriting = false;
		
		while (!doneWriting) {
			if (this.scrollbar != null) {
				if (nextSpace < this.currentIndex) {
					if (y + ((indents) * (1 - scrollPercentToTop)) < 21.5) StdDraw.text(50, y + ((indents) * (1 - scrollPercentToTop)), this.text.substring(previousIndex, nextSpace));
					previousIndex = nextSpace;
					nextSpace = findNextSpace(nextSpace + 40);
					y -= 2;
				} else {
					if (y + ((indents) * (1 - scrollPercentToTop)) < 21.5) StdDraw.text(50, y + ((indents) * (1 - scrollPercentToTop)), this.text.substring(previousIndex, this.currentIndex));
					y -= 4;
					doneWriting = true;
				}
			}
			
			else {
				if (y < 2 && this.scrollbar == null) {
					this.scrollbar = new ScrollBar(82, 20, 22, 2, 1, 2, true, new Color(217, 216, 223));
				}

				if (nextSpace < this.currentIndex) {
					StdDraw.text(50, y, this.text.substring(previousIndex, nextSpace));
					previousIndex = nextSpace;
					nextSpace = findNextSpace(nextSpace + 40);
					y -= 2;
				} else {
					StdDraw.text(50, y, this.text.substring(previousIndex, this.currentIndex));
					y -= 4;
					doneWriting = true;
				}
			}
		}
		
		//Buttons
		int index = 0;
		for (Button button : this.buttons) {
			if (this.buttons.get(0).equals(button)) y -= 2;
			
			if (index == 2) {
				y -= 4;
				index = 0;
			}
			
			if (y + ((indents) * (1 - scrollPercentToTop)) < 21.5) {
				if (index == 0) {
					button.x = 40;
					button.y = y + ((indents) * (1 - scrollPercentToTop));
					button.render();
				} else {
					button.x = 60;
					button.y = y + ((indents) * (1 - scrollPercentToTop));
					button.render();
				}
			}
			
			index++;
		}
		///////////////////
		
		//E to continue text
		if (this.finishedWriting) {
			if (this.scrollbar == null) {
				StdDraw.setPenColor(StdDraw.ORANGE);
				StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
				StdDraw.text(50, 2, "Press E to continue");
			} else {
				if (y + (76 * (1 - scrollPercentToTop)) > -1) {
					StdDraw.setPenColor(StdDraw.ORANGE);
					StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
					StdDraw.text(50, y + ((indents) * (1 - scrollPercentToTop)), "Press E to continue");
				}
			}
		}
		///////////////////
		
		//Entity's name
		StdDraw.picture(50, 20.5, "textbox_namebanner.png");
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
		StdDraw.text(50, 20.5, this.speaker.name);
		///////////////////
		
		//Scroll bar
		if (this.scrollbar != null) {
			this.scrollbar.render();
		}
		///////////////////
	}
	
	public void addButton(Button button) {
		this.buttons.add(button);
	}
	
	private int findNextSpace(int start) {
		int index = start + 1;
		
		for (int i = start + 1; i < this.text.length(); i++) {
			if (this.text.charAt(i) == ' ') {
				index = i;
				break;
			}
		}
		
		if (index > this.text.length()) return this.text.length() + 1;
		else if (index == start + 1 && this.text.charAt(index) != ' ') return this.text.length() + 1;
		
		else return index;
	}

}
