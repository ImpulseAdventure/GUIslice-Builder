/**
 * Copyright 2020-2022 Csekme Krisztián
 * Modified by Paul Conti 2024
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

import builder.Builder;

import javax.swing.*;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.List;
import java.util.*;

import builder.controller.PropManager;
import builder.views.PagePane;
import builder.common.Utils;
import builder.views.TreeView;

/**
 * Office styled RibbonBar main component.
 *
 * @author Csekme Krisztián
 *
 * Paul Conti May 30, 2024
 * Added support for drag and drop. However, DnD can't work with multiple JPanels (like a JTabbedPane)
 * In such a condition you get multiple startDrags and the code has no idea which ones to ignore.
 * Issueing multiple startDrags gives this error:
 * class java.awt.dnd.InvalidDnDOperationException: Drag and drop in progress
 * So I modified this library to use builder.views.PagePane instead of JPanel as a drop target.
 * Then I extended PagePane with a isActive() function.  JPanel doesn't normally support hasFocus function.
 * With this RibbonDragGestureListener() can ignore any panel without focus.
 *
 * Paul Conti - June 7, 2022 Added QuickAccess Bar 
 * Removed scaling since beginning with java 10 scaling should 
 * work automatically with the graphics context so scaling factor
 * is always 1. If you are still running java 1.8 or 9 upgrade 
 * if you need higher DPI support.
 * Reference:
 * https://stackoverflow.com/questions/50968992/how-do-i-run-a-java-app-with-windows-high-dpi-scaling
 */

public class RibbonBar extends JComponent {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6524936981221127992L;

	/** The Constant SERIES. */
	// for generate tokens
	private final static String SERIES = "ABCDEFGHIJKLMNOPQRSTWZXYabcdefghijklmneopqrstzyxwv0123456789#&@{}*";

	/** The instance. */
	private static RibbonBar instance = null;
	
	/** The Constant COLOR_RIBBON_BACKGROUND. */
	// colors
	public static final int COLOR_RIBBON_BACKGROUND = 1;
	
	/** The Constant COLOR_RIBBON_TAB_CONTAINER_BACKGROUND. */
	public static final int COLOR_RIBBON_TAB_CONTAINER_BACKGROUND = 2;
	
	/** The Constant COLOR_RIBBON_TAB_CONTAINER_STRIP. */
	public static final int COLOR_RIBBON_TAB_CONTAINER_STRIP = 3;
	
	/** The Constant COLOR_RIBBON_TAB_BACKGROUND. */
	public static final int COLOR_RIBBON_TAB_BACKGROUND = 4;
	
	/** The Constant COLOR_RIBBON_TAB_FOREGROUND. */
	public static final int COLOR_RIBBON_TAB_FOREGROUND = 5;
	
	/** The Constant COLOR_RIBBON_TAB_HOVER_BACKGROUND. */
	public static final int COLOR_RIBBON_TAB_HOVER_BACKGROUND = 6;
	
	/** The Constant COLOR_RIBBON_TAB_HOVER_FOREGROUND. */
	public static final int COLOR_RIBBON_TAB_HOVER_FOREGROUND = 7;
	
	/** The Constant COLOR_RIBBON_TAB_SELECTED_STRIP_BACKGROUND. */
	public static final int COLOR_RIBBON_TAB_SELECTED_STRIP_BACKGROUND = 8;
	
	/** The Constant COLOR_RIBBON_TAB_SELECTED_FOREGROUND. */
	public static final int COLOR_RIBBON_TAB_SELECTED_FOREGROUND = 9;
	
	/** The Constant COLOR_RIBBON_BUTTON_BACKGROUND. */
	public static final int COLOR_RIBBON_BUTTON_BACKGROUND = 10;
	
	/** The Constant COLOR_RIBBON_BUTTON_PRESSED_BACKGROUND. */
	public static final int COLOR_RIBBON_BUTTON_PRESSED_BACKGROUND = 11;
	
	/** The Constant COLOR_RIBBON_BUTTON_HOVER_BACKGROUND. */
	public static final int COLOR_RIBBON_BUTTON_HOVER_BACKGROUND = 12;
	
	/** The Constant COLOR_RIBBON_BUTTON_FOREGROUND. */
	public static final int COLOR_RIBBON_BUTTON_FOREGROUND = 13;
	
	/** The Constant COLOR_RIBBON_BUTTON_DISABLED_FOREGROUND. */
	public static final int COLOR_RIBBON_BUTTON_DISABLED_FOREGROUND = 14;
	
	/** The Constant COLOR_RIBBON_SEPARATOR_FOREGROUND. */
	public static final int COLOR_RIBBON_SEPARATOR_FOREGROUND = 15;
	
	/** The Constant COLOR_RIBBON_GROUP_COLOR. */
	public static final int COLOR_RIBBON_GROUP_COLOR = 16;
	
	/** The Constant COLOR_RIBBON_SHADOW_DARK. */
	public static final int COLOR_RIBBON_SHADOW_DARK = 17;
	
	/** The Constant COLOR_RIBBON_SHADOW_LIGHT. */
	public static final int COLOR_RIBBON_SHADOW_LIGHT = 18;
	
	/** The Constant COLOR_RIBBON_MENUITEM_HOVER. */
	public static final int COLOR_RIBBON_MENUITEM_HOVER = 19;
	
