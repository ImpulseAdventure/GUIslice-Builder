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
package builder.models;

import java.awt.Color;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;

import builder.Builder;
import builder.codegen.PlatformIO;
import builder.commands.PropertyCommand;
import builder.common.EnumFactory;
import builder.common.ThemeInfo;
import builder.fonts.FontFactory;
import builder.fonts.FontGraphics;

/**
 * The Class GeneralModel implements the model for the builder.
 * 
 * @author Paul Conti
 *  
 */
public class GeneralModel extends WidgetModel {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID  = 1L;
  
  /** The Property Index Constants. */
  public static final int PROP_THEME                = 1;
  public static final int PROP_IDE                  = 2;
  public static final int PROP_PIO_ENV              = 3;
  public static final int PROP_TARGET               = 4;
  public static final int DISPLAY_WIDTH             = 5;
  public static final int DISPLAY_HEIGHT            = 6;
  public static final int PROP_PROJECT_DIR          = 7;
  public static final int PROP_TARGET_IMAGE_DIR     = 8;
  public static final int PROP_BACKGROUND           = 9;
  public static final int PROP_TRANSPARENCY_COLOR   = 10;
  public static final int PROP_MARGINS              = 11;
  public static final int PROP_HSPACING             = 12;
  public static final int PROP_VSPACING             = 13;
  public static final int PROP_MAX_STRING           = 14;
  public static final int PROP_ROTATION             = 15;
  public static final int PROP_BACKWARD_COMPAT      = 16;
  public static final int PROP_PRESERVE_BTN_CALLBACKS = 17;
  // The following properties are hidden from users
  public static final int PROP_IMAGE_DIR            = 18; // last folder used to load image
  public static final int PROP_RECENT_COLORS        = 19; // LRU of recent colors chosen
  public static final int PROP_RECENT_FILES         = 20; // LRU of recent files chosen
  /* window sizes are hidden from the users because if you change one
   * the other values must change in proportion. It's much easier to
   * simply keep track of when users drag a window and record the values.
   */
  public static final int PROP_SIZE_APP_WIDTH       = 21; // Size of App Window 
  public static final int PROP_SIZE_APP_HEIGHT      = 22; 
  public static final int PROP_SIZE_TFT_WIDTH       = 23; // Size of TFT Simulation Pane
  public static final int PROP_SIZE_TFT_HEIGHT      = 24; 
  public static final int PROP_SIZE_PROPVIEW_WIDTH  = 25; // Size of Property View Pane 
  public static final int PROP_SIZE_PROPVIEW_HEIGHT = 26; 
  
  /** The Property Defaults */
  static public  final String  DEF_IDE                 = "Arduino IDE";
  static public  final String  DEF_TARGET              = "Adafruit_GFX";
  static public  final Integer DEF_WIDTH               = Integer.valueOf(320);
  static public  final Integer DEF_HEIGHT              = Integer.valueOf(240);
  static public  final Integer DEF_DPI                 = Integer.valueOf(144);
  static public  final String  DEF_PROJECT_DIR         = "projects";
  static public  final String  DEF_TARGET_IMAGE_DIR    = "/";
  static public  final Color   DEF_BACKGROUND          = Color.BLACK;
  static public  final Color   DEF_TRANSPARENCY_COLOR  = new Color(255,0,255);  // GSLC_COL_MAGENTA
  static public  final Integer DEF_MARGINS             = Integer.valueOf(10);
  static public  final Integer DEF_HSPACING            = Integer.valueOf(20);
  static public  final Integer DEF_VSPACING            = Integer.valueOf(20);
  static public  final Integer DEF_MAX_STRING          = Integer.valueOf(100);
  static public  final Integer DEF_ROTATION            = Integer.valueOf(-1);
  static public  final Boolean DEF_BACKWARD_COMPAT     = Boolean.valueOf(false);
  
  /** The cb themes. */
  public static JComboBox<String> cbThemes;
  
  /** The theme cell editor. */
  DefaultCellEditor  themeCellEditor;

  /** The cb ide. */
  JComboBox<String> cbIDE;
  
  /** The ide cell editor. */
  DefaultCellEditor ideCellEditor;
  
  /** The cb target. */
  JComboBox<String> cbTarget;
  
  /** The target cell editor. */
  DefaultCellEditor targetCellEditor;
  
  /** The cb target. */
  JComboBox<String> cbPioEnv;
  
  /** The target cell editor. */
  DefaultCellEditor pioenvCellEditor;
  
  /** The default theme name */
  public static String defThemeName;

