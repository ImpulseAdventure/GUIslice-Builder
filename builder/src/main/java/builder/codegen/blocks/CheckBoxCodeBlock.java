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
import builder.codegen.TemplateManager;
import builder.models.CheckBoxModel;
import builder.models.WidgetModel;

/**
 * The Class CheckBoxCodeBlock outputs the code block
 * for GUIslice API gslc_ElemXCheckboxCreate() calls.
 * 
 * @author Paul Conti
 * 
 */
public final class CheckBoxCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String CHECKBOX_TEMPLATE      = "<CHECKBOX>";
//  private final static String CHECKBOX_GROUP_TEMPLATE = "<CHECKBOX_GROUP>";
  private final static String CHECKBOXSETSTATE_TEMPLATE = "<CHECKBOXSETSTATE>";
  private final static String COLOR_TEMPLATE         = "<COLOR>";
  private final static String ELEMENTREF_TEMPLATE    = "<ELEMENT_REF>";
//  private final static String GROUP_TEMPLATE         = "<GROUP>";

  /**
   * Instantiates a new check box code block.
   */
  public CheckBoxCodeBlock() {
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
    CheckBoxModel m = (CheckBoxModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    // now output creation API
    template = tm.loadTemplate(CHECKBOX_TEMPLATE);
    /*
    // member of group?
    String groupId = m.getGroupId();
    if (!groupId.equals("GSLC_GROUP_ID_NONE")) {
      tm.loadTemplate(CHECKBOX_GROUP_TEMPLATE);
    }
*/
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);

    if ((!m.getFrameColor().equals(CheckBoxModel.DEF_FRAME_COLOR)) ||
        (!m.getFillColor().equals(CheckBoxModel.DEF_FILL_COLOR))  || 
        (!m.getSelectedColor().equals(CheckBoxModel.DEF_SELECTED_COLOR))) {
      template = tm.loadTemplate(COLOR_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
/*
    if (!groupId.equals("GSLC_GROUP_ID_NONE")) {
      template = tm.loadTemplate(GROUP_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
*/    
    if (m.isCallbackEn()) {
      template = tm.loadTemplate(CHECKBOXSETSTATE_TEMPLATE);
      tm.codeWriter(sBd, template);
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