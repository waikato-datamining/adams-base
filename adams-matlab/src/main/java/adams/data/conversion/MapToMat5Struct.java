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
 * MapToMat5Struct.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Struct;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts a map into a Matlab struct object.<br>
 * Supported nested elements:<br>
 * - java.util.Map<br>
 * - Matlab array&#47;struct<br>
 * - spreadsheet<br>
 * - Double matrix
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
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapToMat5Struct
  extends AbstractConversion {

  private static final long serialVersionUID = 2829495301196516668L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a map into a Matlab struct object.\n"
      + "Supported nested elements:\n"
      + "- " + Utils.classToString(Map.class) + "\n"
      + "- Matlab array/struct\n"
      + "- spreadsheet\n"
      + "- Double matrix";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return Map.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Struct.class;
  }

  /**
   * Converts the object into an array.
   *
   * @param object	the object to convert
   * @return		the generated array
   */
  protected Array convert(Object object) {
    Array			result;
    DoubleMatrixToMat5Array	dm2a;
    SpreadSheetToMat5Array	sp2a;
    String			msg;

    result = null;

    if (object instanceof Array) {
      result = (Array) object;
    }
    else if (object instanceof Map) {
      result = convert((Map) object);
    }
    else if (object instanceof Double[][]) {
      dm2a = new DoubleMatrixToMat5Array();
      dm2a.setInput(object);
      msg = dm2a.convert();
      if (msg == null)
        result = (Array) dm2a.getOutput();
      else
        getLogger().warning(msg);
      dm2a.cleanUp();
    }
    else if (object instanceof SpreadSheet) {
      sp2a = new SpreadSheetToMat5Array();
      sp2a.setInput(object);
      msg = sp2a.convert();
      if (msg == null)
        result = (Array) sp2a.getOutput();
      else
        getLogger().warning(msg);
      sp2a.cleanUp();
    }

    if (result == null)
      getLogger().warning("Cannot convert to Matlab structure: " + Utils.classToString(object));

    return result;
  }

  /**
   * Converts the map into a Matlab struct object.
   *
   * @param map		the map to convert
   * @return		the generated struct
   */
  protected Struct convert(Map map) {
    Struct	result;
    String	key;
    Array	array;

    result = Mat5.newStruct();

    for (Object k: map.keySet()) {
      key   = "" + k;
      array = convert(map.get(k));
      if (array != null)
        result.set(key, array);
      else
        getLogger().warning("Failed to convert map value '" + key + "' into array!");
    }

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    return convert((Map) m_Input);
  }
}
