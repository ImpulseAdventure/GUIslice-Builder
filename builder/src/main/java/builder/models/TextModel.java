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
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import builder.Builder;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.common.FontItem;
import builder.controller.Controller;
import builder.events.MsgBoard;

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

  /** Text Alignment constants */
  static public  final String  ALIGN_LEFT            = "GSLC_ALIGN_MID_LEFT";
  static public  final String  ALIGN_CENTER          = "GSLC_ALIGN_MID_MID";
  static public  final String  ALIGN_RIGHT           = "GSLC_ALIGN_MID_RIGHT";

  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_TEXT              = 8;
  static private final int PROP_UTF8              = 9;
  static private final int PROP_TEXT_SZ           = 10;
  static private final int PROP_TEXT_ALIGN        = 11;
  static private final int PROP_FILL_EN           = 12;
  static private final int PROP_FRAME_EN          = 13;
  static private final int PROP_USE_FLASH         = 14;
  static private final int PROP_TEXT_COLOR        = 15;
  static private final int PROP_FRAME_COLOR       = 16;
  static private final int PROP_FILL_COLOR        = 17;
  static private final int PROP_SELECTED_COLOR    = 18;

  /** The Property Defaults */
  static public  final String  DEF_TEXT              = "";
  static public  final Boolean DEF_UTF8              = Boolean.FALSE;
  static public  final Integer DEF_TEXT_SZ           = Integer.valueOf(0);
  static public  final String  DEF_TEXT_ALIGN        = ALIGN_LEFT;
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

  /**
   * Instantiates a new text model.
   */
  public TextModel() {
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
    cbAlign.addItem(ALIGN_LEFT);
    cbAlign.addItem(ALIGN_CENTER);
    cbAlign.addItem(ALIGN_RIGHT);
    alignCellEditor = new DefaultCellEditor(cbAlign);
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.TEXT;
    data = new Object[19][5];

    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_TEXT, String.class, "TXT-201", Boolean.FALSE,"Text",DEF_TEXT);
    initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.FALSE,"UTF-8?",DEF_UTF8);
    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"External Storage Size",DEF_TEXT_SZ);
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);
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
    if (row == PROP_TEXT) {
      if (getTextStorage() == 0)
        calcSizes(true);
    } 
    if (row == PROP_FONT) {
      calcSizes(true);
    } 
    if (row == PROP_TEXT_SZ) {
      if (getTextStorage() > 0) {
        if (getElementRef().isEmpty()) {
          setElementRef(CommonUtils.createElemName(getKey(), ELEMENTREF_NAME));
          fireTableCellUpdated(PROP_ELEMENTREF, COLUMN_VALUE);
        }
        calcSizes(true);
      }
    }
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.getInstance().sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        Controller.sendRepaint();
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
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = TextModel.ALIGN_LEFT;
     else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("right"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = TextModel.ALIGN_RIGHT;
     else if (((String)data[PROP_TEXT_ALIGN][PROP_VAL_VALUE]).toLowerCase().equals("center"))
      data[PROP_TEXT_ALIGN][PROP_VAL_VALUE] = TextModel.ALIGN_CENTER;
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
  public void calcSizes(boolean fireUpdates) {

    // next does the current font exist? 
    // if we changed target plaform we might need to change font to default
    String name = getFontDisplayName();
    FontItem item = ff.getFontItem(name);
    if (item == null) {
      Builder.logger.error("calcSizes failed, font missing: " + name);
      return;
    }
    if (!item.getDisplayName().equals(name)) {
      data[PROP_FONT][PROP_VAL_VALUE] = item.getDisplayName();
      if (fireUpdates) {
        fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
      }
    }
    Font font = ff.getFont(item.getDisplayName());
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
      Dimension nChSz = ff.measureText(getFontDisplayName(), font, text);
      setWidth(nChSz.width);
      setHeight(nChSz.height);
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
      fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
    }
  }

  /**
   * Copy selected properties from another model.
   * Called by the CopyPropsCommand.
   * @param checklistData
   *          the widget model
   */
  @Override
  public void copyProperties(Object checklistData[][]) {
    super.copyProperties(checklistData);
    calcSizes(true);
  }
  
  /**
   * Paste properties from the PasteCommand.
   *
   * @param m
   *          the widget model
   * @param x
   *          the x
   * @param y
   *          the y
   */
  @Override
  public void pasteProps(WidgetModel m, int x, int y) {
    super.pasteProps(m,x,y);
    calcSizes(false);
  }

}
