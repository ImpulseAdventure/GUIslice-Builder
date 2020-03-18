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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
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

/**
 * The Class AppPipe handles code generation
 * within the "File" tag of our source code.
 * 
 * This simply outputs the name of our source code file
 * and who created it and what builder version was used.
 * 
 * @author Paul Conti
 * 
 */
public class AppPipe extends WorkFlowPipe {

  /** The Constants for templates. */
  private final static String APP_HDR_TEMPLATE          = "<APP_HDR>";
  
  /** The Constants for macros */
  private final static String FILENAME_MACRO             = "FILENAME";
  private final static String VERSION_MACRO              = "VERSION";
  private final static Pattern LTRIM = Pattern.compile("^\\s+");
  private final static String EMPTY_STRING = "";

  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public AppPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.APP_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.APP_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * process
   *
   * @see builder.codegen.pipes.Pipe#process(java.lang.Object, java.lang.Object)
   */
  public StringBuilder process(StringBuilder input) throws CodeGenException {
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
      String sTestTag= "";
      line = br.readLine();
      sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
      // do we need to upgrade from earlier beta versions?
      if (sTestTag.equals(MY_TAG)) {
        processed.append(line);
        processed.append(System.lineSeparator());
        doCodeGen(processed);
        CodeUtils.findTag(br, processed, MY_END_TAG);
      } else {
        throw new CodeGenException("file: " + cg.getTemplateName() + "\n is corrupted missing tag:" + MY_TAG);
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
    List<String> templateLines = tm.loadTemplate(APP_HDR_TEMPLATE);
    String sFileName = cg.getOutputName();
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    map.put(FILENAME_MACRO, sFileName);
    map.put(VERSION_MACRO, Builder.VERSION);
    outputLines = tm.expandMacros(templateLines, map);
    tm.codeWriter(sBd, outputLines);

    templateLines.clear();
    outputLines.clear();
    map.clear();
  }

}
  