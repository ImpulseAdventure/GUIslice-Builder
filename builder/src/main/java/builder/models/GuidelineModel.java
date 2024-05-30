package builder.models;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import builder.common.EnumFactory;

public class GuidelineModel extends WidgetModel {
  public enum Orientation {
    VERTICAL,
    HORIZONTAL
  }

  private int pos;
  private int index; // unique index of the guideline
  private Orientation orientation;

  public GuidelineModel(Orientation orientation, int pos, int index) {
    super();
    this.pos = pos;
    this.index = index;
    this.orientation = orientation;
  }

  static public final int PROP_POS_V = 2;
  static public final int PROP_POS_H = 3;
  static public final int PROP_ORIENTATION = 4;
  static public final int PROP_MAX = PROP_ORIENTATION;

  public int getIndex() {
    return index;
  }

  @Override
  public String getKey() {
    return String.format("%s%s$%d", EnumFactory.GUIDELINE, orientation.toString(), index);
  }

  @Override
  public String getEnum() {
    return String.format("%s_%s_%d", EnumFactory.GUIDELINE, orientation.toString(), index);
  }

  @Override
  public String getType() {
    return EnumFactory.GUIDELINE;
  }

  @Override
  public boolean isCellReadOnly(int row) {
    switch (row) {
      case PROP_POS_H:
        return orientation != Orientation.HORIZONTAL;
      case PROP_POS_V:
        return orientation != Orientation.VERTICAL;
      default:
        return true;
    }
  }

  @Override
  public Class<?> getClassAt(int rowIndex) {
    switch (rowIndex) {
      case PROP_POS_H:
      case PROP_POS_V:
        return Integer.class;
      default:
        return String.class;
    }
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      switch (row) {
        case PROP_KEY:
          return "Key";
        case PROP_ENUM:
          return "ENUM";
        case PROP_POS_H:
          return "Position (H)";
        case PROP_POS_V:
          return "Position (V)";
        case PROP_ORIENTATION:
          return "Orientation";
        default:
          return "";
      }
    }

    switch (row) {
      case PROP_KEY:
        return getKey();
      case PROP_ENUM:
        return getEnum();
      case PROP_POS_H:
        return orientation == Orientation.HORIZONTAL ? Integer.valueOf(pos) : 0;
      case PROP_POS_V:
        return orientation == Orientation.VERTICAL ? Integer.valueOf(pos) : 0;
      case PROP_ORIENTATION:
        return orientation.toString();
      default:
        return "";
    }
  }

  @Override
  public void changeValueAt(Object value, int row) {
    if (row == PROP_POS_H) {
      setPos(value.toString());
    } else if (row == PROP_POS_V) {
      setPos(value.toString());
    }
  }

  @Override
  public String getPropertyName(int row) {
    switch (row) {
      case PROP_KEY:
        return "Key";
      case PROP_ENUM:
        return "ENUM";
      case PROP_POS_H:
        return "Position (H)";
      case PROP_POS_V:
        return "Position (V)";
      case PROP_ORIENTATION:
        return "Orientation";
      default:
        return "";
    }
  }

  @Override
  public void setX(int x) {
    if (orientation == Orientation.VERTICAL) {
      pos = x;
    }
  }

  @Override
  public void setY(int y) {
    if (orientation == Orientation.HORIZONTAL) {
      pos = y;
    }
  }

  @Override
  public int getRowCount() {
    return PROP_MAX;
  }

  public int getPos() {
    return pos;
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

  public void setPos(String pos) {
    setPos(Integer.valueOf(pos));
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

  @Override
  public void restore(Object oldValue, int row) {
    if (row == PROP_POS_V) {
      setX(Integer.valueOf(oldValue.toString()));
    } else if (row == PROP_POS_H) {
      setY(Integer.valueOf(oldValue.toString()));
    }
  }

  @Override
  public void restore(String oldValue) {
    // Not supported, sorry
  }

  public static class Serializer implements JsonSerializer<GuidelineModel> {
    @Override
    public JsonElement serialize(GuidelineModel object, Type type, JsonSerializationContext context) {
      JsonObject result = new JsonObject();
      result.addProperty("orientation", object.orientation.toString());
      result.addProperty("index", String.valueOf(object.index));
      result.addProperty("pos", String.valueOf(object.pos));
      return result;
    }
  }
}
