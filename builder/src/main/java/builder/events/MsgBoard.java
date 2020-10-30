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
package builder.events;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MsgBoard implements the Observer Pattern.
 * <p>
 * The Observer Pattern specifies communication between objects: 
 * observable and observers. An observable is an object which notifies 
 * observers about the changes in its state.
 * </p>
 * <p>
 * MsgBoard is an example of an observable object and state changes are 
 * recorded inside an MsgEvent object.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class MsgBoard {
  
  /** The subscribers. */
//  private static List<Pair> subscribers = new CopyOnWriteArrayList <Pair>();
  static private List<Pair> subscribers = new ArrayList<Pair>();

  /**
   * Publish.
   *
   * @param event
   *          the event
   */
  static public void publish(MsgEvent event, String name) {
    notifySubscribers(event, name);
  }
  
  /**
   * subscribe
   *
   * @see builder.events.iSubject#subscribe(builder.events.iSubscriber)
   */
  static public void subscribe(iSubscriber subscriber, String name) {
//    System.out.println("Register Observer: " + name);
    subscribers.add(new Pair(name, subscriber));
  }

  /**
   * remove
   *
   * @see builder.events.iSubject#remove(builder.events.iSubscriber)
   */
  static public void remove(String name) {
    Pair c = new Pair(name, null);
    subscribers.removeIf(p -> p.equals(c));
//    System.out.println("Removed Observer: " + c.getName());
  }

  /**
   * notifySubscribers
   *
   */
  static public void notifySubscribers(MsgEvent e, String name) {
//    System.out.println("Notifying Observers on event: " + e.toString());
    for (int i=0; i<subscribers.size(); i++) {
      Pair p = subscribers.get(i);
      // avoid loops by not sending the message to the originator
      if (!p.getName().equals(name)) {
        p.getSubscriber().updateEvent(e);
//      System.out.println(p.getName());
      }
    }
  }

  /**
   * sendActionCommand.
   *  Used by our Ribbon's JCommandButtons to send action events
   *  to the Controller.
   *
   * @param command
   *          the action to take
   */
  static public void sendActionCommand(String name, String command) {
    sendEvent(name, MsgEvent.ACTION_COMMAND, command);
  }

  /**
   * sendRepaint.
   *  Routine to send repaint events.
   *
   * @param code
   *          the MsgEvent code
   * @param widgetKey
   *          the targets widget key 
   */
  static public void sendRepaint(String name, String widgetKey) {
//  System.out.println("Notifying Observers on Repaint: " + widgetKey);
    sendEvent(name, MsgEvent.WIDGET_REPAINT, widgetKey);
  }

  /**
   * sendEnumChange.
   *  Routine to send enum change events.
   *
   * @param code
   *          the MsgEvent code
   * @param widgetKey
   *          the targets widget key 
   * @param widgetEnum
   *          the widget's new ENUM 
   */
  static public void sendEnumChange(String name, String widgetKey, String widgetEnum) {
    sendEvent(name, MsgEvent.WIDGET_ENUM_CHANGE, widgetKey, widgetEnum);
  }

  /**
   * sendEvent.
   *  Generic routine to send event codes.
   *
   * @param code
   *          the MsgEvent code
   */
  static public void sendEvent(String name, int code) {
    MsgEvent ev = new MsgEvent();
    ev.code = code;
    ev.message = "";
    ev.xdata = "";
    ev.fromIdx = 0;
    ev.toIdx = 0;
    ev.x = 0;
    ev.y = 0;
    publish(ev, name);
  }

  /**
   * sendEvent.
   *  Generic routine to send events that only require one parameter.
   *
   * @param code
   *          the MsgEvent code
   * @param param1
   *          the message (often a widget key)
   */
  static public void sendEvent(String name, int code, String param1) {
    MsgEvent ev = new MsgEvent();
    ev.code = code;
    ev.message = param1;
    ev.xdata = "";
    ev.fromIdx = 0;
    ev.toIdx = 0;
    ev.x = 0;
    ev.y = 0;
    publish(ev, name);
  }

  /**
   * sendEvent.
   *  Generic routine to send events that require two parameters
   *
   * @param code
   *          the MsgEvent code
   * @param param1
   *          the message (often a widget key)
   * @param param2
   *          the extra data (often a widget's parent page key)
   */
  static public void sendEvent(String name, int code, String param1, String param2) {
    MsgEvent ev = new MsgEvent();
    ev.code = code;
    ev.message = param1;
    ev.xdata = param2;
    ev.fromIdx = 0;
    ev.toIdx = 0;
    ev.x = 0;
    ev.y = 0;
    publish(ev, name);
  }

}
