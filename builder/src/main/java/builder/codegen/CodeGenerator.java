/**
 *
 * The MIT License
 *
 * Copyright 2018-2022 Paul Conti
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
package builder.codegen;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import builder.Builder;
import builder.codegen.pipes.AppPipe;
import builder.codegen.pipes.ButtonCbPipe;
import builder.codegen.pipes.CheckboxCbPipe;
import builder.codegen.pipes.DrawCbPipe;
import builder.codegen.pipes.ElementPipe;
import builder.codegen.pipes.EnumPipe;
import builder.codegen.pipes.ExternRefPipe;
import builder.codegen.pipes.ExtraElementPipe;
import builder.codegen.pipes.FilePipe;
import builder.codegen.pipes.FontsPipe;
import builder.codegen.pipes.HeaderPipe;
import builder.codegen.pipes.FontLoadPipe;
import builder.codegen.pipes.IncludesPipe;
import builder.codegen.pipes.InitGuiPipe;
import builder.codegen.pipes.KeypadCbPipe;
import builder.codegen.pipes.ListboxCbPipe;
import builder.codegen.pipes.PathStoragePipe;
import builder.codegen.pipes.Pipe;
import builder.codegen.pipes.Pipeline;
import builder.codegen.pipes.ResourcesPipe;
import builder.codegen.pipes.SaveRefPipe;
import builder.codegen.pipes.SliderCbPipe;
import builder.codegen.pipes.SpinnerCbPipe;
import builder.codegen.pipes.StartupPipe;
import builder.codegen.pipes.TickCbPipe;
import builder.common.CommonUtils;
import builder.controller.Controller;
import builder.models.ProjectModel;
import builder.models.WidgetModel;
import builder.views.PagePane;
import builder.widgets.Widget;

/**
 * The Class CodeGenerator is the Controller for creation of the C skeleton
 * used by GUIslice API mapped to the specific platform, arduino, or linux.
 * <p>
 * The code generation workflow is defined and controlled by stateless pipes.
 * Contained in a Pipeline object.
 * 
 * Each Pipe (or Step) of the workflow is driven by Tags 
 * that are keyed within the C skeletons.
 * </p>
 * NOTE:
 * <p>
 * As of version 0.13.b21 and higher new projects will create our output
 * inside a header file name 'GUIslice_gen.h'. Supporting backward compatibility
 * requires that older projects will still just have the *.ino file. 
 * </p>
 * <ul>
 * <li>hdr_ino.t for new arduino projects which will contain all generated code
 * <li>ino2.t for new arduino projects and is the skeleton for the users application
 * <li>ino.t for older arduino projects
 * <li>c.t for linux C skeleton
 * <li>graphics.t for non-linux code templates
 * <li>linux.t for linux code templates
 * 
 * @author Paul Conti
 * 
 */
public class CodeGenerator {
  
  /** The instance. */
  private static CodeGenerator instance = null;

  public final static String  PREFIX                 = "//<";
  
  public  final static String TEMPLATE_FOLDER        = "templates";
  public  final static String ARDUINO_RES             = "arduino_res";
  public  final static String LINUX_RES               = "linux_res";
  public  final static String HDR_TEMPLATE            = "hdr.t";
  public  final static String ARDUINO_TEMPLATE        = "ino2.t";
  public  final static String ARDUINO_COMPAT_TEMPLATE = "ino.t";
  public  final static String LINUX_TEMPLATE          = "c.t";
  public  final static String ARDUINO_EXT             = ".ino";
  public  final static String HEADER_EXT              = "_GSLC.h";
  public  final static String LINUX_EXT               = ".c";
  public  final static String PIO_APP                 = "main.cpp";
  public  final static String PIO_TEMPLATE            = "platformio.ini";
  public  final static String DEFAULT_ENVS            = "default_envs =";
  
  /** regex pattern */
  private final static Pattern LTRIM = Pattern.compile("^\\s+");
  private final static String EMPTY_STRING = "";

  /** The projects' pages. */
  private List<PagePane> pages;
  
  /** The full list of widget models. */
  List<WidgetModel> models;
  
  /** The current project name. */
  String m_sProjectName = null;
  
  /** The current workflow file name. */
  String m_sTemplateFileName = null;
  
  /** The current workflow output file name. */
  String m_sOutputFileName = null;
  
  /** path to where our C Application will be created */
  String m_sAppPath = null;
  
