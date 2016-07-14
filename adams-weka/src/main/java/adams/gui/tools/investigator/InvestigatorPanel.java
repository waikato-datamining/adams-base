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

package adams.gui.tools.investigator;

import adams.gui.workspace.AbstractWorkspacePanel;

import javax.swing.JMenuBar;

/**
 * The main panel for the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorPanel
  extends AbstractWorkspacePanel {

  private static final long serialVersionUID = 7442747356297265526L;

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
    // TODO
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
    // TODO
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  @Override
  public void logError(String msg, String title) {
    // TODO
  }
}
