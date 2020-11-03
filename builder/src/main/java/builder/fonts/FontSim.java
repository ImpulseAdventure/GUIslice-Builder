/**
 *
 * The MIT License
 *
 * Copyright 2020 Paul Conti
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
package builder.fonts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Font;

/**
 * FontSim will simulate a TFT Font using one of Java's Built-in fonts.
 * 
 * This allows the Builder to future proof by allowing users to add
 * fonts that we currently can't handle and to support those platforms like
 * Linux that use actual TrueType Fonts.  
 * 
 * @author Paul Conti
 *
 */
public class FontSim extends FontTFT {
  
  private int logicalSize;
  private String logicalStyle;

  private int dpi;
  
  /** The java font. */
  private Font font;
  
  /** The java font size. */
  private int text_size;
  
  public FontSim() {
  }
  
  /**
   * create
   *
   * @see builder.fonts.FontTFT#create(java.lang.String, int, int, java.lang.String)
   */
  @Override
  public boolean create(String fileName, String fontName, int size, String style) throws FontException {
    this.fontName = fontName;
    this.fontType = FONT_SIM;
    this.logicalSize = size;
    this.logicalStyle = style;
    this.text_size = size;

    // Fonts are in Points with 72 points per inch so DPI / 72 is our scaling factor.
    double scaleFactor = (double)dpi / 72.0d;
    double scaleSize = (double)text_size * scaleFactor;
    
    int javaStyle;
    switch (style) {
    case "Bold":
      javaStyle = Font.BOLD;
      break;
    case "Italic":
      javaStyle = Font.ITALIC;
      break;
    case "Bold+Italic":
      javaStyle = Font.BOLD + Font.ITALIC;
      break;
    default:
      javaStyle = Font.PLAIN;
      break;
    }
    font = new Font(fontName, javaStyle, text_size);
    font = font.deriveFont((float) scaleSize);

    return true;
  }

  /**
   * Draw a text string at the given coordinate
   * @param g2d     The graphics context
   * @param r       Rectangle region to contain the text
   * @param str     String to display
   * @param colTxt  Color to draw text
   * @param colBg   Color of background
   */
  @Override
  public void drawString(Graphics2D g2d, Rectangle r, String str, Color colTxt, Color colBg) {
    g2d.setColor(colTxt);
    g2d.setFont(font);
    g2d.drawString(str, r.x, r.y);
  }

  /**
   * Determine size of a string with current font/size. 
   * Pass string and a cursor position, returns UL corner and W,H.
   * @param str     The string to measure
   * @param x       The current cursor X
   * @param y       The current cursor Y
   * @return  FontMetrics
   */
  @Override
  public FontMetrics getTextBounds(String str, int x, int y) {

    /*
     * Because font metrics is based on a graphics context, we need to create
     * a small, temporary image so we can ascertain the width and height
     * of the final image
     */
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    
    g2d.setFont(font);
    java.awt.FontMetrics javaMetrics = g2d.getFontMetrics(font);
    FontRenderContext frc = g2d.getFontRenderContext();

    /* TextLayout is better at descent, and width
     * while java.awt.FontMetrics is more accurate for ascent and height.
     * Ugh!!! Even so still a little off. 
     */
    TextLayout layout = new TextLayout(str, font, frc);
    layout.draw(g2d, (float)x, (float)y);

    Rectangle2D r2D = layout.getBounds();

    FontMetrics metrics = new FontMetrics();
    metrics.x1 = (int) layout.getDescent();
    metrics.y1 = -((int)javaMetrics.getAscent());
    metrics.w = (int) r2D.getWidth();
    metrics.h = javaMetrics.getHeight();

    g2d.dispose();
    return metrics;
  }

  /**
   * drawImage
   *
   * @see builder.fonts.FontTFT#drawImage(java.awt.Rectangle, java.lang.String, java.awt.Color, java.awt.Color)
   */
  @Override
  public BufferedImage drawImage(Rectangle r, String s, Color colTxt, Color colBg) {
    /*
     * Because font metrics is based on a graphics context, we need to create
     * a small, temporary image so we can ascertain the width and height
     * of the final image
     */
    FontMetrics strMetrics = getTextBounds(s,0,0);
    
    BufferedImage image = new BufferedImage(strMetrics.w, strMetrics.h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();    
    g2d.setColor(colTxt);
    g2d.setFont(font);
    g2d.drawString(s, 0, 0);
    g2d.dispose();
    return image;
  }
  
  /**
   * setDPI
   * 
   * We have to take into account the target display screen's DPI.
   * Adafruits's 2.8 screen is about DPI of 141 and GFX fonts are
   * hardcoded to this number. 
   * Fonts are in Points with 72 points per inch so DPI / 72 is our scaling factor.
   * 
   * This routine is only valid for the set of FONT_SIM fonts and must be called before
   * createFont()
   * 
   * @param dpi
   */
  public void setDPI(int dpi) {
    this.dpi = dpi;
  }
  
  /**
   * Gets the logical size.
   *
   * @return the logical size
   */
  @Override
  public int getLogicalSize() {
    return logicalSize;
  }
  
  /**
   * Gets the logical style.
   *
   * @return the logical style
   */
  @Override
  public String getLogicalStyle() {
    return logicalStyle;
  }

}
