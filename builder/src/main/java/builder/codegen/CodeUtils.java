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
package builder.codegen;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

import builder.models.WidgetModel;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class CodeUtils implements various utility routines 
 * needed by code generation modules.
 * 
 * @author Paul Conti
 * 
 */
public final class CodeUtils {
  
  private final static Pattern LTRIM = Pattern.compile("^\\s+");
  private final static String EMPTY_STRING = "";

  public CodeUtils() {
  }

  /**
  * Convert enum 
  * strips leading "E_" off of ENUM.
  *
  * @param enum
  *          the enum
  * @return the <code>String</code> without 'E_' at beginning
  */
  static public String convertEnum(String strEnum) {
    if (strEnum.startsWith("E_")) {
      return strEnum.substring(2);
    }
    return strEnum;
  }
  
  /**
  * Convert key 
  * strips "$" off of key.
  *
  * @param key
  *          the key
  * @return the <code>String</code> without '$'
  */
  static public String convertKey(String key) {
    String sType = "";
    String sCount = "";
    int n = key.indexOf("$");
    sType = key.substring(0,n);
    sCount = key.substring(n+1);
    return sType + sCount;
  }
  
  /**
  * Count by type.
  *
  * @param widgets
  *          the widgets
  * @param widgetTypes
  *          the widget types
  * @return the <code>int</code> object
  */
  static public int countByType(List<Widget> widgets, List<String> widgetTypes) {
    int count = 0;
    for (Widget w : widgets) {
      for (String type : widgetTypes) {
        if (w.getType().equals(type)) {
          count++;
        }
      }
    }
    return count;
  }

  /**
  * finishUp copies what's left inside the BufferedReader 
  * to our StringBuilder object.
  *
  * @return <code>StringBuilder</code> object 
  */
  static public StringBuilder finishUp(BufferedReader br, StringBuilder input) throws CodeGenException {
    // read file and copy to output until end reached
    String line  = "";
    try {
      while ((line = br.readLine()) != null) {
        input.append(line);
        input.append(System.lineSeparator());
      } 
      return input;   
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    }
  }
  
  /**
  * get count from key 
  * strips "$" off of key.
  *
  * @param key
  *          the key
  * @return the <code>String</code> without 'E_' at beginning
  */
  static public String getKeyCount(String key) {
    int n = key.indexOf("$");
    return (key.substring(n+1));
  }
  
  /**
  * Gets the list of enums.
  *
  * @param widgetTypes
  *          the widget types
  * @return the list of enums
  */
  static public List<String> getListOfEnums(List<PagePane> pages, List<String> widgetTypes) {
    // build up a list of widgets that match
    List<String> eList = new ArrayList<String>();
    List<WidgetModel> mList = new ArrayList<WidgetModel>();
    for (PagePane p : pages) {
      getModelsByType(p.getWidgets(), widgetTypes, mList);
    }
    // now pull out from the models our matching widget's enums
    for (WidgetModel m : mList) {
      eList.add(m.getEnum());
    }
    if (eList.size() > 1) {
      Collections.sort(eList);
    }
    return eList;
  }

  /**
  * Gets the widget models by type.
  *
  * @param widgets
  *          the widgets
  * @param widgetTypes
  *          the widget types
  * @param selected
  *          the selected
  * @return the models by type
  */
  static public void getModelsByType(List<Widget> widgets, List<String> widgetTypes, List<WidgetModel> selected) {
    for (Widget w : widgets) {
      for (String type : widgetTypes) {
        if (w.getType().equals(type)) {
          selected.add(w.getModel());
        }
      }
    }
  }

  /**
  * Has type will give back the index into the type list, if found
  *
  * @param widget model
  *          the widget model
  * @param widgetTypes
  *          the widget types
  * @return the <code>int</code> index within the widget type list, or -1 if not found
  */
  static public int hasType(WidgetModel m, String[] widgetTypes) {
    for (int i=0; i<widgetTypes.length; i++) {
      if (m.getType().equals(widgetTypes[i])) {
        return i;
      } 
    }
    return -1;
  }

  /**
  * findTag
  * Continue reading the buffered reader but throw away all input 
  * until finding the end string. Then write the end string output
  * to our StringBuilder object.
  *
  * @param br
  *          the BufferedReader
  * @param sBd
  *          the StringBuilder
  * @param endString
  *          the end string
  * @throws IOException
  *           Signals that an I/O exception has occurred.
  */
  static public void findTag(BufferedReader br, StringBuilder sBd, String endString) throws IOException {
    String line = null;
    String sTestTag = "";
    while ((line = br.readLine()) != null) {
      sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
      if (sTestTag.equals(endString)) {
        break;
      }
    }
    sBd.append(line);
    sBd.append(System.lineSeparator());
  }

  /**
  * Sort list and remove duplicates.
  *
  * @param list
  *          the list
  */
  static public void sortListandRemoveDups(List<String> list) {
    if (list.size() > 1) {
      Collections.sort(list);
      String s = null;
      String prev = null;
      ListIterator<String> litr = list.listIterator();
      while(litr.hasNext()) {
        s = litr.next();
        if (s.equals(prev))
        litr.remove();
        else 
        prev = s;
      }
    }
  }

}
