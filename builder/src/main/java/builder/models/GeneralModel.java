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
package builder.models;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;

import builder.Builder;
import builder.commands.PropertyCommand;
import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.common.FontPlatform;
import builder.common.ThemeInfo;
import builder.controller.Controller;
import builder.tables.ImageCellEditor;

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
  public static final int PROP_TARGET               = 2;
  public static final int DISPLAY_WIDTH             = 3;
  public static final int DISPLAY_HEIGHT            = 4;
  public static final int DISPLAY_DPI               = 5;
  public static final int PROP_PROJECT_DIR          = 6;
  public static final int PROP_TARGET_IMAGE_DIR     = 7;
  public static final int PROP_BACKGROUND           = 8;
  public static final int PROP_USE_BACKGROUND_IMAGE = 9;
  public static final int PROP_BACKGROUND_IMAGE     = 10; 
  public static final int PROP_BACKGROUND_IMAGE_FNAME =11; // full pathname to a background image file
  public static final int PROP_BACKGROUND_DEFINE    = 12;
  public static final int PROP_BACKGROUND_MEMORY    = 13;
  public static final int PROP_BACKGROUND_FORMAT    = 14;
  public static final int PROP_TRANSPARENCY_COLOR   = 15;
  public static final int PROP_MARGINS              = 16;
  public static final int PROP_HSPACING             = 17;
  public static final int PROP_VSPACING             = 18;
  public static final int PROP_MAX_STRING           = 19;
  public static final int PROP_ROTATION             = 20;
  public static final int PROP_BACKWARD_COMPAT      = 21;
  public static final int PROP_PRESERVE_BTN_CALLBACKS = 22;
  public static final int PROP_IMAGE_DIR            = 23; // last folder used to load image
  // The following properties are hidden from users
  public static final int PROP_RECENT_COLORS        = 24; // LRU of recent colors choosen
  public static final int PROP_RECENT_FILES         = 25; // LRU of recent files choosen
  /* window sizes are hidden from the users because if you change one
   * the other values must change in proportion. It's much easier to
   * simply keep track of when users drag a window and record the values.
   */
  public static final int PROP_SIZE_APP_WIDTH       = 26; // Size of App Window 
  public static final int PROP_SIZE_APP_HEIGHT      = 27; 
  public static final int PROP_SIZE_TFT_WIDTH       = 28; // Size of TFT Simulation Pane
  public static final int PROP_SIZE_TFT_HEIGHT      = 29; 
  public static final int PROP_SIZE_PROPVIEW_WIDTH  = 30; // Size of Property View Pane 
  public static final int PROP_SIZE_PROPVIEW_HEIGHT = 31; 
  
  /** The Property Defaults */
  static public  final String  DEF_TARGET              = "arduino";
  static public  final Integer DEF_WIDTH               = Integer.valueOf(320);
  static public  final Integer DEF_HEIGHT              = Integer.valueOf(240);
  static public  final Integer DEF_DPI                 = Integer.valueOf(144);
  static public  final String  DEF_PROJECT_DIR         = "projects";
  static public  final String  DEF_TARGET_IMAGE_DIR    = "/";
  static public  final Color   DEF_BACKGROUND          = Color.BLACK;
  static public  final Boolean DEF_USE_BACKGROUND_IMAGE = Boolean.FALSE;
  static public  final String  DEF_BACKGROUND_IMAGE    = "";
  static public  final String  DEF_BACKGROUND_DEFINE   = "";
  static public  final String  DEF_BACKGROUND_EXTERN   = "";
  static public  final String  DEF_BACKGROUND_MEMORY   = "";
  static public  final String  DEF_BACKGROUND_FORMAT   = "";
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

  /** The cb target. */
  JComboBox<String> cbTarget;
  
  /** The target cell editor. */
  DefaultCellEditor targetCellEditor;
  
  /** The default theme name */
  public static String defThemeName;

  /** The background image. */
  private BufferedImage image = null;

  /** The cb memory. */
  JComboBox<String> cbMemory;
  
  /** The memory cell editor. */
  DefaultCellEditor memoryCellEditor;

  /** The cb format. */
  JComboBox<String> cbFormat;
  
  /** The format cell editor. */
  DefaultCellEditor formatCellEditor;

  /** The image cell editor. */
  ImageCellEditor imageCellEditor;

  public  final static String SRC_SD   = "gslc_GetImageFromSD((const char*)";
