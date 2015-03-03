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
 * SavitzkyGolay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * A filter that applies Savitzky-Golay smoothing.<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 * A. Savitzky, Marcel J.E. Golay (1964). Smoothing and Differentiation of Data by Simplified Least Squares Procedures. Analytical Chemistry. 36:1627-1639.<br/>
 * <br/>
 * William H. Press, Saul A. Teukolsky, William T. Vetterling, Brian P. Flannery (1992). Savitzky-Golay Smoothing Filters.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;article{Savitzky1964,
 *    author = {A. Savitzky and Marcel J.E. Golay},
 *    journal = {Analytical Chemistry},
 *    pages = {1627-1639},
 *    title = {Smoothing and Differentiation of Data by Simplified Least Squares Procedures},
 *    volume = {36},
 *    year = {1964},
 *    HTTP = {http:&#47;&#47;dx.doi.org&#47;10.1021&#47;ac60214a047}
 * }
 * 
 * &#64;inbook{Press1992,
 *    author = {William H. Press and Saul A. Teukolsky and William T. Vetterling and Brian P. Flannery},
 *    chapter = {14.8},
 *    edition = {Second},
 *    pages = {650-655},
 *    publisher = {Cambridge University Press},
 *    series = {Numerical Recipes in C},
 *    title = {Savitzky-Golay Smoothing Filters},
 *    year = {1992},
 *    PDF = {http:&#47;&#47;www.nrbook.com&#47;a&#47;bookcpdf&#47;c14-8.pdf}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-polynomial &lt;int&gt; (property: polynomialOrder)
 * &nbsp;&nbsp;&nbsp;The polynomial order to use, must be at least 2.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * </pre>
 * 
 * <pre>-derivative &lt;int&gt; (property: derivativeOrder)
 * &nbsp;&nbsp;&nbsp;The order of the derivative to use, &gt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-left &lt;int&gt; (property: numPointsLeft)
 * &nbsp;&nbsp;&nbsp;The number of points left of a data point, &gt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-right &lt;int&gt; (property: numPointsRight)
 * &nbsp;&nbsp;&nbsp;The number of points right of a data point, &gt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SavitzkyGolay
  extends AbstractSavitzkyGolay<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -8446122688895546559L;

  /**
   * Returns the X-value of the DataPoint.
   *
   * @param point	the point to get the X-Value from
   * @return		the X-value
   */
  @Override
  protected double getValue(DataPoint point) {
    return ((TimeseriesPoint) point).getValue();
  }

  /**
   * Creates a new DataPoint based on the old one and the new X value.
   *
   * @param oldPoint	the old DataPoint
   * @param x		the new X value
   * @return		the new DataPoint
   */
  @Override
  protected DataPoint newDataPoint(DataPoint oldPoint, double x) {
    return new TimeseriesPoint(((TimeseriesPoint) oldPoint).getTimestamp(), x);
  }
}
