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
package builder.themes;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import builder.Builder;
import builder.common.Utils;

/**
 * A factory for creating and Managing GUIslice Library Themes
 * and Colors. Mapping them to and from something Java can understand.
 * 
 * @author Paul Conti
 * 
 */
public class GUIsliceThemeFactory {
  
  /** The Constant DEFAULT_COLORS_FILE. */
  private static final String GUISLICE_COLORS_FILE = "guislice_colors.csv";
  
  private static final String GUISLICE_THEMES_FILE = "guislice_themes.json";
  
  /** The instance. */
  private static GUIsliceThemeFactory instance = null;
  
  /** The GUIslice colors. */
  private static List<ColorItem> guisliceColors = new ArrayList<ColorItem>();
  
  /** top level container for all GUIslice API themes (not the Builder's FlatLaf themes!) */
  public GUIsliceThemes themes = new GUIsliceThemes();
  
  /**
   * Gets the single instance of ColorFactory.
   *
   * @return single instance of ColorFactory
   */
  public static synchronized GUIsliceThemeFactory getInstance() {
    if (instance == null) {
      instance = new GUIsliceThemeFactory();
      String fileSep = System.getProperty("file.separator");
      String fullPath;
      String strUserDir = System.getProperty("user.dir");
      int n = strUserDir.indexOf("bin");
      if (n > 0) {
        strUserDir = strUserDir.substring(0,n-1);  // remove "/bin"
      }
      fullPath = Utils.getWorkingDir() + "templates" + fileSep;
      String csvFile = fullPath + GUISLICE_COLORS_FILE;
      readGUIsliceColors(csvFile);
      String jsonFile = fullPath + GUISLICE_THEMES_FILE;
      instance.loadGUIsliceThemes(jsonFile);
    }
    return instance;
  }

  /**
   * Instantiates a new color factory.
   */
  public GUIsliceThemeFactory()  {
  }

  /**
   * getListofThemes
   * @return
   */
  public List<String> getListofThemes() {
    List<String> list = new ArrayList<String>();
    for (GUIsliceTheme theme : themes.getAllThemes()) {
      list.add(theme.getThemeName());
    }
    return list;
  }

  public GUIsliceTheme findThemeByName(String name) {
    for (GUIsliceTheme theme : themes.getAllThemes()) {
      if (name.equals(theme.getThemeName()))
        return theme;
    }
    return null;
  }
  
  /**
   * Gets the color list.
   *
   * @return the color list
   */
  public List<ColorItem> getColorList() {
    return guisliceColors;
  }
  
  /**
   * Find color item.
   *
   * @param index
   *          the index
   * @return the <code>ColorItem</code> object
   */
  public ColorItem findColorItem(int index) { 
    return guisliceColors.get(index);
  }

  /**
   * Find color item.
   *
   * @param color
   *          the color
   * @return the <code>ColorItem</code> object
   */
  public ColorItem findColorItem(Color color) { 
    for (int i=1; i<guisliceColors.size(); i++) {
      ColorItem item = guisliceColors.get(i);
      if (item.getColor().equals(color)) {
         return item;
      }
    }
    return guisliceColors.get(0);
  }

  /**
   * colorAsString() - convert java Color object to a string GUIslice API can use
   * Example: We will return either a #define like "Color.BLACK" or if no matching
   * #define can be found we will return the red, green, blue as
   * "(gslc_tsColor){999,999,999}"
   *
   * @param color
   *          the color
   * @return GUIslice Library string representing the color
   */
  public String colorAsString(Color color) {
    String strColor = "";
    for (int i=1; i<guisliceColors.size(); i++) {
      ColorItem item = guisliceColors.get(i);
      if (item.getColor().equals(color)) {
         return item.getDisplayName();
      }
    }
    strColor = String.format("((gslc_tsColor){%d,%d,%d})", color.getRed(), color.getGreen(), color.getBlue());
    return strColor;
  }
  
  /**
   * fromRGB565
   * Convert RGB565 color to Java Color object
   * This hopefully will give users a better idea
   * of what colors will look like on their TFT Displays.
   * 
   * @param n RGB565 as integer
   * @return Color object
   */
  public static Color fromRGB565(int n) {
    int R5 = (n & 0xF800) >>> 11;
    int G6 = (n & 0x7E0)  >>> 5;
    int B5 = (n & 0x1F);
/*
    int R8 = (int) Math.floor( ((255.0 / 31.0) * (double)R5) + 0.5);
    int G8 = (int) Math.floor( ((255.0 / 63.0) * (double)G6) + 0.5);
    int B8 = (int) Math.floor( ((255.0 / 31.0) * (double)B5) + 0.5);
*/
    int R8 = ( R5 * 527 + 23 ) >> 6;
    int G8 = ( G6 * 259 + 33 ) >> 6;
    int B8 = ( B5 * 527 + 23 ) >> 6;
    return new Color(R8,G8,B8);
  }
  
  /**
   * toRGB565
   * Convert Java Color to RGB565 color
   *
   * @param the color to convert
   * @return RGB565 format color
   */
  public static int toRGB565(Color color) {
    //RGB888
    int R8 = color.getRed();
    int G8=  color.getGreen();
    int B8 = color.getBlue();
    int R5 = ( R8 * 249 + 1014 ) >> 11;
    int G6 = ( G8 * 253 +  505 ) >> 10;
    int B5 = ( B8 * 249 + 1014 ) >> 11;
    //Converting to RGB565
    int nColRaw  = R5 << 11; // Mask: 1111 1000 0000 0000
    nColRaw     |= G6 << 5;  // Mask: 0000 0111 1110 0000
    nColRaw     |= B5;       // Mask: 0000 0000 0001 1111
    return nColRaw;
  }
  
  /**
   * convertRGB565
   * Convert Java Color object to reduced RGB565 Colors
   *
   * @param the color to convert
   * @return RGB565 format color
   */
  public static Color convertToRGB565(Color color) {
    int nColRaw = toRGB565(color);
    return fromRGB565(nColRaw);
  }
  
  /**
   * Read GUIslice colors.
   * This is a mapping of GUIslice Color names to Colors
   * Ex: GSLC_COL_BLACK -> 0,0,0
   * @param csvFile
   *          the csv file
   */
  private static void readGUIsliceColors(String csvFile) {
    String line = "";
    String cvsSplitBy = ",";
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(csvFile));
      int i =1;
      ColorItem item = new ColorItem(0, "CUSTOM COLOR");
      guisliceColors.add(item);
      while ((line = br.readLine()) != null) {
        // Need to skip comment lines 
        if (!line.startsWith("#")) {
          String[] f = line.split(cvsSplitBy);
          item = new ColorItem(i, f[0], Integer.parseInt(f[1]), Integer.parseInt(f[2]), Integer.parseInt(f[3]));
          guisliceColors.add(item);
          i++;
        }
      }
      br.close();
    } catch (IOException e) {
      Builder.logger.error("ColorFactory: "+e.toString());
      JOptionPane.showMessageDialog(null, 
          "Sorry, It appears to be a problem with your installion-check builder.log", 
          "ERROR",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void loadGUIsliceThemes(String jsonFile) {
    // de-serialize our font json file
    Gson gson = new GsonBuilder()
        .disableHtmlEscaping() // otherwise "&" will need to be coded as "\u0026"
        .create();

    try (Reader reader = new FileReader(jsonFile)) {
      // Convert JSON File to Java Objects
      themes = gson.fromJson(reader, GUIsliceThemes.class);
    } catch (IOException e) {
      Builder.logger.error(String.format("GUIsliceThemes failed %s", e.toString()));
    }
    
  }

}
