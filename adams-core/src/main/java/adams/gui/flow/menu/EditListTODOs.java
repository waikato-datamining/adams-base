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
 * EditListTODOs.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.base.BaseRegExp;
import adams.flow.processor.ListTODOs;
import adams.gui.core.GUIHelper;

import java.awt.event.ActionEvent;

/**
 * Opens dialog for listing TODOs.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditListTODOs
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
    return "List TODOs";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    String 	regexp;
    BaseRegExp	re;

    regexp = GUIHelper.showInputDialog(m_State, "Please enter the regular expression for locating TODOs:", ".*TODO.*");
    if (regexp == null)
      return;

    re = new BaseRegExp();
    if (re.isValid(regexp)) {
      re.setValue(regexp);
      ListTODOs processor = new ListTODOs();
      processor.setRegExp(re);
      m_State.getCurrentPanel().processActors(processor);
    }
    else {
      GUIHelper.showConfirmMessage(m_State, "Invalid regular expression: " + regexp);
    }
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
