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
 * RemoteControlCenterManagerPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.BaseTabbedPane;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.gui.workspace.AbstractWorkspaceManagerPanel;
import adams.scripting.engine.RemoteScriptingEngine;

import java.awt.BorderLayout;

/**
 * Interface for remote controls.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteControlCenterManagerPanel
  extends AbstractWorkspaceManagerPanel<RemoteControlCenterPanel>
  implements RemoteScriptingEngineUpdateListener {

  private static final long serialVersionUID = -8970736649780738899L;

  /** the owning application. */
  protected AbstractApplicationFrame m_Owner;

  /** the tabbed pane for the engines and sessions. */
  protected BaseTabbedPane m_TabbedPaneMain;

  /** the panel for the engines. */
  protected RemoteControlCenterEnginesPanel m_PanelEngines;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_TabbedPaneMain = new BaseTabbedPane(BaseTabbedPane.TOP);

    m_PanelEngines = new RemoteControlCenterEnginesPanel();
    m_TabbedPaneMain.addTab("Engines", m_PanelEngines);
    m_TabbedPaneMain.addTab("Commands", m_SplitPane);

    add(m_TabbedPaneMain, BorderLayout.CENTER);
  }

  /**
   * Sets the owning application.
   *
   * @param value	the owner
   */
  public void setOwner(AbstractApplicationFrame value) {
    if (m_Owner != null)
      m_Owner.removeRemoteScriptingEngineUpdateListener(this);

    m_Owner = value;

    if (m_Owner != null)
      m_Owner.addRemoteScriptingEngineUpdateListener(this);

    m_PanelEngines.setOwner(value);
  }

  /**
   * Returns the owning application.
   *
   * @return		the owner
   */
  public AbstractApplicationFrame getOwner() {
    return m_Owner;
  }

  /**
   * The default name for a workspace.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultWorkspaceName() {
    return "Remote session";
  }

  /**
   * Returns a new workspace instance.
   *
   * @param init	whether to initialize the workspace
   * @return		the workspace
   */
  @Override
  protected RemoteControlCenterPanel newWorkspace(boolean init) {
    RemoteControlCenterPanel	result;

    result = new RemoteControlCenterPanel();
    result.setOwner(this);

    return result;
  }

  /**
   * Instantiates a new panel for workspaces.
   *
   * @return		the list panel
   */
  @Override
  protected RemoteControlCenterWorkspaceList newWorkspaceList() {
    return new RemoteControlCenterWorkspaceList();
  }

  /**
   * Sets the scripting engine to use.
   *
   * @param value	the engine
   */
  public void setRemoteScriptingEngine(RemoteScriptingEngine value) {
    if (m_Owner != null)
      m_Owner.setRemoteScriptingEngine(value);
  }

  /**
   * Returns the current scripting engine.
   *
   * @return		the engine
   */
  public RemoteScriptingEngine getRemoteScriptingEngine() {
    if (m_Owner != null)
      return m_Owner.getRemoteScriptingEngine();
    else
      return null;
  }

  /**
   * Gets called in case the remote scripting engine got updated.
   *
   * @param e		the event
   */
  public void remoteScriptingEngineUpdated(RemoteScriptingEngineUpdateEvent e) {
    int		i;

    for (i = 0; i < count(); i++)
      getPanel(i).remoteScriptingEngineUpdated(e);
  }

  /**
   * Returns the application frame this panel belongs to.
   *
   * @return		the frame, null if not part of an app frame
   */
  public AbstractApplicationFrame getApplicationFrame() {
    return getOwner();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();

    if (m_Owner != null) {
      m_Owner.removeRemoteScriptingEngineUpdateListener(this);
      m_Owner = null;
    }

    m_PanelEngines.cleanUp();
  }
}
