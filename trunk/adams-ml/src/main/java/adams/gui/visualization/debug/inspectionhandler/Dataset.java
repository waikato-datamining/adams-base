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
 * Dataset.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import java.util.Hashtable;

import adams.core.ClassLocator;

/**
 * Provides further insight into spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Dataset
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(adams.ml.data.Dataset.class, cls);
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
    adams.ml.data.Dataset	data;
    int[]			indices;
    StringBuilder		classes;

    result = new Hashtable<String,Object>();

    if (obj instanceof adams.ml.data.Dataset) {
      data = (adams.ml.data.Dataset) obj;
      result.put("Dataset.Full",    data.toString());
      result.put("Dataset.Header",  data.getHeader().toString());
      result.put("Dataset.Rows",    data.getRowCount());
      result.put("Dataset.Columns", data.getColumnCount());
      indices = data.getClassAttributeIndices();
      result.put("Dataset.NumClasses", indices.length);
      classes = new StringBuilder();
      for (int index: indices) {
	if (classes.length() > 0)
	  classes.append(", ");
	classes.append(index+1);
	classes.append(":");
	classes.append(data.getColumnName(index));
      }
      result.put("Dataset.Classes", classes.toString());
    }

    return result;
  }
}
