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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import builder.Builder;
import builder.codegen.PlatformIO;
import builder.commands.PropertyCommand;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.fonts.FontFactory;
import builder.fonts.FontGraphics;
import builder.tables.ImageCellEditor;
import builder.tables.MultiStringsCell;
import builder.tables.MultipeLineCellListener;
import builder.tables.MultiStringsCell.MCDialogType;
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeElement;

/**
 * The Class ProjectModel implements the model for the builder.
 * 
 * @author Paul Conti
 *  
 */
public class ProjectModel extends PageModel implements MultipeLineCellListener {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID  = 1L;
  
  /** Target IDE */
  public static final String IDE_ARDUINO       = "Arduino IDE";
  public static final String IDE_PIO           = "PlatformIO IDE";
  public static final int    ARDUINO_IDE_ID    = 1;
  public static final int    PIO_IDE_ID        = 2;
  

  /** Target Platforms */
  public static final String PLATFORM_ARDUINO  = "Adafruit_GFX";
  public static final String PLATFORM_TFT_ESPI = "TFT_eSPI";
  public static final String PLATFORM_LINUX    = "Linux";
  public static final String PLATFORM_UTFT     = "UTFT";
  public static final String PLATFORM_M5STACK  = "M5Stack";
  
  /** The Property Index Constants. */
  public static final int PROP_IDE                  = 2;
  public static final int PROP_PIO_ENV              = 3;
  private static final String  DEF_GUISLICE_DEFAULT_THEME = "GUIslice";
  public static final int PROP_TARGET               = 4;
  public static final int PROP_GUISLICE_THEME       = 5;
  public static final int PROP_FONT_LIST            = 6;
  public static final int DISPLAY_WIDTH             = 7;
  public static final int DISPLAY_HEIGHT            = 8;
  public static final int PROP_BACKGROUND           = 9;
  public static final int PROP_USE_IMAGE_BACKGROUND = 10;
  public static final int PROP_TARGET_IMAGE_DIR     = 11;
  public static final int PROP_IMAGE_BACKGROUND_FILE      = 12; 
  public static final int PROP_IMAGE_BACKGROUND_DEFINE    = 13;
  public static final int PROP_IMAGE_BACKGROUND_MEMORY    = 14;
  public static final int PROP_IMAGE_BACKGROUND_FORMAT    = 15;
  public static final int PROP_MARGINS              = 16;
  public static final int PROP_HSPACING             = 17;
  public static final int PROP_VSPACING             = 18;
  public static final int PROP_MAX_STRING           = 19;
  public static final int PROP_ROTATION             = 20;
  
  /** The Property Defaults */
  static public  final String  DEF_IDE                 = "Arduino IDE";
  static public  final String  DEF_TARGET              = "Adafruit_GFX";
  static public  final String[] DEF_INCLUDES           = { "" };
  static public  final Integer DEF_WIDTH               = Integer.valueOf(320);
  static public  final Integer DEF_HEIGHT              = Integer.valueOf(240);
  static public  final Color   DEF_BACKGROUND          = Color.BLACK;
  static public  final Boolean DEF_USE_BACKGROUND_IMAGE = Boolean.FALSE;
  static public  final String  DEF_TARGET_IMAGE_DIR    = "/";
  static public  final String  DEF_BACKGROUND_IMAGE    = "";
  static public  final String  DEF_BACKGROUND_DEFINE   = "";
  static public  final String  DEF_BACKGROUND_EXTERN   = "";
  static public  final String  DEF_BACKGROUND_MEMORY   = "";
  static public  final String  DEF_BACKGROUND_FORMAT   = "";
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
  
  /** The cb GUIslice themes. */
  public static JComboBox<String> cbGUIsliceThemes;
  
  /** The GUIslice theme cell editor. */
  DefaultCellEditor  guisliceThemeCellEditor;

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
  
  FontFactory ff;


  public  final static String SRC_SD   = "gslc_GetImageFromSD((const char*)";
//  public  final static String SRC_PROG = "gslc_GetImageFromProg((const unsigned char*)";
//  public  final static String SRC_RAM  = "gslc_GetImageFromRam((unsigned char*)";
  public  final static String SRC_FILE = "gslc_GetImageFromFile(";

  /** format Constants */
  public  final static String FORMAT_BMP24  = "GSLC_IMGREF_FMT_BMP24";
  public  final static String FORMAT_BMP16  = "GSLC_IMGREF_FMT_BMP16";
  public  final static String FORMAT_RAW    = "GSLC_IMGREF_FMT_RAW";
  
