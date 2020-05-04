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
import builder.controller.Controller;
import builder.models.ImageModel;
import builder.models.ImgButtonModel;
import builder.models.ProjectModel;
import builder.models.WidgetModel;

/**
 * The Class ResourcesPipe handles code generation
 * within the Resources tag of our source code.
 * 
 * This section creates image storage for images.  
 * 
 * @author Paul Conti
 * 
 */
public class ResourcesPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String RESOURCE_DEFINE_TEMPLATE        = "<RESOURCE_DEFINE>"; 
  private final static String RESOURCE_EXTERN_TEMPLATE        = "<RESOURCE_EXTERN>"; 
  private final static String RESOURCE_PROGMEM_TEMPLATE       = "<RESOURCE_PROGMEM>"; 
  
  /** The Constants for macros. */
  private final static String DEFINE_MACRO                    = "DEFINE";
  private final static String EXTERN_NAME_MACRO               = "EXTERN_NAME";
  private final static String IMAGE_NAME_MACRO                = "IMAGE_NAME";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public ResourcesPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.RESOURCES_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.RESOURCES_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    List<String> resources = new ArrayList<String>();
    // now pull out from the models the resources as strings that GUIslice can understand
    tm = cg.getTemplateManager();
    List<String> defineTemplate = tm.loadTemplate(RESOURCE_DEFINE_TEMPLATE);
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    // deal with background image, if any
    ProjectModel pm = Controller.getProjectModel();
    if (pm.useBackgroundImage()) { 
       map.put(DEFINE_MACRO, pm.getBackgroundDefine());
       map.put(IMAGE_NAME_MACRO, pm.getBackgroundImageTName()); // use target name
       outputLines = tm.expandMacros(defineTemplate, map);
       resources.add(outputLines.get(0));
    }
    // gather all #define names
    for (WidgetModel m : cg.getModels()) {
      map.clear();
      if (m.getType().equals(EnumFactory.IMAGE)) {
        if (!((ImageModel)m).getDefine().isEmpty()) {
          map.put(DEFINE_MACRO, ((ImageModel) m).getDefine());
          map.put(IMAGE_NAME_MACRO, ((ImageModel) m).getImageName());
          outputLines = tm.expandMacros(defineTemplate, map);
          resources.add(outputLines.get(0));
        }
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) { 
        if (!((ImgButtonModel)m).getDefine().isEmpty()) {
          map.put(DEFINE_MACRO, ((ImgButtonModel) m).getDefine());
          map.put(IMAGE_NAME_MACRO, ((ImgButtonModel) m).getImageName());
          outputLines = tm.expandMacros(defineTemplate, map);
          resources.add(outputLines.get(0));
        }
        if (!((ImgButtonModel)m).getSelDefine().isEmpty()) {
          map.put(DEFINE_MACRO, ((ImgButtonModel) m).getSelDefine());
          map.put(IMAGE_NAME_MACRO, ((ImgButtonModel) m).getSelectImageName());
          outputLines = tm.expandMacros(defineTemplate, map);
          resources.add(outputLines.get(0));
        }
      }
    }
    // Sort the list in order and remove dups.
    // We might have the same image on different pages of the UI, like a Logo.
    if (resources.size() > 0) {
      CodeUtils.sortListandRemoveDups(resources);
      // finish off by outputting resources, if any
      for (String s : resources) {
        sBd.append(s);
        sBd.append(System.lineSeparator());
      }
    }
    // now make pass to gather all extern names, if any
    List<String> progmemTemplate = tm.loadTemplate(RESOURCE_PROGMEM_TEMPLATE);
    List<String> externTemplate = tm.loadTemplate(RESOURCE_EXTERN_TEMPLATE);
    map.clear();
    resources.clear();
    for (WidgetModel m : cg.getModels()) {
      if (m.getType().equals(EnumFactory.IMAGE)) {
        if (!((ImageModel)m).getExternName().isEmpty()) {
          map.put(EXTERN_NAME_MACRO, ((ImageModel) m).getExternName());
          if (((ImageModel)m).getMemory().equals(ImageModel.SRC_PROG)) {
            outputLines = tm.expandMacros(progmemTemplate, map);
          } else {
            outputLines = tm.expandMacros(externTemplate, map);
          }
          resources.add(outputLines.get(0));
        }
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) { 
        if (!((ImgButtonModel)m).getExternName().isEmpty()) {
          map.put(EXTERN_NAME_MACRO, ((ImgButtonModel) m).getExternName());
          if (((ImgButtonModel)m).getMemory().equals(ImgButtonModel.SRC_PROG)) {
             outputLines = tm.expandMacros(progmemTemplate, map);
          } else {
            outputLines = tm.expandMacros(externTemplate, map);
          }
          resources.add(outputLines.get(0));
        }
        if (!((ImgButtonModel)m).getSelExternName().isEmpty()) {
          map.put(EXTERN_NAME_MACRO, ((ImgButtonModel) m).getSelExternName());
          if (((ImgButtonModel)m).getSelMemory().equals(ImgButtonModel.SRC_PROG)) {
            outputLines = tm.expandMacros(progmemTemplate, map);
          } else {
            outputLines = tm.expandMacros(externTemplate, map);
          }
          resources.add(outputLines.get(0));
        }
      }
    }
    if (resources.size() > 0) {
      CodeUtils.sortListandRemoveDups(resources);
      // finish off by outputting resources, if any
      for (String s : resources) {
        sBd.append(s);
        sBd.append(System.lineSeparator());
      }
    }
  }

}
  