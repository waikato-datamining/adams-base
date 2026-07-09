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
 * ParametersFromFileSupporter.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;

/**
 * Interface for classes that can load some/all of their parameters
 * from a properties file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ParametersFromFileSupporter
  extends OptionHandler {

  /**
   * Sets the properties file with the parameters to load. Ignored if pointing to a directory.
   *
   * @param value	the file
   */
  public void setParametersFile(PlaceholderFile value);

  /**
   * Returns the properties file with the parameters to load. Ignored if pointing to a directory.
   *
   * @return 		the file
   */
  public PlaceholderFile getParametersFile();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String parametersFileTipText();
}
