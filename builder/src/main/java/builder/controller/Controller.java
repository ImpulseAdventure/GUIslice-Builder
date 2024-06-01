/**
 *
 * The MIT License
 *
 * Copyright 2018-2023 Paul Conti
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
package builder.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
//import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import javax.swing.SwingWorker;

import builder.Builder;
import builder.codegen.CodeGenerator;
import builder.codegen.PlatformIO;
import builder.commands.AddWidgetCommand;
import builder.commands.AlignBottomCommand;
import builder.commands.AlignCenterCommand;
import builder.commands.AlignHSpacingCommand;
import builder.commands.AlignHeightCommand;
import builder.commands.AlignLeftCommand;
import builder.commands.AlignRightCommand;
import builder.commands.AlignTopCommand;
import builder.commands.AlignVSpacingCommand;
import builder.commands.AlignWidthCommand;
import builder.commands.ChangeZOrderCommand;
import builder.commands.Command;
import builder.commands.CopyCommand;
import builder.commands.CopyPropsCommand;
import builder.commands.CutCommand;
import builder.commands.DelWidgetCommand;
import builder.commands.GroupCommand;
import builder.commands.History;
import builder.commands.PasteCommand;
import builder.common.EnumFactory;
import builder.common.Guidelines;
import builder.common.Utils;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.events.iSubscriber;
import builder.models.GeneralModel;
import builder.models.GridModel;
import builder.models.GuidelineModel;
import builder.models.PageModel;
import builder.models.ProjectModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.GeneralEditor;
import builder.prefs.GridEditor;
import builder.prefs.ModelEditor;
import builder.prefs.NumKeyPadEditor;
import builder.project.Project;
import builder.themes.GUIsliceTheme;
import builder.themes.GUIsliceThemeFactory;
import builder.views.PagePane;
import builder.views.TreeView;
import builder.widgets.Widget;

/**
 * The Class Controller of the Model View Controller Pattern.
 *
 * 
 * @author Paul Conti
 * 
 */
public class Controller extends JInternalFrame 
  implements iSubscriber, PreferenceChangeListener {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The scrollPane for each page. */
  JScrollPane scrollPane;
  
  /** tabbed panel that shows our pages */
  JTabbedPane tabbedPane;
  
  /** The top frame. */
  private JFrame topFrame;  // used to set title when changing projects

  /** The user preferences. */
  private UserPrefsManager userPreferences;
  
  /** The grid editor. */
  private GridEditor gridEditor;
  
  /** The general editor. */
  public static GeneralEditor generalEditor;
  
  /** The str theme. */
  private String strTheme;
  
  /** The current page. */
  private static PagePane currentPage;
  
  /** The project page which hold all options */
  private PagePane projectPage;
  
  /** The project file. */
  private static File projectFile = null;
  
  /** The title. */
  private String title;
  
  /** The local clipboard */
  private Clipboard clipboard;
  
  /** The project's model */
  static ProjectModel pm;
  
  /** The pages. */
  static List<PagePane> pages = new ArrayList<PagePane>();
  
  /** The tabs to pages keys mapping */
  List<String> tabPages = new ArrayList<String>();

  /** The count of base pages. */
  static int nBasePages;
  
  /** The base page, if any. */
  static private PagePane basePage;

  /** The litr. */
  ListIterator<PagePane> litr;
  
  /** saved icons */
  ImageIcon ic_project_tab,ic_page_tab, ic_base_tab, ic_popup_tab;
  
  /** The instance. */
  private static Controller instance = null;
  
  /**
   * Gets the single instance of Controller.
   *
   * @return single instance of Controller
   */
  public static synchronized Controller getInstance() {
      if (instance == null) {
          instance = new Controller();
      }
      return instance;
  }  

  /**
   * Instantiates a new controller.
   */
  public void initUI() {
    title = "Simulated TFT Panel";
    generalEditor = GeneralEditor.getInstance();
    MsgBoard.subscribe(this, "Controller");

    // trap frame resizing
    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
//          Builder.logger.debug("CHANGED WIDTH: "+getWidth()+" to "+Builder.CANVAS_WIDTH);
          GeneralEditor.getInstance().setTFTWinWidth(getWidth());
//            Builder.logger.debug("CHANGED HEIGHT: "+getHeight()+" to "+Builder.CANVAS_HEIGHT);
            GeneralEditor.getInstance().setTFTWinHeight(getHeight());
      }
    });

    // create our local clipboard
    clipboard = new Clipboard ("My clipboard");
    
    // save icons
    ic_page_tab = Utils.getIcon("resources/icons/page/page_32x.png", 16,16);
    ic_base_tab = Utils.getIcon("resources/icons/page/basepage_32x.png", 16,16);
    ic_popup_tab = Utils.getIcon("resources/icons/page/popup_32x.png", 16,16);
    ic_project_tab = Utils.getIcon("resources/icons/misc/project.png", 16,16);
    
    tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    tabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        changePage(tabbedPane.getSelectedIndex());
      }
    });
    nBasePages = 0;
    basePage = null;
    tabbedPane.setPreferredSize(new Dimension(1200,1200));
    createFirstPage();
//    tabbedPane.setSelectedIndex(0);
    
    this.add(tabbedPane,BorderLayout.CENTER);
    this.setTitle(title);
    this.setFrameIcon(Utils.getIcon("resources/icons/guislicebuilder.png", 24,24));
