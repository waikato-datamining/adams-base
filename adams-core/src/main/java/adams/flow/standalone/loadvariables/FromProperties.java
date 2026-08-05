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
 * FromProperties.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Variables;

/**
 * Loads the variables from a .props (Java properties) file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromProperties
  extends AbstractFileBasedVariableLoader {

  private static final long serialVersionUID = 6598231918083352699L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads the variables from a .props (Java properties) file.";
  }

  /**
   * Loads the variables.
   *
   * @param errors 	for collecting errors
   * @return 		the variables
   */
  @Override
  protected Variables doLoadVariables(MessageCollection errors) {
    Variables		result;
    Properties		props;

    result = new Variables();

    props  = new Properties();
    if (!props.load(m_InputFile.getAbsolutePath())) {
      errors.add("Failed to load properties file: " + m_InputFile);
    }
    else {
      for (String key: props.keySetAll()) {
	if (Variables.isValidName(key))
	  result.set(key, props.getProperty(key));
	else
	  getLogger().warning("Skipping invalid variable name: " + key);
      }
    }

    return result;
  }
}
