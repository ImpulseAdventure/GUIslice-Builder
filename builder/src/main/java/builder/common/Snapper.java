package builder.common;

import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;

import builder.models.AdvancedSnappingModel;
import builder.models.GuidelineModel;
import builder.widgets.Widget;

/**
 * Snapper lets you find the nearest point to span dragged vertice.
 *
 * @author etet100
 */
public class Snapper {
  private boolean snapToGrid;
  private boolean snapToGuidelines;
  private boolean snapToWidgets;
  private boolean snapToMargins;
  private Widget currentWidget;
  private Type type;

  // HORIZONTAL snapper snaps to vertical lines during X-axis movement, and vice versa.
  public enum Type {
    HORIZONTAL,
    VERTICAL
  }

  private enum ItemType {
    GRID,
    GUIDELINE,
    MARGIN,
    WIDGET
  }

  public enum SourceEdge {
    MIN, // left or top edge
    MAX; // right or bottom edge
    public static final EnumSet<SourceEdge> ALL = EnumSet.allOf(SourceEdge.class);
  }

  public class SnappingMarker {
    public final int position, min, max;

    public SnappingMarker(int pos, int min, int max) {
      this.position = pos;
      this.min = min;
      this.max = max;
    }
  }

  private final List<SnappingMarker> snappingMarkers = new ArrayList<SnappingMarker>();

  private class Item {
    public final ItemType type;
    public final EnumSet<SourceEdge> sourceEdge;
    public final int pos;
    public final Widget widget;
    //public final Guidelines.Guideline guideline;

    public Item(ItemType type, int pos) {
      this(type, SourceEdge.ALL, pos, null, null);
    }

    public Item(ItemType type, EnumSet<SourceEdge> sourceEdge, int pos, Widget widget, GuidelineModel guideline) {
      this.type = type;
      this.sourceEdge = sourceEdge;
      this.pos = pos;
      this.widget = widget;
      //this.guideline = guideline;
    }

    public Item(EnumSet<SourceEdge> sourceEdge, int pos, Widget widget) {
      this(ItemType.WIDGET, sourceEdge, pos, widget, null);
    }

    public Item(int pos, GuidelineModel guideline) {
      this(ItemType.GUIDELINE, SourceEdge.ALL, pos, null, guideline);
    }
  }

  private List<Item> items = new ArrayList<Item>();

  private final int SNAP_DISTANCE = 5;

  public Snapper(Type type, Widget currentWidget, boolean snapToGrid, boolean snapToMargins, boolean snapToGuidelines, boolean snapToWidgets) {
    this.type = type;
    this.snapToGrid = snapToGrid;
    this.snapToGuidelines = snapToGuidelines;
    this.snapToWidgets = snapToWidgets;
    this.snapToMargins = snapToMargins;
    this.currentWidget = currentWidget;
  }

  public class Result {
    public int pos;
    public boolean snapped;
    public SourceEdge edge;
    public int distance;

    public Result(int pos, boolean snapped, SourceEdge edge, int distance) {
      this.pos = pos;
      this.snapped = snapped;
      this.edge = edge;
      this.distance = distance;
    }
  }

  public List<SnappingMarker> getSnappingMarkers() {
    return snappingMarkers;
  }

  /**
   * Snap min or max edge, depending on which one is closer to the nearest snapping point.
   */
  public Result snapMinOrMax(int posMin, int posMax) {
    Result snappedMin = snap_(posMin, SourceEdge.MIN);
    Result snappedMax = snap_(posMax, SourceEdge.MAX);

    if (snappedMin.distance < snappedMax.distance) {
      return snappedMin;
    } else {
      return snappedMax;
    }
  }

  private Result snap_(int pos, SourceEdge edge) {
    Item bestItem = findBestMatchingItem(pos, edge);
    if (bestItem != null && Math.abs(bestItem.pos - pos) < SNAP_DISTANCE) {
      System.out.println(type + "; snapped to " + bestItem.type);
      return new Result(
        bestItem.pos,
        true,
        edge,
        Math.abs(bestItem.pos - pos)
      );
    }

    return new Result(pos, false, edge, Integer.MAX_VALUE);
  }

  public int snap(int pos, SourceEdge edge) {
    snappingMarkers.clear();
    Item bestItem = findBestMatchingItem(pos, edge);
    if (bestItem != null && Math.abs(bestItem.pos - pos) < SNAP_DISTANCE) {
      if (bestItem.type == ItemType.WIDGET) {
        snappingMarkers.add(new SnappingMarker(bestItem.pos, bestItem.pos - SNAP_DISTANCE, bestItem.pos + SNAP_DISTANCE));
      }
      System.out.println(type + "; snapped to " + bestItem.type);
      return bestItem.pos;
    }

    return pos;
  }

