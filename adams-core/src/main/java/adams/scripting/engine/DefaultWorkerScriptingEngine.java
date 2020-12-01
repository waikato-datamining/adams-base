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
 * DefaultWorkerScriptingEngine.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.scripting.command.distributed.DeregisterWorker;
import adams.scripting.command.distributed.RegisterWorker;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;

/**
 * Registers itself with a main engine for executing jobs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultWorkerScriptingEngine
  extends AbstractScriptingEngineEnhancer
  implements WorkerScriptingEngine {

  private static final long serialVersionUID = 2201421147846496892L;

  /** the connection to the main engine node. */
  protected Connection m_Main;

  /** the connection for communicating with the worker. */
  protected Connection m_Worker;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Registers itself with a main engine for executing jobs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "main", "main",
      getDefaultMain());

    m_OptionManager.add(
      "worker", "worker",
      getDefaultWorker());
  }

  /**
   * Returns the default scripting engine.
   *
   * @return		the default
   */
  protected RemoteScriptingEngine getDefaultScriptingEngine() {
    DefaultScriptingEngine	result;

    result = new DefaultScriptingEngine();
    result.setPort(result.getPort() + 1);

    return result;
  }

  /**
   * Returns the default connection for the main.
   *
   * @return		the default
   */
  protected Connection getDefaultMain() {
    return new DefaultConnection();
  }

  /**
   * Sets the connection for communicating with the main engine.
   *
   * @param value	the connection
   */
  public void setMain(Connection value) {
    m_Main = value;
    reset();
  }

  /**
   * Returns the connection for communicating with the main engine.
   *
   * @return		the connection
   */
  public Connection getMain() {
    return m_Main;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String mainTipText() {
    return "The connection for communicating with the main engine.";
  }

  /**
   * Returns the default connection for the worker.
   *
   * @return		the default
   */
  protected Connection getDefaultWorker() {
    DefaultConnection	result;

    result = new DefaultConnection();
    result.setPort(result.getPort() + 1);

    return result;
  }

  /**
   * Sets the connection that the main uses for communicating with the worker.
   *
   * @param value	the connection
   */
  public void setWorker(Connection value) {
    m_Worker = value;
    reset();
  }

  /**
   * Returns the connection that the main uses for communicating with the worker.
   *
   * @return		the connection
   */
  public Connection getWorker() {
    return m_Worker;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String workerTipText() {
    return "The connection that the main engine uses for communicating with the worker";
  }

  /**
   * Hook method which gets called just before the base engine is executed.
   * <br>
   * Registers with the main.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String preExecute() {
    String		result;
    RegisterWorker register;

    result = super.preExecute();

    if (result == null) {
      register = new RegisterWorker();
      register.setConnection(m_Worker);
      result = m_Main.sendRequest(register, m_CommandProcessor);
    }

    return result;
  }

  /**
   * Stops the scripting engine and deregisters with the master.
   */
  @Override
  public void stopExecution() {
    DeregisterWorker deregister;
    String 		msg;

    deregister = new DeregisterWorker();
    deregister.setConnection(m_Worker);
    msg = m_Main.sendRequest(deregister, m_CommandProcessor);
    if (msg != null)
      getLogger().severe("Failed to deregister: " + msg);

    super.stopExecution();
  }
}
