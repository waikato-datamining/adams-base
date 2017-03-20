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
 * MultiScriptingEngine.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.MessageCollection;

/**
 * Manages multiple scripting engines.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiScriptingEngine
  extends AbstractScriptingEngine {

  private static final long serialVersionUID = 4169355528492796558L;

  /** the scripting engines to use. */
  protected RemoteScriptingEngine[] m_Engines;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages multiple scripting engines.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("permissionHandler");
    m_OptionManager.removeByProperty("requestHandler");
    m_OptionManager.removeByProperty("responseHandler");

    m_OptionManager.add(
      "engine", "engines",
      new RemoteScriptingEngine[0]);
  }

  /**
   * Adds the scripting engine without resetting the scheme. Does not
   * start or stop the engine. Safe to call at runtime.
   *
   * @param value	the engine to add
   */
  public void addEngine(RemoteScriptingEngine value) {
    RemoteScriptingEngine[]	engines;
    int				i;

    engines = new RemoteScriptingEngine[m_Engines.length + 1];
    for (i = 0; i < m_Engines.length; i++)
      engines[i] = m_Engines[i];
    engines[engines.length - 1] = value;
    m_Engines = engines;
  }

  /**
   * Removes the scripting engine without resetting the scheme. Stops the engine.
   * Safe to call at runtime.
   *
   * @param value	the engine to add
   */
  public void removeEngine(RemoteScriptingEngine value) {
    RemoteScriptingEngine[]	engines;
    int				i;
    int				n;

    engines = new RemoteScriptingEngine[m_Engines.length - 1];
    n       = 0;
    for (i = 0; i < m_Engines.length; i++) {
      if (m_Engines[i].toCommandLine().equals(value.toCommandLine())) {
	m_Engines[i].stopExecution();
	continue;
      }
      engines[n] = m_Engines[i];
      n++;
    }
    m_Engines = engines;
  }

  /**
   * Sets the engines use.
   *
   * @param value	the engines
   */
  public void setEngines(RemoteScriptingEngine[] value) {
    m_Engines = value;
    reset();
  }

  /**
   * Returns the engines in use.
   *
   * @return		the engines
   */
  public RemoteScriptingEngine[] getEngines() {
    return m_Engines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String enginesTipText() {
    return "The remote scripting engines to use.";
  }

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  @Override
  protected String doExecute() {
    MessageCollection	errors;
    String		msg;
    int			i;

    errors = new MessageCollection();

    for (i = 0; i < m_Engines.length; i++) {
      msg = m_Engines[i].execute();
      if (msg != null)
	errors.add("Engine #" + (i+1) + ": " + msg);
    }

    return errors.isEmpty() ? null : errors.toString();
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    int		i;

    for (i = 0; i < m_Engines.length; i++)
      m_Engines[i].pauseExecution();

    super.pauseExecution();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    int		i;

    for (i = 0; i < m_Engines.length; i++)
      m_Engines[i].resumeExecution();

    super.resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    int		i;

    for (i = 0; i < m_Engines.length; i++)
      m_Engines[i].stopExecution();

    super.stopExecution();
  }
}
