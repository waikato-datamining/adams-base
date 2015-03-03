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
 * LogDumper.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.util.Hashtable;

import adams.core.io.LogEntryWriter;
import adams.db.LogEntry;

/**
 <!-- globalinfo-start -->
 * Actor that stores LogEntry objects in a file (CSV format).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.db.LogEntry<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: LogDumper
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file store the log entries in.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogDumper
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 2371387253627286951L;

  /** the key for storing the "logging started" state in the backup. */
  public final static String BACKUP_LOGGINGSTARTED = "logging started";

  /** whether the log file has been started. */
  protected boolean m_LoggingStarted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Actor that stores LogEntry objects in a file (CSV format).";
  }

  /**
   * Resets the scheme.
   */
  protected void reset() {
    super.reset();

    m_LoggingStarted = false;
  }

  /**
   * Removes entries from the backup.
   */
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_LOGGINGSTARTED);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_LoggingStarted)
      result.put(BACKUP_LOGGINGSTARTED, m_LoggingStarted);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_LOGGINGSTARTED)) {
      m_LoggingStarted = (Boolean) state.get(BACKUP_LOGGINGSTARTED);
      state.remove(BACKUP_LOGGINGSTARTED);
    }

    super.restoreState(state);
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
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The file store the log entries in.";
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

    if (!m_LoggingStarted) {
      if (!LogEntryWriter.rewrite(m_OutputFile.getAbsolutePath()))
	result = "Failed to start log file '" + m_OutputFile + "'!";
      else
	m_LoggingStarted = true;
    }

    if (result == null) {
      log = (LogEntry) m_InputToken.getPayload();
      if (!LogEntryWriter.write(m_OutputFile.getAbsolutePath(), log))
	result = "Failed to append log entry to '" + m_OutputFile + "'!";
    }

    return result;
  }
}
