/**
 *
 * The MIT License
 *
 * Copyright (c) 2018-2024 Paul Conti
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import builder.common.Utils;
import builder.common.ThemeInfo;
import builder.controller.Controller;
import builder.controller.LogManager;
import builder.controller.PropManager;
import builder.controller.UserPrefsManager;
import builder.fonts.FontFactory;
import builder.models.AdvancedSnappingModel;
import builder.prefs.GeneralEditor;
import builder.prefs.ModelEditor;
import builder.views.MenuBar;
import builder.views.Ribbon;
import builder.views.RibbonListener;
//import builder.views.ToolBar;
import builder.views.TreeView;

import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;

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
  public static final String VERSION = "0.17.b27";
  
  /** The Constant VERSION_NO is for save and restore of user preferences. */
  public static final String VERSION_NO = "-16";
  
  /** The Constant FILE_VERSION_NO is for save and restore of project file. */
  public static final String FILE_VERSION_NO = "17";
  
  /** The Constant PROGRAM_TITLE. */
  public static final String PROGRAM_TITLE = "GUIslice Builder";
  
  /** The Constant NEW_PROJECT. */
  public static final String NEW_PROJECT = " - unnamed project";
  
  public static FontFactory ff;
  
  /** The canvas width */
  public static int CANVAS_WIDTH;
  
  /** The canvas height */
  public static int CANVAS_HEIGHT;
  
  /** The frame. */
  public static JFrame frame;
  
  /** The themes. */
  public static List<ThemeInfo> themes;
  
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
  
  public static String NO_OPTIONAL_LAFS = "NO_OPTIONAL_LAFS";
  
  public static JSplitPane splitPane;
  
  public static double version;
  
  public static Ribbon ribbon;
  
  public static boolean isMAC = false;
  private static boolean bInsideIDE = false;
  
  /** our logger */
  public static LogManager logger = null;
  
  public static Builder builder;
  
  /**
   * The main method.
   *
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    // Check how many arguments were passed in
    if(args.length > 0)
    {
       /* only one supported -> insideIDE so we can adjust our 
        * working directory path when testing inside an IDE like Eclipse.
        */
       if (args[0].equals("insideIDE")) {
         bInsideIDE = true;
       }
    }    
    
    version = Double.parseDouble(System.getProperty("java.specification.version"));
    // macOS  (see https://www.formdev.com/flatlaf/macos/)
    if( SystemInfo.isMacOS ) {
//      isMAC = true;
      // enable screen menu bar
      // (moves menu bar from JFrame window to top of screen)
      System.setProperty( "apple.laf.useScreenMenuBar", "true" );

      // application name used in screen menu bar
      // (in first menu after the "apple" menu)
      System.setProperty( "apple.awt.application.name", "GUIsliceBuilder" );

      // appearance of window title bars
      // possible values:
      //   - "system": use current macOS appearance (light or dark)
      //   - "NSAppearanceNameAqua": use light appearance
      //   - "NSAppearanceNameDarkAqua": use dark appearance
      // (needs to be set on main thread; setting it on AWT thread does not work)
      System.setProperty( "apple.awt.application.appearance", "system" );
    } else {
    // Linux or Windows
      // enable custom window decorations
      JFrame.setDefaultLookAndFeelDecorated( true );
      JDialog.setDefaultLookAndFeelDecorated( true );
    }

    // start our logger
    logger = LogManager.getLogger();
    // do not use Utils.getWorkingDir() or you will write to package/logs and screw up releases
    String logFile = "./logs/builder.log";
    logger.openLogger(logFile);
    logger.debug("Builder ver: " + VERSION + 
        " started java ver: " + version + 
        " osname: " + System.getProperty( "os.name" ) +
        " osarc: "  + System.getProperty( "os.arch" ) +
        " osver: " + SystemInfo.osVersion);
    loadThemes();
    startUp();
  }

  /**
   * Instantiates a new GUIslice builder.
   */
  public Builder() {
  }
  
  /**
   * starts the builder program.
   */
  public static void startUp() {

    EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            setLookAndFeel(builderThemeClassName());
          } catch( Exception ex ) {
            logger.debug("Failed to initialize LaF Theme EX: "+ex.toString());
          }
          Builder builder = new Builder();
          builder.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
          builder.initUI();
       }
    });
    

    // NOTE: if running a debugger you might want to comment this thread out
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
          Calendar cal = Calendar.getInstance();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
          // always create crash file outside package folder
          bInsideIDE = false;
          String workingDir = Utils.getWorkingDir();
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
              String msg = String.format("A fatal error has occurred. A crash log created as:\n%s", fileName);
              JOptionPane.showMessageDialog(null, 
                  msg, "Failure", JOptionPane.ERROR_MESSAGE);

          } catch (IOException e1) {
              e1.printStackTrace();
          }
          Builder.logger.debug("Builder crash: " + fileName);
          System.exit(0);
