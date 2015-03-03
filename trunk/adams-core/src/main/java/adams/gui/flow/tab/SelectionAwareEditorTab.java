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
 * SelectionAwareEditorTab.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import javax.swing.tree.TreePath;

import adams.flow.core.AbstractActor;

/**
 * Interface for flow editor tabs that need to react to changes of
 * currently selected actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SelectionAwareEditorTab {

  /**
   * Notifies the tab of the currently selected actors.
   *
   * @param paths	the selected paths
   * @param actors	the currently selected actors
   */
  public void actorSelectionChanged(TreePath[] paths, AbstractActor[] actors);
}
