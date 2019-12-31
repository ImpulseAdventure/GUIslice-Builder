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

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.StringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.models.BoxModel;
import builder.models.ImageModel;
import builder.models.ImgButtonModel;
import builder.models.KeyPadModel;
import builder.models.KeyPadTextModel;
import builder.models.TxtButtonModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.NumKeyPadEditor;

/**
 * The Class ButtonCbPipe handles code generation
 * within the "Button Callback" tag of our source code.
 * 
 * This section creates button callbacks for supported UI Widgets
 * like TEXTBUTTON, IMAGEBUTTON, IMAGE, and Box.
 * 
 * @author Paul Conti
 * 
 */
public class ButtonCbPipe extends WorkFlowPipe {

  /** The Constants for tags. */
  private final static String BUTTONCB_TAG           = "//<Button Callback !Start!>";
  private final static String BUTTONCB_END_TAG       = "//<Button Callback !End!>";
  private final static String BUTTON_ENUMS_TAG       = "//<Button Enums !Start!>";
  private final static String BUTTON_ENUMS_END_TAG   = "//<Button Enums !End!>";
  
  /** The Constants for templates. */
  private final static String BUTTON_CB_TEMPLATE     = "<BUTTON_CB>";
  private final static String BUTTON_CASE_TEMPLATE   = "<BUTTON_CB_CASE>";
  private final static String BUTTON_CHGPG_TEMPLATE  = "<BUTTON_CB_CHGPAGE>";
  private final static String BUTTON_INPUT_TEMPLATE  = "<BUTTON_CB_INPUT>";
  private final static String BUTTON_SHOW_TEMPLATE   = "<BUTTON_CB_SHOWPOPUP>";
  private final static String BUTTON_HIDE_TEMPLATE   = "<BUTTON_CB_HIDEPOPUP>";
  
  /** The Constants for macros. */
  private final static String CALLBACK_MACRO         = "CALLBACK";
  private final static String ENUM_MACRO             = "COM-002";
  private final static String PAGE_ENUM_MACRO        = "COM-000";
  private final static String ELEMREF_MACRO          = "COM-019";
  private final static String KEY_ENUM_MACRO         = "KEY-002";
  private final static String KEY_ELEMREF_MACRO      = "KEY-019";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public ButtonCbPipe(CodeGenerator cg) {
    this.cg = cg;
  }
  
  /**
   * process
   *
   * For our Button Callback we have this modified process routine.
   * This version is to support removing the BUTTONCB_TAG and BUTTONCB_END_TAG
   * once we write out any button callbacks.
   *   
   * If this occurs we then place inside the newly generated callback the 
   * BUTTON_ENUMS_TAG and BUTTON_ENUMS_END_TAG between the "switch()" and "default"
   * statements so next time we only handle the individual enum CASE Statements.
   * This means we can detect existing enums and thus prevent us from
   * modifying them.  
   * This allows users to edit the callback with additional code
   * safely from us deleting it on a round trip editing session.
   *
   * NOTE: Notice that we also do not output our end tag by calling
   *       CodeUtils.readPassString(). 
   *
   * @see builder.codegen.pipes.Pipe#process(java.lang.Object, java.lang.Object)
   */
  @Override
  public StringBuilder process(StringBuilder input) throws CodeGenException {
    MY_TAG = BUTTONCB_TAG;
    MY_END_TAG = BUTTONCB_END_TAG;
    MY_ENUM_TAG = BUTTON_ENUMS_TAG;
    MY_ENUM_END_TAG = BUTTON_ENUMS_END_TAG;
    
    return super.processCB(input);
        
  }

