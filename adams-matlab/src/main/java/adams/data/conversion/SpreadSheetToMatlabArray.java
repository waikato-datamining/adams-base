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
 * SpreadSheetToMatlabArray.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.spreadsheet.SpreadSheet;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Cell;
import us.hebi.matlab.mat.types.Matrix;

/**
 <!-- globalinfo-start -->
 * Converts a spreadsheet to a Matlab array (either matrix or a cell array)
 * <br><br>
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetToMatlabArray
  extends AbstractConversion {

  private static final long serialVersionUID = 1720918361869379610L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a spreadsheet to a Matlab array (either matrix or a cell array)";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Array.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Object		result;
    SpreadSheet		sheet;
    int			i;
    int			n;
    boolean		numeric;
    Matrix		matrix;
    Cell 		cell;

    sheet = (SpreadSheet) m_Input;

    // only numeric?
    numeric = true;
    for (i = 0; i < sheet.getColumnCount(); i++) {
      if (!sheet.isNumeric(i)) {
        numeric = false;
        break;
      }
    }

    // convert
    if (numeric) {
      matrix = Mat5.newMatrix(new int[]{sheet.getRowCount(), sheet.getColumnCount()});
      for (n = 0; n < sheet.getRowCount(); n++) {
        for (i = 0; i < sheet.getColumnCount(); i++) {
          if (!sheet.hasCell(n, i) || sheet.getCell(n, i).isMissing())
            matrix.setDouble(n, i, Double.NaN);
          else
	    matrix.setDouble(n, i, sheet.getCell(n, i).toDouble());
	}
      }
      result = matrix;
    }
    else {
      cell = Mat5.newCell(new int[]{sheet.getRowCount(), sheet.getColumnCount()});
      for (n = 0; n < sheet.getRowCount(); n++) {
        for (i = 0; i < sheet.getColumnCount(); i++) {
          if (!sheet.hasCell(n, i) || sheet.getCell(n, i).isMissing())
            cell.set(n, i, Mat5.newScalar(Double.NaN));
          else if (sheet.getCell(n, i).isNumeric())
	    cell.set(n, i, Mat5.newScalar(sheet.getCell(n, i).toDouble()));
          else
	    cell.set(n, i, Mat5.newString(sheet.getCell(n, i).getContent()));
	}
      }
      result = cell;
    }

    return result;
  }
}
