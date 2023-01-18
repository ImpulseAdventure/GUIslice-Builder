/**
 *
 * The MIT License
 *
 * Copyright 2018-2023 Paul Conti
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import builder.Builder;
import builder.commands.Command;
import builder.commands.History;
import builder.commands.PropertyCommand;
import builder.common.Utils;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.fonts.FontFactory;
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class WidgetModel is the base class for all of our models.
 * 
 * @author Paul Conti
 * 
 */
public class WidgetModel extends AbstractTableModel {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Column Constant Values. */
  static public final int PROP_VAL_CLASS=0;
  
  /** The Constant PROP_VAL_ID. */
  static public final int PROP_VAL_ID=1;
  
  /** The Constant PROP_VAL_READONLY. */
  static public final int PROP_VAL_READONLY=2;
  
  /** The Constant PROP_VAL_NAME. */
  static public final int PROP_VAL_NAME=3;
  
  /** The Constant PROP_VAL_VALUE. */
  static public final int PROP_VAL_VALUE=4;
  
  /** The Property Index Constants. */
  static public final int PROP_KEY            = 0;  // key is our primary index
  
  /** The Constant PROP_ENUM. */
  static public final int PROP_ENUM           = 1;
  
  /** The Constant PROP_X. */
  static public final int PROP_X              = 2;
  
  /** The Constant PROP_Y. */
  static public final int PROP_Y              = 3;
  
  /** The Constant PROP_WIDTH. */
  static public final int PROP_WIDTH          = 4;
  
  /** The Constant PROP_HEIGHT. */
  static public final int PROP_HEIGHT         = 5;
  
  /** The Constant PROP_ELEMENTREF. */
  static public final int PROP_ELEMENTREF     = 6;
  
  /** 
   * The data is made up of 5 columns, the first three are hidden from users view.
   *  Column 0 is the Class of the JTable cell contents, like String, Integer, Color, etc...
   *  Column 1 has the Meta Property ID. 
   *  Column 2 is a boolean indicating if this cell is read-only.
   *  Column 3 is the Property Name exposed to users.
   *  Column 4 is the cells Property value, also exposed to users. 
   */
  Object[][] data;
  
  /** The JTable column names. */
  String[] columnNames = {"Name", "Value"};
  
  /** The Constant COLUMN_NAME which is the index into our JTable the user sees. */
  static public final int COLUMN_NAME         =0;

  /** The Constant COLUMN_VALUE which is the index into our JTable the user sees. */
  static public final int COLUMN_VALUE        =1;
  
  /** The widget type. */
  String widgetType; // type is always hidden from user so keep it out of data array
  
  /** The color factory. */
  GUIsliceThemeFactory cf = null;
  
  /** The event. */
  MsgEvent event;
  
  /** The b send events. */
  boolean bSendEvents = true;
  
  /** Did the model change during this session */
  public boolean bModelChanged = false;
  
  public WidgetModel() {
    cf = GUIsliceThemeFactory.getInstance();
  }
  
  /**
   * initProp - helper method for loading a set of property attributes.
   *
   * @param row
   *          the cell's row within our data table
   * @param c
   *          the class of this cell
   * @param id
   *          the meta property id - must not change over the life of the builder
   * @param readOnly
   *          boolean indicating if the cell is currently read only
   * @param name
   *          the property name
   * @param value
   *          the default property value
   */
  public void initProp(int row, Class<?> c, String id, Boolean readOnly, String name, Object value) {
    data[row][PROP_VAL_CLASS]=c;
    data[row][PROP_VAL_ID]=id;
    data[row][PROP_VAL_READONLY]=readOnly;
    data[row][PROP_VAL_NAME]=name;
    data[row][PROP_VAL_VALUE]=value;
  }
  
