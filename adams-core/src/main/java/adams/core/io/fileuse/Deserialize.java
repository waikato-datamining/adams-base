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
 * Deserialize.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileuse;

import adams.core.SerializationHelper;

import java.io.File;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Attempts to deserialize the file.
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
public class Deserialize
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = 9136539418805277545L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Attempts to deserialize the file.";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  @Override
  public boolean isInUse(File file) {
    try {
      SerializationHelper.read(file.getAbsolutePath());
      return false;
    }
    catch (Exception e) {
      if (isLoggingEnabled())
	getLogger().log(Level.SEVERE, "Failed to deserialize: " + file, e);
      return true;
    }
  }
}