//    this.pack();
    this.setVisible(true);
    Builder.logger.debug("New Project");
    pm.printModel("Project Options");
  }
  
  /**
   * Sets the frame.
   *
   * @param frame
   *          the new frame
   */
  public void setFrame(JFrame frame) {
    topFrame = frame;
  }
  
  public JFrame getFrame() {
    return topFrame;
  }
  
  /**
   * Gets the clipboard.
   *
   * @return the clipboard
   */
  public Clipboard getClipboard() {
    return clipboard;
  }
  
  /**
   * Sets the user prefs.
   *
   * @param userPreferences
   *          the new user prefs
   */
  public void setUserPrefs(UserPrefsManager userPreferences) {
    this.userPreferences = userPreferences;
  }
  
  /**
   * get project model
   * @return model
   */
  public static ProjectModel getProjectModel() {
    return pm;
  }
  
  /**
   * get widget list for base page, if any
   * @return list of widgets, or null
   */
  public static List<Widget> getBaseWidgets() {
    if (nBasePages > 0) {
      return basePage.getWidgets();
    }
    return null;
  }
  
  /**
   * get project model's Target Platform 
   * @return platform
   */
  public static String getTargetPlatform() {
    // handle race condition at startup
    if (pm == null) {
      return GeneralEditor.getInstance().getTarget();
    }
    return pm.getTargetPlatform();
  }
  
  /**
   * 
   */
  public void createProjectModel() {
    GeneralModel gm = (GeneralModel) GeneralEditor.getInstance().getModel();
    Object[][] gmData = gm.getData();
    pm = new ProjectModel();
    Object[][] pmData = pm.getData();
    pm.TurnOffEvents();
    int rows = gm.getRowCount();
    int mapRow = 0;
    String metaID = null;
    Object objectData = null;
    for (int i=1; i<rows; i++) {
      metaID = (String)gmData[i][WidgetModel.PROP_VAL_ID];
      objectData = gm.getValueAt(i, 1);;
      mapRow = pm.mapMetaIDtoProperty(metaID);
      if (mapRow >= 0) {
        pmData[mapRow][WidgetModel.PROP_VAL_VALUE] = objectData;
      }
    }
    pm.setReadOnlyProperties();
    pm.TurnOnEvents();
    PagePane p = new PagePane();
    p.setLayout(null);
    p.setModel(pm);
    p.setPageType(EnumFactory.PROJECT);
    projectPage = p;
    addPage(p);
  }
  
  
  /**
   * Refresh view.
   */
  public static void refreshView() {
    if (currentPage == null) return;
    currentPage.refreshView();
  }
  
  /**
   * Find page.
   *
   * @param pageKey
   *          the page key
   * @return the <code>page pane</code> object
   */
  public PagePane findPage(String pageKey) {
    litr = pages.listIterator();
    PagePane p = null;
    String searchKey;
    while(litr.hasNext()){
      p = litr.next();
      searchKey = p.getKey();
      if (searchKey.equals(pageKey)) {
        break;
      }
    }
    return p;
  }
  
  /**
   * Find page tab index.
   *
   * @param pageKey
   *          the page key
   * @return the <code>int</code> index, or -1 on failure
   */
  public int findPageIdx(String pageKey) {
    String searchKey = null; 
    for (int i=0; i<tabPages.size(); i++) {
      searchKey = tabPages.get(i);
      if (searchKey.equals(pageKey)) {
        return i;
      }
    }
    return -1;
  }
  
  /**
   * remove page tab index.
   *
   * @param pageKey
   *          the page key
   * @return true on success, or false on failure
   */
  public boolean removePageIdx(String pageKey) {
    String searchKey = null; 
    ListIterator<String> it = tabPages.listIterator();
    // find the key and remove it from our tab list
    boolean bFound = false;
    while(litr.hasNext()){
      searchKey = it.next();
      if (searchKey.equals(pageKey)) {
        it.remove();
        bFound = true;
        break;
      }
    }
    return bFound;
  }
  
  /**
   * Adds the page to view.
   *
   * @param page
   *          the page
   */
  public void addPageToView(PagePane page) {
    scrollPane = new JScrollPane(page,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    if (page.getPageType().equals(EnumFactory.BASEPAGE)) {
      pages.add(1,page);
      nBasePages++;
      basePage = page;
      tabPages.add(1,page.getKey());
      tabbedPane.insertTab(page.getEnum(), ic_base_tab, scrollPane, null, 1);
      tabbedPane.setSelectedIndex(1);
    } else {
      pages.add(page);
      tabPages.add(page.getKey());
      if (page.getPageType().equals(EnumFactory.PAGE)) {
        tabbedPane.addTab(page.getEnum(), ic_page_tab, scrollPane);
      } else if (page.getPageType().equals(EnumFactory.PROJECT)) {
        tabbedPane.addTab(page.getEnum(), ic_project_tab, scrollPane);
      } else {
        tabbedPane.addTab(page.getEnum(), ic_popup_tab, scrollPane);
      }
      tabbedPane.setSelectedIndex(tabPages.size()-1);
    }
    tabbedPane.repaint();
    if (currentPage != null) {
      currentPage.setActive(false);
    }
    currentPage = page;
    currentPage.setActive(true);
  }

  /**
   * Change page.
   *
   * @param pageKey
   *          the page key
   */
  public void changePage(String pageKey) {
    PagePane page = findPage(pageKey);
    if (page != null) {
      int idx = findPageIdx(pageKey);
      if (currentPage != null) {
        currentPage.selectNone();
      }
      currentPage = page;
      tabbedPane.setSelectedIndex(idx);
      tabbedPane.repaint();
      currentPage.setActive(true);
      PropManager.getInstance().showPropEditor(pageKey);
    }
  }
  
  /**
   * Change page but do not inform PropView
   *
   * @param pageKey
   *          the page key
   */
  public void changePageNoMsg(String pageKey) {
    PagePane page = findPage(pageKey);
    if (page != null) {
      int idx = findPageIdx(pageKey);
      if (currentPage != null) {
        currentPage.setActive(false);
      }
      currentPage = page;
      tabbedPane.setSelectedIndex(idx);
      tabbedPane.repaint();
      currentPage.setActive(true);
// commented out for  [Selecting Items on Other Pages Doesn't Show Selected Widget Properties #118] 
//      PropManager.getInstance().showPropEditor(pageKey);
    }
  }
  
  /**
   * Change page.
   *
   * @param pageKey
   *          the page key
   */
  public void changePage(int idx) {
    String pageKey;
    if (idx == -1) return;
    if (idx <= tabPages.size()) {
      pageKey = tabPages.get(idx);
      PagePane page = findPage(pageKey);
      if (page != null) {
        if (currentPage != null && currentPage != page) {
          currentPage.setActive(false);
          currentPage = page;
          tabbedPane.setSelectedIndex(idx);
          tabbedPane.repaint();
          currentPage.setActive(true);
          PropManager.getInstance().showPropEditor(page.getKey());
          // notify treeview
          MsgBoard.sendEvent("Controller",MsgEvent.PAGE_TAB_CHANGE, pageKey);
        }
      }
    }
  }
  
  /**
   * Change Z order.
   *
   * @param e
   *          the e
   */
  public void changeZOrder(MsgEvent e) {
    if (!e.xdata.equals(currentPage.getKey())) {
      changePage(e.xdata);
    }
    String tree_backup = TreeView.getInstance().getSavedBackup();
    ChangeZOrderCommand c = new ChangeZOrderCommand(currentPage, tree_backup);
    c.change(e.message, e.fromIdx, e.toIdx);
    execute(c);
  }
  
  /**
   * Change page enum.
   *
   *
   * @param e
   *          the MsgEvent e
   */
  public void changePageEnum(MsgEvent e) {
    String pageKey = e.message;
    PagePane page = findPage(pageKey);
    if (page != null) {
      int idx = findPageIdx(pageKey);
      tabbedPane.setTitleAt(idx, e.xdata);
      tabbedPane.setSelectedIndex(idx);
      tabbedPane.repaint();
    }
  }
  
  /**
   * Change view.
   *
   * @param e
   *          the MsgEvent e
   */
  public void changeViewFromTree(MsgEvent e) {
    if (e.xdata.equals("Root")) {
      if (!e.message.equals(currentPage.getKey())) {
        changePage(e.message);
      }
    } else {
      if (!e.xdata.equals(currentPage.getKey())) {
        changePageNoMsg(e.xdata);
      }
      currentPage.objectSelectedTreeView(e.message);
    }
  }
  
  // this function is called in our constructor and by newProject()
  /**
   * Creates the first page.
   */
  // It prevents user from undo'ing our Page_1
  public void createFirstPage() {
    createProjectModel();
    PagePane page = new PagePane();
    page.setLayout(null);
    PageModel m = page.getModel();
    String pageKey = EnumFactory.getInstance().createKey(EnumFactory.PAGE);
    m.setKey(pageKey);
    String pageEnum = EnumFactory.getInstance().createEnum(EnumFactory.PAGE);
    m.setEnum(pageEnum);
    page.setPageType(EnumFactory.PAGE);
    addPageToView(page);
    PropManager.getInstance().addPropEditor(page.getModel());
    TreeView.getInstance().addPage(page.getKey(), pageEnum);
  }
  
  /**
   * Creates the page.
   * this function is called directly by toolbox when page button is pressed
   * It builds an AddPageCommand for undo and redo.
   */
  public void createPage(String sWidgetType) {
//    AddPageCommand c = new AddPageCommand(this);
    PagePane p = new PagePane();
    p.setLayout(null);
    PageModel m = (PageModel) p.getModel();
    String pageKey = null;
    if (sWidgetType.equals(EnumFactory.PAGE)) {
      pageKey = EnumFactory.getInstance().createKey(EnumFactory.PAGE);
      m.setKey(pageKey);
      m.setEnum(EnumFactory.getInstance().createEnum(EnumFactory.PAGE));
      // NOTE: must set type on page pane not model or messages will be lost!
      p.setPageType(EnumFactory.PAGE);
    } else  if (sWidgetType.equals(EnumFactory.BASEPAGE)) {
      if (nBasePages > 0) {
        JOptionPane.showMessageDialog(topFrame, 
            "Sorry, You can only define one Base Page", 
            "ERROR",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      pageKey = EnumFactory.getInstance().createKey(EnumFactory.BASEPAGE);
      m.setKey(pageKey);
      m.setEnum(EnumFactory.getInstance().createEnum(EnumFactory.BASEPAGE));
      // NOTE: must set type on page pane not model or messages will be lost!
      p.setPageType(EnumFactory.BASEPAGE);
    } else {
      pageKey = EnumFactory.getInstance().createKey(EnumFactory.POPUP);
      m.setKey(pageKey);
      m.setEnum(EnumFactory.getInstance().createEnum(EnumFactory.POPUP));
      // NOTE: must set type on page pane not model or messages will be lost!
      p.setPageType(EnumFactory.POPUP);
    }
/* 
 * undo of adding pages is too complex to support correctly at the moment
    c.add(p);
    execute(c);
 */
    addPage(p);
  }
  

  /**
   * Adds the page.
   *
   * @param page
   *          the page
   */
  // this function is called by AddPageCommand
  public void addPage(PagePane page) {
    addPageToView(page);
    PropManager.getInstance().addPropEditor(page.getModel());
    TreeView.getInstance().addPage(page.getKey(), page.getEnum());
  }
  
  /**
   * Removes the component.
   * This function is called when user presses delete button
   */
  public void removeComponent() {
    // Determine if we are to delete a widget or a page
    List<Widget> list= currentPage.getSelectedList();
    /* if no widgets are selected on the current page
     * ask the treeview who it has selected, if any
     */
    if (list.size() < 1) { 
      /* could be a widget or page selected in TreeView
       * ask tree view if anyone selected?
       */
      String selected = TreeView.getInstance().getSelectedWidget();
      if (selected != null && !selected.isEmpty()) {
        PagePane p = findPage(selected);
        if (p != null) {
            removePage(p);
            return;
        } else {  // widget it is...
          delWidget();
          return;
        }
      }
    } else {  // widget removal
        delWidget();
        return;
    }
    // It appears no widget is selected
    JOptionPane.showMessageDialog(topFrame, 
        "You must first select an object for deletion!", 
        "Warning",
        JOptionPane.WARNING_MESSAGE);
  }

  // private void refreshViews() {
  //   for (PagePane p : pages) {
  //     if (p.getPageType() != EnumFactory.PROJECT) {
  //       p.refreshView();
  //     }
  //   }
  // }
  
  /**
   * Removes the page, no longer supports undo/redo.
   * This function is called when user selects deletion of a page
   *
   * @param page
   *          the page
   */
  private void removePage(PagePane page) {
    String msg = null;
    if (page.getKey().equals("Page$1")) {
      // error can't remove first page
      JOptionPane.showMessageDialog(topFrame, 
          "Sorry, You can't remove the main page.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    } 
    if (page.getPageType().equals(EnumFactory.PROJECT)) {
      // error can't remove Project tab
      JOptionPane.showMessageDialog(topFrame, 
          "Sorry, You can't remove Project Tab.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    } 
/* No real point to this code. If the user wants to delete this page just
 * delete it!  No reason to force them to remove each widget.
 
    if (page.getWidgetCount() > 0) {
      msg = String.format("Sorry, you must delete all of %s widgets first.", page.getKey());
      // error can't remove first page
      JOptionPane.showMessageDialog(topFrame, 
          msg, "Error",
          JOptionPane.ERROR_MESSAGE);
        return;
    } else {
*/
      msg = String.format("Are you sure you want to delete PAGE %s?\nWARNING: This has No UNDO/REDO.", page.getKey());
      if (JOptionPane.showConfirmDialog(topFrame, 
          msg, 
          "Really Delete?", 
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
        return;
      }
//    }
/* TODO - No longer supports undo/redo since with Base Page support we need 
 * a major rewrite of Controller's backup and restore to get this to work 
 * correctly. One solution would amount to a complete PROJECT save and restore 
 * which seems rather heavy weight for undo/redo.  Needs much more thought.
    DelPageCommand c = new DelPageCommand(this);
    c.delete(page);
    execute(c);
*/
    // since we don't currently support undo/redo for this operation clear history
    History.getInstance().clearHistory();
    delPage(page);
  }

  /**
   * Del page.
   *
   * @param page
   *          the page
   */
  // function is called from DelPageCommand
  public void delPage(PagePane page) {
    MsgBoard.remove(page.getKey());
    if (page.getPageType().equals(EnumFactory.BASEPAGE)) {
      nBasePages=0;
      basePage = null;
    }
    PagePane p = null;
    litr = pages.listIterator();
    int idx = 0;
    while(litr.hasNext()){
      p = litr.next();
      if (p.getKey().equals(page.getKey())) {
        litr.remove();
        idx = findPageIdx(page.getKey());
        tabbedPane.remove(idx);
        tabbedPane.repaint();
        tabPages.remove(idx);
        TreeView.getInstance().delPage(page.getKey());
        if (p.getPageType().equals(EnumFactory.BASEPAGE))
          nBasePages=0;
        if (pages.size() > 1)
          changePage(pages.get(1).getKey());
        break;
      }
    }
//    removePageIdx(page.getKey()); Moved to while loop above
  }
  
  /**
   * Restore page.
   *
   * @param pageKey
   *          the page key
   * @param pageEnum
   *          the page enum
   * @param pageType
   *          the page widget type
   * @return the <code>page pane</code> object
   */
  private PagePane restorePage(String pageKey, String pageEnum, String pageType){
    PagePane page = new PagePane();
    page.setLayout(null);
    PageModel m = (PageModel) page.getModel();
    m.setKey(pageKey);
    m.setEnum(pageEnum);
    page.setPageType(pageType);
    addPageToView(page);
    PropManager.getInstance().addPropEditor(m);
    return page;
  }
  
  private PagePane restoreProject(){
    PagePane p = new PagePane();
    p.setLayout(null);
    p.setModel(pm);
    p.setPageType(EnumFactory.PROJECT);
    projectPage = p;
    addPageToView(p);
    PropManager.getInstance().addPropEditor(pm);
    return p;
  }
  
  /**
   * Adds the widget.
   *
   * @param w
   *          the w
   */
  public void addWidget(Widget w) {
    if (currentPage == projectPage) {
      JOptionPane.showMessageDialog(null, 
         "You can't add UI Elements to your Project Options Panel", 
         "Add Failed", JOptionPane.WARNING_MESSAGE);
      return;
    }
    AddWidgetCommand c = new AddWidgetCommand(currentPage);
    c.add(w);
    currentPage.execute(c);
  }
  
  /**
   * Del widget.
   *
   * @param list
   *          the list
   */
  private void delWidget() {
    if (JOptionPane.showConfirmDialog(topFrame, 
       "You really want to want to delete selected element(s)?", 
       "Delete", 
       JOptionPane.YES_NO_OPTION,
       JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION){
         return;
    }
    DelWidgetCommand c = new DelWidgetCommand(currentPage);
    if (c.delete()) {
      currentPage.execute(c);
      topFrame.repaint();
    } else {
      selectionWarning();
    }
  }
  
  /**
   * New project.
   */
  public void newProject() {
    projectFile = null;
    String frameTitle = Builder.PROGRAM_TITLE + Builder.NEW_PROJECT;
    topFrame.setTitle(frameTitle);
    closeProject();
    PropManager.getInstance().openProject();
    createFirstPage();
    this.setVisible(true);
    Builder.logger.debug("New Project");
  }

  /**
   * Close project.
   */
  private void closeProject() {
    this.setVisible(false);
    tabPages.clear();
    tabbedPane.removeAll();
    for (PagePane p : pages) {
      MsgBoard.remove(p.getKey());
    }
    pages.clear();
    currentPage = null;
    pm = null;
    nBasePages=0;
    EnumFactory.getInstance().clearCounts();
    TreeView.getInstance().closeProject();
    PropManager.getInstance().closeProject();
    History.getInstance().clearHistory();
  }

  /**
   * Save project.
   *
   * @param file
   *          the output file
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void saveProject(File file) throws IOException {
    if (projectFile == null) {
      projectFile = file;
      // Until the first save occurs no project name exists.
      String frameTitle = Builder.PROGRAM_TITLE + " - " + projectFile.getName();
      topFrame.setTitle(frameTitle);
    }

    com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
      .setPrettyPrinting()
      .excludeFieldsWithoutExposeAnnotation()
      .serializeNulls()
      .registerTypeAdapter(ProjectModel.class, new ProjectModel.Serializer())
      .registerTypeAdapter(GuidelineModel.class, new GuidelineModel.Serializer())
      .registerTypeAdapter(Guidelines.class, new Guidelines.Serializer())
      .registerTypeAdapter(Guidelines.GuidelinesList.class, new Guidelines.GuidelinesList.Serializer())
      .create();

    Project project = new Project(
      Builder.FILE_VERSION_NO,
      Guidelines.getInstance()
    );

    try {
      FileOutputStream fos = new FileOutputStream(file.getPath() + ".json");
      fos.write(gson.toJson(project).getBytes());
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Utils.backupFile(projectFile);
    ObjectOutputStream out =  new ObjectOutputStream(new FileOutputStream(projectFile));
    // output current version so we can make changes on future updates
    out.writeObject(Builder.FILE_VERSION_NO);
//    System.out.println("FILE_VERSION_NO: " + Builder.FILE_VERSION_NO);
    // save last page accessed unless its the project options page
    String tmpKey = currentPage.getKey();
    if (tmpKey.equals("Project$1")) {
      out.writeObject((String)"Page$1");  
    } else {
      out.writeObject(tmpKey);  
    }
//    System.out.println("currentPageKey: " + currentPage.getKey());
    pm.writeModel(out);
    // removing zoom factor from project file (PagePane.zoomFactor)
    out.writeDouble(1.0);
    out.writeInt(pages.size());
    String pageKey = null;
    String pageEnum = null;
    String pageType = null;
//    System.out.println("pages: " + pages.size());
    for (PagePane p : pages) {
      p.selectNone();  // turn off all selections
      pageKey = (String)p.getKey();
      pageEnum = (String)p.getEnum();
      pageType = (String)p.getPageType();
//    System.out.println("save page: " + pageKey);
      out.writeObject(pageKey);
//    System.out.println("save page: " + pageEnum);
      out.writeObject(pageEnum);
//    System.out.println("save page: " + pageType);
      out.writeObject(pageType);
      // now backup our model data to a base64 string
      out.writeObject(p.backup());
    }
    out.writeLong(0);  // extra value to avoid java.io.EOFException
    out.flush();
    out.close();
    History.getInstance().clearHistory();
    Builder.postStatusMsg("Successfully Saved Project into " + projectFile.getName());
    Builder.logger.debug("Saved Project into " + projectFile.getName());
  }

  /**
   * Save as project.
   *
   * @param file
   *          the output file
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void saveAsProject(File file) throws IOException {
    // Test to determine if the user really meant save vs saveas
    if (projectFile == null) {
      saveProject(file);
      return;
    }
    /* first copy our previous project folder 
     * contents to our new project folder.
     * 
     * Our folder names are simply the name minus the ending ".prj"
     */
    String fileSep = System.getProperty("file.separator");
    String srcName = projectFile.getName();
    String oldProjectName = srcName;  // save so we can delete later
    int n = srcName.indexOf(".prj");
    srcName = srcName.substring(0,n);
    String destName = file.getName();
    n = destName.indexOf(".prj");
    destName = destName.substring(0,n);
    
    String destFolder = file.getPath();
    n = destFolder.lastIndexOf(destName);
    destFolder = destFolder.substring(0,n-1);
    oldProjectName = destFolder + System.getProperty("file.separator") + oldProjectName;
    String srcFolder = projectFile.getPath();
    n = srcFolder.lastIndexOf(srcName);
    srcFolder = srcFolder.substring(0,n-1);
    
    int destIDE = getProjectModel().getIDE_ID();
    int srcIDE = ProjectModel.ARDUINO_IDE_ID;
    
    if (destIDE == ProjectModel.ARDUINO_IDE_ID && srcIDE == ProjectModel.ARDUINO_IDE_ID) {
      Utils.copyDirectory(srcFolder, destFolder, null);

      File header = new File(destFolder + fileSep + srcName+"_GSLC.h");
      if (header.exists() && !srcName.equals(destName)) {
        File new_header = new File(destFolder + fileSep + destName+"_GSLC.h");
        Utils.fileReplaceStr(header, new_header, srcName,destName);
        header.delete();
      }
      File ino = new File(destFolder + System.getProperty("file.separator") + srcName +".ino");
      if (ino.exists() && !srcName.equals(destName)) { 
        File new_ino = new File(destFolder + System.getProperty("file.separator") + destName+".ino");
        Utils.fileReplaceStr(ino, new_ino, srcName,destName);
        ino.delete();
      }
    } else if (destIDE == ProjectModel.PIO_IDE_ID && srcIDE == ProjectModel.PIO_IDE_ID) {
      Utils.copyDirectory(srcFolder, destFolder, null);
      File header = new File(destFolder + fileSep + "include" + fileSep + srcName+"_GSLC.h");
      if (header.exists() && !srcName.equals(destName)) {
        File new_header = new File(destFolder + fileSep + destName+"_GSLC.h");
        Utils.fileReplaceStr(header, new_header, srcName,destName);
        header.delete();
      }
      File app = new File(srcFolder + fileSep + "src" + fileSep + "main.cpp");
      if (app.exists() && !srcName.equals(destName)) { 
        File new_cpp = new File(destFolder + fileSep + "src" + fileSep + "main.cpp");
        Utils.fileReplaceStr(app, new_cpp, srcName,destName);
        app.delete();
      }
    } else if (destIDE == ProjectModel.PIO_IDE_ID && srcIDE == ProjectModel.ARDUINO_IDE_ID) {
      PlatformIO.makePIOFileStruct(destFolder);
      String destHeaderFolder = destFolder + fileSep + "include";
      List<String> hList= new ArrayList<String>();
      hList.add(".h");
      Utils.copyDirectory(srcFolder, destHeaderFolder, hList);
      File srcHeader = new File(destFolder + fileSep + "include" + fileSep + srcName+"_GSLC.h");
      if (srcHeader.exists() && !srcName.equals(destName)) {
        File destHeader = new File(destFolder + fileSep + "include" + fileSep + destName+"_GSLC.h");
        Utils.fileReplaceStr(srcHeader, destHeader, srcName,destName);
        srcHeader.delete();
      }
      String destSrcFolder = destFolder + fileSep + "src";
      List<String> cList= new ArrayList<String>();
      cList.add(".c");
      cList.add(".cpp");
      cList.add(".ino");
      Utils.copyDirectory(srcFolder, destSrcFolder, cList);
      File ino = new File(destFolder + fileSep + "src" + fileSep + srcName + ".ino");
      if (ino.exists()) { 
        File new_cpp = new File(destFolder + fileSep + "src" + fileSep + "main.cpp");
        Utils.fileReplaceStr(ino, new_cpp, srcName,destName);
        ino.delete();
      }
      PlatformIO.createIniFile(destFolder);
    } else if (destIDE == ProjectModel.ARDUINO_IDE_ID && srcIDE == ProjectModel.PIO_IDE_ID) {
      String oldFolder = srcFolder + fileSep + "src";
      Utils.copyDirectory(oldFolder, destFolder, null);
      oldFolder = srcFolder + fileSep + "include";
      Utils.copyDirectory(oldFolder, destFolder, null);
      File app = new File(destFolder + fileSep + "main.cpp");
      if (app.exists()) { 
        File new_ino = new File(destFolder + fileSep + destName+".ino");
        Utils.fileReplaceStr(app, new_ino, srcName,destName);
        app.delete();
      }
      File ini = new File(destFolder+fileSep+PlatformIO.PLATFORMIO_INI);
      if (ini.exists()) {
        ini.delete();
      }
    }
    projectFile = file;
    // final cleanup
    File oldProject = new File(oldProjectName);
    if (oldProject.exists()) {
      oldProject.delete();
    }
    saveProject(file);
    closeProject();
    openProject(file);
  }
 
  /**
   * Open project.
   *
   * @param file
   *          the project file to open
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void openProject(File file) throws IOException {
    closeProject();
    projectFile = file;
    String frameTitle = Builder.PROGRAM_TITLE + " - " + projectFile.getName();
    topFrame.setTitle(frameTitle);
    Builder.logger.debug("Open Project: " + projectFile.getName() + " Started");
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(new FileInputStream(projectFile));
    } catch (IOException e3) {
      JOptionPane.showMessageDialog(null, "Project Open Failed", e3.toString(), JOptionPane.ERROR_MESSAGE);
      e3.printStackTrace();
      return;
    }
    nBasePages = 0;
    basePage = null;
    PropManager propMgr = PropManager.getInstance();
    propMgr.openProject();
    String pageKey = null;
    String pageEnum = null;
    String pageType = null;
    String openPage = null;
    PagePane p = null;
    try {
      // Read in version number
      String strVersion = (String)in.readObject();
      if (strVersion.equals("1.01") || strVersion.equals("1.02")) {
        // read in target platform
        @SuppressWarnings("unused")
        String target = (String)in.readObject();
      }
      openPage = (String)in.readObject();
//      System.out.println("currentpage Key: " + currentPage.getKey());
      if (strVersion.equals("13.025") || strVersion.equals("17") ) {
        pm = new ProjectModel();
        pm.readModel(in,EnumFactory.PROJECT + "$1");
      } else {
        createProjectModel();
      }
      pm.printModel("Project Options");
      if (strVersion.equals("17")) {
        // we don't save zoom factor in project file anymore
        // PagePane.setZoom(in.readDouble());
        // refreshView();
      }
      int cnt = in.readInt();
//    System.out.println("pages: " + cnt);
      for (int i=0; i<cnt; i++) {
        pageKey = (String)in.readObject();
//      System.out.println("restore page: " + pageKey);
        pageEnum = (String)in.readObject();
//      System.out.println("restore page: " + pageEnum);
        pageType = EnumFactory.PAGE;
        if (!strVersion.equals("1.01")) {
          pageType = (String)in.readObject();
//        System.out.println("restore page: " + pageType);
          if (pageType.equals(EnumFactory.BASEPAGE)) {
            nBasePages++;
          }
        }
        if (pageType.equals(EnumFactory.PROJECT)) {
          p = restoreProject();
        } else {
          p = restorePage(pageKey, pageEnum, pageType);
        }
        p.restore((String)in.readObject(), false);
        p.selectNone();
        if (pageType != null) p.setPageType(pageType);
      }
      if (strVersion.equals("1.01")) {
        @SuppressWarnings("unused")
        String tree_backup = (String)in.readObject();
//        TreeView.getInstance().restore(tree_backup);
      }
      if (strVersion.equals("1.01") || 
          strVersion.equals("1.02") ||
          strVersion.equals("-13")) {
        @SuppressWarnings("unused")
        String enum_backup = (String)in.readObject();
      }
      EnumFactory.getInstance().resetCounts(pages);
      MsgBoard.sendEvent("Controller",MsgEvent.OBJECT_UNSELECT_PAGEPANE);
      MsgBoard.sendEvent("Controller",MsgEvent.OBJECT_UNSELECT_TREEVIEW);
      MsgBoard.sendEvent("Controller",MsgEvent.TREEVIEW_RESET);
    } catch (ClassNotFoundException e) {
      JOptionPane.showMessageDialog(null, "Project File Corrupted", e.toString(), JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
      in.close();
      return;
    }
    in.close();
    Builder.postStatusMsg("Successfully Opened Project File: " + projectFile.getName());
    changePage(openPage);
    this.setVisible(true);
    Builder.logger.debug("Opened Project File: " + projectFile.getName());
  }

  /**
   * Group buttons.
   */
  public void groupButtons() {
    GroupCommand c = new GroupCommand(currentPage);
    if (c.group()) {
      execute(c);
    } else {
      JOptionPane.showMessageDialog(topFrame, 
          "You must select multiple RadioButtons to group them.",
          "Warning",
          JOptionPane.WARNING_MESSAGE);
    }
  }
  
  /**
   * Align top.
   */
  public void alignTop() {
    AlignTopCommand c = new AlignTopCommand(currentPage);
    if (c.align()) {
      execute(c);
    }
  }
  
  /**
   * Align bottom.
   */
  public void alignBottom() {
    AlignBottomCommand c = new AlignBottomCommand(currentPage);
    if (c.align()) {
      execute(c);
    }
  }
  
  /**
   * Align center.
   */
  public void alignCenter() {
    AlignCenterCommand c = new AlignCenterCommand(currentPage);
    if (c.align()) {
      execute(c);
    }
  }
  
  /**
   * Align width.
   */
  public void alignWidth() {
    AlignWidthCommand c = new AlignWidthCommand(currentPage);
    if (c.align()) {
      execute(c);
    } else {
      selectionWarning();
    }
  }
  
  /**
   * Align height.
   */
  public void alignHeight() {
    AlignHeightCommand c = new AlignHeightCommand(currentPage);
    if (c.align()) {
      execute(c);
    } else {
      selectionWarning();
    }
  }
  
  /**
   * Align left.
   */
  public void alignLeft() {
    AlignLeftCommand c = new AlignLeftCommand(currentPage);
    if (c.align()) {
      execute(c);
    } 
  }
  
  /**
   * Align right.
   */
  public void alignRight() {
    AlignRightCommand c = new AlignRightCommand(currentPage);
    if (c.align()) {
      execute(c);
    }
  }
  
  /**
   * Align V spacing.
   */
  public void alignVSpacing() {
    AlignVSpacingCommand c = new AlignVSpacingCommand(currentPage);
    if (c.align()) {
      execute(c);
    } else {
      selectionWarning();
    }
  }
  
  /**
   * Align H spacing.
   */
  public void alignHSpacing() {
    AlignHSpacingCommand c = new AlignHSpacingCommand(currentPage);
    if (c.align()) {
      execute(c);
    } else {
      selectionWarning();
    }
  }
  
  /**
   * Selection warning.
   */
  public void selectionWarning() {
    JOptionPane.showMessageDialog(topFrame, 
        "This command requires multiple widgets be selected.",
        "Warning",
        JOptionPane.WARNING_MESSAGE);
  }
  
  /**
   * Initializes the user prefs.
   *
   * @return the <code>list</code> object
   */
  public List<ModelEditor> initUserPrefs() {
    generalEditor = GeneralEditor.getInstance();
    gridEditor = GridEditor.getInstance();
    AlphaKeyPadEditor alphakeypadEditor = AlphaKeyPadEditor.getInstance();
    NumKeyPadEditor numkeypadEditor = NumKeyPadEditor.getInstance();
    List<ModelEditor> prefEditors = new ArrayList<ModelEditor>();
    prefEditors.add(generalEditor);
    prefEditors.add(gridEditor);
    prefEditors.add(alphakeypadEditor);
    prefEditors.add(numkeypadEditor);
    /*  
     *  Java 9 and up needs addPreferenceChangeListener() instead of addObserver()
     *  and even then only for generalEditor
     */
    generalEditor.addListener(this);
    strTheme = generalEditor.getThemeClassName();
    return prefEditors;
  }
  
  /**
   * getPreferredSize
   *
   * @see javax.swing.JComponent#getPreferredSize()
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(Builder.CANVAS_WIDTH, Builder.CANVAS_HEIGHT);
  }

  /**
   *  generateCode
   *    calls the code generator to create our output file
   */
  public void generateCode() {
    String skeleton=null;
    CodeGenerator cg = CodeGenerator.getInstance();
    if (projectFile != null) {
      skeleton = cg.generateCode(projectFile, pages, generalEditor.isBackwardCompat());
      if (skeleton != null)
        Builder.postStatusMsg("Successful Code Generation into " + skeleton);
      else 
        Builder.postStatusMsg("Code Generation Failed");
    } else {
        JOptionPane.showMessageDialog(topFrame, "Sorry, You must Name Project before asking for code generation",
            "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  /** getProjectName 
   * @return name of project
   */
  public static String getProjectName() {
    if (projectFile != null) {
      return projectFile.getName();
    }
    return "unnamed";
  }
  
  /**
   * isNamedProject
   *  tests for named project
   * @return true, if project named;  False otherwise.
   */
  public boolean isNamedProject() {
    return (projectFile != null);
  }

  /**
   * onExit
   */
  public void onExit() {
    Builder.logger.debug("Builder exit");
    topFrame.dispose();
    System.exit(0);
  }
  
  /**
   * rectangularSelection.
   */
  public void rectangularSelection() {
    currentPage.rectangularSelection(true);
  }
  
  /**
   * showPreferences
   */
  public void showPreferences() {
    userPreferences.showDialog();
  }
  
  /**
   * cut widgets
   */
  public void cutWidgets() {
    CutCommand c = new CutCommand(this, currentPage);
    if (c.cut()) {
      execute(c);
    }
  }
  
  /**
   * copy widgets
   */
  public void copyWidgets() {
    CopyCommand c = new CopyCommand(this, currentPage);
    if (c.copy()) {
      execute(c);
    }
  }
  
  /**
   * copy properties
   */
  public void copyProps() {
    CopyPropsCommand c = new CopyPropsCommand(topFrame, this);
    if (c.copy(currentPage)) {
      currentPage.rectangularSelection(true);
    }
  }
  
  /**
   * copy properties part two
   */
  public void copyProps2(CopyPropsCommand c) {
    if (c.copy2(currentPage)) {
      execute(c);
    } else {
      currentPage.rectangularSelection(false);
    }
  }
  
  public void scale() {
    NumberFormat format = NumberFormat.getInstance();
    format.setGroupingUsed(false);
    NumberFormatter formatter = new NumberFormatter(format);
    formatter.setValueClass(Integer.class);
    formatter.setMaximum(65535);
    formatter.setAllowsInvalid(false);
    formatter.setCommitsOnValidEdit(true);
    JFormattedTextField txtOldWidth = new JFormattedTextField(formatter);
    JFormattedTextField txtOldHeight = new JFormattedTextField(formatter);

    Object[] message = {
        "Old Screen Width:", txtOldWidth,
        "Old Screen Height:", txtOldHeight,
        "\nWARNING: Requires a new screen size to have been set"
        + "\ninside Project Options before running this command.\n"
        + "Users may need to change fonts manually, if required.\n"
        + "Also this command has no UNDO!"
    };
    
    int option = JOptionPane.showConfirmDialog(null, message, "Scale Elements", JOptionPane.OK_CANCEL_OPTION);
    if (option != JOptionPane.OK_OPTION) {
      Builder.logger.debug("Scale canceled");
    }
    try {
      int oldWidth = Integer.valueOf(txtOldWidth.getText());
      int oldHeight = Integer.valueOf(txtOldHeight.getText());
      int newWidth = Builder.CANVAS_WIDTH;
      int newHeight = Builder.CANVAS_HEIGHT;
      if (oldWidth == newWidth && oldHeight == newHeight) {
        JOptionPane.showConfirmDialog(null, 
            "Did you forget to first change to a\nnew screen size inside Project Options?\n",
             "Error!",
            JOptionPane.PLAIN_MESSAGE);
          return;
      }
      // calcualte our scaling ratios
      double ratioX = (double)newWidth / (double)oldWidth;
      double ratioY = (double)newHeight / (double)oldHeight;
      Builder.logger.debug("Scale ratioX: "+ratioX+" ratioY: "+ratioY);
      // now we can walk the pages and resize each of the elements
      for (PagePane p : pages) {
        p.scale(ratioX, ratioY);
      } 
      refreshView();
      Builder.logger.debug("Scale command completed");
    } catch (NumberFormatException e) {
      JOptionPane.showConfirmDialog(null, "Input must be number!", "Error!",
          JOptionPane.PLAIN_MESSAGE);
      return;
    }
  }
  
  
  /**
   * cut widgets
   */
  public void pasteWidgets() {
    PasteCommand c = new PasteCommand(this, currentPage);
    if (c.paste()) {
      execute(c);
    }
  }
  
  /**
   * toggleGrid
   * Turn grid on/off.
   */
  public void toggleShowGrid() {
    ((GridModel) gridEditor.getModel()).toggleShowGrid();
    refreshView();
  }

  /**
   * toggleSnap
   * Turn snap to grid on/off.
   */
  public void toggleSnapToGrid() {
    ((GridModel) gridEditor.getModel()).toggleSnapToGrid();
    refreshView();
  }
  
  /**
   * Validate page enum
   *
   * @param page enum
   *          the page enum to validate
   * @return true if valid page enum, false otherwise
   */
  public boolean isValidPageEnum(String pageEnum) {
    litr = pages.listIterator();
    PagePane p = null;
    String searchKey;
    while(litr.hasNext()){
      p = litr.next();
      searchKey = p.getEnum();
      if (searchKey.equals(pageEnum)) {
        return true;
      }
    }
    return false;
  }
  
  public void zoomIn() {
    if (currentPage == null) return;
    currentPage.zoomIn();
  }
  
  public void zoomOut() {
    if (currentPage == null) return;
    currentPage.zoomOut();
  }

  public void zoomReset() {
    if (currentPage == null) return;
    currentPage.zoomReset();
  }
  
  static public void sendRepaint() {
    if (currentPage == null) return;
    currentPage.refreshView();
  }
  
  public static void changeGUIsliceTheme(String newTheme) {
    WidgetModel m = null;
    GUIsliceTheme theme = GUIsliceThemeFactory.getInstance().findThemeByName(newTheme);
    if (theme != null) {
      pm.changeThemeColors(theme);
      for (PagePane p : pages) {
        for (Widget w : p.getWidgets()) {
          m = w.getModel();
          m.changeThemeColors(theme);
        }
      }
    }
  }
  
  public static void startGUIsliceTheme(String newTheme) {
    @SuppressWarnings("rawtypes")
    SwingWorker sw1 = new SwingWorker() {

      @Override
      protected String doInBackground() throws Exception {
        changeGUIsliceTheme(newTheme);
        String res = "Finished";
        return res;
      }

      @Override
      protected void done() {
        // this method is called when the background
        // thread finishes execution
      }
    };

    // executes the swing worker on worker thread
    sw1.execute();
  }

  /**
   * updateEvent
   *
   * @see builder.events.iSubscriber#updateEvent(builder.events.MsgEvent)
   */
  @Override
  public void updateEvent(MsgEvent e) {
  //  Builder.logger.debug("Controller: " + e.toString());
    if (e.code == MsgEvent.OBJECT_SELECTED_TREEVIEW) {
      Builder.logger.debug("Controller recv: " + e.toString());
      changeViewFromTree(e);
    } else if (e.code == MsgEvent.DELETE_KEY) {
      Builder.logger.debug("Controller recv: " + e.toString());
      removeComponent();
    } else if (e.code == MsgEvent.WIDGET_CHANGE_ZORDER) {
      Builder.logger.debug("Controller recv: " + e.toString());
      changeZOrder(e);
    } else if (e.code == MsgEvent.PAGE_ENUM_CHANGE) {
      Builder.logger.debug("Controller recv: " + e.toString());
      changePageEnum(e);
    } 

  }

  /**
  * Synchronize the GUI with the user preference for Look and Feel, Size of screen etc...
  * @param o the class object that changed value
  * @param arg the argument passed by the observable object, if any. (usally null)
  */
/*
  @Override public void update(Observable o, Object arg) {

    if (o == generalEditor) {
      if (!generalEditor.getTarget().equals(getTargetPlatform())) {
        // we need to update our current project
        pm.setTargetPlatform(generalEditor.getTarget());
      }
      if (!generalEditor.getThemeClassName().equals(strTheme)) {
        strTheme = generalEditor.getThemeClassName();
        try { // change look and feel
          // NOTE: on mac os you can't get here
          Builder.setLookAndFeel(generalEditor.getThemeClassName());
          // update components in this application
          SwingUtilities.updateComponentTreeUI(topFrame);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  }
*/
// replace update() with this routine for Java 9 and above 
  @Override
  public void preferenceChange(PreferenceChangeEvent evt) {
    String key = evt.getKey();

    if (key.equals("Target Platform")) {
      if (!generalEditor.getTarget().equals(getTargetPlatform())) {
        // we need to update our current project
        pm.setTargetPlatform(generalEditor.getTarget());
      }
    }
    if (key.equals("Java Themes")) {
      if (!generalEditor.getThemeClassName().equals(strTheme)) {
        strTheme = generalEditor.getThemeClassName();
        JOptionPane.showMessageDialog(null, 
            "You will need to restart the Builder\nfor your new theme to take effect",
            "Warning", JOptionPane.WARNING_MESSAGE);
/*  Java jdk-17.0.5+8 and above with FlatLaf 3.0 crashes if I try to use
 *  SwingUtilities.updateComponentTreeUI() so its removed for now...
 */
/*
        try { // change look and feel
          Builder.setLookAndFeel(generalEditor.getThemeClassName());
          // update components in this application
          SwingUtilities.updateComponentTreeUI(topFrame);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
*/
      }
    }
 }

  /**
   * Execute.
   *
   * @param c
   *          the c
   */
  public void execute(Command c) {
    c.execute();
    History.getInstance().push(c);
  }

  /**
   * Backup.
   *
   * @return the <code>string</code> object
   */
  public String backup() {
    try {
//      System.out.println("controller backup*****");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
      out.writeObject(currentPage.getKey());
      out.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Restore.
   *
   * @param state
   *          the state
   */
  public void restore(String state) {
    try {
//      System.out.println("controller restore*****");
      byte[] data = Base64.getDecoder().decode(state);
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
      String pageKey = (String)in.readObject();
      in.close();
      changePage(pageKey);
    } catch (ClassNotFoundException e) {
      System.out.print("ClassNotFoundException occurred.");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
    }
  }
  
}
