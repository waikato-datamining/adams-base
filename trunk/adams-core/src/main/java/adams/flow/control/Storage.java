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
 * Storage.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.CloneHandler;
import adams.core.LRUCache;
import adams.core.Utils;
import adams.core.base.BaseRegExp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Used for temporary storage during flow execution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Storage
  implements CloneHandler<Storage>{

  /** allowed characters. */
  public final static String CHARS = "abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789:.";

  /** the start of a storage placeholder. */
  public final static String START = "%{";

  /** the end of a storage placeholder. */
  public final static String END = "}";

  /** for storing the data. */
  protected Hashtable<String,Object> m_Data;

  /** name LRU caches. */
  protected Hashtable<String,LRUCache<String,Object>> m_Caches;

  /**
   * Initializes the storage.
   */
  public Storage() {
    m_Data   = new Hashtable<String,Object>();
    m_Caches = new Hashtable<String,LRUCache<String,Object>>();
  }

  /**
   * Empties the storage. Also removes all the LRU caches.
   */
  public synchronized void clear() {
    m_Data.clear();
    m_Caches.clear();
  }

  /**
   * Returns the names of the LRU caches currently available.
   *
   * @return 		the names of the LRU caches
   */
  public synchronized Iterator<String> caches() {
    List<String>	result;

    result = new ArrayList<String>(m_Caches.keySet());
    Collections.sort(result);

    return result.iterator();
  }

  /**
   * Adds a LRU cache under the name with the specified size.
   *
   * @param name	the name of the cache
   * @param size	the size of the cache
   */
  public synchronized void addCache(String name, int size) {
    m_Caches.put(name, new LRUCache<String,Object>(size));
  }

  /**
   * Adds the given value under the specified name.
   *
   * @param name	the name to store the value under
   * @param value	the value to store
   * @return		any previous value stored under the same name
   */
  public synchronized Object put(StorageName name, Object value) {
    return m_Data.put(name.getValue(), value);
  }

  /**
   * Adds the given value under the specified name to the named LRU cache.
   * Value gets dropped if cache is not available.
   *
   * @param cache	the name of the cache to add it to
   * @param name	the name to store the value under
   * @param value	the value to store
   */
  public synchronized void put(String cache, StorageName name, Object value) {
    if (m_Caches.containsKey(cache))
      m_Caches.get(cache).put(name.getValue(), value);
  }

  /**
   * Checks whether a value is stored under the given name.
   *
   * @param name	the name to check
   * @return		true if a value is stored under the name
   */
  public synchronized boolean has(StorageName name) {
    return m_Data.containsKey(name.getValue());
  }

  /**
   * Checks whether a value is stored under the given name in the named
   * LRU cache.
   *
   * @param cache	the LRU cache to check
   * @param name	the name to check
   * @return		true if a value is stored under the name
   */
  public synchronized boolean has(String cache, StorageName name) {
    if (m_Caches.containsKey(cache))
      return m_Caches.get(cache).contains(name.getValue());
    else
      return false;
  }

  /**
   * Returns the object associated with the name.
   *
   * @param name	the name of the value to retrieve
   * @return		the associated value, or null if not found
   */
  public synchronized Object get(StorageName name) {
    return m_Data.get(name.getValue());
  }

  /**
   * Returns the object associated with the name from the named LRU cache.
   *
   * @param cache	the LRU cache to access
   * @param name	the name of the value to retrieve
   * @return		the associated value, or null if not found
   */
  public synchronized Object get(String cache, StorageName name) {
    if (m_Caches.containsKey(cache))
      return m_Caches.get(cache).get(name.getValue());
    else
      return null;
  }

  /**
   * Removes the object associated with the name.
   *
   * @param name	the name of the value to remove
   * @return		the previously associated value, or null if none present
   */
  public synchronized Object remove(StorageName name) {
    return m_Data.remove(name.getValue());
  }

  /**
   * Removes the object associated with the name using the named LRU cache.
   *
   * @param cache	the LRU cache to use
   * @param name	the name of the value to remove
   * @return		the previously associated value, or null if none present
   */
  public synchronized Object remove(String cache, StorageName name) {
    if (m_Caches.containsKey(cache))
      return m_Caches.get(cache).remove(name.getValue());
    else
      return null;
  }

  /**
   * Returns the number of elements stored.
   *
   * @return		the number of stored values
   */
  public synchronized int size() {
    return m_Data.size();
  }

  /**
   * Returns the number of elements stored in the named LRU cache.
   *
   * @param cache	the cache to get the size for
   * @return		the number of stored values, 0 if cache not available
   */
  public synchronized int size(String cache) {
    if (m_Caches.containsKey(cache))
      return m_Caches.get(cache).sizeUsed();
    else
      return 0;
  }

  /**
   * Returns the set of keys.
   *
   * @return		the set
   */
  public synchronized Set<StorageName> keySet() {
    HashSet<StorageName>	result;
    Set<String>			set;

    result = new HashSet<StorageName>();
    set    = m_Data.keySet();
    for (String key: set)
      result.add(new StorageName(key));

    return result;
  }

  /**
   * Returns the set of keys from the named LRU cache.
   *
   * @param cache	the cache to query
   * @return		the set, emoty set if cache not available
   */
  public synchronized Set<StorageName> keySet(String cache) {
    HashSet<StorageName>	result;
    Set<String>			set;

    result = new HashSet<StorageName>();

    if (m_Caches.containsKey(cache)) {
      set = m_Caches.get(cache).keySet();
      for (String key: set)
	result.add(new StorageName(key));
    }

    return result;
  }

  /**
   * Returns a clone (deep copy) of the object.
   *
   * @return		the clone
   */
  public synchronized Storage getClone() {
    return getClone(null);
  }

  /**
   * Returns a clone (deep copy) of the object.
   *
   * @param filter	the regular expression that the storage item names 
   * 			must match (not applied to caches!), null to ignore
   * @return		the clone
   */
  public synchronized Storage getClone(BaseRegExp filter) {
    Storage			result;
    LRUCache<String,Object>	cache;

    result = new Storage();
    for (String key: m_Caches.keySet()) {
      cache = (LRUCache<String,Object>) Utils.deepCopy(m_Caches.get(key));
      result.m_Caches.put(key, cache);
    }
    for (String name: m_Data.keySet()) {
      if ((filter == null) || ((filter != null) && filter.isMatch(name)))
	result.m_Data.put(name, Utils.deepCopy(m_Data.get(name)));
    }

    return result;
  }

  /**
   * Returns a shallow copy of the object.
   *
   * @return		the shallow copy
   */
  public synchronized Storage getShallowCopy() {
    Storage			result;
    LRUCache<String,Object>	cache;

    result = new Storage();
    for (String key: m_Caches.keySet()) {
      cache = m_Caches.get(key).getClone();
      result.m_Caches.put(key, cache);
    }
    result.m_Data = (Hashtable<String,Object>) m_Data.clone();

    return result;
  }

  /**
   * Returns a string representation of all stored values.
   *
   * @return		the string representation
   */
  @Override
  public synchronized String toString() {
    StringBuilder	result;
    Iterator<String>	names;
    String		name;

    result = new StringBuilder();
    result.append("Regular:\n");
    result.append(m_Data.toString());

    names = caches();
    while (names.hasNext()) {
      name = names.next();
      result.append("\n");
      result.append("Cache '" + name + "':\n");
      result.append(m_Caches.get(name).toString());
    }

    return result.toString();
  }

  /**
   * Checks whether the string represents a valid name (without the "@{" and "}").
   *
   * @param s		the name to check
   * @return		true if valid
   */
  public static boolean isValidName(String s) {
    boolean	result;
    String	name;

    name   = s;
    result = (name.length() > 0);
    if (result) {
      name   = name.replaceAll("\\w", "").replace("-", "").replace(":", "").replace(".", "");
      result = (name.length() == 0);
    }

    return result;
  }

  /**
   * Replaces all storage placeholders in the string with the currently stored values.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public synchronized String expand(String s) {
    return expand(s, (s.indexOf(START + START) > -1));
  }

  /**
   * Expands storage placeholders.
   *
   * @param s		the string to expand
   * @return		the potentially expanded string
   */
  protected String doExpand(String s) {
    String			result;
    String			part;
    Iterator<StorageName>	names;
    StorageName			name;

    result = s;
    part   = START;
    if (result.indexOf(part) > -1) {
      names = keySet().iterator();
      while (names.hasNext() && (result.indexOf(part) > -1)) {
	name   = names.next();
	result = result.replace(START + name.getValue() + END, "" + get(name));
      }
    }

    return result;
  }

  /**
   * Replaces all storage placeholders in the string with the currently
   * stored values (ie string representation).
   *
   * @param s		the string to process
   * @param recurse	whether to recurse, i.e., replacing "%{%{"
   * @return		the processed string
   */
  protected String expand(String s, boolean recurse) {
    String		result;

    result = doExpand(s);
    if (recurse)
      result = expand(result);

    return result;
  }

  /**
   * Turns any string into a valid storage name,
   * by replacing invalid characters with underscores ("_").
   *
   * @param s		the string to convert into a valid storage name
   * @return		the (potentially) fixed name
   */
  public static String toValidName(String s) {
    return toValidName(s, "_");
  }

  /**
   * Turns any string into a valid storage name,
   * by replacing invalid characters with the specified replacement string.
   *
   * @param s		the string to convert into a valid storage name
   * @param replace	the replacement string for invalida chars
   * @return		the (potentially) fixed name
   */
  public static String toValidName(String s, String replace) {
    StringBuilder	result;
    int			i;
    char		chr;

    result = new StringBuilder();

    for (i = 0; i < s.length(); i++) {
      chr = s.charAt(i);
      if ((chr >= '0') && (chr <= '9'))
        result.append(chr);
      else if ((chr >= 'a') && (chr <= 'z'))
        result.append(chr);
      else if ((chr >= 'A') && (chr <= 'Z'))
        result.append(chr);
      else if (chr == '_')
        result.append(chr);
      else if (chr == '-')
        result.append(chr);
      else if (chr == ':')
        result.append(chr);
      else if (chr == '.')
        result.append(chr);
      else
        result.append(replace);
    }

    return result.toString();
  }
}
