// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Button {
	public double x, y;
	public double x1, y1;
	public double x2, y2;
	public int fontInd;
	public String label;
	public Listener listener;

	public Button(double x, double y, int fontInd, String label, Listener listener) {
		Dimension dim = FontUtil.getTextDimension(label, fontInd);
		dim.width += 20;
		this.x = x;
		this.y = y;
		this.x1 = x - dim.width / 2;
		this.x2 = x + dim.width / 2;
		this.y1 = y - dim.height / 2;
		this.y2 = y + dim.height / 2;

		this.fontInd = fontInd;
		this.label = label;
		this.listener = listener;
	}

	public boolean isWithin(double x, double y, double width) {
		x -= width / 2;
		return x1 <= x && x <= x2 && y1 <= y && y <= y2;
	}

	public void render(double width) {
		glColor4d(0.5, 0.5, 0.5, 0.4);
		glPushMatrix();
		glTranslated(width / 2, 0, 0);
		glBegin(GL_QUADS);
		glVertex2d(x1, y1);
		glVertex2d(x1, y2);
		glVertex2d(x2, y2);
		glVertex2d(x2, y1);
		glEnd();
		glPopMatrix();
		glColor4d(1, 1, 1, 1);
		FontUtil.drawCenterText(label, fontInd, x + width / 2, y);
	}

	public interface Listener {
		void clicked(Button button);
	}
}
