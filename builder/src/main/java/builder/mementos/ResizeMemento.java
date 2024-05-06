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
package builder.mementos;

import java.util.ArrayList;
import java.util.List;

import builder.models.LineModel;
import builder.models.WidgetModel;
import builder.views.PagePane;
import builder.widgets.LineWidget;
import builder.widgets.Widget;

/**
 * <p>
 * The Class ResizeMemento implements the Memento Pattern which is
 * used in our undo framework to bring an object back to a previous state.
 * </p>
 * The ResizeMemento works with the Resize Widget command.
 * 
 * @author etet100
 */
public class ResizeMemento extends Memento {
  
  /** The widget being resized. */
  private Widget widget = null;
  
  /** The saved widget properties. */
  private List<Integer> savedProperties = new ArrayList<Integer>();
  
  /** The page. */
  private PagePane page;
  
  /**
   * Instantiates a new resize memento.
   *
   * @param page
   *          the page containing the moving widgets.
   * @param list
   *          the list of widgets being moved.
   */
  public ResizeMemento(PagePane page, Widget widget) {
    this.page = page;
    this.widget = widget;
    saveWidgetProperties(widget);
  }

  /**
   * saveWidgetProperties - position and size
   * 
   * @param widget
   */
  private void saveWidgetProperties(Widget widget) {
    savedProperties.add((Integer) widget.getModel().getValueAt(WidgetModel.PROP_X, 1));
    savedProperties.add((Integer) widget.getModel().getValueAt(WidgetModel.PROP_Y, 1));
    // @TODO noo!!! 
    if (widget instanceof LineWidget) {
      savedProperties.add((Integer) widget.getModel().getValueAt(LineModel.PROP_LENGTH, 1));
    } else {
      savedProperties.add((Integer) widget.getModel().getValueAt(WidgetModel.PROP_WIDTH, 1));
      savedProperties.add((Integer) widget.getModel().getValueAt(WidgetModel.PROP_HEIGHT, 1));
    }
  }
  
  /**
   * restore
   *
   * @see builder.mementos.Memento#restore()
   */
  @Override
  public void restore() {
    widget.getModel().restore(savedProperties.get(0), WidgetModel.PROP_X);
    widget.getModel().restore(savedProperties.get(1), WidgetModel.PROP_Y);
    // @TODO noo!!!
    if (widget instanceof LineWidget) {
      widget.getModel().restore(savedProperties.get(2), LineModel.PROP_LENGTH);
    } else {
      widget.getModel().restore(savedProperties.get(2), WidgetModel.PROP_WIDTH);
      widget.getModel().restore(savedProperties.get(3), WidgetModel.PROP_HEIGHT);
    }
    page.refreshView();
  }
}
