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
 * BMP.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filecomplete;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Checks whether the BMP file has sufficient bytes according to its header.<br>
 * See also:<br>
 * https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;BMP_file_format#File_structure
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-strict &lt;boolean&gt; (property: strict)
 * &nbsp;&nbsp;&nbsp;Whether to be strict or allow trailing junk data.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BMP
  extends AbstractStrictFileCompleteCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  public final static int MIN_BYTES = 6;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Checks whether the BMP file has sufficient bytes according to its header.\n"
	+ "See also:\n"
	+ "https://en.wikipedia.org/wiki/BMP_file_format#File_structure";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String strictTipText() {
    return "Whether to be strict or allow trailing junk data.";
  }

  /**
   * Checks whether the length in the header works with the actual data length.
   *
   * @param buffer	the header
   * @param actLen	the actual amount of data
   * @return		true if valid
   */
  protected boolean checkLength(byte[] buffer, long actLen) {
    ByteBuffer	bb;
    int 	expLen;

    bb  = ByteBuffer.wrap(buffer, 2, 4).order(ByteOrder.LITTLE_ENDIAN);
    expLen = bb.getInt();
    if (m_Strict)
      return (expLen == actLen);
    else
      return (expLen <= actLen);
  }

  /**
   * Checks whether the byte buffer is complete.
   *
   * @param buffer	the buffer to check
   * @return		true if complete
   */
  @Override
  public boolean isComplete(byte[] buffer) {
    if (buffer.length < MIN_BYTES) {
      if (isLoggingEnabled())
	getLogger().info("Not enough data in file: " + buffer.length + " <= " + MIN_BYTES);
      return false;
    }

    return checkLength(buffer, buffer.length);
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if complete
   */
  @Override
  public boolean isComplete(File file) {
    byte[]	buffer;

    try {
      buffer = read(file, 0, MIN_BYTES);
      if (buffer.length < MIN_BYTES) {
	if (isLoggingEnabled())
	  getLogger().info("Not enough data in file: " + buffer.length + " <= " + MIN_BYTES);
	return false;
      }
      return checkLength(buffer, file.length());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read data from: " + file, e);
      return false;
    }
  }
}
