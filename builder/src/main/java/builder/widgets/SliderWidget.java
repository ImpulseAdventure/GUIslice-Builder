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

import builder.Builder;
import builder.common.Utils;
import builder.common.GUIslice;
import builder.models.SliderModel;

/**
 * The Class SliderWidget simulates GUIslice API gslc_ElemXSliderCreate() call.
 * 
 * @author Paul Conti
 * 
 */
public class SliderWidget extends Widget {
  
  SliderModel m;
  
  /**
   * Instantiates a new slider widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public SliderWidget(int x, int y) {
    u = Utils.getInstance();
    m = new SliderModel();
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

    boolean     bGlow     = isSelected();
    int         nPos      = m.getCurValue();
    int         nPosMin   = m.getMin();
    int         nPosMax   = m.getMax();
    boolean     bVert     = m.isVertical();
    int         nThumbSz  = m.getThumbSize();
    boolean     bTrim     = m.isTrimStyle();
    Color       colTrim   = m.getTrimColor();
    int         nTickDiv  = m.getDivisions();
    int         nTickLen  = m.getTickSize();
    Color       colTick   = m.getTickColor();
    Color       colElemFrame    = m.getFrameColor();
    Color       colElemFill     = m.getFillColor();
    Color       colElemFillGlow = m.getSelectedColor();
    
    // Range check on nPos
    if (nPos < nPosMin) { nPos = nPosMin; }
    if (nPos > nPosMax) { nPos = nPosMax; }

    int nX0,nY0,nX1,nY1,nXMid,nYMid;
    nX0 = rElem.x;
    nY0 = rElem.y;
    nX1 = rElem.x + rElem.width - 1;
    nY1 = rElem.y + rElem.height - 1;
    nXMid = (nX0+nX1)/2;
    nYMid = (nY0+nY1)/2;

    // Scale the current position
    int nPosRng = nPosMax-nPosMin;
    // TODO: Check for nPosRng=0, reversed min/max
    int nPosOffset = nPos-nPosMin;

    // Provide some margin so thumb doesn't exceed control bounds
    int nMargin   = nThumbSz;
    int nCtrlRng;
    if (!bVert) {
      nCtrlRng = (nX1-nMargin)-(nX0+nMargin);
    } else {
      nCtrlRng = (nY1-nMargin)-(nY0+nMargin);
    }
    if (nPosRng == 0) {
      nPosRng=10;
      Builder.postStatusMsg("WARNING: Slider " + m.getEnum() + " max should be > min");
    }
    int nCtrlPos  = (nPosOffset*nCtrlRng/nPosRng)+nMargin;


    /* Draw the background
     * - TODO: To reduce flicker on unbuffered displays, one could consider
     *         redrawing only the thumb (last drawn position) with fill and
     *         then redraw other portions. This would prevent the
     *         track / ticks from flickering needlessly. A full redraw would
     *         be required if it was first draw action.
     */
    GUIslice.drawFillRect(pGui,rElem,(bGlow)?colElemFillGlow:colElemFill);

    // Draw any ticks
    // - Need at least one tick segment
    if (nTickDiv>=1) {
      int  nTickInd;
      int   nTickOffset;
      for (nTickInd=0;nTickInd<=nTickDiv;nTickInd++) {
        nTickOffset = nTickInd * nCtrlRng / nTickDiv;
        if (!bVert) {
          GUIslice.drawLine(pGui,nX0+nMargin+ nTickOffset,nYMid,
                  nX0+nMargin + nTickOffset,nYMid+nTickLen,colTick);
        } else {
          GUIslice.drawLine(pGui,nXMid,nY0+nMargin+ nTickOffset,
                  nXMid+nTickLen,nY0+nMargin + nTickOffset,colTick);
        }
      }
    }


    // Draw the track
    if (!bVert) {
      // Make the track highlight during glow
      GUIslice.drawLine(pGui,nX0+nMargin,nYMid,nX1-nMargin,nYMid, colElemFrame);
      // Optionally draw a trim line
      if (bTrim) {
        GUIslice.drawLine(pGui,nX0+nMargin,nYMid+1,nX1-nMargin,nYMid+1,colTrim);
      }

    } else {
      // Make the track highlight during glow
      GUIslice.drawLine(pGui,nXMid,nY0+nMargin,nXMid,nY1-nMargin,colElemFrame);
      // Optionally draw a trim line
      if (bTrim) {
        GUIslice.drawLine(pGui,nXMid+1,nY0+nMargin,nXMid+1,nY1-nMargin,colTrim);
      }
    }


    int        nCtrlX0,nCtrlY0;
    Rectangle  rThumb = new Rectangle();
    if (!bVert) {
      nCtrlX0   = nX0+nCtrlPos-nThumbSz;
      nCtrlY0   = nYMid-nThumbSz;
    } else {
      nCtrlX0   = nXMid-nThumbSz;
      nCtrlY0   = nY0+nCtrlPos-nThumbSz;
    }
    rThumb.x      = nCtrlX0;
    rThumb.y      = nCtrlY0;
    rThumb.width  = 2*nThumbSz;
    rThumb.height = 2*nThumbSz;

    // Draw the thumb control
    GUIslice.drawFillRect(pGui,rThumb,(bGlow)?colElemFillGlow:colElemFill);
    GUIslice.drawFrameRect(pGui,rThumb,colElemFrame);
    if (bTrim) {
      Rectangle rThumbTrim = GUIslice.expandRect(rThumb,-1,-1);
      GUIslice.drawFrameRect(pGui,rThumbTrim,colTrim);
    }

    super.drawSelRect(pGui, rElem);
  }

}
