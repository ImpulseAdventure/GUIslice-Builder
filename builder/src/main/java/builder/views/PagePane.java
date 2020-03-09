/**
 *
 * The MIT License
 *
 * Copyright 2018-2020 Paul Conti
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import builder.Builder;
import builder.commands.Command;
import builder.commands.DragWidgetCommand;
import builder.commands.History;
import builder.common.EnumFactory;
import builder.controller.PropManager;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.events.iSubscriber;
import builder.models.GeneralModel;
import builder.models.GridModel;
import builder.models.PageModel;
import builder.models.WidgetModel;
import builder.prefs.GeneralEditor;
import builder.prefs.GridEditor;
import builder.widgets.Widget;
import builder.widgets.WidgetFactory;

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
  
  /** our cross hair cursor */
  Cursor crossHairCursor;

  /** The rectangular selection enabled switch */
  private boolean bRectangularSelectionEn = false;
  
  /** The selecting using a rubber band. */
  private boolean bMultiSelectionBox = false;
  
  /** The donotSelectKey */
  private String donotSelectKey = null;
  
  /** The dragging indicator. */
  private boolean bDragging = false;

  /** The general model. */
  private GeneralModel generalModel = null;
  
  /** The grid model. */
  private GridModel gridModel;
  
  /** The model. */
  private PageModel model=null;
  
  /** The instance. */
  private PagePane instance = null;
  
  /** The Ribbon. */
  private static Ribbon ribbon = null;
  
  /** The drag command. */
  public  DragWidgetCommand dragCommand = null;

  /** the number of selected widgets. */
  private int selectedCnt = 0;
  
  /**
   * the number of selected widgets that are either radio buttons or checkboxes.
   */
  private int selectedGroupCnt = 0;
  
  /** The zoom factor. */
  private static double zoomFactor = 1;
  
  /** The zoom AffineTransform at. */
  private static AffineTransform at = null;
  
  /** The inverse AffineTransform at. */
  public static AffineTransform inv_at;
  
  /** The canvas width */
  int canvasWidth;
  
  /** The canvas height */
  int canvasHeight;
  
  MsgBoard msg = null;
  
  /**
   * Instantiates a new page pane.
   */
  public PagePane() {
    instance = this;
    ribbon = Ribbon.getInstance();
    generalModel = (GeneralModel) GeneralEditor.getInstance().getModel();
    gridModel = (GridModel) GridEditor.getInstance().getModel();
    msg = MsgBoard.getInstance();
    model = new PageModel();
    mousePt = new Point(generalModel.getWidth() / 2, generalModel.getHeight() / 2);
    dragPt = mousePt;
    canvasWidth = Builder.CANVAS_WIDTH;
    canvasHeight = Builder.CANVAS_HEIGHT;
    crossHairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
    this.addMouseListener(new MouseHandler());
    this.addMouseMotionListener(new MouseMotionHandler());
    this.setLocation(0, 0);
    this.setOpaque(true);
    this.setFocusable( true ); 
    this.setBorder(BorderFactory.createLineBorder(Color.black));
    this.setVisible(true);
    if (at == null)
      ZoomTransform();
  }

  /**
   * paintComponent.
   *
   * @param g
   *          the g
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.transform(at);
    int width = generalModel.getWidth();
    int height = generalModel.getHeight();
    if (generalModel.useBackgroundImage() && !gridModel.getGrid()) {
      g2d.drawImage(generalModel.getImage(), 0, 0, null);
    } else {
      if (gridModel.getGrid()) {
        g2d.setColor(gridModel.getBackGroundColor());
        g2d.fillRect(0, 0, width, height);
        drawCoordinates(g2d, width, height);
      } else {
        g2d.setColor(generalModel.getBackgroundColor());
        g2d.fillRect(0,  0, width, height);
      }
    }
    // Now set to overwrite
    g2d.setComposite(AlphaComposite.SrcOver);
    for (Widget w : widgets) {
      w.draw(g2d);
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
   * create a transform
   */
  public static void ZoomTransform() {
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
    ZoomTransform();
    ribbon.btn_zoom_out.setEnabled(true);
    ribbon.mini_zoom_out.setEnabled(true);
  }
  
  /**
   * Zoom out.
   */
  public static void zoomOut() {
    zoomFactor /= 1.1;
    if (zoomFactor < 1.1) {
      ribbon.btn_zoom_out.setEnabled(false);
      ribbon.mini_zoom_out.setEnabled(false);
    }
    ZoomTransform();
  }

  /**
   * rectangularSelection.
   */
  public void rectangularSelection() {
    bRectangularSelectionEn = true;
    setCursor(crossHairCursor);
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
  }
  
  /**
   * Gets the page model.
   *
   * @return the <code>PageModel</code> object
   */
  public WidgetModel getModel() {
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
    ribbon.setEditButtons(selectedCnt, selectedGroupCnt);
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
    ribbon.setEditButtons(selectedCnt, selectedGroupCnt);
  }
  
  /**
   * Count all the selected widgets.
   *
   * @param w the widget
   */
  public void doSelectedCount(Widget w) {
    if (w.isSelected()) {
      if (w.getType().equals(EnumFactory.RADIOBUTTON) ||
          w.getType().equals(EnumFactory.CHECKBOX)) {
            selectedGroupCnt++;
      }
      selectedCnt++;
    } else {
      if (w.getType().equals(EnumFactory.RADIOBUTTON) ||
          w.getType().equals(EnumFactory.CHECKBOX)) {
            if (selectedGroupCnt > 0) selectedGroupCnt--;
      }
      if (selectedCnt > 0) selectedCnt--;
    }
    if (selectedCnt == 0) {
      msg.sendEvent(getKey(),MsgEvent.OBJECT_UNSELECT_PAGEPANE,"",getKey());
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
 *   'w.getModel().copyProperties(m);' The fix is to reverse
 *   who gets called with copyProperties() so we can overload 
 *   the function in sub classes that require extra code.
 */
    w.getModel().copyProperties(m, x, y);
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
     msg.sendEvent(getKey(),MsgEvent.WIDGET_DELETE, w.getKey(), getKey());
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
    msg.sendEvent(getKey(),MsgEvent.WIDGET_DELETE, m.getKey(), getKey());
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
      msg.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE, 
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
    if (zoomFactor > 1) {
      Point2D.Double scaledPos = new Point2D.Double();
      scaledPos.x = (double)p.x;
      scaledPos.y = (double)p.y;
      // transforms are only needed in zoom mode
      inv_at.transform(scaledPos, scaledPos);
//    System.out.println("findOne: Z=" + zoomFactor  + " p=[" + p.x + "," + p.y + "] " 
//        + " s=[" + scaledPos.getX() + "," + scaledPos.getY() + "]");
      Widget w = null;
      for (int i=widgets.size()-1; i>=0; i--) {
        w = widgets.get(i);
        //for (Widget w : widgets) {
        if (w.contains(scaledPos)) {
//  System.out.println("found: " + w.getKey() + " p= " + p + w.getBounded());
          return w;
        }
      }
    } else {
      Widget w = null;
      for (int i=widgets.size()-1; i>=0; i--) {
        w = widgets.get(i);
        if (w.contains(p)) {
//  System.out.println("found: " + w.getKey() + " p= " + p + w.getBounded());
          return w;
        }
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
  msg.publish(ev, getKey());
  repaint();
}

  /**
   * Refresh view.
   */
  public void refreshView() {
    ribbon.setEditButtons(selectedCnt, selectedGroupCnt); // needed on page changes
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
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
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
              msg.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE, 
                  selected.getModel().getKey(),
                  getKey());
            }
          } else {
            selectWidget(w);
            msg.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE, 
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
          msg.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE,
              w.getModel().getKey(),
              getKey());
          e.getComponent().repaint();
        } 
        return;
      } 
      // Single Left-click - deselect all widgets then select widget under cursor
      selectNone();
      if (w == null) {
        ribbon.setEditButtons(selectedCnt, selectedGroupCnt);
        msg.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE,
            getKey(), "");
      } else {
//        System.out.println("selectWidget: " + w.getEnum());
        selectWidget(w);
        msg.sendEvent(getKey(),MsgEvent.OBJECT_SELECTED_PAGEPANE,
            w.getModel().getKey(), getKey());
        e.getComponent().repaint();
      }
    }  // end mouseClicked

    /**
     * mouseReleased.
     *
     * @param e
     *          the e
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
      mouseRect.setBounds(0, 0, 0, 0);
      if (dragCommand != null) {
        dragCommand.stop();
        execute(dragCommand);
        dragCommand = null;
      }
      bMultiSelectionBox = false;
      bRectangularSelectionEn = false;
      bDragging = false;
      setCursor(Cursor.getDefaultCursor());
      e.getComponent().repaint();
    }  // end mouseReleased

    /**
     * mousePressed.
     *
     * @param e
     *          the e
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
      mousePt = e.getPoint();
      bDragging = false;
      Widget w = findOne(mousePt);
      if (bRectangularSelectionEn) {
        bMultiSelectionBox = true;
        donotSelectKey = null;
        if (w != null) {
          donotSelectKey = w.getKey();
        }
      } else if (w != null) {
        if (w.isSelected()) bDragging = true;
        dragPt = new Point(mousePt.x, mousePt.y);
      }
    } // end mousePressed
  } // end MouseHandler

  /**
   * The Class MouseMotionHandler.
   */
  private class MouseMotionHandler extends MouseMotionAdapter {
     
     /**
      * mouseDragged.
      *
      * @param e
      *          the e
      * @see java.awt.event.MouseMotionAdapter#mouseDragged(java.awt.event.MouseEvent)
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
     } else if (bDragging ){
       if (dragCommand == null) {
          dragCommand = new DragWidgetCommand(instance);
          if (!dragCommand.start(dragPt)) {
            bDragging = false;
            bMultiSelectionBox = false;
            bRectangularSelectionEn = false;
            dragCommand = null;
            repaint();
            return;
          }
        }
        // No need to adjust our points using u.fromWinPoint() 
        // because here we are calculating offsets not absolute points.
        dragCommand.move(e.getPoint());
      }
      repaint();
    } // end mouseDragged
  }  // end MouseMotionHandler
  
  /**
   * getPreferredSize.
   *
   * @return the preferred size
   * @see javax.swing.JComponent#getPreferredSize()
   */
  @Override
  public Dimension getPreferredSize() {
//    return new Dimension(gridModel.getWidth(), gridModel.getHeight());
    return new Dimension(1200, 1200);
  }

  /**
   * Execute.
   *
   * @param c
   *          the <code>Command</code> object
   * @see builder.commands.Command#execute()
   */
  public void execute(Command c) {
    History.getInstance().push(c);
    c.execute();
  }

  /**
   * updateEvent provides the implementation of Observer Pattern. It monitors
   * selection of widgets in the tree view, modification of widgets by commands,
   * and size changes to the simulated TFT screen.
   *
   * @param e
   *          the e
   * @see builder.events.iSubscriber#updateEvent(builder.events.MsgEvent)
   */
  @Override
  public void updateEvent(MsgEvent e) {
//    System.out.println("PagePane: " + e.toString());
    if (e.code == MsgEvent.WIDGET_REPAINT) {
//   System.out.println("PagePane: " + e.toString());
      Widget w = findWidget(e.message);
      if (w != null) {
        repaint();
      }
    } else if (e.code == MsgEvent.OBJECT_SELECTED_TREEVIEW && 
               e.xdata.equals(getKey())) {
//  System.out.println("PagePane: " + e.toString());
      selectNone();
      Widget w = findWidget(e.message);
      if (w != null) {
        selectWidget(w);
        repaint();
      }
    } else if (e.code == MsgEvent.OBJECT_UNSELECT_TREEVIEW) {
//  System.out.println("PagePane: " + e.toString());
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
        w.readObject(in, widgetType);
        widgets.add(w);
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
    msg.subscribe(this, model.getKey());
  }
}

