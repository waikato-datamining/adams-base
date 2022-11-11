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

import nz.ac.waikato.cms.locator.ClassLocator;
import us.hebi.matlab.mat.types.Struct;

import java.util.Hashtable;

/**
 * For inspecting Matlab Struct objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5Struct
  extends AbstractInspectionHandler{

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls the class to check
   * @return true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Struct.class, cls);
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
    Struct			struct;

    result = new Hashtable<>();

    struct = (Struct) obj;
    for (String name: struct.getFieldNames())
      result.put(name, struct.get(name));

    return result;
  }
}
