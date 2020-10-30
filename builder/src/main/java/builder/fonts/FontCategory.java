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

import java.util.ArrayList;
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
  private String categoryName;
  
  /** The includes path. */
  private String includePath;
  
  /** The ignoreIncludes starting with */
  String ignoreIncludesStartingWith;
  
  /** The extra includes. */
  private List<String> extraIncludes = new ArrayList<String>();
  
  /** The fonts. */
  private List<FontItem> fonts = new ArrayList<FontItem>();
  
  public FontCategory() {
    includePath = "NULL";
    ignoreIncludesStartingWith = "NULL";
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
   * getIngnoreIncludesStartingWith()
   * Gets the template for fonts that need to ignore adding
   * an include header for these fonts.
   * Example: m5stack has the GNU Free fonts with includes
   * its top level M5Stack.h file so no includes are needed
   * generally for any Free Font.
   *
   * @return the name
   */
  public String getIgnoreIncludesStartingWith() {
    return ignoreIncludesStartingWith;
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
   * Adds the extra include string to be output during code generation.
   *
   * @param extra
   *          the extra
   */
  public void addExtra(String extra) {
    extraIncludes.add(extra);
  }
  
  /**
   * Gets the list of extra includes.
   *
   * @return the includes
   */
  public List<String> getIncludes() {
    return extraIncludes;
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
  
  /**
   * toString
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("FontCategory: %s includePath: %s Fonts: ", categoryName, includePath, fonts.size());
  }
}
