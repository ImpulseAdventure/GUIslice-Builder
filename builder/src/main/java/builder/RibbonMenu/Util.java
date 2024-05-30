/**
 * Copyright 2020-2022 Csekme Krisztián
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package builder.RibbonMenu;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * The Class Util.
 */
public class Util {

	/**
   * Get image from resource/image folder via filename.
   *
   * @param filename
   *          desired file path to image
   * @param width
   *          specify width
   * @param height
   *          specify height
   * @return desired image
   */
    public static ImageIcon accessImageFile(String filename, int width, int height) {
    	ImageIcon i = new ImageIcon(accessImageFile(filename).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    	return i;
    }

	/**
   * Get image from resource/image folder via filename.
   *
   * @param filename
   *          desired file path to image
   * @return desired image
   */
	public static ImageIcon accessImageFile(String filename) {
		InputStream in = accessStream(filename);
		ImageIcon imageIcon = null;
		if (in == null) {
			if (!filename.startsWith("/")) {
				filename = "/" + filename;  // for some reason sometimes we need to start with a slash
				in = accessStream(filename);
			}
		}
		BufferedImage im = null;
		try {
			im = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		imageIcon = new ImageIcon(im);
		return imageIcon;
	}

	/**
   * Get input stream from resource folder.
   *
   * @param filename
   *          name of the desired resource
   * @return desired file stream
   */
	public static InputStream accessStream(String filename) {
        // this is the path within the jar file
        InputStream input = Util.class.getResourceAsStream(filename);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = Util.class.getClassLoader().getResourceAsStream(filename);
        }
        return input;
    }

	/**
   * Get Font from file.
   *
   * @param fontPath
   *          path of the desired font
   * @param size
   *          size of the font
   * @return desired Font instance
   */
	public static Font loadFont(String fontPath, float size) {
		InputStream is = Util.class.getResourceAsStream(fontPath);
		try {
			 Font f = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
			 return f;
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
