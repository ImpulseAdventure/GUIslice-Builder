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
package builder.mementos;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import builder.controller.Controller;

/**
 * <p>
 * The Class CopyMemento implements the Memento Pattern which is used
 * in our undo framework to bring an object back to a previous state.
 * The CopyMemento works with our CopyCommand.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class CopyMemento extends Memento {
  
  /** The controller. */
  private Controller controller;
  
  /**
   * Instantiates a new page memento.
   *
   * @param controller
   *          the our controller object
   */
  public CopyMemento(Controller controller) {
    this.controller = controller;
  }

  /**
   * restore
   * This memento simply empties the clipboard
   * Maybe a future version would restore to any previous contents?
   * @see builder.mementos.Memento#restore()
   */
  @Override
  public void restore() {
    // clear out clipboard
    Clipboard clipboard = controller.getClipboard();
    clipboard.setContents(new Transferable() {
      public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[0];
      }

      public boolean isDataFlavorSupported(DataFlavor flavor) {
        return false;
      }

      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        throw new UnsupportedFlavorException(flavor);
      }
      
    }, null);
    Controller.getInstance().refreshView();
  }

}
