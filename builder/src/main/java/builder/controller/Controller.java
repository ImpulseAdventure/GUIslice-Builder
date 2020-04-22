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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
/*
Use these imports instead of Observable and Observer for Java 9 and up.
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
*/

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import builder.Builder;
import builder.codegen.CodeGenerator;
import builder.commands.AddPageCommand;
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
import builder.commands.DelPageCommand;
import builder.commands.DelWidgetCommand;
import builder.commands.GroupCommand;
import builder.commands.History;
import builder.commands.PasteCommand;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.common.FontFactory;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.events.iSubscriber;
import builder.models.GeneralModel;
import builder.models.GridModel;
import builder.models.PageModel;
import builder.models.ProjectModel;
import builder.models.TextModel;
import builder.models.WidgetModel;
import builder.prefs.AlphaKeyPadEditor;
import builder.prefs.BoxEditor;
//import builder.prefs.CheckBoxEditor;
import builder.prefs.GeneralEditor;
import builder.prefs.GridEditor;
import builder.prefs.ModelEditor;
import builder.prefs.NumKeyPadEditor;
import builder.prefs.TextEditor;
import builder.prefs.TxtButtonEditor;
import builder.views.PagePane;
import builder.views.TreeView;
import builder.widgets.Widget;

import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

/**
 * The Class Controller of the Model View Controller Pattern.
 *
 * 
 * @author Paul Conti
 * 
 */
public class Controller extends JInternalFrame 
  implements iSubscriber, Observer {
// Use PreferenceChangeListener instead of Observer for Java 9 and above
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The scrollPane for each page. */
  JScrollPane scrollPane;
  
  /** tabbed panel that shows our pages */
  JTabbedPane tabbedPane;
  
  /** The top frame. */
  private JRibbonFrame topFrame;  // used to set title when changing projects

  /** The user preferences. */
  private UserPrefsManager userPreferences;
  
  /** The grid editor. */
  private GridEditor gridEditor;
  
  /** The general editor. */
  private GeneralEditor generalEditor;
  
  /** The str theme. */
  private String strTheme;
  
  /** The target DPI. */
  private int targetDPI;
  
  /** The current page. */
  private PagePane currentPage;
  
  /** The project page which hold all options */
  private PagePane projectPage;
  
  /** The project file. */
  private File projectFile = null;
  
  /** The title. */
  private String title;
  
  /** The local clipboard */
  private Clipboard clipboard;
  
  /** The project's model */
  ProjectModel pm;
  
  /** The pages. */
  List<PagePane> pages = new ArrayList<PagePane>();
  
  /** The tabs to pages keys mapping */
  List<String> tabPages = new ArrayList<String>();

  /** The count of base pages. */
  int nBasePages;
  
  /** The litr. */
  ListIterator<PagePane> litr;
  
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
    this.generalEditor = GeneralEditor.getInstance();
    MsgBoard.getInstance().subscribe(this, "Controller");

    // trap frame resizing
    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        GeneralEditor.getInstance().setTFTWinWidth(getWidth());
        GeneralEditor.getInstance().setTFTWinHeight(getHeight());
      }
    });

    // create our local clipboard
    clipboard = new Clipboard ("My clipboard");
    
    tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    tabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        changePage(tabbedPane.getSelectedIndex());
      }
    });
    nBasePages = 0;
    tabbedPane.setPreferredSize(new Dimension(1200,1200));
    createFirstPage();
//    tabbedPane.setSelectedIndex(0);
    
    this.add(tabbedPane,BorderLayout.CENTER);
    this.setTitle(title);
    CommonUtils cu = CommonUtils.getInstance();
    this.setFrameIcon(cu.getResizableSmallIcon("resources/icons/guislicebuilder.png", new Dimension(24,24)));
