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
package builder.commands;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.util.List;

import javax.swing.JOptionPane;

import builder.clipboard.WidgetItems;
import builder.clipboard.WidgetItemsSelection;
import builder.controller.Controller;
import builder.mementos.WidgetMemento;
import builder.models.WidgetModel;
import builder.views.PagePane;

/**
 * The Class PasteCommand will
 * paste from clipboard the selected widgets to a page.
 * 
 * @author Paul Conti
 * 
 */
public class PasteCommand extends Command implements ClipboardOwner {
  
  /** The controller. */
  private Controller controller;
  
  /** The page that holds the selected widgets. */
  private PagePane page;
  
  /** The WidgetItems contains the models the widgets on the clipboard. */
  WidgetItems items = null;
  
  /**
   * Instantiates a new paste from clipboard command.
   *
   * @param page
   *          the <code>page</code> is the object that holds the widgets
   */
  public PasteCommand(Controller controller, PagePane page) {
    this.controller = controller;
    this.page = page;
  }
  
  /**
   * Align will setup the align widget(s) to bottom command for later execution
   * and creates the required Memento object for undo/redo.
   *
   * @return <code>true</code>, if successful
   */
  public boolean paste() {
    Clipboard clipboard = controller.getClipboard();
    WidgetItemsSelection clipboardContent = (WidgetItemsSelection) clipboard.getContents(this);
    if (clipboardContent == null) return false;
    DataFlavor[] flavors = clipboardContent.getTransferDataFlavors();
    for (int i = 0; i < flavors.length; i++){
       if (flavors[i] == WidgetItemsSelection.widgetFlavor) {
        try {
          items = (WidgetItems)clipboardContent.getTransferData(flavors[i]);
          break;
        } catch (UnsupportedFlavorException e) {
          e.printStackTrace();
          return false;
        }
       }
    }
    if (items == null) {
      JOptionPane.showMessageDialog(null, 
          "You must first issue a copy or cut.",
          "Warning",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    memento = new WidgetMemento(page);  
    return true;  // success
  }

  /**
   * execute - will actually run the command
   * This will copy our widgets to the clipboard
   * 
   * @see builder.commands.Command#execute()
   */
  @Override
  public void execute() {
    // first turn off any selections on page
    page.selectNone();
    List<WidgetModel> list = items.getItems();
    /* If we are only pasting one widget 
     * work out an offset for x,y position so  
     * it doesn't get placed on top of the original.
     * More than one widget assume copying to another page
     * so keep original x and y positions.
     */
    WidgetModel m1 = list.get(0);
    int nX = m1.getX();
    int nY = m1.getY();
    if (list.size() == 1) {
      // see if we can move the element up to the left?
      if (nX-10 >= 0 && nY-10 >= 0) {
        page.addWidget(m1, nX-10, nY-10);
      } else if (nX-10 >= 0) {  // move side left?
        page.addWidget(m1, nX-10, nY);
      } else if (nY-10 >= 0) {  // move up?
        page.addWidget(m1, nX, nY-10);
      } else {  // ok, i give up. just place on top
        page.addWidget(m1, nX, nY);
      }
    } else {
      // now paste widgets one at a time from the clipboard to the target page
      for (WidgetModel m : list) {
        page.addWidget(m, m.getX(), m.getY());
      }
    }
    Controller.getInstance().refreshView();
  }

  /**
   * toString - converts command to string for debugging
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("Paste Widgets on page:%s",page.getEnum());
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    // TODO Auto-generated method stub
    
  }

}
