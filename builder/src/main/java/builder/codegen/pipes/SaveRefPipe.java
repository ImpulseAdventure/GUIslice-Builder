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
package builder.codegen.pipes;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.models.WidgetModel;

// TODO: Auto-generated Javadoc
/**
 * The Class EnumPipe handles code generation
 * within the "Save_References" tag of our source code.
 * 
 * This section builds up a list of GUISlice Element References
 * that need to be saved for quick access during runtime.
 * 
 * @author Paul Conti
 * 
 */
public class SaveRefPipe extends WorkFlowPipe {

  /** The Constants for tags. */
  private final static String SAVEREF_TAG              = "//<Save_References !Start!>";
  private final static String SAVEREF_END_TAG          = "//<Save_References !End!>";
  
  /** The Constants for templates. */
  private final static String ELEMENTREF_SAVE_TEMPLATE = "<ELEMENT_REF_SAVE>";
  private final static String ELEMREF_MACRO            = "ELEMREF";

  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public SaveRefPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = SAVEREF_TAG;
    this.MY_END_TAG = SAVEREF_END_TAG;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    // scan our widget models for element references and build a list of them
    List<String> refList = new ArrayList<String>();
    for (WidgetModel m : cg.getModels()) {
      if (m.getElementRef() != null && !m.getElementRef().isEmpty())
        refList.add(m.getElementRef());
      if (m.getScrollbarERef() != null)
        refList.add(m.getScrollbarERef());
    }

    // now output any we found
    tm = cg.getTemplateManager();
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<>();
    if (refList.size() > 0) {
      // Now we have a full list of references we can sort the list.
      Collections.sort(refList);
      // Now output the UI widgets element references
      template = tm.loadTemplate(ELEMENTREF_SAVE_TEMPLATE);
      for (String s : refList) {
        map.put(ELEMREF_MACRO, s);
        outputLines = tm.expandMacros(template, map);
        tm.codeWriter(sBd, outputLines);
      }
    }

    // we have hidden cg.getPages() if users are referencing virtual keypads
    boolean bAddNumKeyPad = false;
    boolean bAddAlphaKeyPad = false;
    for (WidgetModel m : cg.getModels()) {
      if (m.getType().equals(EnumFactory.NUMINPUT)) {
        bAddNumKeyPad = true;
      }
      if (m.getType().equals(EnumFactory.TEXTINPUT)) {
        bAddAlphaKeyPad = true;
      }
    }
    if (bAddNumKeyPad) {
      template = tm.loadTemplate(ELEMENTREF_SAVE_TEMPLATE);
      map.clear();
      map.put(ELEMREF_MACRO, EnumFactory.KEYPAD_ELEMREF);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    if (bAddAlphaKeyPad) {
      template = tm.loadTemplate(ELEMENTREF_SAVE_TEMPLATE);
      map.clear();
      map.put(ELEMREF_MACRO, EnumFactory.ALPHAKEYPAD_ELEMREF);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
  }
}
