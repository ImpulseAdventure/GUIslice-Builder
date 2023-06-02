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
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import builder.codegen.CodeUtils;
import builder.commands.PropertyCommand;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.fonts.FontFactory;
import builder.fonts.FontTFT;
import builder.tables.MultipeLineCellListener;
import builder.tables.MultiStringsCell.MCDialogType;
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeElement;
import builder.tables.MultiStringsCell;

/**
 * The Class TextBoxModel implements the model for the Text Box widget.
 * 
 * @author Paul Conti
 * 
 */
public class ListBoxModel extends WidgetModel implements MultipeLineCellListener { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemListbox";
  
  /** The Constant XLISTBOX_BUF_OH_R is the overhead for each row */
  public static final int XLISTBOX_BUF_OH_R = 2;
  
  /** The Property Index Constants. */
  
  static private final int PROP_FONT              = 7;
  static private final int PROP_ITEMS             = 8;
  static private final int PROP_STORAGESZ         = 9;
  static private final int PROP_SELECTED          = 10;
  static private final int PROP_COLS              = 11;
  static private final int PROP_ROWS              = 12;
  static private final int PROP_ITEM_GAP          = 13;
  static private final int PROP_TXT_WIDTH         = 14;
  static private final int PROP_TXT_HEIGHT        = 15;
  static private final int PROP_TXT_MARGIN_WIDTH  = 16;
  static private final int PROP_TXT_MARGIN_HEIGHT = 17;
  static private final int PROP_TEXT_ALIGN        = 18;
  static private final int PROP_FRAME_EN          = 19;
  static private final int PROP_SCROLLBAR         = 20;
  static private final int PROP_SCROLLBAR_WIDTH   = 21;
  static private final int PROP_SCROLLBAR_THUMB   = 22;
  static private final int PROP_SCROLLBAR_ENUM    = 23;
  static private final int PROP_SCROLLBAR_EREF    = 24;
  static private final int PROP_SCROLLBAR_MAX     = 25;
  static private final int PROP_TEXT_COLOR        = 26;
  static private final int PROP_GAP_COLOR         = 27;
  static private final int PROP_FRAME_COLOR       = 28;
  static private final int PROP_FILL_COLOR        = 29;
  static private final int PROP_SELECTED_COLOR    = 30;
  static private final int PROP_BAR_FRAME_COLOR   = 31;
  static private final int PROP_BAR_FILL_COLOR    = 32;
  
  /** The Property Defaults */
  static public  final String[] DEF_ITEMS             = { "" };
  static public  final Integer  DEF_STORAGE           = Integer.valueOf(0);
  static public  final Integer  DEF_SELECTED          = Integer.valueOf(0);
  static public  final Integer  DEF_COLS              = Integer.valueOf(1);
  static public  final Integer  DEF_ROWS              = Integer.valueOf(5);
  static public  final Integer  DEF_ITEM_GAP          = Integer.valueOf(5);
  static public  final Integer  DEF_TXT_WIDTH         = Integer.valueOf(-1);
  static public  final Integer  DEF_TXT_HEIGHT        = Integer.valueOf(-1);
  static public  final Integer  DEF_TXT_MARGIN_WIDTH  = Integer.valueOf(5);
  static public  final Integer  DEF_TXT_MARGIN_HEIGHT = Integer.valueOf(5);
  static public  final String   DEF_TEXT_ALIGN        = FontTFT.ALIGN_LEFT;
  static public  final Boolean  DEF_FRAME_EN          = Boolean.TRUE;
  static public  final Boolean  DEF_SCROLLBAR         = Boolean.TRUE;
  static public  final Integer  DEF_SCROLLBAR_WIDTH   = Integer.valueOf(20);
  static public  final Integer  DEF_SCROLLBAR_THUMB   = Integer.valueOf(5);
  static public  final String   DEF_SCROLLBAR_ENUM    = "";
  static public  final String   DEF_SCROLLBAR_EREF    = "";
  static public  final Integer  DEF_SCROLLBAR_MAX     = Integer.valueOf(100);
  static public  final Color    DEF_TEXT_COLOR        = Color.WHITE;
  static public  final Color    DEF_GAP_COLOR         = Color.BLACK;
  static public  final Color    DEF_FRAME_COLOR       = Color.BLUE;
  static public  final Color    DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color    DEF_SELECTED_COLOR    = Color.BLACK;
  static public  final Color    DEF_BAR_FRAME_COLOR   = Color.BLUE;
  static public  final Color    DEF_BAR_FILL_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 200;
  static private final int DEF_HEIGHT= 100;

  /** The ff. */
  FontFactory  ff = null;

