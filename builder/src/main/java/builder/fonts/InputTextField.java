package builder.fonts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.Document;

public class InputTextField extends JTextField implements MouseListener, ActionListener {
  private static final long serialVersionUID = 1L;

  private FontTFT myFont;
  private int nMaxCols = -1;

  public InputTextField(String text) {
    super(text);
    super.addMouseListener(this);
  }

  public InputTextField(int columns) {
    super(columns);
    super.addMouseListener(this);
    nMaxCols = columns;
  }

  public InputTextField(String text, int columns) {
    super(text, columns);
    super.addMouseListener(this);
    nMaxCols = columns;
  }

  public InputTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    super.addMouseListener(this);
    nMaxCols = columns;
  }


  /**
   * Sets the font TFT.
   *
   * @param ff
   *          the ff
   * @param myFont
   *          the my font
   */
  public void setFontTFT(FontFactory ff, FontTFT myFont) {
    this.myFont = myFont;
    if (myFont != null) {
      int fontSz = 10;
      if (myFont instanceof FontTtf || myFont instanceof FontVLW) {
        fontSz = 18;
      }
      if (myFont.getLogicalSizeAsInt() == fontSz) {
        this.myFont = myFont;
      } else {
        FontItem item = ff.getFontItem(myFont.getDisplayName());
        if (item != null) {
          FontTFT testFont = ff.getFontbySizeStyle(item.getFamilyName(), 
              fontSz, item.getLogicalStyle());
          if (testFont != null) {
            this.myFont = testFont;
          }
        }
      }
    }
  }

  /**
   * Gets the max columns.
   *
   * @return the max columns
   */
  public int getMaxColumns() {
    return nMaxCols;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = ((AbstractButton)e.getSource()).getActionCommand();
    switch(command) {
      case "clear":
      //clear the TextField
        setText("");
        break;
      case "map":
        CharacterMap dialog = new CharacterMap(null, "Character Map", true);
        dialog.setFontTFT(myFont, nMaxCols);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        String text = dialog.showDialog();
        String curText = getText()+text;
        if (nMaxCols == -1 || curText.length() <= nMaxCols) {
          setText(curText);
        } else {
          int nDiff = curText.length() - nMaxCols;
          curText = curText.substring(nDiff);
          setText(curText);
        }
        break;
     default:
       break;
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void mousePressed(MouseEvent e) {
    maybeShowPopup(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  private void maybeShowPopup(MouseEvent event)
  {
      //if the user clicked the right mouse button
      if (javax.swing.SwingUtilities.isRightMouseButton(event))
      {
          //create (and show) the popup
          createPopup().show(event.getComponent(), event.getX(), event.getY());
      }
  }

  private JPopupMenu createPopup()
  {
      JPopupMenu popupMenu = new JPopupMenu();
      //add the clearTextOption to the JPopupMenu
      JMenuItem clearTextOption = new JMenuItem("Clear text");
      clearTextOption.setActionCommand("clear");
      clearTextOption.addActionListener(this);
      popupMenu.add(clearTextOption);
      JMenuItem characterMapOption = new JMenuItem("Character Map");
      characterMapOption.setActionCommand("map");
      characterMapOption.addActionListener(this);
      popupMenu.add(characterMapOption);
      return popupMenu;
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

}
