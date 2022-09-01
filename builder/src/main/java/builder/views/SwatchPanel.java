/**
 *
 * The MIT License
 *
 * Copyright 2019-2022 Paul Conti
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
package builder.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;
import javax.swing.UIManager;

import builder.Builder;

/**
 * The Class SwatchPanel manages recent color selection
 * They are stored inside the users preferences General Model.
 * 
 * @author Paul Conti
 */
public class SwatchPanel extends JPanel {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant CUSTOM_COLORS_FILE. */
  public  static final String CUSTOM_COLORS_FILE = "custom_colors.list";
  
  /** size of color square in our recent colors panel  */
  public static final int NSQUARE = 15;

  /** number of columns in our recent colors panel  */
  public static final int NCOLUMNS = 4;
  
  /** number of rows in our recent colors panel  */
  public static final int NROWS = 4;
  
  /** lru cache of recent colors */
  static List<Color> colorList = null;
  
  /** The swatch size. */
  protected Dimension swatchSize;
  
  /** The num swatches. */
  protected Dimension numSwatches;
  
  /** The gap. */
  protected Dimension gap;

  /** The selected row. */
  private int selRow;
  
  /** The selected col. */
  private int selCol;
  
  /** The num colors. */
  private static final int NUMCOLORS = 16;

  private static Color defaultColor = null;
  
//  private Color defaultColor = new Color(230,230,230);

  /**
   * Instantiates a new swatch panel.
   */
  public SwatchPanel() {
    initValues();
    initColors();
    setToolTipText(""); // register for events
    setOpaque(true);
    setBackground(Color.white);
    setFocusable(true);
    setInheritsPopupMenu(true);
    
    addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        repaint();
      }

      public void focusLost(FocusEvent e) {
        repaint();
      }
    });
  }

  /**
   * Gets the selected color.
   *
   * @return the selected color
   */
  public Color getSelectedColor() {
    return getColorForCell(selCol, selRow);
  }

  /**
   * Initializes the values.
   */
  protected void initValues() {
    swatchSize = new Dimension(NSQUARE, NSQUARE);
    numSwatches = new Dimension(NCOLUMNS, NROWS);
    gap = new Dimension(1, 1);
  }

  /**
   * Initializes the colors.
   */
  protected void initColors() {
    if (defaultColor == null) {
      defaultColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor", getLocale());
    }
    if (colorList == null) {
      colorList = new ArrayList<Color>();
      // initialize lru with nulls
      for (int i = 0; i < NUMCOLORS; i++) {
        colorList.add(null);
      }
    }
  }

  /**
   * paintComponent
   *
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  public void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    for (int row = 0; row < numSwatches.height; row++) {
      int y = row * (swatchSize.height + gap.height);
      for (int column = 0; column < numSwatches.width; column++) {
        Color c = getColorForCell(column, row);
        if (c == null) 
          c = defaultColor;
        g.setColor(c);
        int x;
        if (!this.getComponentOrientation().isLeftToRight()) {
          x = (numSwatches.width - column - 1) * (swatchSize.width + gap.width);
        } else {
          x = column * (swatchSize.width + gap.width);
        }
        g.fillRect(x, y, swatchSize.width, swatchSize.height);
        g.setColor(Color.black);
        g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
        g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);
/*
        if (selRow == row && selCol == column && this.isFocusOwner()) {
          Color c2 = new Color(c.getRed() < 125 ? 255 : 0, c.getGreen() < 125 ? 255 : 0, c.getBlue() < 125 ? 255 : 0);
          g.setColor(c2);

          g.drawLine(x, y, x + swatchSize.width - 1, y);
          g.drawLine(x, y, x, y + swatchSize.height - 1);
          g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
          g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);
          g.drawLine(x, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
          g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y);
        }
*/
      }
    }
  }

  /**
   * getPreferredSize
   *
   * @see javax.swing.JComponent#getPreferredSize()
   */
  public Dimension getPreferredSize() {
    int x = numSwatches.width * (swatchSize.width + gap.width) - 1;
    int y = numSwatches.height * (swatchSize.height + gap.height) - 1;
    return new Dimension(x, y);
  }

  /**
   * getToolTipText
   *
   * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
   */
  public String getToolTipText(MouseEvent e) {
    Color color = getColorForLocation(e.getX(), e.getY());
    if (color == null) return "No Color";
    return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
  }

  /**
   * Sets the selected color from location.
   *
   * @param x
   *          the x
   * @param y
   *          the y
   */
  public void setSelectedColorFromLocation(int x, int y) {
    if (!this.getComponentOrientation().isLeftToRight()) {
      selCol = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
    } else {
      selCol = x / (swatchSize.width + gap.width);
    }
    selRow = y / (swatchSize.height + gap.height);
    repaint();
  }

  /**
   * Gets the color for location.
   *
   * @param x
   *          the x
   * @param y
   *          the y
   * @return the color for location
   */
  public Color getColorForLocation(int x, int y) {
    int column;
    if (!this.getComponentOrientation().isLeftToRight()) {
      column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
    } else {
      column = x / (swatchSize.width + gap.width);
    }
    int row = y / (swatchSize.height + gap.height);
    return getColorForCell(column, row);
  }

  /**
   * Gets the color for cell.
   *
   * @param column
   *          the column
   * @param row
   *          the row
   * @return the color for cell
   */
  private Color getColorForCell(int column, int row) {
    // convert a 2D array index into a 1D index
    int idx = row * NCOLUMNS + column;
    if (idx>=colorList.size()) return null;
    return colorList.get(idx);
  }

  /**
   * Sets the most recent color.
   *
   * @param c
   *          the new most recent color
   */
  public void setMostRecentColor(Color c) {
    /* update our colorlist but first check and 
     * see if the color is already present
     * if so, just return
     */
    if (c == null)
      return;
    if (colorList.size() == 0) {
      // defensive programming, can't or should never get here
      colorList.add(c);
      return;
    } 
    ListIterator<Color> litr = colorList.listIterator();
    Color swatch=null;
    int n = 0;
    // find first free location
    while(litr.hasNext()){
      swatch = litr.next();
      if (swatch == null) {
//        Builder.logger.debug("==null");
        break;
      }
//      Builder.logger.debug("n="+n+" swatch: "+swatch.toString()+" c: "+c.toString());
      if (swatch.getRGB() == c.getRGB()) {
//        Builder.logger.debug("==c.getRGB()");
        return;
      }
      n++;
    }
    if (n >= NUMCOLORS) {
      // just replace end of list
      // TODO - create a hole and push everyone down by one position
      Builder.logger.debug("add to end");
      colorList.set(NUMCOLORS-1,c); // can't use add
    }
    colorList.set(n,c);
    Builder.logger.debug("add next");
    repaint();
  }

}
