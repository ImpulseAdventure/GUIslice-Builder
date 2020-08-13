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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import builder.Builder;
import builder.codegen.CodeGenException;
import builder.controller.Controller;
import builder.models.TextModel;

/**
 * A factory for creating and managing GUIslice Library Font objects
 * and mapping them to and from something Java can understand.
 * 
 * @author Paul Conti
 * 
 */
public class FontFactory {
  
  /** The instance. */
  private static FontFactory instance = null;
  
  /** The Constant FONT_TEMPLATE. */
  public  final static String FONT_TEMPLATE   = "builder_fonts.json";
  
  /** top level object for all fonts. */
  public BuilderFonts builderFonts = new BuilderFonts();

  /** The full font item list. */
  private List<FontItem> platformFonts = new ArrayList<FontItem>();
  
  /** The font map used as index into font item list. */
  private HashMap<String, Integer> fontMap = new HashMap<String, Integer>(128);
  
  /** The number of platforms */
  private int nPlatforms;
  
  /** Platform names */
  private String[] platformNames;
  
  /** The list of templates. */
  List<FontItem>[] fontsByPlatform = null;

  /**
   * Gets the single instance of FontFactory.
   *
   * @return single instance of FontFactory
   */
  public static synchronized FontFactory getInstance() {
    if (instance == null) {
      instance = new FontFactory();
    }
    return instance;
  }

  /**
   * Instantiates a new font factory.
   */
  public FontFactory() {
  }

  /**
   * Initialize our fonts by reading in our json file
   * and building up our list and map of fonts.
   */
  public void init() {
    
    String fullPath = CommonUtils.getInstance().getWorkingDir();
    String csvFile = fullPath + "templates" + System.getProperty("file.separator") 
        + FONT_TEMPLATE;
    readFonts(csvFile);
    Builder.logger.debug("FontFactory Initialized");
  }
  
  /**
   * getBuilderFonts - grab the top level font container 
   * @return builderFonts 
   */
  public BuilderFonts getBuilderFonts() {
    return builderFonts;
  }
  
  /**
   * Gets the font list.
   *
   * @return the font list
   */
  public List<FontItem> getFontList() {
    String target = Controller.getTargetPlatform();
    for (int i=0; i<nPlatforms; i++) {
      if (platformNames[i].equals(target)) {
        return fontsByPlatform[i];
      }
    }
    return null;
  }
  
  /**
   * This method gets a Font's item that matches a key 
   * of the font's DispayName or first in list.
   *
   * @param key
   *          - is the GUIslice font  displayname not the real java font name.
   * @return font item 
   */
  public FontItem getFontItem(String key) {
    Integer idx = Integer.valueOf(0);  // always return something...
    String target = Controller.getTargetPlatform();
    key = target + "_" + key;
    if (fontMap.containsKey(key)) {
      idx = fontMap.get(key);
      return platformFonts.get(idx.intValue());
    } else {
      return null;
    }
  }
  
  /**
   * Gets the font.
   *
   * @param key
   *          the key
   * @return the font
   */
  public Font getFont(String key) {
    FontItem item = getFontItem(key);
    if (item != null) {
      return item.getFont();
    } 
    return null;
  }
  
  /**
   * Gets the font with a temporary style change
   *
   * @param key
   *          the key
   * @param style
   *          the style
   * @return the java <code>Font</code> object
   */
  public Font getStyledFont(String key, String style) {
    FontItem item = getFontItem(key);
    if (item != null) {
      return item.getStyledFont(style);
    } else {
      return null;
    }
  }
  
  /**
   * Gets the name of the default font for the target platform.
   *
   * @return the font name
   */
  public String getDefFontName() {
    List<FontItem> list = getFontList();
    return list.get(0).getDisplayName();
  }
  
  /**
   * Gets the font enum.
   *
   * @param key
   *          the key (display name)
   * @return the font enum
   */
  public String getFontEnum(String key) {
    FontItem item = getFontItem(key);
    return item.getFontId();
  }
  
