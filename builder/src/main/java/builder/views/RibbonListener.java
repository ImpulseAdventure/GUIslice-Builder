/**
 *
 * The MIT License
 *
 * Copyright 2018-2021 Paul Conti
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
package builder.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.swing.filechooser.FileFilter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import builder.Builder;
import builder.codegen.CodeGenerator;
import builder.commands.History;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.controller.Controller;
import builder.models.GeneralModel;
import builder.models.ProjectModel;
import builder.prefs.GeneralEditor;
import builder.widgets.ImageWidget;
import builder.widgets.ImgButtonWidget;
import builder.widgets.Widget;
import builder.widgets.WidgetFactory;

/**
 * The Class RibbonListener.
 *   Handles Message events for widget creation from the Ribbon
 * 
 * @author Paul Conti
 * 
 */
public class RibbonListener implements ActionListener {
  
  /** The controller. */
  Controller controller;
  
  /** The general model. */
  private GeneralModel generalModel;
  
  /** The rand. */
  private Random rand = new Random();
  
  /** The Recent File List */
  private RecentFilePanel recentFileList = null;

  /**
   * Instantiates a new toolbox.
   */
  public RibbonListener() {
    this.controller = Controller.getInstance();
    generalModel = (GeneralModel) GeneralEditor.getInstance().getModel();
  }

  /**
   * Create folder dialog.
   * 
   * @return the <code>file</code> object
   */
  public File createFolderDialog() {
    File currentDirectory;
    String folderPath = generalModel.getProjectDir();
    // absolute path or relative?
    Path path = Paths.get(folderPath);
    if (path.isAbsolute()) {
      currentDirectory = new File(folderPath);
    } else {
      String workingDir = CommonUtils.getInstance().getWorkingDir();
      folderPath = workingDir + folderPath;
      currentDirectory = new File(folderPath);
    }
    JFileChooser chooser = new JFileChooser(folderPath);
    recentFileList = new RecentFilePanel();
    chooser.addChoosableFileFilter(new FileFilter() {
      public String getDescription() {
        String descr = new String("GUIslice Builder Project Folder");
        return descr;
      }
      public boolean accept(File f) {
          return f.isDirectory();
      }
    });
    chooser.setDialogTitle("Choose your Project Folder");
    chooser.setApproveButtonText("Select Folder");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setAcceptAllFileFilterUsed(false);
    // Open Dialog  
    if (chooser.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) { 
      currentDirectory = chooser.getSelectedFile();
    } else {
      return null;
    }
    String strProjectFolder = currentDirectory.getName();
    if (strProjectFolder.equals("New Folder")) {
      JOptionPane.showMessageDialog(null, "You can't use the name 'New Folder' for projects", 
          "Error", JOptionPane.ERROR_MESSAGE);
      currentDirectory.delete();
      return null;
    } 
    File project = new File(new String(currentDirectory.toString()
        + System.getProperty("file.separator")
        + strProjectFolder + ".prj"));
    recentFileList.add(project);
    return project;
  }
  
