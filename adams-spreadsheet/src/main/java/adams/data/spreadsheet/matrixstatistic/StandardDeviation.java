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
 * StandardDeviation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.matrixstatistic;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Determines the standard deviation of the numeric values in the matrix, skips NaN and infinite values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The rows of the subset to retrieve.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns of the subset to retrieve; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; column names (case-sensitive) as well as the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp; last; numeric indices can be enforced by preceding them with '#' (eg '#12'
 * &nbsp;&nbsp;&nbsp;); column names can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-is-sample &lt;boolean&gt; (property: isSample)
 * &nbsp;&nbsp;&nbsp;If set to true, the data is treated as sample and not as population.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class StandardDeviation
  extends AbstractMatrixStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 330391755072250767L;

  /** whether the data is samples or populations. */
  protected boolean m_IsSample;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Determines the standard deviation of the numeric values in the matrix, skips NaN and infinite values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "is-sample", "isSample",
	    true);
  }

  /**
   * Sets whether the data represents samples instead of populations.
   *
   * @param value	true if data is samples and not populations
   */
  public void setIsSample(boolean value) {
    m_IsSample = value;
    reset();
  }

  /**
   * Returns whether the data represents samples instead of populations.
   *
   * @return		true if data is samples and not populations
   */
  public boolean getIsSample() {
    return m_IsSample;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String isSampleTipText() {
    return "If set to true, the data is treated as sample and not as population.";
  }

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @return		the generated stats
   */
  @Override
  protected SpreadSheet doGenerate(SpreadSheet sheet) {
    SpreadSheet		result;
    Row			row;

    result = createOutputHeader();
    row = result.addRow();
    row.addCell(0).setContent("StdDev" + (getIsSample() ? "" : "P"));
    row.addCell(1).setContent(StatUtils.stddev(getNumericValues(sheet).toArray(), m_IsSample));

    return result;
  }
}
