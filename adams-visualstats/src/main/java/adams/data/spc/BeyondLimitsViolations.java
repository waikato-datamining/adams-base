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
 * BeyondLimitsViolations.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import gnu.trove.list.array.TIntArrayList;

/**
 <!-- globalinfo-start -->
 * Flags any data point as violation if below lower or above upper limit.
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
public class BeyondLimitsViolations
  extends AbstractViolationFinder {

  private static final long serialVersionUID = 6050852088287348188L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Flags any data point as violation if below lower or above upper limit.";
  }

  /**
   * Performs the finding.
   *
   * @param data	the data to check
   * @param limits	the limits for the data
   * @return		the indices of the violations
   */
  protected int[] doFind(double[] data, Limits[] limits) {
    TIntArrayList   result;
    int		    i;

    result = new TIntArrayList();
    for (i = 0; i < data.length; i++) {
      if (limits.length == data.length) {
	if ((data[i] < limits[i].getLower()) || (data[i] > limits[i].getUpper()))
	  result.add(i);
      }
      else {
	if ((data[i] < limits[0].getLower()) || (data[i] > limits[0].getUpper()))
	  result.add(i);
      }
    }

    return result.toArray();
  }
}
