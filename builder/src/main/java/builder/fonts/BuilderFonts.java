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
 * The Class BuilderFonts.
 * Top level container for all fonts and platforms.
 * 
 * Filled in by JSON deserialization inside FontFactory
 * 
 * @author Paul Conti
 */
public class BuilderFonts {
  
  /** The all fonts. */
  private List<FontPlatform> allFonts = new ArrayList<FontPlatform>();
  
  /**
   * Instantiates a new builder fonts.
   */
  public BuilderFonts() {
    
  }
  
  /**
   * Adds a platform.
   *
   * @param platform
   *          the platform
   */
  public void addPlatform(FontPlatform platform) {
    allFonts.add(platform);
  }
  
  /**
   * Gets the list of platforms.
   *
   * @return the platforms
   */
  public List<FontPlatform> getPlatforms() {
    return allFonts;
  }
  
  /**
   * Gets a platform object by name.
   *
   * @param platformName
   *          the platform name
   * @return the platform
   */
  public FontPlatform getPlatform(String platformName) {
    for (FontPlatform p : allFonts) {
      if (p.getName().equals(platformName))
        return p;
    }
    return null;
  }
}
