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
 * DCRawHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick;

import java.io.File;

import adams.core.io.FileUtils;
import adams.env.Environment;

/**
 * Helper class for dcraw.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9991 $
 */
public class DCRawHelper {
  
  /** the environment variable to check for dcraw. */
  public final static String ENV_PATH = "DCRAW_TOOLPATH";

  /** whether "dcraw" is present. */
  protected static Boolean m_DcrawPresent;

  /**
   * Checks whether the "dcraw" utility is available.
   *
   * @return		true if "dcraw" is available (on PATH)
   */
  public static boolean isDcrawAvailable() {
    String	path;
    String	exec;

    if (m_DcrawPresent == null) {
      exec = FileUtils.fixExecutable("dcraw");
      path = System.getenv(ENV_PATH);
      if (path != null)
        exec = path + File.separator + exec;
      try {
	Runtime.getRuntime().exec(new String[]{exec});
	m_DcrawPresent = true;
      }
      catch (Exception e) {
        System.err.println("Failed to execute '" + exec + "':");
        e.printStackTrace();
	m_DcrawPresent = false;
      }
    }

    return m_DcrawPresent;
  }

  /**
   * Returns a standard error message if "dcraw" is not available.
   *
   * @return		the error message
   */
  public static String getMissingDcrawErrorMessage() {
    return "dcraw not installed or " + ENV_PATH + " environment variable not pointing to installation!";
  }
  
  /**
   * Just outputs some information on availability of tools.
   * 
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    System.out.println("Tool availability:");
    System.out.println("- dcraw? " + isDcrawAvailable());
  }
}
