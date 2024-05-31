/**
 * Copyright 2020-2022 Csekme Krisztián
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package builder.RibbonMenu;

import java.awt.Color;
//import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

/**
 * The Class RibbonMenuItem.
 */
public class RibbonMenuItem extends JMenuItem {

 	/** The Constant serialVersionUID. */
	 private static final long serialVersionUID = 1L;
 	
 	/** The Constant SCALING_FACTOR. */
	 public static final double SCALING_FACTOR =((double)java.awt.Toolkit.getDefaultToolkit().getScreenResolution()) / 96 ;

	/** The ref. */
	final JMenuItem ref = new JMenuItem();
    
    /** The Constant CHECKED_ICON. */
    private final static ImageIcon CHECKED_ICON = Util.accessImageFile("images/checked.png");
    
    /** The Constant UNCHECKED_ICON. */
    private final static ImageIcon UNCHECKED_ICON = Util.accessImageFile("images/unchecked.png");
	
    /** The hover. */
    boolean hover;
    
    /** The pressed. */
    boolean pressed;
    
    /** The icon. */
    private ImageIcon icon;

    /** The check menu. */
    private boolean checkMenu = false;
    
    /** The color hover. */
    private static Color colorHover = new Color(232, 239, 247);
    
    /** The color pressed. */
    private static Color colorPressed = new Color(201, 224, 247);
    
    /** The color background. */
    private static Color colorBackground = new Color(255, 255, 255);
 
    
    /**
     * Sets the icon.
     *
     * @param icon
     *          the new icon
     */
    public void setIcon(ImageIcon icon) {
    	this.icon = icon;
    }

    /**
     * Instantiates a new ribbon menu item.
     *
     * @param title
     *          the title
     * @param defaultSelection
     *          the default selection
     */
    public RibbonMenuItem(String title, boolean defaultSelection) {
        super(title);
        this.checkMenu = true;
        this.setSelected(defaultSelection);
        addMouseListener(MA);
    }

    /**
     * Instantiates a new ribbon menu item.
     *
     * @param title
     *          the title
     */
    public RibbonMenuItem(String title) {
        super(title);
        addMouseListener(MA);
    }

    /**
     * Instantiates a new ribbon menu item.
     *
     * @param title
     *          the title
     * @param icon
     *          the icon
     */
    public RibbonMenuItem(String title, ImageIcon icon) {
        super(title);

        this.icon = icon;
        addMouseListener(MA);
    }

    /**
     * Checks if is check menu.
     *
     * @return true, if is check menu
     */
    public boolean isCheckMenu() {
        return checkMenu;
    }

    /**
     * Sets the check menu.
     *
     * @param checkMenu
     *          the new check menu
     */
    public void setCheckMenu(boolean checkMenu) {
        this.checkMenu = checkMenu;
    }

    /**
     * Sets the hover color.
     *
     * @param color
     *          the new hover color
     */
    public static void setHoverColor(Color color) {
        colorHover = color;
    }

    /**
     * Sets the pressed color.
     *
     * @param color
     *          the new pressed color
     */
    public static void setPressedColor(Color color) {
        colorPressed = color;
    }

    /**
     * Sets the background color.
     *
     * @param color
     *          the new background color
     */
    public static void setBackgroundColor(Color color) {
        colorBackground = color;
    }

    /**
     * paint
     *
     * @see javax.swing.JComponent#paint(Graphics)
     */
    @Override
    public void paint(Graphics gl) {
        Graphics2D g = (Graphics2D) gl;

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setFont(ref.getFont().deriveFont(Font.PLAIN));
        g.setColor(colorBackground);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (hover) {
            g.setColor(colorHover);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        if (pressed) {
            g.setColor(colorPressed);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(ref.getForeground());
        g.drawString(getText(), getIconTextGap() + 26, (int)(16 * SCALING_FACTOR));
        
        if (!isCheckMenu() && icon != null) {
            g.drawImage(icon.getImage(), 4, 3, (int)(16 * SCALING_FACTOR), (int)(16 * SCALING_FACTOR), this);
        }

        if (isCheckMenu()) {
            if (isSelected()) {
            	if (CHECKED_ICON!=null)
                g.drawImage(CHECKED_ICON.getImage(), 4, 3, (int)(16 * SCALING_FACTOR), (int)(16 * SCALING_FACTOR), this);

            } else {
            	if (UNCHECKED_ICON!=null)
                g.drawImage(UNCHECKED_ICON.getImage(), 4, 3, (int)(16 * SCALING_FACTOR), (int)(16 * SCALING_FACTOR), this);
            }
        }

        // super.paint(g);
    }

    /** The ma. */
    MouseAdapter MA = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {

            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setSelected(!isSelected());
            pressed = true;
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            hover = false;
            pressed = false;
            RibbonBar.fired();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hover = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            hover = true;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            hover = true;
        }

    };

}
