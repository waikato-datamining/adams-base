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
 * DefaultSlaveScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.scripting.command.distributed.RegisterSlave;
import adams.scripting.connection.Connection;
import adams.scripting.connection.DefaultConnection;

/**
 * Registers itself with a master for executing jobs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultSlaveScriptingEngine
  extends AbstractScriptingEngineEnhancerWithJobQueue
  implements SlaveScriptingEngine {

  private static final long serialVersionUID = 2201421147846496892L;

  /** the connection to the master node. */
  protected Connection m_Master;

  /** the connection for communicating with the slave. */
  protected Connection m_Slave;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Registers itself with a master for executing jobs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "master", "master",
      getDefaultMaster());

    m_OptionManager.add(
      "slave", "slave",
      getDefaultSlave());
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
   * Returns the default connection for the master.
   *
   * @return		the default
   */
  protected Connection getDefaultMaster() {
    return new DefaultConnection();
  }

  /**
   * Sets the connection for communicating with the master.
   *
   * @param value	the connection
   */
  public void setMaster(Connection value) {
    m_Master = value;
    reset();
  }

  /**
   * Returns the connection for communicating with the master.
   *
   * @return		the connection
   */
  public Connection getMaster() {
    return m_Master;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String masterTipText() {
    return "The connection for communicating with the master.";
  }

  /**
   * Returns the default connection for the slave.
   *
   * @return		the default
   */
  protected Connection getDefaultSlave() {
    DefaultConnection	result;

    result = new DefaultConnection();
    result.setPort(result.getPort() + 1);

    return result;
  }

  /**
   * Sets the connection that the master uses for communicating with the slave.
   *
   * @param value	the connection
   */
  public void setSlave(Connection value) {
    m_Slave = value;
    reset();
  }

  /**
   * Returns the connection that the master uses for communicating with the slave.
   *
   * @return		the connection
   */
  public Connection getSlave() {
    return m_Slave;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String slaveTipText() {
    return "The connection that the master uses for communicating with the slave";
  }

  /**
   * Hook method which gets called just before the base engine is executed.
   * <br>
   * Registers with the master.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String preExecute() {
    String		result;
    RegisterSlave	register;

    result = super.preExecute();

    if (result == null) {
      register = new RegisterSlave();
      register.setConnection(m_Slave);
      result = m_Master.sendRequest(register);
    }

    return result;
  }
}
