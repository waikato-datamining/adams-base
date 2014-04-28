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
 * IndexedBooleanCondition.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 * Boolean conditions that return also an index, as they do encapsulate
 * multiple boolean conditions at the same time. The index then defines
 * which of the rules applied.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface IndexedBooleanCondition
  extends BooleanCondition {

  /**
   * Returns the index of the case that should get executed.
   * 
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		the index, -1 if not available
   */
  public int getCaseIndex(Actor owner, Token token);

  /**
   * Returns the index of the default case.
   * 
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		the index, -1 if not available
   */
  public int getDefaultCaseIndex(Actor owner, Token token);
}
