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
 * AbstractFlowEditorCheckBoxMenuItemAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import java.awt.Dialog;
import java.awt.Frame;

import adams.core.Properties;
import adams.gui.action.AbstractPropertiesCheckBoxMenuItemAction;
import adams.gui.application.Child;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Ancestor for checkbox menu item actions in the flow editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFlowEditorCheckBoxMenuItemAction
  extends AbstractPropertiesCheckBoxMenuItemAction<FlowEditorPanel, GenericObjectEditorDialog>
  implements FlowEditorAction {
  
  /** for serialization. */
  private static final long serialVersionUID = -6842831257705457783L;
  
  /**
   * Returns the underlying properties.
   * 
   * @return		the properties
   */
  @Override
  protected Properties getProperties() {
    return FlowEditorPanel.getPropertiesMenu();
  }

  /**
   * Tries to determine the parent frame.
   *
   * @return		the parent frame if one exists or null if not
   */
  protected Frame getParentFrame() {
    return GUIHelper.getParentFrame(m_State);
  }

  /**
   * Tries to determine the parent dialog.
   *
   * @return		the parent dialog if one exists or null if not
   */
  protected Dialog getParentDialog() {
    return GUIHelper.getParentDialog(m_State);
  }

  /**
   * Tries to determine the parent child window/frame.
   *
   * @return		the parent child window/frame if one exists or null if not
   */
  protected Child getParentChild() {
    return GUIHelper.getParentChild(m_State);
  }

  /**
   * Returns whether the flow accepts input.
   * 
   * @return		true if user can change flow
   */
  protected boolean isInputEnabled() {
    return
	   !m_State.isRunning() 
	&& !m_State.isStopping() 
	&& !m_State.isSwingWorkerRunning();
  }
}