	/** The Constant COLOR_RIBBON_MENUITEM_PRESSED. */
	public static final int COLOR_RIBBON_MENUITEM_PRESSED = 20;
	
	/** The Constant COLOR_RIBBON_MENUITEM_BACKGROUND. */
	public static final int COLOR_RIBBON_MENUITEM_BACKGROUND = 21;
	
	/** The Constant COLOR_RIBBON_TAB_SELECTED_BACKGROUND. */
	public static final int COLOR_RIBBON_TAB_SELECTED_BACKGROUND = 22;
	
	/** The Constant COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR. */
	public static final int COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR = 23;
	
	/** The size button width. */
	public static int SIZE_BUTTON_WIDTH = 75;
	
	/** The size button height. */
	private static int SIZE_BUTTON_HEIGHT = 75;
	
  /** The quick button image size. */
  public static int QUICKBUTTON_IMAGE_SIZE = 22;
  
	/** The button image size. */
	public static int BUTTON_IMAGE_SIZE = 24;
	
	
	/** The quickbar width. */
	// dimesnions
	static int quickbarWidth = 0;
	
	/** The quickbar height. */
	static int quickbarHeight = 0;
	
	/** The tab layout west east margin. */
	static int tabLayoutWestEastMargin = 8;
	
	/** The ribbon tab height. */
	static int ribbonTabHeight = 28;
	
	/** The strip height. */
	static int stripHeight = 0;
	
	/** The east west tab inset. */
	static int eastWestTabInset = 20;
	
	/** The north tab inset. */
	static int northTabInset = 0;
	
	/** The button left right margin. */
	static int buttonLeftRightMargin = 4;
	
	/** The ribbon button top base. */
	static int ribbonButtonTopBase = ribbonTabHeight + 4;
	
	/** The button width. */
	static int buttonWidth = SIZE_BUTTON_WIDTH;
	
	/** The button height. */
	static int buttonHeight = SIZE_BUTTON_HEIGHT;
	
	/** The button partial height. */
	static int buttonPartialHeight = 35;
	
	/** The slim button height. */
	static int slimButtonHeight = 25;
	
	/** The separator width. */
	static int separatorWidth = 7;
	
	/** The separator height. */
	static int separatorHeight = 88;
	
	/** The shadow height. */
	static int shadowHeight = 10;
	
	/** The ribbon height. */
	static int ribbonHeight = 126 + shadowHeight;

	/** The minimized. */
	boolean minimized = false;
	
	/** The reminimized. */
	boolean reminimized = false;
	
	/** The disable collapse. */
	boolean disable_collapse = false;

	/** The Constant POPUP_MENU. */
	static final JPopupMenu POPUP_MENU = new JPopupMenu();
	
	/** The font. */
	private Font font = null;

	/** The Constant COLORS. */
	// containers
	static final Map<Integer, Color> COLORS = new HashMap<>();
  
  /** The quickbar. */
  static       QuickAccessBar QUICKBAR = null;
	
	/** The Constant TABS. */
	static final List<Tab> TABS = new ArrayList<>();

	/** The Constant toggle. */
	static final Button toggle = new Button(generateToken(20));
  
  /** The pinned. */
  //private static ImageIcon pinned = Util.accessImageFile("images/pinned.png");

	/** The build menu. */
	boolean buildMenu = true;

  /**
   * RibbonBar Factory to create our Singleton Object.
   * 
   * @return Ribbonbar instance only one permitted
   */
  public static synchronized RibbonBar getInstance() {
    if (instance == null) {
      instance = new RibbonBar();
    }
    return instance;
  }

