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
package builder.models;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.events.MsgBoard;
import builder.prefs.GeneralEditor;

/**
 * The Class TxtButtonModel implements the model for the Text Button widget.
 * 
 * @author Paul Conti
 * 
 */
public class TxtButtonModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_TEXT              = 8;
  static private final int PROP_UTF8              = 9;
  static private final int PROP_ROUNDED           = 10;
  static private final int PROP_FILL_EN           = 11;
  static private final int PROP_FRAME_EN          = 12;
  static private final int PROP_TEXT_SZ           = 13;
  static private final int PROP_TEXT_ALIGN        = 14;
  static private final int PROP_CHANGE_PAGE       = 15;
  static private final int PROP_POPUP_SHOW        = 16;
  static private final int PROP_PAGE              = 17;
  static private final int PROP_POPUP_HIDE        = 18;
  static private final int PROP_USE_FLASH         = 19;
  static private final int PROP_DEFAULT_COLORS    = 20;
  static private final int PROP_TEXT_COLOR        = 21;
  static private final int PROP_FRAME_COLOR       = 22;
  static private final int PROP_FILL_COLOR        = 23;
  static private final int PROP_SELECTED_COLOR    = 24;
  
  /** The Property Defaults */
  static public  final String  DEF_TEXT              = "";
  static public  final Boolean DEF_UTF8              = Boolean.FALSE;
  static public  final Boolean DEF_ROUNDED           = Boolean.FALSE;
  static public  final Boolean DEF_FILL_EN           = Boolean.TRUE;
  static public  final Boolean DEF_FRAME_EN          = Boolean.FALSE;
  static public  final Integer DEF_TEXT_SZ           = Integer.valueOf(0);
  static public  final String  DEF_TEXT_ALIGN        = TextModel.ALIGN_CENTER;
  static public  final Boolean DEF_CHANGE_PAGE       = Boolean.FALSE;
  static public  final Boolean DEF_POPUP_SHOW        = Boolean.FALSE;
  static public  final String  DEF_PAGE              = "";
  static public  final Boolean DEF_POPUP_HIDE        = Boolean.FALSE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Boolean DEF_DEFAULT_COLORS    = Boolean.TRUE;
  static public  final Color   DEF_TEXT_COLOR        = Color.WHITE;
  static public  final Color   DEF_FRAME_COLOR       = new Color(0,0,192); // GSLC_COL_BLUE_DK2
  static public  final Color   DEF_FILL_COLOR        = new Color(0,0,128); // GSLC_COL_BLUE_DK4
  static public  final Color   DEF_SELECTED_COLOR    = new Color(0,0,224); // GSLC_COL_BLUE_DK1
  
  static private final int DEF_WIDTH = 80;
  static private final int DEF_HEIGHT= 40;

  /** The ff. */
  private FontFactory ff = null;
  
  /** The cb align. */
  JComboBox<String> cbAlign;
  
  /** The align cell editor. */
  DefaultCellEditor alignCellEditor;

  /**
   * Instantiates a new txt button model.
   */
  public TxtButtonModel() {
    ff = FontFactory.getInstance();
    initProperties();
    initEditors();
  }
  
  /**
   * Initializes the cell editors.
   */
  private void initEditors()
  {
    cbAlign = new JComboBox<String>();
    cbAlign.addItem(TextModel.ALIGN_LEFT);
    cbAlign.addItem(TextModel.ALIGN_CENTER);
    cbAlign.addItem(TextModel.ALIGN_RIGHT);
    alignCellEditor = new DefaultCellEditor(cbAlign);
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.TEXTBUTTON;
    
    data = new Object[25][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_TEXT, String.class, "TXT-202", Boolean.FALSE,"Label",DEF_TEXT);
    String target = ((GeneralModel) GeneralEditor.getInstance().getModel()).getTarget();
    // arduino GFX doesn't support UTF8 only linix with SDL has support
    // so for arduino set UTF8 property to read-only
    if (target.equals("linux")) {
      initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.FALSE,"UTF-8?",DEF_UTF8);
    } else {
      initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.TRUE,"UTF-8?",DEF_UTF8);
    }

    initProp(PROP_ROUNDED, Boolean.class, "COM-012", Boolean.FALSE,"Corners Rounded?",DEF_ROUNDED);
    initProp(PROP_FILL_EN, Boolean.class, "COM-011", Boolean.FALSE,"Fill Enabled?",DEF_FILL_EN);
    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);
    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"External Storage Size",DEF_TEXT_SZ);
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);
    
    initProp(PROP_CHANGE_PAGE, Boolean.class, "TBTN-100", Boolean.FALSE,"Jump to Page?",DEF_CHANGE_PAGE);
    initProp(PROP_POPUP_SHOW, Boolean.class, "TBTN-102", Boolean.FALSE,"Show Popup Page?",DEF_POPUP_SHOW);
    initProp(PROP_PAGE, String.class, "TBNT-101", Boolean.TRUE,"Jump/Popup Page Enum",DEF_PAGE);
    initProp(PROP_POPUP_HIDE, Boolean.class, "TBTN-103", Boolean.FALSE,"Hide Popup Page?",DEF_POPUP_HIDE);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_DEFAULT_COLORS, Boolean.class, "COL-300", Boolean.FALSE,"Use Default Colors?",DEF_DEFAULT_COLORS);
    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.TRUE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.TRUE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.TRUE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.TRUE,"Selected Color",DEF_SELECTED_COLOR);

  }

  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int rowIndex) {
    if (rowIndex == PROP_TEXT_ALIGN)
      return alignCellEditor;
    return null;
  }

  /**
   * changeValueAt
   *
   * @see builder.models.WidgetModel#changeValueAt(java.lang.Object, int)
   */
  @Override
  public void changeValueAt(Object value, int row) {
    // The test for Integer supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      data[row][PROP_VAL_VALUE] = value;
    }
    fireTableCellUpdated(row, COLUMN_VALUE);
    if (row == PROP_DEFAULT_COLORS) {
      // check for switching back and forth
      if (useDefaultColors()) {
        data[PROP_TEXT_COLOR][PROP_VAL_VALUE]=DEF_TEXT_COLOR; 
        data[PROP_FRAME_COLOR][PROP_VAL_VALUE]=DEF_FRAME_COLOR; 
        data[PROP_FILL_COLOR][PROP_VAL_VALUE]=DEF_FILL_COLOR;
        data[PROP_SELECTED_COLOR][PROP_VAL_VALUE]=DEF_SELECTED_COLOR; 
        data[PROP_TEXT_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
      } else {
        data[PROP_TEXT_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
      }
      fireTableCellUpdated(PROP_TEXT_COLOR, COLUMN_VALUE);
      fireTableCellUpdated(PROP_FRAME_COLOR, COLUMN_VALUE);
      fireTableCellUpdated(PROP_FILL_COLOR, COLUMN_VALUE);
      fireTableCellUpdated(PROP_SELECTED_COLOR, COLUMN_VALUE);
    }     
    if (row == PROP_CHANGE_PAGE) {
      if (isChangePage()) {
        data[PROP_POPUP_SHOW][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
      } else {
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
      }
      fireTableCellUpdated(PROP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_SHOW, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_HIDE, COLUMN_VALUE);
    }
    if (row == PROP_POPUP_SHOW) {
      if (isShowPopup()) {
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
      } else {
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
      }
      fireTableCellUpdated(PROP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_CHANGE_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_HIDE, COLUMN_VALUE);
    }
    if (row == PROP_POPUP_HIDE) {
      if (isHidePopup()) {
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_SHOW][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
      } else {
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_VALUE]="";
      }
      fireTableCellUpdated(PROP_CHANGE_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_SHOW, COLUMN_VALUE);
    }
    if (row == PROP_TEXT_SZ) {
      if (getTextStorage() > 0) {
        String strKey = getKey();
        int n = strKey.indexOf("$");
        String strCount = strKey.substring(n + 1, strKey.length());
        if (getElementRef().isEmpty()) {
          setElementRef(new String("m_pElemBtn" + strCount));
          fireTableCellUpdated(PROP_ELEMENTREF, COLUMN_VALUE);
        }
      }
    }
    
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.getInstance().sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        MsgBoard.getInstance().sendRepaint(getKey(),getKey());
      }
    } 
  }

  /**
   * Use Flash API.
   *
   * @return <code>true</code>, if flash is to be used
   */
  @Override
  public boolean useFlash() {
    return ((Boolean) data[PROP_USE_FLASH][PROP_VAL_VALUE]).booleanValue();
  }
  
  /**
   * Checks if is utf8.
   *
   * @return true, if is utf8
   */
  public boolean isUTF8() {
    return ((Boolean) data[PROP_UTF8][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if buttons are round
   *
   * @return true, if they are round
   */
  public boolean isRoundedEn() {
    return ((Boolean) data[PROP_ROUNDED][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is fill enabled.
   *
   * @return true, if is fill enabled
   */
  public boolean isFillEnabled() {
    return ((Boolean) data[PROP_FILL_EN][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is frame enabled.
   *
   * @return true, if is frame enabled
   */
  public boolean isFrameEnabled() {
    return ((Boolean) data[PROP_FRAME_EN][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the alignment.
   *
   * @return the alignment
   */
  public String getAlignment() {
    return (String) data[PROP_TEXT_ALIGN][PROP_VAL_VALUE];
  }
  
  /**
   * Gets the element ref.
   *
   * @return the element ref
   */
  public String getElementRef() {
    return (String) data[PROP_ELEMENTREF][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the element ref.
   *
   * @param s
   *          the new element ref
   */
  public void setElementRef(String s) { 
    shortcutValue(s, PROP_ELEMENTREF);
  }
  
  /**
   * Gets the text storage.
   *
   * @return the text storage
   */
  public int getTextStorage() {
    return (((Integer) (data[PROP_TEXT_SZ][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Checks if is change page funct.
   *
   * @return true, if is change page funct
   */
  public boolean isChangePage() {
    return ((Boolean) data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is show popup page funct.
   *
   * @return true, if is show popup page funct
   */
  public boolean isShowPopup() {
    return ((Boolean) data[PROP_POPUP_SHOW][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is hide popup page funct.
   *
   * @return true, if is hide popup page funct
   */
  public boolean isHidePopup() {
    return ((Boolean) data[PROP_POPUP_HIDE][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the change page enum.
   *
   * @return the change page enum
   */
  public String getChangePageEnum() {
    return ((String) data[PROP_PAGE][PROP_VAL_VALUE]);
  }

  /**
   * Gets the font display name.
   *
   * @return the font display name
   */
  @Override
  public String getFontDisplayName() {
    return (String) ((String)data[PROP_FONT][PROP_VAL_VALUE]);
  }
  
  /**
   * Gets the font enum.
   *
   * @return the font enum
   */
  @Override
  public String getFontEnum() {
    return ff.getFontEnum(getFontDisplayName());
  }
  
  /**
   * Gets the text.
   *
   * @return the text
   */
  public String getText() {
    return ((String) data[PROP_TEXT][PROP_VAL_VALUE]);
  }

  /**
   * Gets the text color.
   *
   * @return the text color
   */
  public Color getTextColor() {
    return (((Color) data[PROP_TEXT_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Use default colors.
   *
   * @return <code>true</code>, if successful
   */
  public boolean useDefaultColors() {
    return ((Boolean) data[PROP_DEFAULT_COLORS][PROP_VAL_VALUE]).booleanValue();
  }
  
  /**
   * Gets the fill color.
   *
   * @return the fill color
   */
  public Color getFillColor() {
    return (((Color) data[PROP_FILL_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the frame color.
   *
   * @return the frame color
   */
  public Color getFrameColor() {
    return (((Color) data[PROP_FRAME_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the selected color.
   *
   * @return the selected color
   */
  public Color getSelectedColor() {
    return (((Color) data[PROP_SELECTED_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * readModel() will deserialize our model's data from a string object for backup
   * and recovery.
   *
   * @param in
   *          the in stream
   * @param widgetType
   *          the widget type
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   * @see builder.models.WidgetModel#readModel(java.io.ObjectInputStream, java.lang.String)
   */
  @Override
  public void readModel(ObjectInputStream in, String widgetType) 
      throws IOException, ClassNotFoundException {
    super.readModel(in,  widgetType);
    if (useDefaultColors()) {
      data[PROP_TEXT_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
      data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
      data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
    } else {
      data[PROP_TEXT_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
      data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
      data[PROP_FILL_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_SELECTED_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
    }
    if (isChangePage()) {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
    } else if (isShowPopup()) {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
    } else if (isHidePopup()) {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).equals("Left"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = "GSLC_ALIGN_MID_LEFT";
     else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).equals("Right"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = "GSLC_ALIGN_MID_RIGHT";
     else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).equals("Center"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = "GSLC_ALIGN_MID_MID";
  }     

}
