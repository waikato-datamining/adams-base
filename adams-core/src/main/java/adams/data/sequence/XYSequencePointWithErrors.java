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
 * XYSequencePointWithErrors.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.sequence;

import adams.data.container.DataPoint;

/**
 * Extends {@link DataPoint} to store X/Y error information as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface XYSequencePointWithErrors
  extends DataPoint {

  /**
   * Returns the minimum for X.
   *
   * @return		the minimum
   */
  public double getMinX();

  /**
   * Returns the maximum for X.
   *
   * @return		the maximum
   */
  public double getMaxX();

  /**
   * Checks whether error information for X is available.
   * 
   * @return		true if available
   */
  public boolean hasErrorX();

  /**
   * Returns the error information for X.
   * 
   * @return		the error information, null if not available
   */
  public Double[] getErrorX();

  /**
   * Returns the minimum for Y.
   *
   * @return		the minimum
   */
  public double getMinY();

  /**
   * Returns the maximum for Y.
   *
   * @return		the maximum
   */
  public double getMaxY();

  /**
   * Checks whether error information for Y is available.
   * 
   * @return		true if available
   */
  public boolean hasErrorY();

  /**
   * Returns the error information for Y.
   * 
   * @return		the error information, null if not available
   */
  public Double[] getErrorY();
}
