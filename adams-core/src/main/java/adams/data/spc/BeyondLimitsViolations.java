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

import adams.flow.container.ControlChartContainer;
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
   * Performs the actual finding.
   *
   * @param cont	the container to check for violations
   * @return		the new and updated container
   */
  @Override
  protected ControlChartContainer doFind(ControlChartContainer cont) {
    ControlChartContainer	result;
    TIntArrayList		violations;
    double			lower;
    double			upper;
    double[]			prepared;
    int				i;

    lower      = (Double) cont.getValue(ControlChartContainer.VALUE_LOWER);
    upper      = (Double) cont.getValue(ControlChartContainer.VALUE_UPPER);
    prepared   = (double[]) cont.getValue(ControlChartContainer.VALUE_PREPARED);
    violations = new TIntArrayList();
    for (i = 0; i < prepared.length; i++) {
      if ((prepared[i] < lower) || (prepared[i] > upper))
	violations.add(i);
    }

    result = (ControlChartContainer) cont.getClone();
    result.setValue(ControlChartContainer.VALUE_VIOLATIONS, violations.toArray());

    return result;
  }
}
