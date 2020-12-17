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

import builder.parser.ParserException;
import builder.parser.Token;
import builder.parser.Tokenizer;

public abstract class FontTFT {

  /** The supported font types */
  static public  final String FONT_GFX               = "FONT_GFX"; 
  static public  final String FONT_GLCD              = "FONT_GLCD"; 
  static public  final String FONT_SIM               = "FONT_SIM"; 
  static public  final String FONT_TTF               = "FONT_TTF"; 
  static public  final String FONT_T3                = "FONT_T3"; 
//  static public  final String FONT_U8G2              = "FONT_U8G2"; 
  
  /** Text Alignment constants */
  static public  final String  ALIGN_LEFT            = "GSLC_ALIGN_MID_LEFT";
  static public  final String  ALIGN_CENTER          = "GSLC_ALIGN_MID_MID";
  static public  final String  ALIGN_RIGHT           = "GSLC_ALIGN_MID_RIGHT";
  static public  final String  ALIGN_TOP_LEFT        = "GSLC_ALIGN_TOP_LEFT";
  static public  final String  ALIGN_TOP_CENTER      = "GSLC_ALIGN_TOP_MID";
  static public  final String  ALIGN_TOP_RIGHT       = "GSLC_ALIGN_TOP_RIGHT";
  static public  final String  ALIGN_BOT_LEFT        = "GSLC_ALIGN_BOT_LEFT";
  static public  final String  ALIGN_BOT_CENTER      = "GSLC_ALIGN_BOT_MID";
  static public  final String  ALIGN_BOT_RIGHT       = "GSLC_ALIGN_BOT_RIGHT";

  /** Tokens for parsing */
  public static final int INTEGER                 = 1;
  public static final int HEX                     = 2;
  public static final int OCTAL                   = 3;
  public static final int WORD                    = 4;
  public static final int OPEN_SQBRACKET          = 5;
  public static final int CLOSE_SQBRACKET         = 6;
  public static final int OPEN_BRACE              = 7;
  public static final int CLOSE_BRACE             = 8;
  public static final int COMMA                   = 9;
  public static final int SEMICOLON               = 10;
  public static final int COMMENT_START           = 11;
  public static final int COMMENT_END             = 12;
  public static final int SPECIALCHAR             = 99;

  /** The Tokenizer. */
  protected static Tokenizer tokenizer = null;

  
  protected String fontType;
  protected FontItem item;

  public FontTFT() {
    fontType = null;
    item = null;
    
    if (tokenizer == null) {
      tokenizer = new Tokenizer();
      // Create our tokenizer for C File Image parsing
      tokenizer.defineIgnored("\\s+"); // Ignore whitespace
      tokenizer.defineIgnored("^#.*$|^\\/\\/.*$"); // Ignore comments (which start with # or //)
      tokenizer.add("0x[a-fA-F0-9]+", HEX);
      tokenizer.add("\\\\[0-9]+", OCTAL);
      tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*", WORD);
      tokenizer.add("-?[0-9]+", INTEGER);
//      tokenizer.add("\\,", COMMA);
      tokenizer.add("/\\*",COMMENT_START);
      tokenizer.add("\\*/",COMMENT_END);
      tokenizer.add("\\[", OPEN_SQBRACKET);
      tokenizer.add("\\{", OPEN_BRACE);
//      tokenizer.add("\\{", CLOSE_SQBRACKET);
      tokenizer.add("\\}", CLOSE_BRACE);
      tokenizer.add("\\;", SEMICOLON);
      tokenizer.add(".", SPECIALCHAR);
    }
  }

  /**
   * create
   * creates a new font
   * 
   * @param item The FontItem
   * @return true if successful
   * @throws FontException
   */
  public abstract boolean create(FontItem item) throws FontException;

  /**
   * canDisplay()
   * Checks if this Font has a glyph for the specified character.
   * @param codePoint
   * @return
   */
  public abstract boolean canDisplay(int codePoint);
  
  /**
   * setTextSize
   * 
   * Set text 'magnification' size.
   * Each increase in size makes 1 pixel that much bigger.
   * 
   * @param size  Desired text size. 1 is default 6x8, 2 is 12x16, 3 is 18x24, etc
   */
  public void setTextSize(int size) {
    // Must be overridden to support
  }

  /**
   * Gets the logical size.
   *
   * @return the logical size
   */
  public int getLogicalSizeAsInt() {
    return item.getLogicalSizeAsInt();
  }
  
  /**
   * Gets the logical style.
   *
   * @return the logical style
   */
  public String getLogicalStyle() {
    return item.getLogicalStyle();
  }
  
  /**
   * Gets the font name that is displayed to users
   * @return display name
   */
  public String getDisplayName() {
    return item.getDisplayName();
  }
  
  /**
   * Gets the font's family name
   * @return family name
   */
  public String getFamilyName() {
    return item.getFamilyName();
  }
  
  /**
   * Get the font item
   * @return font item
   */
  public FontItem getFontItem() {
    return item;
  }
  
  /**
   * Determine size of a string with current font/size. 
   * Pass string and a cursor position, returns UL corner and W,H.
   * 
   * @param g2d     The graphics context
   * @param str     The string to measure
   * @para  x       The current cursor X
   * @param y       The current cursor Y
   * @param bClippingEn Enable Clipping? true or false
   * @return  FontMetrics
   */
  public abstract FontMetrics getTextBounds(String str, int x, int y, boolean bClippingEn);

  /**
   * getCharSize()
   * @param ch
   * @return Dimension of character
   */
  public abstract Dimension getCharSize(char ch);

  /**
   * getMaxCharSize()
   * @return Maximum Dimension for a character
   */
  public abstract Dimension getMaxCharSize();

  /**
   * Draw a text string at the given coordinate
   *
   * WARNING: Newlines and Wrap are NOT supported.
   * 
   * @param g2d     The graphics context
   * @param r       Rectangle region to contain the text
   * @param str     String to display
   * @param colTxt  Color to draw text
   * @param colBg   Color of background
   * @param bClippingEn Enable Clipping? true or false
   */
  public abstract void drawString(Graphics2D g2d, Rectangle r, String str, Color colTxt, Color colBg, boolean bClippingEn);

  /**
   * Draw a text string image
   *
   * WARNING: Newlines and Wrap are NOT supported.
   * 
   * @param r       Rectangle region to contain the text
   * @param str     String to display
   * @param colTxt  Color to draw text
   * @param colBg   Color of background
   * @param bClippingEn Enable Clipping? true or false
   * @return Image  a rendered image given a string using this font.
   */
  public abstract BufferedImage drawImage(Rectangle r, String str, Color colTxt, Color colBg, boolean bClippingEn);

/**
 * getFontType
 * 
 * @return The type of font
 */
  public String getFontType() {
    return fontType;
  }
  
/**
 * getFileName
 * 
 * @return The file name used to load font, can be null
 */
    
  public  String getFileName() {
    return item.getFileName();
  }

  /**
   * Parses the error.
   *
   * @param t
   *          the t
   * @param s
   *          the s
   * @throws ParserException
   *           the parser exception
   */
  public void parseError(Token t, String s) throws ParserException {
    String msg = String.format("Missing '%s' found <%s> line: %d",s,t.getToken(),
        t.getLineNumber());
    throw new ParserException(msg);
  }

  
}