  /** path to where our Header "*_GSLC.h" for Application will be created */
  String m_sHdrPath = null;
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /** Our project model */
  ProjectModel pm = null;
  
  /** The background color. */
  Color background;
  
  /** finite state machine states */
  protected static final int    ST_UNDEFINED             = 0;
  protected static final int    ST_LINUX                 = 1;
  protected static final int    ST_ARDUINO_COMPAT        = 2;
  protected static final int    ST_ARDUINO_HDR           = 3;
  protected static final int    ST_PIO                   = 10;
  protected static final int    ST_PIO_LINUX             = 11;
  protected static final int    ST_PIO_ARDUINO_COMPAT    = 12;
  protected static final int    ST_PIO_ARDUINO_HDR       = 13;
  
  protected int m_nState = ST_UNDEFINED;
  
  protected static String m_sFileSep = null;
  
  /** The code generation pipe lines. */
  private Pipeline<StringBuilder> workFlow_Linux;
  private Pipeline<StringBuilder> workFlow_Compat;
  private Pipeline<StringBuilder> workFlow_ArduinoHdr;
  private Pipeline<StringBuilder> workFlow_ArduinoIno;
  
  /** The workflow pipes. */
  private Pipe<StringBuilder> appPipe;
  private Pipe<StringBuilder> buttonCbPipe;
  private Pipe<StringBuilder> checkboxCbPipe;
  private Pipe<StringBuilder> drawCbPipe;
  private Pipe<StringBuilder> elementPipe;
  private Pipe<StringBuilder> enumPipe;
  private Pipe<StringBuilder> externRefPipe;
  private Pipe<StringBuilder> extraElementPipe;
  private Pipe<StringBuilder> filePipe;
  private Pipe<StringBuilder> fontsPipe;
  private Pipe<StringBuilder> fontLoadPipe;
  private Pipe<StringBuilder> headerPipe;
  private Pipe<StringBuilder> includesPipe;
  private Pipe<StringBuilder> initGuiPipe;
  private Pipe<StringBuilder> keypadCbPipe;
  private Pipe<StringBuilder> listboxCbPipe;
  private Pipe<StringBuilder> pathStoragePipe;
  private Pipe<StringBuilder> resourcesPipe;
  private Pipe<StringBuilder> saveRefPipe;
  private Pipe<StringBuilder> sliderCbPipe;
  private Pipe<StringBuilder> spinnerCbPipe;
  private Pipe<StringBuilder> startupPipe;
  private Pipe<StringBuilder> tickCbPipe;
  
