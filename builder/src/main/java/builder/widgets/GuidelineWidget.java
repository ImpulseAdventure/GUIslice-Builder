package builder.widgets;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;

import builder.common.ScaledGraphics;
import builder.models.GuidelineModel;

public class GuidelineWidget extends Widget {
  private static final int SNAPPING_DISTANCE = 3;

  private GuidelineModel model = null;

  public GuidelineWidget(GuidelineModel.Orientation orientation, int pos) {
    super.model = model = new GuidelineModel(orientation, pos);
  }

  @Override
  public GuidelineModel getModel() {
    return model;
  }

  // @Override
  // public Rectangle getWinBounded() {
  //   Rectangle b = new Rectangle();
  //   if (model.isVertical()) {
  //     b.x = model.getPos() - 1;
  //     b.y = 0;
  //     b.width = 3;
  //     b.height = u.getWinHeight();
  //   } else {
  //     b.x = 0;
  //     b.y = model.getPos() - 1;
  //     b.width = u.getWinWidth();
  //     b.height = 3;
  //   }
  //   b.x = model.getX() - 1;
  //   b.y = model.getY() - 1;
  //   if (model.isVertical()) {
  //     b.width = 3;
  //     b.height = model.getLength();
  //   } else {
  //     b.width = model.getLength();
  //     b.height = 3;
  //   }
  //   return b;
  // }

  public void draw(ScaledGraphics graphics, int pageWidth, int pageHeight) {
    Color currentColor = graphics.getColor();
    if (bSelected) {
      //drawSelRect(graphics, getWinBounded());
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

  public void updateLocation(int x, int y) {
    if (model.isVertical()) {
      model.setPos(x);
    } else {
      model.setPos(y);
    }
    model.setX(x);
    model.setY(y);
  }

  @Override
  public boolean contains(Point2D point) {
    if (model.isVertical()) {
      return (point.getX() >= model.getPos() - SNAPPING_DISTANCE && point.getX() <= model.getPos() + SNAPPING_DISTANCE);
    } else {
      return (point.getY() >= model.getPos() - SNAPPING_DISTANCE && point.getY() <= model.getPos() + SNAPPING_DISTANCE);
    }
    // Rectangle r = getWinBounded();
    // Rectangle2D.Double rectangle = new Rectangle2D.Double();
    // boolean isVertical = model.isVertical();
    // rectangle.x = r.x - (RESIZE_HANDLE_SIZE / 2);
    // rectangle.y = r.y - (RESIZE_HANDLE_SIZE / 2);
    // rectangle.width = RESIZE_HANDLE_SIZE + (isVertical ? 0 : r.width);
    // rectangle.height = RESIZE_HANDLE_SIZE + (isVertical ? r.height : 0);
    // return rectangle.contains(point);
  }
}
