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

import builder.common.EnumFactory;
import builder.controller.Controller;
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
  static private final int PROP_FRAME_COLOR       = 12;
  static private final int PROP_FILL_COLOR        = 13;
  static private final int PROP_SELECTED_COLOR    = 14;
  
  /** The Property Defaults */
  static public  final Boolean DEF_ROUNDED           = Boolean.FALSE;
  static public  final Boolean DEF_TOUCH_EN          = Boolean.FALSE;
  static public  final Boolean DEF_DRAW              = Boolean.FALSE;
  static public  final Boolean DEF_TICKCB            = Boolean.FALSE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
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
    data = new Object[15][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);

    initProp(PROP_ROUNDED, Boolean.class, "COM-012", Boolean.FALSE,"Corners Rounded?",DEF_ROUNDED);
    initProp(PROP_TOUCH_EN, Boolean.class, "COM-016", Boolean.FALSE,"Touch Enabled?",DEF_TOUCH_EN);
    initProp(PROP_DRAW, Boolean.class, "BOX-100", Boolean.FALSE,"Draw Function",DEF_DRAW);
    initProp(PROP_TICKCB, Boolean.class, "BOX-101", Boolean.FALSE,"Tick Function",DEF_TICKCB);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);

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
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.getInstance().sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
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

}
