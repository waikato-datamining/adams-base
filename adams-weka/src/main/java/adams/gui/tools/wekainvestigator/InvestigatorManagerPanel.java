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
 * InvestigatorManagerPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator;

import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.workspace.AbstractSerializableWorkspaceManagerPanel;
import adams.gui.workspace.AbstractWorkspaceHelper;
import adams.gui.workspace.AbstractWorkspaceListPanel;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Manages multiple sessions of the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorManagerPanel
  extends AbstractSerializableWorkspaceManagerPanel<InvestigatorPanel, InvestigatorPanelHandler> {

  private static final long serialVersionUID = -5959114946146695938L;

  /** the session counter. */
  protected int m_Counter;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Counter = 0;
  }

  /**
   * The default name for a workspace.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultWorkspaceName() {
    return "Session";
  }

  /**
   * Returns a new workspace instance.
   *
   * @return		the workspace
   */
  @Override
  protected InvestigatorPanel newWorkspace() {
    InvestigatorPanel	result;

    result = new InvestigatorPanel();
    result.addDefaultTabs();

    m_Counter++;

    return result;
  }

  /**
   * Returns a new instance of the workspace helper to use.
   *
   * @return		the workspace helper
   */
  @Override
  protected AbstractWorkspaceHelper<InvestigatorPanel, AbstractSerializableWorkspaceManagerPanel<InvestigatorPanel, InvestigatorPanelHandler>, InvestigatorPanelHandler> newWorkspaceHelper() {
    return new InvestigatorWorkspaceHelper();
  }

  /**
   * Copies a workspace.
   */
  @Override
  protected void copyWorkspace() {
    String 		nameOld;
    String 		nameNew;
    InvestigatorPanel	panelOld;
    InvestigatorPanel	panelNew;

    nameOld = getHistory().getSelectedEntry();
    nameNew = GUIHelper.showInputDialog(this, "Please enter name for workspace", nameOld + " (" + (m_Counter+1) + ")");
    if (nameNew == null)
      return;
    if (nameNew.equals(nameOld)) {
      GUIHelper.showErrorMessage(this, "No new name for workspace supplied, aborting!");
      return;
    }
    if (m_History.hasEntry(nameNew)) {
      GUIHelper.showErrorMessage(this, "Workspace name already present, aborting!");
      return;
    }

    try {
      panelOld = m_History.getEntry(nameOld);
      panelNew = m_WorkspaceHelper.copy(panelOld);
      m_History.addEntry(nameNew, panelNew);
      m_History.setSelectedEntry(nameNew);
      m_Counter++;
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(this, "Failed to copy workspace!", e);
    }
  }

  /**
   * Instantiates a new panel for workspaces.
   *
   * @return		the list panel
   */
  @Override
  protected AbstractWorkspaceListPanel<InvestigatorPanel> newWorkspaceList() {
    return new InvestigatorWorkspaceList();
  }

  /**
   * Just for testing.
   *
   * @param args	ignored
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    final InvestigatorManagerPanel panel = new InvestigatorManagerPanel();
    BaseFrame frame = new BaseFrame("Investigator");
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        panel.cleanUp();
      }
    });
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setSize(GUIHelper.makeWider(GUIHelper.getDefaultLargeDialogDimension()));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
