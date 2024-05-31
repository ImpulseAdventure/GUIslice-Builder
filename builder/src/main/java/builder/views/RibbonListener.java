/**
 *
 * The MIT License
 *
 * Copyright 2018-2024 Paul Conti
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
import builder.common.Utils;
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

  private boolean bGave_DnD_Warning = false;
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
      String workingDir = Utils.getWorkingDir();
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
    Builder.logger.debug("createFolderDialog: "+currentDirectory);

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
    Builder.logger.debug("createFolderDialog: project file->"+project.toString());
    recentFileList.add(project);
    return project;
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
        String sWorkingDir = Utils.getWorkingDir();
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
      String htmlBody = String.format("<p><center>GUIslice Builder ver: %s<br>CopyRight (c) Paul Conti 2018-2024</center>"
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

    case "dnd_warning":
//      if (!bGave_DnD_Warning) {
        JOptionPane.showMessageDialog(null,
          "You must drag UI Element to your TFT Panel",
          "Drag & Drop",
          JOptionPane.WARNING_MESSAGE);
        bGave_DnD_Warning = true;
//      }
      break;

    case "delete":
      Builder.logger.debug("Menu: delete");
      controller.removeComponent();
      break;
      
    case "exit":
      Builder.logger.debug("Menu: exit");
      onExit();
      break;
      
    case "showgrid":
      Builder.logger.debug("Menu: show grid");
      controller.toggleShowGrid();
      break;

    case "snaptogrid":
      Builder.logger.debug("Menu: snap to grid");
      controller.toggleSnapToGrid();
      break;
      
    case "group":
      Builder.logger.debug("Toolbar: group");
      controller.groupButtons();
      break;
      
    case "new":
      Builder.logger.debug("Menu: new");
      controller.newProject();
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
    
    case "redo":
      Builder.logger.debug("Toolbar: redo");
      History.getInstance().redo();
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
          controller.saveAsProject(file);
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
  
    case "selection":
      Builder.logger.debug("Toolbar: rectangle selection");
      controller.rectangularSelection();
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

    case "zoomreset":
      Builder.logger.debug("Menu: zoomreset");
      controller.zoomReset();
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
