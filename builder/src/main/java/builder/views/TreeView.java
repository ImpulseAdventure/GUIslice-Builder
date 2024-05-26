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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import builder.Builder;
import builder.clipboard.TreeItem;
import builder.clipboard.TreeItemSelection;
import builder.common.EnumFactory;
import builder.common.Utils;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.events.iSubscriber;
import builder.prefs.GeneralEditor;

/**
 * The Class TreeView provides a view of all widgets on all pages.
 * <p>
 * Users may select a single widget and it will be selected
 * inside the owner page view.  A widget may also be selected
 * then dragged to a new Z position. A selected widget or page
 * may also be deleted from the project.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class TreeView extends JInternalFrame implements iSubscriber {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The tree. */
  private JTree tree;
  
  /** The root. */
  private DefaultMutableTreeNode root;
  
  /** The current page. */
  private DefaultMutableTreeNode currentPage;
  
  /** The tree model. */
  protected DefaultTreeModel treeModel;
  
  /** The scroll pane. */
  private JScrollPane scrollPane;
  
  /** The latest backup. */
  private String latestBackup = null;
  
  /** The b dragging node. */
  private boolean bDraggingNode = false;
  
  /** The instance. */
  private static TreeView instance = null;
  
  /** The currently selected widget */
  private TreeItem selectWidget = null;
  
  /** The root object */
  private TreeItem rootItem;
  
  /** TREE_WIDTH */
  private static int TREE_WIDTH;
  
  /** TREE_HEIGHT */
  private static int TREE_HEIGHT;
  
  /**
   * Gets the single instance of TreeView.
   *
   * @return single instance of TreeView
   */
  public static synchronized TreeView getInstance()  {
      if (instance == null) {
          instance = new TreeView();
      }
      return instance;
  }  

  /**
   * Instantiates a new tree view.
   */
  public TreeView() {
    MsgBoard.subscribe(this, "TreeView");
    initUI();
  }
  
  /**
   * Initializes the UI.
   */
  private void initUI() {
    TREE_WIDTH = GeneralEditor.getInstance().getTreeWinWidth();
    TREE_HEIGHT = GeneralEditor.getInstance().getTreeWinHeight();
    this.setResizable(true);
    // trap frame resizing
    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
//          Builder.logger.debug("CHANGED WIDTH: "+getWidth()+" to "+Builder.CANVAS_WIDTH);
          TREE_WIDTH = getWidth();
          TREE_HEIGHT = getHeight();
          instance.setPreferredSize(new Dimension(TREE_WIDTH, TREE_HEIGHT));
          GeneralEditor.getInstance().setTreeWinWidth(TREE_WIDTH);
//          Builder.logger.debug("CHANGED HEIGHT: "+TREE_HEIGHT);
          GeneralEditor.getInstance().setTreeWinHeight(TREE_HEIGHT);
      }
    });
    // create the root node
    rootItem = new TreeItem("Root", null);
    root = new DefaultMutableTreeNode(rootItem);

    treeModel = new DefaultTreeModel(root);

    // create the tree by passing in our tree model
    tree = new JTree(treeModel);
    
    MyTreeRenderer renderer = new MyTreeRenderer();
    tree.setCellRenderer(renderer);

    // if setRootVisible(false) you won't get the expand/collapse icons to show
    //    tree.setRootVisible(false);
    tree.setEditable(true);
    tree.setDragEnabled(true);
    tree.setDropMode(DropMode.ON_OR_INSERT);

    MyTreeCellEditor editor = new MyTreeCellEditor(tree,
        (DefaultTreeCellRenderer) tree.getCellRenderer());
    tree.setCellEditor(editor);

    tree.setTransferHandler(new TreeTransferHandler());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//    tree.setRootVisible(false);
    tree.setFocusable( true );
    
    tree.addMouseListener(new MouseHandler());
    tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode == null) selectWidget = null;
        if (selectedNode != null && !bDraggingNode) {
          TreeItem widget = ((TreeItem)selectedNode.getUserObject());
          // this is necessary to avoid a loop with pagePane.
          if (selectWidget == null || (!selectWidget.equals(widget))) {
            selectWidget = widget;
            TreePath parentPath = e.getPath().getParentPath();
            if (parentPath == null) return;
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
            MsgBoard.sendEvent("TreeView", MsgEvent.OBJECT_SELECTED_TREEVIEW,
                selectWidget.getKey(),
                ((TreeItem) parentNode.getUserObject()).getKey());
          }
        }
      }
    });
    tree.addKeyListener(new java.awt.event.KeyAdapter() {

      public void keyPressed(java.awt.event.KeyEvent evt) {
          if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_C) {
              Controller.getInstance().copyWidgets();
          }
      }
    });
    scrollPane = new JScrollPane(tree,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(300, 650));
    add(scrollPane);
    this.setTitle("Tree View");        
    this.setFrameIcon(Utils.getIcon("resources/icons/guislicebuilder.png",24,24));
    this.setPreferredSize(new Dimension(TREE_WIDTH, 400));
    this.pack();
    this.setVisible(true);
    
 }

  /**
   * The Class MouseHandler.
   */
  private class MouseHandler extends MouseAdapter {

    /**
     * mouseClicked
     *
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
      int row=tree.getRowForLocation(e.getX(),e.getY());
      if(row==-1) { //When user clicks on the "empty surface"
        tree.clearSelection();
        selectWidget= null;
        MsgBoard.sendEvent("TreeView",MsgEvent.OBJECT_UNSELECT_TREEVIEW,
            "", ((TreeItem)currentPage.getUserObject()).getKey());
      }
    }
  }
  
  /**
   * Adds the page.
   *
   * @param pageID
   *          the page ID
   */
  public void addPage(String pageID, String pageEnum) {
    //create and add the top level page node
    TreeItem item = new TreeItem(pageID, pageEnum);
    Builder.logger.debug("TV-add page: " + item.toDebugString());
    currentPage = addObject(null, item);
    TreePath path = new TreePath(currentPage.getPath());
    tree.setSelectionPath(path);
    tree.scrollPathToVisible(path);
  }
  
  /**
   * Del page.
   *
   * @param pageID
   *          the page ID
   */
  public void delPage(String pageID) {
    TreeItem pageItem = new TreeItem(pageID, null);
    Builder.logger.debug("TV-delete page: " + pageItem.toDebugString());
    DefaultMutableTreeNode node = findNode(pageItem);
    if (node != null) {
      node.removeAllChildren(); //this removes all nodes
      delObject(root, pageItem);
    }
    List<DefaultMutableTreeNode> searchNodes = getSearchNodes((DefaultMutableTreeNode) tree.getModel().getRoot());
    currentPage = searchNodes.get(1);
    TreePath path = new TreePath(currentPage.getPath());
    tree.setSelectionPath(path);
    tree.scrollPathToVisible(path);
  }
  
  /**
   * Adds the widget.
   *
   * @param pageID
   *          the page ID
   * @param widget
   *          the widget
   */
  public void addWidget(String pageID, String pageEnum, String widgetID, String widgetEnum) {
    TreeItem pageItem = new TreeItem(pageID, pageEnum);
    TreeItem item = new TreeItem(widgetID, widgetEnum);
//    Builder.logger.debug("TV-add: " + item.toDebugString());
    selectWidget = item;  // avoids loop in pagePane
    if (!((TreeItem)currentPage.getUserObject()).equals(pageItem)) {
      currentPage = findNode(pageItem);
    }
    //create and add the child node to the page node
    DefaultMutableTreeNode newNode = addObject(currentPage, item);
    TreePath path = new TreePath(newNode.getPath());
    tree.setSelectionPath(path);
    tree.scrollPathToVisible(path);
  }

  /**
   * Del widget.
   *
   * @param pageID
   *          the page ID
   * @param widget
   *          the widget
   */
  public void delWidget(String pageID, String widgetID) {
    TreeItem item = new TreeItem(pageID, null);
    Builder.logger.debug("TV-delete: " + item.toDebugString());
    if (!((TreeItem)currentPage.getUserObject()).equals(item)) {
      currentPage = findNode(item);
    }
    //remove the child node from the page node
    item = new TreeItem(widgetID, null);
    delObject(currentPage, item);
  }
  
  /**
   * getSelectedWidget() gets the currently selected widget
   * @return current widget's Key or null;
   */
  public String getSelectedWidget() {
    if (selectWidget != null)
      return selectWidget.getKey();
    return "";
  }
  
  /**
   * Close project.
   */
  public void closeProject() {
    root.removeAllChildren(); //this removes all nodes
    treeModel.reload(); //this notifies the listeners and changes the GUI
  }
  
  /**
   * Gets the saved backup.
   *
   * @return the saved backup
   */
  public String getSavedBackup() {
    return latestBackup;
  }
  
  /**
   * Adds the object.
   *
   * @param parent
   *          the parent
   * @param child
   *          the child
   * @return the <code>default mutable tree node</code> object
   */
  public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, TreeItem child) {
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

    if (parent == null) {
      parent = root;
    }
    // It is key to add this to the TreeModel
    if (parent == root && child.getType().equals(EnumFactory.BASEPAGE)) {
      treeModel.insertNodeInto(childNode, parent, 1);
    } else {
      treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
    }

    // Make sure the user can see the lovely new node.
    tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    return childNode;
  }

  /**
   * Del object.
   *
   * @param parent
   *          the parent
   * @param child
   *          the child
   */
  public void delObject(DefaultMutableTreeNode parent, TreeItem child) {

    if (parent == null) {
      parent = root;
    }
    DefaultMutableTreeNode childNode = findNode(child);
    if (childNode == null) return;
    // It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
    treeModel.removeNodeFromParent(childNode);
  }

  /**
   * Find node.
   *
   * @param searchItem
   *          the search item
   * @return the <code>default mutable tree node</code> object
   */
  public DefaultMutableTreeNode findNode(TreeItem searchItem) {

    List<DefaultMutableTreeNode> searchNodes = getSearchNodes((DefaultMutableTreeNode) tree.getModel().getRoot());
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

    DefaultMutableTreeNode foundNode = null;
    int bookmark = -1;
    if (currentNode != null) {
      if (((TreeItem)currentNode.getUserObject()).equals(searchItem))
        return currentNode;
      for (int index = 0; index < searchNodes.size(); index++) {
        if (searchNodes.get(index) == currentNode) {
          bookmark = index;
          break;
        }
      }
    }

    for (int index = bookmark + 1; index < searchNodes.size(); index++) {
      if (((TreeItem)searchNodes.get(index).getUserObject()).equals(searchItem)) {
        foundNode = searchNodes.get(index);
        break;
      }
    }

    if (foundNode == null) {
      for (int index = 0; index <= bookmark; index++) {
        if (((TreeItem)searchNodes.get(index).getUserObject()).equals(searchItem)) {
          foundNode = searchNodes.get(index);
          break;
        }
      }
    }
    return foundNode;
  }

  public TreePath getPath(TreeNode treeNode) {
    List<Object> nodes = new ArrayList<Object>();
    if (treeNode != null) {
      nodes.add(treeNode);
      treeNode = treeNode.getParent();
      while (treeNode != null) {
        nodes.add(0, treeNode);
        treeNode = treeNode.getParent();
      }
    }
    return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
  }
  
  /**
   * Gets the search nodes.
   *
   * @param root
   *          the root
   * @return the search nodes
   */
  public List<DefaultMutableTreeNode> getSearchNodes(DefaultMutableTreeNode root) {
    List<DefaultMutableTreeNode> searchNodes = new ArrayList<DefaultMutableTreeNode>();

    Enumeration<?> e = root.preorderEnumeration();
    while (e.hasMoreElements()) {
      searchNodes.add((DefaultMutableTreeNode) e.nextElement());
    }
    return searchNodes;
  }

  /**
   * Gets the selected index.
   *
   * @param parent
   *          the parent
   * @param s
   *          the s
   * @return the selected index
   */
  public int getSelectedIndex(DefaultMutableTreeNode parent, TreeItem item) {
    int row = -1;
    for (int i=0;i<parent.getChildCount();i++) {
      DefaultMutableTreeNode child = ((DefaultMutableTreeNode)parent.getChildAt(i));
      if(item.equals((TreeItem) child.getUserObject())){
          row = i;
          break;
      }
    }
    return row;
    
  }

