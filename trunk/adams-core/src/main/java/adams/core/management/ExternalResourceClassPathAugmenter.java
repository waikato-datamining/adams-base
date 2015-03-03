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
 * ExternalResourceClassPathAugmenter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.util.ArrayList;
import java.util.List;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;

/**
 * Allows the user to add external jars and directories.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExternalResourceClassPathAugmenter
  extends AbstractClassPathAugmenter {

  /** for serialization. */
  private static final long serialVersionUID = 3380177345627628995L;

  /** the jars/zips to add to the classpath. */
  protected PlaceholderFile[] m_Files;

  /** the directories with classes to add to the classpath. */
  protected PlaceholderDirectory[] m_Directories;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to add external jars and directories.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "files",
	    new PlaceholderFile[0]);

    m_OptionManager.add(
	    "dir", "directories",
	    new PlaceholderDirectory[0]);
  }

  /**
   * Sets the jar/zip files to add to the classpath.
   *
   * @param value	the jar/zip files
   */
  public void setFiles(PlaceholderFile[] value) {
    m_Files = value;
    reset();
  }

  /**
   * Returns the jar/zip files to add to the classpath.
   *
   * @return		the jar/zip files
   */
  public PlaceholderFile[] getFiles() {
    return m_Files;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filesTipText() {
    return "The jar/zip files to add to the classpath.";
  }

  /**
   * Sets the directories to add to the classpath.
   *
   * @param value	the directories
   */
  public void setDirectories(PlaceholderDirectory[] value) {
    m_Directories = value;
    reset();
  }

  /**
   * Returns the directories to add to the classpath.
   *
   * @return		the directories
   */
  public PlaceholderDirectory[] getDirectories() {
    return m_Directories;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoriesTipText() {
    return "The directories with classes to add to the classpath.";
  }

  /**
   * Returns the classpath parts (jars, directories) to add to the classpath.
   * 
   * @return		the additional classpath parts
   */
  public String[] getClassPathAugmentation() {
    List<String>	result;

    result = new ArrayList<String>();

    for (PlaceholderFile file: m_Files)
      result.add(file.getAbsolutePath());
    for (PlaceholderDirectory dir: m_Directories)
      result.add(dir.getAbsolutePath());
    
    return result.toArray(new String[result.size()]);
  }
}
