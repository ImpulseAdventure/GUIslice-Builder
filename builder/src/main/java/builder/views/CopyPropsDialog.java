/**
 *
 * The MIT License
 *
 * Copyright 2018-2023 Paul Conti
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

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import builder.Builder;
import builder.tables.CopyPropsColorRender;
import builder.tables.CopyPropsTable;
import builder.tables.CopyPropsTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CopyPropsDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  
  /** The dialog. */
  private static CopyPropsDialog dialog;
  
  private boolean bCopyProperties;
  
  private CopyPropsTableModel model;

  /**
   * Set up and show the dialog. The first Component argument determines which
   * frame the dialog depends on; it should be a component in the dialog's
   * controlling frame. The second Component argument should be null if you want
   * the dialog to come up with its left corner in the center of the screen;
   * otherwise, it should be the component on top of which the dialog should
   * appear.
   */
  public static Object[][] showDialog(JFrame f, Object[][] possibleValues) {
    dialog = new CopyPropsDialog(f, "Copy Common Properties", possibleValues);
    dialog.setVisible(true);
    return dialog.getData();
  }

  private CopyPropsDialog(JFrame f, String title, Object[][] possibleValues) {
    super(f, title, true);
    
    bCopyProperties = false;
    
    ImageIcon myIcon = new ImageIcon(Builder.class.getResource("/resources/icons/edit/copy_props.png"));
    JLabel lblIcon = new JLabel(myIcon);

    String htmlBody = "<html><p><center>Mark Properties you want to copy<br>" + 
        " and press OK button when completed.</center></p></html>"; 
    JLabel lblMessage = new JLabel(htmlBody);
    Font font = new Font("Serif", Font.PLAIN, 16);
    lblMessage.setFont(font);
    
    JButton okButton = new JButton("OK");
    okButton.addActionListener( new ActionListener() {
      @Override public void actionPerformed(ActionEvent event) {
        okAction();
      }
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        closeDialog();
      }
    });
    
    JPanel contentPanel = new JPanel();
//    content.setLayout(new GridLayout(1,0));
    
    model = new CopyPropsTableModel(possibleValues);
    CopyPropsTable table = new CopyPropsTable(model);
    table.setPreferredScrollableViewportSize(new Dimension(350, 300));
    table.setFillsViewportHeight(true);

    table.getColumnModel().getColumn(0).setPreferredWidth(50);
    table.getColumnModel().getColumn(1).setPreferredWidth(150);
    table.getColumnModel().getColumn(2).setPreferredWidth(150);
//    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Set up renderer and editor for the Favorite Color column.
    table.setDefaultRenderer(Color.class, new CopyPropsColorRender(true));
 
    // restrict selections to one cell of our table
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(true);
    
    // Force JTable to commit data to model while it is still in editing mode
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    
    // disable user column dragging
    table.getTableHeader().setReorderingAllowed(false);

    setBounds(100, 100, 350, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setLayout(new FlowLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.add(scrollPane);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    buttonPane.add(cancelButton);

    JPanel msgPane = new JPanel();
    msgPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    getContentPane().add(msgPane, BorderLayout.NORTH);
      
    GroupLayout gl_msgPane = new GroupLayout(msgPane);
    gl_msgPane.setHorizontalGroup(
      gl_msgPane.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_msgPane.createSequentialGroup()
          .addContainerGap()
          .addComponent(lblIcon)
          .addGap(28)
          .addComponent(lblMessage)
          .addContainerGap(47, Short.MAX_VALUE))
    );
    gl_msgPane.setVerticalGroup(
      gl_msgPane.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_msgPane.createSequentialGroup()
          .addGroup(gl_msgPane.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_msgPane.createSequentialGroup()
              .addGap(25)
              .addComponent(lblIcon))
            .addComponent(lblMessage))
          .addContainerGap(22, Short.MAX_VALUE))
    );
    msgPane.setLayout(gl_msgPane);
