/**
 *
 * The MIT License
 *
 * Copyright 2018-2024 Paul Conti
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
package builder.swing;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;

public class MyToggleButtonMenuItem extends JCheckBoxMenuItem {
  private static final long serialVersionUID = 1L;

  private static ImageIcon toggleOn  = null;
  private static ImageIcon toggleOff = null;

  public MyToggleButtonMenuItem() {

  }

  /**
   * Creates an initially unselected check box menu item with an icon.
   *
   * @param icon the icon of the {@code JCheckBoxMenuItem}.
   */
  public MyToggleButtonMenuItem(Icon icon) {
    super(icon);
    setSelected(false);
  }

  /**
   * Creates an initially unselected check box menu item with text.
   *
   * @param text the text of the {@code JCheckBoxMenuItem}
   */
  public MyToggleButtonMenuItem(String text) {
    super(text);
    setSelected(false);
  }

  /**
   * Creates a menu item whose properties are taken from the
   * Action supplied.
   *
   * @param a the action of the {@code JCheckBoxMenuItem}
   * @since 1.3
   */
  public MyToggleButtonMenuItem(Action a) {
    super(a);
    setSelected(false);
  }

  /**
   * Creates an initially unselected check box menu item with the specified text and icon.
   *
   * @param text the text of the {@code JCheckBoxMenuItem}
   * @param icon the icon of the {@code JCheckBoxMenuItem}
   */
  public MyToggleButtonMenuItem(String text, Icon icon) {
    super(text, icon);
    setSelected(false);
  }

  /**
   * Creates a check box menu item with the specified text and selection state.
   *
   * @param text the text of the check box menu item.
   * @param b    the selected state of the check box menu item
   */
  public MyToggleButtonMenuItem(String text, boolean b) {
    super(text, b);
    setSelected(false);
  }

  /**
   * Creates a check box menu item with the specified text, icon, and selection state.
   *
   * @param text the text of the check box menu item
   * @param icon the icon of the check box menu item
   * @param b    the selected state of the check box menu item
   */
  public MyToggleButtonMenuItem(String text, Icon icon, boolean b) {
    super(text, icon, b);
    this.toggleOff = (ImageIcon) icon;
  }

  /**
   * Sets the toggle On image.
   *
   * @param toggleOn
   *          the new toggle On image
   */
  public void setOnImage(ImageIcon toggleOn) {
    this.toggleOn = toggleOn;
  }

  /**
   * Sets the toggle Off image.
   *
   * @param toggleOff
   *          the new toggle Off image
   */
  public void setOffImage(ImageIcon toggleOff) {
    this.toggleOff = toggleOff;
  }

  /**
   * Calls the UI delegate's paint method, if the UI delegate
   * is non-<code>null</code>.
   *
   * @param g the <code>Graphics</code> object to protect
   * @see #paint
   * @see ComponentUI
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (isSelected()) {
      if (toggleOn != null) toggleOn.paintIcon(this, g, 11, 3);
    } else {
      if (toggleOff != null) toggleOff.paintIcon(this, g, 11, 3);
    }
  }

}
