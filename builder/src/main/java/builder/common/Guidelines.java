package builder.common;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Consumer;

import builder.models.GuidelineModel;
import builder.widgets.GuidelineWidget;

/**
 * Guideline is a visual guide to help you align widgets.
 */
public class Guidelines {
  private GuidelinesList hGuidelines = new GuidelinesList();
  private GuidelinesList vGuidelines = new GuidelinesList();

  public class Guideline {
    private GuidelineModel model;
    private GuidelineWidget widget;

    public Guideline(GuidelineWidget widget) {
      this.widget = widget;
      this.model = widget.getModel();
    }

    public GuidelineModel getModel() {
      return model;
    }
  }

  public class GuidelinesList extends ArrayList<Guideline> {
    public void forEachWidget(Consumer<GuidelineWidget> action) {
      for (Guideline guideline : this) {
        action.accept(guideline.widget);
      }
    }
  }

  public void createGuideline(GuidelineModel.Orientation orientation, int pos) {
    if (orientation == GuidelineModel.Orientation.HORIZONTAL) {
      hGuidelines.add(new Guideline(new GuidelineWidget(orientation, pos)));
    } else {
      vGuidelines.add(new Guideline(new GuidelineWidget(orientation, pos)));
    }
  }

  public GuidelinesList getGuidelines(GuidelineModel.Orientation orientation) {
    // vGuidelines.f
    if (orientation == GuidelineModel.Orientation.HORIZONTAL) {
      return hGuidelines;
    } else {
      return vGuidelines;
    }
  }

  public boolean hasGuidelines() {
    return !hGuidelines.isEmpty() || !vGuidelines.isEmpty();
  }

  public GuidelineWidget getOne(Point mapPoint) {
    for (Guideline guideline : hGuidelines) {
      if (!guideline.model.isVertical() && mapPoint.y >= guideline.model.getPos() -3 && mapPoint.y <= guideline.model.getPos() + 3) {
        return guideline.widget;
      }
    }

    for (Guideline guideline : vGuidelines) {
      if (guideline.model.isVertical() && mapPoint.x >= guideline.model.getPos() -3 && mapPoint.x <= guideline.model.getPos() + 3) {
        return guideline.widget;
      }
    }

    return null;
  }

  // public void draw(Graphics2D g2d, int pageWidth, int pageHeight) {
  //   for (Guideline guideline : hGuidelines) {
  //     guideline.draw(g2d, pageWidth);
  //     g2d.setColor(u.GUIDELINE_COLOR);
  //     g2d.drawLine(0, guideline.getPos(), u.width, guideline.getPos());
  //   }
  // }
}
