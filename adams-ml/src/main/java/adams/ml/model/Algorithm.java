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
 * Algorithm.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model;

import adams.ml.core.Capabilities;
import adams.ml.core.CapabilitiesHandler;
import adams.ml.data.Dataset;

/**
 * Interface for machine learning algorithms.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of model to generate
 */
public interface Algorithm<T>
  extends CapabilitiesHandler {

  /**
   * Returns the algorithm's capabilities in terms of data.
   *
   * @return		the algorithm's capabilities
   */
  public Capabilities getCapabilities();

  /**
   * Checks whether the data can be handled.
   *
   * @param data	the data to check
   * @param strict	whether to perform a strict check
   * @return		true if data can be handled
   */
  public boolean handles(Dataset data, boolean strict);

  /**
   * Builds a model from the data.
   *
   * @param data	the data to use for building the model
   * @return		the generated model
   * @throws Exception	if the build fails
   */
  public T buildModel(Dataset data) throws Exception;
}
