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
 * MatlabSpreadSheetReader.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import weka.core.converters.AbstractFileLoader;
import weka.core.converters.MatlabLoader;
import adams.data.io.output.MatlabSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;

/**
 <!-- globalinfo-start -->
 * Reads WEKA datasets in ARFF format and turns them into spreadsheets.
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
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: ?
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
public class MatlabSpreadSheetReader
  extends AbstractWekaSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = 4421664099462099419L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads WEKA datasets in Matlab ASCII format (containing single matrix) and turns them into spreadsheets.";
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new MatlabSpreadSheetWriter();
  }

  /**
   * Returns an instance of the file loader.
   * 
   * @return		the file loader
   */
  @Override
  protected AbstractFileLoader newLoader() {
    return new MatlabLoader();
  }
}
