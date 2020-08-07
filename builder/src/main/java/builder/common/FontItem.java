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
package builder.common;

import java.awt.Font;

/**
 * The Class FontItem is a representation of a single GUIslice Library font.
 * 
 * Filled in by JSON deserialization inside FontFactory.
 * 
 * However, JSON will ignore fields marked as transient and
 * these are filled in by walking the top level container
 * BuilderFonts.  This is done inside FontFactory after doing
 * the JSON deserialization. The transient fields are back pointers
 * and act as an optimization to avoid having to repeatedly 
 * walk our containers. 
 * 
 * @author Paul Conti
 * 
 */
public class FontItem {
  
  /** The java font. */
  private transient Font font;
  
  /** The parent platform */
  private transient FontPlatform platform;
  
  /** The parent category */
  private transient FontCategory category;
  
  /** The key as index into our font list */
  private transient String key;
  
  /** The font family name. */
  private String familyName;
  
  /** The display name. */
  private String displayName;
  
  /** The include file (ex: Fonts/FreeSans9pt7b.h). */
  private String includeFile;
  
  /** The define file (ex: /usr/share/fonts/truetype/droid/DroidSans.ttf). */
  private String defineFile;
  
  /** The nFontId parameter used by GUIslice API. */
  private transient String nFontId;
  
  /** The e eFontRefType parameter used by GUIslice API. */
  private String eFontRefType;
  
  /** The pvFontRef parameter used by GUIslice API. */
  private String pvFontRef;
  
  /** The nFontSz parameter used by GUIslice API. */
  private String nFontSz;
  
  /** The java font name. */
  private String logicalName;
  
  /** The java font size. */
  private String logicalSize;
  
  /** The java font style. */
  private String logicalStyle;
  
  /** The Font Ref Mode - optional font set mode API used for built-in fonts */
  private String fontRefMode;
  
  /**
   * Instantiates a new font item.
   * Handled by google's gson now
   */
  public FontItem() {

  }
  
  /**
   * Gets the font.
   *
   * @return the java <code>Font</code> object
   */
  public Font getFont() {
    return font;
  }
  
  /**
   * setFont
   * We have to take into account the target display screen's DPI.
   * Adafruits's 2.8 screen is about DPI of 141 and GFX fonts are
   * hardcoded to this number. 
   * Fonts are in Points with 72 points per inch so DPI / 72 is our scaling factor.
   * @param dpi
   */
  public void setFont(int dpi) {
    double scaleFactor = (double)dpi / 72.0d;
    double size =  ((double)Double.parseDouble(logicalSize));
    size = size * scaleFactor;
    font = createFont(familyName, logicalName, logicalSize, logicalStyle);
    font = font.deriveFont((float) size);
  }
  
  /**
   * setPlatform
   * @param platform
   */
  public void setPlatform(FontPlatform platform) {
    this.platform = platform;
  }
  
  /**
   * getPlatform
   * @return platform
   */
  public FontPlatform getPlatform() {
    return platform;
  }
  
  /**
   * setCategory
   * @param category
   */
  public void setCategory(FontCategory category) {
    this.category = category;
  }

  /**
   * getCategory
   * @return category
   */
  public FontCategory getCategory() {
    return category;
  }
  
  /**
   * generateEnum
   */
  public void generateEnum() {
    int n = 0;
    String name = displayName;
    if (name.startsWith("BuiltIn")) {
      n = name.indexOf(">");
      name = name.substring(n+1);
    }
    name = name.replace('-', '_');
    nFontId = String.format("E_%s_%s", platform.getName().toUpperCase(),
        name.toUpperCase());
  }
  
  /**
   * generateKey
   */
  public void generateKey() {
    key = platform.getName() + "_" + displayName;
  }
  
  /**
   * getKey
   * @return
   */
  public String getKey() {
    return key;
  }
  
