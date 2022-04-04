/**
 *
 * The MIT License
 *
 * Copyright 2018, 2022 Paul Conti
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
package builder.fonts;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

//import builder.Builder;

public class TextTFT extends JTextField implements MouseListener, ActionListener {
  private static final long serialVersionUID = 1L;

  private FontTFT myFont;
  private FontFactory ff;
  private boolean bValidFont = false;
  private Rectangle rAlloc=null;
  private int nCaretPos = -1;
  private int[] nCharWidths = null;
  private int nMaxCols = -1;

  protected TextTFT instance = null;
  
  public TextTFT() {
    super();
    this.setCaret( new NoTextSelectionCaret( this ));
    super.addMouseListener(this);
    this.addCaretListener(new MyCaretListener());
    mapKeys();
    instance = this;
  }

  public TextTFT(String text) {
    super(text);
    this.setCaret( new NoTextSelectionCaret( this ));
    super.addMouseListener(this);
    this.addCaretListener(new MyCaretListener());
    mapKeys();
    instance = this;
  }
  
  public TextTFT(String text, int columns) {
    super(text, columns);
    nMaxCols = columns;
    this.setCaret( new NoTextSelectionCaret( this ));
    super.addMouseListener(this);
    this.addCaretListener(new MyCaretListener());
    mapKeys();
    instance = this;
  }

  public TextTFT(Document doc, String text, int columns) {
    super(doc, text, columns);
    nMaxCols = columns;
    this.setCaret( new NoTextSelectionCaret( this ));
    super.addMouseListener(this);
    this.addCaretListener(new MyCaretListener());
    mapKeys();
    instance = this;
  }
  
  /**
   * mapKeys
   * Setup out left and right arrow keys so 
   * we can move our text caret accordingly.
   * Also, turn off any selection highlights
   * when we lose focus.
   */
  private void mapKeys() {
    LeftAction leftAction = new LeftAction();
    RightAction rightAction = new RightAction();
    KeyStroke keyLeft = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
    KeyStroke keyRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
    KeyStroke keypadLeft = KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, 0);
    KeyStroke keypadRight = KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, 0);
    InputMap im = this.getInputMap(JComponent.WHEN_FOCUSED);
    this.getActionMap().put(im.get(keyLeft), leftAction);
    this.getActionMap().put(im.get(keyRight), rightAction);
    this.getActionMap().put(im.get(keypadLeft), leftAction);
    this.getActionMap().put(im.get(keypadRight), rightAction);
  }

  class LeftAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public LeftAction() {
      putValue(Action.NAME, "Left");
      putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_LEFT));
    }

    public void actionPerformed(ActionEvent e) {
      if (isEditable()) {
        if (nCaretPos > 0) {
          nCaretPos--;
          setCaretPosition(nCaretPos);
        }
      }
    }
  }

  class RightAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public RightAction() {
      putValue(Action.NAME, "Right");
      putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
      putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_RIGHT));
    }

    public void actionPerformed(ActionEvent e) {
      if (isEditable()) {
        if (nCaretPos < getText().length()) {
          nCaretPos++;
          setCaretPosition(nCaretPos);
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

  /**
   * setText
   *
   * @see javax.swing.text.JTextComponent#setText(java.lang.String)
   */
  @Override 
  public void setText(String content) {
    super.setText(content);
    if (content == null)  {
      bValidFont = false;
    } else {
      if (myFont != null) {
        setFontTFT(ff, myFont);
      }
    }
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
    this.ff = ff;
    if (myFont == null) {
      bValidFont = false;
      nCharWidths = null;
      nCaretPos = getText().length();
      return;
    }
    int fontSz = 10;
    if (myFont instanceof FontTtf || myFont instanceof FontVLW) {
      fontSz = 18;
    }
    if (myFont.getLogicalSizeAsInt() == fontSz) {
      this.myFont = myFont;
      bValidFont = true;
    } else {
      FontItem item = myFont.getFontItem();
      if (item != null) {
        FontTFT testFont = ff.getFontbySizeStyle(item.getFamilyName(), 
            fontSz, item.getLogicalStyle());
        if (testFont != null) {
          this.myFont = testFont;
          bValidFont = true;
        } else {
          bValidFont = false;
          nCharWidths = null;
        }
      }
    }
    calculateGlyphs();
    nCaretPos = getText().length();
    setCaretPosition(nCaretPos);
  }
  
  /**
   * Sets the font factory.
   *
   * @param ff
   *          the new font factory
   */
  public void setFontFactory(FontFactory ff) {
    this.ff = ff;
  }

  public FontTFT getFontTFT() {
    return myFont;  
  }
  
  /**
   * Gets the visible editor rect.
   *
   * @return the visible editor rect
   */
  protected Rectangle getVisibleEditorRect() {
    rAlloc = super.getBounds();
    if ((rAlloc != null) && (rAlloc.height > 0)) {
      rAlloc.x = rAlloc.y = 0;
      Insets insets = getInsets();
      rAlloc.x += insets.left;
      rAlloc.y += insets.top;
      rAlloc.width -= insets.left + insets.right;
      rAlloc.height -= insets.top + insets.bottom;
      return rAlloc;
    }
    return null;
  }

  /**
   * Paints a background for the view.  This will only be
   * called if isOpaque() on the associated component is
   * true.  The default is to paint the background color
   * of the component.
   *
   * @param g the graphics context
   */
  protected void paintBackground(Graphics g) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
  }

  @Override
  public void paintComponent(Graphics g) {
    if (!bValidFont) {
      super.paintComponent(g);
      return;
    }

    Highlighter highlighter = getHighlighter();

    // paint the background
    if (isOpaque()) {
      paintBackground(g);
    }

    // paint the highlights
    if (highlighter != null) {
      highlighter.paint(g);
    }

    // paint the view hierarchy
    Rectangle alloc = getVisibleEditorRect();
    if (alloc != null) {
      paint(g, alloc);
    }

    /* Can't paint the caret because Java 
     * doesn't understand our font sizes
     * and setting it to correct values fails due
     * to java clipping position back to where it thinks 
     * it should be so I just turn it off.
     */
//    if (caret != null) {
//      caret.paint(g);
//    }

  }
  
  public void paint(Graphics g, Rectangle r) {
    String content = getText();
    if (content.isEmpty()) return;
    Graphics2D g2d = (Graphics2D) g.create();
    if (isEditable()) {
      ff.drawText(g2d, FontTFT.ALIGN_LEFT, r, content, myFont, getForeground(),getForeground(), 0);
      paintCaret(g2d,r);
    } else {
      ff.drawText(g2d, FontTFT.ALIGN_LEFT, r, content, myFont, getForeground(),getForeground(), 0);
    }
    g2d.dispose();
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
  
  private void calculateGlyphs() {
    if (bValidFont) {
      String content = getText();
      int len = content.length();
//      Builder.logger.debug(String.format("calculateGlyphs: font=%s str[%s] len=%d",myFont.getFontName(),content,len));
      nCharWidths = new int[len];
      char ch;
      Dimension size;
      for (int i = 0; i < len; i++) {
        ch = content.charAt(i);
        size = ff.getCharSize(ch, myFont);
        if (size != null) {
          nCharWidths[i] = size.width;
        } else {
          nCharWidths[i] = 0;
        }
//        Builder.logger.debug("calculateGlyphs nCharWidths[" + i + "]=" + size.width);
      } 
    }
  }
  
  /**
   * calculatePos
   * This class determines our text caret 
   * position given a X axis coordinate.
   * @param x
   */
  private void calculatePos(int x) {
    
    if (bValidFont && isEditable()) {
      int len = getText().length();
      nCaretPos = len;
      int size = 0;
      for (int i=0; i<nCharWidths.length; i++) {
//        Builder.logger.debug(String.format("calculatePos nWidth[%d]=%d",i,nCharWidths[i]));
        size += nCharWidths[i];
        if (size > x) {
          nCaretPos = i;
//          Builder.logger.debug("calculatePos size>=x ->" + size);
          break;
        }
      }
      setCaretPosition(nCaretPos);
//      Builder.logger.debug("calculatePos nCaretPos=" + nCaretPos);
    }
  }

  /**
   * paintCaret
   * Draws our text caret
   * @param g2d
   * @param r
   */
  private void paintCaret(Graphics2D g2d, Rectangle r) {
    if (bValidFont && isEditable()) {
      g2d.setColor(getForeground());
      g2d.setStroke(new BasicStroke(2));
//      Builder.logger.debug("paintCaret nCaretPos=" + nCaretPos);
      int x = r.x;
      int len = getText().length();
      if (len < nCaretPos) nCaretPos = len + 1; 
      for (int i=0; i<nCaretPos; i++) {
        x += nCharWidths[i];
//        Builder.logger.debug("paintCaret nCharWidths[" + i + "]=" + nCharWidths[i] + " x=" + x);
      }
//      Builder.logger.debug("paintCaret new x=" + x);
//      g2d.drawLine(x+1, r.y+5, x+1, r.y+rAlloc.height-7);
      g2d.drawLine(x, r.y, x, r.y+rAlloc.height);
    }
  }

  /**
   * mouseClicked
   * Used to set our caret position and text cursor.
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseClicked(MouseEvent e) {
//    Builder.logger.debug("mouseClicked x=" + e.getX() + " nCaretPos=" + nCaretPos);
    calculatePos(e.getX());
    e.getComponent().repaint();
  } // end mouseClicked
  
  /**
   * mousePressed
   * Used to either show our mini-popup menu or start a 
   * mouse drag operation for a multi-character selection.
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  @Override
  public void mousePressed(MouseEvent e) {
    //if the user clicked the right mouse button
    if (javax.swing.SwingUtilities.isRightMouseButton(e))
    {
        //create (and show) the popup
        createPopup().show(e.getComponent(), e.getX(), e.getY());
    }
//    Builder.logger.debug(String.format("mousePressed: mousePt=%s",mousePt.toString()));
  } // end mousePressed

  /**
   * mouseReleased
   * a required routine - NOOP
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseReleased(MouseEvent e) {
  }

  /**
   * mouseEntered
   * a required routine - NOOP
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseEntered(MouseEvent e) {
  }

  /**
   * mouseExited
   * a required routine - NOOP
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseExited(MouseEvent e) {
    
  }

  /**
   * JPopupMenu
   * Creates our mini-popup menu to allow users
   * to either clear the text field or invoke
   * out Character Map dialog for selecting hard to enter 
   * characters.
   * @return the popup menu
   */
  private JPopupMenu createPopup()
  {
    JPopupMenu popupMenu = new JPopupMenu();
    //add the clearTextOption to the JPopupMenu
    JMenuItem clearTextOption = new JMenuItem("Clear text");
    clearTextOption.setActionCommand("clear");
    clearTextOption.addActionListener(this);
    popupMenu.add(clearTextOption);
    if (this.isEditable()) {
      JMenuItem characterMapOption = new JMenuItem("Character Map");
      characterMapOption.setActionCommand("map");
      characterMapOption.addActionListener(this);
      popupMenu.add(characterMapOption);
    }
    return popupMenu;
  }

  /**
   * MyCaretListener
   * This class provides a plug-in point that allows us to
   * size our Native Font text and therefore our text caret's position.
   */
  protected class MyCaretListener implements CaretListener {
    public void caretUpdate(CaretEvent e) {
      instance.calculateGlyphs();
      nCaretPos=e.getDot();
      instance.repaint();
//      Builder.logger.debug("MyCaretListener e=" + e.toString());
    } // end caretUpdate
  } // end MyCaretListener
  
  public class NoTextSelectionCaret extends DefaultCaret
  {
    private static final long serialVersionUID = 1L;

      public NoTextSelectionCaret(JTextComponent textComponent)
      {
          setBlinkRate( textComponent.getCaret().getBlinkRate() );
          textComponent.setHighlighter( null );
      }

      @Override
      public int getMark()
      {
          return getDot();
      }
  }
}
