package builder.swing;

import javax.swing.*;
import java.awt.*;

public class MyToggleButton extends JToggleButton {

  private static final long serialVersionUID = 1L;

  private  ImageIcon toggleOn  = null;
  private  ImageIcon toggleOff = null;
  private Rectangle iconRect = null;

  /**
   * Creates a toggle button that has the specified text and image,
   * and that is initially unselected.
   *
   * @param text the string displayed on the button
   * @param icon the image that the button should display
   */
  public MyToggleButton(String text, Icon icon) {
    super(text, icon);
    iconRect = new Rectangle(0,0,icon.getIconWidth(),icon.getIconHeight());
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
      if (toggleOn != null) toggleOn.paintIcon(this, g, 5, 5);
    } else {
      if (toggleOff != null) toggleOff.paintIcon(this, g, 5, 5);
    }
  }

}
