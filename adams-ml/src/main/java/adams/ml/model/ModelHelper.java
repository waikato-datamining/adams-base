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
 * ModelHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model;

import adams.ml.data.Dataset;
import adams.ml.data.DatasetInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ModelHelper {

  /**
   * Checks whether the model is compatible with the dataset.
   *
   * @param model	the model to check against
   * @param data	the data to check
   * @return		null if compatible, otherwise error message
   */
  public static String isCompatible(Model model, Dataset data) {
    String		result;
    DatasetInfo		info;
    Set<String> 	modelClasses;
    Set<String> 	dataClasses;

    info   = model.getDatasetInfo();
    result = info.getHeader().equalsHeader(data);
    if (result == null) {
      modelClasses = info.getClassColumns();
      dataClasses  = new HashSet<>(Arrays.asList(data.getClassAttributeNames()));
      if (modelClasses.size() != dataClasses.size()) {
	result = "Number of class columns differ: " + modelClasses + " != " + dataClasses;
      }
      else {
	if (!(modelClasses.containsAll(dataClasses) && dataClasses.containsAll(modelClasses)))
	  result = "Class column names differ: " + modelClasses + " != " + dataClasses;
      }
    }

    return result;
  }
}
