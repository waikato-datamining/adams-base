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
 * AbstractScriptingEngineEnhancer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.multiprocess.CallableWithResult;

/**
 * Ancestor for scripting engines that enhance a base one.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptingEngineEnhancer
  extends AbstractScriptingEngine {

  private static final long serialVersionUID = -6014289902214010349L;

  /** the base scripting engine to use. */
  protected RemoteScriptingEngine m_ScriptingEngine;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "scripting-engine", "scriptingEngine",
      getDefaultScriptingEngine());
  }

  /**
   * Returns the default scripting engine.
   *
   * @return		the default
   */
  protected RemoteScriptingEngine getDefaultScriptingEngine() {
    return new DefaultScriptingEngine();
  }

  /**
   * Sets the scripting engine to use.
   *
   * @param value	the scripting engine
   */
  public void setScriptingEngine(RemoteScriptingEngine value) {
    m_ScriptingEngine = value;
    reset();
  }

  /**
   * Returns the scripting engine in use.
   *
   * @return		the scripting engine
   */
  public RemoteScriptingEngine getScriptingEngine() {
    return m_ScriptingEngine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String scriptingEngineTipText() {
    return "The scripting engine to use.";
  }

  /**
   * Hook method which gets called just before the base engine is executed.
   * <br>
   * Default implementation does nothing.
   *
   * @return		null if successful, otherwise error message
   */
  protected String preExecute() {
    return null;
  }

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  @Override
  protected String doExecute() {
    String	result;

    result = preExecute();
    if (result == null)
      result = m_ScriptingEngine.execute();
    return result;
  }

  /**
   * Executes the job.
   *
   * @param job		the job to execute
   */
  @Override
  public void executeJob(CallableWithResult<String> job) {
    m_ScriptingEngine.executeJob(job);
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    m_ScriptingEngine.pauseExecution();
    super.pauseExecution();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    m_ScriptingEngine.resumeExecution();
    super.resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_ScriptingEngine.stopExecution();
    super.stopExecution();
  }
}
