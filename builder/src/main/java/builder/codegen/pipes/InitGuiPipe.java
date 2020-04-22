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
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.codegen.blocks.BoxCodeBlock;
import builder.codegen.blocks.CheckBoxCodeBlock;
import builder.codegen.blocks.GraphCodeBlock;
import builder.codegen.blocks.ImageCodeBlock;
import builder.codegen.blocks.ImgButtonCodeBlock;
import builder.codegen.blocks.LineCodeBlock;
import builder.codegen.blocks.ListBoxCodeBlock;
import builder.codegen.blocks.NumberInputCodeBlock;
import builder.codegen.blocks.ProgressBarCodeBlock;
import builder.codegen.blocks.RadialGaugeCodeBlock;
import builder.codegen.blocks.RadioButtonCodeBlock;
import builder.codegen.blocks.RampGaugeCodeBlock;
import builder.codegen.blocks.RingGaugeCodeBlock;
import builder.codegen.blocks.SliderCodeBlock;
import builder.codegen.blocks.SpinnerCodeBlock;
import builder.codegen.blocks.TextBoxCodeBlock;
import builder.codegen.blocks.TextCodeBlock;
import builder.codegen.blocks.TextInputCodeBlock;
import builder.codegen.blocks.TxtButtonCodeBlock;
import builder.codegen.flash.Box_P_CodeBlock;
import builder.codegen.flash.CheckBox_P_CodeBlock;
import builder.codegen.flash.NumberInput_P_CodeBlock;
import builder.codegen.flash.ProgressBar_P_CodeBlock;
import builder.codegen.flash.RadioButton_P_CodeBlock;
import builder.codegen.flash.TextInput_P_CodeBlock;
import builder.codegen.flash.Text_P_CodeBlock;
import builder.codegen.flash.TxtButton_P_CodeBlock;
import builder.common.ColorFactory;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.models.KeyPadTextModel;
import builder.models.ProjectModel;
import builder.models.KeyPadModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.NumKeyPadEditor;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class InitGuiPipe handles code generation
 * within the "InitGUI" tag of our source code.
 * 
 * This section creates all of GUIslice creation API calls 
 * for supported UI Widgets.
 * 
 * @author Paul Conti
 * 
 */
public class InitGuiPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String BACKGROUND_TEMPLATE       = "<BACKGROUND>";
  private final static String BACKGROUND_IMAGE_TEMPLATE = "<BACKGROUND_IMAGE>";
  private final static String KEYPAD_CONFIG_TEMPLATE    = "<KEYPAD_CONFIG>"; 
  private final static String KEYPAD_TEXT_TEMPLATE      = "<KEYPAD_TEXT>"; 
  private final static String PAGEADD_TEMPLATE          = "<PAGEADD>"; 
  private final static String PAGEADDKEYPAD_TEMPLATE    = "<PAGEADDKEYPAD>"; 
  private final static String PAGEBASE_TEMPLATE         = "<PAGEBASE>";
  private final static String PAGECOMMENT_TEMPLATE      = "<PAGECOMMENT>";
  private final static String PAGECUR_TEMPLATE          = "<PAGECUR>";
  
  /** The Constants for macros. */
  private final static String BACKGROUND_COLOR_MACRO = "BACKGROUND_COLOR";
  private final static String BUTTONSZ_MACRO         = "BUTTONSZ";
  private final static String ELEMREF_MACRO          = "ELEMREF";
  private final static String ENUM_MACRO             = "WIDGET_ENUM";
  private final static String FLOAT_EN_MACRO         = "FLOAT_EN";
  private final static String FONT_ID_MACRO          = "FONT_ID";
