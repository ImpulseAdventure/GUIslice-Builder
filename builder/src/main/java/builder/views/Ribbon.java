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
package builder.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.UIManager;

import builder.common.Utils;

import hu.csekme.RibbonMenu.Button;
import hu.csekme.RibbonMenu.QuickAccessBar;
import hu.csekme.RibbonMenu.QuickButton;
import hu.csekme.RibbonMenu.RibbonBar;
import hu.csekme.RibbonMenu.Tab;
import hu.csekme.RibbonMenu.ToggleButton;

public class Ribbon extends JPanel {
  private static final long serialVersionUID = 1L;

  private static RibbonBar ribbonBar;
  private static QuickAccessBar quickbar;

  private static Tab toolBox;

  /** The quick access buttons */
  public QuickButton q_new, q_open, q_close,q_save,
    q_code, q_exit, q_selection, q_copy_props, q_paste, 
    q_copy, q_cut, q_undo,q_redo,q_delete;
  
  /** The text elements */
  private static Button btn_text, btn_textbox, 
    btn_listbox, btn_txtinput, btn_numinput;

  /** The controls elements */
  private static Button btn_txtbtn, btn_imgbtn, btn_slider,
    btn_checkbox, btn_radiobtn, btn_spinner, btn_image,  
    btn_toggle, btn_seekbar, btn_progressbar;

  /** The gauge elements */
  private static Button btn_ringgauge, btn_radial, btn_ramp;

  /** The page elements */
  private static Button btn_page, btn_base_page, btn_popup;
  
  /** The misc elements */
  private static Button btn_box, btn_line, btn_graph, btn_group;
  
  /** The view buttons. */
  private static Button btn_zoom_in,btn_zoom_out,btn_zoom_reset;
  private static ToggleButton btn_show_grid,btn_snap_grid;

  private static Tab pageLayout;
  
  /** The alignment buttons. */
  private static Button btn_aligntop,btn_alignbottom, btn_aligncenter,
    btn_alignleft,btn_alignright,btn_alignhspace,
    btn_alignvspace,btn_alignwidth, btn_alignheight, btn_scale;

  static Ribbon instance;
  
  /**
   * getInstance() - get our Singleton Object.
   *
   * @return instance
   */
  public static synchronized Ribbon getInstance() {
    if (instance == null) {
      instance = new Ribbon();
    }
    return instance;
  }

  /**
   * Create the panel.
   */
  public Ribbon() {
    
    ribbonBar = RibbonBar.create();
    
    // Create quick access bar
    quickbar = RibbonBar.addQuickAccess();
    initQuickButtons();

    ribbonBar.disableCollapse();
    
    toolBox = RibbonBar.addTab("ToolBox");
    initText(toolBox);
    initControls(toolBox);
    initGauges(toolBox);
    initPages(toolBox);
    initMisc(toolBox);
    initView(toolBox);
    
    pageLayout = RibbonBar.addTab("Page Layout");
    initLayout(pageLayout);
    
    toolBox.setSelected(true);

   // ribbonBar.setPreferredSize(new Dimension(1045,135));
    setLayout(new BorderLayout());
    add(ribbonBar, BorderLayout.NORTH);
    
  }
  
