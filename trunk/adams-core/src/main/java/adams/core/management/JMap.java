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
 * JMap.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.io.File;

import adams.core.io.FileUtils;

/**
 * A helper class for the jmap utility.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JMap
  extends Java {

  /** the jmap executable. */
  public final static String EXECUTABLE = "jmap";

  /**
   * Checks whether jmap is available at all.
   *
   * @return		true if available
   */
  public static boolean isAvailable() {
    boolean	result;

    result = isJDK();
    if (result)
      result = new File(getBinDir() + File.separator + FileUtils.fixExecutable(EXECUTABLE)).exists();

    return result;
  }

  /**
   * Returns the full path of the JMap executable, if possible.
   *
   * @return		the full path of the executable if possible, otherwise
   * 			just the executable
   */
  public static String getExecutablePath() {
    String	result;

    result = getBinDir() + File.separator + FileUtils.fixExecutable(EXECUTABLE);
    result = FileUtils.quoteExecutable(result);

    return result;
  }

  /**
   * Returns the default options ("-histo:live") for jmap.
   *
   * @return		the default options
   */
  public static String getDefaultOptions() {
    return "-histo:live";
  }

  /**
   * Executes jmap with default options and returns the output.
   *
   * @param pid		the process ID of the JVM to connect to,
   * 			gets determined automatically if AUTO_PID
   * @return		the output
   * @see		#getDefaultOptions()
   * @see		Java#AUTO_PID
   */
  public static String execute(long pid) {
    return execute(getDefaultOptions(), pid);
  }

  /**
   * Executes jmap and returns the output.
   *
   * @param options	additional options for jmap
   * @param pid		the process ID of the JVM to connect to,
   * 			gets determined automatically if AUTO_PID
   * @return		the output
   * @see		Java#AUTO_PID
   */
  public static String execute(String options, long pid) {
    return execute(getExecutablePath(), options, pid);
  }

  /**
   * Executes jmap and returns the output.
   *
   * @param executable	the jmap executable to use
   * @param options	additional options for jmap
   * @param pid		the process ID of the JVM to connect to,
   * 			gets determined automatically if AUTO_PID
   * @return		the output
   * @see		Java#AUTO_PID
   */
  public static String execute(String executable, String options, long pid) {
    // add pid to options
    if (pid == ProcessUtils.AUTO_PID)
      pid = ProcessUtils.getVirtualMachinePID();
    options = options + " " + pid;

    return execute(executable, options);
  }
}
