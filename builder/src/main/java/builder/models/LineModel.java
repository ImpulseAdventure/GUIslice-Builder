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
 * The Class CircleModel implements the model for the Circle widget.
 * 
 * @author Paul Conti
 * 
 */
public class LineModel extends WidgetModel { 
  
  /** The Property Index Constants. */
  private static final long serialVersionUID = 1L;
  static private final int PROP_LENGTH            = 4;
  static private final int PROP_VERTICAL          = 5;
  static private final int PROP_FILL_COLOR        = 6;
  
  /** The Property Defaults */
  static public  final Integer DEF_LENGTH         = Integer.valueOf(50);
  static public  final Boolean DEF_VERTICAL       = Boolean.FALSE;
  static public  final Color   DEF_FILL_COLOR     = new Color(192,192,192); // GSLC_COL_GRAY_LT2

  /**
   * Instantiates a new box model.
   */
  public LineModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.LINE;
    data = new Object[7][5];
    
    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_ENUM, String.class, "COM-002", Boolean.FALSE,"ENUM",widgetType);
    initProp(PROP_X, Integer.class, "COM-003", Boolean.FALSE,"X Start Point",Integer.valueOf(0));
    initProp(PROP_Y, Integer.class, "COM-004", Boolean.FALSE,"Y Start Point",Integer.valueOf(0));
    initProp(PROP_LENGTH, Integer.class, "LINE-100", Boolean.FALSE,"Line Length",DEF_LENGTH);
    initProp(PROP_VERTICAL, Boolean.class, "LINE-101", Boolean.FALSE,"Vertical?",DEF_VERTICAL);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);

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
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.getInstance().sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
      }
    } 
  }

  /**
   * Gets the width.
   *
   * @return the width
   */
  @Override
  public int getWidth() {
    return (((Integer) (data[PROP_LENGTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the height.
   *
   * @return the height
   */
  public int getHeight() {
    return 5;
  }

  /**
   * Gets the element ref.
   *
   * @return the element ref
   */
  @Override
  public String getElementRef() {
    return "";
  }
  
  /**
   * Checks if is vertical.
   *
   * @return true, if is vertical
   */
  public boolean isVertical() {
    return ((Boolean) data[PROP_VERTICAL][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the fill color.
   *
   * @return the fill color
   */
  public Color getFillColor() {
    return (((Color) data[PROP_FILL_COLOR][PROP_VAL_VALUE]));
  }

}
