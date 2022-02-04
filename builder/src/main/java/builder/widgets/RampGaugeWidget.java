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

import builder.common.CommonUtils;
import builder.common.GUIslice;
import builder.models.RampGaugeModel;

/**
 * The Class RampGaugeWidget simulates GUIslice API gslc_ElemXGaugeCreate() call.
 * 
 * @author Paul Conti
 * 
 */
public class RampGaugeWidget extends Widget {

  RampGaugeModel m;
  
  /**
   * Instantiates a new ramp gauge widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public RampGaugeWidget(int x, int y) {
    u = CommonUtils.getInstance();
    m = new RampGaugeModel();
    model = m;
    super.setXY(model, x, y);
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g2d) {
    Rectangle b = getWinBounded();

    g2d.setColor(m.getFillColor());
    g2d.fillRect(b.x, b.y, b.width, b.height);
    drawRamp(g2d, b);
    super.drawSelRect(g2d, b);
  }
  
  public void drawRamp(Graphics2D g2d, Rectangle b) {
    
    int nElemX0   = b.x;
    int nElemY1   = b.y + b.height - 1;
    int nElemW    = b.width;
    int nElemH    = b.height;

    int nMax      = m.getMax();
    int nMin      = m.getMin();
    int nRng      = nMax - nMin;
    int nVal      = m.getCurValue();
    nVal = nMax-10;  // make the gauge pretty on screen
    int nInd;
    
    if (nRng == 0) return;
    
    int nHeight;
    int nHeightTmp;
    int nHeightBot;
    int nX;

    // Calculate region to draw
    int nValStart = 0;
    int nValEnd = nVal;

    // Calculate the scaled gauge position
    int nPosXStart  = (nValStart - nMin)*nElemW/nRng;
    int nPosXEnd    = (nValEnd   - nMin)*nElemW/nRng;

    int nSclFX = nElemH*32767/(nElemW*nElemW);
    Color nCol;
    int nColInd;

    for (nX=nPosXStart;nX<nPosXEnd;nX++) {
      nInd = nElemW-nX;
      nHeightTmp = nSclFX * nInd*nInd /32767;
      nHeight = nElemH-nHeightTmp;
      if (nHeight >= 20) {
        nHeightBot = nHeight-20;
      } else {
        nHeightBot = 0;
      }

      int nSteps = 10;
      int nGap = 3;

      if (nSteps == 0) {
        nColInd = nX*1000/nElemW;
        nCol = GUIslice.colorBlend3(Color.GREEN,Color.YELLOW,Color.RED,500,nColInd);
      } else {
        int  nBlockLen,nSegLen,nSegInd,nSegOffset,nSegStart;
        nBlockLen = ((nElemW-(nSteps-1)*nGap)/nSteps);
        nSegLen =  (nBlockLen + nGap);
        nSegInd =  (nX/nSegLen);
        nSegOffset =  (nX % nSegLen);
        nSegStart =  (nSegInd * nSegLen);

        if (nSegOffset <= nBlockLen) {
          // Inside block
          nColInd = nSegStart*1000/nElemW;
          nCol = GUIslice.colorBlend3(Color.GREEN,Color.YELLOW,Color.RED,500,nColInd);

        } else {
          // Inside gap
          // - No draw
          nCol = m.getFillColor();
        }
      } // end nSteps
      
      g2d.setColor(nCol);
//System.out.println("nX: "+nX+" nHeightBot: "+nHeightBot+" nHeight: "+nHeight);
      if (nHeight > 0)
        g2d.drawLine(nElemX0+nX,nElemY1-nHeightBot,nElemX0+nX,nElemY1-nHeight);

    } // end for

  
  }
  
}
