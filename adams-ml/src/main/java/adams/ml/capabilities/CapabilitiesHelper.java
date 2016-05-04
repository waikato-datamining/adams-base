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
 * CapabilitiesHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.capabilities;

import adams.ml.data.Dataset;

/**
 * Helper class for capabilities.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CapabilitiesHelper {

  /**
   * Returns capabilities that are required for the specified dataset.
   *
   * @param data	the dataset to get the capabilities for
   * @return		the capabilities
   */
  public static Capabilities forDataset(Dataset data) {
    // TODO
    return new Capabilities(null);
  }

  /**
   * Returns whether the capabilities handler handles the dataset.
   *
   * @param handler	the handler to check
   * @param data	the dataset to check against
   * @return		null if OK, otherwise error message
   */
  public static String handles(CapabilitiesHandler handler, Dataset data) {
    return handles(handler.getCapabilities(), data);
  }

  /**
   * Returns whether the capabilities can handle the dataset.
   *
   * @param caps	the capabilities to use as basis
   * @param data	the dataset to check against
   * @return		null if OK, otherwise error message
   */
  public static String handles(Capabilities caps, Dataset data) {
    // TODO
    return null;
  }

  /**
   * Returns whether the capabilities can handle the dataset column.
   *
   * @param caps	the capabilities to use as basis
   * @param data	the dataset to check against
   * @param col		the column to check
   * @return		null if OK, otherwise error message
   */
  public static String handles(Capabilities caps, Dataset data, int col) {
    // TODO
    return null;
  }

  /**
   * Tries to adjust the dataset to the capabilities of the handler.
   *
   * @param handler	the handler to adjust the dataset for
   * @param data	the dataset to adjust
   * @return		the adjusted dataset
   * @throws Exception	if failed to adjust
   */
  public static Dataset adjust(CapabilitiesHandler handler, Dataset data) throws Exception {
    return adjust(handler.getCapabilities(), data);
  }

  /**
   * Tries to adjust the dataset to the capabilities.
   *
   * @param caps	the capabilities to adjust the dataset for
   * @param data	the dataset to adjust
   * @return		the adjusted dataset
   * @throws Exception	if failed to adjust
   */
  public static Dataset adjust(Capabilities caps, Dataset data) throws Exception {
    // TODO create view with columns turned off
    return data;
  }
}
