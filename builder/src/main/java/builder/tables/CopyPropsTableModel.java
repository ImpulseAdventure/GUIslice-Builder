package builder.tables;

import java.awt.Color;
import javax.swing.table.AbstractTableModel;

public class CopyPropsTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  static public final int IN_PROP_VAL_ID = 1;
  static public final int IN_PROP_VAL_NAME = 3;
  static public final int IN_PROP_VAL_VALUE = 4;

  static public final int MODEL_PROP_VAL_COPY = 0;
  static public final int MODEL_PROP_VAL_NAME = 1;
  static public final int MODEL_PROP_VAL_VALUE = 2;
  static public final int MODEL_PROP_VAL_ID = 3;

  static final public String[] COLUMN_NAMES = {
      "Copy?",
      "Property Name",
      "Property Value"
  };

  private Object[][] commonData = {
      { "", "Key", "Text$1", "COM-001" },
      { "", "ENUM", "E_ELEM_TEXT1", "COM-002" },
      { Boolean.valueOf(false), "Width", Integer.valueOf(80), "COM-005" },
      { Boolean.valueOf(false), "Height", Integer.valueOf(40), "COM-006" },
      { Boolean.valueOf(false), "Text is a Variable?", Boolean.valueOf(false), "COM-021" },
      { Boolean.valueOf(false), "Corners Rounded?", Boolean.valueOf(true), "COM-012" },
      { Boolean.valueOf(false), "Frame Enabled?", Boolean.valueOf(true), "COM-010" },
      { Boolean.valueOf(false), "Fill Enabled?", Boolean.valueOf(true), "COM-011" },
      { Boolean.valueOf(false), "Use Flash API?", Boolean.valueOf(true), "COM-020" },
      { Boolean.valueOf(false), "Text Color", Color.yellow, "COL-301" },
      { Boolean.valueOf(false), "Frame Color", Color.gray, "COL-302" },
      { Boolean.valueOf(false), "Fill Color", Color.black, "COL-303" },
      { Boolean.valueOf(false), "Selected Color", Color.black, "COL-304" },
      { Boolean.valueOf(false), "Font", "NotoSans10pt7b", "TXT-200" },
      { Boolean.valueOf(false), "Text Alignment", "GSLC_ALIGN_MID_RIGHT", "TXT-213" }
  };

  private Object data[][];

  public CopyPropsTableModel(Object inputData[][]) {
    copyProperties(inputData);
  }

  @Override
  public int getRowCount() {
    return data.length;
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public String getColumnName(int col) {
    return COLUMN_NAMES[col];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return data[rowIndex][columnIndex];
  }

  /**
   * Gets the class at.
   *
   * @param rowIndex
   *                 the row index
   * @return the class at
   */
  public Class<?> getClassAt(int rowIndex) {
    if (getValueAt(rowIndex, 2) instanceof Color)
      return Color.class;
    return null;
  }

  public boolean isCellEditable(int row, int col) {
    // Note that the data/cell address is constant,
    // no matter where the cell appears onscreen.
    if (col == 0) {
      return true;
    }
    return false;
  }

  public void setValueAt(Object value, int row, int col) {
    /*
     * if (DEBUG) {
     * System.out.println("Setting value at " + row + "," + col
     * + " to " + value
     * + " (an instance of "
     * + value.getClass() + ")");
     * }
     */
    if (col == 0) {
      if (value instanceof String) {
        value = Boolean.valueOf((String) value);
      }
    }
    data[row][col] = value;
    fireTableCellUpdated(row, col);

  }

  /**
   * mapMetaIDtoProperty.
   *
   * @param metaID
   *               the id assigned that must never change over the life time of
   *               the
   *               builder
   * @return row that matches metaID, otherwise a -1 on no matching ID
   */
  public int mapMetaIDtoProperty(String metaID) {
    for (int i = 0; i < commonData.length; i++) {
      if (metaID.equals((String) commonData[i][MODEL_PROP_VAL_ID])) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Copy common properties.
   *
   * @param oldData
   *                the widget model
   */
  public void copyProperties(Object oldData[][]) {
    String metaID = "";
    int row;
    /*
     * make two passes over the data
     * the first is just to get a count so we can dimension data[][]
     * the second pass will copy common properties
     */
    int count = 0;
    for (int i = 0; i < oldData.length; i++) {
      metaID = (String) oldData[i][IN_PROP_VAL_ID];
      row = mapMetaIDtoProperty(metaID);
      if (row >= 0) {
        count++;
      }
    }
    data = new Object[count][4];
    int j = 0;
    for (int i = 0; i < oldData.length; i++) {
      metaID = (String) oldData[i][IN_PROP_VAL_ID];
      row = mapMetaIDtoProperty(metaID);
      if (row >= 0) {
        data[j][MODEL_PROP_VAL_COPY] = commonData[row][MODEL_PROP_VAL_COPY];
        data[j][MODEL_PROP_VAL_NAME] = commonData[row][MODEL_PROP_VAL_NAME];
        data[j][MODEL_PROP_VAL_VALUE] = oldData[i][IN_PROP_VAL_VALUE];
        data[j][MODEL_PROP_VAL_ID] = commonData[row][MODEL_PROP_VAL_ID];
        j++;
      }
    }
  }

  /**
   * get checklist of properties selected by user.
   *
   * @param oldData
   *                the widget model
   */
  public Object[][] getCheckList() {
    int count = 0;
    /*
     * Again we make two passes over the data
     * the first is just to get a count so we can dimension retData[][]
     * the second pass will copy checked properties
     */
    for (int i = 2; i < data.length; i++) {
      if (((Boolean) data[i][MODEL_PROP_VAL_COPY]).booleanValue()) {
        count++;
      }
    }
    // we only pass back metaIds and property values
    if (count == 0)
      return null;
    Object[][] retData = new Object[count][2];
    int j = 0;
    for (int i = 2; i < data.length; i++) {
      if (((Boolean) data[i][MODEL_PROP_VAL_COPY]).booleanValue()) {
        retData[j][0] = data[i][MODEL_PROP_VAL_ID];
        retData[j][1] = data[i][MODEL_PROP_VAL_VALUE];
        j++;
      }
    }
    return retData;
  }
}
