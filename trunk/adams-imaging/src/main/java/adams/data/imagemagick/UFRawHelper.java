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
 * UFRawHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick;

import java.awt.image.BufferedImage;
import java.io.File;

import org.im4java.core.UFRawCmd;
import org.im4java.core.UFRawOperation;

import adams.core.io.FileUtils;
import adams.env.Environment;

/**
 * Helper class for ufraw.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9991 $
 */
public class UFRawHelper {
  
  /** the environment variable to check for ufraw. */
  public final static String ENV_PATH = "UFRAW_TOOLPATH";

  /** whether "ufraw" is present. */
  protected static Boolean m_UfrawPresent;

  /**
   * Checks whether the "ufraw" utility is available.
   *
   * @return		true if "ufraw" is available (on PATH)
   */
  public static boolean isUfrawAvailable() {
    Process	proc;
    String	path;
    String	exec;

    if (m_UfrawPresent == null) {
      exec = FileUtils.fixExecutable("ufraw");
      path = System.getenv(ENV_PATH);
      if (path != null)
        exec = path + File.separator + exec;
      try {
	proc = Runtime.getRuntime().exec(new String[]{exec, "--version"});
	m_UfrawPresent = (proc.waitFor() == 0);
      }
      catch (Exception e) {
        System.err.println("Failed to execute '" + exec + "':");
        e.printStackTrace();
	m_UfrawPresent = false;
      }
    }

    return m_UfrawPresent;
  }

  /**
   * Returns a standard error message if "ufraw" is not available.
   *
   * @return		the error message
   */
  public static String getMissingUfrawErrorMessage() {
    return "ufraw not installed or " + ENV_PATH + " environment variable not pointing to installation!";
  }

  /**
   * Reads a BufferedImage from the file.
   *
   * @param file	the file to read the image from
   * @return		the image, null in case of an error
   */
  public static BufferedImage read(File file) {
    BufferedImage	result;
    UFRawOperation	op;
    UFRawCmd		cmd;
    PPMOutputConsumer 	joc;

    op = new UFRawOperation();
    op.addImage(file.getAbsolutePath());
    op.outType("ppm");
    op.output("-");

    joc = new PPMOutputConsumer();
    cmd = new UFRawCmd(true);
    cmd.setOutputConsumer(joc);
    try {
      cmd.run(op);
      result = joc.getImage();
    }
    catch (Exception e) {
      result = null;
      System.err.println("Failed to read image from file '" + file + "':");
      e.printStackTrace();
    }

    return result;
  }
  
  /**
   * Just outputs some information on availability of tools.
   * 
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    System.out.println("Tool availability:");
    System.out.println("- ufraw? " + isUfrawAvailable());
  }
}
