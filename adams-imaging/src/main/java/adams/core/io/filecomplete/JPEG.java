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
 * JPEG.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filecomplete;

import java.io.File;

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
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JPEG
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
      "Checks whether the JPEG file ends with bytes FFD9 (EOF for JPEGs).\n"
	+ "See also:\n"
	+ "http://en.wikipedia.org/wiki/JPEG#Syntax_and_structure";
  }

  /**
   * Returns the minimally allowed check size.
   *
   * @return the minimum
   */
  @Override
  protected int getMinCheckSize() {
    return 2;
  }

  /**
   * Checks whether the byte buffer is complete.
   *
   * @param buffer	the buffer to check
   * @return		true if complete
   */
  @Override
  public boolean isComplete(byte[] buffer) {
    boolean	result;
    int		i;

    if (buffer.length >= getMinCheckSize()) {
      if (m_Strict) {
        i = buffer.length - 2;
	result = ((buffer[i] == -1) && (buffer[i + 1] == -39));  // FF and D9
      }
      else {
	result = false;
	for (i = 0; i <= buffer.length - 2; i++) {
	  if ((buffer[i] == -1) && (buffer[i + 1] == -39)) {  // FF and D9
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
      return isComplete(file, getMinCheckSize());
    else
      return isComplete(file, m_CheckSize);
  }
}