	/**
   * Constructor.
   */
	public RibbonBar() {
		POPUP_MENU.setOpaque(true);
		POPUP_MENU.setBackground(Color.white);
		add(POPUP_MENU);

		if (font == null) {
			// inherit font from JMenuItem
			font = new JMenuItem().getFont().deriveFont(Font.PLAIN).deriveFont(12f);
		}
		{
			setMinimumSize(new Dimension(0, ribbonHeight));
			setPreferredSize(new Dimension(100, ribbonHeight));
		}
//		toggle.setImage(Util.accessImageFile("images/minimize.png"));

		// load default appearance
		COLORS.put(COLOR_RIBBON_BACKGROUND, new Color(245, 246, 247));
		COLORS.put(COLOR_RIBBON_TAB_CONTAINER_BACKGROUND, new Color(255, 255, 255));
		COLORS.put(COLOR_RIBBON_TAB_CONTAINER_STRIP, new Color(230, 229, 228));
		COLORS.put(COLOR_RIBBON_TAB_BACKGROUND, new Color(255, 255, 255));
		COLORS.put(COLOR_RIBBON_TAB_HOVER_BACKGROUND, new Color(250, 251, 252));
		COLORS.put(COLOR_RIBBON_TAB_SELECTED_BACKGROUND,   new Color(245, 246, 247));
		COLORS.put(COLOR_RIBBON_TAB_FOREGROUND, new Color(70, 70, 70));
		COLORS.put(COLOR_RIBBON_TAB_HOVER_FOREGROUND,  new Color(70, 70, 70));
		COLORS.put(COLOR_RIBBON_TAB_SELECTED_FOREGROUND,  new Color(70, 70, 70));
		COLORS.put(COLOR_RIBBON_TAB_SELECTED_STRIP_BACKGROUND, new Color(245, 246, 247));
		COLORS.put(COLOR_RIBBON_BUTTON_BACKGROUND, new Color(245, 246, 247));
		COLORS.put(COLOR_RIBBON_BUTTON_HOVER_BACKGROUND, new Color(232, 239, 247));
		COLORS.put(COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR, new Color(164, 206, 249));
		COLORS.put(COLOR_RIBBON_SEPARATOR_FOREGROUND, new Color(179, 176, 173));
		COLORS.put(COLOR_RIBBON_BUTTON_FOREGROUND, new Color(72, 70, 68));
		COLORS.put(COLOR_RIBBON_BUTTON_DISABLED_FOREGROUND, new Color(142, 142, 142));
		COLORS.put(COLOR_RIBBON_GROUP_COLOR, new Color(130, 130, 130));
		COLORS.put(COLOR_RIBBON_SHADOW_DARK, new Color(211, 211, 211));
		COLORS.put(COLOR_RIBBON_SHADOW_LIGHT, new Color(230, 230, 230));
		COLORS.put(COLOR_RIBBON_BUTTON_PRESSED_BACKGROUND, new Color(201, 224, 247));	
		// add listeners
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		
		// register for tooltips
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
   * Set color of an UI elements.
   *
   * @param key
   *          Class constant
   * @param value
   *          as Color
   */
	public static void putColor(int key, Color value) {
		COLORS.put(key, value);
		if (key == COLOR_RIBBON_MENUITEM_HOVER) {
			RibbonMenuItem.setHoverColor(value);
		}
		if (key == COLOR_RIBBON_MENUITEM_PRESSED) {
			RibbonMenuItem.setPressedColor(value);
		}
		if (key == COLOR_RIBBON_MENUITEM_BACKGROUND) {
			RibbonMenuItem.setBackgroundColor(value);
		}
	}

	/**
   * Toggle.
   */
	void toggle() {
		if (minimized) {
			setMinimumSize(new Dimension(100, quickbarHeight+ ribbonTabHeight + shadowHeight));
			setPreferredSize(new Dimension(getWidth(), quickbarHeight + ribbonTabHeight + shadowHeight));
			setSize(new Dimension(getWidth(), quickbarHeight + ribbonTabHeight + shadowHeight));
		} else {
			setMinimumSize(new Dimension(0,  ribbonHeight));
			setPreferredSize(new Dimension(getWidth(),  ribbonHeight));
			setSize(new Dimension(getWidth(),  ribbonHeight));
		}
		getParent().revalidate();
	}
	
	/**
	 * disableAutoCollapse
	 * For some applications the autocollapse can be come annoying
	 * so calling this routine will turn this feature off.
	 * The user can still collapse the ribbon my selecting
	 * minimize button but won't have to select the pinned button.
	 */
	public void disableCollapse() {
	  disable_collapse = true;
	}
	
	/**
   * Gets the max line width.
   *
   * @param text
   *          the text
   * @return the max line width
   */
	int getMaxLineWidth(String text) {
		String lines[] = text.split("\n");
		int w = 0;
		for (String line : lines) {
			 int l = getGraphics().getFontMetrics(font).stringWidth(line);
			 if (l>w) {
				 w = l;
			 }
		}
		return w;
	}
	

	/**
   * build an entire menu structure.
   */
	private void buildMenu() {

		{
			toggle.setX(getWidth() - 18);
			toggle.setY(getHeight() - 18 - shadowHeight);
			toggle.setWidth(16);
			toggle.setHeight(16);
		}

		/* iterates over quick access bar and determine
		 * height and width of our bar
		 */
		if (QUICKBAR != null) {
		  quickbarHeight = QUICKBUTTON_IMAGE_SIZE + buttonLeftRightMargin;
		  ribbonHeight += quickbarHeight;
      int offset_bx = tabLayoutWestEastMargin; 
      int offset_by = northTabInset + (buttonLeftRightMargin / 2);
      for (int i = 0; i < QUICKBAR.getButtons().size(); i++) {
        QuickButton b = QUICKBAR.getButtons().get(i);
        if (b.isSeparator()) {
          b.setHeight(QUICKBUTTON_IMAGE_SIZE);
          b.setX(offset_bx);
          b.setY(offset_by);
          offset_bx += separatorWidth;
        } else {
          b.setX(offset_bx);
          b.setY(offset_by);
          b.setHeight(QUICKBUTTON_IMAGE_SIZE);
          b.setWidth(QUICKBUTTON_IMAGE_SIZE);
          offset_bx += QUICKBUTTON_IMAGE_SIZE;
        }
      }
      quickbarWidth = offset_bx;
      ribbonButtonTopBase += quickbarHeight; 
		}
    
		int offset_t = 0;

		// iterates over supreme items
		for (int i = 0; i < TABS.size(); i++) {
			Tab tab = TABS.get(i);
			
			int w =   getMaxLineWidth(tab.getTitle()) + (eastWestTabInset * 2);
			tab.setWidth(w);
			tab.setHeight(ribbonTabHeight);
			tab.setX(offset_t);
			tab.setY(northTabInset+quickbarHeight);
			offset_t += w;
			int offset_bx = tabLayoutWestEastMargin; 
			int offset_by = quickbarHeight;
			int slim_count = 0;
			int slim_max = 0;

			// iterates over buttons
			for (int b = 0; b < tab.getButtons().size(); b++) {
				Button button = tab.getButtons().get(b);
				if (button.isSlim()) {
					int sw;
					if (button.getTitle() != null && button.getTitle().length() > 0) {
						sw = getGraphics().getFontMetrics(font).stringWidth(button.getTitle()) + 26;
					} else {
						sw = 22;
					}

					if (slim_max < sw) {
						slim_max = sw;
					}
					button.setWidth(sw);
					button.setHeight(slimButtonHeight);
					button.setX(offset_bx);
					button.setY(ribbonButtonTopBase + offset_by);
					slim_count++;
					offset_by += slimButtonHeight;
					if (slim_count%3 ==0) {
						offset_bx += slim_max;
						offset_by = 0;
						slim_max = 0;
					}
				}
				if (button.isSeparator()) {
					if (slim_count > 0) {
						slim_count = 0;
						offset_bx += slim_max;
					}
					button.setHeight(separatorHeight);
					button.setX(offset_bx);
					button.setY(ribbonButtonTopBase);
					offset_by = 0;
					offset_bx += separatorWidth;
					slim_count = 0;
				}
				if (!button.isSlim() && !button.isSeparator()) {
					if (slim_count > 0) {
						slim_count = 0;
						offset_bx += slim_max;
					}

					// set new width based on text
					int bw = getMaxLineWidth(button.getTitle()) + buttonLeftRightMargin * 2 ;
					button.setWidth(bw);
					button.setHeight(buttonHeight);
					button.setX(offset_bx);
					button.setY(ribbonButtonTopBase);
					offset_bx += bw + 2;
					slim_count = 0;
					offset_by = 0;
				}
			}
		}
    setMinimumSize(new Dimension(0,  ribbonHeight));
    setPreferredSize(new Dimension(getWidth(),  ribbonHeight));
    setSize(new Dimension(getWidth(),  ribbonHeight));
		repaint();
	}

  /**
   * QuickAccess bar is a toolbar at top of ribbon The quick access bar panel
   * allows showing controls that are always visible.
   *
   * @return currently created Tab
   */
  public static QuickAccessBar addQuickAccess() {
    QUICKBAR = QuickAccessBar.create();
    return QUICKBAR;
  }
  
  /**
   * Tab is a top level menu for the top of ribbon.
   *
   * @param title
   *          caption of Tab button
   * @return currently created Tab
   */
	public static Tab addTab(String title) {
		String gen = generateToken(8);
		Tab tab = new Tab(gen);
		tab.setTitle(title);
		if (TABS.size()==0) {
			tab.setSelected( true );
		}
		TABS.add(tab);
		return tab;
	}

	public void createDragSource(PagePane p) {
//		DragSource dragSource = new DragSource();
		// global singleton
		DragSource dragSource = DragSource.getDefaultDragSource();
		DragSourceMotionListener dsml = new DragSourceMotionListener() {
			@Override
			public void dragMouseMoved(DragSourceDragEvent dsde) {
				DragSourceContext context = dsde.getDragSourceContext();
//				Builder.logger.debug(context.toString());
				Point pts = new Point();
				SwingUtilities.convertPointFromScreen(pts, p);
				if (pts.x < 0 || pts.y < 0) {
					context.setCursor(DragSource.DefaultMoveNoDrop);
//					Builder.logger.debug("--- not on TFT: "+pts.toString());
				}
//				Builder.logger.debug(" pts: " + pts.toString());
			}
		};
		dragSource.addDragSourceMotionListener(dsml);
		RibbonDragGestureListener dlistener = new RibbonDragGestureListener();
		dlistener.setTarget(p);
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, dlistener);

	}

