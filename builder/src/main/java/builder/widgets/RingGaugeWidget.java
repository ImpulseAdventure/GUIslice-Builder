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

//import java.awt.BasicStroke;
import java.awt.Color;
//import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import builder.common.Utils;
import builder.common.GUIslice;
import builder.fonts.FontFactory;
import builder.models.RingGaugeModel;

/**
 * <p>
 * The Class RingGaugeWidget simulates GUIslice API gslc_ElemXRingGaugeCreate() call.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class RingGaugeWidget extends Widget {
  
  private RingGaugeModel m;
  
  /**
   * Instantiates a new radio button widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public RingGaugeWidget(int x, int y) {
    u = Utils.getInstance();
    ff = FontFactory.getInstance();
    m = new RingGaugeModel();
    model = m;
    super.setXY(model, x, y);
  }

  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g) {
    Graphics2D g2d = (Graphics2D) g.create();
    Rectangle b = getWinBounded();
    drawRing(g2d);
/*
 * Code commented out to fix bug 120
 * The generated code always defines an empty string for the gauge text, 
 * which is different to what you see in the builder. 
 *  g2d.setColor(m.getTextColor());
 *  String text = String.valueOf(m.getCurValue()) + "%";
 *  Font font = ff.getFont(m.getFontDisplayName());
 *   ff.alignString(g2d, "GSLC_ALIGN_MID_MID", b, text, font);
 */
    super.drawSelRect(g2d, b);
    g2d.dispose();
  }
  
  public void drawRing(Graphics2D g2d) {
    Rectangle b = getWinBounded();
    int nElemX    = b.x;
    int nElemY    = b.y;
    int nElemW    = b.width;
    int nElemH    = b.height;

    int nVal = 75;
    if (m.getCurValue() > 0) {
      nVal = m.getCurValue();
    }
    Color colRingActive1 = m.getGradientStartColor();
    Color colRingActive2 = m.getGradientEndColor();
    Color colRingInactive = m.getInactiveColor();
    Color colStep;

    // Calculate the ring center and radius
    int nMidX = nElemX + nElemW / 2;
    int nMidY = nElemY + nElemH / 2;
    int nRad2 = (nElemW < nElemH) ? nElemW / 2 : nElemH / 2;
    int nRad1 = nRad2 - m.getLineThickness();

    // --------------------------------------------------------------------------

    Point[] anPts = new Point[4]; // Storage for points in segment Quadrilateral 
    int nAng64Start,nAng64End;

    // Calculate segment ranges
    // - TODO: Handle nPosMin, nPosMax
    // - TODO: Rewrite to use nDeg64PerSeg more effectively
    // - FIXME: Handle case with nDeg64PerSeg < 64 (ie. <1 degree)
    int nStep64 = 5*64; // Trig functions work on 1/64 degree units
    int nStepAng = 5;
    int nValSegs = (nVal * 360 / 100) / nStepAng; 
    int nMaxSegs = 360 / nStepAng; // Final segment index of circle
    int nSegStart = 0;
    int nSegEnd = nMaxSegs;

    // TODO: Consider drawing in reverse order if (nVal < nValLast)
    for (int nSegInd = nSegStart; nSegInd < nSegEnd; nSegInd++) {
      nAng64Start = nSegInd * nStep64;
      nAng64End = nAng64Start + nStep64;
      // Convert polar coordinates into Cartesian
      Point p;
      p = GUIslice.polarToXY(nRad1, nAng64Start);
      anPts[0] = new Point(nMidX + p.x, nMidY + p.y);
      p = GUIslice.polarToXY(nRad2, nAng64Start);
      anPts[1] = new Point(nMidX + p.x, nMidY + p.y);
      p = GUIslice.polarToXY(nRad2, nAng64End);
      anPts[2] = new Point(nMidX + p.x, nMidY + p.y);
      p = GUIslice.polarToXY(nRad1, nAng64End);
      anPts[3] = new Point(nMidX + p.x, nMidY + p.y);

      // Adjust color depending on which segment we are rendering
      if (nSegInd < nValSegs) {
        if (m.useGradientColors()) {
          // Gradient coloring
          int nGradPos = 1000 * nSegInd / nMaxSegs;
          colStep = GUIslice.colorBlend2(colRingActive1, colRingActive2, 500, nGradPos);
        } else {
          // Flat coloring
          colStep = m.getActiveColor();
        }
      } else {
        colStep = colRingInactive;
      }

      // Draw the quadrilateral representing the circle segment
      g2d.setColor(colStep);
      GUIslice.fillQuad(g2d, anPts);

    }
    
  }
}
