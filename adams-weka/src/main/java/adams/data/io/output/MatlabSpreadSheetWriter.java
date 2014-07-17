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
 * MatlabSpreadSheetWriter.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import weka.core.converters.AbstractFileSaver;
import weka.core.converters.MatlabSaver;
import adams.data.io.input.MatlabSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;

/**
 <!-- globalinfo-start -->
 * Writes a spreadsheet in ARFF file format.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatlabSpreadSheetWriter
  extends AbstractWekaSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = -677810919276444654L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes a spreadsheet in Matlab ASCII file format.";
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new MatlabSpreadSheetReader();
  }

  /**
   * Returns an instance of the file loader.
   * 
   * @return		the file loader
   */
  @Override
  protected AbstractFileSaver newSaver() {
    return new MatlabSaver();
  }
}
