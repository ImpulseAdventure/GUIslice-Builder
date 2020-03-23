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
import java.io.IOException;
import java.io.ObjectInputStream;

import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.events.MsgBoard;

/**
 * The Class CheckBoxModel implements the model for the Checkbox widget.
 * 
 * @author Paul Conti
 * 
 */
public class CheckBoxModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemCB";
  
  /** The Property Index Constants. */
  static private final int PROP_CHECKED        = 7;
  static private final int PROP_CALLBACK_EN    = 8;
  static private final int PROP_MARK_COLOR     = 9;
  static private final int PROP_USE_FLASH      = 10;
  static private final int PROP_DEFAULT_COLORS = 11;
  static private final int PROP_FRAME_COLOR    = 12;
  static private final int PROP_FILL_COLOR     = 13;
  static private final int PROP_SELECTED_COLOR = 14;
//  static public  final int PROP_GROUP          = 15;

  /** The Property Defaults */
  static public  final Boolean DEF_CHECKED           = Boolean.FALSE;
  static public  final Boolean DEF_CALLBACK_EN       = Boolean.FALSE;
  static public  final Color   DEF_MARK_COLOR        = new Color(255,165,0); // // GSLC_COL_ORANGE
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Boolean DEF_DEFAULT_COLORS    = Boolean.TRUE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
//  static public  final String  DEF_GROUP             = "GSLC_GROUP_ID_NONE";  

  static private final int DEF_WIDTH = 20;
  static private final int DEF_HEIGHT= 20;

  /**
   * Instantiates a new check box model.
   */
  public CheckBoxModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.CHECKBOX;
    data = new Object[15][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    // bug b90 check boxes and radio buttons should have either width or height.
    data[PROP_HEIGHT][PROP_VAL_READONLY]=Boolean.TRUE;
    
    initProp(PROP_CHECKED, Boolean.class, "CBOX-100", Boolean.FALSE,"Checked?",DEF_CHECKED);
    initProp(PROP_CALLBACK_EN, Boolean.class, "COM-017", Boolean.FALSE,"Callback Enabled?",DEF_CALLBACK_EN);
    initProp(PROP_MARK_COLOR, Color.class, "COL-305", Boolean.FALSE,"Check Mark Color",DEF_MARK_COLOR);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_DEFAULT_COLORS, Boolean.class, "COL-300", Boolean.FALSE,"Use Default Colors?",DEF_DEFAULT_COLORS);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.TRUE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.TRUE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.TRUE,"Selected Color",DEF_SELECTED_COLOR);

//    initProp(PROP_GROUP, String.class, "RBTN-101", Boolean.FALSE,"Group ID",DEF_GROUP);

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
    if (row == PROP_WIDTH) {
      data[PROP_HEIGHT][PROP_VAL_VALUE] = getWidth();
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
    }
    if (row == PROP_HEIGHT) {
      data[PROP_WIDTH][PROP_VAL_VALUE] = getHeight();
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
    }
    if (row == PROP_CALLBACK_EN) {
      if (isCallbackEn() && getElementRef().isEmpty()) {
        setElementRef(CommonUtils.createElemName(getKey(), ELEMENTREF_NAME));
        fireTableCellUpdated(PROP_ELEMENTREF, COLUMN_VALUE);
      }
    }
    if (row == PROP_DEFAULT_COLORS) {
      // check for switching back and forth
      if (useDefaultColors()) {
        data[PROP_FRAME_COLOR][PROP_VAL_VALUE]=DEF_FRAME_COLOR; 
        data[PROP_FILL_COLOR][PROP_VAL_VALUE]=DEF_FILL_COLOR;
        data[PROP_SELECTED_COLOR][PROP_VAL_VALUE]=DEF_SELECTED_COLOR; 
        data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
      } else {
        data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
      }
      fireTableCellUpdated(PROP_FRAME_COLOR, COLUMN_VALUE);
      fireTableCellUpdated(PROP_FILL_COLOR, COLUMN_VALUE);
      fireTableCellUpdated(PROP_SELECTED_COLOR, COLUMN_VALUE);
    }     
    
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.getInstance().sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        MsgBoard.getInstance().sendRepaint(getKey(),getKey());
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
   * Gets the mark color.
   *
   * @return the mark color
   */
  public Color getMarkColor() {
    return (((Color) data[PROP_MARK_COLOR][PROP_VAL_VALUE]));
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
   * Checks if callback enabled?
   *
   * @return true, if callback is enabled
   */
  public boolean isCallbackEn() {
    return ((Boolean) data[PROP_CALLBACK_EN][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Use default colors.
   *
   * @return <code>true</code>, if successful
   */
  public boolean useDefaultColors() {
    return ((Boolean) data[PROP_DEFAULT_COLORS][PROP_VAL_VALUE]).booleanValue();
  }
  
 /**
  * Gets the fill color.
  *
  * @return the fill color
  */
 public Color getFillColor() {
    return (((Color) data[PROP_FILL_COLOR][PROP_VAL_VALUE]));
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
   * Gets the selected color.
   *
   * @return the selected color
   */
  public Color getSelectedColor() {
    return (((Color) data[PROP_SELECTED_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the group id.
   *
   * @return the group id
   */
/*
  @Override
  public String getGroupId() {
    return ((String) data[PROP_GROUP][PROP_VAL_VALUE]);
  }
*/
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
   * @see builder.mementos.PositionMemento
   * @see builder.mementos.WidgetMemento
   * @see builder.views.PagePane
   * @see builder.widgets.Widget
   */
  @Override
  public void readModel(ObjectInputStream in, String widgetType) 
      throws IOException, ClassNotFoundException {
    super.readModel(in,  widgetType);
//    if (((String)data[PROP_GROUP][PROP_VAL_VALUE]).isEmpty()) {
//      data[PROP_GROUP][PROP_VAL_VALUE] = "GSLC_GROUP_ID_NONE";
//    }
    if (useDefaultColors()) {
      data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
      data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
    } else {
      data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
      data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
    }
  }

}
