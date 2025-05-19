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
 * ChangeStorageName.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.MessageCollection;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.PropertyPath;
import adams.core.option.UserMode;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.gui.core.GUIHelper;
import adams.gui.core.UserModeUtils;
import adams.gui.flow.tree.postprocessor.StorageValueRenamed;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Renames the name of the storage item.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeStorageName
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  public static final String STORAGE_NAME = "storageName";

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Change storage name...";
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
      m_State.selNode.getActor().getClass(), STORAGE_NAME, StorageName.class, userMode);
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
    StorageName		nameOld;
    StorageName		nameNew;
    String		name;
    MessageCollection	errors;
    boolean		updated;
    StorageValueRenamed storageRenamed;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old name
    actorOld = m_State.selNode.getActor();
    errors   = new MessageCollection();
    nameOld = (StorageName) PropertyPath.getValue(actorOld, STORAGE_NAME, errors);
    if (nameOld == null)
      nameOld = new StorageName();

    // enter new name
    name = GUIHelper.showInputDialog(parent, "Please enter the new storage name:", nameOld.getValue());
    if (name == null)
      return;
    if (!Storage.isValidName(name)) {
      GUIHelper.showErrorMessage(parent, "Invalid storage name: " + name);
      return;
    }
    nameNew = new StorageName(name);
    if (nameNew.getValue().equals(nameOld.getValue()))
      return;

    // update actor
    actorNew = actorOld.shallowCopy();
    updated  = PropertyPath.setValue(actorNew, STORAGE_NAME, nameNew, errors);
    if (!updated) {
      GUIHelper.showErrorMessage(parent, "Failed to update storage name!");
      return;
    }
    addUndoPoint("Changed storage name to: " + nameNew);
    m_State.selNode.setActor(actorNew);
    if (!m_State.tree.getIgnoreNameChanges()) {
      int retVal = JOptionPane.showConfirmDialog(GUIHelper.getParentComponent(m_State.tree), "Propagate changes throughout the tree?");
      if (retVal == JOptionPane.YES_OPTION) {
	storageRenamed = new StorageValueRenamed();
	storageRenamed.postProcess(m_State.tree, m_State.parent.getActor(), actorOld, actorNew);
      }
      else if (!m_State.tree.getIgnoreNameChangesUserPrompted()) {
	retVal = JOptionPane.showConfirmDialog(GUIHelper.getParentComponent(m_State.tree), "Do you want to ignore name changes for this flow?");
	if (retVal == JOptionPane.YES_OPTION)
	  m_State.tree.setIgnoreNameChanges(true);
	m_State.tree.setIgnoreNameChangesUserPrompted(true);
      }
    }
  }
}
