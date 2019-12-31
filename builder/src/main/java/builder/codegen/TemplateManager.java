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
package builder.codegen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class TemplateManager handles all functions related to 
 * code templates.
 * 
 * It reads platform specific templates into memory, 
 * makes them available for code generation,
 * fills in (expands) macros, and writes out the results.
 * 
 * @author Paul Conti
 * 
 */
public class TemplateManager {

  /** The Constant RESOURCES_PATH. */
  public final static String RESOURCES_PATH         = "/resources/templates/";

  /** The Constant STOP_TEMPLATE. */
  private final static String STOP_TEMPLATE          = "<STOP>";
  
  /** The Constant END_TEMPLATE. */
  private final static String END_TEMPLATE           = "<END>";
  
  /** The Constant MACRO_PATTERN is our regex search pattern '$<xxx>'. */
  private static final Pattern MACRO_PATTERN = Pattern.compile("\\$\\<(.+?)\\>");

  /** The Constant BEGIN_LINE. */
  // finite state machine for printing enums
  private final static int BEGIN_LINE    = 0;
  
  /** The Constant WRITE_NEXT. */
  private final static int WRITE_NEXT    = 1;
  
  /** The Constant CLIP_LINE. */
  private final static int CLIP_LINE     = 72;

  /** The template map. */
  HashMap<String, Integer> templateMap;
  
  /** The list of templates. */
  List<String>[] listOfTemplates = null;

  /**
   * Instantiates a new template manager.
   */
  @SuppressWarnings("unchecked")
  public TemplateManager() {
    listOfTemplates = new ArrayList[256];
  }

  /**
   * Store templates.
   *
   * @param templateFileName
   *          the template file name
   * @throws CodeGenException
   *           the code gen exception
   */
  public void storeTemplates(String templateFileName) throws CodeGenException {
    templateMap = new HashMap<String, Integer>(64);
    String pathName = RESOURCES_PATH + templateFileName;
    BufferedReader tbr = new BufferedReader(new InputStreamReader(
                  this.getClass().getResourceAsStream(pathName)));
    String l = "";
    String templateName = "";
    int i = 0;
    try {
      while((templateName = tbr.readLine()) != null) {
        if (templateName.equals(END_TEMPLATE))
          break;
        List<String> lines = new ArrayList<String>();
        while (!(l = tbr.readLine()).equals(STOP_TEMPLATE)) {
          lines.add(l);
        }
        templateMap.put(templateName, i);
        listOfTemplates[i] = lines;
//        System.out.println("Stored Template: " + templateName + " idx=" + i);
        i++;
      }
      
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    } finally {
      try {
        tbr.close();
      } catch (IOException e) {
        throw new CodeGenException(e.toString());
      }
    }
  }
    
  /**
   * Load template.
   *
   * @param templateName
   *          the template name
   * @return the <code>list</code> object
   */
  public List<String> loadTemplate(String templateName) {
    Integer idx = Integer.valueOf(-1);  
    if (templateMap.containsKey(templateName)) {
      idx = templateMap.get(templateName);
    } else {
      throw new CodeGenException("Missing template: " + templateName);
    }
    List<String> templateOld = listOfTemplates[idx.intValue()];
    List<String> templateNew = new ArrayList<String>();
    for (String s : templateOld) {
      templateNew.add(s);
    }
    
    return templateNew;
  }

  /**
   * Expand macros.
   *  This routine takes a "key"/"value" pair stored inside a Map and 
   *  searches the input template for a matching "key" within a macro.
   *  Upon finding a matching "key" the "value" is substituted for the macro.
   *
   *  Macros in the template are coded as a key name inside character
   *  sequence "$<" and ending with ">".
   *  The Macros must be all alphabetic characters and convention is to use all upper-case.
   *
   *  Example: $<ENUM> where ENUM is the key stored in our Map and value might be E_PG_MAIN
   *  So in this case "$<ENUM>" would become simply "E_PG_MAIN";
   *
   *  One other type of macro is supported.  This is to allow a simple form of 
   *  pretty printing. There are occasions where we would want a "value" in our
   *  "key", "value" pair to be padded out with spaces.  This sometimes makes the code
   *  easier to read.  This macro takes a number instead of alphabetic characters.
   *  This macro must be placed in front of the "key" macro to be padded.
   *
   *  Example:  gslc_tsElemRef*  $<18>$<ELEMREF>= NULL;
   *  Will cause the "value" of ELEMRREF key to be padded out to 18 spaces.
   *  So the output might look like this:
   *            gslc_tsElemRef*  m_pElemCnt        = NULL;
   *            gslc_tsElemRef*  m_pElemProgress   = NULL;
   *                             123456789012345678 <--padding
   *
   * @param template
   *          the template lines
   * @param map
   *          the map containing the key/value pairs.
   * @return the <code>list</code> object containing our expanded template.
   * @throws CodeGenException
   */
  public List<String> expandMacros(List<String> template, Map<String, String> map) 
    throws CodeGenException {
    List<String> outputList = new ArrayList<String>();
    String sKey = null;
    String sValue = null;
    int nPadding=0;
    try {
      for (String l : template) {
        Matcher m = MACRO_PATTERN.matcher(l);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
          sKey = m.group(1);
          // test the key, if its numeric its padding
          // otherwise its a macro to be expanded
          if (sKey.matches("[0-9]+")) {
              nPadding = Integer.parseInt(sKey);
              m.appendReplacement(sb, "");
          } else {
            sValue = map.get(sKey); 
            if (sValue != null) {
              if (nPadding > 0) {
                // space out this value
                nPadding = nPadding - sValue.length();
                if (nPadding > 0) {
                  for (int i=0; i<nPadding; i++)
                    sValue = sValue + " ";
                  nPadding = 0; // reset for next value
                }
              }
// bug: 123 appendReplacementfails if sValue has a '$' in it. 
//              m.appendReplacement(sb, sValue);
              m.appendReplacement(sb, "");
              sb.append(sValue);
            }
          }
        }
        m.appendTail(sb);
        outputList.add(sb.toString());
      }
    } catch(IllegalArgumentException e) {
      System.out.println("sKey: " + sKey + " sValue: " + sValue);
      e.printStackTrace();
      throw new CodeGenException(e.toString());
    }
    return outputList;
  }

  /**
   * codeWriter 
   *    Writes out code block.
   *
   * @param sBd
   *          the s bd
   * @param lines
   *          the lines
   */
  public void codeWriter(StringBuilder sBd, List<String> lines) {
    for (String l : lines) {
      sBd.append(l);
      sBd.append(System.lineSeparator());
    }
  }
 
  /**
   * codeWriterEnums
   *    writes out a formatted list enums.
   *
   * @param enumList
   *          the enum list
   */
  public void codeWriterEnums(StringBuilder sBd, List<String> enumList) {
    int printState = BEGIN_LINE;
    String line = "";
    String sEnum;
    for (int i=0; i<enumList.size(); i++) {
      sEnum = enumList.get(i);
      if (printState == WRITE_NEXT) {
        if ((line.length() + sEnum.length() + 1) < CLIP_LINE) {
          line = line + "," + sEnum;
        } else {
          sBd.append(line);
          sBd.append(System.lineSeparator());
          line = "      ," + sEnum;
        }
      } else if (printState == BEGIN_LINE)  {
        line = "enum {" + sEnum;
        printState = WRITE_NEXT;
      }
    }
    sBd.append(line);
    sBd.append("};");
    sBd.append(System.lineSeparator());
    return;
  }

}
