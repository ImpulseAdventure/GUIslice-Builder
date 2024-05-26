/**
 *
 * The MIT License
 *
 * Copyright 2018-2024 Paul Conti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package builder.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import builder.Builder;
import builder.commands.Command;
import builder.commands.DragByArrowCommand;
import builder.commands.DragWidgetCommand;
import builder.commands.ResizeCommand;
import builder.commands.History;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.controller.PropManager;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.events.iSubscriber;
import builder.fonts.FontFactory;
import builder.models.GridModel;
import builder.models.LineModel;
import builder.models.PageModel;
import builder.models.ProjectModel;
import builder.models.SpinnerModel;
import builder.models.WidgetModel;
import builder.prefs.GridEditor;
import builder.widgets.Widget;
import builder.widgets.WidgetFactory;
import builder.widgets.Widget.HandleType;

/**
 * The Class PagePane provides the view of selected widgets for one page.
 * 
 * @author Paul Conti
 * 
 */
public class PagePane extends JPanel implements iSubscriber {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The widgets. */
  private List<Widget> widgets = new ArrayList<Widget>();
  
  /** The mouse pt. */
  private Point mousePt;
  private Point dragPt;
  
  /** The mouse rect. */
  private Rectangle mouseRect = new Rectangle();
  
  // /** Cursor style will be CROSSHAIR in rectangular selection mode or arrow in default mode */
  // public static Cursor  = new Cursor(Cursor.CROSSHAIR_CURSOR); 

  /** The rectangular selection enabled switch */
  public static boolean bRectangularSelectionMode = false;
  
  /** The selecting using a rubber band. */
  private boolean bMultiSelectionBox = false;
  
  /** The donotSelectKey */
  private String donotSelectKey = null;

  private enum CurrentAction {
    NONE, DRAGGING_WIDGET, RESIZING_WIDGET, RECTANGULAR_SELECTION
  }

  /** The current action. */
  private CurrentAction currentAction = CurrentAction.NONE;

  /** Used in resizing procedure */
  private Widget widgetUnderCursor = null;

  /** The paint base widgets indicator. */
  private boolean bPaintBaseWidgets = false;

  /** The project model. */
  private ProjectModel pm = null;
  
  /** The grid model. */
  private GridModel gridModel;
  
  /** The page model */
  PageModel model = null;
  
  /** The instance. */
  private PagePane instance = null;
  
  /** The Ribbon. */
  private static Ribbon ribbon = null;
  
  /** The drag command. */
  public DragWidgetCommand dragCommand = null;
  
  /** The resize command. */ 
  public ResizeCommand resizeCommand = null;
  
  /** The drag using arrows command */
  public DragByArrowCommand dragArrowsCommand = null;

  /** the number of selected widgets. */
  private int selectedCnt = 0;
  
  /**
   * the number of selected widgets that are either radio buttons or checkboxes.
   */
  private int selectedGroupCnt = 0;
  
  /** The zoom factor. */
  public static double zoomFactor = 1;
  
  /** The zoom AffineTransform at. */
  private static AffineTransform at = null;
  
  /** The inverse AffineTransform at. */
  public static AffineTransform inv_at;
  
  private String[] commands = {
                                  "UP",
                                  "DOWN",
                                  "LEFT",
                                  "RIGHT"
                              };                      
  private int[] keys = {
      KeyEvent.VK_UP,
      KeyEvent.VK_DOWN,
      KeyEvent.VK_LEFT,
      KeyEvent.VK_RIGHT,
      KeyEvent.VK_KP_UP,
      KeyEvent.VK_KP_DOWN,
      KeyEvent.VK_KP_LEFT,
      KeyEvent.VK_KP_RIGHT,
  };

  private ActionListener panelAction;
  
  int nX = 0;
  int nY = 0;