  /**
   * Creates the image widget.
   */
  public void createImageWidget() {
    int  x = getRandomX(50);
    int  y = getRandomY(50);
    ImageWidget w = (ImageWidget) WidgetFactory.getInstance().createWidget(EnumFactory.IMAGE, x, y);
    File file = showImageDialog("Choose your Image file");
    if (file != null) {
      if (w.setImage(file, x, y)) {
        controller.addWidget(w);
      } else {
        JOptionPane.showMessageDialog(null, "Adding Image Failed-Check builder.log", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Creates the img button widget.
   */
  public void createImgButtonWidget() {
    int  x = getRandomX(50);
    int  y = getRandomY(50);
    ImgButtonWidget w = (ImgButtonWidget) WidgetFactory.getInstance().createWidget(EnumFactory.IMAGEBUTTON, x, y);
    File file = showImageDialog("Choose your Button's Image");
    if (file != null) {
      if (w.setImage(file, x, y)) {
        file = showImageDialog("Choose your Disabled Button's Image");
        if (file != null) {
          if (w.setImageSelected(file)) {
            controller.addWidget(w);
          } else {
            JOptionPane.showMessageDialog(null, "Adding Disabled Image Failed-Check builder.log", "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    } else {
      JOptionPane.showMessageDialog(null, "Adding Image Failed-Check builder.log", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Creates the widget.
   *
   * @param name
   *          the name
   */
  public void createWidget(String name) {
    int  x = getRandomX(25);
    int  y = getRandomY(25);
    Widget w = WidgetFactory.getInstance().createWidget(name, x, y);
    if (w != null) {
      controller.addWidget(w);
    }
  }
  
  public int getRandomX(int n) {
    return rand.nextInt(Controller.getProjectModel().getWidth()-n);
  }

  public int getRandomY(int n) {
    return rand.nextInt(Controller.getProjectModel().getHeight()-n);
  }

  /**
   * Show file dialog.
   * 
   * @param title
   *          the title
   * @param fileExtension
   *          the file extension
   * @param suggestedFile
   *          the suggested file
   * @param bAcceptAll
   *          the boolean for accept all
   * @param btnText
   *          the text to show for the accept button
   * @return the <code>file</code> object
   */
  public File showFileDialog(String title, String[] fileExtension, String suggestedFile, 
      boolean bAcceptAll, String btnText) {
    File file = null;
    JFileChooser fileChooser = new JFileChooser();
    if (suggestedFile != null)
      fileChooser.setSelectedFile(new File(suggestedFile));
    fileChooser.setDialogTitle(title);
    if (!bAcceptAll)
      fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileFilter() {
      public String getDescription() {
        String descr = new String("GUIslice Builder file (");
        for (int i=0; i<fileExtension.length; i++) {
          if (i > 0) 
            descr = descr + ", ";
          descr = descr + "*" + fileExtension[i];
        }
        descr = descr  + ")"; 
        return descr;
      }
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        } else {
          String name = f.getName().toLowerCase();
          for (int i=0; i<fileExtension.length; i++) 
            if (name.endsWith(fileExtension[i]))
              return true;
          return false;
        }
      }
    });
    recentFileList = new RecentFilePanel(fileChooser);
    fileChooser.setAccessory(recentFileList);
    String sCurrentFolder = recentFileList.getCurrentFolder();
    fileChooser.setFileView(new FileViewWithIcons());
    File currentDirectory;
    if (sCurrentFolder == null) {
      String sProjectDir = generalModel.getProjectDir();
      // absolute path or relative?
      Path path = Paths.get(sProjectDir);
      if (path.isAbsolute()) {
        currentDirectory = new File(sProjectDir);
      } else {
        String sWorkingDir = CommonUtils.getInstance().getWorkingDir();
        currentDirectory = new File(sWorkingDir + sProjectDir);
      }
    } else {
      currentDirectory = new File(sCurrentFolder);
    }
    fileChooser.setCurrentDirectory(currentDirectory);
    int option = fileChooser.showDialog(new JFrame(), btnText);
    if (option == JFileChooser.APPROVE_OPTION) {
      file = fileChooser.getSelectedFile();
      recentFileList.add(file);
    }
    return file;
  }

  /**
   * Show image dialog.
   *
   * @param title
   *          the title
   * @return the <code>file</code> object
   */
  public File showImageDialog(String title) {
    String target = Controller.getTargetPlatform();
    File file = null;
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle(title);
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileFilter() {
      public String getDescription() {
        if (target.equals(ProjectModel.PLATFORM_LINUX))
          return "16 or 24 Bit Depth Bitmap (*.bmp), C File with extern image (*.c)";
        else if (target.equals("tft_espi"))
          return "24 Bit Depth Bitmap (*.bmp), Jpeg (*jpg), C File with extern image (*.c)";
        else
          return "24 Bit Depth BMP Images (*.bmp), C File with extern image (*.c)";
      }

      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        } else {
          if (f.getName().toLowerCase().endsWith(".c")) {
            return true;
          }
          if (f.getName().toLowerCase().endsWith(".bmp")) {
            return true;
          } 
          if (f.getName().toLowerCase().endsWith(".jpg") &&
                     target.equals("tft_espi")) {
            return true;
          }
          return false;
        }
      }
    });
    ImagePreviewPanel preview = new ImagePreviewPanel();
    fileChooser.setAccessory(preview);
    fileChooser.setFileView(new FileViewWithIcons());
    fileChooser.addPropertyChangeListener(preview);

    File currentDirectory = null;
    // look for images in the last folder accessed
    String resDir = generalModel.getImageDir();
    if (resDir.isEmpty()) {
      if (Controller.getTargetPlatform().equals(ProjectModel.PLATFORM_LINUX)) {
        resDir = CodeGenerator.LINUX_RES;
      } else {
        resDir = CodeGenerator.ARDUINO_RES;
      }
      String workingDir = CommonUtils.getInstance().getWorkingDir();
      currentDirectory = new File(workingDir + resDir);
    } else {
      currentDirectory = new File(resDir);
    }
    fileChooser.setCurrentDirectory(currentDirectory);
    int option = fileChooser.showDialog(new JFrame(), "Select");
    if (option == JFileChooser.APPROVE_OPTION) {
      file = fileChooser.getSelectedFile();
      generalModel.setImageDir(file.getParent());
      GeneralEditor.getInstance().savePreferences();
    } 
    return file;
  }
  
 
  /**
   * onExit
   */
  public void onExit() {
    if (History.getInstance().size() > 0) {
      String title = "Confirm Dialog";
      String message = "Would you like to save project before exit?";
      int answer = JOptionPane.showConfirmDialog(null,message,title, JOptionPane.YES_NO_OPTION); 
      if(answer == JOptionPane.YES_OPTION)
      {
        File file = null;
        if (!controller.isNamedProject()) {
          file = createFolderDialog();
          if (file == null) {
            JOptionPane.showMessageDialog(null, "Project Save Cancelled", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
          } 
        }
        try {
          controller.saveProject(file);
        } catch (IOException e4) {
          JOptionPane.showMessageDialog(null, "Project Save Failed", e4.toString(), JOptionPane.ERROR_MESSAGE);
          e4.printStackTrace();
        }
      }
    }
    controller.onExit();
  }
  
  /**
  * actionPerformed
  *
  * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
  */
  @Override
  public void actionPerformed(ActionEvent e) {
//    String command = ((AbstractButton)e.getSource()).getActionCommand();
    String command = e.getActionCommand().toLowerCase();
    File file = null;
    String title = null;
    int answer = 0;
//    System.out.println("command: " + command);
    switch(command) {
    case "about":
      Builder.logger.debug("Menu: about");
      String htmlBody = String.format("<p><center>GUIslice Builder ver: %s<br>CopyRight (c) Paul Conti 2018-2022</center>"
          + "<br><center>GUIslice CopyRight (c) Calvin Hass 2016-2021</center></p>", Builder.VERSION);
      htmlBody = htmlBody + "<br>For the latest guides, updates and support view:<br>";
      MessageWithLink msgDialog = new MessageWithLink(htmlBody, "https://github.com/ImpulseAdventure/GUIslice/wiki");
      msgDialog.showMessage();
      break;
    
    case "aligntop":
      Builder.logger.debug("Toolbar: aligntop");
      controller.alignTop();
      break;
      
    case "alignbottom":
      Builder.logger.debug("Toolbar: alignbottom");
      controller.alignBottom();
      break;
      
    case "aligncenter":
      Builder.logger.debug("Toolbar: aligncenter");
      controller.alignCenter();
      break;
      
    case "alignleft":
      Builder.logger.debug("Toolbar: alignleft");
      controller.alignLeft();
      break;
      
    case "alignright":
      Builder.logger.debug("Toolbar: alignright");
      controller.alignRight();
      break;
      
    case "alignhspacing":
      Builder.logger.debug("Toolbar: alignhspacing");
      controller.alignHSpacing();
      break;
      
    case "alignvspacing":
      Builder.logger.debug("Toolbar: alignvspacing");
      controller.alignVSpacing();
      break;
      
    case "alignwidth":
      Builder.logger.debug("Toolbar: alignwidth");
      controller.alignWidth();
      break;
      
    case "alignheight":
      Builder.logger.debug("Toolbar: alignheight");
      controller.alignHeight();
      break;

    case "basepage":
      Builder.logger.debug("Toolbar: basepage");
      controller.createPage(EnumFactory.BASEPAGE);
      break;
    
    case "box":
      Builder.logger.debug("Toolbar: box");
      createWidget(EnumFactory.BOX);
      break;
    case "checkbox":
      Builder.logger.debug("Toolbar: checkbox");
      createWidget(EnumFactory.CHECKBOX);
      break;
    
    case "close":
      Builder.logger.debug("Toolbar: close");
      if (History.getInstance().size() > 0) {
        title = "Confirm Dialog";
        String message = "Would you like to save project before closing?";
        answer = JOptionPane.showConfirmDialog(null,message,title, JOptionPane.YES_NO_OPTION); 
        if(answer == JOptionPane.YES_OPTION)
        {
          file = null;
          if (!controller.isNamedProject()) {
            file = createFolderDialog();
            if (file == null) {
              JOptionPane.showMessageDialog(null, "Project Save Cancelled", "Warning", JOptionPane.WARNING_MESSAGE);
              return;
            }
          } 
          try {
            controller.saveProject(file);
            controller.newProject();
          } catch (IOException e3) {
            Builder.logger.debug("Project Save Failed " + e3.toString());
            JOptionPane.showMessageDialog(null, "Project Save Failed", e3.toString(), JOptionPane.ERROR_MESSAGE);
            return;
          }
        } else {
          Builder.logger.debug("Chose not to save project");
        }
      }
      controller.newProject();
      break;
      
    case "code":
      Builder.logger.debug("Toolbar: generateCode");
      controller.generateCode();
      break;
    
    case "copy":
      Builder.logger.debug("Toolbar: copy");
      controller.copyWidgets();
      break;
    
    case "copyprops":
      Builder.logger.debug("Toolbar: copyprops");
      controller.copyProps();
      break;
    
    case "cut":
      Builder.logger.debug("Toolbar: cut");
      controller.cutWidgets();
      break;
    
    case "delete":
      Builder.logger.debug("Menu: delete");
      controller.removeComponent();
      break;
      
    case "exit":
      Builder.logger.debug("Menu: exit");
      onExit();
      break;
      
    case "grid":
      Builder.logger.debug("Menu: grid");
      controller.toggleGrid();
      break;
      
    case "graph":
      Builder.logger.debug("Toolbar: graph");
      createWidget(EnumFactory.GRAPH);
      break;
    
    case "group":
      Builder.logger.debug("Toolbar: group");
      controller.groupButtons();
      break;
      
    case "image":
      Builder.logger.debug("Toolbar: image");
      createImageWidget();
      break;
    
    case "imagebutton":
      Builder.logger.debug("Toolbar: imagebutton");
      createImgButtonWidget();
      break;
    
    case "line":
      Builder.logger.debug("Toolbar: line");
      createWidget(EnumFactory.LINE);
      break;
    
    case "listbox":
      Builder.logger.debug("Toolbar: listbox");
      createWidget(EnumFactory.LISTBOX);
      break;
    
    case "new":
      Builder.logger.debug("Menu: new");
      controller.newProject();
      break;
      
    case "numinput":
      Builder.logger.debug("Toolbar: numinput");
      createWidget(EnumFactory.NUMINPUT);
      break;
    
    case "open":
      Builder.logger.debug("Toolbar: open");
      String [] fileExtPrj = new String[1];
      fileExtPrj[0] = ".prj";
      file = showFileDialog("Open Project", fileExtPrj, null, false, "Open");
      if (file == null) break;
      try {
        controller.openProject(file);
      } catch (IOException e5) {
        Builder.logger.debug("Project Open Failed " + e5.toString());
        JOptionPane.showMessageDialog(null, "Project Open Failed", e5.toString(), JOptionPane.ERROR_MESSAGE);
      }      
      break;
      
    case "options":
      Builder.logger.debug("Menu: options");
      controller.showPreferences();
      break;

    case "page":
      Builder.logger.debug("Toolbar: page");
      controller.createPage(EnumFactory.PAGE);
      break;
    
    case "paste":
      Builder.logger.debug("Toolbar: paste");
      controller.pasteWidgets();
      break;
    
    case "popup":
      Builder.logger.debug("Toolbar: popup");
      controller.createPage(EnumFactory.POPUP);
      break;
    
    case "progressbar":
      Builder.logger.debug("Toolbar: progressbar");
      createWidget(EnumFactory.PROGRESSBAR);
      break;
      
    case "radiobutton":
      Builder.logger.debug("Toolbar: radiobutton");
      createWidget(EnumFactory.RADIOBUTTON);
      break;
    
    case "redo":
      Builder.logger.debug("Toolbar: redo");
      History.getInstance().redo();
      break;
      
    case "ramp":
      Builder.logger.debug("Toolbar: ramp");
      createWidget(EnumFactory.RAMPGAUGE);
      break;
      
    case "radial":
      Builder.logger.debug("Toolbar: radial");
      createWidget(EnumFactory.RADIALGAUGE);
      break;
      
    case "ringgauge":
      Builder.logger.debug("Toolbar: ringgauge");
      createWidget(EnumFactory.RINGGAUGE);
      break;
      
    case "save":
      Builder.logger.debug("Toolbar: save");
      file = null;
      if (!controller.isNamedProject()) {
        file = createFolderDialog();
        if (file == null) { 
          Builder.logger.debug("Project Cancelled");
          JOptionPane.showMessageDialog(null, "Project Save Cancelled", "Warning", JOptionPane.WARNING_MESSAGE);
          return;
        }
      }
      try {
        controller.saveProject(file);
      } catch (IOException e1) {
        Builder.logger.debug("Project Save Failed " + e1.toString());
        JOptionPane.showMessageDialog(null, "Project Save Failed", e1.toString(), JOptionPane.ERROR_MESSAGE);
      }
      break;
      
    case "saveas":
      Builder.logger.debug("Toolbar: saveas");
      file = createFolderDialog();
      if (file != null) {
        try {
          controller.saveProject(file);
        } catch (IOException e2) {
          Builder.logger.debug("Project SaveAs Failed " + e2.toString());
          JOptionPane.showMessageDialog(null, "Project SaveAs Failed", e2.toString(), JOptionPane.ERROR_MESSAGE);
        }
      } else {
        JOptionPane.showMessageDialog(null, "Project SaveAs Cancelled", "Warning", JOptionPane.WARNING_MESSAGE);
      }
      break;
      
    case "scale":
      Builder.logger.debug("Toolbar: scale");
      controller.scale();
      break;
  
    case "seekbar":
      Builder.logger.debug("Toolbar: seekbar");
      createWidget(EnumFactory.SEEKBAR);
    break;
  
    case "selection":
      Builder.logger.debug("Toolbar: rectangle selection");
      controller.rectangularSelection();
      break;
      
    case "slider":
      Builder.logger.debug("Toolbar: slider");
      createWidget(EnumFactory.SLIDER);
      break;
      
    case "spinner":
      Builder.logger.debug("Toolbar: spinner");
      createWidget(EnumFactory.SPINNER);
      break;
      
    case "text":
      Builder.logger.debug("Toolbar: text");
      createWidget(EnumFactory.TEXT);
      break;
    
    case "textbox":
      Builder.logger.debug("Toolbar: textbox");
      createWidget(EnumFactory.TEXTBOX);
      break;
    
    case "textbutton":
      Builder.logger.debug("Toolbar: textbutton");
      createWidget(EnumFactory.TEXTBUTTON);
      break;
    
    case "textinput":
      Builder.logger.debug("Toolbar: textinput");
      createWidget(EnumFactory.TEXTINPUT);
      break;
    
    case "toggle":
      Builder.logger.debug("Toolbar: toggle");
      createWidget(EnumFactory.TOGGLEBUTTON);
      break;
    
    case "undo":
      Builder.logger.debug("Toolbar: undo");
      History.getInstance().undo();
      break;
      
    case "zoomin":
      Builder.logger.debug("Menu: zoomin");
      controller.zoomIn();
      break;
      
    case "zoomout":
      Builder.logger.debug("Menu: zoomout");
      controller.zoomOut();
      break;
      
/*
    case "help":
      Builder.logger.debug("Menu: help");
      JOptionPane.showMessageDialog(null, "Run in Circles, Scream & Shout!", "Help", JOptionPane.INFORMATION_MESSAGE);
      break;
*/
      
    default:
      Builder.logger.debug("Unknown MenuBar action: " + command);
      throw new IllegalArgumentException("Unknown MenuBar action: " + command);
    }

  }

}
