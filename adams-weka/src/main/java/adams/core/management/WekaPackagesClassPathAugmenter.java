/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * WekaPackagesClassPathAugmenter.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import weka.core.packageManagement.Package;
import weka.core.Environment;
import weka.core.WekaPackageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns the classpath augmentations for all the installed WEKA packages.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.core.WekaPackageManager
 */
public class WekaPackagesClassPathAugmenter
  extends AbstractClassPathAugmenter {

  /** for serialization. */
  private static final long serialVersionUID = 5783582495315373807L;
  
  /** for storing the augmentations. */
  protected List<String> m_Augmentations;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the classpath augmentations for all the installed WEKA packages.";
  }

  /**
   * Processes a package directory.
   * 
   * @param directory	the directory to process
   */
  protected void loadPackageDirectory(File directory) {
    File[] contents = directory.listFiles();

    for (int i = 0; i < contents.length; i++) {
      // regular jar
      if (contents[i].isFile() && contents[i].getPath().endsWith(".jar"))
	m_Augmentations.add(contents[i].getAbsolutePath());
      // add any jar files in the lib directory to the classpath
      else if (contents[i].isDirectory() && contents[i].getName().equalsIgnoreCase("lib"))
        loadPackageDirectory(contents[i]);
    }
  }
  
  /**
   * Returns the classpath parts (jars, directories) to add to the classpath.
   * 
   * @return		the additional classpath parts
   */
  public synchronized String[] getClassPathAugmentation() {
    Environment 	env;
    String 		loadPackages;
    File[] 		contents;
    int			i;
    Package 		toLoad;
    boolean 		load;

    m_Augmentations = new ArrayList<String>();
    
    // load packages?
    env = Environment.getSystemWide();
    loadPackages = env.getVariableValue("weka.core.loadPackages");
    if (loadPackages != null && loadPackages.equalsIgnoreCase("false"))
      return new String[0];

    // init packages
    WekaPackageManager.loadPackages(false);

    // try and load any jar files and add to the classpath
    contents = WekaPackageManager.PACKAGES_DIR.listFiles();
    for (i = 0 ; i < contents.length; i++) {
      if (contents[i].isDirectory()) {
        try {
          toLoad = WekaPackageManager.getInstalledPackageInfo(contents[i].getName());
          // Only perform the check against the current version of Weka if there exists 
          // a Description.props file
          if (toLoad != null) {
            load = WekaPackageManager.loadCheck(toLoad, contents[i], System.err);
            if (load)
              loadPackageDirectory(contents[i]);
          }
        } 
        catch (Exception ex) {
          ex.printStackTrace();
          System.err.println("[WEKA] Problem loading package " + contents[i].getName() + " skipping...");
        }          
      }
    }
    
    return m_Augmentations.toArray(new String[m_Augmentations.size()]);
  }
}
