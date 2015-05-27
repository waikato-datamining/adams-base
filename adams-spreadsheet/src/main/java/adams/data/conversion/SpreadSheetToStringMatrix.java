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
 * SpreadSheetToStringMatrix.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into a string matrix.
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6556 $
 */
public class SpreadSheetToStringMatrix
  extends AbstractSpreadSheetToMatrix {

  /** for serialization. */
  private static final long serialVersionUID = 4117708470154504868L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a spreadsheet into a string matrix.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return String[][].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String[][]		result;
    SpreadSheet		sheet;
    int			i;
    int			n;
    Row			row;
    
    sheet  = (SpreadSheet) m_Input;
    result = new String[sheet.getRowCount()][sheet.getColumnCount()];
    for (n = 0; n < sheet.getRowCount(); n++) {
      row = sheet.getRow(n);
      for (i = 0; i < sheet.getColumnCount(); i++) {
	if (!row.hasCell(i))
	  result[n][i] = "";
	else
	  result[n][i] = row.getCell(i).getContent();
      }
    }
    
    return result;
  }
}
