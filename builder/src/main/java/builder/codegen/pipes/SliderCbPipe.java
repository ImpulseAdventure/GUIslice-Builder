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

import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.models.ListBoxModel;
import builder.models.TextBoxModel;
import builder.models.WidgetModel;

/**
 * The Class SliderCbPipe handles code generation
 * within the "Slider Callback" tag of our source code.
 * 
 * This section creates callbacks for XSlider API calls. 
 * 
 * @author Paul Conti
 * 
 */
public class SliderCbPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String SLIDER_CB_TEMPLATE     = "<SLIDER_CB>";
  private final static String SEEKBAR_CASE_TEMPLATE  = "<SEEKBAR_CB_CASE>";
  private final static String SLIDER_CASE_TEMPLATE   = "<SLIDER_CB_CASE>";
  
  /** The Constants for macros. */
  private final static String CALLBACK_MACRO         = "CALLBACK";
  private final static String ELEMREF_MACRO          = "COM-019";
  private final static String ENUM_MACRO             = "COM-002";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public SliderCbPipe(CodeGenerator cg) {
    this.cg = cg;
  }
  
  /**
   * process
   *
   * For our Slider Callback we have this modified process routine.
   * This version is to support removing the SLIDERCB_TAG and SLIDERCB_END_TAG
   * once we write out any slider callbacks.
   *   
   * If this occurs we then place inside the newly generated callback the 
   * SLIDER_ENUMS_TAG and SLIDER_ENUMS_END_TAG between the "switch()" and "default"
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
    this.MY_TAG     = Tags.TAG_PREFIX + Tags.SLIDERCB_TAG + Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX + Tags.SLIDERCB_TAG + Tags.TAG_SUFFIX_END;
    this.MY_ENUM_TAG     = Tags.TAG_PREFIX + Tags.SLIDER_ENUMS_TAG + Tags.TAG_SUFFIX_START;
    this.MY_ENUM_END_TAG = Tags.TAG_PREFIX + Tags.SLIDER_ENUMS_TAG + Tags.TAG_SUFFIX_END;
    
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
      if (m.getType().equals(EnumFactory.SLIDER)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.SEEKBAR)) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.LISTBOX) && ((ListBoxModel) m).addScrollbar()) {
        callbackList.add(m);
      } else if (m.getType().equals(EnumFactory.TEXTBOX) && ((TextBoxModel) m).addScrollbar()) {
        callbackList.add(m);
      }
    }
    super.doCbCommon(br, sBd);
  }
  
   /**
    * outputCB.
    *  This routine outputs the  Common Button callback
    *  bool CbBtnCommon() and the first set of button callbacks.
    *
    * @param sBd
   *          the StringBuilder object containing our project template
    * @param callbackList
    *          the model list to process for callback buttons
    */
  @Override
  public void outputCB(StringBuilder sBd) {
    // create a temporary string buffer to hold the case statements
    // we will add them all at once before we leave this routine
    StringBuilder sTemp = new StringBuilder();
    // create our callback section - start by opening our templates
    tm = cg.getTemplateManager();
    List<String> templateStandard = tm.loadTemplate(SLIDER_CASE_TEMPLATE);
    List<String> templateSeekbar = tm.loadTemplate(SEEKBAR_CASE_TEMPLATE);
    List<String> outputLines;
    Map<String, String> map = new HashMap<String, String>();
    for (WidgetModel m : callbackList) {
      map.clear();
      if (m.getType().equals(EnumFactory.SLIDER)) {
        map.put(ENUM_MACRO, m.getEnum());
        map.put(ELEMREF_MACRO, m.getElementRef());
        outputLines = tm.expandMacros(templateStandard, map);
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.SEEKBAR)) {
        map.put(ENUM_MACRO, m.getEnum());
        map.put(ELEMREF_MACRO, m.getElementRef());
        outputLines = tm.expandMacros(templateSeekbar, map);
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.LISTBOX)) {
        map.put(ENUM_MACRO, m.getScrollbarEnum());
        map.put(ELEMREF_MACRO, m.getScrollbarERef());
        outputLines = tm.expandMacros(templateStandard, map);
        tm.codeWriter(sTemp, outputLines);
      } else if (m.getType().equals(EnumFactory.TEXTBOX)) {
        map.put(ENUM_MACRO, m.getScrollbarEnum());
        map.put(ELEMREF_MACRO, m.getScrollbarERef());
        outputLines = tm.expandMacros(templateStandard, map);
        tm.codeWriter(sTemp, outputLines);
      }
    }

    // now we place all of our new case statements inside our callback template
    map.clear();
    String sButtons = sTemp.toString();
    map.put(CALLBACK_MACRO, sButtons);
    List<String> templateLines = tm.loadTemplate(SLIDER_CB_TEMPLATE);
    outputLines = tm.expandMacros(templateLines, map);
    tm.codeWriter(sBd, outputLines);

  }

  /**
   * doEnums.
   *  The callback already exists so we just process what is  
   *  between the SLIDER_ENUMS_TAG and SLIDER_ENUMS_END_TAG here.
   *
   * @param br
   *          the buffered reader of our project template input
   * @param sBd
   *          the StringBuilder object containing our project template output
    * @param mList
    *          the model list to process for callback buttons
   */
  @Override
  public void doEnums(BufferedReader br, StringBuilder sBd) throws IOException {
    tm = cg.getTemplateManager();

    // build up a list of widget models that have button callbacks enabled
    // also, save the enums into our enumMap for easier checking for existence.
    callbackList = new ArrayList<WidgetModel>();
    List<String> enumList = new ArrayList<String>();
    for (WidgetModel m : cg.getModels()) {
      if (m.getType().equals(EnumFactory.SLIDER)) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      } else if (m.getType().equals(EnumFactory.SEEKBAR)) {
        callbackList.add(m);
        enumList.add(m.getEnum());
      } else if (m.addScrollbar()) {
        callbackList.add(m);
        enumList.add(m.getScrollbarEnum());
      }
    }
