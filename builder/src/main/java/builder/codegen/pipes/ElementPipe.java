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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.models.ListBoxModel;
import builder.models.TextBoxModel;
import builder.models.WidgetModel;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class ElementPipe handles code generation
 * within the ElementDefines tag of our source code.
 * 
 * This section creates storage for all basic elements, like pages,
 * buttons, etc... Not extended elements. 
 * 
 * @author Paul Conti
 * 
 */
public class ElementPipe extends WorkFlowPipe {

  /** The Constants for tags. */
  private final static String ELEMENT_TAG            = "//<ElementDefines !Start!>";
  private final static String ELEMENT_END_TAG        = "//<ElementDefines !End!>";
  
  /** The Constants for templates. */
  private final static String PROGMEM_TEMPLATE           = "<PROGMEM>"; 
  private final static String DEFINE_ELEM_PAGE_TEMPLATE  = "<DEFINE_ELEM_PAGE>";
  private final static String DEFINE_ELEM_RAM_TEMPLATE   = "<DEFINE_ELEM_RAM>";
  private final static String DEFINE_ELEM_RAM_P_TEMPLATE = "<DEFINE_ELEM_RAM_P>";
  private final static String MAX_PAGE_TEMPLATE      = "<MAX_PAGE>";
  
  /** The Constants for macros. */
  private final static String COUNT_MACRO            = "COUNT";
  private final static String FLASH_MACRO            = "FLASH";
  private final static String NAME_MACRO             = "NAME";
  private final static String STRIP_ENUM_MACRO       = "STRIP_ENUM";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public ElementPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = ELEMENT_TAG;
    this.MY_END_TAG = ELEMENT_END_TAG;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    tm = cg.getTemplateManager();
    List<String> templateLines = new ArrayList<String>();
    List<String> outputLines = new ArrayList<String>();
    Map<String, String> map = new HashMap<String,String>();
    // figure out the MAX_PAGE define
    int nPages = cg.getPages().size();
    // we have hidden pages if users are referencing virtual keypads
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
    if (bAddNumKeyPad) nPages++;
    if (bAddAlphaKeyPad) nPages++;
    // output number of pages
    map.clear();
    map.put(NAME_MACRO, "MAX_PAGE");
    map.put(COUNT_MACRO, String.valueOf(nPages));
    templateLines = tm.loadTemplate(MAX_PAGE_TEMPLATE);
    outputLines = tm.expandMacros(templateLines, map);
    tm.codeWriter(sBd, outputLines);
    
    // build up a list of counts for out various UI widgets
    // Be sure and count _P functions stored in flash
    boolean bFirstFlash = true;  // this allows us to output progmem warning
    int elem_cnt = 0;
    int flash_cnt = 0;
    for (PagePane p : cg.getPages()) {
      flash_cnt = 0;
      elem_cnt = p.getWidgets().size();
      for (Widget w : p.getWidgets()) {
        if (w.useFlash()) {
          flash_cnt++;
        }
        if (w.getType().equals(EnumFactory.TEXTBOX) && ((TextBoxModel)w.getModel()).addScrollbar()) {
          elem_cnt += 2;  // TEXTBOX has embedded a wrapper Box and a scrollbar along with text box
        } else if (w.getType().equals(EnumFactory.LISTBOX) && ((ListBoxModel)w.getModel()).addScrollbar()) {
          elem_cnt += 2;  // LISTBOX has embedded a wrapper Box and a scrollbar along with list box
        }
      }
      if (flash_cnt > 0 && bFirstFlash) {
        // we need to output some warning comments about PROGMEM
        templateLines = tm.loadTemplate(PROGMEM_TEMPLATE);
        tm.codeWriter(sBd, templateLines);
        bFirstFlash = false;
      }
      map.clear();
      map.put(STRIP_ENUM_MACRO, CodeUtils.convertEnum(p.getEnum()));
      map.put(COUNT_MACRO, String.valueOf(elem_cnt));
      map.put(FLASH_MACRO, String.valueOf(flash_cnt));
      templateLines = tm.loadTemplate(DEFINE_ELEM_PAGE_TEMPLATE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
      
      if (flash_cnt > 0) {
        templateLines = tm.loadTemplate(DEFINE_ELEM_RAM_P_TEMPLATE);
      } else {
        templateLines = tm.loadTemplate(DEFINE_ELEM_RAM_TEMPLATE);
      }
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
      
  }

}
  