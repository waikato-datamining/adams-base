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
 * PNG.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filecomplete;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Checks whether the PNG file ends with bytes IEND (EOF for PNGs).<br>
 * See also:<br>
 * https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Portable_Network_Graphics#Critical_chunks<br>
 * http:&#47;&#47;www.libpng.org&#47;pub&#47;png&#47;spec&#47;1.2&#47;PNG-Structure.html#Chunk-layout<br>
 * http:&#47;&#47;www.libpng.org&#47;pub&#47;png&#47;spec&#47;1.2&#47;PNG-Chunks.html#C.IEND
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
 * <pre>-check-size &lt;int&gt; (property: checkSize)
 * &nbsp;&nbsp;&nbsp;The number of bytes to read from the back of the file (in non-strict mode
 * &nbsp;&nbsp;&nbsp;) to check for EOF marker.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 8
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PNG
  extends AbstractStrictCheckSizeFileCompleteCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Checks whether the PNG file ends with bytes IEND (EOF for PNGs).\n"
	+ "See also:\n"
	+ "https://en.wikipedia.org/wiki/Portable_Network_Graphics#Critical_chunks\n"
	+ "http://www.libpng.org/pub/png/spec/1.2/PNG-Structure.html#Chunk-layout\n"
	+ "http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html#C.IEND";
  }

  /**
   * Returns the minimally allowed check size.
   *
   * @return the minimum
   */
  @Override
  protected int getMinCheckSize() {
    return 8;
  }

  /**
   * Checks whether the byte buffer is complete.
   *
   * @param buffer	the buffer to check
   * @return		true if complete
   */
  @Override
  public boolean isComplete(byte[] buffer) {
    boolean		result;
    int			i;

    if (buffer.length >= getMinCheckSize()) {
      if (m_Strict) {
        i = buffer.length - 8;
	result = ((buffer[i] == 73) && (buffer[i + 1] == 69) && (buffer[i + 2] == 78) && (buffer[i + 3] == 68));  // IEND
      }
      else {
	result = false;
	for (i = 0; i <= buffer.length - 8; i++) {
	  if ((buffer[i] == 73) && (buffer[i + 1] == 69) && (buffer[i + 2] == 78) && (buffer[i + 3] == 68)) {  // IEND
	    result = true;
	    break;
	  }
	}
      }
      if (isLoggingEnabled())
	getLogger().info("EOF Marker found?" + result);
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Buffer too small: " + buffer.length + " < " + getMinCheckSize());
      result = false;
    }

    return result;
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if complete
   */
  @Override
  public boolean isComplete(File file) {
    if (m_Strict)
      return isCompleteEOF(file, getMinCheckSize());
    else
      return isCompleteEOF(file, m_CheckSize);
  }
}