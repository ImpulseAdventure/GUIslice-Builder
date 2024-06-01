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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import builder.common.Snapper;
import builder.mementos.PositionMemento;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class DragWidgetCommand will
 * allow users to drag widgets around the design canvas.
 * 
 * @author Paul Conti
 * 
 */
public class DragWidgetCommand extends Command {
  
  /** The page that holds the selected widgets. */
  private PagePane page;
  
  /** The p is our adjusted drag point to fit to our design canvas 
   * (TFT screen simulation). Each widget gets one Point (p) object.
   */
  private Point[] pt; // current position
  private Point[] offsetPt; // offset from original position
  
  /** The targets list contains the set of widgets to drag. */
  private List<Widget> targets = new ArrayList<Widget>();

  /**
   * Services used to find the best matching snapping to snap to.
   */
  private Snapper hSnapper;
  private Snapper vSnapper;
  
  /**
   * Instantiates a new drag widget command.
   *
   * @param page
   *          the <code>page</code> object that contains the widgets to drag.
   */
  public DragWidgetCommand(PagePane page, Snapper hSnapper, Snapper vSnapper) {
    this.page = page;
    this.hSnapper = hSnapper;
    this.vSnapper = vSnapper;
  }

  public Snapper getHorizontalSnapper() {
    return hSnapper;
  }

  public Snapper getVerticalSnapper() {
    return vSnapper;
  }
  
  /**
   * Start will setup the drag widgets command for later execution.
   */
  public boolean start(Point mousePt) {
    targets = page.getSelectedList();
    if (targets.size() == 0) {
      JOptionPane.showMessageDialog(null,  
          "You must select something to drag.", 
          "Warning", JOptionPane.WARNING_MESSAGE);
      page.selectNone(); // turn off all selections
      return false;
    }
    // setup for undo
    memento = new PositionMemento(page, targets);

    // house keeping
    pt = new Point[targets.size()];
    offsetPt = new Point[targets.size()];
    /* calculate our point of contact vs the upper edge 
     * of our widgets so we can keep our cursor on the 
     * object dragged
     */
    for (int i=0; i<targets.size(); i++) {
      Point locPt = targets.get(i).getLocation();
      offsetPt[i] = new Point(mousePt.x - locPt.x, mousePt.y - locPt.y);
    }
    return true;
  }

  /**
   * Move will perform dragging the widgets.
   *
   * @param mousePt
   *          the <code>m</code> is the new relative position of our dragged widgets. 
   */
  public void move(Point mousePt, boolean doNotSnap) {
    Widget w;

    for (int i=0; i<targets.size(); i++) {
      /* 
       *  pt[i] will be our new dragged position.
       */
      w = targets.get(i);
      pt[i] = new Point(
        doNotSnap ? mousePt.x - offsetPt[i].x : hSnapper.snap(mousePt.x - offsetPt[i].x, Snapper.SourceEdge.MIN),
        doNotSnap ? mousePt.y - offsetPt[i].y : vSnapper.snap(mousePt.y - offsetPt[i].y, Snapper.SourceEdge.MIN)
      );
      w.updateLocation(
        pt[i].x,
        pt[i].y
      );
    }
  }

  /**
   * Stop ends the drag 
   */
  public void stop(boolean doNotSnap) {
  }

  /**
   * execute - will freeze the final drag point for each widget.
   *
   * @see builder.commands.Command#execute()
   */
  @Override
  public void execute() {
    try {
      for (int i=0; i<targets.size(); i++) {
        targets.get(i).moveBy(pt[i]);
      }
    } catch (NullPointerException e) {
      return;
    }
  }

  /**
   * toString - converts drag command to a string for debugging.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String myEnums = "";
  
    try {
      for (int i=0; i<targets.size(); i++) {
        if (i>0) myEnums = myEnums + ",";  
        myEnums = myEnums + targets.get(i).getEnum();
      }
    } catch (NullPointerException e) {
      return String.format("Drag page:%s widget:Null pointer",page.getEnum());
    }
    return String.format("Drag page:%s widget:%s",page.getEnum(),myEnums);
  }
}
