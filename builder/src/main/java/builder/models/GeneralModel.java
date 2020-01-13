/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.TableCellEditor;

import builder.Builder;
import builder.commands.PropertyCommand;
import builder.common.EnumFactory;
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
  // The following properties are hidden from users
  public static final int PROP_IMAGE_DIR            = 21; // last folder used to load image
  public static final int PROP_RECENT_COLORS        = 22; // LRU of recent colors choosen (reset each startup)
  
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
  
  /* Look and Feel */
  public static final String LAF_AUTUMNSKIN             = "AutumnSkin";
  public static final String LAF_BUSINESSBLACKSTEELSKIN = "BusinessBlackSteelSkin";
  public static final String LAF_BUSINESSBLUESTEELSKIN  = "BusinessBlueSteelSkin";
  public static final String LAF_CHALLENGERDEEPSKIN     = "ChallengerDeepSkin";
  public static final String LAF_CREMECOFFEESKIN        = "CremeCoffeeSkin";
  public static final String LAF_DUSTSKIN               = "DustSkin";
  public static final String LAF_GRAPHITEAQUASKIN       = "GraphiteAquaSkin";
  public static final String LAF_MARINERSKIN            = "MarinerSkin";
  public static final String LAF_MISTAQUASKIN           = "MistAquaSkin";
  public static final String LAF_MISTSILVERSKIN         = "MistSilverSkin";
  public static final String LAF_OFFICEBLACK2007SKIN    = "OfficeBlack2007Skin";
  public static final String LAF_OFFICEBLUE2007SKIN     = "OfficeBlue2007Skin";
  public static final String LAF_OFFICESILVER2007SKIN   = "OfficeSilver2007Skin";
  public static final String LAF_SAHARASKIN             = "SaharaSkin";

  /** The type strings. */
  static public String[] lafObjs = {
    LAF_AUTUMNSKIN,
    LAF_BUSINESSBLACKSTEELSKIN,
    LAF_BUSINESSBLUESTEELSKIN,
    LAF_CHALLENGERDEEPSKIN,
    LAF_CREMECOFFEESKIN,
    LAF_DUSTSKIN,
    LAF_GRAPHITEAQUASKIN,
    LAF_MARINERSKIN,
    LAF_MISTAQUASKIN,
    LAF_OFFICEBLACK2007SKIN,
    LAF_OFFICEBLUE2007SKIN,
    LAF_OFFICESILVER2007SKIN,
    LAF_SAHARASKIN
  };
    
  /** The themes. */
  public static List<String> themes;
  
  /** The theme class names. */
  public static List<String> themeClassNames;
  
  /** The idx theme. */
  public static int idxTheme = 0;
  
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

  /** The default theme class */
  public static String defThemeClass;

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
    data = new Object[23][5];

    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_THEME, String.class, "GEN-100", Boolean.FALSE,"Theme",defThemeName);
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
    initProp(PROP_IMAGE_DIR, String.class, "GEN-113", Boolean.FALSE,"Last Image Directory Accessed","");
    initProp(PROP_RECENT_COLORS, String.class, "GEN-111", Boolean.TRUE,"Recent Colors","");
  }
  
  /**
   * Initializes the themes.
   */
  protected void initThemes()
  {
    themes = new ArrayList<String>();
    themeClassNames = new ArrayList<String>();
    defThemeClass = UIManager.getSystemLookAndFeelClassName();
    idxTheme = 0;
    if (!Builder.isMAC) {
      for (LookAndFeelInfo look_and_feel : UIManager.getInstalledLookAndFeels()) {
        themes.add(look_and_feel.getName());
        themeClassNames.add(look_and_feel.getClassName());
      }
      if (Builder.isSubstanceSupported) {
     		for (int i = 0; i < lafObjs.length; i++) {
          themes.add(lafObjs[i]);
          themeClassNames.add(lafObjs[i]);
        }
      }
        
      cbThemes = new JComboBox<String>();
      int i = 0;
      String testClass;
      for(String t : themes) {
        cbThemes.addItem(t);
        testClass = themeClassNames.get(i);
        if (testClass.equals(defThemeClass)) {
          defThemeName = t;
          idxTheme = i;
        }
        i++;
      }
      cbThemes.setSelectedIndex(idxTheme);
      
      themeCellEditor =  new DefaultCellEditor(cbThemes);
    } else {
      defThemeName = defThemeClass;
    }
    cbTarget = new JComboBox<String>();
    cbTarget.addItem("arduino");
    cbTarget.addItem("arduino TFT_eSPI");
    cbTarget.addItem("linux");
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
   * its 2 less than the data[][] table size because we hide 
   * recent colors and last image dir accessed from the user.
   * 
   * @return the row count
   * @see javax.swing.table.TableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    return data.length-1;  
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
          return;
        }
      }
      if (row == PROP_ROTATION) {
        if (test < -1 || test > 3) {
          JOptionPane.showMessageDialog(null, 
              "Rotation must be 0 to 3 or -1 for no value", 
              "ERROR",
              JOptionPane.ERROR_MESSAGE);
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
      return defThemeClass;
    }
    String currentTheme = (String) data[PROP_THEME][PROP_VAL_VALUE];
    if (currentTheme == null)
      return defThemeClass;
    for (int j = 0; j < themes.size(); j++) {
      if (currentTheme.equals(themes.get(j)))
        idxTheme = j;
    }
    return themeClassNames.get(idxTheme);
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
   * setTarget sets the target platform
   * @param s
   */
  public void setTarget(String s) { 
    shortcutValue(s, PROP_TARGET);
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
        System.out.println("read error: " + e.getMessage());
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
    if (getTarget().equals("linux"))
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
        image = null;
      }
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
          System.out.println("read error: " + e.getMessage());
      }
      data[PROP_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      image = null;
      data[PROP_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
    }
  }


}
