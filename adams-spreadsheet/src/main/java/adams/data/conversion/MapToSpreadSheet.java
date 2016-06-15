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
 * MapToSpreadSheet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns a java.util.Map object into a SpreadSheet object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MapToSpreadSheet
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 1987126684684537405L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a " + Map.class.getName() + " object into a SpreadSheet object.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Map.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		result;
    Map 		map;
    Row			row;

    map = (Map) m_Input;
    result = new DefaultSpreadSheet();
    
    // header
    row = result.getHeaderRow();
    row.addCell("K").setContentAsString("Key");
    row.addCell("V").setContentAsString("Value");
    
    // data
    for (Object key: map.keySet()) {
      row = result.addRow();
      row.addCell("K").setNative(key);
      row.addCell("V").setNative(map.get(key));
    }
    
    return result;
  }
}