//    this.pack();
    this.setVisible(true);
  }
  
  /**
   * Sets the frame.
   *
   * @param frame
   *          the new frame
   */
  public void setFrame(JRibbonFrame frame) {
    topFrame = frame;
  }
  
  public JRibbonFrame getFrame() {
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
   * @return
   */
  public ProjectModel getProjectModel() {
    return pm;
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
  public void refreshView() {
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
    pages.add(page);
    tabPages.add(page.getKey());
    scrollPane = new JScrollPane(page,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    tabbedPane.addTab(page.getEnum(), scrollPane);
    tabbedPane.setSelectedIndex(tabPages.size()-1);
    tabbedPane.repaint();
    currentPage = page;
    currentPage.refreshView();
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
      if (currentPage != null)
        currentPage.selectNone();  // turn off all selections
      currentPage = page;
      tabbedPane.setSelectedIndex(idx);
      tabbedPane.repaint();
      currentPage.refreshView();
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
      if (currentPage != null)
        currentPage.selectNone();  // turn off all selections
      currentPage = page;
      tabbedPane.setSelectedIndex(idx);
      tabbedPane.repaint();
      currentPage.refreshView();
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
          currentPage.selectNone();  // turn off all selections
          currentPage = page;
          tabbedPane.setSelectedIndex(idx);
          tabbedPane.repaint();
          currentPage.refreshView();
          PropManager.getInstance().showPropEditor(page.getKey());
          // notify treeview
          MsgBoard.getInstance().sendEvent("Controller",MsgEvent.PAGE_TAB_CHANGE, pageKey);
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
    } else if (!e.xdata.equals(currentPage.getKey())) {
      changePageNoMsg(e.xdata);
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
    currentPage.refreshView();
  }
  
  /**
   * Creates the page.
   * this function is called directly by toolbox when page button is pressed
   * It builds an AddPageCommand for undo and redo.
   */
  public void createPage(String sWidgetType) {
    AddPageCommand c = new AddPageCommand(this);
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
      nBasePages++;
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
    c.add(p);
    execute(c);
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
  
  // this function is called when user presses delete button
  /**
   * Removes the component.
   */
  // It determines if we are to delete a widget or a page
  public void removeComponent() {
    List<Widget> list= currentPage.getSelectedList();
    if (list.size() < 1) { // could be a widget or page selected in TreeView
      // ask tree view if anyone selected?
      String selected = TreeView.getInstance().getSelectedWidget();
      if (selected != null && !selected.isEmpty()) {
        if (selected.startsWith("Page")      ||
            selected.startsWith("BasePage")  ||
            selected.startsWith("Popup")) {
          PagePane p = findPage(selected);
          if (p != null) {
            removePage(p);
            return;
          }
        } else {  // widget it is...
          delWidget();
          return;
        }
      }
    } else {  // widget removal
        delWidget();
        return;
    }
    JOptionPane.showMessageDialog(topFrame, 
        "You must first select an object for deletion!", 
        "Warning",
        JOptionPane.WARNING_MESSAGE);
  }
  
  // this function is called when user selects deletion of a page
  /**
   * Removes the page.
   *
   * @param page
   *          the page
   */
  // It builds an DelPageCommand for undo and redo.
  private void removePage(PagePane page) {
    String msg = null;
    if (page.getKey().equals("Page$1")) {
      // error can't remove first page
      JOptionPane.showMessageDialog(topFrame, 
          "Sorry, You can't remove the main page.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    } 
    if (page.getWidgetCount() > 0) {
      msg = String.format("Sorry, you must delete all of %s widgets first.", page.getKey());
      // error can't remove first page
      JOptionPane.showMessageDialog(topFrame, 
          msg, "Error",
          JOptionPane.ERROR_MESSAGE);
        return;
    } else {
      msg = String.format("Are you sure you want to delete %s?", page.getKey());
      if (JOptionPane.showConfirmDialog(topFrame, 
          msg, 
          "Really Delete?", 
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
        return;
      }
    }
    DelPageCommand c = new DelPageCommand(this);
    c.delete(page);
    execute(c);
  }

  /**
   * Del page.
   *
   * @param page
   *          the page
   */
  // function is called from DelPageCommand
  public void delPage(PagePane page) {
    MsgBoard.getInstance().remove(page.getKey());
    PagePane p = null;
    litr = pages.listIterator();
    int idx = 0;
    while(litr.hasNext()){
      p = litr.next();
      if (p.getKey().equals(page.getKey())) {
        litr.remove();
        idx = findPageIdx(page.getKey());
        if (idx > 1) {
          tabbedPane.remove(idx);
          tabbedPane.repaint();
        }
        TreeView.getInstance().delPage(page.getKey());
        if (p.getPageType().equals(EnumFactory.BASEPAGE))
          nBasePages=0;
        if (pages.size() > 1)
          changePage(pages.get(1).getKey());
        break;
      }
    }
    removePageIdx(page.getKey());
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
  }

  /**
   * Close project.
   */
  private void closeProject() {
    this.setVisible(false);
    tabPages.clear();
    tabbedPane.removeAll();
    MsgBoard mb = MsgBoard.getInstance();
    for (PagePane p : pages) {
      mb.remove(p.getKey());
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
    if (file != null) {
      projectFile = file;
      // Until the first save occurs no project name exists.
      String frameTitle = Builder.PROGRAM_TITLE + " - " + projectFile.getName();
      topFrame.setTitle(frameTitle);
    } else {
      CommonUtils.backupFile(projectFile);
    }
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
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(new FileInputStream(projectFile));
    } catch (IOException e3) {
      JOptionPane.showMessageDialog(null, "Project Open Failed", e3.toString(), JOptionPane.ERROR_MESSAGE);
      e3.printStackTrace();
      return;
    }
    nBasePages = 0;
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
      if (strVersion.equals("13.025")) {
        pm = new ProjectModel();
        pm.readModel(in);
      } else {
        createProjectModel();
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
      MsgBoard.getInstance().sendEvent("Controller",MsgEvent.OBJECT_UNSELECT_PAGEPANE);
      MsgBoard.getInstance().sendEvent("Controller",MsgEvent.OBJECT_UNSELECT_TREEVIEW);
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
    BoxEditor boxEditor = BoxEditor.getInstance();
    TextEditor textEditor = TextEditor.getInstance();
    TxtButtonEditor txtbuttonEditor = TxtButtonEditor.getInstance();
    AlphaKeyPadEditor alphakeypadEditor = AlphaKeyPadEditor.getInstance();
    NumKeyPadEditor numkeypadEditor = NumKeyPadEditor.getInstance();
    List<ModelEditor> prefEditors = new ArrayList<ModelEditor>();
    prefEditors.add(generalEditor);
    prefEditors.add(gridEditor);
    prefEditors.add(boxEditor);
    prefEditors.add(textEditor);
    prefEditors.add(txtbuttonEditor);
    prefEditors.add(alphakeypadEditor);
    prefEditors.add(numkeypadEditor);
/*  
 *  Java 9 and up needs addPreferenceChangeListener() instead of addObserver()
 *  and even then only for generalEditor
 */
//    Preferences.userRoot().node(GeneralEditor.MY_NODE).addPreferenceChangeListener(this);
    generalEditor.addObserver(this);
    gridEditor.addObserver(this);
    boxEditor.addObserver(this);
    textEditor.addObserver(this);
    txtbuttonEditor.addObserver(this);
    alphakeypadEditor.addObserver(this);
    numkeypadEditor.addObserver(this);
    strTheme = generalEditor.getThemeClassName();
    targetDPI = generalEditor.getDPI();
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
  public void toggleGrid() {
    ((GridModel) gridEditor.getModel()).toggleGrid();
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
  
  /**
   * zoomIn.
   */
  public void zoomIn() {
    PagePane.zoomIn();
    refreshView();
    
  }
  
  /**
   * zoomOut.
   */
  public void zoomOut() {
    PagePane.zoomOut();
    refreshView();
  }
  
  /**
   * updateEvent
   *
   * @see builder.events.iSubscriber#updateEvent(builder.events.MsgEvent)
   */
  @Override
  public void updateEvent(MsgEvent e) {
//    System.out.println("Controller: " + e.toString());
    if (e.code == MsgEvent.OBJECT_SELECTED_TREEVIEW) {
// System.out.println("Controller: " + e.toString());
        changeViewFromTree(e);
    } else if (e.code == MsgEvent.DELETE_KEY) {
      removeComponent();
    } else if (e.code == MsgEvent.WIDGET_CHANGE_ZORDER) {
// System.out.println("Controller: " + e.toString());
      changeZOrder(e);
    } else if (e.code == MsgEvent.PAGE_ENUM_CHANGE) {
// System.out.println("Controller: " + e.toString());
      changePageEnum(e);
    } 

  }

  /**
  * Synchronize the GUI with the user preference for Look and Feel, Size of screen etc...
  * @param o the class object that changed value
  * @param arg the argument passed by the observable object, if any. (usally null)
  */
  @Override public void update(Observable o, Object arg) {

    if (o == generalEditor) {
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
      int dpi = generalEditor.getDPI();
      if (dpi != targetDPI) {
        targetDPI = dpi;
        FontFactory.getInstance().reloadFonts();
        for (PagePane p : pages) {
          for (Widget w : p.getWidgets()) {
            if (w.getType().equals(EnumFactory.TEXT)) {
              ((TextModel)w.getModel()).calcSizes(true);
            }
          }
        }
      }
    }
  }
/* replace update() with this routine for Java 9 and above
  public void preferenceChange(PreferenceChangeEvent evt) {
    int width, height;
    String key = evt.getKey();
    String val = evt.getNewValue();

    if (key.equals("Theme")) {
      if (!generalEditor.getThemeClassName().equals(strTheme)) {
        strTheme = generalEditor.getThemeClassName();
        try { // change look and feel
          // NOTE: on mac os you can't get here
          UIManager.setLookAndFeel(generalEditor.getThemeClassName());
          // update components in this application
          SwingUtilities.updateComponentTreeUI(topFrame);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
    if (key.equals("TFT Screen DPI")) {
      int dpi = Integer.parseInt(val);
      if (dpi != targetDPI) {
        targetDPI = dpi;
        FontFactory.getInstance().reloadFonts();
      }
      for (PagePane p : pages) {
        for (Widget w : p.getWidgets()) {
          if (w.getType().equals(EnumFactory.TEXT)) {
            ((TextModel)w.getModel()).calcSizes(true);
          }
        }
      }
      refreshView();
    }
 }
*/
  /**
   * Execute.
   *
   * @param c
   *          the c
   */
  public void execute(Command c) {
    History.getInstance().push(c);
    c.execute();
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
      out.writeObject(nBasePages);
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
      nBasePages = in.readInt();
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
