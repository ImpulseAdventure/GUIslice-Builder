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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Font;

/**
 * FontSim will simulate a TFT Font using one of Java's Built-in fonts.
 * 
 * This allows the Builder to future proof by allowing users to add
 * fonts that we currently can't handle.  
 * 
 * @author Paul Conti
 *
 */
public class FontSim extends FontTFT {
  
  private int char_maxwidth;
  private int char_maxheight;

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
  public boolean create(FontItem item) throws FontException {
    this.item = item;
    this.fontType = FONT_SIM;
    this.text_size = item.getLogicalSizeAsInt();

    // Fonts are in Points with 72 points per inch so DPI / 72 is our scaling factor.
    double scaleFactor = (double)dpi / 72.0d;
    double scaleSize = (double)text_size * scaleFactor;
    
    int javaStyle;
    switch (item.getLogicalStyle()) {
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
    font = new Font(item.getLogicalName(), javaStyle, text_size);
    font = font.deriveFont((float) scaleSize);

    calculateMaxCharSize();
    return true;
  }

  /**
   * canDisplay
   *
   * @see builder.fonts.FontTFT#canDisplay(int)
   */
  public boolean canDisplay(int codePoint) {
    if (codePoint == (int)'\n' || codePoint == (int)'\r') { // ignore newlines
      return false;
    }
    boolean bCanDisplay = false;
    try {
      if (font.canDisplay(codePoint)) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( codePoint );
        bCanDisplay = (!Character.isISOControl(codePoint)) &&
          (codePoint != KeyEvent.CHAR_UNDEFINED) &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
      }
      } catch(IllegalArgumentException e) {
        ;
      }
    return bCanDisplay;
  }
  
  /**
   * drawString
   *
   * @see builder.fonts.FontTFT#drawString(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, java.awt.Color, java.awt.Color, boolean)
   */
  @Override
  public void drawString(Graphics2D g2d, Rectangle r, String str, Color colTxt, Color colBg, boolean bClippingEn) {
    /* test for zero length string */
    if (str == null || str.isEmpty()) return;
   
    g2d.setColor(colTxt);
    g2d.setFont(font);
    g2d.drawString(str, r.x, r.y);
  }

  /**
   * getTextBounds
   *
   * @see builder.fonts.FontTFT#getTextBounds(java.lang.String, int, int, boolean)
   */
  @Override
  public FontMetrics getTextBounds(String str, int x, int y, boolean bClippingEn) {

    /* test for zero length string */
    if (str == null || str.isEmpty()) return new FontMetrics(0,0,0,0);
   
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
    metrics.w = (int) r2D.getWidth() + 5; // add fudge factor
    metrics.h = javaMetrics.getHeight();

    g2d.dispose();
    return metrics;
  }

  /**
   * getCharSize
   *
   * @see builder.fonts.FontTFT#getCharSize(char)
   */
  @Override
  public Dimension getCharSize(char ch) {
    /* test for zero length string */
    String str = String.valueOf(ch);
    if (str == null || str.isEmpty()) return new Dimension(0,0);
   
    /*
     * Because font metrics is based on a graphics context, we need to create
     * a small, temporary image so we can ascertain the width and height
     * of the final image
     */
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    
    g2d.setFont(font);
    java.awt.FontMetrics javaMetrics = g2d.getFontMetrics(font);
    int w = javaMetrics.charWidth(ch);
    int h = javaMetrics.getHeight();

    g2d.dispose();
    return new Dimension(w, h);
  }

  /**
   * getMaxCharSize
   *
   * @see builder.fonts.FontTFT#getMaxCharSize()
   */
  public Dimension getMaxCharSize() {
    return new Dimension(char_maxwidth,char_maxheight);
  }

  /**
   * drawImage
   *
   * @see builder.fonts.FontTFT#drawImage(java.awt.Rectangle, java.lang.String, java.awt.Color, java.awt.Color, boolean)
   */
  @Override
  public BufferedImage drawImage(Rectangle r, String s, Color colTxt, Color colBg, boolean bClippingEn) {
    /* test for zero length string */
    if (s == null || s.isEmpty()) return null;
    
    /* test for valid rectangle */
    if (r.width <=0 || r.height <= 0) return null;

    /*
     * Because font metrics is based on a graphics context, we need to create
     * a small, temporary image so we can ascertain the width and height
     * of the final image
     */
    BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();    
    g2d.setColor(colTxt);
    g2d.setFont(font);
    g2d.drawString(s, r.x, r.y);
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
   * getTTF_Font
   * @return actual ttf font
   */
  public Font getTTF_Font() {
    return font;
  }
  
  private void calculateMaxCharSize() {
    char_maxwidth = 0;
    char_maxheight = 0;
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    
    g2d.setFont(font);
    java.awt.FontMetrics javaMetrics = g2d.getFontMetrics(font);
    int w = 0;
    int h = 0;
    for (int i=32; i<=126; i++ ) {
      char ch = (char)i;
      if ((ch == '\n') ||
          (ch == '\r') ||
          (ch == ' '))
        continue;

      w = javaMetrics.charWidth(ch);
      h = javaMetrics.getHeight();

      if (w > char_maxwidth) char_maxwidth = w;
      if (h > char_maxheight) char_maxheight = h;
    }

    g2d.dispose();

  }
}
