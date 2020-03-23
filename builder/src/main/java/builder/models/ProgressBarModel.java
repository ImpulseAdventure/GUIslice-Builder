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

import builder.common.EnumFactory;
import builder.events.MsgBoard;

// TODO: Auto-generated Javadoc
/**
 * The Class ProgressBarModel implements the model for the Progress Bar widget.
 * 
 * @author Paul Conti
 * 
 */
public class ProgressBarModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name. */
  public static final String ELEMENTREF_NAME = "m_pElemProgress";
  
  /** The Constant ELEMENTREF_NAME. */
  static private final int PROP_VERTICAL          =7;
  static private final int PROP_MIN               =8;
  static private final int PROP_MAX               =9;
  static private final int PROP_CURVALUE          =10;
  static private final int PROP_GAUGE_COLOR       =11;
  static private final int PROP_USE_FLASH         =12;
  static private final int PROP_DEFAULT_COLORS    =13;
  static private final int PROP_FRAME_COLOR       =14;
  static private final int PROP_FILL_COLOR        =15;
  static private final int PROP_SELECTED_COLOR    =16;
    
  /** The Property Defaults */
  static public  final Boolean DEF_VERTICAL          = Boolean.FALSE;
  static public  final Integer DEF_MIN               = Integer.valueOf(0);
  static public  final Integer DEF_MAX               = Integer.valueOf(100);
  static public  final Integer DEF_CURVALUE          = Integer.valueOf(0);
  static public  final Color   DEF_GAUGE_COLOR       = Color.GREEN;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Boolean DEF_DEFAULT_COLORS    = Boolean.TRUE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 50;
  static private final int DEF_HEIGHT= 12;

  /**
   * Instantiates a new progress bar model.
   */
  public ProgressBarModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.PROGRESSBAR;
    data = new Object[17][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_VERTICAL, Boolean.class, "BAR-100", Boolean.FALSE,"Vertical?",DEF_VERTICAL);

    initProp(PROP_MIN, Integer.class, "BAR-102", Boolean.FALSE,"Minimum Value",DEF_MIN);
    initProp(PROP_MAX, Integer.class, "BAR-103", Boolean.FALSE,"Maximum Value",DEF_MAX);
    initProp(PROP_CURVALUE, Integer.class, "BAR-104", Boolean.FALSE,"Starting Value",DEF_CURVALUE);

    initProp(PROP_GAUGE_COLOR, Color.class, "COL-308", Boolean.FALSE,"Gauge Indicator Color",DEF_GAUGE_COLOR);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_DEFAULT_COLORS, Boolean.class, "COL-300", Boolean.FALSE,"Use Default Colors?",DEF_DEFAULT_COLORS);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.TRUE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.TRUE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.TRUE,"Selected Color",DEF_SELECTED_COLOR);

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
   * Gets the indicator color.
   *
   * @return the indicator color
   */
  public Color getIndicatorColor() {
    return (((Color) data[PROP_GAUGE_COLOR][PROP_VAL_VALUE]));
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
    if (row == PROP_VERTICAL) {
      // swap height and width
      int tmp = getWidth();
      setWidth(getHeight());
      setHeight(tmp);
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
      fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
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
//  System.out.println("WM readModel() " + getKey());
    if (widgetType != null)
      this.widgetType = widgetType;
    bSendEvents = in.readBoolean();
//  System.out.println("bSendEvents: " + bSendEvents);
    int rows = in.readInt();
    String metaID = null;
    Object objectData = null;
    int row;
//  System.out.println("WM rows: " + rows);
    boolean bNeedFix = false;
    for (int i=0; i<rows; i++) {
      metaID = (String)in.readObject();
      objectData = in.readObject();
      // work-around fix for bug in beta release where metaID's BAR-100, BAR-101 were duplicated
      // and BAR-102 was miss-assigned
      if (metaID.equals("BAR-102") && bNeedFix) {
        metaID = "BAR-104";
        bNeedFix = false;
      }
      if (metaID.equals("BAR-100") && objectData instanceof Integer) {
        metaID = "BAR-102";
        bNeedFix = true;
      }
      if (metaID.equals("BAR-101") && objectData instanceof Integer) {
        metaID = "BAR-103";
      }
      // now that we remapped them we need to do another converting isRamp to our cbStyle combo
      if (metaID.equals("BAR-101") && objectData instanceof Boolean) {
         // ignore
        continue;
      }
      row = mapMetaIDtoProperty(metaID);
// System.out.println("metaID: " + metaID + " row: " + row);
      if (row >= 0) {
        data[row][PROP_VAL_VALUE] = objectData;
        
// System.out.println(data[row][PROP_VAL_NAME].toString() + ": " +
//   data[row][PROP_VAL_VALUE].toString() + " mapped to row " + row);
        
      }
    }
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
