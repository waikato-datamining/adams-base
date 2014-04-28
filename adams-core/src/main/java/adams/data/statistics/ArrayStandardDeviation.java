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
 * ArrayStandardDeviation.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;


/**
 <!-- globalinfo-start -->
 * Calculates the standard deviation for a numeric array.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class ArrayStandardDeviation<T extends Number>
  extends AbstractOptionalSampleArrayStatistic<T> {

  /** for serialization. */
  private static final long serialVersionUID = 2678844088824196816L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Calculates the standard deviation for a numeric array.";
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  public int getMin() {
    return 1;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  public int getMax() {
    return -1;
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  protected StatisticContainer doCalculate() {
    StatisticContainer<Double>	result;
    int				i;
    String			prefix;

    result = new StatisticContainer<Double>(1, size());

    prefix = "stdev";
    if (getIsSample())
      prefix += "-sample";
    else
      prefix += "-pop.";

    for (i = 0; i < size(); i++) {
      if (size() > 1)
	result.setHeader(i, prefix + "-" + (i+1));
      else
	result.setHeader(i, prefix);

      result.setCell(0, i, StatUtils.stddev(get(i), getIsSample()));
    }

    return result;
  }
}
