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
package builder.commands;

import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import builder.clipboard.WidgetItems;
import builder.clipboard.WidgetItemsSelection;
import builder.controller.Controller;
import builder.mementos.CutMemento;
import builder.models.WidgetModel;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class CutCommand will
 * delete the widgets from the project
 * and add thgem to the clipboard.
 * 
 * @author Paul Conti
 * 
 */
public class CutCommand extends Command {
  
  /** The controller. */
  private Controller controller;
  
  /** The page that holds the selected widgets. */
  PagePane page;
  
  /** The group list contains the models of all the selected widgets 
   * that will copied to the clipboard.
   */
  private List<WidgetModel> groupList = new ArrayList<WidgetModel>();
  
  /**
   * Instantiates a new del widget command and
   * creates the required Memento object for undo/redo.
   *
   * @param page
   *          the <code>page</code> object that contains the selected widgets.
   */
  public CutCommand(Controller controller, PagePane page) {
    this.page = page;
    this.controller = controller;
  }
  
  /**
   * Del will setup the delete widget(s) command.
   *
   * @return <code>true</code>, if successful
   */
  public boolean cut() {
    List<Widget> list = page.getSelectedList();
    if (list.size() < 1) {
      JOptionPane.showMessageDialog(null, 
          "You must first select elements to cut.",
          "Warning",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    // add to groupList selected widgets
    for (Widget w : list) {
      WidgetModel m = w.getModel();
      groupList.add(m);
    }
    memento = new CutMemento(controller, page);
    return true;
  }

  /**
   * execute - perform theactual deleteion of the widget from the project.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 s                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
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
    // finally delete the selected widgets from the page
    for (WidgetModel m : groupList) {
      page.delWidget(m);
    }
  }

  /**
   * toString
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("Delete widget: ");
  }

}
