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
 * RowStatistic.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import adams.core.Index;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.rowstatistic.AbstractRowStatistic;
import adams.data.spreadsheet.rowstatistic.Mean;

/**
 <!-- globalinfo-start -->
 * Applies the specified row statistic algorithm and returns the specified cell's value as score.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-statistic &lt;adams.data.spreadsheet.rowstatistic.AbstractRowStatistic&gt; (property: statistic)
 * &nbsp;&nbsp;&nbsp;The row statistic to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowstatistic.Mean
 * </pre>
 * 
 * <pre>-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column of the cell from the row statistic's output to use as score.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Index&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row of the cell from the row statistic's output to use as score.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RowStatistic
  extends AbstractRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -9037884201569670797L;

  /** the row statistic to use. */
  protected AbstractRowStatistic m_Statistic;
  
  /** the column of the cell to pick from the stats' output. */
  protected SpreadSheetColumnIndex m_Column;
  
  /** the row of the cell to pick from the stats' output. */
  protected Index m_Row;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies the specified row statistic algorithm and returns the "
	+ "specified cell's value as score.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "statistic", "statistic",
	    new Mean());

    m_OptionManager.add(
	    "column", "column",
	    new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
	    "row", "row",
	    new Index("1"));
  }

  /**
   * Sets the row statistic to use.
   *
   * @param value	the statistic
   */
  public void setStatistic(AbstractRowStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the row statistic in use.
   *
   * @return		the statistic
   */
  public AbstractRowStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The row statistic to use.";
  }

  /**
   * Sets the cell's column to get the score from.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the cell's column to get the score from.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column of the cell from the row statistic's output to use as score.";
  }

  /**
   * Sets the cell's row to get the score from.
   *
   * @param value	the row
   */
  public void setRow(Index value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the cell's row to get the score from.
   *
   * @return		the row
   */
  public Index getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The row of the cell from the row statistic's output to use as score.";
  }

  /**
   * Returns how many score values will get generated.
   * 
   * @return		the number of scores
   */
  @Override
  public int getNumScores() {
    return 1;
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
    SpreadSheet	stats;
    int		col;
    int		row;
    Cell	cell;
    
    result = null;
    
    stats = m_Statistic.generate(sheet, rowIndex);
    if (stats == null) {
      m_LastError = "No statistics generated";
      if (m_Statistic.hasLastError())
	m_LastError += ": "+ m_Statistic.getLastError();
    }
    else {
      m_Column.setData(stats);
      col = m_Column.getIntIndex();
      m_Row.setMax(stats.getRowCount());
      row = m_Row.getIntIndex();
      if (col == -1) {
	m_LastError = "Failed to locate column: " + m_Column;
      }
      else if (row == -1) {
	m_LastError = "Failed to locate row: " + m_Row;
      }
      else {
	cell = stats.getCell(row, col);
	if (cell == null)
	  m_LastError = "Statistics output didn't have cell at " + m_Row + "/" + m_Column + "!";
	else if (!cell.isNumeric())
	  m_LastError = "Cell at " + m_Row + "/" + m_Column + " is not numeric!";
	else
	  result = new Double[]{cell.toDouble()};
      }
    }
    
    return result;
  }
}
