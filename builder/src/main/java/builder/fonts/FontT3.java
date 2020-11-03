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

public class FontT3 extends FontTFT {
  
  // Font Parameters
  private int logicalSize;
  private String logicalStyle;
  
  // Font variables
  private byte[] font_index; 
  private byte[] font_data;

  private int index1_first;
  private int index1_last;
  private int index2_first;
  private int index2_last;
  private int bits_index;
  private int bits_width;
  private int bits_height;
  private int bits_xoffset;
  private int bits_yoffset;
  private int bits_delta;
  private int line_space;
  private int cap_height;
  
  private boolean bWrap = false;
  
  // text drawing variables
  WritableRaster raster;
  private int cursor_x;
  private int cursor_y;
  FontMetrics strMetrics;
  
  public FontT3() {
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
    this.fontType = FONT_T3;
    return parseT3Font();
  }

  /**
   * Draw a text string at the given coordinate into an image
   *
   * WARNING: Newlines and Wrap are NOT supported.
   * 
   * @param g2d     The graphics context
   * @param r       Rectangle region to contain the text
   * @param str     String to display
   * @param colTxt  Color to draw text
   * @param colBg   Color of background
   * @return Image  a rendered image given a string using this font.
   */
  @Override
  public void drawString(Graphics2D g2d, Rectangle r, String s, Color colTxt, Color colBg) {
    
//    Builder.logger.debug("Enter drawTxt: [" + s + "]");

    int ch;
    int length = s.length();
    strMetrics = getTextBounds(s,0,0);

    // clipping
    if (strMetrics.w+r.x > Builder.CANVAS_WIDTH) {
      strMetrics.w = strMetrics.w - (strMetrics.w + r.x - Builder.CANVAS_WIDTH);
    }
    if (strMetrics.h+r.y > Builder.CANVAS_HEIGHT) {
      strMetrics.h = strMetrics.h - (strMetrics.h - r.y - Builder.CANVAS_HEIGHT);
    }

//    Builder.logger.debug("Metrics x1=" + strMetrics.x1 + " y1=" + strMetrics.y1
//        + " w=" + strMetrics.w + " h=" + strMetrics.h);
    
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
    cursor_x = 0;
    cursor_y = 0;

    for (int i = 0; i < length; i++) {
      ch = s.charAt(i);
      // ignore newlines
      if (ch == '\n' || ch == '\r') {
        continue;
      }
      if (!copyChar(ch, colTxt, colBg)) break;
    }

    g2d.drawImage(image, r.x, r.y, null);
  }

