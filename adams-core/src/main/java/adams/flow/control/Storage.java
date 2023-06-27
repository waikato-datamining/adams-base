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
 * Storage.java
 * Copyright (C) 2011-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.CloneHandler;
import adams.core.LRUCache;
import adams.core.base.BaseRegExp;
import adams.core.classmanager.ClassManager;
import adams.event.StorageChangeEvent;
import adams.event.StorageChangeEvent.Type;
import adams.event.StorageChangeListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used for temporary storage during flow execution.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Storage
  implements Serializable, CloneHandler<Storage> {

  /** allowed characters. */
  public final static String CHARS = "abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789:.";

  /** the start of a storage placeholder. */
  public final static String START = "%{";

  /** the end of a storage placeholder. */
  public final static String END = "}";

  private static final long serialVersionUID = 2110403856127326735L;

  /** for storing the data. */
  protected HashMap<String,Object> m_Data;

  /** name LRU caches. */
  protected HashMap<String,LRUCache<String,Object>> m_Caches;

  /** the listeners. */
  protected transient Set<StorageChangeListener> m_ChangeListeners;

  /**
   * Initializes the storage.
   */
  public Storage() {
    m_Data   = new HashMap<>();
    m_Caches = new HashMap<>();
    initChangeListeners();
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

    result = new ArrayList<>(m_Caches.keySet());
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
    m_Caches.put(name, new LRUCache<>(size));
  }

  /**
   * Adds the given value under the specified name.
   *
   * @param name	the name to store the value under
   * @param value	the value to store
   * @return		any previous value stored under the same name
   */
  public synchronized Object put(StorageName name, Object value) {
    Object	result;

    result = m_Data.put(name.getValue(), value);
    notifyChangeListeners(new StorageChangeEvent(this, (result != null) ? Type.MODIFIED : Type.ADDED, name.getValue()));

    return result;
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
    if (m_Caches.containsKey(cache)) {
      m_Caches.get(cache).put(name.getValue(), value);
      notifyChangeListeners(new StorageChangeEvent(this, Type.ADDED, name.getValue(), cache));
    }
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
    return (m_Caches.containsKey(cache))
      && m_Caches.get(cache).contains(name.getValue());
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
   * Returns the objects associated with the regexp matching their name.
   *
   * @param nameRegExp	the regexp for the names must match
   * @return		the matching names with their associated values
   */
  public synchronized Map<String,Object> get(BaseRegExp nameRegExp) {
    Map<String,Object> 	result;

    result = new HashMap<>();

    for (String name: m_Data.keySet()) {
      if (nameRegExp.isMatch(name))
        result.put(name, m_Data.get(name));
    }

    return result;
  }

  /**
   * Returns the objects associated with the regexp matching the names from the specified LRU cache.
   *
   * @param cache	the LRU cache to access
   * @param nameRegExp	the regexp that the names must match
   * @return		the matching names with their associated values
   */
  public synchronized Map<String,Object> get(String cache, BaseRegExp nameRegExp) {
    Map<String,Object> 		result;
    LRUCache<String,Object>	lru;

    result = new HashMap<>();

    if (m_Caches.containsKey(cache)) {
      lru = m_Caches.get(cache);
      for (String name: lru.keySet()) {
	if (nameRegExp.isMatch(name))
	  result.put(name, lru.get(name));
      }
    }

    return result;
  }

  /**
   * Removes the object associated with the name.
   *
   * @param name	the name of the value to remove
   * @return		the previously associated value, or null if none present
   */
  public synchronized Object remove(StorageName name) {
    Object	result;

    result = m_Data.remove(name.getValue());

    if (result != null)
      notifyChangeListeners(new StorageChangeEvent(this, Type.REMOVED, name.getValue()));

    return result;
  }

  /**
   * Removes the object(s) which name(s) match the regular expression.
   *
   * @param regexp	the regular expression to match against
   * @return		true if at least one removed
   */
  public synchronized boolean remove(BaseRegExp regexp) {
    boolean		result;
    List<StorageName>	keys;

    result = false;
    keys   = new ArrayList<>(keySet());
    for (StorageName key: keys) {
      if (regexp.isMatch(key.getValue())) {
	result = true;
	if (remove(key) != null)
	  notifyChangeListeners(new StorageChangeEvent(this, Type.REMOVED, key.getValue()));
      }
    }

    return result;
  }

  /**
   * Removes the object associated with the name using the named LRU cache.
   *
   * @param cache	the LRU cache to use
   * @param name	the name of the value to remove
   * @return		the previously associated value, or null if none present
   */
  public synchronized Object remove(String cache, StorageName name) {
    Object	result;

    result = null;

    if (m_Caches.containsKey(cache))
      result = m_Caches.get(cache).remove(name.getValue());

    if (result != null)
      notifyChangeListeners(new StorageChangeEvent(this, Type.REMOVED, name.getValue(), cache));

    return result;
  }

  /**
   * Removes the object(s) which name(s) match the regular expression using the named LRU cache.
   *
   * @param cache	the LRU cache to use
   * @param regexp	the regular expression to match against
   * @return		true if at least one removed
   */
  public synchronized boolean remove(String cache, BaseRegExp regexp) {
    boolean		result;
    List<StorageName>	keys;

    result = false;
    keys   = new ArrayList<>(keySet(cache));
    for (StorageName key: keys) {
      if (regexp.isMatch(key.getValue())) {
	result = true;
	if (remove(cache, key) != null)
	  notifyChangeListeners(new StorageChangeEvent(this, Type.REMOVED, key.getValue(), cache));
      }
    }

    return result;
  }

  /**
   * Adds all the items from the other Storage object (overwrites
   * any existing ones).
   *
   * @param other	the Storage to copy
   */
  public void assign(Storage other) {
    assign(other, null);
  }

  /**
   * Adds all the items from the other Storage object (overwrites
   * any existing ones).
   *
   * @param other	the Storage to copy
   * @param filter	the regular expression that the item names must
   * 			match, null to ignore
   */
  public void assign(Storage other, BaseRegExp filter) {
    for (StorageName name: other.keySet()) {
      if ((filter == null) || filter.isMatch(name.getValue()))
	put(name, other.get(name));
    }
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

    result = new HashSet<>();
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

    result = new HashSet<>();

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
    Storage 			result;
    LRUCache<String,Object>	cache;

    result = new Storage();
    for (String key: m_Caches.keySet()) {
      cache = (LRUCache<String,Object>) ClassManager.getSingleton().deepCopy(m_Caches.get(key));
      result.m_Caches.put(key, cache);
    }
    for (String name: m_Data.keySet()) {
      if ((filter == null) || ((filter != null) && filter.isMatch(name)))
	result.m_Data.put(name, ClassManager.getSingleton().deepCopy(m_Data.get(name)));
    }

    return result;
  }

  /**
   * Returns a shallow copy of the object.
   *
   * @return		the shallow copy
   */
  public synchronized Storage getShallowCopy() {
    Storage 			result;
    LRUCache<String,Object>	cache;

    result = new Storage();
    for (String key: m_Caches.keySet()) {
      cache = m_Caches.get(key).getClone();
      result.m_Caches.put(key, cache);
    }
    result.m_Data = (HashMap<String,Object>) m_Data.clone();

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
   * Checks whether the string represents a valid name.
   *
   * @param s		the name to check
   * @return		true if valid
   */
  public static boolean isValidName(String s) {
    boolean		result;
    StringBuilder	name;
    int			i;

    name   = new StringBuilder();
    result = (s.length() > 0);
    if (result) {
      for (i = 0; i < s.length(); i++) {
	if (CHARS.indexOf(s.charAt(i)) > -1)
	  continue;
	name.append(s.charAt(i));
      }
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
    return expand(s, s.contains(START + START));
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
    if (result.contains(part)) {
      names = keySet().iterator();
      while (names.hasNext() && result.contains(part)) {
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
   * Initializes the change listeners.
   */
  protected void initChangeListeners() {
    if (m_ChangeListeners == null)
      m_ChangeListeners = new HashSet<>();
  }

  /**
   * Adds the change listener.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(StorageChangeListener l) {
    initChangeListeners();
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the change listener.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(StorageChangeListener l) {
    initChangeListeners();
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners.
   *
   * @param e		the event to send
   */
  protected void notifyChangeListeners(StorageChangeEvent e) {
    initChangeListeners();
    for (StorageChangeListener l: m_ChangeListeners)
      l.storageChanged(e);
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
      if (CHARS.indexOf(s.charAt(i)) > -1)
	result.append(chr);
      else
	result.append(replace);
    }

    return result.toString();
  }
}
