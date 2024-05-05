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

  private static int MIN_SIZE = 10;

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

  /**
   * Moves the widget by the given offset. If preserveSize is true, the widget size will be preserved. 
   * Thanks to this, the widget can be moved in X or Y direction only.
   * 
   * @param point
   *          the point to move the widget to
   * @param preserveSize
   *          preserve the size of the widget
   */
  public void move(Point point, boolean preserveSize) {
    WidgetModel model = widget.getModel();
    boolean updateX = false;
    boolean updateY = false;
    switch (handleType) {
      case TOP: 
        updateY = handleTop(point, model, preserveSize);
        break;
      case TOP_LEFT:
        updateY = handleTop(point, model, preserveSize);
        updateX = handleLeft(point, model, preserveSize);
        break;
      case TOP_RIGHT:
        updateY = handleTop(point, model, preserveSize);
        updateX = handleRight(point, model, preserveSize);
        break;
      case RIGHT: 
        updateX = handleRight(point, model, preserveSize);
        break;
      case BOTTOM: 
        updateY = handleBottom(point, model, preserveSize);
        break;
      case BOTTOM_RIGHT:
        updateY = handleBottom(point, model, preserveSize);
        updateX = handleRight(point, model, preserveSize);
        break;
      case BOTTOM_LEFT:
        updateY = handleBottom(point, model, preserveSize);
        updateX = handleLeft(point, model, preserveSize);
        break;
      case LEFT: 
        updateX = handleLeft(point, model, preserveSize);
        break;
      default:
        break;
    }
    // do not update the last point if minimum size is reached
    if (updateX) { lastPoint.x = point.x; }
    if (updateY) { lastPoint.y = point.y; }
  }

  public void move(Point point) {
    move(point, false);
  }  

  private boolean handleLeft(Point point, WidgetModel model, boolean preserveSize) {
    int newX = model.getX() + (point.x - lastPoint.x);
    int newWidth = initialBounds.width - (newX - initialBounds.x);
    if (preserveSize) {
      model.setX(newX);
      return true;
    }
    if (newWidth < MIN_SIZE) {
      return false;
    }
    model.setX(newX);
    model.setWidth(newWidth);
    return true;
  }

  private boolean handleBottom(Point point, WidgetModel model, boolean preserveSize) {
    if (preserveSize) {
      return handleTop(point, model, true);
    } else {
      int newHeight = model.getHeight() + (point.y - lastPoint.y);
      if (newHeight < MIN_SIZE) {
        return false;
      }
      model.setHeight(newHeight);
    }
    return true;
  }

  private boolean handleRight(Point point, WidgetModel model, boolean preserveSize) {
    if (preserveSize) {
      return handleLeft(point, model, true);
    } else {
      int newWidth = model.getWidth() + (point.x - lastPoint.x);
      if (newWidth < MIN_SIZE) {
        return false;
      }
      model.setWidth(newWidth);
    }
    return true;
  }

  private boolean handleTop(Point point, WidgetModel model, boolean preserveSize) {
    int newY = model.getY() + (point.y - lastPoint.y);
    int newHeight = initialBounds.height - (newY - initialBounds.y);
    if (preserveSize) {
      model.setY(newY);
      return true;
    }    
    if (newHeight < MIN_SIZE) {
      return false;
    }
    model.setY(newY);
    model.setHeight(newHeight);
    return true;
  }
}
