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
 * JpegIsComplete.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileuse;

import adams.core.Utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Checks whether the JPEG file ends with bytes FFD9 (EOF for JPEGs).<br>
 * See also:<br>
 * http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;JPEG#Syntax_and_structure
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JpegIsComplete
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Checks whether the JPEG file ends with bytes FFD9 (EOF for JPEGs).\n"
      + "See also:\n"
      + "http://en.wikipedia.org/wiki/JPEG#Syntax_and_structure";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  @Override
  public boolean isInUse(File file) {
    boolean		result;
    RandomAccessFile	raf;
    byte[]		buffer;

    try {
      buffer = new byte[2];
      raf = new RandomAccessFile(file.getAbsolutePath(), "r");
      raf.seek(file.length() - 2);
      raf.read(buffer, 0, 2);
      result = !((buffer[0] == -1) && (buffer[1] == -39));  // FF and D9
      if (isLoggingEnabled())
	getLogger().info("Last two bytes: " + Utils.toHex(buffer[0]) + Utils.toHex(buffer[1]) + " -> " + result);
    }
    catch (Exception e) {
      if (isLoggingEnabled())
	getLogger().log(Level.SEVERE, "Failed to extract bytes from: " + file, e);
      result = true;
    }

    return result;
  }
}
