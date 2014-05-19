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
 * ScopeHandler.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.flow.core.ActorHandler;

/**
 * For actors that define a scope like the {@link Flow} or {@link LocalScopeTrigger}
 * actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ScopeHandler
  extends ActorHandler {

  /**
   * Sets whether to enforce the callable name check.
   * 
   * @param value	true if to enforce check
   */
  public void setEnforceCallableNameCheck(boolean value);
  
  /**
   * Returns whether the check of callable names is enforced.
   * 
   * @return		true if check enforced
   */
  public boolean getEnforceCallableNameCheck();
  
  /**
   * Checks whether a callable name is already in use.
   * 
   * @param name	the name to check
   * @see		#getEnforceCallableNameCheck()
   */
  public boolean isCallableNameUsed(String name);

  /**
   * Adds the callable name to the list of used ones.
   * 
   * @param name	the name to add
   * @return		null if successfully added, otherwise error message
   * @see		#getEnforceCallableNameCheck()
   */
  public String addCallableName(String name);
}
