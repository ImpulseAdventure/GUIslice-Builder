/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
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
 * The Class WidgetItemsSelection.
 * Supports Cut, Copy and Paste of Widgets
 * using a Local Clipboard.
 * 
 * @author Paul Conti
 * 
 */
public class WidgetItemsSelection implements Transferable, ClipboardOwner {

  /** The widget flavor. */
  public static DataFlavor widgetFlavor = new DataFlavor(WidgetItems.class, "Widget flavor");
  
  /** The selection. */
  private WidgetItems selection;

  /**
   * Instantiates a new tree item selection.
   *
   * @param selection
   *          the selection
   */
  public WidgetItemsSelection(WidgetItems selection){
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
     DataFlavor[] ret = {widgetFlavor};
     return ret;
  }

  /**
   * isDataFlavorSupported
   *
   * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
   */
  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor){
     return widgetFlavor.equals(flavor);
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
        throw new UnsupportedFlavorException(widgetFlavor);
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
     System.out.println("WidgetItemsSelection: Lost ownership");
  }
  
}
