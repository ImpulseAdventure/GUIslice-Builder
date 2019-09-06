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

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.events.MsgBoard;

/**
 * The Class RingGaugeModel implements the model for the RingGauge widget.
 * 
 * @author Paul Conti
 */
public class RingGaugeModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemXRingGauge";
  
  /** The Property Index Constants. */
  static private final int PROP_FONT                  = 7;
  static private final int PROP_TEXT_SZ               = 8;
  static private final int PROP_STARTING_ANGLE        = 9;
  static private final int PROP_ANGULAR_RANGE         = 10;
  static private final int PROP_DIRECTION             = 11;
  static private final int PROP_MIN                   = 12;
  static private final int PROP_MAX                   = 13;
  static private final int PROP_CURVALUE              = 14;
  static private final int PROP_SEGMENTS              = 15;
  static private final int PROP_LINE_SZ               = 16;
  static private final int PROP_USE_GRADIENT          = 17;
  static private final int PROP_ACTIVE_COLOR          = 18;
  static private final int PROP_GRADIENT_START_COLOR  = 19;
  static private final int PROP_GRADIENT_END_COLOR    = 20;
  static private final int PROP_INACTIVE_COLOR        = 21;
  static private final int PROP_TEXT_COLOR            = 22;
  static private final int PROP_FILL_COLOR            = 23;
  
  /** The Property Defaults */
  static public final Integer DEF_TEXT_SZ               = Integer.valueOf(10);
  static public final Integer DEF_STARTING_ANGLE        = Integer.valueOf(0);
  static public final Integer DEF_ANGULAR_RANGE         = Integer.valueOf(360);
  static public final Boolean DEF_DIRECTION             = Boolean.TRUE;
  static public final Integer DEF_MIN                   = Integer.valueOf(0);
  static public final Integer DEF_MAX                   = Integer.valueOf(100);
  static public final Integer DEF_CURVALUE              = Integer.valueOf(0);
  static public final Integer DEF_SEGMENTS              = Integer.valueOf(72);
  static public final Integer DEF_LINE_SZ               = Integer.valueOf(10);
  static public final Boolean DEF_USE_GRADIENT          = Boolean.FALSE;
  static public final Color   DEF_ACTIVE_COLOR          = new Color(128,128,255); // GSLC_COL_BLUE_LT4
  static public final Color   DEF_GRADIENT_START_COLOR  = new Color(128,128,255); // GSLC_COL_BLUE_LT4
  static public final Color   DEF_GRADIENT_END_COLOR    = Color.RED;
  static public final Color   DEF_INACTIVE_COLOR        = new Color(64,64,64); // GSLC_COLGRAY_DK2
  static public final Color   DEF_TEXT_COLOR            = Color.YELLOW;
  static public final Color   DEF_FILL_COLOR            = Color.BLACK;

  /** The ff. */
  private FontFactory ff = null;
  
  static private final int DEF_WIDTH = 100;
  static private final int DEF_HEIGHT= 100;

  /**
   * Instantiates a new text model.
   */
  public RingGaugeModel() {
    ff = FontFactory.getInstance();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.RINGGAUGE;
    data = new Object[24][5];

    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName(2)); // next size up for font
    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"Field Size",DEF_TEXT_SZ);

    initProp(PROP_STARTING_ANGLE, Integer.class, "RING-100", Boolean.FALSE,"Starting Angle\u00B0",DEF_STARTING_ANGLE);
    initProp(PROP_ANGULAR_RANGE, Integer.class, "RING-101", Boolean.FALSE,"Angular Range\u00B0",DEF_ANGULAR_RANGE);
    initProp(PROP_DIRECTION, Boolean.class, "RING-102", Boolean.FALSE,"Clockwise Direction?",DEF_DIRECTION);

    initProp(PROP_MIN, Integer.class, "RING-103", Boolean.FALSE,"Minimum Value",DEF_MIN);
    initProp(PROP_MAX, Integer.class, "RING-104", Boolean.FALSE,"Maximum Value",DEF_MAX);
    initProp(PROP_CURVALUE, Integer.class, "RING-105", Boolean.FALSE,"Starting Value",DEF_CURVALUE);

    initProp(PROP_SEGMENTS, Integer.class, "RING-106", Boolean.TRUE,"Number of Segments",DEF_SEGMENTS);
    initProp(PROP_LINE_SZ, Integer.class, "RING-107", Boolean.FALSE,"Line Thickness",DEF_LINE_SZ);

    initProp(PROP_USE_GRADIENT, Boolean.class, "RING-108", Boolean.FALSE,"Use Gradient Colors?",DEF_USE_GRADIENT);
    initProp(PROP_ACTIVE_COLOR, Color.class, "RING-109", Boolean.FALSE,"Flat Color",DEF_ACTIVE_COLOR);
    initProp(PROP_GRADIENT_START_COLOR, Color.class, "RING-110", Boolean.TRUE,"Gradient Start Color",DEF_GRADIENT_START_COLOR);
    initProp(PROP_GRADIENT_END_COLOR, Color.class, "RING-111", Boolean.TRUE,"Gradient End Color",DEF_GRADIENT_END_COLOR);
    initProp(PROP_INACTIVE_COLOR, Color.class, "RING-112", Boolean.FALSE,"Inactive Color",DEF_INACTIVE_COLOR);
    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.FALSE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.TRUE,"Fill Color",DEF_FILL_COLOR);

  }

  /**
   * setValueAt
   *
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    if (row == PROP_TEXT_SZ) {
      int size = Integer.valueOf((String) value);
      if (size <= 0) {
        JOptionPane.showMessageDialog(null, 
            "Field Size must be > 0", 
            "ERROR",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    super.setValueAt(value, row, col);
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
    if (row == PROP_USE_GRADIENT) {
      if (useGradientColors()) {
        data[PROP_GRADIENT_START_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_GRADIENT_END_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_ACTIVE_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
      } else {
        data[PROP_GRADIENT_START_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_GRADIENT_END_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_ACTIVE_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
      }   
      fireTableCellUpdated(PROP_GRADIENT_START_COLOR, COLUMN_VALUE);
      fireTableCellUpdated(PROP_GRADIENT_END_COLOR, COLUMN_VALUE);
      fireTableCellUpdated(PROP_ACTIVE_COLOR, COLUMN_VALUE);
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
   * getWidth
   *
   * @see builder.models.WidgetModel#getWidth()
   */
  public int getWidth() {
    return (((Integer) (data[PROP_WIDTH][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * getHeight
   *
   * @see builder.models.WidgetModel#getHeight()
   */
  public int getHeight() {
    return (((Integer) (data[PROP_HEIGHT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Checks if is Clockwise direction.
   *
   * @return true, if is Clockwise
   */
  public boolean isClockwise() {
    return ((Boolean) data[PROP_DIRECTION][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * getLineThickness
   *
   * @return line thickness
   */
  public int getLineThickness() {
    return (((Integer) (data[PROP_LINE_SZ][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * getSegments
   *
   * @return number of line segments
   */
  public int getSegments() {
    return (((Integer) (data[PROP_SEGMENTS][PROP_VAL_VALUE])).intValue());
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
   * Gets the text storage.
   *
   * @return the text storage
   */
  public int getTextStorage() {
    return (((Integer) (data[PROP_TEXT_SZ][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the starting angle.
   *
   * @return the max
   */
  public int getStartingAngle() {
    return (((Integer) (data[PROP_STARTING_ANGLE][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the angular range.
   *
   * @return the max
   */
  public int getAngularRange() {
    return (((Integer) (data[PROP_ANGULAR_RANGE][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the font display name.
   *
   * @return the font display name
   */
  @Override
  public String getFontDisplayName() {
    return (String) ((String)data[PROP_FONT][PROP_VAL_VALUE]);
  }
 
  /**
   * Gets the font enum.
   *
   * @return the font enum
   */
  @Override
  public String getFontEnum() {
    return ff.getFontEnum(getFontDisplayName());
  }
 
  /**
   * Gets the text color.
   *
   * @return the text color
   */
  public Color getTextColor() {
    return (((Color) data[PROP_TEXT_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Use Gradient colors.
   *
   * @return <code>true</code>, if successful
   */
  public boolean useGradientColors() {
    return ((Boolean) data[PROP_USE_GRADIENT][PROP_VAL_VALUE]).booleanValue();
  }
  
 /**
  * Gets the active color.
  *
  * @return the active color
  */
 public Color getActiveColor() {
   return (((Color) data[PROP_ACTIVE_COLOR][PROP_VAL_VALUE]));
 }

 /**
  * Gets the gradient start color.
  *
  * @return the gradient start color
  */
 public Color getGradientStartColor() {
   return (((Color) data[PROP_GRADIENT_START_COLOR][PROP_VAL_VALUE]));
 }

 /**
  * Gets the gradient end color.
  *
  * @return the gradient end color
  */
 public Color getGradientEndColor() {
   return (((Color) data[PROP_GRADIENT_END_COLOR][PROP_VAL_VALUE]));
 }

 /**
  * Gets the inactive color.
  *
  * @return the inactive color
  */
 public Color getInactiveColor() {
   return (((Color) data[PROP_INACTIVE_COLOR][PROP_VAL_VALUE]));
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
   if (useGradientColors()) {
     data[PROP_GRADIENT_START_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
     data[PROP_GRADIENT_END_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
     data[PROP_ACTIVE_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
   } else {
     data[PROP_GRADIENT_START_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
     data[PROP_GRADIENT_END_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
     data[PROP_ACTIVE_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
   }   
 }

}
