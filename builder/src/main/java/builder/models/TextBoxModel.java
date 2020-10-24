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

import javax.swing.JTextField;

import builder.codegen.CodeUtils;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.fonts.FontFactory;
import builder.fonts.FontItem;

/**
 * The Class TextBoxModel implements the model for the Text Box widget.
 * 
 * @author Paul Conti
 * 
 */
public class TextBoxModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemTextbox";
  
  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_WRAP              = 8;
  static private final int PROP_ROWS              = 9;
  static private final int PROP_COLS              = 10;
  static private final int PROP_SCROLLBAR         = 11;
  static private final int PROP_SCROLLBAR_ENUM    = 12;
  static private final int PROP_SCROLLBAR_EREF    = 13;
  static private final int PROP_SCROLLBAR_MAX     = 14;
  static private final int PROP_TEXT_COLOR        = 15;
  static private final int PROP_FRAME_COLOR       = 16;
  static private final int PROP_FILL_COLOR        = 17;
  static private final int PROP_SELECTED_COLOR    = 18;
  static private final int PROP_BAR_FRAME_COLOR   = 19;
  static private final int PROP_BAR_FILL_COLOR    = 20;
  
  /** The Property Defaults */
  static public  final Boolean DEF_WRAP              = Boolean.FALSE;
  static public  final Integer DEF_ROWS              = Integer.valueOf(6);
  static public  final Integer DEF_COLS              = Integer.valueOf(28);
  static public  final Boolean DEF_SCROLLBAR         = Boolean.TRUE;
  static public  final String  DEF_SCROLLBAR_ENUM    = "";
  static public  final String  DEF_SCROLLBAR_EREF    = "";
  static public  final Integer DEF_SCROLLBAR_MAX     = Integer.valueOf(100);
  static public  final Color   DEF_TEXT_COLOR        = Color.YELLOW;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  static public  final Color   DEF_BAR_FRAME_COLOR   = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_BAR_FILL_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 203;
  static private final int DEF_HEIGHT= 68;

  /** The ff. */
  FontFactory  ff = null;
  
  /**
   * Instantiates a new text box model.
   */
  public TextBoxModel() {
    ff = FontFactory.getInstance();
    initProperties();
    calcSizes();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.TEXTBOX;
    data = new Object[21][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_WRAP, Boolean.class, "TXT-208", Boolean.FALSE,"Wrap Text",DEF_WRAP);
    initProp(PROP_ROWS, Integer.class, "TXT-209", Boolean.FALSE,"Display Rows",DEF_ROWS);
    initProp(PROP_COLS, Integer.class, "TXT-210", Boolean.FALSE,"Characters per Row",DEF_COLS);
    initProp(PROP_SCROLLBAR, Boolean.class, "BAR-112", Boolean.FALSE,"Add Scrollbar?",DEF_SCROLLBAR);
    initProp(PROP_SCROLLBAR_ENUM, String.class, "BAR-113", Boolean.FALSE,"Scrollbar ENUM",DEF_SCROLLBAR_ENUM);
    initProp(PROP_SCROLLBAR_EREF, String.class, "BAR-114", Boolean.FALSE,"Scrollbar EREF",DEF_SCROLLBAR_EREF);
    initProp(PROP_SCROLLBAR_MAX, Integer.class, "BAR-115", Boolean.FALSE,"Scrollbar Max Value",DEF_SCROLLBAR_MAX);

    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.FALSE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);
    initProp(PROP_BAR_FRAME_COLOR, Color.class, "BAR-116", Boolean.FALSE,"Frame Color",DEF_BAR_FRAME_COLOR);
    initProp(PROP_BAR_FILL_COLOR, Color.class, "BAR-117", Boolean.FALSE,"Fill Color",DEF_BAR_FILL_COLOR);

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
    data[PROP_SCROLLBAR_ENUM][PROP_VAL_VALUE] = EnumFactory.TEXTBOX_SCROLLBAR_ENUM+count;
    data[PROP_SCROLLBAR_EREF][PROP_VAL_VALUE] = EnumFactory.TEXTBOX_SCROLLBAR_EREF+count;
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
    if (row == PROP_COLS  ||
        row == PROP_ROWS ||
        row == PROP_FONT ) {
      // re-calc number of text rows and columns
      calcSizes();
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
      fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
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
   * Gets the scrollbar eref.
   *
   * @return the scrollbar enum
   */
  @Override
  public String getScrollbarERef() {
    return (String)data[PROP_SCROLLBAR_EREF][PROP_VAL_VALUE];
  }
  
  /**
   * Wrap text.
   *
   * @return <code>true</code>, if successful
   */
  public boolean wrapText() {
    return ((Boolean) data[PROP_WRAP][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the num text rows.
   *
   * @return the num text rows
   */
  public int getNumTextRows() {
    return (((Integer) (data[PROP_ROWS][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the num text columns.
   *
   * @return the num text columns
   */
  public int getNumTextColumns() {
    return (((Integer) (data[PROP_COLS][PROP_VAL_VALUE])).intValue());
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
   * Calc sizes.
   */
  private void calcSizes() {
    // first does the current font exist? 
    // if we changed target plaform we might need to change font to default
    int nCols = getNumTextColumns();
    if (nCols == 0) return;
    int nRowWidth, nBoxHeight;
    String name = getFontDisplayName();
    FontItem item = ff.getFontItem(name);
    if (item == null) return;
    if (!item.getDisplayName().equals(name)) {
      data[PROP_FONT][PROP_VAL_VALUE] = item.getDisplayName();
    }
    Dimension nChSz = ff.measureChar(getFontDisplayName());
    nRowWidth = (nCols * nChSz.width) + 33;
    nBoxHeight = (getNumTextRows() * nChSz.height) + 15;
    data[PROP_WIDTH][PROP_VAL_VALUE]=Integer.valueOf(nRowWidth);
    data[PROP_HEIGHT][PROP_VAL_VALUE]=Integer.valueOf(nBoxHeight);
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
}
