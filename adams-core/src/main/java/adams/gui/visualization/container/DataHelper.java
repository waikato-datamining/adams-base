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
 * DataHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetView;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Helper class for data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataHelper {

  /**
   * Filters the data, using the specified ranges.
   *
   * @param data	the data to filter
   * @param xRange	optional limits for X values
   * @param yRange	optional limits for Y values
   * @return		the filtered data
   */
  public static SpreadSheet filter(SpreadSheet data, String xColName, double[] xRange, String yColName, double[] yRange) {
    TIntList rows;
    int 		xCol;
    int 		yCol;
    double		x;
    double		y;
    int			i;
    Row row;

    if ((xRange == null) || (yRange == null))
      return data;

    xCol = data.getHeaderRow().indexOfContent(xColName);
    yCol = data.getHeaderRow().indexOfContent(yColName);
    if ((xCol == -1) || (yCol == -1))
      return data;

    rows = new TIntArrayList();
    for (i = 0; i < data.getRowCount(); i++) {
      row = data.getRow(i);
      if (row.hasCell(xCol) && row.hasCell(yCol)) {
	x = row.getCell(xCol).toDouble();
	y = row.getCell(yCol).toDouble();
	if (Double.isNaN(x) || Double.isNaN(y))
	  continue;
	if ((x < xRange[0]) || (x > xRange[1]))
	  continue;
	if ((y < yRange[0]) || (y > yRange[1]))
	  continue;
	rows.add(i);
      }
    }

    return new SpreadSheetView(data, rows.toArray(), null);
  }
}
