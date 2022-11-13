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
 * Mat5FileToMap.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.MatFile;
import us.hebi.matlab.mat.types.Struct;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns the Mat5File data structure into a nested map.
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
public class Mat5FileToMap
  extends AbstractConversion {

  private static final long serialVersionUID = 448419021229335154L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the Mat5File data structure into a nested map.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return Mat5File.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Adds the Struct recursively.
   *
   * @param map		the map to add to
   * @param struct	the struct to add
   */
  protected void addStruct(Map map, Struct struct) {
    Object	obj;
    Map		submap;

    for (String field: struct.getFieldNames()) {
      obj = struct.get(field);
      if (obj instanceof Struct) {
        submap = new HashMap();
        map.put(field, submap);
        addStruct(submap, (Struct) obj);
      }
      else {
        map.put(field, obj);
      }
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Map		result;
    Map		submap;
    Mat5File	mat5;

    result = new HashMap();
    mat5   = (Mat5File) m_Input;
    for (MatFile.Entry entry: mat5.getEntries()) {
      if (entry.getValue() instanceof Struct) {
        submap = new HashMap();
        result.put(entry.getName(), submap);
        addStruct(submap, (Struct) entry.getValue());
      }
      else {
        result.put(entry.getName(), entry.getValue());
      }
    }

    return result;
  }
}
