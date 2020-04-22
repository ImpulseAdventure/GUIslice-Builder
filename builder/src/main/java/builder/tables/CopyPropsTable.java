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
package builder.tables;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * The CopyPropsTable provides some extensions to the default JTable
 *
 * <p>
 * Supports a table where the table model has the class information
 * available by a calls to model.getClassAt(row#) and model.getEditorAt(row#). 
 * Allowing each cell in a column to contain different classes.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class CopyPropsTable extends JTable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The model. */
  private CopyPropsTableModel model;
  
  /** The renderer class. */
  private Class<?> rendererClass;
  
 /**
   * Constructs a default <code>CopyPropsTable</code> that is initialized with a default
   * data model, a default column model, and a default selection
   * model.
   */
  public CopyPropsTable() {
      this(null, null, null);
  }

  /**
   * Constructs a <code>CopyPropsTable</code> that is initialized with
   * <code>model</code> as the data model, a default column model,
   * and a default selection model.
   *
   * @param model          the data model for the table
   */
  public CopyPropsTable(TableModel model) {
     this(model, null, null);
     this.model = (CopyPropsTableModel) model;
  }

  /**
   * Constructs a <code>CopyPropsTable</code> that is initialized with
   * <code>model</code> as the data model, 
   * <code>column_model</code>
   * as the column model, and a default selection model.
   *
   * @param model           the data model for the table
   * @param column_model    the column model for the table
   */
  public CopyPropsTable(TableModel model, TableColumnModel column_model) {
      this(model, column_model, null);
      this.model = (CopyPropsTableModel) model;
  }

  /**
   * Constructs a <code>MyTable</code> that is initialized with <code>model</code>
   * as the data model, <code>column_model</code> as the column model, and
   * <code>list_model</code> as the list selection model. If any of the parameters
   * are <code>null</code> this method will initialize the table with the
   * corresponding default model.
   *
   * @param model
   *          the data model for the table
   * @param column_model
   *          the column model for the table
   * @param list_model
   *          the list model
   */
  public CopyPropsTable(TableModel model, TableColumnModel column_model, ListSelectionModel list_model) {
      super(model, column_model, list_model);
      this.model = (CopyPropsTableModel) model;
  }

  /**
   * getCellRenderer
   *
   * @see javax.swing.JTable#getCellRenderer(int, int)
   */
  @Override
  public TableCellRenderer getCellRenderer(int row, int column) {
    int modelColumn = convertColumnIndexToModel(column);
    if (modelColumn == 2) {
      rendererClass = model.getClassAt(row);
      if (rendererClass != null)
        return getDefaultRenderer(rendererClass);
    } else if (modelColumn == 0 && row > 1) {
      return getDefaultRenderer(Boolean.class);
    }
    return super.getCellRenderer(row, column);
  }

  /**
   * getCellEditor
   *
   * @see javax.swing.JTable#getCellEditor(int, int)
   */
  @Override
  public TableCellEditor getCellEditor(int row, int column) {
    int modelColumn = convertColumnIndexToModel(column);
    if (modelColumn == 0 && row > 1) {
      return getDefaultEditor(Boolean.class);
    }
    return super.getCellEditor(row, column);
  }

}
