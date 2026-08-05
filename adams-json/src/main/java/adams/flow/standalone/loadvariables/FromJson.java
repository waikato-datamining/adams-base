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
 * FromJson.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.Variables;
import adams.data.json.JsonHelper;

import java.util.Map;

/**
 * Loads the variables from a JSON file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromJson
  extends AbstractFileBasedVariableLoader {

  private static final long serialVersionUID = 718591767635636059L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads the variables from a JSON file.";
  }

  /**
   * Loads the variables.
   *
   * @param errors for collecting errors
   * @return the variables
   */
  @Override
  protected Variables doLoadVariables(MessageCollection errors) {
    Variables	result;
    Object 	map;
    String	key;

    result = new Variables();
    map    = JsonHelper.parse(m_InputFile, this);
    if (map == null) {
      errors.add("Failed to parse JSON file: " + m_InputFile);
    }
    else {
      if (map instanceof Map) {
	for (Object obj: ((Map) map).keySet()) {
	  key = "" + obj;
	  if (Variables.isValidName(key))
	    result.set(key, "" + ((Map) map).get(obj));
	  else
	    getLogger().warning("Skipping invalid variable name: " + key);
	}
      }
      else {
	errors.add("JSON file did not contain a map object: " + m_InputFile);
      }
    }

    return result;
  }
}
