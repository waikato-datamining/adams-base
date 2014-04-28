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
 * Rejector.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.data.id.IDHandler;
import adams.db.LogEntry;
import adams.flow.core.Compatibility;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Rejects data containers with errors attached. The errors get tee-ed off as log entries. Instead of using the full name of the actor as 'source' in the log entries, a custom 'source' value can be supplied.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.container.DataContainer<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.NotesHandler<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
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
 * &nbsp;&nbsp;&nbsp;default: Rejector
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
 * <pre>-tee &lt;adams.flow.core.AbstractActor&gt; (property: teeActor)
 * &nbsp;&nbsp;&nbsp;The actor to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.Sequence -actor adams.flow.sink.Null
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
public class Rejector
  extends Tee {

  /** for serialization. */
  private static final long serialVersionUID = -5532257363818440415L;

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
        "Rejects data containers with errors attached. The errors get tee-ed "
      + "off as log entries. Instead of using the full name of the actor as "
      + "'source' in the log entries, a custom 'source' value can be supplied.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "log-source", "logSource",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_MinimumActiveActors = 0;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "logSource", (m_LogSource.length() > 0 ? m_LogSource : null));

    if (super.getQuickInfo() != null) {
      if (result == null)
	result = super.getQuickInfo();
      else
	result += ", " + super.getQuickInfo();
    }
    
    return result;
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
   * @return		<!-- flow-accepts-start -->adams.data.container.DataContainer.class, adams.data.NotesHandler.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{DataContainer.class, NotesHandler.class};
  }

  /**
   * Gets called in the setUp() method. Returns null if tee-actor is fine,
   * otherwise error message.
   *
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String setUpTeeActors() {
    String		result;
    Compatibility	comp;

    result = null;

    comp = new Compatibility();
    if (!comp.isCompatible(new Class[]{LogEntry.class}, m_Actors.accepts()))
      result = "Tee actors must accept " + LogEntry.class.getName() + "!";

    return result;
  }

  /**
   * Checks whether the input notes contain an error.
   *
   * @param token	the input
   * @return		true if the notes contain an error
   */
  protected boolean hasError(Token token) {
    NotesHandler	handler;

    handler = (NotesHandler) token.getPayload();

    return handler.getNotes().hasError();
  }

  /**
   * Generates the output for the tee actor.
   *
   * @param token	the input
   * @return		the error message token
   */
  @Override
  protected Token createTeeToken(Token token) {
    NotesHandler	handler;
    LogEntry		log;
    Properties		props;

    log     = new LogEntry();
    handler = (NotesHandler) token.getPayload();
    props   = new Properties();
    if (handler instanceof DatabaseIDHandler) {
      props.setInteger(LogEntry.KEY_DBID, ((DatabaseIDHandler) handler).getDatabaseID());
      log.setDatabaseID(((DatabaseIDHandler) handler).getDatabaseID());
    }
    if (handler instanceof IDHandler)
      props.setProperty(LogEntry.KEY_ID, ((IDHandler) handler).getID());
    props.setProperty(LogEntry.KEY_ERRORS, handler.getNotes().getErrors().toString());

    log.setType("Rejection");
    if (m_LogSource.length() == 0)
      log.setSource(getFullName());
    else
      log.setSource(m_LogSource);
    log.setStatus(LogEntry.STATUS_NEW);
    log.setMessage(props);

    return new Token(log);
  }

  /**
   * Returns whether the token can be processed in the tee actor.
   *
   * @param token	the token to process
   * @return		true if token can be processed
   */
  @Override
  protected boolean canProcessInput(Token token) {
    return (    super.canProcessInput(token)
	     && (token.getPayload() instanceof NotesHandler)
	     && hasError(token) );
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = super.doExecute();

    if (hasError(m_OutputToken))
      m_OutputToken = null;

    return result;
  }
}