  /**
   * Instantiates a new page pane.
   */
  public PagePane() {
    instance = this;
    ribbon = Ribbon.getInstance();
    pm = Controller.getProjectModel();
    gridModel = (GridModel) GridEditor.getInstance().getModel();
    model = new PageModel();
    mousePt = new Point(pm.getWidth() / 2, pm.getHeight() / 2);
    dragPt = mousePt;
    MouseHandler mouseHandler = new MouseHandler();
    this.addMouseListener(mouseHandler);
    this.addMouseWheelListener(mouseHandler);
    this.addMouseMotionListener(new MouseMotionHandler());
    panelAction = new ActionListener() {   
      @Override
      public void actionPerformed(ActionEvent ae)
      {
        if (dragArrowsCommand == null) {
          dragArrowsCommand = new DragByArrowCommand(instance);
          if (!dragArrowsCommand.start()) return;
        }
        String command = (String) ae.getActionCommand();
        if (command.equals(commands[0]))
          dragArrowsCommand.moveUP();             
        else if (command.equals(commands[1]))
          dragArrowsCommand.moveDOWN();             
        else if (command.equals(commands[2]))
          dragArrowsCommand.moveLEFT();             
        else if (command.equals(commands[3]))
          dragArrowsCommand.moveRIGHT();             
        repaint();  
      }
    };
    for (int i = 0; i < commands.length; i++) {   
      registerKeyboardAction(panelAction,
                      commands[i],
// Java 9 replaces InputEvent.ALT_MASK with InputEvent.ALT_DOWN_MASK
//                      KeyStroke.getKeyStroke(keys[i], InputEvent.ALT_MASK),
                      KeyStroke.getKeyStroke(keys[i], InputEvent.ALT_DOWN_MASK),
                      JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    for (int i = 0; i < commands.length; i++) {   
      registerKeyboardAction(panelAction,
                      commands[i],
                   // Java 9 replaces InputEvent.ALT_MASK with InputEvent.ALT_DOWN_MASK
//                    KeyStroke.getKeyStroke(keys[i], InputEvent.ALT_MASK),
                      KeyStroke.getKeyStroke(keys[i+4], InputEvent.ALT_DOWN_MASK),
                      JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    this.setLocation(0, 0);
    this.setOpaque(true);
    this.setFocusable( true ); 
    this.setBorder(BorderFactory.createLineBorder(Color.black));
    this.setVisible(true);
    if (at == null) {
      zoomTransform();
    }
  }

  /**
   * paintComponent.
   *
   * @param g
   *          the g
   * @see JComponent#paintComponent(Graphics)
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (getPageType().equals(EnumFactory.PROJECT)) {
      return;
    }

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.transform(at);
    int width = pm.getWidth();
    int height = pm.getHeight();
    if (pm.useBackgroundImage() && !gridModel.getGrid()) {
      g2d.setColor(Color.BLACK);
      g2d.fillRect(0, 0, width, height);
      g2d.drawImage(pm.getImage(), 0, 0, null);
    } else {
      if (gridModel.getGrid()) {
        g2d.setColor(gridModel.getBackGroundColor());
        g2d.fillRect(0, 0, width, height);
        drawCoordinates(g2d, width, height);
      } else {
        g2d.setColor(pm.getBackgroundColor());
        g2d.fillRect(0,  0, width, height);
      }
    }
    // Now set to overwrite
    g2d.setComposite(AlphaComposite.SrcOver);
    // output this page's widgets
    for (Widget w : widgets) {
      w.draw(g2d);
    } 
    /* output any base page widgets unless this is a project
     * base page or popup page.
     */
    if (bPaintBaseWidgets && Controller.getBaseWidgets() != null) {
      for (Widget w : Controller.getBaseWidgets()) {
        w.unSelect(); // just in case
        w.draw(g2d);
      }
    }
    if (bMultiSelectionBox) {
      // draw our selection rubber band 
      g2d.setColor(Color.RED);
      g2d.setStroke(Widget.dashed);
      g2d.drawRect(mouseRect.x, mouseRect.y,
          mouseRect.width, mouseRect.height);
    }
    // gets rid of the copy
    g2d.dispose();
  };

  /**
   * set Zoom factor from open project
   */
  public static void setZoom(double zoom) {
    ribbon.enableZoom(zoom > 1.0);
    ribbon.enableZoomReset(zoom != 1.0);
    MenuBar.miZoomOut.setEnabled(zoom > 1.0);
    MenuBar.miZoomReset.setEnabled(zoom != 1.0);
    zoomFactor = zoom;
    zoomTransform();
  }
  
  /**
   * create a transform
   */
  public static void zoomTransform() {
    at = new AffineTransform();
    double xOffset = 0.0;
    double yOffset = 0.0;
    
    at.translate(xOffset, yOffset);
    at.scale(zoomFactor, zoomFactor);
    try {
      inv_at = at.createInverse();
    } catch (NoninvertibleTransformException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Zoom in.
   */
  public static void zoomIn() {
    zoomFactor *= 1.1;
    zoomTransform();
    ribbon.enableZoom(true);
    MenuBar.miZoomOut.setEnabled(true);
    updateZoomReset();
  }
  
  /**
   * Zoom out.
   */
  public static void zoomOut() {
    zoomFactor /= 1.1;
    if (zoomFactor < 1.1) {
      zoomFactor = 1.0;
      ribbon.enableZoom(false);
      MenuBar.miZoomOut.setEnabled(false);
    }
    updateZoomReset();
    zoomTransform();
  }

  /** Zoom reset */
  public static void zoomReset() {
    zoomFactor = 1.0;
    ribbon.enableZoom(false);
    MenuBar.miZoomOut.setEnabled(false);
    updateZoomReset();
    zoomTransform();
  }

  /**
   * Update zoom reset button state.
   */
  private static void updateZoomReset() {
    MenuBar.miZoomReset.setEnabled(zoomFactor != 1.0);  
    ribbon.enableZoomReset(zoomFactor != 1.0);
  }

  /**
   * Zoom off.
   */
  public static void zoomOff() {
    zoomFactor = 1.0;
    ribbon.enableZoom(false);
    zoomTransform();
  }

  /**
   * rectangularSelection.
   */
  public void rectangularSelection(boolean bValue) {
    bRectangularSelectionMode = bValue;
    if (bValue)
      setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    else
      setCursor(Cursor.getDefaultCursor());
  }
  
  /**
   * Draw grid coordinates.
   *
   * @param g2d
   *          the graphics object
   * @param w
   *          the width of simulated TFT screen
   * @param h
   *          the height of simulated TFT screen
   */
  private void drawCoordinates(Graphics2D g2d, int w, int h) {
    int x, y, dx, dy, dw, dh;
    int minorW = gridModel.getGridMinorWidth();
    int minorH = gridModel.getGridMinorHeight();
    int majorW = gridModel.getGridMajorWidth();
    int majorH = gridModel.getGridMajorHeight();
    Color minorCol = gridModel.getGridMinorColor();
    Color majorCol = gridModel.getGridMajorColor();
    if (minorW > 0 && majorW > 0 && minorH > 0 && majorW > 0) {
      // draw X axis
      dy = 0;
      dh = h;
      for (x=0; x<w; x+=minorW) {
        if (x%majorW == 0) {
          g2d.setColor(majorCol);
        } else {
          g2d.setColor(minorCol);
        }
        dx = x;
        g2d.drawLine(dx, dy, dx, dh);
      }
      // draw Y axis  
      dx = 0;
      dw = w;
      for (y=0; y<h; y+=minorH) {
        if (y%majorH==0) {
          g2d.setColor(majorCol);
        } else {
          g2d.setColor(minorCol);
        }
        dy = y;
        g2d.drawLine(dx, dy, dw, dy);
      }
      return;
    }
    if ((minorW == 0 || minorH == 0) && (majorH > 0 && majorW > 0)) {
      // draw X axis
      dy = 0;
      dh = h;
      g2d.setColor(majorCol);
      for (x=0; x<w; x+=majorW) {
        dx = x;
        g2d.drawLine(dx, dy, dx, dh);
      }
      // draw Y axis  
      dx = 0;
      dw = w;
      for (y=0; y<h; y+=majorH) {
        dy = y;
        g2d.drawLine(dx, dy, dw, dy);
      }
      return;
    } 
    if ((majorW == 0 || majorH == 0) && (minorW > 0 && minorH > 0)) {
      // draw X axis
      dy = 0;
      dh = h;
      g2d.setColor(minorCol);
      for (x=0; x<w; x+=minorW) {
        dx = x;
        g2d.drawLine(dx, dy, dx, dh);
      }
      // draw Y axis  
      dx = 0;
      dw = w;
      for (y=0; y<h; y+=minorH) {
        dy = y;
        g2d.drawLine(dx, dy, dw, dy);
      }
      return;
    } 
  }
  
  /**
   * sets the page model.
   *
   * @return the <code>PageModel</code> object
   */
  public void setModel(PageModel model) {
    this.model = model;
  }

  /**
   * Gets the page model.
   *
   * @return the <code>PageModel</code> object
   */
  public PageModel getModel() {
    return model;
  }

  /**
   * Gets the key for this page.
   *
   * @return the key
   */
  public String getKey() {
    return model.getKey();
  }
  
  /**
   * Gets this page's GUIslice enum.
   *
   * @return the enum
   */
  public String getEnum() {
    return model.getEnum();
  }
  
  /**
   * Gets the count of widgets on this page.
   *
   * @return the widget count
   */
  public int getWidgetCount() {
    return widgets.size();  
  }
  
  /**
   * Find the named widget in list.
   *
   * @param widgetKey
   *          the widget key
   * @return the <code>widget</code> object
   */
  public Widget findWidget(String widgetKey) {
    for (Widget w : widgets) {
      if (w.getModel().getKey().equals(widgetKey)) {
        return w;
      }
    }
    return null;
  }

  /**
   * selectWidget is a central point for selecting any widget it allows the
   * keeping of accurate selection counts for enabling or disabling Ribbon icons.
   *
   * @param w
   *          the w
   */
  public void selectWidget(Widget w) {
    if (w.isSelected()) return;
    w.select();
    doSelectedCount(w);
    ribbon.setEditButtons(selectedGroupCnt);
  }
  
  /**
   * unSelectWidget is a central point for selecting any widget it allows the
   * keeping of accurate selection counts for enabling or disabling Ribbon icons.
   * Also, for keeping views in sync.
   * @param w
   *          the w
   */
  public void unSelectWidget(Widget w) {
    if (!w.isSelected()) return;
    w.unSelect();
    doSelectedCount(w);
    ribbon.setEditButtons(selectedGroupCnt);
  }
  
  /**
   * Count all the selected widgets.
   *
   * @param w the widget
   */
  public void doSelectedCount(Widget w) {
    WidgetModel m = w.getModel();
    if (w.isSelected()) {
      if ((m.getType().equals(EnumFactory.RADIOBUTTON))  ||
          (m.getType().equals(EnumFactory.IMAGEBUTTON) && m.isToggle()) ||
          (m.getType().equals(EnumFactory.TOGGLEBUTTON))) {
            selectedGroupCnt++;
      }
      selectedCnt++;
    } else {
      if ((m.getType().equals(EnumFactory.RADIOBUTTON))  ||
          (m.getType().equals(EnumFactory.IMAGEBUTTON) && m.isToggle()) ||
          (m.getType().equals(EnumFactory.TOGGLEBUTTON))) {
        if (selectedGroupCnt > 0) 
          selectedGroupCnt--;
      }
      if (selectedCnt > 0) selectedCnt--;
    }
    if (selectedCnt == 0) {
      MsgBoard.sendEvent(getKey(),MsgEvent.OBJECT_UNSELECT_PAGEPANE,"",getKey());
    }
  }

  /**
   * Select no widgets.
   */
  public void selectNone() {
    for (Widget w : widgets) {
      w.unSelect();
    }
    selectedCnt=0;
    selectedGroupCnt=0;
    if (dragArrowsCommand != null) {
      execute(dragArrowsCommand);
      dragArrowsCommand = null;
    }
  }

  /**
   * Select the named widget in list.
   *
   * @param widgetKey
   *          the widget key
   * @return the <code>widget</code> object
   */
  public Widget selectName(String widgetKey) {
    for (Widget w : widgets) {
      if (w.getModel().getKey().equals(widgetKey)) {
        selectWidget(w);
      }
    }
    return null;
  }

  /**
   * Adds the widget to this page and to the tree view of widgets.
   *
   * @param w
   *          the widget
   */
  public void addWidget(Widget w) {
    widgets.add(w);
    selectNone();
    selectWidget(w);
    PropManager.getInstance().addPropEditor(w.getModel());
    TreeView.getInstance().addWidget(getKey(), getEnum(), w.getKey(), w.getEnum());
    requestFocus();
    repaint();
  }

  /**
   * Adds the widget to this page and to the tree view of widgets.
   * This function is called by the PasteCommand object.
   *
   * @param m
   *          the widget model
   */
  public void addWidget(WidgetModel m, int x, int y) {
    Widget w = WidgetFactory.getInstance().createWidget(m.getType(), -1, -1);
/*   Bug 138 copy and paste of numeric input fields causes
 *   duplicate m_pElemVal entries.  The problem is that the following 
 *   statement doesn't take into account properties that are specific
 *   to the type of model or specal functions like calcSizes()  
 *   'w.getModel().pasteProps(m);' The fix is to reverse
 *   who gets called with copyProperties() so we can overload 
 *   the function in sub classes that require extra code.
 */
    WidgetModel.pasteProps(m, w.getModel(), x, y);
    w.select();
    widgets.add(w);
    PropManager.getInstance().addPropEditor(w.getModel());
    TreeView.getInstance().addWidget(getKey(), getEnum(), w.getKey(), w.getEnum());
  }

  /**
   * Delete widget.
   *
   * @param w
   *          the widget
   */
  public void delWidget(Widget w) {
    Iterator<?> itr = widgets.iterator();
    while (itr.hasNext())
    {
        Widget x = (Widget)itr.next();
        if (x.getKey().equals(w.getKey())) {
            itr.remove();
            break;
        }
    }
    TreeView.getInstance().delWidget(getKey(), w.getKey());
    repaint();
     MsgBoard.sendEvent(getKey(),MsgEvent.WIDGET_DELETE, w.getKey(), getKey());
  }

  /**
   * Delete widget.
   * This function is called by the CutCommand object.
   *
   * @param m
   *          the widget model
   */
  public void delWidget(WidgetModel m) {
    Iterator<?> itr = widgets.iterator();
    while (itr.hasNext())
    {
        Widget x = (Widget)itr.next();
        if (x.getKey().equals(m.getKey())) {
            itr.remove();
            break;
        }
    }
    TreeView.getInstance().delWidget(getKey(), m.getKey());
    repaint();
    MsgBoard.sendEvent(getKey(),MsgEvent.WIDGET_DELETE, m.getKey(), getKey());
  }

  /**
   * Gets the full list of widgets.
   *
   * @return the widgets
   */
  public List<Widget> getWidgets() {
    return widgets;
  }
  
  /**
   * Collect all the selected widgets into a list.
   *
   * @return the selected list
   */
  public List<Widget> getSelectedList() {
    List<Widget> selected = new ArrayList<Widget>();
    for (Widget w : widgets) {
      if (w.isSelected()) {
        selected.add(w);
      }
    }
    return selected;
  }

  /**
   * Select each widget in rectangle.
   * Used to implement the rubber band selection.
   *
   * @param r
   *          the rectangle
   */
  public void selectRect(Rectangle r) {
    Widget first = null;
    for (Widget w : widgets) {
      if (donotSelectKey != null && w.getKey().equals(donotSelectKey)) {
        continue;
      }
      if (r.contains(w.getLocation())) {
        if (first == null) first = w;
        selectWidget(w);
      } else {
        unSelectWidget(w);
      }
    }
    if (first != null) { // send off a new selected message
      MsgBoard.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE, 
          first.getKey(), getKey());
    }
  }

  /**
   * Find a single node that is inside point p. We only want the highest Z-Order
   * widget, otherwise we will drag around a panel instead of the button on top
   * that the user really wanted to move.
   *
   * @param p
   *          the <code>Point</code> object
   * @return the matching <code>Widget</code> object or null.
   */
  public Widget findOne(Point p) {
    // we may need to deal with scaled points because of zoom feature
    Point2D pos = p;
    if (zoomFactor > 1) {
      Point2D.Double scaledPos = new Point2D.Double();
      scaledPos.x = (double) p.x;
      scaledPos.y = (double) p.y;
      // transforms are only needed in zoom mode
      inv_at.transform(scaledPos, scaledPos);
      pos = scaledPos;
    }

    // last element is considered the topmost
    ListIterator<Widget> iterator = widgets.listIterator(widgets.size());
    while (iterator.hasPrevious()) {
      Widget w = iterator.previous();
      if (w.contains(pos)) {
        return w;
      }
    }

    return null;
  }
  
  static public Point mapPoint(int x, int y) {
    // we may need to deal with scaled points because of zoom feature
    if (zoomFactor > 1) {
      Point2D.Double scaledPos = new Point2D.Double();
      scaledPos.x = (double)x;
      scaledPos.y = (double)y;
      // transforms are only needed in zoom mode
      inv_at.transform(scaledPos, scaledPos);
//    System.out.println("map: Z=" + zoomFactor  + 
//        " [" + x + "," + y + "] "  + " s=["  +
//        scaledPos.x + "," + scaledPos.y + "]");
      return new Point((int)scaledPos.x, (int)scaledPos.y);
    } else {
      return new Point(x,y);
    }
  }
  
  /**
   * Change Z order.
   *
   * @param widgetKey
   *          the widget key
   * @param fromIdx
   *          the from idx
   * @param toIdx
   *          the to idx
   */
  public void changeZOrder(String widgetKey, int fromIdx, int toIdx) {
    Widget w = widgets.get(fromIdx);
    widgets.remove(fromIdx);
    widgets.add(toIdx, w);
    MsgEvent ev = new MsgEvent();
    ev.message = w.getModel().getKey();
    ev.xdata = getKey();
    ev.code = MsgEvent.OBJECT_SELECTED_PAGEPANE;
    MsgBoard.publish(ev,getKey());
    repaint();
  }

  /**
   * Refresh view.
   */
  public void refreshView() {
    ribbon.setEditButtons(selectedGroupCnt); // needed on page changes
    updateContentSize();
    repaint();
  }
  
  /**
   * The Class MouseHandler.
   */
  private class MouseHandler extends MouseAdapter {

    /**
     * mouseClicked.
     *
     * @param e
     *          the e
     * @see MouseAdapter#mouseClicked(MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
      mousePt = e.getPoint();
      Widget w = findOne(mousePt);
      int button = e.getButton();
      if (button != MouseEvent.BUTTON1) {
//    System.out.println("button != MouseEvent.BUTTON1");
        return;
      }
      if (e.isShiftDown() && e.isControlDown()) {
        return;
      }
      if (e.isControlDown()) {
        // Single Left-click + Ctrl means to toggle select under cursor
        if (w != null) {
          if (w.isSelected()) {
            unSelectWidget(w);
            /* after a multi-selection and a toggle off
             * we might be left with treeview and propview
             * showing a widget thats no longer selected.
             * So just pick a new one if that's the case.  
             */
            List<Widget> list = getSelectedList();
            if (list.size() > 0) {
              Widget selected = list.get(0);
              MsgBoard.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE, 
                  selected.getModel().getKey(),
                  getKey());
            }
          } else {
            selectWidget(w);
            MsgBoard.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE, 
                w.getModel().getKey(),
                getKey());
          }
          e.getComponent().repaint();
        }
        return;
      }
      if (e.isShiftDown()) {
        // Single Left-click + Shift means to select under cursor
        if (w != null) {
          selectWidget(w);
          MsgBoard.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE,
              w.getModel().getKey(),
              getKey());
          e.getComponent().repaint();
        } 
        return;
      } 
      
      // Single Left-click - deselect all widgets then select widget under cursor
      selectNone();
      if (w == null) {
        ribbon.setEditButtons(selectedGroupCnt);
        MsgBoard.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE,
            getKey(), "");
      } else {
//        System.out.println("selectWidget: " + w.getEnum());
        selectWidget(w);
        MsgBoard.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE,
            w.getModel().getKey(), getKey());
        e.getComponent().repaint();
      }
    }  // end mouseClicked

    /**
     * mouseReleased.
     *
     * @param e
     *          the e
     * @see MouseAdapter#mouseReleased(MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
      mouseRect.setBounds(0, 0, 0, 0);
      if (dragCommand != null) {
        dragCommand.stop(e.isControlDown());
        execute(dragCommand);
        dragCommand = null;
      }
      if (resizeCommand != null) {
        resizeCommand.stop();
        execute(resizeCommand);
        resizeCommand = null;
      }      
      bMultiSelectionBox = false;
      currentAction = CurrentAction.NONE;
      setCursor(Cursor.getDefaultCursor());
      e.getComponent().repaint();
    }  // end mouseReleased

    /**
     * mousePressed.
     *
     * @param e
     *          the e
     * @see MouseAdapter#mousePressed(MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
      mousePt = e.getPoint();
      if (currentAction == CurrentAction.DRAGGING_WIDGET) {
        currentAction = CurrentAction.NONE;
      }
      Widget w = findOne(mousePt);
      if (currentAction == CurrentAction.RECTANGULAR_SELECTION) {
        bMultiSelectionBox = true;
        donotSelectKey = null;
        if (w != null) {
          donotSelectKey = w.getKey();
        }
      } else if (w != null) {
        Point unscaledPoint = PagePane.mapPoint(e.getPoint().x, e.getPoint().y);
        HandleType handleType = w.getActionHandle(w.toWidgetSpace(unscaledPoint));
        switch (handleType) {
          case DRAG:
            if (w.isSelected()) {
              currentAction = CurrentAction.DRAGGING_WIDGET;
            }
            dragPt = new Point(mousePt.x, mousePt.y);
            break;
          case NONE:
            break;
          default:
            if (widgetUnderCursor != null && widgetUnderCursor.isSelected()) {
              resizeCommand = new ResizeCommand(instance, widgetUnderCursor, handleType);
              resizeCommand.start(unscaledPoint);
              currentAction = CurrentAction.RESIZING_WIDGET;
            }
            break;
        }
      }      
    } // end mousePressed

    /*
     * Handle zooming on mouse wheel.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      if (e.isControlDown()) {
        int distance = e.getWheelRotation();
        if (distance < 0) {
          zoomIn();
        } else {
          zoomOut();
        }
        refreshView();
      }
    }
  } // end MouseHandler

  /**
   * The Class MouseMotionHandler.
   */
  private class MouseMotionHandler extends MouseMotionAdapter {

    @Override
    public void mouseMoved(MouseEvent e) {
      if (bMultiSelectionBox || currentAction != CurrentAction.NONE) {
        return;
      }

      widgetUnderCursor = findOne(e.getPoint());
      if (widgetUnderCursor == null) {
        setCursor(Cursor.getDefaultCursor());
        return;
      }
      if (!widgetUnderCursor.isSelected()) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return;
      }

      Point unscaledPoint = PagePane.mapPoint(e.getPoint().x, e.getPoint().y); 
      HandleType handleType = widgetUnderCursor.getActionHandle(widgetUnderCursor.toWidgetSpace(unscaledPoint));
      switch (handleType) {
        case DRAG:
          setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
          break;
        case TOP_LEFT:
        case TOP_LEFT_PROPORTIONAL:
          setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
          break;
        case TOP:
          setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
          break;
        case TOP_RIGHT:
        case TOP_RIGHT_PROPORTIONAL:
          setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
          break;
        case RIGHT:
          setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
          break;
        case BOTTOM_RIGHT:
        case BOTTOM_RIGHT_PROPORTIONAL:
          setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
          break;
        case BOTTOM:
          setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
          break;
        case BOTTOM_LEFT:
        case BOTTOM_LEFT_PROPORTIONAL:
          setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
          break;
        case LEFT:
          setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
          break;
        default:
          setCursor(Cursor.getDefaultCursor());
          break;
      }
    }
     
    /**
     * mouseDragged.
     *
     * @param e
     *          the e
     * @see MouseMotionAdapter#mouseDragged(MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
      if (bMultiSelectionBox) {
        // Here I'm working out the size and position of my rubber band
        mouseRect.setBounds(
            Math.min(mousePt.x, e.getX()),
            Math.min(mousePt.y, e.getY()),
            Math.abs(mousePt.x - e.getX()),
            Math.abs(mousePt.y - e.getY()));
        // Now select any widgets that fit inside our rubber band
        selectRect(mouseRect);
      } else if (currentAction == CurrentAction.DRAGGING_WIDGET) {
       if (dragCommand == null) {
          dragCommand = new DragWidgetCommand(instance);
          if (!dragCommand.start(dragPt)) {
            currentAction = CurrentAction.NONE;
            bMultiSelectionBox = false;
            dragCommand = null;
            repaint();
            return;
          }
        }
        // No need to adjust our points using u.fromWinPoint() 
        // because here we are calculating offsets not absolute points.
        dragCommand.move(e.getPoint());
      } else if (currentAction == CurrentAction.RESIZING_WIDGET) {
        if (resizeCommand == null) {
          System.out.println("resizeCommand is null");
        } else {
          Point unscaledPoint = PagePane.mapPoint(e.getPoint().x, e.getPoint().y);
          resizeCommand.move(unscaledPoint, e.isAltDown(), e.isControlDown());
        }
      }
      repaint();
    } // end mouseDragged
  }  // end MouseMotionHandler
  
  /**
   * getPreferredSize.
   *
   * @return the preferred size
   * @see JComponent#getPreferredSize()
   */
  @Override
  public Dimension getPreferredSize() {
    Dimension scaledSize = new Dimension(
      (int) (pm.getWidth() * zoomFactor), 
      (int) (pm.getHeight() * zoomFactor)
    );    
    return scaledSize;
  }

  private void updateContentSize() {
    Dimension scaledSize = new Dimension(
      (int) (pm.getWidth() * zoomFactor), 
      (int) (pm.getHeight() * zoomFactor)
    );
    setSize(scaledSize);
  }

  /**
   * Execute.
   *
   * @param c
   *          the <code>Command</code> object
   * @see Command#execute()
   */
  public void execute(Command c) {
    c.execute();
    History.getInstance().push(c);
  }

  public void objectSelectedTreeView(String widgetKey) {
    selectNone();
    Widget w = findWidget(widgetKey);
    if (w != null) {
      selectWidget(w);
    }
    repaint();
  }
  /**
   * updateEvent provides the implementation of Observer Pattern. It monitors
   * selection of widgets in the tree view, modification of widgets by commands,
   * and size changes to the simulated TFT screen.
   *
   * @param e
   *          the e
   * @see iSubscriber#updateEvent(MsgEvent)
   */
  @Override
  public void updateEvent(MsgEvent e) {
//  System.out.println("PagePane: " + e.toString());
    if (e.code == MsgEvent.WIDGET_REPAINT) {
      Builder.logger.debug("PagePane: " + e.toString());
      Widget w = findWidget(e.message);
      if (w != null) {
        repaint();
      }
    } else if (e.code == MsgEvent.OBJECT_SELECTED_TREEVIEW && 
               e.xdata.equals(getKey())) {
      Builder.logger.debug("PagePane: " + e.toString());
      selectNone();
      Widget w = findWidget(e.message);
      if (w != null) {
        selectWidget(w);
        repaint();
      }
    } else if (e.code == MsgEvent.OBJECT_UNSELECT_TREEVIEW) {
      Builder.logger.debug("PagePane: " + e.toString());
      selectNone();
      repaint();
    } else if (e.code == MsgEvent.CANVAS_MODEL_CHANGE) {
      repaint();
    }
  }

  /**
   * Backup all widgets into a serialized string object.
   *
   * @return the <code>String</code> object
   */
  public String backup() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
      out.writeInt(widgets.size());
