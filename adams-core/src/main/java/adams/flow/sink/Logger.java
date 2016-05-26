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
 * Logger.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.db.LogEntry;
import adams.db.LogT;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Actor that stores LogEntry objects in the Log table.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.db.LogEntry<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: Logger
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Logger
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = 1862024453539320530L;

  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Actor that stores LogEntry objects in the Log table.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.db.LogEntry.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{LogEntry.class};
  }

  /**
   * Pre-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String preExecute() {
    String		result;

    result = super.setUp();

    if (result == null) {
      if (m_DatabaseConnection == null) {
        m_DatabaseConnection = ActorUtils.getDatabaseConnection(
          this,
          adams.flow.standalone.DatabaseConnection.class,
          adams.db.DatabaseConnection.getSingleton());
        if (!m_DatabaseConnection.isConnected())
          result = "No active database connection available!";
      }
    }

    return result;
  }

  /**
   * Does nothing.
   *
   * @return		always null
   */
  protected String doExecute() {
    String	result;
    LogEntry	log;

    result = null;

    log = (LogEntry) m_InputToken.getPayload();
    if (!LogT.getSingleton(m_DatabaseConnection).add(log))
      result = "Failed to add log entry:\n" + log;

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  public void wrapUp() {
    m_DatabaseConnection = null;

    super.wrapUp();
  }
}
