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

import java.awt.Point;
import java.awt.Rectangle;

import builder.mementos.ResizeMemento;
import builder.models.WidgetModel;
import builder.views.PagePane;
import builder.widgets.Widget;
import builder.widgets.Widget.HandleType;

/**
 * @author etet100
 */
public class ResizeCommand extends Command {

  /**
   * The page that holds the selected widget.
   */
  private PagePane page;

  private Point lastPoint;

  /**
   * The widget that is being resized.
   */
  private Widget widget;
  private Rectangle initialBounds;

  /**
   * The corner that is being dragged.
   */
  private HandleType handleType;

  public ResizeCommand(PagePane page, Widget widget, HandleType handleType) {
    this.page = page;
    this.widget = widget;
    this.initialBounds = widget.getWinBounded();
    this.handleType = handleType;
  }
  
  public void start(Point point) {
    lastPoint = point;
    memento = new ResizeMemento(page, widget);
  }

  public void stop() {
    page.repaint();
  }

  public void move(Point point) {
    WidgetModel model = widget.getModel();
    switch (handleType) {
      case TOP: 
        handleTop(point, model);
        break;
      case TOP_LEFT:
        handleTop(point, model);
        handleLeft(point, model);
        break;
      case TOP_RIGHT:
        handleTop(point, model);
        handleRight(point, model);
        break;
      case RIGHT: 
        handleRight(point, model);
        break;
      case BOTTOM: 
        handleBottom(point, model);
        break;
      case BOTTOM_RIGHT:
        handleBottom(point, model);
        handleRight(point, model);
        break;
      case BOTTOM_LEFT:
        handleBottom(point, model);
        handleLeft(point, model);
        break;
      case LEFT: 
        handleLeft(point, model);
        break;
      default:
        break;
    }
    lastPoint = point;
  }

  private void handleLeft(Point point, WidgetModel model) {
    int newX = model.getX() + (point.x - lastPoint.x);
    model.setX(newX);
    int newWidth = initialBounds.width - (newX - initialBounds.x);
    model.setWidth(newWidth);
  }

  private void handleBottom(Point point, WidgetModel model) {
    int newHeight = model.getHeight() + (point.y - lastPoint.y);
    model.setHeight(newHeight);
  }

  private void handleRight(Point point, WidgetModel model) {
    int newWidth = model.getWidth() + (point.x - lastPoint.x);
    model.setWidth(newWidth);
  }

  private void handleTop(Point point, WidgetModel model) {
    int newY = model.getY() + (point.y - lastPoint.y);
    model.setY(newY);
    int newHeight = initialBounds.height - (newY - initialBounds.y);
    model.setHeight(newHeight);
  }
}