/*
          frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
              String title = "Confirm Dialog";
              String message = "You're about to quit the application -- are you sure?";
              int answer = JOptionPane.showConfirmDialog(null,message,title, JOptionPane.YES_NO_OPTION); 
              if(answer == JOptionPane.YES_OPTION) {
                Builder.logger.debug("Builder exit");
                System.exit(0);
              } else
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            }
          });
          frame.setVisible(true);

        }
*/        
     }
    });

  }
  
  /**
   * Gets the theme class name.
   *
   * @return the theme class name
   */
  public static String builderThemeClassName() {
  /** The Constant MY_NODE. */
    String MY_NODE = "com/impulseadventure/builder/general";
        // get rid of the bugged Preferences warning - not needed in Java 9 and above
    System.setErr(new PrintStream(new OutputStream() {
        public void write(int b) throws IOException {}
    }));
    String prefNode = MY_NODE + VERSION_NO;
    Preferences fPrefs = Preferences.userRoot().node(prefNode);
    String defThemeName;
    if( SystemInfo.isMacOS ) {
      defThemeName = "Flat Mac Dark";
    } else {
      defThemeName = "Arc Dark (Material)";
    }

    String currentTheme = fPrefs.get("Java Themes", defThemeName);
    Builder.logger.debug("User Preference Java Theme: " + currentTheme);
    return currentTheme;
  }
  
  /**
   * Creates the UI pieces.  
   */
  private void initUI() {
    // setup our fonts from builder_fonts.json file
    ff = FontFactory.getInstance();
    ff.init();

    // access our controllers
    PropManager propManager = PropManager.getInstance();
    controller = Controller.getInstance();
    
    // setup our User Preference UI
    List<ModelEditor> prefEditors = controller.initUserPrefs();
    userPreferences = new UserPrefsManager(frame, prefEditors);
    controller.setUserPrefs(userPreferences);
    
    // create the tree view
    TreeView treeView = TreeView.getInstance();

    // create our main frame and add our panels
    String frameTitle = PROGRAM_TITLE + NEW_PROJECT;
    frame = new JFrame();

    // The listener for message events */
    RibbonListener ribbonListener = new RibbonListener();
    
    // create our menu bar
    MenuBar menuBar = new MenuBar();
    menuBar.addListeners(ribbonListener);
    menuBar.setAdvancedSnappingModel(AdvancedSnappingModel.getInstance());

    // set a listener to capture resize window events
    frame.addComponentListener(new FrameListen());
    
    frame.setTitle(frameTitle);
    frame.setJMenuBar(menuBar);

//    ToolBar toolbar = ToolBar.getInstance();

//    toolbar.addListeners(ribbonListener);
//    frame.getContentPane().add(toolbar.get(),BorderLayout.EAST);

    // create our ribbon
    ribbon = Ribbon.getInstance();
    ribbon.setRibbonColors();
    ribbon.addListeners(ribbonListener);
    ribbon.setAdvancedSnappingModel(AdvancedSnappingModel.getInstance());
    
    frame.getContentPane().add(ribbon,BorderLayout.NORTH);

    frame.setIconImage(new ImageIcon(Builder.class.getResource("/resources/icons/guislicebuilder.png")).getImage());

    // pass on top level frame to controller so it can change project names
    controller.setFrame(frame); 
    controller.initUI();  // now we can start the controller

    // trap the red X on our main frame
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    // setup our canvas offsets
    // NOTE: we can't use controller.getPanel().getSize()
    // since it's scrollpane is much larger than actual screen size
    CANVAS_WIDTH = GeneralEditor.getInstance().getWidth();
    CANVAS_HEIGHT = GeneralEditor.getInstance().getHeight();

    int width = 1040;
    if (GeneralEditor.getInstance().getAppWinWidth() > 0) 
      width = GeneralEditor.getInstance().getAppWinWidth();
    int height = 675;
    if (GeneralEditor.getInstance().getAppWinHeight() > 0) 
      height = GeneralEditor.getInstance().getAppWinHeight();
    frame.setPreferredSize(new Dimension(width, height));

    // trap frame resizing
    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        GeneralEditor.getInstance().setAppWinWidth(frame.getWidth());
        GeneralEditor.getInstance().setAppWinHeight(frame.getHeight());
      }
    });

