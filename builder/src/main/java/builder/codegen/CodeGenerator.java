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
 * <li>arduino.t for arduino code templates
 * <li>linux.t for linux code templates
 * 
 * @author Paul Conti
 * 
 */
public class CodeGenerator {
  
  /** The instance. */
  private static CodeGenerator instance = null;

  /** The Constant PREFIX. */
  public final static String PREFIX                 = "//<";
  
  /** The Constant ARDUINO_FONT_TEMPLATE. */
  public  final static String ARDUINO_FONT_TEMPLATE   = "arduinofonts.csv";
  
  /** The Constant LINUX_FONT_TEMPLATE. */
  public  final static String LINUX_FONT_TEMPLATE     = "linuxfonts.csv";
  
  /** The Constant ARDUINO_RES. */
  public  final static String ARDUINO_RES             = "arduino_res";
  
  /** The Constant LINUX_RES. */
  public  final static String LINUX_RES               = "linux_res";
  
  /** The Constant ARDUINO_TEMPLATE. */
  public  final static String HDR_TEMPLATE            = "hdr.t";
  
  /** The Constant ARDUINO_TEMPLATE. */
  public  final static String ARDUINO_TEMPLATE        = "ino2.t";
  
  /** The Constant ARDUINO_TEMPLATE. */
  public  final static String ARDUINO_COMPAT_TEMPLATE = "ino.t";
  
  /** The Constant LINUX_FILE. */
  public  final static String LINUX_TEMPLATE          = "c.t";
  
  /** The Constant ARDUINO_EXT. */
  public  final static String ARDUINO_EXT             = ".ino";
  
  /** The Constant HEADER_EXT. */
  public  final static String HEADER_EXT              = "_GSLC.h";
  
  /** The Constant LINUX_EXT. */
  public  final static String LINUX_EXT               = ".c";

  /** regex pattern */
  private final static Pattern LTRIM = Pattern.compile("^\\s+");
  private final static String EMPTY_STRING = "";

  /** The projects' pages. */
  private List<PagePane> pages;
  
  /** The full list of widget models. */
  List<WidgetModel> models;
  
  /** The current project name. */
  String sProjectName = null;
  
  /** The current workflow file name. */
  String sTemplateFileName = null;
  
  /** The current workflow output file name. */
  String sOutputFileName = null;
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /** The background color. */
  Color background;
  
  /** fsm states */
  protected static final int    ST_UNDEFINED             = 0;
  protected static final int    ST_LINUX                 = 10;
  protected static final int    ST_LINUX_NEW             = 11;
  protected static final int    ST_LINUX_EXISTS          = 12;
  protected static final int    ST_ARDUINO_COMPAT        = 20;
  protected static final int    ST_ARDUINO_COMPAT_NEW    = 21;
  protected static final int    ST_ARDUINO_COMPAT_EXISTS = 22;
  protected static final int    ST_ARDUINO_HDR           = 30;
  protected static final int    ST_ARDUINO_HDR_NEW       = 31;
  protected static final int    ST_ARDUINO_HDR_EXISTS    = 32;
  
