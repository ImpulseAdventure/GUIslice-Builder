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
package builder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import builder.common.CommonUtils;
import builder.controller.Controller;
import builder.controller.PropManager;
import builder.controller.UserPrefsManager;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.models.GeneralModel;
import builder.prefs.GeneralEditor;
import builder.prefs.ModelEditor;
import builder.views.MenuBar;
import builder.views.Ribbon;
import builder.views.TreeView;

import org.pushingpixels.substance.api.skin.AutumnSkin;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin;
import org.pushingpixels.substance.api.skin.ChallengerDeepSkin;
import org.pushingpixels.substance.api.skin.CremeCoffeeSkin;
import org.pushingpixels.substance.api.skin.DustSkin;
import org.pushingpixels.substance.api.skin.GraphiteAquaSkin;
import org.pushingpixels.substance.api.skin.MarinerSkin;
import org.pushingpixels.substance.api.skin.MistAquaSkin;
import org.pushingpixels.substance.api.skin.OfficeBlack2007Skin;
import org.pushingpixels.substance.api.skin.OfficeBlue2007Skin;
import org.pushingpixels.substance.api.skin.OfficeSilver2007Skin;
import org.pushingpixels.substance.api.skin.SaharaSkin;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;

/**
 * GUIsliceBuilder is the main class of the application.
 * <p>
 * GUIslice C library is a lightweight platform-independent GUI designed 
 * for embedded system TFT displays.
 * </p>
 * <p>
 * The purpose of GUIsliceBuilder application is to take away some of
 * the drudgery required by the GUIslice C library.   It allows users to
 * layout their UI visually and generate a skeleton C file for the target
 * platform, either Arduino, Arduino min (using flash storage), or Linux.
 * </p>
 *<p>
 * An important design goal was to allow users to incrementally build 
 * their UI, generate a .ino or .c file, add code for handling some of
 * the UI pieces (say for some sensors), and then go back to the builder
 * and add more UI bits without losing their code additions.
 * </p>
 * <p>
 * This application follows the Model View Controller Pattern.
 * </p>
 * 
 * @author Paul Conti
 * 
 */
public class Builder  extends JDesktopPane {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant VERSION. */
  public static final String VERSION = "0.13.0.5";
  
  /** The Constant VERSION_NO is for save and restore of user preferences. */
  public static final String VERSION_NO = "-13";
  
  /** The Constant FILE_VERSION_NO is for save and restore of project file. */
  public static final String FILE_VERSION_NO = "13.02";
  
  /** The Constant PROGRAM_TITLE. */
  public static final String PROGRAM_TITLE = "GUIslice Builder";
  
  /** The Constant NEW_PROJECT. */
  public static final String NEW_PROJECT = " - unnamed project";
  
  /** The canvas width */
  public static int CANVAS_WIDTH;
  
  /** The canvas height */
  public static int CANVAS_HEIGHT;
  
  /** The frame. */
  private Ribbon frame;
  
  /** The status bar */
  public JPanel statusBar;
  public JLabel welcomeDate;
  public static JLabel statusMessage;
  
  /** The status bar timer */
  Timer timee;
  
  /** The user preferences. */
  private UserPrefsManager userPreferences;
  
  /** The controller. */
  protected Controller controller;
  
  /* Look and Feel */
  public static final String LAF_ACRYL     = "com.jtattoo.plaf.acryl.AcrylLookAndFeel";
  public static final String LAF_AERO      = "com.jtattoo.plaf.aero.AeroLookAndFeel";
  public static final String LAF_ALUMINIUM = "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel";
  public static final String LAF_BERNSTEIN = "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel";
  public static final String LAF_FAST      = "com.jtattoo.plaf.fast.FastLookAndFeel";
  public static final String LAF_GRAPHITE  = "com.jtattoo.plaf.graphite.GraphiteLookAndFeel";
  public static final String LAF_HIFI      = "com.jtattoo.plaf.hifi.HiFiLookAndFeel";
  public static final String LAF_LUNA      = "com.jtattoo.plaf.luna.LunaLookAndFeel";
  public static final String LAF_MCWIN     = "com.jtattoo.plaf.mcwin.McWinLookAndFeel";
  public static final String LAF_MINT      = "com.jtattoo.plaf.mint.MintLookAndFeel";
  public static final String LAF_SMART     = "com.jtattoo.plaf.smart.SmartLookAndFeel";
  public static final String LAF_TEXTURE   = "com.jtattoo.plaf.texture.TextureLookAndFeel";
 
