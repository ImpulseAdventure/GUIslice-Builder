/**
 *
 * The MIT License
 *
 * Copyright 2018-2022 Paul Conti
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
import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import builder.Builder;
import builder.common.Utils;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.fonts.FontFactory;
import builder.fonts.FontItem;
import builder.fonts.FontTFT;
import builder.fonts.InputTextField;
import builder.tables.TextTFTCellRenderer;
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeElement;

/**
 * The Class TextModel implements the model for the Text widget.
 * 
 * @author Paul Conti
 */
public class TextModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant ELEMENTREF_NAME. */
  public static final String ELEMENTREF_NAME = "m_pElemOutTxt";

  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_TEXT              = 8;
  static private final int PROP_UTF8              = 9;
  static private final int PROP_TEXT_SZ           = 10;
  static private final int PROP_TEXT_ALIGN        = 11;
  static private final int PROP_TEXT_MARGIN       = 12;
  static private final int PROP_FILL_EN           = 13;
  static private final int PROP_FRAME_EN          = 14;
  static private final int PROP_USE_FLASH         = 15;
  static private final int PROP_TEXT_COLOR        = 16;
  static private final int PROP_FRAME_COLOR       = 17;
  static private final int PROP_FILL_COLOR        = 18;
  static private final int PROP_SELECTED_COLOR    = 19;

  /** The Property Defaults */
  static public  final String  DEF_TEXT              = "";
  static public  final Boolean DEF_UTF8              = Boolean.FALSE;
  static public  final Integer DEF_TEXT_SZ           = Integer.valueOf(0);
  static public  final String  DEF_TEXT_ALIGN        = FontTFT.ALIGN_LEFT;
  static public  final Integer DEF_TEXT_MARGIN       = Integer.valueOf(0);
  static public  final Boolean DEF_FILL_EN           = Boolean.TRUE;
  static public  final Boolean DEF_FRAME_EN          = Boolean.FALSE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Color   DEF_TEXT_COLOR        = Color.YELLOW;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 60;
  static private final int DEF_HEIGHT= 10;
  
  /** The ff. */
  private FontFactory ff = null;
  
  /** The scaled width. */
//  private int scaledWidth = 0;
  
  /** The scaled height. */
