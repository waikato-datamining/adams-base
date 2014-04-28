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
 * RunToolPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.db.DatabaseConnection;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.ScriptingEngine;

/**
 * A panel for executing tools from the GUI.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunToolPanel
  extends AbstractRunToolPanel {

  /** for serialization. */
  private static final long serialVersionUID = -2859158797113257026L;

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  public AbstractScriptingEngine getScriptingEngine() {
    // DatabaseConnection.getSingleton() is OK, since only run from main GUI
    return ScriptingEngine.getSingleton(DatabaseConnection.getSingleton());
  }
}
