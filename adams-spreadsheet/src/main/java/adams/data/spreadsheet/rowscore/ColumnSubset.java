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
 * ColumnSubset.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetView;

/**
 <!-- globalinfo-start -->
 * Applies the specified base row score algorithm to the specified subset of columns and returns the calculated score.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row-score &lt;adams.data.spreadsheet.rowscore.AbstractRowScore&gt; (property: rowScore)
 * &nbsp;&nbsp;&nbsp;The row score algorithm to apply to the column subset.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowscore.ArrayStatistic -statistic adams.data.statistics.ArrayMean
 * </pre>
 * 
 * <pre>-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The range of columns to apply the specified row score algorithm to.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ColumnSubset
  extends AbstractMetaRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -9037884201569670797L;
  
  /** the subset of columns to use. */
  protected SpreadSheetColumnRange m_Columns;
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies the specified base row score algorithm to the specified "
	+ "subset of columns and returns the calculated score.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "columns", "columns",
	    new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String rowScoreTipText() {
    return "The row score algorithm to apply to the column subset.";
  }

  /**
   * Sets the range of columns to apply the row score algorithm to.
   *
   * @param value	the range
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the range of columns to apply the row score algorithm to.
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The range of columns to apply the specified row score algorithm to.";
  }

  /**
   * Performs the actual calculation of the row score.
   *
   * @param sheet	the spreadsheet to generate the score for
   * @param rowIndex	the row index
   * @return		the generated score, null in case of an error
   */
  @Override
  protected Double[] doCalculateScore(SpreadSheet sheet, int rowIndex) {
    Double[]	result;
    SpreadSheet	subset;
    
    result = null;

    // create view
    m_Columns.setData(sheet);
    subset = new SpreadSheetView(sheet, null, m_Columns.getIntIndices());

    // calc score(s)
    result = m_RowScore.calculateScore(subset, rowIndex);
    
    return result;
  }
}
