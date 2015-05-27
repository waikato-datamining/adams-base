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
 * UChart.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

import adams.core.Index;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.data.statistics.SPCUtils;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates data for a u chart.<br>
 * <br>
 * For more information see:<br>
 * QICacros. u Chart Formulas. URL http:&#47;&#47;www.qimacros.com&#47;control-chart-formulas&#47;u-chart-formula&#47;.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-non-conform-index &lt;adams.core.Index&gt; (property: nonConformitiesIndex)
 * &nbsp;&nbsp;&nbsp;The index of the matrix column containing the non-conformities.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-sizes-index &lt;adams.core.Index&gt; (property: sizesIndex)
 * &nbsp;&nbsp;&nbsp;The index of the matrix column containing the sample sizes.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UChart
  extends AbstractControlChart
  implements MatrixControlChart {

  private static final long serialVersionUID = 4352909660548550374L;

  /** the column in the matrix with the nonconformities. */
  protected Index m_NonConformitiesIndex;

  /** the column in the matrix with the sample sizes. */
  protected Index m_SizesIndex;

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
    result.setValue(Field.TITLE, "u Chart Formulas");
    result.setValue(Field.URL, "http://www.qimacros.com/control-chart-formulas/u-chart-formula/");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "non-conform-index", "nonConformitiesIndex",
      new Index("1"));

    m_OptionManager.add(
      "sizes-index", "sizesIndex",
      new Index("2"));
  }

  /**
   * Sets the matrix column index for the nonconformities.
   *
   * @param value	the index
   */
  public void setNonConformitiesIndex(Index value) {
    m_NonConformitiesIndex = value;
    reset();
  }

  /**
   * Returns the matrix column index fot he nonconformities.
   *
   * @return		the index
   */
  public Index getNonConformitiesIndex() {
    return m_NonConformitiesIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nonConformitiesIndexTipText() {
    return "The index of the matrix column containing the non-conformities.";
  }

  /**
   * Sets the matrix column index for the sizes.
   *
   * @param value	the index
   */
  public void setSizesIndex(Index value) {
    m_SizesIndex = value;
    reset();
  }

  /**
   * Returns the matrix column index fot he sizes.
   *
   * @return		the index
   */
  public Index getSizesIndex() {
    return m_SizesIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizesIndexTipText() {
    return "The index of the matrix column containing the sample sizes.";
  }

  /**
   * Returns the chart name.
   *
   * @return		the chart name
   */
  public String getName() {
    return "u chart";
  }

  /**
   * Extracts the samples from the matrix (column 0).
   *
   * @param data	the matrix
   * @return		the samples
   */
  protected Number[] getSamples(Number[][] data) {
    Number[] 	result;
    int		i;

    m_SizesIndex.setMax(data[0].length);
    result = new Number[data.length];
    for (i = 0; i < data.length; i++)
      result[i] = data[i][m_SizesIndex.getIntIndex()];

    return result;
  }

  /**
   * Extracts the nonconformities from the matrix (column 1).
   *
   * @param data	the matrix
   * @return		the samples
   */
  protected Number[] getSizes(Number[][] data) {
    Number[] 	result;
    int		i;

    m_NonConformitiesIndex.setMax(data[0].length);
    result = new Number[data.length];
    for (i = 0; i < data.length; i++)
      result[i] = data[i][m_NonConformitiesIndex.getIntIndex()];

    return result;
  }

  /**
   * Calculates the center/lower/upper limit.
   *
   * @param data	the data to use for the calculation
   * @return		the limits
   */
  @Override
  public List<Limits> calculate(Number[][] data) {
    List<Limits>	result;

    result = new ArrayList<>();
    for (double[] stats: SPCUtils.stats_u(getSamples(data), getSizes(data)))
      result.add(new Limits(stats));

    return result;
  }

  /**
   * Prepares the data.
   *
   * @param data	the data to prepare
   * @return		the processed data
   */
  @Override
  public double[] prepare(Number[][] data) {
    return SPCUtils.prepare_data_u(getSamples(data), getSizes(data));
  }
}
