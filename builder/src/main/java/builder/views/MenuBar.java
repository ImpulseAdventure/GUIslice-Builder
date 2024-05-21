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
package builder.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
//import javax.swing.ImageIcon;

import builder.common.Utils;
import builder.swing.MyToggleButton;
import builder.swing.MyToggleButtonMenuItem;

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
  private JMenuItem miNew, miOpen, miSave, miSaveAs, miCode, miClose, miExit;
  
  /** The edit menu item. */
  private JMenuItem miUndo,miRedo,miCopy,miCut,miPaste, miOptions, miDelete;
  
  /** The about menu item. */
  private JMenuItem miAbout;

  public static JMenuItem miZoomIn, miZoomOut, miZoomReset;
  public static MyToggleButtonMenuItem miShowGrid;
  public static MyToggleButton miSnapGrid;
  
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
        Utils.getIcon("resources/icons/file/new.png", 24,24));
    miNew.setActionCommand("new");
    miNew.setAccelerator(KeyStroke.getKeyStroke(
        'N', ActionEvent.CTRL_MASK));
    miNew.setToolTipText("Create New Project File");
    mbFile.add(miNew);
    

    miOpen = new JMenuItem("Open", 
      Utils.getIcon("resources/icons/file/open.png", 24,24));
    miOpen.setActionCommand("open");
    miOpen.setAccelerator(KeyStroke.getKeyStroke(
        'O', ActionEvent.CTRL_MASK));
    miOpen.setToolTipText("Open Project File");
    mbFile.add(miOpen);
    
    miClose = new JMenuItem("Close", 
      Utils.getIcon("resources/icons/file/close.png", 24,24));
    miClose.setActionCommand("close");
    miClose.setAccelerator(KeyStroke.getKeyStroke(
        'W', ActionEvent.CTRL_MASK));
    miClose.setToolTipText("Close Project File");
    mbFile.add(miClose);
    
    miSave = new JMenuItem("Save", 
      Utils.getIcon("resources/icons/file/save.png", 24,24));
    miSave.setActionCommand("save");
    miSave.setAccelerator(KeyStroke.getKeyStroke(
        'S', ActionEvent.CTRL_MASK));
    miSave.setToolTipText("Save Project File");
    mbFile.add(miSave);
    
    miSaveAs = new JMenuItem("Save As...", 
      Utils.getIcon("resources/icons/file/saveas.png", 24,24));
    miSaveAs.setActionCommand("saveas");
    miSaveAs.setToolTipText("Save As Renaming Project File");
    mbFile.add(miSaveAs);
    
    mbFile.add(new JSeparator()); 
    
    miCode = new JMenuItem("Generate Code", 
      Utils.getIcon("resources/icons/file/export.png", 24,24));
    miCode.setActionCommand("code");
    miCode.setAccelerator(KeyStroke.getKeyStroke(
        'G', ActionEvent.CTRL_MASK));
    miCode.setToolTipText("Create C Code Output Files");
    mbFile.add(miCode);
    
    mbFile.add(new JSeparator()); 
    
    miExit = new JMenuItem("Exit", 
      Utils.getIcon("resources/icons/file/logout.png", 24,24));
    miExit.setActionCommand("exit");
    miExit.setAccelerator(KeyStroke.getKeyStroke(
        'E', ActionEvent.CTRL_MASK));
    miExit.setToolTipText("Exit Program");
    mbFile.add(miExit);
    
    this.add(mbFile);
  }
  
  public void initViewMenus() {
    mbView = new JMenu("View");

    miZoomIn = new JMenuItem("Zoom In",
      Utils.getIcon("resources/icons/view/zoom_in.png", 24,24));
    miZoomIn.setActionCommand("zoomin");
    miZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
    miZoomIn.setToolTipText("Zoom In TFT Simulation page");
    mbView.add(miZoomIn);

    miZoomOut = new JMenuItem("Zoom Out",
      Utils.getIcon("resources/icons/view/zoom_out.png", 24,24));
    miZoomOut.setDisabledIcon(
      Utils.getIcon("resources/icons/view/disable_zoom_out.png", 24,24));
    miZoomOut.setEnabled(false);
    miZoomOut.setActionCommand("zoomout");
    miZoomOut.setAccelerator(KeyStroke.getKeyStroke('-', ActionEvent.CTRL_MASK));
    miZoomOut.setToolTipText("Zoom Out TFT Simulation page");
    mbView.add(miZoomOut);

    miZoomReset = new JMenuItem("Zoom Reset",
      Utils.getIcon("resources/icons/view/zoom_reset.png", 24,24));
    miZoomReset.setDisabledIcon(
      Utils.getIcon("resources/icons/view/disable_zoom_reset.png", 24,24));
    miZoomReset.setEnabled(false);
    miZoomReset.setActionCommand("zoomreset");
    miZoomReset.setAccelerator(KeyStroke.getKeyStroke('0', ActionEvent.CTRL_MASK));
    miZoomReset.setToolTipText("Reset Zoom to 100%");
    mbView.add(miZoomReset);

//    miShowGrid = new JMenuItem("Show grid",
//      Utils.getIcon("resources/icons/view/show_grid.png", 24,24));

    miShowGrid = new MyToggleButtonMenuItem("Show grid",
      Utils.getIcon("resources/icons/view/invisible.png", 24,24));
    miShowGrid.setOffImage(Utils.getIcon("resources/icons/view/show_grid_off.png", 24,24));
    miShowGrid.setOnImage(Utils.getIcon("resources/icons/view/show_grid_on.png", 24,24));
    miShowGrid.setActionCommand("showgrid");
    miShowGrid.setAccelerator(KeyStroke.getKeyStroke(
        'L', ActionEvent.CTRL_MASK));
    miShowGrid.setToolTipText("Toggle Grid ON/OFF");
    mbView.add(miShowGrid);

//    miSnapGrid = new JMenuItem("Snap to grid",
//    Utils.getIcon("resources/icons/view/snap_to_grid.png", 24,24));
    miSnapGrid = new MyToggleButton("Snap to grid",
      Utils.getIcon("resources/icons/view/invisible.png", 24,24));
    miSnapGrid.setOffImage(Utils.getIcon("resources/icons/view/snap_to_grid_off.png", 24,24));
    miSnapGrid.setOnImage (Utils.getIcon("resources/icons/view/snap_to_grid_on.png", 24,24));
    miSnapGrid.setActionCommand("snaptogrid");
    miSnapGrid.setToolTipText("Toggle Snap to Grid ON/OFF");
    mbView.add(miSnapGrid);

    this.add(mbView);
  }
  
  /**
   * Initializes the edit menus.
   */
  public void initEditMenus() {
    mbEdit = new JMenu("Edit");
    miUndo = new JMenuItem("Undo", 
      Utils.getIcon("resources/icons/edit/undo.png", 24,24));
    miUndo.setActionCommand("undo");
    miUndo.setAccelerator(KeyStroke.getKeyStroke(
        'Z', ActionEvent.CTRL_MASK));
    miUndo.setToolTipText("Undo edits to your project file.");
    mbEdit.add(miUndo);
    
    miRedo = new JMenuItem("Redo", 
        Utils.getIcon("resources/icons/edit/redo.png", 24,24));
    miRedo.setActionCommand("redo");
    miRedo.setAccelerator(KeyStroke.getKeyStroke(
          'Y', ActionEvent.CTRL_MASK));
    miRedo.setToolTipText("Re-apply edits to your project file.");
    mbEdit.add(miRedo);
      
    mbEdit.add(new JSeparator()); 
    miCopy = new JMenuItem("Copy", 
      Utils.getIcon("resources/icons/edit/copy.png", 24,24));
    miCopy.setActionCommand("copy");
    miCopy.setAccelerator(KeyStroke.getKeyStroke(
        'C', ActionEvent.CTRL_MASK));
    miCopy.setToolTipText("Copy element selections to clipboard.");
    mbEdit.add(miCopy);

    miCut = new JMenuItem("Cut", 
      Utils.getIcon("resources/icons/edit/cut.png", 24,24));
    miCut.setActionCommand("cut");
    miCut.setAccelerator(KeyStroke.getKeyStroke(
        'X', ActionEvent.CTRL_MASK));
    miCut.setToolTipText("Cut element selections from page.");
    mbEdit.add(miCut);

    miPaste = new JMenuItem("Paste", 
      Utils.getIcon("resources/icons/edit/paste.png", 24,24));
    miPaste.setActionCommand("paste");
    miPaste.setAccelerator(KeyStroke.getKeyStroke(
        'V', ActionEvent.CTRL_MASK));
    miPaste.setToolTipText("Paste element(s) from the clipboard.");
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
//    Icon deleteIcon = new ImageIcon(Builder.class.getResource("/resources/icons/edit/delete.png"));
    Icon deleteIcon = Utils.getIcon("resources/icons/edit/delete.png", 24,24);

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
      Utils.getIcon("resources/icons/edit/delete.png", 24,24));
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
    miDelete.setToolTipText("Delete selected element(s) from your project file.");
    mbEdit.add(miDelete);
    
    mbEdit.add(new JSeparator()); 
    
    miOptions = new JMenuItem("Options", 
      Utils.getIcon("resources/icons/misc/options.png", 24,24));
    miOptions.setActionCommand("options");
    miOptions.setToolTipText("View/Edit User Preferences.");
    mbEdit.add(miOptions);
    
    this.add(mbEdit);
  }

  /**
   * Initializes the help.
   */
  public void initHelp() {
    mbHelp = new JMenu("Help");
    
    miAbout= new JMenuItem("About", 
        Utils.getIcon("resources/icons/misc/about.png", 24,24));
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
    miUndo.addActionListener(al); 
    miRedo.addActionListener(al); 
    miCode.addActionListener(al);
    miCopy.addActionListener(al); 
    miCut.addActionListener(al); 
    miPaste.addActionListener(al); 
    miDelete.addActionListener(al); 
    miOptions.addActionListener(al); 
    miAbout.addActionListener(al);
    miClose.addActionListener(al);
    miExit.addActionListener(al);
    miShowGrid.addActionListener(al);
    miSnapGrid.addActionListener(al);
    miZoomIn.addActionListener(al);
    miZoomOut.addActionListener(al);
    miZoomReset.addActionListener(al);
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
