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

package builder.fonts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import builder.Builder;
import builder.parser.ParserException;
import builder.parser.Token;
//import builder.parser.Tokenizer;
import builder.parser.TokenizerException;

public class FontUTFT extends FontTFT {
  
  // Font variables
  private byte[] bitmap;   ///< Character bitmaps
  private int  first;         ///< ASCII extents (first char)
  private int  last;          ///< ASCII extents (last char)

  // sizing variables
  private int char_width;
  private int char_height;

  // text drawing variables
  WritableRaster raster;
  private int cursor_x;
  private int cursor_y;
  FontMetrics strMetrics;
  
  public FontUTFT() {
  }

  /**
   * create
   *
   * @see builder.fonts.FontTFT#create(java.lang.String, java.lang.String)
   */
  @Override
  public boolean create(FontItem item) throws FontException {
    this.item = item;
    this.fontType = FONT_UTFT;
    return parseFont(item.getFileName(), item.getDisplayName());
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
    if ((codePoint >= first) && (codePoint <= last)) { // Char present in this font?
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
    Builder.logger.debug("Metrics x1=" + strMetrics.x1 + " y1=" + strMetrics.y1
        + " w=" + strMetrics.w + " h=" + strMetrics.h);
    
    // create our image
    BufferedImage image = new BufferedImage(strMetrics.w, strMetrics.h, BufferedImage.TYPE_INT_ARGB );
    raster = image.getRaster();
    
    Builder.logger.debug("width=" + image.getWidth() + " height=" + image.getHeight());

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
      if (canDisplay(ch)) {
        copyChar(cursor_x, cursor_y, ch, colTxt, colBg, bClippingEn);
        cursor_x += char_width; // Advance x one char
      }
    }
    g2d.drawImage(image, r.x, r.y, null);
  }

