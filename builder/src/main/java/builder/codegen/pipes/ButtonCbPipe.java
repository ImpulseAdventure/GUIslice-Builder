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

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import builder.Builder;
import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.models.BoxModel;
import builder.models.GeneralModel;
import builder.models.ImageModel;
import builder.models.ImgButtonModel;
import builder.models.KeyPadModel;
import builder.models.KeyPadTextModel;
import builder.models.TxtButtonModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.GeneralEditor;
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

  /** The Constants for templates. */
  private final static String BUTTON_CB_TEMPLATE     = "<BUTTON_CB>";
  private final static String BUTTON_CASE_TEMPLATE   = "<BUTTON_CB_CASE>";
  private final static String BUTTON_CHGPG_TEMPLATE  = "<BUTTON_CB_CHGPAGE>";
  private final static String BUTTON_HIDE_TEMPLATE   = "<BUTTON_CB_HIDEPOPUP>";
  private final static String BUTTON_INPUT_TEMPLATE  = "<BUTTON_CB_INPUT>";
  private final static String BUTTON_SHOW_TEMPLATE   = "<BUTTON_CB_SHOWPOPUP>";
  private final static String BUTTON_TOGGLE_TEMPLATE = "<BUTTON_CB_TOGGLE>";
  
  /** The Constants for macros. */
  private final static String CALLBACK_MACRO         = "CALLBACK";
  private final static String ENUM_MACRO             = "COM-002";
  private final static String ELEMREF_MACRO          = "COM-019";
  private final static String KEY_ENUM_MACRO         = "KEY-002";
  private final static String KEY_ELEMREF_MACRO      = "KEY-019";
  private final static String JUMPPAGE_ENUM_MACRO    = "TBNT-101";
  private final static String POPUPPAGE_ENUM_MACRO   = "TBTN-104";
  
  /** The template manager. */
  TemplateManager tm = null;

  /** The regex word patterns */
  private final static Pattern LTRIM = Pattern.compile("^\\s+");
  private final static String EMPTY_STRING = "";

  /** Button Case Statement types */
  private final static int CT_UNDEFINED  = 0;
  private final static int CT_STANDARD   = 1;
  private final static int CT_CHGPAGE    = 2;
  private final static int CT_INPUTNUM   = 3;
  private final static int CT_INPUTTXT   = 4;
  private final static int CT_SHOWPOPUP  = 5;
  private final static int CT_HIDEPOPUP  = 6;
  private final static int CT_UPDINPUTNUM = 7;
  private final static int CT_UPDINPUTTXT = 8;
  private final static int CT_TOGGLEBTN   = 9;

  /** The case statement map. */
  HashMap<String, Integer> caseMap;
  
  /** The list of case statements. */
  List<String>[] listOfCases = null;

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
    this.MY_TAG     = Tags.TAG_PREFIX + Tags.BUTTONCB_TAG + Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX + Tags.BUTTONCB_TAG + Tags.TAG_SUFFIX_END;
    this.MY_ENUM_TAG     = Tags.TAG_PREFIX + Tags.BUTTON_ENUMS_TAG + Tags.TAG_SUFFIX_START;
    this.MY_ENUM_END_TAG = Tags.TAG_PREFIX + Tags.BUTTON_ENUMS_TAG + Tags.TAG_SUFFIX_END;
    
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
      } else if (m.getType().equals(EnumFactory.TOGGLEBUTTON)) {
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
    List<String> templateHidePopup = tm.loadTemplate(BUTTON_HIDE_TEMPLATE);
    List<String> templateInput     = tm.loadTemplate(BUTTON_INPUT_TEMPLATE);
    List<String> templateShowPopup = tm.loadTemplate(BUTTON_SHOW_TEMPLATE);
    List<String> templateToggleBtn = tm.loadTemplate(BUTTON_TOGGLE_TEMPLATE);
    List<String> outputLines;
    Map<String, String> map = new HashMap<String, String>();
    for (WidgetModel m : callbackList) {
      map.clear();
      map.put(ENUM_MACRO, m.getEnum());
      if (m.getType().equals(EnumFactory.TEXTBUTTON)) {
        if (((TxtButtonModel) m).getJumpPage() != null &&
            !((TxtButtonModel) m).getJumpPage().isEmpty()) {
          map.put(JUMPPAGE_ENUM_MACRO, ((TxtButtonModel) m).getJumpPage());
          outputLines = tm.expandMacros(templateChgPage, map);
        } else if (((TxtButtonModel) m).getPopupPage() != null && 
              !((TxtButtonModel) m).getPopupPage().isEmpty()) {
          map.put(POPUPPAGE_ENUM_MACRO, ((TxtButtonModel) m).getPopupPage());
          outputLines = tm.expandMacros(templateShowPopup, map);
        } else if (((TxtButtonModel) m).isHidePopup()) {
          outputLines = tm.expandMacros(templateHidePopup, map);
        } else {
          outputLines = tm.expandMacros(templateStandard, map);
        }
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) {
        if (((ImgButtonModel) m).getJumpPage() != null && 
            !((ImgButtonModel) m).getJumpPage().isEmpty()) {
          map.put(JUMPPAGE_ENUM_MACRO, ((ImgButtonModel) m).getJumpPage());
          outputLines = tm.expandMacros(templateChgPage, map);
        } else if (((ImgButtonModel) m).getPopupPage() != null && 
            !((ImgButtonModel) m).getPopupPage().isEmpty()) {
          map.put(POPUPPAGE_ENUM_MACRO, ((ImgButtonModel) m).getPopupPage());
          outputLines = tm.expandMacros(templateShowPopup, map);
        } else if (((ImgButtonModel) m).isHidePopup()) {
          outputLines = tm.expandMacros(templateHidePopup, map);
        } else {
          outputLines = tm.expandMacros(templateStandard, map);
        }
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.TOGGLEBUTTON)) {
        map.put(ELEMREF_MACRO, m.getElementRef());
        outputLines = tm.expandMacros(templateToggleBtn, map);
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
  @SuppressWarnings("unchecked")
  @Override
  public void doEnums(BufferedReader br, StringBuilder sBd) throws IOException {
    // setup for code generation
    KeyPadModel km = (KeyPadModel) NumKeyPadEditor.getInstance().getModel();
    KeyPadTextModel ktm = (KeyPadTextModel) AlphaKeyPadEditor.getInstance().getModel();
    tm = cg.getTemplateManager();
    GeneralModel gm = (GeneralModel) GeneralEditor.getInstance().getModel();
    // do we support round trip edits?
    boolean bPreserveCode = gm.isPreserveButtonCallbacks();
    
    listOfCases = new ArrayList[256];

    // setup our templates for outputs   
    List<String> templateStandard = tm.loadTemplate(BUTTON_CASE_TEMPLATE);
    List<String> templateChgPage = tm.loadTemplate(BUTTON_CHGPG_TEMPLATE);
    List<String> templateHidePopup = tm.loadTemplate(BUTTON_HIDE_TEMPLATE);
    List<String> templateInput     = tm.loadTemplate(BUTTON_INPUT_TEMPLATE);
    List<String> templateShowPopup = tm.loadTemplate(BUTTON_SHOW_TEMPLATE);
    List<String> templateToggleBtn = tm.loadTemplate(BUTTON_TOGGLE_TEMPLATE);
    List<String> outputLines;
    Map<String, String> map = new HashMap<String,String>();

    // build up a list of widget models that have button callbacks enabled
    callbackList = new ArrayList<WidgetModel>();
    for (WidgetModel m : cg.getModels()) {
      if (m.getType().equals(EnumFactory.TEXTBUTTON)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.TOGGLEBUTTON)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.NUMINPUT)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.TEXTINPUT)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.IMAGE) && ((ImageModel)m).isTouchEn()) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.BOX) && ((BoxModel)m).isTouchEn()) {
        callbackList.add(m);
      }
    }
    /* our callback section already exists - read it into a buffers so we can scan 
     * it for existing Enum case statements. 
     * This will allow us to determine if a case statement for an ENUM needs updating.
     */
    storeCaseStatements(br);
    /* now search the callback list and for each model
     * check for an existing case statement in our saved buffers.
     * If none, just do the output.
     * Otherwise, determine type of case statement and if it needs to updated. 
     * No need to worry about any that need deletion since we only output
     * those in our callbackList.
     */
    WidgetModel m = null;
    String e = "";
    for (int n = 0; n < callbackList.size(); n++) {
      // grab our model and enum
      m = callbackList.get(n);
      e = m.getEnum();
      boolean bNeedOutput = true;
      String replacement = "";
      // now examine the model and determine the case type to be generated
      CaseInfo modelInfo = getCaseType(m);
      // lookup our enum to see if we have an existing case statement
      int idx = -1;
      if (bPreserveCode) {
        idx = findCaseStatement(e);
      }
      if (idx != -1) {
        /*
         * we do have an existing case statement so now the fun begins need to parse it
         * for case type and compare it to what our model now wants if everything
         * matches just output the stored source code in case user has modified it. This
         * makes round trip edits possible.
         */
        CaseInfo oldInfo = parseCaseType(e, idx);
        switch (modelInfo.getCaseType()) {
        case CT_UNDEFINED: 
          break;
        case CT_STANDARD:
          // output what we have stored
          outputLines = listOfCases[idx];
          tm.codeWriter(sBd, outputLines);
          bNeedOutput = false;
          break;
        case CT_CHGPAGE:
          if (oldInfo.getCaseType() == CT_CHGPAGE) {
            if (oldInfo.getPageEnum().equals(modelInfo.getPageEnum())) {
              outputLines = listOfCases[idx];
              tm.codeWriter(sBd, outputLines);
              bNeedOutput = false;
            } else {
              if (oldInfo.getLineNo() > -1) { 
                // output what is stored but replace the one line that is wrong
                replacement = String.format("        gslc_SetPageCur(&m_gui, %s);", modelInfo.getPageEnum());
                outputLines = listOfCases[idx];
                tm.codeReplaceLine(sBd, outputLines, replacement, 
                    oldInfo.getLineNo(), oldInfo.getLineNo());
                bNeedOutput = false;
              }
            }
          } else if (oldInfo.getCaseType() == CT_SHOWPOPUP) {
            if (oldInfo.getLineNo() > -1) { 
              // output what is stored but replace the one line that is wrong
              replacement = String.format("        gslc_SetPageCur(&m_gui, %s);", modelInfo.getPageEnum());
              outputLines = listOfCases[idx];
              tm.codeReplaceLine(sBd, outputLines, replacement, 
                  oldInfo.getLineNo(), oldInfo.getLineNo());
              bNeedOutput = false;
            }
          } else if (oldInfo.getCaseType() == CT_STANDARD) {
            if (oldInfo.getLineNo() > -1) { 
              // output what is stored but replace the one line that is wrong
              replacement = String.format("        gslc_SetPageCur(&m_gui, %s);", modelInfo.getPageEnum());
              outputLines = listOfCases[idx];
              tm.codeAppendLine(sBd, outputLines, replacement);
              bNeedOutput = false;
            }
          }
          break;
        case CT_INPUTNUM:
          if (oldInfo.getCaseType() == CT_INPUTNUM) {
            if (oldInfo.getElementRef().equals(modelInfo.getElementRef())) {
              outputLines = listOfCases[idx];
              tm.codeWriter(sBd, outputLines);
              bNeedOutput = false;
            }
          }
          if (oldInfo.getCaseType() == CT_UPDINPUTNUM) {
            if (oldInfo.getElementRef().equals(modelInfo.getElementRef())) {
              outputLines = listOfCases[idx];
              // our old input template was 4 lines long, if match reduce to one line
              if (oldInfo.getLineNo()+3 <= oldInfo.getStopNo()) {
                replacement = String.format("        gslc_ElemXKeyPadInputAsk(&m_gui, %s, %s, %s);", 
                  oldInfo.getKeyElementRef(),oldInfo.getPageEnum(),oldInfo.getElementRef());
                tm.codeReplaceLine(sBd, outputLines, replacement, 
                    oldInfo.getLineNo(), oldInfo.getStopNo());
              } else {
                tm.codeWriter(sBd, outputLines);
              }
              bNeedOutput = false;
            }
          }
          break;
        case CT_INPUTTXT:
          if (oldInfo.getCaseType() == CT_INPUTTXT) {
            if (oldInfo.getElementRef().equals(modelInfo.getElementRef())) {
              outputLines = listOfCases[idx];
              tm.codeWriter(sBd, outputLines);
              bNeedOutput = false;
            }
          }
          if (oldInfo.getCaseType() == CT_UPDINPUTTXT) {
            if (oldInfo.getElementRef().equals(modelInfo.getElementRef())) {
              outputLines = listOfCases[idx];
              // our old input template was 4 lines long, if match reduce to one line
              if (oldInfo.getLineNo()+3 <= oldInfo.getStopNo()) {
                replacement = String.format("        gslc_ElemXKeyPadInputAsk(&m_gui, %s, %s, %s);", 
                  oldInfo.getKeyElementRef(),oldInfo.getPageEnum(),oldInfo.getElementRef());
                tm.codeReplaceLine(sBd, outputLines, replacement, 
                    oldInfo.getLineNo(), oldInfo.getStopNo());
              } else {
                tm.codeWriter(sBd, outputLines);
              }
              bNeedOutput = false;
            }
          }
          break;
        case CT_SHOWPOPUP:
          if (oldInfo.getCaseType() == CT_SHOWPOPUP) {
            if (oldInfo.getPageEnum().equals(modelInfo.getPageEnum())) {
              outputLines = listOfCases[idx];
              tm.codeWriter(sBd, outputLines);
              bNeedOutput = false;
            } else {
              if (oldInfo.getLineNo() > -1) { 
                // output what is stored but replace the one line that is wrong
                replacement = String.format("        gslc_PopupShow(&m_gui, %s);", modelInfo.getPageEnum());
                outputLines = listOfCases[idx];
                tm.codeReplaceLine(sBd, outputLines, replacement, 
                    oldInfo.getLineNo(), oldInfo.getLineNo());
                bNeedOutput = false;
              }
            }
          } else if (oldInfo.getCaseType() == CT_CHGPAGE) {
            if (oldInfo.getLineNo() > -1) { 
              // output what is stored but replace the one line that is wrong
              replacement = String.format("        gslc_PopupShow(&m_gui, %s);", modelInfo.getPageEnum());
              outputLines = listOfCases[idx];
              tm.codeReplaceLine(sBd, outputLines, replacement, 
                  oldInfo.getLineNo(), oldInfo.getLineNo());
              bNeedOutput = false;
            }
          } else if (oldInfo.getCaseType() == CT_STANDARD) {
            if (oldInfo.getLineNo() > -1) { 
              // output what is stored but replace the one line that is wrong
              replacement = String.format("        gslc_PopupShow(&m_gui, %s);", modelInfo.getPageEnum());
              outputLines = listOfCases[idx];
              tm.codeAppendLine(sBd, outputLines, replacement);
              bNeedOutput = false;
            }
          }
          break;
        case CT_HIDEPOPUP:
          if (oldInfo.getCaseType() == CT_HIDEPOPUP) {
            outputLines = listOfCases[idx];
            tm.codeWriter(sBd, outputLines);
            bNeedOutput = false;
          }
          break;
        case CT_TOGGLEBTN:
          if (oldInfo.getCaseType() == CT_TOGGLEBTN) {
            if (oldInfo.getElementRef().equals(modelInfo.getElementRef())) {
              outputLines = listOfCases[idx];
              tm.codeWriter(sBd, outputLines);
              bNeedOutput = false;
            }
          }
          break;
        }
      }
      if (bNeedOutput) {
        // No existing case statement or we need to regenerate it.
        map.clear();
        map.put(ENUM_MACRO, modelInfo.getKey());
        switch (modelInfo.getCaseType()) {
        case CT_UNDEFINED: // better not happen
          break;
        case CT_STANDARD:
          outputLines = tm.expandMacros(templateStandard, map);
          tm.codeWriter(sBd, outputLines);
          break;
        case CT_CHGPAGE:
          map.put(JUMPPAGE_ENUM_MACRO, modelInfo.getPageEnum());
          outputLines = tm.expandMacros(templateChgPage, map);
          tm.codeWriter(sBd, outputLines);
          break;
        case CT_INPUTNUM:
          map.put(ELEMREF_MACRO, modelInfo.getElementRef());
          map.put(KEY_ENUM_MACRO, km.getEnum());
          map.put(KEY_ELEMREF_MACRO, km.getElementRef());
          outputLines = tm.expandMacros(templateInput, map);
          tm.codeWriter(sBd, outputLines);
          break;
        case CT_INPUTTXT:
          map.put(ELEMREF_MACRO, modelInfo.getElementRef());
          map.put(KEY_ENUM_MACRO, ktm.getEnum());
          map.put(KEY_ELEMREF_MACRO, ktm.getElementRef());
          outputLines = tm.expandMacros(templateInput, map);
          tm.codeWriter(sBd, outputLines);
          break;
        case CT_SHOWPOPUP:
          map.put(POPUPPAGE_ENUM_MACRO, modelInfo.getPageEnum());
          outputLines = tm.expandMacros(templateShowPopup, map);
          tm.codeWriter(sBd, outputLines);
          break;
        case CT_HIDEPOPUP:
          outputLines = tm.expandMacros(templateHidePopup, map);
          tm.codeWriter(sBd, outputLines);
          break;
        case CT_TOGGLEBTN:
          map.put(ELEMREF_MACRO, modelInfo.getElementRef());
          outputLines = tm.expandMacros(templateToggleBtn, map);
          tm.codeWriter(sBd, outputLines);
          break;
        }
      }
    }

  }    

  /**
   * storeCaseStatements - read all existing case statements in the source code
   * and store them in buffers.  Keep a map of them for fast lookups.
   *
   * @param br
   *          the BufferedReader br
   * @throws IOException
   *           the code gen exception
   */
  public void storeCaseStatements(BufferedReader br) throws IOException {
    caseMap = new HashMap<String, Integer>(64);
    String scan = "";
    String enumName = "";
    int i = 0;
    String sTestTag= "";
    // read everything into an array first so we can go backwards if needed
    List<String> inputLines = new ArrayList<String>();
    while((scan = br.readLine()) != null) {
      sTestTag = LTRIM.matcher(scan).replaceAll(EMPTY_STRING);
      if (sTestTag.equals(MY_ENUM_END_TAG)) 
        break;
      inputLines.add(scan);
    } 
    int nState = 0;
    List<String> lines = null;
    for (int n=0; n<inputLines.size(); n++) {
      scan = inputLines.get(n);
      // break the line up into words
      if (scan.isEmpty()) continue;
      String[] words = CodeUtils.splitWords(scan);
      if (words.length == 0) {
        if (nState == 1) {
          lines.add(scan);
        }
        continue;
      }
      // first word must be "case"
      if (words[0].equals("case")) {
        if (nState == 0) {
          enumName = words[1];
          lines = new ArrayList<String>();
          nState = 1;
        } else {
          caseMap.put(enumName, i);
          listOfCases[i] = lines;
//          Builder.logger.debug("Stored Case Statement: " + enumName + " idx=" + i);
          i++;
          nState = 0;
          n--;
        }
      }
      if (nState == 1) {
        lines.add(scan);
      }
    }
    if (nState == 1) {
      caseMap.put(enumName, i);
      listOfCases[i] = lines;
//      Builder.logger.debug("Stored Case Statement: " + enumName + " idx=" + i);
      i++;
    }
  }

  /**
   * findCaseStatement - find the case statement for this enum.
   *
   * @param caseEnum
   *          the caseEnum name
   * @return the index into our list of case statements
   */
  public int findCaseStatement(String caseEnum) {
    Integer idx = Integer.valueOf(-1);  
    if (caseMap.containsKey(caseEnum)) {
      idx = caseMap.get(caseEnum);
    }
    
    return idx.intValue();
  }
  
  public CaseInfo getCaseType(WidgetModel m) {
    CaseInfo ci = new CaseInfo(m.getEnum());
    ci.setCaseType(CT_STANDARD);
    if (m.getType().equals(EnumFactory.TEXTBUTTON)) {
      if (((TxtButtonModel) m).getJumpPage() != null && 
          !((TxtButtonModel) m).getJumpPage().isEmpty()) {
        ci.setCaseType(CT_CHGPAGE);
        ci.setPageEnum(((TxtButtonModel) m).getJumpPage());
      } else if (((TxtButtonModel) m).getPopupPage() != null && 
          !((TxtButtonModel) m).getPopupPage().isEmpty()) {
        ci.setCaseType(CT_SHOWPOPUP);
        ci.setPageEnum(((TxtButtonModel) m).getPopupPage());
      } else if (((TxtButtonModel) m).isHidePopup()) {
        ci.setCaseType(CT_HIDEPOPUP);
      }
    } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) {
      if (((ImgButtonModel) m).getJumpPage() != null && 
          !((ImgButtonModel) m).getJumpPage().isEmpty()) {
        ci.setCaseType(CT_CHGPAGE);
        ci.setPageEnum(((ImgButtonModel) m).getJumpPage());
      } else if (((ImgButtonModel) m).getPopupPage() != null && 
          !((ImgButtonModel) m).getPopupPage().isEmpty()) {
        ci.setCaseType(CT_SHOWPOPUP);
        ci.setPageEnum(((ImgButtonModel) m).getPopupPage());
      } else if (((ImgButtonModel) m).isHidePopup()) {
        ci.setCaseType(CT_HIDEPOPUP);
      }
    } else if (m.getType().equals(EnumFactory.TOGGLEBUTTON)) {
      ci.setCaseType(CT_TOGGLEBTN);
      ci.setElementRef(m.getElementRef());
    } else if (m.getType().equals(EnumFactory.NUMINPUT)) {
      ci.setCaseType(CT_INPUTNUM);
      ci.setElementRef(m.getElementRef());
    } else if (m.getType().equals(EnumFactory.TEXTINPUT)) {
      ci.setCaseType(CT_INPUTTXT);
      ci.setElementRef(m.getElementRef());
    }
    return ci;
  }

  public CaseInfo parseCaseType(String e, int idx) {
    CaseInfo ci = new CaseInfo(e);      
    ci.setCaseType(CT_STANDARD);
    List<String> caseList = listOfCases[idx];
    int n =-1;
    for (String line : caseList) {
      n++;
      if (line.isEmpty()) continue;
      String split[] = CodeUtils.splitWords(line);
      if (split.length == 0) continue;
      if (split[0].equals("case")) continue;
      if (split[0].equals("break")) break;
      if (split[0].equals("gslc_SetPageCur")) {
        if (split.length > 2) {
          ci.setCaseType(CT_CHGPAGE);
          ci.setPageEnum(split[2]);
          ci.setLineNo(n);
        } else {
          ci.setCaseType(CT_UNDEFINED);
        }
        continue;
      }
      if (split[0].equals("gslc_ElemXKeyPadTargetIdSet")) {
        if (split.length > 3) {
          ci.setKeyElementRef(split[2]);
          ci.setLineNo(n);
        }
        continue;
      }
      if (split[0].equals("gslc_PopupShow")) {
        if (split.length > 2) {
          if (split[2].equals("E_POP_KEYPAD")) {
            ci.setCaseType(CT_UPDINPUTNUM);
            ci.setPageEnum(split[2]);
          } else if (split[2].equals("E_POP_AKEYPAD")) {
            ci.setPageEnum(split[2]);
            ci.setCaseType(CT_UPDINPUTTXT);
          } else {
            ci.setCaseType(CT_SHOWPOPUP);
            ci.setPageEnum(split[2]);
            ci.setLineNo(n);
          }
        } else {
          ci.setCaseType(CT_UNDEFINED);
        }
        continue;
      }
      if (split[0].equals("gslc_ElemXKeyPadInputAsk")) {
        if (split.length > 5) {
          if (split[4].equals("E_POP_KEYPAD")) {
            ci.setCaseType(CT_INPUTNUM);
          } else {
            ci.setCaseType(CT_INPUTTXT);
          }
          ci.setKeyElementRef(split[3]);
          ci.setPageEnum(split[4]);
          ci.setElementRef(split[5]);
          ci.setLineNo(n);
        }
        continue;
      }
      if (split[0].equals("gslc_ElemXKeyPadValSet") && 
          (ci.getCaseType() == CT_UPDINPUTNUM || ci.getCaseType() == CT_UPDINPUTTXT)) {
        ci.setStopNo(n);
        if (split.length > 5) {
          ci.setElementRef(split[5]);
        }
        continue;
      }
      if (split[0].equals("gslc_PopupHide")) {
        ci.setCaseType(CT_HIDEPOPUP);
        ci.setLineNo(n);
        continue;
      }      
      if (split[0].equals("if")) {
        if (split[1].equals("gslc_ElemXTogglebtnGetState")) {
          ci.setCaseType(CT_TOGGLEBTN);
          ci.setElementRef(split[2]);
          ci.setLineNo(n);
        }
      }
    }
    return ci;  
  }
  
  /**
   * The Private Class CaseInfo used to store Case Enum (key) 
   * with Case Type, Page ENUM and Element Reference, if any.
   * It allows the Button Callback pipe to more easily track
   * any required changes to any existing Case Statements.
   */
   class CaseInfo {
    
    /** The key. */
    String key;
    
    /** The case type */
    int caseType;
    
    /** The page enum. */
    String pageEnum;

    /** The element ref */
    String elementRef;
    
    /** The keypad element ref */
    String keyElementRef;
    
    /** 
     * The line number tracks where we found a matching statement 
     * like gslc_SetPageCur or gslc_PopupShow
     */
    int  line_no;
    
    /**
     *  The stop number tracks where we stop matching
     */
    int  stop_no;
    
    /**
     * Instantiates a new CaseInfo.
     */
    CaseInfo(String key) {
      this.key = key;
      this.caseType = CT_UNDEFINED;
      this.pageEnum = "";
      this.elementRef = "";
      this.keyElementRef = "";
      this.line_no = -1;
      this.stop_no = -1;
    }
 
    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
      return key;
    }
    
    /**
     * Sets the caseType.
     */
    public void setCaseType(int caseType) {
      this.caseType = caseType;
    }
    
    /**
     * Gets the caseType.
     *
     * @return the caseType
     */
     public int getCaseType() {
      return caseType;
    }

    /**
     * Sets the pageEnum.
     */
    public void setPageEnum(String pageEnum) {
      this.pageEnum = pageEnum;
    }

    /**
     * Gets the pageEnum.
     *
     * @return the pageEnum
     */
    public String getPageEnum() {
      return pageEnum;
    }

    /**
     * Gets the elementRef.
     *
     * @return the elementRef
     */
    public String getElementRef() {
      return elementRef;
    }

    /**
     * Sets the elementRef.
     */
    public void setElementRef(String elementRef) {
      this.elementRef = elementRef;
    }

    /**
     * Gets the keypad elementRef.
     *
     * @return the elementRef
     */
    public String getKeyElementRef() {
      return keyElementRef;
    }

    /**
     * Sets the keypad elementRef.
     */
    public void setKeyElementRef(String keyElementRef) {
      this.keyElementRef = keyElementRef;
    }

    public int getLineNo() {
      return line_no;
    }
    
    public void setLineNo(int line_no) {
      this.line_no = line_no;
    }
    
    public int getStopNo() {
      return stop_no;
    }
    
    public void setStopNo(int stop_no) {
      this.stop_no = stop_no;
    }
    
  }
 
}
  