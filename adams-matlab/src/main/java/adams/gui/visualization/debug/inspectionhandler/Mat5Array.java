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
 * Mat5Struct.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.debug.inspectionhandler;

import adams.data.conversion.Mat5ArrayToSpreadSheet;
import nz.ac.waikato.cms.locator.ClassLocator;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Struct;

import java.util.Hashtable;

/**
 * For inspecting Matlab Array/Struct objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5Array
  extends AbstractInspectionHandler{

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls the class to check
   * @return true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Array.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj the object to further inspect
   * @return the named inspected values
   */
  @Override
  public Hashtable<String, Object> inspect(Object obj) {
    Hashtable<String,Object>	result;
    Object			value;
    Array 			array;
    Struct			struct;
    Mat5ArrayToSpreadSheet conv;
    String			msg;

    result = new Hashtable<>();

    array = (Array) obj;
    if (array instanceof Struct) {
      struct = (Struct) array;
      for (String field: struct.getFieldNames())
	result.put(field, struct.get(field));
    }
    else if (array.getNumDimensions() <= 2) {
      conv = new Mat5ArrayToSpreadSheet();
      conv.setInput(array);
      msg = conv.convert();
      if (msg == null)
	result.put("data", conv.getOutput());
      conv.cleanUp();
    }

    return result;
  }
}
