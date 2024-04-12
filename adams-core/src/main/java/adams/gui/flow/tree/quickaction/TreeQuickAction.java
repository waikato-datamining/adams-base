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
 * TreeQuickAction.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.quickaction;

import adams.gui.action.PropertiesAction;
import adams.gui.flow.tree.StateContainer;

import javax.swing.KeyStroke;

/**
 * Interface for menu items in the quick action menu of the flow tree.
 * 
 * @author fracpete
 */
public interface TreeQuickAction
  extends PropertiesAction<StateContainer> {
  
  /**
   * Checks whether the keystroke matches.
   * 
   * @param ks		the keystroke to match
   * @return		true if a match
   */
  public boolean keyStrokeApplies(KeyStroke ks);
  
  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  public void addUndoPoint(String comment);
}
