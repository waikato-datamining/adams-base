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
 * PChart.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.data.statistics.SPCUtils;

/**
 <!-- globalinfo-start -->
 * Generates data for a p chart.<br>
 * <br>
 * For more information see:<br>
 * QICacros. p Chart Formulas. URL http:&#47;&#47;www.qimacros.com&#47;control-chart-formulas&#47;p-chart-formula&#47;.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-sample-size &lt;int&gt; (property: sampleSize)
 * &nbsp;&nbsp;&nbsp;The sample size to use.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PChart
  extends AbstractControlChartWithSampleSize
  implements IndividualsControlChart {

  private static final long serialVersionUID = 4352909660548550374L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates data for a " + getName() + ".\n\n"
      + "For more information see:\n"
      + getTechnicalInformation().toString();
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
    result.setValue(Field.AUTHOR, "QICacros");
    result.setValue(Field.TITLE, "p Chart Formulas");
    result.setValue(Field.URL, "http://www.qimacros.com/control-chart-formulas/p-chart-formula/");

    return result;
  }

  /**
   * Returns the default sample size.
   *
   * @return		the default size
   */
  protected int getDefaultSampleSize() {
    return 100;
  }

  /**
   * Returns the chart name.
   *
   * @return		the chart name
   */
  public String getName() {
    return "p chart";
  }

  /**
   * Calculates the center/lower/upper limit.
   *
   * @param data	the data to use for the calculation
   * @return		center/lower/upper
   */
  @Override
  public double[] calculate(Number[] data) {
    return SPCUtils.stats_p(data, m_SampleSize);
  }

  /**
   * Prepares the data.
   *
   * @param data	the data to prepare
   * @return		the processed data
   */
  @Override
  public double[] prepare(Number[] data) {
    return SPCUtils.prepare_data_p(data, m_SampleSize);
  }
}