//    content.add(scrollPane);
/*   
    getContentPane().add(instruct,BorderLayout.NORTH);
    getContentPane().add(content, BorderLayout.CENTER);
    getContentPane().add(buttons, BorderLayout.SOUTH);

    setPreferredSize(new Dimension(500,500));
*/
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//    this.setResizable(false);
//    setLocationRelativeTo(f);
    Point framePt = f.getLocation();
    framePt.x = framePt.x + 275;
    framePt.y = framePt.y + 150;
    this.setLocation(framePt);
    this.pack();
  }
  
  /**
   * Ok action.
   */
  public void okAction() {
    bCopyProperties = true;
    dialog.dispose();
  }
  
  /**
   * Close dialog.
   */
  private void closeDialog(){
    dialog.dispose();
  }

  /**
   * get the checklist values
   * @return
   */
  private Object[][] getData() {
    if (!bCopyProperties)
      return null;
    return model.getCheckList();
  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
/*
  private static void createAndShowGUI(JFrame frame) {
    //Create and set up the window.
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    JPanel panel = new JPanel();
    panel.setOpaque(true); //content panes must be opaque
    frame.setContentPane(panel);
    createSample();
    //Display the window.
    frame.pack();
    
    frame.setVisible(true);
  }

  private static void printDebugData(Object[][] data) {
    if (data == null) return;
    int numRows = data.length;
    int numCols = 2;

    for (int i=0; i < numRows; i++) {
        System.out.print("    row " + i + ":");
        for (int j=0; j < numCols; j++) {
            System.out.print("  " + data[i][j]);
        }
        System.out.println();
    }
    System.out.println("--------------------------");
  }

  public static void main(String[] args) {
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("CopyPropsDialog");
        frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent we) {
            System.exit(0);
          }
        });
        createAndShowGUI(frame);
        Object[][] outputData = CopyPropsDialog.showDialog(null, sampleData);
        printDebugData(outputData);
        System.exit(0);
      }
    });
  }

  public static void createSample() {
    sampleData = new Object[19][5];

    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key","Text$1");
    initProp(PROP_ENUM, String.class, "COM-002", Boolean.FALSE,"ENUM","E_ELEM_TEXT1");
    initProp(PROP_X, Integer.class, "COM-003", Boolean.FALSE,"X",Integer.valueOf(10));
    initProp(PROP_Y, Integer.class, "COM-004", Boolean.FALSE,"Y",Integer.valueOf(20));
    initProp(PROP_WIDTH, Integer.class, "COM-005", Boolean.FALSE,"Width",Integer.valueOf(80));
    initProp(PROP_HEIGHT, Integer.class, "COM-006", Boolean.FALSE,"Height",Integer.valueOf(12));
    initProp(PROP_ELEMENTREF, String.class, "COM-019", Boolean.FALSE,"ElementRef","m_pElemText1");
    initProp(PROP_FONT, JTextField.class, "TXT-200", Boolean.FALSE,"Font","NotoSans10pt7b");
    initProp(PROP_TEXT, String.class, "TXT-201", Boolean.FALSE,"Text",DEF_TEXT);
    initProp(PROP_UTF8, Boolean.class, "TXT-203", Boolean.FALSE,"UTF-8?",DEF_UTF8);
    initProp(PROP_TEXT_SZ, Integer.class, "TXT-205", Boolean.FALSE,"External Storage Size",DEF_TEXT_SZ);
    initProp(PROP_TEXT_ALIGN, String.class, "TXT-213", Boolean.FALSE,"Text Alignment",DEF_TEXT_ALIGN);
    initProp(PROP_FILL_EN, Boolean.class, "COM-011", Boolean.FALSE,"Fill Enabled?",DEF_FILL_EN);
    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);
    initProp(PROP_USE_FLASH, Boolean.class, "COM-020", Boolean.FALSE,"Use Flash API?",DEF_USE_FLASH);
    initProp(PROP_TEXT_COLOR, Color.class, "COL-301", Boolean.FALSE,"Text Color",DEF_TEXT_COLOR);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.FALSE,"Frame Color",DEF_FRAME_COLOR);
    initProp(PROP_FILL_COLOR, Color.class, "COL-303", Boolean.FALSE,"Fill Color",DEF_FILL_COLOR);
    initProp(PROP_SELECTED_COLOR, Color.class, "COL-304", Boolean.FALSE,"Selected Color",DEF_SELECTED_COLOR);
  }
  
  public static void initProp(int row, Class<?> c, String id, Boolean readOnly, String name, Object value) {
    sampleData[row][PROP_VAL_CLASS]=c;
    sampleData[row][PROP_VAL_ID]=id;
    sampleData[row][PROP_VAL_READONLY]=readOnly;
    sampleData[row][PROP_VAL_NAME]=name;
    sampleData[row][PROP_VAL_VALUE]=value;
  }
*/  

}
