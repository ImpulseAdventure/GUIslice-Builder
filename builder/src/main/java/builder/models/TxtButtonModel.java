/**
 *
 * The MIT License
 *
 * Copyright 2018-2023 Paul Conti
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
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import builder.Builder;
import builder.commands.PropertyCommand;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.fonts.FontFactory;
import builder.fonts.FontItem;
import builder.fonts.FontTFT;
import builder.fonts.InputTextField;
import builder.tables.MultiStringsCell;
import builder.tables.MultipeLineCellListener;
import builder.tables.TextTFTCellRenderer;
import builder.tables.MultiStringsCell.MCDialogType;
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeElement;

/**
 * The Class TxtButtonModel implements the model for the Text Button widget.
 * 
 * @author Paul Conti
 * 
 */
public class TxtButtonModel extends WidgetModel implements MultipeLineCellListener { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_TEXT              = 8;
//  static private final int PROP_UTF8              = 9;
  static private final int PROP_ROUNDED           = 9;
  static private final int PROP_FILL_EN           = 10;
  static private final int PROP_FRAME_EN          = 11;
  static private final int PROP_TEXT_SZ           = 12;
  static private final int PROP_TEXT_ALIGN        = 13;
  static private final int PROP_TEXT_MARGIN       = 14;
  static private final int PROP_CODE              = 15;
  static private final int PROP_JUMP_PAGE         = 16;
  static private final int PROP_POPUP_PAGE        = 17;
  static private final int PROP_POPUP_HIDE        = 18;
  static private final int PROP_USE_FLASH         = 19;
  static private final int PROP_TEXT_COLOR        = 20;
  static private final int PROP_FRAME_COLOR       = 21;
  static private final int PROP_FILL_COLOR        = 22;
  static private final int PROP_SELECTED_COLOR    = 23;
  
  /** The Property Defaults */
  static public  final String  DEF_TEXT              = "";
  static public  final Boolean DEF_UTF8              = Boolean.FALSE;
  static public  final Boolean DEF_ROUNDED           = Boolean.FALSE;
  static public  final Boolean DEF_FILL_EN           = Boolean.TRUE;
  static public  final Boolean DEF_FRAME_EN          = Boolean.TRUE;
  static public  final Integer DEF_TEXT_SZ           = Integer.valueOf(0);
  static public  final String  DEF_TEXT_ALIGN        = FontTFT.ALIGN_CENTER;
  static public  final Integer DEF_TEXT_MARGIN       = Integer.valueOf(0);
  static public  final String[] DEF_CODE             = { "" };
  static public  final Boolean DEF_POPUP_HIDE        = Boolean.FALSE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
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

  /** The optional code segment for our button */
  MultiStringsCell codeCell;

  private InputTextField textBox = new InputTextField(DEF_TEXT);
  private DefaultCellEditor editorText;
  private TextTFTCellRenderer rendererText;

  /**
   * Instantiates a new text button model.
   */
  public TxtButtonModel() {
    ff = FontFactory.getInstance();
    initProperties();
    initEditors();
    calcSizes(false);
  }
  
