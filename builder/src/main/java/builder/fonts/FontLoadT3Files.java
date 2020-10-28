/**
 *
 * The MIT License
 *
 * Copyright 2020 Paul Conti
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import builder.Builder;
import builder.parser.ParserException;
import builder.parser.Token;
import builder.parser.Tokenizer;
import builder.parser.TokenizerException;

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
public class FontLoadT3Files extends SimpleFileVisitor<Path> {
  
  static private Pattern numPattern = Pattern.compile("\\d+");
  
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

  
  private FontPlatform p;
  private FontCategory c;
  private String familyName;
  private String logicalStyle;
  
  public FontLoadT3Files(FontPlatform p, FontCategory c) {
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
        String name = path.toString();
        int n = name.indexOf(".h");
        if (n == -1) {
          return CONTINUE;
        }
        String displayName;
        String includeFile = name;
        String parseName = String.format("fonts/t3/%s/%s/%s",familyName,logicalStyle,name);
        name = name.substring(0,n) + ".c";
        String fileName = String.format("fonts/t3/%s/%s/%s",familyName,logicalStyle,name);
        Token token = null;
        File hfile = new File(parseName);
        try {
          tokenizer.setSource(hfile);
//          Builder.logger.debug("Opened file: " + fileName);
          //----------------------------------------------------------------------------------------
          // Find our font name 
          //----------------------------------------------------------------------------------------
          while ((token = tokenizer.nextToken()).getType() != 0) {
            if (token.getType() == WORD) {
              if (!token.getToken().equals("extern")) {
                continue;
              }
              token = tokenizer.nextToken();
              if (token.getType() == WORD) {
                if (!token.getToken().equals("const")) {
                  continue;
                }
              } else {
                continue;
              }
              // found extern const so next should be ILI9341_t3_font_t
              token = tokenizer.nextToken();
              if (!token.getToken().equals("ILI9341_t3_font_t")) {
                continue;
              }
              token = tokenizer.nextToken();
              if (token.getType() == WORD) {
                displayName = token.getToken();
  
                FontItem item = new FontItem();
                item.setFamilyName(familyName);
                item.setDisplayName(displayName);
                item.setFileName(fileName);
                item.setIncludeFile(includeFile);
                String fontRef = "&" + displayName;
                item.setFontRef(fontRef);
                Matcher m = numPattern.matcher(displayName);
                if (m.find()) {
                  item.setLogicalSize(m.group());
                } else {
                  Builder.logger.error("unable to parse font: " + file);
                  return CONTINUE;
                }
                item.setLogicalStyle(logicalStyle);
                item.setPlatform(p);
                item.setCategory(c);
                item.generateEnum();
                item.generateKey();
                String key = item.getKey();
                if (!FontFactory.fontMap.containsKey(key)) {
                  c.addFont(item);
                  FontFactory.platformFonts.add(item);
                  FontFactory.list.add(item);
                  FontFactory.fontMap.put(key, Integer.valueOf(FontFactory.idx++));
//                  Builder.logger.debug(item.toString());
                } else {
                  FontFactory.nErrors++;
                  Builder.logger.error("duplicate font: " + key);
                }
              }
            }
          }

          tokenizer.close();
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
