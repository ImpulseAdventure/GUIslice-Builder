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

import builder.controller.Controller;
import builder.mementos.PositionMemento;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class DragByArrowCommand will
 * allow users to drag widgets around the design canvas
 * using control-<UP,DOWN,LEFT,RIGHT>
 * 
 * @author Paul Conti
 * 
 */
public class DragByArrowCommand extends Command {
  
  /** The page that holds the selected widgets. */
  private PagePane page;
  
  /** The p is our adjusted drag point to fit to our design canvas 
   * (TFT screen simulation). Each widget gets one Point (p) object.
   */
  private Point offsetPt = new Point(); // offset from original position
  
  /** The targets list contains the set of widgets to drag. */
  private List<Widget> targets = new ArrayList<Widget>();
  private int tft_width = 0;
  private int tft_height = 0;
  private boolean bSuccess;
  
  /**
   * Instantiates a new drag widget command.
   *
   * @param page
   *          the <code>page</code> object that contains the widgets to drag.
   */
  public DragByArrowCommand(PagePane page) {
    this.page = page;
    tft_width = Controller.getProjectModel().getWidth();
    tft_height = Controller.getProjectModel().getHeight();
    bSuccess = false;
  }
  
  /**
   * Start will setup the drag widgets command for later execution.
   */
  public boolean start() {
    targets = page.getSelectedList();
    if (targets.size() == 0) {
      JOptionPane.showMessageDialog(null, 
          "You must first select something to move.", 
          "Warning", JOptionPane.WARNING_MESSAGE);
//      page.selectNone(); // turn off all selections
      return false;
    }
    // setup for undo
    memento = new PositionMemento(page, targets);
        
    return true;
  }

  public void moveUP() {
    offsetPt.x = 0;
    offsetPt.y = -1;
    move();
  }

  public void moveDOWN() {
    offsetPt.x = 0;
    offsetPt.y = +1;
    move();
  }

  public void moveLEFT() {
    offsetPt.x = -1;
    offsetPt.y = 0;
    move();
  }

  public void moveRIGHT() {
    offsetPt.x = +1;
    offsetPt.y = 0;
    move();
  }

  
  /**
   * Move will perform dragging the widgets.
   *
   * @param m
   *          the <code>m</code> is the new relative position of our dragged widgets. 
   */
  public void move() {
    /* 
     * Test our drag position to be sure it will fit to our TFT screen.
     * Simply return if it doesn't.
     */
    Widget w;
    Point testPt;
    int x, y;
    for (int i=0; i<targets.size(); i++) {
      w = targets.get(i);
      x = w.getX();
      y = w.getY();
      testPt = new Point(x+offsetPt.x, y+offsetPt.y);
      if(!w.testLocation(testPt.x, testPt.y, tft_width, tft_height)) return;
    }

    for (int i=0; i<targets.size(); i++) {
      /* 
       *  pt[i] will be our new dragged position.
       */
      w = targets.get(i);
      x = w.getX();
      y = w.getY();
      w.updateLocation(x+offsetPt.x, y+offsetPt.y);
    }
    bSuccess = true;
  }

  /**
   * execute - will freeze the final drag point for each widget.
   *
   * @see builder.commands.Command#execute()
   */
  @Override
  public void execute() {
  }

  /**
   * toString - converts drag command to a string for debugging.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String myEnums = "";
  
    if (bSuccess) {
      try {
        for (int i=0; i<targets.size(); i++) {
          if (i>0) myEnums = myEnums + ",";  
          myEnums = myEnums + targets.get(i).getEnum();
        }
      } catch (NullPointerException e) {
        return String.format("Drag page:%s widget:Null pointer",page.getEnum());
      }
      return String.format("Drag page:%s widget:%s",page.getEnum(),myEnums);
    } else {
      return String.format("Drag Failed");
    }
  }

}
