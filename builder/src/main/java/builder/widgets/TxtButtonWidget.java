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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import builder.common.CommonUtils;
import builder.common.FontFactory;
import builder.models.TxtButtonModel;
import builder.prefs.TxtButtonEditor;

/**
 * The Class TxtButtonWidget simulates GUIslice API gslc_ElemCreateTxt() call.
 * 
 * @author Paul Conti
 * 
 */
public class TxtButtonWidget extends Widget {
  
  /** The TxtButton Model. */
  TxtButtonModel m = null;
  
  /**
   * Instantiates a new txt button widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public TxtButtonWidget(int x, int y) {
    u = CommonUtils.getInstance();
    ff = FontFactory.getInstance();
    m = new TxtButtonModel();
    model = m;
    super.setXY(model, x, y);
    setUserPrefs(TxtButtonEditor.getInstance().getModel());
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g2d) {
    Font font = null;
    Rectangle b = getWinBounded();
    if (bSelected) {
      g2d.setColor(m.getSelectedColor());
      if (m.isRoundedEn())
        g2d.fillRoundRect(b.x, b.y, b.width, b.height, 15, 15);
      else
        g2d.fillRect(b.x, b.y, b.width, b.height);
    } else {
      g2d.setColor(m.getFillColor());
      if (m.isRoundedEn())
        g2d.fillRoundRect(b.x, b.y, b.width, b.height, 15, 15);
      else
        g2d.fillRect(b.x, b.y, b.width, b.height);
    }
    String text = m.getText();
    if (text.isEmpty()) {
      text = "TODO";
      font = ff.getStyledFont(m.getFontDisplayName(), "BOLD+ITALIC");
    } else {
      font = ff.getFont(m.getFontDisplayName());
    }
    if (font != null) {
      if (m.isFrameEnabled()) {
        g2d.setColor(m.getFrameColor());
        if (m.isRoundedEn()) {
          g2d.drawRoundRect(b.x, b.y, b.width, b.height, 15, 15);
        } else {
          g2d.drawRect(b.x, b.y, b.width, b.height);
        }
      }
      g2d.setColor(m.getTextColor());
      ff.alignString(g2d, m.getAlignment(), b, text, font);
    } else {
      g2d.setColor(Color.RED);
      if (m.isRoundedEn()) {
        g2d.drawRoundRect(b.x, b.y, b.width, b.height, 15, 15);
      } else {
        g2d.drawRect(b.x, b.y, b.width, b.height);
      }
    }
    super.drawSelRect(g2d, b);
  }
 
}
