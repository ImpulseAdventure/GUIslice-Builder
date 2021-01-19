/**
 *
 * The MIT License
 *
 * Copyright 2018, 2021 Paul Conti
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
import builder.fonts.FontVLW;

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
    if (myFont instanceof FontTtf || myFont instanceof FontVLW) {
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
