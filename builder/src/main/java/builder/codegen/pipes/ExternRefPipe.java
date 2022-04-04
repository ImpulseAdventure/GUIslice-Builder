/**
 *
 * The MIT License
 *
 * Copyright 2018-2022 Paul Conti
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

import builder.Builder;
import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.fonts.FontFactory;
import builder.models.KeyPadModel;
import builder.models.KeyPadTextModel;
import builder.models.ProjectModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.NumKeyPadEditor;

// TODO: Auto-generated Javadoc
/**
 * The Class ExternRefPipe handles code generation
 * within the "Save_References" tag of our source code.
 * 
 * This section builds up a list of GUISlice Element References
 * that need to be saved for quick access during runtime.
 * 
 * @author Paul Conti
 * 
 */
public class ExternRefPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String ELEMENTREF_EXTERN_TEMPLATE = "<ELEMENT_REF_EXTERN>";
  private final static String EXTERN_UTFT_TEMPLATE       = "<EXTERN_UTFT_FONT>";
  private final static String ELEMREF_MACRO              = "ELEMREF";
  private final static String UTFT_MACRO                 = "UTFT_FONT";

  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public ExternRefPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.EXTERNREF_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.EXTERNREF_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    // setup
    ProjectModel pm = Controller.getProjectModel();
    FontFactory ff = FontFactory.getInstance();

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
      template = tm.loadTemplate(ELEMENTREF_EXTERN_TEMPLATE);
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
      template = tm.loadTemplate(ELEMENTREF_EXTERN_TEMPLATE);
      map.clear();
      map.put(ELEMREF_MACRO, EnumFactory.KEYPAD_ELEMREF);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    if (bAddAlphaKeyPad) {
      template = tm.loadTemplate(ELEMENTREF_EXTERN_TEMPLATE);
      map.clear();
      map.put(ELEMREF_MACRO, EnumFactory.ALPHAKEYPAD_ELEMREF);
      outputLines = tm.expandMacros(template, map);
      tm.codeWriter(sBd, outputLines);
    }
    
    // Now add any extern's for UTFT fonts, if any
    String target = Controller.getTargetPlatform();
    if (target.equals(ProjectModel.PLATFORM_UTFT)) {
      // scan thru all of the projects widgets and
      // build up a list of all font display names.
      List<String> fontNames = new ArrayList<String>();
      String name = null;
      int nErrors = 0;
      for (WidgetModel m : cg.getModels()) {
        name = m.getFontDisplayName();
        if (name != null) {
          if (ff.getFont(name) == null) {
            Builder.logger.error("widget: " +  m.getEnum() + " refers to missing font=" + name);
            nErrors++;
          }
          fontNames.add(name);
        }
        if (m.getType().equals(EnumFactory.NUMINPUT)) {
          bAddNumKeyPad = true;
        }
        if (m.getType().equals(EnumFactory.TEXTINPUT)) {
          bAddAlphaKeyPad = true;
        }
      }
      // End with keyboard fonts - bug 144 missing keyboard font #include
      // place any keypads at end
      if (bAddNumKeyPad) {
        KeyPadModel m = (KeyPadModel)NumKeyPadEditor.getInstance().getModel();
        name = m.getFontDisplayName();
        if (name != null) {
          if (ff.getFont(name) != null) {
            fontNames.add(name);
          } else {
            name = ff.getDefFontName();
            if (name != null) {
              fontNames.add(name);
            } else {
              Builder.logger.error("NumKeyPad: " +  m.getEnum() + " refers to missing font=" + name);
              nErrors++;
            }
          }
        } else {
          Builder.logger.error("NumKeyPad: " +  m.getEnum() + " is missing font");
          nErrors++;
        }
      }
      if (bAddAlphaKeyPad) {
        KeyPadTextModel m = (KeyPadTextModel)AlphaKeyPadEditor.getInstance().getModel();
        name = m.getFontDisplayName();
        if (name != null) {
          if (ff.getFont(name) != null) {
            fontNames.add(name);
          } else {
            name = ff.getDefFontName();
            if (name != null) {
              fontNames.add(name);
            } else {
              Builder.logger.error("AlphaKeyPad: " +  m.getEnum() + " refers to missing font=" + name);
              nErrors++;
            }
          }
        } else {
          Builder.logger.error("AlphaKeyPad: " +  m.getEnum() + " refers to missing font=" + name);
          nErrors++;
        }
      }
      // add any extra fonts requested
      for (String s : pm.getFontsList()) {
        if (s != null && !s.isEmpty()) {
          if (ff.getFont(s) != null) {
            fontNames.add(s);
          } else {
            Builder.logger.error("Project Extra Font: " + s + " is not supported on this platform");
            nErrors++;
          }
        }
      }
      if (nErrors > 0) {
        String fileName = CommonUtils.getWorkingDir()
            + "logs" 
            + System.getProperty("file.separator")
            + "builder.log";
        throw new CodeGenException(String.format("Sketch has %d missing font(s).\nExamine %s for list of fonts.",
            nErrors,fileName));
      }
      if (fontNames.size() == 0)
        return;
      // sort the names and remove duplicates
      CodeUtils.sortListandRemoveDups(fontNames);
      
      // now output our extern list
      template = tm.loadTemplate(EXTERN_UTFT_TEMPLATE);
      for (String s : fontNames) {
        map.clear();
        map.put(UTFT_MACRO, s);
        outputLines = tm.expandMacros(template, map);
        tm.codeWriter(sBd, outputLines);
        
      }
    }
  }
}
