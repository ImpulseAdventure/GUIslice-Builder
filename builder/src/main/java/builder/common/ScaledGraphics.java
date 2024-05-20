package builder.common;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Helper facade to draw scaled graphics.
 */
public class ScaledGraphics {
  private Graphics2D graphics;
  private double zoomFactor;

  public ScaledGraphics(final Graphics2D graphics, double zoomFactor) {
    this.graphics = graphics;
    this.zoomFactor = zoomFactor;
  }

  public Graphics2D getWrapped() {
    return graphics;
  }

  public void dispose() {
    graphics.dispose();
  }

  public void setColor(Color color) {
    graphics.setColor(color);
  }

  public Color getColor() {
    return graphics.getColor();
  }

  public void setStroke(Stroke s) {
    graphics.setStroke(s);
  }

  public void drawRect(int x, int y, int width, int height) {
    graphics.drawRect((int) (x * zoomFactor), (int) (y * zoomFactor), (int) (width * zoomFactor),
        (int) (height * zoomFactor));
  }

  public void fillRect(int x, int y, int width, int height) {
    graphics.fillRect((int) (x * zoomFactor), (int) (y * zoomFactor), (int) (width * zoomFactor),
        (int) (height * zoomFactor));
  }

  public void drawLine(int x1, int y1, int x2, int y2) {
    graphics.drawLine((int) (x1 * zoomFactor), (int) (y1 * zoomFactor), (int) (x2 * zoomFactor),
        (int) (y2 * zoomFactor));
  }
}
