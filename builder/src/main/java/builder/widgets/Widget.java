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
package builder.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import builder.common.Utils;
import builder.controller.Controller;
import builder.fonts.FontFactory;
import builder.models.WidgetModel;

/**
 * The Class Widget is the base class for simulating GUIslice API calls.
 * 
 * @author Paul Conti
 * 
 */
public class Widget {
  
  /** The Constant dashed. */
  final static public  BasicStroke dashed = new BasicStroke(3.0f, 
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5.0f, new float[]{5.0f}, 0);
  
  /** The Constant dotted. */
  final static public  BasicStroke dotted = new BasicStroke(1.0f, 
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {1.0f,2.0f}, 0);

  /** The u. */
  Utils u;
  
  /** The ff. */
  FontFactory ff;
  
  /** The model. */
  WidgetModel model;
  
  /** The b selected. */
  boolean bSelected = false;
  
  public Widget() {
    u = Utils.getInstance();
  }

  /**
   * setUserPrefs from PrefsEditor stored model
   *
   * @param peModel
   *          the new user prefs
   */
  public void setUserPrefs(WidgetModel peModel) {
     // merge user preference into our model
     // avoid x,y
    model.TurnOffEvents();
    int rows = model.getRowCount();
    for (int r=WidgetModel.PROP_Y+1; r<rows; r++) {
      // skip font so we can set default according to platform
      if (peModel.getMetaId(r).equals("TXT-200")) continue;
      Object o = peModel.getValueAt(r, 1);
      model.changeValueAt(o, r);
    }
    model.TurnOnEvents();
  }

  /**
   * Gets the model.
   *
   * @return the model
   */
  public WidgetModel getModel() {
    return model;
  }
  
  public int getX() {
    return model.getX();
  }
  
  public int getY() {
    return model.getY();
  }
  
  public void setXY(WidgetModel m, int x, int y) {
    // if x and y are set to -1 we will position widget later (paste operation)
    if (x >= 0 && y >= 0) {
      Point p = Utils.getInstance().fitToGrid(x, y, m.getWidth(), m.getHeight());
      p = Utils.getInstance().snapToGrid(p.x, p.y);
      m.setX(p.x);
      m.setY(p.y);
    }
  }
  
  /**
   * Gets the key.
   *
   * @return the key
   */
  public String getKey() {
    return model.getKey();
  }
  
