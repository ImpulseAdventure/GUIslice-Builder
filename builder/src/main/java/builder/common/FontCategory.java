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
  
  /** The extra includes. */
  private List<String> extraIncludes = new ArrayList<String>();
  
  /** The fonts. */
  private List<FontItem> fonts = new ArrayList<FontItem>();
  
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
   * Gets the name the name of this category.
   *
   * @return the name
   */
  public String getName() {
    return categoryName;
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

}
