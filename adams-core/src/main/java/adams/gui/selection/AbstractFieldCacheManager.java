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
 * FieldCacheManager.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.io.Serializable;
import java.util.Hashtable;

import adams.db.FieldProvider;

/**
 * Manages the field caches.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFieldCacheManager
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -6836635614654765458L;

  /** the cache items to manage. */
  protected Hashtable<String,AbstractFieldCacheItem> m_Items;

  /**
   * Initializes manager.
   */
  public AbstractFieldCacheManager() {
    m_Items = new Hashtable<String,AbstractFieldCacheItem>();
  }

  /**
   * Clears the cache.
   */
  public void clear() {
    m_Items.clear();
  }

  /**
   * Creates a new cache item.
   *
   * @param provider	the provider for which to create a new item
   */
  protected abstract AbstractFieldCacheItem newFieldCacheItem(FieldProvider provider);

  /**
   * Retrieves the cache item for the specified database connection,
   * creates it if necessary.
   *
   * @param provider	the field provider
   * @return		the cache item
   */
  public AbstractFieldCacheItem get(FieldProvider provider) {
    String	key;

    key = provider.getDatabaseConnection().getURL() + "\t" + provider.getDatabaseConnection().getUser();
    if (!m_Items.containsKey(key))
      m_Items.put(key, newFieldCacheItem(provider));

    return m_Items.get(key);
  }
}