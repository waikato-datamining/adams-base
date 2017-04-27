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
 * JDeps.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.io.FileUtils;

import java.io.File;

/**
 * A helper class for the jdeps utility.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JDeps
  extends Java {

  /** the jdeps executable. */
  public final static String EXECUTABLE = "jdeps";

  /**
   * Checks whether jdeps is available at all.
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
   * Returns the default options ("-histo:live") for jdeps.
   *
   * @return		the default options
   */
  public static String getDefaultOptions() {
    return "";
  }

  /**
   * Executes jdeps and returns the output.
   * Automatically adds classpath to commandline.
   *
   * @param options	additional options for jdeps
   * @return		the output
   */
  public static String execute(String options) {
    return execute(getExecutablePath(), options);
  }

  /**
   * Executes the executable and returns the output.
   * Automatically adds classpath to commandline.
   *
   * @param executable	the jdeps executable to use
   * @param options	additional options for jdeps
   * @return		the output
   */
  public static String execute(String executable, String options) {
    return execute(executable, null, options);
  }

  /**
   * Executes the executable and returns the output.
   * Automatically adds classpath to commandline.
   *
   * @param executable	the jdeps executable to use
   * @param classpath 	the classpath parts (directories and jar files); uses
   *                    application classpath if null or 0-length
   * @param options	additional options for jdeps
   * @return		the output
   */
  public static String execute(String executable, File[] classpath, String options) {
    StringBuilder	fullClasspath;
    int			i;

    fullClasspath = new StringBuilder();
    if ((classpath != null) && (classpath.length > 0)) {
      for (i = 0; i < classpath.length; i++) {
	if (i > 0)
	  fullClasspath.append(File.pathSeparator);
	fullClasspath.append(classpath[i].getAbsolutePath());
      }
    }
    else {
      fullClasspath.append(Java.getClassPath(false));
    }

    options = "-cp " + fullClasspath + " " + options;
    return Java.execute(executable, options);
  }
}
