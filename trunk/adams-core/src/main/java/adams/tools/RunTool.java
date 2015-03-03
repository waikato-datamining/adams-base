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
 * RunTool.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

import adams.core.option.OptionUtils;
import adams.db.DatabaseConnectionHandler;
import adams.env.Environment;
import adams.run.RunDatabaseScheme;

/**
 * Runs a tool from commandline.
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-driver &lt;java.lang.String&gt; (property: driver)
 *         The JDBC driver.
 *         default: com.mysql.jdbc.Driver
 * </pre>
 *
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 *         The database URL.
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 *         The database user.
 * </pre>
 *
 * <pre>-password &lt;java.lang.String&gt; (property: password)
 *         The password of the database user.
 * </pre>
 *
 * <pre>-tool &lt;adams.tools.AbstractTool [options]&gt; (property: tool)
 *         The tool to use.
 *         default: adams.tools.InitializeTables
 * </pre>
 *
 * Default options for adams.tools.InitializeTables (-tool/tool):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunTool
  extends RunDatabaseScheme {

  /** for serialization. */
  private static final long serialVersionUID = 7648856946524265052L;

  /** the tool to run. */
  protected AbstractTool m_Tool;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"tool", "tool",
	new InitializeTables());
  }

  /**
   * Sets the tool to run.
   *
   * @param value 	the tool
   */
  public void setTool(AbstractTool value) {
    m_Tool = value;
  }

  /**
   * Returns the tool being used.
   *
   * @return 		the tool
   */
  public AbstractTool getTool() {
    return m_Tool;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String toolTipText() {
    return "The tool to use.";
  }

  /**
   * Performs some initializations before the actual run.
   * Connects to the database.
   *
   * @throws Exception 	if something goes wrong
   */
  @Override
  protected void preRun() throws Exception {
    super.preRun();

    if (m_Tool instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Tool).setDatabaseConnection(m_DbConn);
  }

  /**
   * Runs the tool and prints some information to stdout.
   *
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void doRun() throws Exception {
    getLogger().info("- running: " + OptionUtils.getCommandLine(m_Tool));
    m_Tool.run();
  }

  /**
   * Runs the tool from commandline.
   *
   * @param args	the commandline arguments, use -help to display all
   */
  public static void main(String[] args) {
    runScheme(Environment.class, RunTool.class, args);
  }
}