//      System.out.println("widgets: " + widgets.size());
      for (Widget w : widgets) {
        w.writeObject(out);
      }
      out.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Restore all widgets from a serialized string object.
   *
   * @param state
   *          the state backup string
   */
  public void restore(String state, boolean bUndo) {
    try {
      byte[] data = Base64.getDecoder().decode(state);
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
      if (!bUndo) {
        TreeView.getInstance().addPage(getKey(), getEnum());
      }
      widgets = new ArrayList<Widget>();
      int cnt = in.readInt();  // size of array list
//      System.out.println("widgets: " + cnt);
      Widget w = null;
      String widgetType = null;
      for (int i=0; i<cnt; i++) {
        widgetType = (String)in.readObject();
        w = WidgetFactory.getInstance().createWidget(widgetType,0,0);
        try {
          w.readObject(in, widgetType);
          widgets.add(w);
        } catch(Exception e) {
          Builder.logger.error(e.toString());
          JOptionPane.showMessageDialog(null, 
              e, 
              "ERROR",
              JOptionPane.ERROR_MESSAGE);
        }
        // without this check we duplicate elemnts on tree and prop views
        if (!bUndo) {
          PropManager.getInstance().addPropEditor(w.getModel());
          TreeView.getInstance().addWidget(getKey(), getEnum(),
              w.getKey(), w.getEnum());
        }
      }
      in.close();
      selectedCnt = 0;
      selectedGroupCnt = 0;
      repaint();
    } catch (ClassNotFoundException e) {
      System.out.println("ClassNotFoundException occurred.");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IOException occurred." + e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }

  /**
   * Gets the type of Page.
   *
   * @return the Page Type
   */
  public String getPageType() {
    return model.getType();
  }

  /**
   * Sets the type of Page.
   *
   * @param the pageType
   */
  public void setPageType(String pageType) {
    model.setType(pageType);
    MsgBoard.subscribe(this, model.getKey());
    if (pageType.equals(EnumFactory.PROJECT)  ||
        pageType.equals(EnumFactory.BASEPAGE) ||
        pageType.equals(EnumFactory.POPUP)) {
      bPaintBaseWidgets=false;
    } else {
      bPaintBaseWidgets=true;
    }
  }
  
  /**
   * scale
   * scale each element acccording to the supplied ratios
   * this will change each element's width, height, x, and y positions.
   * Fonts will be ignored since there is no font scaling within GUIslice API
   * so exact or even close matches may no be possible.  Users may need to
   * manually change fonts. Although, generally fonts will simply work without
   * any changes.
   * 
   * @param ratioX
   * @param ratioY
   */
  public void scale(double ratioX, double ratioY) {
    int newX, newY, newHeight, newWidth;
    FontFactory ff = FontFactory.getInstance();
    HashMap<String, String> fontMap = new HashMap<String, String>();
    for (Widget w : widgets) {
      WidgetModel m = w.getModel();
      newX = (int)(((double)m.getX() * ratioX) + 0.5);
      newY = (int)(((double)m.getY() * ratioY) + 0.5);
      newWidth = (int)(((double)m.getWidth() * ratioX) + 0.5);
      newHeight = (int)(((double)m.getHeight() * ratioY) + 0.5);
      m.setX(newX);
      m.setY(newY);
      if (m instanceof LineModel) {
        /* LineModel.getWidth() actually returns length of line
         * while LineModel.setWidth() changes the length
         * use ratioY for vertical lines ratioX horizontal lines
         */
        if (((LineModel)m).isVertical()) {
          m.setWidth((int)(((double)m.getWidth() * ratioY) + 0.5));
        } else {
          m.setWidth((int)(((double)m.getWidth() * ratioX) + 0.5));
        }
      } else if (!(m instanceof SpinnerModel)){
        m.setWidth(newWidth);
        m.setHeight(newHeight);
      }
      String key = m.getFontDisplayName();
      if (key != null) {
        /* see if we can scale the font
         * since scanning fonts takes a while and generally we use very
         * few different fonts I build a map of matching fonts as I scan
         * to avoid a brute force search each time.
         */
        String scaledFontName = null;
        if (fontMap.containsKey(key)) {
          scaledFontName = fontMap.get(key);
          m.setFont(scaledFontName);
        } else {
          if (ratioY < 1.0) {
            // smaller font available?
            scaledFontName = ff.getSmallerFont(key, ratioY);
          } else {
            // larger font available?
            scaledFontName = ff.getLargerFont(key, ratioY);
          }
          if (scaledFontName != null) {
            fontMap.put(key, scaledFontName);
            m.setFont(scaledFontName);
          }
        } // end !fontMap.containsKey(key)
      } // end key != null
    } // end for (Widget w)
    Builder.postStatusMsg("Scale operation completed!");
  }
}
