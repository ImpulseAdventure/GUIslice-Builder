package builder.models;

import java.awt.Point;

//import builder.common.Guidelines;

public class GuidelineModel extends LineModel {
  public enum Orientation {
    VERTICAL,
    HORIZONTAL
  }

  private int pos;
  private Orientation orientation;

  public GuidelineModel(Orientation orientation, int pos) {
    super();
    this.pos = pos;
    this.orientation = orientation;
  }

  public int getPos() {
    return pos;
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

  public int getX() {
    return orientation == Orientation.VERTICAL ? pos : 0;
  }

  public int getY() {
    return orientation == Orientation.HORIZONTAL ? pos : 0;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public boolean isVertical() {
    return orientation == Orientation.VERTICAL;
  }
}
