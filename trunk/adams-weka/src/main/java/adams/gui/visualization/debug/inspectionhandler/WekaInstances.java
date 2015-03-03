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
 * WekaInstances.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import java.util.Hashtable;

import weka.core.Instance;
import weka.core.Instances;
import adams.core.ClassLocator;

/**
 * Provides further insight into {@link Instance} and {@link Instances} objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstances
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Instance.class, cls) || ClassLocator.isSubclass(Instances.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		the named inspected values
   */
  @Override
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>	result;
    Instances			data;
    Instance			inst;

    result = new Hashtable<String,Object>();

    if (obj instanceof Instances) {
      data = (Instances) obj;
      inst = null;
    }
    else {
      inst = (Instance) obj;
      data = inst.dataset();
    }
    
    result.put("num attributes", data.numAttributes());
    result.put("class attribute", (data.classIndex() == -1) ? "-none-" : ((data.classIndex()+1) + " (" + data.classAttribute().name() + ")"));
    if (inst == null) {
      result.put("num instances", data.numInstances());
      result.put("instances", data.toArray());
    }

    return result;
  }
}
