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
 * CloseAndCleanUp.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.notificationareaaction;

import adams.gui.core.GUIHelper;

import java.awt.event.ActionEvent;

/**
 * Closes the area panel and cleans up GUI output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CloseAndCleanUp
  extends AbstractNotificationAreaAction {

  private static final long serialVersionUID = -2884370713454014768L;

  /**
   * Instantiates the action.
   */
  public CloseAndCleanUp() {
    super();
    setName("Close/clean up");
    setIcon(GUIHelper.getIcon("close_window.png"));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    if (m_Owner.getOwner() != null)
      m_Owner.getOwner().cleanUp();
    m_Owner.clearNotification();
    m_Owner.notifyCloseListeners();
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled((m_Owner.getOwner().getLastFlow() != null));
  }
}
