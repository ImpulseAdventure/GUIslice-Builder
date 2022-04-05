/**
 *
 * The MIT License
 *
 * Copyright 2020-2022 Paul Conti
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
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import builder.Builder;
import builder.parser.ParserException;
import builder.parser.Token;
//import builder.parser.Tokenizer;
import builder.parser.TokenizerException;

public class FontGlcd extends FontTFT {
  
  // Font variables
  private byte[] bitmap;   ///< Character bitmaps

  // state variables
  boolean     bWrap;
  int         textsize_x;  
  int         textsize_y;
  
  // sizing variables
  private int tmpX;
  private int tmpY;
  private int x1;
  private int y1;
  private int minx;
  private int miny;
  private int maxx;
  private int maxy;
  
  // text drawing variables
  WritableRaster raster;
  private int cursor_x;
  private int cursor_y;
  FontMetrics strMetrics;
  
  public FontGlcd() {
  }

  /**
   * setTextSize
   * Set text 'magnification' size.
   * Each increase in size makes 1 pixel that much bigger.
   * @param size  Desired text size. 1 is default 6x8, 2 is 12x16, 3 is 18x24, etc
   * @see builder.fonts.FontTFT#setTextSize(int)
   */
  @Override
  public void setTextSize(int size) {
    this.textsize_x = size;
    this.textsize_y = size;
  }

  /**
   * create
   *
   * @see builder.fonts.FontTFT#create(java.lang.String, java.lang.String)
   */
  @Override
  public boolean create(FontItem item) throws FontException {
    this.item = item;
    this.fontType = FONT_GLCD;
    this.bWrap = true;
    this.textsize_x = 1;
    this.textsize_y = 1;
    return parseGlcdFont(item.getFileName(), item.getDisplayName());
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
    if ((codePoint >= 0) && (codePoint <= 255)) { // Char present in this font?
      return true;
    }
    return false;
  }
  
  /**
   * drawString
   *
   * @see builder.fonts.FontTFT#drawString(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, java.awt.Color, java.awt.Color, boolean)
   */
  @Override
  public void drawString(Graphics2D g2d, Rectangle r, String s, Color colTxt, Color colBg, boolean bClippingEn) {
    
//    Builder.logger.debug("Enter drawTxt: [" + s + "]");

    int ch;
    if (s == null) return;
    int length = s.length();
    if (length == 0) return;
    strMetrics = getTextBounds(s,0,0,bClippingEn);

    if (strMetrics.w <=0 || strMetrics.h <= 0) return;

    // clipping
    if (bClippingEn) {
      if (strMetrics.w+r.x > Builder.CANVAS_WIDTH) {
        strMetrics.w = strMetrics.w - (strMetrics.w + r.x - Builder.CANVAS_WIDTH);
      }
      if (strMetrics.h+r.y > Builder.CANVAS_HEIGHT) {
        strMetrics.h = strMetrics.h - (strMetrics.h - r.y - Builder.CANVAS_HEIGHT);
      }
    }
//    Builder.logger.debug("Metrics x1=" + strMetrics.x1 + " y1=" + strMetrics.y1
//        + " w=" + strMetrics.w + " h=" + strMetrics.h);
    try {
    // create our image
    BufferedImage image = new BufferedImage(strMetrics.w, strMetrics.h, BufferedImage.TYPE_INT_ARGB );
    raster = image.getRaster();
    
//    Builder.logger.debug("width=" + image.getWidth() + " height=" + image.getHeight());

    // create a transparent background
    if (colTxt == colBg) {
      for (int i = 0; i < image.getWidth(); i++) {
        for (int j = 0; j < image.getHeight(); j++) {
          raster.setPixel(i, j, new int[] { 0, 0, 0, 0 });
//          Builder.logger.debug("raster.setPixel [" + i + " , " + j + "]");
        }
      }
    }
    cursor_x = 0;
    cursor_y = 0;

    for (int i = 0; i < length; i++) {
      ch = s.charAt(i);
      // ignore newlines
      if (ch == '\n' || ch == '\r') {
        continue;
      }
      copyChar(cursor_x, cursor_y, ch, colTxt, colBg, textsize_x, textsize_y, bClippingEn);
      cursor_x += textsize_x * 6; // Advance x one char
    }
    g2d.drawImage(image, r.x, r.y, null);
    } catch (IllegalArgumentException e) {
      Builder.logger.debug("Text element exceeds screen");
    }
//    Builder.logger.debug("minx=" + minx + " maxx=" + maxx + " miny=" + miny + " maxy=" + maxy);
  }

  /**
   * drawImage
   *
   * @see builder.fonts.FontTFT#drawImage(java.awt.Rectangle, java.lang.String, java.awt.Color, java.awt.Color, boolean)
   */
  @Override
  public BufferedImage drawImage(Rectangle r, String s, Color colTxt, Color colBg, boolean bClippingEn) {
    
  //  Builder.logger.debug("Enter drawTxt: [" + s + "]");
  
    int ch;
    if (s == null) return null;
    int length = s.length();
    if (length == 0) return null;
    strMetrics = getTextBounds(s,0,0,bClippingEn);

    int img_w = strMetrics.w;
    int img_h = strMetrics.h;
    if (r.width > img_w)
      img_w = r.width;
    if (r.height > img_h)
      img_h = r.height;
      
    // no clipping is needed since JTable will do that for us.
/*
    if (bClippingEn) {
      if (strMetrics.w+r.x > Builder.CANVAS_WIDTH) {
        strMetrics.w = strMetrics.w - (strMetrics.w + r.x - Builder.CANVAS_WIDTH);
      }
      if (strMetrics.h+r.y > Builder.CANVAS_HEIGHT) {
        strMetrics.h = strMetrics.h - (strMetrics.h - r.y - Builder.CANVAS_HEIGHT);
      }
    }
*/
//    Builder.logger.debug("Metrics x1=" + strMetrics.x1 + " y1=" + strMetrics.y1
//        + " w=" + strMetrics.w + " h=" + strMetrics.h);
    
    // create our image
    BufferedImage image = new BufferedImage(img_w, img_h, BufferedImage.TYPE_INT_ARGB );
    raster = image.getRaster();
    
  //  Builder.logger.debug("width=" + image.getWidth() + " height=" + image.getHeight());
  
    // create a background
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        raster.setPixel(i, j, new int[] { 255, 
                                          colBg.getRed(), 
                                          colBg.getGreen(),
                                          colBg.getBlue() });
      }
    }
    // draw our text, if any
    if (strMetrics.w > 0 || strMetrics.h > 0) {
  
//      cursor_x = 0;
//      cursor_y = 0;
      cursor_x = r.x;
      cursor_y = r.y;
    
      for (int i = 0; i < length; i++) {
        ch = s.charAt(i);
        // ignore newlines
        if (ch == '\n' || ch == '\r') {
          continue;
        }
        copyChar(cursor_x, cursor_y, ch, colTxt, colBg, textsize_x, textsize_y, bClippingEn);
        cursor_x += textsize_x * 6; // Advance x one char
      }
    }
    return image;
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
   
    char ch; // Current character

    x1 = x;
    tmpX = x;
    y1 = y;
    tmpY = y;
    int w  = 0;
    int h = 0;
    // clipping
    if (bClippingEn) {
      minx = Builder.CANVAS_WIDTH;
      miny = Builder.CANVAS_HEIGHT;
    } else {
      minx = 32767;
      miny = 32767;
    }
    maxx = -1;
    maxy = -1;
    
    for (int i=0; i<str.length(); i++) {
      ch = str.charAt(i);
      if (ch > 255) continue;
      if (ch == '\n' || ch == '\r') { // ignore newlines
        continue;
      }
      charBounds(ch);  // modifies class variables for sizing
    }
    
    if (maxx >= minx) {
      x1 = 0;
      w = maxx - minx + 1;
    }
    if (maxy >= miny) {
      y1 = 0;
      h = maxy - miny + 1;
    }

    // clipping
    w++;
    h++;
    if (bClippingEn) {
      if (w > Builder.CANVAS_WIDTH) w = Builder.CANVAS_WIDTH;
      if (h > Builder.CANVAS_HEIGHT) h = Builder.CANVAS_HEIGHT;
    }
    return new FontMetrics(x1,y1,w,h);
  }
 
  /**
   * getCharSize
   *
   * @see builder.fonts.FontTFT#getCharSize(char)
   */
  @Override
  public Dimension getCharSize(char ch) {
    tmpX = 0;
    tmpY = 0;
    int w  = 0;
    int h = 0;
    minx = 32767;
    miny = 32767;
    maxx = -1;
    maxy = -1;
    
    if (ch == '\n' || ch == '\r') return null;
    charBounds(ch);  // modifies class variables for sizing

    if (maxx >= minx) {
      w = maxx - minx + 1;
    }
    if (maxy >= miny) {
      h = maxy - miny + 1;
    }
    w = tmpX;
    return new Dimension(w,h);
  }

  /**
   * getMaxCharSize
   *
   * @see builder.fonts.FontTFT#getMaxCharSize()
   */
  public Dimension getMaxCharSize() {
    int w = textsize_x * 6;
    int h = textsize_y * 8;
    return new Dimension(w,h);
  }

  /**
   * Helper to determine size of a character with this font/size.
   * used by getTextBounds() function.
   * It modifies class variables: x1,y1,minX,maxX,minY, and maxY
   * @param    ch    The character in question
   */
  private void charBounds(char ch) {
    int x2 = tmpX + textsize_x * 6 - 1; // Lower-right pixel of char
    int y2 = tmpY + textsize_y * 8 - 1;
    if (x2 > maxx)
      maxx = x2; // Track max x, y
    if (y2 > maxy)
      maxy = y2;
    if (tmpX < minx)
      minx = tmpX; // Track min x, y
    if (tmpY < miny)
      miny = tmpY;
    tmpX += textsize_x * 6; // Advance x one char
  }

  /**
   * Copy a single character into display raster in order to render text. 
   * Handles all the color manipulation and conversion from a single bit to colored pixel.
   * @param x           Bottom left corner x coordinate
   * @param y           Bottom left corner y coordinate
   * @param ch          The 8-bit font-indexed character
   * @param colTxt
   * @param colBg
   * @param size_x
   * @param size_y
   * @param bClippingEn
   */
  private void copyChar(int x, int y, int ch, Color colTxt, Color colBg, int size_x, int size_y, boolean bClippingEn) {

//    Builder.logger.debug("***copyChar [" + Integer.toHexString(ch) + "]");

    if (bClippingEn) {
      if ((x >= Builder.CANVAS_WIDTH) || // Clip right
          (y >= Builder.CANVAS_HEIGHT) || // Clip bottom
          ((x + 6 * size_x - 1) < 0) || // Clip left
          ((y + 8 * size_y - 1) < 0)) // Clip top
        return;
    }
    
    for (int i = 0; i < 5; i++) { // Char bitmap = 5 columns
      if (ch > 255) continue;
      int line = bitmap[ch * 5 + i] & 0xFF;
      for (int j = 0; j < 8; j++, line >>= 1) {
        if ((line & 0x01) > 0) { // Bit On?
          if (size_x == 1 && size_y == 1) {
            writePixel(x + i, y + j, colTxt);
          } else {
            writeFillRect(x + i * size_x, y + j * size_y, size_x, size_y, colTxt);
          }
        } else if (colBg != colTxt) {
          if (size_x == 1 && size_y == 1)
            writePixel(x + i, y + j, colBg);
          else
            writeFillRect(x + i * size_x, y + j * size_y, size_x, size_y, colBg);
        }
      }
    }
    if (colBg != colTxt) { // If opaque, draw vertical line for last column
      writeFillRect(x + 5 * size_x, y, size_x, 8 * size_y, colBg);
    }
  }
  
  /**
   * Write a pixel into destination array
   * 
   * @param x Top left corner x coordinate
   * @param y Top left corner y coordinate
   * @param col Color to fill
   */
  private void writePixel(int x, int y, Color col) {
    // our gate protection against crashes
    try {
      raster.setPixel(x, y, new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
    } catch(ArrayIndexOutOfBoundsException e) {
 //     Builder.logger.debug(String.format("%s writePixel ch: %c exceeded: %d,%d", getDisplayName(), ch, x,y));
    }
  }

  /**
   * Fill a rectangle completely with one color into destArray
   * 
   * @param x Top left corner x coordinate
   * @param y Top left corner y coordinate
   * @param w Width in pixels
   * @param h Height in pixels
   * @param colFill Color to fill
   */
  private void writeFillRect(int x, int y, int w, int h, Color colFill) {
    for (int i = x; i < x + w; i++) {
      for (int j = y; j < y + h; j++) {
        writePixel(i, j, colFill);
      }
    }
  }

  /**
   * Parse the glcdfont.c file
   * @return true if successful
   * @throws FontException
   */
  private boolean parseGlcdFont(String fontFileName, String fontName) throws FontException {
    Token token = null;
    File file = new File(fontFileName);
    try {
      tokenizer.setSource(file);
//      Builder.logger.debug("Opened file: " + fontFileName);
      // loop until we find we find open brace '{'
      boolean bFound = false;
      while ((token = tokenizer.nextToken()).getType() != SEMICOLON) {
        if (token.getType() == OPEN_BRACE) {
          bFound = true;
          break;
        }
      }
      if (!bFound) parseError(token, "bitmap not found");
      /* Read our giant bitmap using array list of class Short.
       * We can't use Byte here because Java doesn't have ubyte.
       * GLCD fonts don't list the number of bytes involved in the array 
       * so we have to waste memory using class Short instead
       * of something like: byte[] bytes = new byte[size];
       */
      ArrayList<Short> byteList = new ArrayList<Short>();
      Short  n;
      while ((token = tokenizer.nextToken()).getType() != SEMICOLON) {
        if (token.getType() == HEX) {
          n = new Short(Integer.decode(token.getToken()).shortValue());
          byteList.add(n);
        }
      }
//      Builder.logger.debug("bitmap bytes: " + byteList.size());
      bitmap = new byte[byteList.size()];
      n = 0;
      for (Short b : byteList) {
        bitmap[n++] = (byte) (b.byteValue() & 0xFF);
      }
      byteList.clear();
      byteList = null;
      tokenizer.close();
      return true;
    } catch (IOException | ParserException | FontException | NumberFormatException | TokenizerException e) {
      String msg = String.format("File [%s]: %s", 
          file.getName(), e.toString());
      tokenizer.close();
      Builder.logger.error(msg);
      return false;
    }
  }

}
