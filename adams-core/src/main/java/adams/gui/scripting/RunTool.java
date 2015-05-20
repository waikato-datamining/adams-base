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
 * RunTool.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.option.OptionUtils;
import adams.db.DatabaseConnectionHandler;
import adams.tools.AbstractTool;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br>
 * <pre>   run-tool &lt;tool + options&gt;</pre>
 * <br><br>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Runs the specified tool.</pre>
 * <br><br>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunTool
  extends AbstractScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 2954163003128167687L;

  /** the action to execute. */
  public final static String ACTION = "run-tool";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  protected String getOptionsDescription() {
    return "<tool + options>";
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  public String getDescription() {
    return "Runs the specified tool.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options) throws Exception {
    String[]		schemeOptions;
    String		schemeClassname;
    AbstractTool	runScheme;

    // setup tool
    schemeOptions    = OptionUtils.splitOptions(options);
    schemeClassname  = schemeOptions[0];
    schemeOptions[0] = "";
    runScheme        = AbstractTool.forName(schemeClassname, schemeOptions);
    if (runScheme instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) runScheme).setDatabaseConnection(getOwner().getDatabaseConnection());

    // run matcher
    showStatus("Running...");
    runScheme.run();
    showStatus("");

    return null;
  }
}
