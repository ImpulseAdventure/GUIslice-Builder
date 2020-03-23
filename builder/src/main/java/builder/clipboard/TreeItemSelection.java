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
package builder.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * The Class TreeItemSelection.
 * Supports Drag and Drop of TreeItems inside TreeView
 * (maybe some day drag and drop between views?)
 * and ClipBoard between views.
 * 
 * @author Paul Conti
 * 
 */
public class TreeItemSelection implements Transferable, ClipboardOwner {

  /** The treeitem flavor. */
  public static DataFlavor treeitemFlavor = new DataFlavor(TreeItem.class, "TreeItem flavor");
  
  /** The selection. */
  private TreeItem selection;

  /**
   * Instantiates a new tree item selection.
   *
   * @param selection
   *          the selection
   */
  public TreeItemSelection(TreeItem selection){
     this.selection = selection;
  }

  // Transferable implementation

  /**
   * getTransferDataFlavors
   *
   * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
   */
  @Override
  public DataFlavor[] getTransferDataFlavors(){
//     System.out.println("getTransferDataFlavors");
     DataFlavor[] ret = {treeitemFlavor};
     return ret;
  }

  /**
   * isDataFlavorSupported
   *
   * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
   */
  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor){
     return treeitemFlavor.equals(flavor);
  }

  /**
   * getTransferData
   *
   * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
   */
  @Override
  public synchronized Object getTransferData (DataFlavor flavor)
     throws UnsupportedFlavorException 
  {
     if (isDataFlavorSupported(flavor)){
        return this.selection;
     } else {
        throw new UnsupportedFlavorException(treeitemFlavor);
     }
  }

  // ClipboardOwner implementation

  /**
   * lostOwnership
   * meant to inform users when logging out that clipboard stills has data
   *
   * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
   */
  @Override
  public void lostOwnership(Clipboard clipboard, Transferable transferable){
     System.out.println("TreeItemSelection: Lost ownership");
  }
  
}
