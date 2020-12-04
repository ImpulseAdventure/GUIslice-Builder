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
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.prefs.NumKeyPadEditor;
import builder.events.MsgBoard;
import builder.fonts.FontFactory;
import builder.fonts.FontItem;
import builder.fonts.FontTFT;

/**
 * The Class TextInputModel implements the model for the TextInput widget.
 * 
 * @author Paul Conti
 */
public class NumberInputModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemVal";
  
  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_TEXT              = 8;
  static private final int PROP_UTF8              = 9;
  static private final int PROP_TEXT_SZ           = 10;
  static private final int PROP_TEXT_ALIGN        = 11;
  static private final int PROP_TEXT_MARGIN       = 12;
  static private final int PROP_FILL_EN           = 13;
  static private final int PROP_USE_FLASH         = 14;
  static private final int PROP_TEXT_COLOR        = 15;
  static private final int PROP_FRAME_COLOR       = 16;
  static private final int PROP_FILL_COLOR        = 17;
  static private final int PROP_SELECTED_COLOR    = 18;

  /** The Property Defaults */
  static public  final String  DEF_TEXT              = "";
  static public  final Boolean DEF_UTF8              = Boolean.FALSE;
  static public  final Integer DEF_TEXT_SZ           = Integer.valueOf(6);
  static public  final String  DEF_TEXT_ALIGN        = "GSLC_ALIGN_MID_LEFT";
  static public  final Integer DEF_TEXT_MARGIN       = Integer.valueOf(5);
  static public  final Boolean DEF_FILL_EN           = Boolean.TRUE;
  static public  final Boolean DEF_USE_FLASH         = Boolean.FALSE;
  static public  final Color   DEF_TEXT_COLOR        = Color.YELLOW;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 40;
  static private final int DEF_HEIGHT= 10;

  /** The ff. */
  private FontFactory ff = null;
  
  /** The cb align. */
  JComboBox<String> cbAlign;
  
  /** The align cell editor. */
  DefaultCellEditor alignCellEditor;

  /**
   * Instantiates a new text model.
   */
  public NumberInputModel() {
    ff = FontFactory.getInstance();
    initProperties();
    initAlignments();
    calcSizes(false);
  }
  
  /**
   * Initializes the alignments.
   */
  private void initAlignments()
  {
    cbAlign = new JComboBox<String>();
    cbAlign.addItem("GSLC_ALIGN_MID_LEFT");
    cbAlign.addItem("GSLC_ALIGN_MID_MID");
    cbAlign.addItem("GSLC_ALIGN_MID_RIGHT");
    alignCellEditor = new DefaultCellEditor(cbAlign);
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.NUMINPUT;
    data = new Object[19][5];
    
    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_TEXT, String.class, "TXT-201", Boolean.FALSE,"Text",DEF_TEXT);
    initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.FALSE,"UTF-8?",DEF_UTF8);

    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"Field Size",DEF_TEXT_SZ);
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);
    initProp(PROP_TEXT_MARGIN, Integer.class, "TXT-212", Boolean.FALSE,"Text Margin",DEF_TEXT_MARGIN);
    initProp(PROP_FILL_EN, Boolean.class, "COM-011", Boolean.FALSE,"Fill Enabled?",DEF_FILL_EN);

    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    
    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.FALSE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);

  }

  /**
   * setValueAt
   *
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @SuppressWarnings("unused")
  @Override
  public void setValueAt(Object value, int row, int col) {
    if (row == PROP_TEXT && !((String)value).isEmpty()) {
      if (!NumKeyPadEditor.getInstance().isSignEn() &&
          ((String) value).charAt(0) == '-') {
        JOptionPane.showMessageDialog(null, 
            "Your Keypad perference says not to allow minus sign", 
            "ERROR",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (NumKeyPadEditor.getInstance().isFloatingPointEn()) {
        try {
          float val = Float.parseFloat((String) value);
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(null, 
              "Field must be valid floating point number", 
              "ERROR",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
      } else {
        try {
          int val = Integer.parseInt((String) value);
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(null, 
              "Field must be valid integer number", 
              "ERROR",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
    }
    if (row == PROP_TEXT_SZ) {
      int size = Integer.valueOf((String) value);
      if (size <= 0) {
        JOptionPane.showMessageDialog(null, 
            "Field Size must be > 0", 
            "ERROR",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    super.setValueAt(value, row, col);
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
    if (row > PROP_HEIGHT || row == PROP_ENUM)
      super.setModelChanged();
    if (row == PROP_X) {
      calcSizes(true);
    } 
    if (row == PROP_Y) {
      calcSizes(true);
    } 
    if (row == PROP_FONT) {
      calcSizes(true);
    } 
    if (row == PROP_TEXT_SZ) {
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
   * Checks if is fill enabled.
   *
   * @return true, if is fill enabled
   */
  public boolean isFillEnabled() {
    return ((Boolean) data[PROP_FILL_EN][PROP_VAL_VALUE]).booleanValue();
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
   * Gets the text storage.
   *
   * @return the text storage
   */
  public int getTextStorage() {
    return (((Integer) (data[PROP_TEXT_SZ][PROP_VAL_VALUE])).intValue());
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
    // first does the current font exist? 
    // if we changed target plaform we might need to change font to default
    String name = getFontDisplayName();
    FontItem item = ff.getFontItem(name);
    if (item == null) return;
    if (!item.getDisplayName().equals(name)) {
      data[PROP_FONT][PROP_VAL_VALUE] = item.getDisplayName();
      if (fireUpdates) {
        fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
      }
    }
    FontTFT font = ff.getFont(item.getDisplayName());
    // our text is input only so create a string getTextStorage() size
    String text = "";
    for (int i=0; i<getTextStorage(); i++) {
      text = text + "9";
    }
    // do not do these calculations when reloading our model from a file
    if (fireUpdates) {
      // calculate the real sizes of our display text
      Dimension nChSz = ff.getTextBounds(getX(),getY(),font, text);
      setWidth(nChSz.width+(getTextMargin()*2)); // account for margin
      setHeight(nChSz.height+1); // account for frame
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

