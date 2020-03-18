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

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.models.GeneralModel;
import builder.prefs.GeneralEditor;
import builder.models.ImageModel;
import builder.models.ImgButtonModel;
import builder.models.WidgetModel;

/**
 * The Class IncludesPipe handles code generation
 * within the "Includes" tag of our source code.
 * 
 * This section creates all of GUIslice include headers  
 * for any compound elements in this project file.
 * 
 * @author Paul Conti
 * 
 */
public class IncludesPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String ELEM_COMMENT_TEMPLATE     = "<ELEM_COMMENT>"; 
  private final static String ELEM_INCLUDE_TEMPLATE     = "<ELEM_INCLUDE>"; 
  private final static String WARNING_CONFIG_TEMPLATE   = "<WARNING_CONFIG>"; 
  private final static String WARNING_COMPOUND_TEMPLATE = "<WARNING_COMPOUND>"; 
  private final static String WARNING_SD_TEMPLATE       = "<WARNING_SD>"; 
  
  /** The Constants for macros. */
  private final static String WIDGET_MACRO           = "WIDGET";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public IncludesPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG     = Tags.TAG_PREFIX + Tags.INCLUDES_TAG + Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX + Tags.INCLUDES_TAG + Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {

    List<String> template;
    List<String> outputLines = null;
    tm = cg.getTemplateManager();
    Map<String, String> map = new HashMap<String,String>();

    /* scan our models for matching UI widgets and build up a list of 
     * their headers. While we are at it lets track if we need to 
     * output some warnings that optional features need to be enabled.
     */
    List<String> headerList = new ArrayList<String>();
    boolean bCompound = false;
    boolean bSD       = false;
    boolean bWarning  = false;
    
    for (WidgetModel m : cg.getModels()) {
      switch(m.getType()) {
        case EnumFactory.CHECKBOX:
          headerList.add("XCheckbox");
          break;
        case EnumFactory.GRAPH:
          headerList.add("XGraph");
          break;
        case EnumFactory.IMAGE:
          if (((ImageModel)m).getMemory().equals(ImageModel.SRC_SD)) {
            bSD = true;
            bWarning = true;
          }
          break;
        case EnumFactory.IMAGEBUTTON:
          if (((ImgButtonModel)m).getMemory().equals(ImageModel.SRC_SD)) {
            bSD = true;
            bWarning = true;
          }
          if (((ImgButtonModel)m).getSelMemory().equals(ImageModel.SRC_SD)) {
            bSD = true;
            bWarning = true;
          }
          break;
        case EnumFactory.LISTBOX:
          headerList.add("XListbox");
          break;
        case EnumFactory.NUMINPUT:
          headerList.add("XKeyPad_Num");
          bCompound = true;
          bWarning = true;
          break;
        case EnumFactory.PROGRESSBAR:
          headerList.add("XProgress");
          break;
        case EnumFactory.RADIOBUTTON:
          headerList.add("XCheckbox");
          break;
        case EnumFactory.RAMPGAUGE:
          headerList.add("XRamp");
          break;
        case EnumFactory.RADIALGAUGE:
          headerList.add("XRadial");
          break;
        case EnumFactory.RINGGAUGE:
          headerList.add("XRingGauge");
          break;
        case EnumFactory.SLIDER:
          headerList.add("XSlider");
          break;
        case EnumFactory.SPINNER:
          headerList.add("XSpinner");
          bCompound = true;
          bWarning = true;
          break;
        case EnumFactory.TEXTBOX:
          headerList.add("XTextbox");
          break;
        case EnumFactory.TEXTINPUT:
          headerList.add("XKeyPad_Alpha");
          bCompound = true;
          bWarning = true;
          break;
        default:
          break;
      }
      if (m.addScrollbar()) {
        headerList.add("XSlider");
      }
    }
    
    if (headerList.size() > 0) {
      // now sort our list of headers and remove any duplicates
      CodeUtils.sortListandRemoveDups(headerList);
  
      // we are ready to output our include files
      template = tm.loadTemplate(ELEM_COMMENT_TEMPLATE);
      tm.codeWriter(sBd, template);
      template = tm.loadTemplate(ELEM_INCLUDE_TEMPLATE);
      for (String s : headerList) {
        map.put(WIDGET_MACRO, s);
        outputLines = tm.expandMacros(template, map);
        tm.codeWriter(sBd, outputLines);
      }
    }
    
    // output any warnings required
    GeneralModel generalModel = ((GeneralModel)GeneralEditor.getInstance().getModel());
    if (generalModel.useBackgroundImage() && 
        generalModel.getBackgroundMemory().equals(GeneralModel.SRC_SD)) 
      bSD = true;
    if (bWarning) {
      template = tm.loadTemplate(WARNING_CONFIG_TEMPLATE);
      tm.codeWriter(sBd, template);
      if (bCompound) {
        template = tm.loadTemplate(WARNING_COMPOUND_TEMPLATE);
        tm.codeWriter(sBd, template);
      }
      if (bSD) {
        template = tm.loadTemplate(WARNING_SD_TEMPLATE);
        tm.codeWriter(sBd, template);
      }
    }
  }

}
  