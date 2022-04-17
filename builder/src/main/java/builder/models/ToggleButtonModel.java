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
//import java.io.IOException;
//import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeElement;

/**
 * The Class ToggleButtonModel implements the model for the Toggle Button widget.
 * 
 * @author Paul Conti
 * 
 */
public class ToggleButtonModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemToggle";
  
  /** The Property Index Constants. */
  static private final int PROP_CHECKED        = 7;
  static private final int PROP_THUMB_COLOR     =8;
  static private final int PROP_ON_COLOR       = 9;
  static private final int PROP_OFF_COLOR      = 10;
  static private final int PROP_CIRCULAR       = 11;
  static private final int PROP_USE_FLASH      = 12;
  static private final int PROP_FRAME_EN       = 13;
  static private final int PROP_FRAME_COLOR    = 14;
  static public  final int PROP_GROUP          = 15;
//  static private final int PROP_FILL_COLOR     = 16;
//  static private final int PROP_SELECTED_COLOR = 17;

  /** The Property Defaults */
  static public  final Boolean DEF_CHECKED           = Boolean.FALSE;
  static public  final Color   DEF_THUMB_COLOR       = Color.GRAY;
  static public  final Color   DEF_ON_COLOR          = new Color(0,0,224);     // GSLC_COL_BLUE_DK1
  static public  final Color   DEF_OFF_COLOR         = new Color(224,224,224); // GSLC_COL_GRAY_LT3
  static public  final Boolean DEF_CIRCULAR          = Boolean.TRUE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Boolean DEF_FRAME_EN          = Boolean.TRUE;
  static public  final Color   DEF_FRAME_COLOR       = Color.GRAY; 
//  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
//  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  static public  final String  DEF_GROUP             = "GSLC_GROUP_ID_NONE";  

  static private final int DEF_WIDTH = 35;
  static private final int DEF_HEIGHT= 20;

  /**
   * Instantiates a new radio button model.
   */
  public ToggleButtonModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.TOGGLEBUTTON;
    data = new Object[16][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);

    initProp(PROP_CHECKED, Boolean.class, "CBOX-100", Boolean.FALSE,"Checked?",DEF_CHECKED);
    initProp(PROP_THUMB_COLOR, Color.class, "COL-317", Boolean.FALSE,"Thumb Color",DEF_THUMB_COLOR);
    initProp(PROP_ON_COLOR, Color.class, "COL-319", Boolean.FALSE,"On State Color",DEF_ON_COLOR);
    initProp(PROP_OFF_COLOR, Color.class, "COL-320", Boolean.FALSE,"Off State Color",DEF_OFF_COLOR);
    initProp(PROP_CIRCULAR, Boolean.class, "RBTN-102", Boolean.FALSE,"Circular?",DEF_CIRCULAR);
    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
//    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
//    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);

    initProp(PROP_GROUP, String.class, "RBTN-101", Boolean.FALSE,"Group ID",DEF_GROUP);

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
    fireTableCellUpdated(row, COLUMN_VALUE);
    if (row > PROP_HEIGHT || row == PROP_ENUM)
      super.setModelChanged();
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
      }
    } 
  }

  /**
   * Use Flash API.
   *
   * @return <code>true</code>, if flash is to be used
   */
  @Override
  public boolean useFlash() {
    return ((Boolean) data[PROP_USE_FLASH][PROP_VAL_VALUE]).booleanValue();
  }
  
  /**
   * Gets the Thumb color.
   *
   * @return the Thumb color
   */
  public Color getThumbColor() {
    return (((Color) data[PROP_THUMB_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the On color.
   *
   * @return the on color
   */
  public Color getOnColor() {
    return (((Color) data[PROP_ON_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the Off color.
   *
   * @return the off color
   */
  public Color getOffColor() {
    return (((Color) data[PROP_OFF_COLOR][PROP_VAL_VALUE]));
  }
  /**
   * Checks if is checked.
   *
   * @return true, if is checked
   */
  public boolean isChecked() {
    return ((Boolean) data[PROP_CHECKED][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if circular style?
   *
   * @return true, if callback is enabled
   */
  public boolean isCircular() {
    return ((Boolean) data[PROP_CIRCULAR][PROP_VAL_VALUE]).booleanValue();
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
   * Gets the frame color.
   *
   * @return the frame color
   */
  public Color getFrameColor() {
    return (((Color) data[PROP_FRAME_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the fill color.
   *
   * @return the fill color
   */
/*
  public Color getFillColor() {
     return (((Color) data[PROP_FILL_COLOR][PROP_VAL_VALUE]));
   }
*/
  /**
   * Gets the selected color.
   *
   * @return the selected color
   */
/*
  public Color getSelectedColor() {
    return (((Color) data[PROP_SELECTED_COLOR][PROP_VAL_VALUE]));
  }
*/
  /**
   * Gets the group id.
   *
   * @return the group id
   */

  @Override
  public String getGroupId() {
    return ((String) data[PROP_GROUP][PROP_VAL_VALUE]);
  }
  
  /**
   * 
   * changeThemeColors
   *
   * @see builder.models.WidgetModel#changeThemeColors(builder.themes.GUIsliceTheme)
   */
  @Override
  public void changeThemeColors(GUIsliceTheme theme) {
    GUIsliceThemeElement element = theme.getElement("ToggleButton");
    if (element != null) {
      data[PROP_CIRCULAR][PROP_VAL_VALUE] = element.isCornersRounded();
      data[PROP_FRAME_EN][PROP_VAL_VALUE] = element.isFrameEnabled();
      if (element.getThumbCol() != null)
        data[PROP_THUMB_COLOR][PROP_VAL_VALUE] = element.getThumbCol();
      if (element.getOnCol() != null)
        data[PROP_ON_COLOR][PROP_VAL_VALUE] = element.getOnCol();
      if (element.getOffCol() != null)
        data[PROP_OFF_COLOR][PROP_VAL_VALUE] = element.getOffCol();
      if (element.getFrameCol() != null)
        data[PROP_FRAME_COLOR][PROP_VAL_VALUE] = element.getFrameCol();
      fireTableStructureChanged();
    }
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
  public void readModel(ObjectInputStream in, String widgetType) 
      throws IOException, ClassNotFoundException {
    super.readModel(in,  widgetType);
    if (((String)data[PROP_GROUP][PROP_VAL_VALUE]).isEmpty()) {
      data[PROP_GROUP][PROP_VAL_VALUE] = "GSLC_GROUP_ID_NONE";
    }
  }

}