  /**
   * Gets the font display name.
   *
   * @param fontEnum
   *          the font enum
   * @return the font display name or null on failure
   */
  public String getFontDisplayName(String fontEnum) {
    String name = null;
    List<FontItem> list = getFontList();
    // this isn't called often enough to warrant anything but brute force search
    for (FontItem item : list) {
      if (item.getFontId().equals(fontEnum)) {
        name = item.getDisplayName();
        break;
      }
    }
    return name;
  }
  
  /**
   * This method gets a Font's item with a Display name using 
   * the String values displayed to users of GUIsliceBuider 
   * by our Font Chooser.
   *
   * @param fontName
   *          - is the GUIslice font name (family name) not the real java font name.
   * @param fontSize
   *          - is the point size of our font as a String value.
   * @param fontStyle
   *          - is the font style "PLAIN", "BOLD", "ITALIC", or "BOLD+ITALIC".
   * @return font The java font we can use to display text
   * @see java.awt.Font
   * @see java.lang.String
   */
  public FontItem getFontItem(String fontName, String fontSize, String fontStyle) {
    List<FontItem> list = getFontList();
    for (FontItem item : list) {
      if (item.getName().equals(fontName)         &&
          item.getLogicalSize().equals(fontSize)  &&
          item.getLogicalStyle().equals(fontStyle)) {
        return item;
      }
    }
    return null;
  }
  
  /**
   * measureChar() - Give back the size of a character adjusted for target
   * platform.
   *
   * @param fontName
   *          the font name
   * @return the <code>dimension</code> object
   */
  public Dimension measureChar(String fontName) {
    FontItem item = getFontItem(fontName);
    if (item != null) {
      Dimension nChSz = new Dimension();
      if (fontName.startsWith("BuiltIn")) {
        int size = Integer.parseInt(item.getFontSz());
        nChSz.width = (6 * size);
        nChSz.height = 8 * size;
        return nChSz;
      }
      String acHeight = "p$";
      String acWidth  = "%";
      Font tmpFont = item.createFont();
      Dimension txtHeight = measureText(fontName, tmpFont,acHeight);
      Dimension txtWidth = measureText(fontName, tmpFont,acWidth);
      nChSz.width = txtWidth.width;
      nChSz.height = txtHeight.height;
      return nChSz;
    }
    return new Dimension(0,0);
  }
  
  /**
   * Measure adafruit text.
   *
   * @param s
   *          the s
   * @param fontName
   *          the font name
   * @return the <code>dimension</code> object
   */
  public Dimension measureAdafruitText(String fontName, String s) {
    FontItem item = getFontItem(fontName);
    if (item != null) {
      Dimension nChSz = new Dimension();
      int size = Integer.parseInt(item.getFontSz());
      nChSz.width = (6 * size) * s.length();
      nChSz.height = (8 * size) + 2;
      return nChSz;
    }
    return new Dimension(0,0);
  }
  
  /**
   * measureText() - Give back the size of our text.
   *
   * @param s
   *          the s
   * @param font
   *          the font
   * @return the <code>dimension</code> object
   */
  public Dimension measureText(String fontName, Font font, String s) {
    if (fontName.startsWith("BuiltIn")) {
        return measureAdafruitText(fontName, s);
    }
    Canvas c = new Canvas();
    // get metrics from the Canvas
    FontMetrics metrics = c.getFontMetrics(font);
    // get the height of a line of text in this
    // font and render context
    int hgt = metrics.getHeight();
    // get the advance of my text in this font
    // and render context
    int adv = metrics.stringWidth(s);
    // calculate the size of a box to hold the
    // text with some padding.
    return new Dimension(adv, hgt);
  }
  
