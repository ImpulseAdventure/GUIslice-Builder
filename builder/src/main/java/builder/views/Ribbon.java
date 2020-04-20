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
package builder.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
//import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
//import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
//import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import static org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority.MEDIUM;
import static org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority.TOP;


import builder.common.CommonUtils;
import builder.events.MsgBoard;

/**
 * The Class ToolBar.
 * 
 * @author Paul Conti
 * 
 */
public class Ribbon extends JRibbonFrame {
  private static final long serialVersionUID = 1L;

  /** The instance. */
  private static Ribbon instance = null;
  
  private JRibbonBand layoutBand;
  private JRibbonBand editBand;
  private JRibbonBand viewBand;
  private JRibbonBand pagesBand;
  private JRibbonBand controlsBand;
  private JRibbonBand textBand;
  private JRibbonBand gaugesBand;
  private JRibbonBand shapesBand;
  private RibbonTask  pageLayoutTask;
  private RibbonTask  toolboxTask;
  
  // The listener for ribbon message events */
  private RibbonListener ribbonListener;
  
  private MsgBoard mb;
  
  private CommonUtils cu = null;
  
  /** The btn files. */
  public JCommandButton btn_new, btn_open, btn_close,btn_save,btn_saveas,
    btn_code, btn_exit;
  
  /** The undo, redo, and delete buttons. */
  public JCommandButton btn_undo,btn_redo,btn_delete;
  
  /** The edit buttons. */
  public JCommandButton btn_copy,btn_cut,btn_paste,btn_copy_props;
  public JCommandButton mini_copy,mini_cut,mini_paste;
  
  /** The alignment buttons. */
  public JCommandButton btn_aligntop,btn_alignbottom, btn_aligncenter,
    btn_alignleft,btn_alignright,btn_alignhspace,
    btn_alignvspace,btn_alignwidth, btn_alignheight, btn_selection;
  
  /** The view buttons. */
  public JCommandButton btn_grid,btn_zoom_in,btn_zoom_out;
  public JCommandButton mini_grid,mini_zoom_in,mini_zoom_out;
  
  /** The page elements */
  public JCommandButton btn_page, btn_base_page, btn_popup;
  
  /** The text elements */
  public JCommandButton btn_text, btn_textbox, 
    btn_listbox, btn_txtinput, btn_numinput;
  
  /** The controls elements */
  public JCommandButton btn_txtbtn, btn_imgbtn, btn_slider,
    btn_checkbox, btn_radiobtn, btn_spinner, btn_image, btn_graph, btn_group;
  
  /** The gauge elements */
  public JCommandButton btn_ringgauge, btn_progressbar, btn_radial, 
    btn_ramp;
  
  /** The shapes elements */
  public JCommandButton btn_box, btn_line;
  
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
   * Instantiates a new tool bar.
   */
  public Ribbon() {
    cu = CommonUtils.getInstance();
    configureApp();
    configureTaskBar();
    configureRibbon();
    // create a listener for ribbon message events
    ribbonListener = new RibbonListener();
    mb = MsgBoard.getInstance();
  }
  
  /**
   * setJmenuBar
   *  The original JRibbonFrame will not allow this override
   *  but my modified version will.
   */
  @Override
  public void setJMenuBar(JMenuBar bar) {
    super.setJMenuBar(bar);
  }
  
  /**
   * getRibbonListener
   * 
   * @return the RibbonListener object
   */
  public RibbonListener getRibbonListener() {
     return ribbonListener;
  }
  
