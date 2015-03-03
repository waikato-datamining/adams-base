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
 * SAX.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.Date;

import adams.core.ClassCrossReference;
import adams.data.container.DataPoint;
import adams.data.statistics.TimeseriesStatistic;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Performs Symbolic Aggregate approXimation (SAX).<br/>
 * The data must be normalized using adams.data.filter.RowNorm beforehand.<br/>
 * For more information see:<br/>
 * <br/>
 * Chiu, B., Keogh, E., Lonardi, S. (2003). Probabilistic Discovery of Time Series Motifs.<br/>
 * <br/>
 * See also:<br/>
 * adams.data.filter.RowNorm
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;proceedings{Chiu2003,
 *    author = {Chiu, B. and Keogh, E. and Lonardi, S.},
 *    booktitle = {9th ACM SIGKDD International Conference on Knowledge Discovery and Data Mining},
 *    pages = {493-498},
 *    title = {Probabilistic Discovery of Time Series Motifs},
 *    year = {2003},
 *    location = {Washington, DC, USA},
 *    PDF = {http:&#47;&#47;www.cs.ucr.edu&#47;\~eamonn&#47;SIGKDD_Motif.pdf}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-id-update &lt;boolean&gt; (property: dontUpdateID)
 * &nbsp;&nbsp;&nbsp;If enabled, suppresses updating the ID of adams.data.id.IDHandler data containers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-windows &lt;int&gt; (property: numWindows)
 * &nbsp;&nbsp;&nbsp;The number of windows to use for Piecewise Aggregate Approximation (PAA).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to use for the Gaussian.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-output-labels &lt;boolean&gt; (property: outputLabels)
 * &nbsp;&nbsp;&nbsp;If enabled, labels are output instead of distances.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 */
public class SAX
  extends AbstractSAX<Timeseries>
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = 1836858988505886282L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Performs Symbolic Aggregate approXimation (SAX).\n"
	+ "The data must be normalized using " + RowNorm.class.getName() + " beforehand.\n"
	+ "For more information see:\n\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{RowNorm.class};
  }

  /**
   * Computes the mean difference between data points on the X axis.
   * 
   * @param data	the data to use for the calculation
   * @return		the mean
   */
  @Override
  protected double getMeanDeltaX(Timeseries data) {
    TimeseriesStatistic<Timeseries>	stats;
    
    stats = new TimeseriesStatistic<Timeseries>(data);
    
    return stats.getStatistic(TimeseriesStatistic.MEAN_DELTA_TIMESTAMP);
  }

  /**
   * Obtains the X value from the given data point.
   * 
   * @param point	the data point to extract the X value from
   * @return		the X value
   */
  @Override
  protected double getX(DataPoint point) {
    return ((TimeseriesPoint) point).getTimestamp().getTime();
  }

  /**
   * Obtains the Y value from the given data point.
   * 
   * @param point	the data point to extract the Y value from
   * @return		the Y value
   */
  @Override
  protected double getY(DataPoint point) {
    return ((TimeseriesPoint) point).getValue();
  }

  /**
   * Creates a new data point from the X and Y values.
   * 
   * @param x		the raw X value
   * @param y		the raw Y value
   * @return		the data point
   */
  @Override
  protected DataPoint newDataPoint(double x, double y) {
    return new TimeseriesPoint(new Date((long) x), y);
  }
}