  /**
   * Initializes the quick access bar buttons.
   */
  public void initQuickButtons() {
/*    
    q_new = quickbar.addButton("q_new");
    q_new.setImage(Utils.getIcon("resources/icons/file/new.png"));
    q_new.addToolTip("New Project");
    q_new.setActionCommand("new");
*/
    q_open = quickbar.addButton("q_open");
    q_open.setImage(Utils.getIcon("resources/icons/file/open.png"));
    q_open.addToolTip("Open Project");
    q_open.setActionCommand("open");

    q_close = quickbar.addButton("q_close");
    q_close.setImage(Utils.getIcon("resources/icons/file/close.png"));
    q_close.addToolTip("Close Project");
    q_close.setActionCommand("close");

    q_save = quickbar.addButton("q_save");
    q_save.setImage(Utils.getIcon("resources/icons/file/save.png"));
    q_save.addToolTip("Save Project");
    q_save.setActionCommand("save");

    q_code = quickbar.addButton("q_code");
    q_code.setImage(Utils.getIcon("resources/icons/file/export.png"));
    q_code.addToolTip("Generate Code");
    q_code.setActionCommand("code");
    
    quickbar.addSeperator();

    q_cut = quickbar.addButton("q_cut");
    q_cut.setImage(Utils.getIcon("resources/icons/edit/cut.png"));
    q_cut.addToolTip("cut");
    q_cut.setActionCommand("cut");
    
    q_copy = quickbar.addButton("q_copy");
    q_copy.setImage(Utils.getIcon("resources/icons/edit/copy.png"));
    q_copy.addToolTip("Copy");
    q_copy.setActionCommand("copy");
    
    q_paste = quickbar.addButton("q_paste");
    q_paste.setImage(Utils.getIcon("resources/icons/edit/paste.png"));
    q_paste.addToolTip("Paste");
    q_paste.setActionCommand("paste");

    quickbar.addSeperator();

    q_copy_props = quickbar.addButton("q_copy_props");
    q_copy_props.setImage(Utils.getIcon("resources/icons/edit/copy_props.png"));
    q_copy_props.addToolTip("Copy Properties");
    q_copy_props.setActionCommand("copyprops");
        
    quickbar.addSeperator();

    q_undo = quickbar.addButton("q_undo");
    q_undo.setImage(Utils.getIcon("resources/icons/edit/undo.png"));
    q_undo.setDisabledImage(Utils.getIcon("resources/icons/edit/disable_undo.png"));
    q_undo.setEnabled(false);
    q_undo.addToolTip("Undo Actions");
    q_undo.setActionCommand("undo");

    q_redo = quickbar.addButton("q_redo");
    q_redo.setImage(Utils.getIcon("resources/icons/edit/redo.png"));
    q_redo.setDisabledImage(Utils.getIcon("resources/icons/edit/disable_redo.png"));
    q_redo.setEnabled(false);
    q_redo.addToolTip("Redo Actions");
    q_redo.setActionCommand("redo");
    
    quickbar.addSeperator();

    q_selection = quickbar.addButton("q_selection");
    q_selection.setImage(Utils.getIcon("resources/icons/layout/selection.png"));
    q_selection.addToolTip("Rectangular Selection");
    q_selection.setActionCommand("selection");
    
    quickbar.addSeperator();

    q_delete = quickbar.addButton("q_delete");
    q_delete.setImage(Utils.getIcon("resources/icons/edit/delete.png"));
    q_delete.addToolTip("Delete Widget");
    q_delete.setActionCommand("delete");
    
    quickbar.addSeperator();

    q_exit = quickbar.addButton("q_exit");
    q_exit.setImage(Utils.getIcon("resources/icons/file/logout.png"));
    q_exit.addToolTip("Exit Builder");
    q_exit.setActionCommand("exit");

  }

  /**
   * Initializes the Text elements.
   */
  public void initText(Tab band) {
    band.setGroupName("Text");
    btn_text= band.addButton("   Text   ");
    btn_text.setImage(Utils.getIcon(
        "resources/icons/text/label_32x.png"));
    btn_text.setActionCommand("text");
    btn_text.addToolTip("Add display-only text element.");

    btn_txtinput = band.addSlimButton("TextInput");
    btn_txtinput.setImage(Utils.getIcon(
        "resources/icons/text/textinput_32x.png"));
    btn_txtinput.setActionCommand("textinput");
    btn_txtinput.addToolTip("Accept text input\nfrom a virtual keypad.");
    
    btn_numinput = band.addSlimButton("NumberInput");
    btn_numinput.setImage(Utils.getIcon(
        "resources/icons/text/numinput_32x.png"));
    btn_numinput.setActionCommand("numinput");
    btn_numinput.addToolTip("Accept numberic input\nfrom a virtual keypad.");
    
    btn_listbox = band.addSlimButton("ListBox");
    btn_listbox.setImage(Utils.getIcon(
        "resources/icons/controls/listbox_32x.png"));
    btn_listbox.setActionCommand("listbox");
    btn_listbox.addToolTip("List of items");
    
    btn_textbox = band.addSlimButton("TextBox");
    btn_textbox.setImage(Utils.getIcon(
        "resources/icons/text/textbox_32x.png"));
    btn_textbox.setActionCommand("textbox");
    btn_textbox.addToolTip("Scrolling multi-line text");
    
    band.addSeperator();
  }
  
