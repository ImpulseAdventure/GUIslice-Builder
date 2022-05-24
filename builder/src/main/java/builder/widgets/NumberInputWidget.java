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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import builder.common.Utils;
import builder.fonts.FontFactory;
import builder.fonts.FontTFT;
import builder.models.NumberInputModel;

/**
 * The Class NumberInputWidget simulates GUIslice API gslc_ElemCreateTxt() call
 * when setup for text input using a virtual keyboard.
 * 
 * @author Paul Conti
 * 
 */
public class NumberInputWidget extends Widget {

  NumberInputModel m = null;

  /**
   * Instantiates a new box widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public NumberInputWidget(int x, int y) {
    u = Utils.getInstance();
    ff = FontFactory.getInstance();
    m = new NumberInputModel();
    model = m;
    super.setXY(model, x, y);
//    setUserPrefs(NumberInputEditor.getInstance().getModel());
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
      g2d.fillRect(b.x, b.y, b.width, b.height);
    }
    g2d.setColor(m.getFrameColor());
    g2d.drawRect(b.x, b.y, b.width, b.height);
    FontTFT font = ff.getFont(m.getFontDisplayName());
    if (font != null) {
      g2d.setColor(m.getFrameColor());
      g2d.drawRect(b.x, b.y, b.width, b.height);
      String text = m.getText();
      if (text.isEmpty()) {
        for (int i=0; i<m.getTextStorage(); i++) {
          text = text + "9";
          Dimension d = ff.getTextBounds(b.x,b.y,font, text);
          if (d.width > b.width) {
             text = text.substring(0, text.length() - 1);
             break;
          }
        }
      }
      if (m.isFillEnabled()) {
        ff.drawText(g2d, m.getAlignment(), b, text, font, m.getTextColor(), m.getFillColor(), m.getTextMargin());
      } else {
        ff.drawText(g2d, m.getAlignment(), b, text, font, m.getTextColor(), m.getTextColor(), m.getTextMargin());
      }
    } else {
      g2d.setColor(Color.RED);
      g2d.drawRect(b.x, b.y, b.width, b.height);
    }
    super.drawSelRect(g2d, b);
  }

}
