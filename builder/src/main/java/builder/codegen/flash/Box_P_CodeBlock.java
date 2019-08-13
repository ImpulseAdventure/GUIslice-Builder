/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
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
package builder.codegen.flash;

import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.TemplateManager;
import builder.codegen.blocks.CodeBlock;
import builder.models.BoxModel;
import builder.models.WidgetModel;

/**
 * The Class Box_P_CodeBlock outputs the code block for
 * GUIslice API flash version gslc_ElemCreateBox_P() calls.
 * 
 * @author Paul Conti
 * 
 */
public class Box_P_CodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String BOX_TEMPLATE           = "<BOX_P>";
  private final static String BOX_ALL_TEMPLATE       = "<BOX_ALL_FUNCT_P>";
  private final static String BOX_DRAW_TEMPLATE      = "<BOX_DRAW_FUNCT_P>";
  private final static String BOX_TICK_TEMPLATE      = "<BOX_TICK_FUNCT_P>";
  private final static String CORNERS_ROUNDED_TEMPLATE = "<CORNERS_ROUNDED_P>";
  private final static String TOUCH_EN_TEMPLATE      = "<TOUCH_EN_P>";
  private final static String ELEMENTREF_FIND_TEMPLATE = "<ELEMENT_REF_FIND_P>";
  
  /**
   * Instantiates a new box code block.
   */
  public Box_P_CodeBlock() {
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
    BoxModel m = (BoxModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    // now output creation API
    String templateName = BOX_TEMPLATE;
    if (m.hasDrawFunc() && m.hasTickFunc()) {
      templateName = BOX_ALL_TEMPLATE;
    } else if (m.hasDrawFunc()) {
      templateName = BOX_DRAW_TEMPLATE;
    } else if (m.hasTickFunc()) {
      templateName = BOX_TICK_TEMPLATE;
    } 
    
    template = tm.loadTemplate(templateName);
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);

    if (m.isRoundedEn()) {
      template = tm.loadTemplate(CORNERS_ROUNDED_TEMPLATE);
      tm.codeWriter(sBd, template);
    }
    
    if (m.isTouchEn()) {
      template = tm.loadTemplate(TOUCH_EN_TEMPLATE);
      tm.codeWriter(sBd, template);
    }
    
    if (!m.getElementRef().isEmpty()) {
      // we need to do a gslc_PageFindElemById
      template = tm.loadTemplate(ELEMENTREF_FIND_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    } 

    template.clear();
    outputLines.clear();
    map.clear();
    return sBd;   
  }
  
}