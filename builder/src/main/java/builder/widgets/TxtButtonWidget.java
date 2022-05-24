/**
 *
 * The MIT License
 *
 * Copyright 2018-2022 Paul Conti
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
import builder.common.GUIslice;
import builder.fonts.FontFactory;
import builder.fonts.FontTFT;
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
    u = Utils.getInstance();
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
    FontTFT font = null;
    Rectangle b = getWinBounded();
    Color colBg = m.getFillColor();
    if (bSelected) {
      colBg = m.getSelectedColor();
    }
    g2d.setColor(colBg);
    if (m.isFillEnabled()) {
      if (m.isRoundedEn())
        g2d.fillRoundRect(b.x, b.y, b.width, b.height, 15, 15);
      else
        g2d.fillRect(b.x, b.y, b.width, b.height);
    }

    String text = m.getText();
    if (text.isEmpty()) {
      text = "TODO";
    }
    font = ff.getFont(m.getFontDisplayName());
    if (font != null) {
      if (m.isFrameEnabled()) {
        g2d.setColor(m.getFrameColor());
        if (m.isRoundedEn()) {
          g2d.drawRoundRect(b.x, b.y, b.width, b.height, 15, 15);
        } else {
          g2d.drawRect(b.x, b.y, b.width, b.height);
        }
      }
      if (m.isFillEnabled()) {
        if (m.isFrameEnabled()) {
          Rectangle rElemInner = GUIslice.expandRect(b, -1, -1);
          ff.drawText(g2d, m.getAlignment(), rElemInner, text, font, m.getTextColor(), colBg, m.getTextMargin());
        } else {
          ff.drawText(g2d, m.getAlignment(), b, text, font, m.getTextColor(), colBg, m.getTextMargin());
        }
      } else {
        ff.drawText(g2d, m.getAlignment(), b, text, font, m.getTextColor(), m.getTextColor(), m.getTextMargin());
      }
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
