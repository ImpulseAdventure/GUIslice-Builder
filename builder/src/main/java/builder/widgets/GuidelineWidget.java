package builder.widgets;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import builder.common.ScaledGraphics;
import builder.models.GuidelineModel;

public class GuidelineWidget extends Widget {
  private GuidelineModel model = null;

  public GuidelineWidget(GuidelineModel.Orientation orientation, int pos) {
    super.model = model = new GuidelineModel(orientation, pos);
  }

  @Override
  public GuidelineModel getModel() {
    return model;
  }

  public void draw(ScaledGraphics g2d, int pageWidth, int pageHeight) {
    // TODO Auto-generated method stub
    if (bSelected) {
      drawSelRect(g2d, getWinBounded());
    }

    if (model.isVertical()) {
      g2d.drawLine(model.getPos(), 0, model.getPos(), pageHeight);
    } else {
      g2d.drawLine(0, model.getPos(), pageWidth, model.getPos());
    }
  }

  @Override
  public void draw(Graphics2D g2d) {
    throw new UnsupportedOperationException("Not supported. Use ScaledGraphics instead.");
  }

  public void drawSelRect(ScaledGraphics g2d, Rectangle b) {
    // TODO Auto-generated method stub
    //super.drawSelRect(g2d, b);
  }

  @Override
  public HandleType getActionHandle(Point point) {
    // TODO Auto-generated method stub
    return super.getActionHandle(point);
  }
}