  /**
   * Initializes the cell editors.
   */
  private void initEditors()
  {
    cbAlign = new JComboBox<String>();
    cbAlign.addItem(FontTFT.ALIGN_LEFT);
    cbAlign.addItem(FontTFT.ALIGN_CENTER);
    cbAlign.addItem(FontTFT.ALIGN_RIGHT);
    cbAlign.addItem(FontTFT.ALIGN_TOP_LEFT);
    cbAlign.addItem(FontTFT.ALIGN_TOP_CENTER);
    cbAlign.addItem(FontTFT.ALIGN_TOP_RIGHT);
    cbAlign.addItem(FontTFT.ALIGN_BOT_LEFT);
    cbAlign.addItem(FontTFT.ALIGN_BOT_CENTER);
    cbAlign.addItem(FontTFT.ALIGN_BOT_RIGHT);
    alignCellEditor = new DefaultCellEditor(cbAlign);
    textBox.setFontTFT(ff, null);
    editorText = new DefaultCellEditor(textBox);
    rendererText = new TextTFTCellRenderer();
    rendererText.setFontTFT(ff, null);

    codeCell = new MultiStringsCell("Custom Code Segment", MCDialogType.CODE_DIALOG);
    codeCell.setData(DEF_CODE);
    codeCell.addButtonListener(this);
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.TEXTBUTTON;
    
    data = new Object[24][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_TEXT, String.class, "TXT-202", Boolean.FALSE,"Label",DEF_TEXT);

//    initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.FALSE,"UTF-8?",DEF_UTF8);

    initProp(PROP_ROUNDED, Boolean.class, "COM-012", Boolean.FALSE,"Corners Rounded?",DEF_ROUNDED);
    initProp(PROP_FILL_EN, Boolean.class, "COM-011", Boolean.FALSE,"Fill Enabled?",DEF_FILL_EN);
    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);
    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"External Storage Size",DEF_TEXT_SZ);
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);
    initProp(PROP_TEXT_MARGIN, Integer.class, "TXT-212", Boolean.FALSE,"Text Margin",DEF_TEXT_MARGIN);
    
    initProp(PROP_CODE, String[].class, "TBTN-113", Boolean.FALSE,"Custom Code (optional)",DEF_CODE);
    initProp(PROP_JUMP_PAGE, String.class, "TBNT-101", Boolean.FALSE,"Jump Page ENUM","");
    initProp(PROP_POPUP_PAGE, String.class, "TBTN-104", Boolean.TRUE,"Popup Page Enum","");
    initProp(PROP_POPUP_HIDE, Boolean.class, "TBTN-103", Boolean.FALSE,"Hide Popup Page?",DEF_POPUP_HIDE);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.FALSE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);

  }

  /**
   * setFontReadOnly
   *
   * @see builder.models.WidgetModel#setFontReadOnly()
   */
  @Override
  public void setFontReadOnly() {
    data[PROP_FONT][PROP_VAL_READONLY] = true;
    data[PROP_TEXT][PROP_VAL_READONLY] = true;
    data[PROP_ELEMENTREF][PROP_VAL_READONLY] = true;
    data[PROP_ENUM][PROP_VAL_READONLY] = true;
    data[PROP_FONT][PROP_VAL_VALUE] = "";
    data[PROP_TEXT][PROP_VAL_VALUE] = "";
    data[PROP_ELEMENTREF][PROP_VAL_VALUE] = "";
    data[PROP_ENUM][PROP_VAL_VALUE] = "";
  }

  /**
   * buttonClicked
   *
   * @see builder.tables.MultipeLineCellListener#buttonClicked(java.lang.String[])
   */
  @Override
  public void buttonClicked(String[] strings) {
    // commands are used to support undo and redo actions.
    PropertyCommand c = new PropertyCommand(this, strings, PROP_CODE);
    execute(c);
  }

  /**
   * setValueAt
   *
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    // we handle code segment through a backdoor "buttonClicked"
    if (row == PROP_CODE) return;
    if (col == COLUMN_VALUE) {
      // commands are used to support undo and redo actions.
      PropertyCommand c = new PropertyCommand(this, value, row);
      execute(c);
    }
  }

  /**
   * Gets the optional code segment, if any.
   *
   * @return the code segment
   */
  public String[] getCode() {
    return ((String[]) (data[PROP_CODE][PROP_VAL_VALUE]));
  }

  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int row) {
    if (row == PROP_CODE)
      return codeCell;
    if (row == PROP_TEXT)
      return editorText;
    if (row == PROP_TEXT_ALIGN)
      return alignCellEditor;
    return null;
  }

  /**
   * getRendererAt
   *
   * @see builder.models.WidgetModel#getRendererAt(int)
   */
  @Override
  public TableCellRenderer getRendererAt(int row) {
    if (row == PROP_CODE)
      return codeCell;
    if (row == PROP_TEXT) {
      return rendererText;
    }
    return null;
  }

  /**
   * setFont 
   * @param fontName
   */
  public void setFont(String fontName) {
    data[PROP_FONT][PROP_VAL_VALUE] = fontName;
    fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
    FontTFT myFont = ff.getFont(fontName);
    textBox.setFontTFT(ff, myFont);
    rendererText.setFontTFT(ff, myFont);
    calcSizes(true);
    fireTableCellUpdated(PROP_TEXT, COLUMN_VALUE);
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
    if (row == PROP_CODE) {
      if (getCode() != null && !getCode()[0].isEmpty()) {
        data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
      } else {
        data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
      }
      fireTableStructureChanged();
    }
    if (row > PROP_HEIGHT || row == PROP_ENUM)
      super.setModelChanged();
    if (row == PROP_JUMP_PAGE) {
      if (getJumpPage().isEmpty()) {
        data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
      } else {
        data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
      }
      fireTableCellUpdated(PROP_POPUP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_HIDE, COLUMN_VALUE);
    }
    if (row == PROP_POPUP_PAGE) {
      if (getPopupPage().isEmpty()) {
        data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
      } else {
        data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
      }
      fireTableCellUpdated(PROP_JUMP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_HIDE, COLUMN_VALUE);
    }
    if (row == PROP_POPUP_HIDE) {
      if (isHidePopup()) {
        data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
      } else {
        data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
      }
      fireTableCellUpdated(PROP_JUMP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_PAGE, COLUMN_VALUE);
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
    if (row == PROP_FONT) {
      String fontName = getFontDisplayName();
      FontTFT myFont = ff.getFont(fontName);
      textBox.setFontTFT(ff, myFont);
      rendererText.setFontTFT(ff, myFont);
      calcSizes(true);
      fireTableCellUpdated(PROP_TEXT, COLUMN_VALUE);
    } 
    
    if (row == PROP_TEXT) {
      data[PROP_TEXT][PROP_VAL_VALUE] = removeInvalidChars((String)value);
      fireTableCellUpdated(PROP_TEXT, COLUMN_VALUE);
      calcSizes(true);
    } 

    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
      }
    } 
  }

  /**
   * Gets the text margin.
   *
   * @return the text margin
   */
  public int getTextMargin() {
    return (((Integer) (data[PROP_TEXT_MARGIN][PROP_VAL_VALUE])).intValue());
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
//    return ((Boolean) data[PROP_UTF8][PROP_VAL_VALUE]).booleanValue();
    return false;
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
  public String getJumpPage() {
    return ((String) data[PROP_JUMP_PAGE][PROP_VAL_VALUE]);
  }

  /**
   * Gets the change page enum.
   *
   * @return the change page enum
   */
  public String getPopupPage() {
    return ((String) data[PROP_POPUP_PAGE][PROP_VAL_VALUE]);
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
   * calcSizes() - This routine is really just checking that our font exists
   * 
   * @param fireUpdates indicates that we should notify JTable of changes
   */
   public void calcSizes(boolean fireUpdates) {

     // next does the current font exist? 
     // if we changed target plaform we might need to change font to default
     String name = getFontDisplayName();
     if (name == null || name.isEmpty()) return;
     FontItem item = ff.getFontItem(name);
     if (item == null) return;
     FontTFT font = ff.getFont(item.getDisplayName());
     textBox.setFontTFT(ff, font);
     if (!item.getDisplayName().equals(name)) {
       data[PROP_FONT][PROP_VAL_VALUE] = item.getDisplayName();
       if (fireUpdates) {
         fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
       }
     }
   }

   /**
    * Test each character to determine if its in the chosen font
    * @param s
    * @return a valid string
    */
   public String removeInvalidChars(String s) {
     boolean bError = false;
     int len = s.length();
     StringBuffer sb = new StringBuffer();
     if (len > 0) {
       int cp;
       int ch;
       String fontName = getFontDisplayName();
       FontTFT myFont = ff.getFont(fontName);
       if (myFont == null)
         return s;
       for (int i = 0; i < len; i++) {
         ch = s.charAt(i);
         // test for \n and if so place actual newline character
         if (ch == 92 && i+1 < len) {
           if (s.charAt(i+1) == 'n' ) {
             sb.append((char)10);
             i++;
             continue;
           }
         }
         cp = s.codePointAt(i);
         if (myFont.canDisplay(cp)) {
           sb.append((char)cp);
         } else {
           bError = true;
         }
       }
       if (bError) {
         JOptionPane.showMessageDialog(null, 
             "<html>You have entered characters outside range supported by your chosen font.<br>"+
             "Maybe UTF8 characters with font that only has ASCII?<br>" +
             "You will need to pick a different font or right click to see character map.</html>", 
             "ERROR",
             JOptionPane.WARNING_MESSAGE);
         Builder.logger.debug("characters outside range of font: " + s);
       }
     }
     return sb.toString();
   }
   
   /**
    * 
    * changeThemeColors
    *
    * @see builder.models.WidgetModel#changeThemeColors(builder.themes.GUIsliceTheme)
    */
   @Override
   public void changeThemeColors(GUIsliceTheme theme) {
     if (theme == null) return;
     GUIsliceThemeElement element = theme.getElement("TextButton");
     if (element != null) {
       data[PROP_ROUNDED][PROP_VAL_VALUE] = element.isCornersRounded();
       data[PROP_FRAME_EN][PROP_VAL_VALUE] = element.isFrameEnabled();
       data[PROP_FILL_EN][PROP_VAL_VALUE] = element.isFillEnabled();
       if (element.getTextCol() != null)
         data[PROP_TEXT_COLOR][PROP_VAL_VALUE] = element.getTextCol();
       if (element.getFrameCol() != null)
         data[PROP_FRAME_COLOR][PROP_VAL_VALUE] = element.getFrameCol();
       if (element.getFillCol() != null)
         data[PROP_FILL_COLOR][PROP_VAL_VALUE] = element.getFillCol();
       if (element.getGlowCol() != null)
         data[PROP_SELECTED_COLOR][PROP_VAL_VALUE] = element.getGlowCol();
       fireTableStructureChanged();
     }
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
//  System.out.println("===== WM readModel() ========");
    if (widgetType != null)
      this.widgetType = widgetType;
    bSendEvents = in.readBoolean();
//  System.out.println("bSendEvents: " + bSendEvents);
    int rows = in.readInt();
    String metaID = null;
    Object objectData = null;
    int row;
//  System.out.println("WM rows: " + rows);
    boolean bPopup = false;
    boolean bJump = false;
    String pageEnum = "";
    // in case of upgrade make sure we start fresh
    data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
    data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
    
    /*
     * This is complicated because I decided to remove two booleans
     * Jump to Page? and Show Popup Page? and replace them
     * with simply storing the Jump Page Enum and Popup Page Enum.
     * This avoids the case where someone sets one of the values true
     * but never fills in the page enum to change to.
     * This does make it hard to do an update to a project with the old booleans.
     */
    for (int i = 0; i < rows; i++) {
      metaID = (String) in.readObject();
      objectData = in.readObject();
      if (metaID.equals("TBTN-100")) {
        if (((Boolean)objectData).booleanValue()) {
          bJump = true;
        }
      }
      if (metaID.equals("TBTN-102")) {
        if (((Boolean)objectData).booleanValue()) {
          bPopup = true;
        }
      }
      if (metaID.equals("TBNT-101")) {
        if (((String) objectData) != null &&
            !((String) objectData).isEmpty()) {
          pageEnum = ((String) objectData); 
        }
      }
      row = mapMetaIDtoProperty(metaID);
      if (row >= 0) {
        data[row][PROP_VAL_VALUE] = objectData;
        
//  System.out.println(data[row][PROP_VAL_NAME].toString() + ": " +
//           data[row][PROP_VAL_VALUE].toString() + " mapped to row " + row);
        
      }
    }
    if (bJump) {
      data[PROP_JUMP_PAGE][PROP_VAL_VALUE]=pageEnum;
      data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
      data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
    } else if (bPopup) {
      data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
      data[PROP_POPUP_PAGE][PROP_VAL_VALUE]=pageEnum;
      data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
    }
    if (!getJumpPage().isEmpty()) {
      data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
    } else if (!getPopupPage().isEmpty()) {
      data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
    } else if (isHidePopup()) {
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_JUMP_PAGE][PROP_VAL_VALUE]="";
      data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_PAGE][PROP_VAL_VALUE]="";
    }
    if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("left"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = FontTFT.ALIGN_LEFT;
    else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("right"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = FontTFT.ALIGN_RIGHT;
    else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("center"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = FontTFT.ALIGN_CENTER;
    codeCell.setData((String[])data[PROP_CODE][PROP_VAL_VALUE]);
    if (getCode() != null && !getCode()[0].isEmpty()) {
      data[PROP_JUMP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
    }
    calcSizes(false);
  }     
}