 	public void configureRibbon() {

    pagesBand = createBand("Pages");
    textBand = createBand("Text");
    controlsBand = createBand("Controls");
    gaugesBand = createBand("Gauges");
    shapesBand = createBand("Misc");

    initPages(pagesBand);
    initControls(controlsBand);
    initText(textBand);
    initGauges(gaugesBand);
    initShapes(shapesBand);

    toolboxTask = new RibbonTask("ToolBox", 
      textBand, controlsBand, gaugesBand,pagesBand,shapesBand);

    editBand = createBand("Edit");
    layoutBand = createBand("Layout");
    viewBand = createBand("View");

    initEdit(editBand);
    initLayout(layoutBand);
    initView(viewBand);

		pageLayoutTask = new RibbonTask("PageLayout", 
		    viewBand, layoutBand, editBand);
    
    this.getRibbon().addTask(toolboxTask);
    this.getRibbon().addTask(pageLayoutTask);

  }

  public void configureApp() {
    this.getRibbon().setApplicationMenu(null);
    this.setApplicationIcon(
        cu.getResizableIcon("resources/icons/guislicebuilder.png"));
  }
 
  protected void configureTaskBar() {
    // taskbar components
    btn_open = new JCommandButton("",
        cu.getResizableIcon("resources/icons/file/open.png"));
    btn_open
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_open.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "open");
      }
    });
//    btn_open.setToolTipText("Open Project File");
    this.getRibbon().addTaskbarComponent(btn_open);

    btn_close = new JCommandButton("",
        cu.getResizableIcon("resources/icons/file/close.png"));
    btn_close
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_close.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "close");
      }
    });
//    btn_close.setToolTipText("Close Project File");
    this.getRibbon().addTaskbarComponent(btn_close);

    btn_save = new JCommandButton("",
        cu.getResizableIcon("resources/icons/file/save.png"));
    btn_save
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_save.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "save");
      }
    });
    btn_save.setActionRichTooltip(new RichTooltip(
        "Save", 
        "Save project file"));
    this.getRibbon().addTaskbarComponent(btn_save);

    btn_saveas = new JCommandButton("",
        cu.getResizableIcon("resources/icons/file/saveas.png"));
    btn_saveas
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_saveas.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "saveas");
      }
    });
    btn_saveas.setActionRichTooltip(new RichTooltip(
        "Save As", 
        "Save As renaming project file"));
    this.getRibbon().addTaskbarComponent(btn_saveas);

    this.getRibbon().addTaskbarComponent(
        new JSeparator(JSeparator.VERTICAL));

    btn_undo = new JCommandButton("",
        cu.getResizableIcon("resources/icons/edit/undo.png"));
    btn_undo
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_undo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "undo");
      }
    });
    btn_undo.setActionRichTooltip(new RichTooltip(
        "Undo", 
        "Undo edits to your project file"));
    btn_undo.setDisabledIcon(
        cu.getResizableIcon("resources/icons/edit/disable_undo.png"));
//    btn_undo.setEnabled(false);;
    this.getRibbon().addTaskbarComponent(btn_undo);

    btn_redo = new JCommandButton("",
        cu.getResizableIcon("resources/icons/edit/redo.png"));
    btn_redo
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_redo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "redo");
      }
    });
    btn_redo.setActionRichTooltip(new RichTooltip(
        "Redo", 
        "Re-apply edits to your project file"));
    btn_redo.setDisabledIcon(
        cu.getResizableIcon("resources/icons/edit/disable_redo.png"));
