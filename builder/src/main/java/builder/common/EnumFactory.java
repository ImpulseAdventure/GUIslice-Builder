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
package builder.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.List;

import builder.codegen.CodeUtils;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * 
 * A factory for creating Enum objects.
 * <p>
 * NOTE: the Enums are not Java Language Enums but will be used by
 * the code generator as C language enums within the generated C skeleton.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class EnumFactory {
  
  /** The instance. */
  private static EnumFactory instance = null;
  
  /** The count keys. */
  private static int[] countKeys;
  
  /** The Constant numberOfTypes. */
  static final public int numberOfTypes  = 31;
  
  /** The Constant GENERAL. */
  static final public String PROJECT     = "Project"; 
  
  /** The Constant GENERAL. */
  static final public String GENERAL     = "General"; 
  
  /** The Constant GRID. */
  static final public String GRID        = "Grid";    
  
  /** The Constant BASEPAGE. */
  static final public String BASEPAGE    = "BasePage";
  
  /** The Constant PAGE. */
  static final public String PAGE        = "Page";
  
  /** The Constant BOX. */
  static final public String BOX         = "Box";
  
  /** The Constant CHECKBOX. */
  static final public String CHECKBOX    = "CheckBox";
  
  /** The Constant CIRCLE. */
  static final public String CIRCLE      = "Circle";
  
  /** The Constant LINE. */
  static final public String LINE        = "Line";
  
  /** The Constant LISTBOX. */
  static final public String LISTBOX     = "ListBox";
  
  /** The Constant NUMINPUT. */
  static final public String NUMINPUT    = "NumberInput";
  
  /** The Constant IMAGEBUTTON. */
  static final public String IMAGEBUTTON = "ImageButton";
  
  /** The Constant IMAGE. */
  static final public String IMAGE       = "Image";
  
  /** The Constant POPUP. */
  static final public String POPUP       = "Popup";
  
  /** The Constant PROGRESSBAR. */
  static final public String PROGRESSBAR = "ProgressBar";
  
  /** The Constant RADIOBUTTON. */
  static final public String RADIOBUTTON = "RadioButton";
  
  /** The Constant RINGGAUGE. */
  static final public String RAMPGAUGE   = "RampGauge";
  
  /** The Constant RINGGAUGE. */
  static final public String RADIALGAUGE = "RadialGauge";
  
  /** The Constant RINGGAUGE. */
  static final public String RINGGAUGE =   "RingGauge";
  
  /** The Constant SEEKBAR. */
  static final public String SEEKBAR      = "Seekbar";
  
  /** The Constant SLIDER. */
  static final public String SLIDER      = "Slider";
  
  /** The Constant SPINNER. */
  static final public String SPINNER     = "Spinner";
  
  /** The Constant TEXT. */
  static final public String TEXT        = "Text";
  
  /** The Constant TEXTBOX. */
  static final public String TEXTBOX     = "TextBox";
  
  /** The Constant TEXTBUTTON. */
  static final public String TEXTBUTTON  = "TextButton";
  
  /** The Constant TEXTINPUT. */
  static final public String TEXTINPUT   = "TextInput";
  
  /** The Constant TOGGLEBUTTON. */
  static final public String TOGGLEBUTTON= "ToggleButton";
  
  /** The Constant GRAPH. */
  static final public String GRAPH       = "Graph";
  
  /** The Constant GROUPID. */
  static final public String GROUPID     = "E_GROUP";
  
  /** The Constant WIDGET. */
  static final public String WIDGET      = "Widget";  // used as a key to find our models
  
  /** The Constants for KeyPads used mostly by code generation. */
  static final public String NUMKEYPAD                = "KeyPad";  // widget type
  static final public String KEYPAD_PAGE_ENUM         = "E_POP_KEYPAD";
  static final public String KEYPAD_ELEM_ENUM         = "E_ELEM_KEYPAD";
  static final public String KEYPAD_PAGE_STORAGE      = "m_asKeypadElem";
  static final public String KEYPAD_ELEM_STORAGE      = "m_sKeyPad";
  static final public String KEYPAD_ELEMREF           = "m_pElemKeyPad";
 
  static final public String ALPHAKEYPAD              = "KeyPadText";  // widget type
  static final public String ALPHAKEYPAD_PAGE_ENUM    = "E_POP_AKEYPAD";
  static final public String ALPHAKEYPAD_ELEM_ENUM    = "E_ELEM_AKEYPAD";
  static final public String ALPHAKEYPAD_PAGE_STORAGE = "m_asAKeypadElem";
  static final public String ALPHAKEYPAD_ELEM_STORAGE = "m_sAKeyPad";
  static final public String ALPHAKEYPAD_ELEMREF      = "m_pAElemKeyPad";
  
  // text boxes hidden scrollbar enums
  static final public String LISTBOX_SCROLLBAR_ENUM   = "E_LISTSCROLL";
  static final public String TEXTBOX_SCROLLBAR_ENUM   = "E_TXTSCROLL";
  static final public String LISTBOX_SCROLLBAR_EREF   = "m_pListSlider";
  static final public String TEXTBOX_SCROLLBAR_EREF   = "m_pTextSlider";
  
 
  /** The type strings. */
  static public String[] typeStrings = 
    { PAGE, 
      BASEPAGE, 
      GENERAL, 
      GRID, 
      BOX, 
      TEXT, 
      TEXTBOX, 
      TEXTBUTTON, 
      CHECKBOX, 
      RADIOBUTTON,  
      IMAGEBUTTON, 
      IMAGE, 
      PROGRESSBAR, 
      SLIDER, 
      SPINNER, 
      GRAPH, 
      GROUPID, 
      WIDGET,
      LISTBOX, 
      NUMINPUT, 
      TEXTINPUT, 
      POPUP, 
      NUMKEYPAD, 
      ALPHAKEYPAD, 
      RINGGAUGE,
      RAMPGAUGE,
      RADIALGAUGE,
      CIRCLE,
      LINE,
      TOGGLEBUTTON,
      SEEKBAR
    };
  
  /** The enum strings. */
  static public String[] enumStrings = 
    { 
      "E_PG",
      "E_PG_BASE",      
      "GENERAL", 
      "GRID", 
      "E_ELEM_BOX", 
      "E_ELEM_TEXT", 
      "E_ELEM_TEXTBOX", 
      "E_ELEM_BTN", 
      "E_ELEM_CHECK", 
      "E_ELEM_RADIO",  
      "E_ELEM_IMAGEBTN", 
      "E_ELEM_IMAGE",
      "E_ELEM_PROGRESS",
      "E_ELEM_SLIDER",
      "E_ELEM_SPINNER",
      "E_ELEM_GRAPH",
      "E_GROUP",
      "W_",
      "E_ELEM_LISTBOX",
      "E_ELEM_NUMINPUT",
      "E_ELEM_TEXTINPUT",
      "E_PG_POPUP",
      "E_POP_KEYPAD",
      "E_POP_AKEYPAD",
      "E_ELEM_RINGGAUGE",
      "E_ELEM_RAMPGAUGE",
      "E_ELEM_RADIALGAUGE",
      "E_DRAW_CIRCLE",
      "E_DRAW_LINE",
      "E_ELEM_TOGGLE",
      "E_ELEM_SEEKBAR"
    };
  
  /** The Constant PAGE_MAIN. */
  static final public String PAGE_MAIN = "E_PG_MAIN";
  
  /**
   * Gets the single instance of EnumFactory.
   *
   * @return single instance of EnumFactory
   */
  public static synchronized EnumFactory getInstance() {
    if (instance == null) {
      instance = new EnumFactory();
    }
    return instance;
  }

  /**
   * Instantiates a new enum factory.
   */
  public EnumFactory() {
    countKeys = new int[EnumFactory.numberOfTypes];
    clearCounts();
  }

  /**
   * This will generate a unique key string for our widget. It's required by our
   * views to ID all UI pieces. WE can't use our Enum strings as the key because
   * users have to be able to edit or change them to apply semantic meanings to
   * the UI pieces. For example, we would generate E_ELEM_BTN_1 for a button while
   * a user might rename it E_ELEM_BTN_QUIT. The returned string will be the
   * original type with a unique number appended to the end.
   *
   * @param type
   *          - from the set of EnumFactory.types returns key String.
   * @return the string
   * @see builder.widgets.Widget
   * @see java.lang.String
   */
  public String createKey(String type) {
    String key = null;
    int i = 0;
    for(i=0; i<EnumFactory.numberOfTypes; i++) {
      if (type.equals(EnumFactory.typeStrings[i])) {
        break;
      }
    }
    countKeys[i]++;
    key = String.format("%s$%d", EnumFactory.typeStrings[i],countKeys[i]);
    return key;
  }
  
  /**
   * <p>
   * This will generate a unique enum string for our widget. It's required by
   * GUIslice runtime to ID all UI pieces. The returned string will be one of the
   * enumStrings[] array where the original type is used as the index key, a
   * unique number is then appended to the end.
   * </p>
   * 
   * @param type
   *          - from the set of EnumFactory.types returns Enum String.
   * @return the string
   * @see builder.widgets.Widget
   * @see java.lang.String
   */
  public String createEnum(String type) {
    String strEnum = "";
    int i = 0;
    for(i=0; i<EnumFactory.numberOfTypes; i++) {
      if (type.equals(EnumFactory.typeStrings[i])) {
        strEnum = EnumFactory.enumStrings[i];
        break;
      }
    }
    if (type.equals(PAGE) && countKeys[i] == 1)
      return EnumFactory.PAGE_MAIN;
    if (type.equals(BASEPAGE))
      return strEnum;
    return String.format("%s%d", strEnum,countKeys[i]);
  }
  
  /**
   * Clear counts.
   */
  public void clearCounts() {
    for (int i=0; i<(EnumFactory.numberOfTypes); i++) {
      countKeys[i]= 0;
    }
  }

  public int getPageCount() {
    return countKeys[0];
  }
  
  public void resetCounts(List<PagePane> pages) {
    String sType = "";
    String sCount = "";
    int c = 0;
    for (PagePane p : pages) {
      sType = p.getPageType();
      sCount = CodeUtils.getKeyCount(p.getKey());
      c = Integer.valueOf(sCount);
      resetCount(sType, c);
      List<Widget> widgets = p.getWidgets();
      for (Widget w : widgets) {
        sType = w.getType();
        sCount = CodeUtils.getKeyCount(w.getKey());
        c = Integer.valueOf(sCount);  // added for bug 130 Wrong count keys causes dup elements
        resetCount(sType, c);
      }
    }
  }
  
  public void resetCount(String type, int c) {
    int idx = -1;
    for(int i=0; i<EnumFactory.numberOfTypes; i++) {
      if (type.equals(EnumFactory.typeStrings[i])) {
        idx = i;
        break;
      }
    }
    if (idx == -1) return;
    if (countKeys[idx] < c)
      countKeys[idx] = c;
  }
  
  /**
   * Backup the current state of this object as a String.
   *
   * @return the <code>String</code> object
   */
  public String backup() {
    try {
//      System.out.println("enum backup====");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
      int nodeCount = EnumFactory.numberOfTypes;
      out.writeInt(nodeCount);
      for(int i=0; i<nodeCount; i++) {
        out.writeInt(countKeys[i]);
        out.writeInt(countKeys[i]);
      }
      out.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Restore the state of this object from the String previously
   * created by Backup().
   *
   * @param state
   *          the state
   */
  public void restore(String state) {
    try {
//      System.out.println("enum restore====");
      @SuppressWarnings("unused")
      int skip=0;
      byte[] data = Base64.getDecoder().decode(state);
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
      int nodeCount = in.readInt();
      for (int i=0; i<nodeCount; i++) {
        countKeys[i] = in.readInt();
        skip = in.readInt();
      }
      in.close();
    } catch (IOException e) {
      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
    }
  }
}
