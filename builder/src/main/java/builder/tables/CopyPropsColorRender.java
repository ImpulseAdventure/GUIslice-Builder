package builder.tables;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class CopyPropsColorRender extends JLabel implements TableCellRenderer {
  private static final long serialVersionUID = 1L;
  Border unselectedBorder = null;
  Border selectedBorder = null;
  boolean isBordered = true;

  public CopyPropsColorRender(boolean isBordered) {
    this.isBordered = isBordered;
    setOpaque(true); //MUST do this for background to show up.
  }

  public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus,
      int row, int column) {
    Color newColor = (Color) color;
    setBackground(newColor);
    if (isBordered) {
      if (isSelected) {
        if (selectedBorder == null) {
          selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, 
              Color.LIGHT_GRAY);
        }
        setBorder(selectedBorder);
      } else {
        if (unselectedBorder == null) {
          unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, 
              Color.LIGHT_GRAY);
        }
        setBorder(unselectedBorder);
      }
    }

    setToolTipText("RGB value: " + newColor.getRed() + ", " + newColor.getGreen() + ", " + newColor.getBlue());
    return this;
  }
}
