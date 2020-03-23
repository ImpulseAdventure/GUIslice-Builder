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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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
import builder.common.CommonUtils;
import builder.controller.Controller;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.events.iSubscriber;

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
  
  /** The image icon. */
  private ImageIcon imageIcon;
  
  /** The parent icon. */
  private ImageIcon parentIcon;
  
  /** The scroll pane. */
  private JScrollPane scrollPane;
  
  /** The lastest backup. */
  private String lastestBackup = null;
  
  /** The b dragging node. */
  private boolean bDraggingNode = false;
  
  /** The instance. */
  private static TreeView instance = null;
  
  /** The currently selected widget */
  private TreeItem selectWidget = null;
  
  /** The root object */
  private TreeItem rootItem;
  
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
    initUI();
    MsgBoard.getInstance().subscribe(this, "TreeView");
  }
  
  /**
   * Initializes the UI.
   */
  private void initUI() {
    // create the root node
    rootItem = new TreeItem("Root", null);
    root = new DefaultMutableTreeNode(rootItem);

    treeModel = new DefaultTreeModel(root);

    // create the tree by passing in our tree model
    tree = new JTree(treeModel);
    imageIcon = new ImageIcon(Builder.class.getResource("/resources/icons/misc/widget.png"));
    parentIcon = new ImageIcon(Builder.class.getResource("/resources/icons/misc/brick.png"));
    
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setOpenIcon(parentIcon);
    renderer.setClosedIcon(parentIcon);
    renderer.setLeafIcon(imageIcon);
    tree.setCellRenderer(renderer);

    tree.setRootVisible(false);
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
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
            MsgBoard.getInstance().sendEvent("TreeView", MsgEvent.OBJECT_SELECTED_TREEVIEW,
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
     CommonUtils cu = CommonUtils.getInstance();
    this.setFrameIcon(cu.getResizableIcon("resources/icons/guislicebuilder.png"));
    this.setPreferredSize(new Dimension(210, 400));
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
        MsgBoard.getInstance().sendEvent("TreeView",MsgEvent.OBJECT_UNSELECT_TREEVIEW,
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
    return lastestBackup;
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

    // It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
    treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

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
   * The Class MyTreeCellEditor will prevent anyone from editing a widget node.
   * Since we only show keys; A user changing a key value would crash the system.
   */
  private static class MyTreeCellEditor extends DefaultTreeCellEditor {

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
        return true;  // weird to return true but this allows us to append to end of branch
      }
      TreePath parentPath = dropLocation.getPath();

      parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
 
      // do not allow parent to be root
//      if (tiParent.getKey().equals("Root"))
//        return false;
      dropIndex = dropLocation.getChildIndex();
      numberChildren = parentNode.getChildCount();
      
      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      TreePath dropPath = new TreePath(selectedNode.getPath());

      // Do not allow MOVE-action drops if a non-leaf node is selected
      // non-leaf node?
      if(!selectedNode.isLeaf()) {
        return false;
      }
      
      // Do not allow moves between parent nodes
      if (!parentPath.isDescendant(dropPath)) {
        return false;
      }

      // make sure we have a valid drop point
      bDraggingNode = dropLocation.getPath() != null;
      if (bDraggingNode) {
        lastestBackup = backup(); // save for undo command
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
      
      /* add the new node to the tree path
       * 
       * JTree has an implementation issue with moving a leaf node to the end of a 
       * branch. There is a reported bug in jvm 1.8 and earlier jvm's about trying 
       * to drag pass the end leaf of branch of a JTree.
       * example:
       *   PageMain
       *     TXTBUtton_1 <-- select and drag below TXT_Button_2
       *     TXTButton_2
       * Supposed to be fixed in jvm 1.9 but I have tested to jvm 11 and still broken
       * and seems to be treated as a feature now.
       *
       * So..., I check here for root and if thats the case I use my own routine 
       * to find parent node and muck around a bit with creating a new path.
       * Sadly, this has the weird behavior when users instead drags a leaf
       * above the container (page) of moving it pass the end of the containing
       * branch.  No way to fix that but its not harmful so... 
       * 
       * A destination index value of -1 means that the user dropped the node 
       * over an empty part of the tree.  This means Root for us since we are
       * a simple two level tree.
       * If so, we will add the node to the end its branch.
       * By the way, for more than two level tree this doesn't work at all.
       * 
       * An alternative implementation might be to add an empty leaf node
       * at the end of each branch when creating a new container.
       * example: 
       *   PageMain
       *     TXTBUtton_1 <-- select and drag below TXT_Button_2
       *     TXTButton_2
       *     "" empty node
       * This also works but you can't avoid showing the empty node to users
       * easily. It is simple to prevent selecting and dragging the empty node though.    
       */
      TreePath newPath;
      if (dropIndex == -1) {
        // getLastPathComponent() will find the parent node associated with the drop location
        dropIndex = treeModel.getChildCount(destPath.getLastPathComponent()); 
        parentNode = findParentNode(((TreeItem)newNode.getUserObject()).getKey(), root);
        dropIndex = treeModel.getChildCount(parentNode); 
        fromIndex = getSelectedIndex(parentNode, ((TreeItem) newNode.getUserObject())); 
        // add the new node to the tree path
        treeModel.insertNodeInto(newNode, parentNode, dropIndex);
        newPath = getPath(newNode);
      } else {
        fromIndex = getSelectedIndex(parentNode, ((TreeItem) newNode.getUserObject())); 
        treeModel.insertNodeInto(newNode, parentNode, dropIndex);
        newPath = destPath.pathByAddingChild(newNode);
        
      }
 
      // ensure the new path element is visible.
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
        MsgEvent ev = new MsgEvent();
        ev.code = MsgEvent.WIDGET_CHANGE_ZORDER;
        ev.message = item.getKey();
        ev.xdata = ((TreeItem) parentNode.getUserObject()).getKey();
        ev.fromIdx = fromIndex;
        ev.toIdx = toRow;
        MsgBoard.getInstance().publish(ev, "TreeView");
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
//    System.out.println("TreeView: " + e.toString());
    if (e.code == MsgEvent.OBJECT_SELECTED_PAGEPANE ||
        e.code == MsgEvent.PAGE_TAB_CHANGE) {
//  System.out.println("TreeView: " + e.toString());
      TreeItem pageItem = new TreeItem(e.message, null);
      DefaultMutableTreeNode w = findNode(pageItem);
      if (w != null) {
        selectWidget = ((TreeItem)w.getUserObject());
        TreePath path = new TreePath(w.getPath());
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
        repaint();
      }
    } else if (e.code == MsgEvent.OBJECT_UNSELECT_PAGEPANE) {
//  System.out.println("TreeView: " + e.toString());
      tree.clearSelection();
      scrollPane.repaint();
    } else if (e.code == MsgEvent.WIDGET_ENUM_CHANGE ||
          e.code == MsgEvent.PAGE_ENUM_CHANGE) {
//  System.out.println("TreeView: " + e.toString());
        TreeItem pageItem = new TreeItem(e.message, null);
        DefaultMutableTreeNode w = findNode(pageItem);
        if (w != null) {
          selectWidget = ((TreeItem)w.getUserObject());
          selectWidget.setEnum(e.xdata);
          treeModel.nodeChanged(w);
          TreePath path = new TreePath(w.getPath());
          tree.setSelectionPath(path);
          tree.scrollPathToVisible(path);
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