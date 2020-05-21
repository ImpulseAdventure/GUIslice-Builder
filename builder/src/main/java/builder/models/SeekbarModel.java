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

import builder.common.ColorFactory;
import builder.common.EnumFactory;
import builder.events.MsgBoard;

/**
 * The Class SeekbarModel implements the model for the SeekbarModel widget.
 * 
 * @author Paul Conti
 * 
 */
public class SeekbarModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemSeekbar";
  
  /** The Constant ELEMENTREF_NAME. */
  static private final int PROP_MIN              =7;
  static private final int PROP_MAX              =8;
  static private final int PROP_CURVALUE         =9;
  static private final int PROP_THUMBSZ          =10;
  static private final int PROP_THUMB_COLOR      =11;
  static private final int PROP_THUMB_HASTRIM    =12;
  static private final int PROP_THUMBTRIM_COLOR  =13;
  static private final int PROP_THUMB_HASFRAME   =14;
  static private final int PROP_THUMBFRAME_COLOR =15;
  static private final int PROP_PROGRESS_WIDTH   =16;
  static private final int PROP_PROGRESS_COLOR   =17;
  static private final int PROP_REMAIN_WIDTH     =18;
  static private final int PROP_REMAIN_COLOR     =19;
  static private final int PROP_VERTICAL         =20;
  static private final int PROP_DIVISIONS        =21;
  static private final int PROP_TICKSZ           =22;
  static private final int PROP_TICK_COLOR       =23;
  static private final int PROP_USE_FLASH        =24;
  static private final int PROP_FRAME_COLOR      =25;
  static private final int PROP_FILL_COLOR       =26;
  static private final int PROP_SELECTED_COLOR   =27;
    
  /** The Property Defaults */
  static public  final Integer DEF_MIN               = Integer.valueOf(0);
  static public  final Integer DEF_MAX               = Integer.valueOf(100);
  static public  final Integer DEF_CURVALUE          = Integer.valueOf(0);
  static public  final Integer DEF_THUMBSZ           = Integer.valueOf(8);
  static public  final Color   DEF_THUMB_COLOR       = new Color(0,0,192); // GSLC_COL_BLUE_DK2;;
  static public  final Boolean DEF_THUMB_HASTRIM     = Boolean.TRUE;
  static public  final Color   DEF_TRIM_COLOR        = new Color(128,128,255); // GSLC_COL_BLUE_LT4;
  static public  final Boolean DEF_THUMB_HASFRAME    = Boolean.FALSE;
  static public  final Color   DEF_THUMBFRAME_COLOR  = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Integer DEF_PROGRESS_WIDTH    = Integer.valueOf(4);
  static public  final Color   DEF_PROGRESS_COLOR    = Color.BLUE;
  static public  final Integer DEF_REMAIN_WIDTH      = Integer.valueOf(2);
  static public  final Color   DEF_REMAIN_COLOR      = Color.GRAY;
  static public  final Boolean DEF_VERTICAL          = Boolean.FALSE;
  static public  final Integer DEF_DIVISIONS         = Integer.valueOf(0);
  static public  final Integer DEF_TICKSZ            = Integer.valueOf(10);
  static public  final Color   DEF_TICK_COLOR        = Color.GRAY;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 80;
  static private final int DEF_HEIGHT= 30;

  /**
   * Instantiates a new slider model.
   */
  public SeekbarModel() {
    cf = ColorFactory.getInstance();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.SEEKBAR;
    data = new Object[28][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_MIN, Integer.class, "SLD-100", Boolean.FALSE,"Minimum Value",DEF_MIN);
    initProp(PROP_MAX, Integer.class, "SLD-101", Boolean.FALSE,"Maximum Value",DEF_MAX);
    initProp(PROP_CURVALUE, Integer.class, "SLD-102", Boolean.FALSE,"Starting Value",DEF_CURVALUE);

    initProp(PROP_THUMBSZ, Integer.class, "SLD-103", Boolean.FALSE,"Thumb Size",DEF_THUMBSZ);
    initProp(PROP_THUMB_COLOR, Color.class, "COL-317", Boolean.FALSE,"Thumb Color",DEF_THUMB_COLOR);
    initProp(PROP_THUMB_HASTRIM, Boolean.class, "SLD-107", Boolean.FALSE,"Add Trim?",DEF_THUMB_HASTRIM);
    initProp(PROP_THUMBTRIM_COLOR, Color.class, "COL_307", Boolean.FALSE,"Trim Color",DEF_TRIM_COLOR);
    initProp(PROP_THUMB_HASFRAME, Boolean.class, "SLD-110", Boolean.FALSE,"Add Frame to Thumb?",DEF_THUMB_HASFRAME);
    initProp(PROP_THUMBFRAME_COLOR, Color.class, "COL-318", Boolean.FALSE,"Thumb Frame Color",DEF_THUMBFRAME_COLOR);
    initProp(PROP_PROGRESS_WIDTH, Integer.class, "SLD-108", Boolean.FALSE,"Progress Bar Width",DEF_PROGRESS_WIDTH);
    initProp(PROP_PROGRESS_COLOR, Color.class, "COL-315", Boolean.FALSE,"Progress Bar Color",DEF_PROGRESS_COLOR);
    initProp(PROP_REMAIN_WIDTH, Integer.class, "SLD-109", Boolean.FALSE,"Remaining Bar Width",DEF_REMAIN_WIDTH);
    initProp(PROP_REMAIN_COLOR, Color.class, "COL-316", Boolean.FALSE,"Remaining Bar Color",DEF_REMAIN_COLOR);

    initProp(PROP_VERTICAL, Boolean.class, "SLD-104", Boolean.FALSE,"Vertical?",DEF_VERTICAL);
    initProp(PROP_DIVISIONS, Integer.class, "SLD-105", Boolean.FALSE,"Tick Divisions",DEF_DIVISIONS);
    initProp(PROP_TICKSZ, Integer.class, "SLD-106", Boolean.FALSE,"Tick Size",DEF_TICKSZ);
    initProp(PROP_TICK_COLOR, Color.class, "COL-306", Boolean.FALSE,"Tick Color",DEF_TICK_COLOR);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);

    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);
    
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
    if (row == PROP_VERTICAL) {
      // swap height and width
      int tmp = getWidth();
      setWidth(getHeight());
      setHeight(tmp);
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
      fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
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
   * Gets the tick color.
   *
   * @return the tick color
   */
  public Color getTickColor() {
    return (((Color) data[PROP_TICK_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the trim color.
   *
   * @return the trim color
   */
  public Color getThumbTrimColor() {
    return (((Color) data[PROP_THUMBTRIM_COLOR][PROP_VAL_VALUE]));
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
   * Checks if is thumb trim style.
   *
   * @return true, if is trim style
   */
  public boolean isThumbTrim() {
    return ((Boolean) data[PROP_THUMB_HASTRIM][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is thumb frame style.
   *
   * @return true, if is trim style
   */
  public boolean isThumbFrame() {
    return ((Boolean) data[PROP_THUMB_HASFRAME][PROP_VAL_VALUE]).booleanValue();
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
   * Gets the Progress bar width.
   *
   * @return the width
   */
  public int getProgressWidth() {
    return (((Integer) (data[PROP_PROGRESS_WIDTH][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the Remaining bar width.
   *
   * @return the width
   */
  public int getRemainWidth() {
    return (((Integer) (data[PROP_REMAIN_WIDTH][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the thumb size.
   *
   * @return the thumb size
   */
  public int getThumbSize() {
    return (((Integer) (data[PROP_THUMBSZ][PROP_VAL_VALUE])).intValue());
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
   * Gets the divisions.
   *
   * @return the divisions
   */
  public int getDivisions() {
    return (((Integer) (data[PROP_DIVISIONS][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the Progress bar color.
   *
   * @return the fill color
   */
  public Color getProgressColor() {
    return (((Color) data[PROP_PROGRESS_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the Remaining bar color.
   *
   * @return the fill color
   */
  public Color getRemainColor() {
    return (((Color) data[PROP_REMAIN_COLOR][PROP_VAL_VALUE]));
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
   * Gets the thumb frame color.
   *
   * @return the Thumb frame color
   */
  public Color getThumbFrameColor() {
    return (((Color) data[PROP_THUMBFRAME_COLOR][PROP_VAL_VALUE]));
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
