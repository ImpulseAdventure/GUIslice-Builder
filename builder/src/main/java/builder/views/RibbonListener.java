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
package builder.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.swing.AbstractButton;
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
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.events.iSubscriber;
import builder.models.GeneralModel;
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
public class RibbonListener implements ActionListener, iSubscriber {
  
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
    MsgBoard.getInstance().subscribe(this, "RibbonListener");
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
    int  x = rand.nextInt(generalModel.getWidth()-50);
    int  y = rand.nextInt(generalModel.getHeight()-50);
    ImageWidget w = (ImageWidget) WidgetFactory.getInstance().createWidget(EnumFactory.IMAGE, x, y);
    File file = showImageDialog("Choose your Image file");
    if (file != null) {
      w.setImage(file, x, y);
      controller.addWidget(w);
    }
  }

  /**
   * Creates the img button widget.
   */
  public void createImgButtonWidget() {
    int  x = rand.nextInt(generalModel.getWidth()-50);
    int  y = rand.nextInt(generalModel.getHeight()-50);
    ImgButtonWidget w = (ImgButtonWidget) WidgetFactory.getInstance().createWidget(EnumFactory.IMAGEBUTTON, x, y);
    File file = showImageDialog("Choose your Button's Image");
    if (file != null) {
      w.setImage(file, x, y);
      file = showImageDialog("Choose your Disabled Button's Image");
      if (file != null) {
        w.setImageSelected(file);
        controller.addWidget(w);
      }
    }
  }

  /**
   * Creates the widget.
   *
   * @param name
   *          the name
   */
  public void createWidget(String name) {
    int  x = rand.nextInt(generalModel.getWidth()-25);
    int  y = rand.nextInt(generalModel.getHeight()-25);
    Widget w = WidgetFactory.getInstance().createWidget(name, x, y);
    if (w != null) {
      controller.addWidget(w);
    }
  }
  
  public int getRandomX() {
    return rand.nextInt(generalModel.getWidth()-25);
  }

  public int getRandomY() {
    return rand.nextInt(generalModel.getHeight()-25);
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
    String target = generalModel.getTarget();
    File file = null;
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle(title);
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileFilter() {
      public String getDescription() {
        if (target.equals("linux"))
          return "16 or 24 Bit Depth Bitmap (*.bmp), C File with extern image (*.c)";
        else if (target.equals("arduino TFT_eSPI"))
          return "16 or 24 Bit Depth Bitmap (*.bmp), Jpeg (*jpg), C File with extern image (*.c)";
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
                     target.equals("arduino TFT_eSPI")) {
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
      if (generalModel.getTarget().equals("linux")) {
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
   * updateEvent provides the implementation of Observer Pattern. 
   * It monitors selection of widgets in the ribbon.
   *
   * @param e
   *          the e
   * @see builder.events.iSubscriber#updateEvent(builder.events.MsgEvent)
   */
  @Override
  public void updateEvent(MsgEvent e) {
//    System.out.println("PagePane: " + e.toString());
    File file = null;
    String title = null;
    int answer = 0;
    if (e.code != MsgEvent.ACTION_COMMAND)
      return;
    switch(e.message) {
      case "aligntop":
        controller.alignTop();
        break;
        
      case "alignbottom":
        controller.alignBottom();
        break;
        
      case "aligncenter":
        controller.alignCenter();
        break;
        
      case "alignleft":
        controller.alignLeft();
        break;
        
      case "alignright":
        controller.alignRight();
        break;
        
      case "alignhspacing":
        controller.alignHSpacing();
        break;
        
      case "alignvspacing":
        controller.alignVSpacing();
        break;
        
      case "alignwidth":
        controller.alignWidth();
        break;
        
      case "alignheight":
        controller.alignHeight();
        break;

      case "basepage":
        controller.createPage(EnumFactory.BASEPAGE);
        break;
      
      case "box":
        createWidget(EnumFactory.BOX);
        break;
/*      
      case "circle":
        createWidget(EnumFactory.CIRCLE);
        break;
*/      
      case "checkbox":
        createWidget(EnumFactory.CHECKBOX);
        break;
      
      case "close":
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
              JOptionPane.showMessageDialog(null, "Project Save Failed", e3.toString(), JOptionPane.ERROR_MESSAGE);
              e3.printStackTrace();
              return;
            }
          }
        }
        controller.newProject();
        break;
        
      case "code":
        controller.generateCode();
        break;
      
      case "copy":
        controller.copyWidgets();
        break;
      
      case "copyprops":
        controller.copyProps();
        break;
      
      case "cut":
        controller.cutWidgets();
        break;
      
      case "Delete":
        controller.removeComponent();
        break;
        
      case "exit":
        onExit();
        break;
        
      case "grid":
        controller.toggleGrid();
        break;
        
      case "graph":
        createWidget(EnumFactory.GRAPH);
        break;
      
      case "group":
        controller.groupButtons();
        break;
        
      case "image":
        createImageWidget();
        break;
      
      case "imagebutton":
        createImgButtonWidget();
        break;
      
      case "line":
        createWidget(EnumFactory.LINE);
        break;
      
      case "listbox":
        createWidget(EnumFactory.LISTBOX);
        break;
      
      case "numinput":
        createWidget(EnumFactory.NUMINPUT);
        break;
      
      case "open":
        String [] fileExtPrj = new String[1];
        fileExtPrj[0] = ".prj";
        file = showFileDialog("Open Project", fileExtPrj, null, false, "Open");
        if (file == null) break;
        try {
          controller.openProject(file);
        } catch (IOException e5) {
          JOptionPane.showMessageDialog(null, "Project Open Failed", e5.toString(), JOptionPane.ERROR_MESSAGE);
          e5.printStackTrace();
        }      
        break;
        
      case "page":
        controller.createPage(EnumFactory.PAGE);
        break;
      
      case "paste":
        controller.pasteWidgets();
        break;
      
      case "popup":
        controller.createPage(EnumFactory.POPUP);
        break;
      
      case "progressbar":
        createWidget(EnumFactory.PROGRESSBAR);
        break;
        
      case "radiobutton":
        createWidget(EnumFactory.RADIOBUTTON);
        break;
      
      case "redo":
        History.getInstance().redo();
        break;
        
      case "ramp":
        createWidget(EnumFactory.RAMPGAUGE);
        break;
        
      case "radial":
        createWidget(EnumFactory.RADIALGAUGE);
        break;
        
      case "ringgauge":
        createWidget(EnumFactory.RINGGAUGE);
        break;
        
      case "save":
        file = null;
        if (!controller.isNamedProject()) {
          file = createFolderDialog();
          if (file == null) { 
            JOptionPane.showMessageDialog(null, "Project Folder Creation Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        try {
          controller.saveProject(file);
        } catch (IOException e1) {
          JOptionPane.showMessageDialog(null, "Project Save Failed", e1.toString(), JOptionPane.ERROR_MESSAGE);
          e1.printStackTrace();
        }
        break;
        
      case "saveas":
        file = createFolderDialog();
        if (file != null) {
          try {
            controller.saveProject(file);
          } catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "Project SaveAs Failed", e2.toString(), JOptionPane.ERROR_MESSAGE);
            e2.printStackTrace();
          }
        } else {
          JOptionPane.showMessageDialog(null, "Project SaveAs Cancelled", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        break;
        
      case "selection":
        controller.rectangularSelection();
        break;
        
      case "slider":
        createWidget(EnumFactory.SLIDER);
        break;
        
      case "spinner":
        createWidget(EnumFactory.SPINNER);
        break;
        
      case "text":
        createWidget(EnumFactory.TEXT);
        break;
      
      case "textbox":
        createWidget(EnumFactory.TEXTBOX);
        break;
      
      case "textbutton":
        createWidget(EnumFactory.TEXTBUTTON);
        break;
      
      case "textinput":
        createWidget(EnumFactory.TEXTINPUT);
        break;
      
      case "undo":
        History.getInstance().undo();
        break;
        
      case "zoomin":
        controller.zoomIn();
        break;
        
      case "zoomout":
        controller.zoomOut();
        break;
        
        default:
          throw new IllegalArgumentException("Unknown Ribbon Action: " + e.toString());
      }
    
  }

  /**
  * actionPerformed
  *
  * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
  */
  @Override
  public void actionPerformed(ActionEvent e) {
    String command = ((AbstractButton)e.getSource()).getActionCommand();
    File file = null;
    String title = null;
    int answer = 0;
//    System.out.println("command: " + command);
    switch(command) {
    case "about":
      String htmlBody = String.format("<p><center>GUIslice Builder ver: %s<br>CopyRight (c) Paul Conti 2018-2020</center>"
          + "<br><center>GUIslice CopyRight (c) Calvin Hass 2016-2020</center></p>", Builder.VERSION);
      htmlBody = htmlBody + "<br>For the latest guides, updates and support view:<br>";
      MessageWithLink msgDialog = new MessageWithLink(htmlBody, "https://github.com/ImpulseAdventure/GUIslice/wiki");
      msgDialog.showMessage();
      break;
    
    case "code":
      controller.generateCode();
      break;
    
    case "copy":
      controller.copyWidgets();
      break;
    
    case "cut":
      controller.cutWidgets();
      break;
    
    case "Delete":
      controller.removeComponent();
      break;
      
    case "grid":
      controller.toggleGrid();
      break;
      
    case "new":
      controller.newProject();
      break;
      
    case "open":
      String [] fileExtPrj = new String[1];
      fileExtPrj[0] = ".prj";
      file = showFileDialog("Open Project", fileExtPrj, null, false, "Open");
      if (file == null) break;
      try {
        controller.openProject(file);
      } catch (IOException e5) {
        JOptionPane.showMessageDialog(null, "Project Open Failed", e5.toString(), JOptionPane.ERROR_MESSAGE);
        e5.printStackTrace();
      }      
      break;
      
    case "options":
      controller.showPreferences();
      break;

    case "paste":
      controller.pasteWidgets();
      break;
    
    case "save":
      file = null;
      if (!controller.isNamedProject()) {
        file = createFolderDialog();
        if (file == null) { 
          JOptionPane.showMessageDialog(null, "Project Folder Creation Failed", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      try {
        controller.saveProject(file);
      } catch (IOException e1) {
        JOptionPane.showMessageDialog(null, "Project Save Failed", e1.toString(), JOptionPane.ERROR_MESSAGE);
        e1.printStackTrace();
      }
      break;
      
    case "saveas":
      file = createFolderDialog();
      if (file != null) {
        try {
          controller.saveProject(file);
        } catch (IOException e2) {
          JOptionPane.showMessageDialog(null, "Project SaveAs Failed", e2.toString(), JOptionPane.ERROR_MESSAGE);
          e2.printStackTrace();
        }
      } else {
        JOptionPane.showMessageDialog(null, "Project SaveAs Cancelled", "Warning", JOptionPane.WARNING_MESSAGE);
      }
      break;
      
    case "close":
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
            JOptionPane.showMessageDialog(null, "Project Save Failed", e3.toString(), JOptionPane.ERROR_MESSAGE);
            e3.printStackTrace();
            return;
          }
        }
      }
      controller.newProject();
      break;
      
    case "exit":
      onExit();
      break;
      
    case "zoomin":
      controller.zoomIn();
      break;
      
    case "zoomout":
      controller.zoomOut();
      break;
      
/*
      case "help":
        JOptionPane.showMessageDialog(null, "Run in Circles, Scream & Shout!", "Help", JOptionPane.INFORMATION_MESSAGE);
        break;
*/
      
    default:
      throw new IllegalArgumentException("Unknown MenuBar action: " + command);
    }

  }

}
