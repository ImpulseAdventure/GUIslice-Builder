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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import builder.common.Utils;
import builder.models.LineModel;

/**
 * The Class LineWidget simulates GUIslice API gslc_DrawLine() call.
 * 
 * @author Paul Conti
 * 
 */
public class LineWidget extends Widget {
  
  LineModel m;
  
  /**
   * Instantiates a new line widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public LineWidget(int x, int y) {
    u = Utils.getInstance();
    m = new LineModel();
    model = m;
    super.setXY(model, x, y);
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g2d) {
    g2d.setColor(m.getFillColor());
    if (m.isVertical()) {
      g2d.drawLine(m.getX(), m.getY(), m.getX(), m.getY() + m.getLength());
    } else {
      g2d.drawLine(m.getX(), m.getY(), m.getX() + m.getLength(), m.getY());
    }
    Rectangle b = getWinBounded();
    drawSelRect(g2d, b);
  }

  /**
   * Gets the win bounded.
   * Custom version to placeline in middle of rectangle
   * @return the window bounded <code>Rectangle</code> object
   */
  @Override
  public Rectangle getWinBounded() {
    Rectangle b = new Rectangle();
    b.x = m.getX();
    b.y = m.getY();
    if (m.isVertical()) {
      b.width = 1;
      b.height = m.getLength();
    } else {
      b.width = m.getLength();
      b.height = 1;
    }
    return b;
  }
  
  /**
   * Draw sel rect.
   *
   * @param g2d
   *          the graphics object
   * @param b
   *          the bounded <code>Rectangle</code> object
   */
  @Override
  public void drawSelRect(Graphics2D g2d, Rectangle b) {
    if (!bSelected) {
      return;
    }

    g2d.setColor(Color.RED);

    if (m.isVertical()) {
      // top center
      g2d.fillRect(b.x - (RESIZE_HANDLE_SIZE / 2), b.y - (RESIZE_HANDLE_SIZE / 2), RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
      // bottom center
      g2d.fillRect(b.x - (RESIZE_HANDLE_SIZE / 2), b.y + b.height - (RESIZE_HANDLE_SIZE / 2), RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    } else {
      // left top corner
      g2d.fillRect(b.x - (RESIZE_HANDLE_SIZE / 2), b.y - (RESIZE_HANDLE_SIZE / 2), RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
      // right top corner
      g2d.fillRect(b.x + b.width - (RESIZE_HANDLE_SIZE / 2), b.y - (RESIZE_HANDLE_SIZE / 2), RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    } 
  }

  /**
   * @Inherited
   */
  @Override
  public HandleType getActionHandle(Point point) {
    double width = model.getWidth();
    double height = model.getHeight();
    
    if (m.isVertical()) {
      if (point.getY() < RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.TOP;
      } else if (point.getY() > height - RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.BOTTOM;
      }
    } else {
      if (point.getX() < RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.LEFT;
      } else if (point.getX() > width - RESIZE_HANDLE_ACTIVE_SIZE) {
        return HandleType.RIGHT; 
      }
    }

    return HandleType.DRAG;
  }  
  
  /**
   * Return true if this node contains p.
   *
   * @param p
   *          the <code>Point</code> object
   * @return <code>true</code>, if successful
   */
  @Override
  public boolean contains(Point p) {
    Rectangle b = getWinBounded();
    if (m.isVertical()) {
      b.grow(RESIZE_HANDLE_SIZE, 2);
    } else {
      b.grow(2, RESIZE_HANDLE_SIZE);
    }
    return b.contains(p);
  }

  /**
   * Return true if this node contains p.
   *
   * @param p
   *          the <code>Point</code> object
   * @return <code>true</code>, if successful
   */
  @Override
  public boolean contains(Point2D p) {
    Rectangle r = getWinBounded();
    Rectangle2D.Double b = new Rectangle2D.Double();
    boolean isVertical = m.isVertical();
    b.x = r.x - (RESIZE_HANDLE_SIZE / 2);
    b.y = r.y - (RESIZE_HANDLE_SIZE / 2);
    b.width = RESIZE_HANDLE_SIZE + (isVertical ? 0 : r.width);
    b.height = RESIZE_HANDLE_SIZE + (isVertical ? r.height : 0);
    return b.contains(p);
  }
}
