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
 * PassThrough.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.multispreadsheetoperation;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Dummy, just passes through the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractMultiSpreadSheetOperation<SpreadSheet[]> {

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, just passes through the data.";
  }

  private static final long serialVersionUID = 5831884654010979232L;

  /**
   * Returns the minimum number of sheets that are required for the operation.
   *
   * @return the number of sheets that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumSheetsRequired() {
    return -1;
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
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SpreadSheet[].class;
  }

  /**
   * Performs the actual processing of the sheets.
   *
   * @param sheets 	the containers to process
   * @param errors	for collecting errors
   * @return 		the generated data
   */
  @Override
  protected SpreadSheet[] doProcess(SpreadSheet[] sheets, MessageCollection errors) {
    return sheets;
  }
}
