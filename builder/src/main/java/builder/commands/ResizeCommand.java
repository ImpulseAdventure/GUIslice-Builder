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

import builder.common.Snapper;
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

  /**
   * Services used to find the best matching snapping to snap to.
   */
  private Snapper hSnapper;
  private Snapper vSnapper;

  public ResizeCommand(PagePane page, Widget widget, HandleType handleType, Snapper hSnapper, Snapper vSnapper) {
    this.page = page;
    this.widget = widget;
    this.initialBounds = widget.getWinBounded();
    this.currentBounds = widget.getWinBounded();
    this.handleType = handleType;
    this.hSnapper = hSnapper;
    this.vSnapper = vSnapper;
  }

  public Snapper getHorizontalSnapper() {
    return hSnapper;
  }

  public Snapper getVerticalSnapper() {
    return vSnapper;
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
  public void move(Point point, boolean preserveSize, boolean doNotSnap) {
    WidgetModel model = widget.getModel();
    boolean updateX = false;
    boolean updateY = false;
    switch (handleType) {
      case TOP: 
        updateY = handleTop(point, model, preserveSize, doNotSnap);
        break;
      case TOP_LEFT: {
        updateY = handleTop(point, model, preserveSize, doNotSnap);
        updateX = handleLeft(point, model, preserveSize, doNotSnap);
        break;
      }
      case TOP_RIGHT: {
        updateY = handleTop(point, model, preserveSize, doNotSnap);
        updateX = handleRight(point, model, preserveSize, doNotSnap);
        break;
      }
      case RIGHT:
        updateX = handleRight(point, model, preserveSize, doNotSnap);
        break;
      case BOTTOM:
        updateY = handleBottom(point, model, preserveSize, doNotSnap);
        break;
      case BOTTOM_RIGHT:
        updateY = handleBottom(point, model, preserveSize, doNotSnap);
        updateX = handleRight(point, model, preserveSize, doNotSnap);
        break;
      case BOTTOM_LEFT:
        updateY = handleBottom(point, model, preserveSize, doNotSnap);
        updateX = handleLeft(point, model, preserveSize, doNotSnap);
        break;
      case BOTTOM_LEFT_PROPORTIONAL:
        handleBottomLeftProportional(point, model, preserveSize, doNotSnap);
        break;
      case BOTTOM_RIGHT_PROPORTIONAL:
        handleBottomRightProportional(point, model, preserveSize, doNotSnap);
        break;
      case TOP_LEFT_PROPORTIONAL:
        handleTopLeftProportional(point, model, preserveSize, doNotSnap);
        break;
      case TOP_RIGHT_PROPORTIONAL:
        handleTopRightProportional(point, model, preserveSize, doNotSnap);
        break;
      case LEFT: 
        updateX = handleLeft(point, model, preserveSize, doNotSnap);
        break;
      default:
        break;
    }
    // do not update the last point if minimum size is reached
    if (updateX) { lastPoint.x = point.x; }
    if (updateY) { lastPoint.y = point.y; }
  }

  private boolean handleLeft(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    int newPos = currentBounds.x + (point.x - lastPoint.x);
    int newSnappedPos = doNotSnap ? newPos : hSnapper.snap(newPos, Snapper.SourceEdge.MIN);
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

  private boolean handleBottom(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    int newSize = currentBounds.height + (point.y - lastPoint.y);
    int newSnappedSize = doNotSnap ? newSize : (vSnapper.snap(currentBounds.y + newSize, Snapper.SourceEdge.MAX) - currentBounds.y);
    if (newSnappedSize < MIN_SIZE) {
      return false;
    }
    if (preserveSize) {
      // order of operations is important!
      model.setY((currentBounds.y + newSnappedSize) - currentBounds.height);
      currentBounds.y += (newSize - currentBounds.height);
    } else {
      currentBounds.height = newSize;
      model.setHeight(newSnappedSize);
    }    
    return true;
  }

  private void handleBottomLeftProportional(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    // calculate the projection of the cursor position on a line passing through the center of the object and the dragged vertex
    Point vertexToCursor = getCursorRelativeToVertex(point, new Point(initialBounds.x, initialBounds.y + initialBounds.height));
    Double bParam = getBParam(vertexToCursor, -1);
    // we need one of the coordinates of the mapped point, preferably with positive value
    int mappedCoordinate = (int) (-(bParam / 2));
    //System.out.println("vertexToCursor: " + vertexToCursor + ",bParam: " + bParam + ", mappedCoordinate: " + mappedCoordinate);

    int newPosX = initialBounds.x - mappedCoordinate;
    int newSnappedPosX = doNotSnap ? newPosX : hSnapper.snap(newPosX, Snapper.SourceEdge.MIN);
    int newSnappedSizeX = initialBounds.width - (newPosX - initialBounds.x);
    newSnappedSizeX -= newSnappedPosX - newPosX; // adjust width to snapped position
    if (newSnappedSizeX < MIN_SIZE) {
      newSnappedSizeX = MIN_SIZE;
      newSnappedPosX = initialBounds.x + initialBounds.width - newSnappedSizeX;
    }
    int snappedDistanceX = Math.abs(newSnappedPosX - newPosX);

    int newSizeY = initialBounds.height + mappedCoordinate;
    int newSnappedSizeY = doNotSnap ? newSizeY : (vSnapper.snap(initialBounds.y + newSizeY, Snapper.SourceEdge.MAX) - initialBounds.y);
    if (newSnappedSizeY < MIN_SIZE) { newSnappedSizeY = MIN_SIZE; }
    int snappedDistanceY = Math.abs(newSnappedSizeY - newSizeY);

    // snap the axis that is closer to the grid
    if (snappedDistanceX < snappedDistanceY) {
      model.setX(newSnappedPosX);
      model.setWidth(newSnappedSizeX);
    } else {
      model.setHeight(newSnappedSizeY);
      model.setX((initialBounds.x + initialBounds.width) - newSnappedSizeY); // adjust y position to maintain aspect ratio
    }
  }

  private void handleTopLeftProportional(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    // calculate the projection of the cursor position on a line passing through the center of the object and the dragged vertex
    Point vertexToCursor = getCursorRelativeToVertex(point, new Point(initialBounds.x, initialBounds.y));
    Double bParam = getBParam(vertexToCursor, 1);
    // we need one of the coordinates of the mapped point, preferably with positive value
    int mappedCoordinate = (int) (bParam / 2);
    //System.out.println("vertexToCursor: " + vertexToCursor + ",bParam: " + bParam + ", mappedCoordinate: " + mappedCoordinate);  

    int newPosX = initialBounds.x - mappedCoordinate;
    int newSnappedPosX = doNotSnap ? newPosX : hSnapper.snap(newPosX, Snapper.SourceEdge.MIN);
    int newSnappedSizeX = initialBounds.width - (newPosX - initialBounds.x);
    newSnappedSizeX -= newSnappedPosX - newPosX; // adjust width to snapped position
    if (newSnappedSizeX < MIN_SIZE) {
      newSnappedSizeX = MIN_SIZE;
      newSnappedPosX = initialBounds.x + initialBounds.width - newSnappedSizeX;
    }
    int snappedDistanceX = Math.abs(newSnappedPosX - newPosX);

    int newPosY = initialBounds.y - mappedCoordinate;
    int newSnappedPosY = doNotSnap ? newPosY : vSnapper.snap(newPosY, Snapper.SourceEdge.MIN);
    int newSnappedSizeY = initialBounds.height - (newPosY - initialBounds.y);
    newSnappedSizeY -= newSnappedPosY - newPosY; // adjust height to snapped position
    if (newSnappedSizeY < MIN_SIZE) {
      newSnappedSizeY = MIN_SIZE;
      newSnappedPosY = initialBounds.y + initialBounds.height - newSnappedSizeY;
    }
    int snappedDistanceY = Math.abs(newSnappedPosY - newPosY);

    // snap the axis that is closer to the grid
    if (snappedDistanceX < snappedDistanceY) {
      model.setX(newSnappedPosX);
      model.setWidth(newSnappedSizeX);
      model.setY((initialBounds.y + initialBounds.height) - newSnappedSizeX); // adjust y position to maintain aspect ratio
    } else {
      model.setY(newSnappedPosY);
      model.setHeight(newSnappedSizeY);
      model.setX((initialBounds.x + initialBounds.width) - newSnappedSizeY); // adjust x position to maintain aspect ratio
    }
  }

  private void handleTopRightProportional(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    // calculate the projection of the cursor position on a line passing through the center of the object and the dragged vertex
    Point vertexToCursor = getCursorRelativeToVertex(point, new Point(initialBounds.x + initialBounds.width, initialBounds.y));
    Double bParam = getBParam(vertexToCursor, -1);
    // we need one of the coordinates of the mapped point, preferably with positive value
    int mappedCoordinate = (int) (bParam / 2);
    //System.out.println("vertexToCursor: " + vertexToCursor + ",bParam: " + bParam + ", mappedCoordinate: " + mappedCoordinate); 

    int newSizeX = initialBounds.width + mappedCoordinate;
    int newSnappedSizeX = doNotSnap ? newSizeX : (hSnapper.snap(initialBounds.x + newSizeX, Snapper.SourceEdge.MAX) - initialBounds.x);
    if (newSnappedSizeX < MIN_SIZE) { newSnappedSizeX = MIN_SIZE; }
    int snappedDistanceX = Math.abs(newSnappedSizeX - newSizeX);

    int newPosY = initialBounds.y - mappedCoordinate;
    int newSnappedPosY = doNotSnap ? newPosY : vSnapper.snap(newPosY, Snapper.SourceEdge.MIN);
    int newSnappedSizeY = initialBounds.height - (newPosY - initialBounds.y);
    newSnappedSizeY -= newSnappedPosY - newPosY; // adjust height to snapped position
    if (newSnappedSizeY < MIN_SIZE) {
      newSnappedSizeY = MIN_SIZE;
      newSnappedPosY = initialBounds.y + initialBounds.height - newSnappedSizeY;
    }
    int snappedDistanceY = Math.abs(newSnappedPosY - newPosY);

    // snap the axis that is closer to the grid
    if (snappedDistanceX < snappedDistanceY) {
      model.setWidth(newSnappedSizeX);
      model.setY((initialBounds.y + initialBounds.height) - newSnappedSizeX); // adjust y position to maintain aspect ratio
    } else {
      model.setY(newSnappedPosY);
      model.setHeight(newSnappedSizeY);
    }
  }

  private void handleBottomRightProportional(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    // calculate the projection of the cursor position on a line passing through the center of the object and the dragged vertex
    Point vertexToCursor = getCursorRelativeToVertex(point, new Point(initialBounds.x + initialBounds.width, initialBounds.y + initialBounds.height));
    Double bParam = getBParam(vertexToCursor, 1);
    // we need one of the coordinates of the mapped point, preferably with positive value
    int mappedCoordinate = (int) (-(bParam / 2));
    //System.out.println("vertexToCursor: " + vertexToCursor + ",bParam: " + bParam + ", mappedCoordinate: " + mappedCoordinate);

    int newSizeX = initialBounds.width + mappedCoordinate;
    int newSnappedSizeX = doNotSnap ? newSizeX : (hSnapper.snap(initialBounds.x + newSizeX, Snapper.SourceEdge.MAX) - initialBounds.x);
    if (newSnappedSizeX < MIN_SIZE) { newSnappedSizeX = MIN_SIZE; }
    int snappedDistanceX = Math.abs(newSnappedSizeX - newSizeX);

    int newSizeY = initialBounds.height + mappedCoordinate;
    int newSnappedSizeY = doNotSnap ? newSizeY : (vSnapper.snap(initialBounds.y + newSizeY, Snapper.SourceEdge.MAX) - initialBounds.y);
    if (newSnappedSizeY < MIN_SIZE) { newSnappedSizeY = MIN_SIZE; }
    int snappedDistanceY = Math.abs(newSnappedSizeY - newSizeY);

    // snap the axis that is closer to the grid
    if (snappedDistanceX < snappedDistanceY) {
      model.setWidth(newSnappedSizeX);
    } else {
      model.setHeight(newSnappedSizeY);
    }
  }

  // aParam is the slope of the line passing through the center of the object and the dragged vertex:
  // 1 for the bottom right and top left vertices, -1 for the top right and bottom left vertices
  private Double getBParam(Point vertexToCursor, int aParam) {
    int negatedAParam = -aParam; // aParam of the perpendicular line which will be used to map cursor position
    return Double.valueOf(vertexToCursor.y - (negatedAParam * vertexToCursor.x));
  }

  private Point getCursorRelativeToVertex(Point cursor, Point vertex) {
    return new Point(vertex.x - cursor.x, vertex.y - cursor.y);
  }

  private boolean handleRight(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    int newSize = currentBounds.width + (point.x - lastPoint.x);
    int newSnappedSize = doNotSnap ? newSize : (hSnapper.snap(currentBounds.x + newSize, Snapper.SourceEdge.MAX) - currentBounds.x);
    if (newSnappedSize < MIN_SIZE) {
      return false;
    }
    if (preserveSize) {
      // order of operations is important!
      model.setX((currentBounds.x + newSnappedSize) - currentBounds.width);
      currentBounds.x += (newSize - currentBounds.width);
    } else {
      currentBounds.width = newSize;
      model.setWidth(newSnappedSize);
    }
    return true;
  }

  private boolean handleTop(Point point, WidgetModel model, boolean preserveSize, boolean doNotSnap) {
    int newPos = currentBounds.y + (point.y - lastPoint.y);
    int newSnappedPos = doNotSnap ? newPos : vSnapper.snap(newPos, Snapper.SourceEdge.MIN);
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
