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
 * Max.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.matrixstatistic;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Counts the missing cells in the spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The rows of the subset to retrieve.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns of the subset to retrieve; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; column names (case-sensitive) as well as the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp; last; numeric indices can be enforced by preceding them with '#' (eg '#12'
 * &nbsp;&nbsp;&nbsp;); column names can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class Missing
  extends AbstractMatrixStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 330391755072250767L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts the missing cells in the spreadsheet.";
  }

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @return		the generated stats
   */
  @Override
  protected SpreadSheet doGenerate(SpreadSheet sheet) {
    SpreadSheet	result;
    Row		row;
    int		count;
    int		i;
    int		n;

    result = createOutputHeader();

    count = 0;
    for (i = 0; i < sheet.getRowCount(); i++) {
      row = sheet.getRow(i);
      for (n = 0; n < sheet.getColumnCount(); n++) {
	if (!row.hasCell(n))
	  count++;
	else if (row.getCell(n).isMissing())
	  count++;
      }
    }

    row = result.addRow();
    row.addCell(0).setContent("Missing");
    row.addCell(1).setContent(count);

    return result;
  }
}
