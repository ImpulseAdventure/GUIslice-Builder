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
package builder.views;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import javax.swing.filechooser.FileView;

import builder.Builder;

/**
 * FileViewWithIcons
 * @author Paul Conti
 *
 */
public class FileViewWithIcons extends FileView
{
   private Icon bmp_icon, jpg_icon, c_icon, prj_icon;

   /**
    * Create FileViewWithIcons for a JFileChooser so it can display
    * file type (formats) icons for supported files.
    */
   FileViewWithIcons() {
     prj_icon = new ImageIcon(Builder.class.getResource("/resources/icons/file/PRJ.png"));
     bmp_icon = new ImageIcon(Builder.class.getResource("/resources/icons/file/BMP.png"));
     jpg_icon = new ImageIcon(Builder.class.getResource("/resources/icons/file/JPG.png"));
     c_icon = new ImageIcon(Builder.class.getResource("/resources/icons/file/C.png"));
   }

   /**
    * getTypeDescription
    * @param f
    *   The file
    * @see javax.swing.filechooser.FileView#getTypeDescription(java.io.File)
    */
   @Override
   public String getTypeDescription(File f) {
     // Let the look and feel figure out the type description.
     return null;
   }

   /**
    * getIcon
    * Return the icon that associates with the file's type. 
    * @param f
    *   The file
    * @see javax.swing.filechooser.FileView#getIcon(java.io.File)
    */
   @Override
   public Icon getIcon(File f) {
      String s = f.getName();
      int i = s.lastIndexOf('.');

      if (i > 0 && i < s.length()-1) {
        String ext = s.substring(i + 1).toLowerCase();
        if (ext.equals("prj")) {
          return prj_icon;
        } else if (ext.equals("bmp")) {
          return bmp_icon;
        } else if (ext.equals("c")) {
          return c_icon;
        } else if (ext.equals("jpeg") || ext.equals("jpg")) {
          return jpg_icon;
        }
      }
      return null;
   }

   /**
    * getName
    * Return the file's name minus its extension for files with icons.
    * @param f
    *   The file
    * @see javax.swing.filechooser.FileView#getName(java.io.File)
    */
   // jpeg, jpg, or png extensions.
   @Override
   public String getName(File f) {         
      String s = f.getName();
      int i = s.lastIndexOf('.');
      if (i > 0 && i < s.length()-1) {
         String ext = s.substring(i+1).toLowerCase();
         if (ext.equals("prj")  || 
             ext.equals("bmp")  ||
             ext.equals("c")    || 
             ext.equals("jpeg") || 
             ext.equals("jpg"))   {
           return s.substring(0, i);
         }
      }
      return null;
   }

   /**
    * getDescription
    * Return an individual file's description.
    * @param f
    *   The file
    * @see javax.swing.filechooser.FileView#getDescription(java.io.File)
    */
   @Override
   public String getDescription(File f) {
      // Let the look and feel figure out the description.
      return null;
   }

   /**
    * isTraversable
    * Determine if a directory is traversable.
    * @param f
    *   The file
    * @see javax.swing.filechooser.FileView#isTraversable(java.io.File)
    */
   @Override
   public Boolean isTraversable(File f) {
      // Let the look and feel determine if the directory is traversable.
      return null;
   }
}
