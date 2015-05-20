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
 * MakeLogEntry.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.db.LogEntry;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Creates a log entry from the incoming token. The incoming string gets added as the 'error' message in the log entry.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: MakeLogEntry
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
 * <pre>-log-type &lt;java.lang.String&gt; (property: logType)
 * &nbsp;&nbsp;&nbsp;The value to use as 'type' field in the log entries; 'Error' is used by
 * &nbsp;&nbsp;&nbsp;default if left empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-log-source &lt;java.lang.String&gt; (property: logSource)
 * &nbsp;&nbsp;&nbsp;The value to use as 'source' field in the log entries; the actor's full
 * &nbsp;&nbsp;&nbsp;name is used by default if left empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MakeLogEntry
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6516304745462094703L;

  /** the default type for log entries. */
  public final static String DEFAULT_TYPE = "Error";

  /** the type of log entries to produce. */
  protected String m_LogType;

  /** the source to fill in the log entries. */
  protected String m_LogSource;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Creates a log entry from the incoming token. The incoming string "
      + "gets added as the 'error' message in the log entry.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "log-type", "logType",
	    "");

    m_OptionManager.add(
	    "log-source", "logSource",
	    "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "logType", (m_LogType.length() == 0 ? "-none-" : m_LogType));
    result += QuickInfoHelper.toString(this, "logSource", (m_LogSource.length() == 0 ? "-none-" : m_LogSource), "/");

    return result;
  }

  /**
   * Sets the type to use for the entries.
   *
   * @param value	the type
   */
  public void setLogType(String value) {
    m_LogType = value;
    reset();
  }

  /**
   * Returns the type to use for the entries.
   *
   * @return		the type
   */
  public String getLogType() {
    return m_LogType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logTypeTipText() {
    return
        "The value to use as 'type' field in the log entries; "
      + "'" + DEFAULT_TYPE + "' is used by default if left empty.";
  }

  /**
   * Sets the source to use in the log entries. Empty string uses the full name
   * of the actor as default value.
   *
   * @param value	the source
   */
  public void setLogSource(String value) {
    m_LogSource = value;
    reset();
  }

  /**
   * Returns the source to use in the log entries. An empty string uses the full
   * name of the actor as default value.
   *
   * @return		the source
   */
  public String getLogSource() {
    return m_LogSource;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logSourceTipText() {
    return
        "The value to use as 'source' field in the log entries; the actor's "
      + "full name is used by default if left empty.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    LogEntry		log;
    Properties		props;

    result = null;

    props = new Properties();
    props.setProperty(LogEntry.KEY_ERRORS, (String) m_InputToken.getPayload());

    log = new LogEntry();
    if (m_LogType.length() == 0)
      log.setType(DEFAULT_TYPE);
    else
      log.setType(m_LogType);
    if (m_LogSource.length() == 0)
      log.setSource(getFullName());
    else
      log.setSource(m_LogSource);
    log.setStatus(LogEntry.STATUS_NEW);
    log.setMessage(props);

    m_OutputToken = new Token(log);

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.db.LogEntry.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{LogEntry.class};
  }
}
