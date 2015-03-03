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
 * DatabaseConnectionPanel.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import adams.core.base.BasePassword;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.env.Project;
import adams.gui.core.GUIHelper;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.Connect;
import adams.gui.scripting.Disconnect;
import adams.gui.scripting.ScriptingCommandCode;
import adams.gui.scripting.ScriptingEngine;

/**
 * A panel for connecting to a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseConnectionPanel
  extends AbstractDatabaseConnectionPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5065572637368668864L;

  /** the scripting engine to use. */
  protected AbstractScriptingEngine m_ScriptingEngine;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ScriptingEngine = getDefaultScriptingEngine();
  }

  /**
   * Returns the default scripting engine.
   *
   * @return		the scripting engine
   */
  protected AbstractScriptingEngine getDefaultScriptingEngine() {
    return ScriptingEngine.getSingleton(getDefaultDatabaseConnection());
  }

  /**
   * Returns the default database connection to use.
   *
   * @return		the database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * The title of the connection panel.
   *
   * @return		the title
   */
  @Override
  protected String getTitle() {
    return Project.NAME.toUpperCase() + "/" + "data";
  }

  /**
   * Performs the reconnection.
   */
  @Override
  protected void doReconnect() {
    String 	cmd;

    m_ButtonConnect.setEnabled(false);

    if (getDatabaseConnection().isConnected()) {
      cmd = Disconnect.ACTION;
      showStatus("Disconnecting...");
    }
    else {
      cmd = Connect.ACTION + " "
      + m_TextURL.getText() + " "
      + m_TextUser.getText() + " "
      + new BasePassword(m_TextPassword.getText()).stringValue() + " "
      + m_ComboBoxLoggingLevel.getSelectedItem() + " "
      + m_CheckBoxConnectOnStartUp.isSelected() + " "
      + m_CheckBoxAutoCommit.isSelected();
      showStatus("Connecting...");
    }

    m_ScriptingEngine.add(
	DatabaseConnectionPanel.this,
	cmd,
	new ScriptingCommandCode() {
	  @Override
	  public void execute() {
	    m_ButtonConnect.setEnabled(true);

	    try {
	      if (hasError()) {
		GUIHelper.showErrorMessage(m_Self, getError());
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
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }

	    notifyChangeListeners();
	    showStatus("");
	    update();
	  }
	});
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  @Override
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_ScriptingEngine.getDatabaseConnection();
  }
}
