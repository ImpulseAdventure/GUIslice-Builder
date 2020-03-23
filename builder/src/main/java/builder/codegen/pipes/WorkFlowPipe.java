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

//import builder.Builder;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

import builder.codegen.CodeGenException;
import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.models.WidgetModel;

/**
 * The Class WorkFlowPipe is the base class for all of our 
 * code generation workflow pipes.
 * 
 * @author Paul Conti
 * 
 */
public class WorkFlowPipe implements Pipe<StringBuilder> {

  public String MY_TAG;
  public String MY_END_TAG;
  public String MY_ENUM_TAG;
  public String MY_ENUM_END_TAG;
  
  private final static Pattern LTRIM = Pattern.compile("^\\s+");
  private final static String EMPTY_STRING = "";
  
  List<WidgetModel> callbackList = null;;
  
  /** The cg. */
  CodeGenerator cg = null;
  
  /** The line. */
  String line  = "";

  /**
   * process
   *
   * @see builder.codegen.pipes.Pipe#process(java.lang.Object, java.lang.Object)
   */
  public StringBuilder process(StringBuilder input) throws CodeGenException {
    // bTagFound allows us to detect if we find our tag and error out on failure.
    boolean bTagFound = false;  // detect if we find our tag
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
        processed.append(line);
        processed.append(System.lineSeparator());
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals(MY_TAG)) {
          bTagFound = true;
          doCodeGen(processed);
          break;
        }
      }
      if (!bTagFound) {
//        throw new CodeGenException("file: " + cg.getTemplateName() + "\n is corrupted missing tag:" + MY_TAG);
        throw new CodeGenException("file: " + cg.getTemplateName() + 
            "\n corrupted:" + MY_TAG + " out: " + cg.getOutputName());
      }
      CodeUtils.findTag(br, processed, MY_END_TAG);
      CodeUtils.finishUp(br, processed);
      br.close();
      return processed;   
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    }      
  }

  /**
   * process callback tags
   *
   * @see builder.codegen.pipes.Pipe#process(java.lang.Object, java.lang.Object)
   */
  public StringBuilder processCB(StringBuilder input) throws CodeGenException {
    // bTagFound allows us to detect if we find our tag and error out on failure.
    boolean bTagFound = false;  // detect if we find our tag
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
          bTagFound = true;
          doCbCommon(br, processed);
          break;
        } else if (sTestTag.equals(MY_ENUM_TAG)) {
          bTagFound = true;
          processed.append(sTestTag); // output our BUTTON_ENUMS_TAG
          processed.append(System.lineSeparator());  
          doEnums(br, processed);
          processed.append(MY_ENUM_END_TAG); 
          processed.append(System.lineSeparator());  
        } else {
          processed.append(line);
          processed.append(System.lineSeparator());
        }
      }
      if (!bTagFound) {
        throw new CodeGenException("file: " + cg.getTemplateName() + 
            "\n is corrupted missing tag:" + MY_TAG);
      }
      CodeUtils.finishUp(br, processed);
      br.close();
      return processed;   
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    }      
  }
  
  /**
   * doCodeGen.
   *
   * @param sBd
   *          the StringBuilder object containing our project template
   */
  public void doCodeGen(StringBuilder sBd) {

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
  public void doCbCommon(BufferedReader br, StringBuilder sBd) throws IOException {
    if (callbackList == null || callbackList.size() == 0) {
      sBd.append(MY_TAG); 
      sBd.append(System.lineSeparator());  
      return;      
    }
    // now process our model list and create the callbacks
    outputCB(sBd);
    // now remove the existing MY_END_TAG
    String sTestTag= "";
    while ((line = br.readLine()) != null) {
      sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
      if (sTestTag.equals(MY_END_TAG)) break;
    }
  }
  
  /**
   * outputCB.
   *  This routine outputs the callback
   *
   * @param sBd
   *          the StringBuilder object containing our project template
   */
  public void outputCB(StringBuilder sBd) {
    
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
  public void doEnums(BufferedReader br, StringBuilder sBd) throws IOException {
    
  }

  /**
   * mapEnums.
   * searches callbackList of widget models and creates 
   * a map of enums with a code of 0 for new or 1 for existing.
   * It will also detect and prune deleted UI elements.
   *
   * @param br
   *          the buffered reader of our project template input
   * @return map
   */
  public Map<String, String> mapEnums(BufferedReader br, StringBuilder sBd, List<String>enumList) throws IOException {
    Map<String, String> enumMap = new HashMap<String, String>();
    for (String s : enumList) {
      enumMap.put(s, "0");
    }
    List<String> scanLines = new ArrayList<String>();
    /* our callback section already exists - read it into a buffer we can scan 
     * it for existing Enum case statements. This will allow us to determine
     * if a case statement for an ENUM already exists or not.
     * Also, we can detect of a ENUM case should be deleted because the button
     * was removed.
     */     
    String sTestTag= "";
    while((line = br.readLine()) != null) {
      sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
      if (sTestTag.equals(MY_ENUM_END_TAG)) break;
      scanLines.add(line);
    }
    /* now search the saved buffer (scanLines) for any 
     * deleted enums and remove their case statements 
     * from the buffer.
     */
    ListIterator<String> litr = scanLines.listIterator();
    while(litr.hasNext()) {
      String s = litr.next();
      // break the line up into words
      if (s.isEmpty()) continue;
      String[] words = s.split("\\W+");
      // first word must be "case"
      if (words.length == 0) continue;
      if (words[1].equals("case")) {
        // second word will be our ENUM, look it up in our ENUM map
        if (enumMap.containsKey(words[2])) {
          /* since we know we have this ENUM case statement already 
           * exists lets set the value inside the map to
           * indicate it's already been dealt with.
           */
          enumMap.put(words[2], "1");  // changed from "0" to "1"
        } else {        
          // this ENUM isn't in our list of valid ENUMs so we need to delete it
          litr.remove();
          // now remove all lines up to and including the "break" statement
          while(litr.hasNext()) {
            s = litr.next();
            litr.remove();
            // search for "break" statement
            if (s.matches(".*\\bbreak\\b.*")) 
              break;
          }
        }
      } 
    }
    
    // Now that our previous case statement ENUMs have been cleaned up output them
    if (scanLines.size() > 0) {
      for (String s : scanLines) {
        sBd.append(s);
        sBd.append(System.lineSeparator());  
      } 
    }
    
    return enumMap; 
  }

}
  