  /** The boolean indicating running on a MacOS system */
  public static boolean isMAC = false;
  
  /**
   * The main method.
   *
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    Builder builder = new Builder();
    double version = Double.parseDouble(System.getProperty("java.specification.version"));
    if (version < 1.8) {
      String msg = String.format("Java 8 is needed to run. Yours is %.2f", version);
      JOptionPane.showMessageDialog(null, 
        msg, "Failure", JOptionPane.ERROR_MESSAGE);
    }
/*  Use this code for Java 9 and above
    if (version < 9) {
      String msg = String.format("Java 9 or higher is needed to run. Yours is %.2f", version);
      JOptionPane.showMessageDialog(null, 
        msg, "Failure", JOptionPane.ERROR_MESSAGE);
    }
*/
    builder.startUp();
  }

  /**
   * Instantiates a new GUIslice builder.
   */
  public Builder() {
  }
  
  /**
   * starts the builder program.
   */
  public void startUp() {

    EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
          initUI();
          try {
            String osName = System.getProperty("os.name").toLowerCase();
            isMAC = osName.startsWith("mac os x");
            if (isMAC) 
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else{
                setLookAndFeel(GeneralEditor.getInstance().getThemeClassName());
            }            
            SwingUtilities.updateComponentTreeUI(frame);
          } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
          }
          frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
              String title = "Confirm Dialog";
              String message = "You're about to quit the application -- are you sure?";
              int answer = JOptionPane.showConfirmDialog(null,message,title, JOptionPane.YES_NO_OPTION); 
              if(answer == JOptionPane.YES_OPTION) {
                System.exit(0);
              } else
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            }
          });
          frame.setVisible(true);
        }
    });
    // NOTE: if running a debugger you might want to comment this thread out
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
          Calendar cal = Calendar.getInstance();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
          String workingDir = CommonUtils.getInstance().getWorkingDir();
          File directory = new File(workingDir + "logs");
          if(!directory.exists()){
            directory.mkdir();
          }
          String fileName = workingDir + "logs" 
            + System.getProperty("file.separator")
            + sdf.format(cal.getTime())+".txt";
          PrintStream writer;
          try {
              writer = new PrintStream(fileName, "UTF-8");
              writer.println(e.getClass() + ": " + e.getMessage());
              for (int i = 0; i < e.getStackTrace().length; i++) {
                  writer.println(e.getStackTrace()[i].toString());
              }
              String msg = String.format("A fatal error has occurred. A crash log created as %s", fileName);
              JOptionPane.showMessageDialog(null, 
                  msg, "Failure", JOptionPane.ERROR_MESSAGE);

          } catch (IOException e1) {
              e1.printStackTrace();
          }
          System.exit(0);
     }
    });
  }
  
  /**
   * Creates the UI pieces.  
   */
  private void initUI() {
    // access our controllers
    PropManager propManager = PropManager.getInstance();
    controller = Controller.getInstance();
    
    // create our menu bar
    MenuBar mb = new MenuBar();

    // create the tree view
    TreeView treeView = TreeView.getInstance();

    // create our main frame and add our panels
    String frameTitle = PROGRAM_TITLE + NEW_PROJECT;
    // create out ribbon
    frame = Ribbon.getInstance();
    
    // setup our listeners
    mb.addListeners(frame.getRibbonListener());

    // set a listener to capture resize window events
    frame.addComponentListener(new FrameListen());;
    
    frame.setTitle(frameTitle);
    frame.setJMenuBar(mb);

    frame.setUndecorated( true );
    int x=1;// use x value from 1 to 8
    frame.getRootPane().setWindowDecorationStyle( x);
  
    frame.setIconImage(new ImageIcon(Builder.class.getResource("/resources/icons/guislicebuilder.png")).getImage());

    // pass on top level frame to controller so it can change project names
    controller.setFrame(frame); 

    // trap the red X on our main frame
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    // setup our User Preference UI
    List<ModelEditor> prefEditors = controller.initUserPrefs();
    userPreferences = new UserPrefsManager(frame, prefEditors);
    controller.setUserPrefs(userPreferences);
    controller.initUI();  // now we can start the controller
    
//    JRibbonFrame defaults to BorderLayout so no need to set it.
//    frame.setLayout(new BorderLayout());
    // add our views to the frame
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
        controller,
        propManager); 
    frame.add(splitPane,BorderLayout.CENTER);