  /**
   * Gets the single instance of CodeGenerator.
   *
   * @return single instance of CodeGenerator
   */
  public static synchronized CodeGenerator getInstance() {
    if (instance == null) {
      instance = new CodeGenerator();
      CodeGenerator.m_sFileSep = System.getProperty("file.separator");
    }
    // create our pipe line for processing code generation
    instance.appPipe = new AppPipe(instance);
    instance.buttonCbPipe = new ButtonCbPipe(instance);
    instance.checkboxCbPipe = new CheckboxCbPipe(instance);
    instance.drawCbPipe = new DrawCbPipe(instance);
    instance.elementPipe = new ElementPipe(instance);
    instance.enumPipe = new EnumPipe(instance);
    instance.externRefPipe = new ExternRefPipe(instance);
    instance.extraElementPipe = new ExtraElementPipe(instance);
    instance.filePipe = new FilePipe(instance);
    instance.fontsPipe = new FontsPipe(instance);
    instance.fontLoadPipe = new FontLoadPipe(instance);
    instance.headerPipe = new HeaderPipe(instance);
    instance.includesPipe = new IncludesPipe(instance);
    instance.initGuiPipe = new InitGuiPipe(instance);
    instance.keypadCbPipe = new KeypadCbPipe(instance);
    instance.listboxCbPipe = new ListboxCbPipe(instance);
    instance.pathStoragePipe = new PathStoragePipe(instance);
    instance.resourcesPipe = new ResourcesPipe(instance);
    instance.saveRefPipe = new SaveRefPipe(instance);
    instance.sliderCbPipe = new SliderCbPipe(instance);
    instance.spinnerCbPipe = new SpinnerCbPipe(instance);
    instance.startupPipe = new StartupPipe(instance);
    instance.tickCbPipe = new TickCbPipe(instance);

    // create our workflow pipeline for single Linux C file output
    instance.workFlow_Linux = new Pipeline<StringBuilder>(
        instance.filePipe,
        instance.includesPipe,
        instance.pathStoragePipe,
        instance.fontsPipe,
        instance.resourcesPipe,
        instance.enumPipe,
        instance.elementPipe,
        instance.extraElementPipe,
        instance.saveRefPipe,
        instance.buttonCbPipe,
        instance.checkboxCbPipe,
        instance.keypadCbPipe,
        instance.spinnerCbPipe,
        instance.listboxCbPipe,
        instance.drawCbPipe,
        instance.sliderCbPipe,
        instance.tickCbPipe,
        instance.initGuiPipe,
        instance.fontLoadPipe,
        instance.startupPipe
    );

    // create our workflow pipeline for single ino file output bCompat=true
    instance.workFlow_Compat = new Pipeline<StringBuilder>(
        instance.filePipe,
        instance.includesPipe,
        instance.fontsPipe,
        instance.resourcesPipe,
        instance.enumPipe,
        instance.elementPipe,
        instance.extraElementPipe,
        instance.saveRefPipe,
        instance.buttonCbPipe,
        instance.checkboxCbPipe,
        instance.keypadCbPipe,
        instance.spinnerCbPipe,
        instance.listboxCbPipe,
        instance.drawCbPipe,
        instance.sliderCbPipe,
        instance.tickCbPipe,
        instance.initGuiPipe,
        instance.fontLoadPipe,
        instance.startupPipe
    );

    // create our workflow pipeline for header file output bCompat=false
    instance.workFlow_ArduinoHdr = new Pipeline<StringBuilder>(
        instance.filePipe,
        instance.includesPipe,
        instance.fontsPipe,
        instance.resourcesPipe,
        instance.enumPipe,
        instance.elementPipe,
        instance.extraElementPipe,
        instance.externRefPipe,
        instance.initGuiPipe,
        instance.fontLoadPipe,
        instance.startupPipe
    );

    // create our workflow pipeline for single file output bCompat=false
    instance.workFlow_ArduinoIno = new Pipeline<StringBuilder>(
        instance.appPipe,
        instance.headerPipe,
        instance.saveRefPipe,
        instance.buttonCbPipe,
        instance.checkboxCbPipe,
        instance.keypadCbPipe,
        instance.spinnerCbPipe,
        instance.listboxCbPipe,
        instance.drawCbPipe,
        instance.sliderCbPipe,
        instance.tickCbPipe
    );

    return instance;
  }
  
  /**
   * Instantiates a new code generator.
   */
  public CodeGenerator() {
  }
  