  /**
   * 
   * drawImage
   *
   * @see builder.fonts.FontTFT#drawImage(java.awt.Graphics2D, java.lang.String, java.awt.Color, java.awt.Color)
   */
  @Override
  public BufferedImage drawImage(Rectangle r, String s, Color colTxt, Color colBg) {
    
  //  Builder.logger.debug("Enter drawTxt: [" + s + "]");
  
    int ch;
    int length = s.length();
    strMetrics = getTextBounds(s,0,0);
  
  //  Builder.logger.debug("Metrics x1=" + strMetrics.x1 + " y1=" + strMetrics.y1
  //      + " w=" + strMetrics.w + " h=" + strMetrics.h);
    
    // create our image
    BufferedImage image = new BufferedImage(strMetrics.w, strMetrics.h, BufferedImage.TYPE_INT_ARGB );
    raster = image.getRaster();
    
  //  Builder.logger.debug("width=" + image.getWidth() + " height=" + image.getHeight());
  
    // create a transparent background
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        raster.setPixel(i, j, new int[] { 0, 0, 0, 0 });
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
      if (!copyChar(ch, colTxt, colBg)) break;
    }
    return image;
  }

  /**
   * Determine size of a string with current font/size. 
   * Pass string and a cursor position, returns UL corner and W,H.
   * 
   * @param str     The string to measure
   * @param x       The current cursor X
   * @param y       The current cursor Y
   * @return  FontMetrics
   */
  @Override
  public FontMetrics getTextBounds(String str, int x, int y) {
    char ch; // Current character

    int w  = 0;
    int h = 0;
    Dimension chSz;
    for (int i=0; i<str.length(); i++) {
      ch = str.charAt(i);
      if (ch == '\n' || ch == '\r') { // ignore newlines
        continue;
      }
      chSz = charBounds(ch);  // modifies class variables for sizing
      if (chSz == null) continue; //skip undefined chacters
      if (chSz.height > h) h = chSz.height;
      w += chSz.width;
    }
    if (h >= 48) h++; // fudge factor for larger fonts
    
    // clipping
    if (w > Builder.CANVAS_WIDTH) w = Builder.CANVAS_WIDTH;
    if (h > Builder.CANVAS_HEIGHT) h = Builder.CANVAS_HEIGHT;
    return new FontMetrics(0,0,w,h);
  }
 
  /**
   * Helper to determine size of a character with this font/size.
   * used by getTextBounds() function.
   * @param    ch    The character in question
   */
  private Dimension charBounds(char ch) {
    int w = 0;
    int h = cap_height;
    // Ported from Paul Stoffregen's ILI9341_t3.cpp
    
    // Treat non-breaking space as normal space
    // And Yes, I have no idea what a non-breaking space means...
    int c = (int)ch;
    if (c == 0xa0) {
      c = ' ';
    }

    int bitoffset;
    
    if (c >= index1_first && c <= index1_last) {
      bitoffset = c - index1_first;
      bitoffset *= bits_index;
    } else if (c >= index2_first && c <= index2_last) {
      bitoffset = c - index2_first + index1_last - index1_first + 1;
      bitoffset *= bits_index;
    } else {
      return null;
    }

    int data_pos = fetchbits_unsigned(font_index, 0, bitoffset, bits_index);

    int encoding = fetchbits_unsigned(font_data, data_pos, 0, 3);

    if (encoding != 0) return null;
    
//    int width = fetchbits_unsigned(font_data, data_pos, 3, bits_width);
    bitoffset = bits_width + 3;

//    int height = fetchbits_unsigned(font_data, data_pos, bitoffset, bits_height);
    bitoffset += bits_height;

//    int xoffset = fetchbits_signed(font_data, data_pos, bitoffset, bits_xoffset);
    bitoffset += bits_xoffset;

//    int yoffset = fetchbits_signed(font_data, data_pos, bitoffset, bits_yoffset);
    bitoffset += bits_yoffset;

    int delta = fetchbits_unsigned(font_data, data_pos, bitoffset, bits_delta);
    w = (int)delta;
    if (w == 0 || h == 0) return null;
    return new Dimension(w,h);
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
  private boolean copyChar(int ch, Color colTxt, Color colBg) {

//    Builder.logger.debug("***copyChar [" + Integer.toHexString(ch) + "]");
    int bitoffset;
    //short[] subset;

    //Serial.printf("drawFontChar %d\n", c);

    if (ch >= index1_first && ch <= index1_last) {
      bitoffset = ch - index1_first;
      bitoffset *= bits_index;
    } else if (ch >= index2_first && ch <= index2_last) {
      bitoffset = ch - index2_first + index1_last - index1_first + 1;
      bitoffset *= bits_index;
    } else {
      return true;
    }
    int data_pos = fetchbits_unsigned(font_index,0,bitoffset, bits_index);

    int encoding = fetchbits_unsigned(font_data, data_pos, 0, 3);
    if (encoding != 0) return false;
    
    int width = fetchbits_unsigned(font_data, data_pos, 3, bits_width);
    bitoffset = bits_width + 3;
    int height = fetchbits_unsigned(font_data, data_pos, bitoffset, bits_height);
    bitoffset += bits_height;
//    Builder.logger.debug(String.format("  size =   %d,%d", width, height));

    int xoffset = fetchbits_signed(font_data, data_pos, bitoffset, bits_xoffset);
    bitoffset += bits_xoffset;
    int yoffset = fetchbits_signed(font_data, data_pos, bitoffset, bits_yoffset);
    bitoffset += bits_yoffset;
//    Builder.logger.debug(String.format("  offset = %d,%d\n", xoffset, yoffset));

    int delta = fetchbits_unsigned(font_data, data_pos, bitoffset, bits_delta);
    bitoffset += bits_delta;
//    Builder.logger.debug(String.format("  delta =  %d\n", delta));

//    Builder.logger.debug(String.format("  cursor = %d,%d\n", cursor_x, cursor_y));

    // horizontally, we draw every pixel, or none at all
    if (cursor_x < 0) cursor_x = 0;
    int origin_x = cursor_x + xoffset;
    if (origin_x < 0) {
      cursor_x -= xoffset;
      origin_x = 0;
    }
    if (origin_x + width > Builder.CANVAS_WIDTH) {
      if (!bWrap) return false;
      origin_x = 0;
      if (xoffset >= 0) {
        cursor_x = 0;
      } else {
        cursor_x = -xoffset;
      }
      cursor_y += line_space;
    }
    if (cursor_y >= Builder.CANVAS_HEIGHT) return false;
    cursor_x += delta;

    // vertically, the top and/or bottom can be clipped
    int origin_y = cursor_y + cap_height - height - yoffset;
//    Builder.logger.debug(String.format("  origin = %d,%d\n", origin_x, origin_y));

    // TODO: compute top skip and number of lines
    int linecount = height;
//    int loopcount = 0;
    int y = origin_y;
    while (linecount > 0) {
      int b = fetchbit(font_data, data_pos, bitoffset);
      bitoffset++;
//      Builder.logger.debug(String.format("linecount=%d b=%d bitoffset=%d",linecount,b,bitoffset));
      if (b == 0) {
//        Builder.logger.debug("    single line");
        int x = 0;
        do {
          int xsize = width - x;
          if (xsize > 32) xsize = 32;
          int bits = fetchbits_unsigned(font_data, data_pos, bitoffset, xsize);
          drawFontBits(bits, xsize, origin_x + x, y, 1, colTxt);
          bitoffset += xsize;
          x += xsize;
        } while (x < width);
        y++;
        linecount--;
      } else {
        int  n = fetchbits_unsigned(font_data, data_pos, bitoffset, 3) + 2;
        bitoffset += 3;
        int x = 0;
        do {
          int xsize = width - x;
          if (xsize > 32) xsize = 32;
//          Builder.logger.debug(String.format("    multi line %d\n", n));
          int bits = fetchbits_unsigned(font_data, data_pos, bitoffset, xsize);
          drawFontBits(bits, xsize, origin_x + x, y, n, colTxt);
          bitoffset += xsize;
          x += xsize;
        } while (x < width);
        y += n;
        linecount -= n;
      }
//      if (++loopcount > 100) {
//        Builder.logger.debug(String.format("     abort draw loop"));
//        break;
//      }
    }
    return true;
  }
  
  private void drawFontBits(int bits, int numbits, int x, int y, int repeat, Color colTxt) {
//    Builder.logger.debug("bits = 0" + Integer.toHexString(bits));
//    Builder.logger.debug(String.format("numbits=%d repeat=%d x=%d, y=%d",
//        numbits,repeat,x,y));
    int w;
    if (bits == 0) return;
    bits <<= (32-numbits); // left align bits 
    do {
      w = Integer.numberOfLeadingZeros(bits); // skip over zeros
      if (w > numbits) w = numbits;
      numbits -= w;
      x += w;
      bits <<= w;
      bits = ~bits; // invert to count 1s as 0s
      w = Integer.numberOfLeadingZeros(bits);
      if (w > numbits) w = numbits; 
      numbits -= w;
      bits <<= w;
      bits = ~bits; // invert back to original polarity
      if (w > 0) {
        x += w;
//        setAddr(x-w, y, x-1, y+repeat-1); // write a block of pixels w x repeat sized
//        while (w-- > 1) { // draw line
//          writedata16_cont(textcolor);
//        }
//        writedata16_last(textcolor);
        for (int i=x-w; i<=x-1; i++) {
          for (int j=y; j<=y+repeat-1; j++) {
            writePixel(i,j,colTxt);
          }
        }
      }
    } while (numbits > 0);
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
    if (x< 0 || x > strMetrics.w-1) {
      return;
    }
    if (y< 0 || y > strMetrics.h-1) {
      return;
    }
    raster.setPixel(x, y, new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
  }

  private int fetchbit(byte[] p, int pos, int idx) {
    int i = pos + (idx >>> 3);
    return  Byte.toUnsignedInt(p[i]) & (0x80 >>> (idx & 7));
  }

  private int fetchbits_unsigned(byte[] p, int pos, int idx, int required) {
    long val;
    int i = pos + (idx >>> 3);
    
    val  = Byte.toUnsignedInt(p[i]) << 24;
    val |= Byte.toUnsignedInt(p[i+1]) << 16;
    val |= Byte.toUnsignedInt(p[i+2]) << 8;
    val |= Byte.toUnsignedInt(p[i+3]);
    val =  (val << (idx & 7)) & 0x00000000FFFFFFFFL; // shift out used bits
    if (Integer.compareUnsigned(32- (idx & 7), required) < 0) { // need to get more bits
      val = (val | (Byte.toUnsignedInt(p[i+4]) >>> (8 - (idx & 7)))) & 0xFFFFFFFFL; 
    }
    val = (val >>> (32-required)) & 0xFFFFFFFFL; // right align the bits
    return (int)val;
  }

  private int fetchbits_signed(byte[] p, int pos, int idx, int required) {
    int val = fetchbits_unsigned(p, pos, idx, required);
    long tmp = Integer.toUnsignedLong(val & (1 << (required - 1)));
    if (tmp > 0) {
      return (val - (1 << required)) & 0xFFFFFFFF;
    }
    return val;
  }

  /**
   * Parse the glcdfont.c file
   * @return true if successful
   * @throws FontException
   */
  private boolean parseT3Font() throws FontException {
    Token token = null;
    File file = new File(fontFileName);
    try {
      tokenizer.setSource(file);
//    Builder.logger.debug("Opened file: " + fileName);
      //----------------------------------------------------------------------------------------
      // Find and load the T3 font data
      //----------------------------------------------------------------------------------------
      boolean bFound = false;
      String fontData = fontName + "_data";
      while ((token = tokenizer.nextToken()).getType() != 0 /*EOF*/) {
        if (token.getType() == WORD &&
            token.getToken().equals(fontData)) {
          bFound = true;
          break;
        }
      }
      if (!bFound) parseError(token, "font data [" + fontData + "] not found");
      /* Read our bitmap using array list of class Short.
       * We can't use Byte here because Java doesn't have ubyte.
       * T3 fonts don't list the number of bytes involved in the array 
       * so we have to waste memory using class Short instead
       * of something like: byte[] bytes = new byte[size];
       */
      ArrayList<Short> byteList = new ArrayList<Short>();
      Short  n;
      while ((token = tokenizer.nextToken()).getType() != CLOSE_BRACE ) {
        if (token.getType() == COMMENT_START) {
          do { 
            token = tokenizer.nextToken();
            if (token.getType() == 0) {
              parseError(token, "font data [" + fontData + "] comment didn't end");
            }
          } while (token.getType() != COMMENT_END);
        }
        if (token.getType() == HEX) {
          n = new Short(Integer.decode(token.getToken()).shortValue());
          byteList.add(n);
        }
      }
//    Builder.logger.debug("data bytes: " + byteList.size());
      font_data = new byte[byteList.size()];
      n = 0;
      for (Short b : byteList) {
        font_data[n++] = (byte) (b.byteValue() & 0xFF);
      }
  
      //----------------------------------------------------------------------------------------
      // Find and load the T3 font index
      //----------------------------------------------------------------------------------------
      String fontIndex = fontName + "_index";
      bFound = false;
      while ((token = tokenizer.nextToken()).getType() != 0) {
        if (token.getType() == WORD &&
            token.getToken().equals(fontIndex)) {
          bFound = true;
          break;
        }
      }
      if (!bFound) parseError(token, "font index [" + fontIndex + "] not found");
      byteList.clear();
      while ((token = tokenizer.nextToken()).getType() != CLOSE_BRACE) {
        if (token.getType() == HEX) {
          n = new Short(Integer.decode(token.getToken()).shortValue());
          byteList.add(n);
        }
      }
//    Builder.logger.debug("index bytes: " + byteList.size());
      font_index = new byte[byteList.size()];
      n = 0;
      for (Short b : byteList) {
        font_index[n++] = (byte) (b.shortValue() & 0x00FF);
      }
      byteList.clear();
      byteList = null;
  
      //----------------------------------------------------------------------------------------
      // Find T3 glyph data
      //----------------------------------------------------------------------------------------
      bFound = false;
      while ((token = tokenizer.nextToken()).getType() != 0) {
        if (token.getType() == WORD &&
            token.getToken().equals(fontName)) {
          bFound = true;
          break;
        }
      }
      if (!bFound) parseError(token, "font name [" + fontName + "] not found");
      // scan past font index name   
      while ((token = tokenizer.nextToken()).getType() != 0) {
        if (token.getType() == WORD &&
            token.getToken().equals(fontIndex)) {
          bFound = true;
          break;
        }
      }
      if (!bFound) parseError(token, "inside " + fontName + " array [" + fontIndex + "] not found");
  
      // get version number
      token = tokenizer.nextToken(); // comma
      if (token.getType() != SPECIALCHAR) parseError(token, "inside " + fontName + " array missing comma after " + fontIndex);
      token = tokenizer.nextToken(); // unicode
      if (token.getType() != INTEGER) parseError(token, "inside " + fontName + " missing version");
      int unicode = Integer.parseInt(token.getToken());
      if (unicode != 0) parseError(token, "inside " + fontName + " unicode not 0 so its unsupported");

      token = tokenizer.nextToken(); // comma
      if (token.getType() != SPECIALCHAR) parseError(token, "inside " + fontName + " array missing comma after version");
      token = tokenizer.nextToken(); // font data name
      if (token.getType() != WORD) parseError(token, "inside " + fontName + " array missing font data name");
      if (!token.getToken().equals(fontData)) parseError(token, "inside " + fontName + " array font data name error");

      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // version
      if (token.getType() != INTEGER) parseError(token, "inside " + fontName + " missing version");
      int version = Integer.parseInt(token.getToken());
      if (version != 1) parseError(token, "inside " + fontName + " version not 1 so its unsupported");
      token = tokenizer.nextToken(); // comma
      if (token.getType() != SPECIALCHAR) parseError(token, "inside " + fontName + " array missing comma after version");
      token = tokenizer.nextToken(); // reserved
      @SuppressWarnings("unused")
      int reserved = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // index1_first
      index1_first = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // index1_last
      index1_last = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // index2_first
      index2_first = Integer.parseInt(token.getToken());

      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // index2_last
      index2_last = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // bits_index
      bits_index = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // bits_width
      bits_width = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // bits_height
      bits_height = Integer.parseInt(token.getToken());

      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // bits_xoffset
      bits_xoffset = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // bits_yoffset
      bits_yoffset = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // bits_delta
      bits_delta = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // line_space
      line_space = Integer.parseInt(token.getToken());
      token = tokenizer.nextToken(); // comma
      token = tokenizer.nextToken(); // cap_height
      cap_height = Integer.parseInt(token.getToken());
/*
      Builder.logger.debug("version: " + version);
      Builder.logger.debug("reserved: " + reserved);
      Builder.logger.debug("index1_first: " + index1_first);
      Builder.logger.debug("index1_last: " + index1_last);
      Builder.logger.debug("index2_first: " + index2_first);
      Builder.logger.debug("index2_last: " + index2_last);
      Builder.logger.debug("bits_index: " + bits_index);
      Builder.logger.debug("bits_width: " + bits_width);
      Builder.logger.debug("bits_height: " + bits_height);
      Builder.logger.debug("bits_xoffset: " + bits_xoffset);
      Builder.logger.debug("bits_yoffset: " + bits_yoffset);
      Builder.logger.debug("bits_delta: " + bits_delta);
      Builder.logger.debug("line_space: " + line_space);
      Builder.logger.debug("cap_height: " + cap_height);
*/
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
