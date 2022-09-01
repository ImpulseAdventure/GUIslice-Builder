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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The Class FontCategory.
 * Holds the set of font definitions for one category, 
 * like default,gfx.
 * 
 * Filled in by JSON deserialization inside FontFactory
 * 
 * @author Paul Conti
 */
public class FontCategory {

  /** The category name. */
  public String categoryName;
  
  /** The includes path. */
  public String includePath;
  
  /** where to find the font data */
  public String fontFolderPath;

  /** The fonts. */
  public List<FontItem> fonts = new ArrayList<FontItem>();
  
  private transient static HashMap<String, Integer> fontMap = null;

  public FontCategory() {
    includePath = "NULL";
  }
  
  /**
   * Sets the name of this category.
   *
   * @param name
   *          the new name
   */
  public void setName(String name) {
    categoryName = name;
  }

  /**
   * Gets the name of this category.
   *
   * @return the name
   */
  public String getName() {
    return categoryName;
  }

  /**
   * Gets the folder path to add to include file names
   * This optionally replaces the need for the font definition
   * to specify a full path to its include file.
   *
   * @return the name
   */
  public String getIncludePath() {
    return includePath;
  }

  /**
   * Adds a font definition
   *
   * @param category
   *          the category
   */
  public void addFont(FontItem category) {
    fonts.add(category);
  }

  /**
   * Gets the list of fonts for this category.
   *
   * @return the fonts
   */
  public List<FontItem> getFonts() {
    return fonts;
  }

  public void addFontItem(FontItem item) {
    fonts.add(item);
  }
  
  public void buildMap() {
    fontMap = new HashMap<String, Integer>();
    for(int i=0; i<fonts.size(); i++) {
      FontItem item = fonts.get(i);
      fontMap.put(item.getDisplayName(), i);
    }
  }
  
  public FontItem findFontItem(String displayName) {
    FontItem item = null;
    int n = 0;
    if (fontMap.containsKey(displayName)) {
      n = fontMap.get(displayName);
      item = fonts.get(n);
    }
    return item;
  }
  
  public boolean isInstalledFont(String displayName) {
     boolean bResult = false;
     
     FontItem item = findFontItem(displayName);
     if (item != null) {
       return item.isInstalledFont();
     }
     return bResult;
  }
  
  /**
   *  
   */
  public void sortFonts() {
    if (fonts.size() > 2) {
      Collections.sort(fonts);
    }
  }
  
  /**
   * toString
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("FontCategory: %s includePath: %s Fonts: ", categoryName, includePath, fonts.size());
  }

  public String getFontFolderPath() {
    return fontFolderPath;
  }

}
