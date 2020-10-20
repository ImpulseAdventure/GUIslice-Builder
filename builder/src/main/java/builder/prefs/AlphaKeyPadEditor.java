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
package builder.prefs;


import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

import builder.Builder;
import builder.common.EnumFactory;
import builder.models.GeneralModel;
import builder.models.KeyPadTextModel;
import builder.models.WidgetModel;

/**
 * The Class GeneralEditor manages the user preferences for the builder.
 * 
 * @author Paul Conti
 *  
 */
public class AlphaKeyPadEditor extends ModelEditor {
  
  /** The Constant MY_NODE. */
  public static final String MY_NODE = "com/impulseadventure/builder/alphakeypad";
  
  /** The Constant TITLE. */
  private static final String TITLE = EnumFactory.ALPHAKEYPAD;

  /** The instance. */
  private static AlphaKeyPadEditor instance = null;
  
  /**
   * Gets the single instance of GeneralEditor.
   *
   * @return single instance of GeneralEditor
   */
  public static synchronized AlphaKeyPadEditor getInstance()  {
      if (instance == null) {
          instance = new AlphaKeyPadEditor();
      }
      return instance;
  }  
  
  /**
   * Instantiates a new general editor.
   */
  public AlphaKeyPadEditor() {
    // get rid of the bugged Preferences warning - not needed in Java 9 and above
    System.setErr(new PrintStream(new OutputStream() {
        public void write(int b) throws IOException {}
    }));
    String prefNode = MY_NODE + Builder.VERSION_NO;
    fPrefs = Preferences.userRoot().node(prefNode);
    model = new KeyPadTextModel();
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
  public void setModel(KeyPadTextModel model) {
    this.model = model;
  }
  
  /**
   * Save preferences.
   */
  public void savePreferences() {
    super.savePreferences();
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
  * Write model.
  *
  * @param out
  *          the out
  */
 public void writeModel(ObjectOutputStream out) {
    try {
      out.writeObject((KeyPadTextModel)model);
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
   int rows = model.getPropertyCount();
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
   ((KeyPadTextModel) model).setReadOnlyProperties();
   model.TurnOnEvents();
 }
 

}
