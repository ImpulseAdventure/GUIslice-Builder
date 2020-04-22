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
import builder.events.MsgBoard;

/**
 * The Class RadialGaugeModel implements the model for the Redial Gauge widget.
 * 
 * @author Paul Conti
 * 
 */
public class RadialGaugeModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name. */
  public static final String ELEMENTREF_NAME = "m_pElemRadial";
  
  /** The Constant ELEMENTREF_NAME. */
  static private final int PROP_MIN               =7;
  static private final int PROP_MAX               =8;
  static private final int PROP_CURVALUE          =9;
  static private final int PROP_CLOCKWISE         =10;
  static private final int PROP_DIVISIONS         =11;
  static private final int PROP_TICKSZ            =12;
  static private final int PROP_TICK_COLOR        =13;
  static private final int PROP_INDICATOR_SZ      =14;
  static private final int PROP_INDICATOR_TIP_SZ  =15;
  static private final int PROP_INDICATOR_FILL    =16;
  static private final int PROP_GAUGE_COLOR       =17;
  static private final int PROP_FRAME_COLOR       =18;
  static private final int PROP_FILL_COLOR        =19;
  static private final int PROP_SELECTED_COLOR    =20;
    
  /** The Property Defaults */
  static public  final Integer DEF_MIN               = Integer.valueOf(0);
  static public  final Integer DEF_MAX               = Integer.valueOf(100);
  static public  final Integer DEF_CURVALUE          = Integer.valueOf(0);
  static public  final Boolean DEF_CLOCKWISE         = Boolean.TRUE;
  static public  final Integer DEF_DIVISIONS         = Integer.valueOf(8);
  static public  final Integer DEF_TICKSZ            = Integer.valueOf(5);
  static public  final Color   DEF_TICK_COLOR        = Color.GRAY;
  static public  final Integer DEF_INDICATOR_SZ      = Integer.valueOf(20);
  static public  final Integer DEF_INDICATOR_TIP_SZ  = Integer.valueOf(3);
  static public  final Boolean DEF_INDICATOR_FILL    = Boolean.FALSE;
  static public  final Color   DEF_GAUGE_COLOR       = Color.GREEN;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 80;
  static private final int DEF_HEIGHT= 80;

  /**
   * Instantiates a new redial gauge model.
   */
  public RadialGaugeModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.RADIALGAUGE;
    data = new Object[21][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_MIN, Integer.class, "BAR-102", Boolean.FALSE,"Minimum Value",DEF_MIN);
    initProp(PROP_MAX, Integer.class, "BAR-103", Boolean.FALSE,"Maximum Value",DEF_MAX);
    initProp(PROP_CURVALUE, Integer.class, "BAR-104", Boolean.FALSE,"Starting Value",DEF_CURVALUE);
    initProp(PROP_CLOCKWISE, Boolean.class, "BAR-118", Boolean.FALSE,"Rotation Clockwise?",DEF_CLOCKWISE);

    initProp(PROP_DIVISIONS, Integer.class, "BAR-106", Boolean.FALSE,"Tick Divisions",DEF_DIVISIONS);
    initProp(PROP_TICKSZ, Integer.class, "BAR-107", Boolean.FALSE,"Tick Size",DEF_TICKSZ);
    initProp(PROP_TICK_COLOR, Color.class, "BAR-108", Boolean.FALSE,"Tick Color",DEF_TICK_COLOR);
    initProp(PROP_INDICATOR_SZ, Integer.class, "BAR-109", Boolean.FALSE,"Indicator Length",DEF_INDICATOR_SZ);
    initProp(PROP_INDICATOR_TIP_SZ, Integer.class, "BAR-110", Boolean.FALSE,
        "Indicator Tip Size",DEF_INDICATOR_TIP_SZ);
    initProp(PROP_INDICATOR_FILL, Boolean.class, "BAR-111", Boolean.FALSE,"Indicator Fill?",DEF_INDICATOR_FILL);

    initProp(PROP_GAUGE_COLOR, Color.class, "COL-308", Boolean.FALSE,"Gauge Indicator Color",DEF_GAUGE_COLOR);

    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);

  }
  
  /**
   * Gets the divisions.
   *
   * @return the divisions
   */
  public int getDivisions() {
    return (((Integer) (data[PROP_DIVISIONS][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the tick size.
   *
   * @return the tick size
   */
  public int getTickSize() {
    return (((Integer) (data[PROP_TICKSZ][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the tick color.
   *
   * @return the tick color
   */
  public Color getTickColor() {
    return (((Color) data[PROP_TICK_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the indicator size.
   *
   * @return the indicator size
   */
  public int getIndicatorSize() {
    return (((Integer) (data[PROP_INDICATOR_SZ][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the indicator tip size.
   *
   * @return the indicator tip size
   */
  public int getIndicatorTipSize() {
    return (((Integer) (data[PROP_INDICATOR_TIP_SZ][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Checks if is indicator fill.
   *
   * @return true, if is indicator fill
   */
  public boolean isIndicatorFill() {
    return ((Boolean) data[PROP_INDICATOR_FILL][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is rotation is clockwise.
   *
   * @return true, if is clockwise
   */
  public boolean isClockwise() {
    return ((Boolean) data[PROP_CLOCKWISE][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the indicator color.
   *
   * @return the indicator color
   */
  public Color getIndicatorColor() {
    return (((Color) data[PROP_GAUGE_COLOR][PROP_VAL_VALUE]));
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
        MsgBoard.getInstance().sendRepaint(getKey(),getKey());
      }
    } 
  }

}
