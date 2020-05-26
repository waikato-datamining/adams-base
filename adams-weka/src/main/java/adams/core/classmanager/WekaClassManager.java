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
 * WekaClassManager.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.core.classmanager;

import adams.core.logging.CustomLoggingLevelObject;
import weka.core.SerializedObject;
import weka.core.WekaPackageClassLoaderManager;

import java.io.Serializable;
import java.util.logging.Level;

/**
 * Uses WekaPackageClassLoaderManager.forName(...).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaClassManager
  extends CustomLoggingLevelObject
  implements CustomClassManager {

  private static final long serialVersionUID = 9001224189591616594L;

  /**
   * Instantiates the class by its name.
   *
   * @param classname	the name of the class
   * @return		the class
   * @throws Exception	if instantiation fails
   */
  public Class forName(String classname) throws Exception {
    return WekaPackageClassLoaderManager.forName(classname);
  }

  /**
   * Creates a deep copy of the given object (must be serializable!). Returns
   * null in case of an error.
   *
   * @param o		the object to copy
   * @param silent	whether to suppress error output
   * @return		the deep copy
   */
  public Object deepCopy(Object o, boolean silent) {
    Object		result;
    SerializedObject so;

    try {
      so     = new SerializedObject((Serializable) o);
      result = so.getObject();
    }
    catch (Exception e) {
      if (!silent)
        getLogger().log(Level.SEVERE, "Failed to serialize " + o.getClass().getName() + ":", e);
      result = null;
    }

    return result;
  }
}
