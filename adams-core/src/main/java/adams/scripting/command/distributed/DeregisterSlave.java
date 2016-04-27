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
 * DeregisterSlave.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.distributed;

import adams.core.option.OptionUtils;
import adams.scripting.command.AbstractCommand;
import adams.scripting.connection.Connection;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.engine.SlaveScriptingEngine;

import java.util.logging.Level;

/**
 * Deregisters a {@link SlaveScriptingEngine} from a master.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DeregisterSlave
  extends AbstractCommand {

  private static final long serialVersionUID = -1657908444959620122L;

  /** the connection to use for the slave. */
  protected Connection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Deregisters the owning " + SlaveScriptingEngine.class.getName() + " from a master.";
  }

  /**
   * Sets the connection to use as payload.
   *
   * @param value	the connection
   */
  public void setConnection(Connection value) {
    m_Connection = value;
  }

  /**
   * Retrieves the currently set connection.
   *
   * @return		the connection
   */
  public Connection getConnection() {
    return m_Connection;
  }

  /**
   * Sets the payload for the request.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
    String	cmdline;

    if (value.length == 0) {
      m_Connection = null;
      return;
    }

    cmdline = new String(value);
    if (isLoggingEnabled())
      getLogger().info("Create connection object from: " + cmdline);

    try {
      m_Connection = (Connection) OptionUtils.forString(Connection.class, cmdline);
    }
    catch (Exception e) {
      m_Connection = null;
      getLogger().log(Level.SEVERE, "Failed to create connection object from: " + cmdline);
    }
  }

  /**
   * Returns the payload of the request, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    if (m_Connection != null)
      return OptionUtils.getCommandLine(m_Connection).getBytes();
    else
      return new byte[0];
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[]{m_Connection};
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @return		null if successful, otherwise error message
   */
  protected String doHandleRequest(RemoteScriptingEngine engine) {
    return null;
  }
}