  protected int m_nState = ST_UNDEFINED;
  
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
        tm.storeTemplates("linux.t");
      } else {
        if (bCompat) {
          m_nState = ST_ARDUINO_COMPAT;
        } else {
          m_nState = ST_ARDUINO_HDR;
        }
        tm.storeTemplates("arduino.t");
      }
      // do the work
      return doCodeGen(projectFile);
    } catch (CodeGenException e) {
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
    String sMessage = null;
    StringBuilder sBd = null;
    StringBuilder code = null;
    
    String folder = projectFile.getParent();
    // remove the project extension from our input file
    sProjectName = projectFile.getName();
    int n = sProjectName.indexOf(".prj");
    sProjectName = sProjectName.substring(0,n);
    String appName = null;
    String appFullPath = null;
    String hdrName = null;
    String hdrFullPath = null;
    
    File tmFile = null;
    File appFile = null;
    File hdrFile = null;
    BufferedWriter bw = null;
    try {
      switch (m_nState) {
        case ST_LINUX:
          appName = new String(sProjectName + LINUX_EXT);
          appFullPath = folder + System.getProperty("file.separator") + appName;
          sTemplateFileName = appFullPath;
          /* Do we need to create our application file from templateName?
           */
          appFile = new File(appFullPath);
          if (!appFile.exists()) {
            String fullPath = CommonUtils.getInstance().getWorkingDir() +
                "templates" + System.getProperty("file.separator") + LINUX_TEMPLATE;
            tmFile = new File(fullPath);
            CommonUtils.copyFile(tmFile, appFile);
            sTemplateFileName = fullPath;
          } else {
            // Make a backup copy of project's file
            CommonUtils.backupFile(appFile);
          }
          sBd = CodeUtils.copyFileToBuffer(appFile);
          // run our pipe line
          sTemplateFileName = appFullPath;
          sOutputFileName = appName;
          code = workFlow_Linux.process(sBd);
//          bw = new BufferedWriter(new FileWriter(appFile));
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(appFile), "UTF-8"));
          bw.write(code.toString());
          bw.flush();
          bw.close();
          sMessage = new String(appName);
          break;
        case ST_ARDUINO_COMPAT:
          appName = new String(sProjectName + ARDUINO_EXT);
          appFullPath = folder + System.getProperty("file.separator") + appName;
          sTemplateFileName = appFullPath;
          /* Do we need to upgrade a beta application file?
           */
          upgradeBetaApp(appFullPath);
          /* Do we need to create our application file from templateName?
           */
          appFile = new File(appFullPath);
          if (!appFile.exists()) {
            String fullPath = CommonUtils.getInstance().getWorkingDir() +
                "templates" + System.getProperty("file.separator") + ARDUINO_COMPAT_TEMPLATE;
            tmFile = new File(fullPath);
            CommonUtils.copyFile(tmFile, appFile);
            sTemplateFileName = fullPath;
          } else {
            // Make a backup copy of project's file
            CommonUtils.backupFile(appFile);
          }
          sBd = CodeUtils.copyFileToBuffer(appFile);
          // run our pipe line
          sOutputFileName = appName;
          code = workFlow_Compat.process(sBd);
//          bw = new BufferedWriter(new FileWriter(appFile));
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(appFile), "UTF-8"));
          bw.write(code.toString());
          bw.flush();
          bw.close();
          sMessage = new String(appName);
          break;
        case ST_ARDUINO_HDR:
          /* due to my dyslexia I created files with _GLSC.h
           * instead of _GSLC.h so I check here and delete
           * them if found.
           */
          hdrName = new String(sProjectName + "_GLSC.h");
          hdrFullPath = folder + System.getProperty("file.separator") + hdrName;
          hdrFile = new File(hdrFullPath);
          if (hdrFile.exists()) {
            hdrFile.delete();
          }
          /* Back to business - create app + header pathnames 
           */
          appName = new String(sProjectName + ARDUINO_EXT);
          appFullPath = folder + System.getProperty("file.separator") + appName;
          hdrName = new String(sProjectName + HEADER_EXT);
          hdrFullPath = folder + System.getProperty("file.separator") + hdrName;
          sTemplateFileName = appFullPath;
          /* Do we need to repair the application file?
           */
          removePastSins(appFullPath);
          /* Do we need to upgrade a beta application file?
           */
          upgradeBetaApp(appFullPath);
          /* now we need to determine if we have a 
           * older single file that needs upgrading.
           */
          modifyAppToUseHdr(folder, appFullPath, hdrFullPath);
          /* Do we need to create our application file from templateName?
           */
          appFile = new File(appFullPath);
          if (!appFile.exists()) {
            String name= CommonUtils.getInstance().getWorkingDir() +
                "templates" + System.getProperty("file.separator") + ARDUINO_TEMPLATE;
            tmFile = new File(name);
            CommonUtils.copyFile(tmFile, appFile);
            sTemplateFileName = name;
          } else {
            // Make a backup copy of project's app file
            CommonUtils.backupFile(appFile);
          }
          hdrFile = new File(hdrFullPath);
          String hdrTemplate = hdrFullPath;
          if (!hdrFile.exists()) {
            String name = CommonUtils.getInstance().getWorkingDir() +
                "templates" + System.getProperty("file.separator") + HDR_TEMPLATE;
            tmFile = new File(name);
            CommonUtils.copyFile(tmFile, hdrFile);
            hdrTemplate = name;
          } else {
            // Make a backup copy of project's header file
            CommonUtils.backupFile(hdrFile);
          }
          sBd = CodeUtils.copyFileToBuffer(appFile);
          // run our pipe line
          sOutputFileName = appName;
          code = workFlow_ArduinoIno.process(sBd);
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(appFile), "UTF-8"));
          bw.write(code.toString());
          bw.flush();
          bw.close();
          sBd = CodeUtils.copyFileToBuffer(hdrFile);
          // run our pipe line
          sTemplateFileName = hdrTemplate;  // for any error messages
          sOutputFileName = hdrName;
          code = workFlow_ArduinoHdr.process(sBd);
          bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(hdrFile), "UTF-8"));
          bw.write(code.toString());
          bw.flush();
          bw.close();
          sMessage = new String(appName + ", " + hdrName);
          break;
      }
      return sMessage;
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    } 
  }
  
  /**
   * upgrade our beta version of app to current single file app 
   * @param appName
   * @param backupName
   * @param hdrName
   * @param bOldApp
   */
  public void upgradeBetaApp(String appName) throws CodeGenException, IOException {
    File appFile = new File(appName);
    if (!appFile.exists()) {
      return; // nothing to do here
    }
    BufferedReader br;
    BufferedWriter bwA;
    String APP_TAG = new String(Tags.TAG_PREFIX + Tags.APP_TAG + Tags.TAG_SUFFIX_START);
    String FILE_TAG = new String(Tags.TAG_PREFIX + Tags.FILE_TAG + Tags.TAG_SUFFIX_START);
    String FILE_END_TAG = new String(Tags.TAG_PREFIX + Tags.FILE_TAG + Tags.TAG_SUFFIX_END);
    String FONTS_TAG = new String(Tags.TAG_PREFIX + Tags.FONTS_TAG + Tags.TAG_SUFFIX_START);
    String INCLUDES_TAG = new String(Tags.TAG_PREFIX + Tags.INCLUDES_TAG + Tags.TAG_SUFFIX_START);
    String INCLUDES_END_TAG = new String(Tags.TAG_PREFIX + Tags.INCLUDES_TAG + Tags.TAG_SUFFIX_END);
    String CHECKBOXCB_TAG = new String(Tags.TAG_PREFIX + Tags.CHECKBOXCB_TAG + Tags.TAG_SUFFIX_START);
    String CHECKBOXCB_END_TAG = new String(Tags.TAG_PREFIX + Tags.CHECKBOXCB_TAG + Tags.TAG_SUFFIX_END);
    String KEYPADCB_TAG = new String(Tags.TAG_PREFIX + Tags.KEYPADCB_TAG + Tags.TAG_SUFFIX_START);
    String KEYPADCB_END_TAG = new String(Tags.TAG_PREFIX + Tags.KEYPADCB_TAG + Tags.TAG_SUFFIX_END);
    String SPINNERCB_TAG = new String(Tags.TAG_PREFIX + Tags.SPINNERCB_TAG + Tags.TAG_SUFFIX_START);
    String SPINNERCB_END_TAG = new String(Tags.TAG_PREFIX + Tags.SPINNERCB_TAG + Tags.TAG_SUFFIX_END);
    String LISTBOXCB_TAG = new String(Tags.TAG_PREFIX + Tags.LISTBOXCB_TAG + Tags.TAG_SUFFIX_START);
    String LISTBOXCB_END_TAG = new String(Tags.TAG_PREFIX + Tags.LISTBOXCB_TAG + Tags.TAG_SUFFIX_END);
    String STARTUP_TAG = new String(Tags.TAG_PREFIX + Tags.STARTUP_TAG + Tags.TAG_SUFFIX_START);
    String STARTUP_END_TAG = new String(Tags.TAG_PREFIX + Tags.STARTUP_TAG + Tags.TAG_SUFFIX_END);
    
    try {
      br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(appFile), "UTF8"));

      String backupName = "";
      String sTestTag = "";
      String line = "";

      line = br.readLine();
      /*
       * we have three conditions here 
       * 1- line == "//<App !Start!>" no upgrade needed
       * 2- line == "//<File !Start!>" upgrade may be needed, look deeper 
       * 3- line not equal to either - needs upgrade
       */
      if (line.equals(APP_TAG)) {
        br.close();
        return;
      }
      if (line.equals(FILE_TAG)) {
        /* We need to look deeper, search for INCLUDES_TAG 
         * If we find it great, no update needed.
         * If we find FONTS_TAG first we need upgrade
         */
        boolean bNeedUpgrade = false;
        while ((line = br.readLine()) != null) {
          if (line.equals(INCLUDES_TAG)) {
            break;
          }
          if (line.equals(FONTS_TAG)) {
            bNeedUpgrade = true;
            break;
          }
        }
        if (!bNeedUpgrade) {
          br.close();
          return;
        }
      } 
      // We need to upgrade so first make a backup copy of app file
      br.close();
      backupName = new String(appName + ".beta");
      File backupFile = new File(backupName);
      appFile.renameTo(backupFile);
      br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(backupFile), "UTF8"));
      appFile = new File(appName);
      bwA = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(appFile), "UTF-8"));
      line = br.readLine();
      if (!line.equals(FILE_TAG)) {
        bwA.write(FILE_TAG);
        bwA.newLine();
        bwA.write(line);
        bwA.newLine();
        while ((line = br.readLine()) != null) {
          if (line.isEmpty())
            break;
          bwA.write(line);
          bwA.newLine();
        }
        bwA.write(FILE_END_TAG);
        bwA.newLine();
        bwA.newLine();
      } 

      while ((line = br.readLine()) != null) {
        if (line.equals("#include \"GUIslice_ex.h\"")) {
          continue;
        }
        if (line.equals("#include <Adafruit_GFX.h>")) {
          bwA.write(line);
          bwA.newLine();
          bwA.newLine();
          bwA.write(INCLUDES_TAG);
          bwA.newLine();
          bwA.write(INCLUDES_END_TAG);
          bwA.newLine();
          bwA.newLine();
          continue;
        }
        if (line.equals("// Common Button callback")) {
          bwA.write(line);
          bwA.newLine();
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      while ((line = br.readLine()) != null) {
        if (line.equals("}")) {
          bwA.write(line);
          bwA.newLine();
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      bwA.newLine();
      bwA.write(CHECKBOXCB_TAG);
      bwA.newLine();
      bwA.write(CHECKBOXCB_END_TAG);
      bwA.newLine();

      bwA.write(KEYPADCB_TAG);
      bwA.newLine();
      bwA.write(KEYPADCB_END_TAG);
      bwA.newLine();

      bwA.write(SPINNERCB_TAG);
      bwA.newLine();
      bwA.write(SPINNERCB_END_TAG);
      bwA.newLine();

      bwA.write(LISTBOXCB_TAG);
      bwA.newLine();
      bwA.write(LISTBOXCB_END_TAG);
      bwA.newLine();

      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals("InitGUI();")) {
          bwA.write(line);
          bwA.newLine();
          bwA.newLine();
          bwA.write(STARTUP_TAG);
          bwA.newLine();
          bwA.write(STARTUP_END_TAG);
          bwA.newLine();
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      // remove Quick_Access section
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals("//<Quick_Access !Start!>")) {
          break;
        }
        bwA.write(line);
        bwA.newLine();
      }
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.equals("//<Quick_Access !End!>")) {
          break;
        }
      }
      // finish up by copying everything left over
      while ((line = br.readLine()) != null) {
        bwA.write(line);
        bwA.newLine();
      }
      br.close();
      bwA.close();
    } catch (IOException e) {
      throw new CodeGenException("IOException converting beta App: " + e.toString());
    }
  }

  /**
   * Modify the application to use a header
   * instead of a single file, if necessary
   * 
   * @param folder
   * @param appName
   * @param hdrName
   */
  public void modifyAppToUseHdr(String folder, String appName, String hdrName) throws CodeGenException {
    File appFile = new File(appName);
    if (!appFile.exists()) {
      return; // nothing to do here
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
      return;
    } catch (IOException e) {
      e.printStackTrace();
    }
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
   * remove past sins to create the new app file.
   * Over time we have made few mistakes that need cleaning up.
   * This routine will scan and repair them for future use.
   * It will first check the builder version number and if its
   * 14 or higher we do nothing. Otherwise we do a full scan
   * and if we don't find anything, great we just bail out.
   * Once we find the first problem we stop scanning and make
   * a backup copy with the extension ".bad" and then make our 
   * repairs to a new file with original appName.
   * 
   * @param appName
   */
  public void removePastSins(String appName) throws CodeGenException, IOException {
    String PROGMEM_ELEMREF_ERROR1  =
        "gslc_tsElem* pElem = pElemRef->pElem;";
    String PROGMEM_ELEMREF_ERROR2 =
        "gslc_tsElem*    pElem     = pElemRef->pElem;";
    String PROGMEM_ELEMREF_FIX  =
        "  gslc_tsElem* pElem = gslc_GetElemFromRef(&m_gui,pElemRef);";
    
    File appFile = new File(appName);
    if (!appFile.exists()) return;
    BufferedWriter bwA = null;
    BufferedReader br;
    try {
      br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(appFile), "UTF8"));
      String line = "";
      line = br.readLine();
      if (!(line.startsWith("//<App") || line.startsWith("//<File"))) {
        // not sure what we have here so bail
        br.close();
        return;
      }
      line = br.readLine();
      if (!line.startsWith("// FILE:")) {
        br.close();
        return;
      }
      line = br.readLine();
      if (!line.startsWith("// Created")) {
        br.close();
        return;
      }
      /* now we can check version number
       * 14 or greater and we have nothing further to do.
       * we will treat it as 9.99 number and ignore the final .b999
       */
      String sVersion;
      int n = line.indexOf("[");
      sVersion = line.substring(n+1, n+5);
      float version = Float.parseFloat(sVersion);
      if (version >= 0.14) {
        br.close();
        return;
      }
      // first scan for errors if none found bail.
      boolean bFoundError = false;
      String sTestTag = "";
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.startsWith("#include")) {
          if(sTestTag.endsWith("_GLSC.h\"")) {
            bFoundError = true;
            break;
          }
        } else if (sTestTag.equals(PROGMEM_ELEMREF_ERROR1)) {
          bFoundError = true;
          break;
        } else if (sTestTag.equals(PROGMEM_ELEMREF_ERROR2)) {
          bFoundError = true;
          break;
        }
      }
      br.close();
      if (!bFoundError) {
        return;
      }
      // found errors so we need to create a backup and fix them.
      String backupName = new String(appName + ".bad");
      File backupFile = new File(backupName);
      appFile.renameTo(backupFile);
      br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(backupFile), "UTF8"));
      appFile = new File(appName);
      bwA = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(appFile), "UTF-8"));
      String line2 = "";
      // now fix the errors
      while ((line = br.readLine()) != null) {
        sTestTag = LTRIM.matcher(line).replaceAll(EMPTY_STRING);
        if (sTestTag.startsWith("#include")) {
          if(sTestTag.endsWith("_GLSC.h\"")) {
            n = line.indexOf("_GLSC.h");
            line2 = line.substring(0,n) + "_GSLC.h\"";
            bwA.write(line2);
            bwA.newLine();
          } else {
            bwA.write(line);
            bwA.newLine();
          }
        } else if (sTestTag.equals(PROGMEM_ELEMREF_ERROR1)) {
          bwA.write(PROGMEM_ELEMREF_FIX);
          bwA.newLine();
        } else if (sTestTag.equals(PROGMEM_ELEMREF_ERROR2)) {
          bwA.write(PROGMEM_ELEMREF_FIX);
          bwA.newLine();
        } else {
          bwA.write(line);
          bwA.newLine();
        }
      }
      br.close();
      bwA.close();
    } catch (IOException e) {
      throw new CodeGenException("removePastSins IOException: " + e.toString());
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
    return sProjectName;
  }
  
  /**
   * Gets the workflow's current template name.
   *
   * @return the project template
   */
  public String getTemplateName() {
    return sTemplateFileName;
  }
  
  /**
   * Gets the workflow's current output name.
   *
   * @return the project template
   */
  public String getOutputName() {
    return sOutputFileName;
  }
  
  /**
   * Gets the template manager.
   *
   * @return the template manager
   */
  public TemplateManager getTemplateManager() {
    return tm;
  }
  
}
