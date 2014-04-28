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
 * ScriptingEngine.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import java.util.Iterator;

import adams.core.Properties;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.env.ScriptingEngineDefinition;

/**
 * Processes scripting commands.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingEngine
  extends AbstractScriptingEngine {

  /** for serialization. */
  private static final long serialVersionUID = -2966869686762723507L;

  /** the scripting engine manager. */
  private static ScriptingEngineManager m_ScriptingEngineManager;

  /** the properties for scripting. */
  private static Properties m_Properties;

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the properties key to use for retrieving the properties.
   *
   * @return		the key
   */
  @Override
  protected String getDefinitionKey() {
    return ScriptingEngineDefinition.KEY;
  }

  /**
   * Provides access to the properties object.
   *
   * @return		the properties
   */
  @Override
  protected synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = readProperties();

    return m_Properties;
  }

  /**
   * Returns the singleton instance of the scripting engine.
   *
   * @param dbcon	the database context
   * @return		the singleton
   */
  public synchronized static AbstractScriptingEngine getSingleton(AbstractDatabaseConnection dbcon) {
    if (m_ScriptingEngineManager == null)
      m_ScriptingEngineManager = new ScriptingEngineManager();
    if (!m_ScriptingEngineManager.has(dbcon)) {
      m_ScriptingEngineManager.add(dbcon, new ScriptingEngine());
      m_ScriptingEngineManager.get(dbcon).setDatabaseConnection(dbcon);
    }

    return m_ScriptingEngineManager.get(dbcon);
  }

  /**
   * Stops all scripting engines.
   */
  public synchronized static void stopAllEngines() {
    Iterator<AbstractScriptingEngine>	iter;
    if (m_ScriptingEngineManager != null) {
      iter = m_ScriptingEngineManager.iterator();
      while (iter.hasNext())
	iter.next().stopEngine();
    }
  }
}
