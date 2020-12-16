package builder.fonts;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import builder.Builder;
import builder.controller.Controller;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.Scrollable;

import java.awt.FlowLayout;

//import builder.Builder;

public class CharacterMap extends JDialog {
  private static final long serialVersionUID = 1L;

  /** The Constant dashed. */
  final static public  BasicStroke dashed = new BasicStroke(3.0f, 
      BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5.0f, new float[]{5.0f}, 0);
  
  private static final int PANEL_WIDTH = 700;
  private int MAXCODEPOINT = 0;
  private int SCROLL_TOP_ROW = 0;
  private int SCROLL_MAX_ROW = 0;
  private int GRID_HEIGHT = 0;
  private static final int NUM_ROWS = 13;
  private static final int NUM_COLS = 20;
  private static final int BOX_WIDTH = 35;
  public static final int BOX_HEIGHT = 35;
  private static final int SCALESZ = 60;
  private static final int MARGIN = 0;
  
  private Color colLineTxt;
  private Color colBg;
  
  private JPanel contentPanel = new JPanel();
  private JScrollPane scrollPane;
  private DrawPanel characterPane;
  private JPanel selectPane;
  private JLabel lbPreview;
  private BufferedImage selectImage=null;
  
  private JTextField txtFontFamily;
  private JButton selectBtn;
  private JButton copyBtn;
  private TextTFT txtCharacters;
  private JTextField txtCode;
  
  protected int   nScrollPos=0;
  private boolean bTrueTypeFont;

  /** The ff. */
  FontFactory ff = null;
  
  /** The current name. */
  private String currentName;

  private FontTFT font=null;
  private boolean bValidFont = false;
  
  private FontTFT inFont;
  private int nMaxCols = -1;
  private String text;
  private String textAnswer;
  private CharacterHelper currentCharacter = null;
  private List<CharacterHelper> characterList;
  private JButton btnCancel;

  public String showDialog() {
    this.setLocationRelativeTo(null);
    if (!bValidFont) return "";
    this.setVisible(true);
    return textAnswer;
  }
  
  /**
   * Create the dialog.
   */
  public CharacterMap(JFrame owner, String title, boolean modal) {
    super(owner, "Font Chooser", modal);
  }
  
  public void setFontTFT(FontTFT inFont, int nMaxCols) {
    this.inFont = inFont;
    this.nMaxCols = nMaxCols;
    initUI();
  }
  
  public void initUI() {
    colLineTxt = SystemColor.textText;
    colBg = SystemColor.info;

    ff = FontFactory.getInstance();
    characterList = new ArrayList<CharacterHelper>();
    text = new String("");
    textAnswer = new String("");

    getContentPane().setPreferredSize(new Dimension(700, 535));
    getContentPane().setSize(new Dimension(750, 635));
    setBounds(100, 100, 750, 635);

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    currentName = inFont.getFamilyName();
    {
      JPanel fontPane = new JPanel();
      contentPanel.add(fontPane, BorderLayout.NORTH);
      txtFontFamily = new JTextField(currentName);
      txtFontFamily.setEditable(false);
      
      JLabel lbFontLabel = new JLabel("Font:");
      GroupLayout gl_fontPane = new GroupLayout(fontPane);
      gl_fontPane.setHorizontalGroup(
        gl_fontPane.createParallelGroup(Alignment.LEADING)
          .addGroup(gl_fontPane.createSequentialGroup()
            .addContainerGap()
            .addComponent(lbFontLabel)
            .addGap(37)
            .addComponent(txtFontFamily, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
            .addContainerGap(89, Short.MAX_VALUE))
      );
      gl_fontPane.setVerticalGroup(
        gl_fontPane.createParallelGroup(Alignment.LEADING)
          .addGroup(gl_fontPane.createSequentialGroup()
            .addGap(5)
            .addGroup(gl_fontPane.createParallelGroup(Alignment.BASELINE)
              .addComponent(txtFontFamily, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(lbFontLabel))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );
      fontPane.setLayout(gl_fontPane);
    }
    
    characterPane = new DrawPanel();

    ActionListener selectListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (currentCharacter != null) {
          text = txtCharacters.getText() + currentCharacter.ch;
          if (nMaxCols != -1 && text.length() > nMaxCols)
            text = text.substring(1);
          txtCharacters.setText(text);
          txtCharacters.repaint();
        }
      }
    };

    ActionListener copyListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textAnswer = text;
        setVisible(false);
        dispose();
      }
    };