  /**
   * Gets the enum.
   *
   * @return the enum
   */
  public String getEnum() {
    return model.getEnum();
  }
  
  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return model.getType();
  }
  
  /**
   * Use Flash API.
   *
   * @return <code>true</code>, if flash is to be used
   */
  public boolean useFlash() {
    return model.useFlash();
  }
  
  /**
   * Return this widget's location.
   *
   * @return the location <code>Point</code> object
   */
  public Point getLocation() {
    return new Point(model.getX(), model.getY());
  }
  
  /**
   * Gets the win bounded.
   *
   * @return the window bounded <code>Rectangle</code> object
   */
  public Rectangle getWinBounded() {
    int dx = model.getX();
    int dy = model.getY();
    Rectangle b = new Rectangle();
    b.x = dx;
    b.y = dy;
    b.width = model.getWidth();
    b.height = model.getHeight();
    return b;
  }
  
  /**
   * testLocation() will determine if our new location will fit our screen
   *
   * @param x
   *          the test destination point x
   * @param y
   *          the test destination point y
   * @param tft_width
   *          the width of our target tft display
   * @param tft_height
   *          the height of our target tft display
   * @return true if we still fit on the display, false otherwise
   */
  public boolean testLocation(int x, int y, int tft_width, int tft_height) {
    int w = model.getWidth();
    int h = model.getHeight();
    // Do the new Coordinates fit on our Display?
    if ((x + w) > tft_width)
      return false;
    if ((y + h) > tft_height)
      return false;
    if (x < 0 || y < 0)
      return false;
    return true;
  }
  
  /**
   * updateLocation() will set our location
   * ignore margins.
   *
   * @param pt
   *          the new location point
   * @return the <code>point</code> object
   */
  public void updateLocation(Point pt) {
    model.setX(pt.x);
    model.setY(pt.y);
  }
  
  /**
   * updateLocation() will set our location
   * ignore margins.
   *
   * @param x
   *          the new location x point
   * @param y
   *          the new location y point
   * @return the <code>point</code> object
   */
  public void updateLocation(int x, int y) {
    model.setX(x);
    model.setY(y);
  }
  
  /**
   * Move by.
   *
   * @param d
   *          the destination point
   */
  public void moveBy(Point d) {
    model.setValueAt(Integer.valueOf(d.x), WidgetModel.PROP_X, WidgetModel.COLUMN_VALUE);
    model.setValueAt(Integer.valueOf(d.y), WidgetModel.PROP_Y, WidgetModel.COLUMN_VALUE);
    Controller.sendRepaint();
  }

  /**
   * drop() will adjust our drop point to make sure it snaps to our grid
   * if thats turned on.
   * @param d is our widget drop point on the screen.
   * @return a possibly different point that fits on our screen.
   */
  public Point drop(Point d) {
    d = u.snapToGrid(d.x, d.y);
    return d;
  }

  /**
   * Return true if this node contains p.
   *
   * @param p
   *          the <code>Point</code> object
   * @return <code>true</code>, if successful
   */
  public boolean contains(Point p) {
    Rectangle b = getWinBounded();
    b.grow(1,1);
    return b.contains(p);
  }

  /**
   * Convert a point from page space to widget space.
   * 
   * @param p page space <code>Point</code> object
   * @return the widget space <code>Point</code> object
   */
  public Point toWidgetSpace(Point p) {
    return new Point(p.x - model.getX(), p.y - model.getY());
  }

  /**
   * Return true if this node contains p.
   *
   * @param p
   *          the <code>Point</code> object
   * @return <code>true</code>, if successful
   */
  public boolean contains(Point2D p) {
    Rectangle2D.Double b = new Rectangle2D.Double();
    b.x = model.getX()-1;
    b.y = model.getY()-1;
    b.width = model.getWidth()+2;
    b.height = model.getHeight()+2;
    return b.contains(p);
  }

  /**
   * Return true if this node is selected.
   *
   * @return true, if is selected
   */
  public boolean isSelected() {
    return bSelected;
  }

  /**
   * Mark this node as selected.
   */
  public void select() {
    this.bSelected = true;
  }

  /**
   * Mark this node as not selected.
   */
  public void unSelect() {
    this.bSelected = false;
  }

  /**
   * Draw.
   *
   * @param g2d
   *          the graphics object
   */
  public void draw(Graphics2D g2d) {
    
  }

  public enum HandleType {
    NONE, DRAG, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT,
    TOP_RIGHT_PROPORTIONAL, BOTTOM_LEFT_PROPORTIONAL, TOP_LEFT_PROPORTIONAL, BOTTOM_RIGHT_PROPORTIONAL
  }
  static final int RESIZE_HANDLE_SIZE = 5;
  static final int RESIZE_HANDLE_ACTIVE_SIZE = RESIZE_HANDLE_SIZE + 2;
  
  /**
   * Draw resizing handles for selected widget.
   *
   * @param g2d
   *          the graphics object
   * @param b
   *          the bounded <code>Rectangle</code> object
   */
  public void drawSelRect(Graphics2D g2d, Rectangle b) {
    if (!bSelected) {
      return;
    }
    
    g2d.setColor(Color.RED);

    // @TODO: optimize calculations
    // double xMax = b.x + b.width + 2 - HANDLE_SIZE;
    // double yMax = b.y + b.height + 2 - HANDLE_SIZE;

    // left top corner
    g2d.fillRect(b.x - 2, b.y-2, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    // right top corner
    g2d.fillRect(b.x+b.width+2-RESIZE_HANDLE_SIZE, b.y-2, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    // left bottom corner
    g2d.fillRect(b.x - 2, b.y+b.height+2 - RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    // right bottom corner
    g2d.fillRect(b.x+b.width+2-RESIZE_HANDLE_SIZE, b.y+b.height+2 - RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    // top center
    g2d.fillRect(b.x + (b.width / 2) - (RESIZE_HANDLE_SIZE / 2), b.y-2, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    // bottom center
    g2d.fillRect(b.x + (b.width / 2) - (RESIZE_HANDLE_SIZE / 2), b.y + b.height + 2 - RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    // left center
    g2d.fillRect(b.x - 2, b.y + (b.height / 2) - (RESIZE_HANDLE_SIZE / 2), RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    // right center
    g2d.fillRect(b.x + b.width + 2 - RESIZE_HANDLE_SIZE, b.y + (b.height / 2) - (RESIZE_HANDLE_SIZE / 2), RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
  }

  /**
   * Gets the resizing/dragging handler type.
   *
   * @param point
   *          point in widget space
   * @return the resizing handler type
   */
  public HandleType getActionHandle(Point point) {
    double width = model.getWidth();
    double xCenter = width / 2;
    double height = model.getHeight();
    double yCenter = height / 2;
    
    if (point.getX() < RESIZE_HANDLE_ACTIVE_SIZE) {
      if (point.getY() < RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.TOP_LEFT;
      } else if (point.getY() > height - RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.BOTTOM_LEFT;
      } else if (point.getY() > yCenter - (RESIZE_HANDLE_ACTIVE_SIZE / 2) && point.getY() < yCenter + (RESIZE_HANDLE_ACTIVE_SIZE / 2)) {
        return HandleType.LEFT;
      }
    } else if (point.getX() > width - RESIZE_HANDLE_ACTIVE_SIZE) {
      if (point.getY() < RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.TOP_RIGHT;
      } else if (point.getY() > height - RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.BOTTOM_RIGHT;
      } else if (point.getY() > yCenter - (RESIZE_HANDLE_ACTIVE_SIZE / 2) && point.getY() < yCenter + (RESIZE_HANDLE_ACTIVE_SIZE / 2)) {
        return HandleType.RIGHT;
      }
    } else if (point.getX() > xCenter - (RESIZE_HANDLE_ACTIVE_SIZE / 2) && point.getX() < xCenter + (RESIZE_HANDLE_ACTIVE_SIZE / 2)) {
      if (point.getY() < RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.TOP;
      } else if (point.getY() > height - RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.BOTTOM;
      }
    }

    return HandleType.DRAG;
  }  
  
  /**
   * Write object serializes this object
   *
   * @param out
   *          the out
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void writeObject(ObjectOutputStream out) 
      throws IOException {
//    System.out.println("W writeObject(): " + getType());
    out.writeObject(model.getType());
    out.writeBoolean(bSelected);
    model.writeModel(out);
  }

  /**
   * Read object deserializes this object
   *
   * @param in
   *          the in
   * @param widgetType
   *          the widget type
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  public void readObject(ObjectInputStream in, String widgetType) 
      throws IOException, ClassNotFoundException {
//    System.out.println("W readObject(): " + widgetType);
    bSelected = in.readBoolean();
    model.readModel(in, widgetType);
  }
}
