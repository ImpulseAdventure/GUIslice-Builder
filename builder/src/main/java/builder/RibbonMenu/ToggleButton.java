package builder.RibbonMenu;

import javax.swing.*;

/**
 * Dropdown button that allow to use submenu.
 *
 * @author paul conti
 */
public class ToggleButton extends Button {
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  private boolean toggle_state; // true is on, false otherwise
  /** image to display of toggle is in ON state. */
  private ImageIcon onImage;

  public ToggleButton (String token) {
    super(token);
    this.toggle_state = false;
  }

  public void setToggle(boolean bValue) { this.toggle_state = bValue; }

  public boolean getToggle() { return this.toggle_state; }
  /**
   * Sets the disabled image.
   *
   * @param onImage
   *          the new disabled image
   */
  public void setOnImage(ImageIcon onImage) {
    this.onImage = onImage;
  }

  /**
   * getImage
   *
   * @see VirtualObject#getImage()
   */
  @Override
  public ImageIcon getImage() {
    if (!isEnabled()) {
      //If no default disabled image create it from original image
      if (disabledImage==null) {
        disabledImage = convertToGrayScale(super.getImage());
      }
      return disabledImage;
    } else if (toggle_state) {
      return onImage;
    }
    return super.getImage();
  }

}
