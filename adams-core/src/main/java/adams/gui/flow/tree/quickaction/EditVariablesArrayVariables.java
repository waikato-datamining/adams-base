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
 * EditVariablesArrayVariables.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseObject;
import adams.flow.source.VariablesArray;
import adams.gui.core.GUIHelper;

import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Lets the user edit the variable names of a VariablesArray source.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EditVariablesArrayVariables
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Edit variables...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.isSingleSel && (m_State.selNode.getActor() instanceof VariablesArray));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Component		parent;
    VariablesArray 	actor;
    VariableName[] 	valueOld;
    String		valueOldStr;
    VariableName[]	valueNew;
    String 		valueNewStr;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old value
    actor       = (VariablesArray) m_State.selNode.getActor();
    valueOld    = actor.getVariableNames();
    valueOldStr = Utils.flatten(BaseObject.toStringArray(valueOld), "\n");

    // enter new value
    valueNewStr = GUIHelper.showInputDialog(parent, "Enter the variable names (one per line, Ctrl+Enter for new line):", valueOldStr, "Edit variables", null, 40, 5);
    if (valueNewStr == null)
      return;
    if (valueNewStr.equals(valueOldStr))
      return;
    valueNew = (VariableName[]) BaseObject.toObjectArray(valueNewStr.split("\n"), VariableName.class);

    // update actor
    addUndoPoint("Updated variables");
    actor.setVariableNames(valueNew);
    updateSelectedActor(actor);
  }
}
