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
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.control.Breakpoint;
import adams.flow.core.Actor;
import adams.gui.flow.tree.BreakpointSuggestion;
import adams.gui.flow.tree.TreeOperations.InsertPosition;
import adams.parser.ActorSuggestion.SuggestionData;

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
    Actor		result;
    SuggestionData 	context;
    Actor[]		suggestions;

    context     = m_State.tree.getOperations().configureSuggestionContext(path, position);
    suggestions = BreakpointSuggestion.getSingleton().suggest(context);
    if (suggestions.length > 0)
      result = suggestions[0];
    else
      result = new Breakpoint();

    return result;
  }
}