// BUG 122 - Deletion of listbox leaves ENUM case statements in slider callback
//    if (callbackList.size() == 0)
//      return;
    
    // this removes duplicates and detects and removes deleted ui elements
    Map<String, String> enumMap = super.mapEnums(br, sBd, enumList);
    
    // now deal with our new enums    
    List<String> templateStandard = tm.loadTemplate(SLIDER_CASE_TEMPLATE);
    List<String> templateSeekbar = tm.loadTemplate(SEEKBAR_CASE_TEMPLATE);
    List<String> outputLines;
    Map<String, String> map = new HashMap<String,String>();
    for (WidgetModel m : callbackList) {
      /* search our enumMap for this enum. 
       * if found to have been dealt with already; skip it (value == "1"),      
       * otherwise expand the macros
       */
      map.clear();
      if (m.getType().equals(EnumFactory.SLIDER)) {
        if (enumMap.get(m.getEnum()).equals("0")) {
          map.put(ENUM_MACRO, m.getEnum());
          map.put(ELEMREF_MACRO, m.getElementRef());
          outputLines = tm.expandMacros(templateStandard, map);
          tm.codeWriter(sBd, outputLines);
        }
      } else if (m.getType().equals(EnumFactory.SEEKBAR)) {
        if (enumMap.get(m.getEnum()).equals("0")) {
          map.put(ENUM_MACRO, m.getEnum());
          map.put(ELEMREF_MACRO, m.getElementRef());
          outputLines = tm.expandMacros(templateSeekbar, map);
          tm.codeWriter(sBd, outputLines);
        }
      } else if (m.getType().equals(EnumFactory.LISTBOX)) {
        if (enumMap.get(m.getScrollbarEnum()).equals("0")) {
          map.put(ENUM_MACRO, m.getScrollbarEnum());
          map.put(ELEMREF_MACRO, m.getScrollbarERef());
          outputLines = tm.expandMacros(templateStandard, map);
          tm.codeWriter(sBd, outputLines);
        }
      } else if (m.getType().equals(EnumFactory.TEXTBOX)) {
        if (enumMap.get(m.getScrollbarEnum()).equals("0")) {
          map.put(ENUM_MACRO, m.getScrollbarEnum());
          map.put(ELEMREF_MACRO, m.getScrollbarERef());
          outputLines = tm.expandMacros(templateStandard, map);
          tm.codeWriter(sBd, outputLines);
        }
      }

    }
  }    

}
  