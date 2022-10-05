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
 * PyroCall.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.CleanUpHandler;
import adams.core.option.OptionHandler;

/**
 * Interface for Pyro5 method calls.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface PyroCall
  extends OptionHandler, FlowContextHandler, CleanUpHandler {

  /**
   * Sets the name of the remote object to use.
   *
   * @param value 	the name
   */
  public void setRemoteObjectName(String value);

  /**
   * Returns the name of the remote object to use.
   *
   * @return 		the name
   */
  public String getRemoteObjectName();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteObjectNameTipText();

  /**
   * Sets the name of the method to call.
   *
   * @param value 	the name
   */
  public void setMethodName(String value);

  /**
   * Returns the name of the method to call.
   *
   * @return 		the name
   */
  public String getMethodName();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String methodNameTipText();

  /**
   * Configures the call.
   *
   * @return		null if successful, otherwise error message
   */
  public String setUp();

  /**
   * Performs the call.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute();
}
