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

/*
 * SqlConnectionPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.sqlworkbench;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionProvider;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.dialog.DatabaseConnectionDialog;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Panel for database connection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SqlConnectionPanel
  extends BasePanel
  implements DatabaseConnectionProvider {

  private static final long serialVersionUID = 8008764357731780782L;

  /** the label for the showing the active connection. */
  protected JLabel m_LabelConnection;

  /** the button for opening the connection dialog. */
  protected BaseButton m_ButtonConnection;

  /** the current connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = adams.db.DatabaseConnection.getSingleton();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_LabelConnection = new JLabel();
    add(m_LabelConnection, BorderLayout.CENTER);

    m_ButtonConnection = new BaseButton("...");
    m_ButtonConnection.setToolTipText("Opens dialog to select database connection");
    m_ButtonConnection.addActionListener((ActionEvent e) -> selectConnection());
    add(m_ButtonConnection, BorderLayout.EAST);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateConnection();
  }

  /**
   * Allows the user to select a connection.
   */
  protected void selectConnection() {
    DatabaseConnectionDialog 	dialog;

    if (getParentDialog() != null)
      dialog = new DatabaseConnectionDialog(getParentDialog());
    else
      dialog = new DatabaseConnectionDialog(getParentFrame());
    dialog.setDefaultCloseOperation(DatabaseConnectionDialog.DISPOSE_ON_CLOSE);
    dialog.setLocationRelativeTo(getParent());
    dialog.setVisible(true);
    m_DatabaseConnection = dialog.getConnectionPanel().getDatabaseConnection();
    updateConnection();
  }

  /**
   * Updates the displayed connection.
   */
  protected void updateConnection() {
    m_LabelConnection.setText(
      (m_DatabaseConnection.getUser().isEmpty() ? "" : m_DatabaseConnection.getUser() + "@")
      + m_DatabaseConnection.getURL());
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }
}
