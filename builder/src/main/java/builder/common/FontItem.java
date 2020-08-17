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
  
  /** The font scaling */
  private double scaleFactor;
  
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
    scaleFactor = (double)dpi / 72.0d;
    double size =  ((double)Double.parseDouble(logicalSize));
    size = size * scaleFactor;
    font = createFont();
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
    String p = platform.getName().toUpperCase();
    if (p.equals("ARDUINO")) {
      p ="AO";
    } else if (p.equals("M5STACK")) {
      p = "MK";
    } else if (p.equals("TEENSY")) {
      p = "TY";
    } else if (p.equals("TFT_ESPI")) {
      p = "TI";
    } else if (p.equals("LINUX")) {
      p = "LX";
    }
    name = name.replace('-', '_');
    nFontId = String.format("E_%s_%s", p,
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
   * This method creates a <code>Font</code> using the String values displayed to
   * users of GUIsliceBuider by our various Widget Models.
   *
   * @return font The java font we can use to display text
   * @see java.awt.Font
   * @see java.lang.String
   */
  public Font createFont() {
    int style;
    switch (logicalStyle) {
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
    return new Font(logicalName, style, Integer.parseInt(logicalSize));
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
  

  public double getScaleFactor() {
    return scaleFactor;
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
