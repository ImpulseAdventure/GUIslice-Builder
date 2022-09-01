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
 * T3 fonts into a folder without editing the JSON file.
 * 
 * Calling Sequence:
 *   String fullPath = CommonUtils.getInstance().getWorkingDir();
 *   String fontsPath = fullPath + "fonts" + System.getProperty("file.separator") 
 *       + "t3";
 *   Path startingDir = Paths.get(fontsPath);
 *   FontLoadGFXFiles fileVisitor = new FontLoadGFXFiles();
 *   Files.walkFileTree(path, fileVisitor);
 * 
 * @author Paul Conti
 */
public class FontLoadT3Files extends SimpleFileVisitor<Path> {
  
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

  
  private FontGraphics p;
  private FontCategory c;
  private String familyName;
  private String logicalStyle;
  
  public FontLoadT3Files(FontGraphics p, FontCategory c) {
    this.p = p;
    this.c = c;
    familyName = null;
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
        String hdrName = path.toString();
//      Builder.logger.debug("hdrName: " + hdrName);
        int n = hdrName.indexOf(".h");
        if (n == -1) {
          return CONTINUE;
        }
        String cName = hdrName.substring(0,n) + ".c";
        String displayName;
        String includeFile = hdrName;
        String parseName = String.format(c.getFontFolderPath()+"%s/%s/%s",familyName,logicalStyle,hdrName);
        parseName = Utils.getWorkingDir()+parseName;
        String fileName = String.format("%s/%s/%s",familyName,logicalStyle,cName);
        Token token = null;
        File hfile = new File(parseName);
        try {
          tokenizer.setSource(hfile);
          //Builder.logger.debug(p.getName()+" Opened file: " + parseName);
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
        
          //----------------------------------------------------------------------------------------
          // Find our font name 
          //----------------------------------------------------------------------------------------
          while ((token = tokenizer.nextToken()).getType() != 0) {
            if (token.getType() == WORD) {
              if (!token.getToken().equals("extern")) {
                Builder.logger.error("missing extern");
                continue;
              }
              token = tokenizer.nextToken();
              if (token.getType() == WORD) {
                if (!token.getToken().equals("const")) {
                  Builder.logger.error(parseName+ " missing const");
                  continue;
                }
              } else {
                Builder.logger.error(parseName+" missing WORD const");
                continue;
              }
              // found extern const so next should be ILI9341_t3_font_t
              token = tokenizer.nextToken();
              if (!( (token.getToken().equals("ILI9341_t3_font_t")) ||
                     (token.getToken().equals("ILI9488_t3_font_t")) ) ) {
                Builder.logger.error("missing ILI9341_t3_font_t or ILI9488_t3_font_t");
                continue;
              }
              token = tokenizer.nextToken();
              if (token.getType() == WORD) {
                displayName = token.getToken();
                // make sure we didn't already read font in from our json file
                if (c.findFontItem(displayName) == null) {
                  FontItem item = new FontItem();
                  item.setFamilyName(familyName);
                  item.setDisplayName(displayName);
                  if (c.isInstalledFont(displayName)) {
                    //Builder.logger.debug(parseName+" Installed font: " + displayName + " from json file");
                    continue;
                  }
  
                  item.setFileName(fileName);
                  item.setIncludeFile(includeFile);
                  String fontRef = "&" + displayName;
                  item.setFontRef(fontRef);
                  item.setFontRefMode("GSLC_FONTREF_MODE_1");
  
                  n = displayName.indexOf("_");
                  String temp = displayName.substring(n+1);
                  String size = "";
                  for (int k=0; k<temp.length(); k++) {
                    char ch = temp.charAt(k);
                    if (!Character.isDigit(ch))
                      break;
                    size += ch;
                  }
                  item.setLogicalSize(size);
                  item.setLogicalStyle(logicalStyle);
                  c.addFont(item);
                }
              }
            }
          }

          tokenizer.close();
          return CONTINUE;
        } catch (IOException | ParserException | FontException | NumberFormatException | TokenizerException e) {
          String msg = String.format("Platform: %s File [%s]: %s", 
              p.getName(), fileName, e.toString());
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
    // Builder.logger.debug("Directory: " + dir);
    return CONTINUE;
  }

}
