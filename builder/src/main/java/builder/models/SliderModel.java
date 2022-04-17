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
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeElement;
import builder.themes.GUIsliceThemeFactory;

/**
 * The Class SliderModel implements the model for the Slider widget.
 * 
 * @author Paul Conti
 * 
 */
public class SliderModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemSlider";
  
  /** The Constant ELEMENTREF_NAME. */
  static private final int PROP_MIN            =7;
  static private final int PROP_MAX            =8;
  static private final int PROP_CURVALUE       =9;
  static private final int PROP_THUMBSZ        =10;
  static private final int PROP_VERTICAL       =11;
  static private final int PROP_DIVISIONS      =12;
  static private final int PROP_TICKSZ         =13;
  static private final int PROP_TICK_COLOR     =14;
  static private final int PROP_TRIM           =15;
  static private final int PROP_TRIM_COLOR     =16;
  static private final int PROP_USE_FLASH      =17;
  static private final int PROP_FRAME_COLOR    =18;
  static private final int PROP_FILL_COLOR     =19;
  static private final int PROP_SELECTED_COLOR =20;
    
  /** The Property Defaults */
  static public  final Integer DEF_MIN               = Integer.valueOf(0);
  static public  final Integer DEF_MAX               = Integer.valueOf(100);
  static public  final Integer DEF_CURVALUE          = Integer.valueOf(0);
  static public  final Integer DEF_THUMBSZ           = Integer.valueOf(5);
  static public  final Boolean DEF_VERTICAL          = Boolean.FALSE;
  static public  final Integer DEF_DIVISIONS         = Integer.valueOf(10);
  static public  final Integer DEF_TICKSZ            = Integer.valueOf(5);
  static public  final Color   DEF_TICK_COLOR        = Color.BLUE;
  static public  final Boolean DEF_TRIM              = Boolean.FALSE;
  static public  final Color   DEF_TRIM_COLOR        = Color.BLUE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 80;
  static private final int DEF_HEIGHT= 20;

  /**
   * Instantiates a new slider model.
   */
  public SliderModel() {
    cf = GUIsliceThemeFactory.getInstance();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.SLIDER;
    data = new Object[21][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_MIN, Integer.class, "SLD-100", Boolean.FALSE,"Minimum Value",DEF_MIN);
    initProp(PROP_MAX, Integer.class, "SLD-101", Boolean.FALSE,"Maximum Value",DEF_MAX);
    initProp(PROP_CURVALUE, Integer.class, "SLD-102", Boolean.FALSE,"Starting Value",DEF_CURVALUE);
    initProp(PROP_THUMBSZ, Integer.class, "SLD-103", Boolean.FALSE,"Thumb Size",DEF_THUMBSZ);
    initProp(PROP_VERTICAL, Boolean.class, "SLD-104", Boolean.FALSE,"Vertical?",DEF_VERTICAL);
    initProp(PROP_DIVISIONS, Integer.class, "SLD-105", Boolean.FALSE,"Tick Divisions",DEF_DIVISIONS);
    initProp(PROP_TICKSZ, Integer.class, "SLD-106", Boolean.FALSE,"Tick Size",DEF_TICKSZ);
    initProp(PROP_TRIM, Boolean.class, "SLD-107", Boolean.FALSE,"Trim Style?",DEF_TRIM);

    initProp(PROP_TICK_COLOR, Color.class, "COL-306", Boolean.FALSE,"Tick Color",DEF_TICK_COLOR);
    initProp(PROP_TRIM_COLOR, Color.class, "COL_307", Boolean.FALSE,"Trim Color",DEF_TRIM_COLOR);

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
        MsgBoard.sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
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
  public Color getTrimColor() {
    return (((Color) data[PROP_TRIM_COLOR][PROP_VAL_VALUE]));
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
   * Checks if is trim style.
   *
   * @return true, if is trim style
   */
  public boolean isTrimStyle() {
    return ((Boolean) data[PROP_TRIM][PROP_VAL_VALUE]).booleanValue();
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

/*
  static private final int PROP_TICK_COLOR     =14;
*/
  /**
   * 
   * changeThemeColors
   *
   * @see builder.models.WidgetModel#changeThemeColors(builder.themes.GUIsliceTheme)
   */
  @Override
  public void changeThemeColors(GUIsliceTheme theme) {
    GUIsliceThemeElement element = theme.getElement("Slider");
    if (element != null) {
      data[PROP_TRIM][PROP_VAL_VALUE] = element.isTrimEnabled();
      if (element.getTrimCol() != null)
        data[PROP_TRIM_COLOR][PROP_VAL_VALUE] = element.getTrimCol();
      if (element.getTickCol() != null)
        data[PROP_TICK_COLOR][PROP_VAL_VALUE] = element.getTickCol();
      if (element.getTickCol() != null)
        data[PROP_TICK_COLOR][PROP_VAL_VALUE] = element.getTickCol();
      if (element.getFrameCol() != null)
        data[PROP_FRAME_COLOR][PROP_VAL_VALUE] = element.getFrameCol();
      if (element.getFillCol() != null)
        data[PROP_FILL_COLOR][PROP_VAL_VALUE] = element.getFillCol();
      if (element.getGlowCol() != null)
        data[PROP_SELECTED_COLOR][PROP_VAL_VALUE] = element.getGlowCol();
      fireTableStructureChanged();
    }
  }

}
