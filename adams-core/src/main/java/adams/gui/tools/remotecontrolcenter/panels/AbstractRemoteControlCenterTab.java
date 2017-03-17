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
 * AbstractRemoteControlCenterTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.BasePanel;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.gui.tools.remotecontrolcenter.RemoteControlCenterPanel;

/**
 * Ancestor for tabs to be shown in the Remote Control Center.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteControlCenterTab
  extends BasePanel
  implements RemoteScriptingEngineUpdateListener {

  private static final long serialVersionUID = 5896827287386114875L;

  /** the owning control center panel. */
  protected RemoteControlCenterPanel m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteControlCenterPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public RemoteControlCenterPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the application frame this tab belongs to.
   *
   * @return		the frame, null if not part of an app frame
   */
  public AbstractApplicationFrame getApplicationFrame() {
    if (getOwner() != null)
      return getOwner().getApplicationFrame();
    return null;
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  public abstract String getTabIcon();

  /**
   * Gets called in case the remote scripting engine got updated.
   *
   * @param e		the event
   */
  public void remoteScriptingEngineUpdated(RemoteScriptingEngineUpdateEvent e) {
  }
}