    characterPane.addMouseListener(new MouseHandler());
    scrollPane = new JScrollPane(characterPane,
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    contentPanel.add(scrollPane, BorderLayout.CENTER);

    JPanel btnPane = new JPanel();
    contentPanel.add(btnPane, BorderLayout.SOUTH);
    
    selectPane = new JPanel();
    selectPane.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
    selectPane.setBackground(colBg);
    selectPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    
    lbPreview = new JLabel();
    lbPreview.setPreferredSize(new Dimension(60, 60));
    selectPane.add(lbPreview);
    
    txtCharacters = new TextTFT();
    txtCharacters.setColumns(10);
    txtCharacters.setEditable(false);
    selectBtn = new JButton("Select");
    selectBtn.setActionCommand("SELECT");
    selectBtn.addActionListener(selectListener);
    copyBtn = new JButton("Copy");
    copyBtn.addActionListener(copyListener);
    
    txtCode = new JTextField();
    txtCode.setEditable(false);
    txtCode.setColumns(10);
    txtCode.setBackground(Color.WHITE);
    txtCode.setForeground(Color.BLACK);
    JLabel lbCopy = new JLabel("Characters to copy");
    
    JLabel lbCode = new JLabel("Code:");
    
    btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textAnswer = "";
        setVisible(false);
        dispose();
      }
    });
    
    GroupLayout gl_btnPane = new GroupLayout(btnPane);
    gl_btnPane.setHorizontalGroup(
      gl_btnPane.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_btnPane.createSequentialGroup()
          .addContainerGap()
          .addComponent(selectPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addGap(44)
          .addGroup(gl_btnPane.createParallelGroup(Alignment.LEADING)
            .addComponent(lbCopy)
            .addGroup(gl_btnPane.createSequentialGroup()
              .addGroup(gl_btnPane.createParallelGroup(Alignment.LEADING, false)
                .addGroup(gl_btnPane.createSequentialGroup()
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(txtCharacters, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(selectBtn)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(copyBtn)
                  .addGap(27))
                .addGroup(gl_btnPane.createSequentialGroup()
                  .addComponent(lbCode)
                  .addPreferredGap(ComponentPlacement.UNRELATED)
                  .addComponent(txtCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                  .addGap(213)))
              .addGap(116)
              .addComponent(btnCancel)))
          .addGap(62))
    );
    gl_btnPane.setVerticalGroup(
      gl_btnPane.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_btnPane.createSequentialGroup()
          .addContainerGap(13, Short.MAX_VALUE)
          .addGroup(gl_btnPane.createParallelGroup(Alignment.TRAILING)
            .addGroup(gl_btnPane.createSequentialGroup()
              .addGroup(gl_btnPane.createParallelGroup(Alignment.BASELINE)
                .addComponent(txtCharacters, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(selectBtn)
                .addComponent(copyBtn)
                .addComponent(btnCancel))
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(lbCopy)
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addGroup(gl_btnPane.createParallelGroup(Alignment.BASELINE)
                .addComponent(lbCode)
                .addComponent(txtCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
            .addComponent(selectPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap())
    );
    btnPane.setLayout(gl_btnPane);
    updateFont();
  }
  
  private void updateFont() {
    currentCharacter = null;
    int fontSz = 12;
    if (Controller.getTargetPlatform().equals("linux")) {
      fontSz = 20;
    }
    font = ff.getFontbySizeStyle(currentName, fontSz, FontItem.PLAIN);
    if (font == null)
      font = ff.getFontbySizeStyle(currentName, fontSz, FontItem.BOLD);
    if (font == null) {
      JOptionPane.showMessageDialog(null, 
          "Sorry, CharacterMap can only display Font Families with fonts size "
          + Integer.valueOf(fontSz)
          + " or less", 
          "ERROR",
          JOptionPane.ERROR_MESSAGE);
      bValidFont = false;
      return;
    }
    if (font instanceof FontTtf) {
      bTrueTypeFont = true;
      characterPane.setPreferredSize(new Dimension(700, 7500));
    } else {
      bTrueTypeFont = false;
      characterPane.setPreferredSize(new Dimension(700, 490));
    }
    
    bValidFont = true;

    txtCharacters.setFontTFT(ff, font);
    characterList.clear();
    MAXCODEPOINT = 255;
    if (bTrueTypeFont) MAXCODEPOINT = Short.MAX_VALUE;
    Builder.logger.debug(String.format("MAXCODEPOINT: %d",MAXCODEPOINT));
    String testCh = null;
    Point p;
    int x = MARGIN;
    int y = 0;
    int row = 0;
    int col = 0;
//    int code_start = 32;
    int code_start = 0;
//    if (font.getDisplayName().startsWith("BuiltIn"))
//      code_start = 0;
    for (int i=code_start; i<=MAXCODEPOINT; i++) {
      if (col % NUM_COLS == 0 && col > NUM_COLS-1) {
        x = MARGIN;
        y += BOX_HEIGHT;
        row++; 
        col = 0;
      }
      // as we scroll we may already have this char in out list
      testCh =  String.format("%c", i);
      p = new Point(x, y);
      if (font.canDisplay(i)) {
//        String sCode = String.format("x%04x", i);
        characterList.add(new CharacterHelper(p,testCh,i,true));
        x += BOX_WIDTH;
        col++;
      }
    }
    SCROLL_MAX_ROW = row;
    GRID_HEIGHT = y + BOX_HEIGHT;
    nScrollPos = 0;
    scrollPane.getViewport().setViewPosition(new Point(0,0));
    revalidate();
    repaint();
  }

  protected void clearCurChar() {
    currentCharacter = null;
    txtCode.setText(null);
    lbPreview.setIcon(null);
  }
  
  /**
   * Scale image.
   */
  private void scaleImage(CharacterHelper h) {
      Rectangle r = new Rectangle(0,0,BOX_WIDTH,BOX_HEIGHT);
      BufferedImage charImage = ff.drawTextImage(FontTFT.ALIGN_CENTER, r, h.ch, font, colLineTxt, colLineTxt, 0);
      if (charImage == null) return;
      int width = charImage.getWidth(this);
      int height = charImage.getHeight(this);
      double ratio = 1.0;
      if (width >= height) {
          ratio = (double)(SCALESZ-5) / width;
          width = SCALESZ-5;
          height = (int)(height * ratio);
      }
      else {
          if (getHeight() > 150) {
              ratio = (double)(SCALESZ-5) / height;
              height = SCALESZ-5;
              width = (int)(width * ratio);
          }
          else {
              ratio = (double)getHeight() / height;
              height = getHeight();
              width = (int)(width * ratio);
          }
      }
      // To get a scaled instance of a buffered image we need
      // to create a new image, a BufferedImage with the TookitImage.
      // width and height are for the scaled image
      Image toolkitImage = charImage.getScaledInstance(width, height, 
          Image.SCALE_SMOOTH);

      // now replace the original image with a new scaled image
      selectImage = new BufferedImage(SCALESZ, SCALESZ, charImage.getType());
      Graphics2D g2d = selectImage.createGraphics();
      g2d.drawImage(toolkitImage, 0, 0, null);
      g2d.dispose();
      lbPreview.setIcon(new ImageIcon(selectImage));
  }
  
  public boolean findChar(Point p) {
//    Builder.logger.debug(String.format("findChar point=%s",p.toString()));
    for (CharacterHelper h : characterList) {
      if (h.contains(p)) {
        currentCharacter = h;
        String sCode = String.format("0x%02x", currentCharacter.nCode);
        txtCode.setText(sCode);
        scaleImage(currentCharacter);
//        Builder.logger.debug(String.format("***findChar MATCH->%s",h.toString()));
        return true;
      }
    }
    return false;
  }
  
  /**
   * Draw selection rect.
   *
   * @param g2d
   *          the graphics object
   * @param b
   *          the bounded <code>Rectangle</code> object
   */
  public void drawSelRect(Graphics2D g2d, Rectangle b) {
    Stroke defaultStroke = g2d.getStroke();
    g2d.setColor(Color.BLUE);
//    g2d.setStroke(dashed);
    g2d.setStroke(new BasicStroke(6f));
    g2d.drawRect(b.x-2, b.y-2, b.width+4, b.height+4);
    g2d.setStroke(defaultStroke);  
  }
  
  /**
   * The Class MouseHandler for characterPane
   */
  private class MouseHandler extends MouseAdapter {

    /**
     * mouseClicked.
     *
     * @param e
     *          the e
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
      Point mousePt = e.getPoint();
      if (findChar(mousePt)) {
        repaint();
      }
    }
  }
  
  class DrawPanel extends JPanel implements Scrollable {
    private static final long serialVersionUID = 1L;

    public DrawPanel() {
    }
    
    /**
     * paintComponent.
     *
     * @param g
     *          the g
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (GRID_HEIGHT == 0) return;
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(MARGIN, 0, PANEL_WIDTH, GRID_HEIGHT);
      drawCoordinates(g2d, PANEL_WIDTH+MARGIN, GRID_HEIGHT);
      int idx = 0;
      g2d.setColor(Color.BLACK);
//      System.out.println(String.format("row=%d nMaxRow=%d",SCROLL_TOP_ROW,nMaxRow));
//      int yOffset = BOX_HEIGHT/2+2;
      for (int row=0; row<=SCROLL_MAX_ROW; row++) {
        for (int col=0; col<NUM_COLS; col++) {
          idx = row * NUM_COLS + col;
          if (idx < characterList.size()) {
            CharacterHelper h = characterList.get(idx);
//              System.out.println(String.format("idx=%d [%d][%d] code %d char[%s]",idx,h.r.x,h.r.y,h.nCode,h.ch));
            if (h.bValid) {
                ff.drawChar(g2d, FontTFT.ALIGN_CENTER, h.r, h.ch, font, txtCode.getForeground(), txtCode.getForeground(), 0);
//              g2d.setFont(fontCode);
//              g2d.drawString(h.sCode,h.r.x,h.r.y+BOX_HEIGHT);
            }
          }
        }
      }
      if (currentCharacter != null) 
        drawSelRect(g2d, currentCharacter.r);

      g2d.dispose();
    }
    
    /**
     * Draw grid coordinates.
     *
     * @param g2d
     *          the graphics object
     * @param w
     *          the width of simulated TFT screen
     * @param h
     *          the height of simulated TFT screen
     */
    private void drawCoordinates(Graphics2D g2d, int w, int h) {
      int x, y, dx, dy, dw, dh;
      // draw X axis
      dy = 0;
      dh = h;
      g2d.setColor(Color.GRAY);
      for (x=MARGIN; x<=(w-BOX_WIDTH)+MARGIN; x+=BOX_WIDTH) {
        dx = x;
        g2d.drawLine(dx, dy, dx, dh);
      }
      // draw Y axis  
      dx = MARGIN;
      dw = w;
      for (y=0; y<h; y+=BOX_HEIGHT) {
        dy = y;
        g2d.drawLine(dx, dy, dw, dy);
      }
      g2d.drawRect(MARGIN, 0, w-MARGIN, h);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      //Get the current position.
      int currentPosition = visibleRect.y;

      //Return the number of pixels between currentPosition
      //and the nearest tick mark in the indicated direction.
      if (direction < 0) {
        int newPosition = currentPosition - (currentPosition / BOX_HEIGHT) * BOX_HEIGHT;
          nScrollPos = (newPosition == 0) ? BOX_HEIGHT : newPosition;
          if (SCROLL_TOP_ROW > 0) {
            SCROLL_TOP_ROW--;
          } else {
            nScrollPos = 0;
          }
      } else {
        if (SCROLL_TOP_ROW+NUM_ROWS < SCROLL_MAX_ROW-1) {
          nScrollPos = ((currentPosition / BOX_HEIGHT) + 1) * BOX_HEIGHT - currentPosition;
          SCROLL_TOP_ROW++;
        } else {
          nScrollPos = 0;
        }

      }
//      Builder.logger.debug(String.format("SCROLL_TOP_ROW=%d nScrollPos=%d",SCROLL_TOP_ROW,nScrollPos));
      clearCurChar();
      return nScrollPos;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
//    System.out.println(String.format("getScrollableBlockIncrement SCROLL_TOP_ROW=%d nScrollPos=%d",SCROLL_TOP_ROW,nScrollPos));
      //Get the current position.
      int currentPosition = visibleRect.y;
//    System.out.println("BlockIncrement: y=" + currentPosition);

      //Return the number of pixels between currentPosition
      //and the nearest tick mark in the indicated direction.
      if (direction < 0) {
        int newPosition = currentPosition - (currentPosition / BOX_HEIGHT) * BOX_HEIGHT;
          nScrollPos = (newPosition == 0) ? BOX_HEIGHT : newPosition;
          if (SCROLL_TOP_ROW > 0) {
            SCROLL_TOP_ROW--;
          } else {
            nScrollPos = 0;
          }
      } else {
        if (SCROLL_TOP_ROW+NUM_ROWS < SCROLL_MAX_ROW-1) {
          nScrollPos = ((currentPosition / BOX_HEIGHT) + 1) * BOX_HEIGHT - currentPosition;
          SCROLL_TOP_ROW++;
        } else {
          nScrollPos = 0;
        }
  
      }
//    System.out.println(String.format("SCROLL_TOP_ROW=%d nScrollPos=%d",SCROLL_TOP_ROW,nScrollPos));
      clearCurChar();
      return nScrollPos;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
      return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }

  }

  class CharacterHelper {
    public Point p;
    public String ch;
    public Rectangle r;
    public int nCode;
    public boolean bValid;
    
    public CharacterHelper(Point p, String ch, int nCode, boolean bValid) {
      this.ch = ch;
      this.p = p;
      this.r = new Rectangle(p.x, p.y, BOX_WIDTH, BOX_HEIGHT);
      this.nCode = nCode;
      this.bValid = bValid;
    }
    
    /**
     * Return true if this node contains p.
     *
     * @param p
     *          the <code>Point</code> object
     * @return <code>true</code>, if successful
     */
    public boolean contains(Point p) {

      return r.contains(p);
    }

    /**
     * toString
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return String.format("CharacterHelper: %s code: %d rect: %s", ch, nCode, r.toString());
    }
  }
}
