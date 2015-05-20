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
 * SMTPConnection.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.net.AbstractSendEmail;
import adams.core.net.EmailHelper;
import adams.flow.core.OptionalPasswordPrompt;
import adams.gui.dialog.PasswordDialog;

/**
 <!-- globalinfo-start -->
 * SMTP server setup for overriding default parameters.
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
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SMTPConnection
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-server &lt;java.lang.String&gt; (property: server)
 * &nbsp;&nbsp;&nbsp;The SMTP server (name&#47;IP address) to use.
 * &nbsp;&nbsp;&nbsp;default: smtp.gmail.com
 * </pre>
 * 
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The SMTP port to use.
 * &nbsp;&nbsp;&nbsp;default: 587
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65536
 * </pre>
 * 
 * <pre>-use-tls &lt;boolean&gt; (property: useTLS)
 * &nbsp;&nbsp;&nbsp;If enabled, TLS (transport layer security) is used.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-use-ssl &lt;boolean&gt; (property: useSSL)
 * &nbsp;&nbsp;&nbsp;If enabled, SSL (secure sockets layer) is used for connecting.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-timeout &lt;int&gt; (property: timeout)
 * &nbsp;&nbsp;&nbsp;The timeout in msecs.
 * &nbsp;&nbsp;&nbsp;default: 30000
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-requires-auth &lt;boolean&gt; (property: requiresAuthentication)
 * &nbsp;&nbsp;&nbsp;Enable this if SMTP server requires authentication using user&#47;pw.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The SMTP user to use.
 * </pre>
 * 
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the SMTP user.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SMTPConnection
  extends AbstractStandalone 
  implements OptionalPasswordPrompt {

  /** for serialization. */
  private static final long serialVersionUID = 9145039564243937635L;

  /** the SMTP server. */
  protected String m_Server;

  /** the SMTP port. */
  protected int m_Port;

  /** whether to use TLS. */
  protected boolean m_UseTLS;

  /** whether to use SSL. */
  protected boolean m_UseSSL;

  /** the timeout in msecs. */
  protected int m_Timeout;

  /** whether the SMTP server requires authentication. */
  protected boolean m_RequiresAuthentication;

  /** the SMTP user to use. */
  protected String m_User;

  /** the SMTP password to use. */
  protected BasePassword m_Password;

  /** the actual SMTP password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "SMTP server setup for overriding default parameters.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "server", "server",
	    EmailHelper.getSmtpServer());

    m_OptionManager.add(
	    "port", "port",
	    EmailHelper.getSmtpPort(), 1, 65536);

    m_OptionManager.add(
	    "use-tls", "useTLS",
	    EmailHelper.getSmtpStartTLS());

    m_OptionManager.add(
	    "use-ssl", "useSSL",
	    EmailHelper.getSmtpUseSSL());

    m_OptionManager.add(
	    "timeout", "timeout",
	    EmailHelper.getSmtpTimeout(), 0, null);

    m_OptionManager.add(
	    "requires-auth", "requiresAuthentication",
	    EmailHelper.getSmtpRequiresAuthentication());

    m_OptionManager.add(
	    "user", "user",
	    EmailHelper.getSmtpUser(), false);

    m_OptionManager.add(
	    "password", "password",
	    EmailHelper.getSmtpPassword(), false);

    m_OptionManager.add(
	    "prompt-for-password", "promptForPassword",
	    false);

    m_OptionManager.add(
	    "stop-if-canceled", "stopFlowIfCanceled",
	    false);

    m_OptionManager.add(
	    "custom-stop-message", "customStopMessage",
	    "");
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
    String		value;

    result = "";
    
    if (QuickInfoHelper.hasVariable(this, "requiresAuthentication") || m_RequiresAuthentication) {
      result += QuickInfoHelper.toString(this, "user", m_User);
      value = QuickInfoHelper.toString(this, "password", m_Password.getValue().replaceAll(".", "*"));
      if (value != null)
	result += ":" + value;
      result += "@";
    }
    
    result += QuickInfoHelper.toString(this, "server", m_Server);
    result += QuickInfoHelper.toString(this, "port", m_Port, ":");
    
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useTLS", m_UseTLS, "TLS"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useSSL", m_UseSSL, "SSL"));
    if (   (QuickInfoHelper.hasVariable(this, "requiresAuthentication") || m_RequiresAuthentication)
        && (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) ) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
    }
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the SMTP server to use.
   *
   * @param value	the host name/ip
   */
  public void setServer(String value) {
    m_Server = value;
    reset();
  }

  /**
   * Returns the SMTP server in use.
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
    return "The SMTP server (name/IP address) to use.";
  }

  /**
   * Sets the SMTP port to use.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if ((value >= 1) && (value <= 65536)) {
      m_Port = value;
      reset();
    }
    else {
      getLogger().severe("Port has to satisfy 1<=x<=65536, provided: " + value);
    }
  }

  /**
   * Returns the SMTP port in use.
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
    return "The SMTP port to use.";
  }

  /**
   * Sets whether to use TLS.
   *
   * @param value	if true TLS is used
   */
  public void setUseTLS(boolean value) {
    m_UseTLS = value;
    reset();
  }

  /**
   * Returns whether TLS is used.
   *
   * @return		true if TLS is used
   */
  public boolean getUseTLS() {
    return m_UseTLS;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useTLSTipText() {
    return "If enabled, TLS (transport layer security) is used.";
  }

  /**
   * Sets whether to use SSL.
   *
   * @param value	if true SSL is used
   */
  public void setUseSSL(boolean value) {
    m_UseSSL = value;
    reset();
  }

  /**
   * Returns whether SSL is used.
   *
   * @return		true if SSL is used
   */
  public boolean getUseSSL() {
    return m_UseSSL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useSSLTipText() {
    return "If enabled, SSL (secure sockets layer) is used for connecting.";
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
   * Sets whether SMTP server requires authentication.
   *
   * @param value	if true user/pw is used
   */
  public void setRequiresAuthentication(boolean value) {
    m_RequiresAuthentication = value;
    reset();
  }

  /**
   * Returns whether SMTP server requires authentication.
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
    return "Enable this if SMTP server requires authentication using user/pw.";
  }

  /**
   * Sets the SMTP user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the SMTP user name to use.
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
    return "The SMTP user to use.";
  }

  /**
   * Sets the SMTP password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the SMTP password to use.
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
    return "The password of the SMTP user.";
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
   * @param 		the stop message
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
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteract() {
    boolean		result;
    PasswordDialog	dlg;
    
    dlg = new PasswordDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dlg.setLocationRelativeTo(getParentComponent());
    dlg.setVisible(true);
    result = (dlg.getOption() == PasswordDialog.APPROVE_OPTION);
    
    if (result)
      m_ActualPassword = dlg.getPassword();
    
    return result;
  }

  /**
   * Initializes the SMTP session with the specified parameters.
   *
   * @param sendEmail	the object to initialize
   * @throws Exception	if initialization fails
   */
  public void initializeSmtpSession(AbstractSendEmail sendEmail) throws Exception {
    sendEmail.initializeSmtpSession(
	  m_Server, 
	  m_Port, 
	  m_UseTLS, 
	  m_UseSSL,
	  m_Timeout, 
	  m_RequiresAuthentication, 
	  m_User, 
	  m_ActualPassword);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if ok, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    result = null;
    
    m_ActualPassword = m_Password;
    
    if (m_RequiresAuthentication && m_PromptForPassword && (m_Password.getValue().length() == 0)) {
      if (!isHeadless()) {
	if (!doInteract()) {
	  if (m_StopFlowIfCanceled) {
	    if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
	      stopExecution("Flow canceled: " + getFullName());
	    else
	      stopExecution(m_CustomStopMessage);
	    result = getStopMessage();
	  }
	}
      }
    }
    
    return result;
  }
}
