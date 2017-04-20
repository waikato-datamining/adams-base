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
 * AbstractAddBreakpointAction.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.control.Breakpoint;
import adams.flow.core.Actor;
import adams.flow.execution.debug.DebugScopeRestrictionHelper;
import adams.gui.flow.tree.TreeOperations.InsertPosition;

import javax.swing.tree.TreePath;

/**
 * Ancestor for adding breakpoint actors.
 * 
 * @author fracpete
 * @version $Revision$
 */
public abstract class AbstractAddBreakpointAction
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;

  /**
   * Tries to figure what actors fit best in the tree at the given position.
   *
   * @param path	the path where to insert the actors
   * @param position	how the actors are to be inserted
   * @return		the actors
   */
  protected Actor suggestBreakpoint(TreePath path, InsertPosition position) {
    Breakpoint 		result;

    result = new Breakpoint();
    result.setScopeRestriction(DebugScopeRestrictionHelper.getDebugScopeRestriction(m_State.selNode));

    return result;
  }
}
