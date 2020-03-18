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
package builder.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * The Class GUIslice holds various method that emulate 
 * GUIslice API calls, in particular those that involve drawing.
 * 
 * @author Paul Conti
 */
public final class GUIslice {
  
  public GUIslice() {
    
  }
  
  /**
   * Draw circle.
   * Convenience method to draw a circle from center with radius
   *
   * @param g2d
   *          the graphics object
   * @param xCenter
   *          the x center
   * @param yCenter
   *          the y center
   * @param r
   *          the r
   */
  static public void drawCircle(Graphics2D g2d, int xCenter, int yCenter, int r) {
    g2d.drawOval(xCenter-r, yCenter-r, 2*r, 2*r);
  }

  /**
   * Fill circle.
   * Convenience method to fill circle from center with radius
   *
   * @param g2d
   *          the graphics
   * @param xCenter
   *          the x center
   * @param yCenter
   *          the y center
   * @param r
   *          the r
   */
  static public void fillCircle(Graphics2D g2d, int xCenter, int yCenter, int r) {
    g2d.fillOval(xCenter-r, yCenter-r, 2*r, 2*r);
  }
  
  static public void drawTriangle(Graphics2D g2d, Point pts[]) {
    int x[]= new int[3];
    int y[]= new int[3];
    x[0] = pts[0].x;
    y[0] = pts[0].y;
    x[1] = pts[1].x;
    y[1] = pts[1].y;
    x[2] = pts[2].x;
    y[2] = pts[2].y;
    g2d.drawPolygon(x,y,3);
  }

  static public void fillTriangle(Graphics2D g2d, Point pts[]) {
    int x[]= new int[3];
    int y[]= new int[3];
    x[0] = pts[0].x;
    y[0] = pts[0].y;
    x[1] = pts[1].x;
    y[1] = pts[1].y;
    x[2] = pts[2].x;
    y[2] = pts[2].y;
    g2d.fillPolygon(x,y,3);
  }

  static public void drawQuad(Graphics2D g2d, Point pts[]) {
    int x[]= new int[4];
    int y[]= new int[4];
    x[0] = pts[0].x;
    y[0] = pts[0].y;
    x[1] = pts[1].x;
    y[1] = pts[1].y;
    x[2] = pts[2].x;
    y[2] = pts[2].y;
    x[3] = pts[3].x;
    y[3] = pts[3].y;
    g2d.drawPolygon(x,y,3);
  }
  
  static public void fillQuad(Graphics2D g2d, Point psPt[]) {
    // Break down quadrilateral into two triangles
    Point[] t1 = new Point[3];
    t1[0] = new Point(psPt[0]);
    t1[1] = new Point(psPt[1]);
    t1[2] = new Point(psPt[2]);
    fillTriangle(g2d, t1);

    Point[] t2 = new Point[3];
    t2[0] = new Point(psPt[2]);
    t2[1] = new Point(psPt[0]);
    t2[2] = new Point(psPt[3]);
    fillTriangle(g2d, t2);
  }
  
  //Sine function with optional lookup table
  static public int sinFX(int n64Ang) {
    int    nRetValS;
    // Use floating-point math library function
    // Calculate angle in radians
    float fAngRad = (float) ((float)n64Ang*Math.PI*2.0/(360.0*64.0));
//    System.out.println("fAngRad=" + fAngRad + " n64Ang=" + n64Ang);
    // Perform floating point calc
    float fSin = (float) Math.sin(fAngRad);
    // Return as fixed point result
    nRetValS = (int) (fSin * 32767.0);
//    System.out.println("fSin=" + fSin + " nRetValS=" + nRetValS);
    return nRetValS;
}

  //Sine function with optional lookup table
  static public int cosFX(int n64Ang) {
    int    nRetValS;
    // Calculate angle in radians
    float fAngRad = (float) ((float)n64Ang*Math.PI*2.0/(360.0*64.0));
//    System.out.println("fAngRad=" + fAngRad + " n64Ang=" + n64Ang);
    // Perform floating point calc
    float fCos = (float) Math.cos(fAngRad);
    // Return as fixed point result
    nRetValS = (int) (fCos * 32767.0);
//    System.out.println("fCos=" + fCos + " nRetValS=" + nRetValS);
    return nRetValS;
  }
  
// Convert from polar to Cartesian (angles are * 64)
  static public Point polarToXY(int nRad,int nAng) {
    Point result = new Point();
    result.x = (int)((nRad *  sinFX(nAng)) / 32767);
    result.y = (int)((nRad *  -cosFX(nAng)) / 32767);
    return result;
  }

  static public void drawLinePolar(Graphics2D g2d,int nX,int nY,int nRadStart,int nRadEnd,int nAng64) {
    // Note that angle is in degrees * 64
    
    // Draw the ray representing the current value
    int nDxS = (int)nRadStart * sinFX(nAng64)/32768;
    int nDyS = (int)nRadStart * cosFX(nAng64)/32768;
    int nDxE = (int)nRadEnd   * sinFX(nAng64)/32768;
    int nDyE = (int)nRadEnd   * cosFX(nAng64)/32768;
    g2d.drawLine(nX+nDxS,nY-nDyS,nX+nDxE,nY-nDyE);
  }
  
  //Call with nMidAmt=500 to create simple linear blend between two colors
  static public Color colorBlend2(Color colStart,Color colEnd,int nMidAmt,int nBlendAmt) {
    int r, g, b;
    r = ((colEnd.getRed()+colStart.getRed())/2);
    g = (colEnd.getGreen()+colStart.getGreen())/2;
    b = (colEnd.getBlue()+colStart.getBlue())/2;
    Color colMid = new Color(r,g,b);
    return colorBlend3(colStart,colMid,colEnd,nMidAmt,nBlendAmt);
  }


  static public Color colorBlend3(Color colStart,Color colMid,Color colEnd,int nMidAmt,int nBlendAmt) {
    nMidAmt   = (nMidAmt  >1000)?1000:nMidAmt;
    nBlendAmt = (nBlendAmt>1000)?1000:nBlendAmt;

    int  nRngLow   = nMidAmt;
    int  nRngHigh  = 1000-nMidAmt;
    int  nSubBlendAmt;
    int r, g, b;
    if (nBlendAmt >= nMidAmt) {
      nSubBlendAmt = (nBlendAmt - nMidAmt)*1000/nRngHigh;
      r = nSubBlendAmt*(colEnd.getRed() - colMid.getRed())/1000 + colMid.getRed();
      g = nSubBlendAmt*(colEnd.getGreen() - colMid.getGreen())/1000 + colMid.getGreen();
      b = nSubBlendAmt*(colEnd.getBlue() - colMid.getBlue())/1000 + colMid.getBlue();
    } else {
      nSubBlendAmt = (nBlendAmt - 0)*1000/nRngLow;
      r = nSubBlendAmt*(colMid.getRed() - colStart.getRed())/1000 + colStart.getRed();
      g = nSubBlendAmt*(colMid.getGreen() - colStart.getGreen())/1000 + colStart.getGreen();
      b = nSubBlendAmt*(colMid.getBlue() - colStart.getBlue())/1000 + colStart.getBlue();
    }
    return new Color(r,g,b);
  }

}
