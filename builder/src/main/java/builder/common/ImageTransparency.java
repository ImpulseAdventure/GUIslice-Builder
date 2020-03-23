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
package builder.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import builder.models.GeneralModel;
import builder.prefs.GeneralEditor;

/**
 * The Class ImageTransparency.
 * 
 * @author Paul Conti
 * 
 */
public class ImageTransparency {

  /**
   * Make color transparent.
   *
   * @param im
   *          the im
   * @param color
   *          the color
   * @return the <code>buffered image</code> object
   */
  public static BufferedImage makeColorTransparent(BufferedImage im) {
    ImageFilter filter = new RGBImageFilter() {

        // the color we are looking for... Alpha bits are set to opaque
        Color color = ((GeneralModel) GeneralEditor.getInstance().getModel()).getTransparencyColor();
        public int markerRGB = color.getRGB() | 0xFF000000;

        public final int filterRGB(int x, int y, int rgb) {
            if ((rgb | 0xFF000000) == markerRGB) {
                // Mark the alpha bits as zero - transparent
                return 0x00FFFFFF & rgb;
            } else {
                // nothing to do
                return rgb;
            }
        }
    };

    ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
    Image image = Toolkit.getDefaultToolkit().createImage(ip);
    return imageToBufferedImage(image);
  }

  /**
   * Image to buffered image.
   *
   * @param image
   *          the image
   * @return the <code>buffered image</code> object
   */
  private static BufferedImage imageToBufferedImage(Image image) {

    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bufferedImage.createGraphics();
    g2.drawImage(image, 0, 0, null);
    g2.dispose();

    return bufferedImage;
  }

}
