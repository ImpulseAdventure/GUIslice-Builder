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
import java.io.IOException;
import java.io.ObjectInputStream;

import builder.common.EnumFactory;
import builder.events.MsgBoard;

/**
 * The Class BoxModel implements the model for the Box widget.
 * 
 * @author Paul Conti
 * 
 */
public class BoxModel extends WidgetModel { 
  
  /** The Property Index Constants. */
  private static final long serialVersionUID = 1L;
  static private final int PROP_ROUNDED           = 7;
  static private final int PROP_TOUCH_EN          = 8;
  static private final int PROP_DRAW              = 9;
  static private final int PROP_TICKCB            = 10;
  static private final int PROP_USE_FLASH         = 11;
  static private final int PROP_DEFAULT_COLORS    = 12;
  static private final int PROP_FRAME_COLOR       = 13;
  static private final int PROP_FILL_COLOR        = 14;
  static private final int PROP_SELECTED_COLOR    = 15;
  
  /** The Property Defaults */
  static public  final Boolean DEF_ROUNDED           = Boolean.FALSE;
  static public  final Boolean DEF_TOUCH_EN          = Boolean.FALSE;
  static public  final Boolean DEF_DRAW              = Boolean.FALSE;
  static public  final Boolean DEF_TICKCB            = Boolean.FALSE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Boolean DEF_DEFAULT_COLORS    = Boolean.TRUE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;

  static private final int DEF_WIDTH = 300;
  static private final int DEF_HEIGHT= 150;

  /**
   * Instantiates a new box model.
   */
  public BoxModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.BOX;
    data = new Object[16][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);

    initProp(PROP_ROUNDED, Boolean.class, "COM-012", Boolean.FALSE,"Corners Rounded?",DEF_ROUNDED);
    initProp(PROP_TOUCH_EN, Boolean.class, "COM-016", Boolean.FALSE,"Touch Enabled?",DEF_TOUCH_EN);
    initProp(PROP_DRAW, Boolean.class, "BOX-100", Boolean.FALSE,"Draw Function",DEF_DRAW);
    initProp(PROP_TICKCB, Boolean.class, "BOX-101", Boolean.FALSE,"Tick Function",DEF_TICKCB);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_DEFAULT_COLORS, Boolean.class, "COL-300", Boolean.FALSE,"Use Default Colors?",DEF_DEFAULT_COLORS);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.TRUE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.TRUE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.TRUE,"Selected Color",DEF_SELECTED_COLOR);

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
    if (row == PROP_DRAW) {
      String strKey = "";
      String strCount = "";
      String strEnum = "E_SCAN";
      if (!hasDrawFunc()) {
        strEnum = "E_BOX";
      } 
      strKey = getKey();
      int i = strKey.indexOf("$");
      strCount = strKey.substring(i+1, strKey.length());
      strEnum = strEnum + strCount;
      setEnum(strEnum);
      fireTableCellUpdated(PROP_ENUM, COLUMN_VALUE);
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
   * Checks if buttons are round
   *
   * @return true, if they are round
   */
  public boolean isRoundedEn() {
    return ((Boolean) data[PROP_ROUNDED][PROP_VAL_VALUE]).booleanValue();
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
   * Checks for draw func.
   *
   * @return <code>true</code>, if successful
   */
  public boolean hasDrawFunc() {
    return ((Boolean) data[PROP_DRAW][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks for tick func.
   *
   * @return <code>true</code>, if successful
   */
  public boolean hasTickFunc() {
    return ((Boolean) data[PROP_TICKCB][PROP_VAL_VALUE]).booleanValue();
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
