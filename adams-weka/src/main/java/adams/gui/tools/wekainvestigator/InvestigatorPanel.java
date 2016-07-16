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
 * InvestigatorPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator;

import adams.gui.core.BaseStatusBar;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;
import adams.gui.tools.wekainvestigator.tab.InvestigatorTabbedPane;
import adams.gui.tools.wekainvestigator.tab.LogTab;
import adams.gui.workspace.AbstractWorkspacePanel;

import javax.swing.JMenuBar;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * The main panel for the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorPanel
  extends AbstractWorkspacePanel {

  private static final long serialVersionUID = 7442747356297265526L;

  /** the tabbed pane for the tabs. */
  protected InvestigatorTabbedPane m_TabbedPane;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the log. */
  protected StringBuilder m_Log;

  /** the data loaded. */
  protected List<DataContainer> m_Data;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Log  = new StringBuilder();
    m_Data = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    LogTab	log;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new InvestigatorTabbedPane(this);
    add(m_TabbedPane, BorderLayout.CENTER);

    log = new LogTab();
    log.setOwner(this);
    m_TabbedPane.addTab(log.getTitle(), log);

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    // TODO
    return null;
  }

  /**
   * Updates the title of the dialog.
   */
  @Override
  protected void updateTitle() {
    setParentTitle(m_TitleGenerator.generate(getDefaultTitle()));
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateMenu() {
    // TODO
  }

  /**
   * Returns the default title.
   *
   * @return		the default title
   */
  @Override
  protected String getDefaultTitle() {
    return "Investigator";
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  @Override
  public void logMessage(String msg) {
    m_Log.append(msg);
    m_Log.append("\n");
    m_StatusBar.showStatus(msg);
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  @Override
  public void logError(String msg, String title) {
    logMessage(msg);
    GUIHelper.showErrorMessage(this, msg, title);
  }

  /**
   * Returns the internal log buffer.
   *
   * @return		the buffer
   */
  public StringBuilder getLog() {
    return m_Log;
  }

  /**
   * Empties the log.
   */
  public void clearLog() {
    m_Log.setLength(0);
  }

  /**
   * Returns the currently loaded data.
   *
   * @return		the data
   */
  public List<DataContainer> getData() {
    return m_Data;
  }

  /**
   * Notifies all the tabs that the data has changed.
   */
  public void fireDataChange() {
    int		i;

    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      ((AbstractInvestigatorTab) m_TabbedPane.getTabComponentAt(i)).dataChanged();
  }
}