  /**
   * Initializes the Control elements.
   */
  public void initControls(Tab band) {
    band.setGroupName("Controls");
    btn_txtbtn = band.addButton("Text\nButton");
    btn_txtbtn.setImage(Utils.getIcon(
        "resources/icons/controls/button_32x.png"));
    btn_txtbtn.setActionCommand("textbutton");
    btn_txtbtn.addToolTip("Button with Text.");

    btn_toggle = band.addSlimButton("Toggle Button");
    btn_toggle.setImage(Utils.getIcon(
        "resources/icons/controls/togglebtn.png"));
    btn_toggle.setActionCommand("toggle");
    btn_toggle.addToolTip("Add Toggle Button element.");
      
    btn_checkbox = band.addSlimButton("Checkbox");
    btn_checkbox.setImage(Utils.getIcon(
         "resources/icons/controls/checkbox_32x.png"));
    btn_checkbox.setActionCommand("checkbox");
    btn_checkbox.addToolTip("Add Checkbox element.");

    btn_radiobtn = band.addSlimButton("Radio Button");
    btn_radiobtn.setImage(Utils.getIcon(
         "resources/icons/controls/radiobutton_32x.png"));
    btn_radiobtn.setActionCommand("radiobutton");
    btn_radiobtn.addToolTip("Add Radio Button element.");

    btn_imgbtn = band.addSlimButton("Image Button");
    btn_imgbtn.setImage(Utils.getIcon(
        "resources/icons/controls/imgbutton_32x.png"));
    btn_imgbtn.setActionCommand("imagebutton");
    btn_imgbtn.addToolTip("Button with BMP image.");
            
    btn_seekbar = band.addSlimButton("Seekbar");
    btn_seekbar.setImage(Utils.getIcon(
        "resources/icons/controls/seekbar.png"));
    btn_seekbar.setActionCommand("seekbar");
    btn_seekbar.addToolTip("Seekbar slider element.");
        
    btn_spinner = band.addSlimButton("Spinner");
    btn_spinner.setImage(Utils.getIcon(
        "resources/icons/controls/spinner_32x.png"));
    btn_spinner.setActionCommand("spinner");
    btn_spinner.addToolTip("Numeric up-down graphical control.");

    btn_slider = band.addSlimButton("Slider");
    btn_slider.setImage(Utils.getIcon(
        "resources/icons/controls/slider_32x.png"));
    btn_slider.setActionCommand("slider");
    btn_slider.addToolTip("Slider presents values along a line.");

    btn_image = band.addSlimButton("Image");
    btn_image.setImage(Utils.getIcon(
        "resources/icons/controls/image_32x.png"));
    btn_image.setActionCommand("image");
    btn_image.addToolTip("Add image element.");

    btn_progressbar = band.addSlimButton("Progress Bar");
    btn_progressbar.setImage(Utils.getIcon(
        "resources/icons/gauges/progressbar_32x.png"));
    btn_progressbar.setActionCommand("progressbar");
    btn_progressbar.addToolTip("Indicate progress of a\nlengthy operation as a bar.");

    band.addSeperator();
  }
  
