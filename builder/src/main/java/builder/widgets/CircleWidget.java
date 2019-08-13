/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import builder.common.CommonUtils;
import builder.models.CircleModel;

/**
 * The Class BoxWidget simulates GUIslice API gslc_DrawFillCircle() 
 * and gslc_DrawFrameCircle() calls.
 * 
 * @author Paul Conti
 * 
 */
public class CircleWidget extends Widget {
  
  CircleModel m;
  
  /**
   * Instantiates a new box widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public CircleWidget(int x, int y) {
    u = CommonUtils.getInstance();
    m = new CircleModel();
    model = m;
    Point p = CommonUtils.getInstance().fitToGrid(x, y, model.getWidth(), model.getHeight());
    p = CommonUtils.getInstance().snapToGrid(p.x, p.y);
    model.setX(p.x);
    model.setY(p.y);
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g2d) {
    Rectangle b = getWinBounded();
    if (m.isFillEnabled()) {
      g2d.setColor(m.getFillColor());
      g2d.fillOval(m.getX(), m.getY(), 2*m.getRadius(), 2*m.getRadius());
    }
    if (m.isFrameEnabled()) {
      g2d.setColor(m.getFrameColor());
      g2d.drawOval(m.getX(), m.getY(), 2*m.getRadius(), 2*m.getRadius());
    }
    super.drawSelRect(g2d, b);
  }

}
