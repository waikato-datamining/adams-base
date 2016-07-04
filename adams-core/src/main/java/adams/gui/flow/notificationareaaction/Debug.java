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
 * Debug.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.notificationareaaction;

import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanel;

import java.awt.event.ActionEvent;

/**
 * Debugs the flow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Debug
  extends AbstractNotificationAreaAction {

  private static final long serialVersionUID = -2884370713454014768L;

  /**
   * Instantiates the action.
   */
  public Debug() {
    super();
    setName("Debug");
    setIcon(GUIHelper.getIcon("run_debug.png"));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    if (getOwner().getOwner() instanceof FlowPanel)
      ((FlowPanel) getOwner().getOwner()).run(true, true);
    getOwner().clearNotification();
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(getOwner().getOwner() instanceof FlowPanel);
  }
}
