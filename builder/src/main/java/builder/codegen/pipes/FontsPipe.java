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
package builder.codegen.pipes;

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
import builder.common.FontItem;
import builder.common.FontPlatform;
import builder.controller.Controller;
import builder.common.BuilderFonts;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.common.FontCategory;
import builder.common.FontFactory;
import builder.models.KeyPadModel;
import builder.models.KeyPadTextModel;
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
  private final static String FONT_DEFINE_TEMPLATE            = "<FONT_DEFINE>"; 
  private final static String FONT_INCLUDE_TEMPLATE           = "<FONT_INCLUDE>"; 
   
  /** The Constants for macros. */
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
    if (nErrors > 0) {
      String fileName = CommonUtils.getInstance().getWorkingDir()
          + "logs" 
          + System.getProperty("file.separator")
          + "builder.log";
      throw new CodeGenException(String.format("Sketch has %d missing font(s).\nExamine %s for list of fonts.",
          nErrors,fileName));
    }
    // End with keyboard fonts - bug 144 missing keyboard font #include
    // place any keypads at end
    if (bAddNumKeyPad) {
      KeyPadModel m = (KeyPadModel)NumKeyPadEditor.getInstance().getModel();
      name = m.getFontDisplayName();
      if (name != null)
        fontNames.add(name);
    }
    if (bAddAlphaKeyPad) {
      KeyPadTextModel m = (KeyPadTextModel)AlphaKeyPadEditor.getInstance().getModel();
      name = m.getFontDisplayName();
      if (name != null)
        fontNames.add(name);
    }
    if (fontNames.size() == 0)
      return;
    // sort the names and remove duplicates
    CodeUtils.sortListandRemoveDups(fontNames);
    
    // now use our reduced fontNames list to build up a   
    // list of font items for fonts in use by this project.
    List<FontItem> fonts = new ArrayList<FontItem>();
    FontItem item = null;
    for (String s : fontNames) {
      item = ff.getFontItem(s);
      if (item == null) continue;
      fonts.add(item);
    }
    
    String target = Controller.getTargetPlatform();
    String category;
    List<String> categories = new ArrayList<String>();
    for (FontItem f : fonts) {
      category = f.getCategory().getName();
      categories.add(category);
    }
    if (categories.size() == 0)
      return;
    // sort the names and remove duplicates
    CodeUtils.sortListandRemoveDups(categories);
        
    // Do we have any warnings to output?
    BuilderFonts topOfFonts = ff.getBuilderFonts();
    FontPlatform fontPlatform = topOfFonts.getPlatform(target);
    for (String s : fontPlatform.getWarnings()) {
      if (s.equals("NULL")) continue;
      sBd.append(s);
      sBd.append(System.lineSeparator());
    }

    // Now we need check for any extra include files that might be needed
    for (FontCategory c : fontPlatform.getCategories()) {
      for (String s : c.getIncludes()) {
        if (s.equals("NULL")) continue;
        sBd.append(s);
        sBd.append(System.lineSeparator());
      }
    }

    // we are ready to output our font information
    List<String> includeTemplate = null;
    List<String> defineTemplate = null;
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String, String>();
    for (FontItem f : fonts) {
      if (!f.getIncludeFile().equals("NULL")) {
          includeTemplate = tm.loadTemplate(FONT_INCLUDE_TEMPLATE);
          map.put(INCLUDE_FILE_MACRO, f.getIncludeFile());
          outputLines = tm.expandMacros(includeTemplate, map);
          tm.codeWriter(sBd, outputLines);
      } else if (!f.getDefineFile().equals("NULL")) {
        // This code only affects linux implementation.
        defineTemplate = tm.loadTemplate(FONT_DEFINE_TEMPLATE);
        map.put(FONT_REF_MACRO, f.getFontRef());
        map.put(DEFINE_FILE_MACRO, f.getDefineFile());
        outputLines = tm.expandMacros(defineTemplate, map);
        tm.codeWriter(sBd, outputLines);
      }
    }
  }
}