//  private int scaledHeight = 0;
  
  /** The cb align. */
  JComboBox<String> cbAlign;
  
  /** The align cell editor. */
  DefaultCellEditor alignCellEditor;
  
  private InputTextField textBox = new InputTextField(DEF_TEXT);
  private DefaultCellEditor editorText;
  private TextTFTCellRenderer rendererText;

  /**
   * Instantiates a new text model.
   */
  public TextModel() {
    ff = FontFactory.getInstance();
    initEditors();
    initProperties();
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
    FontTFT tmpfont = ff.getFont(ff.getDefFontName());
    textBox.setFontTFT(ff, tmpfont);
    editorText = new DefaultCellEditor(textBox);
    rendererText = new TextTFTCellRenderer();
    rendererText.setFontTFT(ff, tmpfont);
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.TEXT;
    data = new Object[20][5];

    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_TEXT, String.class, "TXT-201", Boolean.FALSE,"Text",DEF_TEXT);
    
    if(Controller.getTargetPlatform().equals(ProjectModel.PLATFORM_LINUX)) { 
      initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.FALSE,"UTF-8?",DEF_UTF8);
    } else {
      initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.TRUE,"UTF-8?",DEF_UTF8);
    }
    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"External Storage Size",DEF_TEXT_SZ);
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);
    initProp(PROP_TEXT_MARGIN, Integer.class, "TXT-212", Boolean.FALSE,"Text Margin",DEF_TEXT_MARGIN);
    initProp(PROP_FILL_EN, Boolean.class, "COM-011", Boolean.FALSE,"Fill Enabled?",DEF_FILL_EN);
    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.FALSE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);
    
  }

  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int row) {
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
   * setValueAt
   *
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    if (row == PROP_TEXT_SZ) {
      try {
        int size = Integer.parseInt((String) value);
        if (size <= 0) {
          JOptionPane.showMessageDialog(null, 
              "Field Size must be > 0", 
              "ERROR",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, 
            "Field must be valid integer number", 
            "ERROR",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    super.setValueAt(value, row, col);
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
/*
    if (row == PROP_X) {
      calcSizes(true);
    } 
    if (row == PROP_Y) {
      calcSizes(true);
    }
*/ 
    if (row == PROP_TEXT) {
      data[PROP_TEXT][PROP_VAL_VALUE] = removeInvalidChars((String)value);
      fireTableCellUpdated(PROP_TEXT, COLUMN_VALUE);
      if (getTextStorage() == 0)
        calcSizes(true);
    } 
    if (row == PROP_FONT) {
      String fontName = getFontDisplayName();
      FontTFT myFont = ff.getFont(fontName);
      textBox.setFontTFT(ff, myFont);
      rendererText.setFontTFT(ff, myFont);
      calcSizes(true);
      fireTableCellUpdated(PROP_TEXT, COLUMN_VALUE);
    } 
    if (row == PROP_TEXT_ALIGN) {
      calcSizes(true);
    }
    if (row == PROP_TEXT_SZ) {
      if (getTextStorage() > 0) {
        if (getElementRef().isEmpty()) {
          setElementRef(Utils.createElemName(getKey(), ELEMENTREF_NAME));
          fireTableCellUpdated(PROP_ELEMENTREF, COLUMN_VALUE);
        }
        calcSizes(true);
      }
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
   * getWidth
   *
   * @see builder.models.WidgetModel#getWidth()
   */
/*
  public int getWidth() {
    // this is complicated by users needing to change size
    // return the larger value, either the scaled value or user defined
    if (getTargetWidth() > scaledWidth)
      return getTargetWidth();
    return scaledWidth;
  }
*/  
  /**
   * getHeight
   *
   * @see builder.models.WidgetModel#getHeight()
   */
/*
  public int getHeight() {
    // this is complicated by users needing to change size
    // return the larger value, either the scaled value or user defined
    if (getTargetHeight() > scaledHeight)
      return getTargetHeight();
    return scaledHeight;
  }
*/
  /**
   * Gets the target width.
   *
   * @return the target width
   */
  public int getTargetWidth() {
    return (((Integer) (data[PROP_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the target height.
   *
   * @return the target height
   */
  public int getTargetHeight() {
    return (((Integer) (data[PROP_HEIGHT][PROP_VAL_VALUE])).intValue());
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
   * Test each character to determine if its in the chosen font
   * @param s
   * @return a valid string
   */
  public String removeInvalidChars(String s) {
    String ret = "";
    boolean bError = false;
    int len = s.length();
    if (len > 0) {
      int cp;
      String fontName = getFontDisplayName();
      FontTFT myFont = ff.getFont(fontName);
      if (myFont == null)
        return s;
      for (int i = 0; i < len; i++) {
        cp = s.codePointAt(i);
        if (myFont.canDisplay(cp)) {
          ret = ret + (char)cp;
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
    return ret;
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
    GUIsliceThemeElement element = theme.getElement("Text");
    if (element != null) {
      data[PROP_FILL_EN][PROP_VAL_VALUE] = element.isCornersRounded();
      data[PROP_FRAME_EN][PROP_VAL_VALUE] = element.isFrameEnabled();
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
   super.readModel(in,  widgetType);
   if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("left"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = FontTFT.ALIGN_LEFT;
     else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("right"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = FontTFT.ALIGN_RIGHT;
     else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("center"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = FontTFT.ALIGN_CENTER;
   calcSizes(false);
 }

 /**
  * <p>
  * calcSizes() - This routine is complicated because we use one font size on our display
  * vs the font size we will be using on the target TFT screen.
  * FontItem already has created a scaled font for our display but we want to show
  * width and height to the user as the target TFT's width and height of our text.
  * </p>
  * 
  * @param fireUpdates indicates that we should notify JTable of changes
  */
  @Override
  public void calcSizes(boolean fireUpdates) {

    // next does the current font exist? 
    // if we changed target plaform we might need to change font to default
    String name = getFontDisplayName();
    if (name == null || name.isEmpty()) return;
    FontItem item = ff.getFontItem(name);
    if (item == null) return;
    if (!item.getDisplayName().equals(name)) {
      data[PROP_FONT][PROP_VAL_VALUE] = item.getDisplayName();
      if (fireUpdates) {
        fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
      }
    }
    FontTFT font = ff.getFont(item.getDisplayName());
    textBox.setFontTFT(ff, font);
    String text = getText();
    if (getTextStorage() > 0) {
      text = "";
      for (int i=0; i<getTextStorage(); i++) {
        text = text + "?";
      }
    } else {
      if (text.isEmpty()) 
        text = "TODO";
    }
    // calculate the sizes of our display text
    if (fireUpdates) {
      // calculate the real sizes of our display text
      Dimension nChSz = ff.getTextBounds(getX(),getY(),font, text);
      setWidth(nChSz.width+(getTextMargin()*2)); // account for margin
      setHeight(nChSz.height+1); // account for frame
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
      fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
    }
  }

}
