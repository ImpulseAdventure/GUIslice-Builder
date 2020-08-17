package builder.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import builder.common.FontFactory;
import builder.common.FontItem;
import builder.views.FontChooserHelper;

public class FontChooser extends JDialog {
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Closed option. */
  protected int Closed_Option = JOptionPane.CLOSED_OPTION;
  
  /** The ff. */
  FontFactory ff = null;
  
  /** helper that store font data for screen display */
  List<FontChooserHelper> helper;
  
  /** selected helper index */
  int selectedHelper;
  
  /** The current name. */
  private String currentName;
  private String selectedName;
  private FontItem currentItem;
  
  /** The fonts. */
  List<FontItem> fonts;
  List<String>   fontNames;
  
  /** The cb font. */
  JComboBox<String> cbFont;
  DefaultComboBoxModel<String> dcmFont;
  
  /** The fill color. */
  Color fillColor = Color.WHITE;
  
  /** The text color. */
  Color textColor = Color.BLACK;
  
  /** The b save dialog. */
  boolean bSaveFont;
  
  /** The boolean that tracks if the program is making changes to comboboxes. */
  boolean bProgramChange;
  
  /** The preview label. */
  protected FontLabel previewLabel;

  JComboBox<String> cbFontSize;
  DefaultComboBoxModel<String> dcmFontSize;
  
  JComboBox<String> cbFontStyle;
  DefaultComboBoxModel<String> dcmFontStyle;

  protected ButtonGroup styleGroup;
  
  public FontChooser(JFrame owner) {
    super(owner, "Font Chooser", true);
    ff = FontFactory.getInstance();
    helper = new ArrayList<FontChooserHelper>(); 
  }
  
  public String showDialog() {
    this.setLocationRelativeTo(null);
    this.setVisible(true);
    return currentName;
  }
  
  public void setFontName(String fontName) {
    fontNames = new ArrayList<String>();
    bSaveFont = false;
    currentName = fontName;
    initUI();
    updatePreview();
  }
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void initUI() {
    bProgramChange = false;
    getContentPane().setLayout(
        new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    ActionListener fontListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateFont();
      }
    };

