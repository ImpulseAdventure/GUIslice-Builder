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

//import builder.Builder;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.Builder;
import builder.codegen.CodeGenerator;
import builder.codegen.TemplateManager;

/**
 * The Class FilePipe handles code generation
 * within the "File" tag of our source code.
 * 
 * This simply outputs the name of our source code file
 * and who created it and what builder version was used.
 * 
 * @author Paul Conti
 * 
 */
public class FilePipe extends WorkFlowPipe {

  /** The Constants for tags. */
  private final static String FILE_TAG                   = "//<File !Start!>";
  private final static String FILE_END_TAG               = "//<File !End!>";
  
  /** The Constants for templates. */
  private final static String FILE_HDR_TEMPLATE          = "<FILE_HDR>";
  
  /** The Constants for macros */
  private final static String FILENAME_MACRO             = "FILENAME";
  private final static String VERSION_MACRO              = "VERSION";

  /** The cg. */
  CodeGenerator cg = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public FilePipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = FILE_TAG;
    this.MY_END_TAG = FILE_END_TAG;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    TemplateManager tm = cg.getTemplateManager();
    List<String> templateLines = tm.loadTemplate(FILE_HDR_TEMPLATE);
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();

    map.put(FILENAME_MACRO, cg.getProjectFile().getName());
    map.put(VERSION_MACRO, Builder.VERSION);
    outputLines = tm.expandMacros(templateLines, map);
    tm.codeWriter(sBd, outputLines);

    templateLines.clear();
    outputLines.clear();
    map.clear();
  }
  
}
  