//    btn_redo.setEnabled(false);;
    this.getRibbon().addTaskbarComponent(btn_redo);

    this.getRibbon().addTaskbarComponent(
        new JSeparator(JSeparator.VERTICAL));
    
    mini_paste = new JCommandButton("Paste",
        cu.getResizableIcon("resources/icons/edit/paste.png"));
    mini_paste.setActionRichTooltip(new RichTooltip(
        "Paste",
        "Paste an element from the clipboard."));
    mini_paste.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "paste");
        }
      });
    this.getRibbon().addTaskbarComponent(mini_paste);
      
    mini_copy = new JCommandButton("Copy",
        cu.getResizableIcon("resources/icons/edit/copy.png"));
    mini_copy.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "copy");
        }
      });
    mini_copy.setActionRichTooltip(new RichTooltip(
          "Copy",
          "Copy element selections to clipboard."));
    mini_copy.setEnabled(true);
    this.getRibbon().addTaskbarComponent(mini_copy);

    mini_cut = new JCommandButton("Cut",
        cu.getResizableIcon("resources/icons/edit/cut.png"));
    mini_cut.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "cut");
        }
      });
    mini_cut.setActionRichTooltip(new RichTooltip(
          "Cut",
          "Cut element selections from page."));
    this.getRibbon().addTaskbarComponent(mini_cut);

      btn_delete = new JCommandButton("",
        cu.getResizableIcon("resources/icons/edit/delete.png"));
      btn_delete
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
      btn_delete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "Delete");
      }
    });
    btn_delete.setActionRichTooltip(new RichTooltip(
        "Delete", 
        "Delete selected element(s) from your project file."));
    this.getRibbon().addTaskbarComponent(btn_delete);

    this.getRibbon().addTaskbarComponent(
        new JSeparator(JSeparator.VERTICAL));

    mini_grid = new JCommandButton("Grid",
      cu.getResizableIcon("resources/icons/view/grid.png"));
    mini_grid
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    mini_grid.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "grid");
      }
    });
    mini_grid.setActionRichTooltip(new RichTooltip(
      "Grid",
      "Toggle Grid ON/OFF"));
    this.getRibbon().addTaskbarComponent(mini_grid);
      
    mini_zoom_in = new JCommandButton("Zoom In",
      cu.getResizableIcon("resources/icons/view/zoom_in.png"));
    mini_zoom_in
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    mini_zoom_in.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "zoomin");
      }
    });
    mini_zoom_in.setDisabledIcon(
        cu.getResizableIcon("resources/icons/view/disable_zoom_in.png"));
    mini_zoom_in.setActionRichTooltip(new RichTooltip(
        "Zoom In",
        "Zoom In the TFT Simulation Panel"));
    this.getRibbon().addTaskbarComponent(mini_zoom_in);

    mini_zoom_out = new JCommandButton("Zoom Out",
      cu.getResizableIcon("resources/icons/view/zoom_out.png"));
    mini_zoom_out.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "zoomout");
      }
    });
    mini_zoom_out.setActionRichTooltip(new RichTooltip(
        "Zoom Out",
        "Zoom Out the TFT Simulation Panel"));
    mini_zoom_out
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    mini_zoom_out.setDisabledIcon(
        cu.getResizableIcon("resources/icons/view/disable_zoom_out.png"));
    mini_zoom_out.setEnabled(false);
    this.getRibbon().addTaskbarComponent(mini_zoom_out);

    this.getRibbon().addTaskbarComponent(
        new JSeparator(JSeparator.VERTICAL));

    btn_code = new JCommandButton("",
        cu.getResizableIcon("resources/icons/file/export.png"));
    btn_code
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_code.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "code");
      }
    });
    btn_code.setActionRichTooltip(new RichTooltip(
        "Code Generation", 
        "Create code output file"));
    this.getRibbon().addTaskbarComponent(btn_code);

    this.getRibbon().addTaskbarComponent(
        new JSeparator(JSeparator.VERTICAL));

    btn_exit = new JCommandButton("",
        cu.getResizableIcon("resources/icons/file/logout.png"));
    btn_exit
        .setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
    btn_exit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "exit");
      }
    });
    btn_exit.setActionRichTooltip(new RichTooltip(
        "Exit",
        "Exit Program"));
    this.getRibbon().addTaskbarComponent(btn_exit);

  }

  /** 
   * createBand
   */
  public JRibbonBand createBand(String strTitle) {
    JRibbonBand band = new JRibbonBand(strTitle, null);
    List<RibbonBandResizePolicy> policies = new ArrayList<RibbonBandResizePolicy>();
    policies.add(new CoreRibbonResizePolicies.None(band.getControlPanel()));
    policies.add(new CoreRibbonResizePolicies.Mirror(band.getControlPanel()));
    policies.add(new CoreRibbonResizePolicies.Mid2Low(band.getControlPanel()));
    policies.add(new CoreRibbonResizePolicies.High2Low(band.getControlPanel()));
//    policies.add(new IconRibbonBandResizePolicy(band.getControlPanel()));
    band.setResizePolicies(policies);
    return band;
  }
  
  /**
   * Initializes the cut, copy, and paste buttons.
   */
  public void initEdit(JRibbonBand band) {
    btn_paste = new JCommandButton("Paste",
      cu.getResizableIcon("resources/icons/edit/paste.png"));
    btn_paste.setActionRichTooltip(new RichTooltip(
      "Paste",
      "Paste element(s) from the clipboard."));
    btn_paste.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "paste");
      }
    });
