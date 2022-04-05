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
 package builder.fonts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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

import builder.Builder;

@SuppressWarnings("unused")
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
  protected JLabel previewLabel;
  
  /** The preview panel */
  protected JPanel previewPanel;

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
    JLabel lblFontStyle = new JLabel("Font Style:");
    p.add(lblFontStyle);
    JLabel lblFontSize = new JLabel("Font Size:");
    p.add(lblFontSize);

    p.add(cbFont);

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

    getContentPane().add(Box.createVerticalStrut(5));
    previewPanel = new JPanel(new GridBagLayout()) {
      private static final long serialVersionUID = 1L;

      @Override
      public Dimension getPreferredSize() {
          return new Dimension(500, 100);
      };
    };    
    previewLabel = new JLabel();

    previewPanel.add(previewLabel);
    getContentPane().add(previewPanel);

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
//    Builder.logger.debug("item: "+item.toString());
    Dimension ppDim = previewPanel.getSize();
    Rectangle r = new Rectangle(ppDim);
    FontTFT font = item.getFont();
    if (font == null) {
      Builder.logger.error("font failure: "+item.toString());
      return;
    }
    BufferedImage img = ff.drawPreviewImage(FontTFT.ALIGN_TOP_CENTER, r, "Preview Font", 
        font, textColor, fillColor, 5);
    if (img != null) {
      previewLabel.setEnabled(true);
      previewLabel.setIcon(new ImageIcon(img));
    } else {
      previewLabel.setEnabled(false);
    }
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
      if (!name.equals(fontItem.getFamilyName())) {
        if (nextHelper != null) {
          helper.add(nextHelper);
        }
        name = fontItem.getFamilyName();
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
      if (currentName != null && currentName.equals(fontItem.getDisplayName())) {
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


