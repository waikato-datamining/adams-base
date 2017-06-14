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
 * Normalize.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.filter;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Normalizes numeric columns to the specified lower and upper bound.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-range &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: range)
 * &nbsp;&nbsp;&nbsp;The column range to normalize.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-lower &lt;double&gt; (property: lower)
 * &nbsp;&nbsp;&nbsp;The lower bound to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-upper &lt;double&gt; (property: upper)
 * &nbsp;&nbsp;&nbsp;The upper bound to use.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Normalize
  extends AbstractTrainableSpreadSheetFilter {

  private static final long serialVersionUID = 5377534668839214763L;

  /** the column range to normalize. */
  protected SpreadSheetColumnRange m_Range;

  /** the lower bound. */
  protected double m_Lower;

  /** the upper bound. */
  protected double m_Upper;

  /** the indices of the columns to process. */
  protected int[] m_Indices;

  /** whether a column is numeric. */
  protected boolean[] m_Numeric;

  /** the minimum value for a column. */
  protected double[] m_Min;

  /** the maximum value for a column. */
  protected double[] m_Max;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Normalizes numeric columns to the specified lower and upper bound.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "range", "range",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));

    m_OptionManager.add(
      "lower", "lower",
      0.0);

    m_OptionManager.add(
      "upper", "upper",
      1.0);
  }

  /**
   * Sets the range of columns to process.
   *
   * @param value the range
   */
  public void setRange(SpreadSheetColumnRange value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the range of columns to process.
   *
   * @return the range
   */
  public SpreadSheetColumnRange getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The column range to normalize.";
  }

  /**
   * Sets the lower bound to use.
   *
   * @param value the bound
   */
  public void setLower(double value) {
    m_Lower = value;
    reset();
  }

  /**
   * Returns the lower bound in use.
   *
   * @return the bound
   */
  public double getLower() {
    return m_Lower;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String lowerTipText() {
    return "The lower bound to use.";
  }

  /**
   * Sets the upper bound to use.
   *
   * @param value the bound
   */
  public void setUpper(double value) {
    m_Upper = value;
    reset();
  }

  /**
   * Returns the upper bound in use.
   *
   * @return the bound
   */
  public double getUpper() {
    return m_Upper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String upperTipText() {
    return "The upper bound to use.";
  }

  /**
   * Hook method for checks (training data).
   *
   * @param data	the spreadsheet to filter
   * @return		the filtered spreadsheet
   */
  @Override
  protected String checkTrain(SpreadSheet data) {
    String	result;

    result = super.checkTrain(data);

    if (result == null) {
      if (m_Upper <= m_Lower)
	result = "Upper bound must be larger than lower one: lower=" + m_Lower + ", upper=" + m_Upper;
    }

    return result;
  }

  /**
   * Performs the actual retraining on the spreadsheet.
   *
   * @param data	the spreadsheet to train with and filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  @Override
  protected SpreadSheet doTrain(SpreadSheet data) throws Exception {
    Cell 	cell;
    double 	value;

    m_Range.setData(data);
    m_Indices = m_Range.getIntIndices();

    // find numeric columns
    m_Numeric = new boolean[data.getColumnCount()];
    Arrays.fill(m_Numeric, false);
    for (int index : m_Indices) {
      if (data.isNumeric(index))
	m_Numeric[index] = true;
    }

    // determine the ranges
    m_Min = new double[data.getColumnCount()];
    m_Max = new double[data.getColumnCount()];
    Arrays.fill(m_Min, Double.POSITIVE_INFINITY);
    Arrays.fill(m_Max, Double.NEGATIVE_INFINITY);
    for (Row row : data.rows()) {
      for (int index : m_Indices) {
	if (m_Numeric[index]) {
	  if (row.hasCell(index)) {
	    cell = row.getCell(index);
	    if (!cell.isMissing()) {
	      value = cell.toDouble();
	      m_Min[index] = Math.min(m_Min[index], value);
	      m_Max[index] = Math.max(m_Max[index], value);
	    }
	  }
	}
      }
    }

    // failed to determine range?
    for (int index : m_Indices) {
      if (m_Numeric[index]) {
	if (Double.isInfinite(m_Min[index]) || Double.isInfinite(m_Max[index]))
	  m_Numeric[index] = false;
	else if (m_Min[index] == m_Max[index])
	  m_Numeric[index] = false;
      }
    }

    // normalize
    return doFilter(data);
  }

  /**
   * Performs the actual filtering of the spreadsheet.
   *
   * @param data the spreadsheet to filter
   * @throws Exception if filtering fails
   * @return the filtered spreadsheet
   */
  @Override
  protected SpreadSheet doFilter(SpreadSheet data) throws Exception {
    SpreadSheet		result;
    Cell		cell;

    result = data.getClone();

    for (Row row : result.rows()) {
      for (int index : m_Indices) {
	if (m_Numeric[index]) {
	  if (row.hasCell(index)) {
	    cell = row.getCell(index);
	    if (!cell.isMissing()) {
	      cell.setContent(
		(cell.toDouble() - m_Min[index]) / (m_Max[index] - m_Min[index]) * (m_Upper - m_Lower) + m_Lower);
	    }
	  }
	}
      }
    }

    return result;
  }
}
