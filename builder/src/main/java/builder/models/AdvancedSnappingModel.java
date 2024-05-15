package builder.models;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.event.*;
import java.util.EventListener;

public class AdvancedSnappingModel implements RibbonModel, Serializable {
  public static final String ACTION_SHOW_GRID = "showgrid";
  public static final String ACTION_SHOW_GRID_ON = ACTION_SHOW_GRID + "_on";
  public static final String ACTION_SHOW_GRID_OFF = ACTION_SHOW_GRID + "_off";
  public static final String ACTION_SHOW_GRID_BG = "showgridbg";
  public static final String ACTION_SHOW_GRID_BG_ON = ACTION_SHOW_GRID_BG + "_on";
  public static final String ACTION_SHOW_GRID_BG_OFF = ACTION_SHOW_GRID_BG + "_off";
  public static final String ACTION_SNAP_TO_GRID = "snaptogrid";
  public static final String ACTION_SNAP_TO_GRID_ON = ACTION_SNAP_TO_GRID + "_on";
  public static final String ACTION_SNAP_TO_GRID_OFF = ACTION_SNAP_TO_GRID + "_off";
  public static final String ACTION_SHOW_GUIDELINES = "showguidelines";
  public static final String ACTION_SHOW_GUIDELINES_ON = ACTION_SHOW_GUIDELINES + "_on";
  public static final String ACTION_SHOW_GUIDELINES_OFF = ACTION_SHOW_GUIDELINES + "_off";
  public static final String ACTION_SNAP_TO_GUIDELINES = "snaptoguidelines";
  public static final String ACTION_SNAP_TO_GUIDELINES_ON = ACTION_SNAP_TO_GUIDELINES + "_on";
  public static final String ACTION_SNAP_TO_GUIDELINES_OFF = ACTION_SNAP_TO_GUIDELINES + "_off";
  public static final String ACTION_EDIT_GUIDELINES = "editguidelines";
  public static final String ACTION_SHOW_MARGINS = "showmargins";
  public static final String ACTION_SHOW_MARGINS_ON = ACTION_SHOW_MARGINS + "_on";
  public static final String ACTION_SHOW_MARGINS_OFF = ACTION_SHOW_MARGINS + "_off";
  public static final String ACTION_SNAP_TO_MARGINS = "snaptomargins";
  public static final String ACTION_SNAP_TO_MARGINS_ON = ACTION_SNAP_TO_MARGINS + "_on";
  public static final String ACTION_SNAP_TO_MARGINS_OFF = ACTION_SNAP_TO_MARGINS + "_off";
  public static final String ACTION_SNAP_TO_WIDGETS = "snaptowidgets";
  public static final String ACTION_SNAP_TO_WIDGETS_ON = ACTION_SNAP_TO_WIDGETS + "_on";
  public static final String ACTION_SNAP_TO_WIDGETS_OFF = ACTION_SNAP_TO_WIDGETS + "_off";

  private static AdvancedSnappingModel instance = null;

  public static synchronized AdvancedSnappingModel getInstance() {
    if (instance == null) {
      instance = new AdvancedSnappingModel();
    }
    return instance;
  }

  private boolean showGrid = false;
  private boolean showGridBg = false;
  private boolean snapToGrid = false;
  private boolean showGuidelines = false;
  private boolean snapToGuidelines = false;
  private boolean showMargins = false;
  private boolean snapToMargins = false;
  private boolean snapToWidgets = false;
  private EventListenerList listenerList = new EventListenerList();
  private ActionListener actionListener = null;

  public interface AdvancedSnappingModelListenerInterface extends EventListener {
    public void showGridChanged(boolean showGrid);
    public void showGridBgChanged(boolean showGridBg);
    public void snapToGridChanged(boolean snapToGrid);
    public void showGuidelinesChanged(boolean showGuidelines);
    public void snapToGuidelinesChanged(boolean snapToGuidelines);
    public void editGuidelinesCalled();
    public void showMarginsChanged(boolean showMargins);
    public void snapToMarginsChanged(boolean snapToMargins);
    public void snapToWidgetsChanged(boolean snapToWidgets);
  }

  // helper class to avoid having to implement all methods in the interface
  public class AdvancedSnappingModelListener implements AdvancedSnappingModelListenerInterface {
    public void showGridChanged(boolean showGrid) {};
    public void showGridBgChanged(boolean showGridBg) {};
    public void snapToGridChanged(boolean snapToGrid) {};
    public void showGuidelinesChanged(boolean showGuidelines) {};
    public void snapToGuidelinesChanged(boolean snapToGuidelines) {};
    public void editGuidelinesCalled() {};
    public void showMarginsChanged(boolean showMargins) {};
    public void snapToMarginsChanged(boolean snapToMargins) {};
    public void snapToWidgetsChanged(boolean snapToWidgets) {};
  }

