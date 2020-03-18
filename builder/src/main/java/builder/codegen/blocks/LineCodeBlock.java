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
import builder.codegen.TemplateManager;
import builder.models.LineModel;
import builder.models.WidgetModel;

/**
 * The Class LineCodeBlock outputs the code block for
 * GUIslice API gslc_ElemCreateLine() calls.
 * 
 * @author Paul Conti
 * 
 */
public class LineCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String LINE_TEMPLATE       = "<LINE>";
  
  /** The Constants for MACROS */
  private final static String X1_MACRO            = "X1";
  private final static String Y1_MACRO            = "Y1";
  
  /**
   * Instantiates a new box code block.
   */
  public LineCodeBlock() {
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
    LineModel m = (LineModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);
    
    // now output creation API
    template = tm.loadTemplate(LINE_TEMPLATE);

    // we need to work out our end points
    int nX1, nY1;
    if (m.isVertical()) {
      nX1 = m.getX();
      nY1 = m.getY()+m.getWidth();
    } else {
      nX1 = m.getX()+m.getWidth();
      nY1 = m.getY();
    }
    map.put(X1_MACRO, String.valueOf(nX1));
    map.put(Y1_MACRO, String.valueOf(nY1));
    
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);

    template.clear();
    outputLines.clear();
    map.clear();
    return sBd;   
  }

}