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
    /* we get sizes out of order
     * so sort them as they are added.
     */
    
    int size = Integer.parseInt(key);
    int compare = 0;
    boolean bLess = false;
    int i = 0;
    for (String s : fontSizeList) {
      compare = Integer.parseInt(s);
      if(size < compare) {
        bLess = true;
        break;
      }
      i++;
    }
    if (bLess) 
      fontSizeList.add(i,key);
    else
      fontSizeList.add(key);
  }

  private void addStyleToList(String key) {
    /* we get Styles out of order
     * so sort them as they are added.
     */
    int map = mapStyle(key);
    int compare = 0;
    boolean bLess = false;
    int i = 0;
    for (String s : fontStyleList) {
      compare = mapStyle(s);
      if(map < compare) {
        bLess = true;
        break;
      }
      i++;
    }
    if (bLess) 
      fontStyleList.add(i,key);
    else
      fontStyleList.add(key);
  }
  
  /**
   * mapStyle will map a style to the order we want them displayed
   * Regular,Bold,Italic,Bold+Italic
   * @param style
   * @return sequence number for sorting
   */
  private int mapStyle(String style) {
    switch (style) {
    case "Bold":
      return 1;
    case "Bold+Italic":
      return 3;
    case "Italic":
      return 2;
    default:
      return 0;
    }
  }
  
}
  
