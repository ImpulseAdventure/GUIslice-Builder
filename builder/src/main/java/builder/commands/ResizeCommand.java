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

import builder.common.Utils;
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
   * Stores calculated bounds before snapping to grid.
   */
  private Rectangle currentBounds;

  /**
   * The corner that is being dragged.
   */
  private HandleType handleType;

  private static int MIN_SIZE = 10;

  private Utils utils = Utils.getInstance();

  public ResizeCommand(PagePane page, Widget widget, HandleType handleType) {
    this.page = page;
    this.widget = widget;
    this.initialBounds = widget.getWinBounded();
    this.currentBounds = widget.getWinBounded();
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
  public void move(Point point, boolean preserveSize, boolean ignoreSnapToGrid) {
    WidgetModel model = widget.getModel();
    boolean updateX = false;
    boolean updateY = false;
    switch (handleType) {
      case TOP: 
        updateY = handleTop(point, model, preserveSize, ignoreSnapToGrid);
        break;
      case TOP_LEFT:
        updateY = handleTop(point, model, preserveSize, ignoreSnapToGrid);
        updateX = handleLeft(point, model, preserveSize, ignoreSnapToGrid);
        break;
      case TOP_RIGHT:
        updateY = handleTop(point, model, preserveSize, ignoreSnapToGrid);
        updateX = handleRight(point, model, preserveSize, ignoreSnapToGrid);
        break;
      case RIGHT: 
        updateX = handleRight(point, model, preserveSize, ignoreSnapToGrid);
        break;
      case BOTTOM: 
        updateY = handleBottom(point, model, preserveSize, ignoreSnapToGrid);
        break;
      case BOTTOM_RIGHT:
        updateY = handleBottom(point, model, preserveSize, ignoreSnapToGrid);
        updateX = handleRight(point, model, preserveSize, ignoreSnapToGrid);
        break;
      case BOTTOM_LEFT:
        updateY = handleBottom(point, model, preserveSize, ignoreSnapToGrid);
        updateX = handleLeft(point, model, preserveSize, ignoreSnapToGrid);
        break;
      case LEFT: 
        updateX = handleLeft(point, model, preserveSize, ignoreSnapToGrid);
        break;
      default:
        break;
    }
    // do not update the last point if minimum size is reached
    if (updateX) { lastPoint.x = point.x; }
    if (updateY) { lastPoint.y = point.y; }
  }

  private boolean handleLeft(Point point, WidgetModel model, boolean preserveSize, boolean ignoreSnapToGrid) {
    int newPos = currentBounds.x + (point.x - lastPoint.x);
    int newSnappedPos = ignoreSnapToGrid ? newPos : utils.snapToGrid(newPos);
    int newSize = initialBounds.width - (newPos - initialBounds.x);
    newSize -= newSnappedPos - newPos; // adjust width to snapped position
    if (preserveSize) {
      currentBounds.x = newPos;
      model.setX(newSnappedPos);
      return true;
    }
    if (newSize < MIN_SIZE) {
      return false;
    }
    currentBounds.x = newPos;
    model.setX(newSnappedPos);
    model.setWidth(newSize);
    return true;
  }

  private boolean handleBottom(Point point, WidgetModel model, boolean preserveSize, boolean ignoreSnapToGrid) {
    if (preserveSize) {
      return handleTop(point, model, true, true);
    } else {
      int newSize = currentBounds.height + (point.y - lastPoint.y);
      int newSnappedSize = ignoreSnapToGrid ? newSize : (utils.snapToGrid(currentBounds.y + newSize) - currentBounds.y);
      if (newSnappedSize < MIN_SIZE) {
        return false;
      }
      currentBounds.height = newSize;
      model.setHeight(newSnappedSize);
    }
    return true;
  }

  private boolean handleRight(Point point, WidgetModel model, boolean preserveSize, boolean ignoreSnapToGrid) {
    if (preserveSize) {
      return handleLeft(point, model, true, true);
    } else {
      int newSize = currentBounds.width + (point.x - lastPoint.x);
      int newSnappedSize = ignoreSnapToGrid ? newSize : (utils.snapToGrid(currentBounds.x + newSize) - currentBounds.x);
      if (newSnappedSize < MIN_SIZE) {
        return false;
      }
      currentBounds.width = newSize;
      model.setWidth(newSnappedSize);
    }
    return true;
  }

  private boolean handleTop(Point point, WidgetModel model, boolean preserveSize, boolean ignoreSnapToGrid) {
    int newPos = currentBounds.y + (point.y - lastPoint.y);
    int newSnappedPos = ignoreSnapToGrid ? newPos : utils.snapToGrid(newPos);
    int newSize = initialBounds.height - (newPos - initialBounds.y);
    newSize -= newSnappedPos - newPos; // adjust height to snapped position
    if (preserveSize) {
      currentBounds.y = newPos;
      model.setY(newSnappedPos);
      return true;
    }    
    if (newSize < MIN_SIZE) {
      return false;
    }
    currentBounds.y = newPos;
    model.setY(newSnappedPos);
    model.setHeight(newSize);
    return true;
  }
}
