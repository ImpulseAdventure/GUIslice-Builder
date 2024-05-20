package builder.models;

import builder.common.Guidelines;

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

  public Orientation getOrientation() {
    return orientation;
  }

  @Override
  public boolean isVertical() {
    return orientation == Orientation.VERTICAL;
  }
}
