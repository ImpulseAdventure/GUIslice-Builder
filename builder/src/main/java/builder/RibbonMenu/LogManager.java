/**
 *
 * The MIT License
 *
 * Copyright 2022 Paul Conti
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

package builder.RibbonMenu;

import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * LogManager This class is intended to be use with the default logging class of
 * java.
 *
 * @author Paul Conti
 */
public class LogManager {

  /** The fh. */
  private FileHandler fh = null;
  
  /** The logger. */
  private Logger logger = null;
  
  /** The instance. */
  private static LogManager instance = null;

  /**
   * getInstance() - get our Singleton Object.
   *
   * @return instance
   */
  public static synchronized LogManager getLogger() {
    if (instance == null) {
      instance = new LogManager();
    }
    return instance;
  }

  /**
   * Instantiates a new log manager.
   */
  public LogManager() {
    
  }
  
  /**
   * Open logger.
   *
   * @param fileName
   *          the file name
   */
  public void openLogger(String fileName) {
    logger = Logger.getLogger(LogManager.class.getName());
    try {
      fh = new FileHandler(fileName);
    } catch (Exception e) {
        e.printStackTrace();
    }
    Properties props = System.getProperties();
    props.setProperty("java.util.logging.SimpleFormatter.format", 
        "[%1$tc] %4$s: %5$s %n");
    
    fh.setFormatter(new SimpleFormatter());
    logger.addHandler(fh);
    logger.setUseParentHandlers(false);
  }
  
  /**
   * Debug.
   *
   * @param msg
   *          the msg
   */
  public void debug(String msg) {
    if (logger != null) {
      logger.info(msg);
    }
  }
  
  /**
   * Error.
   *
   * @param msg
   *          the msg
   */
  public void error(String msg) {
    if (logger != null) {
      logger.severe(msg);
    }
  }
  
  /**
   * Warning.
   *
   * @param msg
   *          the msg
   */
  public void warning(String msg) {
    if (logger != null) {
      logger.warning(msg);
    }
  }
  
}