  /**
   * alignString().
   *
   * @param g
   *          the g
   * @param align
   *          - String "GSLC_ALIGN_MID_LEFT", "GSLC_ALIGN_MID_RIGHT", or "GSLC_ALIGN_MID_MID"
   * @param r
   *          the r
   * @param s
   *          the s
   * @param font
   *          the font
   */
  public void alignString(Graphics g, String align, Rectangle r, String s, Font font) {
    if (font != null) {
      FontRenderContext frc = new FontRenderContext(null, true, true);
      Rectangle2D r2D = font.getStringBounds(s, frc);
      int rHeight = (int) Math.round(r2D.getHeight());
      int rY = (int) Math.round(r2D.getY());
      int b = (r.height / 2) - (rHeight / 2) - rY;
      Canvas c = new Canvas();
      FontMetrics metrics = c.getFontMetrics(font);
      int adv = metrics.stringWidth(s);
      g.setFont(font);
      switch (align)
      {
      case TextModel.ALIGN_LEFT:
          g.drawString(s, r.x, r.y + b);
          break;
        case TextModel.ALIGN_CENTER:
          centerString(g, r, s, font);
          break;
        case TextModel.ALIGN_RIGHT:
          g.drawString(s, r.x + (r.width - adv), r.y + b);
          break;
      } 
    }
  }
  
  /**
   * This method centers a <code>String</code> in a bounding
   * <code>Rectangle</code>.
   * 
   * @param g - The <code>Graphics</code> instance.
   * @param r - The bounding <code>Rectangle</code>.
   * @param s - The <code>String</code> to center in the bounding rectangle.
   * @param font - The display font of the <code>String</code>
   * 
   * @see java.awt.Graphics
   * @see java.awt.Rectangle
   * @see java.lang.String
   */
  public void centerString(Graphics g, Rectangle r, String s, Font font) {
    if (font != null) {
      FontRenderContext frc = new FontRenderContext(null, true, true);
      Rectangle2D r2D = font.getStringBounds(s, frc);
      
      int rWidth = (int) Math.round(r2D.getWidth());
      int rHeight = (int) Math.round(r2D.getHeight());
      int rY = (int)r2D.getY();
      
      int a = (r.width - rWidth) / 2;
      int b = (r.height / 2) - (rHeight / 2) - rY;
  
      g.setFont(font);
      g.drawString(s, r.x + a, r.y + b);
    }
  }

  /**
   * Read fonts.
   *
   * @param csvFile
   *          the csv file
   * @param list
   *          the list
   * @param map
   *          the map
   */
  @SuppressWarnings("unchecked")
  public void readFonts(String jsonFile) {
    // de-serialize our font json file
    Gson gson = new GsonBuilder()
        .disableHtmlEscaping() // otherwise "&" will need to be coded as "\u0026"
        .create();

    try (Reader reader = new FileReader(jsonFile)) {
      // Convert JSON File to Java Objects
      builderFonts = gson.fromJson(reader, BuilderFonts.class);
    } catch (IOException e) {
        e.printStackTrace();
    }
    /* now we need to walk our top level font object
     * and fill in each font item with parent information
     * and build up our font list and index map.
     */
    nPlatforms = 0;
    int idx = 0;
    int nErrors = 0;
    platformNames = new String[10];
    fontsByPlatform = new ArrayList[10];
    for (FontPlatform p : builderFonts.getPlatforms()) {
      platformNames[nPlatforms] = p.getName();
      List<FontItem> list = new ArrayList<FontItem>();
      fontsByPlatform[nPlatforms++] = list;
      for (FontCategory c : p.getCategories()) {
        for (FontItem item : c.getFonts()) {
          item.setPlatform(p);
          item.setCategory(c);
          item.generateEnum();
          item.generateKey();
          if (p.getName().toLowerCase().equals("linux")) {
            item.setFont(72);
          } else {
            item.setFont(141);
          }
          String key = item.getKey();
          // check for duplicates
          if (!fontMap.containsKey(key)) {
            platformFonts.add(item);
            list.add(item);
            fontMap.put(key, Integer.valueOf(idx++));
          } else {
            Builder.logger.error("duplicate font: " + key);
            nErrors++;
          }
        }
      }
    }
    if (nErrors > 0) {
      String fileName = CommonUtils.getInstance().getWorkingDir()
          + "logs" 
          + System.getProperty("file.separator")
          + "builder.log";
      throw new CodeGenException(String.format("builder_fonts.json has %d duplicate font(s).\nExamine %s for list of fonts.",
          nErrors,fileName));
    }
  }
 
}
