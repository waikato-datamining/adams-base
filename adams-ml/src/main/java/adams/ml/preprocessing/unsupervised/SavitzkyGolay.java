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
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing.unsupervised;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.spreadsheet.Row;
import adams.ml.capabilities.Capabilities;
import adams.ml.capabilities.Capability;
import adams.ml.data.Dataset;
import adams.ml.data.DefaultDataset;
import adams.ml.preprocessing.AbstractStreamFilter;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SavitzkyGolay
  extends AbstractStreamFilter
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 5753905967950878654L;

  /** the polynomial order. */
  protected int m_PolynomialOrder;

  /** the order of the derivative. */
  protected int m_DerivativeOrder;

  /** the number of points to the left of a data point. */
  protected int m_NumPointsLeft;

  /** the number of points to the right of a data point. */
  protected int m_NumPointsRight;

  /** the calculated coefficients. */
  protected double[] m_Coefficients;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A filter that applies Savitzky-Golay smoothing.\n\n"
	+ "For more information see:\n\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return adams.data.utils.SavitzkyGolay.getTechnicalInformation();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Coefficients = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "polynomial", "polynomialOrder",
      2, 2, null);

    m_OptionManager.add(
      "derivative", "derivativeOrder",
      1, 0, null);

    m_OptionManager.add(
      "left", "numPointsLeft",
      3, 0, null);

    m_OptionManager.add(
      "right", "numPointsRight",
      3, 0, null);
  }

  /**
   * Resets the coefficients.
   */
  public void resetCoefficients() {
    m_Coefficients = null;
  }

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setPolynomialOrder(int value) {
    if (value >= 2) {
      m_PolynomialOrder = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	"The polynomial order must be at least 2 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
   */
  public int getPolynomialOrder() {
    return m_PolynomialOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String polynomialOrderTipText() {
    return "The polynomial order to use, must be at least 2.";
  }

  /**
   * Sets the order of the derivative.
   *
   * @param value 	the order
   */
  public void setDerivativeOrder(int value) {
    if (value >= 0) {
      m_DerivativeOrder = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	"The order of the derivative must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the order of the derivative.
   *
   * @return 		the order
   */
  public int getDerivativeOrder() {
    return m_DerivativeOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String derivativeOrderTipText() {
    return "The order of the derivative to use, >= 0.";
  }

  /**
   * Sets the number of points to the left of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsLeft(int value) {
    if (value >= 0) {
      m_NumPointsLeft = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	"The number of points to the left must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the number of points to the left of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsLeft() {
    return m_NumPointsLeft;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsLeftTipText() {
    return "The number of points left of a data point, >= 0.";
  }

  /**
   * Sets the number of points to the right of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsRight(int value) {
    if (value >= 0) {
      m_NumPointsRight = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	"The number of points to the right must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the number of points to the right of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsRight() {
    return m_NumPointsRight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsRightTipText() {
    return "The number of points right of a data point, >= 0.";
  }

  /**
   * Returns the capabilities.
   *
   * @return		the capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enable(Capability.NUMERIC_ATTRIBUTE);
    result.enableAllClass();

    return result;
  }

  /**
   * Filter-specific initialization.
   *
   * @param data 	the data to initialize with
   * @throws Exception	if initialization fails
   */
  @Override
  protected void doInitFilter(Row data) throws Exception {
    if (m_DataColumns.size() < m_NumPointsLeft + m_NumPointsRight + 1)
      throw new Exception("Not enough data columns available for window size: "
	+ m_DataColumns.size() + " < " + (m_NumPointsLeft + m_NumPointsRight + 1));

    m_Coefficients = adams.data.utils.SavitzkyGolay.determineCoefficients(
      m_NumPointsLeft, m_NumPointsRight, m_PolynomialOrder, m_DerivativeOrder, isLoggingEnabled());
  }

  /**
   * Initializes the output format.
   *
   * @param data	the output format
   * @throws Exception	if initialization fails
   */
  protected Dataset initOutputFormat(Row data) throws Exception {
    Dataset	result;
    int		i;
    Row		row;

    result = new DefaultDataset();

    row = result.getHeaderRow();
    for (i = 0; i <= m_DataColumns.size() - m_Coefficients.length; i++)
      row.addCell("" + row.getCellCount()).setContentAsString("att" + (i+1));

    appendHeader(data.getOwner(), row, m_OtherColumns);
    appendHeader(data.getOwner(), row, m_ClassColumns);

    return result;
  }

  /**
   * Filters the dataset row coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  @Override
  protected Row doFilter(Row data) throws Exception {
    Row		result;
    int		i;
    int		n;
    int		width;
    double	value;

    result = getOutputFormat().addRow();

    width  = m_NumPointsLeft + m_NumPointsRight + 1;
    for (i = 0; i <= m_DataColumns.size() - width; i++) {
      // apply coefficients to window
      value = 0;
      for (n = 0; n < width; n++)
	value += m_Coefficients[n] * data.getCell(m_DataColumns.get(i + n)).toDouble();

      result.getCell(i).setContent(value);
    }

    appendData(data, result, m_OtherColumns);
    appendData(data, result, m_ClassColumns);

    return result;
  }
}
