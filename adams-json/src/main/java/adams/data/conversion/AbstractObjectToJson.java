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
 * MapToJson.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts the Map into a JSON object. Handles nested maps, lists and arrays.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10824 $
 */
public abstract class AbstractObjectToJson
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4017583319699378889L;

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return JSONAware.class;
  }

  /**
   * Turns the object into a JSON object, if necessary.
   *
   * @param value	the value associated with the key
   */
  protected Object toJSON(Object value) {
    Map 	map;
    List	list;
    JSONObject 	json;
    JSONArray 	array;
    int		i;

    if (value instanceof Map) {
      map  = (Map) value;
      json = new JSONObject();
      for (Object key : map.keySet())
	json.put(key.toString(), toJSON(map.get(key)));
      return json;
    }
    else if (value instanceof List) {
      list  = (List) value;
      array = new JSONArray();
      for (i = 0; i < list.size(); i++)
	array.add(toJSON(list.get(i)));
      return array;
    }
    else if (value.getClass().isArray()) {
      array = new JSONArray();
      for (i = 0; i < Array.getLength(value); i++)
	array.add(toJSON(Array.get(value, i)));
      return array;
    }
    else {
      return value;
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    return toJSON(m_Input);
  }
}
