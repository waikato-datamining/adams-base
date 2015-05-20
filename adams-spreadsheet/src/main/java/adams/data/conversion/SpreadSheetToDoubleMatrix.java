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
 * SpreadSheetToDoubleMatrix.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.ArrayList;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into a double matrix, using only the numeric columns.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToDoubleMatrix
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4117708470154504868L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a spreadsheet into a double matrix, using only the numeric columns.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Double[][].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Double[][]		result;
    SpreadSheet		sheet;
    ArrayList<Integer>	numeric;
    int			i;
    int			n;
    Row			row;
    
    sheet   = (SpreadSheet) m_Input;
    numeric = new ArrayList<Integer>();
    for (i = 0; i < sheet.getColumnCount(); i++) {
      if (sheet.isNumeric(i, true))
	numeric.add(i);
    }
    if (numeric.size() == 0)
      throw new IllegalStateException("No numeric columns in spreadsheet!");
    
    result = new Double[sheet.getRowCount()][numeric.size()];
    for (n = 0; n < sheet.getRowCount(); n++) {
      row = sheet.getRow(n);
      for (i = 0; i < numeric.size(); i++) {
	if (!row.hasCell(numeric.get(i)))
	  result[n][i] = Double.NaN;
	else
	  result[n][i] = row.getCell(numeric.get(i)).toDouble();
      }
    }
    
    return result;
  }
}
