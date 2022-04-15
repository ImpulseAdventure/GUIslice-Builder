package builder.themes;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class GUIsliceThemes.
 * Holds the set of all GUIslice API themes. 
 * 
 * Filled in by JSON deserialization inside GUIsliceThemeFactory
 * 
 * @author Paul Conti
 */
public class GUIsliceThemes {

  /** top level container for all GUIslice API themes (not the Builder's FlatLaf themes!) */
  public List<GUIsliceTheme> allThemes = new ArrayList<GUIsliceTheme>();
 
  public GUIsliceThemes() {
    // TODO Auto-generated constructor stub
  }

  public List<GUIsliceTheme> getAllThemes() {
    return allThemes;
  }

}