  /**
   * doCbCommon
   *  Builds up a list of models that require a callback then calls 
   *  outputButtonCB with this list for the actual code generation.
   *
   * @param br
   *          the buffered reader of our project template input
   * @param sBd
   *          the StringBuilder object containing our project template
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @Override
  public void doCbCommon(BufferedReader br, StringBuilder sBd) throws IOException {
    // build up a list of widget models that have button callbacks enabled
    callbackList = new ArrayList<WidgetModel>();
    for (WidgetModel m : cg.getModels()) {
      if (m.getType().equals(EnumFactory.TEXTBUTTON)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.IMAGE) && ((ImageModel)m).isTouchEn()) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.NUMINPUT)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.TEXTINPUT)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.BOX) && ((BoxModel)m).isTouchEn()) {
        callbackList.add(m);
      }
    }
    super.doCbCommon(br, sBd);
  }
  
   /**
    * outputCB.
    *  This routine outputs the callback
    *
    * @param sBd
    *          the StringBuilder object containing our project template
    */
  @Override
  public void outputCB(StringBuilder sBd) {
    // setup for code generation
    KeyPadModel km = (KeyPadModel) NumKeyPadEditor.getInstance().getModel();
    KeyPadTextModel ktm = (KeyPadTextModel) AlphaKeyPadEditor.getInstance().getModel();
    tm = cg.getTemplateManager();
    // create a temporary string buffer to hold the case statements
    // we will add them all at once before we leave this routine
    StringBuilder sTemp = new StringBuilder();
    // create our callback section - start by opening our templates
    List<String> templateStandard  = tm.loadTemplate(BUTTON_CASE_TEMPLATE);
    List<String> templateChgPage   = tm.loadTemplate(BUTTON_CHGPG_TEMPLATE);
    List<String> templateInput     = tm.loadTemplate(BUTTON_INPUT_TEMPLATE);
    List<String> templateShowPopup = tm.loadTemplate(BUTTON_SHOW_TEMPLATE);
    List<String> templateHidePopup = tm.loadTemplate(BUTTON_HIDE_TEMPLATE);
    List<String> outputLines;
    Map<String, String> map = new HashMap<String, String>();
    for (WidgetModel m : callbackList) {
      map.clear();
      map.put(ENUM_MACRO, m.getEnum());
      if (m.getType().equals(EnumFactory.TEXTBUTTON)) {
        if (((TxtButtonModel) m).isChangePage()) {
          map.put(PAGE_ENUM_MACRO, ((TxtButtonModel) m).getChangePageEnum());
          outputLines = tm.expandMacros(templateChgPage, map);
        } else if (((TxtButtonModel) m).isShowPopup()) {
          map.put(PAGE_ENUM_MACRO, ((TxtButtonModel) m).getChangePageEnum());
          outputLines = tm.expandMacros(templateShowPopup, map);
        } else if (((TxtButtonModel) m).isHidePopup()) {
          outputLines = tm.expandMacros(templateHidePopup, map);
        } else {
          outputLines = tm.expandMacros(templateStandard, map);
        }
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) {
        if (((ImgButtonModel) m).isChangePage()) {
          map.put(PAGE_ENUM_MACRO, ((ImgButtonModel) m).getChangePageEnum());
          outputLines = tm.expandMacros(templateChgPage, map);
        } else if (((ImgButtonModel) m).isShowPopup()) {
          map.put(PAGE_ENUM_MACRO, ((ImgButtonModel) m).getChangePageEnum());
          outputLines = tm.expandMacros(templateShowPopup, map);
        } else if (((ImgButtonModel) m).isHidePopup()) {
          outputLines = tm.expandMacros(templateHidePopup, map);
        } else {
          outputLines = tm.expandMacros(templateStandard, map);
        }
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.NUMINPUT)) {
        map.put(ELEMREF_MACRO, m.getElementRef());
        map.put(KEY_ENUM_MACRO, km.getEnum());
        map.put(KEY_ELEMREF_MACRO, km.getElementRef());
        outputLines = tm.expandMacros(templateInput, map);
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.TEXTINPUT)) {
        map.put(ELEMREF_MACRO, m.getElementRef());
        map.put(KEY_ENUM_MACRO, ktm.getEnum());
        map.put(KEY_ELEMREF_MACRO, ktm.getElementRef());
        outputLines = tm.expandMacros(templateInput, map);
        tm.codeWriter(sTemp, outputLines);
      } else {
        outputLines = tm.expandMacros(templateStandard, map);
        tm.codeWriter(sTemp, outputLines);
      }
    }

    // now we place all of our new case statements inside our callback template
    map.clear();
    String sButtons = sTemp.toString();
    map.put(CALLBACK_MACRO, sButtons);
    List<String> templateLines = tm.loadTemplate(BUTTON_CB_TEMPLATE);
    outputLines = tm.expandMacros(templateLines, map);
    tm.codeWriter(sBd, outputLines);

  }

  /**
   * doEnums.
   *  The callback already exists so we just process what is  
   *  between the BUTTON_ENUMS_TAG and BUTTON_ENUMS_END_TAG here.
   *
   * @param br
   *          the buffered reader of our project template input
   * @param sBd
   *          the StringBuilder object containing our project template output
   */
  @Override
  public void doEnums(BufferedReader br, StringBuilder sBd) throws IOException {
    // setup for code generation
    KeyPadModel km = (KeyPadModel) NumKeyPadEditor.getInstance().getModel();
    KeyPadTextModel ktm = (KeyPadTextModel) AlphaKeyPadEditor.getInstance().getModel();
    tm = cg.getTemplateManager();

    // build up a list of widget models that have button callbacks enabled
    // also, save the enums into our enumMap for easier checking for existence.
    callbackList = new ArrayList<WidgetModel>();
    List<String> enumList = new ArrayList<String>();
    for (WidgetModel m : cg.getModels()) {
      if (m.getType().equals(EnumFactory.TEXTBUTTON)) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      } else if (m.getType().equals(EnumFactory.NUMINPUT)) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      } else if (m.getType().equals(EnumFactory.TEXTINPUT)) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      } else if (m.getType().equals(EnumFactory.IMAGE) && ((ImageModel)m).isTouchEn()) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      } else if (m.getType().equals(EnumFactory.BOX) && ((BoxModel)m).isTouchEn()) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      }
    }
