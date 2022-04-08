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
import java.util.List;

import javax.swing.JOptionPane;

import builder.Builder;
import builder.common.CommonUtils;
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
  private final static String PLATFORMIO_CUSTOM    = "platformio_custom_env.t";
  private final static String PLATFORMIO_DEFAULT   = "platformio_default_env.t";
  private final static String PLATFORMIO_INI_MACRO = "$<PLATFORMIO_INI>";
  private final static String DEFAULT_ENV_MACRO    = "$<DEFAULT_ENV>";
  private final static String GUIslice_ENV         = "$<GUIslice_ENV>";
  
  public static void createIniFile(String folder) {
    String home = CommonUtils.getWorkingDir();
    String m_sFileSep = System.getProperty("file.separator");
    
    File destFile = new File(folder +  m_sFileSep + PLATFORMIO_INI);
    // first test for platform.ini and if found just copy it
    File srcFile = new File(home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_INI);
    if (srcFile.exists()) {
      CommonUtils.copyFile(srcFile, destFile);
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
    File tmFile = new File(home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_CUSTOM);
    if (tmFile.exists()) {
      tm.storeTemplates(PLATFORMIO_CUSTOM);
    } else {
      tm.storeTemplates(PLATFORMIO_DEFAULT);
    }
    // load our environments from the template file
    List<String>templateLines = tm.loadTemplate("<" + myEnv + ">");
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
      }
      /* here we simple need to output
       * default_envs = XXXXX
       * where XXXXX is myEnv string from Project Options model.
       * NOTE: that a user may have hard-coded this environment
       * so no error is generated if we don't find it.
       */
      while ((line = br.readLine()) != null) {
        if (line.equals(DEFAULT_ENV_MACRO)) {
          bw.write(myEnv);
          bw.newLine();
          break;
        } else {
          bw.write(line);
          bw.newLine();
        }
       }
      /*
       * here we generate an error if the guislice_env isn't found
       * since the user could have simply created their own ini file
       * and avoided this complication.
       */
      boolean bFound = false;
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
        Builder.logger.debug(tmFile.getName()+" missing [env:"+myEnv+"]");
        JOptionPane.showMessageDialog(null, 
            tmFile.getName()+" missing [env:"+myEnv+"]", 
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
          tmFile.getName()+" "+e.toString(), 
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public static List<String> getListEnv() {
    String home = CommonUtils.getWorkingDir();
    String m_sFileSep = System.getProperty("file.separator");
    List<String> result = new ArrayList<String>();
    String tmName = home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_CUSTOM;
    File tmFile = new File(tmName);
    if (!tmFile.exists()) {
      tmName = home + CodeGenerator.TEMPLATE_FOLDER + m_sFileSep + PLATFORMIO_DEFAULT;
      tmFile = new File(tmName);
      if (!tmFile.exists()) {
        Builder.logger.debug(tmName+" is missing");
        JOptionPane.showMessageDialog(null, 
            tmName+" is missing", 
          "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    BufferedReader br;
    String line = null;
    String sEnv = null;
    int n;
    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(tmFile), "UTF8"));
      while ((line = br.readLine()) != null) {
        if (line.startsWith("<") && !line.equals("<STOP>")) {
          n = line.indexOf(">");
          if (n > 0) {
            sEnv = new String(line.substring(1, n));
            result.add(sEnv);
          }
        }
      }
    } catch (IOException e) {
      Builder.logger.debug(tmFile.getName() + " " + e.toString());
    }
    return result;
  }
  
  public static boolean isPlatformIO_INI_Present() {
    boolean bResult = false;
    String home = CommonUtils.getWorkingDir();
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
