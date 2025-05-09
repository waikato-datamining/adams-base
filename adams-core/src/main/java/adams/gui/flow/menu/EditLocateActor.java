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
 * EditLocateActor.java
 * Copyright (C) 2014-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.StringHistory;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.gui.core.GUIHelper;

import java.awt.event.ActionEvent;

/**
 * Opens dialog for locating actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class EditLocateActor
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  protected static StringHistory m_History;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Locate actor";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    String	path;

    if (m_History == null) {
      m_History = new StringHistory();
      m_History.setHistoryFile(new PlaceholderFile(Environment.getInstance().getHome() + "/" + "HistoryLocateActor.list"));
    }

    path = GUIHelper.showInputDialog(
      m_State, "Please enter the full name of the actor (e.g., 'Flow.Sequence.Display'):",
      null, "Enter actor path", null, 40, 4, m_History);
    if (path == null)
      return;

    m_State.getCurrentPanel().getTree().locateAndDisplay(path, true);
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
