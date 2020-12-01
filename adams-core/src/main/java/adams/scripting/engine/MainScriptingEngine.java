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
 * MainScriptingEngine.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.scripting.command.RemoteCommand;
import adams.scripting.connection.Connection;

/**
 * Interface for scripting engines that manage worker scripting engines
 * and sending them jobs for execution.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface MainScriptingEngine
  extends RemoteScriptingEngine {

  /**
   * Registers a worker with the given connection.
   *
   * @param conn	the connection of the worker
   */
  public void registerWorker(Connection conn);

  /**
   * Deregisters a worker with the given connection.
   *
   * @param conn	the connection of the worker
   */
  public void deregisterWorker(Connection conn);

  /**
   * Kills all workers registered.
   */
  public void killWorkers();

  /**
   * Sends the command to a worker.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  public String sendCommand(RemoteCommand cmd);
}
