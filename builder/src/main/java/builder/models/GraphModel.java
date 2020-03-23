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

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import builder.common.ColorFactory;
import builder.common.EnumFactory;
import builder.events.MsgBoard;

/**
 * The Class GraphModel implements the model for the Graph widget.
 * 
 * @author Paul Conti
 * 
 */
public class GraphModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemGraph";
  
  /** Graph Style Constants. */
  static public  final String  STYLE_DOT          = "GSLCX_GRAPH_STYLE_DOT";
  static public  final String  STYLE_LINE         = "GSLCX_GRAPH_STYLE_LINE";
  static public  final String  STYLE_FILL         = "GSLCX_GRAPH_STYLE_FILL";

  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_ROWS              = 8;
  static private final int PROP_STYLE             = 9;
  static private final int PROP_GRAPH_COLOR       = 10;
  static private final int PROP_DEFAULT_COLORS    = 11;
  static private final int PROP_FRAME_COLOR       = 12;
  static private final int PROP_FILL_COLOR        = 13;
  static private final int PROP_SELECTED_COLOR    = 14;

  /** The Property Defaults */
  static public  final Integer DEF_ROWS              = Integer.valueOf(0);
  static public  final String  DEF_STYLE             = "GSLCX_GRAPH_STYLE_DOT";
  static public  final Color   DEF_GRAPH_COLOR       = Color.ORANGE;
  static public  final Boolean DEF_DEFAULT_COLORS    = Boolean.TRUE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 180;
  static private final int DEF_HEIGHT= 120;
  
  /** The cb style. */
  JComboBox<String> cbStyle;
  
  /** The style cell editor. */
  DefaultCellEditor styleCellEditor;

  /**
   * Instantiates a new graph model.
   */
  public GraphModel() {
    cf = ColorFactory.getInstance();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.GRAPH;
    data = new Object[15][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);

    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font","BuiltIn->5x8pt7b");

    initProp(PROP_ROWS, Integer.class, "GRPH-100", Boolean.FALSE,"Maximum Points",DEF_ROWS);
    initProp(PROP_STYLE, String.class, "GRPH-102", Boolean.FALSE,"Graph Style",DEF_STYLE);

    initProp(PROP_GRAPH_COLOR, Color.class, "COL-309", Boolean.FALSE,"Color of Graph",DEF_GRAPH_COLOR);

    initProp(PROP_DEFAULT_COLORS, Boolean.class, "COL-300", Boolean.FALSE,"Use Default Colors?",DEF_DEFAULT_COLORS);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.TRUE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.TRUE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.TRUE,"Selected Color",DEF_SELECTED_COLOR);

    cbStyle = new JComboBox<String>();
    cbStyle.addItem(STYLE_DOT);
//    cbStyle.addItem(STYLE_LINE);
    cbStyle.addItem(STYLE_FILL);
    styleCellEditor = new DefaultCellEditor(cbStyle);
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
    if (row == PROP_DEFAULT_COLORS) {
      // check for switching back and forth
      if (useDefaultColors()) {
        data[PROP_FRAME_COLOR][PROP_VAL_VALUE]=cf.getDefFrameCol(); 
        data[PROP_FILL_COLOR][PROP_VAL_VALUE]=cf.getDefFillCol();
        data[PROP_SELECTED_COLOR][PROP_VAL_VALUE]=cf.getDefGlowCol(); 
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
   * Gets the font display name.
   *
   * @return the font display name
   */
  @Override
  public String getFontDisplayName() {
    return (String) ((String)data[PROP_FONT][PROP_VAL_VALUE]);
  }
  
  /**
   * Gets the graph color.
   *
   * @return the graph color
   */
  public Color getGraphColor() {
    return (((Color) data[PROP_GRAPH_COLOR][PROP_VAL_VALUE]));
  }
  
  /**
   * Gets the num rows.
   *
   * @return the num rows
   */
  public int getNumRows() {
    return (((Integer) (data[PROP_ROWS][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the graph style.
   *
   * @return the graph style
   */
  public String getGraphStyle() {
    return (String) data[PROP_STYLE][PROP_VAL_VALUE];
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
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int rowIndex) {
    if (rowIndex == PROP_STYLE)
      return styleCellEditor;
    return null;
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
    String strKey = "";
    int n = 0;
    String strCount = ""; 
    String ref = ""; 
    if (getElementRef().isEmpty()) {
      ref = ELEMENTREF_NAME;
      strKey = getKey();
      n = strKey.indexOf("$");
      strCount = strKey.substring(n+1, strKey.length());
      ref = ref + strCount;
      setElementRef(ref);
    }
  }

}
