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
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts the Map into a JSON object. Handles nested maps.
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
public class MapToJson
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4017583319699378889L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Converts the Map into a JSON object. Handles nested maps.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Map.class;
  }

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
   * Transfers the map key/value into the JSON object.
   *
   * @param json	where to store the map data
   * @param key		the key of the value
   * @param value	the value associated with the key
   */
  protected void add(JSONObject json, Object key, Object value) {
    Map		nestedMap;
    JSONObject	nestedJson;
    JSONArray	nestedArray;
    int		i;

      if (value instanceof Map) {
	nestedMap = (Map) value;
	nestedJson = new JSONObject();
	convert(nestedJson, nestedMap);
	json.put(key.toString(), nestedJson);
      }
      else if (value.getClass().isArray()) {
	nestedArray = new JSONArray();
	for (i = 0; i < Array.getLength(value); i++)
	  nestedArray.add(Array.get(value, i));
	json.put(key.toString(), nestedArray);
      }
      else {
	json.put(key.toString(), value);
      }
  }

  /**
   * Transfers the map data into the JSON object.
   *
   * @param json	where to store the map data
   * @param map		the map to transfer
   */
  protected void convert(JSONObject json, Map map) {
    Object	value;

    for (Object key : map.keySet()) {
      value = map.get(key);
      add(json, key, value);
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    Map		map;
    JSONObject	result;

    map    = (Map) m_Input;
    result = new JSONObject();
    convert(result, map);

    return result;
  }
}
