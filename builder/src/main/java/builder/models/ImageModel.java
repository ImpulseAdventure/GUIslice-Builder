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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import builder.Builder;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.common.HexToImgConv;
import builder.controller.Controller;
import builder.events.MsgBoard;

/**
 * The Class ImageModel implements the model for the Image widget.
 */
public class ImageModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Property Index Constants. */
  static private final int PROP_IMAGE             = 7;
  static private final int PROP_DEFINE            = 8;
  static private final int PROP_EXTERN            = 9;
  static private final int PROP_MEMORY            = 10;
  static private final int PROP_FORMAT            = 11;
  static private final int PROP_TRANSPARENCY      = 12;
  static private final int PROP_TOUCH_EN          = 13;
  static private final int PROP_FRAME_EN          = 14;
  static private final int PROP_FRAME_COLOR       = 15;

  /** The Property Defaults */
  static public  final String  DEF_IMAGE             = "";
  static public  final String  DEF_DEFINE            = "";
  static public  final String  DEF_EXTERN            = "";
  static public  final String  DEF_MEMORY            = "";
  static public  final String  DEF_FORMAT            = "";
  static public  final Boolean DEF_TRANSPARENCY      = Boolean.FALSE;
  static public  final Boolean DEF_TOUCH_EN          = Boolean.FALSE;
  static public  final Boolean DEF_FRAME_EN          = Boolean.FALSE;
  static public  final Color   DEF_FRAME_COLOR       = Color.GRAY;

  /** The image. */
  private BufferedImage image;

  /** The cb memory. */
  JComboBox<String> cbMemory;
  
  /** The memory cell editor. */
  DefaultCellEditor memoryCellEditor;

  /** The cb format. */
  JComboBox<String> cbFormat;
  
  /** The format cell editor. */
  DefaultCellEditor formatCellEditor;

  /** memory Constants */
  public  final static String SRC_SD   = "gslc_GetImageFromSD((const char*)";
  public  final static String SRC_PROG = "gslc_GetImageFromProg((const unsigned char*)";
  public  final static String SRC_RAM  = "gslc_GetImageFromRam((const unsigned char*)";
  public  final static String SRC_FILE = "gslc_GetImageFromFile(";

  /** format Constants */
  public  final static String FORMAT_BMP24  = "GSLC_IMGREF_FMT_BMP24";
  public  final static String FORMAT_BMP16  = "GSLC_IMGREF_FMT_BMP16";
  public  final static String FORMAT_RAW    = "GSLC_IMGREF_FMT_RAW1";
  public  final static String FORMAT_JPG    = "GSLC_IMGREF_FMT_JPG";
  
  /**
   * Instantiates a new image model.
   */
  public ImageModel() {
    initProperties();
    initComboBoxes();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.IMAGE;
    data = new Object[16][5];
    
    initCommonProps(0, 0);
    
    // can't change height and width of image without scaling support
//    data[PROP_WIDTH][PROP_VAL_READONLY]=Boolean.TRUE;
//    data[PROP_HEIGHT][PROP_VAL_READONLY]=Boolean.TRUE;

//    initProp(PROP_IMAGE, String.class, "IMG-100", Boolean.TRUE,"Image",DEF_IMAGE);
    initProp(PROP_IMAGE, String.class, "IMG-100", Boolean.FALSE,"Image",DEF_IMAGE);
    initProp(PROP_DEFINE, String.class, "IMG-101", Boolean.FALSE,"Image #defines",DEF_DEFINE);
    initProp(PROP_EXTERN, String.class, "IMG-108", Boolean.FALSE,"Image Extern",DEF_EXTERN);
    initProp(PROP_MEMORY, String.class, "IMG-109", Boolean.FALSE,"Image Memory",DEF_MEMORY);
    initProp(PROP_FORMAT, String.class, "IMG-102", Boolean.FALSE,"Image Format",DEF_FORMAT);
    initProp(PROP_TRANSPARENCY, Boolean.class, "IMG-107", Boolean.FALSE,"Transparent?",DEF_TRANSPARENCY);
    initProp(PROP_TOUCH_EN, Boolean.class, "COM-016", Boolean.FALSE,"Touch Enabled?",DEF_TOUCH_EN);
    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);

  }

  /**
   * Initializes the comboboxes.
   */
  private void initComboBoxes()
  {
    cbMemory = new JComboBox<String>();
    cbMemory.addItem(SRC_SD);
    cbMemory.addItem(SRC_FILE);
    cbMemory.addItem(SRC_PROG);
    cbMemory.addItem(SRC_RAM);
    memoryCellEditor = new DefaultCellEditor(cbMemory);
    cbFormat = new JComboBox<String>();
    cbFormat.addItem(FORMAT_BMP24);
    cbFormat.addItem(FORMAT_BMP16);
    cbFormat.addItem(FORMAT_JPG);
    cbFormat.addItem(FORMAT_RAW);
    formatCellEditor = new DefaultCellEditor(cbFormat);
  }
  
  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int rowIndex) {
    if (rowIndex == PROP_MEMORY)
      return memoryCellEditor;
    if (rowIndex == PROP_FORMAT)
      return formatCellEditor;
    return null;
  }

  /**
   * changeValueAt
   *
   * @see builder.models.WidgetModel#changeValueAt(java.lang.Object, int)
   */
  @Override
  public void changeValueAt(Object value, int row) {
    // The test for Integer supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      data[row][PROP_VAL_VALUE] = value;
    }
    fireTableCellUpdated(row, 1);
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
      }
    } 
  }

  /**
   * Checks if touch enabled?
   *
   * @return true, if touch is enabled
   */
  public boolean isTouchEn() {
    return ((Boolean) data[PROP_TOUCH_EN][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is frame enabled.
   *
   * @return true, if is frame enabled
   */
  public boolean isFrameEnabled() {
    return ((Boolean) data[PROP_FRAME_EN][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the extern name.
   *
   * @return the extern name
   */
  public String getExternName() {
    return (String) data[PROP_EXTERN][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the extern name.
   *
   * @param name
   *          the new extern name
   */
  public void setExternName(String name) {
    shortcutValue(name, PROP_EXTERN);
  }

  /**
   * Gets the memory type.
   *
   * @return the memory type
   */
  public String getMemory() {
    return (String) data[PROP_MEMORY][PROP_VAL_VALUE];
  }
  
 /**
  * Gets the image name.
  *
  * @return the image name
  */
 public String getImageName() {
   String dir = Controller.getProjectModel().getTargetImageDir();
   String name = (String) data[PROP_IMAGE][PROP_VAL_VALUE];
   // do we need to add a relative path for code generation?
   if (dir.length() > 0)
     name = dir + name;

   return name;
 }
 
 /**
  * Sets the image name.
  *
  * @param name
  *          the new image name
  */
 public void setImageName(String name) {
   shortcutValue(name, PROP_IMAGE);
 }

 /**
  * Gets the image format.
  *
  * @return the image format
  */
 public String getImageFormat() {
   return (String) data[PROP_FORMAT][PROP_VAL_VALUE];
 }
 
 /**
  * Sets the image format.
  *
  * @param name
  *          the new image format
  */
 public void setImageFormat(String name) {
   data[PROP_FORMAT][PROP_VAL_VALUE]=name;
 }

 /**
  * is Transparent?
  *
  * @return <code>true</code>, if successful
  */
 public boolean isTransparent() {
   return (((Boolean) data[PROP_TRANSPARENCY][PROP_VAL_VALUE]).booleanValue());
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
   * Sets the image.
   *
   * @param file
   *          the file
   * @param x
   *          the x
   * @param y
   *          the y
   */
  public boolean setImage(File file, int x, int y) {
    image = null;
    if (file.getName().toLowerCase().endsWith(".c")) {
      HexToImgConv convert = new HexToImgConv();
      image = convert.doConvert(file);
      if (image != null) {
        setImageFormat("GSLC_IMGREF_FMT_BMP24");
        setExternName(convert.getExternName());
        if (Controller.getTargetPlatform().equals(ProjectModel.PLATFORM_LINUX))
          data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_RAM;
        else      
          data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_PROG;
        setWidth(convert.getWidth());
        setHeight(convert.getHeight());
      } else {
        Builder.logger.error("image conversion from C error: " + file.getName());
        return false;
      }
    } else {
      try {
          image = ImageIO.read(file);
          if (image.getType() == 2) {
            throw new IOException(file.getName() + " is 32-bit BMP GUIslice only supports 24 bit");
          }
      } catch(IOException e) {
          Builder.logger.error("image error: " + file.getName() + "->" + e.getMessage());
          return false;
      }

      setWidth(image.getWidth());
      setHeight(image.getHeight());
      if (file.getName().toLowerCase().endsWith(".jpg"))
        setImageFormat("GSLC_IMGREF_FMT_JPG");
      else if (image.getType() == BufferedImage.TYPE_3BYTE_BGR)
        setImageFormat("GSLC_IMGREF_FMT_BMP24");
      else if (image.getType() == BufferedImage.TYPE_USHORT_555_RGB) 
        setImageFormat("GSLC_IMGREF_FMT_BMP16");
      else
        setImageFormat("GSLC_IMGREF_FMT_RAW1");
      String target = Controller.getTargetPlatform();
      if (target.equals(ProjectModel.PLATFORM_LINUX))
        data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_FILE;
      else if (target.equals(ProjectModel.PLATFORM_TFT_ESPI) &&
               file.getName().toLowerCase().endsWith(".jpg"))
        data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_FILE;
      else      
        data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_SD;
      // now construct a #define to use during code generation
      String fileName = file.getName();
      int n = fileName.indexOf(".");
      if (n > 0) {
        String tmp = fileName.substring(0,n);
        fileName = tmp.toUpperCase();
      } 
      // remove all special characters
      fileName = fileName.replaceAll("\\W", ""); 
      fileName = "IMG_" + fileName;
      setDefine(fileName);
      fileName = file.getName();
      setImageName(fileName);
    }
    return true;
  }
 
  /**
   * Gets the define.
   *
   * @return the define
   */
  public String getDefine() {
    return (String) data[PROP_DEFINE][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the define.
   *
   * @param s
   *          the new define
   */
  public void setDefine(String s) {
    shortcutValue(s, PROP_DEFINE);
  }
  
  /**
   * Gets the frame color.
   *
   * @return the frame color
   */
  public Color getFrameColor() {
    return (((Color) data[PROP_FRAME_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * writeModel
   * @param out
   *          the out stream
   *
   * @see builder.models.WidgetModel#writeModel(java.io.ObjectOutputStream)
   */
  @Override
  public void writeModel(ObjectOutputStream out) throws IOException {
    super.writeModel(out);
    out.writeObject((String) CommonUtils.getInstance().encodeToString(image));
  }
  
  /**
   * readModel() will deserialize our model's data from a string object for backup
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
  public void readModel(ObjectInputStream in, String widgetType) throws IOException, ClassNotFoundException {
    super.readModel(in,  widgetType);
    String imageString = (String) in.readObject();
    image = CommonUtils.getInstance().decodeToImage(imageString);
    if (image == null) {
      throw new IOException("image: " + getImageName() + " is unsupported and has been deleted");
    }
    if (((String)data[PROP_MEMORY][PROP_VAL_VALUE]).equals("PROGMEM"))
      data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_PROG;
     else if (((String)data[PROP_MEMORY][PROP_VAL_VALUE]).equals("SRAM"))
      data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_RAM;
     else if (((String)data[PROP_MEMORY][PROP_VAL_VALUE]).isEmpty())
      data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_SD;
  }

}