  /**
   * Generate code setup.
   *
   * @param folder
   *          the folder
   * @param fileName
   *          the file name
   * @param pages
   *          the pages
   * @return the <code>string</code> object
   */
  public String generateCode(File projectFile, List<PagePane> pages, boolean bCompat) {
    this.pages = pages;
    
    // First build up a full list of widget models for later phases
    models = new ArrayList<WidgetModel>();
    for (PagePane p : pages) {
      List<Widget> widgets = p.getWidgets();
      for (Widget w : widgets) {
        models.add(w.getModel());
      }
    }
    // create our template manager
    tm = new TemplateManager();
    // grab user's defaults from the General model so we can determine our target platform.
    String target =Controller.getTargetPlatform();  
    try {
      // set our FSM state
      if(target.equals(ProjectModel.PLATFORM_LINUX)) {
        // do not use header version with linux C files
        m_nState = ST_LINUX;
        tm.storeTemplatesFromFileName("linux.t");
      } else {
        if (bCompat) {
          m_nState = ST_ARDUINO_COMPAT;
        } else {
          m_nState = ST_ARDUINO_HDR;
        }
        tm.storeTemplatesFromFileName("graphics.t");
      }
      if (m_nState == 0) {
        Builder.logger.debug("Code Generation Failed: Graphics Library not defined");
        JOptionPane.showMessageDialog(null, "Code Generation Failed: Graphics Library not defined", 
            "Error", JOptionPane.ERROR_MESSAGE);
      }
      // adjust for chosen IDE
      pm = Controller.getProjectModel();
      if (pm.getIDE().equals(ProjectModel.IDE_PIO)) {
        m_nState += ST_PIO;
      }
      // do the work
      return doCodeGen(projectFile);
    } catch (CodeGenException e) {
      Builder.logger.debug("Code Generation Failed: " + e.toString());
      JOptionPane.showMessageDialog(null, "Code Generation Failed: " + e.toString(), 
          "Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }      

  /**
   * doCodeGen is the main code generation loop.
   *
   * @param projectFile
   *          the project file (*.prj)
   * @throws CodeGenException
   *           the code gen exception
   */
  public String doCodeGen(File projectFile) throws CodeGenException 
  {
    StringBuilder sBd = null;
    StringBuilder code = null;
    
    String folder = projectFile.getParent();
    // remove the project extension from our input file
    m_sProjectName = projectFile.getName();
    int n = m_sProjectName.indexOf(".prj");
    m_sProjectName = m_sProjectName.substring(0,n);
    
    File tmFile = null;
    File appFile = null;
    File hdrFile = null;
    BufferedWriter bw = null;

    try {
      // if we are using PlatformIO IDE we may need to create some directory structure
      if (m_nState >= 10) {
        PlatformIO.makePIOFileStruct(folder);
        PlatformIO.createIniFile(folder);
      }
      
      String appName = createAppName(m_sProjectName);
      m_sAppPath = createAppPath(folder);
      String appFullPath = m_sAppPath + appName;
      String hdrName = createHdrName(m_sProjectName);
      m_sHdrPath = createHdrPath(folder);
      String hdrFullPath = m_sHdrPath + hdrName;
      /* for user's information in case of any errors
       * we track our input file inside m_sTemplateFileName variable
       * which will either be the previous C Code app (*.ino), 
       * or our a template file like ino.t, ino2.t, or c.t
       */
      m_sTemplateFileName = appFullPath;
      m_sOutputFileName = appName;
      /* does our app c code exist?
       * NOTE: our *.prj file was already backed up by Controller.save()
       */
      appFile = new File(appFullPath);
      if (appFile.exists()) {
        boolean bUpgraded = false;
        /*
         * One more complication is a possible upgrade of
         * our single *.ino file needing to be broken up
         * into two files *.ino and *_GSLC.h
         */
        if (m_nState == ST_ARDUINO_HDR) {
          bUpgraded = modifyAppToUseHdr(folder, appFullPath, hdrFullPath);
        }
        if (!bUpgraded) {
          /*
           * No upgrade needed so just backup our existing App File
           */
          CommonUtils.backupFile(appFile);
        }
      } else {
        /*
         * since our app doesn't currently exist create a new one
         * from one of our templates, ino.t, ino2.t or c.t
         */
        m_sTemplateFileName = createTemplateName();
        tmFile = new File(m_sTemplateFileName);
        CommonUtils.copyFile(tmFile, appFile);
      }
      /* now dump our app starting code into a string buffer
       * so our pipes can process it for code generation
       */
      sBd = CodeUtils.copyFileToBuffer(appFile);
      // run our chosen pipe line
      switch (m_nState) {
        case ST_PIO_LINUX:
        case ST_LINUX:
          code = workFlow_Linux.process(sBd);
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(appFile), "UTF-8"));
          bw.write(code.toString());
          break;
        case ST_PIO_ARDUINO_COMPAT:
        case ST_ARDUINO_COMPAT:
          // run our pipe line
          code = workFlow_Compat.process(sBd);
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(appFile), "UTF-8"));
          bw.write(code.toString());
          break;
        case ST_PIO_ARDUINO_HDR:
        case ST_ARDUINO_HDR:
          /* here we have two files to output
           * the app file *.ino,*.c, or *.cpp
           * and the header file *._GSLC.h
           */
          code = workFlow_ArduinoIno.process(sBd); 
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(appFile), "UTF-8"));
          bw.write(code.toString());
          bw.flush();
          bw.close();
          // finished with app now we deal with our header
          hdrFile = new File(hdrFullPath);
          String hdrTemplate = hdrFullPath;
          if (!hdrFile.exists()) {
            /* since it doesn't exist we create a fresh copy
             * from our template HDR_TEMPLATE
             */
            String name = CommonUtils.getWorkingDir() +
                TEMPLATE_FOLDER + m_sFileSep + HDR_TEMPLATE;
            tmFile = new File(name);
            CommonUtils.copyFile(tmFile, hdrFile);
            hdrTemplate = name;
          } else {
            // Make a backup copy of project's header file
            CommonUtils.backupFile(hdrFile);
          }
          sBd = CodeUtils.copyFileToBuffer(hdrFile);
          // run our pipe line
          m_sTemplateFileName = hdrTemplate;  // for any error messages
          m_sOutputFileName = hdrName;
          code = workFlow_ArduinoHdr.process(sBd);
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(hdrFile), "UTF-8"));
          bw.write(code.toString());
          break;
      }
      bw.flush();
      bw.close();
      return new String(m_sProjectName + m_sFileSep + appName);
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    } 
  }
  
  private String createAppName(String m_sProjectName) {
    String name=null;
    switch (m_nState) {
      case ST_LINUX:
        name = new String(m_sProjectName + LINUX_EXT);
        break;
      case ST_ARDUINO_COMPAT:
      case ST_ARDUINO_HDR:
        name = new String(m_sProjectName + ARDUINO_EXT);
        break;
      case ST_PIO_LINUX:
      case ST_PIO_ARDUINO_COMPAT:
      case ST_PIO_ARDUINO_HDR:
        name = PIO_APP;
        break;
    }
    return name;
  }

  private String createHdrName(String m_sProjectName) {
    String name=null;
    switch (m_nState) {
      case ST_LINUX:
        break;
      case ST_ARDUINO_COMPAT:
        break;
      case ST_ARDUINO_HDR:
      case ST_PIO_LINUX:
      case ST_PIO_ARDUINO_COMPAT:
      case ST_PIO_ARDUINO_HDR:
        name = new String(m_sProjectName + HEADER_EXT);
        break;
    }
    return name;
  }
  
  private String createTemplateName() {
    String fullPath = null;
    String wdPath = CommonUtils.getWorkingDir() + TEMPLATE_FOLDER + m_sFileSep;
    switch (m_nState) {
      case ST_LINUX:
      case ST_PIO_LINUX:
        fullPath = wdPath + LINUX_TEMPLATE;
        break;
      case ST_ARDUINO_COMPAT:
      case ST_PIO_ARDUINO_COMPAT:
        fullPath = wdPath + ARDUINO_COMPAT_TEMPLATE;
      case ST_ARDUINO_HDR:
      case ST_PIO_ARDUINO_HDR:
        fullPath = wdPath + ARDUINO_TEMPLATE;
        break;
  }
    return fullPath;
  }

  private String createAppPath(String folder) {
    String name=null;
    switch (m_nState) {
      case ST_LINUX:
      case ST_ARDUINO_COMPAT:
      case ST_ARDUINO_HDR:
        name = folder + m_sFileSep;
        break;
      case ST_PIO_LINUX:
      case ST_PIO_ARDUINO_COMPAT:
      case ST_PIO_ARDUINO_HDR:
        name = folder + m_sFileSep + "src" + m_sFileSep;
        break;
    }
    return name;
  }

  private String createHdrPath(String folder) {
    String name=null;
    switch (m_nState) {
      case ST_LINUX:
        break;
      case ST_ARDUINO_COMPAT:
        break;
      case ST_ARDUINO_HDR:
        name = folder + m_sFileSep;
        break;
      case ST_PIO_LINUX:
        break;
      case ST_PIO_ARDUINO_COMPAT:
        break;
      case ST_PIO_ARDUINO_HDR:
        name = folder + m_sFileSep + "include" + m_sFileSep;
        break;
    }
    return name;
  }
  
  /**
   * Modify the application to use a header
   * instead of a single file, if necessary
   * 
   * @param folder
   * @param appName
   * @param hdrName
   */
  public boolean modifyAppToUseHdr(String folder, String appName, String hdrName) throws CodeGenException {
    File appFile = new File(appName);
    if (!appFile.exists()) {
      return false; // nothing to do here
    }
    File hdrFile = new File(hdrName);
    if (hdrFile.exists()) {
      return false; // nothing to do here
    }
    /*
     * It exists so we need to read the first line. It will tell us if we need to
     * upgrade or not.
     */
    BufferedReader br;
    try {
      br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(appFile), "UTF8"));
      String line = "";
      if ((line = br.readLine()) != null) {
        /*
         * we have three conditions here 
         * 1- line == "//<App !Start!>" no upgrade needed
         * 2- line == "//<File !Start!>" upgrade needed 
         * 3- line not equal to either - really old beta project should have been upgraded
         */
        if (!line.equals("//<App !Start!>")) {
          if (!line.equals("//<File !Start!>")) {
            br.close();
            throw new CodeGenException("file: " + getTemplateName() + "\n is corrupted missing tag: //<File !Start!>");
          }
          // Make a backup copy of app file
          br.close();
          String backupName = new String(appName + ".orig");
          File backupFile = new File(backupName);
          appFile.renameTo(backupFile);
          /*
           * now we want to remove tags from the backup file.
           * and create a new *.ino file. 
           * The new header file will be created later using a template
           */
          removeTags(appName, backupName);
        }
      }
      br.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * remove tags to create the new app file
   * 
   * @param appName
   * @param backupName
   */
  public void removeTags(String appName, String backupName) throws CodeGenException, IOException {
    File appFile = new File(appName);
    File backupFile = new File(backupName);
    BufferedReader br;
    BufferedWriter bwA;
    String FILE_TAG = new String(Tags.TAG_PREFIX + Tags.FILE_TAG + Tags.TAG_SUFFIX_START);
    String FILE_END_TAG = new String(Tags.TAG_PREFIX + Tags.FILE_TAG + Tags.TAG_SUFFIX_END);
    String APP_TAG = new String(Tags.TAG_PREFIX + Tags.APP_TAG + Tags.TAG_SUFFIX_START);
    String APP_END_TAG = new String(Tags.TAG_PREFIX + Tags.APP_TAG + Tags.TAG_SUFFIX_END);
    String HEADER_TAG = new String(Tags.TAG_PREFIX + Tags.HEADER_TAG + Tags.TAG_SUFFIX_START);
    String HEADER_END_TAG = new String(Tags.TAG_PREFIX + Tags.HEADER_TAG + Tags.TAG_SUFFIX_END);
    String LOADFONTS_TAG = new String(Tags.TAG_PREFIX + Tags.LOADFONTS_TAG + Tags.TAG_SUFFIX_START);
    String LOADFONTS_END_TAG = new String(Tags.TAG_PREFIX + Tags.LOADFONTS_TAG + Tags.TAG_SUFFIX_END);
    String STARTUP_TAG = new String(Tags.TAG_PREFIX + Tags.STARTUP_TAG + Tags.TAG_SUFFIX_START);
    String STARTUP_END_TAG = new String(Tags.TAG_PREFIX + Tags.STARTUP_TAG + Tags.TAG_SUFFIX_END);
    String COMMENTS_START = new String("// ------------------------------------------------");
    try {
      br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(backupFile), "UTF8"));
      bwA = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(appFile), "UTF-8"));
      String sTestTag = "";
      String line = "";
      String line2 = "";
      line = br.readLine();
      sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
      if (sTestTag.equals(FILE_TAG)) {
        bwA.write(APP_TAG);
        bwA.newLine();
        while ((line = br.readLine()) != null) {
          if (line.equals(FILE_END_TAG)) {
            break;
          }
          bwA.write(line);
          bwA.newLine();
        }
        bwA.write(APP_END_TAG);
        bwA.newLine();
        bwA.newLine();
      } else {
        br.close();
        bwA.close();
        throw new CodeGenException("file: " + backupName + "\n is corrupted missing tag:" + FILE_TAG);
      }

      while ((line = br.readLine()) != null) {
        if (line.equals("#include \"GUIslice.h\"")) {
          bwA.write(HEADER_TAG);
          bwA.newLine();
          bwA.write(HEADER_END_TAG);
          bwA.newLine();
          bwA.newLine();
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      // remove tags
      int nBlankLines = 0;
      while ((line = br.readLine()) != null) {
        if (line.isEmpty()) {
          nBlankLines++;
          if (nBlankLines < 2) {
            bwA.write(line);
            bwA.newLine();
         }
        } else {
          if (line.equals("//<Includes !Start!>")) {
            CodeUtils.discardTag(br, "//<Includes !End!>");
          } else if (line.equals("//<Fonts !Start!>")) {
              CodeUtils.discardTag(br, "//<Fonts !End!>");
          } else if (line.equals("//<Resources !Start!>")) {
            CodeUtils.discardTag(br, "//<Resources !End!>");
          } else if (line.equals("//<Enum !Start!>")) {
            CodeUtils.discardTag(br, "//<Enum !End!>");
          } else if (line.equals("//<ElementDefines !Start!>")) {
            CodeUtils.discardTag(br, "//<ElementDefines !End!>");
          } else if (line.equals("//<GUI_Extra_Elements !Start!>")) {
            CodeUtils.discardTag(br, "//<GUI_Extra_Elements !End!>");
          } else if (line.equals("gslc_tsGui                      m_gui;")) {
            CodeUtils.discardTag(br, "gslc_tsPage                     m_asPage[MAX_PAGE];");
          } else if (line.equals("// Include any extended elements")) {
            continue;
          } else if (line.equals("#include \"GUIslice_drv.h\"")) {
            continue;
          } else if (line.equals(COMMENTS_START)) {
            line2 = br.readLine();
            if (line2.equals("// Headers and Defines for fonts")) {
              CodeUtils.discardTag(br, COMMENTS_START);
            } else if (line2.equals("// Defines for resources")) {
              CodeUtils.discardTag(br, COMMENTS_START);
            } else if (line2.equals("// Enumerations for pages, elements, fonts, images")) {
              CodeUtils.discardTag(br, COMMENTS_START);
            } else if (line2.equals("// Instantiate the GUI")) {
              CodeUtils.discardTag(br, COMMENTS_START);
            } else if (line2.equals("// Define the maximum number of elements and pages")) {
              CodeUtils.discardTag(br, COMMENTS_START);
            } else if (line2.equals("// Create element storage")) {
              CodeUtils.discardTag(br, COMMENTS_START);
            } else if (line2.equals("// Create page elements")) {
              CodeUtils.discardTag(br, "}");
              break;
            } else {
              bwA.write(line);
              bwA.newLine();
              bwA.write(line2);
              bwA.newLine();
              nBlankLines = 0;
            }
          } else {
            bwA.write(line);
            bwA.newLine();
            nBlankLines = 0;
          }
        } 
      }
      // scan for gslc_Init and remove it
      boolean bFoundInit = false;
      while ((line = br.readLine()) != null) {
        // break the line up into words
        if (!line.isEmpty()) {
          String[] words = line.split("\\W+");
          if (words.length > 0) {
            for (int i=0; i<words.length; i++) {
              if (words[i].equals("gslc_Init")) {
                bFoundInit = true;
              }
            }
          }
        }
        if (bFoundInit) break;
        bwA.write(line);
        bwA.newLine();
      }
      // remove fonts tag
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals(LOADFONTS_TAG)) {
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line
            ).replaceAll(EMPTY_STRING);
        if (sTestTag.equals(LOADFONTS_END_TAG)) {
          break;
        }
      }
      // rename InitGUI to InitGUIslice_gen
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals("InitGUI();")) {
          bwA.write("  InitGUIslice_gen();");
          bwA.newLine();
          bwA.newLine();
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      // remove STARTUP tag
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals(STARTUP_TAG)) {
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals(STARTUP_END_TAG)) {
          break;
        }
      }
      while ((line = br.readLine()) != null) {
        bwA.write(line);
        bwA.newLine();
      }

      br.close();
      bwA.close();
    } catch (IOException e) {
      throw new CodeGenException("removeTags IOException: " + e.toString());
    }
  }

  /**
   * Gets the pages.
   *
   * @return the pages
   */
  public List<PagePane> getPages() {
    return pages;
  }
  
  /**
   * Gets the models.
   *
   * @return the models
   */
  public List<WidgetModel> getModels() {
    return models;
  }
  
  /**
   * Gets the current project name.
   *
   * @return the project name
   */
  public String getProjectName() {
    return m_sProjectName;
  }
  
  /**
   * Gets the workflow's current template name.
   *
   * @return the project template
   */
  public String getTemplateName() {
    return m_sTemplateFileName;
  }
  
  /**
   * Gets the workflow's current output name.
   *
   * @return the project template
   */
  public String getOutputName() {
    return m_sOutputFileName;
  }
  
  /**
   * Gets the template manager.
   *
   * @return the template manager
   */
  public TemplateManager getTemplateManager() {
    return tm;
  }

  /**
   * get the path to where our application will store C source code
   * This varies according to IDE in use. 
   * PlatformIO for example uses "src" folder.
   * @return Application Path
   */
  public String getAppPath() {
    return m_sAppPath;
  }
  
  /**
   * get the path to where our application will store H header source code
   * This varies according to IDE in use.
   * PlatformIO for example uses "include" folder.
   * @return Header path 
   */
  public String getHdrPath() {
    return m_sHdrPath;
  }
}
