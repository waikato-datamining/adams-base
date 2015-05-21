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
 * ArrayLinearRegression.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 <!-- globalinfo-start -->
 * Calculates the slope and intercept of linear regression between two arrays (x and y).<br>
 * If more than two arrays supplied, then the linear regression is computed between the first (x) and all the other ones (y).
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class ArrayLinearRegression<T extends Number>
  extends AbstractArrayStatistic<T>
  implements EqualLengthArrayStatistic {

  /** for serialization. */
  private static final long serialVersionUID = -5911270089583842477L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Calculates the slope and intercept of linear regression between two arrays (x and y).\n"
      + "If more than two arrays supplied, then the linear regression is computed "
      + "between the first (x) and all the other ones (y).";
  }

  /**
   * Returns the length of the stored arrays.
   *
   * @return		the length of the arrays, -1 if none stored
   */
  public int getLength() {
    if (size() > 0)
      return get(0).length;
    else
      return -1;
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  public int getMin() {
    return 2;
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
    StatisticContainer<Number>	result;
    int				i;
    double[]			lr;

    result = new StatisticContainer<Number>(1, (size() - 1)*2);

    for (i = 1; i < size(); i++) {
      if (size() > 2) {
	result.setHeader((i - 1)*2 + 0, "intercept_1-" + (i + 1));
	result.setHeader((i - 1)*2 + 1, "slope_1-" + (i + 1));
      }
      else {
	result.setHeader(0, "intercept");
	result.setHeader(1, "slope");
      }

      lr = StatUtils.linearRegression(get(0), get(i));
      result.setCell(0, (i - 1)*2 + 0, lr[0]);
      result.setCell(0, (i - 1)*2 + 1, lr[1]);
    }

    return result;
  }
}
