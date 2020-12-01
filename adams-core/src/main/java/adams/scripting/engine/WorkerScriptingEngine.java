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
 * WorkerScriptingEngine.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.scripting.connection.Connection;

/**
 * Interface for scripting engines that register themselves with a main engine
 * for executing jobs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface WorkerScriptingEngine {

  /**
   * Sets the connection for communicating with the main engine.
   *
   * @param value	the connection
   */
  public void setMain(Connection value);

  /**
   * Returns the connection for communicating with the main engine.
   *
   * @return		the connection
   */
  public Connection getMain();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String mainTipText();

  /**
   * Sets the connection that the main uses for communicating with the worker.
   *
   * @param value	the connection
   */
  public void setWorker(Connection value);

  /**
   * Returns the connection that the main uses for communicating with the worker.
   *
   * @return		the connection
   */
  public Connection getWorker();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String workerTipText();
}
