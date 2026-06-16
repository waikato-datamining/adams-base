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
 * Main.java
 * Copyright (C) 2009-2026 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.env.Environment;
import adams.gui.application.AbstractApplicationFrame;

/**
 * GUI for ADAMS.
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level to use.
 * &nbsp;&nbsp;&nbsp;default: INFO
 * </pre>
 *
 * <pre>-title &lt;java.lang.String&gt; (property: applicationTitle)
 * &nbsp;&nbsp;&nbsp;The title for the application.
 * &nbsp;&nbsp;&nbsp;default: ADAMS
 * </pre>
 *
 * <pre>-user-mode &lt;BASIC|EXPERT|DEVELOPER|DEBUGGER&gt; (property: userMode)
 * &nbsp;&nbsp;&nbsp;The user mode, which determines the visibility of the menu items.
 * &nbsp;&nbsp;&nbsp;default: EXPERT
 * </pre>
 *
 * <pre>-minimal-window &lt;boolean&gt; (property: minimalWindow)
 * &nbsp;&nbsp;&nbsp;If enabled, the main window does not extend the full width of the screen.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-start-up &lt;adams.core.base.BaseString&gt; [-start-up ...] (property: startUps)
 * &nbsp;&nbsp;&nbsp;The menu items to start up immediately; each consists of classname and optional
 * &nbsp;&nbsp;&nbsp;parameters (in case the menu definition implements adams.gui.application.AdditionalParameterHandler
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-doc-dir &lt;adams.core.io.PlaceholderDirectory&gt; [-doc-dir ...] (property: documentationDirectories)
 * &nbsp;&nbsp;&nbsp;The directories containing PDF documentation (may get listed in the Help
 * &nbsp;&nbsp;&nbsp;menu).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-enable-restart &lt;boolean&gt; (property: enableRestart)
 * &nbsp;&nbsp;&nbsp;If enabled and started through the adams.core.management.Launcher class,
 * &nbsp;&nbsp;&nbsp;the application can be restarted through the menu.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-remote-scripting-engine-cmdline &lt;java.lang.String&gt; (property: remoteScriptingEngineCmdLine)
 * &nbsp;&nbsp;&nbsp;The command-line of the remote scripting engine to execute at startup time;
 * &nbsp;&nbsp;&nbsp; use empty string for disable scripting.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Main
  extends AbstractApplicationFrame {

  /** for serialization. */
  private static final long serialVersionUID = -5800519559483605870L;

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the filename that stores the menu layout.
   *
   * @return		the filename
   */
  protected String getMenuLayoutFile() {
    return "adams/gui/Main.props";
  }

  /**
   * Returns the default title of the application.
   *
   * @return		the default title
   */
  protected String getDefaultApplicationTitle() {
    return "ADAMS";
  }

  /**
   * Closes the application.
   */
  protected void closeApplication() {
    m_DbConn.disconnect();
    super.closeApplication();
  }

  /**
   * starts the application.
   *
   * @param args	the commandline arguments
   */
  public static void main(String[] args) {
    runApplication(Environment.class, Main.class, args);
  }
}