  /**
   * Gets the font with a temporary style change
   *
   * @param style
   *          the style
   * @return the java <code>Font</code> object
   */
  public Font getStyledFont(String style) {
    /* Here I use a scaled font because this routine is used on our
     * TFT Simulation to display text like: "TODO", "99999", etc.
     * The Style is most often Italic.
     */
    double size =  ((double)Double.parseDouble(logicalSize));
    size = size * 1.958333d;
    font = createFont(familyName, logicalName, logicalSize, style);
    font = font.deriveFont((float) size);
    return font;
  }
  
  /**
   * This method creates a <code>Font</code> using the default values
   */
  public Font createFont() {
    /* Note that here I don't use scaling, this is because this routine
     * is used to size text on "real" TFT display.  Not our Simulation.
     */
    return createFont(familyName, logicalName, logicalSize, logicalStyle);
  }

  /**
   * This method creates a <code>Font</code> using the String values displayed to
   * users of GUIsliceBuider by our various Widget Models.
   *
   * @param familyName
   *          - is the windows or linux font name.
   * @param logicalName
   *          - is the java built-in font name.
   * @param fontSize
   *          - is the point size of our font as a String value.
   * @param fontStyle
   *          - is the font style "PLAIN", "BOLD", "ITALIC", or "BOLD+ITALIC".
   * @return font The java font we can use to display text
   * @see java.awt.Font
   * @see java.lang.String
   */
  public Font createFont(String familyName, String logicalName, String fontSize, String fontStyle) {
    Font font;
    int style;
    switch (fontStyle) {
    case "BOLD":
      style = Font.BOLD;
      break;
    case "ITALIC":
      style = Font.ITALIC;
      break;
    case "BOLD+ITALIC":
      style = Font.BOLD + Font.ITALIC;
      break;
    default:
      style = Font.PLAIN;
      break;
    }
    try {
      // First try to use the "real" request font, if its installed
      font = new Font(familyName, style, Integer.parseInt(fontSize));
    }catch(NullPointerException e) {
      // Otherwise, use one of the Java built-in fonts
      font = new Font(logicalName, style, Integer.parseInt(fontSize));
    }
    return font;
  }
  
  /**
   * Sets the font.
   *
   * @param font
   *          the new java <code>Font</code>
   */
  public void setFont(Font font) {
    this.font = font;
  }
  
  /**
   * Gets the font family name.
   *
   * @return the name
   */
  public String getName() {
    return familyName;
  }
  
  /**
   * Gets the display name.
   *
   * @return the display name
   */
  public String getDisplayName() {
    return displayName;
  }
  
  /**
   * Gets the include file.
   *
   * @return the include file
   */
  public String getIncludeFile() {
    return includeFile;
  }
  
  /**
   * Gets the define file.
   *
   * @return the define file
   */
  public String getDefineFile() {
    return defineFile;
  }
  
  /**
   * Gets the font id.
   *
   * @return the nFontId
   */
  public String getFontId() {
    return nFontId;
  }
  
  /**
   * Gets the font ref type.
   *
   * @return the eFontRefType
   */
  public String getFontRefType() {
    return eFontRefType;
  }

  /**
   * Gets the font ref.
   *
   * @return the font ref
   */
  public String getFontRef() {
    return pvFontRef;
  }

  /**
   * Gets the font sz.
   *
   * @return the nFontSz
   */
  public String getFontSz() {
    return nFontSz;
  }
  
  /**
   * Gets the logical name.
   *
   * @return the logical name
   */
  public String getLogicalName() {
    return logicalName;
  }
  
  /**
   * Gets the logical size.
   *
   * @return the logical size
   */
  public String getLogicalSize() {
    return logicalSize;
  }
  
  /**
   * Gets the logical style.
   *
   * @return the logical style
   */
  public String getLogicalStyle() {
    return logicalStyle;
  }
  
  /**
   * Gets the fontRefMode.
   *
   * @return the fontRefMode
   */
  public String getFontRefMode() {
    return fontRefMode;
  }
  

  /**
   * toString
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("Font: %s size: %s style: %s", displayName, logicalSize, logicalStyle);
  }
}
