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
 * MapToProperties.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.map;

import adams.core.Properties;
import adams.core.Utils;

import java.util.List;
import java.util.Map;

/**
 * Converts a Map object into a Properties string.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapToProperties
  extends AbstractMapToString {

  private static final long serialVersionUID = -7855014008863230853L;
  
  /** whether to output the keys in sortKeys fashion. */
  protected boolean m_SortKeys;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a " + Utils.classToString(Map.class) + " object into a " + Utils.classToString(Properties.class) + " string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "sort-keys", "sortKeys",
      false);
  }

  /**
   * Sets whether to sort the keys.
   *
   * @param value 	true if to sort
   */
  public void setSortKeys(boolean value) {
    m_SortKeys = value;
    reset();
  }

  /**
   * Returns whether to sort the keys.
   *
   * @return 		true if to sort
   */
  public boolean getSortKeys() {
    return m_SortKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortKeysTipText() {
    return "If enabled, the keys will get sorted.";
  }

  /**
   * Converts the map into a string.
   *
   * @param map the map to convert
   * @return the generated string
   */
  @Override
  protected String toString(Map map) {
    Properties	props;
    Object	value;
    
    props = new Properties();
    for (Object key: map.keySet()) {
      value = map.get(key);
      if (value instanceof List)
	props.setProperty("" + key, Utils.flatten((List) value, ","));
      else if (value instanceof Map)
	props.setProperty("" + key, toString((Map) value));
      else if (value.getClass().isArray())
	props.setProperty("" + key, Utils.arrayToString(value));
      else
	props.setProperty("" + key, "" + map.get(key));
    }
    
    return props.toString(m_SortKeys);
  }
}
