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
 * The Class FontGraphics.
 * Holds the set of categories with their font definitions
 * for one package, like Adafruit_GFX..Linux
 * 
 * Filled in by JSON deserialization inside FontFactory
 * 
 * @author Paul Conti
 */
public class FontGraphics {
  
  /** The graphic package name. */
  private String graphicPackage;
  
  /** The dpi for displays on this platform */
  private String dpi;
  
  /** The warnings. */
  private List<String> warnings = new ArrayList<String>();
  
  /** The categories. */
  private List<FontCategory> categories = new ArrayList<FontCategory>();
  
  /** The extra includes. */
  private List<String> extraIncludes = new ArrayList<String>();
  
 /**
   * Sets the name of a platform.
   *
   * @param name
   *          the new name
   */
  public void setName(String name) {
    graphicPackage = name;
  }
  
  /**
   * Gets the name of this platform.
   *
   * @return the name
   */
  public String getName() {
    return graphicPackage;
  }

  /**
   * getDPI
   * @return the dpi for this platform's displays
   */
  public int getDPI() {
    return Integer.parseInt(dpi);
  }
  
  /**
   * Adds a warning string to be output during code generation.
   *
   * @param warning
   *          the warning
   */
  public void addWarning(String warning) {
    warnings.add(warning);
  }
  
  /**
   * Gets the list of warnings.
   *
   * @return the warnings
   */
  public List<String> getWarnings() {
    return warnings;
  }
  
  /**
   * Adds a category.
   *
   * @param category
   *          the category
   */
  public void addCategory(FontCategory category) {
    categories.add(category);
  }
  
  /**
   * Gets the list of categories.
   *
   * @return the categories
   */
  public List<FontCategory> getCategories() {
    return categories;
  }

  /**
   * Gets a category by name.
   *
   * @param categoryName
   *          the category name
   * @return the category
   */
  public FontCategory getCategory(String categoryName) {
    for (FontCategory c : categories) {
      if (c.getName().equals(categoryName))
        return c;
    }
    return null;
  }

  /**
   * Gets the list of extra includes.
   *
   * @return the includes
   */
  public List<String> getIncludes() {
    return extraIncludes;
  }
  
}
