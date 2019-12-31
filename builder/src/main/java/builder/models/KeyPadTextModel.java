/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
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

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JTextField;

import builder.common.EnumFactory;
import builder.common.FontFactory;

/**
 * The Class KeyPadModel implements the Numeric Keypad model for the builder.
 * 
 * @author Paul Conti
 *  
 */
public class KeyPadTextModel extends WidgetModel {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID  = 1L;
  
  /** The Property Index Constants. */
  static private final int PROP_ELEMENTREF        = 4;
  static private final int PROP_FONT              = 5;
  static private final int PROP_BUTTONSZ          = 6;
  static private final int PROP_ROUNDED           = 7;
  
  /** The Property Defaults */
  static public  final Boolean DEF_ROUNDED           = Boolean.FALSE;
  static public  final Integer DEF_BUTTONSZ          = Integer.valueOf(20);

  /** The ff. */
  private FontFactory ff = null;

 /**
   * Instantiates a new general model.
   */
  public KeyPadTextModel() {
    ff = FontFactory.getInstance();
    initProperties();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.ALPHAKEYPAD;
    data = new Object[8][5];

    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_ENUM, String.class, "COM-002", Boolean.FALSE,"ENUM",EnumFactory.ALPHAKEYPAD_PAGE_ENUM);
    initProp(PROP_X, Integer.class, "COM-003", Boolean.FALSE,"X",Integer.valueOf(65));
    initProp(PROP_Y, Integer.class, "COM-004", Boolean.FALSE,"Y",Integer.valueOf(80));
    initProp(PROP_ELEMENTREF, String.class, "COM-019", Boolean.FALSE,"ElementRef",EnumFactory.ALPHAKEYPAD_ELEMREF);

    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font",ff.getDefFontName());

    initProp(PROP_BUTTONSZ, Integer.class, "COM-013", Boolean.FALSE,"Button Size",DEF_BUTTONSZ);
    initProp(PROP_ROUNDED, Boolean.class, "COM-012", Boolean.FALSE,"Corners Rounded?",DEF_ROUNDED);
    
  }
  
  /**
   * Gets the element ref.
   *
   * @return the element ref
   */
  @Override
  public String getElementRef() {
    return (String) data[PROP_ELEMENTREF][PROP_VAL_VALUE];
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
   * Gets the button size.
   *
   * @return the button size.
   */
  public int getButtonSz() {
    return (((Integer) (data[PROP_BUTTONSZ][PROP_VAL_VALUE])).intValue());
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
   * Gets the button gap for X direction
   *
   * @return the button gap for X direction
   */
//  public int getButtonGapX() {
//    return (((Integer) (data[PROP_BUTTONGAPX][PROP_VAL_VALUE])).intValue());
//  }

  /**
   * Gets the button gap for Y direction
   *
   * @return the button gap for Y direction
   */
//  public int getButtonGapY() {
//    return (((Integer) (data[PROP_BUTTONGAPY][PROP_VAL_VALUE])).intValue());
//  }

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
//   System.out.println("WM readModel() " + getKey());
     if (widgetType != null)
       this.widgetType = widgetType;
     bSendEvents = in.readBoolean();
//   System.out.println("bSendEvents: " + bSendEvents);
     int rows = in.readInt();
     String metaID = null;
     Object objectData = null;
     int row;
//   System.out.println("WM rows: " + rows);
     for (int i=0; i<rows; i++) {
       metaID = (String)in.readObject();
       objectData = in.readObject();
       if (metaID.equals("KEY-002")) {
         metaID = "COM-002";
       }
       if (metaID.equals("KEY-019")) {
         metaID = "COM-019";
       }
       row = mapMetaIDtoProperty(metaID);
// System.out.println("metaID: " + metaID + " row: " + row);
       if (row >= 0) {
         data[row][PROP_VAL_VALUE] = objectData;
         
//       System.out.println(data[row][PROP_VAL_NAME].toString() + ": " +
//       data[row][PROP_VAL_VALUE].toString() + " mapped to row " + row);
         
       }
     }
   }
}
