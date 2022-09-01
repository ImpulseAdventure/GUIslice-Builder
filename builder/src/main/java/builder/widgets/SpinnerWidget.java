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
import java.awt.Rectangle;

import builder.common.Utils;
import builder.fonts.FontFactory;
import builder.fonts.FontTFT;
import builder.models.SpinnerModel;

/**
 * The Class SpinnerWidget simulates GUIslice API gslc_ElemXSpinnerCreate() call.
 * 
 * @author Paul Conti
 * 
 */
public class SpinnerWidget extends Widget {
  
  /** The text model. */
  SpinnerModel m = null;
  
  /**
   * Instantiates a new text widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public SpinnerWidget(int x, int y) {
    u = Utils.getInstance();
    ff = FontFactory.getInstance();
    m = new SpinnerModel();
    model = m;
    super.setXY(model, x, y);
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g2d) {
    FontTFT font = ff.getFont(m.getFontDisplayName());
    Rectangle b = super.getWinBounded();
    int buttonsz = m.getButtonSize();
    g2d.setColor(m.getFillColor());
//    g2d.fillRect(b.x+1, b.y-1, b.width-2, b.height-2);
    g2d.fillRect(b.x, b.y, b.width, b.height);
    if (font != null) {
      g2d.setColor(m.getFrameColor());
      g2d.drawRect(b.x, b.y, b.width, b.height);
      g2d.setColor(m.getTextColor());
      Rectangle t = new Rectangle();
      t.width = m.getTextWidth()+5;
      t.height = b.height-2;
      t.x = b.x + 1;
      t.y = b.y;
      ff.drawText(g2d, FontTFT.ALIGN_RIGHT, t, "0", font, m.getTextColor(), m.getFillColor(), 0);

      int dxDown, dxUp;
      dxDown = b.x + b.width - buttonsz - 1;  // down arrow
      g2d.setColor(m.getButtonColor());
      g2d.fillRect(dxDown, b.y+1, buttonsz, b.height-2);
      dxUp = dxDown - buttonsz - 2;
      g2d.fillRect(dxUp, b.y+1, buttonsz, b.height-2);
  
      g2d.setColor(m.getButtonTextColor());
      t.x = dxUp;
      t.width = buttonsz; 
      // up arrow
      ff.drawChar(g2d, FontTFT.ALIGN_CENTER, t, m.getIncrementChar(), font, m.getTextColor(), m.getTextColor(),0);
      t.x = dxDown;
      // down arrow
      ff.drawChar(g2d, FontTFT.ALIGN_CENTER, t, m.getDecrementChar(), font, m.getTextColor(), m.getTextColor(), 0);
    } else {
      g2d.setColor(Color.RED);
      g2d.drawRect(b.x, b.y, b.width, b.height);
    }
    super.drawSelRect(g2d, b);
  }

}
