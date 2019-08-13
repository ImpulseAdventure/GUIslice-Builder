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
package builder.prefs;


import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

import builder.Builder;
import builder.models.GeneralModel;
import builder.models.WidgetModel;

/**
 * The Class GeneralEditor manages the user preferences for the builder.
 * 
 * @author Paul Conti
 *  
 */
public class GeneralEditor extends ModelEditor {
  
  /** The Constant MY_NODE. */
  public static final String MY_NODE = "com/impulseadventure/builder/general";
  
  /** The Constant TITLE. */
  private static final String TITLE = "General";

  /** The instance. */
  private static GeneralEditor instance = null;
  
  /**
   * Gets the single instance of GeneralEditor.
   *
   * @return single instance of GeneralEditor
   */
  public static synchronized GeneralEditor getInstance()  {
      if (instance == null) {
          instance = new GeneralEditor();
      }
      return instance;
  }  
  
  /**
   * Instantiates a new general editor.
   */
  public GeneralEditor() {
    // get rid of the bugged Preferences warning - not needed in Java 9 and above
    System.setErr(new PrintStream(new OutputStream() {
        public void write(int b) throws IOException {}
    }));
    String prefNode = MY_NODE + Builder.VERSION_NO;
    fPrefs = Preferences.userRoot().node(prefNode);
    model = new GeneralModel();
    model.TurnOffEvents();
    updateModel();
    System.setErr(System.err);  
  }

  /**
   * Sets the model.
   *
   * @param model
   *          the new model
   */
  public void setModel(GeneralModel model) {
    this.model = model;
  }
  
  /**
   * Save preferences.
   */
  public void savePreferences() {
    super.savePreferences();
  }
  
  /**
   * Gets the recent colors.
   *
   * @return the recent colors
   */
  public String getRecentColors() {
    return ((GeneralModel) model).getRecentColors();
  }

  /**
   * setRecentColors sets the recent colors
   * called by our color chooser.
   * @param s
   */
  public void setRecentColors(String s) { 
    ((GeneralModel) model).setRecentColors(s);
  }

  /**
   * getTitle
   *
   * @see builder.prefs.ModelEditor#getTitle()
   */
  @Override 
  public String getTitle() {
    return TITLE;
  }

  /**
   * Gets the theme class name.
   *
   * @return the theme class name
   */
  public String getThemeClassName() {
    return ((GeneralModel) model).getThemeClassName();
  }
  
  /**
   * Gets the target.
   *
   * @return the target
   */
  public String getTarget() {
    return ((GeneralModel) model).getTarget();
  }
  
  /**
   * Gets the width.
   *
   * @return the width
   */
  public int getWidth() {
    return ((GeneralModel) model).getWidth();
  }
  
  /**
   * Sets the width.
   *
   * @param width
   *          the new width
   */
  public void setWidth(int width) {
    ((GeneralModel) model).setWidth(width);
  }

  /**
   * Gets the height.
   *
   * @return the height
   */
  public int getHeight() {
    return ((GeneralModel) model).getHeight();
  }

  /**
   * Sets the height.
   *
   * @param height
   *          the new height
   */
  public void setHeight(int height) {
    ((GeneralModel) model).setHeight(height);
  }

  /**
   * Gets the dpi.
   *
   * @return the dpi
   */
  public int getDPI() {
    return ((GeneralModel) model).getDPI();
  }
  
  /**
   * Gets the margins.
   *
   * @return the margins
   */
  public int getMargins() {
    return ((GeneralModel) model).getMargins();
  }

  /**
   * Gets the h spacing.
   *
   * @return the h spacing
   */
  public int getHSpacing() {
    return ((GeneralModel) model).getHSpacing();
  }

  /**
   * Gets the v spacing.
   *
   * @return the v spacing
   */
  public int getVSpacing() {
    return ((GeneralModel) model).getVSpacing();
  }

 /**
  * Write model.
  *
  * @param out
  *          the out
  */
 public void writeModel(ObjectOutputStream out) {
    try {
      out.writeObject((GeneralModel)model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

 /**
  * Update model.
  */
 @Override
 public void updateModel() {
   model.TurnOffEvents();
   int rows = model.getRowCount();
   for (int i=0; i<rows; i++) {
     String key = (String) model.getValueAt(i, WidgetModel.COLUMN_NAME);
     Object o = model.getValueAt(i, WidgetModel.COLUMN_VALUE);
     if(o instanceof String) {
       model.changeValueAt(fPrefs.get(key, (String)o), i);
     } else if(o instanceof Integer) {
       int def = ((Integer)o).intValue();
       int value = fPrefs.getInt(key, def);
       if (value != def)
         model.changeValueAt(Integer.valueOf(value), i);
     } else if(o instanceof Boolean) {
       boolean def = ((Boolean)o).booleanValue();
       boolean value = fPrefs.getBoolean(key, def);
       if (value != def)
         model.changeValueAt(Boolean.valueOf(value), i);
     } else if (o instanceof Color) {
       int def = ((Color)o).getRGB();
       int value = fPrefs.getInt(key, def);
       if (value != def)
         model.changeValueAt(new Color(value), i);
     }
   }
   ((GeneralModel) model).setReadOnlyProperties();
   model.TurnOnEvents();
 }
 

}
