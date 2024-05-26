package builder.common;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.event.EventListenerList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import builder.controller.Controller;
import builder.controller.PropManager;
import builder.models.AdvancedSnappingModel;
import builder.models.AdvancedSnappingModel.AdvancedSnappingModelListenerInterface;
import builder.models.GuidelineModel;
import builder.models.PageModel;
import builder.models.ProjectModel;
import builder.models.GuidelineModel.Orientation;
import builder.widgets.GuidelineWidget;
import builder.widgets.Widget;

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

  public static interface ActionListenerInterface extends EventListener {
    public void updated();
  }

  // helper class to avoid having to implement all methods in the interface
  public static class ActionListener implements ActionListenerInterface {
    public void updated() {}
  }

  public void addEventListener(ActionListenerInterface listener) {
    listenerList.add(ActionListenerInterface.class, listener);
  }

  private GuidelinesList hGuidelines = new GuidelinesList();
  private GuidelinesList vGuidelines = new GuidelinesList();
  private EventListenerList listenerList = new EventListenerList();

  public Guidelines() {
    AdvancedSnappingModel.getInstance().addEventListener(new AdvancedSnappingModel.AdvancedSnappingModelListener() {
      public void addHGuidelinePressed() {
        Guidelines.this.createGuidelineOnTheBestPosition(GuidelineModel.Orientation.HORIZONTAL);
      }

      public void addVGuidelinePressed() {
        Guidelines.this.createGuidelineOnTheBestPosition(GuidelineModel.Orientation.VERTICAL);
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

    public int findBestPositionForNewItem(int max) {
      if (size() == 0) {
        return max / 2;
      }

      ArrayList<Integer> numbers = new ArrayList<Integer>(size() + 2);
      for (Guideline guideline : this) {
        numbers.add(guideline.getModel().getPos());
      }
      numbers.add(0);
      numbers.add(max);
      numbers.sort((Integer o1, Integer o2) -> o1.compareTo(o2));

      int bestDistance = 0;
      int bestNumber = 0;

      for (int i = 1; i < numbers.size(); i++) {
        int distance = Math.abs(numbers.get(i) - numbers.get(i - 1));
        if (distance > bestDistance) {
          bestDistance = distance;
          bestNumber = (numbers.get(i) + numbers.get(i - 1)) / 2;
        }
      }

      return bestNumber;
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

    public int getFreeIndex() {
      // @TODO this is not the best way to find a free index
      findexistingindex:
      for (int index = 0; index < Integer.MAX_VALUE; index++) {
        for (Guideline guideline : this) {
          if (guideline.getModel().getIndex() == index) {
            continue findexistingindex;
          }
        }
        return index;
      }
      return -1;
    }
  }

  protected void createGuidelineOnTheBestPosition(GuidelineModel.Orientation orientation) {
    ProjectModel project = Controller.getProjectModel();
    if (orientation == GuidelineModel.Orientation.HORIZONTAL) {
      createGuideline(orientation, hGuidelines.findBestPositionForNewItem(project.getHeight()));
    } else {
      createGuideline(orientation, vGuidelines.findBestPositionForNewItem(project.getWidth()));
    }
    notifyListeners();
  }

  private void notifyListeners() {
    for (ActionListenerInterface listener : listenerList.getListeners(ActionListenerInterface.class)) {
      listener.updated();
    }
  }

  public GuidelineWidget createGuideline(GuidelineModel.Orientation orientation, int pos) {
    GuidelinesList guidelines = getGuidelines(orientation);
    GuidelineModel model = new GuidelineModel(orientation, pos, guidelines.getFreeIndex());
    GuidelineWidget widget = new GuidelineWidget(model);
    Guideline guideline = new Guideline(widget);
    guidelines.add(guideline);
    PropManager.getInstance().addPropEditor(guideline.getModel());
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

  public Widget findOne(Point2D p) {
    for (Guideline guideline : hGuidelines) {
      if (guideline.widget.contains(p)) {
        return guideline.widget;
      }
    }
    for (Guideline guideline : vGuidelines) {
      if (guideline.widget.contains(p)) {
        return guideline.widget;
      }
    }
    return null;
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
