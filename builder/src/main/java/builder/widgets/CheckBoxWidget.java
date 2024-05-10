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
import java.awt.Point;

import builder.common.GUIslice;
import builder.models.CheckBoxModel;

/**
 * <p>
 * The Class CheckBoxWidget simulates GUIslice API gslc_ElemXCheckboxCreate() call
 * with the GSLCX_CHECKBOX_STYLE_X.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class CheckBoxWidget extends Widget {
  
  CheckBoxModel m;
  
  /**
   * Instantiates a new check box widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public CheckBoxWidget(int x, int y) {
//    u = CommonUtils.getInstance();
    m = new CheckBoxModel();
    model = m;
    super.setXY(model, x, y);
//    setUserPrefs(CheckBoxEditor.getInstance().getModel());
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D pGui) {
    Rectangle rElem = getWinBounded();
    Rectangle rInner;

    boolean bChecked   = m.isChecked();
    String nStyle      = m.getStyle();
    Color colCheck     = m.getMarkColor();
    Color colElemFrame = m.getFrameColor();
    Color colElemFill  = m.getFillColor();
    
    // Draw the background
    GUIslice.drawFillRect(pGui,rElem,colElemFill);

    // Generic coordinate calcs
    int nX0,nY0,nX1,nY1,nMidX,nMidY;
    nX0 = rElem.x;
    nY0 = rElem.y;
    nX1 = rElem.x + rElem.width - 1;
    nY1 = rElem.y + rElem.height - 1;
    nMidX = (nX0+nX1)/2;
    nMidY = (nY0+nY1)/2;
    if (nStyle.equals(CheckBoxModel.CHECKBOX_STYLE_BOX)) {
      // Draw the center indicator if checked
      rInner = GUIslice.expandRect(rElem,-5,-5);
      if (bChecked) {
        // If checked, fill in the inner region
        GUIslice.drawFillRect(pGui,rInner,colCheck);
      } else {
        // Assume the background fill has already been done so
        // we don't need to do anything more in the unchecked case
      }
      // Draw a frame around the checkbox
      GUIslice.drawFrameRect(pGui,rElem,colElemFrame);

    } else if (nStyle.equals(CheckBoxModel.CHECKBOX_STYLE_X)) {
      // Draw an X through center if checked
      if (bChecked) {
        GUIslice.drawLine(pGui,nX0,nY0,nX1,nY1,colCheck);
        GUIslice.drawLine(pGui,nX0,nY1,nX1,nY0,colCheck);
      }
      // Draw a frame around the checkbox
      GUIslice.drawFrameRect(pGui,rElem,colElemFrame);

    } else if (nStyle.equals(CheckBoxModel.CHECKBOX_STYLE_ROUND)) {
      // Draw inner circle if checked
      if (bChecked) {
        GUIslice.drawFillCircle(pGui,nMidX,nMidY,5,colCheck);
      }
      // Draw a frame around the checkbox
      GUIslice.drawFrameCircle(pGui,nMidX,nMidY,(rElem.width/2),
          m.getFrameColor());

    }

    super.drawSelRect(pGui, rElem);
  }

  @Override
  public HandleType getActionHandle(Point point) {
    HandleType handleType = super.getActionHandle(point);

    switch (handleType) {
      case BOTTOM_LEFT:
        return HandleType.BOTTOM_LEFT_PROPORTIONAL;
      case BOTTOM_RIGHT:
        return HandleType.BOTTOM_RIGHT_PROPORTIONAL;
      case TOP_LEFT:
        return HandleType.TOP_LEFT_PROPORTIONAL;
      case TOP_RIGHT:
        return HandleType.TOP_RIGHT_PROPORTIONAL;
      default:
        return handleType;
    }
  }  
}
