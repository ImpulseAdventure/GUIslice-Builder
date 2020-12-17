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

import builder.Builder;

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
  
  static public String BOLD = "Bold";
  static public String ITALIC = "Italic";
  static public String PLAIN = "Regular";
  static public String BOLD_ITALIC = "Bold+Italic";

  /** The java font. */
  private transient FontTFT font;
  
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
  
  /** 
   * The external font file name 
   * Points within GUIsliceBuilder/fonts folder
   * (ex: fonts/gfx/FreeSans9pt7b.h). 
   */
  private String fileName;
  
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
     font = null;
     includeFile = "NULL";
     defineFile = "NULL";
     nFontId = "NULL";
     eFontRefType = "GSLC_FONTREF_PTR";
     pvFontRef = "NULL";
     nFontSz = "1";
     logicalName = "NULL";
     logicalSize = "NULL";
     logicalStyle = "NULL";
     fontRefMode = "NULL";
  }
  
  /**
   * Gets the font.
   *
   * @return the java <code>Font</code> object
   */
  public FontTFT getFont() {
    if (font == null) {
      createFont();
    }
    return font;
  }
  
  /**
   * getPlatform
   * @return platform
   */
  public FontPlatform getPlatform() {
    return platform;
  }
  
  /**
   * setPlatform
   */
  public void setPlatform(FontPlatform platform) {
    this.platform = platform;
  }
  
  /**
   * getCategory
   * @return category
   */
  public FontCategory getCategory() {
    return category;
  }
  
  /**
   * setCategory
   */
  public void setCategory(FontCategory category) {
    this.category = category;
  }
  
  /**
   * generateEnum
   */
  public void generateEnum() {
    int n = 0;
    String name = displayName;
    if (name.startsWith("BuiltIn")) {
      n = name.indexOf(">");
      name = "BUILTIN" + name.substring(n+1);
    }
    name = name.replace('-', '_');
    n = name.indexOf("pt7b");
    if (n > 0) name = name.substring(0,n);
    nFontId = String.format("E_%s", name.toUpperCase());
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
  public void createFont() {
    boolean ret = false;
    switch (getCategory().getName()) {
/* Reserve for future use
      case FontTFT.FONT_U8G2:
        font = new FontU8G2();
        ret = font.create(this);
        break;
*/
      case FontTFT.FONT_GFX:
        font = new FontGFX();
        ret = font.create(this);
        break;
      case FontTFT.FONT_GLCD:
        font = new FontGlcd();
        ret = font.create(this);
        if (getLogicalSizeAsInt() > 0)
          font.setTextSize(getLogicalSizeAsInt()/5);
        break;
      case FontTFT.FONT_SIM:
        font = new FontSim();
        ((FontSim)font).setDPI(platform.getDPI());
        ret = font.create(this);
        break;
      case FontTFT.FONT_T3:
        font = new FontT3();
        ret = font.create(this);
        break;
      case FontTFT.FONT_TTF:
        font = new FontTtf();
        ret = font.create(this);
        break;
      default:
        break;
    }
    if (!ret) {
      font = null;
      Builder.logger.error("Unable to create font: " + displayName);
    }
  }
  
  /**
   * Gets the font family name.
   *
   * @return the name
   */
  public String getFamilyName() {
    return familyName;
  }
  
  /**
   * setFamilyName
   * sets the font family name.
   */
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
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
   * setDisplayName
   * 
   * @param displayName
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  /**
   * Gets the external font file name.
   *
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }
  
  /**
   * setFileName
   * 
   * @param fileName
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
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
   * setIncludeFile
   * 
   * @param includeFile
   */
  public void setIncludeFile(String includeFile) {
    this.includeFile = includeFile;
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
   * setDefineFile
   * 
   * @param defineFile
   */
  public void setDefineFile(String defineFile) {
    this.defineFile = defineFile;
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
   * setFontRefType
   * 
   * @param eFontRefType
   */
  public void setFontRefType(String eFontRefType) {
    this.eFontRefType = eFontRefType;
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
   * setFontRef
   * 
   * @param pvFontRef
   */
  public void setFontRef(String pvFontRef) {
    this.pvFontRef = pvFontRef;
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
   * setFontSz
   * 
   * @param nFontSz
   */
  public void setFontSz(String nFontSz) {
    this.nFontSz = nFontSz;
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
   * setLogicalName
   * 
   * @param logicalName
   */
  public void setLogicalName(String logicalName) {
    this.logicalName = logicalName;
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
   * setLogicalSize
   * 
   * @param logicalSize
   */
  public void setLogicalSize(String logicalSize) {
    this.logicalSize = logicalSize;
  }
  
  /**
   * Gets the font size.
   *
   * @return the font size
   */
  public int getLogicalSizeAsInt() {
    return Integer.parseInt(logicalSize);
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
   * setLogicalStyle
   * 
   * @param logicalStyle
   */
  public void setLogicalStyle(String style) {
    switch (style) {
    case "BOLD":
      this.logicalStyle = BOLD;
      break;
    case "BOLD_ITALIC":
      this.logicalStyle = BOLD_ITALIC;
      break;
    case "ITALIC":
      this.logicalStyle = ITALIC;
      break;
    default:
      this.logicalStyle = PLAIN;
      break;
    }
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
   * setFontRefMode
   * @param fontRefMode
   */
  public void setFontRefMode(String fontRefMode) {
    this.fontRefMode = fontRefMode;
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
    return String.format("Platform: %s Font: %s size: %s style: %s include: %s", 
        platform.getName(), displayName, logicalSize, logicalStyle, includeFile);
  }
}
