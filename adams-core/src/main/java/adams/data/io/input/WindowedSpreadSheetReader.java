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
 * WindowedSpreadSheetReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;



/**
 * Allows the retrieval of a certain "window" of rows from the data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface WindowedSpreadSheetReader
  extends SpreadSheetReader {

  /**
   * Sets the first row to return.
   *
   * @param value	the first row (1-based)
   */
  public void setFirstRow(int value);

  /**
   * Returns the first row to return.
   *
   * @return		the first row (1-based)
   */
  public int getFirstRow();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstRowTipText();

  /**
   * Sets the number of data rows to return.
   *
   * @param value	the number of rows
   */
  public void setNumRows(int value);

  /**
   * Returns the number of data rows to return.
   *
   * @return		the number of rows
   */
  public int getNumRows();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRowsTipText();
}