  /** The list of font includes */
  MultiStringsCell fontsListCell;

  /**
   * Instantiates a new general model.
   */
  public ProjectModel() {
    ff = FontFactory.getInstance();
    initComboBoxes();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.PROJECT + "$1";
    data = new Object[21][5];

    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_ENUM, String.class, "COM-002", Boolean.FALSE,"ENUM","E_PROJECT_OPTIONS");
    initProp(PROP_IDE, String.class, "GEN-098", Boolean.FALSE,"Target IDE",DEF_IDE);
    initProp(PROP_PIO_ENV, String.class, "GEN-099", Boolean.TRUE,"PlatformIO default_envs","");
    initProp(PROP_TARGET, String.class, "GEN-101", Boolean.FALSE,"Graphics Library",DEF_TARGET);
    initProp(PROP_GUISLICE_THEME, String.class, "GEN-097", Boolean.FALSE,"GUIslice API Theme",
        DEF_GUISLICE_DEFAULT_THEME);

    initProp(PROP_FONT_LIST, String[].class, "LIST-108", Boolean.FALSE,
        "Project's Extra Fonts",DEF_INCLUDES);

    initProp(DISPLAY_WIDTH, Integer.class, "GEN-102", Boolean.FALSE,"TFT Screen Width",DEF_WIDTH);
    initProp(DISPLAY_HEIGHT, Integer.class, "GEN-103", Boolean.FALSE,"TFT Screen Height",DEF_HEIGHT);

    initProp(PROP_BACKGROUND, Color.class, "COL-310", Boolean.FALSE,"Background Color",DEF_BACKGROUND);

    initProp(PROP_USE_IMAGE_BACKGROUND, Boolean.class, "PRJ-116", Boolean.FALSE,
        "Use Background Image?",DEF_USE_BACKGROUND_IMAGE);
    initProp(PROP_TARGET_IMAGE_DIR, String.class, "GEN-106", Boolean.FALSE,
        "Target Platform Image Directory",DEF_TARGET_IMAGE_DIR);
    initProp(PROP_IMAGE_BACKGROUND_FILE, String.class, "PRJ-117", Boolean.TRUE,"Background Image File","");
    initProp(PROP_IMAGE_BACKGROUND_DEFINE, String.class, "IMG-101", Boolean.TRUE,"Background Image #defines",DEF_BACKGROUND_DEFINE);
    initProp(PROP_IMAGE_BACKGROUND_MEMORY, String.class, "IMG-109", Boolean.TRUE,"Background Image Memory",DEF_BACKGROUND_MEMORY);
    initProp(PROP_IMAGE_BACKGROUND_FORMAT, String.class, "IMG-102", Boolean.TRUE,"Background Image Format",DEF_BACKGROUND_FORMAT);

