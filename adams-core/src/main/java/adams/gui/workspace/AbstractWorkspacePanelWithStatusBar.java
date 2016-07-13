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
 * AbstractWorkspacePanelWithStatusBar.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.workspace;

import adams.core.StatusMessageHandler;
import adams.gui.core.BaseStatusBar;

import java.awt.BorderLayout;

/**
 * The ancestor for a workspace panel that also have a statusbar.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see BaseStatusBar
 */
public abstract class AbstractWorkspacePanelWithStatusBar
  extends AbstractWorkspacePanel
  implements StatusMessageHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7314544066929763500L;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Displays a message.
   * 
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }
}