//  public  final static String SRC_PROG = "gslc_GetImageFromProg((const unsigned char*)";
//  public  final static String SRC_RAM  = "gslc_GetImageFromRam((unsigned char*)";
  public  final static String SRC_FILE = "gslc_GetImageFromFile(";

  /** format Constants */
  public  final static String FORMAT_BMP24  = "GSLC_IMGREF_FMT_BMP24";
  public  final static String FORMAT_BMP16  = "GSLC_IMGREF_FMT_BMP16";
  public  final static String FORMAT_RAW    = "GSLC_IMGREF_FMT_RAW";
  
  /**
   * Instantiates a new general model.
   */
  public GeneralModel() {
    initThemes();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.GENERAL;
    data = new Object[32][5];

    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_THEME, String.class, "GEN-100", Boolean.FALSE,"Themes","");
    if (Builder.isMAC) {
      data[PROP_THEME][PROP_VAL_READONLY]= Boolean.TRUE;
    }
    initProp(PROP_TARGET, String.class, "GEN-101", Boolean.FALSE,"Target Platform",DEF_TARGET);

    initProp(DISPLAY_WIDTH, Integer.class, "GEN-102", Boolean.FALSE,"TFT Screen Width",DEF_WIDTH);
    initProp(DISPLAY_HEIGHT, Integer.class, "GEN-103", Boolean.FALSE,"TFT Screen Height",DEF_HEIGHT);
    initProp(DISPLAY_DPI, Integer.class, "GEN-104", Boolean.FALSE,"TFT Screen DPI",DEF_DPI);

    initProp(PROP_PROJECT_DIR, String.class, "GEN-105", Boolean.FALSE,"Project Directory",DEF_PROJECT_DIR);
    initProp(PROP_TARGET_IMAGE_DIR, String.class, "GEN-106", Boolean.FALSE,
        "Target Platform Image Directory",DEF_TARGET_IMAGE_DIR);

    initProp(PROP_BACKGROUND, Color.class, "COL-310", Boolean.FALSE,"Background Color",DEF_BACKGROUND);
    initProp(PROP_USE_BACKGROUND_IMAGE, Boolean.class, "COM-020", Boolean.FALSE,
        "Use Background Image?",DEF_USE_BACKGROUND_IMAGE);
    initProp(PROP_BACKGROUND_IMAGE, String.class, "GEN-117", Boolean.TRUE,"Background Image Path","");
    initProp(PROP_BACKGROUND_IMAGE_FNAME, String.class, "GEN-118", Boolean.TRUE,"Background Image Name","");
    initProp(PROP_BACKGROUND_DEFINE, String.class, "IMG-101", Boolean.TRUE,"Background Image #defines",DEF_BACKGROUND_DEFINE);
