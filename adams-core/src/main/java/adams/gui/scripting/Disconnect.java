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
 * Disconnect.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br>
 * <pre>   disconnect</pre>
 * <br><br>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Disconnects from the database.</pre>
 * <br><br>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Disconnect
  extends AbstractDatabaseScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 2760286358879561274L;

  /** the action to execute. */
  public final static String ACTION = "disconnect";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		<!-- scriptlet-action-start -->disconnect<!-- scriptlet-action-end -->
   */
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  public String getDescription() {
    return "Disconnects from the database.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options) throws Exception {
    if (getDatabaseConnection().isConnected())
      getDatabaseConnection().disconnect();

    if (getDatabaseConnection().isConnected())
      return "Failed to disconnect from database!";

    return null;
  }
}
