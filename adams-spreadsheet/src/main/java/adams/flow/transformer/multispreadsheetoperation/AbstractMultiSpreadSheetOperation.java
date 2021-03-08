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
 * AbstractMultiSpreadSheetOperation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.multispreadsheetoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Abstract base class for operations that require multiple spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <O> the generated output type
 */
public abstract class AbstractMultiSpreadSheetOperation<O>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 1185449853784824033L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the minimum number of sheets that are required for the operation.
   *
   * @return		the number of sheets that are required, <= 0 means no lower limit
   */
  public abstract int minNumSheetsRequired();

  /**
   * Returns the maximum number of sheets that are required for the operation.
   *
   * @return		the number of sheets that are required, <= 0 means no upper limit
   */
  public abstract int maxNumSheetsRequired();

  /**
   * The type of data that is generated.
   *
   * @return		the class
   */
  public abstract Class generates();

  /**
   * Checks the sheets.
   * <br><br>
   * Default implementation only ensures that sheets are present.
   *
   * @param sheets	the sheets to check
   */
  protected void check(SpreadSheet[] sheets) {
    if ((sheets == null) || (sheets.length == 0))
      throw new IllegalStateException("No sheets provided!");

    if (minNumSheetsRequired() > 0) {
      if (sheets.length < minNumSheetsRequired())
	throw new IllegalStateException(
	  "Not enough sheets supplied (min > supplied): " + minNumSheetsRequired() + " > " + sheets.length);
    }

    if (maxNumSheetsRequired() > 0) {
      if (sheets.length > maxNumSheetsRequired())
	throw new IllegalStateException(
	  "Too many sheets supplied (max < supplied): " + maxNumSheetsRequired() + " < " + sheets.length);
    }
  }

  /**
   * Performs the actual processing of the sheets.
   *
   * @param sheets	the containers to process
   * @param errors	for collecting errors
   * @return		the generated data
   */
  protected abstract O doProcess(SpreadSheet[] sheets, MessageCollection errors);

  /**
   * Processes the containers.
   *
   * @param sheets	the containers to process
   * @param errors	for collecting errors
   * @return		the generated data
   */
  public O process(SpreadSheet[] sheets, MessageCollection errors) {
    check(sheets);
    return doProcess(sheets, errors);
  }
}
