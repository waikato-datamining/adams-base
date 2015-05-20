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
 * JsonObjectHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import java.util.logging.Level;

import net.minidev.json.JSONAware;
import net.minidev.json.parser.JSONParser;
import adams.core.ClassLocator;

/**
 <!-- globalinfo-start -->
 * Handler for JSON.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonObjectHandler
  extends AbstractObjectHandler<JSONAware> {

  /** for serialization. */
  private static final long serialVersionUID = -6046786448098226273L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Handler for JSON.";
  }

  /**
   * Checks whether the handler can process the given object.
   * 
   * @param cls		the class to check
   * @return		true if handler can process the object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(JSONAware.class, cls);
  }

  /**
   * Parses the given string.
   * 
   * @param s		the string
   * @return		the generated object, null if failed to convert
   */
  @Override
  public JSONAware parse(String s) {
    try {
      return (JSONAware) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(s);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse: " + s, e);
      return null;
    }
  }

  /**
   * Turns the given object back into a string.
   * 
   * @param obj		the object to convert into a string
   * @return		the string representation
   */
  @Override
  public String format(JSONAware obj) {
    return obj.toJSONString();
  }
}