    ActionListener previewListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!bProgramChange) {
          updatePreview();
        }
      }
    };
    
    // scan the fonts list and process it into something we can display on screen
    scanFontLists();
    FontChooserHelper h = helper.get(selectedHelper);    
    dcmFont = new DefaultComboBoxModel<String>();
    cbFont = new JComboBox<String>();
    cbFont.setModel(dcmFont);
    for (String s : fontNames) {
      cbFont.addItem(s);
    }
    cbFont.setSelectedItem(h.getFontName());
    cbFont.addActionListener(fontListener);
    cbFont.setToolTipText("Click to Select Font");

    JPanel p = new JPanel(new GridLayout(2, 3, 10, 2));
    JLabel lblFont = new JLabel("Font:");
    p.add(lblFont);
    JLabel lblFontSize = new JLabel("Font Size:");
    p.add(lblFontSize);
    JLabel lblFontStyle = new JLabel("Font Style:");
    p.add(lblFontStyle);

    p.add(cbFont);

    dcmFontSize = new DefaultComboBoxModel();
    cbFontSize = new JComboBox<String>();
    cbFontSize.setModel(dcmFontSize);
    for (String sz : h.getFontSize()) {
      cbFontSize.addItem(sz);
    }
    cbFontSize.setSelectedItem(currentItem.getLogicalSize());
    cbFontSize.setToolTipText("Click to Select Font size");
    cbFontSize.addActionListener(previewListener);
    cbFontSize.setToolTipText("Click to Select Font Size");
    p.add(cbFontSize);
    getContentPane().add(p);

    dcmFontStyle = new DefaultComboBoxModel();
    cbFontStyle = new JComboBox<String>();
    cbFontStyle.setModel(dcmFontStyle);
    for (String style : h.getFontStyle()) {
      cbFontStyle.addItem(style);
    }
    cbFontStyle.setSelectedItem(currentItem.getLogicalStyle());
    cbFontStyle.setToolTipText("Click to Select Font Style");
    cbFontStyle.addActionListener(previewListener);
    cbFontStyle.setToolTipText("Click to Select Font Style");
    p.add(cbFontStyle);
    getContentPane().add(p);

    getContentPane().add(Box.createVerticalStrut(5));
    p = new JPanel(new BorderLayout());
    p.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
    previewLabel = new FontLabel("Preview Font");

    p.add(previewLabel, BorderLayout.CENTER);
    getContentPane().add(p);

    p = new JPanel(new FlowLayout());
    JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
    JButton btOK = new JButton("OK");
    btOK.setToolTipText("Save and exit");
    getRootPane().setDefaultButton(btOK);
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        currentName = selectedName;
        setVisible(false);
        dispose();
      }
    };
    btOK.addActionListener(actionListener);
    p1.add(btOK);

    JButton btCancel = new JButton("Cancel");
    btCancel.setToolTipText("Exit without save");
    actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    };
    btCancel.addActionListener(actionListener);
    p1.add(btCancel);
    p.add(p1);
    getContentPane().add(p);

    pack();
    setResizable(false);
    this.setVisible(false);
  }

  public int getOption() {
    return Closed_Option;
  }

  protected void updateFont() {
    bProgramChange = true;
    selectedHelper = cbFont.getSelectedIndex();
    
    FontChooserHelper h = helper.get(selectedHelper);
    dcmFontSize.removeAllElements();
    cbFontSize.setModel(dcmFontSize);
    for (String sz : h.getFontSize()) {
      cbFontSize.addItem(sz);
    }
    
    dcmFontStyle.removeAllElements();
    cbFontStyle.setModel(dcmFontStyle);
    for (String style : h.getFontStyle()) {
      cbFontStyle.addItem(style);
    }

    int i = cbFontSize.getSelectedIndex();
    String sSize = cbFontSize.getItemAt(i);
    i = cbFontStyle.getSelectedIndex();
    String sStyle = cbFontStyle.getItemAt(i);
    currentItem = ff.getFontItem(h.getFontName(),sSize, sStyle);
    
    bProgramChange = false;
    updatePreview();
  }
  
  protected void updatePreview() {
    FontChooserHelper h = helper.get(selectedHelper);
    int i = cbFontSize.getSelectedIndex();
    String sSize = cbFontSize.getItemAt(i);
    i = cbFontStyle.getSelectedIndex();
    String sStyle = cbFontStyle.getItemAt(i);
    FontItem item = ff.getFontItem(h.getFontName(),sSize, sStyle);
    if (item == null) return;
    previewLabel.setFont(item.getFont());
    selectedName = item.getDisplayName();
    repaint();
  }

  protected void scanFontLists() {
    String name = "";
    fonts = ff.getFontList();
    FontChooserHelper nextHelper = null;
    int j=-1;
    currentItem = null;
    for (int i=0; i<fonts.size(); i++) {
      FontItem fontItem = fonts.get(i);
      if (!name.equals(fontItem.getName())) {
        if (nextHelper != null) {
          helper.add(nextHelper);
        }
        name = fontItem.getName();
        nextHelper = new FontChooserHelper();
        nextHelper.setFontName(name);
        nextHelper.addFontSize(fontItem.getLogicalSize());
        nextHelper.addFontStyle(fontItem.getLogicalStyle());
        fontNames.add(name);
        j++;
      } else {
        nextHelper.addFontSize(fontItem.getLogicalSize());
        nextHelper.addFontStyle(fontItem.getLogicalStyle());
      }
      if (currentName.equals(fontItem.getDisplayName())) {
        selectedHelper = j;
        currentItem = fontItem;
      }
    }
    helper.add(nextHelper);
    if (currentItem == null) {
      currentItem = fonts.get(0);
      selectedHelper = 0;
    }
  }
  
  class FontLabel extends JLabel {
    private static final long serialVersionUID = 1L;

    public FontLabel(String text) {
      super(text, JLabel.CENTER);
      setBackground(fillColor);
      setForeground(textColor);
      setOpaque(true);
      setBorder(new LineBorder(Color.black));
      setPreferredSize(new Dimension(120, 40));
    }
  }
}


