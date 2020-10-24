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

  @Override
  public boolean create(String fileName, String fontName) throws FontException {
    this.fontFileName = fileName;
    this.fontName = fontName;
    this.fontType = FONT_GLCD;
    this.bWrap = true;
    this.textsize_x = 1;
    this.textsize_y = 1;
    return parseGlcdFont();
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
      copyChar(cursor_x, cursor_y, ch, colTxt, colBg, textsize_x, textsize_y);
      cursor_x += textsize_x * 6; // Advance x one char
    }
    g2d.drawImage(image, r.x, r.y, null);
//    Builder.logger.debug("minx=" + minx + " maxx=" + maxx + " miny=" + miny + " maxy=" + maxy);
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
  
    for (int i = 0; i < length; i++) {
      ch = s.charAt(i);
      // ignore newlines
      if (ch == '\n' || ch == '\r') {
        continue;
      }
      copyChar(cursor_x, cursor_y, ch, colTxt, colBg, textsize_x, textsize_y);
      cursor_x += textsize_x * 6; // Advance x one char
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

    x1 = x;
    tmpX = x;
    y1 = y;
    tmpY = y;
    int w  = 0;
    int h = 0;
    // clipping
    minx = Builder.CANVAS_WIDTH;
    miny = Builder.CANVAS_HEIGHT;
    maxx = -1;
    maxy = -1;
    
    for (int i=0; i<str.length(); i++) {
      ch = str.charAt(i);
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
    if (w > Builder.CANVAS_WIDTH) w = Builder.CANVAS_WIDTH;
    if (h > Builder.CANVAS_HEIGHT) h = Builder.CANVAS_HEIGHT;
    return new FontMetrics(x1,y1,w,h);
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
   */
  private void copyChar(int x, int y, int ch, Color colTxt, Color colBg, int size_x, int size_y) {

//    Builder.logger.debug("***copyChar [" + Integer.toHexString(ch) + "]");

    if ((x >= Builder.CANVAS_WIDTH) || // Clip right
        (y >= Builder.CANVAS_HEIGHT) || // Clip bottom
        ((x + 6 * size_x - 1) < 0) || // Clip left
        ((y + 8 * size_y - 1) < 0)) // Clip top
      return;

    for (int i = 0; i < 5; i++) { // Char bitmap = 5 columns
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
    if (x< 0 || x > strMetrics.w-1) {
      return;
    }
    if (y< 0 || y > strMetrics.h-1) {
      return;
    }
    raster.setPixel(x, y, new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
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
  private boolean parseGlcdFont() throws FontException {
    Token token = null;
    File file = new File(fontFileName);
    try {
      tokenizer.setSource(file);
//      Builder.logger.debug("Opened file: " + fileName);
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

  @Override
  public boolean create(String fontName, int dpi, int size, String style) {
    // TODO Auto-generated method stub
    return false;
  }

}