  /**
   * Initializes the Gauge elements.
   */
  public void initGauges(Tab band) {
    band.setGroupName("Gauges");
    btn_ringgauge = band.addSlimButton("Ring Gauge");
    btn_ringgauge.setImage(Utils.getIcon(
        "resources/icons/gauges/ringgauge_32x.png"));
    btn_ringgauge.setActionCommand("ringgauge");
    btn_ringgauge.addToolTip("Add Ring Gauge element.");
   
    btn_radial = band.addSlimButton("Radial Gauge");
    btn_radial.setImage(Utils.getIcon(
      "resources/icons/gauges/radial_32x.png"));
    btn_radial.setActionCommand("radial");
    btn_radial.addToolTip("Indicate progress of a\nlengthy operation as a dial.");
    
    btn_ramp = band.addSlimButton("Ramp Gauge");
    btn_ramp.setImage(Utils.getIcon(
      "resources/icons/gauges/ramp_32x.png"));
    btn_ramp.setActionCommand("ramp");
    btn_ramp.addToolTip("Indicate the progress of a\nlengthy operation as a ramp.");
    
    band.addSeperator();
  }

  /**
   * Initializes the Page elements.
   */
  public void initPages(Tab band) {
    band.setGroupName("Pages");
    btn_page = band.addSlimButton("Page");
    btn_page.setImage(Utils.getIcon(
      "resources/icons/page/page_32x.png"));
    btn_page.setActionCommand("page");
    btn_page.addToolTip("New Page menu.");
    
    btn_base_page = band.addSlimButton("Base Page");
    btn_base_page.setImage(Utils.getIcon(
      "resources/icons/page/basepage_32x.png"));
    btn_base_page.setActionCommand("basepage");
    btn_base_page.addToolTip("Page overlay placed on top of all pages.");

    btn_popup = band.addSlimButton("Popup Page");
    btn_popup.setImage(Utils.getIcon(
      "resources/icons/page/popup_32x.png"));
    btn_popup.addToolTip("Popup Page Dialog.");
    btn_popup.setActionCommand("popup");

    band.addSeperator();
  }

  /**
   * Initializes the Miscellaneous elements.
   */
  public void initMisc(Tab band) {
    band.setGroupName("Misc");
    btn_box = band.addSlimButton("Box");
    btn_box.setImage(Utils.getIcon(
      "resources/icons/shapes/box_32x.png"));
    btn_box.setActionCommand("box");
    btn_box.addToolTip("Rectangle box element.");
      
    btn_line = band.addSlimButton("Line");
    btn_line.setImage(Utils.getIcon(
        "resources/icons/shapes/line_32x.png"));
    btn_line.setActionCommand("line");
    btn_line.addToolTip("Horizontal or Vertical line element.");
    
    btn_graph = band.addSlimButton("Graph");
    btn_graph.setImage(Utils.getIcon(
        "resources/icons/controls/graph_32x.png"));
    btn_graph.setActionCommand("graph");
    btn_graph.addToolTip("Graph element.");

    btn_group = band.addSlimButton("Group");
    btn_group.setImage(Utils.getIcon(
        "resources/icons/controls/group.png"));
    btn_group.addToolTip("Assign GroupID to RadioButtons");
    btn_group.setActionCommand("group");
    btn_group.setDisabledImage(Utils.getIcon(
        "resources/icons/controls/disable_group.png"));
    btn_group.setEnabled(false);

    band.addSeperator();
  }

