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
import java.util.List;

import adams.core.ClassCrossReference;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.statistics.TimeseriesStatistic;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.utils.SAXUtils;

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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 */
public class SAX
  extends AbstractFilter<Timeseries>
  implements TechnicalInformationHandler, ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = 1836858988505886282L;

  /** the number of windows to use for PAA. */
  protected int m_NumWindows;
  
  /** the number of breakpoints to use (for the Gaussian). */
  protected int m_NumBins;
  
  /** the calculated breakpoints. */
  protected double[] m_BreakPoints;
  
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
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.PROCEEDINGS);
    result.setValue(Field.AUTHOR, "Chiu, B. and Keogh, E. and Lonardi, S.");
    result.setValue(Field.TITLE, "Probabilistic Discovery of Time Series Motifs");
    result.setValue(Field.BOOKTITLE, "9th ACM SIGKDD International Conference on Knowledge Discovery and Data Mining");
    result.setValue(Field.PAGES, "493-498");
    result.setValue(Field.YEAR, "2003");
    result.setValue(Field.LOCATION, "Washington, DC, USA");
    result.setValue(Field.PDF, "http://www.cs.ucr.edu/~eamonn/SIGKDD_Motif.pdf");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-windows", "numWindows",
	    10, 1, null);

    m_OptionManager.add(
	    "num-bins", "numBins",
	    10, 1, null);
  }
  
  /**
   * Resets the filter.
   */
  @Override
  public void reset() {
    super.reset();
    
    m_BreakPoints = null;
  }

  /**
   * Sets the number of windows to use for PAA.
   *
   * @param value 	the number
   */
  public void setNumWindows(int value) {
    if (value >= 1) {
      m_NumWindows = value;
      reset();
    }
    else {
      getLogger().severe("The number of windows must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of windows to use for PAA.
   *
   * @return 		the number
   */
  public int getNumWindows() {
    return m_NumWindows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numWindowsTipText() {
    return "The number of windows to use for Piecewise Aggregate Approximation (PAA).";
  }

  /**
   * Sets the number of bins to use for the Gaussian.
   *
   * @param value 	the number
   */
  public void setNumBins(int value) {
    if (value >= 1) {
      m_NumBins = value;
      reset();
    }
    else {
      getLogger().severe("The number of bins must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of bins to use for the Gaussian.
   *
   * @return 		the number
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use for the Gaussian.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Timeseries processData(Timeseries data) {
    Timeseries				result;
    List<TimeseriesPoint>		points;
    TimeseriesPoint			point;
    double[]				values;
    int					i;
    double[]				sax;
    TimeseriesStatistic<Timeseries>	stats;
    long				meanDelta;

    result = data.getHeader();
    if (data.size() == 0)
      return result;
    
    points    = data.toList();
    stats     = new TimeseriesStatistic<Timeseries>(data);
    meanDelta = (long) stats.getStatistic(TimeseriesStatistic.MEAN_DELTA_TIMESTAMP);

    if (m_BreakPoints == null)
      m_BreakPoints = SAXUtils.calcBreakPoints(m_NumBins);
    
    values = new double[points.size()];
    for (i = 0; i < points.size(); i++)
      values[i] = points.get(i).getValue();
    
    sax = SAXUtils.toSAX(values, m_NumWindows, m_BreakPoints);
    for (i = 0; i < sax.length; i++) {
      point = new TimeseriesPoint(
	  new Date(points.get(0).getTimestamp().getTime() + i * meanDelta),
	  sax[i]);
      result.add(point);
    }
    
    return result;
  }
}
