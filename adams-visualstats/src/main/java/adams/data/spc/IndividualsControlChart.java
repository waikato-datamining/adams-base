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
 * IndividualsControlChart.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import java.util.List;

/**
 * Interface for control charts that work on individuals rather than samples.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface IndividualsControlChart
  extends ControlChart {

  /**
   * Calculates center/lower/upper limit.
   *
   * @param data	the data to use for the calculation
   * @return		center/lower/upper
   */
  public List<Limits> calculate(Number[] data);

  /**
   * Prepares the data.
   *
   * @param data	the data to prepare
   * @return		the prepared/processed data
   */
  public double[] prepare(Number[] data);
}
