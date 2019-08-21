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

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.codegen.CodeGenerator;
import builder.codegen.CodeUtils;
import builder.codegen.TemplateManager;
import builder.common.FontItem;
import builder.common.FontFactory;
import builder.models.WidgetModel;

/**
 * The Class FontsPipe handles code generation
 * within the "Fonts" tag of our source code.
 * 
 * This section writes out include files or #defines 
 * for fonts used in this project.
 * 
 * @author Paul Conti
 * 
 */
public class FontsPipe extends WorkFlowPipe {

  /** The Constants for tags. */
  private final static String FONTS_TAG              = "//<Fonts !Start!>";
  private final static String FONTS_END_TAG          = "//<Fonts !End!>";
  
  /** The Constants for templates. */
  private final static String FONT_ADAFRUIT_TEMPLATE = "<FONT_ADAFRUIT>"; 
  private final static String FONT_TFT_ESPI_TEMPLATE = "<FONT_TFT_ESPI>"; 
  private final static String FONT_DEFINE_TEMPLATE   = "<FONT_DEFINE>"; 
  private final static String FONT_INCLUDE_TEMPLATE  = "<FONT_INCLUDE>"; 
   
  /** The Constants for macros. */
  private final static String DEFINE_FILE_MACRO      = "DEFINE_FILE";
  private final static String FONT_REF_MACRO         = "FONT_REF";
  private final static String INCLUDE_FILE_MACRO     = "INCLUDE_FILE";
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /**
   * Instantiates a new pipe.
   *
   * @param cg
   *          the cg
   */
  public FontsPipe(CodeGenerator cg) {
    this.cg = cg;
    this.MY_TAG = FONTS_TAG;
    this.MY_END_TAG = FONTS_END_TAG;
  }
  
  /**
   * doCodeGen
   *
   * @see builder.codegen.pipes.WorkFlowPipe#doCodeGen(java.lang.StringBuilder)
   */
  public void doCodeGen(StringBuilder sBd) {
    // setup
    FontFactory ff = FontFactory.getInstance();
    
    // scan thru all of the projects widgets and
    // build up a list of all font display names.
    List<String> fontNames = new ArrayList<String>();
    String name = null;
    for (WidgetModel m : cg.getModels()) {
      name = m.getFontDisplayName();
      if (name != null)
        fontNames.add(name);
    }
    if (fontNames.size() == 0)
      return;
    // sort the names and remove duplicates
    CodeUtils.sortListandRemoveDups(fontNames);
    
    // now use our reduced fontNames list to build up a   
    // list of font items for fonts in use by this project.
    List<FontItem> fonts = new ArrayList<FontItem>();
    for (String s : fontNames) {
      fonts.add(ff.getFontItem(s));
    }

    // do we need to output AdaFruit's include file?
    tm = cg.getTemplateManager();
    // do we need to output AdaFruit's include file?
    if (cg.getTargetPlatform().equals("arduino")) {
      // need to output AdaFruit include
      for (FontItem f : fonts) {
        if (!f.getIncludeFile().equals("NULL")) {
          // This code only affects arduino implementation.
          List<String> adafruitTemplate = tm.loadTemplate(FONT_ADAFRUIT_TEMPLATE);;
          tm.codeWriter(sBd, adafruitTemplate);
          break;
        }
      }
    }

    // do we need to output TFT_eSPI include file?
    if (cg.getTargetPlatform().equals("arduino TFT_eSPI")) {
      // need to output AdaFruit include
      for (FontItem f : fonts) {
        if (!f.getIncludeFile().equals("NULL")) {
          // This code only affects arduino implementation.
          List<String> tft_espiTemplate = tm.loadTemplate(FONT_TFT_ESPI_TEMPLATE);;
          tm.codeWriter(sBd, tft_espiTemplate);
          break;
        }
      }
    }

    // we are ready to output our font information
    tm = cg.getTemplateManager();
    List<String> includeTemplate = null;
    List<String> defineTemplate = null;
    List<String> outputLines = null;
    Map<String, String> map = new HashMap<String,String>();
    
    for (FontItem f : fonts) {
      if (!f.getIncludeFile().equals("NULL")) {
        // This code only affects arduino implementation.
        includeTemplate = tm.loadTemplate(FONT_INCLUDE_TEMPLATE);;
        map.put(INCLUDE_FILE_MACRO, f.getIncludeFile());
        outputLines = tm.expandMacros(includeTemplate, map);
        tm.codeWriter(sBd, outputLines);
      } else if (!f.getDefineFile().equals("NULL") && cg.getTargetPlatform().equals("linux")) {
        // This code only affects linux implementation.
        defineTemplate = tm.loadTemplate(FONT_DEFINE_TEMPLATE);;
        map.put(FONT_REF_MACRO, f.getFontRef());
        map.put(DEFINE_FILE_MACRO, f.getDefineFile());
        outputLines = tm.expandMacros(defineTemplate, map);
        tm.codeWriter(sBd, outputLines);
      }
    }
    
  }

}
  