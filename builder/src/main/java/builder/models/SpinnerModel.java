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

import javax.swing.JTextField;

import builder.common.ColorFactory;
import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.common.FontItem;
import builder.controller.Controller;
import builder.events.MsgBoard;

/**
 * The Class SpinnerModel implements the model for the Spinner widget.
 * 
 * @author Paul Conti
 * 
 */
public class SpinnerModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant for gslc_tsElemRef* m_pElementRef name */
  public static final String ELEMENTREF_NAME = "m_pElemSpinner";
  
  /** The Property Index Constants. */
  static private final int PROP_FONT           = 7;
  static private final int PROP_MIN            = 8;
  static private final int PROP_MAX            = 9;
  static private final int PROP_CURVALUE       = 10;
  static private final int PROP_INCREMENT      = 11;
  static private final int PROP_BUTTONSZ       = 12;

  /** The Property Defaults */
  static public final Integer DEF_MIN                   = Integer.valueOf(0);
  static public final Integer DEF_MAX                   = Integer.valueOf(99);
  static public final Integer DEF_CURVALUE              = Integer.valueOf(0);
  static public final Integer DEF_INCREMENT             = Integer.valueOf(1);
  static public final Integer DEF_BUTTONSZ              = Integer.valueOf(20);

 
  /** The ff. */
  private FontFactory ff = null;
  
  /** The calculated text width. */
  private int textWidth = 0;

  /**
   * Instantiates a new spinner model.
   */
  public SpinnerModel() {
    cf = ColorFactory.getInstance();
    ff = FontFactory.getInstance();
    initProperties();
    calcSizes();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.SPINNER;
    data = new Object[13][5];
    
    initCommonProps(0, 0);
    
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());

    initProp(PROP_MIN, Integer.class, "SLD-100", Boolean.FALSE,"Minimum Value",DEF_MIN);
    initProp(PROP_MAX, Integer.class, "SLD-101", Boolean.FALSE,"Maximum Value",DEF_MAX);
    initProp(PROP_CURVALUE, Integer.class, "SLD-102", Boolean.FALSE,"Starting Value",DEF_CURVALUE);
    initProp(PROP_INCREMENT, Integer.class, "SPIN-100", Boolean.FALSE,"Increment by",DEF_INCREMENT);
    initProp(PROP_BUTTONSZ, Integer.class, "COM-013", Boolean.FALSE,"Button Size",DEF_BUTTONSZ);

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
    if (row == PROP_MAX || row == PROP_BUTTONSZ) {
      calcSizes();
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
      fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
    }
    if (row == PROP_FONT) {
      calcSizes();
      fireTableCellUpdated(PROP_WIDTH, COLUMN_VALUE);
      fireTableCellUpdated(PROP_HEIGHT, COLUMN_VALUE);
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
   * Gets the text width.
   *
   * @return the width
   */
  public int getTextWidth() {
    return textWidth;
  }

  /**
   * Sets the width.
   *
   * @param w
   *          the new width
   */
  public void setTextWidth(int w) {
    textWidth = w;
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
   * Gets the min.
   *
   * @return the min
   */
  public int getMin() {
    return (((Integer) (data[PROP_MIN][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the max.
   *
   * @return the max
   */
  public int getMax() {
    return (((Integer) (data[PROP_MAX][PROP_VAL_VALUE])).intValue());
  }
  
  /**
   * Gets the value.
   *
   * @return the value
   */
  public int getCurValue() {
    return (((Integer) (data[PROP_CURVALUE][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the increment
   *
   * @return the increment
   */
  public int getIncrement() {
    return (((Integer) (data[PROP_INCREMENT][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the button size.
   *
   * @return the button size
   */
  public int getButtonSize() {
    return (((Integer) (data[PROP_BUTTONSZ][PROP_VAL_VALUE])).intValue());
  }

  /**
   * Gets the button fill color.
   *
   * @return the button color
   */
  public Color getButtonColor() {
    return new Color(0,0,192);
  }

  /**
   * Gets the button text color.
   *
   * @return the button text color
   */
  public Color getButtonTextColor() {
    return Color.WHITE;
  }

  /**
   * Gets the fill color.
   *
   * @return the fill color
   */
  public Color getFillColor() {
    return Color.BLACK;
  }

  /**
   * Gets the frame color.
   *
   * @return the frame color
   */
  public Color getFrameColor() {
    return Color.GRAY;
  }

  /**
   * Gets the text color.
   *
   * @return the text color
   */
  public Color getTextColor() {
    return Color.YELLOW;
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
   public void calcSizes() {
     // first does the current font exist? 
     // if we changed target plaform we might need to change font to default
     String name = getFontDisplayName();
     FontItem item = ff.getFontItem(name);
     if (item == null) return;
     if (!item.getDisplayName().equals(name)) {
       data[PROP_FONT][PROP_VAL_VALUE] = item.getDisplayName();
       fireTableCellUpdated(PROP_FONT, COLUMN_VALUE);
     }
     Font font = ff.getFont(item.getDisplayName());
     // our text is input only so create a string getTextStorage() size
     String text_min = String.valueOf(getMin());
     String text = String.valueOf(getMax());
     if (text.length() < text_min.length())
       text = text_min;
     // calculate the real sizes of our display text
     Dimension d = ff.measureText(item.getDisplayName(), font, text);
     // now figure out the rect size needed on the target platform
     // that we show to our user and also push out during code generation.
     if (getFontDisplayName().startsWith("BuiltIn")) {
       textWidth = d.width;
       d.width = d.width + 10 + (getButtonSize() * 2);
       data[PROP_WIDTH][PROP_VAL_VALUE]=Integer.valueOf(d.width);
       data[PROP_HEIGHT][PROP_VAL_VALUE]=Integer.valueOf(d.height);
     } else {
       // if font is not one of the built-in fonts than actual size is correct even though font is scaled.
       textWidth = d.width;
       d.width = d.width + 10 + (getButtonSize() * 2);
       data[PROP_WIDTH][PROP_VAL_VALUE]=Integer.valueOf(d.width);
       data[PROP_HEIGHT][PROP_VAL_VALUE]=Integer.valueOf(d.height);
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
    calcSizes();
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
    calcSizes();
  }
  

}
