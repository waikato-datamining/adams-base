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
 * DatabaseConnections.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.gui.core.GUIHelper;
import adams.gui.dialog.DatabaseConnectionsPanel;

/**
 * Initializes the database connections.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseConnections
  extends AbstractInitialization {

  /** for serialization. */
  private static final long serialVersionUID = 8918944463363086116L;

  /**
   * The title of the initialization.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Database connections";
  }
  
  /**
   * Performs the initialization.
   * 
   * @param parent	the application this initialization is for
   * @return		true if successful
   */
  @Override
  public boolean initialize(final AbstractApplicationFrame parent) {
    if (!GUIHelper.isHeadless())
      new DatabaseConnectionsPanel();
    else
      System.err.println(getClass().getName() + ": Headless environment, skipping init!");
    return true;
  }
}
