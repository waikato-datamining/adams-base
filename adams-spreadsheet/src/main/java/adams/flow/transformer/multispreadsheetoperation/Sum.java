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
 * Sum.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.multispreadsheetoperation;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Computes the sum of the numeric cells between two spreadsheets.<br>
 * The values of the second spreadsheet are added to the first one.<br>
 * If no 'key' columns are defined, the current order of rows is used for comparison.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-key-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: keyColumns)
 * &nbsp;&nbsp;&nbsp;The columns to use as keys for identifying rows in the spreadsheets, if
 * &nbsp;&nbsp;&nbsp;empty the row index is used instead; A range is a comma-separated list of
 * &nbsp;&nbsp;&nbsp;single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)'
 * &nbsp;&nbsp;&nbsp; inverts the range '...'; column names (case-sensitive) as well as the following
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last; numeric
 * &nbsp;&nbsp;&nbsp;indices can be enforced by preceding them with '#' (eg '#12'); column names
 * &nbsp;&nbsp;&nbsp;can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Sum
  extends AbstractIndentifiableRowOperation {

  /** for serialization. */
  private static final long serialVersionUID = -5056170789277731638L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Computes the sum of the numeric cells between two spreadsheets.\n"
        + "The values of the second spreadsheet are added to the first one.\n"
        + "If no 'key' columns are defined, the current order of rows is used "
        + "for comparison.";
  }

  /**
   * Computes the difference between the two rows: actual difference is
   * computed for numeric cells. If cells are strings, then the results is
   * a missing value in case of differing strings, otherwise the same.
   *
   * @param output	the spreadsheet the new row will get added to
   * @param row1	the row from the first sheet
   * @param row2	the row from the second sheet
   * @return		the generated difference
   */
  protected Row performOperation(SpreadSheet output, Row row1, Row row2) {
    Row			result;
    Cell		cell1;
    Cell		cell2;
    int			index;
    Set<Integer> 	indices;

    result = row1.getClone(output);
    result.clear();

    indices = new HashSet<>();
    for (int i: m_ColIndices)
      indices.add(m_ColIndices[i]);

    for (String key: row1.cellKeys()) {
      index = row1.getOwner().getHeaderRow().indexOf(key);
      cell1 = row1.getCell(key);
      cell2 = row2.getCell(key);
      if (indices.contains(index)) {
        result.addCell(key).setContent(cell1.getContent());
      }
      else if ((cell1 == null) || (cell2 == null)) {
        result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
      else if (cell1.isMissing() || cell2.isMissing()) {
        result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
      else if (cell1.isNumeric() && cell2.isNumeric()){
        result.addCell(key).setContent(cell1.toDouble() + cell2.toDouble());
      }
      else {
        if (cell1.getContent().equals(cell2.getContent()))
          result.addCell(key).setContent(cell1.getContent());
        else
          result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
    }

    return result;
  }
}