// BUG 124 - Deletion of all UI Buttons leaves ENUM case statements in callback
//    if (callbackList.size() == 0)
//      return;
    
    // this removes duplicates and detects and removes deleted ui elements
    Map<String, String> enumMap = super.mapEnums(br, sBd, enumList);

    // now deal with our new enums    
    List<String> templateStandard = tm.loadTemplate(BUTTON_CASE_TEMPLATE);
    List<String> templateChgPage = tm.loadTemplate(BUTTON_CHGPG_TEMPLATE);
    List<String> templateInput     = tm.loadTemplate(BUTTON_INPUT_TEMPLATE);
    List<String> templateShowPopup = tm.loadTemplate(BUTTON_SHOW_TEMPLATE);
    List<String> templateHidePopup = tm.loadTemplate(BUTTON_HIDE_TEMPLATE);
    List<String> outputLines;
    Map<String, String> map = new HashMap<String,String>();
    for (WidgetModel m : callbackList) {
      /* search our enumMap for this enum. 
       * if found to have been dealt with already; skip it (value == "1"),      
       * otherwise expand the macros
       */
      if (enumMap.get(m.getEnum()).equals("0")) {
        map.clear();
        map.put(ENUM_MACRO, m.getEnum());
        if (m.getType().equals(EnumFactory.TEXTBUTTON)) {
          if (((TxtButtonModel)m).isChangePage()) {
            map.put(PAGE_ENUM_MACRO, ((TxtButtonModel)m).getChangePageEnum());
            outputLines = tm.expandMacros(templateChgPage, map);
          } else if (((TxtButtonModel)m).isShowPopup()) {
            map.put(PAGE_ENUM_MACRO, ((TxtButtonModel)m).getChangePageEnum());
            outputLines = tm.expandMacros(templateShowPopup, map);
          } else if (((TxtButtonModel)m).isHidePopup()) {
            outputLines = tm.expandMacros(templateHidePopup, map);
          } else {
            outputLines = tm.expandMacros(templateStandard, map);
          }
          tm.codeWriter(sBd, outputLines);
        } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) {
          if (((ImgButtonModel)m).isChangePage()) {
            map.put(PAGE_ENUM_MACRO, ((ImgButtonModel)m).getChangePageEnum());
            outputLines = tm.expandMacros(templateChgPage, map);
          } else if (((ImgButtonModel)m).isShowPopup()) {
            map.put(PAGE_ENUM_MACRO, ((ImgButtonModel)m).getChangePageEnum());
            outputLines = tm.expandMacros(templateShowPopup, map);
          } else if (((ImgButtonModel)m).isHidePopup()) {
            outputLines = tm.expandMacros(templateHidePopup, map);
          } else {
            outputLines = tm.expandMacros(templateStandard, map);
          }
          tm.codeWriter(sBd, outputLines);
        } else if (m.getType().equals(EnumFactory.NUMINPUT)) {
          map.put(ELEMREF_MACRO, m.getElementRef());
          map.put(KEY_ENUM_MACRO, km.getEnum());
          map.put(KEY_ELEMREF_MACRO, km.getElementRef());
          outputLines = tm.expandMacros(templateInput, map);
          tm.codeWriter(sBd, outputLines);
        } else if (m.getType().equals(EnumFactory.TEXTINPUT)) {
          map.put(ELEMREF_MACRO, m.getElementRef());
          map.put(KEY_ENUM_MACRO, ktm.getEnum());
          map.put(KEY_ELEMREF_MACRO, ktm.getElementRef());
          outputLines = tm.expandMacros(templateInput, map);
          tm.codeWriter(sBd, outputLines);
        } else {
          outputLines = tm.expandMacros(templateStandard, map);
          tm.codeWriter(sBd, outputLines);
        }
      }
    }
  }    

}
  