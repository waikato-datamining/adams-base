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
 * PropertiesBasedEnumeration.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.ConfigurableEnumeration.AbstractItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration that uses properties as backend.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class PropertiesBasedEnumeration<T extends AbstractItem>
  extends ConfigurableEnumeration<T> {

  private static final long serialVersionUID = -1152323610526845659L;

  /** the key for listing all the items. */
  public final static String KEY_ITEMS = "items";

  /** the key suffix for the ID. */
  public final static String SUFFIX_ID = ".id";

  /** the key suffix for the display text. */
  public final static String SUFFIX_DISPLAY = ".display";

  /** the properties to use. */
  protected Properties m_Properties;

  /**
   * Initializes the items.
   */
  protected T[] initialize() {
    Object	result;
    List<T> 	items;
    Properties	props;
    int		i;

    items = new ArrayList<>();
    props  = getProperties();
    if (props.hasKey(KEY_ITEMS)) {
      String[] keys = props.getProperty(KEY_ITEMS).split(",");
      for (String key: keys) {
	if (props.hasKey(key + SUFFIX_ID)) {
	  String id = props.getProperty(key + SUFFIX_ID);
	  String display = props.getProperty(key + SUFFIX_DISPLAY);
	  T item = postProcess(newItem(id, display), props, key);
	  items.add(item);
	}
      }
    }

    if (items.size() == 0)
      throw new IllegalStateException("No items in properties!\n" + props);

    result = Array.newInstance(items.get(0).getClass(), items.size());
    for (i = 0; i < items.size(); i++)
      Array.set(result, i, items.get(i));

    return (T[]) result;
  }

  /**
   * For post-processing an item. E.g., adding additional information.
   *
   * @param item	the new item
   * @param props	the underlying properties
   * @param key		the current key in the properties
   * @return		the updated item
   */
  protected T postProcess(T item, Properties props, String key) {
    return item;
  }

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  protected synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = readProperties();
    return m_Properties;
  }

  /**
   * Reads the properties from disk.
   *
   * @return		the properties
   */
  protected abstract Properties readProperties();
}
