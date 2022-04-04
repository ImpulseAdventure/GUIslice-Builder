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
package builder.codegen.pipes;

import java.io.File;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.Builder;
import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.controller.Controller;
import builder.fonts.BuilderFonts;
//import builder.fonts.FontCategory;
import builder.fonts.FontFactory;
import builder.fonts.FontItem;
import builder.fonts.FontGraphics;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.common.Pair;
import builder.models.KeyPadModel;
import builder.models.KeyPadTextModel;
import builder.models.ProjectModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.NumKeyPadEditor;

/**
 * The Class FontsPipe handles code generation
 * within the "Fonts" tag of our source code.
 * 
 * This section writes out include files or #defines 
 * for fonts used in this project.
 * 
 * @author Paul Conti
 * 
 */
public class FontsPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String FONT_EXTERN_TEMPLATE            = "<FONT_EXTERN>"; 
  private final static String FONT_DEFINE_TEMPLATE            = "<FONT_DEFINE>"; 
  private final static String FONT_INCLUDE_TEMPLATE           = "<FONT_INCLUDE>"; 
   
  /** The Constants for macros. */
  private final static String EXTERN_MACRO           = "EXTERN_STORAGE";
  private final static String DEFINE_FILE_MACRO      = "DEFINE_FILE";
  private final static String FONT_REF_MACRO         = "FONT_REF";
  private final static String INCLUDE_FILE_MACRO     = "INCLUDE_FILE";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public FontsPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.FONTS_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.FONTS_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  public void doCodeGen(StringBuilder sBd) {
    // setup
    ProjectModel pm = Controller.getProjectModel();
    FontFactory ff = FontFactory.getInstance();
    tm = cg.getTemplateManager();
    
    // scan thru all of the projects widgets and
    // build up a list of all font display names.
    List<String> fontNames = new ArrayList<String>();
    String name = null;
    boolean bAddNumKeyPad = false;
    boolean bAddAlphaKeyPad = false;
    int nErrors = 0;
    for (WidgetModel m : cg.getModels()) {
      name = m.getFontDisplayName();
      if (name != null) {
        if (ff.getFont(name) == null) {
          Builder.logger.error("widget: " +  m.getEnum() + " refers to missing font=" + name);
          nErrors++;
        }
        fontNames.add(name);
      }
      if (m.getType().equals(EnumFactory.NUMINPUT)) {
        bAddNumKeyPad = true;
      }
      if (m.getType().equals(EnumFactory.TEXTINPUT)) {
        bAddAlphaKeyPad = true;
      }
    }
    // End with keyboard fonts - bug 144 missing keyboard font #include
    // place any keypads at end
    if (bAddNumKeyPad) {
      KeyPadModel m = (KeyPadModel)NumKeyPadEditor.getInstance().getModel();
      name = m.getFontDisplayName();
      if (name != null) {
        if (ff.getFont(name) != null) {
          fontNames.add(name);
        } else {
          name = ff.getDefFontName();
          if (name != null) {
            fontNames.add(name);
          } else {
            Builder.logger.error("NumKeyPad: " +  m.getEnum() + " refers to missing font=" + name);
            nErrors++;
          }
        }
      } else {
        Builder.logger.error("NumKeyPad: " +  m.getEnum() + " is missing font");
        nErrors++;
      }
    }
    if (bAddAlphaKeyPad) {
      KeyPadTextModel m = (KeyPadTextModel)AlphaKeyPadEditor.getInstance().getModel();
      name = m.getFontDisplayName();
      if (name != null) {
        if (ff.getFont(name) != null) {
          fontNames.add(name);
        } else {
          name = ff.getDefFontName();
          if (name != null) {
            fontNames.add(name);
          } else {
            Builder.logger.error("AlphaKeyPad: " +  m.getEnum() + " refers to missing font=" + name);
            nErrors++;
          }
        }
      } else {
        Builder.logger.error("AlphaKeyPad: " +  m.getEnum() + " refers to missing font=" + name);
        nErrors++;
      }
    }
    // add any extra fonts requested
    for (String s : pm.getFontsList()) {
      if (s != null && !s.isEmpty()) {
        if (ff.getFont(s) != null) {
          fontNames.add(s);
        } else {
          Builder.logger.error("Project Extra Font: " + s + " is not supported on this platform");
          nErrors++;
        }
      }
    }
    if (nErrors > 0) {
      String fileName = CommonUtils.getWorkingDir()
          + "logs" 
          + System.getProperty("file.separator")
          + "builder.log";
      throw new CodeGenException(String.format("Sketch has %d missing font(s).\nExamine %s for list of fonts.",
          nErrors,fileName));
    }
    // sort the names and remove duplicates
    if (fontNames.size() > 0)
      CodeUtils.sortListandRemoveDups(fontNames);
    
    // now use our reduced fontNames list to build up a   
    // list of font items for fonts in use by this project.
    List<FontItem> fonts = new ArrayList<FontItem>();
    FontItem item = null;
    for (String s : fontNames) {
      item = ff.getFontItem(s);
      if (item == null) continue;
      fonts.add(item);
      Builder.logger.debug("FontsPipe: "+item.toString());
    }
    
    String target = Controller.getTargetPlatform();
    String category;
    List<String> categories = new ArrayList<String>();
    for (FontItem f : fonts) {
      category = f.getCategory().getName();
      categories.add(category);
    }
  	// sort the names and remove duplicates
    if (categories.size() > 0)
			CodeUtils.sortListandRemoveDups(categories);
        
    // Do we have any warnings to output?
    BuilderFonts topOfFonts = ff.getBuilderFonts();
    FontGraphics fontGraphics = topOfFonts.getPlatform(target);
    for (String s : fontGraphics.getWarnings()) {
      if (s.equals("NULL")) continue;
      sBd.append(s);
      sBd.append(System.lineSeparator());
    }

    // Now we need check for any extra include files that might be needed
		for (String s : fontGraphics.getIncludes()) {
			if (s == null || s.isEmpty()) continue;
			sBd.append(s);
			sBd.append(System.lineSeparator());
		}

    // we are ready to output our font information
		List<String> externTemplate = null;
    List<String> includeTemplate = null;
    List<String> defineTemplate = null;
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String, String>();
    List<Pair> includesList = new ArrayList<Pair>();
    List<Pair> copyList = new ArrayList<Pair>();
    int idx;
    /* the following scans for any headers we need to include,
     * any defines we need to create, and create a list
     * of any un-installed fonts we need to copy to the
     * user's project folder.
     */
    boolean bCopyFonts = true;
    if (pm.getTargetPlatform().equals(ProjectModel.PLATFORM_LINUX) ) {
      bCopyFonts = false;
    }
    Pair pair = null;
    for (FontItem f : fonts) {
      if (!f.getIncludeFile().equals("NULL")) {
        if (!f.isInstalledFont())
          pair = new Pair("", f.getIncludeFile());
        else
          pair = new Pair(f.getCategory().getIncludePath(), f.getIncludeFile());
        includesList.add(pair);
      } else if (!f.getDefineFile().equals("NULL")) {
        // This code only affects linux implementation.
        defineTemplate = tm.loadTemplate(FONT_DEFINE_TEMPLATE);
        map.clear();
        map.put(FONT_REF_MACRO, f.getFontRef());
        map.put(DEFINE_FILE_MACRO, f.getDefineFile());
        outputLines = tm.expandMacros(defineTemplate, map);
        tm.codeWriter(sBd, outputLines);
      } else if (!f.getExternName().equals("NULL")) {
        map.clear();
        map.put(EXTERN_MACRO, f.getExternName());
        externTemplate = tm.loadTemplate(FONT_EXTERN_TEMPLATE);
        outputLines = tm.expandMacros(externTemplate, map);
        tm.codeWriter(sBd, outputLines);
      }
      if (!f.isInstalledFont() && bCopyFonts) {
        if (!f.getIncludeFile().equals("NULL")) {
          String FName = f.getFileName();
          idx = FName.indexOf(".h");
          if (idx > 0) {
            pair = new Pair(FName,f.getIncludeFile());
          } else {
            idx = FName.indexOf(".c");
            if (idx > 0) {
              FName = FName.substring(0,idx) + ".h";
              pair = new Pair(FName,f.getIncludeFile());
            }
          }
          if (pair != null)
            copyList.add(pair);
        }
        /*
         * Currently only Teensy needs C files
         */
        if (!f.getSrcFilename().equals("NULL")) {
          pair = null;
          String FName = f.getFileName();
          idx = FName.indexOf(".c");
          if (idx > 0) {
            pair = new Pair(FName,f.getSrcFilename());
          }
          if (pair != null)
            copyList.add(pair);
        }
      }
    }
    if (includesList.size() > 0) {
      // sort the names and remove duplicates
      CodeUtils.sortPairsRemoveDups(includesList);
      for (Pair px : includesList) {
        String s = px.getValue1() + px.getValue2();
        includeTemplate = tm.loadTemplate(FONT_INCLUDE_TEMPLATE);
        map.put(INCLUDE_FILE_MACRO, s);
        outputLines = tm.expandMacros(includeTemplate, map);
        tm.codeWriter(sBd, outputLines);
      }
    }
    File inFile = null;
    File outFile = null;
    /*
     * delete any font files we may 
     * have previously copied
     */
    CommonUtils.cleanFolderOfFontHeaders(cg.getHdrPath());
    /* copy font sources for any fonts 
     * not included with the chosen graphics package, 
     * Adafruit_GFX using Google's Noto fonts.
     */
    if (copyList.size() > 0 && bCopyFonts) {
      // sort the names and remove duplicates
      CodeUtils.sortPairsRemoveDups(copyList);
      for (Pair px : copyList) {
        String sIn = px.getValue1();
        String sOut = px.getValue2();
        if (sIn.endsWith(".h")) {
          inFile = new File(sIn);
          outFile = new File(cg.getHdrPath()+sOut); 
        } else {
          inFile = new File(sIn);
          outFile = new File(cg.getAppPath()+sOut); 
        }
        CommonUtils.copyFile(inFile, outFile);
      }
    }
  }
}