  /** The cb align. */
  JComboBox<String> cbAlign;
  
  /** The align cell editor. */
  DefaultCellEditor alignCellEditor;

  /** The list of items for our ListBox */
  MultiStringsCell itemsCell;

  /**
   * Instantiates a new text box model.
   */
  public ListBoxModel() {
    ff = FontFactory.getInstance();
    initProperties();
    initEditors();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.LISTBOX;
    data = new Object[33][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_ITEMS, String[].class, "LIST-103", Boolean.FALSE,"List of Items",DEF_ITEMS);
    initProp(PROP_STORAGESZ, Integer.class, "LIST-104", Boolean.FALSE,
        "Storage Size",DEF_STORAGE);
    initProp(PROP_SELECTED, Integer.class, "LIST-102", Boolean.FALSE,"Selected Item",DEF_SELECTED);
    initProp(PROP_COLS, Integer.class, "TXT-210", Boolean.FALSE,"Items per Row",DEF_COLS);
    initProp(PROP_ROWS, Integer.class, "TXT-209", Boolean.FALSE,"Display Rows",DEF_ROWS);

    initProp(PROP_SCROLLBAR, Boolean.class, "BAR-112", Boolean.FALSE,"Add Scrollbar?",DEF_SCROLLBAR);
    initProp(PROP_SCROLLBAR_WIDTH, Integer.class, "SLD-111", Boolean.FALSE,"Scrollbar Width",DEF_SCROLLBAR_WIDTH);
    initProp(PROP_SCROLLBAR_THUMB, Integer.class, "SLD-103", Boolean.FALSE,"Scrollbar Thumb Size",DEF_SCROLLBAR_THUMB);
    initProp(PROP_SCROLLBAR_ENUM, String.class, "BAR-113", Boolean.FALSE,"Scrollbar ENUM",DEF_SCROLLBAR_ENUM);
    initProp(PROP_SCROLLBAR_EREF, String.class, "BAR-114", Boolean.FALSE,"Scrollbar EREF",DEF_SCROLLBAR_EREF);
    initProp(PROP_SCROLLBAR_MAX, Integer.class, "BAR-115", Boolean.FALSE,"Scrollbar Max Value",DEF_SCROLLBAR_MAX);

    initProp(PROP_ITEM_GAP, Integer.class, "LIST-106", Boolean.FALSE,"Item Gap",DEF_ITEM_GAP);
    initProp(PROP_TXT_WIDTH, Integer.class, "LIST-110", Boolean.FALSE,"Text Width",DEF_TXT_WIDTH);
    initProp(PROP_TXT_HEIGHT, Integer.class, "LIST-111", Boolean.FALSE,"Text Height",DEF_TXT_HEIGHT);
    initProp(PROP_TXT_MARGIN_WIDTH, Integer.class, "LIST-100", Boolean.FALSE,"Text Margin Width",DEF_TXT_MARGIN_WIDTH);
    initProp(PROP_TXT_MARGIN_HEIGHT, Integer.class, "LIST-101", Boolean.FALSE,"Text Margin Height",DEF_TXT_MARGIN_HEIGHT);
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);

    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);

    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.FALSE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_GAP_COLOR, Color.class, "LIST-107", Boolean.FALSE,"Item Gap Color",DEF_GAP_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);
    initProp(PROP_BAR_FRAME_COLOR, Color.class, "BAR-116", Boolean.FALSE,"Scrollbar Frame Color",DEF_BAR_FRAME_COLOR);
    initProp(PROP_BAR_FILL_COLOR, Color.class, "BAR-117", Boolean.FALSE,"Scrollbar Fill Color",DEF_BAR_FILL_COLOR);

  }

  /**
   * Initializes the alignments.
   */
  private void initEditors()
  {
    cbAlign = new JComboBox<String>();
    cbAlign.addItem(FontTFT.ALIGN_LEFT);
    cbAlign.addItem(FontTFT.ALIGN_CENTER);
    cbAlign.addItem(FontTFT.ALIGN_RIGHT);
    alignCellEditor = new DefaultCellEditor(cbAlign);

    itemsCell = new MultiStringsCell("List of Items", MCDialogType.STRING_DIALOG);
    itemsCell.setData(DEF_ITEMS);
    itemsCell.addButtonListener(this);
  }
  
  /**
   * Sets the key.
   *
   * @param key
   *          the new key
   */
  @Override
  public void setKey(String key) { 
    data[PROP_KEY][PROP_VAL_VALUE] = key;
    String count = CodeUtils.getKeyCount(key);
    String ref = ELEMENTREF_NAME;
    ref = ref + count;
    data[PROP_SCROLLBAR_ENUM][PROP_VAL_VALUE] = EnumFactory.LISTBOX_SCROLLBAR_ENUM+count;
    data[PROP_SCROLLBAR_EREF][PROP_VAL_VALUE] = EnumFactory.LISTBOX_SCROLLBAR_EREF+count;
  }
  
  /**
   * buttonClicked
   *
   * @see builder.tables.MultipeLineCellListener#buttonClicked(java.lang.String[])
   */
  @Override
  public void buttonClicked(String[] strings) {
    // commands are used to support undo and redo actions.
    PropertyCommand c = new PropertyCommand(this, strings, PROP_ITEMS);
    execute(c);
  }

  /**
   * setValueAt
   *
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    // we handle list of items through a backdoor "buttonClicked"
    if (row == PROP_ITEMS) return;
    super.setValueAt(value, row, col);
  }

  /**
   * setFont 
   * @param fontName
   */
  public void setFont(String fontName) {
    data[PROP_FONT][PROP_VAL_VALUE] = fontName;
    fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
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
    if (row == PROP_ITEMS) {
      calcSizes(false);
      fireTableStructureChanged();
    }
    if (row == PROP_FONT) {
      // re-calc number of text rows and columns
      calcSizes(false);
      fireTableCellUpdated(PROP_TXT_HEIGHT, COLUMN_VALUE);
      fireTableCellUpdated(PROP_STORAGESZ, COLUMN_VALUE);
      fireTableCellUpdated(PROP_SCROLLBAR_MAX, COLUMN_VALUE); 
    }
    if (row == PROP_SCROLLBAR) {
      if (addScrollbar()) {
        data[PROP_SCROLLBAR_ENUM][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_SCROLLBAR_EREF][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_SCROLLBAR_MAX][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_BAR_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
        data[PROP_BAR_FILL_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
      } else {
        data[PROP_SCROLLBAR_ENUM][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_SCROLLBAR_EREF][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_SCROLLBAR_MAX][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_BAR_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
        data[PROP_BAR_FILL_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
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
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int row) {
    if (row == PROP_ITEMS)
      return itemsCell;
    else if (row == PROP_TEXT_ALIGN)
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
    if (row == PROP_ITEMS)
      return itemsCell;
    return null;
  }

  /**
   * Get Item gap
   *
   * @return amount of gap
   */
  public int getItemGap() {
    return ((Integer) data[PROP_ITEM_GAP][PROP_VAL_VALUE]).intValue();
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
   * Checks if we need to add a scrollbar.
   *
   * @return true, if we add a scrollbar
   */
  @Override
  public boolean addScrollbar() {
    return ((Boolean) data[PROP_SCROLLBAR][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the scrollbar enum.
   *
   * @return the scrollbar enum
   */
  @Override
  public String getScrollbarEnum() {
    return (String)data[PROP_SCROLLBAR_ENUM][PROP_VAL_VALUE];
  }
  
  /**
   * getScrollbarWidth
   * @return width
   */
  public int getScrollbarWidth() {
    return (((Integer) (data[PROP_SCROLLBAR_WIDTH][PROP_VAL_VALUE])).intValue());
  }

  public int getScrollbarMaxValue() {
    return (((Integer) (data[PROP_SCROLLBAR_MAX][PROP_VAL_VALUE])).intValue());
  }

  /**
   * getScrollbarThumb
   * @return thumb size
   */
  public int getScrollbarThumb() {
    return (((Integer) (data[PROP_SCROLLBAR_THUMB][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the scrollbar eref.
   *
   * @return the scrollbar enum
   */
  @Override
  public String getScrollbarERef() {
    return (String)data[PROP_SCROLLBAR_EREF][PROP_VAL_VALUE];
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
   * Gets the num items.
   *
   * @return the number of items in list
   */
  public String[] getItems() {
    return ((String[]) (data[PROP_ITEMS][PROP_VAL_VALUE]));
  }

  /**
   * Gets the selected item.
   *
   * @return the selected item
   */
  public int getSelectedItem() {
    return (((Integer) (data[PROP_SELECTED][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the number characters per item.
   *
   * @return the number characters per item.
   */
  public int getStorageSz() {
    return (((Integer) (data[PROP_STORAGESZ][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the number items per row
   *
   * @return the number items per row
   */
  public int getNumItemsPerRow() {
    return (((Integer) (data[PROP_COLS][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the number of rows to display.
   *
   * @return the number of rows to display
   */
  public int getRows() {
    return (((Integer) (data[PROP_ROWS][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the number of columns.
   *
   * @return the number of columns
   */
  public int getColumns() {
    return (((Integer) (data[PROP_COLS][PROP_VAL_VALUE])).intValue());
  }

  public int getTextWidth() {
    return (((Integer) (data[PROP_TXT_WIDTH][PROP_VAL_VALUE])).intValue());
  }
  

  public int getTextHeight() {
    return (((Integer) (data[PROP_TXT_HEIGHT][PROP_VAL_VALUE])).intValue());
  }
  
  public int getMarginWidth() {
    return (((Integer) (data[PROP_TXT_MARGIN_WIDTH][PROP_VAL_VALUE])).intValue());
  }
  

  public int getMarginHeight() {
    return (((Integer) (data[PROP_TXT_MARGIN_HEIGHT][PROP_VAL_VALUE])).intValue());
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
   * Gets the gap color.
   *
   * @return the gap color
   */
  public Color getGapColor() {
    return (((Color) data[PROP_GAP_COLOR][PROP_VAL_VALUE]));
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
   * Gets the fill color.
   *
   * @return the fill color
   */
  public Color getBarFillColor() {
    return (((Color) data[PROP_BAR_FILL_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * Gets the frame color.
   *
   * @return the frame color
   */
  public Color getBarFrameColor() {
    return (((Color) data[PROP_BAR_FRAME_COLOR][PROP_VAL_VALUE]));
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
    GUIsliceThemeElement element = theme.getElement("ListBox");
    if (element != null) {
      data[PROP_FRAME_EN][PROP_VAL_VALUE] = element.isFrameEnabled();
      if (element.getTextCol() != null)
        data[PROP_TEXT_COLOR][PROP_VAL_VALUE] = element.getTextCol();
      if (element.getGapCol() != null)
        data[PROP_GAP_COLOR][PROP_VAL_VALUE] = element.getGapCol();
      if (element.getFrameCol() != null)
        data[PROP_FRAME_COLOR][PROP_VAL_VALUE] = element.getFrameCol();
      if (element.getFillCol() != null)
        data[PROP_FILL_COLOR][PROP_VAL_VALUE] = element.getFillCol();
      if (element.getGlowCol() != null)
        data[PROP_SELECTED_COLOR][PROP_VAL_VALUE] = element.getGlowCol();
      if (element.getBarFrameCol() != null)
        data[PROP_BAR_FRAME_COLOR][PROP_VAL_VALUE] = element.getBarFrameCol();
      if (element.getBarFillCol() != null)
        data[PROP_BAR_FILL_COLOR][PROP_VAL_VALUE] = element.getBarFillCol();
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
    itemsCell.setData((String[])data[PROP_ITEMS][PROP_VAL_VALUE]);
    if (addScrollbar()) {
      data[PROP_SCROLLBAR_ENUM][PROP_VAL_READONLY]=Boolean.FALSE; 
      data[PROP_SCROLLBAR_EREF][PROP_VAL_READONLY]=Boolean.FALSE; 
      data[PROP_SCROLLBAR_MAX][PROP_VAL_READONLY]=Boolean.FALSE; 
      data[PROP_BAR_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
      data[PROP_BAR_FILL_COLOR][PROP_VAL_READONLY]=Boolean.FALSE; 
    } else {
      data[PROP_SCROLLBAR_ENUM][PROP_VAL_READONLY]=Boolean.TRUE; 
      data[PROP_SCROLLBAR_EREF][PROP_VAL_READONLY]=Boolean.TRUE; 
      data[PROP_SCROLLBAR_MAX][PROP_VAL_READONLY]=Boolean.TRUE; 
      data[PROP_BAR_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
      data[PROP_BAR_FILL_COLOR][PROP_VAL_READONLY]=Boolean.TRUE; 
    }
  }

  /**
   * <p>
   * calcSizes() - This routine scans our list of items and comes up with
   *               the buffer size required for storage.
   * </p>
   * 
   * @param fireUpdates indicates that we should notify JTable of changes
   */
  @Override
  public void calcSizes(boolean fireUpdates) {
    String[] items = (String[])data[PROP_ITEMS][PROP_VAL_VALUE];
    if (items == null || items[0].isEmpty()) {
      return;
    }
    int nChars = 0;
    for (int i=0; i<items.length; i++) {
      nChars += items[i].length() + 1;
    }
    data[PROP_STORAGESZ][PROP_VAL_VALUE] = Integer.valueOf(nChars);
    data[PROP_SCROLLBAR_MAX][PROP_VAL_VALUE]=Math.min(100, items.length+5); 
    FontTFT font = ff.getFont(getFontDisplayName());
    Dimension nChSz = ff.getMaxTextBounds(0,0,font,2);
    data[PROP_TXT_HEIGHT][PROP_VAL_VALUE]=Integer.valueOf(nChSz.height);
  }

}
