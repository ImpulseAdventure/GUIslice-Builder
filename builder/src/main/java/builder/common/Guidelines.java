package builder.common;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import builder.models.AdvancedSnappingModel;
import builder.models.GuidelineModel;
import builder.widgets.GuidelineWidget;

/**
 * Guideline is a visual guide to help you align widgets.
 *
 * @author etet100
 */
public class Guidelines {
  private static Guidelines instance = null;

  public static Guidelines getInstance() {
    if (instance == null) {
      instance = new Guidelines();
    }
    return instance;
  }

  private GuidelinesList hGuidelines = new GuidelinesList();
  private GuidelinesList vGuidelines = new GuidelinesList();

  public Guidelines() {
    AdvancedSnappingModel.getInstance().addEventListener(new AdvancedSnappingModel.AdvancedSnappingModelListener() {
      public void addHGuidelinePressed() {
        Guidelines.this.createGuideline(GuidelineModel.Orientation.HORIZONTAL, 10);
      }

      public void addVGuidelinePressed() {
        Guidelines.this.createGuideline(GuidelineModel.Orientation.VERTICAL, 10);
      }
    });
  }

  public class Guideline  {
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

    public static class Serializer implements JsonSerializer<GuidelinesList> {
      @Override
      public JsonArray serialize(GuidelinesList object, Type type, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        for (Guideline guideline : object) {
          result.add(context.serialize(guideline.getModel()));
        }
        return result;
      }
    }
  }

  public GuidelineWidget createGuideline(GuidelineModel.Orientation orientation, int pos) {
    GuidelineWidget widget = new GuidelineWidget(orientation, pos);
    if (orientation == GuidelineModel.Orientation.HORIZONTAL) {
      hGuidelines.add(new Guideline(widget));
    } else {
      vGuidelines.add(new Guideline(widget));
    }
    return widget;
  }

  public GuidelinesList getGuidelines(GuidelineModel.Orientation orientation) {
    if (orientation == GuidelineModel.Orientation.HORIZONTAL) {
      return hGuidelines;
    } else {
      return vGuidelines;
    }
  }

  public boolean hasGuidelines() {
    return !hGuidelines.isEmpty() || !vGuidelines.isEmpty();
  }

  public static class Serializer implements JsonSerializer<Guidelines> {
    @Override
    public JsonElement serialize(Guidelines object, Type type, JsonSerializationContext context) {
      JsonObject result = new JsonObject();
      result.add("horizontal", context.serialize(object.hGuidelines));
      result.add("vertical", context.serialize(object.vGuidelines));
      return result;
    }
  }
}
