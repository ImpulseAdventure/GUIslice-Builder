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
import builder.models.ImgButtonModel;
import builder.models.WidgetModel;

/**
 * The Class GraphCodeBlock outputs the code block
 * for GUIslice API gslc_ElemCreateBtnImg() calls.
 * 
 * @author Paul Conti
 * 
 */
public final class ImgButtonCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String IMGBUTTON_DEFINE_TEMPLATE  = "<IMGBUTTON_DEFINE>";
  private final static String IMGBUTTON_EXTERN_TEMPLATE  = "<IMGBUTTON_EXTERN>";
  private final static String IMGTOGGLE_DEFINE_TEMPLATE  = "<IMGTOGGLE_DEFINE>";
  private final static String IMGTOGGLE_EXTERN_TEMPLATE  = "<IMGTOGGLE_EXTERN>";
  private final static String IMAGETRANSPARENT_TEMPLATE  = "<IMGBTNTRANSPARENT>";
  private final static String ELEMENTREF_TEMPLATE    = "<ELEMENT_REF>";
  private final static String FRAME_EN_TEMPLATE      = "<FRAME_EN>";
  private final static String COLOR_TEMPLATE         = "<COLOR_IMAGE>";
  private final static String COUNT_MACRO            = "COUNT";
  private final static String GROUP_TEMPLATE         = "<GROUP>";

  /**
   * Instantiates a new check box code block.
   */
  public ImgButtonCodeBlock() {
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
    ImgButtonModel m = (ImgButtonModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    map.put(COUNT_MACRO, CodeUtils.getKeyCount(m.getKey()));

    // now output creation API
    if (m.getDefine() != null && !m.getDefine().isEmpty()) {
      if (m.isToggle()) {
        template = tm.loadTemplate(IMGTOGGLE_DEFINE_TEMPLATE);
      } else {
        template = tm.loadTemplate(IMGBUTTON_DEFINE_TEMPLATE);
      }
    } else {
      if (m.isToggle()) {
        template = tm.loadTemplate(IMGTOGGLE_EXTERN_TEMPLATE);
      } else {
        template = tm.loadTemplate(IMGBUTTON_EXTERN_TEMPLATE);
      }
    }
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);

    // handle transparency
    if (m.isTransparent()) {
      template = tm.loadTemplate(IMAGETRANSPARENT_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }    
    if (m.isFrameEnabled()) {
      template = tm.loadTemplate(FRAME_EN_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
      template = tm.loadTemplate(COLOR_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    String groupId = m.getGroupId();

    if (m.isToggle() && !groupId.equals("GSLC_GROUP_ID_NONE")) {
      template = tm.loadTemplate(GROUP_TEMPLATE);
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