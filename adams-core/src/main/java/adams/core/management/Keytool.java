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
 * Keytool.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.io.FileUtils;

import java.io.File;

/**
 * A helper class for the keytool utility.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Keytool
  extends Java {

  /** the keytool executable. */
  public final static String EXECUTABLE = "keytool";

  /**
   * Checks whether keytool is available at all.
   *
   * @return		true if available
   */
  public static boolean isAvailable() {
    return new File(getBinDir() + File.separator + FileUtils.fixExecutable(EXECUTABLE)).exists();
  }

  /**
   * Returns the full path of the keytool executable, if possible.
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
   * Executes keytool and returns the output.
   *
   * @param options	additional options for keytool
   * @return		the output
   */
  public static String execute(String options) {
    return execute(getExecutablePath(), options);
  }

  /**
   * Executes the executable and returns the output.
   *
   * @param executable	the jvisualvm executable to use
   * @param options	additional options for jvisualvm
   * @return		the output
   */
  public static String execute(String executable, String options) {
    return Java.execute(executable, options);
  }
}
