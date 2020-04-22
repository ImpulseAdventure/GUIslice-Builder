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

import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;

/**
 * The Class HeaderPipe handles code generation
 * within the "Tick Callback" tag of our source code.
 * 
 * This section creates callbacks for BOX tick function. 
 * 
 * @author Paul Conti
 * 
 */
public class HeaderPipe extends WorkFlowPipe {

  private final static String HDR_TEMPLATE         = "<HDR_INCLUDE>";
  private final static String FILENAME_MACRO       = "FILENAME";

  /** The Constants for templates. */
  private final static Pattern LTRIM = Pattern.compile("^\\s+");
  private final static String EMPTY_STRING = "";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public HeaderPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG     = Tags.TAG_PREFIX + Tags.HEADER_TAG + Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX + Tags.HEADER_TAG + Tags.TAG_SUFFIX_END;
  }
  
  /**
   * process
   *
   * For our tick Callback we have this modified process routine.
   * This version is to support removing the HEADER_TAG and HEADER_END_TAG
   * once we write out any callbacks.
   *   
   * NOTE: Notice that we also do not output our end tag by calling
   *       CodeUtils.readPassString(). 
   *
   * @see builder.codegen.pipes.Pipe#process(java.lang.Object, java.lang.Object)
   */
  @Override
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
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals(MY_TAG)) {
          doCallback(br, processed);
          break;
        } else {
          processed.append(line);
          processed.append(System.lineSeparator());
        }
      }
      CodeUtils.finishUp(br, processed);
      br.close();
      return processed;   
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    }      
  }

  /**
   * doCallback
   * @param br
   *          the buffered reader of our project template input
   * @param sBd
   *          the StringBuilder object containing our project template
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void doCallback(BufferedReader br, StringBuilder sBd) throws IOException {
    TemplateManager tm = cg.getTemplateManager();
    List<String> templateLines = tm.loadTemplate(HDR_TEMPLATE);
    String sFileName = new String(cg.getProjectName() + CodeGenerator.HEADER_EXT);
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    map.put(FILENAME_MACRO, sFileName);
    outputLines = tm.expandMacros(templateLines, map);
    tm.codeWriter(sBd, outputLines);

    templateLines.clear();
    outputLines.clear();
    map.clear();

    // now remove the existing HEADER_END_TAG
    String sTestTag= "";
    while ((line = br.readLine()) != null) {
      sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
      if (sTestTag.equals(MY_END_TAG)) break;
    }

  }
  
}
  