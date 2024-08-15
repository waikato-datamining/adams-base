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
 * BackgroundScriptingEngineRegistry.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scriptingengine;

import adams.core.logging.CustomLoggingLevelObject;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;

import java.util.HashSet;
import java.util.Set;

/**
 * Registry for background scripting engines that need to be stopped before
 * the application can terminate properly.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BackgroundScriptingEngineRegistry
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 8586296256047371291L;

  /** the scripting engines. */
  protected Set<BackgroundScriptingEngine> m_Engines;

  /** the singleton. */
  protected static BackgroundScriptingEngineRegistry m_Singleton;

  /**
   * Initializes the manager.
   */
  protected BackgroundScriptingEngineRegistry() {
    m_Engines = new HashSet<>();
  }

  /**
   * Pre-configures the logging.
   */
  protected void initializeLogging() {
    m_LoggingLevel = LoggingHelper.getLoggingLevel(getClass(), LoggingLevel.INFO);
  }

  /**
   * Registers the specified engine.
   *
   * @param engine	the engine to register
   */
  public void register(BackgroundScriptingEngine engine) {
    getLogger().info("Registering: " + engine.getClass().getName());
    m_Engines.add(engine);
  }

  /**
   * Deregisters the specified engine.
   *
   * @param engine	the engine to deregister
   */
  public void deregister(BackgroundScriptingEngine engine) {
    getLogger().info("Deregistering: " + engine.getClass().getName());
    m_Engines.remove(engine);
  }

  /**
   * Returns the singleton of the registry.
   *
   * @return		the registry
   */
  public synchronized static BackgroundScriptingEngineRegistry getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new BackgroundScriptingEngineRegistry();
    return m_Singleton;
  }

  /**
   * Stops all scripting engines.
   */
  public synchronized void stopAllEngines() {
    for (BackgroundScriptingEngine engine: m_Engines.toArray(new BackgroundScriptingEngine[0])) {
      getSingleton().deregister(engine);
      getLogger().info("Stopping: " + engine.getClass().getName());
      engine.stopEngine();
    }
  }
}
