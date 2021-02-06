/**
 *
 * The MIT License
 *
 * Copyright 2018-2021 Paul Conti
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

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import hu.csekme.RibbonMenu.Util;

/**
 * The Class ToolBar.
 * 
 * @author Paul Conti
 * 
 */
public class ToolBar {
  
  /** The instance. */
  private static ToolBar instance = null;
  
  /** our actual toolbar */
  private static JToolBar toolbar = null;
  
  /** The file buttons. */
  public JButton btn_new, btn_open, btn_close,btn_save,
    btn_code, btn_exit;
  
  /** The edit buttons. */
  public JButton btn_selection, btn_copy_props, btn_paste, btn_copy, btn_cut, btn_undo,btn_redo,btn_delete;
  
  /**
   * Gets the single toolbar of ToolBar.
   *
   * @return single toolbar of ToolBar
   */
  public static synchronized ToolBar getInstance() {
    if (instance == null) {
      instance = new ToolBar();
      instance.initQuitButtons();
      instance.initEditButtons();
      instance.initFileButtons();  
    }
    return instance;
  }

  /**
   * Instantiates a new tool bar.
   */
  public ToolBar() {
    toolbar = new JToolBar(JToolBar.VERTICAL);
  }
  
  public JToolBar get() {
    return toolbar;
  }
  
  /**
   * Initializes the file buttons.
   */
  public void initFileButtons() {
    btn_new = new JButton(Util.accessImageFile("resources/icons/file/new.png",22,22));
    btn_new.setToolTipText("New Project");
    btn_new.setActionCommand("new");
    toolbar.add(btn_new);

    btn_open = new JButton(
        Util.accessImageFile("resources/icons/file/open.png",22,22));
    btn_open.setToolTipText("Open Project");
    btn_open.setActionCommand("open");
    toolbar.add(btn_open);

    btn_close = new JButton(
        Util.accessImageFile("resources/icons/file/close.png",22,22));
    btn_close.setToolTipText("Close Project");
    btn_close.setActionCommand("close");
    toolbar.add(btn_close);

    btn_save = new JButton(
        Util.accessImageFile("resources/icons/file/save.png",22,22));
    btn_save.setToolTipText("Save Project");
    btn_save.setActionCommand("save");
    toolbar.add(btn_save);
  }
  
  /**
   * Initializes the undo buttons.
   */
  public void initEditButtons() {
    
    btn_undo = new JButton(
        Util.accessImageFile("resources/icons/edit/undo.png",22,22));
    btn_undo.setDisabledIcon(
        Util.accessImageFile("resources/icons/edit/disable_undo.png",22,22));
    btn_undo.setEnabled(false);
    btn_undo.setToolTipText("Undo Actions");
    btn_undo.setActionCommand("undo");
    toolbar.add(btn_undo);

    btn_redo = new JButton(
        Util.accessImageFile("resources/icons/edit/redo.png",22,22));
    btn_redo.setDisabledIcon(
        Util.accessImageFile("resources/icons/edit/disable_redo.png",22,22));
    btn_redo.setEnabled(false);
    btn_redo.setToolTipText("Redo Actions");
    btn_redo.setActionCommand("redo");
    toolbar.add(btn_redo);
    
    btn_delete = new JButton(
        Util.accessImageFile("resources/icons/edit/delete.png",22,22));
    btn_delete.setToolTipText("Delete Widget");
    btn_delete.setActionCommand("delete");
    toolbar.add(btn_delete);
    
    btn_selection = new JButton(
        Util.accessImageFile("resources/icons/layout/selection.png",22,22));
    btn_selection.setToolTipText("Rectangular Selection");
    btn_selection.setActionCommand("selection");
    toolbar.add(btn_selection);
    
    btn_copy_props = new JButton(
        Util.accessImageFile("resources/icons/edit/copy_props.png",22,22));
    btn_copy_props.setToolTipText("Copy Properties");
    btn_copy_props.setActionCommand("copyprops");
    toolbar.add(btn_copy_props);
        
    btn_paste = new JButton(
        Util.accessImageFile("resources/icons/edit/paste.png",22,22));
    btn_paste.setToolTipText("Paste");
    btn_paste.setActionCommand("paste");
    toolbar.add(btn_paste);

    btn_copy = new JButton(
        Util.accessImageFile("resources/icons/edit/copy.png",22,22));
    btn_copy.setToolTipText("Copy");
    btn_copy.setActionCommand("copy");
    toolbar.add(btn_copy);
    
    btn_cut = new JButton(
        Util.accessImageFile("resources/icons/edit/cut.png",22,22));
    btn_cut.setToolTipText("cut");
    btn_cut.setActionCommand("cut");
    toolbar.add(btn_cut);
    
  }
  
  /**
   * Initializes the quit buttons.
   */
  public void initQuitButtons() {
    btn_exit = new JButton(
        Util.accessImageFile("resources/icons/file/logout.png",22,22));
    btn_exit.setToolTipText("Exit Builder");
    btn_exit.setActionCommand("exit");
    toolbar.add(btn_exit);

    btn_code = new JButton(
        Util.accessImageFile("resources/icons/file/export.png",22,22));
    btn_code.setToolTipText("Generate Code");
    btn_code.setActionCommand("code");
    toolbar.add(btn_code);
    
  }
  
  /**
   * Adds the listeners.
   *
   * @param al
   *          the object that implements ActionListener
   */
  public void addListeners(ActionListener al)
  {
   
    btn_new.addActionListener(al);
    btn_open.addActionListener(al);
    btn_save.addActionListener(al);
    btn_close.addActionListener(al);

    btn_code.addActionListener(al);
    btn_exit.addActionListener(al);
 
    btn_undo.addActionListener(al);
    btn_redo.addActionListener(al);
    btn_delete.addActionListener(al);
    btn_selection.addActionListener(al);
    btn_paste.addActionListener(al);
    btn_copy.addActionListener(al);
    btn_copy_props.addActionListener(al);
    btn_cut.addActionListener(al);

  }
  
  public void enableUndo(boolean bEnable) {
    btn_undo.setEnabled(bEnable);
  }
  
  public void enableRedo(boolean bEnable) {
    btn_redo.setEnabled(bEnable);
  }
  
}
