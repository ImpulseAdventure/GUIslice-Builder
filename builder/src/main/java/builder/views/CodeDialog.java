package builder.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.net.URL;

//import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
//import javax.swing.JTextArea;
//import javax.swing.JScrollPane;
//import javax.swing.text.StyleConstants;
import java.awt.Dimension;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import hu.csekme.RibbonMenu.Util;
import javax.swing.ScrollPaneConstants;

public class CodeDialog extends JDialog implements ActionListener {
  private static final long serialVersionUID = 1L;

  private static CodeDialog dialog;
  
  private final JPanel contentPanel = new JPanel();
  private RTextScrollPane scrollPane;
  private RSyntaxTextArea textArea;
  
  private String[] inData;

  private static final String okString = "OK";
  private static final String cancelString = "cancel";
  
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
    dialog = new CodeDialog(frame, locationComp, labelText, title, possibleValues);
    dialog.setVisible(true);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    return dialog.getData();
  }

  /**
   * Create the dialog.
   */
  private CodeDialog(Frame frame, Component locationComp, String labelText, 
      String title, Object[] data) {
    super(frame, title, true);
    getContentPane().setPreferredSize(new Dimension(450, 300));
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    
    textArea = new RSyntaxTextArea(25,70);
    textArea.setRows(25);
    textArea.setColumns(70);
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
    inData = (String[]) data;
    setData(inData);
    textArea.setTabSize(2);
    textArea.setCaretPosition(0);
    textArea.setCaretPosition(0);
    textArea.requestFocusInWindow();
    textArea.setMarkOccurrences(true);
    textArea.setCodeFoldingEnabled(true);
    textArea.setClearWhitespaceLinesEnabled(false);

    contentPanel.setLayout(new BorderLayout(0, 0));
    
    scrollPane = new RTextScrollPane(textArea, true);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    Gutter gutter = scrollPane.getGutter();
    gutter.setBookmarkingEnabled(false);
//    URL url = getClass().getResource("bookmark.png");
//    gutter.setBookmarkIcon(new ImageIcon(url));
    contentPanel.add(scrollPane);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    {
      JButton okButton = new JButton("OK");
      okButton.setToolTipText("Press 'OK' when list is completed.");
      okButton.setActionCommand(okString);
      okButton.addActionListener(this);
      buttonPane.add(okButton);
      getRootPane().setDefaultButton(okButton);
    }
    {
      JButton cancelButton = new JButton("Cancel");
      cancelButton.setToolTipText("Press 'Cancel' to abort.");
      cancelButton.setActionCommand(cancelString);
      cancelButton.addActionListener(this);
      buttonPane.add(cancelButton);
    }
    
    JPanel ToolPane = new JPanel();
    getContentPane().add(ToolPane, BorderLayout.NORTH);
    
    JButton btnCut = new JButton("Cut");
    btnCut.setAction(RTextArea.getAction(RTextArea.CUT_ACTION));
    ToolPane.add(btnCut);
    
    JButton btnCopy = new JButton("Copy");
    btnCopy.setAction(RTextArea.getAction(RTextArea.COPY_ACTION));
    ToolPane.add(btnCopy);

    JButton btnPaste = new JButton("Paste");
    btnPaste.setAction(RTextArea.getAction(RTextArea.PASTE_ACTION));
    ToolPane.add(btnPaste);
    
    JButton btnDel = new JButton("Delete");
    ToolPane.add(btnDel);
    btnDel.setAction(RTextArea.getAction(RTextArea.DELETE_ACTION));
    
    JButton btnUndo = new JButton("Undo");
    btnUndo.setAction(RTextArea.getAction(RTextArea.UNDO_ACTION));
    ToolPane.add(btnUndo);
    
    JButton btnRedo = new JButton("Redo");
    btnRedo.setAction(RTextArea.getAction(RTextArea.REDO_ACTION));
    ToolPane.add(btnRedo);
    
    pack();
    setLocationRelativeTo(locationComp);
  }

  public String[] getData() {
    String txt = textArea.getText();
    String [] data = txt.split("\n");
    return data;    
  }
  
  public void setData(String[] data) {
    if (data != null) {
      String testStr = (String)data[0];
      if (!testStr.isEmpty()) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<data.length; i++) {
          sb.append((String) data[i]);
          sb.append("\n");
        }
        textArea.setText(sb.toString());
      }
    }
  }
  
  // Handle clicks on the Add and Cancel buttons.
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    switch (command) {
    case okString:
      CodeDialog.dialog.setVisible(false);
      CodeDialog.dialog.dispose();
      break;
    case cancelString:
      setData(inData);
      CodeDialog.dialog.setVisible(false);
      CodeDialog.dialog.dispose();
      break;
   default:
     break;
  }
}
}
