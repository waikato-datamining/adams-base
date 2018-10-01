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
 * Filter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing;

import adams.ml.capabilities.Capabilities;
import adams.ml.capabilities.CapabilitiesHandler;
import adams.ml.data.Dataset;

/**
 * Interface for filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface Filter
  extends CapabilitiesHandler {

  /**
   * Returns the capabilities.
   *
   * @return		the capabilities
   */
  public Capabilities getCapabilities();

  /**
   * Returns whether the filter has been initialized.
   *
   * @return		true if initialized
   */
  public boolean isInitialized();

  /**
   * Returns the output format.
   *
   * @return		the format, null if not yet defined
   */
  public Dataset getOutputFormat();
}