//    JRibbonFrame defaults to BorderLayout so no need to set it.
//    frame.setLayout(new BorderLayout());
    // add our views to the frame
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
        controller,
        propManager);
    width = CANVAS_WIDTH + 40;
    if (GeneralEditor.getInstance().getTFTWinWidth() > 0) 
        width = GeneralEditor.getInstance().getTFTWinWidth();
    splitPane.setDividerLocation(width);
    frame.getContentPane().add(splitPane,BorderLayout.CENTER);

//    frame.getContentPane().add(propManager, BorderLayout.EAST);
//    frame.getContentPane().add(controller,BorderLayout.CENTER);
    frame.getContentPane().add(treeView, BorderLayout.WEST);
    
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
    timee = new Timer(5000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            java.util.Date now = new java.util.Date();
            String ss = DateFormat.getDateTimeInstance().format(now);
            welcomeDate.setText(ss);
            welcomeDate.setToolTipText("Welcome, Today is " + ss);

        }
    });
    timee.start();
    
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        String title = "Confirm Dialog";
        String message = "You're about to quit the application -- are you sure?";
        int answer = JOptionPane.showConfirmDialog(null,message,title, JOptionPane.YES_NO_OPTION); 
        if(answer == JOptionPane.YES_OPTION) {
          Builder.logger.debug("Builder exit");
          System.exit(0);
        } else
          frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      }
    });
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
  
//  @SuppressWarnings("resource")
  public static void setLookAndFeel(String selectedLaf) {
    try {
      if (selectedLaf.equals(UIManager.getSystemLookAndFeelClassName())) {
        Builder.logger.debug("LookAndFeel: " + UIManager.getSystemLookAndFeelClassName());
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        return;
      }
        
      // scan themes for a match
      ThemeInfo themeInfo = null;
      for (ThemeInfo ti : themes) {
         if (ti.name.equals(selectedLaf)) {
           themeInfo = ti;
         }
      }
      if (themeInfo != null) {
        if( themeInfo.lafClassName != null ) {
          Builder.logger.debug("LookAndFeel: " + themeInfo.lafClassName);
          UIManager.setLookAndFeel( themeInfo.lafClassName );
          return;
        }
      }
      Builder.logger.debug("LookAndFeel: " + UIManager.getSystemLookAndFeelClassName());
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      try {
        Builder.logger.debug("Exception! setLookAndFeel: " + UIManager.getSystemLookAndFeelClassName()+" EX: "+ex.toString());
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  } // end setLookAndFeel
/*  
  public static void dumpUI(String name) {
    System.out.println("Theme: " + name);
    UIDefaults defaults = UIManager.getDefaults();
    System.out.println(defaults.size()+ " properties defined !");
    String[ ] colName = {"Key", "Value"};
    String[ ][ ] rowData = new String[ defaults.size() ][ 2 ];
    int i = 0;
    for(Enumeration e = defaults.keys(); e.hasMoreElements(); i++){
        Object key = e.nextElement();
        rowData[ i ] [ 0 ] = key.toString();
        rowData[ i ] [ 1 ] = ""+defaults.get(key);
        System.out.println(rowData[i][0]+" ,, "+rowData[i][1]);
    }
  }
*/
  public static void loadThemes() {
    themes = new ArrayList<ThemeInfo>();
    // add system look an feels
    for (LookAndFeelInfo look_and_feel : UIManager.getInstalledLookAndFeels()) {
      JFrame.setDefaultLookAndFeelDecorated( false );
      JDialog.setDefaultLookAndFeelDecorated( false );
      themes.add(new ThemeInfo(look_and_feel.getName(),
        null, look_and_feel.getClassName()));
    }

    // add core themes next
    themes.add( new ThemeInfo( "Flat Light"   , null, FlatLightLaf.class.getName() ) );
    themes.add( new ThemeInfo( "Flat Dark"    , null, FlatDarkLaf.class.getName() ) );
    themes.add( new ThemeInfo( "Flat Mac Light"   , null, FlatMacLightLaf.class.getName() ) );
    themes.add( new ThemeInfo( "Flat Mac Dark"    , null, FlatMacDarkLaf.class.getName() ) );
    themes.add( new ThemeInfo( "Flat IntelliJ", null, FlatIntelliJLaf.class.getName() ) );
    themes.add( new ThemeInfo( "Flat Darcula" , null, FlatDarculaLaf.class.getName() ) );

    // add intellij themes next
    for (FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
      themes.add( new ThemeInfo( info.getName() , null, info.getClassName()) );
    }
  }
  
  /**
   * isInsideIDE allows us to adjust our working directory to find
   * folders like templates, fonts, etc... when using something like Eclipse IDE
   * 
   * @return true if using IDE
   */
  public static boolean isInsideIDE() {
    return bInsideIDE;
  }
  
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
//      logger.debug("*** componentResized ***");
      Controller.sendRepaint();
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
