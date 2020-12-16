package builder.tables;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import builder.fonts.FontFactory;
import builder.fonts.FontItem;
import builder.fonts.FontTFT;
import builder.fonts.FontTtf;

public class TextTFTCellRenderer extends JLabel implements TableCellRenderer {

  private static final long serialVersionUID = 1L;

  private FontTFT myFont;
  private FontFactory ff;
  private boolean bValidFont = false;
  
  public TextTFTCellRenderer() {
    setOpaque(true); //MUST do this for background to show up
  }

  public void setFontTFT(FontFactory ff, FontTFT myFont) {
    this.ff = ff;
        
    if (myFont == null) {
      bValidFont = false;
      return;
    }
    int fontSz = 10;
    if (myFont instanceof FontTtf) {
      fontSz = 18;
    }
    if (myFont.getLogicalSizeAsInt() == fontSz) {
      this.myFont = myFont;
      bValidFont = true;
    } else {
      FontItem item = ff.getFontItem(myFont.getDisplayName());
      if (item != null) {
        FontTFT testFont = ff.getFontbySizeStyle(item.getFamilyName(), 
            fontSz, item.getLogicalStyle());
        if (testFont != null) {
          this.myFont = testFont;
          bValidFont = true;
        } else {
          bValidFont = false;
        }
      }
    }
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int col) {
    if (value != null) {
      String content = value.toString();
      TableColumn col_model = table.getColumnModel().getColumn(col);
      if (bValidFont) {
        int width = col_model.getWidth();
//        int height = table.getTableHeader().getSize().height;
        int height = 30;
        Dimension ppDim = new Dimension(width,height);
        Rectangle r = new Rectangle(ppDim);
        if (content != null && !content.isEmpty()) {
          BufferedImage img = ff.drawTextImage(FontTFT.ALIGN_LEFT, r, content, myFont, getForeground(), getForeground(), 0);
          this.setIcon(new ImageIcon(img));
          setText(null);
        } else {
          BufferedImage img = ff.drawTextImage(FontTFT.ALIGN_LEFT, r, " ", myFont, getForeground(), getForeground(), 0);
          this.setIcon(new ImageIcon(img));
          setText(null);
        }
      } else {
        this.setIcon(null);
        setText(content);
      }
    }
    return this;
  }

}
