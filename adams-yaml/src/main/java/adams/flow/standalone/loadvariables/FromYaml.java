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
 * FromYAML.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.Variables;
import adams.core.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

/**
 * Loads the variables from a YAML file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromYaml
  extends AbstractFileBasedVariableLoader {

  private static final long serialVersionUID = -2381776417480141293L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads the variables from a YAML file:\n"
	     + "http://yaml.org/";
  }

  /**
   * Loads the variables.
   *
   * @param errors for collecting errors
   * @return the variables, null if failed to load
   */
  @Override
  protected Variables doLoadVariables(MessageCollection errors) {
    Variables		result;
    FileReader 		freader;
    BufferedReader 	breader;
    Yaml		yaml;
    Map			map;
    String		key;

    result  = new Variables();
    freader = null;
    breader = null;
    try {
      freader = new FileReader(m_InputFile.getAbsolutePath());
      breader = new BufferedReader(freader);
      yaml    = new Yaml();
      map     = yaml.loadAs(breader, Map.class);
      for (Object obj: map.keySet()) {
	key = "" + obj;
	if (Variables.isValidName(key))
	  result.set(key, "" + map.get(obj));
	else
	  getLogger().warning("Skipping invalid variable name: " + key);
      }
    }
    catch (Exception e) {
      errors.add("Failed to read YAML file: " + m_InputFile, e);
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }

    return result;
  }
}
