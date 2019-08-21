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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import builder.Builder;
import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.Builder;
import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
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
  private final static String INCLUDES_TAG                 = "//<Includes !Start!>";
  private final static String INCLUDES_END_TAG             = "//<Includes !End!>";
  private final static String TICKCB_TAG           = "//<Tick Callback !Start!>";
  private final static String CHECKBOXCB_TAG           = "//<Checkbox Callback !Start!>";
  private final static String CHECKBOXCB_END_TAG       = "//<Checkbox Callback !End!>";
  private final static String KEYPADCB_TAG           = "//<Keypad Callback !Start!>";
  private final static String KEYPADCB_END_TAG       = "//<Keypad Callback !End!>";
  private final static String SPINNERCB_TAG           = "//<Spinner Callback !Start!>";
  private final static String SPINNERCB_END_TAG       = "//<Spinner Callback !End!>";
  private final static String LISTBOXCB_TAG           = "//<Listbox Callback !Start!>";
  private final static String LISTBOXCB_END_TAG       = "//<Listbox Callback !End!>";
  
  /** The Constants for templates. */
  private final static String FILE_HDR_TEMPLATE          = "<FILE_HDR>";
  
  /** The Constants for macros */
  private final static String FILENAME_MACRO             = "FILENAME";
  private final static String VERSION_MACRO              = "VERSION";

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
   * process
   *
   * @see builder.codegen.pipes.Pipe#process(java.lang.Object, java.lang.Object)
   */
  public StringBuilder process(StringBuilder input) throws CodeGenException {
    if (!bEnable) {
      return input;
    }
    try {
      /*
       * To convert StringBuilder to InputStream in Java, first get bytes
       * from StringBuilder after converting it into String object.
       */
      byte[] bytes = input.toString().getBytes(StandardCharsets.UTF_8);
      /*
       * Get ByteArrayInputStream from byte array.
       */
      InputStream is = new ByteArrayInputStream(bytes);
      BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      StringBuilder processed = new StringBuilder();
      line = br.readLine();
      // do we need to upgrade from earlier beta versions?
      if (line.equals(MY_TAG)) {
        processed.append(line);
        processed.append(System.lineSeparator());
        doCodeGen(processed);
        CodeUtils.readPassString(br, processed, MY_END_TAG);
      } else {
        // yes, we need to upgrade so first add our new file tag and header
        processed.append(MY_TAG);
        processed.append(System.lineSeparator());
        doCodeGen(processed);
        processed.append(MY_END_TAG);
        processed.append(System.lineSeparator());
        // now copy everything up to extended element tag
        CodeUtils.findTag(br, processed, "#include \"GUIslice_ex.h\"");
        processed.append("#include \"GUIslice_drv.h\"");
        processed.append(System.lineSeparator());
        processed.append(System.lineSeparator());
        processed.append(INCLUDES_TAG);
        processed.append(System.lineSeparator());
        processed.append(INCLUDES_END_TAG);
        processed.append(System.lineSeparator());
        processed.append(System.lineSeparator());
        CodeUtils.findTag(br, processed, TICKCB_TAG);
        // output our new tags
        upgradeToVer13(processed);
      }
      CodeUtils.finishUp(br, processed);
      br.close();
      return processed;   
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    }      
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

  /**
   * Upgrade to ver 13.
   *
   * @param sBd
   *          the s bd
   */
  public void upgradeToVer13(StringBuilder sBd) {
    sBd.append(CHECKBOXCB_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(CHECKBOXCB_END_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(KEYPADCB_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(KEYPADCB_END_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(SPINNERCB_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(SPINNERCB_END_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(LISTBOXCB_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(LISTBOXCB_END_TAG);
    sBd.append(System.lineSeparator());
    sBd.append(TICKCB_TAG);
    sBd.append(System.lineSeparator());
  }


}
  