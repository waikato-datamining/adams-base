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
 * SlaveScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.scripting.connection.Connection;

/**
 * Interface for scripting engines that register themselves with a master
 * for executing jobs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SlaveScriptingEngine
  extends JobQueueHandler {

  /**
   * Sets the connection for communicating with the master.
   *
   * @param value	the connection
   */
  public void setMaster(Connection value);

  /**
   * Returns the connection for communicating with the master.
   *
   * @return		the connection
   */
  public Connection getMaster();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String masterTipText();

  /**
   * Sets the connection that the master uses for communicating with the slave.
   *
   * @param value	the connection
   */
  public void setSlave(Connection value);

  /**
   * Returns the connection that the master uses for communicating with the slave.
   *
   * @return		the connection
   */
  public Connection getSlave();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String slaveTipText();
}