//    initProp(PROP_BACKGROUND_EXTERN, String.class, "IMG-108", Boolean.TRUE,"Background Image Extern",DEF_BACKGROUND_EXTERN);
    initProp(PROP_BACKGROUND_MEMORY, String.class, "IMG-109", Boolean.TRUE,"Background Image Memory",DEF_BACKGROUND_MEMORY);
    initProp(PROP_BACKGROUND_FORMAT, String.class, "IMG-102", Boolean.TRUE,"Background Image Format",DEF_BACKGROUND_FORMAT);

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
   * Initializes the themes.
   */
  protected void initThemes()
  {
    defThemeName = "Flat IntelliJ";
    
    cbThemes = new JComboBox<String>();
    for(ThemeInfo ti : Builder.themes) {
      cbThemes.addItem(ti.name);
    }
    themeCellEditor =  new DefaultCellEditor(cbThemes);

    if (Builder.isMAC) {
      defThemeName = UIManager.getSystemLookAndFeelClassName();
    }
    
    cbTarget = new JComboBox<String>();
    FontFactory ff = FontFactory.getInstance();
    for (FontPlatform p : ff.getBuilderFonts().getPlatforms()) {
      cbTarget.addItem(p.getName());
    }
    targetCellEditor = new DefaultCellEditor(cbTarget);
    
    imageCellEditor = new ImageCellEditor();

    cbMemory = new JComboBox<String>();
    cbMemory.addItem(SRC_SD);
    cbMemory.addItem(SRC_FILE);
//    cbMemory.addItem(SRC_PROG);
//    cbMemory.addItem(SRC_RAM);
    memoryCellEditor = new DefaultCellEditor(cbMemory);
    
    cbFormat = new JComboBox<String>();
    cbFormat.addItem(FORMAT_BMP24);
    cbFormat.addItem(FORMAT_BMP16);
    cbFormat.addItem(FORMAT_RAW);
    formatCellEditor = new DefaultCellEditor(cbFormat);
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
    return data.length-8;  
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
   * Gets the target.
   *
   * @return the target
   */
  public String getTarget() {
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
   * Gets the dpi.
   *
   * @return the dpi
   */
  public int getDPI() {
    return (((Integer) (data[DISPLAY_DPI][PROP_VAL_VALUE])).intValue());
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
   * Use Background image.
   *
   * @return <code>true</code>, if background image is to be used
   */
  public boolean useBackgroundImage() {
    return ((Boolean) data[PROP_USE_BACKGROUND_IMAGE][PROP_VAL_VALUE]).booleanValue();
  }
  
 /**
   * Gets the background image define.
   *
   * @return the define
   */
  public String getBackgroundDefine() {
    return (String) data[PROP_BACKGROUND_DEFINE][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the define.
   *
   * @param s
   *          the new define
   */
  public void setBackgroundDefine(String s) {
    data[PROP_BACKGROUND_DEFINE][PROP_VAL_VALUE] = (String)s;
  }
  
  /**
   * Gets the background extern name.
   *
   * @return the extern name
   */
//  public String getBackgroundExtern() {
//    return (String) data[PROP_BACKGROUND_EXTERN][PROP_VAL_VALUE];
//  }
  
  /**
   * Sets the background extern name.
   *
   * @param name
   *          the new extern name
   */
//  public void setBackgroundExtern(String name) {
//    data[PROP_BACKGROUND_EXTERN][PROP_VAL_VALUE] = (String)name;
//  }

  /**
   * Gets the background image memory type.
   *
   * @return the memory type
   */
  public String getBackgroundMemory() {
    return (String) data[PROP_BACKGROUND_MEMORY][PROP_VAL_VALUE];
  }
  
 /**
  * Gets the background image name on target machine.
  *
  * @return the image name
  */
 public String getBackgroundImageTName() {
   String dir = getTargetImageDir();
   String name = (String) data[PROP_BACKGROUND_IMAGE_FNAME][PROP_VAL_VALUE];
   // do we need to add a relative path for code generation?
   if (dir.length() > 0)
     name = dir + name;

   return name;
 }
 
 /**
  * Sets the background image file name full path
  *
  * @param name
  *          the new image name
  */
 public void setBackgroundImageName(String name) {
   data[PROP_BACKGROUND_IMAGE][PROP_VAL_VALUE] = (String)name;
 }

 /**
  * Gets the background image file full path name
  *
  * @return the image name
  */
 public String getBackgroundImageName() {
   return (String) data[PROP_BACKGROUND_IMAGE][PROP_VAL_VALUE];
 }
 
 /**
  * Gets the background image file simple name
  *
  * @return the image name
  */
 public String getBackgroundImageFName() {
   return (String) data[PROP_BACKGROUND_IMAGE_FNAME][PROP_VAL_VALUE];
 }
 
 /**
  * Sets the background image file full path.
  *
  * @param name
  *          the new image name
  */
 public void setBackgroundImageFName(String name) {
   data[PROP_BACKGROUND_IMAGE_FNAME][PROP_VAL_VALUE] = (String)name;
 }

 /**
  * Gets the background image format.
  *
  * @return the image format
  */
 public String getBackgroundFormat() {
   return (String) data[PROP_BACKGROUND_FORMAT][PROP_VAL_VALUE];
 }
 
 /**
  * Sets the image format.
  *
  * @param name
  *          the new image format
  */
 public void setBackgroundFormat(String name) {
   data[PROP_BACKGROUND_FORMAT][PROP_VAL_VALUE]=(String)name;
 }

  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int rowIndex) {
    if (rowIndex == PROP_THEME)
      return themeCellEditor;
    else if (rowIndex == PROP_TARGET)
      return targetCellEditor;
    else if (rowIndex == PROP_BACKGROUND_MEMORY)
      return memoryCellEditor;
    else if (rowIndex == PROP_BACKGROUND_IMAGE)
      return imageCellEditor;
    else if (rowIndex == PROP_BACKGROUND_FORMAT)
      return formatCellEditor;
    return null;
  }

  /**
   * Gets the image.
   *
   * @return the image
   */
  public BufferedImage getImage() {
    return image;
  }

  /**
   * Sets the image selected.
   *
   * @param file
   *          the new image selected
   */
  public void setImage(String fileName) {
    image = null;
    File file = new File(fileName);
    try {
      image = ImageIO.read(file);
    } catch(IOException e) {
      Builder.logger.error("GM image read error: " + e.getMessage());
    }
    // save the full path so we can restore on program startup
    setBackgroundImageName(file.getAbsolutePath()); 
    // save the name without the full path
    setBackgroundImageFName(file.getName());
    // now construct a #define to use during code generation
    String name = "IMG_BKGND";
    setBackgroundDefine(name);
    if (image.getType() == BufferedImage.TYPE_3BYTE_BGR)
      setBackgroundFormat("GSLC_IMGREF_FMT_BMP24");
    else if (image.getType() == BufferedImage.TYPE_USHORT_555_RGB) 
      setBackgroundFormat("GSLC_IMGREF_FMT_BMP16");
    else
      setBackgroundFormat("GSLC_IMGREF_FMT_RAW1");
    if (Controller.getTargetPlatform().equals(ProjectModel.PLATFORM_LINUX))
      data[PROP_BACKGROUND_MEMORY][PROP_VAL_VALUE] = SRC_FILE;
    else if (Controller.getTargetPlatform().equals(ProjectModel.PLATFORM_TFT_ESPI) &&
        file.getName().toLowerCase().endsWith(".jpg"))
      data[PROP_BACKGROUND_MEMORY][PROP_VAL_VALUE] = SRC_FILE;
    else      
      data[PROP_BACKGROUND_MEMORY][PROP_VAL_VALUE] = SRC_SD;
    data[PROP_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
    data[PROP_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.FALSE;
    data[PROP_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.FALSE;
  }

  /**
   * changeValueAt
   *
   * @see builder.models.WidgetModel#changeValueAt(java.lang.Object, int)
   */
  @Override
  public void changeValueAt(Object value, int row) {
    if (row == PROP_BACKGROUND_IMAGE) {
      String fileName = (String)value;
      if (!fileName.isEmpty()) {
          setImage(fileName);
      } else {
        data[PROP_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_USE_BACKGROUND_IMAGE][PROP_VAL_VALUE] = Boolean.FALSE;
        data[PROP_BACKGROUND_IMAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        image = null;
      }
      fireTableCellUpdated(PROP_USE_BACKGROUND_IMAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_BACKGROUND_DEFINE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_BACKGROUND_MEMORY, COLUMN_VALUE);
      fireTableCellUpdated(PROP_BACKGROUND_FORMAT, COLUMN_VALUE);
      fireTableCellUpdated(PROP_BACKGROUND_IMAGE_FNAME, COLUMN_VALUE);
      fireTableCellUpdated(PROP_BACKGROUND_IMAGE, COLUMN_VALUE);
      return;
    }
    // The test for Integer. supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      data[row][PROP_VAL_VALUE] = value;
    }
    fireTableCellUpdated(row, COLUMN_VALUE);
    if (row == PROP_USE_BACKGROUND_IMAGE) {
      if (useBackgroundImage()) {
        data[PROP_BACKGROUND_IMAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      } else {
        setBackgroundImageName("");
        setBackgroundImageFName("");
        data[PROP_BACKGROUND_MEMORY][PROP_VAL_VALUE] = "";
        data[PROP_BACKGROUND_DEFINE][PROP_VAL_VALUE] = "";
        data[PROP_BACKGROUND_FORMAT][PROP_VAL_VALUE] = "";
        data[PROP_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_BACKGROUND_IMAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        fireTableCellUpdated(PROP_BACKGROUND_MEMORY, COLUMN_VALUE);
        fireTableCellUpdated(PROP_BACKGROUND_DEFINE, COLUMN_VALUE);
        fireTableCellUpdated(PROP_BACKGROUND_FORMAT, COLUMN_VALUE);
        fireTableCellUpdated(PROP_BACKGROUND_IMAGE_FNAME, COLUMN_VALUE);
      }
      fireTableCellUpdated(PROP_BACKGROUND_IMAGE, COLUMN_VALUE);
    }
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
    if (!getBackgroundImageName().isEmpty()) {
      File file = new File(getBackgroundImageName());
      try {
        image = ImageIO.read(file);
//        setBackgroundImageName(file.getName());
      } catch(IOException e) {
        Builder.logger.error("image: " + e.getMessage());
      }
      data[PROP_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      image = null;
      data[PROP_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_BACKGROUND_IMAGE][PROP_VAL_READONLY]=Boolean.TRUE;
    }
    if (getTarget().equals("arduino TFT_eSPI")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "tft_espi";
    }
  }


}