  /**
   * drawImage
   *
   * @see builder.fonts.FontTFT#drawImage(java.awt.Rectangle, java.lang.String, java.awt.Color, java.awt.Color, boolean)
   */
  @Override
  public BufferedImage drawImage(Rectangle r, String s, Color colTxt, Color colBg, boolean bClippingEn) {
    
  //  Builder.logger.debug("Enter drawTxt: [" + s + "]");
  
    char ch; // current char
    if (s == null) return null;
    int length = s.length();
    if (length == 0) return null;
    strMetrics = getTextBounds(s,0,0,bClippingEn);

    if (strMetrics.w <=0 || strMetrics.h <= 0) return null;
  
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
    
    // create our image
    BufferedImage image = new BufferedImage(strMetrics.w, strMetrics.h, BufferedImage.TYPE_INT_ARGB );
    raster = image.getRaster();
    
  //  Builder.logger.debug("width=" + image.getWidth() + " height=" + image.getHeight());
  
    // create a transparent background
    if (colTxt == colBg) {
      for (int i = 0; i < image.getWidth(); i++) {
        for (int j = 0; j < image.getHeight(); j++) {
          raster.setPixel(i, j, new int[] { 0, 0, 0, 0 });
  //        Builder.logger.debug("raster.setPixel [" + i + " , " + j + "]");
        }
      }
    }
    cursor_x = 0;
    cursor_y = 0;
    Dimension size;
    for (int i = 0; i < length; i++) {
      ch = s.charAt(i);
      if (canDisplay(ch)) {
        size = getCharSize(ch);
        if (size == null) continue;
        
        copyChar(cursor_x, cursor_y, ch, colTxt, colBg, bClippingEn);
        cursor_x += char_width; // Advance x one char
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
   
    int w  = 0;
    int h = char_height;
    
    char ch; // current char
    Dimension size;
    for (int i=0; i<str.length(); i++) {
      ch = str.charAt(i);
      if (canDisplay(ch)) {
        size = getCharSize(ch);
        if (size == null) continue;
        w += size.width;
      }
    }
    
    // clipping
    if (bClippingEn) {
      if (w > Builder.CANVAS_WIDTH) w = Builder.CANVAS_WIDTH;
      if (h > Builder.CANVAS_HEIGHT) h = Builder.CANVAS_HEIGHT;
    }
    return new FontMetrics(x,y,w,h);
  }
 
  /**
   * getCharSize
   *
   * @see builder.fonts.FontTFT#getCharSize(char)
   */
  @Override
  public Dimension getCharSize(char ch) {
    if (ch > 255) return null;
    if (ch == '\n' || ch == '\r') return null;
    return getMaxCharSize();
  }

  /**
   * getMaxCharSize
   *
   * @see builder.fonts.FontTFT#getMaxCharSize()
   */
  public Dimension getMaxCharSize() {
    return new Dimension(char_width,char_height);
  }

  /**
   * Copy a single character into display raster in order to render text. 
   * Handles all the color manipulation and conversion from a single bit to colored pixel.
   * @param x           Bottom left corner x coordinate
   * @param y           Bottom left corner y coordinate
   * @param ch          The 8-bit font-indexed character
   * @param colTxt
   * @param colBg
   * @param bClippingEn
   */
  private void copyChar(int x, int y, int ch, Color colTxt, Color colBg, boolean bClippingEn) {

//    Builder.logger.debug("***copyChar [" + Integer.toHexString(ch) + "]");

    if (bClippingEn) {
      if ((x >= Builder.CANVAS_WIDTH) || // Clip right
          (y >= Builder.CANVAS_HEIGHT) || // Clip bottom
          ((x + char_width - 1) < 0) || // Clip left
          ((y + char_height - 1) < 0)) // Clip top
        return;
    }
    
    // Todo: Add character clipping here
    byte bits = 0;
    int bo = ((ch - first)*((char_width/8)*char_height));
    for(int i=0; i<char_height; i++) {
      for (int zz=0; zz<(char_width/8); zz++) {
        try {
        bits = bitmap[bo+zz]; 
        } catch(java.lang.ArrayIndexOutOfBoundsException e) {
          Builder.logger.error(String.format("ch: %c bo=%d zz=%d i=%d", (char)ch, bo, zz, i));
        }
        for(int j=0; j<8; j++) {   
        
          if((bits&(1<<(7-j)))!=0) {
            writePixel((char)ch, x+j+(zz*8), y + i, colTxt);
          } 
        }
      }
      bo+=(char_width/8);
    }
  }
  
  /**
   * Write a pixel into destination array
   * 
   * @param ch character we are displaying - only needed for debug
   * @param x Top left corner x coordinate
   * @param y Top left corner y coordinate
   * @param col Color to fill
   */
  private void writePixel(char ch, int x, int y, Color col) {
    // our gate protection against crashes
    try {
      raster.setPixel(x, y, new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
    } catch(ArrayIndexOutOfBoundsException e) {
      Builder.logger.debug(String.format("%s writePixel ch: %c exceeded: %d,%d", getDisplayName(), ch, x,y));
    }
  }

  /**
   * Parse the font.c file
   * @return true if successful
   * @throws FontException
   */
  private boolean parseFont(String fontFileName, String fontName) throws FontException {
    Token token = null;
    File file = new File(fontFileName);
    int nBytes = 0;
    try {
      tokenizer.setSource(file);
      Builder.logger.debug("Opened file: " + fontFileName);
      boolean bFound = false;
      // loop until we find "fontName"
      while ((token = tokenizer.nextToken()).getType() != 0) {
        if (token.getType() == WORD) {
          if (token.getToken().equals(fontName)) {
            bFound = true;
            break;
          }
        }
      }
      if (!bFound) {
        Builder.logger.debug(String.format("missing 'fontdatatype %s[nnnn]'",fontName));
        return false;
      }
      token = tokenizer.nextToken(); // should be '['
      // now we should have number of bytes in bitmap plus 4 for header 
      token = tokenizer.nextToken(); 
      if (token.getType() != INTEGER) {
        Builder.logger.debug(String.format("missing '%s[nnnn' found %s",fontName,token.getToken()));
        return false;
      }
      try{
        nBytes = Integer.parseInt(token.getToken()) - 4; // remove 4 byte header from count
      }
      catch (NumberFormatException ex){
        Builder.logger.debug(String.format("NumberFormatException %s[%s",fontName,token.getToken()));
      }
      
      // find our first hex number
      while ((token = tokenizer.nextToken()).getType() != HEX) {
        continue;
      }
      /* now we should be on our 4 byte header
       * byte 1 character width, 
       * byte 2 character height,
       * byte 3 first character ascii value
       * byte 4 number of characters in font
       */
      char_width = Integer.decode(token.getToken()).intValue();
      token = tokenizer.nextToken(); // should be comma
      token = tokenizer.nextToken(); 
      char_height = Integer.decode(token.getToken()).intValue();
      token = tokenizer.nextToken(); // should be comma
      token = tokenizer.nextToken(); 
      first = Integer.decode(token.getToken()).intValue();
      token = tokenizer.nextToken(); // should be comma
      token = tokenizer.nextToken(); 
      last = Integer.decode(token.getToken()).intValue();
      last += first - 1;
      
      bitmap = new byte[nBytes];
      Short b;
      token = tokenizer.nextToken(); // should be comma
      for (int i=0; i<nBytes; i++) {
        token = tokenizer.nextToken(); 
        if (token.getType() != HEX) {
          Builder.logger.debug(String.format("missing hex number->found %s",fontName,token.getToken()));
          return false;
        }
        b = new Short(Integer.decode(token.getToken()).shortValue());
        bitmap[i] = (byte) (b.byteValue() & 0xFF);
        token = tokenizer.nextToken(); // should be comma
      }
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
