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
 * Distinct.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.rowstatistic;

import gnu.trove.set.hash.TDoubleHashSet;

import java.util.HashSet;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Counts the distinct numeric&#47;string values.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class Distinct
  extends AbstractRowStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 4899075284716702404L;
  
  /** for counting the distinct the numeric values. */
  protected TDoubleHashSet m_Numbers;
  
  /** for counting the distinct the labels. */
  protected HashSet<String> m_Labels;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts the distinct numeric/string values.";
  }

  /**
   * Performs initialization before the cells are being visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   */
  @Override
  protected void preVisit(SpreadSheet sheet, int rowIndex) {
    m_Numbers = new TDoubleHashSet();
    m_Labels  = new HashSet<String>();
  }

  /**
   * Gets called with every row in the spreadsheet for generating the stats.
   * 
   * @param row		the current row
   * @param colIndex	the column index
   */
  @Override
  protected void doVisit(Row row, int colIndex) {
    Cell	cell;
    
    if (row.hasCell(colIndex)) {
      cell = row.getCell(colIndex);
      if (cell.isNumeric())
	m_Numbers.add(cell.toDouble());
      else if (!cell.isMissing())
	m_Labels.add(cell.getContent());
    }
  }

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   * @return		the generated stats
   */
  @Override
  protected SpreadSheet postVisit(SpreadSheet sheet, int rowIndex) {
    SpreadSheet	result;
    Row		row;

    result = createOutputHeader();

    if (m_Numbers.size() > 0) {
      row = result.addRow();
      row.addCell(0).setContent("Distinct numbers");
      row.addCell(1).setContent(m_Numbers.size());
    }
    if (m_Labels.size() > 0) {
      row = result.addRow();
      row.addCell(0).setContent("Distinct labels");
      row.addCell(1).setContent(m_Labels.size());
    }

    m_Numbers = null;
    m_Labels  = null;
    
    return result;
  }
}
