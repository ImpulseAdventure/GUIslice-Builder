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
package builder.tables;

import javax.swing.AbstractCellEditor;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;

import builder.codegen.CodeGenerator;
import builder.common.CommonUtils;
import builder.models.GeneralModel;
import builder.prefs.GeneralEditor;
import builder.views.ImagePreviewPanel;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * <p>
 * The Class ImageCellEditor treats the font cell string as a button and
 * calls the custom GUIslice font chooser.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class ImageCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener  {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The file. */
  String imageName;

  /** The button. */
  JButton button;
  
  /** The chooser. */
  JFileChooser chooser;
  
  /** The dialog. */
  JDialog dialog;
  
  /** The Constant EDIT. */
  protected static final String EDIT = "edit";

  /**
   * Instantiates a new image cell editor.
   */
  public ImageCellEditor() {
//    generalModel = (GeneralModel) GeneralEditor.getInstance().getModel();

    //This button brings up the file chooser dialog,
    //which is the editor from the user's point of view.
    button = new JButton();
    button.setBackground(Color.white);
    button.setFont(button.getFont().deriveFont(Font.PLAIN));
    button.setActionCommand(EDIT);
    button.addActionListener(this);
//    button.setBorderPainted(false);
    button.setBorder(null);

    //Set up the dialog that the button brings up.
    chooser = new JFileChooser();
    chooser.setDialogTitle("Choose your Background Image");
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.addChoosableFileFilter(new FileFilter() {
      public String getDescription() {
//        if (!generalModel.getTarget().equals("linux"))
//          return "BMP Images (*.bmp), C File with extern image (*.c)";
//        else
          return "BMP Images (*.bmp)";
      }

      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        } else {
          if ((f.getName().toLowerCase().endsWith(".bmp")))
            return true;
/*
          if ((f.getName().toLowerCase().endsWith(".bmp")) ||
              (f.getName().toLowerCase().endsWith(".c") && 
               !generalModel.getTarget().equals("linux")) )
            return true;
*/
          
          return false;
        }
      }
    });
    ImagePreviewPanel preview = new ImagePreviewPanel();
    chooser.setAccessory(preview);
    chooser.addPropertyChangeListener(preview);

  }

  /**
   * getCellEditorValue
   *
   * @see javax.swing.CellEditor#getCellEditorValue()
   */
  //Implement the one CellEditor method that AbstractCellEditor doesn't.
  public Object getCellEditorValue() {
      return imageName;
  }

  /**
   * getTableCellEditorComponent
   *
   * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
   */
  //Implement the one method defined by TableCellEditor.
  public Component getTableCellEditorComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               int row,
                                               int column) 
  {
    String imageName = ((String)value);
    button.setText(imageName);
    return button;
  }

  /**
   * actionPerformed
   *
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (EDIT.equals(e.getActionCommand())) {
      File file = null;
      File currentDirectory = null;
      // look for images in the last folder accessed
      GeneralModel generalModel = ((GeneralModel)GeneralEditor.getInstance().getModel());
      String resDir = (String) generalModel.getImageDir();
      if (resDir.isEmpty()) {
//        if (generalModel.getTarget().equals("linux")) {
//          resDir = CodeGenerator.LINUX_RES;
//        } else {
          resDir = CodeGenerator.ARDUINO_RES;
//        }
        String workingDir = CommonUtils.getWorkingDir();
        currentDirectory = new File(workingDir + resDir);
      } else {
        currentDirectory = new File(resDir);
      }
      chooser.setCurrentDirectory(currentDirectory);
      int option = chooser.showDialog(new JFrame(), "Select");
      if (option == JFileChooser.APPROVE_OPTION) {
        file = chooser.getSelectedFile();
        generalModel.setImageDir(file.getParent());
        imageName = file.getAbsolutePath();
      } else {
        imageName = "";
      }
      
      //Make the renderer reappear.
      fireEditingStopped();
    }
  }
}
