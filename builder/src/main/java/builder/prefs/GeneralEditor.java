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
package builder.prefs;


import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import builder.Builder;
import builder.models.GeneralModel;

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

  public void addListener(PreferenceChangeListener pcl) {
    fPrefs.addPreferenceChangeListener(pcl);
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
    return ((GeneralModel) model).getTargetPlatform();
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
   * @param h
   *          the new height
   */
  public void setHeight(int h) {
    ((GeneralModel) model).setHeight(h);
  }

  /**
   * getAppWinWidth.
   *
   * @return the width
   */
  public int getAppWinWidth() {
    return ((GeneralModel) model).getAppWinWidth();
  }
  
  /**
   * setAppWinWidth.
   *
   * @param width
   *          the new width
   */
  public void setAppWinWidth(int width) {
    ((GeneralModel) model).setAppWinWidth(width);
    savePreferences();
  }

  /**
   * getAppWinHeight.
   *
   * @return the height
   */
  public int getAppWinHeight() {
    return ((GeneralModel) model).getAppWinHeight();
  }

  /**
   * setAppWinHeight.
   *
   * @param h
   *          the new height
   */
  public void setAppWinHeight(int h) {
    ((GeneralModel) model).setAppWinHeight(h);
    savePreferences();
  }

  /**
   * getTFTWinWidth.
   *
   * @return the width
   */
  public int getTFTWinWidth() {
    return ((GeneralModel) model).getTFTWinWidth();
  }
  
  /**
   * setTFTWinWidth.
   *
   * @param width
   *          the new width
   */
  public void setTFTWinWidth(int width) {
    ((GeneralModel) model).setTFTWinWidth(width);
    savePreferences();
  }

  /**
   * getTFTWinHeight.
   *
   * @return the height
   */
  public int getTFTWinHeight() {
    return ((GeneralModel) model).getTFTWinHeight();
  }

  /**
   * setTFTWinHeight.
   *
   * @param h
   *          the new height
   */
  public void setTFTWinHeight(int h) {
    ((GeneralModel) model).setTFTWinHeight(h);
    savePreferences();
  }

  /**
   * getPropWinWidth.
   *
   * @return the width
   */
  public int getPropWinWidth() {
    return ((GeneralModel) model).getPropWinWidth();
  }
  
  /**
   * setPropWinWidth.
   *
   * @param width
   *          the new width
   */
  public void setPropWinWidth(int width) {
    ((GeneralModel) model).setPropWinWidth(width);
    savePreferences();
  }

  /**
   * getPropWinHeight.
   *
   * @return the height
   */
  public int getPropWinHeight() {
    return ((GeneralModel) model).getPropWinHeight();
  }

  /**
   * setPropWinHeight.
   *
   * @param h
   *          the new height
   */
  public void setPropWinHeight(int h) {
    ((GeneralModel) model).setPropWinHeight(h);
    savePreferences();
  }

  /**
   * getTreeWinWidth.
   *
   * @return the width
   */
  public int getTreeWinWidth() {
    return ((GeneralModel) model).getTreeWinWidth();
  }
  
  /**
   * setTreeWinWidth.
   *
   * @param width
   *          the new width
   */
  public void setTreeWinWidth(int width) {
    ((GeneralModel) model).setTreeWinWidth(width);
    savePreferences();
  }

  /**
   * getTreeWinHeight.
   *
   * @return the height
   */
  public int getTreeWinHeight() {
    return ((GeneralModel) model).getTreeWinHeight();
  }

  /**
   * setTreeWinHeight.
   *
   * @param h
   *          the new height
   */
  public void setTreeWinHeight(int h) {
    ((GeneralModel) model).setTreeWinHeight(h);
    savePreferences();
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
   * is Backward Compatibility Mode?
   *
   * @return <code>true</code>, if Backward Compatibility Mode is to be used
   */
  public boolean isBackwardCompat() {
    return ((GeneralModel) model).isBackwardCompat();
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

}