  /**
   * Instantiates a new general model.
   */
  public GeneralModel() {
    initComboBoxes();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.GENERAL;
    data = new Object[27][5];

    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_THEME, String.class, "GEN-100", Boolean.FALSE,"Themes","");
//    if (Builder.isMAC) {
//      data[PROP_THEME][PROP_VAL_READONLY]= Boolean.TRUE;
//    }
    initProp(PROP_IDE, String.class, "GEN-098", Boolean.FALSE,"Target IDE",DEF_IDE);
    initProp(PROP_PIO_ENV, String.class, "GEN-099", Boolean.TRUE,"PlatformIO default_envs",cbPioEnv.getItemAt(0));
    initProp(PROP_TARGET, String.class, "GEN-101", Boolean.FALSE,"Font Library",DEF_TARGET);

    initProp(DISPLAY_WIDTH, Integer.class, "GEN-102", Boolean.FALSE,"TFT Screen Width",DEF_WIDTH);
    initProp(DISPLAY_HEIGHT, Integer.class, "GEN-103", Boolean.FALSE,"TFT Screen Height",DEF_HEIGHT);

    initProp(PROP_PROJECT_DIR, String.class, "GEN-105", Boolean.FALSE,"Project Directory",DEF_PROJECT_DIR);
    initProp(PROP_TARGET_IMAGE_DIR, String.class, "GEN-106", Boolean.FALSE,
        "Target Platform Image Directory",DEF_TARGET_IMAGE_DIR);

    initProp(PROP_BACKGROUND, Color.class, "COL-310", Boolean.FALSE,"Background Color",DEF_BACKGROUND);

    initProp(PROP_TRANSPARENCY_COLOR, Color.class, "COL-314", Boolean.FALSE,
        "Image Transparency Color",DEF_TRANSPARENCY_COLOR);

