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

package adams.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * An LRU cache, based on <code>LinkedHashMap</code>.<br>
 * This cache has a fixed maximum number of elements (<code>cacheSize</code>).
 * If the cache is full and another entry is added, the LRU (least recently used) entry is dropped.
 * <br><br>
 * This class is thread-safe. All methods of this class are synchronized.<br>
 * <br>
 * License: <a href="http://www.gnu.org/licenses/lgpl.html">LGPL</a>.
 *
 * @author Christian d'Heureuse (<a href="http://www.source-code.biz">www.source-code.biz</a>)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LRUCache<K,V>
  implements Serializable, CloneHandler<LRUCache<K,V>> {

  /** for serialization. */
  private static final long serialVersionUID = -4869609636566618142L;

  /**
   * The class that does the actual caching.
   *
   * @author Christian d'Heureuse (<a href="http://www.source-code.biz">www.source-code.biz</a>)
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  protected class LRUCacheLinkedHashMap
    extends LinkedHashMap<K,V> {

    /** for serialization. */
    private static final long serialVersionUID = 6485446895298838183L;

    /**
     * the constructor.
     *
     * @param capacity		the capacity of the cache
     * @param loadFactor	the load factor of the hashtable
     */
    public LRUCacheLinkedHashMap(int capacity, float loadFactor) {
      super(capacity, loadFactor, true);
    }

    /**
     * Returns whether the oldest entry has to be removed.
     *
     * @param eldest		the entry
     * @return			true if the cache is full
     */
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
      return (size() > m_CacheSize);
    }
  }

  /** the load factor. */
  public static final float HASHTABLE_LOAD_FACTOR = 0.75f;

  /** the cache. */
  protected LinkedHashMap<K,V> m_Map;

  /** the cache size. */
  protected int m_CacheSize;

  /** whether the cache is enabled. */
  protected boolean m_Enabled;

  /**
   * Creates a new LRU cache.
   *
   * @param cacheSize 	the maximum number of entries that will be kept in
   * 			this cache.
   */
  public LRUCache(int cacheSize) {
    resize(cacheSize);
  }

  /**
   * Returns whether the cache is enabled.
   *
   * @return		true if the cache is enabled
   */
  public synchronized boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * Sets the enabled state of the cache. Enabling the cache is ignored if
   * the cache size is 0.
   *
   * @param value	if true then the cache is activated
   */
  public synchronized void setEnabled(boolean value) {
    if ((value && (m_CacheSize > 0)) || !value)
      m_Enabled = value;
  }

  /**
   * Returns the cache size.
   *
   * @return		the size of the cache.
   */
  public int size() {
    return m_CacheSize;
  }

  /**
   * Returns the number of used entries in the cache.
   *
   * @return 		the number of entries currently in the cache.
   */
  public synchronized int sizeUsed() {
    return m_Map.size();
  }

  /**
   * Resizes the caches (disables, empties and re-enables the cache). If
   * the cache size is 0 then the cache won't get enabled.
   *
   * @param cacheSize	the new siuze of the cache
   */
  public synchronized void resize(int cacheSize) {
    m_Enabled   = false;
    m_CacheSize = cacheSize;
    m_Map       = new LRUCacheLinkedHashMap(
			(int) Math.ceil(cacheSize / HASHTABLE_LOAD_FACTOR) + 1,
			HASHTABLE_LOAD_FACTOR);
    m_Enabled   = (m_CacheSize > 0);
  }

  /**
   * Checks whether a given key is already stored in the cache.
   *
   * @param key		the key to look for
   * @return		true if a value is already stored under this key
   */
  public synchronized boolean contains(K key) {
    return m_Map.containsKey(key);
  }

  /**
   * Retrieves an entry from the cache.<br>
   *
   * The retrieved entry becomes the MRU (most recently used) entry.
   *
   * @param key 	the key whose associated value is to be returned.
   * @return    	the value associated to this key, or null if no value
   * 			with this key exists in the cache.
   */
  public synchronized V get(K key) {
    return m_Map.get(key);
  }

  /**
   * Adds an entry to this cache (if enabled).
   * If the cache is full, the LRU (least recently used) entry is dropped.
   *
   * @param key    	the key with which the specified value is to be associated.
   * @param value  	a value to be associated with the specified key.
   */
  public synchronized void put(K key, V value) {
    if (m_Enabled)
      m_Map.put(key,value);
  }

  /**
   * Removes the entry from this cache (if enabled).
   *
   * @param key    	the key of the value to remove from the cache
   * @param return  	the previously with the key associated value, or null
   * 			if not in cache or cache not enabled
   */
  public synchronized V remove(K key) {
    if (m_Enabled)
      return m_Map.remove(key);
    else
      return null;
  }

  /**
   * Clears the cache.
   */
  public synchronized void clear() {
    m_Map.clear();
  }

  /**
   * Returns a <code>Collection</code> that contains a copy of all cache entries.
   *
   * @return 		a <code>Collection</code> with a copy of the cache content.
   */
  public synchronized Collection<Map.Entry<K,V>> getAll() {
    return new ArrayList<Map.Entry<K,V>>(m_Map.entrySet());
  }

  /**
   * Returns a {@link Set} view of the keys contained in this map.
   *
   * @return		the set of keys
   */
  public Set<K> keySet() {
    return m_Map.keySet();
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public LRUCache<K,V> getClone() {
    LRUCache<K,V>	result;

    result           = new LRUCache<K,V>(m_CacheSize);
    result.m_Map     = (LinkedHashMap<K,V>) m_Map.clone();
    result.m_Enabled = m_Enabled;

    return result;
  }

  /**
   * Test routine for the LRUCache class.
   *
   * @param args	ignored
   */
  public static void main (String[] args) {
    LRUCache<String,String> c = new LRUCache<String,String>(3);
    c.put("1","one");                            // 1
    c.put("2","two");                            // 2 1
    c.put("3","three");                          // 3 2 1
    c.put("4","four");                           // 4 3 2
    if (c.get("2") == null)
      throw new Error();      // 2 4 3
    c.put("5","five");                           // 5 2 4
    c.put("4","second four");                    // 4 5 2

    // Verify cache content.
    if (c.sizeUsed() != 3)              throw new Error();
    if (!c.get("4").equals("second four")) throw new Error();
    if (!c.get("5").equals("five"))        throw new Error();
    if (!c.get("2").equals("two"))         throw new Error();

    // List cache content.
    for (Map.Entry<String,String> e: c.getAll())
      System.out.println(e.getKey() + " : " + e.getValue());
  }
}
