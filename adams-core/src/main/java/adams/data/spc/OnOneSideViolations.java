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
 * OnOneSideViolations.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import adams.flow.container.ControlChartContainer;
import gnu.trove.list.array.TIntArrayList;

/**
 <!-- globalinfo-start -->
 * Flags a data point as violation if it is the start of consecutive sequence (of specified minimum length) of points that are all on one side.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-min-points &lt;int&gt; (property: minPoints)
 * &nbsp;&nbsp;&nbsp;The minimum number of points that have to be on one side.
 * &nbsp;&nbsp;&nbsp;default: 7
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OnOneSideViolations
  extends AbstractViolationFinder {

  private static final long serialVersionUID = 6050852088287348188L;

  /** the minimum number of data points on one side before flagging as violation. */
  protected int m_MinPoints;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Flags a data point as violation if it is the start of consecutive "
	+ "sequence (of specified minimum length) of points that are all on one side.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-points", "minPoints",
      7, 1, null);
  }

  /**
   * Sets the minimum number of points that have to be on one side.
   *
   * @param value	the minimum number of points
   */
  public void setMinPoints(int value) {
    if (value >= 1) {
      m_MinPoints = value;
      reset();
    }
    else {
      getLogger().warning("Minimum number of points must be 1, provided: " + value);
    }
  }

  /**
   * Returns whether to produce mean or variation data.
   *
   * @return		true if to generate variation data
   */
  public int getMinPoints() {
    return m_MinPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minPointsTipText() {
    return "The minimum number of points that have to be on one side.";
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
    double			center;
    double[]			prepared;
    int				i;
    int				n;
    boolean			mixed;

    center     = (Double) cont.getValue(ControlChartContainer.VALUE_CENTER);
    prepared   = (double[]) cont.getValue(ControlChartContainer.VALUE_PREPARED);
    violations = new TIntArrayList();
    for (i = 0; i < prepared.length - m_MinPoints; i++) {
      mixed = false;
      for (n = 0; n < m_MinPoints - 1; n++) {
	if ((prepared[n] - center) * (prepared[n + 1] - center) < 0) {
	  mixed = true;
	  break;
	}
      }
      if (!mixed)
	violations.add(i);
    }

    result = (ControlChartContainer) cont.getClone();
    result.setValue(ControlChartContainer.VALUE_VIOLATIONS, violations.toArray());

    return result;
  }
}
