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
 * ArrayNormalize.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

/**
 <!-- globalinfo-start -->
 * Normalizes the array(s), i.e., the sum of its/their values is 1.0.
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
public class ArrayNormalize<T extends Number>
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
    return "Normalizes the array(s), i.e., the sum of its/their values is 1.0.";
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
    StatisticContainer<Number>	result;
    int				i;
    int				n;
    String 			prefix;
    Double[]			normalized;

    result = new StatisticContainer<>(getLength(), size());

    prefix = "normalized";
    if (size() > 2)
      prefix += "-";

    for (i = 0; i < size(); i++) {
      if (size() > 1)
	result.setHeader(i, prefix + "1-" + (i+1));
      else
	result.setHeader(i, prefix);

      normalized = StatUtils.normalize(get(i));
      if (normalized != null) {
	for (n = 0; n < getLength(); n++)
	  result.setCell(n, i, normalized[n]);
      }
      else {
	getLogger().severe("Failed to normalize array! Only positive numbers?");
      }
    }

    return result;
  }
}
