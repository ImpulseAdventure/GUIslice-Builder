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
package builder.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import builder.Builder;

/**
 * The Class MenuBar.
 * 
 * @author Paul Conti
 * 
 */
public class MenuBar extends JMenuBar {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The mb help. */
  JMenu mbFile, mbView, mbEdit, mbHelp;
  
  /** The exit menu item. */
  private JMenuItem miNew, miOpen, miSave, 
    miSaveAs, miCode, miClose, miExit;
  
  /** The edit menu item. */
  private JMenuItem miCopy,miCut,miPaste, miOptions, miDelete;
  
  /** The about menu item. */
  private JMenuItem miAbout;
  
  private JMenuItem miZoomIn, miZoomOut, miGrid;
  
  /**
   * Instantiates a new menu bar.
   */
  public MenuBar() {
    initFileMenus();
    initEditMenus();
    initViewMenus();
    initHelp();
  }
  
  /**
   * Initializes the file menus.
   */
  public void initFileMenus() {
    mbFile = new JMenu("File");
    
    miNew = new JMenuItem("New", 
        new ImageIcon(Builder.class.getResource("/resources/icons/file/new.png")));
    miNew.setActionCommand("new");
    miNew.setAccelerator(KeyStroke.getKeyStroke(
        'N', ActionEvent.CTRL_MASK));
    mbFile.add(miNew);
    
    miOpen = new JMenuItem("Open", 
        new ImageIcon(Builder.class.getResource("/resources/icons/file/open.png")));
    miOpen.setActionCommand("open");
    miOpen.setAccelerator(KeyStroke.getKeyStroke(
        'O', ActionEvent.CTRL_MASK));
    mbFile.add(miOpen);
    
    miClose = new JMenuItem("Close", 
        new ImageIcon(Builder.class.getResource("/resources/icons/file/close.png")));
    miClose.setActionCommand("close");
    miClose.setAccelerator(KeyStroke.getKeyStroke(
        'W', ActionEvent.CTRL_MASK));
    mbFile.add(miClose);
    
    miSave = new JMenuItem("Save", 
        new ImageIcon(Builder.class.getResource("/resources/icons/file/save.png")));
    miSave.setActionCommand("save");
    miSave.setAccelerator(KeyStroke.getKeyStroke(
        'S', ActionEvent.CTRL_MASK));
    mbFile.add(miSave);
    
    miSaveAs = new JMenuItem("Save As...", 
        new ImageIcon(Builder.class.getResource("/resources/icons/file/saveas.png")));
    miSaveAs.setActionCommand("saveas");
    mbFile.add(miSaveAs);
    
    mbFile.add(new JSeparator()); 
    
    miCode = new JMenuItem("Generate Code", 
        new ImageIcon(Builder.class.getResource("/resources/icons/file/export.png")));
    miCode.setActionCommand("code");
    miCode.setAccelerator(KeyStroke.getKeyStroke(
        'G', ActionEvent.CTRL_MASK));
    mbFile.add(miCode);
    
    mbFile.add(new JSeparator()); 
    
    miExit = new JMenuItem("Exit", 
        new ImageIcon(Builder.class.getResource("/resources/icons/file/logout.png")));
    miExit.setActionCommand("exit");
    miExit.setAccelerator(KeyStroke.getKeyStroke(
        'E', ActionEvent.CTRL_MASK));
    mbFile.add(miExit);
    
    this.add(mbFile);
  }
  
  public void initViewMenus() {
    mbView = new JMenu("View");
    miGrid = new JMenuItem("Grid",
        new ImageIcon(Builder.class.getResource("/resources/icons/view/grid.png")));
    miGrid.setActionCommand("grid");
    miGrid.setAccelerator(KeyStroke.getKeyStroke(
        'L', ActionEvent.CTRL_MASK));
    mbView.add(miGrid);
    miZoomIn = new JMenuItem("Zoom In",
        new ImageIcon(Builder.class.getResource("/resources/icons/view/zoom_in.png")));
    miZoomIn.setActionCommand("zoomin");
    miZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
    mbView.add(miZoomIn);
    miZoomOut = new JMenuItem("Zoom Out",
        new ImageIcon(Builder.class.getResource("/resources/icons/view/zoom_out.png")));
    miZoomOut.setActionCommand("zoomout");
    miZoomOut.setAccelerator(KeyStroke.getKeyStroke('-', ActionEvent.CTRL_MASK));
    mbView.add(miZoomOut);
    this.add(mbView);
  }
  
