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
 * RemoteControlCenterLogPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.SimpleLogPanel;

import java.awt.BorderLayout;

/**
 * Displays logging information.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteControlCenterLogPanel
  extends BasePanel {

  private static final long serialVersionUID = 4110604734547592494L;

  /** the owner. */
  protected RemoteControlCenterManagerPanel m_Owner;

  /** the request log. */
  protected SimpleLogPanel m_LogRequest;

  /** the response log. */
  protected SimpleLogPanel m_LogResponse;

  /** the log for other messages. */
  protected SimpleLogPanel m_LogOther;

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    m_LogRequest = new SimpleLogPanel();
    m_TabbedPane.addTab("Requests", m_LogRequest);

    m_LogResponse = new SimpleLogPanel();
    m_TabbedPane.addTab("Responses", m_LogResponse);

    m_LogOther = new SimpleLogPanel();
    m_TabbedPane.addTab("Other", m_LogOther);
  }

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  public String getTabIcon() {
    return "log.gif";
  }

  /**
   * Sets the owning application.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteControlCenterManagerPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owning application.
   *
   * @return		the owner
   */
  public RemoteControlCenterManagerPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the application frame this panel belongs to.
   *
   * @return		the frame, null if not part of an app frame
   */
  public AbstractApplicationFrame getApplicationFrame() {
    if (getOwner() != null)
      return getOwner().getApplicationFrame();
    return null;
  }

  /**
   * Returns the log panel.
   *
   * @return		the panel, null if no owner set
   */
  public RemoteControlCenterLogPanel getLogPanel() {
    if (getOwner() != null)
      return getOwner().getLogPanel();
    return null;
  }

  /**
   * Returns the log for requests.
   *
   * @return		the log
   */
  public SimpleLogPanel getRequestLog() {
    return m_LogRequest;
  }

  /**
   * Returns the log for response.
   *
   * @return		the log
   */
  public SimpleLogPanel getResponseLog() {
    return m_LogResponse;
  }

  /**
   * Returns the log for other messages.
   *
   * @return		the log
   */
  public SimpleLogPanel getOtherLog() {
    return m_LogOther;
  }
}
