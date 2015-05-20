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
 * ODFSpreadSheetHandler.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Range;
import adams.core.io.FileUtils;
import adams.data.io.input.ODFSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import org.jopendocument.dom.ODPackage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Displays the following spreadsheet types: ods
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
public class ODFSpreadSheetHandler
  extends AbstractSpreadSheetHandler {

  /** for serialization. */
  private static final long serialVersionUID = -57718234789783854L;

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"ods"};
  }

  /**
   * Determines the number of sheets in the spreadsheet file.
   *
   * @param file	the spreadsheet file to check
   * @return		the number of sheets
   */
  protected int getSheetCount(File file) {
    int							result;
    org.jopendocument.dom.spreadsheet.SpreadSheet	spreadsheet;
    BufferedInputStream					input;
    FileInputStream					fis;

    input = null;
    fis   = null;
    try {
      fis         = new FileInputStream(file.getAbsoluteFile());
      input       = new BufferedInputStream(fis);
      spreadsheet = org.jopendocument.dom.spreadsheet.SpreadSheet.get(new ODPackage(input));
      result      = spreadsheet.getSheetCount();
    }
    catch (Exception e) {
      result = 0;
      getLogger().log(Level.SEVERE, "Failed to determine sheet count for '" + file + "':", e);
    }
    finally {
      FileUtils.closeQuietly(input);
      FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * Reads all the invidivual spreadsheets from the file.
   *
   * @param file	the file to read
   * @return		the spreadsheet objects that were read from the file
   */
  @Override
  protected SpreadSheet[] readAll(File file) {
    List<SpreadSheet>		result;
    ODFSpreadSheetReader	reader;

    result = new ArrayList<SpreadSheet>();
    reader = new ODFSpreadSheetReader();
    reader.setSheetRange(new Range(Range.ALL));
    result = reader.readRange(file);

    return result.toArray(new SpreadSheet[result.size()]);
  }
}
