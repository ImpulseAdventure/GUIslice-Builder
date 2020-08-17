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
 * The Class RampGaugeModel implements the model for the Ramp Gauge widget.
 * 
 * @author Paul Conti
 * 
 */
public class RampGaugeModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name. */
  public static final String ELEMENTREF_NAME = "m_pElemRamp";
  
  /** The Constant ELEMENTREF_NAME. */
  static private final int PROP_MIN               =7;
  static private final int PROP_MAX               =8;
  static private final int PROP_CURVALUE          =9;
  static private final int PROP_FILL_COLOR        =10;
    
  /** The Property Defaults */
  static public  final Integer DEF_MIN               = Integer.valueOf(0);
  static public  final Integer DEF_MAX               = Integer.valueOf(100);
  static public  final Integer DEF_CURVALUE          = Integer.valueOf(0);
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  
  static private final int DEF_WIDTH = 100;
  static private final int DEF_HEIGHT= 80;

  /**
   * Instantiates a new ramp gauge model.
   */
  public RampGaugeModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.RAMPGAUGE;
    data = new Object[11][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_MIN, Integer.class, "BAR-102", Boolean.FALSE,"Minimum Value",DEF_MIN);
    initProp(PROP_MAX, Integer.class, "BAR-103", Boolean.FALSE,"Maximum Value",DEF_MAX);
    initProp(PROP_CURVALUE, Integer.class, "BAR-104", Boolean.FALSE,"Starting Value",DEF_CURVALUE);

    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);

  }
  
  /**
   * Gets the min.
   *
   * @return the min
   */
  public int getMin() {
    return (((Integer) (data[PROP_MIN][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the max.
   *
   * @return the max
   */
  public int getMax() {
    return (((Integer) (data[PROP_MAX][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the value.
   *
   * @return the value
   */
  public int getCurValue() {
    return (((Integer) (data[PROP_CURVALUE][PROP_VAL_VALUE])).intValue());
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
   * changeValueAt.
   *
   * @param value
   *          the value
   * @param row
   *          the row
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

}
