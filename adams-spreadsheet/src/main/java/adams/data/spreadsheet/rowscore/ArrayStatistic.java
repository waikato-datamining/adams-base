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
 * ArrayStatistic.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

import adams.core.Index;
import adams.core.Range;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.ArrayMean;

/**
 <!-- globalinfo-start -->
 * Applies the specified array statistic algorithm and returns the specified cell's value as score.<br/>
 * NB: Only collects numeric values from the rows.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-statistic &lt;adams.data.statistics.AbstractArrayStatistic&gt; (property: statistic)
 * &nbsp;&nbsp;&nbsp;The array statistic to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.statistics.ArrayMean
 * </pre>
 * 
 * <pre>-additional-rows &lt;adams.core.Range&gt; (property: additionalRows)
 * &nbsp;&nbsp;&nbsp;The additional rows to feed into the array statistic.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column of the cell from the row statistic's output to use as score.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Index&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row of the cell from the row statistic's output to use as score.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayStatistic
  extends AbstractRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -9037884201569670797L;

  /** the row statistic to use. */
  protected AbstractArrayStatistic m_Statistic;
  
  /** the additional rows to use for the array statistic. */
  protected Range m_AdditionalRows;
  
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
	"Applies the specified array statistic algorithm and returns the "
	+ "specified cell's value as score.\n"
	+ "NB: Only collects numeric values from the rows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "statistic", "statistic",
	    new ArrayMean());

    m_OptionManager.add(
	    "additional-rows", "additionalRows",
	    new Range());

    m_OptionManager.add(
	    "column", "column",
	    new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
	    "row", "row",
	    new Index(Index.FIRST));
  }

  /**
   * Sets the row statistic to use.
   *
   * @param value	the statistic
   */
  public void setStatistic(AbstractArrayStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the row statistic in use.
   *
   * @return		the statistic
   */
  public AbstractArrayStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The array statistic to use.";
  }

  /**
   * Sets the additional rows to feed into the array statistic.
   *
   * @param value	the rows
   */
  public void setAdditionalRows(Range value) {
    m_AdditionalRows = value;
    reset();
  }

  /**
   * Returns the additional rows to feed into the array statistic.
   *
   * @return		the rows
   */
  public Range getAdditionalRows() {
    return m_AdditionalRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalRowsTipText() {
    return "The additional rows to feed into the array statistic.";
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
   * Performs the actual calculation of the row score.
   *
   * @param sheet	the spreadsheet to generate the score for
   * @param rowIndex	the row index
   * @return		the generated score, null in case of an error
   */
  @Override
  protected Double doCalculateScore(SpreadSheet sheet, int rowIndex) {
    Double		result;
    SpreadSheet		stats;
    int			col;
    int			row;
    Cell		cell;
    TIntArrayList	rows;
    int			i;
    int			n;
    List<Double>	values;
    
    result = null;
    
    // determine indices for array statistic
    rows = new TIntArrayList();
    if (!m_AdditionalRows.isEmpty()) {
      m_AdditionalRows.setMax(sheet.getRowCount());
      rows.addAll(m_AdditionalRows.getIntIndices());
      rows.remove(rowIndex);
    }
    rows.insert(0, rowIndex);
    
    // generate stats
    values = new ArrayList<Double>();
    for (i = 0; i < rows.size(); i++) {
      values.clear();
      for (n = 0; n < sheet.getColumnCount(); n++) {
	cell = sheet.getCell(rows.get(i), n);
	if ((cell != null) && cell.isNumeric())
	  values.add(cell.toDouble());
      }
      m_Statistic.add(values.toArray(new Double[values.size()]));
    }
    stats = m_Statistic.calculate().toSpreadSheet();
    
    // get statistic
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
	result = cell.toDouble();
    }
    
    return result;
  }
}
