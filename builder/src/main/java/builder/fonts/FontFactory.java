/**
 *
 * The MIT License
 *
 * Copyright 2018-2022 Paul Conti
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import builder.Builder;
import builder.common.CommonUtils;
import builder.controller.Controller;

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
  public static List<FontItem> platformFonts = new ArrayList<FontItem>();
  
  public static List<FontItem> list;
  
  public static int idx = 0;

  public static int nErrors = 0;

  /** The font map used as index into font item list. */
  public static HashMap<String, Integer> fontMap = new HashMap<String, Integer>(128);
  
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
    String jsonFile = fullPath + "templates" + System.getProperty("file.separator") 
        + FONT_TEMPLATE;
    readFonts(jsonFile);
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
  public FontTFT getFont(String key) {
    FontItem item = getFontItem(key);
    if (item != null) {
      return item.getFont();
    } 
    return null;
  }
  
  /**
   * Gets the name of the default font for the target platform.
   *
   * @return the font name
   */
  public String getDefFontName() {
    List<FontItem> list = getFontList();
    if (list == null) return "";
    return list.get(0).getDisplayName();
  }
  
  /**
   * Gets the ENUM of the default font for the target platform.
   *
   * @return the font name
   */
  public String getDefFontEnum() {
    List<FontItem> list = getFontList();
    if (list == null) return null;
    FontItem item = list.get(0);
    return item.getFontId();
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
    if (item == null) return null;
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
   * Gets the font display name.
   *
   * @param fontEnum
   *          the font enum
   * @return the font display name or null on failure
   */
  public FontTFT getFontbySizeStyle(String family, int size, String style) {
    List<FontItem> list = getFontList();
    FontTFT holdFont = null;
    int fontMax = 0;
    int fontSz = 0;
    // this isn't called often enough to warrant anything but brute force search
    for (FontItem item : list) {
      if (item.getFamilyName().equals(family) && item.getLogicalStyle().equals(style)) {
        fontSz = item.getLogicalSizeAsInt();
        if (fontSz == size) {
          holdFont = item.getFont();
          break;
        } else if (fontSz < size && fontSz > fontMax) {
          fontMax = fontSz;
          holdFont = item.getFont();
        }
      }
    }
    return holdFont;
  }
  
  /**
   * This method gets a Font's item with a Family name using 
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
      if (item.getFamilyName().equals(fontName)         &&
          item.getLogicalSize().equals(fontSize)  &&
          item.getLogicalStyle().equals(fontStyle)) {
        return item;
      }
    }
    return null;
  }
  
  /**
   * getCharBounds
   * Used by Character Map
   * 
   * @param x        x position
   * @param y        y position
   * @param w        width of box
   * @param h        height of box
   * @param str      The text string to display
   * @param font     The TFT font to use for this text string
   * @param align    Text alignment / justification mode
   *                 String "GSLC_ALIGN_MID_LEFT", "GSLC_ALIGN_MID_RIGHT", or "GSLC_ALIGN_MID_MID"
   * @param nMargin  Number of pixels gap to leave surrounding text
   */
  public Rectangle getCharBounds(int x, int y, int w, int h, String str, String align, FontTFT font, int nMargin) {
    
    // Fetch the size of the text to allow for justification
    FontMetrics metrics = font.getTextBounds(str, 0, 0, false);
    if (metrics.w <= 0 || metrics.h <= 0) return null;
    // determine bounds
    Rectangle r = new Rectangle(x,y,w,h);
    
    return alignBounds(align, r, metrics, nMargin);
  }
  
  /**
   * getMaxTextBounds 
   * Size for maximum character size times storage length.
   * Used for dynamic text that will change during runtime.
   * @param x
   * @param y
   * @param font
   * @param s
   * @return the <code>dimension</code> object
   */
  public Dimension getMaxTextBounds(int x, int y, FontTFT font, int length) {
    // Fetch the maximum size of a character
    Dimension size = font.getMaxCharSize();
    // calculate the size of a box to hold the text with some padding.
    size.width = size.width * length + 2;
    size.height = size.height + 2;
    // clipping
    if (size.width+x > Builder.CANVAS_WIDTH) {
      size.width = size.width - (size.width + x - Builder.CANVAS_WIDTH);
    }
    if (size.height+y > Builder.CANVAS_HEIGHT) {
      size.height = size.height - (size.height - y - Builder.CANVAS_HEIGHT);
    }

    return size;
  }
  
  /**
   * getTextBounds() - Give back the size of a String.
   * Used for static text like labels
   * @param s
   *          the s
   * @param font
   *          the font
   * @return the <code>dimension</code> object
   */
  /**
   * getTextBounds() - Give back the size of a String.
   * Used for static text like labels
   * @param x
   * @param y
   * @param font
   * @param s
   * @return
   */
  public Dimension getTextBounds(int x, int y, FontTFT font, String s) {
    // Fetch the size of the text to allow for justification
    FontMetrics metrics = font.getTextBounds(s, 0, 0,false);
    // clipping
    if (metrics.w+x > Builder.CANVAS_WIDTH) {
      metrics.w = metrics.w - (metrics.w + x - Builder.CANVAS_WIDTH);
    }
    if (metrics.h+y > Builder.CANVAS_HEIGHT) {
      metrics.h = metrics.h - (metrics.h - y - Builder.CANVAS_HEIGHT);
    }

    return new Dimension(metrics.w, metrics.h);
  }

  /**
   * getCharSize() - Give back the size of a Character.
   *
   * @param s
   *          the s
   * @param font
   *          the font
   * @return the <code>dimension</code> object
   */
  public Dimension getCharSize(char ch, FontTFT font) {
    return font.getCharSize(ch);
  }

  /**
   * drawText
   * 
   * @param g2d      The graphics context
   * @param align    Text alignment / justification mode
   *                 String "GSLC_ALIGN_MID_LEFT", "GSLC_ALIGN_MID_RIGHT", or "GSLC_ALIGN_MID_MID"
   * @param r        Rectangle region to contain the text
   * @param str      The text string to display
   * @param font     The TFT font to use for this text string
   * @param colTxt   Color for text
   * @param colBg    Color for background, transparent if color matches colTxt
   * @param nMargin  Number of pixels gap to leave surrounding text
   */
  public void drawText(Graphics2D g2d, String align, Rectangle r, String str, FontTFT font, 
    Color colTxt, Color colBg, int nMargin) {
    
    if (font == null) return;
    
    String[] lines = str.split("\\n");
    if (lines.length > 1) {
      FontMetrics metrics = new FontMetrics(); 
      for (int i=0; i<lines.length; i++) {
        // Fetch the size of the text to allow for justification
        FontMetrics metricLine = font.getTextBounds(lines[i], 0, 0, true);
        metrics.x1 = metricLine.x1;
        metrics.y1 = metricLine.y1;
        if (metricLine.w > metrics.w)
          metrics.w = metricLine.w;
        metrics.h += metricLine.h;
        metrics.base_height = metricLine.base_height;
      }
      // Calculate the text alignment
      Rectangle rTxt = alignBounds(align, r, metrics, nMargin);
  
      for (int i=0; i<lines.length; i++) {
        // Fetch the size of the text to allow for justification
        FontMetrics metricLine = font.getTextBounds(lines[i], 0, 0, true);
        
        // Call the font's text rendering routine
        font.drawString(g2d,rTxt,lines[i], colTxt, colBg,true);
        rTxt.y += metricLine.h;
      }
    } else {
      // Fetch the size of the text to allow for justification
      FontMetrics metrics = font.getTextBounds(str, 0, 0, true);
      
      // Calculate the text alignment
      Rectangle rTxt = alignBounds(align, r, metrics, nMargin);
  
      // Call the font's text rendering routine
      font.drawString(g2d,rTxt,str, colTxt, colBg,true);
    }
  }
  
  /**
   * drawTextImage
   * 
   * @param r        Rectangle region to contain the text
   * @param align    Text alignment / justification mode
   *                 String "GSLC_ALIGN_MID_LEFT", "GSLC_ALIGN_MID_RIGHT", or "GSLC_ALIGN_MID_MID"
   * @param str      The text string to display
   * @param font     The TFT font to use for this text string
   * @param colTxt   Color for text
   * @param colBg    Color for background, transparent if color matches colTxt
   * @param nMargin  Number of pixels gap to leave surrounding text
   */
  public BufferedImage drawTextImage(String align, Rectangle r, String str, FontTFT font, 
    Color colTxt, Color colBg, int nMargin) {
    
    // Fetch the size of the text to allow for justification
    FontMetrics metrics = font.getTextBounds(str, 0, 0, false);
    
    // determine bounds
    Rectangle rTxt = alignBounds(align, r, metrics, nMargin);

    // Call the font's text rendering routine
    return font.drawImage(rTxt,str, colTxt, colBg,false);

  }
  
  /**
   * drawChar
   * Used by CharacterMap
   * @param g2d      The graphics context
   * @param align    Text alignment / justification mode
   *                 String "GSLC_ALIGN_MID_LEFT", "GSLC_ALIGN_MID_RIGHT", or "GSLC_ALIGN_MID_MID"
   * @param r        Rectangle region to contain the text
   * @param str      The text string to display
   * @param font     The TFT font to use for this text string
   * @param colTxt   Color for text
   * @param colBg    Color for background, transparent if color matches colTxt
   * @param nMargin  Number of pixels gap to leave surrounding text
   */
  public void drawChar(Graphics2D g2d, String align, Rectangle r, String str, FontTFT font, 
    Color colTxt, Color colBg, int nMargin) {
    
    if (font == null) return;
    // Fetch the size of the text to allow for justification
    FontMetrics metrics = font.getTextBounds(str, 0, 0, false);

    // determine bounds
    Rectangle rTxt = alignBounds(align, r, metrics, nMargin);

    // Call the font's text rendering routine
    font.drawString(g2d,rTxt,str, colTxt, colBg,false);

  }
  
  public Rectangle alignBounds(String align, Rectangle r, FontMetrics metrics, int nMargin) {
    int nElemH = r.height;
    int nElemW = r.width;
    int nTxtOffsetX=0;
    int nTxtOffsetY=0;
    int nTxtSzW=0;
    int nTxtSzH=0;

    // Calculate the text alignment
    int nTxtX = r.x;
    int nTxtY = r.y;
    nTxtSzW = metrics.w;
    nTxtSzH = metrics.h;
    nTxtOffsetX = metrics.x1;
    nTxtOffsetY = metrics.y1;
    switch (align)
    {
      case FontTFT.ALIGN_LEFT:
        nTxtY = nTxtY+(nElemH/2)-(nTxtSzH/2);
        nTxtX = nTxtX+nMargin;
        break;
      case FontTFT.ALIGN_CENTER:
        nTxtY = nTxtY+(nElemH/2)-(nTxtSzH/2);
        nTxtX = nTxtX+(nElemW/2)-(nTxtSzW/2);
        break;
      case FontTFT.ALIGN_RIGHT:
        nTxtY = nTxtY+(nElemH/2)-(nTxtSzH/2);
        nTxtX = nTxtX+nElemW-nMargin-nTxtSzW;
        break;
      case FontTFT.ALIGN_TOP_LEFT:
        nTxtY = nTxtY+nMargin;
        nTxtX = nTxtX+nMargin;
        break;
      case FontTFT.ALIGN_TOP_CENTER:
        nTxtY = nTxtY+nMargin;
        nTxtX = nTxtX+(nElemW/2)-(nTxtSzW/2);
        break;
      case FontTFT.ALIGN_TOP_RIGHT:
        nTxtY = nTxtY+nMargin;
        nTxtX = nTxtX+nElemW-nMargin-nTxtSzW;
        break;
      case FontTFT.ALIGN_BOT_LEFT:
        nTxtY = nTxtY+nElemH-nMargin-nTxtSzH;
        nTxtX = nTxtX+nMargin;
        break;
      case FontTFT.ALIGN_BOT_CENTER:
        nTxtY = nTxtY+nElemH-nMargin-nTxtSzH;
        nTxtX = nTxtX+(nElemW/2)-(nTxtSzW/2);
        break;
      case FontTFT.ALIGN_BOT_RIGHT:
        nTxtY = nTxtY+nElemH-nMargin-nTxtSzH;
        nTxtX = nTxtX+nElemW-nMargin-nTxtSzW;
        break;
    } 
    
    // Now correct for offset from text bounds
    nTxtX -= nTxtOffsetX;
    nTxtY -= nTxtOffsetY;
    
    // Boundary Test
    if (nTxtX <= r.x) nTxtX = r.x+1;
    return new Rectangle(nTxtX, nTxtY, r.width, r.height);
  }
  
  /**
   * Read fonts.
   *
   * @param jsonFile
   *          the json file
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
    /*
     * now we need to walk our top level font object and fill in each font item with
     * parent information and build up our font list and index map.
     */
    nPlatforms = 0;
    idx = 0;
    nErrors = 0;
    platformNames = new String[10];
    fontsByPlatform = new ArrayList[10];
    for (FontPlatform p : builderFonts.getPlatforms()) {
  Builder.logger.debug("Platform: " + p.getName());
      platformNames[nPlatforms] = p.getName();
      list = new ArrayList<FontItem>();
      fontsByPlatform[nPlatforms++] = list;
      for (FontCategory c : p.getCategories()) {
  Builder.logger.debug("Platform: " + c.getName());
        if (c.getFonts().size() == 0) {
          // handle native fonts that did not require JSON entries
          if (c.getName().equals(FontTFT.FONT_GFX)) {
            //Builder.logger.debug(c.toString());
            String fullPath = CommonUtils.getInstance().getWorkingDir();
            String fontsPath = fullPath + "fonts" + System.getProperty("file.separator") + "gfx";
            Path startingDir = Paths.get(fontsPath);
            FontLoadGFXFiles fileVisitor = new FontLoadGFXFiles(p, c);
            try {
              Files.walkFileTree(startingDir, fileVisitor);
            } catch (IOException e) {
              nErrors++;
              Builder.logger.error(e.toString());
            }
          } else if (c.getName().equals(FontTFT.FONT_T3)) {
            // Builder.logger.debug(c.toString());
            String fullPath = CommonUtils.getInstance().getWorkingDir();
            String fontsPath = fullPath + "fonts" + System.getProperty("file.separator") + "t3";
            Path startingDir = Paths.get(fontsPath);
            FontLoadT3Files fileVisitor = new FontLoadT3Files(p, c);
            try {
              Files.walkFileTree(startingDir, fileVisitor);
            } catch (IOException e) {
              nErrors++;
              Builder.logger.error(e.toString());
            }
          } else if (c.getName().equals(FontTFT.FONT_UTFT)) {
   Builder.logger.debug(c.toString());
            String fullPath = CommonUtils.getInstance().getWorkingDir();
            String fontsPath = fullPath + "fonts" + System.getProperty("file.separator") + "utft";
            Path startingDir = Paths.get(fontsPath);
            FontLoadUtftFiles fileVisitor = new FontLoadUtftFiles(p, c);
            try {
              Files.walkFileTree(startingDir, fileVisitor);
            } catch (IOException e) {
              nErrors++;
              Builder.logger.error(e.toString());
            }
          }
        } else {
          for (FontItem item : c.getFonts()) {
            item.setPlatform(p);
            item.setCategory(c);
            item.generateEnum();
            item.generateKey();
            String key = item.getKey();
            // Builder.logger.debug("Font: " + item.toString());
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
    }
    Builder.logger.debug("Total number Platforms: " + nPlatforms + " number of fonts: " + idx);
    if (nErrors > 0) {
      String fileName = CommonUtils.getInstance().getWorkingDir() + "logs" + System.getProperty("file.separator")
          + "builder.log";
      String msg = String.format("builder_fonts.json has %d duplicate font(s).\nExamine %s for list of fonts.", nErrors,
          fileName);
      JOptionPane.showMessageDialog(null, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
  }

}
