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

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.FontItem;
import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.models.KeyPadModel;
import builder.models.KeyPadTextModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.NumKeyPadEditor;

/**
 * The Class FontLoadPipe handles code generation
 * within the "Load_Fonts" tag of our source code.
 * 
 * This section builds up a list of GUISlice ENUMs in use.
 * 
 * @author Paul Conti
 * 
 */
public class FontLoadPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String FONT_LOAD_TEMPLATE     = "<FONT_LOAD>";
  private final static String FONT_MODE_TEMPLATE     = "<FONT_MODE>";

  /** The Constants for macros. */
  private final static String FONT_ID_MACRO          = "FONT_ID";
  private final static String FONT_REF_MACRO         = "FONT_REF";
  private final static String FONT_REFTYPE_MACRO     = "FONT_REFTYPE";
  private final static String FONT_MODE_MACRO     = "FONT_MODE";
  private final static String FONT_SZ_MACRO          = "FONT_SZ";

  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public FontLoadPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.LOADFONTS_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.LOADFONTS_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    // setup
    FontFactory ff = FontFactory.getInstance();
    
    // create a list of font enums in use by this project.
    // has to be created the same way as EnumPipe or the gslc_FontSet will be off.
    List<String> fontList = new ArrayList<String>();
    String name = null;
    boolean bAddNumKeyPad = false;
    boolean bAddAlphaKeyPad = false;
    for (WidgetModel m : cg.getModels()) {
      name = m.getFontEnum();
      if (name != null)
        fontList.add(name);
      if (m.getType().equals(EnumFactory.NUMINPUT)) {
        bAddNumKeyPad = true;
      }
      if (m.getType().equals(EnumFactory.TEXTINPUT)) {
        bAddAlphaKeyPad = true;
      }
    }
    // End with keyboard fonts - bug 126 missing keyboard font
    // place any keypads at end
    if (bAddNumKeyPad) {
      KeyPadModel m = (KeyPadModel)NumKeyPadEditor.getInstance().getModel();
      name = m.getFontEnum();
      if (name != null)
        fontList.add(name);
    }
    if (bAddAlphaKeyPad) {
      KeyPadTextModel m = (KeyPadTextModel)AlphaKeyPadEditor.getInstance().getModel();
      name = m.getFontEnum();
      if (name != null)
        fontList.add(name);
    }
    // sort the names and remove duplicates
    CodeUtils.sortListandRemoveDups(fontList);
    // now create the font load code
    tm = cg.getTemplateManager();
    List<String> load_template = tm.loadTemplate(FONT_LOAD_TEMPLATE);;
    List<String> mode_template = tm.loadTemplate(FONT_MODE_TEMPLATE);;
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    FontItem font = null;
    for (String fontEnum : fontList) {
      // two step process to get our font information
      // first step retrieve the name of our font using the font enum as the key
      name = ff.getFontDisplayName(fontEnum);
      if (name == null) continue;
      // step two, map the name to our font item record.
      font = ff.getFontItem(name);
      if (font == null) continue;
      map.clear();
      map.put(FONT_ID_MACRO, font.getFontId());
      map.put(FONT_REFTYPE_MACRO, font.getFontRefType());
      map.put(FONT_REF_MACRO, font.getFontRef());
      map.put(FONT_SZ_MACRO, font.getFontSz());
      map.put(FONT_MODE_MACRO, font.getFontRefMode());
      outputLines = tm.expandMacros(load_template, map);
      tm.codeWriter(sBd, outputLines);
      if (!font.getFontRefMode().equals("NULL")) {
        outputLines = tm.expandMacros(mode_template, map);
        tm.codeWriter(sBd, outputLines);
      }
    }

  }

}