  /**
   * Initializes the edit menus.
   */
  public void initEditMenus() {
    mbEdit = new JMenu("Edit");
    
    miCopy = new JMenuItem("Copy", 
        new ImageIcon(Builder.class.getResource("/resources/icons/edit/copy.png")));
    miCopy.setActionCommand("copy");
    miCopy.setAccelerator(KeyStroke.getKeyStroke(
        'C', ActionEvent.CTRL_MASK));
    mbEdit.add(miCopy);

    miCut = new JMenuItem("Cut", 
        new ImageIcon(Builder.class.getResource("/resources/icons/edit/cut.png")));
    miCut.setActionCommand("cut");
    miCut.setAccelerator(KeyStroke.getKeyStroke(
        'X', ActionEvent.CTRL_MASK));
    mbEdit.add(miCut);

    miPaste = new JMenuItem("Paste", 
        new ImageIcon(Builder.class.getResource("/resources/icons/edit/paste.png")));
    miPaste.setActionCommand("paste");
    miPaste.setAccelerator(KeyStroke.getKeyStroke(
        'V', ActionEvent.CTRL_MASK));
    mbEdit.add(miPaste);

    mbEdit.add(new JSeparator()); 
    
    /*
     *  WOW, you are not going to believe how much effort
     *  is involved with getting a MenuItem to support two
     *  key strokes.  In the case of "delete" we want
     *  both ctrl-D and the Command "Delete" keys supported.
     */
    // start with creating the key strokes we need
    KeyStroke keyStrokeDeleteCtrl = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK);
    KeyStroke keyStrokeDeleteCmd = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
    /*
     *  Now create the Action, most examples show doing this inline,
     *  but No, can't cast from AbstractAction to Action
     *  so I create a class and extend instead.
     */
    Icon deleteIcon = new ImageIcon(Builder.class.getResource("/resources/icons/edit/delete.png"));
    DeleteAction deleteAction = new DeleteAction("Delete", deleteIcon);
    /*
     *  Nothing happens unless we also place the KeyStroke into 
     *  this actions property map. Maybe this should be an
     *  Argument to our Action constructor?
     */
    deleteAction.putValue(Action.ACCELERATOR_KEY,
        keyStrokeDeleteCtrl);
    // Obviously, we need to create the menuitem
    miDelete = new JMenuItem("Delete", 
        new ImageIcon(Builder.class.getResource("/resources/icons/edit/delete.png")));
    // set menuitem to use our delete action
    miDelete.setAction(deleteAction);
    /*
     *  Almost there, only ctrl-D supported, so grab one of the 
     *  input maps for this memuitem. There are 3 such maps we
     *  will use the JComponent.WHEN_IN_FOCUSED_WINDOW one.
     */
    InputMap im = miDelete.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    // Finally we add the second keyStroke to the input map and DONE!
    im.put(keyStrokeDeleteCmd, im.get(keyStrokeDeleteCtrl));
    // well we do need to add the menuitem to our menu...
    mbEdit.add(miDelete);
    
    mbEdit.add(new JSeparator()); 
    
    miOptions = new JMenuItem("Options", 
        new ImageIcon(Builder.class.getResource("/resources/icons/misc/options.png")));
    miOptions.setActionCommand("options");
    mbEdit.add(miOptions);
    
    this.add(mbEdit);
  }

  /**
   * Initializes the help.
   */
  public void initHelp() {
    mbHelp = new JMenu("Help");
    
    miAbout= new JMenuItem("About", 
        new ImageIcon(Builder.class.getResource("/resources/icons/misc/about.png")));
    miAbout.setActionCommand("about");
    mbHelp.add(miAbout);
    
    this.add(mbHelp);
  }
  
  /**
   * Adds the listeners.
   *
   * @param al
   *          the object that implements ActionListener
   */
  public void addListeners(ActionListener al)
  {
    miNew.addActionListener(al);
    miOpen.addActionListener(al); 
    miSave.addActionListener(al);
    miSaveAs.addActionListener(al);
    miCode.addActionListener(al);
    miCopy.addActionListener(al); 
    miCut.addActionListener(al); 
    miPaste.addActionListener(al); 
    miDelete.addActionListener(al); 
    miOptions.addActionListener(al); 
    miAbout.addActionListener(al);
    miClose.addActionListener(al);
    miExit.addActionListener(al);
    miGrid.addActionListener(al);
    miZoomIn.addActionListener(al);
    miZoomOut.addActionListener(al);
  }
  
  @SuppressWarnings("serial")
  public class DeleteAction extends AbstractAction {
     public DeleteAction(String text, Icon icon) {
       super(text, icon);  // the text will be the action command.
     }

    @Override
    public void actionPerformed(ActionEvent e) {
      // No need to do anything here
    }
  }
  
}
