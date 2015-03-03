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
 * ScriptingEngineManager.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;

/**
 * Manages the database URL/scripting engine relations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingEngineManager
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 739416057351690903L;

  /** for storing the engine objects. */
  protected Hashtable<String,AbstractScriptingEngine> m_Engines;

  /**
   * Initializes the manager.
   */
  public ScriptingEngineManager() {
    super();

    m_Engines = new Hashtable<String,AbstractScriptingEngine>();
  }

  /**
   * Returns the key for this database connection.
   * 
   * @param dbcon	the database connection to get a key for
   * @return		the key
   */
  protected String getKey(AbstractDatabaseConnection dbcon) {
    return dbcon.getUser() + "@" + dbcon.getURL();
  }
  
  /**
   * Checks whether a engine object for the specified database connection is
   * available.
   *
   * @param dbcon	the connection to check
   * @return		true if a engine object is available
   */
  public boolean has(AbstractDatabaseConnection dbcon) {
    if (dbcon == null)
      dbcon = DatabaseConnection.getSingleton();
    return m_Engines.containsKey(getKey(dbcon));
  }

  /**
   * Gets the engine object for the specified database connection.
   *
   * @param dbcon	the connection to get the engine for
   * @return		the engine object if available, otherwise null
   */
  public AbstractScriptingEngine get(AbstractDatabaseConnection dbcon) {
    if (dbcon == null)
      dbcon = DatabaseConnection.getSingleton();
    return m_Engines.get(getKey(dbcon));
  }

  /**
   * Adds the engine object for the specified database connection.
   *
   * @param dbcon	the connection to add the engine for
   * @param engine	the engine object to add
   * @return		the previous engine, null if no previous one stored
   */
  public AbstractScriptingEngine add(AbstractDatabaseConnection dbcon, AbstractScriptingEngine engine) {
    if (dbcon == null)
      dbcon = DatabaseConnection.getSingleton();
    return m_Engines.put(getKey(dbcon), engine);
  }

  /**
   * Returns an iterator over all engines.
   *
   * @return		the iterator
   */
  public Iterator<AbstractScriptingEngine> iterator() {
    return m_Engines.values().iterator();
  }

  /**
   * Returns a short string representation of the manager.
   *
   * @return		the string representation
   */
  public String toString() {
    return "Scripting engines: " + m_Engines.keySet();
  }
}