//    frame.add(propManager, BorderLayout.EAST);
//    frame.add(controller,BorderLayout.CENTER);
    frame.add(treeView, BorderLayout.WEST);
    
    // create our status bar
    statusBar = new JPanel();
    setLayout(new BorderLayout());//frame layout
    statusMessage = new JLabel("GUIsliceBuilder Started!", JLabel.LEFT);
    welcomeDate = new JLabel();
    welcomeDate.setOpaque(true);//to set the color for jlabel
    statusBar.setLayout(new BorderLayout());
    statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    statusBar.add(statusMessage, BorderLayout.WEST);
    statusBar.add(welcomeDate, BorderLayout.EAST);
    frame.add(statusBar, BorderLayout.SOUTH);
    //display date time to status bar
    timee = new Timer(1000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            java.util.Date now = new java.util.Date();
            String ss = DateFormat.getDateTimeInstance().format(now);
            welcomeDate.setText(ss);
            welcomeDate.setToolTipText("Welcome, Today is " + ss);

        }
    });
    timee.start();
    
    
    // setup our canvas offsets
    // NOTE: we can't use controller.getPanel().getSize()
    // since it's scrollpane is much larger than actual screen size
    CANVAS_WIDTH = GeneralEditor.getInstance().getWidth()+160;
    CANVAS_HEIGHT = GeneralEditor.getInstance().getHeight()+100;

    int width = Math.max(GeneralEditor.getInstance().getWidth()+716, 1040);
    int height = Math.max(GeneralEditor.getInstance().getHeight()+360, 675);
    frame.setPreferredSize(new Dimension(width, height));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    postStatusMsg("GUIsliceBuilder Started!");
  }
  
  public static void postStatusMsg(String message) {
    statusMessage.setText(message);
    Timer timer = new Timer(10000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        statusMessage.setText("");
      }
    });
    timer.setRepeats(false); // Only execute once
    timer.start(); // Go go go!
  }
  
  public static void setLookAndFeel(String selectedLaf) {
    try {
      switch(selectedLaf) {
        case GeneralModel.LAF_AUTUMNSKIN:
          SubstanceLookAndFeel.setSkin(new AutumnSkin());
          break;
        case GeneralModel.LAF_BUSINESSBLACKSTEELSKIN:
          SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
          break;
        case GeneralModel.LAF_BUSINESSBLUESTEELSKIN:
          SubstanceLookAndFeel.setSkin(new BusinessBlueSteelSkin());
          break;
        case GeneralModel.LAF_CHALLENGERDEEPSKIN:
          SubstanceLookAndFeel.setSkin(new ChallengerDeepSkin());
          break;
        case GeneralModel.LAF_CREMECOFFEESKIN:
          SubstanceLookAndFeel.setSkin(new CremeCoffeeSkin());
          break;
        case GeneralModel.LAF_DUSTSKIN:
          SubstanceLookAndFeel.setSkin(new DustSkin());
          break;
        case GeneralModel.LAF_GRAPHITEAQUASKIN:
          SubstanceLookAndFeel.setSkin(new GraphiteAquaSkin());
          break;
        case GeneralModel.LAF_MARINERSKIN:
          SubstanceLookAndFeel.setSkin(new MarinerSkin());
          break;
        case GeneralModel.LAF_MISTAQUASKIN:
          SubstanceLookAndFeel.setSkin(new MistAquaSkin());
          break;
        case GeneralModel.LAF_OFFICEBLACK2007SKIN:
          SubstanceLookAndFeel.setSkin(new OfficeBlack2007Skin());
          break;
        case GeneralModel.LAF_OFFICEBLUE2007SKIN:
          SubstanceLookAndFeel.setSkin(new OfficeBlue2007Skin());
          break;
        case GeneralModel.LAF_OFFICESILVER2007SKIN:
          SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());
          break;
        case GeneralModel.LAF_SAHARASKIN:
          SubstanceLookAndFeel.setSkin(new SaharaSkin());
          break;
        default:
          UIManager.setLookAndFeel(selectedLaf);
          break;
      }
    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
  } // end setLookAndFeel
  
  /**
   * The Class FrameListen traps the resize frame event so we can
   * reset our point mapping function to the new settings.
   */
  private class FrameListen implements ComponentListener{
    
    /**
     * componentHidden
     *
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent arg0) {
    }
    
    /**
     * componentMoved
     *
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent arg0) {   
    }
    
    /**
     * componentResized
     *
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent arg0) {
      MsgBoard.getInstance().sendEvent("Builder",MsgEvent.CANVAS_MODEL_CHANGE);
    }
    
    /**
     * componentShown
     *
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent arg0) {

    }
  }
}
