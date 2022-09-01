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
import java.util.List;

public class FontChooserHelper {
  private String fontName;
  private List<String> fontSizeList;
  private List<String> fontStyleList;
  
  public FontChooserHelper() {
    fontSizeList = new ArrayList<String>();
    fontStyleList= new ArrayList<String>();
  }
  
  public String getFontName() {
    return fontName;
  }
  
  public void setFontName(String fontName) {
    this.fontName = fontName;
  }
  
  public List<String> getFontSize() {
    return fontSizeList;
  }
  
  public void addFontSize(String fontSize) {
    if (!isInList(fontSize, this.fontSizeList))
      addSizeToList(fontSize);
  }
  
  public List<String> getFontStyle() {
    return fontStyleList;
  }
  
  public void addFontStyle(String fontStyle) {
    if (!isInList(fontStyle, this.fontStyleList)) {
      addStyleToList(fontStyle);
    }
  }
  
  private boolean isInList(String key, List<String> list) {
    for (String s : list) {
      if(s.equals(key))
        return true;
    }
    return false;
  }

  private void addSizeToList(String key) {
    fontSizeList.add(key);
  }

  private void addStyleToList(String key) {
    fontStyleList.add(key);
  }
  
}
  
