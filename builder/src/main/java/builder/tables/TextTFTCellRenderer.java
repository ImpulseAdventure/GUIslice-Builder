/**
 *
 * The MIT License
 *
 * Copyright 2018, 2022 Paul Conti
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import builder.fonts.FontFactory;
import builder.fonts.FontItem;
import builder.fonts.FontTFT;
import builder.fonts.FontTtf;
import builder.fonts.FontVLW;
import builder.models.WidgetModel;

public class TextTFTCellRenderer extends JLabel implements TableCellRenderer {

  private static final long serialVersionUID = 1L;

  private FontTFT myFont = null;
  private FontFactory ff;
  private String myFontName = "";
  private boolean bValidFont = false;
  
  public TextTFTCellRenderer() {
    setOpaque(true); //MUST do this for background to show up
  }

  public void setFontTFT(FontFactory ff, FontTFT myFont) {
    this.ff = ff;
    updateFont(myFont);
  }
  
  private void updateFont(FontTFT myFont) {
    
    if (myFont == null) {
      bValidFont = false;
      return;
    }
    int fontSz = 12;
    int fontInc = 2;
    if (myFont instanceof FontTtf || myFont instanceof FontVLW) {
      fontSz = 20;
      fontInc = 4;
    }
    if (myFont.getLogicalSizeAsInt()+fontInc <= fontSz) {
      this.myFont = myFont;
      this.myFontName = myFont.getDisplayName();
      bValidFont = true;
    } else {
      FontItem item = ff.getFontItem(myFont.getDisplayName());
      if (item != null) {
        FontTFT testFont = ff.getFontbySizeStyle(item.getFamilyName(), 
            fontSz, item.getLogicalStyle());
        if (testFont != null) {
          this.myFont = testFont;
          this.myFontName = myFont.getDisplayName();
          bValidFont = true;
        } else {
          bValidFont = false;
        }
      }
    }
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
      boolean hasFocus, int row, int col) {
    Color fg = Color.BLACK;
    Color bg = Color.WHITE;
    TableColumnModel col_Model = table.getColumnModel();
    int w = col_Model.getColumn(col).getWidth();
    int h = table.getRowHeight(row);
    Dimension ppDim = new Dimension(w,h);
    Rectangle r = new Rectangle(ppDim);
    String content = "";
    if (value == null) {
      super.setBackground(Color.WHITE);
      setText(null);
      return this;
    }
    content = value.toString();
    WidgetModel model = (WidgetModel)table.getModel();
    String fontName = model.getFontDisplayName();
    /* we must always check our model for font
     * because we are not always notified of changes
     */
    if (myFont == null || !myFontName.equals(fontName)) {
      FontTFT font = ff.getFont(fontName);
      updateFont(font);
    }
    if (bValidFont &&
        content != null &&
        !content.isEmpty() ) {
        BufferedImage img = ff.drawTextImage(r, content, myFont, fg, bg, 5);
        this.setIcon(new ImageIcon(img));
        setText(null);
    } else {
      // here just use the java dialog font
      super.setBackground(Color.WHITE);
      setText(content);
    }
    return this;
  }

}
