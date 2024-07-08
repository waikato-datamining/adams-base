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
 * DataContainerWithSpreadSheetSupport.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

import java.util.List;

/**
 * Data containers that can turn themselves into spreadsheets.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface DataContainerWithSpreadSheetSupport<T extends DataPoint>
  extends DataContainer<T>, SpreadSheetSupporter {

  /**
   * Returns the list of points as spreadsheet.
   *
   * @param points 	the points to convert
   * @return		the content
   */
  public SpreadSheet toSpreadSheet(List<T> points);
}