	/**
   * Scale.
   *
   * @param im
   *          the im
   * @param width
   *          the width
   * @param height
   *          the height
   * @return the <code>image</code> object
   */
	public static Image scale(Image im, int width, int height) {
		Image b = im.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	  BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	  Graphics2D g2d = dimg.createGraphics();
	  g2d.drawImage(b, 0, 0, null);
	  g2d.dispose();

		return b;
	}

	/**
   * Generate unique token for element.
   *
   * @param length
   *          as length of token
   * @return desired token
   */
	public static String generateToken(int length) {
		StringBuilder sb = new StringBuilder();
		Random rnd = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(SERIES.charAt(rnd.nextInt(SERIES.length() - 1)));
		}
		return sb.toString();
	}

	/**
   * Graphics function.
   *
   * @param gg
   *          the Graphics context in which to paint
   */
	@Override
	public void paint(Graphics gg) {
		Graphics2D g = (Graphics2D) gg;		 
		// set quality
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// the very first time build entire menu structure
		if (buildMenu) {
			buildMenu = false;
			buildMenu();
		}

		// Ribbon background
		g.setColor(COLORS.get(COLOR_RIBBON_BACKGROUND));
		g.fillRect(0, 0, getWidth(), getHeight());

		// Ribbon tab background
		g.setColor(COLORS.get(COLOR_RIBBON_TAB_CONTAINER_BACKGROUND));
		g.fillRect(0, 0, getWidth(), ribbonTabHeight);

		g.setColor(COLORS.get(COLOR_RIBBON_TAB_CONTAINER_STRIP));
		g.drawLine(0, ribbonTabHeight, getWidth(), ribbonTabHeight);

		// set graphics font
		if (font != null) {
			g.setFont(font);
		}

    // draw quick access bar
    if (QUICKBAR != null) {
      Shape contour = getOutline();
      if (contour != null) {
        g.setColor(COLORS.get(COLOR_RIBBON_SEPARATOR_FOREGROUND));
        g.draw(contour);
      }
      for (int i = 0; i < QUICKBAR.getButtons().size(); i++) {
        QuickButton b = QUICKBAR.getButtons().get(i);
        if (b.isSeparator()) {
          g.setColor(COLORS.get(COLOR_RIBBON_SEPARATOR_FOREGROUND));
          g.drawLine(b.getX() + separatorWidth / 2, b.getY() + 1,
              b.getX() + separatorWidth / 2, b.getY() + b.getHeight());
        } else {
          if (b.isPressed()) {
            g.setColor(COLORS.get(COLOR_RIBBON_BUTTON_PRESSED_BACKGROUND));
          }
          if (b.getImage() != null) {
            int image_size = QUICKBUTTON_IMAGE_SIZE;
            g.drawImage(scale(b.getImage().getImage(), image_size, image_size), 
                b.getX(), b.getY(), 
                image_size,
                image_size, 
                this);
          }
        }
      } 
    }
    
		// draw tabs
		for (int i = 0; i < TABS.size(); i++) {
			Tab tab = TABS.get(i);
			if (tab.isHover()) {
				g.setColor(COLORS.get(COLOR_RIBBON_TAB_HOVER_BACKGROUND));
			} else {
				if (tab.isSelected()) {
					g.setColor(COLORS.get(COLOR_RIBBON_TAB_SELECTED_BACKGROUND));
				} else {
					g.setColor(COLORS.get(COLOR_RIBBON_TAB_BACKGROUND));
				}
			}
			g.fillRect(tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight());

			// Selected tab
			if (tab.isSelected()) {
				g.setColor(COLORS.get(COLOR_RIBBON_TAB_CONTAINER_STRIP));
				g.drawRect(tab.getX(), tab.getY(), tab.getWidth()-1, tab.getHeight());
				g.setColor(COLORS.get(COLOR_RIBBON_BACKGROUND));
				g.drawLine(tab.getX(), tab.getHeight(), tab.getX()+tab.getWidth(), tab.getHeight());
				
				g.setColor(COLORS.get(COLOR_RIBBON_TAB_SELECTED_STRIP_BACKGROUND));
				if (tab.isHover()) {
					g.fillRect(tab.getX(), tab.getY() + tab.getHeight() - stripHeight, tab.getWidth(), stripHeight);
				} else {
					int half = (tab.getWidth() - g.getFontMetrics().stringWidth(tab.getTitle())) / 3;
					g.fillRect(tab.getX() + half, tab.getY() + tab.getHeight() - stripHeight, tab.getWidth() - half * 2, stripHeight);
				}
			
			}

			if (tab.isHover()) {
				g.setColor(COLORS.get(COLOR_RIBBON_TAB_HOVER_FOREGROUND));
			} else {
				g.setColor(COLORS.get(COLOR_RIBBON_TAB_FOREGROUND));
			}
			if (tab.isSelected()) {
				g.setColor(COLORS.get(COLOR_RIBBON_TAB_SELECTED_FOREGROUND));
			}
			g.drawString(tab.getTitle(),
					tab.getX() + tab.getWidth() / 2 - g.getFontMetrics().stringWidth(tab.getTitle()) / 2,
					tab.getY() +  20 );

		 
			int lastSeparatorposition = 0;
			// render selected tab
			if (tab.isSelected() && !minimized) {
				{ // Group title
					g.setFont(font);
					int index = 0;
					for (Button separator : tab.getSeparators()) {
						String groupname = tab.getGroupName(index);
						if (groupname != null) {
							g.setColor(COLORS.get(COLOR_RIBBON_GROUP_COLOR));
							int groupname_length = g.getFontMetrics().stringWidth(groupname);
							int west = separator.getX();
							if (index==0) {
								g.drawString( groupname, west/2-groupname_length/2, getHeight() - 6 - shadowHeight );
							} else  {
								g.drawString( groupname, lastSeparatorposition +(((west - lastSeparatorposition)/2) - groupname_length/2), 
								    getHeight() - 6 - shadowHeight );
							} 
						}
						 
						lastSeparatorposition = separator.getX();
						index++;
					}
				
					g.setFont(font);
				}

				// Buttons under selected tab
				for (int y = 0; y < tab.getButtons().size(); y++) {
					Button button = tab.getButtons().get(y);

					if (button.isSeparator()) {
						g.setColor(COLORS.get(COLOR_RIBBON_SEPARATOR_FOREGROUND));
						g.drawLine(button.getX() + separatorWidth / 2, button.getY() + 1,
								button.getX() + separatorWidth / 2, button.getY() + separatorHeight);
					} else {
						if (button.isHover()) {
							g.setColor(COLORS.get(COLOR_RIBBON_BUTTON_HOVER_BACKGROUND));
						} else {
							g.setColor(COLORS.get(COLOR_RIBBON_BUTTON_BACKGROUND));
						}
						if (button.isPressed()) {
							g.setColor(COLORS.get(COLOR_RIBBON_BUTTON_PRESSED_BACKGROUND));
						}

						if (!button.isSlim() && button.hasDropDown() && button.isHover()) {
							if (button.isHoverTop()) {
								g.fillRect(button.getX(), button.getY(), button.getWidth() + 1, buttonPartialHeight);
								
								g.setColor( COLORS.get(COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR) );
								g.setStroke(new BasicStroke(1.0f));
								
								g.drawRect(button.getX(), button.getY() + buttonPartialHeight, button.getWidth() + 1,
										button.getHeight() - buttonPartialHeight);
								
								g.drawRect(button.getX(), button.getY(), button.getWidth() + 1, buttonPartialHeight);
								
								
							} else {
								g.setColor( COLORS.get(COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR) );
								g.setStroke(new BasicStroke(1.0f));

								g.drawRect(button.getX(), button.getY(), button.getWidth() + 1, buttonPartialHeight);

								g.setColor( COLORS.get(COLOR_RIBBON_BUTTON_HOVER_BACKGROUND) );

								
								g.fillRect(button.getX(), button.getY() + buttonPartialHeight, button.getWidth() + 1,
										button.getHeight() - buttonPartialHeight);

								g.setColor( COLORS.get(COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR) );
								g.setStroke(new BasicStroke(1.0f));

								g.drawRect(button.getX(), button.getY() + buttonPartialHeight, button.getWidth() + 1,
										button.getHeight() - buttonPartialHeight);

							}

						} else {
							g.fillRect(button.getX(), button.getY(), button.getWidth(), button.getHeight());
							if (button.isHover()) {
								g.setColor( COLORS.get(COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR) );
								g.setStroke(new BasicStroke(1.0f));
								g.drawRect(button.getX(), button.getY(), button.getWidth(), button.getHeight()-1);
							}
						}

						//button text color
						g.setColor( button.isEnabled()?COLORS.get(COLOR_RIBBON_BUTTON_FOREGROUND):COLORS.get(COLOR_RIBBON_BUTTON_DISABLED_FOREGROUND) );
						//slim button
						if (button.isSlim()) {
							if (button.getImage() == null) {
								g.drawString(button.getTitle(), button.getX() + 4, button.getY() + button.getHeight() - 8);
							} else {
								g.drawImage(scale(button.getImage().getImage(), 16, 16),
								    button.getX() + 2, 
								    button.getY() + 4,
										16, 
										16,
										this);
								
								g.drawString(button.getTitle(), button.getX() + 4 + 16,
										button.getY() + button.getHeight() - 8);
							}
						} else {
							if (button.hasDropDown()) {
								if (button.getImage() != null) {
									int image_size = BUTTON_IMAGE_SIZE;
									int shift = 2;
									if (button.getTitle().contains("\n")) {
										shift=4;
									}
									
									g.drawImage(scale(button.getImage().getImage(), image_size, image_size), 
											button.getX() + (button.getWidth() / 2) - image_size / 2 , 
											button.getY() + (button.getHeight() / 2) - image_size - shift , 
											image_size,
											image_size, 
											this);
									
									
									//g.drawImage(button.getImage().getImage(), button.getX() + 26, button.getY() + 6, 24,
									//		24, this);
								}
								String[] lines = button.getTitle().split("\n");
								for (int l = 0; l < lines.length; l++) {
									int w = g.getFontMetrics().stringWidth(lines[l]);
									g.drawString(lines[l], button.getX() + button.getWidth() / 2 - w / 2, button.getY()
											+ button.getHeight() - 16 + (l * 14) - (lines.length > 1 ? 10 : 0));
								}
								g.setColor(Color.GRAY);
								g.setStroke(new BasicStroke(1.3f));
								g.drawLine(button.getX() + button.getWidth() / 2 - 3,
										button.getY() + button.getHeight() - 6, button.getX() + button.getWidth() / 2,
										button.getY() + button.getHeight() - 4);
								g.drawLine(button.getX() + button.getWidth() / 2 + 3,
										button.getY() + button.getHeight() - 6, button.getX() + button.getWidth() / 2,
										button.getY() + button.getHeight() - 4);

								// Normal classic button
							} else {
								if (button.getImage() != null) {
									int image_size = BUTTON_IMAGE_SIZE;
								 
									g.drawImage(scale(button.getImage().getImage(), image_size, image_size), 
											button.getX() + (button.getWidth() / 2) - image_size / 2 , 
											button.getY() + (button.getHeight() / 2) - image_size , 
											image_size,
											image_size, 
											this);
								}
								String[] lines = button.getTitle().split("\n");
								for (int l = 0; l < lines.length; l++) {
									if (l>1) {
										break;
									}
									int w = g.getFontMetrics().stringWidth(lines[l]);
									g.drawString(lines[l], button.getX() + button.getWidth() / 2 - w / 2, 
											button.getY()
											+ button.getHeight() - 16 + (l * 14) - (lines.length > 1 ? 10 : 0)  );
								}

							}

						}
					}
				}
			}

			// draw shadow
			if (shadowHeight > 0) {
				GradientPaint shadow_paint = new GradientPaint(0, getHeight() - shadowHeight,
						COLORS.get(COLOR_RIBBON_SHADOW_DARK), 0, getHeight(), COLORS.get(COLOR_RIBBON_SHADOW_LIGHT));
				g.setPaint(shadow_paint);
				g.fill(new Rectangle2D.Double(0, getHeight() - shadowHeight, getWidth(), getHeight()));
			}
		}

		{
			toggle.setX(getWidth() - 20 - tabLayoutWestEastMargin);
			toggle.setY(getHeight() - 18 - shadowHeight);
			toggle.setWidth(16);
			toggle.setHeight(16);
		}
//		if (!minimized && !disable_collapse) {
//			if (reminimized) {
//				g.drawImage(pinned.getImage(), toggle.getX(), toggle.getY(), 16, 16, this);
//			} else {
//				g.drawImage(toggle.getImage().getImage(), toggle.getX(), toggle.getY(), 16, 16, this);
//			}
//		}
		super.paint(g);
	}

	/**
   * Clear all selection and hover flags.
   */
	public void clearFlag() {
		for (int i = 0; i < TABS.size(); i++) {
			for (int j = 0; j < TABS.get(i).getButtons().size(); j++) {
				TABS.get(i).getButtons().get(j).setSelected(false);
				TABS.get(i).getButtons().get(j).setHover(false);
			}
		}
		repaint();
	}
	
	/**
   * Fired.
   */
	public static void fired() {
	  if (instance != null) {
		  if (instance.reminimized) {
        instance.minimized = true;
        instance.toggle();
      }
			if (instance.minimized) {
				instance.minimized = false;
				instance.toggle();
			}
	  }
	}
 
  /**
   * getToolTipText
   *
   * @see JComponent#getToolTipText(MouseEvent)
   */
  @Override
  public String getToolTipText(MouseEvent e) {
    if (QUICKBAR != null) {
      for (int i = 0; i < QUICKBAR.getButtons().size(); i++) {
        QuickButton b = QUICKBAR.getButtons().get(i);
        if (!b.isSeparator() && b.inBounds(e.getPoint(), b.getToken())) {
          return b.getToolTip();
        }
      }
    }
    for (int i = 0; i < TABS.size(); i++) {
      Tab t = TABS.get(i);
      if (t.isSelected()) {
        for (int j = 0; j < t.getButtons().size(); j++) {
          Button b = t.getButtons().get(j);
          if (!b.isSeparator()) {
            if (b.inBounds(e.getPoint(), b.getToken())) {
              return b.getToolTip();
            }
          }
        } // end for button search
      } // end t.isSelected()
    } // end for tab search
    // no tool tip
    return null;
  }

  /**
   * Returns the outline of Quick Access Bar.
   *
   * @return The outline of this taskbar panel.
   */
  protected Shape getOutline() {
    double height = (double)quickbarHeight - 1.0f;
    if (QUICKBAR == null) {
        return null;
    }
    int minX = 0;
    int maxX = quickbarWidth;
 
    float radius = (float) height / 2.0f;

    GeneralPath outline = new GeneralPath();

    // top left corner
      outline.moveTo(minX - 1, 0);
    // top right corner
    outline.lineTo(maxX, 0);
    // right arc
    outline.append(new Arc2D.Double(maxX - radius, 0, height,
        height, 90, -180, Arc2D.OPEN), true);
    // bottom left corner
    outline.lineTo(minX - 1, height);
    outline.lineTo(minX - 1, 0);

    return outline;
  }

	private Button findDragButton(Point p) {
		if (p.y > ribbonTabHeight) {
			for (int t = 0; t < TABS.size(); t++) {
				Tab tab = TABS.get(t);
				if (tab.isSelected()) {
					for (int b = 0; b < TABS.get(t).getButtons().size(); b++) {
						Button but = TABS.get(t).getButtons().get(b);
						if (but.inBounds(p, but.getToken()) && but.isDrageEnabled()) {
							return but;
						}
					}
				}
			}
		}
		return null;
	}

	/** Global mouse adapter. */
	final MouseAdapter mouse = new MouseAdapter() {

		@Override
		public void mouseMoved(MouseEvent e) {
			
			
			for (int i = 0; i < TABS.size(); i++) {
				TABS.get(i).setHover(false);
			}
			for (int i = 0; i < TABS.size(); i++) {
				TABS.get(i).setHover(TABS.get(i).inBounds(e.getPoint(), TABS.get(i).getToken()));
			}
			for (int i = 0; i < TABS.size(); i++) {
				Tab t = TABS.get(i);
				if (t.isSelected()) {
					for (int j = 0; j < t.getButtons().size(); j++) {
						Button b = t.getButtons().get(j);
						//detected mouse only if button is enabled
						if (!b.isSeparator() && b.isEnabled()) {
							b.setHover(b.inBounds(e.getPoint(), b.getToken()));
							if (b.hasDropDown()) {
								b.setHoverTop(b.inBoundsPartOf(e.getPoint(), buttonPartialHeight, b.getToken()));
							}
						}
					}
				}
			}

			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			boolean found = false;
			if (QUICKBAR != null && e.getPoint().y <= quickbarHeight) {
	      for (int i = 0; i < QUICKBAR.getButtons().size(); i++) {
	        QuickButton b = QUICKBAR.getButtons().get(i);
          if (b.isSeparator()) continue;
          b.setPressed(false);
          // fire action if it is in bounds and token is equal and it is not disabled
          if (b.inBounds(e.getPoint(), b.getToken()) && b.isEnabled()) {
            b.setPressed(true);
            b.fireAction(new ActionEvent(RibbonBar.this, (int) AWTEvent.MOUSE_EVENT_MASK,
                "onClick"));
          }
	      }		    
			} else if (e.getPoint().y <= ribbonTabHeight+quickbarHeight) {

				for (int i = 0; i < TABS.size(); i++) {
					if (TABS.get(i).inBounds(e.getPoint(), TABS.get(i).getToken())) {
						found = true;
					}
				}
				if (found) {
					for (int i = 0; i < TABS.size(); i++) {
						TABS.get(i).setSelected(TABS.get(i).inBounds(e.getPoint(), TABS.get(i).getToken()));
					}
					minimized = false;
					toggle();
				}
			} else if (e.getPoint().y > ribbonTabHeight) {
				for (int t = 0; t < TABS.size(); t++) {

					Tab tab = TABS.get(t);
					if (tab.isSelected()) {
						for (int b = 0; b < TABS.get(t).getButtons().size(); b++) {
							Button but = TABS.get(t).getButtons().get(b);
							but.setPressed(false);
							// fire action if it is in bounds and token is equal and it is not disabled
							if (but.inBounds(e.getPoint(), but.getToken()) && but.isEnabled()) {
								if (!but.hasDropDown() || but.isHoverTop()) {
									but.fireAction(new ActionEvent(RibbonBar.this, (int) AWTEvent.MOUSE_EVENT_MASK,
											"onClick"));
								}
								if (but.hasDropDown() && !but.isHoverTop()) {
									POPUP_MENU.removeAll();
									for (int i = 0; i < but.getSubMenuList().size(); i++) {
										if (but.getSubMenuList().get(i) instanceof JMenuItem) {
											POPUP_MENU.add((JMenuItem)but.getSubMenuList().get(i));
										}
										if (but.getSubMenuList().get(i) instanceof JCheckBoxMenuItem) {
											POPUP_MENU.add((JCheckBoxMenuItem) but.getSubMenuList().get(i));
										}
										if (but.getSubMenuList().get(i) instanceof RibbonMenuItem) {
											RibbonMenuItem ri =	((RibbonMenuItem)but.getSubMenuList().get(i));
											int w = new JMenuItem(ri.getText()).getPreferredSize().width + 28 + ri.getIconTextGap();
											ri.setPreferredSize(new Dimension(w, 22)); //TODO maybe scaling issue
										}
									}
									
									POPUP_MENU.show(RibbonBar.this, but.getX(), but.getY() + but.getHeight());
									found = true;
								}
							}//in bounds button
						}//end iteration button of selected tab
					}// end case of tab is selected
				} //end tab iteration
			}

			if (toggle.isBound(e.getPoint())) {
				reminimized = !reminimized;
				if (reminimized) {
				minimized = !minimized;
				}
				toggle();
				
			} else {
				if (reminimized && !found) {
					minimized = true;
					toggle();
				}
			}
			
			repaint();
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getPoint().y > ribbonTabHeight) {
				for (int t = 0; t < TABS.size(); t++) {
					Tab tab = TABS.get(t);
					if (tab.isSelected()) {
						for (int b = 0; b < TABS.get(t).getButtons().size(); b++) {
							Button but = TABS.get(t).getButtons().get(b);
							//set pressed flag is it is in bounds and it is enabled
							if (but.isEnabled()) {
								but.setPressed(but.inBounds(e.getPoint(), but.getToken()));
							}
						}
					}
				}
			}
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			clearFlag();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

	};

	class RibbonDragGestureListener implements DragGestureListener {

		PagePane page = null;
		public RibbonDragGestureListener() { };

		public void setTarget(PagePane page) { this.page = page; }
		@Override
		public void dragGestureRecognized(DragGestureEvent event) {
      if (!page.isActive()) { return; }
			Point p = event.getDragOrigin();
			Button selection = findDragButton(p);
			if (selection != null) {
//				Cursor ghost = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				Cursor ghost = DragSource.DefaultMoveNoDrop;
				ImageIcon icon = selection.getDragImage();
				BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics g = (Graphics) img.getGraphics();
				icon.paintIcon(null, g, 0, 0);
				g.dispose();
				Transferable transferable = new Transferable() {

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[]{DataFlavor.stringFlavor};
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor flavor) {
						return DataFlavor.stringFlavor.equals(flavor);
					}

					@Override
					public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
						return selection.transfer_data;
					}
				};
//				Builder.logger.debug("startDrag");
				event.startDrag(ghost, (Image)img,
					new Point(-10,-16),
					transferable,
					new DragSourceListener() {

						@Override
						public void dragEnter(DragSourceDragEvent dsde) {
							DragSourceContext context = dsde.getDragSourceContext();
							context.setCursor(DragSource.DefaultMoveNoDrop);
						}

						@Override
						public void dragOver(DragSourceDragEvent dsde) {
							Point p = dsde.getLocation();
							SwingUtilities.convertPointFromScreen(p, page);
							DragSourceContext context = dsde.getDragSourceContext();
							if (Utils.testLocation(p.x, p.y)) {
								context.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//								Builder.logger.debug("--- not on TFT: "+p.toString());
							} else {
								context.setCursor(DragSource.DefaultMoveNoDrop);
//								Builder.logger.debug("--- not on TFT: "+p.toString());
							}
					}

						@Override
						public void dropActionChanged(DragSourceDragEvent dsde) { }

						@Override
						public void dragExit(DragSourceEvent dse) { }

						@Override
						public void dragDropEnd(DragSourceDropEvent dsde) { setCursor(Cursor.getDefaultCursor()); }

				});
			}
		}
	}

}
