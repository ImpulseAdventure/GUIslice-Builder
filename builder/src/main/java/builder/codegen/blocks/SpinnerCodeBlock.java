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

import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.TemplateManager;
import builder.fonts.FontFactory;
import builder.fonts.FontTFT;
import builder.models.SpinnerModel;
import builder.models.WidgetModel;

/**
 * The Class SpinnerCodeBlock outputs the code block for
 * GUIslice API gslc_ElemXSpinnerCreate() calls.
 * 
 * @author Paul Conti
 * 
 */
public class SpinnerCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String SPINNER_TEMPLATE       = "<SPINNER>";
  private final static String SPINNER_ARROWS_TEMPLATE       = "<SPINNER_ARROWS>";
  private final static String ELEMENTREF_TEMPLATE    = "<ELEMENT_REF>";
  
  /** The Constants for MACROS */
  private final static String INCR_MACRO             = "ARROW_UP";
  private final static String DECR_MACRO             = "ARROW_DOWN";

  /**
   * Instantiates a new box code block.
   */
  public SpinnerCodeBlock() {
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
    SpinnerModel m = (SpinnerModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    // now output creation API
    template = tm.loadTemplate(SPINNER_TEMPLATE);
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);
    
    // deal with any overrides
    if (!(m.getIncrementChar().equals(SpinnerModel.DEF_INCRBUTTON)) ||
        !(m.getDecrementChar().equals(SpinnerModel.DEF_DECRBUTTON))) {
      template = tm.loadTemplate(SPINNER_ARROWS_TEMPLATE);
      String fontName = m.getFontDisplayName();
      FontTFT font = FontFactory.getInstance().getFont(fontName);
      /* we can't use standard mapping of SPIN-102 and SPIN-103 since we may need
       * to handle converting utf8 to hex characters and deal with builtin
       * (classic) character sets that be not be in display 32-126 ascii range.
       */
      map.put(INCR_MACRO, CodeUtils.createLiteral(font, "'", m.getIncrementChar()));
      map.put(DECR_MACRO, CodeUtils.createLiteral(font, "'", m.getDecrementChar()));
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    template = tm.loadTemplate(ELEMENTREF_TEMPLATE);
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);

    template.clear();
    outputLines.clear();
    map.clear();
    return sBd;   
  }

}