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
 * EditStringConstantsStrings.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.flow.source.StringConstants;
import adams.gui.core.GUIHelper;

import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Lets the user edit the strings of a StringConstants source.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EditStringConstantsStrings
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  public static final String VARIABLE_VALUE = "variableValue";

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Edit strings...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.isSingleSel && (m_State.selNode.getActor() instanceof StringConstants));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Component		parent;
    StringConstants 	actor;
    BaseString[] 	valueOld;
    String		valueOldStr;
    BaseString[]	valueNew;
    String 		valueNewStr;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old value
    actor       = (StringConstants) m_State.selNode.getActor();
    valueOld    = actor.getStrings();
    valueOldStr = Utils.flatten(BaseObject.toStringArray(valueOld), "\n");

    // enter new value
    valueNewStr = GUIHelper.showInputDialog(parent, "Enter the strings (one per line, Ctrl+Enter for new line):", valueOldStr, "Edit strings", null, 40, 5);
    if (valueNewStr == null)
      return;
    if (valueNewStr.equals(valueOldStr))
      return;
    valueNew = (BaseString[]) BaseObject.toObjectArray(valueNewStr.split("\n"), BaseString.class);

    // update actor
    addUndoPoint("Updated strings");
    actor.setStrings(valueNew);
    updateSelectedActor(actor);
  }
}
