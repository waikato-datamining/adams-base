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
 * ScriptingDialog.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import java.awt.Dialog;
import java.awt.Frame;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.gui.core.BasePanel;

/**
 * A dialog for loading/saving and executing scripts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingDialog
  extends AbstractScriptingDialog {

  /** for serialization. */
  private static final long serialVersionUID = 8200417116225554201L;

  /**
   * Creates a non-modal dialog.
   *
   * @param owner	the owning dialog
   * @param panel	the base panel this dialog belongs to
   */
  public ScriptingDialog(Dialog owner, BasePanel panel) {
    super(owner, panel);
  }

  /**
   * Creates a non-modal dialog.
   *
   * @param owner	the owning frame
   * @param panel	the base panel this dialog belongs to
   */
  public ScriptingDialog(Frame owner, BasePanel panel) {
    super(owner, panel);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  public AbstractScriptingEngine getScriptingEngine() {
    return ScriptingEngine.getSingleton(getDatabaseConnection());
  }
}
