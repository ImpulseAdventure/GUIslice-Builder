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
package builder.common;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import builder.Builder;
import builder.prefs.GeneralEditor;
import builder.prefs.GridEditor;

import java.util.Base64;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonUtils is a catch all for useful routines that don't seem to fit
 * in any other classes.
 * 
 * @author Paul Conti
 * 
 */
public class CommonUtils {
  
  /** The instance. */
  private static CommonUtils instance = null;
  
  /** Backup Folder Name. */
  private static final String BACKUP_FOLDER = "gui_backup";
  
  /**
   * getInstance() - get our Singleton Object.
   *
   * @return instance
   */
  public static synchronized CommonUtils getInstance() {
    if (instance == null) {
      instance = new CommonUtils();
    }
    return instance;
  }

  /**
   * empty constructor.
   */
  public CommonUtils() {
  }

  /**
   * fitToGrid() - Remaps coordinates to fit on our design canvas and stay inside
   * the margins. Doesn't handle widgets if they are too large to fit.
   *
   * @param x
   *          the x
   * @param y
   *          the y
   * @param widgetWidth
   *          the widget width
   * @param widgetHeight
   *          the widget height
   * @return p
   */
  public Point fitToGrid(int x, int y, int widgetWidth, int widgetHeight) {
    GeneralEditor ed = GeneralEditor.getInstance();
    int margins = ed.getMargins();
    int canvas_width = ed.getWidth() - margins;
    int canvas_height = ed.getHeight() - margins;
    // force the new Coordinates fit on our canvas and inside the margins
    if ((x + widgetWidth) > canvas_width)
      x = canvas_width - widgetWidth;
    if (x < margins)
      x = margins;
    if ((y + widgetHeight) > canvas_height)
      y = canvas_height - widgetHeight;
    if (y < margins)
      y = margins;
    Point p = new Point(x, y);
    return p;
  }
  
  /**
   * snapToGrid() - Remaps coordinates to our nearest grid line.
   *
   * @param x
   *          the x
   * @param y
   *          the y
   * @return p
   */
  public Point snapToGrid(int x, int y) {
    GridEditor ed = GridEditor.getInstance();
    // check for snap to grid
    if (ed.getGridSnapTo()) {
      x = (x / ed.getGridMinorWidth()) * ed.getGridMinorWidth();
      y = (y / ed.getGridMinorHeight()) * ed.getGridMinorHeight();
    }
    Point p = new Point(x, y);
    return p;
  }
  
  /**
  * createElemName - Create Element Reference Name 
  * strips "$" off of key to find number and adds the
  * number to the element reference name.
  *
  * @param key
  *          the key
  * @param refName
  *          the element reference name
  * @return the <code>String</code> with the new element ref name
  */
  static public String createElemName(String key, String refName) {
    // first test to see if we have a ElementRef name
    if (refName == null || refName.isEmpty())
      return new String("");
    // We have one, now strip off the number from the key
    String sCount = "";
    int n = key.indexOf("$");
    sCount = key.substring(n+1);
    /* now check the refName and determine if it has a number
     * at the end. When we create elementrefs 
     * we use the key number at the end but users
     * can and will add numbers themselves.
     * So if we find a number we will add "_keycount"
     * instead of simply "keycount".
     * This will only happen during a paste operation.
     */
    int i = refName.length()-1;
    if(Character.isDigit(refName.charAt(i))) {
      return new String(refName + "_" + sCount);
    }
    return new String(refName + sCount);
  }
  
  /**
   * encodeToString() - .
   *
   * @param image
   *          the image
   * @return imageString
   */
  public String encodeToString(BufferedImage image) {
    String imageString = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      ImageIO.write(image, "bmp", bos);
      byte[] imageBytes = bos.toByteArray();

      imageString = Base64.getEncoder().encodeToString(imageBytes);

      bos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imageString;
  }

  /**
   * decodeToImage() - .
   *
   * @param imageString
   *          the image string
   * @return image
   */
  public BufferedImage decodeToImage(String imageString) {

    BufferedImage image = null;
    byte[] imageByte;
    try {
      imageByte = Base64.getDecoder().decode(imageString);
      ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
      image = ImageIO.read(bis);
      bis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }

  /**
   * Gets the resizable icon.
   *
   * @param resource
   *          the resource
   * @return the resizable icon
   */
  public ResizableIcon getResizableIcon(String resource) {

    return ImageWrapperResizableIcon.getIcon(Builder.class.getClassLoader().getResource(resource),
        new Dimension(48, 48));
  }

  /**
   * getWorkingDir - attempts to find the directory where our executable is
   * running.
   *
   * @return workingDir - our working directory
   */
  public String getWorkingDir() {
    // The code checking for "lib" is to take care of the case 
    // where we are running not inside eclipse IDE
    String workingDir;
    String strUserDir = System.getProperty("user.dir");
    int n = strUserDir.indexOf("lib");
    if (n > 0) {
      strUserDir = strUserDir.substring(0,n-1);  // remove "/bin"
    }
    workingDir = strUserDir + System.getProperty("file.separator"); 
    return workingDir;
  }
  
  /**
   * Backup file.
   *
   * @param file
   *          the file
   */
  public void backupFile(File file)
  {
    String newTemplate = null;
    String backupName = null;
    File backupFile = null;
    File newFile = null;
    if(file.exists()) {
      // first check to see if we have a backup folder
      String strBackupDir = file.getParent() + System.getProperty("file.separator") 
          + BACKUP_FOLDER;
      File backupDir = new File(strBackupDir);
      if (!backupDir.exists()) {
        backupDir.mkdir();
      }
      // Make a backup copy of file and overwrite backup file if it exists.
      backupName = new String(file.getAbsolutePath() + ".bak");
      backupFile = new File(backupName);
      if (backupFile.exists()) {
        // rename previous backup files so we don't lose them
        newTemplate = new String(strBackupDir +
            System.getProperty("file.separator") +
            file.getName() +
            ".##");
        int idx = 0;
        String newName = newTemplate + String.valueOf(++idx);
        newFile = new File(newName);
        while (newFile.exists()) {
          newName = newTemplate + String.valueOf(++idx);
          newFile = new File(newName);
        }
        backupFile.renameTo(newFile);
      }
      copyFile(file, backupFile);
    }
    
  }

  /**
   * Copy file.
   *
   * @param inFile
   *          the in file
   * @param outFile
   *          the out file
   */
  public void copyFile(File inFile, File outFile)
  { 
    InputStream inStream = null;
    OutputStream outStream = null;
    try{
      inStream = new FileInputStream(inFile);
      outStream = new FileOutputStream(outFile);
      byte[] buffer = new byte[1024];
      int length;
      //copy the file content in bytes 
      while ((length = inStream.read(buffer)) > 0){
        outStream.write(buffer, 0, length);
      }
      inStream.close();
      outStream.close();
//      System.out.println("File is copied successfully!");
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  
}
