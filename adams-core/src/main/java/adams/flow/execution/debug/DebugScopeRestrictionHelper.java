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
 * DebugScopeRestrictionHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;

/**
 * Helper class for scope restrictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DebugScopeRestrictionHelper {

  /**
   * Goes up in the flow locating the first {@link DebugScopeRestrictionHandler}
   * to determine any different scope restrictions.
   *
   * @param actor	the actor to start with
   * @return		the scope restriction, default is {@link NoScopeRestriction}
   */
  public static AbstractScopeRestriction getDebugScopeRestriction(Actor actor) {
    AbstractScopeRestriction	result;

    result = new NoScopeRestriction();

    while (actor != null) {
      if (actor instanceof DebugScopeRestrictionHandler) {
	result = ((DebugScopeRestrictionHandler) actor).getDebugScopeRestriction();
	break;
      }
      actor = actor.getParent();
    }

    return result;
  }

  /**
   * Goes up in the flow locating the first {@link DebugScopeRestrictionHandler}
   * to determine any different scope restrictions.
   *
   * @param node	the node to start with
   * @return		the scope restriction, default is {@link NoScopeRestriction}
   */
  public static AbstractScopeRestriction getDebugScopeRestriction(Node node) {
    AbstractScopeRestriction	result;

    result = new NoScopeRestriction();

    while (node != null) {
      if (node.getActor() instanceof DebugScopeRestrictionHandler) {
	result = ((DebugScopeRestrictionHandler) node.getActor()).getDebugScopeRestriction(node);
	break;
      }
      node = (Node) node.getParent();
    }

    return result;
  }
}