/**
 * MyTreeRenderer
 * This allows us to display custom icons for each type of object
 *
 */
  private class MyTreeRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;
    private final ImageIcon widgetIcon;
    private final ImageIcon projectIcon;
    private final ImageIcon pageIcon;
    private final ImageIcon basepageIcon;
    private final ImageIcon popupIcon;
    private final ImageIcon labelIcon;
    private final ImageIcon textinputIcon;
    private final ImageIcon numinputIcon;
    private final ImageIcon textboxIcon;
    private final ImageIcon txtbuttonIcon;
    private final ImageIcon imgbuttonIcon;
    private final ImageIcon checkboxIcon;
    private final ImageIcon radiobuttonIcon;
    private final ImageIcon imageIcon;
    private final ImageIcon listboxIcon;
    private final ImageIcon sliderIcon;
    private final ImageIcon spinnerIcon;
    private final ImageIcon ringgaugeIcon;
    private final ImageIcon progressbarIcon;
    private final ImageIcon radialIcon;
    private final ImageIcon rampIcon;
    private final ImageIcon boxIcon;
    private final ImageIcon lineIcon;
    private final ImageIcon graphIcon;

    public MyTreeRenderer() {
      projectIcon = Utils.getIcon("resources/icons/misc/project.png",24,24);
      pageIcon = Utils.getIcon("resources/icons/page/page_32x.png",  24,24);
      basepageIcon = Utils.getIcon("resources/icons/page/basepage_32x.png",  24,24);
      boxIcon = Utils.getIcon("resources/icons/shapes/box_32x.png",  24,24);
      checkboxIcon = Utils.getIcon("resources/icons/controls/checkbox_32x.png",  24,24);
      graphIcon = Utils.getIcon("resources/icons/controls/graph_32x.png",  24,24);
      imageIcon = Utils.getIcon("resources/icons/controls/image_32x.png",  24,24);
      imgbuttonIcon = Utils.getIcon("resources/icons/controls/imgbutton_32x.png",  24,24);
      lineIcon = Utils.getIcon("resources/icons/shapes/line_32x.png",  24,24);
      listboxIcon = Utils.getIcon("resources/icons/controls/listbox_32x.png",  24,24);
      numinputIcon = Utils.getIcon("resources/icons/text/numinput_32x.png",  24,24);
      popupIcon = Utils.getIcon("resources/icons/page/popup_32x.png",  24,24);
      progressbarIcon = Utils.getIcon("resources/icons/gauges/progressbar_32x.png",  24,24);
      radiobuttonIcon = Utils.getIcon("resources/icons/controls/radiobutton_32x.png",  24,24);
      radialIcon = Utils.getIcon("resources/icons/gauges/radial_32x.png",  24,24);
      rampIcon = Utils.getIcon("resources/icons/gauges/ramp_32x.png",  24,24);
      ringgaugeIcon = Utils.getIcon("resources/icons/gauges/ringgauge_32x.png",  24,24);
      sliderIcon = Utils.getIcon("resources/icons/controls/slider_32x.png",  24,24);
      spinnerIcon = Utils.getIcon("resources/icons/controls/spinner_32x.png",  24,24);
      labelIcon = Utils.getIcon("resources/icons/text/label_32x.png",  24,24);
      textboxIcon = Utils.getIcon("resources/icons/text/textbox_32x.png",  24,24);
      txtbuttonIcon = Utils.getIcon("resources/icons/controls/button_32x.png",  24,24);
      textinputIcon = Utils.getIcon("resources/icons/text/textinput_32x.png",  24,24);
      
      widgetIcon = Utils.getIcon("resources/icons/misc/widget.png", 24,24);
   }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Object userObject = node.getUserObject();
      if (userObject instanceof TreeItem) {
        TreeItem item = (TreeItem) userObject;
        String widgetType = item.getType();
        switch(widgetType) {
          case EnumFactory.BASEPAGE:
            this.setIcon(basepageIcon);
            break;
          case EnumFactory.BOX:
            this.setIcon(boxIcon);
            break;
          case EnumFactory.CHECKBOX:
            this.setIcon(checkboxIcon);
            break;
          case EnumFactory.GRAPH:
            this.setIcon(graphIcon);
            break;
          case EnumFactory.IMAGE:
            this.setIcon(imageIcon);
            break;
          case EnumFactory.IMAGEBUTTON:
            this.setIcon(imgbuttonIcon);
            break;
          case EnumFactory.LINE:
            this.setIcon(lineIcon);
            break;
          case EnumFactory.LISTBOX:
            this.setIcon(listboxIcon);
            break;
          case EnumFactory.NUMINPUT:
            this.setIcon(numinputIcon);
            break;
          case EnumFactory.PAGE:
            this.setIcon(pageIcon);
            break;
          case EnumFactory.POPUP:
            this.setIcon(popupIcon);
            break;
          case EnumFactory.PROGRESSBAR:
            this.setIcon(progressbarIcon);
            break;
          case EnumFactory.PROJECT:
            this.setIcon(projectIcon);
            break;
          case EnumFactory.RADIOBUTTON:
            this.setIcon(radiobuttonIcon);
            break;
          case EnumFactory.RADIALGAUGE:
            this.setIcon(radialIcon);
            break;
          case EnumFactory.RAMPGAUGE:
            this.setIcon(rampIcon);
            break;
          case EnumFactory.RINGGAUGE:
            this.setIcon(ringgaugeIcon);
            break;
          case EnumFactory.SLIDER:
            this.setIcon(sliderIcon);
            break;
          case EnumFactory.SPINNER:
            this.setIcon(spinnerIcon);
            break;
          case EnumFactory.TEXT:
            this.setIcon(labelIcon);
            break;
          case EnumFactory.TEXTBOX:
            this.setIcon(textboxIcon);
            break;
          case EnumFactory.TEXTBUTTON:
            this.setIcon(txtbuttonIcon);
            break;
          case EnumFactory.TEXTINPUT:
            this.setIcon(textinputIcon);
            break;
          default:
            if (item.getKey().equals("Root")) {
                this.setIcon(null);
                return this;
            }
            this.setIcon(widgetIcon);
        }
        this.setText("<html>" + item.getEnum() + "</html>");
      }
      return this;
    }
  }

  /**
   * The Class MyTreeCellEditor will prevent anyone from editing a widget node.
   * Since we only show keys; A user changing a key value would crash the system.
   */
  private class MyTreeCellEditor extends DefaultTreeCellEditor {

    /**
     * Instantiates a new my tree cell editor.
     *
     * @param tree
     *          the tree
     * @param renderer
     *          the renderer
     */
    public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
    }

    /**
     * getTreeCellEditorComponent
     *
     * @see javax.swing.tree.DefaultTreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
     */
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {
        return super.getTreeCellEditorComponent(tree, value, isSelected, expanded,
                leaf, row);
    }

    /**
     * isCellEditable
     *
     * @see javax.swing.tree.DefaultTreeCellEditor#isCellEditable(java.util.EventObject)
     */
    @Override
    public boolean isCellEditable(EventObject e) {
        return false;
    }
  }
  
  /**
   * The Class TreeTransferHandler allows a widget to be dragged within the tree view.
   * <p>
   * NOTE: Widgets cannot be dragged between pages, only reordered within a page.
   */
  class TreeTransferHandler extends TransferHandler {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The selected node. */
    private DefaultMutableTreeNode selectedNode;
    
    /** The parent node. */
    private DefaultMutableTreeNode parentNode;
    
    /** The from index. */
    private int fromIndex;
    
    /** The drop index. */
    int dropIndex;
    
    /** The number of children. */
    int numberChildren;
    
    /**
     * canImport
     * for a TreeItem-flavored, drop transfer over a non-null tree path.
     *
     * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
     */
    public boolean canImport(TransferHandler.TransferSupport support) {
      DataFlavor widgets = new DataFlavor(TreeItem.class, "TreeItem");
      if (!support.isDataFlavorSupported(widgets) || !support.isDrop()) {
        return false;
      }
      // Only support moves within our tree branch
      int action = support.getDropAction();
      if(action != MOVE) return false;
   
//      System.out.println("**Enter canImport");
      JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
      int parentRow = tree.getRowForPath(dropLocation.getPath());
      if (parentRow == -1) {
        return false;  
      }
      TreePath parentPath = dropLocation.getPath();

      parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
      TreeItem tiParent = (TreeItem)(parentNode.getUserObject());
//      Builder.logger.debug("parentNode: " + tiParent.toDebugString());

      // do not allow parent to be root
      if (tiParent.getKey().equals("Root") || tiParent.getType().equals("Project"))
        return false;
      dropIndex = dropLocation.getChildIndex();
      numberChildren = parentNode.getChildCount();
      
      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      TreePath dropPath = new TreePath(selectedNode.getPath());

      // Do not allow MOVE-action drops if a non-leaf node is selected
      TreeItem item = ((TreeItem)selectedNode.getUserObject());

      // non-leaf node?
      if(!selectedNode.isLeaf() || item.getType().equals("Page")) {
//        Builder.logger.debug("non-leaf node->abort");
        return false;
      }
      
      // Do not allow moves between parent nodes
      if (!parentPath.isDescendant(dropPath)) {
//        Builder.logger.debug("move between parent nodes->abort");
        return false;
      }

      // make sure we have a valid drop point
      bDraggingNode = dropLocation.getPath() != null;
      if (bDraggingNode) {
        latestBackup = backup(); // save for undo command
//        Builder.logger.debug("Drag Started-dropLocation: " + dropLocation.getPath().toString());
//      } else {
//        Builder.logger.debug("TV-Drag drop point invalid");
      }
      return bDraggingNode;
    }

    /**
     * importData
     *
     * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
     */
    public boolean importData(TransferHandler.TransferSupport support) {
      if (!canImport(support)) {
        return false;
      }
//    System.out.println("##Enter importData");
      // dropLocation will give you the location in the tree to add the new node. 
      JTree.DropLocation dropLocation =
      (JTree.DropLocation) support.getDropLocation();
      // grab the path to the dropLocation
      TreePath destPath = dropLocation.getPath();
      /*
       * we don't pass around the actual objects during Drag N Drop just the 
       * data contained in the object in our case just the string value.
       */
      Transferable transferable = support.getTransferable();

      TreeItem transferData;
      try {
        transferData = (TreeItem) transferable.getTransferData(
            TreeItemSelection.treeitemFlavor);
      } catch (IOException e) {
        return false;
      } catch (UnsupportedFlavorException e) {
        return false;
      }
      
      // create the new node using the transfer data
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(transferData);
      fromIndex = getSelectedIndex(parentNode, ((TreeItem) newNode.getUserObject())); 
//      System.out.println("dropIndex: " + dropIndex + " fromIndex: " + fromIndex);
      
      // add the new node to the tree path
      int childIndex = dropLocation.getChildIndex();
//      System.out.println("childIndex: " + childIndex);
      treeModel.insertNodeInto(newNode, parentNode, childIndex);
 
      // ensure the new path element is visible.
      TreePath newPath = destPath.pathByAddingChild(newNode);
      tree.makeVisible(newPath);
      tree.scrollRectToVisible(tree.getPathBounds(newPath));

      return true;
    }
    
    /**
     * getSourceActions
     *
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE;
    }

    /**
     * createTransferable
     *
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    public Transferable createTransferable(JComponent c) {
      if (c instanceof JTree) {
        TreePath[] paths = tree.getSelectionPaths();
        if(paths != null) {
          // grab the selected node
          selectedNode = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
          return new TreeItemSelection((TreeItem) selectedNode.getUserObject());
        }
      }
      return null;
    }

    /**
     * exportDone
     *
     * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
     */
    public void exportDone(JComponent c, Transferable t, int action) {
      if (action == MOVE) {
        JTree tree = (JTree)c;
        DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
        // Remove node saved in selectedNode in createTransferable.
        treeModel.removeNodeFromParent(selectedNode);
        // determine our destination row where we have been moved.
        TreeItem item = ((TreeItem)selectedNode.getUserObject());
        int toRow = getSelectedIndex(parentNode, item); 
        if (toRow != -1) {
          MsgEvent ev = new MsgEvent();
          ev.code = MsgEvent.WIDGET_CHANGE_ZORDER;
          ev.message = item.getKey();
          ev.xdata = ((TreeItem) parentNode.getUserObject()).getKey();
          ev.fromIdx = fromIndex;
          ev.toIdx = toRow;
          MsgBoard.publish(ev, "TreeView");
          Builder.logger.debug("TV-Drag success: " +
              ((TreeItem) parentNode.getUserObject()).getKey() +
              " widget: " + item.toDebugString() +
              " from: " + fromIndex +
              " to: " + toRow
          );
        } else {
          Builder.logger.debug("TV-Drag failed: " +
              ((TreeItem) parentNode.getUserObject()).getKey() +
              " widget: " + item.toDebugString() +
              " from: " + fromIndex
          );
        }
      }
      bDraggingNode = false;
    } 

    public DefaultMutableTreeNode findParentNode(String searchKey, DefaultMutableTreeNode root){
      DefaultMutableTreeNode parent=null;
      DefaultMutableTreeNode scan=null;
      for (int i=0;i<root.getChildCount();i++) {
        scan = ((DefaultMutableTreeNode)root.getChildAt(i));
        if(searchKey.equals(((TreeItem)scan.getUserObject()).getKey())){
          parent = root;
          break;
        } else if (!treeModel.isLeaf(scan)) {
          parent=findParentNode(searchKey, (DefaultMutableTreeNode)root.getChildAt(i));
          if (parent != null) break;
        }
      }
      return parent;
    }

  }

  public int getNumberOfNodes(DefaultTreeModel model)  
  {  
      return getNumberOfNodes(model, model.getRoot());  
  }  

  private int getNumberOfNodes(DefaultTreeModel model, Object node)  
  {  
      int count = 1;
      int nChildren = model.getChildCount(node);  
      for (int i = 0; i < nChildren; i++)  
      {  
          count += getNumberOfNodes(model, model.getChild(node, i));  
      }  
      return count;  
  }
  
  @Override
  public void updateEvent(MsgEvent e) {
    TreePath treePath = null;
//  System.out.println("TreeView: " + e.toString());
    if (e.code == MsgEvent.OBJECT_SELECTED_PAGEPANE) {
      Builder.logger.debug("TreeView recv: " + e.toString());
      TreeItem pageItem = new TreeItem(e.message, null);
      DefaultMutableTreeNode w = findNode(pageItem);
      if (w != null) {
        selectWidget = ((TreeItem)w.getUserObject());
        treePath = new TreePath(w.getPath());
        tree.setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);
        repaint();
      }
    } else if (e.code == MsgEvent.OBJECT_UNSELECT_PAGEPANE) {
      Builder.logger.debug("TreeView recv: " + e.toString());
      tree.clearSelection();
      scrollPane.repaint();
    } else if (e.code == MsgEvent.WIDGET_ENUM_CHANGE ||
          e.code == MsgEvent.PAGE_ENUM_CHANGE) {
      Builder.logger.debug("TreeView recv: " + e.toString());
        TreeItem pageItem = new TreeItem(e.message, e.xdata);
        DefaultMutableTreeNode w = findNode(pageItem);
        if (w != null) {
          selectWidget = ((TreeItem)w.getUserObject());
          selectWidget.setEnum(e.xdata);
          treeModel.nodeChanged(w);
          treePath = new TreePath(w.getPath());
          tree.setSelectionPath(treePath);
          tree.scrollPathToVisible(treePath);
          repaint();
        }
    } else if (e.code == MsgEvent.TREEVIEW_RESET) {
      Builder.logger.debug("TreeView recv: " + e.toString());
      // start with all pages shown but all elements collapsed
      treePath = new TreePath(root.getPath());
      expandOrCollapsToLevel(tree, treePath, 1, false);
    } else if (e.code == MsgEvent.PAGE_TAB_CHANGE) {
        Builder.logger.debug("TreeView recv: " + e.toString());
        TreeItem pageItem = new TreeItem(e.message, null);
        DefaultMutableTreeNode w = findNode(pageItem);
        if (w != null) {
          selectWidget = ((TreeItem)w.getUserObject());
          treePath = new TreePath(w.getPath());
          tree.setSelectionPath(treePath);
          tree.scrollPathToVisible(treePath);
          currentPage = w;
          repaint();
        }
    } 
  }

  /**
   * Backup our tree model.
   * <p>
   * Since TreeModel is serializable we could do this in one
   * writeObject() but getting our view to refresh is a nightmare best avoided.
   * (Don't ask me how I know).
   * Our tree only contains string values so not a big deal.
   *
   * @param list
   *          - pairs of parent and child are added to this list as we traverse
   *          the tree.
   * @param node
   *          - parent node to search, must start with root
   */
  private void backupTree(ObjectOutputStream out, DefaultMutableTreeNode node) 
   throws IOException {

    // We have to walk our tree to find parents and children 
//  System.out.println("---" + node.toString() + "---");
    int childCount = node.getChildCount();
//    System.out.println("Child count: " + childCount);
    TreeItem item = ((TreeItem)node.getUserObject());
//    System.out.println("Parent: " + item.toDebugString());
    TreeItem child = null;
    
    if (!item.equals(rootItem)) {
      out.writeObject(item.getKey());
      out.writeObject(item.getEnum());
      out.writeObject("*");
//      System.out.println("===Root Item written");
    }
    for (int i = 0; i < childCount; i++) {

      DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
      if (childNode.getChildCount() > 0) {
//        System.out.println("===Drill deeper");
        backupTree(out, childNode);
      } else {
//        System.out.println(childNode.toString());
        child = ((TreeItem)childNode.getUserObject());
//        System.out.println("Child: " + child.toDebugString());
        if (item.equals(rootItem)) {
          out.writeObject(child.getKey());
          out.writeObject(child.getEnum());
          out.writeObject("*");
//          System.out.println("===Page Item written");
        } else {
          out.writeObject(item.getKey());
          out.writeObject(item.getEnum());
          out.writeObject(child.getKey());
          out.writeObject(child.getEnum());
//          System.out.println("===Parent<->Child written");
        }
      }
    }

  }

  public void expandOrCollapsToLevel(JTree tree, TreePath treePath, int level, boolean expand) {
    try {
      expandOrCollapsePath(tree, treePath, level, 0, expand);
    } catch (Exception e) {
      e.printStackTrace();
      // do nothing
    }
  }

  public void expandOrCollapsePath(JTree tree, TreePath treePath, int level, int currentLevel, boolean expand) {
    // System.err.println("Exp level "+currentLevel+", exp="+expand);
    if (expand && level <= currentLevel && level > 0)
      return;

    TreeNode treeNode = (TreeNode) treePath.getLastPathComponent();
    if (treeModel.getChildCount(treeNode) >= 0) {
      for (int i = 0; i < treeModel.getChildCount(treeNode); i++) {
        TreeNode n = (TreeNode) treeModel.getChild(treeNode, i);
        TreePath path = treePath.pathByAddingChild(n);
        expandOrCollapsePath(tree, path, level, currentLevel + 1, expand);
      }
      if (!expand && currentLevel < level)
        return;
    }
    if (expand) {
      tree.expandPath(treePath);
      // System.err.println("Path expanded at level "+currentLevel+"-"+treePath);
    } else {
      tree.collapsePath(treePath);
      // System.err.println("Path collapsed at level "+currentLevel+"-"+treePath);
    }
  }