  /**
   * Initialize the View Buttons.
   */
  public void initView(Tab band) {
    band.setGroupName("View\\Editor");

    btn_zoom_in = band.addSlimButton("Zoom In");
    btn_zoom_in.setImage(Utils.getIcon(
        "resources/icons/view/zoom_in.png"));
    btn_zoom_in.setDisabledImage(Utils.getIcon(
        "resources/icons/view/disable_zoom_in.png"));
    btn_zoom_in.addToolTip("Make display larger.");
    btn_zoom_in.setActionCommand("zoomin");

    btn_zoom_out = band.addSlimButton("Zoom Out");
    btn_zoom_out.setImage(Utils.getIcon(
        "resources/icons/view/zoom_out.png"));
    btn_zoom_out.setDisabledImage(Utils.getIcon(
        "resources/icons/view/disable_zoom_out.png"));
    btn_zoom_out.addToolTip("Make display smaller.");
    btn_zoom_out.setActionCommand("zoomout");
    btn_zoom_out.setEnabled(false);

    btn_zoom_reset = band.addSlimButton("Zoom Reset");
    btn_zoom_reset.setImage(Utils.getIcon(
        "resources/icons/view/zoom_reset.png"));
    btn_zoom_reset.setDisabledImage(Utils.getIcon(
        "resources/icons/view/disable_zoom_reset.png"));
    btn_zoom_reset.addToolTip("Reset display to 100%.");
    btn_zoom_reset.setActionCommand("zoomreset");
    btn_zoom_reset.setEnabled(false);

    btn_show_grid = band.addSlimToggleButton("Show grid");
    btn_show_grid.setImage(Utils.getIcon(
      "resources/icons/view/show_grid_off.png"));
    btn_show_grid.setOnImage(Utils.getIcon(
      "resources/icons/view/show_grid_on.png"));
    btn_show_grid.addToolTip("Toggle Grid ON/OFF");
    btn_show_grid.setActionCommand("showgrid");

    btn_snap_grid = band.addSlimToggleButton("Snap to grid");
    btn_snap_grid.setImage(Utils.getIcon(
      "resources/icons/view/snap_to_grid_off.png"));
    btn_snap_grid.setOnImage(Utils.getIcon(
      "resources/icons/view/snap_to_grid_on.png"));
    btn_snap_grid.addToolTip("Toggle Snap to Grid ON/OFF");
    btn_snap_grid.setActionCommand("snaptogrid");

    band.addSeperator();
  }
    
  /**
   * Initializes the layout buttons.
   */
  public void initLayout(Tab band) {
    band.setGroupName("Layout");
    btn_aligntop = band.addButton("Align\nTop");
    btn_aligntop.setImage(Utils.getIcon(
        "resources/icons/layout/align_top.png"));
    btn_aligntop.setActionCommand("aligntop");
    btn_aligntop.addToolTip("Align Elements to Top Most Element");
    
    btn_alignbottom = band.addButton("Align\nBottom");
    btn_alignbottom.setImage(Utils.getIcon(
        "resources/icons/layout/align_bottom.png"));
    btn_alignbottom.setActionCommand("alignbottom");
    btn_alignbottom.addToolTip("Align Elements to Bottom Most Element");
    
    btn_aligncenter = band.addButton("Center");
    btn_aligncenter.setImage(Utils.getIcon(
        "resources/icons/layout/align_center.png"));
    btn_aligncenter.setActionCommand("aligncenter");
    btn_aligncenter.addToolTip("Center Elements Horizontally along X axis");
    
    btn_alignleft = band.addButton("Align\nLeft");
    btn_alignleft.setImage(Utils.getIcon(
        "resources/icons/layout/align_left.png"));
    btn_alignleft.setActionCommand("alignleft");
    btn_alignleft.addToolTip("Align Horizontally by Leftmost Element");
    
    btn_alignright = band.addButton("Align\nRight");
    btn_alignright.setImage(Utils.getIcon(
        "resources/icons/layout/align_right.png"));
    btn_alignright.setActionCommand("alignright");
    btn_alignright.addToolTip("Align Horizontally by Rightmost Element");

    btn_alignhspace = band.addButton("Align\nHorizontal");
    btn_alignhspace.setImage(Utils.getIcon(
        "resources/icons/layout/align_hspacing.png"));
    btn_alignhspace.setActionCommand("alignhspacing");
    btn_alignhspace.addToolTip("Align the Horizontal Spacing between Elements");
    
    btn_alignvspace = band.addButton("Align\nVertical");
    btn_alignvspace.setImage(Utils.getIcon(
        "resources/icons/layout/align_vspacing.png"));
    btn_alignvspace.setActionCommand("alignvspacing");
    btn_alignvspace.addToolTip("Align the Vertical Spacing between Elements");
    
    btn_alignwidth = band.addButton("Align\nWidth");
    btn_alignwidth.setImage(Utils.getIcon(
        "resources/icons/layout/align_width.png"));
    btn_alignwidth.setActionCommand("alignwidth");
    btn_alignwidth.addToolTip("Align Elements to a Uniform Width");
    
    btn_alignheight = band.addButton("Align\nHeight");
    btn_alignheight.setImage(Utils.getIcon(
        "resources/icons/layout/align_height.png"));
    btn_alignheight.setActionCommand("alignheight");
    btn_alignheight.addToolTip("Align Elements to a Uniform Element's Height");
    
    btn_scale = band.addButton("scale");
    btn_scale.setImage(Utils.getIcon(
        "resources/icons/layout/scale.png"));
    btn_scale.setActionCommand("scale");
    btn_scale.addToolTip("Scale elements to a new screen size");
    
    band.addSeperator();
  }

