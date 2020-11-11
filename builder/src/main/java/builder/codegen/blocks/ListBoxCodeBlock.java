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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.TemplateManager;
import builder.fonts.FontTFT;
import builder.models.ListBoxModel;
import builder.models.WidgetModel;

/**
 * The Class ListBoxCodeBlock outputs the code block
 * for GUIslice API gslc_ElemXListboxCreate() calls.
 * 
 * @author Paul Conti
 * 
 */
public final class ListBoxCodeBlock implements CodeBlock {

  /** The Constants for TEMPLATES. */
  private final static String LISTBOX_TEMPLATE       = "<LISTBOX>";
  private final static String LISTBOXSLIDER1_TEMPLATE = "<LISTBOXSLIDER_1>";
  private final static String LISTBOXSLIDER2_TEMPLATE = "<LISTBOXSLIDER_2>";
  private final static String LISTBOX_ITEM_TEMPLATE   = "<LISTBOX_ITEM>";
  private final static String LISTBOX_GAP_TEMPLATE    = "<LISTBOX_GAP>";
  private final static String ALIGN_TEMPLATE         = "<TEXTALIGN>";
  private final static String FRAME_EN_TEMPLATE       = "<FRAME_EN>";
  private final static String ELEMENTREF_TEMPLATE     = "<ELEMENT_REF>";
  private final static String TEXT_MACRO              = "TEXT";
  
  /**
   * Instantiates a new check box code block.
   */
  public ListBoxCodeBlock() {
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
    ListBoxModel m = (ListBoxModel)wm;
    List<String> template = null;
    List<String> outputLines = null;
    Map<String, String> map = m.getMappedProperties(pageEnum);

    // now output creation API
    if (m.addScrollbar()) {
      template = tm.loadTemplate(LISTBOXSLIDER1_TEMPLATE);
    } else {
      template = tm.loadTemplate(LISTBOX_TEMPLATE);
    }
    outputLines = tm.expandMacros(template, map);
    tm.codeWriter(sBd, outputLines);
    
    if (m.getItemGap() > 0) {
      template = tm.loadTemplate(LISTBOX_GAP_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    String[] items = m.getItems();
    if (items != null && !items[0].isEmpty()) {
      Map<String, String> mapItems = new HashMap<String, String>(16);
      template = tm.loadTemplate(LISTBOX_ITEM_TEMPLATE);
      for (int i=0; i<items.length; i++) {
        mapItems.put(TEXT_MACRO, items[i]);
        outputLines = tm.expandMacros(template, mapItems);
        tm.codeWriter(sBd, outputLines);
        mapItems.clear();
      }
    }
    
    String strAlign = m.getAlignment();
    if (!strAlign.equals(FontTFT.ALIGN_LEFT)) {
      template = tm.loadTemplate(ALIGN_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }

    if (m.isFrameEnabled()) {
      template = tm.loadTemplate(FRAME_EN_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }

    if (!m.getElementRef().isEmpty()) {
      template = tm.loadTemplate(ELEMENTREF_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }

    if (m.addScrollbar()) {
      template = tm.loadTemplate(LISTBOXSLIDER2_TEMPLATE);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    map.clear();
    template.clear();
    outputLines.clear();
    return sBd;   
  }
  
}