/**
   * Backup all widget keys and their parent keys into a serialized string object.
   *
   * @return the <code>String</code> object
   */
  public String backup() {

    try {
//      System.out.println(">>>>tree backup");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
      // now that we have our backup list we need to write it to our stream
      int nodeCount = getNumberOfNodes(treeModel)-1; // don't include the root
//      System.out.println("Node count: " + nodeCount);
      out.writeInt(nodeCount); 
      backupTree(out, root);
      out.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
//      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
      return "";
    }

  }

  /**
   * Restore tree.
   *
   * @param in
   *          the in
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  private void restoreTree(ObjectInputStream in) 
      throws IOException, ClassNotFoundException {
//    System.out.println(">>>>tree restore");
    int nodeCount = in.readInt();
//    System.out.println("Node count: " + nodeCount);
    String parentKey;
    String parentEnum;
    String childKey;
    String childEnum;
    for (int i=0; i<nodeCount; i++) {
      parentKey=(String)in.readObject();
      parentEnum=(String)in.readObject();
      childKey=(String)in.readObject();
      if (childKey.equals("*")) {
        addPage(parentKey, parentEnum);
      } else {
        childEnum=(String)in.readObject();
        addWidget(parentKey, parentEnum, childKey, childEnum);
      }
    }

  }

  /**
   * Restore all widgets from a serialized string object.
   *
   * @param state
   *          the state backup string
   */
  public void restore(String state) {

    try {
//      System.out.println("tree restore>>>");
      byte[] data = Base64.getDecoder().decode(state);
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
      closeProject();
      restoreTree(in);
      in.close();
      repaint();
    } catch (ClassNotFoundException e) {
//      System.out.print("ClassNotFoundException occurred.");
      e.printStackTrace();
    } catch (IOException e) {
//      System.out.print("IOException occurred." + e.toString());
      e.printStackTrace();
    }

  }

}