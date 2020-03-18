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
import java.awt.Point;
import java.awt.Rectangle;

import builder.common.CommonUtils;
import builder.common.GUIslice;
import builder.models.RadialGaugeModel;

/**
 * The Class RadialGaugeWidget simulates GUIslice API gslc_ElemXGaugeCreate() call.
 * 
 * @author Paul Conti
 * 
 */
public class RadialGaugeWidget extends Widget {

  RadialGaugeModel m;
  
  /**
   * Instantiates a new redial gauge widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public RadialGaugeWidget(int x, int y) {
    u = CommonUtils.getInstance();
    m = new RadialGaugeModel();
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
    drawRadial(g2d, b);
    super.drawSelRect(g2d, b);
  }
  
  public void drawRadial(Graphics2D g2d, Rectangle b) {
  
    int nElemX0     = b.x;
    int nElemY0     = b.y;
    int nElemX1     = b.x + b.width - 1;
    int nElemY1     = b.y + b.height - 1;
    int nX          = (nElemX0+nElemX1)/2;
    int nY          = (nElemY0+nElemY1)/2;
    int nElemRad    = (b.width>=b.height) ? b.height/2 : b.width/2;
    
    int nArrowLen   = m.getIndicatorSize();
    int nArrowSz    = m.getIndicatorTipSize();
    boolean bFill   = m.isIndicatorFill();

    int nMax        = m.getMax();
    int nMin        = m.getMin();
    int nRng        = nMax - nMin;
    int nVal        = m.getCurValue();
    
    // draw our circle
    int cx = b.x + (b.width/2);
    int cy = b.y + (b.height/2);
    int radius = b.width/2;
    g2d.setColor(m.getFrameColor());
    GUIslice.drawCircle(g2d, cx, cy, radius);
    
    // Support reversing of direction
    int nAng64      = 0;
    if (!m.isClockwise()) {
      nAng64      = (int)(nMax - nVal    )* 360*64 /nRng;
    } else {
      nAng64      = (int)(nVal     - nMin)* 360*64 /nRng;
    }

    // draw any tick marks
    if (m.getDivisions() > 0) {
      int    nTickLen  = m.getTickSize();
      int    nTickAng  = 360 / m.getDivisions();
      
      g2d.setColor(m.getTickColor());
      for (int nInd=0;nInd<360; nInd+=nTickAng) {
        GUIslice.drawLinePolar(g2d,nX,nY,nElemRad-nTickLen,nElemRad,nInd*64);
      }
    }
    
    // draw indicator
    Point nTip     = GUIslice.polarToXY(nArrowLen,nAng64);
    Point nTipBase = GUIslice.polarToXY(nArrowLen-nArrowSz,nAng64);
    Point nBase1   = GUIslice.polarToXY(nArrowSz,nAng64-90*64);
    Point nBase2   = GUIslice.polarToXY(nArrowSz,nAng64+90*64);

    g2d.setColor(m.getIndicatorColor());
    if (!bFill) {
      // Framed
      g2d.drawLine(nX+nBase1.x,nY+nBase1.y,nX+nBase1.x+nTipBase.x,nY+nBase1.y+nTipBase.y);
      g2d.drawLine(nX+nBase2.x,nY+nBase2.y,nX+nBase2.x+nTipBase.x,nY+nBase2.y+nTipBase.y);
      g2d.drawLine(nX+nBase1.x+nTipBase.x,nY+nBase1.y+nTipBase.y,nX+nTip.x,nY+nTip.y);
      g2d.drawLine(nX+nBase2.x+nTipBase.x,nY+nBase2.y+nTipBase.y,nX+nTip.x,nY+nTip.y);
      g2d.drawLine(nX+nBase1.x,nY+nBase1.y,nX+nBase2.x,nY+nBase2.y);
    } else {
      // Filled
      Point[] pts = new Point[4];
      
      // Main body of pointer
      pts[0] = new Point(nX+nBase1.x, nY+nBase1.y);
      pts[1] = new Point(nX+nBase1.x+nTipBase.x, nY+nBase1.y+nTipBase.y);
      pts[2] = new Point(nX+nBase2.x+nTipBase.x, nY+nBase2.y+nTipBase.y);
      pts[3] = new Point(nX+nBase2.x, nY+nBase2.y);
      GUIslice.fillQuad(g2d, pts);

      // Tip of pointer
      pts[0] = new Point(nX+nBase1.x+nTipBase.x, nY+nBase1.y+nTipBase.y);
      pts[1] = new Point(nX+nTip.x, nY+nTip.y);
      pts[2] = new Point(nX+nBase2.x+nTipBase.x, nY+nBase2.y+nTipBase.y);
      GUIslice.fillTriangle(g2d, pts);
    }
    
  }

}
