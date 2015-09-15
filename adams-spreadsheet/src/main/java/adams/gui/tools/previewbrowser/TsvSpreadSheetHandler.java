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
 * TsvSpreadSheetHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Displays the following spreadsheet types: tsv
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
public class TsvSpreadSheetHandler
  extends AbstractSpreadSheetHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"tsv"};
  }

  /**
   * Reads all the invidivual spreadsheets from the file.
   *
   * @param file	the file to read
   * @return		the spreadsheet objects that were read from the file
   */
  @Override
  protected SpreadSheet[] readAll(File file) {
    CsvSpreadSheetReader	reader;
    SpreadSheet			sheet;

    reader = new CsvSpreadSheetReader();
    reader.setSeparator("\\t");
    sheet = reader.read(file);
    if (sheet != null)
      return new SpreadSheet[]{sheet};
    else
      return new SpreadSheet[0];
  }
}
