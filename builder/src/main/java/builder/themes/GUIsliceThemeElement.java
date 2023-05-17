/**
 *

 * The MIT License
 *
 * Copyright 2022 Paul Conti
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

/**
 * The Class GUIsliceThemeElement.
 * Holds the set of style definitions for one UI Element. 
 * Example: Box
 * 
 * Filled in by JSON deserialization inside GUIsliceThemeFactory
 * 
 * @author Paul Conti
 */
public class GUIsliceThemeElement {

  public String  uiElemName;
  public String width;
  public String height;

  public boolean use_flash;
  public boolean corners_rounded;
  public boolean trim_en;
  public boolean thumb_frame_en;
  public boolean frame_en;
  public boolean fill_en;
  public boolean glow_en;

  public String  style;

  public String  bar_frame_col;
  public String  bar_fill_col;
  public String  checkmark_col;
  public String  frame_col;
  public String  fill_col;
  public String  gap_col;
  public String  gauge_col;
  public String  glow_col;
  public String  on_col;
  public String  off_col;
  public String  remain_col;
  public String  text_col;
  public String  thumb_col;
  public String  thumb_frame_col;
  public String  tick_col;
  public String  trim_col;
  
  public GUIsliceThemeElement() {
    width = null;
    height = null;
    use_flash = false;
    corners_rounded = false;
    trim_en = false;
    thumb_frame_en = false;
    frame_en = false;
    fill_en = false;
    glow_en = false;
    style = "NULL";
    bar_frame_col = "NULL";
    bar_fill_col = "NULL";
    checkmark_col = "NULL";
    frame_col = "NULL";
    fill_col = "NULL";
    gap_col = "NULL";
    gauge_col = "NULL";
    glow_col = "NULL";
    remain_col = "NULL";
    on_col = "NULL";
    off_col = "NULL";
    tick_col = "NULL";
    text_col = "NULL";
    thumb_col = "NULL";
    thumb_frame_col = "NULL";
    trim_col = "NULL";
  }

  public Integer getWidth() {
    return Integer.valueOf(width);
  }

  public Integer getHeight() {
    return Integer.valueOf(height);
  }

  public boolean isUse_flash() {
    return use_flash;
  }

  public String getUiElemName() {
    return uiElemName;
  }

  public Boolean isCornersRounded() {
    return Boolean.valueOf(corners_rounded);
  }

  public Boolean isTrimEnabled() {
    return Boolean.valueOf(trim_en);
  }

  public Boolean isFrameEnabled() {
    return Boolean.valueOf(frame_en);
  }

  public Boolean isFillEnabled() {
    return Boolean.valueOf(fill_en);
  }

  public Boolean isGlowEnabled() {
    return Boolean.valueOf(glow_en);
  }

  public Boolean isThumbFrameEnabled() {
    return Boolean.valueOf(thumb_frame_en);
  }

  public String getStyle() {
    return style;
  }

  public Color getCheckmarkCol() {
    return convertStringToColor(checkmark_col);
  }

  public Color getTrimCol() {
    return convertStringToColor(trim_col);
  }

  public Color getThumbCol() {
    return convertStringToColor(thumb_col);
  }

  public Color getGaugeCol() {
    return convertStringToColor(gauge_col);
  }

  public Color getTickCol() {
    return convertStringToColor(tick_col);
  }

  public Color getTextCol() {
    return convertStringToColor(text_col);
  }

  public Color getFrameCol() {
    return convertStringToColor(frame_col);
  }

  public Color getFillCol() {
    return convertStringToColor(fill_col);
  }

  public Color getGlowCol() {
    return convertStringToColor(glow_col);
  }

  public Color getThumbFrameCol() {
    return convertStringToColor(thumb_frame_col);
  }

  public Color getRemainCol() {
    return convertStringToColor(remain_col);
  }

  public Color getOnCol() {
    return convertStringToColor(on_col);
  }

  public Color getOffCol() {
    return convertStringToColor(off_col);
  }

  public Color getGapCol() {
    return convertStringToColor(gap_col);
  }

  public Color getBarFrameCol() {
    return convertStringToColor(bar_frame_col);
  }

  public Color getBarFillCol() {
    return convertStringToColor(bar_fill_col);
  }

  private Color convertStringToColor(String s) {
    Color c = null;
    if (!s.equals("NULL")) {
      int n = Integer.decode(s); // hex string to int
      c = new Color(n);
    }
    return c;
  }
}
