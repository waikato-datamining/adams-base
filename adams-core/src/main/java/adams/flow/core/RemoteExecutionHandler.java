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
 * RemoteExecutionHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.VariableName;
import adams.flow.control.StorageName;
import adams.scripting.connection.Connection;

/**
 * Interface for actors that manage remote execution.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RemoteExecutionHandler
  extends Actor {

  /**
   * Sets the names of the storage items to transfer.
   *
   * @param value	the storage names
   */
  public void setStorageNames(StorageName[] value);

  /**
   * Returns the names of the storage items to transfer.
   *
   * @return		the storage names
   */
  public StorageName[] getStorageNames();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String storageNamesTipText();

  /**
   * Sets the names of the variables to transfer.
   *
   * @param value	the variable names
   */
  public void setVariableNames(VariableName[] value);

  /**
   * Returns the names of the variables to transfer.
   *
   * @return		the variable names
   */
  public VariableName[] getVariableNames();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String variableNamesTipText();

  /**
   * Sets the connection used for sending the flow.
   *
   * @param value	the connection
   */
  public void setConnection(Connection value);

  /**
   * Returns the connection used for sending the flow.
   *
   * @return		the connection
   */
  public Connection getConnection();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String connectionTipText();
}