    initProp(PROP_MARGINS, Integer.class, "GEN-107", Boolean.FALSE,"Screen Margins",DEF_MARGINS);
    initProp(PROP_HSPACING, Integer.class, "GEN-108", Boolean.FALSE,
        "Horizontal Spacing between widgets",DEF_HSPACING);
    initProp(PROP_VSPACING, Integer.class, "GEN-109", Boolean.FALSE,
        "Vertical Spacing between widgets",DEF_VSPACING);
    initProp(PROP_MAX_STRING, Integer.class, "GEN-110", Boolean.FALSE,"MAX_STR",DEF_MAX_STRING);
    initProp(PROP_ROTATION, Integer.class, "GEN-112", Boolean.FALSE,
        "Screen Rotation [0-3 or -1 default]",DEF_ROTATION);
    initProp(PROP_BACKWARD_COMPAT, Boolean.class, "GEN-120", Boolean.FALSE,
        "Backward Compatibility Mode?",DEF_BACKWARD_COMPAT);
    initProp(PROP_PRESERVE_BTN_CALLBACKS, Boolean.class, "GEN-136", Boolean.FALSE,
        "Preserve Button Callbacks?",Boolean.TRUE);
    initProp(PROP_IMAGE_DIR, String.class, "GEN-113", Boolean.FALSE,"Last Image Directory Accessed","");
    initProp(PROP_RECENT_COLORS, String.class, "GEN-111", Boolean.TRUE,"Recent Colors","");
    initProp(PROP_RECENT_FILES, String.class, "GEN-121", Boolean.TRUE,"Recent Files","");
    initProp(PROP_SIZE_APP_WIDTH, Integer.class,  "GEN-130", Boolean.FALSE,"App Win Width",Integer.valueOf(0));
    initProp(PROP_SIZE_APP_HEIGHT, Integer.class, "GEN-131", Boolean.FALSE,"App Win Height",Integer.valueOf(0));
    initProp(PROP_SIZE_TFT_WIDTH, Integer.class,  "GEN-132", Boolean.FALSE,"TFT Pane Width",Integer.valueOf(0));
    initProp(PROP_SIZE_TFT_HEIGHT, Integer.class, "GEN-133", Boolean.FALSE,"TFT Pane Height",Integer.valueOf(0));
    initProp(PROP_SIZE_PROPVIEW_WIDTH, Integer.class,  "GEN-134", Boolean.FALSE,"PropView Pane Width",Integer.valueOf(0));
    initProp(PROP_SIZE_PROPVIEW_HEIGHT, Integer.class, "GEN-135", Boolean.FALSE,"PropView Pane Height",Integer.valueOf(0));
  }
  
  /**
   * Initializes the combo boxes.
   */
  protected void initComboBoxes()
  {
//    defThemeName = "Flat IntelliJ";
    defThemeName = "Arc Dark (Material)";
    
    cbThemes = new JComboBox<String>();
    for(ThemeInfo ti : Builder.themes) {
      cbThemes.addItem(ti.name);
    }
    themeCellEditor =  new DefaultCellEditor(cbThemes);

    if (Builder.isMAC) {
      defThemeName = UIManager.getSystemLookAndFeelClassName();
    }
    
    cbIDE = new JComboBox<String>();
    cbIDE.addItem(ProjectModel.IDE_ARDUINO);
    cbIDE.addItem(ProjectModel.IDE_PIO);
    ideCellEditor = new DefaultCellEditor(cbIDE);

    cbTarget = new JComboBox<String>();
    FontFactory ff = FontFactory.getInstance();
    for (FontGraphics p : ff.getBuilderFonts().getPlatforms()) {
      cbTarget.addItem(p.getName());
    }
    targetCellEditor = new DefaultCellEditor(cbTarget);
    
    List<String> envOptions = PlatformIO.getListEnv();
    cbPioEnv = new JComboBox<String>();
    for (String env : envOptions) {
      cbPioEnv.addItem(env);
    }
    pioenvCellEditor = new DefaultCellEditor(cbPioEnv);
  }
  
  /**
   * getRowCount gives back the number of user visible properties
   * it's less than the data[][] table size because we hide 
   * certain properties from users like recent colors.
   * 
   * @return the row count
   * @see javax.swing.table.TableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    return data.length-9;  
  }

  /**
   * setValueAt
   *
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    int test = 0;
    if (col == COLUMN_VALUE) {
      // check for invalid data
      if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        try {
          test = Integer.valueOf(Integer.parseInt((String)value));
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(null, "You entered non-numeric data in an number field.", 
              "Error", JOptionPane.ERROR_MESSAGE);
          Builder.logger.error("GM Row: " + row + " non-numeric data in an number field");
          return;
        }
      }
      if (row == PROP_ROTATION) {
        if (test < -1 || test > 3) {
          JOptionPane.showMessageDialog(null, 
              "Rotation must be 0 to 3 or -1 for no value", 
              "ERROR",
              JOptionPane.ERROR_MESSAGE);
          Builder.logger.error("PM: " + test + " Rotation must be 0 to 3 or -1 for no value");
          return;
        }
      }
      if (row == DISPLAY_WIDTH) {
        Builder.CANVAS_WIDTH = getWidth();
      }
      if (row == DISPLAY_HEIGHT) {
        Builder.CANVAS_HEIGHT = getHeight();
      }
//      changeValueAt(value, row);
      // commands are used to support undo and redo actions.
      PropertyCommand c = new PropertyCommand(this, value, row);
      execute(c);
    }
  }

  /**
   * Gets the theme class name.
   *
   * @return the theme class name
   */
  public String getThemeClassName() {
    // look-and-feel user setting
    if (Builder.isMAC) {
      return defThemeName;
    }
    String currentTheme = (String) data[PROP_THEME][PROP_VAL_VALUE];
    if (currentTheme == null || currentTheme.isEmpty())
      return defThemeName;
    return currentTheme;
  }
  
  /**
   * Gets the target platform
   *
   * @return the target platform
   */
  public String getTargetPlatform() {
    return (String) data[PROP_TARGET][PROP_VAL_VALUE];
  }

  /**
   * Gets the recent colors.
   *
   * @return the recent colors
   */
  public String getRecentColors() {
    return (String) data[PROP_RECENT_COLORS][PROP_VAL_VALUE];
  }

  /**
   * setRecentColors sets the recent colors
   * called by our color chooser.
   * @param s
   */
  public void setRecentColors(String s) { 
    shortcutValue(s, PROP_RECENT_COLORS);
  }

  /**
   * Gets the recent file list.
   *
   * @return the recent file list
   */
  public String getRecentFilesList() {
    return (String) data[PROP_RECENT_FILES][PROP_VAL_VALUE];
  }

  /**
   * setRecentFilesList sets the recent colors
   * called by our file chooser.
   * @param s
   */
  public void setRecentFilesList(String s) { 
    shortcutValue(s, PROP_RECENT_FILES);
  }

  public String getIDE() {
    return (String) data[PROP_IDE][PROP_VAL_VALUE];
  }
  
  public String getPioEnv() {
    return (String) data[PROP_PIO_ENV][PROP_VAL_VALUE];
  }
  
  /**
   * Gets the project dir.
   *
   * @return the project dir
   */
  public String getProjectDir() {
    return (String) data[PROP_PROJECT_DIR][PROP_VAL_VALUE];
  }

  /**
   * Gets the image dir.
   *
   * @return the image dir
   */
  public String getTargetImageDir() {
    return (String) data[PROP_TARGET_IMAGE_DIR][PROP_VAL_VALUE];
  }

  /**
   * Gets the image dir.
   *
   * @return the image dir
   */
  public String getImageDir() {
    return (String) data[PROP_IMAGE_DIR][PROP_VAL_VALUE];
  }

  /**
   * Sets the image dir.
   *
   * @param the image dir
   */
  public void setImageDir(String dir) {
    data[PROP_IMAGE_DIR][PROP_VAL_VALUE] = dir;
  }

 /**
  * getWidth
  *
  * @see builder.models.WidgetModel#getWidth()
  */
 @Override
  public int getWidth() {
    // return Integer.parseInt((String) data[PROP_WIDTH][PROP_VAL_VALUE]);
    return (((Integer) (data[DISPLAY_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setWidth
   *
   * @see builder.models.WidgetModel#setWidth(int)
   */
  @Override
  public void setWidth(int w) {
    shortcutValue(Integer.valueOf(w), DISPLAY_WIDTH);
  }

  /**
   * getHeight
   *
   * @see builder.models.WidgetModel#getHeight()
   */
  @Override
  public int getHeight() {
  //  return Integer.parseInt((String) data[PROP_HEIGHT][PROP_VAL_VALUE]);
    return (((Integer) (data[DISPLAY_HEIGHT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setHeight
   *
   * @see builder.models.WidgetModel#setHeight(int)
   */
  @Override
  public void setHeight(int h) {
    shortcutValue(Integer.valueOf(h), DISPLAY_HEIGHT);
  }

  /**
   * getAppWinWidth
   * 
   * @return the width
   */
  public int getAppWinWidth() {
    // return Integer.parseInt((String) data[PROP_WIDTH][PROP_VAL_VALUE]);
    return (((Integer) (data[PROP_SIZE_APP_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setAppWinWidth
   *
   * @param w
   *          the new width
   */
  public void setAppWinWidth(int w) {
    shortcutValue(Integer.valueOf(w), PROP_SIZE_APP_WIDTH);
  }

  /**
   * getAppWinHeight
   *
   * @return the height
   */
  public int getAppWinHeight() {
    // return Integer.parseInt((String) data[PROP_HEIGHT][PROP_VAL_VALUE]);
    return (((Integer) (data[PROP_SIZE_APP_HEIGHT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setAppWinHeight
   *
   * @param h
   *          the new height
   */
  public void setAppWinHeight(int h) {
    shortcutValue(Integer.valueOf(h), PROP_SIZE_APP_HEIGHT);
  }

  /**
   * getTFTWinWidth
   * 
   * @return the width
   */
  public int getTFTWinWidth() {
    // return Integer.parseInt((String) data[PROP_WIDTH][PROP_VAL_VALUE]);
    return (((Integer) (data[PROP_SIZE_TFT_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setTFTWinWidth
   *
   * @param w
   *          the new width
   */
  public void setTFTWinWidth(int w) {
    shortcutValue(Integer.valueOf(w), PROP_SIZE_TFT_WIDTH);
  }

  /**
   * getTFTWinHeight
   *
   * @return the height
   */
  public int getTFTWinHeight() {
    // return Integer.parseInt((String) data[PROP_HEIGHT][PROP_VAL_VALUE]);
    return (((Integer) (data[PROP_SIZE_TFT_HEIGHT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setTFTWinHeight
   *
   * @param h
   *          the new height
   */
  public void setTFTWinHeight(int h) {
    shortcutValue(Integer.valueOf(h), PROP_SIZE_TFT_HEIGHT);
  }

  /**
   * getPropWinWidth
   * 
   * @return the width
   */
  public int getPropWinWidth() {
    // return Integer.parseInt((String) data[PROP_WIDTH][PROP_VAL_VALUE]);
    return (((Integer) (data[PROP_SIZE_PROPVIEW_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setPropWinWidth
   *
   * @param w
   *          the new width
   */
  public void setPropWinWidth(int w) {
    shortcutValue(Integer.valueOf(w), PROP_SIZE_PROPVIEW_WIDTH);
  }

  /**
   * getPropWinHeight
   *
   * @return the height
   */
  public int getPropWinHeight() {
    // return Integer.parseInt((String) data[PROP_HEIGHT][PROP_VAL_VALUE]);
    return (((Integer) (data[PROP_SIZE_PROPVIEW_HEIGHT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * setPropWinHeight
   *
   * @param h
   *          the new height
   */
  public void setPropWinHeight(int h) {
    shortcutValue(Integer.valueOf(h), PROP_SIZE_PROPVIEW_HEIGHT);
  }

  /**
   * Gets the fill color.
   *
   * @return the fill color
   */
  public Color getBackgroundColor() {
    return (((Color) data[PROP_BACKGROUND][PROP_VAL_VALUE]));
  }

  /**
   * Gets the transparency color.
   *
   * @return the transparency color
   */
  public Color getTransparencyColor() {
    return (((Color) data[PROP_TRANSPARENCY_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the margins.
   *
   * @return the margins
   */
  public int getMargins() {
    return (((Integer) (data[PROP_MARGINS][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the h spacing.
   *
   * @return the h spacing
   */
  public int getHSpacing() {
    return (((Integer) (data[PROP_HSPACING][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the v spacing.
   *
   * @return the v spacing
   */
  public int getVSpacing() {
    return (((Integer) (data[PROP_VSPACING][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the max str.
   *
   * @return the max str
   */
  public int getMaxStr() {
    return (((Integer) (data[PROP_MAX_STRING][PROP_VAL_VALUE])).intValue());
  }

  /**
   * getScreenRotation
   *
   */
  public int getScreenRotation() {
    return (((Integer) (data[PROP_ROTATION][PROP_VAL_VALUE])).intValue());
  }

  /**
   * is Backward Compatibility Mode?
   *
   * @return <code>true</code>, if Backward Compatibility Mode is to be used
   */
  public boolean isBackwardCompat() {
    return ((Boolean) data[PROP_BACKWARD_COMPAT][PROP_VAL_VALUE]).booleanValue();
  }
  
  /**
   * isRoundTripEdits
   *
   * @return <code>true</code>, if user wants Round Trip Edits for Existing Code
   */
  public boolean isPreserveButtonCallbacks() {
    return ((Boolean) data[PROP_PRESERVE_BTN_CALLBACKS][PROP_VAL_VALUE]).booleanValue();
  }
  
  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int row) {
    if (row == PROP_THEME)
      return themeCellEditor;
    else if (row == PROP_IDE)
      return ideCellEditor;
    else if (row == PROP_TARGET)
      return targetCellEditor;
    else if (row == PROP_PIO_ENV)
      return pioenvCellEditor;
    return null;
  }

  /**
   * changeValueAt
   *
   * @see builder.models.WidgetModel#changeValueAt(java.lang.Object, int)
   */
  @Override
  public void changeValueAt(Object value, int row) {
    // The test for Integer. supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      data[row][PROP_VAL_VALUE] = value;
    }
    if (row == PROP_IDE) {
      if (getIDE().equals(ProjectModel.IDE_PIO)) {
        if (PlatformIO.isPlatformIO_INI_Present())
          data[PROP_PIO_ENV][PROP_VAL_READONLY] = true;
        else
          data[PROP_PIO_ENV][PROP_VAL_READONLY] = false;
      } else {
        data[PROP_PIO_ENV][PROP_VAL_READONLY] = true;
      }
    }
    fireTableCellUpdated(row, COLUMN_VALUE);
  }

  /**
   * Sets the read only properties and any other items 
   * needed at startup.
   * 
   * Called by GeneralEditor on startup Basically this 
   * replaces a subclassed readModel() since we don't 
   * serialize GeneralModel for save and restores.
   * It's saved wherever java stores UserPrefences (registry for windows).
   */
  public void setReadOnlyProperties() {
    if (getTargetPlatform().equals("arduino")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "Adafruit_GFX";
    }
    if (getTargetPlatform().equals("tft_espi")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "TFT_eSPI";
    }
    if (getTargetPlatform().equals("teensy")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "ILI9341_t3";
    }
    if (getTargetPlatform().equals("m5stack")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "M5Stack";
    }
    if (getTargetPlatform().equals("utft")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "UTFT";
    }
    if (getTargetPlatform().equals("linux")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "Linux";
    }
    if (getTargetPlatform().equals("arduino TFT_eSPI")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "TFT_eSPI";
    }
    if (getIDE().equals(ProjectModel.IDE_PIO)) {
      data[PROP_PIO_ENV][PROP_VAL_READONLY] = false;
    } else {
      data[PROP_PIO_ENV][PROP_VAL_READONLY] = true;
    }
    if (getIDE().equals(ProjectModel.IDE_PIO)) {
      if (PlatformIO.isPlatformIO_INI_Present())
        data[PROP_PIO_ENV][PROP_VAL_READONLY] = true;
      else
        data[PROP_PIO_ENV][PROP_VAL_READONLY] = false;
    } else {
      data[PROP_PIO_ENV][PROP_VAL_READONLY] = true;
    }
    Builder.CANVAS_WIDTH = getWidth();
    Builder.CANVAS_HEIGHT = getHeight();
  }


}
