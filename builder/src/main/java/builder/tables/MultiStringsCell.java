package builder.tables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import builder.views.FontListDialog;
import builder.views.StringListDialog;

public class MultiStringsCell extends AbstractCellEditor 
  implements TableCellRenderer, TableCellEditor {
  private static final long serialVersionUID = 1L;
  private JPanel panel;
  private JLabel label;
  private String title;
  private String[] lines = null;
  MultipeLineCellListener listener;
  public static enum MCDialogType { STRING_DIALOG, FONT_DIALOG }
  
  JButton b;

  public MultiStringsCell(String t, MCDialogType dt) {
    this.title = t;
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
    // t.setPreferredSize(new Dimension(50,16));
    label = new JLabel();
    label.setOpaque(true);
    label.setHorizontalAlignment(JLabel.LEFT);
    label.setVerticalAlignment(JLabel.CENTER);
    panel.add(label, BorderLayout.CENTER);
    b = new JButton("...");
    if (dt.equals(MCDialogType.STRING_DIALOG)) {
      b.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          lines = StringListDialog.showDialog(
              new JFrame(),
              null,
              title,
              "List",
              lines);
          listener.buttonClicked(lines);
        }
      }); 
    }
    if (dt.equals(MCDialogType.FONT_DIALOG)) {
      b.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          lines = FontListDialog.showDialog(
              new JFrame(),
              null,
              title,
              "List",
              lines);
          listener.buttonClicked(lines);
        }
      }); 
    }
    b.setPreferredSize(new Dimension(16, 16));
    panel.add(b, BorderLayout.EAST);
  }

  public void addButtonListener( MultipeLineCellListener l ) {
    listener = l;
  }
  
  public void setData(String[] strings) {
    lines = strings;
  }

  @Override
  public Object getCellEditorValue() {
    return label.getText();
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    return panel;
  }

  public void addListener(ActionListener l) {
    b.addActionListener(l);
  }
  
  @Override
  public String toString() {
    return lines.toString();
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {
    panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    label.setBackground(Color.WHITE);
    label.setForeground(Color.BLACK);
    String result = "";
    label.setText(result);
    if (lines != null) {
      if (lines.length > 0) {
        if (!lines[0].isEmpty()) {
          result = convertToHtml(value);
          label.setText(result);
        }
      }
    }
    int size = label.getPreferredSize().height + 20;
    table.setRowHeight(row, size);
    return panel;
  }

  /**
   * Converts the value to html string
   *
   * @param value
   *          the value to convert html
   * @return the <code>string</code> object
   */
  protected String convertToHtml(Object value) { 
    if (value != null && value instanceof String[]) { 
      String[] sVal = (String[])value;
      String result = "<html>";
      for (int i =0; i<sVal.length; i++) {
        result = result + htmlEncodeLines(sVal[i]) + "<br>";
      }
      result = result + "</html>";
      return result;
    }
    return "";
  }

  /** 
   * Encode string as HTML. Convert newlines to &lt;br&gt; as well.
   * @param s 
   *          String to encode.
   * @return Encoded string. 
   */
  protected static String htmlEncodeLines(String s) {
    int i = indexOfAny(s, "<>&", 0); // find first char to encode
    if (i < 0)
      return s; // none
    StringBuffer sb = new StringBuffer(s.length() + 20);
    int j = 0;
    do {
      sb.append(s, j, i).append(htmlEncode(s.charAt(i)));
      i = indexOfAny(s, "<>&", j = i + 1);
    } while (i >= 0);
    sb.append(s, j, s.length());
    return sb.toString();
  }

  /**
   * Html encode.
   *
   * @param c
   *          the character to encode
   * @return the <code>string</code> object
   */
  private static String htmlEncode(char c) {
    switch (c) {
      case '<': 
        return "&lt;";
      case '>': 
        return "&gt;";
      case '&': 
        return "&amp;";
      case '\"': 
        return "&quot;";
      case '\'': 
        return "&apos;";
      default: 
        return Character.toString(c);
    } 
  }

  /**
   * Index of any.
   *
   * @param s
   *          the s
   * @param set
   *          the set
   * @param start
   *          the start
   * @return the <code>int</code> object
   */
  private static int indexOfAny(String s, String set, int start) {
    for (int i = start; i < s.length(); ++i) {
      if (set.indexOf(s.charAt(i)) >= 0)
        return i;
    }
    return -1;
  }

}
