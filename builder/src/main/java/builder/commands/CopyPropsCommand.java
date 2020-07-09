/**
 *
 * The MIT License
 *
 * Copyright 2018-2020 Paul Conti
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
package builder.commands;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import builder.Builder;
import builder.controller.Controller;
import builder.mementos.CopyPropsMemento;
import builder.models.WidgetModel;
import builder.views.CopyPropsDialog;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class CopyPropsCommand will
 * align the selected widgets to the bottom most widget.
 * 
 * @author Paul Conti
 * 
 */
public class CopyPropsCommand extends Command {
  
  /** The controller. */
  private Controller controller;
  
  /** The page that holds the selected widgets. */
  private PagePane page;
  
  /** The top most frame */
  JFrame frame;
  
  /** this copy properties instance so we can callback to controller */
  CopyPropsCommand instance;
  
  private WidgetModel sourceModel;
  
  /** The group list contains the models of all the selected widgets 
   * that will copied to the clipboard.
   */
  private List<WidgetModel> groupList = new ArrayList<WidgetModel>();
  
  private Object checklistData[][];
  
  /**
   * Instantiates a new align bottom command.
   *
   * @param page
   *          the <code>page</code> is the object that holds the widgets
   */
  public CopyPropsCommand(JFrame frame, Controller controller) {
    this.frame = frame;
    this.controller = controller;
    instance = this;
  }
  
  /**
   * copy will setup the part one of the command 
   * It checks that the user has selected just one source UI Element
   * to copy from and will then notify the user to select
   * the TargetUI Elements to modify.
   * These checked properties will be saved for later execution
   *
   * @return <code>true</code>, if successful
   */
  public boolean copy(PagePane page) {
    List<Widget> list = page.getSelectedList();
    if (list.size() != 1) {
      JOptionPane.showMessageDialog(null, 
          "You must first select a single UI element to use for copy properties.",
          "Warning",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    // save our source model for it's property values
    sourceModel = list.get(0).getModel();

    // post checklist dialog
    checklistData = CopyPropsDialog.showDialog(frame, sourceModel.getData());
    if (checklistData == null) {
      page.selectNone();
      page.refreshView();
      Builder.postStatusMsg("Cancel copy properties from " + sourceModel.getEnum());
      return false;
    }
    page.selectNone();
    page.refreshView();
    this.page = page;
    postSelectMsg();
    return true;  // success, so far
  }

  /**
   * copy2 will be invoked from the controller if the user
   * clicked the "Copy" button in the post selected message dialog.
   * This routine will make sure one or more target UI Elements have 
   * been selected. 
   * by the execute() function. It will also create the required 
   * Memento object for undo/redo.
   *
   * @return <code>true</code>, if successful
   */
  public boolean copy2(PagePane page) {
    this.page = page;
    List<Widget> list = page.getSelectedList();
    if (list.size() == 0) {
      Builder.postStatusMsg("No elements selected for copy properties from " + sourceModel.getEnum());
      return false;
    }
    // add to groupList selected widgets
    for (Widget w : list) {
      WidgetModel m = w.getModel();
      groupList.add(m);
    }
    
    // create our memento object for undo/redo
    memento = new CopyPropsMemento(page, checklistData); 
    return true;
  }
  
  /**
   * execute - will actually run the command
   * This will copy our widgets to the clipboard
   * 
   * @see builder.commands.Command#execute()
   */
  @Override
  public void execute() {
    for(WidgetModel m : groupList) {
      m.copyProperties(checklistData);
    }
    // turn off selections
    page.selectNone();
    page.refreshView();
    Builder.postStatusMsg("Successfully copied properties from " + sourceModel.getEnum());
  }

  private void postSelectMsg() {
    JDialog dialog = new JDialog(frame, "Select UI Elements to Modify");
    JPanel contentPanel = new JPanel();
    
    String htmlBody1 = "<html><p><center>Now please select the " + 
        "UI ElementsYou wish to modify" +
        "<br>then Click Copy button to complete the transaction.</center></p></html>"; 
    JLabel lblMessage1 = new JLabel(htmlBody1);
    Font font1 = new Font("Serif", Font.PLAIN, 16);
    lblMessage1.setFont(font1);
    String htmlBody2 = "<html><p><center>The Rectangular Selection Tool" + 
        " has been enabled for your convenience;<br>" +
        "alternately, control+left click can be used to select multiple elements.</center></p></html>";
    JLabel lblMessage2 = new JLabel(htmlBody2);
    Font font2 = new Font("Serif", Font.PLAIN, 12);
    lblMessage2.setFont(font2);

    ImageIcon myIcon = new ImageIcon(Builder.class.getResource("/resources/icons/edit/copy_props.png"));
    JLabel lblIcon = new JLabel(myIcon);
    JButton okButton = new JButton("Copy");
    JButton cancelButton = new JButton("Cancel");
  
    dialog.setBounds(100, 100, 450, 240);
    dialog.getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
   
    dialog.getContentPane().add(contentPanel, BorderLayout.SOUTH);
    GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
    gl_contentPanel.setHorizontalGroup(
      gl_contentPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_contentPanel.createSequentialGroup()
          .addContainerGap(11, Short.MAX_VALUE)
          .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
            .addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
              .addComponent(lblIcon)
              .addGap(18)
              .addComponent(lblMessage1)
              .addContainerGap())
            .addGroup(gl_contentPanel.createSequentialGroup()
              .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
                .addComponent(lblMessage2, GroupLayout.PREFERRED_SIZE, 356, GroupLayout.PREFERRED_SIZE)
                .addGroup(gl_contentPanel.createSequentialGroup()
                  .addComponent(okButton)
                  .addPreferredGap(ComponentPlacement.UNRELATED)
                  .addComponent(cancelButton)))
              .addGap(23))))
    );
    gl_contentPanel.setVerticalGroup(
      gl_contentPanel.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_contentPanel.createSequentialGroup()
          .addContainerGap(10, Short.MAX_VALUE)
          .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
            .addGroup(gl_contentPanel.createSequentialGroup()
              .addComponent(lblIcon)
              .addGap(20))
            .addGroup(gl_contentPanel.createSequentialGroup()
              .addComponent(lblMessage1)
              .addPreferredGap(ComponentPlacement.UNRELATED)))
          .addComponent(lblMessage2, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
          .addGap(20)
          .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
            .addComponent(okButton)
            .addComponent(cancelButton))
          .addContainerGap())
    );
    contentPanel.setLayout(gl_contentPanel);

    dialog.setModal(false); // this says not to block background components
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setContentPane(contentPanel);

    okButton.addActionListener( new ActionListener() {
      @Override public void actionPerformed(ActionEvent event) {
        controller.copyProps2(instance);
        dialog.dispose();
      }
    });
    cancelButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        dialog.dispose();
        page.rectangularSelection(false);
        Builder.postStatusMsg("Cancel copy properties from " + sourceModel.getEnum());
      }
    });
    
    dialog.pack();
    Point framePt = frame.getLocation();
    int w = frame.getWidth();
    w = w - 450;
    framePt.x = framePt.x + w;
    dialog.setLocation(framePt);
    dialog.setVisible(true);
  }
  
  /**
   * toString - converts command to string for debugging
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String myEnums = "";
    WidgetModel m = null;
    for(int i=0; i<groupList.size(); i++) {
      m = groupList.get(i);
      if (i > 0) myEnums = myEnums + ",";
      myEnums = myEnums + m.getEnum();
    }
    return String.format("Copy Properties: from:%s to:%s",sourceModel.getEnum(),myEnums);
  }

}
