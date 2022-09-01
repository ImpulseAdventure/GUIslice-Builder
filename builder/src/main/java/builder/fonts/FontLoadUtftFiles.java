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
package builder.fonts;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import builder.Builder;
import builder.common.Utils;
import builder.parser.ParserException;
import builder.parser.Token;
import builder.parser.Tokenizer;
import builder.parser.TokenizerException;

/**
 * The Class FontLoadGFXFiles.
 * Handles filling in the gaps left by JSON deserialization of font
 * hierarchy inside FontFactory.  This allows users to simply drop new
 * UTFT fonts into a folder without editing the JSON file.
 * 
 * Calling Sequence:
 *   String fullPath = CommonUtils.getInstance().getWorkingDir();
 *   String fontsPath = fullPath + "fonts" + System.getProperty("file.separator") 
 *       + "utft";
 *   Path startingDir = Paths.get(fontsPath);
 *   FontLoadUtftFiles fileVisitor = new FontLoadUtftFiles();
 *   Files.walkFileTree(path, fileVisitor);
 * 
 * @author Paul Conti
 */
public class FontLoadUtftFiles extends SimpleFileVisitor<Path> {
  
  /** Tokens for parsing */
  public static final int INTEGER                 = 1;
  public static final int HEX                     = 2;
  public static final int OCTAL                   = 3;
  public static final int WORD                    = 4;
  public static final int OPEN_SQBRACKET          = 5;
  public static final int CLOSE_SQBRACKET         = 6;
  public static final int OPEN_BRACE              = 7;
  public static final int CLOSE_BRACE             = 8;
  public static final int COMMA                   = 9;
  public static final int SEMICOLON               = 10;
  public static final int SPECIALCHAR             = 99;

  /** The Tokenizer. */
  protected static Tokenizer tokenizer = null;

  
  @SuppressWarnings("unused")
  private FontGraphics p;
  private FontCategory c;
  
  public FontLoadUtftFiles(FontGraphics p, FontCategory c) {
    this.p = p;
    this.c = c;
    if (tokenizer == null) {
      tokenizer = new Tokenizer();
      // Create our tokenizer for C File Image parsing
      tokenizer.defineIgnored("\\s+"); // Ignore whitespace
      tokenizer.defineIgnored("^#.*$|^\\/\\/.*$"); // Ignore comments (which start with # or //)
      tokenizer.add("0x[a-fA-F0-9]+", HEX);
      tokenizer.add("\\\\[0-9]+", OCTAL);
      tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*", WORD);
      tokenizer.add("-?[0-9]+", INTEGER);
//      tokenizer.add("\\,", COMMA);
      tokenizer.add("\\[", OPEN_SQBRACKET);
      tokenizer.add("\\{", OPEN_BRACE);
//      tokenizer.add("\\{", CLOSE_SQBRACKET);
//      tokenizer.add("\\}", CLOSE_BRACE);
      tokenizer.add("\\;", SEMICOLON);
      tokenizer.add(".", SPECIALCHAR);
    }
  }

  // Print information about
  // each type of file.
  @Override
  public FileVisitResult visitFile(Path file,
                                 BasicFileAttributes attr) {
      if (attr.isRegularFile()) {
        Path path = file.getFileName();
        String fileName = path.toString();
        int n = fileName.indexOf(".c");
        if (n == -1) {
          return CONTINUE;
        }
        Token token = null;
        String workingDir = Utils.getWorkingDir();
        String pathName = workingDir + String.format(c.getFontFolderPath()+"%s",fileName);
        File cfile = new File(pathName);
        n = fileName.indexOf(".c");
        String displayName = fileName.substring(0,n);
        // make sure we didn't already read font in from our json file
        if (c.findFontItem(displayName) != null) {
//          Builder.logger.debug("font: " + displayName + " from json file");
          return CONTINUE;
        }
        try {
          tokenizer.setSource(cfile);
//          Builder.logger.debug("Opened file: " + pathName);

          // find open brace
          boolean bFound = false;
          /* 
           * scan for open brace
           */
          while ((token = tokenizer.nextToken()).getType() != 0) {
            if (token.getType() == OPEN_BRACE) {
              bFound = true;
              break;
            }
          }
          if (!bFound) {
            Builder.logger.debug(fileName + " missing " +"{");
            return CONTINUE;
          }
        
          // font size should be first hex value in bitmap
          token = tokenizer.nextToken();
          if (token.getType() != HEX) {
            Builder.logger.debug(fileName + " Missing->Font Size");
            return CONTINUE;
          }
          int size = Integer.decode(token.getToken()).intValue();
          String sSize = Integer.valueOf(size).toString(); // get font width as string
      
          tokenizer.close();

          FontItem item = new FontItem();

          item.setFamilyName(displayName);
          item.setDisplayName(displayName);
          item.setSrcFilename(fileName);
          item.setExternName(displayName);
          item.setLogicalSize(sSize);
          item.setLogicalStyle(FontItem.PLAIN);

          // now finish up
          item.setFileName(fileName);
          String fontRef = "&" + displayName;
          item.setFontRef(fontRef);
          c.addFont(item);
          return CONTINUE;
        } catch (IOException | ParserException | FontException | NumberFormatException | TokenizerException e) {
          String msg = String.format("File [%s]: %s", 
              fileName, e.toString());
          tokenizer.close();
          Builder.logger.error(msg);
          return CONTINUE;
        }
        
      }
      return CONTINUE;
  }

  // Print each directory visited.
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
//    Builder.logger.debug("Directory: " + dir);
    return CONTINUE;
  }

}
