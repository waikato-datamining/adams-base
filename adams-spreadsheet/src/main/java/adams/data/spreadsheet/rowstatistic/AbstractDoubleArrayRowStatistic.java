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
 * AbstractDoubleArrayRowStatistic.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.rowstatistic;

import gnu.trove.list.array.TDoubleArrayList;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for statistics that just use all numeric values in the row.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public abstract class AbstractDoubleArrayRowStatistic
  extends AbstractRowStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 9076465449580989452L;
  
  /** for calculating the stats. */
  protected TDoubleArrayList m_Values;

  /**
   * Performs initialization before the cells are being visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   */
  @Override
  protected void preVisit(SpreadSheet sheet, int rowIndex) {
    m_Values = new TDoubleArrayList();
  }

  /**
   * Gets called with every cell in the row for generating the stats.
   * 
   * @param row		the current row
   * @param colIndex	the column index
   */
  @Override
  protected void doVisit(Row row, int colIndex) {
    if (row.hasCell(colIndex) && row.getCell(colIndex).isNumeric())
      m_Values.add(row.getCell(colIndex).toDouble());
  }
}
