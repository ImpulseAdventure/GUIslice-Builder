/**
 *
 * The MIT License
 *
 * Copyright 2020-2022 Paul Conti
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
package builder.fonts;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import builder.Builder;

/**
 * The Class FontLoadGFXFiles.
 * Handles filling in the gaps left by JSON deserialization of font
 * hierarchy inside FontFactory.  This allows users to simply drop new
 * GFX fonts into a folder without editing the JSON file.
 * 
 * Calling Sequence:
 *   String fullPath = CommonUtils.getInstance().getWorkingDir();
 *   String fontsPath = fullPath + "fonts" + System.getProperty("file.separator") 
 *       + "gfx";
 *   Path startingDir = Paths.get(fontsPath);
 *   FontLoadGFXFiles fileVisitor = new FontLoadGFXFiles();
 *   Files.walkFileTree(path, fileVisitor);
 * 
 * @author Paul Conti
 */
public class FontLoadGFXFiles extends SimpleFileVisitor<Path> {
  
  static private Pattern numPattern = Pattern.compile("\\d+");
  
  private FontGraphics p;
  private FontCategory c;
  private String familyName;
  private String logicalStyle;
  
  public FontLoadGFXFiles(FontGraphics p, FontCategory c) {
    this.p = p;
    this.c = c;
    familyName = null;
  }

  // Print information about
  // each type of file.
  @Override
  public FileVisitResult visitFile(Path file,
                                 BasicFileAttributes attr) {
      if (attr.isRegularFile()) {
        Path path = file.getFileName();
        String hdrName = path.toString();
//        Builder.logger.debug("hdrName: " + hdrName);
        int n = hdrName.indexOf(".h");
        if (n == -1) {
          Builder.logger.error("unable to parse font: " + file);
          return CONTINUE;
        }
        FontItem item = new FontItem();
        item.setFamilyName(familyName);
        String displayName = hdrName.substring(0,n);
        item.setDisplayName(displayName);
        // make sure we didn't already read font in from our json file
        if (c.findFontItem(displayName) != null) {
//          Builder.logger.debug("font: " + displayName + " from json file");
          return CONTINUE;
        }
        String fileName = String.format("%s/%s/%s",familyName,logicalStyle,hdrName);
        item.setFileName(fileName);
        item.setIncludeFile(hdrName);
        String fontRef = "&" + displayName;
        item.setFontRef(fontRef);
        // find size of font
        String size = null;
        String testSize =hdrName;
        n = testSize.indexOf("pt7b");
        if (n == -1) {
          n = testSize.indexOf("pt8b");
        }
        if (n != -1) {
           testSize = testSize.substring(0,n);
        }
        Matcher m = numPattern.matcher(testSize);
        while (m.find()) {
          size = m.group(); // we want the last number
        } 
        if (size == null) {
          Builder.logger.error(p.getName() + ": unable to parse font: " + file);
          return CONTINUE;
        }
        item.setLogicalSize(size);
        item.setLogicalStyle(logicalStyle);
        c.addFont(item);
      }
      return CONTINUE;
  }

  // Print each directory visited.
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    Path path = dir.getFileName();
    String dirName = path.toString();
    switch (dirName) {
      case "BOLD":
        logicalStyle = dirName;
        break;
      case "BOLD_ITALIC":
        logicalStyle = dirName;
        break;
      case "ITALIC":
        logicalStyle = dirName;
        break;
      case "PLAIN":
        logicalStyle = dirName;
        break;
      default:
        familyName = dirName;
        break;
    }
//    Builder.logger.debug("Directory: " + dir);
    return CONTINUE;
  }

}
