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
package builder.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import builder.Builder;
import builder.common.Utils;
import builder.controller.Controller;
import builder.models.ProjectModel;

/**
 * The Class PlatformIO is used to manage creation and 
 * copying of platformio.ini file into a project folder
 * during code generation.
 * 
 * @author Paul Conti
 * 
 */
public class PlatformIO {

  public  final static String PLATFORMIO_INI       = "platformio.ini";
  private final static String PLATFORMIO_TEMPLATE  = "platformio.t";
  private final static String PLATFORMIO_CUSTOM    = "PlatformIO_Custom";
  private final static String PLATFORMIO_DEFAULT   = "PlatformIO_Config";
  private final static String PLATFORMIO_INI_MACRO = "$<PLATFORMIO_INI>";
  private final static String DEFAULT_ENV_MACRO    = "$<DEFAULT_ENV>";
  private final static String GUIslice_ENV         = "$<GUIslice_ENV>";
  
  public static void createIniFile(String folder) {
    String home = Utils.getWorkingDir();
    String m_sFileSep = System.getProperty("file.separator");
    
    File destFile = new File(folder +  m_sFileSep + PLATFORMIO_INI);
    // first test for platform.ini and if found just copy it
    File srcFile = new File(home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_INI);
    if (srcFile.exists()) {
      Utils.copyFile(srcFile, destFile);
      return;
    }
    // has user set a platformio environment?
    ProjectModel pm = Controller.getProjectModel();
    String myEnv = pm.getPioEnv();
    if (myEnv == null || myEnv.isEmpty())
      return; // nothing to see here
    // user selected an env so we need to create a platformio.ini
    TemplateManager tm = new TemplateManager();
    // check for a custom env file
    String tmPathName = home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_CUSTOM;
    List<String> filter = new ArrayList<String>();
    filter.add(".txt");
    List<String> list = Utils.listDirectory(tmPathName,filter);
    boolean bFound = false;
    for(String str: list) {
      if (str.equals(myEnv+".txt")) {
        tmPathName = tmPathName + m_sFileSep + myEnv+".txt";
        bFound = true;
        break;
      }
    }
    if (!bFound) {
      tmPathName = home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_DEFAULT;
      list = Utils.listDirectory(tmPathName,filter);
      for(String str: list) {
        if (str.equals(myEnv+".txt")) {
          tmPathName = tmPathName + m_sFileSep + myEnv+".txt";
          bFound = true;
          break;
        }
      }
    }
    if (!bFound) {
      JOptionPane.showMessageDialog(null, 
          "missing "+myEnv+".txt", 
        "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    tm.storeTemplateFromPathName(myEnv, tmPathName);
      
    // load our environments from the template file
    List<String>templateLines = tm.loadTemplate(myEnv);
    // open source (platflorm.t) and destination (platform.ini) file
    String srcName = home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_TEMPLATE;
    srcFile = new File(srcName);
    destFile = new File(folder +  m_sFileSep + PLATFORMIO_INI);
    BufferedReader br;
    BufferedWriter bw;
    try {
      br = new BufferedReader(new InputStreamReader(
              new FileInputStream(srcFile), "UTF8"));
      bw = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(destFile), "UTF-8"));
      /* scan source code template and copy to our destination 
       * while searching for matching macro expansions needed.
       */
      String line  = "";
      line = br.readLine();
      String logMsg = null;
      String errMsg = null;
      // this test just makes sure no one has mucked with the template
      if (!line.equals(PLATFORMIO_INI_MACRO)) {
        logMsg = PLATFORMIO_INI+" Generation Failed: "+
            srcName+" missing "+PLATFORMIO_INI_MACRO;
        Builder.logger.debug(logMsg);
        errMsg = PLATFORMIO_INI+" Generation Failed: \n"+
          srcName+"\nmissing "+PLATFORMIO_INI_MACRO;
        JOptionPane.showMessageDialog(null, errMsg, 
            "Error", JOptionPane.ERROR_MESSAGE);
        br.close();
        bw.close();
        return;
      }
      /* here we simple need to output
       * default_envs = XXXXX
       * where XXXXX is myEnv string from Project Options model.
       */
      bFound = false;
      while ((line = br.readLine()) != null) {
        if (line.equals(DEFAULT_ENV_MACRO)) {
          bw.write(myEnv);
          bw.newLine();
          bFound = true;
          break;
        } else {
          bw.write(line);
          bw.newLine();
        }
       }
      if (!bFound) {
        Builder.logger.debug(srcName+" missing $<DEFAULT_ENV> macro");
        JOptionPane.showMessageDialog(null, 
            srcName+" missing $<DEFAULT_ENV> macro", 
          "Error", JOptionPane.ERROR_MESSAGE);
        br.close();
        bw.close();
      }
      /*
       * here we generate an error if the guislice_env isn't found
       * since the user could have simply created their own ini file
       * and avoided this complication.
       */
      bFound = false;
      while ((line = br.readLine()) != null) {
        if (line.startsWith(GUIslice_ENV)) {
          bFound = true;
          copyMacroToOutput(bw, templateLines);
        } else if (line.equals("<STOP>")) {
          continue;
        } else {
          bw.write(line);
          bw.newLine();
        }
      }
      if (!bFound) {
        Builder.logger.debug(srcName+" missing $<GUIslice_ENV> macro");
        JOptionPane.showMessageDialog(null, 
            srcName+" missing $<GUIslice_ENV> macro", 
          "Error", JOptionPane.ERROR_MESSAGE);
      }
      br.close();
      bw.close();
    } catch (UnsupportedEncodingException e) {
      JOptionPane.showMessageDialog(null, 
          srcName+" "+e.toString(), 
        "Error", JOptionPane.ERROR_MESSAGE);
    } catch (FileNotFoundException e) {
      JOptionPane.showMessageDialog(null, 
          srcName+" is missing", 
        "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      Builder.logger.debug(srcName+" "+e.toString());
      JOptionPane.showMessageDialog(null, 
          srcName+" "+e.toString(), 
        "Error", JOptionPane.ERROR_MESSAGE);
    } catch (CodeGenException e) {
      JOptionPane.showMessageDialog(null, 
          srcName+" "+e.toString(), 
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public static List<String> getListEnv() {
    List<String> result = new ArrayList<String>();
    String home = Utils.getWorkingDir();
    String m_sFileSep = System.getProperty("file.separator");
    int n = 0;
    HashMap<String, Integer> map = new HashMap<String, Integer>();;
    // check for a custom env file
    String tmPathName = home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_CUSTOM;
    List<String> filter = new ArrayList<String>();
    filter.add(".txt");
    List<String> list = Utils.listDirectory(tmPathName,filter);
    for (String str : list) {
      n = str.indexOf(".txt");
      String name = str.substring(0,n);
      map.put(name, 1);
      result.add(name);
    }
    tmPathName = home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_DEFAULT;
    list = Utils.listDirectory(tmPathName,filter);
    for (String str : list) {
      n = str.indexOf(".txt");
      String name = str.substring(0,n);
      if (map.containsKey(name)) {
        continue;
      }
      result.add(name);
    }
    return result;
  }
  
  public static boolean isPlatformIO_INI_Present() {
    boolean bResult = false;
    String home = Utils.getWorkingDir();
    String m_sFileSep = System.getProperty("file.separator");
    File iniFile = new File(home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_INI);
    if (iniFile.exists()) {
      bResult = true;
    }
    return bResult;
  }
  
  public static void makePIOFileStruct(String folder) {
    String m_sFileSep = System.getProperty("file.separator");
    // check for src folder
    File src = new File(folder+m_sFileSep+"src");
    if (!src.exists())
      src.mkdir();
    File include = new File(folder+m_sFileSep+"include");
    if (!include.exists())
      include.mkdir();
    File lib = new File(folder+m_sFileSep+"lib");
    if (!lib.exists())
      lib.mkdir();
    File test = new File(folder+m_sFileSep+"test");
    if (!test.exists())
      test.mkdir();
  }
  
  private static void copyMacroToOutput(BufferedWriter bw, List<String> outputLines) 
      throws IOException {
    for (String line : outputLines) {
      bw.write(line);
      bw.newLine();
    }
  }
  
}