//  private final static String GAPX_MACRO             = "GAPX";
//  private final static String GAPY_MACRO             = "GAPY";
  private final static String PAGE_ENUM_MACRO        = "PAGE_ENUM";
  private final static String ROUND_EN_MACRO         = "ROUND_EN";
  private final static String SIGN_EN_MACRO          = "SIGN_EN";
  private final static String STORAGE_MACRO          = "STORAGE";
  private final static String STRIP_ENUM_MACRO       = "STRIP_ENUM";
  private final static String STRIP_KEY_MACRO        = "STRIP_KEY";
  private final static String X_MACRO                = "X";
  private final static String Y_MACRO                = "Y";
  private final static String DEFINE_MACRO           = "IMG-101";
  private final static String MEMORY_MACRO           = "IMG-109";
  private final static String FORMAT_MACRO           = "IMG-102";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public InitGuiPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.INITGUI_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.INITGUI_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    tm = cg.getTemplateManager();
    List<String> templateLines = tm.loadTemplate(PAGEADD_TEMPLATE);
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    String basePageEnum = "";
    String mainPageEnum = "";
    // first output base page, if any
    for (PagePane p : cg.getPages()) {
      if (p.getPageType().equals(EnumFactory.BASEPAGE)) {
        map.clear();
        map.put(PAGE_ENUM_MACRO, p.getEnum());
        map.put(STRIP_KEY_MACRO, CodeUtils.convertKey(p.getKey()));
        map.put(STRIP_ENUM_MACRO, CodeUtils.convertEnum(p.getEnum()));
        outputLines = tm.expandMacros(templateLines, map);
        tm.codeWriter(sBd, outputLines);
        basePageEnum = p.getEnum();
      }
    }
    // now standard pages
    for (PagePane p : cg.getPages()) {
      if (!p.getPageType().equals(EnumFactory.BASEPAGE) &&
          !p.getPageType().equals(EnumFactory.PROJECT)) {
        map.clear();
        map.put(PAGE_ENUM_MACRO, p.getEnum());
        map.put(STRIP_KEY_MACRO, CodeUtils.convertKey(p.getKey()));
        map.put(STRIP_ENUM_MACRO, CodeUtils.convertEnum(p.getEnum()));
        outputLines = tm.expandMacros(templateLines, map);
        tm.codeWriter(sBd, outputLines);
        if (p.getKey().equals("Page$1"))
          mainPageEnum = p.getEnum();
      }
    }
    // now handle any keypads
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
      templateLines = tm.loadTemplate(PAGEADDKEYPAD_TEMPLATE);
      map.clear();
      map.put(PAGE_ENUM_MACRO, EnumFactory.KEYPAD_PAGE_ENUM);
      map.put(STORAGE_MACRO, EnumFactory.KEYPAD_PAGE_STORAGE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
   }
   if (bAddAlphaKeyPad) {
      templateLines = tm.loadTemplate(PAGEADDKEYPAD_TEMPLATE);
      map.clear();
      map.put(PAGE_ENUM_MACRO, EnumFactory.ALPHAKEYPAD_PAGE_ENUM);
      map.put(STORAGE_MACRO, EnumFactory.ALPHAKEYPAD_PAGE_STORAGE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
    map.clear();
    
    // if we found a base page we need to register it.
    if (!basePageEnum.isEmpty()) {
      templateLines = tm.loadTemplate(PAGEBASE_TEMPLATE);
      map.clear();
      map.put(PAGE_ENUM_MACRO, basePageEnum);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    // now force 'Page$1' to be the main page
    templateLines = tm.loadTemplate(PAGECUR_TEMPLATE);
    map.clear();
    map.put(PAGE_ENUM_MACRO, mainPageEnum);
    outputLines = tm.expandMacros(templateLines, map);
    tm.codeWriter(sBd, outputLines);
    
    // deal with background
    ProjectModel pm = Controller.getInstance().getProjectModel();
    if (pm.useBackgroundImage()) {
      map.clear();
      map.put(MEMORY_MACRO, pm.getBackgroundMemory());
      map.put(DEFINE_MACRO, pm.getBackgroundDefine());
      map.put(FORMAT_MACRO, pm.getBackgroundFormat());
      templateLines = tm.loadTemplate(BACKGROUND_IMAGE_TEMPLATE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    } else {
      Color bCol = (Color) pm.getBackgroundColor();
      String color = ColorFactory.getInstance().colorAsString(bCol);
      map.clear();
      map.put(BACKGROUND_COLOR_MACRO, color);
      templateLines = tm.loadTemplate(BACKGROUND_TEMPLATE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    // output ui widget creation APIs for each page
    for (PagePane p : cg.getPages()) {
      if (!p.getPageType().equals(EnumFactory.PROJECT)) {
        map.clear();
        map.put(PAGE_ENUM_MACRO, p.getEnum());
        templateLines = tm.loadTemplate(PAGECOMMENT_TEMPLATE);
        outputLines = tm.expandMacros(templateLines, map);
        tm.codeWriter(sBd, outputLines);
        for (Widget w : p.getWidgets()) {
          outputAPI(sBd, p.getEnum(), w.getModel());
        }
      }
    }
    
    // output keypad configurations
    if (bAddNumKeyPad) {
      KeyPadModel m = (KeyPadModel)NumKeyPadEditor.getInstance().getModel();
      map.clear();
      map.put(PAGE_ENUM_MACRO, EnumFactory.KEYPAD_PAGE_ENUM);
      templateLines = tm.loadTemplate(PAGECOMMENT_TEMPLATE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
      templateLines = tm.loadTemplate(KEYPAD_CONFIG_TEMPLATE);
      map.clear();
      map.put(ELEMREF_MACRO, EnumFactory.KEYPAD_ELEMREF);
      map.put(PAGE_ENUM_MACRO, m.getEnum());
      map.put(ENUM_MACRO, EnumFactory.KEYPAD_ELEM_ENUM);
      map.put(STORAGE_MACRO, EnumFactory.KEYPAD_ELEM_STORAGE);
      map.put(FLOAT_EN_MACRO, String.valueOf(m.isFloatingPointEn()));
      map.put(SIGN_EN_MACRO, String.valueOf(m.isSignEn()));
      map.put(BUTTONSZ_MACRO, String.valueOf(m.getButtonSz()));
      map.put(ROUND_EN_MACRO, String.valueOf(m.isRoundedEn()));
//      map.put(GAPX_MACRO, String.valueOf(m.getButtonGapX()));
//      map.put(GAPY_MACRO, String.valueOf(m.getButtonGapY()));
      map.put(X_MACRO, String.valueOf(m.getX()));
      map.put(Y_MACRO, String.valueOf(m.getY()));
      map.put(FONT_ID_MACRO, String.valueOf(m.getFontEnum()));
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }

    if (bAddAlphaKeyPad) {
      KeyPadTextModel m = (KeyPadTextModel)AlphaKeyPadEditor.getInstance().getModel();
      map.clear();
      map.put(PAGE_ENUM_MACRO, EnumFactory.ALPHAKEYPAD_PAGE_ENUM);
      templateLines = tm.loadTemplate(PAGECOMMENT_TEMPLATE);
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
      templateLines = tm.loadTemplate(KEYPAD_TEXT_TEMPLATE);
      map.clear();
      map.put(ELEMREF_MACRO, EnumFactory.ALPHAKEYPAD_ELEMREF);
      map.put(PAGE_ENUM_MACRO, m.getEnum());
      map.put(ENUM_MACRO, EnumFactory.ALPHAKEYPAD_ELEM_ENUM);
      map.put(STORAGE_MACRO, EnumFactory.ALPHAKEYPAD_ELEM_STORAGE);
      map.put(BUTTONSZ_MACRO, String.valueOf(m.getButtonSz()));
      map.put(ROUND_EN_MACRO, String.valueOf(m.isRoundedEn()));
//      map.put(GAPX_MACRO, String.valueOf(m.getButtonGapX()));
//      map.put(GAPY_MACRO, String.valueOf(m.getButtonGapY()));
      map.put(X_MACRO, String.valueOf(m.getX()));
      map.put(Y_MACRO, String.valueOf(m.getY()));
      map.put(FONT_ID_MACRO, String.valueOf(m.getFontEnum()));
      outputLines = tm.expandMacros(templateLines, map);
      tm.codeWriter(sBd, outputLines);
    }
  }
  
   /**
    * Output API.
    *
    * @param sBd
   *          the StringBuilder object containing our project template
    * @param pageEnum
    *          the page enum
    * @param m
    *          the m
    * @return the <code>string builder</code> object
    */
  private StringBuilder outputAPI(StringBuilder sBd, String pageEnum, WidgetModel m) {
//    System.out.println("outputAPI page: " + pageEnum + " widget: " + m.getType());
    switch(m.getType()) {
      case EnumFactory.BOX:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          Box_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          BoxCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      case EnumFactory.CHECKBOX:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          CheckBox_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          CheckBoxCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      case EnumFactory.GRAPH:
        GraphCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.IMAGE:
        ImageCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.IMAGEBUTTON:
        ImgButtonCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.LINE:
        LineCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.LISTBOX:
        ListBoxCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.NUMINPUT:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          NumberInput_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          NumberInputCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      case EnumFactory.PROGRESSBAR:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          ProgressBar_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          ProgressBarCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      case EnumFactory.RADIOBUTTON:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          RadioButton_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          RadioButtonCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      case EnumFactory.RAMPGAUGE:
        RampGaugeCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.RADIALGAUGE:
        RadialGaugeCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.RINGGAUGE:
        RingGaugeCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.SLIDER:
        SliderCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.SPINNER:
        SpinnerCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.TEXT:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          Text_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          TextCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      case EnumFactory.TEXTBOX:
        TextBoxCodeBlock.process(cg, tm, sBd, pageEnum, m);
        break;
      case EnumFactory.TEXTBUTTON:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          TxtButton_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          TxtButtonCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      case EnumFactory.TEXTINPUT:
        if (m.useFlash() && !cg.getTargetPlatform().equals("linux")) {
          TextInput_P_CodeBlock.process(cg, tm, sBd, pageEnum, m);
        } else {
          TextInputCodeBlock.process(cg, tm, sBd, pageEnum, m);
        }
        break;
      default:
        break;
    }
    
    return sBd;
  }

}
  