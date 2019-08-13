package builder.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFormattedTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatter;

public class StringListDialog extends JDialog implements ActionListener, ListSelectionListener {
  private static final long serialVersionUID = 1L;

  private static StringListDialog dialog;
  private DefaultListModel<String> listModel;

  private JList<String> list;
  private JFormattedTextField itemName;
  private JButton btn_remove;
  private JButton btn_add;
  
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
    dialog = new StringListDialog(frame, locationComp, labelText, title, possibleValues);
    dialog.setVisible(true);
    return dialog.getData();
  }

  private StringListDialog(Frame frame, Component locationComp, String labelText, 
      String title, Object[] data) {
    super(frame, title, true);

    // Create and initialize the buttons.
    JButton okButton = new JButton("OK");
    okButton.setActionCommand("ok");
    okButton.setToolTipText("Press 'OK' when list is completed.");
    okButton.addActionListener(this);
    //
    btn_add = new JButton("Add Item");
    btn_add.setActionCommand("add");
    btn_add.addActionListener(this);
    btn_add.setToolTipText("Enter an item then press 'Add Item' to place in list.");
    
    btn_remove = new JButton("Remove");
    btn_remove.setActionCommand("remove");
    btn_remove.addActionListener(this);
    btn_remove.setToolTipText("Select an item then press 'Remove' to delete.");
    btn_remove.setEnabled(false);
    AddItemListener addItemListener = new AddItemListener(btn_remove);

    itemName = new JFormattedTextField(new DefaultFormatter());
    itemName.setColumns(20);
    itemName.addActionListener(addItemListener);
    itemName.getDocument().addDocumentListener(addItemListener);
    itemName.setToolTipText("<html>" +
      "Type Text and press ENTER key to add item to list.<br>" +
      "Insert after a particular item by selecting the item first.<br>" +
      "Reorder items by drag and drop of an item<br>" +
      "</html");

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
    list.setDragEnabled(true);
//    list.setDropMode(DropMode.INSERT);
    list.setDropMode(DropMode.ON_OR_INSERT );
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    list.setTransferHandler(new TransferHandler() {
      private static final long serialVersionUID = 1L;
      private int index;
      //Start with `false` therefore if it is removed from or added to the list it still works
      private boolean beforeIndex = false; 

      @Override
      public int getSourceActions(JComponent comp) {
        return MOVE;
      }

      @Override
      public Transferable createTransferable(JComponent comp) {
        index = list.getSelectedIndex(); 
        return new StringSelection(list.getSelectedValue());
      }

      @Override
      public void exportDone(JComponent comp, Transferable trans, int action) {
        if (action == MOVE) {
          if(beforeIndex)
          listModel.remove(index + 1);
          else
          listModel.remove(index);
        }
      }

      @Override
      public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
      }

      @Override
      public boolean importData(TransferHandler.TransferSupport support) {
        try {
          String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
          JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
          listModel.add(dl.getIndex(), s);
          beforeIndex = dl.getIndex() < index ? true : false;
          return true;
        } catch (UnsupportedFlavorException | IOException e) {
          e.printStackTrace();
        }

        return false;
      }
    });

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
    buttonPane.add(itemName);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(btn_remove);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(okButton);

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
    int index = 0;
    switch (command) {
      case "add":
        try {
          itemName.commitEdit();
          addItem(itemName.getText());
        } catch (ParseException e1) {
          return;
        }
        break;
      case "remove":
        // This method can be called only if
        // there's a valid selection
        // so go ahead and remove whatever's selected.
        index = list.getSelectedIndex();
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
      case "ok":
        try {
          itemName.commitEdit();
          addItem(itemName.getText());
        } catch (ParseException e1) {
          return;
        }
        StringListDialog.dialog.setVisible(false);
        StringListDialog.dialog.dispose();
        break;
     default:
       break;
    }
  }
  
  //This listener is used by the text field.
  class AddItemListener implements ActionListener, DocumentListener {
      private boolean alreadyEnabled = false;
      private JButton button;

      public AddItemListener(JButton button) {
          this.button = button;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
        String name = itemName.getText();
        int index = addItem(name);
        if (index != -1) {
          enableButton();

          //Reset the text field.
          itemName.requestFocusInWindow();

          //Select the new item and make it visible.
          list.setSelectedIndex(index);
          list.ensureIndexIsVisible(index);
        }
      }

      //Required by DocumentListener.
      public void insertUpdate(DocumentEvent e) {
        enableButton();
      }

      //Required by DocumentListener.
      public void removeUpdate(DocumentEvent e) {
          handleEmptyTextField(e);
      }

      //Required by DocumentListener.
      public void changedUpdate(DocumentEvent e) {
          if (!handleEmptyTextField(e)) {
              enableButton();
          }
      }

      private void enableButton() {
          if (!alreadyEnabled) {
              button.setEnabled(true);
          }
      }

      private boolean handleEmptyTextField(DocumentEvent e) {
          if (e.getDocument().getLength() <= 0) {
              button.setEnabled(false);
              alreadyEnabled = false;
              return true;
          }
          return false;
      }

  }

  public int addItem(String name) {
    //User didn't type in a unique name...
    if (name.equals("") || alreadyInList(name)) {
        Toolkit.getDefaultToolkit().beep();
        itemName.requestFocusInWindow();
        itemName.selectAll();
        return -1;
    }

    int index = list.getSelectedIndex(); //get selected index
    if (index == -1) { //no selection, so insert at end
      listModel.addElement(itemName.getText());
    } else {           //add after the selected item
       index++;
       listModel.insertElementAt(itemName.getText(), index);
    }
    itemName.setText("");
    return index;
  }
  
  //This method is required by ListSelectionListener.
  public void valueChanged(ListSelectionEvent e) {
      if (e.getValueIsAdjusting() == false) {

          if (list.getSelectedIndex() == -1) {
          //No selection, disable fire button.
            btn_remove.setEnabled(false);

          } else {
          //Selection, enable the fire button.
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
