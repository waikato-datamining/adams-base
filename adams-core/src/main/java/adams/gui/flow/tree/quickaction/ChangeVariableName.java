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
 * ChangeVariableName.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.MessageCollection;
import adams.core.VariableName;
import adams.core.Variables;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.PropertyPath;
import adams.core.option.UserMode;
import adams.flow.core.Actor;
import adams.gui.core.GUIHelper;
import adams.gui.core.UserModeUtils;
import adams.gui.flow.tree.postprocessor.VariableRenamed;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Renames the name of the variable.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeVariableName
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  public static final String VARIABLE_NAME = "variableName";

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Change variable name...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    UserMode	userMode;
    boolean	hasMethod;

    userMode  = UserModeUtils.getUserMode(m_State.tree);
    hasMethod = IntrospectionHelper.hasProperty(
      m_State.selNode.getActor().getClass(), VARIABLE_NAME, VariableName.class, userMode);
    setEnabled(m_State.editable && m_State.isSingleSel && hasMethod);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Component		parent;
    Actor		actorOld;
    Actor 		actorNew;
    VariableName	nameOld;
    VariableName	nameNew;
    String		name;
    MessageCollection	errors;
    boolean		updated;
    VariableRenamed	varRenamed;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old name
    actorOld = m_State.selNode.getActor();
    errors   = new MessageCollection();
    nameOld = (VariableName) PropertyPath.getValue(actorOld, VARIABLE_NAME, errors);
    if (nameOld == null)
      nameOld = new VariableName();

    // enter new name
    name = GUIHelper.showInputDialog(parent, "Please enter the new variable name:", nameOld.getValue());
    if (name == null)
      return;
    if (!Variables.isValidName(name)) {
      GUIHelper.showErrorMessage(parent, "Invalid variable name: " + name);
      return;
    }
    nameNew = new VariableName(name);
    if (nameNew.getValue().equals(nameOld.getValue()))
      return;

    // update actor
    actorNew = actorOld.shallowCopy();
    updated  = PropertyPath.setValue(actorNew, VARIABLE_NAME, nameNew, errors);
    if (!updated) {
      GUIHelper.showErrorMessage(parent, "Failed to update variable name!");
      return;
    }
    addUndoPoint("Changed variable name to:" + nameNew);
    m_State.selNode.setActor(actorNew);
    if (!m_State.tree.getIgnoreNameChanges()) {
      if (JOptionPane.showConfirmDialog(GUIHelper.getParentComponent(m_State.tree), "Propagate changes throughout the tree?") == JOptionPane.YES_OPTION) {
	varRenamed = new VariableRenamed();
	varRenamed.postProcess(m_State.tree, m_State.parent.getActor(), actorOld, actorNew);
      }
    }
  }
}
