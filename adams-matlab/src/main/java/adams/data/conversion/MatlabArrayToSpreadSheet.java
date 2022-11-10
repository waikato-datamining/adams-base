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
 * MatlabArrayToSpreadSheet.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Utils;
import adams.data.matlab.MatlabUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import us.hebi.matlab.mat.types.AbstractCharBase;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Cell;
import us.hebi.matlab.mat.types.Char;
import us.hebi.matlab.mat.types.Matrix;

/**
 <!-- globalinfo-start -->
 * Turns the Matlab array into a spreadsheet.
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
public class MatlabArrayToSpreadSheet
  extends AbstractConversion {

  private static final long serialVersionUID = -2006396004849089721L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the Matlab array into a spreadsheet.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return Array.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet			result;
    Row				row;
    Array			array;
    Matrix			matrix;
    Char 			matChar;
    Cell 			matCell;
    adams.data.spreadsheet.Cell	cell;
    int				i;
    int				n;
    String[]			lines;

    array = (Array) m_Input;
    if (array.getNumDimensions() > 2)
      throw new IllegalStateException("Cannot handle arrays with more than two dimensions, received: " + array.getNumDimensions());
    matrix = null;
    if (array instanceof Matrix)
      matrix = (Matrix) array;
    matCell = null;
    if (array instanceof Cell)
      matCell = (Cell) array;
    matChar = null;
    if (array instanceof Char)
      matChar = (Char) array;
    if ((matrix == null) && (matCell == null) && (matChar == null))
      throw new IllegalStateException("Unhandled array type: " + Utils.classToString(array));

    if (matChar != null) {
      lines = MatlabUtils.charToString(matChar).split("\n");
      result = new DefaultSpreadSheet();
      row    = result.getHeaderRow();
      row.addCell("0").setContentAsString("Line");
      for (String line: lines)
        result.addRow().addCell("0").setContentAsString(line);
      return result;
    }

    // header
    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    for (i = 0; i < array.getNumCols(); i++)
      row.addCell("" + i).setContentAsString("col-" + (i+1));

    // data
    for (n = 0; n < array.getNumRows(); n++) {
      row = result.addRow();
      for (i = 0; i < array.getNumCols(); i++) {
	cell = row.addCell(i);
	if (matrix != null) {
	  cell.setContent(matrix.getDouble(n, i));
	}
	else if (matCell != null) {
	  if (matCell.get(n, i) instanceof AbstractCharBase)
	    cell.setContent(MatlabUtils.charToString((AbstractCharBase) matCell.get(n, i)));
	  else
	    cell.setContent(matCell.get(n, i).toString());
	}
	else if (matChar != null) {
	  cell.setContentAsString("" + matChar.getChar(n, i));
	}
      }
    }

    return result;
  }
}
