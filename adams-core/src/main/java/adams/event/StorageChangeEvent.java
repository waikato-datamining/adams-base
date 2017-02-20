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
 * StorageChangeEvent.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.event;

import adams.flow.control.Storage;

import java.util.EventObject;

/**
 * Gets sent whenever storage items get modified.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StorageChangeEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 265149599197540318L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** a storage item got added. */
    ADDED,
    /** a storage item's value got modified. */
    MODIFIED,
    /** a storage item got removed. */
    REMOVED
  }

  /** the type of the event. */
  protected Type m_Type;

  /** the name of the storage item. */
  protected String m_Name;

  /** the cache (null if none). */
  protected String m_Cache;

  /**
   * Initializes the event.
   *
   * @param source	the storage object that triggered the event
   * @param type	the type of event
   * @param name	the name of the variable
   */
  public StorageChangeEvent(Storage source, Type type, String name) {
    this(source, type, name, null);
  }

  /**
   * Initializes the event.
   *
   * @param source	the storage object that triggered the event
   * @param type	the type of event
   * @param name	the name of the variable
   * @param cache	the affected cache, null if none
   */
  public StorageChangeEvent(Storage source, Type type, String name, String cache) {
    super(source);

    m_Type  = type;
    m_Name  = name;
    m_Cache = cache;
  }

  /**
   * Returns the storage that triggered the event.
   *
   * @return		the source
   */
  public Storage getVariables() {
    return (Storage) getSource();
  }

  /**
   * Returns the type of the event.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the name of the storage item of this event.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the name of the affected cache.
   *
   * @return		the name, null if no cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns a string representation of the event.
   *
   * @return		the string representation
   */
  public String toString() {
    return
        "source=" + getSource().getClass().getName() + "/" + getSource().hashCode()
        + ", name=" + getName()
        + ", cache=" + getCache()
        + ", type=" + getType();
  }
}
