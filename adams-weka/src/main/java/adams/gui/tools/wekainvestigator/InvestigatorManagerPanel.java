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
import adams.gui.workspace.AbstractWorkspaceListPanel;
import adams.gui.workspace.AbstractWorkspaceManagerPanel;

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
  extends AbstractWorkspaceManagerPanel<InvestigatorPanel> {

  private static final long serialVersionUID = -5959114946146695938L;

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
    return new InvestigatorPanel();
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
