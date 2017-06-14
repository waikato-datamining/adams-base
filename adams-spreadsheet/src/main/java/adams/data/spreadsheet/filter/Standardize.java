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
 * Standardize.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.filter;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.StatUtils;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Standardizes numeric columns to mean 0.0 and standard deviation 1.0.
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
 * &nbsp;&nbsp;&nbsp;The column range to standardize.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Standardize
  extends AbstractTrainableSpreadSheetFilter {

  private static final long serialVersionUID = 5377534668839214763L;

  /** the column range to normalize. */
  protected SpreadSheetColumnRange m_Range;

  /** the indices of the columns to process. */
  protected int[] m_Indices;

  /** whether a column is numeric. */
  protected boolean[] m_Numeric;

  /** the means. */
  protected double[] m_Means;

  /** the stddevs. */
  protected double[] m_StdDevs;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Standardizes numeric columns to mean 0.0 and standard deviation 1.0.";
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
  }

  /**
   * Sets the range of columns to process.
   *
   * @param value 	the range
   */
  public void setRange(SpreadSheetColumnRange value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the range of columns to process.
   *
   * @return 		the range
   */
  public SpreadSheetColumnRange getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The column range to standardize.";
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
    double[] 		allValues;
    TDoubleList		values;

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
    m_Means   = new double[data.getColumnCount()];
    m_StdDevs = new double[data.getColumnCount()];
    Arrays.fill(m_Means,   Double.NaN);
    Arrays.fill(m_StdDevs, Double.NaN);
    values    = new TDoubleArrayList();
    for (int index : m_Indices) {
      if (m_Numeric[index]) {
	allValues = SpreadSheetUtils.getNumericColumn(data, index);
	values.clear();
	for (double v: allValues) {
	  if (!Double.isNaN(v))
	    values.add(v);
	}
	m_Means[index]   = StatUtils.mean(values.toArray());
	m_StdDevs[index] = StatUtils.stddev(values.toArray(), true);
	if (Double.isNaN(m_Means[index]))
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
	      if (m_StdDevs[index] == 0) {
		cell.setContent(
		  (cell.toDouble() - m_Means[index]));
	      }
	      else {
		cell.setContent(
		  (cell.toDouble() - m_Means[index]) / m_StdDevs[index]);
	      }
	    }
	  }
	}
      }
    }

    return result;
  }
}
