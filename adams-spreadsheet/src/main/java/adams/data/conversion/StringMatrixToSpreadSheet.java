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
 * StringMatrixToSpreadSheet.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Converts a string matrix into a SpreadSheet object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-data-row-type &lt;adams.data.spreadsheet.DataRow&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DenseDataRow
 * </pre>
 * 
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-force-string &lt;boolean&gt; (property: forceString)
 * &nbsp;&nbsp;&nbsp;If enabled, the value is set as string, even if it resembles a number.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9589 $
 */
public class StringMatrixToSpreadSheet
  extends AbstractMatrixToSpreadSheet {

  /** for serialization. */
  private static final long serialVersionUID = -2047404866165517428L;

  /** whether to set value as string. */
  protected boolean m_ForceString;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a string matrix into a SpreadSheet object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "force-string", "forceString",
	    false);
  }

  /**
   * Sets whether to force setting the value as string even if it resembles
   * a number.
   *
   * @param value	true if to force string
   */
  public void setForceString(boolean value) {
    m_ForceString = value;
    reset();
  }

  /**
   * Returns whether to force setting the value as string even if it resembles
   * a number.
   *
   * @return		true if string type is enforced
   */
  public boolean getForceString() {
    return m_ForceString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceStringTipText() {
    return "If enabled, the value is set as string, even if it resembles a number.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "forceString", m_ForceString, "force string", "");
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
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
    SpreadSheet		result;
    String[][]		matrix;
    int			i;
    int			n;
    Row			row;

    result = newInstance();
    matrix = (String[][]) getInput();

    // header
    row = result.getHeaderRow();
    for (i = 0; i < matrix[0].length; i++)
      row.addCell("" + (i+1)).setContent("col-" + (i+1));

    // data
    for (n = 0; n < matrix.length; n++) {
      row = result.addRow("" + (n+1));
      for (i = 0; i < matrix[0].length; i++) {
	if (m_ForceString)
	  row.addCell("" + (i+1)).setContentAsString(matrix[n][i]);
	else
	  row.addCell("" + (i+1)).setContent(matrix[n][i]);
      }
    }

    return result;
  }
}