//    btn_paste.setDisabledIcon(
//        cu.getResizableIcon("resources/icons/edit/disable_paste.png"));
//    btn_paste.setEnabled(false);
    band.addCommandButton(btn_paste, TOP);
    
    btn_copy = new JCommandButton("Copy",
      cu.getResizableIcon("resources/icons/edit/copy.png"));
    btn_copy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "copy");
      }
    });
    btn_copy.setActionRichTooltip(new RichTooltip(
        "Copy",
        "Copy element selections to clipboard."));
    band.addCommandButton(btn_copy, MEDIUM);

    btn_cut = new JCommandButton("Cut",
      cu.getResizableIcon("resources/icons/edit/cut.png"));
    btn_cut.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "cut");
      }
    });
    btn_cut.setActionRichTooltip(new RichTooltip(
        "Cut",
        "Cut element selections from page."));
    band.addCommandButton(btn_cut, MEDIUM);

  }

  /**
   * Initializes the layout buttons.
   */
  public void initLayout(JRibbonBand band) {
    btn_aligntop = new JCommandButton("Align Top",
      cu.getResizableIcon("resources/icons/layout/align_top.png"));
    btn_aligntop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "aligntop");
      }
    });
    btn_aligntop.setDisabledIcon(
        cu.getResizableIcon("resources/icons/layout/disable_top.png"));
    btn_aligntop.setActionRichTooltip(new RichTooltip(
        "Align Top",
        "Align Elements to Top Most Element"));
    band.addCommandButton(btn_aligntop, TOP);    
    
    btn_alignbottom = new JCommandButton("Align Bottom",
      cu.getResizableIcon("resources/icons/layout/align_bottom.png"));
    btn_alignbottom.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "alignbottom");
      }
    });
    btn_alignbottom.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_bottom.png"));
    btn_alignbottom.setActionRichTooltip(new RichTooltip(
        "Align Bottom",
        "Align Elements to Bottom Most Element"));
    band.addCommandButton(btn_alignbottom, TOP);    
    
    btn_aligncenter = new JCommandButton("Center",
      cu.getResizableIcon("resources/icons/layout/align_center.png"));
    btn_aligncenter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "aligncenter");
      }
    });
    btn_aligncenter.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_center.png"));
    btn_aligncenter.setActionRichTooltip(new RichTooltip(
        "Center",
        "Center Elements Horizontally along X axis"));
    band.addCommandButton(btn_aligncenter, TOP);    
    
    btn_alignleft = new JCommandButton("Align Left",
      cu.getResizableIcon("resources/icons/layout/align_left.png"));
    btn_alignleft.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "alignleft");
      }
    });
    btn_alignleft.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_left.png"));
    btn_alignleft.setActionRichTooltip(new RichTooltip(
        "Align Left",
        "Align Horizontally by Leftmost Element"));
    band.addCommandButton(btn_alignleft, TOP);
    
    btn_alignright = new JCommandButton("Align Right",
      cu.getResizableIcon("resources/icons/layout/align_right.png"));
    btn_alignright.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "alignright");
      }
    });
    btn_alignright.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_right.png"));
    btn_alignright.setActionRichTooltip(new RichTooltip(
        "Align Right",
        "Align Horizontally by Rightmost Element"));
    band.addCommandButton( btn_alignright, TOP);

    btn_alignhspace = new JCommandButton("Align Horizontal",
      cu.getResizableIcon("resources/icons/layout/align_hspacing.png"));
    btn_alignhspace.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "alignhspacing");
      }
    });
    btn_alignhspace.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_hspacing.png"));
    btn_alignhspace.setActionRichTooltip(new RichTooltip(
        "Align Horizontal",
        "Align the Horizontal Spacing between Elements"));
    band.addCommandButton(btn_alignhspace, TOP);
    
    btn_alignvspace = new JCommandButton("Align Vertical",
      cu.getResizableIcon("resources/icons/layout/align_vspacing.png"));
    btn_alignvspace.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "alignvspacing");
      }
    });
    btn_alignvspace.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_vspacing.png"));
    btn_alignvspace.setActionRichTooltip(new RichTooltip(
        "Align Vertical",
        "Align the Vertical Spacing between Elements"));
    band.addCommandButton(btn_alignvspace, TOP);

    btn_alignwidth = new JCommandButton("Align Width",
      cu.getResizableIcon("resources/icons/layout/align_width.png"));
    btn_alignwidth.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "alignwidth");
      }
    });
    btn_alignwidth.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_width.png"));
    btn_alignwidth.setActionRichTooltip(new RichTooltip(
        "Align Width",
        "Align Elements to a Uniform Width"));
    band.addCommandButton( btn_alignwidth, TOP);
    
    btn_alignheight = new JCommandButton("Align Height",
      cu.getResizableIcon("resources/icons/layout/align_height.png"));
    btn_alignheight.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "alignheight");
      }
    });
    btn_alignheight.setDisabledIcon(cu.getResizableIcon("resources/icons/layout/disable_height.png"));
    btn_alignheight.setActionRichTooltip(new RichTooltip(
        "Align Height",
        "Align Elements to a Uniform Element's Height"));
    band.addCommandButton(btn_alignheight, TOP);

    btn_selection = new JCommandButton("Rectangular Selection",
      cu.getResizableIcon("resources/icons/layout/selection.png"));
    btn_selection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "selection");
      }
    });
    band.addCommandButton(btn_selection, TOP);

    btn_copy_props = new JCommandButton("Copy Properties",
        cu.getResizableIcon("resources/icons/edit/copy_props.png"));
    btn_copy_props.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "copyprops");
        }
      });
    btn_copy_props.setActionRichTooltip(new RichTooltip(
          "Copy Properties",
          "Copy a UI Element's common properties to selected elements."));
    band.addCommandButton(btn_copy_props, TOP);

  }

  /**
   * Initializes the grid, and zoom buttons.
   */
  public void initView(JRibbonBand band) {
    btn_grid = new JCommandButton("Grid",
      cu.getResizableIcon("resources/icons/view/grid.png"));
    btn_grid.setActionRichTooltip(new RichTooltip(
      "Grid",
      "Toggle Grid ON/OFF"));
    btn_grid.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "grid");
      }
    });
    band.addCommandButton(btn_grid, TOP);
    
    btn_zoom_in = new JCommandButton("Zoom In",
      cu.getResizableIcon("resources/icons/view/zoom_in.png"));
    btn_zoom_in.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "zoomin");
      }
    });
    btn_zoom_in.setDisabledIcon(
        cu.getResizableIcon("resources/icons/view/disable_zoom_in.png"));
    btn_zoom_in.setActionRichTooltip(new RichTooltip(
        "Zoom In",
        "Zoom In the TFT Simulation Panel"));
    band.addCommandButton(btn_zoom_in, MEDIUM);

    btn_zoom_out = new JCommandButton("Zoom Out",
      cu.getResizableIcon("resources/icons/view/zoom_out.png"));
    btn_zoom_out.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "zoomout");
      }
    });
    btn_zoom_out.setActionRichTooltip(new RichTooltip(
        "Zoom Out",
        "Zoom Out the TFT Simulation Panel"));
    btn_zoom_out.setDisabledIcon(
        cu.getResizableIcon("resources/icons/view/disable_zoom_out.png"));
    btn_zoom_out.setEnabled(false);
    band.addCommandButton(btn_zoom_out, MEDIUM);

  }

  /**
   * Initializes the Page elements.
   */
  public void initPages(JRibbonBand band) {
    btn_page = new JCommandButton("Page",
      cu.getResizableIcon("resources/icons/page/page_32x.png"));
    btn_page.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "page");
			}
		});
    btn_page.setActionRichTooltip(new RichTooltip(
        "Page",
        "Add a new Page layer."));
    band.addCommandButton(btn_page, TOP);
    
    btn_base_page = new JCommandButton("Base Page",
      cu.getResizableIcon("resources/icons/page/basepage_32x.png"));
    btn_base_page.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "basepage");
			}
		});
    btn_base_page.setActionRichTooltip(new RichTooltip(
        "Base Page",
        "Add a page for the base layer in the page stack."));
    band.addCommandButton(btn_base_page, MEDIUM);

    btn_popup = new JCommandButton("Popup Page",
      cu.getResizableIcon("resources/icons/page/popup_32x.png"));
    btn_popup.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "popup");
			}
		});
    btn_popup.setActionRichTooltip(new RichTooltip(
        "Popup Dialog",
        "Add a new Popup Page for the overlay layer in the page stack."));
    band.addCommandButton(btn_popup, MEDIUM);

  }

  /**
   * Initializes the Text elements.
   */
  public void initText(JRibbonBand band) {
    btn_text = new JCommandButton("Text",
      cu.getResizableIcon("resources/icons/text/label_32x.png"));
    btn_text.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "text");
      }
    });
    btn_text.setActionRichTooltip(new RichTooltip(
        "Text",
        "Add display-only text element."));
    band.addCommandButton(btn_text, TOP);
    
    btn_txtinput = new JCommandButton("Text Input",
      cu.getResizableIcon("resources/icons/text/textinput_32x.png"));
    btn_txtinput.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "textinput");
      }
    });
    btn_txtinput.setActionRichTooltip(new RichTooltip(
        "Text Input",
        "Add clickable element that will accept text input from a virtual keypad."));
    band.addCommandButton(btn_txtinput, MEDIUM);

    btn_numinput = new JCommandButton("Number Input",
      cu.getResizableIcon("resources/icons/text/numinput_32x.png"));
    btn_numinput.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "numinput");
      }
    });
    btn_numinput.setActionRichTooltip(new RichTooltip(
        "Numeric Input",
        "Add clickable element that will accept numberic input from a virtual keypad."));
    band.addCommandButton(btn_numinput, MEDIUM);
      
    btn_textbox = new JCommandButton("Textbox",
      cu.getResizableIcon("resources/icons/text/textbox_32x.png"));
    btn_textbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "textbox");
      }
    });
    btn_textbox.setActionRichTooltip(new RichTooltip(
        "Textbox",
        "Add element with a scrolling window designed for displaying multi-line text using a monospaced font."));
    band.addCommandButton(btn_textbox, MEDIUM);

  }

  /**
   * Initializes the Control elements.
   */
  public void initControls(JRibbonBand band) {
    btn_txtbtn = new JCommandButton("Text Button",
        cu.getResizableIcon("resources/icons/controls/button_32x.png"));
      btn_txtbtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "textbutton");
        }
      });
      btn_txtbtn.setActionRichTooltip(new RichTooltip(
          "Text Button",
          "Add clickable element that has a textual label with frame and fill."));
      band.addCommandButton(btn_txtbtn, TOP);

    btn_imgbtn = new JCommandButton("Image Button",
      cu.getResizableIcon("resources/icons/controls/imgbutton_32x.png"));
    btn_imgbtn.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "imagebutton");
			}
		});
    btn_imgbtn.setActionRichTooltip(new RichTooltip(
        "Image Button",
        "Add clickable element that uses a BMP image with no frame or fill."));
    band.addCommandButton(btn_imgbtn, MEDIUM);
        
    btn_checkbox = new JCommandButton("Checkbox",
      cu.getResizableIcon("resources/icons/controls/checkbox_32x.png"));
    btn_checkbox.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "checkbox");
			}
		});
    btn_checkbox.setActionRichTooltip(new RichTooltip(
        "Checkbox",
        "Add Checkbox element."));
    band.addCommandButton(btn_checkbox, MEDIUM);

    btn_radiobtn = new JCommandButton("Radio Button",
      cu.getResizableIcon("resources/icons/controls/radiobutton_32x.png"));
    btn_radiobtn.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "radiobutton");
			}
		});
    btn_radiobtn.setActionRichTooltip(new RichTooltip(
        "Radio Button",
        "Add Radio Button element."));
    band.addCommandButton(btn_radiobtn, MEDIUM);

    btn_image = new JCommandButton("Image",
        cu.getResizableIcon("resources/icons/controls/image_32x.png"));
      btn_image.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "image");
        }
      });
      btn_image.setActionRichTooltip(new RichTooltip(
          "Image",
          "Add image element."));
      band.addCommandButton(btn_image, MEDIUM);
    
    btn_listbox = new JCommandButton("Listbox",
      cu.getResizableIcon("resources/icons/controls/listbox_32x.png"));
    btn_listbox.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "listbox");
			}
		});
    RichTooltip tip_listbox = new RichTooltip();
    tip_listbox.setTitle("Listbox");
    tip_listbox.addDescriptionSection(
        "Add element that presents"
        + "the user with a group of items,"
        + "displayed in one or more columns");
    btn_listbox.setActionRichTooltip(tip_listbox);
    band.addCommandButton(btn_listbox, MEDIUM);
        
    btn_slider = new JCommandButton("Slider",
        cu.getResizableIcon("resources/icons/controls/slider_32x.png"));
    btn_slider.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "slider");
      }
    });
    RichTooltip tip_slider = new RichTooltip();
    tip_slider.setTitle("Slider");
    tip_slider.addDescriptionSection(
        "Add element that is a graphical control which presents"
        + "values along a line, and that allows users to slide a "
        + "position marker to select a specific value.");
    btn_slider.setActionRichTooltip(tip_slider);
    band.addCommandButton(btn_slider, MEDIUM);

    btn_spinner = new JCommandButton("Spinner",
        cu.getResizableIcon("resources/icons/controls/spinner_32x.png"));
    btn_spinner.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "spinner");
			}
		});
    RichTooltip tip_spinner = new RichTooltip();
    tip_spinner.setTitle("Spinner");
    tip_spinner.addDescriptionSection(
        "Add numeric updown graphical control,"
        + "where a value in an adjoining text box may be"
        + "adjusted by either clicking on the up or down arrow.");
    btn_spinner.setActionRichTooltip(tip_spinner);
    band.addCommandButton(btn_spinner, MEDIUM);

  }

  /**
   * Initializes the Gauge elements.
   */
  public void initGauges(JRibbonBand band) {
    btn_ringgauge = new JCommandButton("Ring Gauge",
        cu.getResizableIcon("resources/icons/gauges/ringgauge_32x.png"));
    btn_ringgauge.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "ringgauge");
        }
      });
    btn_ringgauge.setActionRichTooltip(new RichTooltip(
        "Ring Gauge",
        "Awaiting details to complete implemention."));
    band.addCommandButton(btn_ringgauge, TOP);
      
    btn_progressbar = new JCommandButton("Progress Bar",
      cu.getResizableIcon("resources/icons/gauges/progressbar_32x.png"));
    btn_progressbar.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "progressbar");
			}
		});
    btn_progressbar.setActionRichTooltip(new RichTooltip(
        "ProgressBar",
        "Add element that can be used to indicate the progress of a lengthy operation as a bar."));
    band.addCommandButton(btn_progressbar, TOP);
    
    btn_radial = new JCommandButton("Radial Gauge",
      cu.getResizableIcon("resources/icons/gauges/radial_32x.png"));
    btn_radial.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "radial");
			}
		});
    btn_radial.setActionRichTooltip(new RichTooltip(
        "Radial Gauge",
        "Add element that can be used to indicate the progress of a lengthy operation as a dial gauge."));
    band.addCommandButton(btn_radial, TOP);
    
    btn_ramp = new JCommandButton("Ramp Gauge",
      cu.getResizableIcon("resources/icons/gauges/ramp_32x.png"));
    btn_ramp.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "ramp");
			}
		});
    btn_ramp.setActionRichTooltip(new RichTooltip(
        "Ramp Gauge",
        "Add element that can be used to indicate the progress of a lengthy operation as a ramp gauge."));
    band.addCommandButton(btn_ramp, TOP);
    
  }

  /**
   * Initializes the Gauge elements.
   */
  public void initShapes(JRibbonBand band) {
    btn_box = new JCommandButton("Box",
      cu.getResizableIcon("resources/icons/shapes/box_32x.png"));
    btn_box.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "box");
			}
		});
    btn_box.setActionRichTooltip(new RichTooltip(
        "Box",
        "Add box element."));
    band.addCommandButton(btn_box, TOP);
      
    btn_line = new JCommandButton("Line",
        cu.getResizableIcon("resources/icons/shapes/line_32x.png"));
    btn_line.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "line");
      }
    });
    btn_line.setActionRichTooltip(new RichTooltip(
        "Line",
        "Add line element."));
    band.addCommandButton(btn_line, MEDIUM);
    
    btn_graph = new JCommandButton("Graph",
        cu.getResizableIcon("resources/icons/controls/graph_32x.png"));
      btn_graph.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mb.sendActionCommand("Ribbon", "graph");
        }
      });
      btn_graph.setActionRichTooltip(new RichTooltip(
          "Graph",
          "Add graph element."));
      band.addCommandButton(btn_graph, MEDIUM);

    btn_group = new JCommandButton("Group",
        cu.getResizableIcon("resources/icons/controls/group.png"));
    btn_group.setActionRichTooltip(new RichTooltip(
        "Group ID",
        "Assign GroupID to RadioButtons and/or CheckBoxes"));
    btn_group.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "group");
      }
    });
    btn_group.setDisabledIcon(
        cu.getResizableIcon("resources/icons/controls/disable_group.png"));
    band.addCommandButton(btn_group, MEDIUM);

/*        
    btn_circle = new JCommandButton("Circle",
        cu.getResizableIcon("resources/icons/shapes/circle_32x.png"));
    btn_circle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mb.sendActionCommand("Ribbon", "circle");
      }
    });
    btn_circle.setActionRichTooltip(new RichTooltip(
        "Circle",
        "Add circle element."));
    band.addCommandButton(btn_circle, MEDIUM);
*/        
  }

  /**
   * setEditButtons used by PagePane to set/reset TooBar buttons
   * @param selectedCnt is the number of widgets thats currently selected on the page
   * @param selectedGroupCnt is the number of checkboxes selected on the page
   */

  public void setEditButtons(int selectedGroupCnt) {
    if (selectedGroupCnt > 1) {
      btn_group.setEnabled(true);
    } else {
      btn_group.setEnabled(false);
    }
  }
  
  public void enablePaste() {
    btn_paste.setEnabled(true);
    mini_paste.setEnabled(true);
  }
  
  public void disablePaste() {
    btn_paste.setEnabled(false);
    mini_paste.setEnabled(false);
  }
}
