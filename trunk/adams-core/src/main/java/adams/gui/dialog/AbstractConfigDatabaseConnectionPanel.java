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
 * AbstractConfigDatabaseConnectionPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import adams.core.base.BasePassword;
import adams.db.AbstractDatabaseConnection;
import adams.db.ReconnectableDatabaseConnection;
import adams.gui.core.GUIHelper;

/**
 * A panel for connecting to a (config) database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConfigDatabaseConnectionPanel
  extends AbstractDatabaseConnectionPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5065572637368668864L;

  /** the database connection in use. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = getDefaultDatabaseConnection();
  }

  /**
   * Performs the reconnection.
   */
  protected void doReconnect() {
    m_ButtonConnect.setEnabled(false);

    if (getDatabaseConnection().isConnected()) {
      showStatus("Disconnecting...");
      getDatabaseConnection().disconnect();
    }
    else {
      showStatus("Connecting...");
      if (!((ReconnectableDatabaseConnection) getDatabaseConnection()).reconnect(
	  m_TextURL.getText(),
	  m_TextUser.getText(),
	  new BasePassword(m_TextPassword.getText()))) {
	GUIHelper.showErrorMessage(m_Self, "Failed to connect to " + m_TextURL.getText());
      }
      else {
	// add connection
	getDatabaseConnection().addConnection(
	    getDatabaseConnection().getCurrentConnection());
	// set as default (for session)
	if (getDatabaseConnection().getOwner() != null)
	  getDatabaseConnection().getOwner().setDefault(getDatabaseConnection());
      }
    }

    m_ButtonConnect.setEnabled(true);
    notifyChangeListeners();
    showStatus("");
    update();
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
