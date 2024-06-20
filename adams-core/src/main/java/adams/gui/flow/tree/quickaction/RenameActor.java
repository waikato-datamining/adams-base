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
 * RenameActor.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import java.awt.event.ActionEvent;

/**
 * Renames an actor.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RenameActor
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Rename actor...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.isSingleSel);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    m_State.tree.getOperations().renameActor(m_State.selPath);
  }
}
