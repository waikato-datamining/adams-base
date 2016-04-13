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
 * FTPConnection.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.flow.core.OptionalPasswordPrompt;
import adams.gui.dialog.PasswordDialog;

/**
 <!-- globalinfo-start -->
 * Provides access to a FTP host.<br>
 * If debugging is enabled, the FTP commands issued by other actors will get printed as debug output of this actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: FTPConnection
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
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host (name&#47;IP address) to connect to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The FTP user to use for connecting.
 * </pre>
 * 
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the FTP user to use for connecting.
 * </pre>
 * 
 * <pre>-passive (property: usePassiveMode)
 * &nbsp;&nbsp;&nbsp;If enabled, passive mode is used instead.
 * </pre>
 * 
 * <pre>-binary (property: useBinaryMode)
 * &nbsp;&nbsp;&nbsp;If enabled, binary mode is used instead of ASCII.
 * </pre>
 * 
 * <pre>-prompt-for-password (property: promptForPassword)
 * &nbsp;&nbsp;&nbsp;If enabled, the user gets prompted for enter a password if none has been 
 * &nbsp;&nbsp;&nbsp;provided in the setup.
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
public class FTPConnection
  extends AbstractStandalone
  implements ProtocolCommandListener, OptionalPasswordPrompt {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the FTP host. */
  protected String m_Host;

  /** the FTP user to use. */
  protected String m_User;

  /** the FTP password to use. */
  protected BasePassword m_Password;

  /** whether to use passive mode. */
  protected boolean m_UsePassiveMode;

  /** whether to use binary file transfer mode. */
  protected boolean m_UseBinaryMode;

  /** the actual SMTP password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** the FTP client object. */
  protected FTPClient m_Client;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Provides access to a FTP host.\n"
      + "If debugging is enabled, the FTP commands issued by other actors will "
      + "get printed as debug output of this actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "host", "host",
	    "");

    m_OptionManager.add(
	    "user", "user",
	    "anonymous", false);

    m_OptionManager.add(
	    "password", "password",
	    new BasePassword(""), false);

    m_OptionManager.add(
	    "passive", "usePassiveMode",
	    false);

    m_OptionManager.add(
	    "binary", "useBinaryMode",
	    false);

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
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    disconnect();
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

    result  = QuickInfoHelper.toString(this, "user", m_User);
    value = QuickInfoHelper.toString(this, "password", m_Password.getValue().replaceAll(".", "*"));
    if (value != null)
      result += ":" + value;
    value = QuickInfoHelper.toString(this, "host", m_Host);
    if (value != null)
      result += "@" + value;

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "usePassiveMode", m_UsePassiveMode, "passive"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useBinaryMode", m_UseBinaryMode, "binary"));
    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
    }
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host (name/IP address) to connect to.";
  }

  /**
   * Sets the FTP user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the FTP user name to use.
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
    return "The FTP user to use for connecting.";
  }

  /**
   * Sets the FTP password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the FTP password to use.
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
    return "The password of the FTP user to use for connecting.";
  }

  /**
   * Sets whether to use passive mode.
   *
   * @param value	if true passive mode is used
   */
  public void setUsePassiveMode(boolean value) {
    m_UsePassiveMode = value;
    reset();
  }

  /**
   * Returns whether passive mode is used.
   *
   * @return		true if passive mode is used
   */
  public boolean getUsePassiveMode() {
    return m_UsePassiveMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usePassiveModeTipText() {
    return "If enabled, passive mode is used instead.";
  }

  /**
   * Sets whether to use binary mode.
   *
   * @param value	if true binary mode is used
   */
  public void setUseBinaryMode(boolean value) {
    m_UseBinaryMode = value;
    reset();
  }

  /**
   * Returns whether binary mode is used.
   *
   * @return		true if binary mode is used
   */
  public boolean getUseBinaryMode() {
    return m_UseBinaryMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useBinaryModeTipText() {
    return "If enabled, binary mode is used instead of ASCII.";
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
	"If enabled, the user gets prompted "
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
   * Returns the FTP client object.
   *
   * @return		the FTP client, null if failed to connect
   */
  public synchronized FTPClient getFTPClient() {
    if (m_Client == null)
      connect();
    
    return m_Client;
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
    
    // close any active connection first
    disconnect();
    
    m_ActualPassword = m_Password;
    
    if (m_PromptForPassword && (m_Password.getValue().length() == 0)) {
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

    if (result == null)
      result = connect();

    return result;
  }
  
  /**
   * Starts up a FTP session.
   * 
   * @return		null if OK, otherwise error message
   */
  protected String connect() {
    String	result;
    int		reply;

    result = null;
    
    try {
      m_Client = new FTPClient();
      m_Client.addProtocolCommandListener(this);
      m_Client.connect(m_Host);
      reply = m_Client.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
	result = "FTP server refused connection: " + reply;
      }
      else {
	if (!m_Client.login(m_User, m_ActualPassword.getValue())) {
	  result = "Failed to connect to '" + m_Host + "' as user '" + m_User + "'";
	}
	else {
	  if (m_UsePassiveMode)
	    m_Client.enterLocalPassiveMode();
	  if (m_UseBinaryMode)
	    m_Client.setFileType(FTPClient.BINARY_FILE_TYPE);
	}
      }
    }
    catch (Exception e) {
      result   = handleException("Failed to connect to '" + m_Host + "' as user '" + m_User + "': ", e);
      m_Client = null;
    }

    return result;
  }

  /**
   * Disconnects the FTP session, if necessary.
   */
  public void disconnect() {
    if (m_Client != null) {
      if (m_Client.isConnected()) {
	try {
	  m_Client.disconnect();
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to disconnect from '" + m_Host + "':", e);
	}
	m_Client.removeProtocolCommandListener(this);
      }
    }
    m_Client = null;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    disconnect();
    super.wrapUp();
  }

  /***
   * This method is invoked by a ProtocolCommandEvent source after
   * sending a protocol command to a server.
   *
   * @param event The ProtocolCommandEvent fired.
   */
  public void protocolCommandSent(ProtocolCommandEvent event) {
    if (isLoggingEnabled())
      getLogger().info("cmd sent: " + event.getCommand() + "/" + event.getReplyCode());
    else if (event.getReplyCode() >= 400)
      getLogger().severe("cmd sent: " + event.getCommand() + "/" + event.getReplyCode());
  }

  /***
   * This method is invoked by a ProtocolCommandEvent source after
   * receiving a reply from a server.
   *
   * @param event The ProtocolCommandEvent fired.
   */
  public void protocolReplyReceived(ProtocolCommandEvent event) {
    if (isLoggingEnabled())
      getLogger().info("reply received: " + event.getMessage() + "/" + event.getReplyCode());
    else if (event.getReplyCode() >= 400)
      getLogger().severe("reply received: " + event.getMessage() + "/" + event.getReplyCode());
  }
}
