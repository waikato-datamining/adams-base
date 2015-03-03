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
 * DatabaseConnection.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

/**
 <!-- globalinfo-start -->
 * For initializing an ADAMS database connection that differs from the system-wide one.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DatabaseConnection
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The JDBC URL of the database to connect to, can contain variables or file 
 * &nbsp;&nbsp;&nbsp;placeholders.
 * </pre>
 * 
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The database user to connect with.
 * </pre>
 * 
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the database user.
 * </pre>
 * 
 * <pre>-prompt-for-password (property: promptForPassword)
 * &nbsp;&nbsp;&nbsp;If enabled and authentication is required, the user gets prompted for enter 
 * &nbsp;&nbsp;&nbsp;a password if none has been provided in the setup.
 * </pre>
 * 
 * <pre>-stop-if-canceled (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseConnection
  extends AbstractDatabaseConnection {

  /** for serialization. */
  private static final long serialVersionUID = -1726172998200420556L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "For initializing an ADAMS database connection that differs from the "
      + "system-wide one.";
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  @Override
  public adams.db.AbstractDatabaseConnection getConnection() {
    return adams.db.DatabaseConnection.getSingleton(getResolvedURL(), getUser(), (m_ActualPassword == null ? m_Password : m_ActualPassword));
  }
}
