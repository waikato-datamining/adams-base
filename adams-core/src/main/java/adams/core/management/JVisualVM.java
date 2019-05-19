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

/*
 * JVisualVM.java
 * Copyright (C) 2010-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.env.Environment;
import adams.env.JVisualVMDefinition;

import java.io.File;

/**
 * A helper class for the jvisualvm utility.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JVisualVM
  extends Java {

  /** the props file. */
  public final static String FILENAME = "JVisualVM.props";

  /** the jvisualvm executable when distributed with a JDK. */
  public final static String EXECUTABLE = "jvisualvm";

  /** the key in the properties file. */
  public static final String KEY_EXECUTABLE = "Executable";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Checks whether the user defined a custom binary.
   *
   * @return		true if custom binary
   */
  public static boolean hasCustomBinary() {
    return !getProperties().getProperty(KEY_EXECUTABLE, "").isEmpty();
  }

  /**
   * Checks whether jvisualvm is available at all.
   *
   * @return		true if available
   */
  public static boolean isAvailable() {
    boolean	result;

    if (hasCustomBinary()) {
      result = new File(getProperties().getProperty(KEY_EXECUTABLE, "")).exists();
    }
    else{
      result = isJDK();
      if (result)
	result = new File(getBinDir() + File.separator + FileUtils.fixExecutable(EXECUTABLE)).exists();
    }

    return result;
  }

  /**
   * Returns the full path of the JVisualVM executable, if possible.
   *
   * @return		the full path of the executable if possible, otherwise
   * 			just the executable
   */
  public static String getExecutablePath() {
    String	result;

    if (hasCustomBinary())
      result = FileUtils.fixExecutable(getProperties().getProperty(KEY_EXECUTABLE, ""));
    else
      result = getBinDir() + File.separator + FileUtils.fixExecutable(EXECUTABLE);
    result = FileUtils.quoteExecutable(result);

    return result;
  }

  /**
   * Returns the default options ("--nosplash -J-Xmx512m") for jvisualvm.
   *
   * @return		the default options
   */
  public static String getDefaultOptions() {
    return "--nosplash -J-Xmx512m";
  }

  /**
   * Executes jvisualvm with default options and returns the output.
   *
   * @param pid		the process ID of the JVM to connect to,
   * 			gets determined automatically if AUTO_PID
   * @return		the output
   * @see		#getDefaultOptions()
   */
  public static String execute(long pid) {
    return execute(getExecutablePath(), pid);
  }

  /**
   * Executes jvisualvm with default options and returns the output.
   *
   * @param options	additional options for jvisualvm
   * @param pid		the process ID of the JVM to connect to,
   * 			gets determined automatically if AUTO_PID
   * @return		the output
   * @see		#getDefaultOptions()
   */
  public static String execute(String options, long pid) {
    return execute(getExecutablePath(), options, pid);
  }

  /**
   * Executes jvisualvm and returns the output.
   *
   * @param executable	the jvisualvm executable to use
   * @param options	additional options for jvisualvm
   * @param pid		the process ID of the JVM to connect to,
   * 			gets determined automatically if AUTO_PID
   * @return		the output, if any
   */
  public static String execute(String executable, String options, long pid) {
    // add pid to options
    if (pid == ProcessUtils.AUTO_PID)
      pid = ProcessUtils.getVirtualMachinePID();
    options = options + " --openpid " + pid;

    return execute(executable, options);
  }

  /**
   * Generates a help string for the properties.
   *
   * @return		the help string
   */
  public static String getPropertiesHelp() {
    return "Create props file '" + FILENAME + "' with the following content:\n\n"
      + "- Linux/Mac:\n"
      + KEY_EXECUTABLE + "=/some/where/" + EXECUTABLE + "\n\n"
      + "- Windows:\n"
      + KEY_EXECUTABLE + "=C:/some/where/" + EXECUTABLE + "\n\n"
      + "and place the props file, e.g., in your ADAMS home directory:\n"
      + Environment.getInstance().getHome();
  }

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
        m_Properties = Environment.getInstance().read(JVisualVMDefinition.KEY);
      }
      catch (Exception e) {
        m_Properties = new Properties();
      }
    }

    return m_Properties;
  }
}
