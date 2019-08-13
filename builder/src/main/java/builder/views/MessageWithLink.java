package builder.views;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import builder.Builder;

public class MessageWithLink extends JEditorPane {
  private static final long serialVersionUID = 1L;
  private ImageIcon myIcon;
 
  public MessageWithLink(String htmlBody, String htmlLink) {
    super("text/html", "<html><body style=\"" +
           getStyle() + 
           "\">" +
           htmlBody +
           "<a href=\"" +
           htmlLink +
           "\">" +
           htmlLink +
           "</a><br><br></body></html>");
    
    myIcon = new ImageIcon(Builder.class.getResource("/resources/icons/guislicebuilder.png"));
    
    addHyperlinkListener(new HyperlinkListener() {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
          try {
            Desktop.getDesktop().browse(e.getURL().toURI());
          } catch (IOException e1) {
            e1.printStackTrace();
          } catch (URISyntaxException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    setEditable(false);
    setBorder(null);
  }
  
  public void showMessage() {
    JOptionPane.showMessageDialog(null, this,
        "About",
        JOptionPane.PLAIN_MESSAGE, myIcon);
  }

  static StringBuffer getStyle() {
    // for copying style
    JLabel label = new JLabel();
//    Font font = label.getFont();
    Font font = new Font("Serif", Font.PLAIN, 16);
    Color color = label.getBackground();
    label.setFont(font);
    // create some css from the label's font
    StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
    style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
    style.append("font-size:" + font.getSize() + "pt;");
    style.append("background-color: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");");
    return style;
  }
}
