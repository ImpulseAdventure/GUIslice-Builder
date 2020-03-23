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
import builder.models.WidgetModel;

/**
 * The Class PathStoragePipe handles code generation
 * within the PathStorage tag of our source code.
 * 
 * This section creates path storage for images on the  
 * linux platform.
 * 
 * @author Paul Conti
 * 
 */
public class PathStoragePipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String IMAGE_PATH_TEMPLATE             = "<IMAGE_PATH>"; 
  private final static String IMGBUTTON_PATH_TEMPLATE         = "<IMGBUTTON_PATH>"; 
  
  /** The Constants for macros. */
  private final static String COUNT_MACRO                     = "COUNT";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public PathStoragePipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.PATHSTORAGE_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.PATHSTORAGE_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    // scan our models for matching UI widgets and output image path storage
    tm = cg.getTemplateManager();
    List<String> imageTemplate = tm.loadTemplate(IMAGE_PATH_TEMPLATE);;
    List<String> buttonTemplate = tm.loadTemplate(IMGBUTTON_PATH_TEMPLATE);;
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    for (WidgetModel m : cg.getModels()) {
      if (m.getType().equals(EnumFactory.IMAGE)) {
        map.put(COUNT_MACRO, CodeUtils.getKeyCount(m.getKey()));
        outputLines = tm.expandMacros(imageTemplate, map);
        tm.codeWriter(sBd, outputLines);
      } else if (m.getType().equals(EnumFactory.IMAGEBUTTON)) { 
        map.put(COUNT_MACRO, CodeUtils.getKeyCount(m.getKey()));
        outputLines = tm.expandMacros(buttonTemplate, map);
        tm.codeWriter(sBd, outputLines);
      }
    }
  }

}
  