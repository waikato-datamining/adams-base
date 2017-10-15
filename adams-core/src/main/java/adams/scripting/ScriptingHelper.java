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
 * ScriptingHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.processor.DefaultProcessor;
import adams.scripting.processor.RemoteCommandProcessor;

/**
 * Helper class for remote command scripting.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ScriptingHelper {

  /** the filename of the props file. */
  public final static String FILENAME = "adams/scripting/ScriptingHelper.props";

  public static final String KEY_ENGINE = "Engine";

  public static final String KEY_CONNECTION = "Connection";

  public static final String KEY_PROCESSOR = "Processor";

  /** the singleton. */
  protected static ScriptingHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /**
   * Initializes the helper.
   */
  protected ScriptingHelper() {
    initialize();
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    try {
      m_Properties = Properties.read(FILENAME);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Returns the default scripting engine.
   *
   * @return		the default
   */
  public RemoteScriptingEngine getDefaultEngine() {
    try {
      return (RemoteScriptingEngine) OptionUtils.forCommandLine(
        RemoteScriptingEngine.class,
	m_Properties.getProperty(KEY_ENGINE, new DefaultScriptingEngine().toCommandLine()));
    }
    catch (Exception e) {
      return new DefaultScriptingEngine();
    }
  }

  /**
   * Returns the default scripting connection.
   *
   * @return		the default
   */
  public Connection getDefaultConnection() {
    try {
      return (Connection) OptionUtils.forCommandLine(
        Connection.class,
	m_Properties.getProperty(KEY_CONNECTION, new DefaultConnection().toCommandLine()));
    }
    catch (Exception e) {
      return new DefaultConnection();
    }
  }

  /**
   * Returns the default command processor.
   *
   * @return		the default
   */
  public RemoteCommandProcessor getDefaultProcessor() {
    try {
      return (RemoteCommandProcessor) OptionUtils.forCommandLine(
        RemoteCommandProcessor.class,
	m_Properties.getProperty(KEY_PROCESSOR, new DefaultProcessor().toCommandLine()));
    }
    catch (Exception e) {
      return new DefaultProcessor();
    }
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public static synchronized ScriptingHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ScriptingHelper();
    return m_Singleton;
  }
}
