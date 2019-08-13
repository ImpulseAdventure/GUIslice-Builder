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

import builder.common.EnumFactory;
import builder.events.MsgBoard;

/**
 * The Class CircleModel implements the model for the Circle widget.
 * 
 * @author Paul Conti
 * 
 */
public class CircleModel extends WidgetModel { 
  
  /** The Property Index Constants. */
  private static final long serialVersionUID = 1L;
  static private final int PROP_RADIUS            = 4;
  static private final int PROP_FRAME_EN          = 5;
  static private final int PROP_FRAME_COLOR       = 6;
  static private final int PROP_FILL_EN           = 7;
  static private final int PROP_FILL_COLOR        = 8;
  
  /** The Property Defaults */
  static public  final Integer DEF_RADIUS            = Integer.valueOf(40);
  static public  final Boolean DEF_FRAME_EN          = Boolean.TRUE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(192,192,192); // GSLC_COL_GRAY_LT2
  static public  final Boolean DEF_FILL_EN           = Boolean.TRUE;
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;

  /**
   * Instantiates a new box model.
   */
  public CircleModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.CIRCLE;
    data = new Object[9][5];
    
    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_ENUM, String.class, "COM-002", Boolean.FALSE,"ENUM",widgetType);
    initProp(PROP_X, Integer.class, "COM-003", Boolean.FALSE,"Center X",Integer.valueOf(0));
    initProp(PROP_Y, Integer.class, "COM-004", Boolean.FALSE,"Center Y",Integer.valueOf(0));
    initProp(PROP_RADIUS, Integer.class, "CIRCLE-100", Boolean.FALSE,"Radius of Circle",DEF_RADIUS);

    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_EN, Boolean.class, "COM-011", Boolean.FALSE,"Fill Enabled?",DEF_FILL_EN);
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
        MsgBoard.getInstance().sendRepaint(getKey(),getKey());
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
    int r = (((Integer) (data[PROP_RADIUS][PROP_VAL_VALUE])).intValue());
    return (r*2)+2;
  }

  /**
   * Gets the height.
   *
   * @return the height
   */
  public int getHeight() {
    int r = (((Integer) (data[PROP_RADIUS][PROP_VAL_VALUE])).intValue());
    return (r*2)+2;
  }

  /**
   * Gets the radius.
   *
   * @return the radius
   */
  public int getRadius() {
    return (((Integer) (data[PROP_RADIUS][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Checks if is fill enabled.
   *
   * @return true, if is fill enabled
   */
  public boolean isFillEnabled() {
    return ((Boolean) data[PROP_FILL_EN][PROP_VAL_VALUE]).booleanValue();
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

}
