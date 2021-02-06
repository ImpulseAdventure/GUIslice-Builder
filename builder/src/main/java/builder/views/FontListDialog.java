/**
 *
 * The MIT License
 *
 * Copyright (c) 2018-2021 Paul Conti
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import builder.fonts.FontChooser;
import hu.csekme.RibbonMenu.Util;

public class FontListDialog extends JDialog implements ActionListener, ListSelectionListener {
  private static final long serialVersionUID = 1L;

  private static FontListDialog dialog;
  private DefaultListModel<String> listModel;

  private JList<String> list;
  private JButton btn_remove;
  private JButton btn_add;
  private JButton btn_edit;
  private FontChooser chooser; 
  
  private static final String okString = "OK";
  private static final String cancelString = "cancel";
  private static final String addString = "add";
  private static final String removeString = "remove";
  private static final String editString = "edit";
  private static String[] emptyList = { "" };

  /**
   * Set up and show the dialog. The first Component argument determines which
   * frame the dialog depends on; it should be a component in the dialog's
   * controlling frame. The second Component argument should be null if you want
   * the dialog to come up with its left corner in the center of the screen;
   * otherwise, it should be the component on top of which the dialog should
   * appear.
   */
  public static String[] showDialog(Component frameComp, Component locationComp, String labelText, String title,
      String[] possibleValues) {
    Frame frame = JOptionPane.getFrameForComponent(frameComp);
    dialog = new FontListDialog(frame, locationComp, labelText, title, possibleValues);
    dialog.setVisible(true);
    return dialog.getData();
  }

  private FontListDialog(Frame frame, Component locationComp, String labelText, 
      String title, Object[] data) {
    super(frame, title, true);

    // Create and initialize the buttons.
    JButton okButton = new JButton("OK");
    okButton.setActionCommand(okString);
    okButton.setToolTipText("Press 'OK' when list is completed.");
    okButton.addActionListener(this);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(cancelString);
    cancelButton.setToolTipText("Press 'Cancel' to abort.");
    cancelButton.addActionListener(this);

    btn_add = new JButton("Add Font");
    btn_add.setActionCommand(addString);
    btn_add.addActionListener(this);
    btn_add.setToolTipText("Enter an item then press 'Add' to place in list.");
    
    btn_edit = new JButton("Edit Font");
    btn_edit.setActionCommand(editString);
    btn_edit.addActionListener(this);
    btn_edit.setToolTipText("Select an item then press to edit.");

    btn_remove = new JButton(Util.accessImageFile("resources/icons/edit/delete.png", 24,24));
    btn_remove.setActionCommand(removeString);
    btn_remove.addActionListener(this);
    btn_remove.setToolTipText("Select an item then press to delete.");
    btn_remove.setEnabled(false);

    // main part of the dialog
    listModel = new DefaultListModel<String>();
    if (data != null) {
      String testStr = (String)data[0];
      if (!testStr.isEmpty()) {
        for (int i=0; i<data.length; i++) {
          listModel.addElement((String) data[i]);
        }
      }
    }
    list = new JList<String>(listModel);
    list.setDragEnabled(false);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    list.addListSelectionListener(this);
    list.setVisibleRowCount(-1);
    JScrollPane scrollPane = new JScrollPane(list);
    scrollPane.setPreferredSize(new Dimension(100, 200));
    scrollPane.setAlignmentX(LEFT_ALIGNMENT);

    // Create a container so that we can add a title around
    // the scroll pane. Can't add a title directly to the
    // scroll pane because its background would be white.
    // Lay out the label and scroll pane from top to bottom.
    JPanel listPane = new JPanel();
    listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
    JLabel label = new JLabel(labelText);
    label.setLabelFor(list);
    listPane.add(label);
    listPane.add(Box.createRigidArea(new Dimension(0, 5)));
    listPane.add(scrollPane);
    listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Lay out the buttons from left to right.
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(btn_add);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(btn_edit);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(btn_remove);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(okButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(cancelButton);

    // Put everything together, using the content pane's BorderLayout.
    Container contentPane = getContentPane();
    contentPane.add(listPane, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.PAGE_END);

    pack();
    setLocationRelativeTo(locationComp);
  }
  
  public String[] getData() {
    if (listModel.getSize() == 0) {
      return emptyList;
    }
    String[] data = new String[listModel.getSize()];
    for(int i = 0; i < listModel.getSize(); i++) {
      data[i] = listModel.getElementAt(i);
    }
    return data;
  }

  // Handle clicks on the Add and Cancel buttons.
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    int index = list.getSelectedIndex();
    switch (command) {
      case addString:
        chooser = new FontChooser(null);
        chooser.setFontName("");
        String newName = chooser.showDialog();
        if (newName != null) {
          addItem(newName);
        }
        break;
      case removeString:
        // no need to test for valid selection
        // you can't get here unless valid
        // so go ahead and remove whatever's selected.
        listModel.remove(index);
        int size = listModel.getSize();
        if (size == 0) { // Nothing left, disable removal.
          btn_remove.setEnabled(false);
        } else { // Select an index.
          if (index == listModel.getSize()) {
            // removed item in last position
            index--;
          }
          list.setSelectedIndex(index);
          list.ensureIndexIsVisible(index);
        }
        break;
      case editString: 
        // test for valid selection
        if (index == -1) {
          JOptionPane.showMessageDialog(null, 
              "You must first select a Font to edit.",
              "Warning",
              JOptionPane.WARNING_MESSAGE);
          return;
        } else {
          String name = (String)listModel.getElementAt(index);
          chooser = new FontChooser(null);
          chooser.setFontName(name);
          name = chooser.showDialog();
          if (name != null) {
            listModel.set(index, name); 
          }
        }
        break;
      case okString:
        FontListDialog.dialog.setVisible(false);
        FontListDialog.dialog.dispose();
        break;
      case cancelString:
        listModel.clear();
        FontListDialog.dialog.setVisible(false);
        FontListDialog.dialog.dispose();
        break;
     default:
       break;
    }
  }
  
  public void addItem(String name) {
    //User didn't type in a unique name...
    if (name.equals("") || alreadyInList(name)) {
      JOptionPane.showMessageDialog(null, 
          "Sorry, You can't duplicate fonts in the list.", 
          "ERROR",
          JOptionPane.ERROR_MESSAGE);
        return;
    }
    listModel.addElement(name);
  }
  
  //This method is required by ListSelectionListener.
  public void valueChanged(ListSelectionEvent e) {
      if (e.getValueIsAdjusting() == false) {

          if (list.getSelectedIndex() == -1) {
          //No selection, disable remove button.
            btn_remove.setEnabled(false);

          } else {
          //Selection, enable the remove button.
            btn_remove.setEnabled(true);
          }
      }
  }

  //This method tests for string equality. You could certainly
  //get more sophisticated about the algorithm.  For example,
  //you might want to ignore white space and capitalization.
  protected boolean alreadyInList(String name) {
      return listModel.contains(name);
  }

}
