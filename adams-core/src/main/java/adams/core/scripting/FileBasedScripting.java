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
 * FileBasedScripting.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.scripting;

import adams.core.io.PlaceholderFile;

/**
 * Interface for scripted classes that use a file-based approach.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FileBasedScripting {

  /**
   * Sets the script file.
   *
   * @param value 	the script
   */
  public void setScriptFile(PlaceholderFile value);

  /**
   * Gets the script file.
   *
   * @return 		the script
   */
  public PlaceholderFile getScriptFile();

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String scriptFileTipText();
}
