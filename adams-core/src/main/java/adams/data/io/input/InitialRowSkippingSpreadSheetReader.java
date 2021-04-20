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
 * InitialRowSkippingSpreadSheetReader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

/**
 * Interface for spreadsheet readers that can skip initial rows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface InitialRowSkippingSpreadSheetReader
  extends SpreadSheetReader {

  /**
   * Sets the number of initial rows to skip.
   *
   * @param value	the number of rows
   */
  public void setSkipNumRows(int value);

  /**
   * Returns the number of initial rows to skip.
   *
   * @return		the number of rows
   */
  public int getSkipNumRows();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipNumRowsTipText();
}
