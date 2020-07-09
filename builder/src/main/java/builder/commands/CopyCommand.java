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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import builder.clipboard.WidgetItems;
import builder.clipboard.WidgetItemsSelection;
import builder.controller.Controller;
import builder.mementos.CopyMemento;
import builder.models.WidgetModel;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class CopyCommand will
 * align the selected widgets to the bottom most widget.
 * 
 * @author Paul Conti
 * 
 */
public class CopyCommand extends Command {
  
  /** The controller. */
  private Controller controller;
  
  /** The page that holds the selected widgets. */
  private PagePane page;
  
  /** The group list contains the models of all the selected widgets 
   * that will copied to the clipboard.
   */
  private List<WidgetModel> groupList = new ArrayList<WidgetModel>();
  
  /**
   * Instantiates a new align bottom command.
   *
   * @param page
   *          the <code>page</code> is the object that holds the widgets
   */
  public CopyCommand(Controller controller, PagePane page) {
    this.controller = controller;
    this.page = page;
  }
  
  /**
   * Align will setup the align widget(s) to bottom command for later execution
   * and creates the required Memento object for undo/redo.
   *
   * @return <code>true</code>, if successful
   */
  public boolean copy() {
    List<Widget> list = page.getSelectedList();
    if (list.size() < 1) {
      JOptionPane.showMessageDialog(null, 
          "You must first select elements to copy.",
          "Warning",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    // add to groupList selected widgets
    for (Widget w : list) {
      WidgetModel m = w.getModel();
      groupList.add(m);
    }
    memento = new CopyMemento(controller);  // empty mem
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
    Clipboard clipboard = controller.getClipboard();
    // make sure we start with an empty clipboard
    clipboard.setContents(new WidgetItemsSelection(null), null);
    // now place selected widgets onto the clipboard
    WidgetItems items = new WidgetItems(groupList);
    WidgetItemsSelection selection = new WidgetItemsSelection(items);
    clipboard.setContents(selection, null);
    // turn off selections
    page.selectNone();
    page.refreshView();
  }

  /**
   * toString - converts command to string for debugging
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String myEnums = "";
    WidgetModel m = null;
    for(int i=0; i<groupList.size(); i++) {
      m = groupList.get(i);
      if (i > 0) myEnums = myEnums + ",";
      myEnums = myEnums + m.getEnum();
    }
    return String.format("Copy Widgets: %s",myEnums);
  }

}
