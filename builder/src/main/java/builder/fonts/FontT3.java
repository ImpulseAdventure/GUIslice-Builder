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
import java.awt.Point;
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
  
  // sizing variables
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
   * canDisplay
   *
   * @see builder.fonts.FontTFT#canDisplay(int)
   */
  public boolean canDisplay(int codePoint) {
    if (codePoint == (int)'\n' || codePoint == (int)'\r') { // ignore newlines
      return false;
    }
    if (codePoint >= index1_first && codePoint <= index1_last) {
      Dimension chSz = charBounds((char)codePoint,false);
      if (chSz == null) return false;
      if (chSz.width <= 0 || chSz.height <=0) return false;
      return true;
    }
    if (!(index2_first == 0 && index2_last == 0)) {
      if (codePoint >= index2_first && codePoint <= index2_last) {
        Dimension chSz = charBounds((char)codePoint,false);
        if (chSz == null) return false;
        if (chSz.width <= 0 || chSz.height <=0) return false;
        return true;
      }
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

    //    Builder.logger.debug("drawString: " + s + " font: " + fontName + " " + strMetrics.toString());
    
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
      if (!copyChar(ch, colTxt, colBg, bClippingEn)) break;
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
  public BufferedImage drawImage(Rectangle r, String s, Color colTxt, Color colBg, boolean bClippingEn) {
    
  //  Builder.logger.debug("Enter drawTxt: [" + s + "]");
  
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

    //    Builder.logger.debug("drawImage: " + s + " font: " + fontName + " " + strMetrics.toString());
    
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
      if (!copyChar(ch, colTxt, colBg, bClippingEn)) break;
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

    cursor_x = 0;
    cursor_y = 0;

    int w  = 0;
    int h = 0;
    Dimension chSz;
    for (int i=0; i<str.length(); i++) {
      ch = str.charAt(i);
      if (ch == '\n' || ch == '\r') { // ignore newlines
        continue;
      }
      chSz = charBounds(ch,bClippingEn);  // modifies class variables for sizing
      if (chSz == null) 
        continue; //skip undefined characters
      if (chSz.height > h) h = chSz.height;
      w += chSz.width;
    }
    if (h >= 48) h++; // fudge factor for larger fonts
    
    // clipping
    if (bClippingEn) {
      if (w > Builder.CANVAS_WIDTH) w = Builder.CANVAS_WIDTH;
      if (h > Builder.CANVAS_HEIGHT) h = Builder.CANVAS_HEIGHT;
    }
    FontMetrics metrics = new FontMetrics(0,0,w,h);
//    Builder.logger.debug("getTextBounds: " + str + " " + metrics.toString());
    return metrics;
  }
 
  /**
   * Helper to determine size of a character with this font/size.
   * used by getTextBounds() function.
   * @param    ch    The character in question
   * @param bClippingEn 
   */
  private Dimension charBounds(char ch, boolean bClippingEn) {
//    Builder.logger.debug(String.format("charBounds [%c] hex=0x%02X", ch, (int)ch));
    int maxHeight = -1;
    int bitoffset;

    if (ch >= index1_first && ch <= index1_last) {
      bitoffset = ch - index1_first;
      bitoffset *= bits_index;
    } else if (ch >= index2_first && ch <= index2_last) {
      bitoffset = ch - index2_first + index1_last - index1_first + 1;
      bitoffset *= bits_index;
    } else {
      return null;
    }
//    Builder.logger.debug(String.format("bitoffset [%d]", bitoffset));
    int data_pos = fetchbits_unsigned(font_index, 0, bitoffset, bits_index);
//    Builder.logger.debug(String.format("data_pos [%d]", data_pos));

    int encoding = fetchbits_unsigned(font_data, data_pos, 0, 3);
    if (encoding != 0)
      return null;

    int width = fetchbits_unsigned(font_data, data_pos, 3, bits_width);
    bitoffset = bits_width + 3;
    int height = fetchbits_unsigned(font_data, data_pos, bitoffset, bits_height);
    bitoffset += bits_height;
    // Builder.logger.debug(String.format(" size = %d,%d", width, height));

    int xoffset = fetchbits_signed(font_data, data_pos, bitoffset, bits_xoffset);
//    Builder.logger.debug(String.format("ch[%c] xoffset=%d",ch,xoffset));
    bitoffset += bits_xoffset;
    int yoffset = fetchbits_signed(font_data, data_pos, bitoffset, bits_yoffset);
    bitoffset += bits_yoffset;
    // Builder.logger.debug(String.format(" offset = %d,%d\n", xoffset, yoffset));

    int delta = fetchbits_unsigned(font_data, data_pos, bitoffset, bits_delta);
    bitoffset += bits_delta;
    // Builder.logger.debug(String.format(" delta = %d\n", delta));

    // Builder.logger.debug(String.format(" cursor = %d,%d\n", cursor_x, cursor_y));

    // horizontally, we draw every pixel, or none at all
    if (cursor_x < 0) {
      cursor_x = 0;
    }
    int origin_x = cursor_x + xoffset;
    if (origin_x < 0) {
      cursor_x -= xoffset;
      origin_x = 0;
    }
    if (bClippingEn) {
      if (origin_x + width > Builder.CANVAS_WIDTH) {
        if (!bWrap) {
          return null;
        }
        origin_x = 0;
        if (xoffset >= 0) {
          cursor_x = 0;
        } else {
          cursor_x = -xoffset;
        }
        cursor_y += line_space;
      }

      if (cursor_y >= Builder.CANVAS_HEIGHT)
        return null;
    }
    cursor_x += delta;

    // vertically, the top and/or bottom can be clipped
    int origin_y = cursor_y + cap_height - height - yoffset;
    // Builder.logger.debug(String.format(" origin = %d,%d\n", origin_x, origin_y));

    // TODO: compute top skip and number of lines
    int linecount = height;
    int y = origin_y;
    Point p = null;
    if (linecount == 0) {
      maxHeight = cap_height;
    }
    while (linecount > 0) {
      int b = fetchbit(font_data, data_pos, bitoffset);
      bitoffset++;
      // Builder.logger.debug(String.format("linecount=%d b=%d
      // bitoffset=%d",linecount,b,bitoffset));
      if (b == 0) {
        // Builder.logger.debug(" single line");
        int x = 0;
        do {
          int xsize = width - x;
          if (xsize > 32)
            xsize = 32;
          int bits = fetchbits_unsigned(font_data, data_pos, bitoffset, xsize);
          p = sizeGlyph((char) ch, bits, xsize, origin_x + x, y, 1);
          if (p != null) {
            if (p.y > maxHeight) maxHeight = p.y;
          }
          bitoffset += xsize;
          x += xsize;
        } while (x < width);
        y++;
        linecount--;
      } else {
        int n = fetchbits_unsigned(font_data, data_pos, bitoffset, 3) + 2;
        bitoffset += 3;
        int x = 0;
        do {
          int xsize = width - x;
          if (xsize > 32)
            xsize = 32;
          // Builder.logger.debug(String.format(" multi line %d\n", n));
          int bits = fetchbits_unsigned(font_data, data_pos, bitoffset, xsize);
          p = sizeGlyph((char) ch, bits, xsize, origin_x + x, y, n);
          if (p != null) {
            if (p.y > maxHeight) maxHeight = p.y;
          }
          bitoffset += xsize;
          x += xsize;
        } while (x < width);
        y += n;
        linecount -= n;
      }
    }
    if (delta <= 0 || maxHeight <=0) 
      return null;
    Dimension chSz = new Dimension(delta,maxHeight+1);
    return chSz;
  }

  private Point sizeGlyph(char ch, int bits, int numbits, int x, int y, int repeat) {
//  Builder.logger.debug("sizeGlyph bits = 0x%04X",bits));
//  Builder.logger.debug(String.format("numbits=%d repeat=%d x=%d, y=%d",
//      numbits,repeat,x,y));
  int nZeroes;
  int minx = 32767;
  int miny = 32767;
  int maxx = -1;
  int maxy = -1;
  if (bits == 0) return null;
  bits <<= (32-numbits); // left align bits 
  do {
    nZeroes = Integer.numberOfLeadingZeros(bits); // skip over zeros
    if (nZeroes > numbits) nZeroes = numbits;
    numbits -= nZeroes;
    x += nZeroes;
    bits <<= nZeroes;
    bits = ~bits; // invert to count 1s as 0s
    nZeroes = Integer.numberOfLeadingZeros(bits);
    if (nZeroes > numbits) nZeroes = numbits; 
    numbits -= nZeroes;
    bits <<= nZeroes;
    bits = ~bits; // invert back to original polarity
    if (nZeroes > 0) {
      x += nZeroes;
      for (int i=x-nZeroes; i<=x-1; i++) {
        for (int j=y; j<=y+repeat-1; j++) {
          if (i > maxx) maxx = i;
          if (j > maxy) maxy = j;
          if (i < minx) minx = i;
          if (j > miny) miny = j;
//          Builder.logger.debug(String.format("Pixel: %d,%d", i,j));
        }
      }
    }
  } while (numbits > 0);
//  Builder.logger.debug(String.format("sizeGlyph: ch[%c] minx=%d maxx=%d miny=%d maxy=%d", 
//      ch, minx, maxx, miny, maxy));
  if (maxx <= 0 || maxy <=0) return null;
  Point p = new Point(maxx,maxy);
  return p;
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
  private boolean copyChar(int ch, Color colTxt, Color colBg, boolean bClippingEn) {

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
    if (encoding != 0) 
      return false;
    
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
    if (bClippingEn) {
      if (origin_x + width > Builder.CANVAS_WIDTH) {
        if (!bWrap) 
          return false;
        origin_x = 0;
        if (xoffset >= 0) {
          cursor_x = 0;
        } else {
          cursor_x = -xoffset;
        }
        cursor_y += line_space;
      }
      
      if (cursor_y >= Builder.CANVAS_HEIGHT) return false;
    }
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
          drawFontBits((char)ch, bits, xsize, origin_x + x, y, 1, colTxt);
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
          drawFontBits((char)ch, bits, xsize, origin_x + x, y, n, colTxt);
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
  
  private void drawFontBits(char ch, int bits, int numbits, int x, int y, int repeat, Color colTxt) {
//    Builder.logger.debug("bits = 0" + Integer.toHexString(bits));
//    Builder.logger.debug(String.format("numbits=%d repeat=%d x=%d, y=%d",
//        numbits,repeat,x,y));
    int nZeroes;
    if (bits == 0) return;
    bits <<= (32-numbits); // left align bits 
    do {
      nZeroes = Integer.numberOfLeadingZeros(bits); // skip over zeros
      if (nZeroes > numbits) nZeroes = numbits;
      numbits -= nZeroes;
      x += nZeroes;
      bits <<= nZeroes;
      bits = ~bits; // invert to count 1s as 0s
      nZeroes = Integer.numberOfLeadingZeros(bits);
      if (nZeroes > numbits) nZeroes = numbits; 
      numbits -= nZeroes;
      bits <<= nZeroes;
      bits = ~bits; // invert back to original polarity
      if (nZeroes > 0) {
        x += nZeroes;
//        setAddr(x-nZeroes, y, x-1, y+repeat-1); // write a block of pixels nZeroes x repeat sized
//        while (nZeroes-- > 1) { // draw line
//          writedata16_cont(textcolor);
//        }
//        writedata16_last(textcolor);
        for (int i=x-nZeroes; i<=x-1; i++) {
          for (int j=y; j<=y+repeat-1; j++) {
            writePixel(ch,i,j,colTxt);
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
  private void writePixel(char ch, int x, int y, Color col) {
    // our gate protection against crashes
    try {
    raster.setPixel(x, y, new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
    } catch(ArrayIndexOutOfBoundsException e) {
//      Builder.logger.debug(String.format("%s writePixel ch: %c exceeded: %d,%d", fontName,ch,x,y));
    }
  }

  private int fetchbit(byte[] p, int pos, int idx) throws ArrayIndexOutOfBoundsException {
    int i = pos + (idx >>> 3);
    return  Byte.toUnsignedInt(p[i]) & (0x80 >>> (idx & 7));
  }

  private int fetchbits_unsigned(byte[] p, int pos, int idx, int required)
    throws ArrayIndexOutOfBoundsException {
    long val;
    int i = pos + (idx >>> 3);
    val  = Byte.toUnsignedInt(p[i]) << 24 & 0x00000000FFFFFFFFL;
    val |= Byte.toUnsignedInt(p[i+1]) << 16 & 0x00000000FFFFFFFFL;
    val |= Byte.toUnsignedInt(p[i+2]) << 8 & 0x00000000FFFFFFFFL;
    val |= Byte.toUnsignedInt(p[i+3]) & 0x00000000FFFFFFFFL;
    val =  (val << (idx & 7)) & 0x00000000FFFFFFFFL; // shift out used bits
    if (Integer.compareUnsigned(32- (idx & 7), required) < 0) { // need to get more bits
      val = (val | (Byte.toUnsignedInt(p[i+4]) >>> (8 - (idx & 7)))) & 0xFFFFFFFFL; 
    }
    val = (val >>> (32-required)) & 0xFFFFFFFFL; // right align the bits
    return (int)val;
  }

  private int fetchbits_signed(byte[] p, int pos, int idx, int required) throws ArrayIndexOutOfBoundsException {
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
      /* Arg! Debugging the T3 code in C++ shows that they address beyond the data size
       * So if the data size is 808 bytes valid address are 0 to 807 but
       * they address 808 and 809 and maybe more.
       * C++ is giving back 0's in this case.  Why no crashes? I have no idea.
       * Addressing this simply requires adding a couple of zeroes to our data.
       */
      for (int i=0; i<4; i++) {
        n = 0;
        byteList.add(n);
      }
//    Builder.logger.debug(String.format("%s data bytes: %d",fontName, byteList.size()));
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
      /* Arg! Debugging the T3 code in C++ shows that they address beyond the index size
       * So if the index size is 119 bytes they address 119 and 120 and maybe more
       * C++ is giving back 0's in this case.  why no crashes? I have no idea.
       * Addressing this simply requires adding a couple of zeroes to out index.
       */
      for (int i=0; i<4; i++) {
        n = 0;
        byteList.add(n);
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
