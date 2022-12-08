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
 * EditParseError.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.core.GUIHelper;
import adams.gui.flow.tabhandler.ParsedErrorHandler;

import java.awt.event.ActionEvent;

/**
 * Opens dialog for parsing an error.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RunParseError
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
    return "Parse error";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    String error;
    ParsedErrorHandler  handler;

    error = GUIHelper.showInputDialog(
      m_State, "Please enter the error string:",
      null, "Enter error", null, 40, 4);
    if (error == null)
      return;

    handler = m_State.getCurrentPanel().getTabHandler(ParsedErrorHandler.class);
    if (handler == null)
      return;

    handler.add(m_State.getCurrentPanel(), error);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.hasCurrentPanel());
  }
}
