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
package builder.widgets;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import builder.common.Utils;
import builder.common.ImageTransparency;
import builder.models.ImgButtonModel;

/**
 * The Class ImgButtonWidget simulates GUIslice API gslc_ElemCreateBtnImg() call.
 * 
 * @author Paul Conti
 * 
 */
public class ImgButtonWidget extends Widget {

  private ImgButtonModel m;
  
  /**
   * Instantiates a new img button widget.
   */
  public ImgButtonWidget() {
    u = Utils.getInstance();
    m = new ImgButtonModel();
    model = m;
  }

  /**
   * Sets the image.
   *
   * @param file
   *          the file containing image to use when button is not selected.
   * @param x
   *          the x coordinate position
   * @param y
   *          the y coordinate position
   */
  public boolean setImage(File file, int x, int y) {
    boolean bResult =  m.setImage(file, x, y);
    super.setXY(model, x, y);
    return bResult;
  }
  
  /**
   * Sets the image selected.
   *
   * @param file
   *          the file containing image to use when button is selected.
   */
  public boolean setImageSelected(File file) {
    return m.setImageSelected(file);
  }
  
  /**
   * draw
   *
   * @see builder.widgets.Widget#draw(java.awt.Graphics2D)
   */
  public void draw(Graphics2D g2d) {
    Rectangle b = getWinBounded();
    if (bSelected) {
      if (m.isTransparent()) {
        BufferedImage image = ImageTransparency.makeColorTransparent(m.getImageSelected());
        g2d.drawImage(image, b.x, b.y, null);
      } else {
        g2d.drawImage(m.getImageSelected(), b.x, b.y, null);
      }
    } else {
      if (m.isTransparent()) {
        BufferedImage image = ImageTransparency.makeColorTransparent(m.getImage());
        g2d.drawImage(image, b.x, b.y, null);
      } else {
        g2d.drawImage(m.getImage(), b.x, b.y, null);
      }
    }
    if (m.isFrameEnabled()) {
      g2d.setColor(m.getFrameColor());
      g2d.drawRect(b.x, b.y, b.width, b.height);
    }
    super.drawSelRect(g2d, b);
  }

}
