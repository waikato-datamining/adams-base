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
 * AbstractFlowEditorSubMenuAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.Properties;
import adams.gui.action.AbstractPropertiesSubMenuAction;
import adams.gui.flow.FlowEditorPanel;

/**
 * Ancestor for actions in the flow editor that generate a submenu.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFlowEditorSubMenuAction
  extends AbstractPropertiesSubMenuAction<FlowEditorPanel>
  implements FlowEditorAction {

  /** for serialization. */
  private static final long serialVersionUID = 1168747259624542350L;
  
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
