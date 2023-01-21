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
package builder.codegen.blocks;

import java.lang.StringBuilder;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.TemplateManager;
import builder.controller.Controller;
import builder.fonts.FontFactory;
import builder.fonts.FontTFT;
import builder.models.ProjectModel;
import builder.models.TextInputModel;
import builder.models.WidgetModel;

/**
 * The Class TextInputCodeBlock outputs the code block
 * for GUIslice API gslc_ElemCreateTxt() calls that bring
 * up the alpha (text based) keypad.
 * 
 * @author Paul Conti
 * 
 */
public final class TextInputCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String ALIGN_TEMPLATE         = "<TEXTALIGN>";
  private final static String ELEMENTREF_TEMPLATE    = "<ELEMENT_REF>";
  private final static String FILL_EN_TEMPLATE       = "<FILL_EN>";
  private final static String FRAME_EN_TEMPLATE      = "<INPUTFRAME_EN>";
  private final static String TEXTMARGIN_TEMPLATE     = "<TEXT_MARGIN>";
  private final static String TEXTCOLOR_TEMPLATE     = "<TEXT_COLOR>";
  private final static String TEXT_INPUT_TEMPLATE    = "<TEXT_INPUT>";
  private final static String TEXTUTF8_TEMPLATE      = "<TEXT_UTF8>";
  private final static String TOUCH_EN_TEMPLATE      = "<TOUCH_EN>";
  private final static String COLOR_TEMPLATE         = "<COLOR>";

  /** The Constants for MACROS */
  private final static String TEXT_MACRO             = "TEXT";

  /**
   * Instantiates a new check box code block.
   */
  public TextInputCodeBlock() {
  }

  /**
   * Process will create our new code block and append it to
   * our input string builder object.
   *
   * @param cg
   *          the cg points to our CodeGenerator object that 
   *          is the controller for code generation.
   * @param tm
   *          the tm is the TemplateManager
   * @param sBd
   *          the sBd is the processed code
   * @param pageEnum
   *          the page enum
   * @param wm
   *          the wm is the widget model to use for code generation
   * @return the <code>string builder</code> object
   */
  static public StringBuilder process(CodeGenerator cg, TemplateManager tm, StringBuilder sBd, String pageEnum, WidgetModel wm) {
    TextInputModel m = (TextInputModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    // now output creation API
    template = tm.loadTemplate(TEXT_INPUT_TEMPLATE);
    
    String fontName = m.getFontDisplayName();
    FontTFT font = FontFactory.getInstance().getFont(fontName);
    /* we can't use standard mapping of TXT-201 since we may need
     * to handle converting utf8 to hex characters and deal with builtin
     * (classic) character sets that be not be in display 32-126 ascii range.
     */
    map.put(TEXT_MACRO, CodeUtils.createLiteral(font, "\"", m.getText()));

    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);
    
    // now deal with any extra switches
    String strAlign = m.getAlignment();
    if (!strAlign.equals(FontTFT.ALIGN_LEFT)) {
      template = tm.loadTemplate(ALIGN_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    if (m.getTextMargin() != 0) {
      template = tm.loadTemplate(TEXTMARGIN_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    if (!m.getTextColor().equals(TextInputModel.DEF_TEXT_COLOR)) {
      template = tm.loadTemplate(TEXTCOLOR_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    if ((!m.getFrameColor().equals(TextInputModel.DEF_FRAME_COLOR)) ||
        (!m.getFillColor().equals(TextInputModel.DEF_FILL_COLOR))  || 
        (!m.getSelectedColor().equals(TextInputModel.DEF_SELECTED_COLOR))) {
      template = tm.loadTemplate(COLOR_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    if (!m.isFillEnabled()) {
      template = tm.loadTemplate(FILL_EN_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }

    template = tm.loadTemplate(FRAME_EN_TEMPLATE);
    tm.codeWriter(sBd, template);
    
    template = tm.loadTemplate(TOUCH_EN_TEMPLATE);
    tm.codeWriter(sBd, template);
    
    if (m.isUTF8() && Controller.getTargetPlatform().equals(ProjectModel.PLATFORM_LINUX)) {
      template = tm.loadTemplate(TEXTUTF8_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }

    if (!m.getElementRef().isEmpty()) {
      template = tm.loadTemplate(ELEMENTREF_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }

    template.clear();
    outputLines.clear();
    map.clear();
    return sBd;   
  }
  
}