  /**
   * initCommonProps - Initialize the set of common property attributes.
   *
   * @param width
   *          the widget's width
   * @param height
   *          the widget's height
   */
  public void initCommonProps(int width, int height) {
    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_ENUM, String.class, "COM-002", Boolean.FALSE,"ENUM",widgetType);
    initProp(PROP_X, Integer.class, "COM-003", Boolean.FALSE,"X",Integer.valueOf(0));
    initProp(PROP_Y, Integer.class, "COM-004", Boolean.FALSE,"Y",Integer.valueOf(0));
    initProp(PROP_WIDTH, Integer.class, "COM-005", Boolean.FALSE,"Width",Integer.valueOf(width));
    initProp(PROP_HEIGHT, Integer.class, "COM-006", Boolean.FALSE,"Height",Integer.valueOf(height));
    initProp(PROP_ELEMENTREF, String.class, "COM-019", Boolean.FALSE,"ElementRef","");
    bModelChanged = false;
  }
  
  /**
   * Turn off events.
   */
  public void TurnOffEvents() {
    bSendEvents = false;
  }
  
  /**
   * Turn on events.
   */
  public void TurnOnEvents() {
    bSendEvents = true;
  }
  
  public void setModelChanged() {
    if (bSendEvents)
      bModelChanged = true;
  }
  
  /**
   * get count from key strips "$" off of key.
   *
   * @return the <code>String</code> without 'E_' at beginning
   */
  public String getKeyCount() {
    String key = getKey();
    int n = key.indexOf("$");
    return (key.substring(n+1));
  }
  
  
  /**
   * getRowCount gives back the number of user visible properties.
   *
   * @return the row count
   * @see javax.swing.table.TableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    return data.length;  
  }

  /**
   * Use Flash API.
   *
   * @return <code>true</code>, if flash is to be used
   */
  public boolean useFlash() {
    return false;
  }
  
  /**
   * is Toggle?
   *
   * @return <code>true</code>, if successful
   */
  public boolean isToggle() {
    return false;
  }
  
  /**
   * getPropertyCount gives the actual number of properties while getRowCount
   * gives back the number of user visible properties allowing us to hide some at
   * the end of data[][].
   *
   * @return the property count
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getPropertyCount() {
    return data.length;  
  }

  /**
   * getColumnName.
   *
   * @param index
   *          the index
   * @return the column name
   * @see javax.swing.table.AbstractTableModel#getColumnName(int)
   */
  @Override
  public String getColumnName(int index) {
      return columnNames[index];
  }
  
  /**
   * getColumnCount.
   *
   * @return the column count
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * getValueAt 
   * This routine is called by JTable to display values
   * but we don't want to show read only values to the user.
   * 
   * The first three fields of our data array is always hidden so we
   * just add 3 to columnIndex when our property editor asks for a field. This
   * makes our five field table look a two field table.
   * JTable asks for col 0 we give back PROP_VAL_NAME 
   * and for col 1 we give back PROP_VAL_VALUE.
   *
   * @param rowIndex
   *          the row index
   * @param columnIndex
   *          the column index
   * @return the value at
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  @Override
  public Object getValueAt(int row, int col) {
    if ((boolean) data[row][PROP_VAL_READONLY] && col == 1) 
      return "";
    return data[row][col+3];
  }
  
  /**
   * getDataValue 
   * This routine is called by out User Preferences Editors
   * during updateModel() function. Unlike getValueAt we don't screen
   * read only fields.
   * 
   * The first three fields of our data array is always hidden so we
   * just add 3 to columnIndex when our property editor asks for a field. 
   * This makes our five field table look a two field table.
   *
   * @param rowIndex
   *          the row index
   * @param columnIndex
   *          the column index
   * @return the value at
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getDataValue(int row, int col) {
    return data[row][col+3];
  }
  
  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return widgetType;
  }
  
  /**
   * Gets the MetaId.
   *
   * @return the metaid
   */
  public String getMetaId(int row) {
    return (String)data[row][PROP_VAL_ID];
  }
  
  /**
   * Sets the type.
   *
   * @param type
   *          the new type
   */
  public void setType(String type) {
    widgetType = type;
  }
  
  /**
   * Gets the key.
   * key property is used as an index key in various lists to identify specific
   * object instances.  It starts off as simply a copy of widgetType and gets
   * a serial number appended to make it unique.  
   * ie. WidgetType$1,..WidgetType$N.
   *
   * @return the key
   * @see builder.common.EnumFactory#createKey(java.lang.String)
   */
  public String getKey() {
    return (String) data[PROP_KEY][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the key.
   *
   * @param key
   *          the new key
   */
  public void setKey(String key) { 
    shortcutValue(key, PROP_KEY);
  }
  
  /**
   * Gets the enum.
   * The enum is for the GUIslice API's use as an identifier of the widget.
   * @return the enum
   */
  public String getEnum() {
    return (String) data[PROP_ENUM][PROP_VAL_VALUE];
  }

  /**
   * Gets the property name for a row.
   * 
   * @param row the row of our data table
   * @return the property name
   */
  public String getPropertyName(int row) {
    return (String) data[row][PROP_VAL_NAME];
  }

  /**
   * Sets the enum.
   *
   * @param s
   *          the new enum
   */
  public void setEnum(String s) { 
    shortcutValue(s, PROP_ENUM);
  }
  
  /**
   * Gets the x coordinate.
   *
   * @return the x
   */
  public int getX() {
    return (((Integer) (data[PROP_X][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Sets the x coordinate.
   *
   * @param x
   *          the new x
   */
  public void setX(int x) {
    shortcutValue(Integer.valueOf(x), PROP_X);
  }

  /**
   * Gets the y coordinate.
   *
   * @return the y
   */
  public int getY() {
    return (((Integer) (data[PROP_Y][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Sets the y coordinate.
   *
   * @param y
   *          the new y
   */
  public void setY(int y) {
    shortcutValue(Integer.valueOf(y), PROP_Y);
  }

  /**
   * Gets the width.
   *
   * @return the width
   */
  public int getWidth() {
    return (((Integer) (data[PROP_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Sets the width.
   *
   * @param w
   *          the new width
   */
  public void setWidth(int w) {
    shortcutValue(Integer.valueOf(w), PROP_WIDTH);
  }

  /**
   * Gets the height.
   *
   * @return the height
   */
  public int getHeight() {
    return (((Integer) (data[PROP_HEIGHT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Sets the height.
   *
   * @param h
   *          the new height
   */
  public void setHeight(int h) {
    shortcutValue(Integer.valueOf(h), PROP_HEIGHT);
  }

  /**
   * Gets the element ref.
   *
   * @return the element ref
   */
  public String getElementRef() {
    return (String) data[PROP_ELEMENTREF][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the element ref.
   *
   * @param s
   *          the new element ref
   */
  public void setElementRef(String s) { 
    shortcutValue(s, PROP_ELEMENTREF);
  }
  
  /**
   * Gets the font display name.
   *
   * @return the font display name
   */
  public String getFontDisplayName() {
    return null;
  }
  
  /**
   * Gets the font enum.
   *
   * @return the font enum
   */
  public String getFontEnum() {
    return null;
  }
  
  /**
   * Checks if we need to add a scrollbar.
   *
   * @return true, if we add a scrollbar
   */
  public boolean addScrollbar() {
    return false;
  }

  /**
   * Gets the scrollbar enum.
   *
   * @return the scrollbar enum
   */
  public String getScrollbarEnum() {
    return null;
  }
  
  /**
   * Gets the scrollbar eref.
   *
   * @return the scrollbar enum
   */
  public String getScrollbarERef() {
    return null;
  }
  
  /**
   * Gets the group id.
   *
   * @return the group id
   */
  public String getGroupId() {
    return null;
  }

  /**
   * setFontReadOnly
   * Called by various Editors so users can't change 
   * default fonts in some objects.
   */
  public void setFontReadOnly() {

  }
  
  /**
   * setFont 
   * dummy routine that subclasses that use fonts should override
   * @param displayName
   */
  public void setFont(String displayName) {
    
  }
  
  /**
   * Gets the class at.
   *
   * @param rowIndex
   *          the row index
   * @return the class at
   */
  public Class<?> getClassAt(int rowIndex) {
    return (Class<?>)data[rowIndex][PROP_VAL_CLASS];
  }

  /**
   * Gets the editor at.
   *
   * @param rowIndex
   *          the row index
   * @return the editor at
   */
  public TableCellEditor getEditorAt(int rowIndex) {
    return null;
  }
  
  /**
   * Gets the renderer at.
   *
   * @param rowIndex
   *          the row index
   * @return the renderer at
   */
  public TableCellRenderer getRendererAt(int rowIndex) {
    return null;
  }

  /**
   * isCellReadOnly.
   *
   * @param row
   *          cell row number
   * @return true, if is cell readonly
   */
  public boolean isCellReadOnly(int row) {
    if (data[row][PROP_VAL_READONLY] instanceof String) {
      System.out.println("What???");
    }
    return ((Boolean) data[row][PROP_VAL_READONLY]).booleanValue();
  }
  
  /**
   * isCellEditable.
   *
   * @param row
   *          the JTable row in question
   * @param col
   *          the JTable col 
   * @return true, if is cell editable
   * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    if (col == 0) return false;
    return !isCellReadOnly(row);
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
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int,
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
   * shortcutValue() is a method used by our setXXX() methods so
   * Widgets can sync models with User Preferences, and Pagepane
   * can drag widgets around without creating circular loops.
   * Note that column isn't required because changes are only
   * allowed for column 2.  Again remember our column 2 is
   * thought to be column 1 by our JTable.
   * 
   * @param value - new object value
   * @param row   - row in table to change
   */
  public void shortcutValue(Object value, int row) {
    data[row][PROP_VAL_VALUE] = value;
    fireTableCellUpdated(row, COLUMN_VALUE);
  }

  /**
   * execute() will indirectly call changeValueAT() to make
   * the actual changes to the table.  It will also record a 
   * historical record of the change for undo/redo.
   * 
   * @param c is the property change command to run.
   */
  public void execute(Command c) {
    c.execute();
    History.getInstance().push(c);
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
    if (row > PROP_HEIGHT || row == PROP_ENUM)
      bModelChanged = true;

    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
      }
    } 
  }

  /**
   * Gets the data.
   *
   * @return the data
   */
  public Object[][] getData() {
    return data;
  }

  /**
   * Gets the image.
   *
   * @return the image
   */
  public BufferedImage getImage() {
    return null;
  }

  /**
   * Sets the image.
   *
   * @return the image
   */
  public void setImage(BufferedImage image) {
    
  }

  /**
   * Gets the image selected.
   *
   * @return the image selected
   */
  public BufferedImage getImageSelected() {
    return null;
  }

  /**
   * Sets the image selected.
   * used by copy/paste
   * @param the image selected
   */
  public void setImageSelected(BufferedImage imageSelected) {

  }

  /**
   * Sets the data - Used for JUNIT testing someday.
   *
   * @param data
   *          the new data
   */
  public void setData(Object[][] data) {
    this.data = data;
  }

  /**
   * Gets the mapped properties.
   *
   * @param pageEnum
   *          the page enum
   * @return the mapped properties
   */
  public Map<String, String> getMappedProperties(String pageEnum) {
    int def=0;
    GUIsliceThemeFactory cf = GUIsliceThemeFactory.getInstance();
    FontFactory  ff = FontFactory.getInstance();
    Map<String, String> map = new HashMap<>();
    // start with our page enum
    map.put("COM-000", pageEnum);
    // place our unique count value from the key into our map
    map.put("COM-018", getKeyCount());
    // now rip through our data model and add each property to our map
    int rows = getRowCount();
    for (int i=0; i<rows; i++) {
      String key = (String)data[i][PROP_VAL_ID];
      Object o = getValueAt(i, WidgetModel.COLUMN_VALUE);
      if(o instanceof String) {
        if (key.equals("TXT-200")) {
          // special case font enum's which are not stored
          map.put("TXT-211", ff.getFontEnum((String)o));
        } else {
          map.put(key, (String)o);
        }
      } else if(o instanceof Integer) {
        def = ((Integer)o).intValue();
        if (key.equals("TXT-205")) {
          //special case Text storage size needs a 1 added
           def++;
        }
        map.put(key, String.valueOf(def));
      } else if(o instanceof Boolean) {
        map.put(key, ((Boolean)o).toString());
      } else if (o instanceof Color) {
        map.put(key, cf.colorAsString((Color)o));
      }
    }
    return map;
  }
  
  /**
   * backup() supports our undo command by making a copy of the table cell's value
   * before any changes have taken place. This version is only used to
   * deal with X,Y,Width, and Height and is only called by
   * AlignPropertyMemento as an optimization to avoid serializing the full model.
   *
   * @param row
   *          the row
   * @return cell's current value.
   * @see builder.commands.AlignBottomCommand
   * @see builder.commands.AlignCenterCommand
   * @see builder.commands.AlignHeightCommand
   * @see builder.commands.AlignHSpacingCommand
   * @see builder.commands.AlignLeftCommand
   * @see builder.commands.AlignRightCommand
   * @see builder.commands.AlignTopCommand
   * @see builder.commands.AlignVSpacingCommand
   * @see builder.commands.AlignWidthCommand
   * @see builder.mementos.AlignPropertyMemento
   * @see builder.commands.History
   */
  public Object backup(int row) {
    return data[row][PROP_VAL_VALUE];
  }

  /**
   * backup() supports our undo command by making a copy of 
   * all the model's cell values and read-only settings
   * before any changes have taken place.
   *
   * @return Model's cell values as a serialize string.
   * @see builder.commands.PropertyCommand
   * @see builder.mementos.PropertyMemento
   * @see builder.commands.History
   */
  public String backup() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
//    System.out.println("WM writeModel(): " + getKey());
//    System.out.println("bSendEvents: " + bSendEvents);
      int rows = getRowCount();
//    System.out.println("WM rows: " + rows);
      out.writeInt(rows);
      for (int i=0; i<rows; i++) {
        out.writeObject(data[i][PROP_VAL_READONLY]);
        out.writeObject(data[i][PROP_VAL_VALUE]);
//     System.out.println(data[i][PROP_VAL_ID] + ": "
//       + data[i][PROP_VAL_VALUE].toString() +
//       " Read-Only: " + data[i][PROP_VAL_READONLY].toString());
      }
      out.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
      return "";
    }
  }

  /**
   * restore() supports our redo command by replacing the current value of the
   * cell with the backup copy made earlier.
   *
   * @param oldValue
   *          the Model's cell previous value
   * @param row
   *          the row
   * @see builder.commands.AlignBottomCommand
   * @see builder.commands.AlignCenterCommand
   * @see builder.commands.AlignHeightCommand
   * @see builder.commands.AlignHSpacingCommand
   * @see builder.commands.AlignLeftCommand
   * @see builder.commands.AlignRightCommand
   * @see builder.commands.AlignTopCommand
   * @see builder.commands.AlignVSpacingCommand
   * @see builder.commands.AlignWidthCommand
   * @see builder.mementos.AlignPropertyMemento
   * @see builder.commands.History
   */
  public void restore(Object oldValue, int row) {
    data[row][PROP_VAL_VALUE] = oldValue;
    fireTableCellUpdated(1, COLUMN_VALUE);
  }

  /**
   * restore() supports our redo command by replacing all of the 
   * Model's current cell values with the backup copy made earlier.
   *
   * @param oldValue
   *          the Serialized model's cell values
   * @see builder.commands.PropertyCommand
   * @see builder.mementos.PropertyMemento
   * @see builder.commands.History
   */
  public void restore(String oldValue) {
    try {
      byte[] state = Base64.getDecoder().decode(oldValue);
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(state));
//    System.out.println("WM rows: " + rows);
      int rows = in.readInt();
      for (int i = 0; i < rows; i++) {
        data[i][PROP_VAL_READONLY] = in.readObject();
        data[i][PROP_VAL_VALUE] = in.readObject();
//      System.out.println(data[i][PROP_VAL_ID] + ": "
//      + data[i][PROP_VAL_VALUE].toString() +
//      " Read-Only: " + data[i][PROP_VAL_READONLY].toString());
      }
      in.close();
      fireTableDataChanged();
    } catch (ClassNotFoundException e) {
      System.out.println("ClassNotFoundException occurred.");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IOException occurred." + e.toString());
      e.printStackTrace();
    }
  }

  /**
   * writeModel() will serialize our model's data to a string object for backup
   * and recovery.
   *
   * @param out
   *          is our ObjectOutputStream.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see builder.mementos.PositionMemento
   * @see builder.mementos.WidgetMemento
   * @see builder.views.PagePane
   * @see builder.widgets.Widget
   */
  public void writeModel(ObjectOutputStream out) 
      throws IOException {
//    System.out.println("WM writeModel(): " + getKey());
//    System.out.println("bSendEvents: " + bSendEvents);
    out.writeBoolean(bSendEvents);
    int rows = getRowCount();
//    System.out.println("WM rows: " + rows);
    out.writeInt(rows);
    for (int i=0; i<rows; i++) {
      out.writeObject(data[i][PROP_VAL_ID]);
      out.writeObject(data[i][PROP_VAL_VALUE]);
//     System.out.println(data[i][PROP_VAL_ID] + ": "
//       + data[i][PROP_VAL_VALUE].toString());

    }
  }
  
  /**
   * readModel() will de-serialize our model's data from a string object for backup
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
   * @see builder.widgets.Widget
   */
  public void readModel(ObjectInputStream in, String widgetType) throws IOException, ClassNotFoundException {
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
    for (int i = 0; i < rows; i++) {
      metaID = (String) in.readObject();
      objectData = in.readObject();
      row = mapMetaIDtoProperty(metaID);
      if (row >= 0) {
        data[row][PROP_VAL_VALUE] = objectData;
        
//  System.out.println(data[row][PROP_VAL_NAME].toString() + ": " +
//           data[row][PROP_VAL_VALUE].toString() + " mapped to row " + row);
        
      }
    }
  }
  
  /**
   * Sets the read only properties and any other items 
   * needed at startup.
   * 
   * Called by User Preferences ModelEditor on startup Basically this 
   * replaces a subclassed readModel() since we don't 
   * serialize User Preferences Models for save and restores.
   * It's saved wherever java stores UserPrefences (registry for windows).
   */
  public void setReadOnlyProperties() {
  }
  
  /**
   * mapMetaIDtoProperty.
   *
   * @param metaID
   *          the id assigned that must never change over the life time of the
   *          builder
   * @return row that matches metaID, otherwise a -1 on no matching ID
   */
  public int mapMetaIDtoProperty(String metaID) {
    for (int i=0; i<data.length; i++) {
      if (metaID == null) {
        Builder.logger.error(getType() + " error metaID == null");
        return -1;
      }
      if (data[i][PROP_VAL_ID] == null) {
        Builder.logger.error(getType() + " error data[" + i + ", " +
          PROP_VAL_ID + "] == null");
        return -1;
      }
      if (metaID.equals((String)data[i][PROP_VAL_ID])) {
        return i;
      }
    }
    return -1;
  }
  

  /**
   * <p>
   * calcSizes() - This routine scans our list of items and comes up with
   *               the buffer size required for storage.
   * </p>
   * 
   * @param fireUpdates indicates that we should notify JTable of changes
   */
  public void calcSizes(boolean fireUpdates) {
    
  }
  
  
  /**
   * changeThemeColors
   * @param theme
   */
  public void changeThemeColors(GUIsliceTheme theme) {
    
  }
  
  /**
   * Copy selected properties from another model.
   * Called by the CopyPropsCommand.
   * @param checklistData
   *          the widget model
   */
  public void copyProperties(Object checklistData[][]) {
    String metaID = "";
    int row;
    for (int i=0; i<checklistData.length; i++) {
      metaID = (String)checklistData[i][0];
      row = mapMetaIDtoProperty(metaID);
      if (row >= 0) {
        data[row][PROP_VAL_VALUE] = checklistData[i][1];
      }
    }
    calcSizes(false);
    fireTableDataChanged();
  }

  /**
   * Paste properties from the PasteCommand.
   *
   * @param m
   *          the widget model
   * @param x
   *          the x
   * @param y
   *          the y
   */
  public static void pasteProps(WidgetModel src_m, WidgetModel dest_m, int x, int y) {
    Object oldData[][] = src_m.getData();
    Object data[][] = dest_m.getData();
    // skip over key, enum, x and y position
    for (int i=2; i<src_m.getPropertyCount(); i++) {
      for (int j=0; j<5; j++) {
        if (i == PROP_X && j == PROP_VAL_VALUE) {
          data[i][j] = Integer.valueOf(x);
        } else if (i == PROP_Y && j == PROP_VAL_VALUE) {
          data[i][j] = Integer.valueOf(y);
        } else if (i == PROP_ELEMENTREF && j == PROP_VAL_VALUE) {
          if (src_m.getElementRef() == null || src_m.getElementRef().isEmpty()) {
            data[i][j] = oldData[i][j];
          } else {
            data[i][j] = Utils.createElemName(dest_m.getKey(), src_m.getElementRef());
          }
        } else {
          data[i][j] = oldData[i][j];
        }
      }
    }
    dest_m.calcSizes(false);
    if (src_m instanceof ImageModel) {
      dest_m.setImage(src_m.getImage());
    } else if (src_m instanceof ImgButtonModel) {
      dest_m.setImage(src_m.getImage());
      dest_m.setImageSelected(src_m.getImageSelected());
    }

  }
  
  public void printModel(String name) {
    Builder.logger.debug(name);
    for (int i=2; i<getPropertyCount(); i++) {
      Builder.logger.debug(data[i][PROP_VAL_NAME].toString() + ": " +
        data[i][PROP_VAL_VALUE].toString());
     }
  }

}