  @Override
  public ActionListener getIncomingActionListener() {
    if (actionListener == null) {
      actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          System.out.println("AdvancedSnappingModel received action: " + e.getActionCommand());
          switch (e.getActionCommand()) {
            case ACTION_SHOW_GRID_ON:
            case ACTION_SHOW_GRID_OFF:
              setShowGrid(e.getActionCommand() == ACTION_SHOW_GRID_ON);
              break;
            case ACTION_SHOW_GRID_BG_ON:
            case ACTION_SHOW_GRID_BG_OFF:
              setShowGridBg(e.getActionCommand() == ACTION_SHOW_GRID_BG_ON);
              break;
            case ACTION_SNAP_TO_GRID_ON:
            case ACTION_SNAP_TO_GRID_OFF:
              setSnapToGrid(e.getActionCommand() == ACTION_SNAP_TO_GRID_ON);
              break;
            case ACTION_SHOW_GUIDELINES_ON:
            case ACTION_SHOW_GUIDELINES_OFF:
              setShowGuidelines(e.getActionCommand() == ACTION_SHOW_GUIDELINES_ON);
              break;
            case ACTION_SNAP_TO_GUIDELINES_ON:
            case ACTION_SNAP_TO_GUIDELINES_OFF:
              setSnapToGuidelines(e.getActionCommand() == ACTION_SNAP_TO_GUIDELINES_ON);
              break;
            case ACTION_SHOW_MARGINS_ON:
            case ACTION_SHOW_MARGINS_OFF:
              setShowMargins(e.getActionCommand() == ACTION_SHOW_MARGINS_ON);
              break;
            case ACTION_SNAP_TO_MARGINS_ON:
            case ACTION_SNAP_TO_MARGINS_OFF:
              setSnapToMargins(e.getActionCommand() == ACTION_SNAP_TO_MARGINS_ON);
              break;
            case ACTION_EDIT_GUIDELINES:
              callEditGuidelines();
              break;
            case ACTION_SNAP_TO_WIDGETS_ON:
            case ACTION_SNAP_TO_WIDGETS_OFF:
              setSnapToWidgets(e.getActionCommand() == ACTION_SNAP_TO_WIDGETS_ON);
              break;
          }
        }
      };
    }
    return actionListener;
  }

  @Override
  public void addActionListener(ActionListener listener) {
    assert(listener instanceof AdvancedSnappingModelListenerInterface);
    listenerList.add(AdvancedSnappingModelListenerInterface.class, (AdvancedSnappingModelListenerInterface) listener);
  }

  //

  public boolean isShowGrid() {
    return showGrid;
  }

  public boolean isShowGridBg() {
    return showGridBg;
  }

  public boolean isSnapToGrid() {
    return snapToGrid;
  }

  public boolean isShowGuidelines() {
    return showGuidelines;
  }

  public boolean isSnapToGuidelines() {
    return snapToGuidelines;
  }

  public boolean isShowMargins() {
    return showMargins;
  }

  public boolean isSnapToMargins() {
    return snapToMargins;
  }

  public boolean isSnapToWidgets() {
    return snapToWidgets;
  }

  //

  public void setShowGrid(boolean showGrid) {
    this.showGrid = showGrid;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.showGridChanged(showGrid); }});
  }

  public void setShowGridBg(boolean showGridBg) {
    this.showGridBg = showGridBg;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.showGridBgChanged(showGridBg); }});
  }

  public void setSnapToGrid(boolean snapToGrid) {
    this.snapToGrid = snapToGrid;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.snapToGridChanged(snapToGrid); }});
  }

  public void setShowGuidelines(boolean showGuidelines) {
    this.showGuidelines = showGuidelines;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.showGuidelinesChanged(showGuidelines); }});
  }

  public void setSnapToGuidelines(boolean snapToGuidelines) {
    this.snapToGuidelines = snapToGuidelines;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.snapToGuidelinesChanged(snapToGuidelines); }});
  }

  public void setShowMargins(boolean showMargins) {
    this.showMargins = showMargins;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.showMarginsChanged(showMargins); }});
  }

  public void setSnapToMargins(boolean snapToMargins) {
    this.snapToMargins = snapToMargins;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.snapToMarginsChanged(snapToMargins); }});
  }

  public void setSnapToWidgets(boolean snapToWidgets) {
    this.snapToWidgets = snapToWidgets;
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.snapToWidgetsChanged(snapToWidgets); }});
  }

  public void callEditGuidelines() {
    fireEvent(new FireEventCallback() { public void call(AdvancedSnappingModelListenerInterface listener) { listener.editGuidelinesCalled(); }});
  }

  //

  protected interface FireEventCallback {
    void call(AdvancedSnappingModelListenerInterface listener);
  }

  private void fireEvent(FireEventCallback callback) {
    for (AdvancedSnappingModelListenerInterface listener : listenerList.getListeners(AdvancedSnappingModelListenerInterface.class)) {
      callback.call(listener);
    }
  }
}
