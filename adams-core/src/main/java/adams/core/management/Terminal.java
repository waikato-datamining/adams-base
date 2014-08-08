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
 * Terminal.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.io.File;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.env.Environment;

/**
 * 
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Terminal {

  /** the properties file. */
  public final static String FILENAME = "adams/core/management/Terminal.props";

  /** the key for the Linux executable. */
  public final static String LINUX_EXECUTABLE = "LinuxExecutable";

  /** the key for the Linux options. */
  public final static String LINUX_OPTIONS = "LinuxOptions";

  /** the key for the Mac executable. */
  public final static String MAC_EXECUTABLE = "MacExecutable";

  /** the key for the Mac options. */
  public final static String MAC_OPTIONS = "MacOptions";

  /** the key for the Windows executable. */
  public final static String WINDOWS_EXECUTABLE = "WindowsExecutable";

  /** the key for the Windows options. */
  public final static String WINDOWS_OPTIONS = "WindowsOptions";
  
  /** the placeholder for the directory. */
  public final static String PLACEHOLDR_DIR = "%d";
  
  /** the properties. */
  protected static Properties m_Properties;
  
  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    Properties	result;

    if (m_Properties == null) {
      try {
	result = Properties.read(FILENAME);
      }
      catch (Exception e) {
	result = new Properties();
      }
      m_Properties = result;
    }

    return m_Properties;
  }

  /**
   * Writes the properties to disk.
   *
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties() {
    return writeProperties(getProperties());
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties(Properties props) {
    boolean	result;
    String	filename;

    filename = Environment.getInstance().createPropertiesFilename(new File(FILENAME).getName());
    result   = props.save(filename);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the windows executable.
   * 
   * @return		the executable
   */
  public static String getWindowsExecutable() {
    return getProperties().getProperty(WINDOWS_EXECUTABLE);
  }

  /**
   * Returns the Linux executable.
   * 
   * @return		the executable
   */
  public static String getLinuxExecutable() {
    return getProperties().getProperty(LINUX_EXECUTABLE);
  }

  /**
   * Returns the Mac executable.
   * 
   * @return		the executable
   */
  public static String getMacExecutable() {
    return getProperties().getProperty(MAC_EXECUTABLE);
  }

  /**
   * Returns the platform-specific executable.
   * 
   * @return		the executable
   */
  public static String getExecutable() {
    if (OS.isWindows())
      return getWindowsExecutable();
    else if (OS.isMac())
      return getMacExecutable();
    else
      return getLinuxExecutable();
  }

  /**
   * Returns the windows options.
   * 
   * @return		the options
   */
  public static String getWindowsOptions() {
    return getProperties().getProperty(WINDOWS_OPTIONS);
  }

  /**
   * Returns the Linux options.
   * 
   * @return		the options
   */
  public static String getLinuxOptions() {
    return getProperties().getProperty(LINUX_OPTIONS);
  }

  /**
   * Returns the Mac options.
   * 
   * @return		the options
   */
  public static String getMacOptions() {
    return getProperties().getProperty(MAC_OPTIONS);
  }

  /**
   * Returns the platform-specific options.
   * 
   * @return		the options
   */
  public static String getOptions() {
    if (OS.isWindows())
      return getWindowsOptions();
    else if (OS.isMac())
      return getMacOptions();
    else
      return getLinuxOptions();
  }

  /**
   * Returns the platform-specific executable with its expanded options.
   * 
   * @param dir		the directory to use in the command; if pointing to a
   * 			file the parent directory is used
   * @return		the executable
   * @see		#PLACEHOLDR_DIR
   */
  public static String getCommand(File dir) {
    if (!dir.isDirectory())
      dir = dir.getParentFile();
    return getExecutable() + " " + getOptions().replace(PLACEHOLDR_DIR, dir.getAbsolutePath());
  }
  
  /**
   * Launches the platform-specific executable with its expanded options.
   * 
   * @param dir		the directory to use in the command; if pointing to a
   * 			file the parent directory is used
   * @see		#PLACEHOLDR_DIR
   */
  public static boolean launch(File dir) {
    String		cmd;
    String[]		parts;
    ProcessBuilder	pb;
    
    cmd = getCommand(dir);
    try {
      parts = OptionUtils.splitOptions(cmd);
      pb    = new ProcessBuilder(parts);
      pb.start();
      return true;
    }
    catch (Exception e) {
      System.err.println("Failed to launch terminal: " + cmd);
      e.printStackTrace();
      return false;
    }
  }
}
