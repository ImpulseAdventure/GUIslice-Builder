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
package builder.codegen;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import builder.codegen.pipes.ButtonCbPipe;
import builder.codegen.pipes.CheckboxCbPipe;
import builder.codegen.pipes.DrawCbPipe;
import builder.codegen.pipes.ElementPipe;
import builder.codegen.pipes.EnumPipe;
import builder.codegen.pipes.ExtraElementPipe;
import builder.codegen.pipes.FilePipe;
import builder.codegen.pipes.FontsPipe;
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
import builder.models.GeneralModel;
import builder.models.WidgetModel;
import builder.prefs.GeneralEditor;
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
 * <ul>
 * <li>ino.t for arduino C skeleton
 * <li>min.t for arduino using flash C skeleton
 * <li>c.t for linux C skeleton
 * <li>arduino.t for arduino code templates
 * <li>arduino_min.t for arduino flash code templates
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
  public  final static String ARDUINO_FONT_TEMPLATE  = "arduinofonts.csv";
  
  /** The Constant LINUX_FONT_TEMPLATE. */
  public  final static String LINUX_FONT_TEMPLATE    = "linuxfonts.csv";
  
  /** The Constant ARDUINO_RES. */
  public  final static String ARDUINO_RES            = "arduino_res";
  
  /** The Constant LINUX_RES. */
  public  final static String LINUX_RES              = "linux_res";
  
  /** The Constant ARDUINO_FILE. */
  public  final static String ARDUINO_FILE           = "ino.t";
  
  /** The Constant LINUX_FILE. */
  public  final static String LINUX_FILE             = "c.t";
  
  /** The Constant ARDUINO_EXT. */
  public  final static String ARDUINO_EXT            = ".ino";
  
  /** The Constant LINUX_EXT. */
  public  final static String LINUX_EXT              = ".c";

  /** The projects' pages. */
  private List<PagePane> pages;
  
  /** The full list of widget models. */
  List<WidgetModel> models;
  
  /** The skeleton file. */
  File skeletonFile = null;
  
  /** The project's template name. */
  String projectTemplate = null;
  
  /** The target. */
  String target = null;
  
  /** The template manager. */
  TemplateManager tm = null;
  
  /** The background color. */
  Color background;
  
  /** The code generation pipe line. */
  private Pipeline<StringBuilder> workFlow;
  
  /** The workflow pipes. */
  private Pipe<StringBuilder> buttonCbPipe;
  private Pipe<StringBuilder> checkboxCbPipe;
  private Pipe<StringBuilder> drawCbPipe;
  private Pipe<StringBuilder> elementPipe;
  private Pipe<StringBuilder> enumPipe;
  private Pipe<StringBuilder> extraElementPipe;
  private Pipe<StringBuilder> filePipe;
  private Pipe<StringBuilder> fontsPipe;
  private Pipe<StringBuilder> fontLoadPipe;
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
    instance.buttonCbPipe = new ButtonCbPipe(instance);
    instance.checkboxCbPipe = new CheckboxCbPipe(instance);
    instance.drawCbPipe = new DrawCbPipe(instance);
    instance.elementPipe = new ElementPipe(instance);
    instance.enumPipe = new EnumPipe(instance);
    instance.extraElementPipe = new ExtraElementPipe(instance);
    instance.filePipe = new FilePipe(instance);
    instance.fontsPipe = new FontsPipe(instance);
    instance.fontLoadPipe = new FontLoadPipe(instance);
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

    // create our workflow pipeline
    instance.workFlow = new Pipeline<StringBuilder>(
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
  public String generateCode(File projectFile, List<PagePane> pages) {
    String folder = projectFile.getParent();
    String fileName = projectFile.getName();
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
    GeneralModel gm = (GeneralModel) GeneralEditor.getInstance().getModel();
    target = gm.getTarget();  
    String defFName = null;
    String fileExt = null;
    try {
      if(target.equals("linux")) {
        // "linux"
        defFName = LINUX_FILE;
        fileExt = LINUX_EXT;
        tm.storeTemplates("linux.t");
        // Path Storage step is only for linux
        pathStoragePipe.pipeEn(true);
      } else {
        defFName = ARDUINO_FILE;
        fileExt = ARDUINO_EXT;
        tm.storeTemplates("arduino.t");
        pathStoragePipe.pipeEn(false);
      }
      // load our project template into a StringBuilder object
      StringBuilder sBd = processFiles(folder, fileName, defFName, fileExt);
      // pass the project template on to our workflow
      return doCodeGen(sBd);
    } catch (CodeGenException e) {
      JOptionPane.showMessageDialog(null, "Code Generation Failed: " + e.toString(), 
          "Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }
  }      

  /**
   * doCodeGen is the main code generation loop.
   *
   * @param fr
   *          the fr
   * @throws CodeGenException
   *           the code gen exception
   */
  public String doCodeGen(StringBuilder sBd) throws CodeGenException { 
     try {
      // run our pipe line
      StringBuilder code = workFlow.process(sBd);
      // before writing out our C Source file make a backup of the original
      if(skeletonFile.exists()) {
        // Make a backup copy of projectFile
        CommonUtils.getInstance().backupFile(skeletonFile);
      }
      String fileName = skeletonFile.getName();
      // now that all phases have completed output the results to our new c file
      BufferedWriter bw = new BufferedWriter(new FileWriter(skeletonFile));
      bw.write(code.toString());
      bw.flush();
      bw.close();
      return fileName;
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
    }
  }
  
  /**
   * Process files, makes a copy of the input template in the returned
   * StringBuilder object, also makes any backups necessary for recovery.
   *
   * @param folder
   *          the folder
   * @param fileName
   *          the file name
   * @return the <code>StringBuilder</code> object that contains the complete template.
   * @throws CodeGenException
   *           the code gen exception
   */
  private StringBuilder processFiles(String folder, String fileName, String defFName, String fileExt) 
    throws CodeGenException {
    if (fileName == null) {
      throw new CodeGenException("CodeGen fileName==null");
    }
    // remove the project extension from our input file and replace with either .ino or .C
    int n = fileName.indexOf(".prj");
    String tmp = fileName.substring(0,n);
    projectTemplate = new String(folder + System.getProperty("file.separator") + tmp + fileExt);
    skeletonFile = new File(projectTemplate);
    File templateFile = null;
    try {
      // Here we are either going to use a previously generated file as input
      // or we are generating a brand new file from one of our templates.
      // I do all of this so users can create a file, then edit it do a run on a target platform
      // and go back and add or subtract widgets from the same file and not lose edits.
      FileReader fr = null;
      if(skeletonFile.exists()) {
        templateFile = new File(projectTemplate);
        fr = new FileReader(templateFile);
      } else {
        String fullPath = CommonUtils.getInstance().getWorkingDir();
        // here our template file is either ino.t, min.t or c.t inside templates folder.
        projectTemplate = fullPath + "templates" + System.getProperty("file.separator") 
            + defFName;
        templateFile = new File(projectTemplate);
        fr = new FileReader(templateFile);
      }
      // open our source code template and copy its contents into a StringBuider for our workflow.
      StringBuilder sBd = new StringBuilder();
      BufferedReader br = new BufferedReader(fr);
      String line  = "";
      while ((line = br.readLine()) != null) {
        sBd.append(line);
        sBd.append(System.lineSeparator());
      } 
      br.close();
      fr.close();
      return sBd;
    } catch (IOException e) {
      throw new CodeGenException(e.toString());
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
   * Gets the project's template.
   *
   * @return the project template
   */
  public String getProjectTemplate() {
    return projectTemplate;
  }
  
  /**
   * Gets the project's C file.
   *
   * @return the project file
   */
  public File getProjectFile() {
    return skeletonFile;
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
 * Gets the target platform.
 *
 * @return the target platform
 */
public String getTargetPlatform() {
    return target;
  }
}
