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
 * EditLocateStorageName.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.flow.processor.ListStorageUsage;
import adams.gui.core.GUIHelper;

import java.awt.event.ActionEvent;

/**
 * Opens dialog for locating a storage item.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditLocateStorageName
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Locate storage name";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    String name;

    name = GUIHelper.showInputDialog(m_State, "Please enter the name of the storage item to locate:");
    if (name == null)
      return;

    ListStorageUsage processor = new ListStorageUsage();
    processor.setName(name);
    m_State.getCurrentPanel().processActors(processor);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel() 
	&& !m_State.isSwingWorkerRunning());
  }
}
