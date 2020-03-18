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
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.common.FontItem;
import builder.models.GeneralModel;
import builder.prefs.GeneralEditor;
import builder.events.MsgBoard;

/**
 * The Class TextInputModel implements the model for the TextInput widget.
 * 
 * @author Paul Conti
 */
public class TextInputModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemInTxt";
  
  /** The Property Index Constants. */
  static private final int PROP_FONT              = 7;
  static private final int PROP_TEXT              = 8;
  static private final int PROP_UTF8              = 9;
  static private final int PROP_TEXT_SZ           = 10;
  static private final int PROP_TEXT_ALIGN        = 11;
  static private final int PROP_FILL_EN           = 12;
  static private final int PROP_DEFAULT_COLORS    = 13;
  static private final int PROP_TEXT_COLOR        = 14;
  static private final int PROP_FRAME_COLOR       = 15;
  static private final int PROP_FILL_COLOR        = 16;
  static private final int PROP_SELECTED_COLOR    = 17;

  /** The Property Defaults */
  static public  final String  DEF_TEXT              = "";
  static public  final Boolean DEF_UTF8              = Boolean.FALSE;
  static public  final Integer DEF_TEXT_SZ           = Integer.valueOf(10);
  static public  final String  DEF_TEXT_ALIGN        = "GSLC_ALIGN_MID_LEFT";
  static public  final Boolean DEF_FILL_EN           = Boolean.TRUE;
  static public  final Boolean DEF_DEFAULT_COLORS    = Boolean.TRUE;
  static public  final Color   DEF_TEXT_COLOR        = Color.YELLOW;
  static public  final Color   DEF_FRAME_COLOR       = new Color(128,128,128); // GSLC_COL_GRAY
  static public  final Color   DEF_FILL_COLOR        = Color.BLACK;
  static public  final Color   DEF_SELECTED_COLOR    = Color.BLACK;
  
  static private final int DEF_WIDTH = 65;
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
  public TextInputModel() {
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
    widgetType = EnumFactory.TEXTINPUT;
    data = new Object[18][5];

    initCommonProps(DEF_WIDTH, DEF_HEIGHT);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());
    initProp(PROP_TEXT, String.class, "TXT-201", Boolean.FALSE,"Text",DEF_TEXT);
    String target = ((GeneralModel) GeneralEditor.getInstance().getModel()).getTarget();
    // arduino GFX doesn't support UTF8 only linix with SDL has support
    // so for arduino set UTF8 property to read-only
    if (target.equals("linux")) {
      initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.FALSE,"UTF-8?",DEF_UTF8);
    } else {
      initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.TRUE,"UTF-8?",DEF_UTF8);
    }

    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"Field Size",DEF_TEXT_SZ);
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);
    initProp(PROP_FILL_EN, Boolean.class, "COM-011", Boolean.FALSE,"Fill Enabled?",DEF_FILL_EN);

    initProp(PROP_DEFAULT_COLORS, Boolean.class, "COL-300", Boolean.FALSE,"Use Default Colors?",DEF_DEFAULT_COLORS);
    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.TRUE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.TRUE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.TRUE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.TRUE,"Selected Color",DEF_SELECTED_COLOR);

  }

  /**
   * setValueAt
   *
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
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
    if (row == PROP_FONT) {
      calcSizes(true);
    } 
    if (row == PROP_TEXT_SZ) {
      calcSizes(true);
    }
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
    
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.getInstance().sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        MsgBoard.getInstance().sendRepaint(getKey(),getKey());
      }
    } 
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
    if (!item.getDisplayName().equals(name)) {
      data[PROP_FONT][PROP_VAL_VALUE] = item.getDisplayName();
      if (fireUpdates) {
        fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
      }
    }
    Font font = ff.getFont(item.getDisplayName());
    // our text is input only so create a string getTextStorage() size
    String text = "";
    for (int i=0; i<getTextStorage(); i++) {
      text = text + "W";
    }
    // calculate the real sizes of our display text
    Dimension d = ff.measureText(text, font);
    // do not do these calculations when reloading our model from a file
    if (fireUpdates) {
      // now figure out the rect size needed on the target platform
      // that we show to our user and also push out during code generation.
      if (getFontDisplayName().startsWith("BuiltIn")) {
        Dimension nChSz = ff.measureAdafruitText(text,getFontDisplayName());
        setWidth(nChSz.width);
        setHeight(nChSz.height);
      } else {
        // if font is not one of the built-in fonts than actual size is correct even though font is scaled.
        setWidth(d.width);
        setHeight(d.height);
      }
      if (fireUpdates) {
        fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
        fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
      }
    }
  }

}
