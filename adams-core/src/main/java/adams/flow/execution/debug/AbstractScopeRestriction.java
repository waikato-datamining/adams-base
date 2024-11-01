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
 * AbstractScopeRestriction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution.debug;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.execution.ExecutionStage;

/**
 * Ancestor for scope restrictions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScopeRestriction
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4654096855875796107L;

  /**
   * Checks whether the specified actor falls within the scope.
   *
   * @param actor	the actor to check
   * @param stage	the execution stage
   * @return		true if within scope
   */
  public abstract boolean checkScope(Actor actor, ExecutionStage stage);
}
