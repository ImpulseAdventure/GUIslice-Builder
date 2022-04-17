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
import java.awt.Point;
import java.awt.Rectangle;

import builder.common.GUIslice;
import builder.fonts.FontFactory;
import builder.fonts.FontTFT;
import builder.common.CommonUtils;
import builder.models.ListBoxModel;

/**
 * The Class TextBoxWidget simulates GUIslice API gslc_ElemXTextboxCreate() call.
 * 
 * @author Paul Conti
 * 
 */
public class ListBoxWidget extends Widget {

  ListBoxModel m;

  private int nListRows=0;
  private int nItemW=0;
  private int nItemH=0;
  private int nItemGap=0;
  
  /**
   * Instantiates a new text box widget.
   *
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public ListBoxWidget(int x, int y) {
    u = CommonUtils.getInstance();
    ff = FontFactory.getInstance();
    m = new ListBoxModel();
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
    ListBoxModel m = ((ListBoxModel) model);
    if (m.addScrollbar()) {
      Rectangle rInner = GUIslice.expandRect(b,-1,-1);
      listboxDraw(g2d, rInner);
/*
      g2d.setColor(m.getFillColor());
      g2d.fillRect(b.x, b.y, b.width, b.height);
      g2d.setColor(m.getFrameColor());
*/
//      g2d.drawRect(b.x, b.y, b.width, b.height);
//      g2d.drawRect(b.x+2, b.y+4, b.width-2-m.getScrollbarWidth(), b.height-7);
      Rectangle rScrollBar = new Rectangle((b.x+b.width-2),(b.y+4),m.getScrollbarWidth(),(b.height-8));   
      drawScrollBar(g2d, m, rScrollBar);
      if (m.isFrameEnabled()) {
        Rectangle rFrame = new Rectangle(b.x,b.y,b.width+m.getScrollbarWidth(),b.height);   
        GUIslice.drawFrameRect(g2d, rFrame, m.getFrameColor());
      }
    } else {
      Rectangle rInner = GUIslice.expandRect(b,-1,-1);
      listboxDraw(g2d, rInner);
      if (m.isFrameEnabled()) {
        GUIslice.drawFrameRect(g2d, b, m.getFrameColor());
      }
    }
    super.drawSelRect(g2d, b);
  }

  private void listboxDraw(Graphics2D pGui,Rectangle rElem) {

    Rectangle rInner;

    Color colElemFill  = m.getFillColor();
    Color colTxt       = m.getTextColor();
    Color colGap       = m.getGapColor();
    nItemGap           = m.getItemGap();
    nItemW             = m.getTextWidth();
    nItemH             = m.getTextHeight();
    
    if (m.isFrameEnabled()) {
    // If a frame is enabled, shrink the inner region
      rInner = GUIslice.expandRect(rElem,-1,-1);
      Rectangle rFrame = new Rectangle(rElem.x,rElem.y,rElem.width-1,rElem.height);
      GUIslice.drawFrameRect(pGui, rFrame, m.getFrameColor());
    } else {
      rInner = rElem;
    }

    GUIslice.drawFillRect(pGui, rInner, colGap);

    int       nRows = m.getRows();
    int       nCols = m.getColumns();
    int       nItemBaseX, nItemBaseY;
    int       nItemX, nItemY;

    String[] items = m.getItems();
    
    if (items == null || items[0].isEmpty()) {
      return;
    }

    // Error check
    if (nRows == 0 || nCols == 0) {
      return;
    }

    // Determine top-left coordinate of list matrix
    int nX0 = rInner.x;
    int nY0 = rInner.y;
    nItemBaseX = nX0 + m.getMarginWidth();
    nItemBaseY = nY0 + m.getMarginHeight();

    String acStr = "";

    // Loop through the items in the list
    int nItemTop = 0;
    int nItemCnt = items.length;
    int nItemInd;

    // The builder only supports auto sizing for now...
    listboxRecalcSize(nRows, nCols, nItemCnt, rInner);

    // Note that nItemTop is always pointing to an
    // item index at the start of a row

    // Determine the list indices to display in the visible window due to scrolling
    int nDispIndMax = (nListRows * nCols);
    
    // grab out font
    FontTFT font = ff.getFont(m.getFontDisplayName());
    if (font == null) {
      return;
    }

    for (int nDispInd = 0; nDispInd < nDispIndMax; nDispInd++) {

      // Calculate the item index based on the display index
      nItemInd = nItemTop + nDispInd;

      // Did we go past the end of our list?
      if (nItemInd >= nItemCnt) {
        break;
      }

      // Fetch the list item
      acStr = items[nItemInd];

      int   nItemIndX, nItemIndY;
      int   nItemOuterW, nItemOuterH;

      // Convert linear count into row & column
      nItemIndY = nDispInd / nCols; // Round down
      nItemIndX = nDispInd % nCols;

      // Calculate total spacing between items (including gap)
      nItemOuterW = nItemW + nItemGap;
      nItemOuterH = nItemH + nItemGap;

      // Determine top-left corner of each item
      nItemY = nItemBaseY + (nItemIndY * nItemOuterH);
      nItemX = nItemBaseX + (nItemIndX * nItemOuterW);

      // Create rect for item
      Rectangle rItemRect = new Rectangle(nItemX, nItemY, nItemW, nItemH);

      //test top make sure it fits on screen
      if (!isInside(rInner, rItemRect))
        break;
      // Draw the list item
      GUIslice.drawFillRect(pGui, rItemRect, colElemFill);

      // Draw the aligned text string (by default it is GSLC_ALIGN_MID_LEFT)
      if (acStr != null && !acStr.isEmpty()) {
        ff.drawText(pGui, m.getAlignment(), rItemRect, acStr, font, colTxt, colElemFill, 0);
      }
    }

  }

  // Recalculate listbox item sizing
  private void listboxRecalcSize(int nRows, int nCols, int nItemCnt, Rectangle rElem) {
    int nElem;
    int nElemInner;
    int nItemOuter;

    nListRows = ((nItemCnt + 1) / nCols);

    // NOTE: In the nElemInner calculation, we add nItemGap to account
    // for the fact that the last column does not include a "gap" after it.
    
    // AUTO SIZE?
    if (nItemW < 0) {
      nElem = rElem.width;
      nElemInner = nElem - (2 * m.getMarginWidth()) + nItemGap;
      nItemOuter = nElemInner / nCols;
      nItemW = nItemOuter - nItemGap;
    }

    if (nItemH < 0) {
      nElem = rElem.height;
      nElemInner = nElem - (2 * m.getMarginHeight()) + nItemGap;
      nItemOuter = nElemInner / nRows;
      nItemH = nItemOuter - nItemGap;
    }
  }
  
  /**
   * Draw scroll bar.
   *
   * @param g2d
   *          the g 2 d
   * @param m
   *          the m
   * @param r
   *          the r
   */
  private void drawScrollBar(Graphics2D pGui, ListBoxModel m, Rectangle rElem) {
    // Most of this code was shamelessly ripped from GUIslice_ex.c->gslc_ElemXSliderDraw()
    int         nPos      = 0;
    int         nPosMin   = 0;
    int         nPosMax   = m.getScrollbarMaxValue();
    int         nThumbSz  = m.getScrollbarThumb();
    Color       colElemFrame    = m.getFrameColor();
    Color       colElemFill     = m.getFillColor();
    
    // Range check on nPos
    if (nPos < nPosMin) { nPos = nPosMin; }
    if (nPos > nPosMax) { nPos = nPosMax; }

    int nX0,nY0,nX1,nY1,nXMid;
    nX0 = rElem.x;
    nY0 = rElem.y;
    nX1 = rElem.x + rElem.width - 1;
    nY1 = rElem.y + rElem.height - 1;
    nXMid = (nX0+nX1)/2;

    // Scale the current position
    int nPosRng = nPosMax-nPosMin;
    // TODO: Check for nPosRng=0, reversed min/max
    int nPosOffset = nPos-nPosMin;

    // Provide some margin so thumb doesn't exceed control bounds
    int nMargin   = nThumbSz;
    int nCtrlRng;
    nCtrlRng = (nY1-nMargin)-(nY0+nMargin);
    if (nPosRng == 0) {
      nPosRng=10;
    }
    int nCtrlPos  = (nPosOffset*nCtrlRng/nPosRng)+nMargin;

    GUIslice.drawFillRect(pGui,rElem,colElemFill);

    // Draw the track
    GUIslice.drawLine(pGui,nXMid,nY0+nMargin,nXMid,nY1-nMargin,colElemFrame);


    int        nCtrlX0,nCtrlY0;
    Rectangle  rThumb = new Rectangle();
    nCtrlX0   = nXMid-nThumbSz;
    nCtrlY0   = nY0+nCtrlPos-nThumbSz;
    rThumb.x      = nCtrlX0;
    rThumb.y      = nCtrlY0;
    rThumb.width  = 2*nThumbSz;
    rThumb.height = 2*nThumbSz;

    // Draw the thumb control
    GUIslice.drawFillRect(pGui,rThumb,colElemFill);
    GUIslice.drawFrameRect(pGui,rThumb,colElemFrame);

  }
  
  private boolean isInside(Rectangle outer, Rectangle inner)
  {
    Point pInnerStart = new Point(inner.x, inner.y);
    Point PInnerEnd = new Point(inner.x+inner.width, inner.y+inner.height);
      return outer.contains(pInnerStart)
          && outer.contains(PInnerEnd);
  }
}
