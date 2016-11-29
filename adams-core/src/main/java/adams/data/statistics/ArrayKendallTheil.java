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
 * ArrayKendallTheil.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;

/**
 <!-- globalinfo-start -->
 * Calculates the Kendall-Theil robust slope (also called Theil-Sen estimator) between the first array and the remaining arrays. The arrays must be numeric, of course.<br>
 * <br>
 * For more information:<br>
 * Wikipedia. Theil–Sen estimator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {Wikipedia},
 *    title = {Theil–Sen estimator},
 *    HTTP = {https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Theil%E2%80%93Sen_estimator}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
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
public class ArrayKendallTheil<T extends Number>
  extends AbstractArrayStatistic<T>
  implements EqualLengthArrayStatistic, TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8764320126807871007L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Calculates the Kendall-Theil robust slope (also called Theil-Sen estimator) "
        + "between the first array and the remaining arrays. The arrays must "
        + "be numeric, of course.\n\n"
        + "For more information:\n"
        + getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Wikipedia");
    result.setValue(Field.TITLE, "Theil–Sen estimator");
    result.setValue(Field.HTTP, "https://en.wikipedia.org/wiki/Theil%E2%80%93Sen_estimator");

    return result;
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
   * Generates the actual result.
   *
   * @return		the generated result
   */
  protected StatisticContainer doCalculate() {
    StatisticContainer<Number>	result;
    int				i;
    double[] 			kt;

    result = new StatisticContainer<>(1, (size() - 1)*2);

    for (i = 1; i < size(); i++) {
      if (size() > 2) {
	result.setHeader((i - 1)*2 + 0, "intercept_1-" + (i + 1));
	result.setHeader((i - 1)*2 + 1, "slope_1-" + (i + 1));
      }
      else {
	result.setHeader(0, "intercept");
	result.setHeader(1, "slope");
      }

      kt = StatUtils.kendallTheil(get(0), get(i));
      result.setCell(0, (i - 1)*2 + 0, kt[0]);
      result.setCell(0, (i - 1)*2 + 1, kt[1]);
    }

    return result;
  }
}
