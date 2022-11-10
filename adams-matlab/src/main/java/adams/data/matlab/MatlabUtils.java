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
 * MatlabUtils.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.matlab;

import us.hebi.matlab.mat.types.Char;

/**
 * TODO: What this class does.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MatlabUtils {

  /**
   * Converts a character cell into a string. Rows are interpreted as lines.
   *
   * @param matChar	the cell to convert
   * @return		the generated string
   */
  public static String charToString(Char matChar) {
    StringBuilder 	result;
    int			cols;
    int			rows;
    int			x;
    int			y;

    result = new StringBuilder();

    rows = matChar.getNumRows();
    cols = matChar.getNumCols();

    for (y = 0; y < rows; y++) {
      if (y > 0)
	result.append("\n");
      for (x = 0; x < cols; x++)
	result.append(matChar.getChar(y, x));
    }

    return result.toString();
  }
}