  private Item findBestMatchingItem(int pos, SourceEdge edge) {
    Item bestItem = null;
   //System.out.println(items);
    for (Item item : items) {
      if (item.type == ItemType.WIDGET && item.widget == currentWidget) { continue; }
      if (!item.sourceEdge.contains(edge)) { continue; }
      if (item.type == ItemType.GRID && !snapToGrid) { continue; }
      if (item.type == ItemType.GUIDELINE && !snapToGuidelines) { continue; }
      if (item.type == ItemType.WIDGET && !snapToWidgets) { continue; }
      if (item.type == ItemType.MARGIN && !snapToMargins) { continue; }
      if (Math.abs(item.pos - pos) > SNAP_DISTANCE) { continue; }
      if (bestItem == null || Math.abs(item.pos - pos) < Math.abs(bestItem.pos - pos)) {
        bestItem = item;
      }
      if (item.pos > pos) {
        break;
      }
    }

    return bestItem;
  }

  private void sortItems() {
    items.sort((a, b) -> a.pos - b.pos);
  }

  // position relative to the left or top edge of the widget
  public void addMargin(int pos, SourceEdge snappingTo) {
    items.add(new Item(ItemType.MARGIN, pos));
    sortItems();
  }

  // position relative to the left or top edge of the widget
  public void addGuideline(GuidelineModel guidelineModel) {
    items.add(new Item(guidelineModel.getPos(), guidelineModel));
    sortItems();
  }

  public void addWidget(Widget widget, int margin) {
    // horizontal: snap to left and right edges, vertical: snap to top and bottom edges
    if (type == Type.HORIZONTAL) {
      // edge
      items.add(new Item(EnumSet.of(SourceEdge.MIN), widget.getX(), widget));
      items.add(new Item(EnumSet.of(SourceEdge.MAX), widget.getX() + widget.getWidth(), widget));
      // edge with margin
      items.add(new Item(EnumSet.of(SourceEdge.MAX), widget.getX() - margin, widget));
      items.add(new Item(EnumSet.of(SourceEdge.MIN), widget.getX() + widget.getWidth() + margin, widget));
    } else {
      // edge
      items.add(new Item(EnumSet.of(SourceEdge.MIN), widget.getY(), widget));
      items.add(new Item(EnumSet.of(SourceEdge.MAX), widget.getY() + widget.getHeight(), widget));
      // edge with margin
      items.add(new Item(EnumSet.of(SourceEdge.MAX), widget.getY() - margin, widget));
      items.add(new Item(EnumSet.of(SourceEdge.MIN), widget.getY() + widget.getHeight() + margin, widget));
    }

    sortItems();
  }

  public void clearWidgets() {
    //widgets.clear();
  }

  public void addGrid(int majorDivs, int minorDivs, int size) {
    int gridSize = minorDivs > 0 ? minorDivs : majorDivs;
    int gridPos = gridSize;
    while (gridPos < size) {
      items.add(new Item(ItemType.GRID, gridPos));
      gridPos += gridSize;
    }
    sortItems();
  }

  public static class Builder {
    public static Snapper buildHSnapper(
      Widget currentWidget,
      AdvancedSnappingModel snappingModel,
      Guidelines guidelines,
      List<Widget> widgets,
      int pageSize,
      int marginSize,
      int widgetsSpacing,
      int gridMajorSpacing,
      int gridMinorSpacing
    ) {
      Snapper snapper = new Snapper(Type.HORIZONTAL, currentWidget, snappingModel.isSnapToGrid(), snappingModel.isSnapToMargins(), snappingModel.isSnapToGuidelines(), snappingModel.isSnapToWidgets());
      snapper.addGrid(gridMajorSpacing, gridMinorSpacing, pageSize);
      addMargins(pageSize, marginSize, snapper);
      addGuidelines(guidelines, GuidelineModel.Orientation.VERTICAL, snapper); // horizontal snaps to vertical lines
      addWidgets(widgets, widgetsSpacing, snapper);
      return snapper;
    }

    private static void addGuidelines(Guidelines guidelines, GuidelineModel.Orientation orientation, Snapper snapper) {
      for (Guidelines.Guideline guideline : guidelines.getGuidelines(orientation)) {
        snapper.addGuideline(guideline.getModel());
      }
    }

    private static void addWidgets(List<Widget> widgets, int widgetsSpacing, Snapper snapper) {
      for (Widget widget : widgets) {
        snapper.addWidget(widget, widgetsSpacing);
      }
    }

    private static void addMargins(int pageSize, int marginSize, Snapper snapper) {
      snapper.addMargin(marginSize, Snapper.SourceEdge.MIN);
      snapper.addMargin(pageSize - marginSize, Snapper.SourceEdge.MAX);
    }

    public static Snapper buildVSnapper(
      Widget currentWidget,
      AdvancedSnappingModel snappingModel,
      Guidelines guidelines,
      List<Widget> widgets,
      int pageSize,
      int marginSize,
      int widgetsSpacing,
      int gridMajorSpacing,
      int gridMinorSpacing
    ) {
      Snapper snapper = new Snapper(Type.VERTICAL, currentWidget, snappingModel.isSnapToGrid(), snappingModel.isSnapToMargins(), snappingModel.isSnapToGuidelines(), snappingModel.isSnapToWidgets());
      snapper.addGrid(gridMajorSpacing, gridMinorSpacing, pageSize);
      addMargins(pageSize, marginSize, snapper);
      addGuidelines(guidelines, GuidelineModel.Orientation.HORIZONTAL, snapper); // vertical snaps to horizontal lines
      addWidgets(widgets, widgetsSpacing, snapper);
      return snapper;
    }
  }
}
