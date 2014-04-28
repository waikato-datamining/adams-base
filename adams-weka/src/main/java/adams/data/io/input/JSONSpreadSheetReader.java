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
 * JSONSpreadSheetReader.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import weka.core.converters.AbstractFileLoader;
import weka.core.converters.JSONLoader;

/**
 <!-- globalinfo-start -->
 * Reads WEKA datasets in JSON format and turns them into spreadsheets.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-data-row-type &lt;DENSE|SPARSE&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: DENSE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JSONSpreadSheetReader
  extends AbstractWekaSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = -399502805573519804L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads WEKA datasets in JSON format and turns them into spreadsheets.";
  }

  /**
   * Returns an instance of the file loader.
   * 
   * @return		the file loader
   */
  @Override
  protected AbstractFileLoader newLoader() {
    return new JSONLoader();
  }
}