  /**
   * Sets the ribbon colors.
   */
  public void setRibbonColors() {
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_BACKGROUND, UIManager.getColor("Button.background"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_CONTAINER_BACKGROUND,  UIManager.getColor("TabbedPane.background"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_CONTAINER_STRIP,  UIManager.getColor("TabbedPane.underlineColor"));

    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_BACKGROUND,  UIManager.getColor("TabbedPane.background"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_HOVER_BACKGROUND,  UIManager.getColor("TabbedPane.buttonHoverBackground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_SELECTED_BACKGROUND,  UIManager.getColor("MenuItem.selectionBackground"));
//    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_FOREGROUND,  UIManager.getColor("TabbedPane.foreground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_FOREGROUND,  UIManager.getColor("Button.foreground"));
//    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_HOVER_FOREGROUND,  UIManager.getColor("TabbedPane.hoverColor"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_HOVER_FOREGROUND,  UIManager.getColor("Button.foreground"));

//    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_SELECTED_FOREGROUND,  UIManager.getColor("MenuItem.selectionForeground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_SELECTED_FOREGROUND,  UIManager.getColor("MenuItem.selectionForeground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_TAB_SELECTED_STRIP_BACKGROUND,  UIManager.getColor("MenuItem.underlineSelectionBackground"));

    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_BUTTON_BACKGROUND,  UIManager.getColor("Button.background"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_BUTTON_HOVER_BACKGROUND,  UIManager.getColor("Button.hoverBackground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_BUTTON_HOVER_BORDER_COLOR, UIManager.getColor("Button.default.hoverBorderColor"));

    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_SEPARATOR_FOREGROUND,  UIManager.getColor("TabbedPane.darkShadow"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_BUTTON_FOREGROUND,  UIManager.getColor("Button.foreground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_GROUP_COLOR,  UIManager.getColor("TabbedPane.foreground"));
    
//    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_GROUP_TEXT_COLOR,  UIManager.getColor("Button.foreground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_SHADOW_DARK,  UIManager.getColor("TabbedPane.darkShadow"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_SHADOW_LIGHT,  UIManager.getColor("TabbedPane.shadow"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_BUTTON_PRESSED_BACKGROUND,  UIManager.getColor("TabbedPane.buttonPressedBackground"));
    
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_MENUITEM_HOVER,UIManager.getColor("MenuBar.hoverBackground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_MENUITEM_PRESSED,UIManager.getColor("MenuItem.selectionForeground"));
    RibbonBar.putColor(RibbonBar.COLOR_RIBBON_MENUITEM_BACKGROUND,UIManager.getColor("MenuItem.background"));
  }
 
  /**
   * getPreferredSize
   *
   * @see javax.swing.JComponent#getPreferredSize()
   */
//  @Override
//  public Dimension getPreferredSize() {
//     return new Dimension(1045,200);
//  }
  
  /**
   * Adds the listeners.
   *
   * @param al
   *          the object that implements ActionListener
   */
  public void addListeners(ActionListener al)
  {
//    q_new.addActionListener(al);
    q_open.addActionListener(al);
    q_save.addActionListener(al);
    q_close.addActionListener(al);

    q_code.addActionListener(al);
    q_exit.addActionListener(al);
 
    q_undo.addActionListener(al);
    q_redo.addActionListener(al);
    q_delete.addActionListener(al);
    q_selection.addActionListener(al);
    q_paste.addActionListener(al);
    q_copy.addActionListener(al);
    q_copy_props.addActionListener(al);
    q_cut.addActionListener(al);
    
    btn_text.addActionListener(al); 
    btn_textbox.addActionListener(al); 
    btn_listbox.addActionListener(al);
    btn_txtinput.addActionListener(al); 
    btn_numinput.addActionListener(al);

    btn_txtbtn.addActionListener(al);
    btn_imgbtn.addActionListener(al); 
    btn_slider.addActionListener(al);
    btn_checkbox.addActionListener(al);
    btn_radiobtn.addActionListener(al);
    btn_spinner.addActionListener(al);
    btn_image.addActionListener(al); 
    btn_toggle.addActionListener(al); 
    btn_seekbar.addActionListener(al); 
    btn_progressbar.addActionListener(al);
    
    btn_ringgauge.addActionListener(al); 
    btn_radial.addActionListener(al); 
    btn_ramp.addActionListener(al);

    btn_page.addActionListener(al);
    btn_base_page.addActionListener(al);
    btn_popup.addActionListener(al);
    
    btn_box.addActionListener(al); 
    btn_line.addActionListener(al); 
    btn_graph.addActionListener(al); 
    btn_group.addActionListener(al);

    btn_show_grid.addActionListener(al);
    btn_snap_grid.addActionListener(al);
    btn_zoom_in.addActionListener(al);
    btn_zoom_out.addActionListener(al);
    btn_zoom_reset.addActionListener(al);

    btn_aligntop.addActionListener(al);
    btn_alignbottom.addActionListener(al);
    btn_aligncenter.addActionListener(al);
    btn_alignleft.addActionListener(al);
    btn_alignright.addActionListener(al);
    btn_alignhspace.addActionListener(al);
    btn_alignvspace.addActionListener(al);
    btn_alignwidth.addActionListener(al);
    btn_alignheight.addActionListener(al);
    btn_scale.addActionListener(al);

//    btn_selection.addActionListener(al);

  }
  
  /**
   * Enable grouping of radiobuttons
   * @param bEnable is true if enable, false otherwise
   */
  public static void enableGroup(boolean bEnable) {
    btn_group.setEnabled(bEnable);
  }
  
  public void enableZoom(boolean bEnable) {
    btn_zoom_out.setEnabled(bEnable);
  }

  public void enableZoomReset(boolean bEnable) {
    btn_zoom_reset.setEnabled(bEnable);
  }  
  
  public void enableShowGrid(boolean bEnable) {
    btn_show_grid.setToggle(bEnable);
  }  
  
  public void enableSnapGrid(boolean bEnable) {
    btn_snap_grid.setToggle(bEnable);
  }
  
  /**
   * Enable grouping of radiobuttons
   * @param selectedGroupCnt is the number of radiobuttons selected on the page
   */
  public void setEditButtons(int selectedGroupCnt) {
    if (selectedGroupCnt > 1) {
      btn_group.setEnabled(true);
    } else {
      btn_group.setEnabled(false);
    }
  }

  public void enableUndo(boolean bEnable) {
    q_undo.setEnabled(bEnable);
  }
  
  public void enableRedo(boolean bEnable) {
    q_redo.setEnabled(bEnable);
  }
  
}
