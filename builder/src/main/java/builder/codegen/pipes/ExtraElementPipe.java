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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.models.GraphModel;
import builder.models.ImgButtonModel;
import builder.models.ListBoxModel;
import builder.models.ProjectModel;
import builder.models.TextBoxModel;
import builder.models.WidgetModel;
import builder.views.PagePane;

/**
 * The Class ExtraElementPipe handles code generation
 * within the GUI_Extra_Elements tag of our source code.
 * 
 * This section creates storage for all extended elements, like XSlider,
 * XGraph, etc... 
 * 
 * @author Paul Conti
 * 
 */
public class ExtraElementPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String GUI_ELEMENT_TEMPLATE       = "<GUI_ELEMENT>";
  private final static String KEYPAD_ELEM_NUM_TEMPLATE   = "<KEYPAD_ELEM_NUM>";
  private final static String KEYPAD_ELEM_TEXT_TEMPLATE  = "<KEYPAD_ELEM_TEXT>";
  private final static String KEYPAD_PAGE_TEMPLATE       = "<KEYPAD_PAGE>";
  
  /** The Constants for macros. */
  private final static String STORAGE_MACRO          = "STORAGE";
  private final static String STRIP_ENUM_MACRO       = "STRIP_ENUM";
  private final static String STRIP_KEY_MACRO        = "STRIP_KEY";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public ExtraElementPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.EXTRA_ELEMENT_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.EXTRA_ELEMENT_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * Do section.
   *
   * @param sBd
   *          the StringBuilder object containing our project template
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    tm = cg.getTemplateManager();
    List<String> templateLines = tm.loadTemplate(GUI_ELEMENT_TEMPLATE);
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    for (PagePane p : cg.getPages()) {
      if (p.getPageType().equals(EnumFactory.PROJECT))
        continue;
      map.clear();
      map.put(STRIP_KEY_MACRO, CodeUtils.convertKey(p.getKey()));
      map.put(STRIP_ENUM_MACRO, CodeUtils.convertEnum(p.getEnum()));
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
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
      templateLines = tm.loadTemplate(KEYPAD_PAGE_TEMPLATE);
      map.clear();
      map.put(STORAGE_MACRO, EnumFactory.KEYPAD_PAGE_STORAGE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
   }
    if (bAddAlphaKeyPad) {
      templateLines = tm.loadTemplate(KEYPAD_PAGE_TEMPLATE);
      map.clear();
      map.put(STORAGE_MACRO, EnumFactory.ALPHAKEYPAD_PAGE_STORAGE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
    if (bAddNumKeyPad) {
      templateLines = tm.loadTemplate(KEYPAD_ELEM_NUM_TEMPLATE);
      map.clear();
      map.put(STORAGE_MACRO, EnumFactory.KEYPAD_ELEM_STORAGE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
    if (bAddAlphaKeyPad) {
      templateLines = tm.loadTemplate(KEYPAD_ELEM_TEXT_TEMPLATE);
      map.clear();
      map.put(STORAGE_MACRO, EnumFactory.ALPHAKEYPAD_ELEM_STORAGE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, (outputLines));
    }

    /*
     * Page storage is completed now move on to extended storage.
     * Note that APIs that are _P flash versions do not required
     * definition of extended storage here, so we skip those.
     * 
     * Keep in mind text and list boxes may have an embedded scrollbar (sliders) 
     * that need to be taken into account.
     */
    int nRows, nCols;
    String ref = "";
    String strCount;
    String strElement;
    for (WidgetModel m : cg.getModels()) {
      // check to flash API version
      if (m.useFlash())
        continue;
      if (m.getType().equals(EnumFactory.CHECKBOX)) {
        strElement = "gslc_tsXCheckbox";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_asXCheck" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.GRAPH)) {
        nRows = ((GraphModel)m).getNumRows();
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sGraph" + strCount;
        strElement = "gslc_tsXGraph";
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
        strElement = "int16_t";
        ref = "m_anGraphBuf" + strCount;
        sBd.append(String.format("%-32s%s[%d]; // NRows=%d", 
            strElement, ref, nRows, nRows));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.LISTBOX)) {
        int nSize = ((ListBoxModel)m).getStorageSz();
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sListbox" + CodeUtils.getKeyCount(m.getKey());
        strElement = "gslc_tsXListbox";
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
        strElement = "char";
        ref = "m_acListboxBuf" + strCount;
        sBd.append(String.format("%-32s","// - Note that XLISTBOX_BUF_OH_R is extra required per item"));
        sBd.append(System.lineSeparator());
        sBd.append(String.format("%-32s%s[%d + XLISTBOX_BUF_OH_R];", 
            strElement, ref, nSize));
        sBd.append(System.lineSeparator());
        if (((ListBoxModel)m).addScrollbar()) {
          strElement = "gslc_tsXSlider";
          strCount = CodeUtils.getKeyCount(m.getKey());
          ref = "m_sListScroll" + strCount;
          sBd.append(String.format("%-32s%s;", strElement, ref));
          sBd.append(System.lineSeparator());
        }
      }
      if (m.getType().equals(EnumFactory.PROGRESSBAR)) {
        strElement = "gslc_tsXProgress";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXBarGauge" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.RADIOBUTTON)) {
        strElement = "gslc_tsXCheckbox";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_asXRadio" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.RAMPGAUGE)) {
        strElement = "gslc_tsXRamp";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXRampGauge" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.RADIALGAUGE)) {
        strElement = "gslc_tsXRadial";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXRadialGauge" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.RINGGAUGE)) {
        strElement = "gslc_tsXRingGauge";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXRingGauge" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.SEEKBAR)) {
        strElement = "gslc_tsXSeekbar";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXSeekbar" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.SLIDER)) {
        strElement = "gslc_tsXSlider";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXSlider" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.SPINNER)) {
        strElement = "gslc_tsXSpinner";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sXSpinner" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.TEXTBOX)) {
        nRows = ((TextBoxModel)m).getNumTextRows();
        nCols = ((TextBoxModel)m).getNumTextColumns();
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sTextbox" + strCount;
        strElement = "gslc_tsXTextbox";
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
        strElement = "char";
        ref = "m_acTextboxBuf" + strCount;
        sBd.append(String.format("%-32s%s[%d]; // NRows=%d NCols=%d", 
            strElement, ref, nRows*nCols, nRows, nCols));
        sBd.append(System.lineSeparator());
        if (((TextBoxModel)m).addScrollbar()) {
          strElement = "gslc_tsXSlider";
          strCount = CodeUtils.getKeyCount(m.getKey());
          ref = "m_sTextScroll" + strCount;
          sBd.append(String.format("%-32s%s;", strElement, ref));
          sBd.append(System.lineSeparator());
        }
      }
      if (m.getType().equals(EnumFactory.TOGGLEBUTTON)) {
        strElement = "gslc_tsXTogglebtn";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_asXToggle" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
      if (m.getType().equals(EnumFactory.IMAGEBUTTON) && ((ImgButtonModel)m).isToggle()) {
        strElement = "gslc_tsXToggleImgbtn";
        strCount = CodeUtils.getKeyCount(m.getKey());
        ref = "m_sToggleImg" + strCount;
        sBd.append(String.format("%-32s%s;", strElement, ref));
        sBd.append(System.lineSeparator());
      } 
    }
    // output MAX String size
    ProjectModel pm = Controller.getProjectModel();
    strElement = "MAX_STR";
    sBd.append(System.lineSeparator());
    sBd.append(String.format("#define %-24s%d", strElement, pm.getMaxStr()));
    sBd.append(System.lineSeparator());
    sBd.append(System.lineSeparator());
  }

}
  