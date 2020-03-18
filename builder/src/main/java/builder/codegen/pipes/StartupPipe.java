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

import java.awt.Color;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.ColorFactory;
import builder.models.GeneralModel;
import builder.prefs.GeneralEditor;

/**
 * The Class StartupPipe handles code generation
 * within the "Startup" tag of our source code.
 * 
 * This section finishes off code generation with any startup details.
 * Currently only screen rotation.
 * 
 * @author Paul Conti
 * 
 */
public class StartupPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String ROTATE_TEMPLATE             = "<ROTATE>";
  private final static String TRANSPARENCY_COLOR_TEMPLATE = "<TRANSPARENCY_COLOR>";

  /** The Constants for macros. */
  private final static String COLOR_MACRO                 = "COLOR";
  private final static String ROTATION_MACRO              = "ROTATION";

  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public StartupPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG     = Tags.TAG_PREFIX + Tags.STARTUP_TAG + Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX + Tags.STARTUP_TAG + Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    // grab our user preferences
    GeneralModel gm = (GeneralModel) GeneralEditor.getInstance().getModel();
    // setup    
    tm = cg.getTemplateManager();
    List<String> templateLines = null;
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();

    // do we need to set transparency color?
    Color color = ((GeneralModel) GeneralEditor.getInstance().getModel()).getTransparencyColor();
    Color defaultColor = new Color(255,0,255);
    if (!color.equals(defaultColor)) {
      map.clear();
      String strColor = ColorFactory.getInstance().colorAsString(color);
      map.put(COLOR_MACRO, strColor);      
      templateLines = tm.loadTemplate(TRANSPARENCY_COLOR_TEMPLATE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }

    // do we need to rotate display?
    if (gm.getScreenRotation() != -1) {
      // seems so.. 
      map.clear();
      map.put(ROTATION_MACRO, String.valueOf(gm.getScreenRotation()));      
      templateLines = tm.loadTemplate(ROTATE_TEMPLATE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
  }

}
