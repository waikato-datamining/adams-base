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
 * Overlay.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.multispreadsheetoperation;

import adams.core.MessageCollection;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Combines non-empty cells from the spreadsheets into one by overlaying them.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Overlay
  extends AbstractMultiSpreadSheetOperation<SpreadSheet> {

  private static final long serialVersionUID = 5831884654010979232L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines non-empty cells from the spreadsheets into one by overlaying them.\n"
	     + "If the same cell is non-empty for two or more spreadsheets, then the value of the "
	     + "last spreadsheet with a non-empty value will end up as the final value.";
  }

  /**
   * Returns the minimum number of sheets that are required for the operation.
   *
   * @return the number of sheets that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumSheetsRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of sheets that are required for the operation.
   *
   * @return the number of sheets that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumSheetsRequired() {
    return -1;
  }

  /**
   * Checks the sheets.
   *
   * @param sheets	the sheets to check
   */
  @Override
  protected void check(SpreadSheet[] sheets) {
    int		i;

    super.check(sheets);

    for (i = 1; i < sheets.length; i++) {
      if (sheets[i].getRowCount() != sheets[0].getRowCount())
	throw new IllegalStateException("Sheet #" + (i+1) + "has different number of rows to first one: " + sheets[i].getRowCount() + " != " + sheets[0].getRowCount());
      if (sheets[i].getColumnCount() != sheets[0].getColumnCount())
	throw new IllegalStateException("Sheet #" + (i+1) + "has different number of columns to first one: " + sheets[i].getColumnCount() + " != " + sheets[0].getColumnCount());
    }
  }

  /**
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual processing of the sheets.
   *
   * @param sheets 	the containers to process
   * @param errors	for collecting errors
   * @return 		the generated data
   */
  @Override
  protected SpreadSheet doProcess(SpreadSheet[] sheets, MessageCollection errors) {
    SpreadSheet		result;
    int			i;
    int 		r;
    int 		c;
    Row			rowFirst;
    Row			rowCurrent;
    Cell		cellFirst;
    Cell		cellCurrent;

    result = sheets[0].getClone();

    for (r = 0; r < result.getRowCount(); r++) {
      rowFirst = result.getRow(r);
      for (i = 1; i < sheets.length; i++) {
	rowCurrent = sheets[i].getRow(r);
	for (c = 0; c < result.getColumnCount(); c++) {
	  if (!rowCurrent.hasCell(c))
	    continue;
	  cellCurrent = rowCurrent.getCell(c);
	  if (cellCurrent.isMissing() || cellCurrent.getContent().isEmpty())
	    continue;
	  // assign value
	  cellFirst = rowFirst.getCell(c);
	  cellFirst.assign(cellCurrent);
	}
      }
    }

    return result;
  }
}
