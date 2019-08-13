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
package builder.views;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import builder.clipboard.ClipboardKeyAdapter;
import builder.models.WidgetModel;
import builder.tables.ColorCellEditor;
import builder.tables.ColorCellRenderer;
import builder.tables.FontCellEditor;
import builder.tables.MultiClassTable;
import builder.tables.NonEditableCellRenderer;
import builder.tables.SelectAllCellEditor;

/**
 * The Class PropEditor handles the properties for one widget instance.
 * 
 * @author Paul Conti
 * 
 */
public class PropEditor {
  
  /** The scroll pane. */
  private  JScrollPane scrollPane;
  
  /** The table. */
  private  MultiClassTable table;
  
  /** The key. */
  private  String key;
  
  /** The model. */
  private  WidgetModel model;

  /**
   * Instantiates a new prop editor.
   *
   * @param m
   *          the model
   */
  public PropEditor(WidgetModel m) {
    model = m;
    table = new MultiClassTable(model);

    table.setPreferredScrollableViewportSize(table.getPreferredSize());

    // Set up renderer and editor for our Color cells.
    table.setDefaultRenderer(Color.class, new ColorCellRenderer(true));
    table.setDefaultEditor(Color.class, new ColorCellEditor());

    // Set up our gray out non editable cells renderer.
    table.setDefaultRenderer(String.class, new NonEditableCellRenderer());
    DefaultTableCellRenderer integerNonEditableCellRenderer = new NonEditableCellRenderer();
    integerNonEditableCellRenderer.setHorizontalAlignment( SwingConstants.LEFT );
    table.setDefaultRenderer(Integer.class, integerNonEditableCellRenderer);
    DefaultTableCellRenderer checkBoxNonEditableCellRenderer = new NonEditableCellRenderer();
    checkBoxNonEditableCellRenderer.setHorizontalAlignment( SwingConstants.LEFT );
    table.setDefaultRenderer(Boolean.class, checkBoxNonEditableCellRenderer);

    // Setup editor for our Font cells.
    table.setDefaultEditor(JTextField.class, new FontCellEditor());

    // Setup editor for Integer cells.
    table.setDefaultEditor(Integer.class, new SelectAllCellEditor());

    // set our preferred column widths
    table.getColumnModel().getColumn(0).setPreferredWidth(130);
    table.getColumnModel().getColumn(1).setPreferredWidth(175);
//    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    // restrict selections to one cell of our table
//    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(true);

    // Force JTable to “commit” data to model while it is still in editing mode
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    
    // disable user column dragging
    table.getTableHeader().setReorderingAllowed(false);
    
    // set keyboard listener for copy and paste
    table.addKeyListener(new ClipboardKeyAdapter(table));
/*
    scrollPane = new JScrollPane(table,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
*/
    scrollPane = new JScrollPane(table,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(1200, 650));
  }
  
  /**
   * Gets the key.
   *
   * @return the key
   */
  public String getKey() {
    return key;
  }
  
  /**
   * Sets the key.
   *
   * @param key
   *          the new key
   */
  public void setKey(String key) {
    this.key = key;
  }
  
  /**
   * Gets the prop panel.
   *
   * @return the prop panel
   */
  public JScrollPane getPropPanel()
  {
    return scrollPane;
  }
  
}
