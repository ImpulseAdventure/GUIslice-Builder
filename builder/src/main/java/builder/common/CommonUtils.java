/**
 *
 * The MIT License
 *
 * Copyright 2018-2022 Paul Conti
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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import builder.Builder;
import builder.codegen.PlatformIO;
import builder.controller.Controller;
import builder.fonts.FontFactory;
import builder.prefs.GeneralEditor;
import builder.prefs.GridEditor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
    int canvas_width = Controller.getProjectModel().getWidth() - margins;
    int canvas_height = Controller.getProjectModel().getHeight() - margins;
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
      if (ed.getGridMinorWidth() > 0 && ed.getGridMinorHeight() > 0) {
        x = (x / ed.getGridMinorWidth()) * ed.getGridMinorWidth();
        y = (y / ed.getGridMinorHeight()) * ed.getGridMinorHeight();
      } else if (ed.getGridMajorWidth() > 0 && ed.getGridMajorHeight() > 0) {
        x = (x / ed.getGridMajorWidth()) * ed.getGridMajorWidth();
        y = (y / ed.getGridMajorHeight()) * ed.getGridMajorHeight();
      }

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
    if (image == null) 
      return "";
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      ImageIO.write(image, "png", bos);
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
   * getWorkingDir - attempts to find the directory where our executable is
   * running.
   *
   * @return workingDir - our working directory
   */
  static public String getWorkingDir() {
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
   * Backup projects *.prj, programs *.ino or main.cpp
   * and header *_GSLC.h files.  
   * 
   * The exact paths will be determined by the current
   * IDE in use, Either Arduino IDE or PlatformIO with
   * another IDE like VSCODE.
   * 
   * @param file
   *          the file
   */
  static public void backupFile(File file)
  {
    int n;
    String strFSep = System.getProperty("file.separator"); 
    // start with backup folder itself
    String strParent = file.getParent();
    if (strParent.endsWith("src")) {
      n = strParent.lastIndexOf("src");
      strParent = strParent.substring(0,n-1);
    }
    if (strParent.endsWith("include")) {
      n = strParent.lastIndexOf("include");
      strParent = strParent.substring(0,n-1);
    }
    String strBackupDir = strParent + strFSep + BACKUP_FOLDER;
    File backupDir = new File(strBackupDir);
    if (!backupDir.exists()) {
      backupDir.mkdir();
    }
    
    String FName = file.getName();
    String newTemplate = new String(strBackupDir + strFSep + FName + ".##");
    File newFile = null;
    /* search for a backup name that doesn't yet exist
     * if we go over a 100 just overwrite last version
     */
    for (int idx=0; idx<101; idx++) {
      String newName = newTemplate + String.valueOf(idx);
      newFile = new File(newName);
      if (!newFile.exists())
        break;
    }
    copyFile(file, newFile);
  }

  /**
   * Copy file.
   *
   * @param inFile
   *          the in file
   * @param outFile
   *          the out file
   */
  static public void copyFile(File inFile, File outFile)
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
  
  public static void copyDirectory(String src, String dest, List<String>filterList) 
      throws IOException {
        Files.walk(Paths.get(src))
          .forEach(a -> {
              Path b = Paths.get(dest, a.toString().substring(src.length()));
              try {
                boolean bMatch = false;
                String name = b.toString();
                if (filterList != null) {
                  for(String include : filterList) {
                    if (name.endsWith(include))
                      bMatch=true;
                  }
                } else {
                  bMatch=true;
                }
                if (bMatch &&
                    !name.contains("gui_backup") &&
                    !name.startsWith(".") &&
                    !name.endsWith(".prj") &&
                    !name.endsWith(".bak")) {
                  Files.copy(a, b,new CopyOption[]{StandardCopyOption.REPLACE_EXISTING,
                      StandardCopyOption.COPY_ATTRIBUTES});
                }
              } catch (IOException e) {
                  e.printStackTrace();
              }
          });
  }
  
  public static boolean isPlatformIO_INI_Present(String folder) {
    boolean bResult = false;
    String m_sFileSep = System.getProperty("file.separator");
    File iniFile = new File(folder + m_sFileSep + PlatformIO.PLATFORMIO_INI);
    if (iniFile.exists()) {
      bResult = true;
    }
    return bResult;
  }
  
  public static void cleanFolderOfFontHeaders(String folder) {
      List<File> fileList = new ArrayList<>();

      try (DirectoryStream<Path> stream = Files
        .newDirectoryStream(Paths.get(folder))) {
        for (Path path : stream) {
          if (!Files.isDirectory(path)) {
            fileList.add(path.toFile());
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
      String fileName = null;
      for (File f : fileList) {
        fileName = f.getName();
        Builder.logger.debug("found: "+f.toString());
        if (FontFactory.getFontCleanupMap().containsKey(fileName)) {
          Builder.logger.debug("deleted font header: "+f.toString());
          f.delete();
        }
      }
  }
  
  public static void fileReplaceStr(File oldFile, File newFile,String stringToReplace, String replaceWith) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(oldFile), "UTF8"));
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));
      String line;

      while ((line = br.readLine()) != null) {
        if (line.contains(stringToReplace))
          line = line.replace(stringToReplace, replaceWith);
        bw.write(line);
        bw.newLine();
      }
      br.close();
      bw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static List<String> fileFindStringValues(File file, String stringToFind, String stringTerminal) {
    List<String> list = new ArrayList<String>();
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
      String line;
      int n;
      String strValue;
      while ((line = br.readLine()) != null) {
        if (line.contains(stringToFind)) {
          n = line.indexOf(stringToFind);
          n += stringToFind.length();
          strValue = new String(line.substring(n));
          n = strValue.indexOf(stringTerminal);
          if (n > 0) {
            strValue = strValue.substring(0,n);
            list.add(strValue);
          }
        }
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

}
