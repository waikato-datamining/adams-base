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
 * XBarRChart.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.data.statistics.SPCUtils;

/**
 <!-- globalinfo-start -->
 * Generates data for a XBar R chart.<br>
 * <br>
 * For more information see:<br>
 * QICacros. X bar R Chart Formulas. URL http:&#47;&#47;www.qimacros.com&#47;control-chart-formulas&#47;x-bar-r-chart-formula&#47;.
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
 * &nbsp;&nbsp;&nbsp;The sample size to use; use &lt; 0 to automatically determine from data.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-variation &lt;boolean&gt; (property: variation)
 * &nbsp;&nbsp;&nbsp;If enabled, variation data instead of mean data is generated.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XBarRChart
  extends AbstractControlChartWithSampleSize
  implements SamplesControlChart {

  private static final long serialVersionUID = 4352909660548550374L;

  /** whether to generate mean or variation data. */
  protected boolean m_Variation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates data for a XBar R chart.\n\n"
      + "For more information see:\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "variation", "variation",
      false);
  }

  /**
   * Returns the default sample size.
   *
   * @return		the default size
   */
  protected int getDefaultSampleSize() {
    return -1;
  }

  /**
   * Returns the default lower limit for the sample size.
   *
   * @return		the default lower limit
   */
  protected Number getDefaultSampleSizeLowerLimit() {
    return -1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sampleSizeTipText() {
    return "The sample size to use; use < 0 to automatically determine from data.";
  }

  /**
   * Sets whether to produce mean or variation data.
   *
   * @param value	true if to generate variation data
   */
  public void setVariation(boolean value) {
    m_Variation = value;
    reset();
  }

  /**
   * Returns whether to produce mean or variation data.
   *
   * @return		true if to generate variation data
   */
  public boolean getVariation() {
    return m_Variation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variationTipText() {
    return "If enabled, variation data instead of mean data is generated.";
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
    result.setValue(Field.TITLE, "X bar R Chart Formulas");
    result.setValue(Field.URL, "http://www.qimacros.com/control-chart-formulas/x-bar-r-chart-formula/");

    return result;
  }

  /**
   * Returns the chart name.
   *
   * @return		the chart name
   */
  public String getName() {
    return "Xbar R chart (" + (m_Variation ? "variation" : "mean") + ")";
  }

  /**
   * Returns the actual sample size to use.
   *
   * @param data	the data to base the calculation on
   * @return		the actual sample size
   */
  protected int getActualSampleSize(Number[][] data) {
    if (m_SampleSize > 0)
      return m_SampleSize;
    else
      return data[0].length;
  }

  /**
   * Calculates the center/lower/upper limit.
   *
   * @param data	the data to use for the calculation
   * @return		center/lower/upper
   */
  @Override
  public double[] calculate(Number[][] data) {
    if (m_Variation)
      return SPCUtils.stats_x_bar_r_r(data, getActualSampleSize(data));
    else
      return SPCUtils.stats_x_bar_r_x(data, getActualSampleSize(data));
  }

  /**
   * Prepares the data.
   *
   * @param data	the data to prepare
   * @return		the processed data
   */
  @Override
  public double[] prepare(Number[][] data) {
    if (m_Variation)
      return SPCUtils.prepare_data_x_bar_r_r(data, getActualSampleSize(data));
    else
      return SPCUtils.prepare_data_x_bar_rs_x(data, getActualSampleSize(data));
  }
}
