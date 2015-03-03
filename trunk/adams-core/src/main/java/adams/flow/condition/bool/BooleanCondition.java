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
 * BooleanCondition.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 * Ancestor for conditions that get evaluated in, e.g., the IfThenElse control 
 * actor, deciding which branch to take.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface BooleanCondition
  extends ShallowCopySupporter<BooleanCondition>, QuickInfoSupporter {

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  public String getQuickInfo();

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted class
   */
  public Class[] accepts();

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  public String setUp(Actor owner);

  /**
   * Evaluates whether to executed the "then" or "else" branch.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		true if the condition applies
   */
  public boolean evaluate(Actor owner, Token token);

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public BooleanCondition shallowCopy();
  
  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public BooleanCondition shallowCopy(boolean expand);
}
