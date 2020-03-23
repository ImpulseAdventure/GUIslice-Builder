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
import java.util.Collections;
import java.util.List;

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.Tags;
import builder.codegen.TemplateManager;
import builder.common.EnumFactory;
import builder.models.KeyPadModel;
import builder.models.KeyPadTextModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.NumKeyPadEditor;
import builder.views.PagePane;

/**
 * The Class EnumPipe handles code generation
 * within the "Enum" tag of our source code.
 * 
 * This section builds up a list of GUISlice ENUMs in use.
 * 
 * @author Paul Conti
 * 
 */
public class EnumPipe extends WorkFlowPipe {
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the code generator 
   */
  public EnumPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = Tags.TAG_PREFIX+Tags.ENUM_TAG+Tags.TAG_SUFFIX_START;
    this.MY_END_TAG = Tags.TAG_PREFIX+Tags.ENUM_TAG+Tags.TAG_SUFFIX_END;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  @Override
  public void doCodeGen(StringBuilder sBd) {
    // first find our Page enums and output them
    List<String> enumList = new ArrayList<String>();
    for (PagePane p : cg.getPages()) {
      enumList.add(p.getEnum());
    }
    // now handle any keypads
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
      enumList.add(EnumFactory.KEYPAD_PAGE_ENUM);
    }
    if (bAddAlphaKeyPad) {
      enumList.add(EnumFactory.ALPHAKEYPAD_PAGE_ENUM);
    }
    // now output all page enums    
    tm = cg.getTemplateManager();
    tm.codeWriterEnums(sBd, enumList);
    
    // Now build up a list of our remaining UI widget enums
    enumList.clear();
    String sEnum = null;
    for (WidgetModel m : cg.getModels()) {
      sEnum = m.getEnum();
      if (!sEnum.equals("GSLC_ID_AUTO")) 
        enumList.add(sEnum);
      if (m.addScrollbar()) {
        enumList.add(m.getScrollbarEnum());
      }
    }
    if (enumList.size() > 0) {
      // Now we have a full list of enum names we can sort the list.
      Collections.sort(enumList);
    }
    // place any keypads at end
    if (bAddNumKeyPad) {
      enumList.add(EnumFactory.KEYPAD_ELEM_ENUM);
    }
    if (bAddAlphaKeyPad) {
      enumList.add(EnumFactory.ALPHAKEYPAD_ELEM_ENUM);
    }
    if (enumList.size() > 0) {
      // Now output the UI widgets enum list
      tm.codeWriterEnums(sBd, enumList);
    }
    // next pass output any group enums
    enumList.clear();
    String name = null;
    for (WidgetModel m : cg.getModels()) {
      name = m.getGroupId();
      if (name != null && !name.equals("GSLC_GROUP_ID_NONE"))
        enumList.add(name);
    }
    if (enumList.size() > 0) {
      // sort the names and remove duplicates
      CodeUtils.sortListandRemoveDups(enumList);
      // Now output the list of group enums
      tm.codeWriterEnums(sBd, enumList);
    }
    // Final pass output any font enums
    enumList.clear();
    // Start with keyboard fonts - bug 126 missing keyboard font
    // place any keypads at end
    if (bAddNumKeyPad) {
      KeyPadModel m = (KeyPadModel)NumKeyPadEditor.getInstance().getModel();
      name = m.getFontEnum();
      if (name != null)
        enumList.add(name);
    }
    if (bAddAlphaKeyPad) {
      KeyPadTextModel m = (KeyPadTextModel)AlphaKeyPadEditor.getInstance().getModel();
      name = m.getFontEnum();
      if (name != null)
        enumList.add(name);
    }

    for (WidgetModel m : cg.getModels()) {
      name = m.getFontEnum();
      if (name != null)
        enumList.add(name);
    }
    // sort the names and remove duplicates
    CodeUtils.sortListandRemoveDups(enumList);
    // add our MAX_FONT enum so we can use gslc_FontSet instead of gslc_FontAdd
    enumList.add("MAX_FONT");
    sBd.append("// Must use separate enum for fonts with MAX_FONT at end to use gslc_FontSet.");
    sBd.append(System.lineSeparator());
    // Now output the font list of enums
    tm.codeWriterEnums(sBd, enumList);
  }

}
