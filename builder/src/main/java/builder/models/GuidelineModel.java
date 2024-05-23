package builder.models;

import java.lang.reflect.Type;
import java.awt.Point;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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

  public static class Serializer implements JsonSerializer<GuidelineModel> {
    @Override
    public JsonElement serialize(GuidelineModel object, Type type, JsonSerializationContext context) {
      JsonObject result = new JsonObject();
      result.addProperty("orientation", object.orientation.toString());
      result.addProperty("pos", String.valueOf(object.pos));
      return result;
    }
  }
}
