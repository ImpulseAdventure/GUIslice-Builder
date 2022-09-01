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
import java.awt.Stroke;
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
      g2d.drawLine(m.getX(), m.getY(), m.getX(), m.getY()+m.getWidth());
    } else {
      g2d.drawLine(m.getX(), m.getY(), m.getX()+m.getWidth(), m.getY());
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
    if (m.isVertical()) {
      b.x = m.getX() - 2;
      b.y = m.getY() - 2;
      b.width = model.getHeight();
      b.height = model.getWidth()+4;
    } else {
      b.x = m.getX() - 2;
      b.y = m.getY() - 2;
      b.width = m.getWidth()+4;
      b.height = m.getHeight();
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
    if (bSelected) {
      Stroke defaultStroke = g2d.getStroke();
      g2d.setColor(Color.RED);
      g2d.setStroke(Widget.dashed);
      g2d.drawRect(b.x-2, b.y-2, b.width+4, b.height+4);
      g2d.setStroke(defaultStroke);  
    }
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
    b.x = r.x;
    b.y = r.y;
    b.width = r.width;
    b.height = r.height;
    return b.contains(p);
  }


}
