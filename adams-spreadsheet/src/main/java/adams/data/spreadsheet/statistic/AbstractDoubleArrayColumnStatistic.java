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
 * AbstractDoubleArrayColumnStatistic.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.statistic;

import gnu.trove.list.array.TDoubleArrayList;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for statistics that just use all numeric values in the column.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDoubleArrayColumnStatistic
  extends AbstractColumnStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 9076465449580989452L;
  
  /** for calculating the mean. */
  protected TDoubleArrayList m_Values;

  /**
   * Performs initialization before the cells are being visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   */
  @Override
  protected void preVisit(SpreadSheet sheet, int colIndex) {
    m_Values = new TDoubleArrayList();
  }

  /**
   * Gets called with every row in the spreadsheet for generating the stats.
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
