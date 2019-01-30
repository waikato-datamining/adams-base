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
 * CompareJustActors.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;

/**
 * Performs a diff on two actors.
 * 
 * @author fracpete
 */
public class CompareJustActors
  extends AbstractCompareActors {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Just actors";
  }

  /**
   * Turns the node into an actor.
   *
   * @param node	the node to get the actor from
   * @return		the retrieved actor
   */
  protected Actor nodeToActor(Node node) {
    return node.getActor();
  }
}
