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
 * IMAPConnection.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.PasswordPrompter;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.flow.core.ActorUtils;
import adams.flow.core.OptionalPasswordPrompt;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import jodd.mail.ImapServer;
import jodd.mail.MailServer;
import jodd.mail.ReceiveMailSession;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * IMAP server setup.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: IMAPConnection
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-scheme &lt;IMAP|IMAPS&gt; (property: scheme)
 * &nbsp;&nbsp;&nbsp;The IMAP scheme to use.
 * &nbsp;&nbsp;&nbsp;default: IMAPS
 * </pre>
 *
 * <pre>-server &lt;java.lang.String&gt; (property: server)
 * &nbsp;&nbsp;&nbsp;The IMAP server (name&#47;IP address) to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The IMAP port to use, uses default if &lt;1.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * &nbsp;&nbsp;&nbsp;maximum: 65536
 * </pre>
 *
 * <pre>-timeout &lt;int&gt; (property: timeout)
 * &nbsp;&nbsp;&nbsp;The timeout in msecs.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-requires-auth &lt;boolean&gt; (property: requiresAuthentication)
 * &nbsp;&nbsp;&nbsp;Enable this if IMAP server requires authentication using user&#47;pw.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The IMAP user to use.
 * </pre>
 *
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the IMAP user.
 * </pre>
 *
 * <pre>-prompt-for-password &lt;boolean&gt; (property: promptForPassword)
 * &nbsp;&nbsp;&nbsp;If enabled and authentication is required, the user gets prompted for enter
 * &nbsp;&nbsp;&nbsp;a password if none has been provided in the setup.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class IMAPConnection
  extends AbstractStandalone
  implements OptionalPasswordPrompt, PasswordPrompter {

  /** for serialization. */
  private static final long serialVersionUID = 9145039564243937635L;

  /**
   * The IMAP schemes.
   */
  public enum IMAPScheme {
    IMAP,
    IMAPS,
  }

  /** the scheme to use. */
  protected IMAPScheme m_Scheme;

  /** the IMAP server. */
  protected String m_Server;

  /** the IMAP port. */
  protected int m_Port;

  /** the timeout in msecs. */
  protected int m_Timeout;

  /** whether the IMAP server requires authentication. */
  protected boolean m_RequiresAuthentication;

  /** the IMAP user to use. */
  protected String m_User;

  /** the IMAP password to use. */
  protected BasePassword m_Password;

  /** the actual IMAP password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** how to perform the stop. */
  protected StopMode m_StopMode;

  /** the server. */
  protected transient ImapServer m_ImapServer;

  /** the session. */
  protected transient ReceiveMailSession m_ImapSession;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "IMAP server setup.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "scheme", "scheme",
      IMAPScheme.IMAPS);

    m_OptionManager.add(
      "server", "server",
      "");

    m_OptionManager.add(
      "port", "port",
      -1, -1, 65536);

    m_OptionManager.add(
      "timeout", "timeout",
      0, 0, null);

    m_OptionManager.add(
      "requires-auth", "requiresAuthentication",
      false);

    m_OptionManager.add(
      "user", "user",
      "").dontOutputDefaultValue();

    m_OptionManager.add(
      "password", "password",
      new BasePassword()).dontOutputDefaultValue();

    m_OptionManager.add(
      "prompt-for-password", "promptForPassword",
      false);

    m_OptionManager.add(
      "stop-if-canceled", "stopFlowIfCanceled",
      false);

    m_OptionManager.add(
      "custom-stop-message", "customStopMessage",
      "");

    m_OptionManager.add(
      "stop-mode", "stopMode",
      StopMode.GLOBAL);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    closeImap();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result = QuickInfoHelper.toString(this, "scheme", m_Scheme) + "://";

    if (QuickInfoHelper.hasVariable(this, "requiresAuthentication") || m_RequiresAuthentication)
      result += QuickInfoHelper.toString(this, "user", m_User) + ":***";

    result += QuickInfoHelper.toString(this, "server", m_Server);
    result += QuickInfoHelper.toString(this, "port", m_Port, ":");

    options = new ArrayList<>();
    if (   (QuickInfoHelper.hasVariable(this, "requiresAuthentication") || m_RequiresAuthentication)
	     && (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) ) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
    }
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the IMAP scheme to use.
   *
   * @param value	the scheme
   */
  public void setScheme(IMAPScheme value) {
    m_Scheme = value;
    reset();
  }

  /**
   * Returns the IMAP scheme in use.
   *
   * @return		the scheme
   */
  public IMAPScheme getScheme() {
    return m_Scheme;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String schemeTipText() {
    return "The IMAP scheme to use.";
  }

  /**
   * Sets the IMAP server to use.
   *
   * @param value	the host name/ip
   */
  public void setServer(String value) {
    m_Server = value;
    reset();
  }

  /**
   * Returns the IMAP server in use.
   *
   * @return		the host name/ip
   */
  public String getServer() {
    return m_Server;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String serverTipText() {
    return "The IMAP server (name/IP address) to use.";
  }

  /**
   * Sets the IMAP port to use.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if ((value >= -1) && (value <= 65536)) {
      m_Port = value;
      reset();
    }
    else {
      getLogger().severe("Port has to satisfy -1<=x<=65536, provided: " + value);
    }
  }

  /**
   * Returns the IMAP port in use.
   *
   * @return		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The IMAP port to use, uses default if <1.";
  }

  /**
   * Sets the timeout in msecs.
   *
   * @param value	the timeout
   */
  public void setTimeout(int value) {
    if (value >= 0) {
      m_Timeout = value;
      reset();
    }
    else {
      getLogger().severe("Timeout has to be >= 0, provided: " + value);
    }
  }

  /**
   * Returns the timeout in msecs.
   *
   * @return		the timeout
   */
  public int getTimeout() {
    return m_Timeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timeoutTipText() {
    return "The timeout in msecs.";
  }

  /**
   * Sets whether IMAP server requires authentication.
   *
   * @param value	if true user/pw is used
   */
  public void setRequiresAuthentication(boolean value) {
    m_RequiresAuthentication = value;
    reset();
  }

  /**
   * Returns whether IMAP server requires authentication.
   *
   * @return		true if user/pw is used
   */
  public boolean getRequiresAuthentication() {
    return m_RequiresAuthentication;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String requiresAuthenticationTipText() {
    return "Enable this if IMAP server requires authentication using user/pw.";
  }

  /**
   * Sets the IMAP user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the IMAP user name to use.
   *
   * @return		the user name
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The IMAP user to use.";
  }

  /**
   * Sets the IMAP password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the IMAP password to use.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password of the IMAP user.";
  }

  /**
   * Sets whether to prompt for a password if none currently provided.
   *
   * @param value	true if to prompt for a password
   */
  public void setPromptForPassword(boolean value) {
    m_PromptForPassword = value;
    reset();
  }

  /**
   * Returns whether to prompt for a password if none currently provided.
   *
   * @return		true if to prompt for a password
   */
  public boolean getPromptForPassword() {
    return m_PromptForPassword;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String promptForPasswordTipText() {
    return
      "If enabled and authentication is required, the user gets prompted "
	+ "for enter a password if none has been provided in the setup.";
  }

  /**
   * Sets whether to stop the flow if dialog canceled.
   *
   * @param value	if true flow gets stopped if dialog canceled
   */
  public void setStopFlowIfCanceled(boolean value) {
    m_StopFlowIfCanceled = value;
    reset();
  }

  /**
   * Returns whether to stop the flow if dialog canceled.
   *
   * @return 		true if the flow gets stopped if dialog canceled
   */
  public boolean getStopFlowIfCanceled() {
    return m_StopFlowIfCanceled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String stopFlowIfCanceledTipText() {
    return "If enabled, the flow gets stopped in case the user cancels the dialog.";
  }

  /**
   * Sets the custom message to use when stopping the flow.
   *
   * @param value	the stop message
   */
  public void setCustomStopMessage(String value) {
    m_CustomStopMessage = value;
    reset();
  }

  /**
   * Returns the custom message to use when stopping the flow.
   *
   * @return		the stop message
   */
  public String getCustomStopMessage() {
    return m_CustomStopMessage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String customStopMessageTipText() {
    return
      "The custom stop message to use in case a user cancelation stops the "
	+ "flow (default is the full name of the actor)";
  }

  /**
   * Sets the stop mode.
   *
   * @param value	the mode
   */
  @Override
  public void setStopMode(StopMode value) {
    m_StopMode = value;
    reset();
  }

  /**
   * Returns the stop mode.
   *
   * @return		the mode
   */
  @Override
  public StopMode getStopMode() {
    return m_StopMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String stopModeTipText() {
    return "The stop mode to use.";
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		null if successfully interacted, otherwise error message
   */
  @Override
  public String doInteract() {
    m_ActualPassword = ActorUtils.promptPassword(this);
    if (m_ActualPassword == null)
      return INTERACTION_CANCELED;
    else
      return null;
  }

  /**
   * Returns whether headless interaction is supported.
   *
   * @return		true if interaction in headless environment is possible
   */
  public boolean supportsHeadlessInteraction() {
    return true;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		null if successfully interacted, otherwise error message
   */
  @Override
  public String doInteractHeadless() {
    String		result;
    BasePassword	password;

    result   = INTERACTION_CANCELED;
    password = ConsoleHelper.enterPassword("Please enter password (" + getName() + "):");
    if (password != null) {
      result           = null;
      m_ActualPassword = password;
    }

    return result;
  }

  /**
   * Returns the imap server instance, instantiates it if necessary.
   *
   * @return		the server instance
   */
  public synchronized ImapServer getImapServer() {
    if (m_ImapServer == null) {
      m_ImapServer = MailServer.create()
		       .host(m_Server)
		       .ssl(m_Scheme == IMAPScheme.IMAPS)
		       .auth(m_User, m_ActualPassword.getValue())
		       .buildImapMailServer();
    }

    return m_ImapServer;
  }

  /**
   * Returns image session, instantiates it if necessary and opens it.
   *
   * @return		the session
   */
  public synchronized ReceiveMailSession getImapSession() {
    if (m_ImapSession == null) {
      m_ImapSession = getImapServer().createSession();
      m_ImapSession.open();
    }

    return m_ImapSession;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if ok, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	msg;

    result = null;

    m_ActualPassword = m_Password;

    if (m_RequiresAuthentication && m_PromptForPassword && (m_Password.getValue().isEmpty())) {
      if (!isHeadless()) {
	msg = doInteract();
	if (msg != null) {
	  if (m_StopFlowIfCanceled) {
	    if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().isEmpty()))
	      StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
	    else
	      StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
	    result = getStopMessage();
	  }
	}
      }
      else if (supportsHeadlessInteraction()) {
	msg = doInteractHeadless();
	if (msg != null) {
	  if (m_StopFlowIfCanceled) {
	    if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().isEmpty()))
	      StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
	    else
	      StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
	    result = getStopMessage();
	  }
	}
      }
    }

    return result;
  }

  /**
   * Closes the image connection.
   */
  protected void closeImap() {
    if (m_ImapSession != null) {
      try {
	m_ImapSession.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_ImapSession = null;
    }
    m_ImapServer = null;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();
    closeImap();
  }
}
