package builder.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Guideline is a visual guide to help you align widgets.
 */
public class Guidelines {
  public enum Type {
    VERTICAL,
    HORIZONTAL
  }

  private List<Guideline> hGuidelines = new ArrayList<Guideline>();
  private List<Guideline> vGuidelines = new ArrayList<Guideline>();

  public class Guideline {
    private int pos;

    public Guideline(int pos) {
      this.pos = pos;
    }

    public int getPos() {
      return pos;
    }
  }

  public void createGuideline(Type type, int pos) {
    if (type == Type.HORIZONTAL) {
      hGuidelines.add(new Guideline(pos));
    } else {
      vGuidelines.add(new Guideline(pos));
    }
  }

  public List<Guideline> getGuidelines(Type type) {
    if (type == Type.HORIZONTAL) {
      return hGuidelines;
    } else {
      return vGuidelines;
    }
  }
}
