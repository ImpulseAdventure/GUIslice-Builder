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
package builder.codegen.blocks;

import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.TemplateManager;
import builder.models.BoxModel;
import builder.models.WidgetModel;

/**
 * The Class BoxCodeBlock outputs the code block for
 * GUIslice API gslc_ElemCreateBox() calls.
 * 
 * @author Paul Conti
 * 
 */
public class BoxCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String BOX_TEMPLATE           = "<BOX>";
  private final static String FRAME_EN_TEMPLATE      = "<FRAME_EN>";
  private final static String COLOR_TEMPLATE         = "<COLOR>";
  private final static String CORNERS_ROUNDED_TEMPLATE = "<CORNERS_ROUNDED>";
  private final static String DRAWFUNC_TEMPLATE      = "<DRAWFUNC>";
  private final static String ELEMENTREF_TEMPLATE    = "<ELEMENT_REF>";
  private final static String TICKFUNC_TEMPLATE      = "<TICKFUNC>";
  private final static String TOUCH_EN_TEMPLATE      = "<TOUCH_EN>";
  
  /**
   * Instantiates a new box code block.
   */
  public BoxCodeBlock() {
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
    template = tm.loadTemplate(BOX_TEMPLATE);
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
    
    if (m.hasDrawFunc()) {
      template = tm.loadTemplate(DRAWFUNC_TEMPLATE);
      tm.codeWriter(sBd, template);
    } 

    if (m.hasTickFunc()) {
      template = tm.loadTemplate(TICKFUNC_TEMPLATE);
      tm.codeWriter(sBd, template);
    } 

    if ((!m.getFrameColor().equals(BoxModel.DEF_FRAME_COLOR)) ||
        (!m.getFillColor().equals(BoxModel.DEF_FILL_COLOR))  || 
        (!m.getSelectedColor().equals(BoxModel.DEF_SELECTED_COLOR))) {
      template = tm.loadTemplate(COLOR_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    if (!m.isFrameEnabled()) {
      template = tm.loadTemplate(FRAME_EN_TEMPLATE);
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