    initProp(PROP_MARGINS, Integer.class, "GEN-107", Boolean.FALSE,"Screen Margins",DEF_MARGINS);
    initProp(PROP_HSPACING, Integer.class, "GEN-108", Boolean.FALSE,
        "Horizontal Spacing between widgets",DEF_HSPACING);
    initProp(PROP_VSPACING, Integer.class, "GEN-109", Boolean.FALSE,
        "Vertical Spacing between widgets",DEF_VSPACING);
    initProp(PROP_MAX_STRING, Integer.class, "GEN-110", Boolean.FALSE,"MAX_STR",DEF_MAX_STRING);
    initProp(PROP_ROTATION, Integer.class, "GEN-112", Boolean.FALSE,
        "Screen Rotation [0-3 or -1 default]",DEF_ROTATION);

  }
  
  /**
   * Initializes the combo boxes.
   */
  protected void initComboBoxes()
  {
    cbIDE = new JComboBox<String>();
    cbIDE.addItem(IDE_ARDUINO);
    cbIDE.addItem(IDE_PIO);
    ideCellEditor = new DefaultCellEditor(cbIDE);

    cbTarget = new JComboBox<String>();
    FontFactory ff = FontFactory.getInstance();
    for (FontGraphics p : ff.getBuilderFonts().getPlatforms()) {
      cbTarget.addItem(p.getName());
    }
    targetCellEditor = new DefaultCellEditor(cbTarget);
    
    cbGUIsliceThemes = new JComboBox<String>();
    for (String s : cf.getListofThemes()) {
      cbGUIsliceThemes.addItem(s);
    }
    guisliceThemeCellEditor = new DefaultCellEditor(cbGUIsliceThemes);
    
    List<String> envOptions = PlatformIO.getListEnv();
    cbPioEnv = new JComboBox<String>();
    for (String env : envOptions) {
      cbPioEnv.addItem(env);
    }
    pioenvCellEditor = new DefaultCellEditor(cbPioEnv);

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

    fontsListCell = new MultiStringsCell("Project's Extra Fonts",
        MCDialogType.FONT_DIALOG);
    fontsListCell.setData(DEF_INCLUDES);
    fontsListCell.addButtonListener(this);
  }
  
  /**
   * isCellEditable.
   *
   * @param row
   *          the JTable row in question
   * @param col
   *          the JTable col 
   * @return true, if is cell editable
   * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    if (col == 0) return false;
    return !isCellReadOnly(row);
  }

  /**
   * buttonClicked
   *
   * @see builder.tables.MultipeLineCellListener#buttonClicked(java.lang.String[])
   */
   @Override
   public void buttonClicked(String[] strings) {
     // commands are used to support undo and redo actions.
     PropertyCommand c = new PropertyCommand(this, strings, PROP_FONT_LIST);
     execute(c);
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
          Builder.logger.error("PM Row: " + row + " non-numeric data in an number field");
          return;
        }
      }
      if (row == PROP_FONT_LIST) {
        if ((Object)value == null || ((String)value).isEmpty())
          return;
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
      // commands are used to support undo and redo actions.
      PropertyCommand c = new PropertyCommand(this, value, row);
      execute(c);
    }
  }

  /**
   * getGUIsliceThemeName
   * @return
   */
  public String getGUIsliceThemeName() {
    return (String) data[PROP_GUISLICE_THEME][PROP_VAL_VALUE];
  }
  
  public String getIDE() {
    return (String) data[PROP_IDE][PROP_VAL_VALUE];
  }
  
  public int getIDE_ID() {
    if(((String) data[PROP_IDE][PROP_VAL_VALUE]).equals(IDE_ARDUINO)) {
      return ARDUINO_IDE_ID;
    }
    return PIO_IDE_ID;
  }
  
  public String getPioEnv() {
    return (String) data[PROP_PIO_ENV][PROP_VAL_VALUE];
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
   * Sets the target platform
   *
   * @param the target platform
   */
  public void  setTargetPlatform(String target) {
    changeValueAt(target, PROP_TARGET);
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
    changeValueAt(Integer.valueOf(w), DISPLAY_WIDTH);
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
    changeValueAt(Integer.valueOf(h), DISPLAY_HEIGHT);
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
    return ((Boolean) data[PROP_USE_IMAGE_BACKGROUND][PROP_VAL_VALUE]).booleanValue();
  }
  
 /**
   * Gets the background image define.
   *
   * @return the define
   */
  public String getBackgroundDefine() {
    return (String) data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the define.
   *
   * @param s
   *          the new define
   */
  public void setBackgroundDefine(String s) {
    data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_VALUE] = (String)s;
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
   * Gets the image dir.
   *
   * @return the image dir
   */
  public String getTargetImageDir() {
    return (String) data[PROP_TARGET_IMAGE_DIR][PROP_VAL_VALUE];
  }

  /**
   * Gets the background image memory type.
   *
   * @return the memory type
   */
  public String getBackgroundMemory() {
    return (String) data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_VALUE];
  }
  
 /**
  * Gets the background image name on target machine.
  *
  * @return the image name
  */
 public String getBackgroundImageTName() {
   String dir = getTargetImageDir();
   String name = (String) data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_VALUE];
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
   data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_VALUE] = (String)name;
 }

 /**
  * Gets the background image file full path name
  *
  * @return the image name
  */
 public String getBackgroundImageName() {
   return (String) data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_VALUE];
 }
 
 /**
  * Gets the background image format.
  *
  * @return the image format
  */
 public String getBackgroundFormat() {
   return (String) data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_VALUE];
 }
 
 /**
  * Sets the image format.
  *
  * @param name
  *          the new image format
  */
 public void setBackgroundFormat(String name) {
   data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_VALUE]=(String)name;
 }

  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int row) {
    if (row == PROP_FONT_LIST)
      return fontsListCell;
    else if (row == PROP_IDE)
      return ideCellEditor;
    else if (row == PROP_GUISLICE_THEME)
      return guisliceThemeCellEditor;
    else if (row == PROP_TARGET)
      return targetCellEditor;
    else if (row == PROP_IMAGE_BACKGROUND_MEMORY)
      return memoryCellEditor;
    else if (row == PROP_IMAGE_BACKGROUND_FILE)
      return imageCellEditor;
    else if (row == PROP_IMAGE_BACKGROUND_FORMAT)
      return formatCellEditor;
    else if (row == PROP_PIO_ENV)
      return pioenvCellEditor;
    return null;
  }

  /**
   * getRendererAt
   *
   * @see builder.models.WidgetModel#getRendererAt(int)
   */
  @Override
  public TableCellRenderer getRendererAt(int row) {
    if (row == PROP_FONT_LIST)
      return fontsListCell;
    return null;
  }

  /**
   * Gets the list fonts used by this project, if any.
   *
   * @return the list of fonts, may be empty
   */
  public String[] getFontsList() {
    return ((String[]) (data[PROP_FONT_LIST][PROP_VAL_VALUE]));
  }

  /**
   * Adds a font to the list of fonts used by this project
   * unless its already present.
   *
   */
  public void addFontToList(String fontName) {
     String[] list = ((String[]) (data[PROP_FONT_LIST][PROP_VAL_VALUE]));
     for (int i=0; i<list.length; i++) {
       if (fontName.equals(list[i])) return;
     }
     String[] newList = new String[list.length+1];
     for (int i=0; i<list.length; i++) {
       newList[i] = list[i];
     }
     newList[list.length] = fontName;
     Arrays.sort(newList);
     data[PROP_FONT_LIST][PROP_VAL_VALUE] = newList;
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
      Builder.logger.error("PM image read error: " + e.getMessage());
    }
    // save the name without the full path
    setBackgroundImageName(file.getName());
    // now construct a #define to use during code generation
    String name = "IMG_BKGND";
    setBackgroundDefine(name);
    if (image.getType() == BufferedImage.TYPE_3BYTE_BGR)
      setBackgroundFormat("GSLC_IMGREF_FMT_BMP24");
    else if (image.getType() == BufferedImage.TYPE_USHORT_555_RGB) 
      setBackgroundFormat("GSLC_IMGREF_FMT_BMP16");
    else
      setBackgroundFormat("GSLC_IMGREF_FMT_RAW1");
    if (getTargetPlatform().equals(ProjectModel.PLATFORM_LINUX))
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_VALUE] = SRC_FILE;
    else if (getTargetPlatform().equals(ProjectModel.PLATFORM_TFT_ESPI) &&
        file.getName().toLowerCase().endsWith(".jpg"))
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_VALUE] = SRC_FILE;
    else      
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_VALUE] = SRC_SD;
    data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
    data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.FALSE;
    data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.FALSE;
  }

  /**
   * changeValueAt
   *
   * @see builder.models.WidgetModel#changeValueAt(java.lang.Object, int)
   */
  @Override
  public void changeValueAt(Object value, int row) {
    if (row == PROP_IMAGE_BACKGROUND_FILE) {
      String fileName = (String)value;
      if (!fileName.isEmpty()) {
          setImage(fileName);
      } else {
        data[PROP_USE_IMAGE_BACKGROUND][PROP_VAL_VALUE] = Boolean.FALSE;
        data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_READONLY]=Boolean.TRUE;
        image = null;
      }
      fireTableCellUpdated(PROP_USE_IMAGE_BACKGROUND, COLUMN_VALUE);
      fireTableCellUpdated(PROP_IMAGE_BACKGROUND_DEFINE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_IMAGE_BACKGROUND_MEMORY, COLUMN_VALUE);
      fireTableCellUpdated(PROP_IMAGE_BACKGROUND_FORMAT, COLUMN_VALUE);
      fireTableCellUpdated(PROP_IMAGE_BACKGROUND_FILE, COLUMN_VALUE);
      return;
    }
    // The test for Integer. supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      if (row == PROP_FONT_LIST) 
        Arrays.sort((String[]) value);
      data[row][PROP_VAL_VALUE] = value;
    }
    fireTableCellUpdated(row, COLUMN_VALUE);
    if (row == DISPLAY_WIDTH) {
      Builder.CANVAS_WIDTH = getWidth();
    }
    if (row == DISPLAY_HEIGHT) {
      Builder.CANVAS_HEIGHT = getHeight();
    }
    if (row == PROP_IDE) {
      if (getIDE().equals(ProjectModel.IDE_PIO)) {
        if (PlatformIO.isPlatformIO_INI_Present())
          data[PROP_PIO_ENV][PROP_VAL_READONLY] = true;
        else {
          data[PROP_PIO_ENV][PROP_VAL_VALUE] = (String)cbPioEnv.getItemAt(0);
          data[PROP_PIO_ENV][PROP_VAL_READONLY] = false;
        }
      } else {
        data[PROP_PIO_ENV][PROP_VAL_READONLY] = true;
      }
    }
    if (row == PROP_USE_IMAGE_BACKGROUND) {
      if (useBackgroundImage()) {
        data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_READONLY]=Boolean.FALSE;
      } else {
        setBackgroundImageName("");
        data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_VALUE] = "";
        data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_VALUE] = "";
        data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_VALUE] = "";
        data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_READONLY]=Boolean.TRUE;
        fireTableCellUpdated(PROP_IMAGE_BACKGROUND_MEMORY, COLUMN_VALUE);
        fireTableCellUpdated(PROP_IMAGE_BACKGROUND_DEFINE, COLUMN_VALUE);
        fireTableCellUpdated(PROP_IMAGE_BACKGROUND_FORMAT, COLUMN_VALUE);
      }
      fireTableCellUpdated(PROP_IMAGE_BACKGROUND_FILE, COLUMN_VALUE);
    }
    if (row == PROP_GUISLICE_THEME) {
      Controller.startGUIsliceTheme(getGUIsliceThemeName());
    }    
    if (row == PROP_FONT_LIST) {
      fireTableStructureChanged();
    }
  }

  @Override
  public void changeThemeColors(GUIsliceTheme theme) {
    GUIsliceThemeElement element = theme.getElement("Page");
    if (element != null) {
      data[PROP_BACKGROUND][PROP_VAL_VALUE] = element.getFillCol();
    }
  }

  /**
   * setReadOnlyProperties
   * Called by Controller on creating a new project file. 
   *
   * @see builder.models.WidgetModel#setReadOnlyProperties()
   */
  @Override
  public void setReadOnlyProperties() {
    if (!getBackgroundImageName().isEmpty()) {
      File file = new File(getBackgroundImageName());
      try {
        image = ImageIO.read(file);
//        setBackgroundImageName(file.getName());
      } catch(IOException e) {
        Builder.logger.error("PM image read error: " + e.getMessage());
      }
      data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      image = null;
      data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_READONLY]=Boolean.TRUE;
    }
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
  }

  /**
   * writeModel() will serialize our model's data to a string object for backup
   * and recovery.
   *
   * @param out
   *          is our ObjectOutputStream.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see builder.models.WidgetModel#writeModel(java.io.ObjectOutputStream)
   */
  @Override
  public void writeModel(ObjectOutputStream out) throws IOException {
    super.writeModel(out);
    if (useBackgroundImage()) {
      out.writeObject((String) CommonUtils.getInstance().encodeToString(image));
    }
  }

  /**
   * readModel() will de-serialize our model's data from a string object for backup
   * and recovery.
   *
   * @param in
   *          the in stream
   * @param widgetType
   *          the widget type
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   * @see builder.models.WidgetModel#readModel(java.io.ObjectInputStream, java.lang.String)
   */
  @Override
  public void readModel(ObjectInputStream in, String widgetType) 
      throws IOException, ClassNotFoundException {
    super.readModel(in,  widgetType);
    if (useBackgroundImage()) {
      String imageString = (String) in.readObject();
      image = CommonUtils.getInstance().decodeToImage(imageString);
      data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      image = null;
      data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_VALUE] = "";
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_VALUE] = "";
      data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_VALUE] = "";
      data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_VALUE] = "";
      data[PROP_IMAGE_BACKGROUND_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_IMAGE_BACKGROUND_MEMORY][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_IMAGE_BACKGROUND_FORMAT][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_IMAGE_BACKGROUND_FILE][PROP_VAL_READONLY]=Boolean.TRUE;
    }
    if (getTargetPlatform().equals("arduino")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "Adafruit_GFX";
    }
    if (getTargetPlatform().equals("tft_espi")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "TFT_eSPI";
    }
    if (getTargetPlatform().equals("teensy")) {
      data[PROP_TARGET][PROP_VAL_VALUE] = "Teensy";
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
    fontsListCell.setData((String[])data[PROP_FONT_LIST][PROP_VAL_VALUE]);
  }

}
