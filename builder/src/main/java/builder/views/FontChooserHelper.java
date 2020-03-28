package builder.views;

import java.util.ArrayList;
import java.util.List;

public class FontChooserHelper {
  private String fontName;
  private List<String> fontSize;
  private List<String> fontStyle;
  
  public FontChooserHelper() {
    fontSize = new ArrayList<String>();
    fontStyle= new ArrayList<String>();
  }
  
  public String getFontName() {
    return fontName;
  }
  
  public void setFontName(String fontName) {
    this.fontName = fontName;
  }
  
  public List<String> getFontSize() {
    return fontSize;
  }
  
  public void addFontSize(String fontSize) {
    if (!isInList(fontSize, this.fontSize))
      this.fontSize.add(fontSize);
  }

  public List<String> getFontStyle() {
    return fontStyle;
  }
  
  public void addFontStyle(String fontStyle) {
    if (!isInList(fontStyle, this.fontStyle)) {
      this.fontStyle.add(fontStyle);
    }
  }
  
  private boolean isInList(String key, List<String> list) {
    for (String s : list) {
      if(s.equals(key))
        return true;
    }
    return false;
  }
}
