package builder.widgets;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.Color;

import builder.common.ScaledGraphics;
import builder.models.GuidelineModel;

public class GuidelineWidget extends Widget {
  private static final int SNAPPING_DISTANCE = 3;

  private GuidelineModel model = null;

  public GuidelineWidget(GuidelineModel model) {
    super.model = this.model = model;
  }

  @Override
  public GuidelineModel getModel() {
    return model;
  }

  public void draw(ScaledGraphics graphics, int pageWidth, int pageHeight) {
    Color currentColor = graphics.getColor();
    if (bSelected) {
      graphics.setColor(Color.RED);
    }

    if (model.isVertical()) {
      graphics.drawLine(model.getPos(), 0, model.getPos(), pageHeight);
    } else {
      graphics.drawLine(0, model.getPos(), pageWidth, model.getPos());
    }

    graphics.setColor(currentColor);
  }

  @Override
  public void draw(Graphics2D g2d) {
    throw new UnsupportedOperationException("Not supported. Use ScaledGraphics instead.");
  }

  public void drawSelRect(ScaledGraphics graphics, Rectangle rectangle) {
    Color currentColor = graphics.getColor();
    graphics.setColor(Color.RED);

    graphics.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

    graphics.setColor(currentColor);
  }

  @Override
  public HandleType getActionHandle(Point point) {
    return model.isVertical() ? HandleType.DRAG_VERTICAL : HandleType.DRAG_HORIZONTAL;
  }

  @Override
  public boolean contains(Point point) {
    return contains(new Point2D.Double(point.x, point.y));
  }

  @Override
  public void moveBy(Point destination) { // moveBy means move TO
    model.setX(destination.x);
    model.setY(destination.y);
  }

  @Override
  public boolean contains(Point2D point) {
    if (model.isVertical()) {
      return (point.getX() >= model.getPos() - SNAPPING_DISTANCE && point.getX() <= model.getPos() + SNAPPING_DISTANCE);
    } else {
      return (point.getY() >= model.getPos() - SNAPPING_DISTANCE && point.getY() <= model.getPos() + SNAPPING_DISTANCE);
    }
  }
}
