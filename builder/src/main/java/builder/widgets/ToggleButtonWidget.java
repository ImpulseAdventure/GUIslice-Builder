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

import java.awt.Graphics2D;
import java.awt.Rectangle;

import builder.common.GUIslice;
import builder.models.ToggleButtonModel;

/**
 * <p>
 * The Class ToggleButtonWidget simulates GUIslice API gslc_ElemXTogglebtnCreate() call
 * with the GSLCX_CHECKBOX_STYLE_ROUND.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class ToggleButtonWidget extends Widget {
  
  private ToggleButtonModel m;
  
  /**
   * Instantiates a new radio button widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public ToggleButtonWidget(int x, int y) {
    m = new ToggleButtonModel();
    model = m;
    super.setXY(model, x, y);
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D pGui) {
    Rectangle rElem = getWinBounded();
    if (m.isCircular()) {
      // Work out the sizes of the inner rectangles 
      Rectangle rInner = GUIslice.expandRect(rElem,-1,-1);

      // work out our circle positions
      int  nRadius  = rInner.height / 2;
      int  nLeftX   = rInner.x + nRadius;
      int  nLeftY   = rInner.y + nRadius;
      int  nRightX  = rInner.x + rElem.width - nRadius -1;
      int  nRightY  = rInner.y + nRadius;
        
      if (m.isChecked()) {
        // draw our main body
        GUIslice.drawFillRoundRect(pGui,rInner,rInner.height,m.getOnColor());
        // place thumb on left-hand side
        GUIslice.drawFillCircle(pGui,nLeftX, nLeftY, nRadius-1,m.getThumbColor());
        if (m.isFrameEnabled()) {
          GUIslice.drawFrameRoundRect(pGui, rElem, rElem.height,m.getFrameColor());
          GUIslice.drawFrameCircle(pGui, nLeftX, nLeftY, nRadius, m.getFrameColor());
        }
      } else {
        // draw our main body
        GUIslice.drawFillRoundRect(pGui,rInner, rInner.height,m.getOffColor());
        // place thumb on right-hand side
        GUIslice.drawFillCircle(pGui,nRightX, nRightY, nRadius-1,m.getThumbColor());
        if (m.isFrameEnabled()) {
          GUIslice.drawFrameRoundRect(pGui, rElem, rElem.height,m.getFrameColor());
          GUIslice.drawFrameCircle(pGui, nRightX, nRightY, nRadius, m.getFrameColor());
        }
      }
    } else { // not circular
      // Work out the sizes of the inner rectangles 
      Rectangle rSquare = new Rectangle();
      rSquare.x      = rElem.x;
      rSquare.y      = rElem.y;
      rSquare.width  = rElem.height;
      rSquare.height = rElem.height;
      Rectangle rInner = GUIslice.expandRect(rElem,-1,-1);

      if (m.isChecked()) {
        GUIslice.drawFillRect(pGui,rInner,m.getOnColor());
        // place thumb on left-hand side
        GUIslice.drawFillRect(pGui,rSquare,m.getThumbColor());
        if (m.isFrameEnabled()) {
          GUIslice.drawFrameRect(pGui,rElem,m.getFrameColor());
          GUIslice.drawFrameRect(pGui,rSquare,m.getFrameColor());
        }
      } else {
        GUIslice.drawFillRect(pGui,rInner,m.getOffColor());
        // place thumb on right-hand side
        rSquare.x = rInner.x + rInner.width - rInner.height - 1;
        GUIslice.drawFillRect(pGui,rSquare,m.getThumbColor());
        if (m.isFrameEnabled()) {
          GUIslice.drawFrameRect(pGui,rElem,m.getFrameColor());
          GUIslice.drawFrameRect(pGui,rSquare,m.getFrameColor());
        }
      }
    }
    
    super.drawSelRect(pGui, rElem);
  }

}
