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

import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;

import builder.commands.PropertyCommand;
import builder.common.EnumFactory;

/**
 * The Class GridModel implements the model for the Grid on our design canvas.
 * 
 * @author Paul Conti
 * 
 */
public class GridModel extends WidgetModel {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Property Index Constants. */
  public static final int GRID_WIDTH        = 1;
  public static final int GRID_HEIGHT       = 2;
  public static final int GRID_MAJOR_WIDTH  = 3;
  public static final int GRID_MAJOR_HEIGHT = 4;
  public static final int GRID_MAJOR_COLOR  = 5;
  public static final int GRID_MINOR_COLOR  = 6;
  public static final int GRID_BACKGROUND_COLOR  = 7;
  
  /** The Property Defaults */
  static public  final Integer DEF_WIDTH              = Integer.valueOf(10);
  static public  final Integer DEF_HEIGHT             = Integer.valueOf(10);
  static public  final Integer DEF_MAJOR_WIDTH        = Integer.valueOf(50);
  static public  final Integer DEF_MAJOR_HEIGHT       = Integer.valueOf(50);
  static public  final Color   DEF_MAJOR_COLOR        = Color.BLACK;
  static public  final Color   DEF_MINOR_COLOR        = new Color(128,128,128);
  static public  final Color   DEF_BACKGROUND         = Color.WHITE;

  private boolean bGridState;
  private boolean bGridSnapState;

  /**
   * Instantiates a new grid model.
   */
  public GridModel() {
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.GRID;
    data = new Object[8][5];

    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(GRID_WIDTH, Integer.class, "GRID-102", Boolean.FALSE,"Grid Minor Width",DEF_WIDTH);
    initProp(GRID_HEIGHT, Integer.class, "GRID-103", Boolean.FALSE,"Grid Minor Height",DEF_HEIGHT);
    initProp(GRID_MAJOR_WIDTH, Integer.class, "GRID-104", Boolean.FALSE,
        "Grid Major Width",DEF_MAJOR_WIDTH);
    initProp(GRID_MAJOR_HEIGHT, Integer.class, "GRID-105", Boolean.FALSE,
        "Grid Major Height",DEF_MAJOR_HEIGHT);

    initProp(GRID_MAJOR_COLOR, Color.class, "COL-311", Boolean.FALSE,"Grid Major Line Color", DEF_MAJOR_COLOR);
    initProp(GRID_MINOR_COLOR, Color.class, "COL-312", Boolean.FALSE,"Grid Minor Line Color",DEF_MINOR_COLOR);
    initProp(GRID_BACKGROUND_COLOR, Color.class, "COL-313", Boolean.FALSE,
        "Grid Background Color",DEF_BACKGROUND);
    bGridState = false;
    bGridSnapState = false;
  }
  
  /**
   * setValueAt.
   *
   * @param value
   *          the value
   * @param row
   *          the row
   * @param col
   *          the col
   * @see javax.swing.table.AbstractTableModel#setValueAt(Object, int,
   *      int)
   */
  @SuppressWarnings("unused")
  @Override
  public void setValueAt(Object value, int row, int col) {
    if (col == COLUMN_VALUE) {
      // check for invalid data
      if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        try {
          int test = Integer.valueOf(Integer.parseInt((String)value));
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(null, "You entered non-numeric data in an number field.", 
              "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      // commands are used to support undo and redo actions.
      PropertyCommand c = new PropertyCommand(this, value, row);
      execute(c);
    }
  }

  /**
   * changeValueAt is a method used by our command interface 
   * so we can support undo/redo commands.
   * setValue will create the command and execute will 
   * cause the command to call this routine.
   * It is also responsible for sending around repaint notices
   * to our observer views, like Pagepage.
   * 
   * @param value - new object value
   * @param row   - row in table to change
   */
  public void changeValueAt(Object value, int row) {
    // The test for Integer supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      data[row][PROP_VAL_VALUE] = value;
    }
    fireTableCellUpdated(row, COLUMN_VALUE);
  }

 /**
   * getEditorAt
   *
   * @see WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int rowIndex) {
    return null;
  }

  /**
   * Gets the grid.
   *
   * @return the grid
   */
  public boolean getGrid() {
    return bGridState;
  }
  
  /**
   * Sets the grid.
   *
   * @param the grid
   */
  public void setGrid(boolean b) {
    bGridState = b;
  }
  
  /**
   * Gets the grid snap to.
   *
   * @return the grid snap to
   */
  public boolean getGridSnapTo() { return bGridSnapState; }

  /**
   * Sets the grid snap to.
   *
   * @param b the new grid snap to
   */
  public void setGridSnapTo(boolean b) {
    bGridSnapState = b;
  }
  
  /**
   * Gets the grid minor width.
   *
   * @return the grid minor width
   */
  public int getGridMinorWidth() {
    return (((Integer) (data[GRID_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the grid minor height.
   *
   * @return the grid minor height
   */
  public int getGridMinorHeight() {
    return (((Integer) (data[GRID_HEIGHT][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the fill color.
   *
   * @return the fill color
   */
  public Color getBackGroundColor() {
    return (((Color) data[GRID_BACKGROUND_COLOR][PROP_VAL_VALUE]));
  }

  

  /**
   * Gets the grid major width.
   *
   * @return the grid major width
   */
  public int getGridMajorWidth() {
    return (((Integer) (data[GRID_MAJOR_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the grid major height.
   *
   * @return the grid major height
   */
  public int getGridMajorHeight() {
    return (((Integer) (data[GRID_MAJOR_HEIGHT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the grid minor color.
   *
   * @return the grid minor color
   */
  public Color getGridMinorColor() {
    return (((Color) data[GRID_MINOR_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the grid major color.
   *
   * @return the grid major color
   */
  public Color getGridMajorColor() {
    return (((Color) data[GRID_MAJOR_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * isCellEditable
   *
   * @see WidgetModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    if (col == 0 || row == 0)
      return false;
    return true;
  }

}
