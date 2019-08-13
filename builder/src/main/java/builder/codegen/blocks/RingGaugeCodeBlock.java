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
package builder.codegen.blocks;

import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.TemplateManager;
import builder.models.RingGaugeModel;
import builder.models.WidgetModel;

/**
 * The Class RingGaugeCodeBlock outputs the code block for
 * GUIslice API gslc_ElemXRingGaugeCreate() calls.
 * 
 * @author Paul Conti
 * 
 */
public class RingGaugeCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String RINGGAUGE_TEMPLATE     = "<RINGGAUGE>";
  private final static String FLATCOL_TEMPLATE       = "<RINGGAUGE_FLATCOL>";
  private final static String GRADIENTCOL_TEMPLATE   = "<RINGGAUGE_GRADIENTCOL>";
  private final static String INACTIVECOL_TEMPLATE   = "<RINGGAUGE_INACTIVECOL>";
  private final static String LINE_TEMPLATE          = "<RINGGAUGE_LINE>";
  private final static String RANGE_TEMPLATE         = "<RINGGAUGE_RANGE>";
  private final static String SEGMENTS_TEMPLATE      = "<RINGGAUGE_SEGMENTS>";
  private final static String TEXTCOLOR_TEMPLATE     = "<TEXT_COLOR>";
  private final static String COLOR_TEMPLATE          = "<COLOR_FILL>";
  private final static String ELEMENTREF_TEMPLATE    = "<ELEMENT_REF>";
  
  /**
   * Instantiates a new box code block.
   */
  public RingGaugeCodeBlock() {
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
    RingGaugeModel m = (RingGaugeModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    // now output creation API
    template = tm.loadTemplate(RINGGAUGE_TEMPLATE);
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);
    
    if (m.getLineThickness() != RingGaugeModel.DEF_LINE_SZ) {
      template = tm.loadTemplate(LINE_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    if (m.getSegments() != RingGaugeModel.DEF_SEGMENTS) {
      template = tm.loadTemplate(SEGMENTS_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    if (!m.isClockwise() ||
        m.getStartingAngle() != RingGaugeModel.DEF_STARTING_ANGLE ||
        m.getAngularRange() != RingGaugeModel.DEF_ANGULAR_RANGE) {
      template = tm.loadTemplate(RANGE_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    if (m.useGradientColors()) {
      template = tm.loadTemplate(GRADIENTCOL_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    } else {
      if (!m.getActiveColor().equals(RingGaugeModel.DEF_ACTIVE_COLOR)) {
        template = tm.loadTemplate(FLATCOL_TEMPLATE);
        outputLines = tm.expandMacros(template, map);
        tm.codeWriter(sBd, outputLines);
      }
    }
    
    if (!m.getInactiveColor().equals(RingGaugeModel.DEF_INACTIVE_COLOR)) {
      template = tm.loadTemplate(INACTIVECOL_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    if (!m.getTextColor().equals(RingGaugeModel.DEF_TEXT_COLOR)) {
      template = tm.loadTemplate(TEXTCOLOR_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    if (!m.getFillColor().equals(RingGaugeModel.DEF_FILL_COLOR)) {
      template = tm.loadTemplate(COLOR_TEMPLATE);
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