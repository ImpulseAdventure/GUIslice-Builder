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
import builder.common.GUIslice;
import builder.models.SeekbarModel;

/**
 * The Class SleekbarWidget simulates GUIslice API gslc_ElemXSeekbarCreate() call.
 * 
 * @author Paul Conti
 * 
 */
public class SeekbarWidget extends Widget {
  
  SeekbarModel m;
  
  /**
   * Instantiates a new slider widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public SeekbarWidget(int x, int y) {
    u = Utils.getInstance();
    m = new SeekbarModel();
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
    int       nPosMin     = m.getMin();
    int       nPosMax     = m.getMax();
    int       nProgressW  = m.getProgressWidth();
    int       nRemainW    = m.getRemainWidth();
    int       nThumbSz    = m.getThumbSize();
    boolean   bVert       = m.isVertical();
    boolean   bTrimThumb  = m.isThumbTrim();
    boolean   bFrameThumb = m.isThumbFrame();
    int       nTickDiv    = m.getDivisions();
    int       nTickLen    = m.getTickSize();
    Color     colProgress = m.getProgressColor();
    Color     colRemain   = m.getRemainColor();
    Color     colThumb    = m.getThumbColor();
    Color     colFrame    = m.getFrameColor();
    Color     colTrim     = m.getThumbTrimColor();
    Color     colTick     = m.getTickColor();

    int       nPos        = m.getCurValue();
    if (nPos == 0) nPos = 30; // just to make control look pretty

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
    int nCtrlPos  = (nPosOffset*nCtrlRng/nPosRng)+nMargin;

    int       nCtrlX0,nCtrlY0;
    if (!bVert) {
      nCtrlX0   = nX0+nCtrlPos-nThumbSz;
      nCtrlY0   = nYMid-nThumbSz;
    } else {
      nCtrlX0   = nXMid-nThumbSz;
      nCtrlY0   = nY0+nCtrlPos-nThumbSz;
    }
    Rectangle   rThumb = new Rectangle();
    rThumb.x  = nCtrlX0;
    rThumb.y  = nCtrlY0;
    rThumb.width  = 2*nThumbSz;
    rThumb.height  = 2*nThumbSz;


    // Draw the thumb control
    // work out size of thumb circle
    int  nLeftX   = rThumb.x+nThumbSz;
    int  nLeftY   = rThumb.y + nThumbSz;
    
    // Draw the background
    // - TODO: To reduce flicker on unbuffered displays, one could consider
    //         redrawing only the thumb (last drawn position) with fill and
    //         then redraw other portions. This would prevent the
    //         track / ticks from flickering needlessly. A full redraw would
    //         be required if it was first draw action.
    GUIslice.drawFillRect(pGui,rElem,m.getFillColor());

    // Draw the progress part of track
    Rectangle rTrack = new Rectangle();
    if (!bVert) {
      rTrack.x = nX0+nMargin;
      rTrack.y = nYMid-(nProgressW/2);
      rTrack.width = nCtrlPos;
      rTrack.height = nProgressW;
      GUIslice.drawFillRect(pGui,rTrack,colProgress);
    } else {
      rTrack.x = nXMid-(nProgressW/2);
      rTrack.y = nY0+nMargin;
      rTrack.width = nProgressW;
      rTrack.height = nCtrlPos;
      GUIslice.drawFillRect(pGui,rTrack,colProgress);
    }

    // test for thumb trim color
    if (bTrimThumb) {
      // two color thumb
      GUIslice.drawFillCircle(pGui,nLeftX,nLeftY,nThumbSz,colTrim);
      GUIslice.drawFillCircle(pGui,nLeftX,nLeftY,3,colThumb);
    } else {
      // one solid color thumb
      GUIslice.drawFillCircle(pGui,nLeftX,nLeftY,nThumbSz,colProgress);
    }
    if (bFrameThumb) {
      GUIslice.drawFrameCircle(pGui,nLeftX,nLeftY,nThumbSz,colFrame);
    }
    
    // Draw the remaining part of track
    Rectangle rRemain = new Rectangle();
    if (!bVert) {
      if (nRemainW == 1) {
        GUIslice.drawLine(pGui,nX0+nMargin,nYMid,nX1-nMargin,nYMid,colRemain);
      } else {
        rRemain.x = nX0+nMargin+nCtrlPos;
        rRemain.y = nYMid-(nRemainW/2);
        rRemain.width = nX1 - (nX0 + nCtrlPos+ nMargin*2) +1;
        rRemain.height = nRemainW;
        GUIslice.drawFillRect(pGui,rRemain,colRemain);
      }
    } else {
      if (nRemainW == 1) {
        GUIslice.drawLine(pGui,nXMid,nY0+nMargin,nXMid,nY1-nMargin,colRemain);
      } else {
        rRemain.x = nXMid-(nRemainW/2);
        rRemain.y = nY0+nMargin+nCtrlPos;
        rRemain.width = nRemainW;
        rRemain.height = (nY1-nMargin)-(nY0+nMargin+nCtrlPos);
        GUIslice.drawFillRect(pGui,rRemain,colRemain);
      }
    }

    // Draw any ticks - we need to do this last or the ticks get over written
    // - Need at least one tick segment
    if (nTickDiv>=1) {
      int  nTickInd;
      int   nTickOffset;
      for (nTickInd=0;nTickInd<=nTickDiv;nTickInd++) {
        nTickOffset = nTickInd * nCtrlRng / nTickDiv;
        if (!bVert) {
          GUIslice.drawLine(pGui,nX0+nMargin+nTickOffset,nYMid-(nTickLen/2),
                  nX0+nTickOffset+nMargin,nYMid+(nTickLen/2),colTick);
        } else {
          GUIslice.drawLine(pGui,nXMid+(nTickLen/2),nY0+nTickOffset+nMargin,nXMid-(nTickLen/2),
                  nY0+nTickOffset+nMargin,colTick);
        }
      }
    }

    super.drawSelRect(pGui, rElem);
  }

}
