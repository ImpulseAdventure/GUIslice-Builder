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

public class FontGFX extends FontTFT {
  
  private int logicalSize;
  private String logicalStyle;

  // Font variables
  private ArrayList<FontGFXGlyph>  glyphList  = new ArrayList<FontGFXGlyph>();
  private byte[] bitmap;   ///< Character bitmaps
  private int  first;     ///< ASCII extents (first char)
  private int  last;      ///< ASCII extents (last char)
  @SuppressWarnings("unused")
  private int  yAdvance;  ///< Newline distance (y axis)

  // state variables
  int         textsize_x;  
  int         textsize_y;
  
  // sizing variables
  private int tmpX;
  private int tmpY;
  private int minx;
  private int miny;
  private int maxx;
  private int maxy;
  private int minbase;
  private int maxbase;
  
  // text drawing variables
  WritableRaster raster;
  private int cursor_x;
  private int cursor_y;
  FontMetrics strMetrics;

  // DEBUG
  char dbgCh;
  
  public FontGFX() {
  }

  /**
   * setTextSize
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
  public boolean create(String fileName, String fontName, int size, String style) throws FontException {
    this.fontFileName = fileName;
    this.fontName = fontName;
    this.logicalSize = size;
    this.logicalStyle = style;
    this.fontType = FONT_GFX;
    this.textsize_x = 1;
    this.textsize_y = 1;

    return parseGFXFont();
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

    if (strMetrics.w <=0 || strMetrics.h <= 0) return;

    //    Builder.logger.debug("drawString: " + s + " clipping=" + bClippingEn + " " + strMetrics.toString());

    // create our image
    BufferedImage image = new BufferedImage(strMetrics.w, strMetrics.h, BufferedImage.TYPE_INT_ARGB );
    raster = image.getRaster();
    
//    Builder.logger.debug("width=" + image.getWidth() + " height=" + image.getHeight());

    // create a transparent background
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        raster.setPixel(i, j, new int[] { 0, 0, 0, 0 });
      }
    }
    cursor_x = strMetrics.x1;
    cursor_y = strMetrics.base_height;
    char c;
    for (int i = 0; i < length; i++) {
      c = s.charAt(i);
      ch = c;
      // ignore newlines
      if (ch == '\n' || ch == '\r') {
        continue;
      }
      if ((ch >= first) && (ch <= last)) {
        FontGFXGlyph glyph = glyphList.get(ch - first);
        int w = glyph.width;
        int h = glyph.height;
        if ((w > 0) && (h > 0)) { // Skip if char not printable
          copyChar(cursor_x, cursor_y, ch, colTxt, colBg, textsize_x, textsize_y);
        }
        cursor_x += glyph.xAdvance * textsize_x;
      }
    }
    g2d.drawImage(image, r.x, r.y+strMetrics.y1, null);
  }

  /**
   * drawImage
   *
   * @see builder.fonts.FontTFT#drawImage(java.awt.Rectangle, java.lang.String, java.awt.Color, java.awt.Color, boolean)
   */
  @Override
  public BufferedImage drawImage(Rectangle r, String s, Color colTxt, Color colBg, boolean bClippingEn) {
  //  Builder.logger.debug("Enter drawImage: [" + s + "]");
  
    int ch;
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

    if (strMetrics.w <=0 || strMetrics.h <= 0) return null;

    //    Builder.logger.debug("drawImage: " + s + " " + strMetrics.toString());
    
    // create our image
    BufferedImage image = new BufferedImage(strMetrics.w, strMetrics.h, BufferedImage.TYPE_INT_ARGB );
    raster = image.getRaster();
    
//    Builder.logger.debug("width=" + image.getWidth() + " height=" + image.getHeight());
  
    // create a transparent background
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        raster.setPixel(i, j, new int[] { 0, 0, 0, 0 });
      }
    }
    cursor_x = strMetrics.x1;
    cursor_y = strMetrics.base_height;
  
    for (int i = 0; i < length; i++) {
      ch = s.charAt(i);
      // ignore newlines
      if (ch == '\n' || ch == '\r') {
        continue;
      }
      if ((ch >= first) && (ch <= last)) {
        FontGFXGlyph glyph = glyphList.get(ch - first);
        int w = glyph.width;
        int h = glyph.height;
        if ((w > 0) && (h > 0)) { // Skip if char not printable
          copyChar(cursor_x, cursor_y, ch, colTxt, colBg, textsize_x, textsize_y);
        }
        cursor_x += glyph.xAdvance * textsize_x;
      }
    }
    return image;
  }

  /**
   * Determine size of a string with current font/size. 
   * Pass string and a cursor position, returns UL corner and W,H.
   * 
   * WARNING!  This code is a port of Adafruit's GFX code and
   * and that code is buggy. 
   * 
   * (1) If you use GFXfonts the width given back is too short.  You can adjust 
   * this to the true width by taking the width (w) given back and adding
   * the x1 value * 2.
   * 
   * (2) The code for sizing strings with newlines ('\n') is completely bogus.
   * You need to break the string up into multiple strings with each one
   * representing one line. I have therefore flag newlines by throwing
   * an FontException. 
   * 
   * @param str     The string to measure
   * @param x       The current cursor X
   * @param y       The current cursor Y
   * @param clippingEn
   * @return  FontMetrics
   */
  @Override
  public FontMetrics getTextBounds(String str, int x, int y, boolean bClippingEn) throws FontException {

    /* test for zero length string */
    if (str == null || str.isEmpty()) return new FontMetrics(0,0,0,0);
   
    char ch; // Current character

    int x1 = x;
    tmpX = x;
    int y1 = y;
    tmpY = y;
    int w  = 0;
    int h = 0;
    // clipping
    if (bClippingEn) {
      minx = Builder.CANVAS_WIDTH;
      miny = Builder.CANVAS_HEIGHT;
      minbase = Builder.CANVAS_HEIGHT;
    } else {
      minx = 32767;
      miny = 32767;
      minbase = 32767;
    }
    maxx = -1;
    maxy = -1;
    maxbase = -1;
    
    for (int i=0; i<str.length(); i++) {
      ch = str.charAt(i);
      if (ch == '\n' || ch == '\r') throw new FontException("newlines are not supported");
      charBounds(ch, bClippingEn);  // modifies class variables for sizing
    }
    
    if (maxx >= minx) {
      x1 = minx;
      w = maxx - minx + 1;
    }
    if (maxy >= miny) {
      y1 = miny;
      h = maxy - miny + 1;
    }
    if (maxbase >= minbase) {
      maxbase = maxbase - minbase + 1;
    }
    // clipping
    if (bClippingEn) {
      if (w > Builder.CANVAS_WIDTH) w = Builder.CANVAS_WIDTH;
      if (h > Builder.CANVAS_HEIGHT) h = Builder.CANVAS_HEIGHT;
    }
    FontMetrics metrics = new FontMetrics(x1,y1,w,h,maxbase);
//    Builder.logger.debug("getTextBounds: " + str + " " + metrics.toString());
    return metrics;
  }
 
  /**
   * Helper to determine size of a character with this font/size.
   * used by getTextBounds() function.
   * It modifies class variables: minX,maxX,minY, and maxY
   * @param    ch    The character in question
   */
  private void charBounds(char ch, boolean bClippingEn) {
    if ((ch >= first) && (ch <= last)) { // Char present in this font?
      FontGFXGlyph glyph = glyphList.get(ch - first);
      int gw = glyph.image_width;
      int gh = glyph.image_height;
      int xa = glyph.xAdvance;
      int xo = glyph.xOffset;
      int yo = glyph.yOffset;
      if (((tmpX + ((xo + gw) * textsize_x)) > Builder.CANVAS_WIDTH)) {
        return;
      }
      int tsx = textsize_x;
      int tsy = textsize_y;
      int x1 = tmpX + xo * tsx;
      int y1 = tmpY + yo * tsy;
      int x2 = x1 + gw * tsx - 1;
      int y2 = y1 + gh * tsy - 1;
      int yBase1 = tmpY + yo * tsy;
      int yBase2 = yBase1 + glyph.height * tsy - 1;
      if (x1 < minx)
        minx = x1;
      if (x2 > maxx)
        maxx = x2;
      if (y1 < miny)
        miny = y1;
      if (y2 > maxy)
        maxy = y2;
      // determine our baseline offset
      if (yBase1 < minbase)
        minbase = yBase1;
      if (yBase2 > maxbase)
        maxbase = yBase2;
      tmpX += xa * tsx;
    }
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
   */
  private void copyChar(int x, int y, int ch, Color colTxt, Color colBg, int size_x,
    int size_y) {
    
//    Builder.logger.debug("***copyChar [" + Integer.toHexString(ch) + "]");
  
    // Character is assumed previously filtered by drawText() to eliminate
    // newlines, returns, non-printable characters, etc. Calling
    // copyChar() directly with 'bad' characters of font may cause mayhem!

    FontGFXGlyph glyph = glyphList.get(ch - first);
    int bo = glyph.bitmapOffset;
    int w = glyph.width;
    int h = glyph.height;
    int xo = glyph.xOffset;
    int yo = glyph.yOffset;
    int xx, yy;
    int bits = 0;
    int xo16 = 0;
    int yo16 = 0;
    
//    y = h;
    
    if (size_x > 1 || size_y > 1) {
      xo16 = xo;
      yo16 = yo;
    }
    // Todo: Add character clipping here
    int dbit;
    int bit = 0;
    for (yy = 0; yy < h; yy++) {
      for (xx = 0; xx < w; xx++) {
        /* I know this is ugly and stupid but I just copied it over
         * from Adafruit's GFX font handling and it works so...
         */
        dbit = bit++ & 7;
        if (dbit == 0) {
          bits = bitmap[bo++] & 0xFF;
        }
        if ((bits & 0x80) > 0) {
          if (size_x == 1 && size_y == 1) {
            writePixel((char)ch, x + xo + xx, y + yo + yy, colTxt);
          } else {
            writeFillRect((char)ch,x + (xo16 + xx) * size_x, y + (yo16 + yy) * size_y, size_x, size_y, colTxt);
          }
        }
        bits = (bits << 1) & 0xFF;
      } // end xx < w
    } // end yy < h

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
      // we need to work around the Adafruit's bug in sizing
      x = x - (strMetrics.x1 * 2);
      y = y - 1;
    // our gate protection against crashes
    try {
      raster.setPixel(x, y, new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
    } catch(ArrayIndexOutOfBoundsException e) {
//      Builder.logger.debug(String.format("%s writePixel ch: %c exceeded: %d,%d", fontName, ch, x,y));
    }
  }

  /**
   * Fill a rectangle completely with one color into destArray
   * 
   * @param ch character we are displaying - only needed for debug
   * @param x Top left corner x coordinate
   * @param y Top left corner y coordinate
   * @param w Width in pixels
   * @param h Height in pixels
   * @param colFill Color to fill
   */
  private void writeFillRect(char ch, int x, int y, int w, int h, Color colFill) {
    for (int i = x; i < x + w; i++) {
      for (int j = y; j < y + h; j++) {
        writePixel(ch, i, j, colFill);
      }
    }
  }

  /**
   * Parse the GFX Font's C header definition file
   * @return true if successful
   * @throws FontException
   */
  private boolean parseGFXFont() throws FontException {
    Token token = null;
    File file = new File(fontFileName);
    try {
      tokenizer.setSource(file);
//      Builder.logger.debug("Opened file: " + fileName);
      //----------------------------------------------------------------------------------------
      // Find our font name 
      //----------------------------------------------------------------------------------------
      boolean bFound = false;
      while ((token = tokenizer.nextToken()).getType() != SEMICOLON) {
        if (token.getType() == WORD &&
            token.getToken().startsWith(fontName)) {
          bFound = true;
          break;
        }
      }
      if (!bFound) parseError(token, "fontName not found");
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

      //----------------------------------------------------------------------------------------
      // Now read our glyph data
      //----------------------------------------------------------------------------------------
      while ((token = tokenizer.nextToken()).getType() != SEMICOLON) {
        if (token.getType() == OPEN_BRACE) break;
      }
      if (token.getType() != OPEN_BRACE) parseError(token, "glyph data");
      FontGFXGlyph glyph = null;
//      char test_idx = ' ';
      while ((token = tokenizer.nextToken()).getType() != SEMICOLON) {
        if (token.getType() == OPEN_BRACE) {
          // found a glyph
          glyph = new FontGFXGlyph();
          token = tokenizer.nextToken();
          if (token.getType() != INTEGER) parseError(token, "glyph missing bitmapoffset");
          glyph.bitmapOffset = Integer.parseInt(token.getToken());
          token = tokenizer.nextToken(); // COMMA
          token = tokenizer.nextToken(); 
          if (token.getType() != INTEGER) parseError(token, "glyph missing width");
          glyph.width = Integer.parseInt(token.getToken());
          token = tokenizer.nextToken(); // COMMA
          token = tokenizer.nextToken();
          if (token.getType() != INTEGER) parseError(token, "glyph missing height");
          glyph.height = Integer.parseInt(token.getToken());
          token = tokenizer.nextToken(); // COMMA
          token = tokenizer.nextToken();
          if (token.getType() != INTEGER) parseError(token, "glyph missing xAdvance");
          glyph.xAdvance = Integer.parseInt(token.getToken());
          token = tokenizer.nextToken(); // COMMA
          token = tokenizer.nextToken();
          if (token.getType() != INTEGER) parseError(token, "glyph missing xOffset");
          glyph.xOffset = Integer.parseInt(token.getToken());
          token = tokenizer.nextToken(); // COMMA
          token = tokenizer.nextToken();
          if (token.getType() != INTEGER) parseError(token, "glyph missing yOffset");
          glyph.yOffset = Integer.parseInt(token.getToken());
/*
          Builder.logger.debug(String.format(
              "Char [%c]-> bitmapOffset=%d  width=%d height=%d xAdvance=%d xOffset=%d yOffset="  
              ,test_idx++ 
              ,glyph.bitmapOffset
              ,glyph.width
              ,glyph.height
              ,glyph.xAdvance
              ,glyph.xOffset
              ,glyph.yOffset
              ));
*/
          glyphList.add(glyph);
        }
      }
      //----------------------------------------------------------------------------------------
      // Fill in our header
      //----------------------------------------------------------------------------------------
      bFound = false;
      while ((token = tokenizer.nextToken()).getType() != SEMICOLON) {
        if (token.getType() == HEX) {
          first = Integer.decode(token.getToken());
          token = tokenizer.nextToken();
          if (token.getType() != SPECIALCHAR) parseError(token, "GFXfont struct: missing ',' after first");
          token = tokenizer.nextToken();
          if (token.getType() != HEX) parseError(token, "GFXfont missing last");
          last = Integer.decode(token.getToken());
          token = tokenizer.nextToken();
          if (token.getType() != SPECIALCHAR) parseError(token, "GFXfont struct: missing ',' after last");
          token = tokenizer.nextToken();
          if (token.getType() != INTEGER) parseError(token, "GFXfont struct: missing yAdvance");
          yAdvance = Integer.parseInt(token.getToken());
          bFound = true;
        }
      }
      if (!bFound) parseError(token, "GFXfont struct missing");
//      Builder.logger.debug("GFXfont struct: first=" + first + " last=" + last + " yAdvance=" + yAdvance);
      bitmap = new byte[byteList.size()];
      n = 0;
      for (Short b : byteList) {
        bitmap[n++] = (byte) (b.byteValue() & 0xFF);
      }
      byteList.clear();
      byteList = null;
      tokenizer.close();
      // now we need to repair our glyphs due to the fact descent isn't recorded.
      for (int i=first; i<=last; i++) {
        repairGlyph((char)i);
      }
      return true;
    } catch (IOException | ParserException | FontException | NumberFormatException | TokenizerException e) {
      String msg = String.format("File [%s]: %s", 
          file.getName(), e.toString());
      tokenizer.close();
      Builder.logger.error(msg);
      return false;
    }
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

  /**
   * repairGlyph()
   * It turns out that the information inside the Adafruit's GFX glyphs is wrong.
   * While width is sometimes incorrect the biggest issue is the the glyph height
   * is for the baseline of the character not its real height. This means that
   * characters with a descent are not properly sized for bounding boxes.
   * This isn't a big problem for Adafruit since they can write past a boundary box
   * bit Java won't let us to do that.
   * Fixing height and width is one issue but if that is all thats done we will still
   * truncate characters with a descent because to do the display we need to adjust
   * the y parameter to the baseline since all of the bitmaps assume thats the
   * starting point for drawing.
   * @param    ch    The character in question
   */
  private void repairGlyph(char ch) {
    if ((ch >= first) && (ch <= last)) { // Char present in this font?
      FontGFXGlyph glyph = glyphList.get(ch - first);
      int w = glyph.width;
      int h = glyph.height;
      int xo = glyph.xOffset;
      int yo = glyph.yOffset;
      int bo = glyph.bitmapOffset;
      int xx, yy;
      int dbit;
      int bit = 0;
      int bits = 0;
      int x = 0;
      int y = h;

      int maxx = -32767;
      int maxy = -32767;
      int minx = 32767;
      int miny = 32767;
      int posX = 0;
      int posY = 0;
      /* So the idea here is to pretend to draw the character to an imaginary display
       * tracking maximum x and y values so we can later adjust the height and width
       * stored for the glyph.
       */
      for (yy = 0; yy < h; yy++) {
        for (xx = 0; xx < w; xx++) {
          dbit = bit++ & 7;
          if (dbit == 0) {
            bits = bitmap[bo++] & 0xFF;
          }
          if ((bits & 0x80) > 0) {
            posX = x + xo + xx;
            posY = y + yo + yy;
            if (posX > maxx)
              maxx = posX;
            if (posY > maxy)
              maxy = posY;
            if (posX < minx)
              minx = posX;
            if (posY < miny)
              miny = posY;
          }
          bits = (bits << 1) & 0xFF;
        } // end xx < w
      } // end yy < h
      if (maxx + 1 > glyph.width) {
        glyph.image_width = maxx + 1;
      } else {
        glyph.image_width = glyph.width;
      }
      if (maxy + 1 > glyph.height) {
        glyph.image_height = maxy + 1;
      } else {
        glyph.image_height = glyph.height;
      }
/*
      Builder.logger.debug(String.format(
          "Char [%c]->width=%d height=%d xAdvance=%d xOffset=%d yOffset=%d img_w=%d img_h=%d"  
          ,ch 
          ,glyph.width
          ,glyph.height
          ,glyph.xAdvance
          ,glyph.xOffset
          ,glyph.yOffset
          ,glyph.image_width
          ,glyph.image_height
          ));
*/
    }
  }


}
