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
 * SSHConnection.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 * Copyright (C) JSch
 */

package adams.flow.standalone;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BasePassword;
import adams.core.io.PlaceholderFile;
import adams.core.net.SSHAuthenticationType;
import adams.core.net.SSHSessionProvider;
import adams.flow.core.OptionalPasswordPrompt;
import adams.gui.dialog.PasswordDialog;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Provides access to a remote host via SSH.<br>
 * <br>
 * For more information see:<br>
 * <br>
 *  (2011). JSch - JSch is a pure Java implementation of SSH2..
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
 * &nbsp;&nbsp;&nbsp;default: SSHConnection
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host (name&#47;IP address) to connect to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to connect to.
 * &nbsp;&nbsp;&nbsp;default: 22
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 * 
 * <pre>-authentication-type &lt;CREDENTIALS|PUBLIC_KEY&gt; (property: authenticationType)
 * &nbsp;&nbsp;&nbsp;The type of authentication to use.
 * &nbsp;&nbsp;&nbsp;default: CREDENTIALS
 * </pre>
 * 
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The SSH user to use for connecting.
 * </pre>
 * 
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the SSH user to use for connecting.
 * </pre>
 * 
 * <pre>-private-key-file &lt;adams.core.io.PlaceholderFile&gt; (property: privateKeyFile)
 * &nbsp;&nbsp;&nbsp;The location of the private key.
 * &nbsp;&nbsp;&nbsp;default: ${HOME}&#47;.ssh&#47;id_rsa
 * </pre>
 * 
 * <pre>-private-key-passphrase &lt;adams.core.base.BasePassword&gt; (property: privateKeyPassphrase)
 * &nbsp;&nbsp;&nbsp;The passphrase for the private key file, ignored if empty.
 * </pre>
 * 
 * <pre>-known-hosts &lt;adams.core.io.PlaceholderFile&gt; (property: knownHosts)
 * &nbsp;&nbsp;&nbsp;The file storing the known hosts.
 * &nbsp;&nbsp;&nbsp;default: ${HOME}&#47;.ssh&#47;known_hosts
 * </pre>
 * 
 * <pre>-strict-host-key-checking &lt;boolean&gt; (property: strictHostKeyChecking)
 * &nbsp;&nbsp;&nbsp;Enables&#47;disables strict host key checking - strict checking is the recommended 
 * &nbsp;&nbsp;&nbsp;setting, as disabling it is very insecure!
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-forward-x &lt;boolean&gt; (property: forwardX)
 * &nbsp;&nbsp;&nbsp;If set to true, then X is forwarded.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-x-host &lt;java.lang.String&gt; (property: XHost)
 * &nbsp;&nbsp;&nbsp;The xhost (name&#47;IP address) to connect to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-x-port &lt;java.lang.String&gt; (property: XPort)
 * &nbsp;&nbsp;&nbsp;The xport to connect to.
 * &nbsp;&nbsp;&nbsp;default: 0:0
 * </pre>
 * 
 * <pre>-prompt-for-password &lt;boolean&gt; (property: promptForPassword)
 * &nbsp;&nbsp;&nbsp;If enabled, the user gets prompted for enter a password if none has been 
 * &nbsp;&nbsp;&nbsp;provided in the setup.
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
@MixedCopyright(
  copyright = "JCraft",
  license = License.BSD3,
  url = "http://www.jcraft.com/jsch/"
)
public class SSHConnection
  extends AbstractStandalone
  implements TechnicalInformationHandler, OptionalPasswordPrompt, SSHSessionProvider {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the SSH host. */
  protected String m_Host;

  /** the SSH port. */
  protected int m_Port;

  /** the type of authentication to use. */
  protected SSHAuthenticationType m_AuthenticationType;

  /** the SSH user to use. */
  protected String m_User;

  /** the SSH password to use. */
  protected BasePassword m_Password;

  /** the location of the private key. */
  protected PlaceholderFile m_PrivateKeyFile;

  /** the passphrase for the private key. */
  protected BasePassword m_PrivateKeyPassphrase;

  /** the file with known hosts. */
  protected PlaceholderFile m_KnownHosts;

  /** whether to perform strict host key checking (only disable for testing!! insecure!!). */
  protected boolean m_StrictHostKeyChecking;

  /** whether to forward X11. */
  protected boolean m_ForwardX;

  /** the xhost to use. */
  protected String m_XHost;

  /** the xport to use. */
  protected String m_XPort;

  /** the actual SMTP password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** the SSH session. */
  protected transient Session m_Session;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Provides access to a remote host via SSH.\n\n"
        + "For more information see:\n\n"
        + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.YEAR, "2011");
    result.setValue(Field.TITLE, "JSch - JSch is a pure Java implementation of SSH2.");
    result.setValue(Field.HTTP, "http://www.jcraft.com/jsch/");

    return result;
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
      "port", "port",
      22, 1, 65535);

    m_OptionManager.add(
      "authentication-type", "authenticationType",
      SSHAuthenticationType.CREDENTIALS);

    m_OptionManager.add(
      "user", "user",
      System.getProperty("user.name"), false);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(""), false);

    m_OptionManager.add(
      "private-key-file", "privateKeyFile",
      new PlaceholderFile(
        System.getProperty("user.home")
          + File.separator
          + ".ssh"
          + File.separator
          + "id_rsa"));

    m_OptionManager.add(
      "private-key-passphrase", "privateKeyPassphrase",
      new BasePassword(""), false);

    m_OptionManager.add(
      "known-hosts", "knownHosts",
      new PlaceholderFile(
        System.getProperty("user.home")
          + File.separator
          + ".ssh"
          + File.separator
          + "known_hosts"));

    m_OptionManager.add(
      "strict-host-key-checking", "strictHostKeyChecking",
      true);

    m_OptionManager.add(
      "forward-x", "forwardX",
      false);

    m_OptionManager.add(
      "x-host", "XHost",
      "");

    m_OptionManager.add(
      "x-port", "XPort",
      "0:0");

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

    if (m_AuthenticationType == SSHAuthenticationType.CREDENTIALS) {
      result = QuickInfoHelper.toString(this, "user", m_User);
      value = QuickInfoHelper.toString(this, "password", m_Password.getValue().replaceAll(".", "*"));
      if (value != null)
        result += ":" + value;
    }
    else {
      result = QuickInfoHelper.toString(this, "privateKeyFile", m_PrivateKeyFile);
    }
    result += QuickInfoHelper.toString(this, "host", (m_Host.length() == 0 ? "??" : m_Host), "@");
    result += QuickInfoHelper.toString(this, "port", m_Port, ":");

    options = new ArrayList<String>();
    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "strictHostKeyChecking", m_StrictHostKeyChecking, "strict hostkey"));
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
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to connect to.";
  }

  /**
   * Sets the type of authentication to use.
   *
   * @param value	the type
   */
  public void setAuthenticationType(SSHAuthenticationType value) {
    m_AuthenticationType = value;
    reset();
  }

  /**
   * Returns the type of authentication to use.
   *
   * @return		the type
   */
  public SSHAuthenticationType getAuthenticationType() {
    return m_AuthenticationType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String authenticationTypeTipText() {
    return "The type of authentication to use.";
  }

  /**
   * Sets the SSH user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the SSH user name to use.
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
    return "The SSH user to use for connecting.";
  }

  /**
   * Sets the SSH password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the SSH password to use.
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
    return "The password of the SSH user to use for connecting.";
  }

  /**
   * Sets the location of the private key file.
   *
   * @param value	the key file
   */
  public void setPrivateKeyFile(PlaceholderFile value) {
    m_PrivateKeyFile = value;
    reset();
  }

  /**
   * Returns the location of the private key file.
   *
   * @return		the key file
   */
  public PlaceholderFile getPrivateKeyFile() {
    return m_PrivateKeyFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String privateKeyFileTipText() {
    return "The location of the private key.";
  }

  /**
   * Sets the passphrase for the private key file, ignored if empty.
   *
   * @param value	the passphrase
   */
  public void setPrivateKeyPassphrase(BasePassword value) {
    m_PrivateKeyPassphrase = value;
    reset();
  }

  /**
   * Returns the passphrase for the private key file, ignored if empty.
   *
   * @return		the passphrase
   */
  public BasePassword getPrivateKeyPassphrase() {
    return m_PrivateKeyPassphrase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String privateKeyPassphraseTipText() {
    return "The passphrase for the private key file, ignored if empty.";
  }

  /**
   * Sets the file with the known hosts.
   *
   * @param value	the file
   */
  public void setKnownHosts(PlaceholderFile value) {
    m_KnownHosts = value;
    reset();
  }

  /**
   * Returns the file with the known hosts.
   *
   * @return		the file
   */
  public PlaceholderFile getKnownHosts() {
    return m_KnownHosts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String knownHostsTipText() {
    return "The file storing the known hosts.";
  }

  /**
   * Sets whether to perform strict host key checking.
   * NB: only disabled for testing, as it is very insecure to disable it!
   *
   * @param value	if true then strict checking is on
   */
  public void setStrictHostKeyChecking(boolean value) {
    m_StrictHostKeyChecking = value;
    reset();
  }

  /**
   * Returns whether to perform strict host key checking.
   * NB: only disabled for testing, as it is very insecure to disable it!
   *
   * @return 		true if strict checking is on
   */
  public boolean getStrictHostKeyChecking() {
    return m_StrictHostKeyChecking;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String strictHostKeyCheckingTipText() {
    return
      "Enables/disables strict host key checking - strict checking is the "
	+ "recommended setting, as disabling it is very insecure!";
  }

  /**
   * Sets whether to forward X11.
   *
   * @param value	if true then X11 is forwarded
   */
  public void setForwardX(boolean value) {
    m_ForwardX = value;
    reset();
  }

  /**
   * Returns whether X11 is forwarded.
   *
   * @return 		true if X11 is forwarded
   */
  public boolean getForwardX() {
    return m_ForwardX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String forwardXTipText() {
    return "If set to true, then X is forwarded.";
  }

  /**
   * Sets the xhost to connect to.
   *
   * @param value	the host name/ip
   */
  public void setXHost(String value) {
    m_XHost = value;
    reset();
  }

  /**
   * Returns the xhost to connect to.
   *
   * @return		the host name/ip
   */
  public String getXHost() {
    return m_XHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XHostTipText() {
    return "The xhost (name/IP address) to connect to.";
  }

  /**
   * Sets the xport to connect to.
   *
   * @param value	the port
   */
  public void setXPort(String value) {
    m_XPort = value;
    reset();
  }

  /**
   * Returns the xport to connect to.
   *
   * @return 		the port
   */
  public String getXPort() {
    return m_XPort;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String XPortTipText() {
    return "The xport to connect to.";
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
   * Returns the SSH session.
   *
   * @return		the SSH session, null if not connected
   */
  public synchronized Session getSession() {
    return m_Session;
  }

  /**
   * Returns a new session for the host/port defined in the options.
   *
   * @return		the session
   */
  public Session newSession() {
    return newSession(m_Host, m_Port);
  }

  /**
   * Returns a new session for the given host/port.
   *
   * @param host	the host to create the session for
   * @return		the session
   */
  public Session newSession(String host, int port) {
    Session result;
    JSch	jsch;

    try {
      jsch = new JSch();
      jsch.setLogger(new Logger() {
        @Override
        public boolean isEnabled(int level) {
          return true;
        }
        @Override
        public void log(int level, String message) {
          switch (level) {
            case DEBUG:
              getLogger().fine(message);
              break;
            case INFO:
              getLogger().info(message);
              break;
            case WARN:
              getLogger().warning(message);
              break;
            case ERROR:
            case FATAL:
              getLogger().severe(message);
              break;
          }
        }
      });
      // TODO choose RSA, DSA, ECDSA?
      jsch.setKnownHosts(m_KnownHosts.getAbsolutePath());
      switch (m_AuthenticationType) {
	case CREDENTIALS:
	  result = jsch.getSession(m_User, host, port);
	  result.setPassword(m_ActualPassword.getValue());
	  result.setConfig("StrictHostKeyChecking", m_StrictHostKeyChecking ? "yes" : "no");
	  break;
	case PUBLIC_KEY:
	  if (m_ActualPassword.getValue().isEmpty())
	    jsch.addIdentity(m_PrivateKeyFile.getAbsolutePath());
	  else
	    jsch.addIdentity(m_PrivateKeyFile.getAbsolutePath(), m_ActualPassword.getValue());
	  result = jsch.getSession(m_User, host, port);
	  result.setConfig("StrictHostKeyChecking", m_StrictHostKeyChecking ? "yes" : "no");
	  break;
	default:
	  throw new IllegalStateException("Unhandled authentication type: " + m_AuthenticationType);
      }
      if (m_ForwardX) {
	result.setX11Host(host);
	result.setX11Port(6000 + 0);
      }
      result.connect();
    }
    catch (Exception e) {
      handleException("Failed to establish connection to '" + host + "' (using " + m_AuthenticationType + "): ", e);
      result = null;
    }

    return result;
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

    // password
    switch (m_AuthenticationType) {
      case CREDENTIALS:
	m_ActualPassword = m_Password;
	break;
      case PUBLIC_KEY:
	m_ActualPassword = m_PrivateKeyPassphrase;
	break;
      default:
	throw new IllegalStateException("Unhandled authentication type: " + m_AuthenticationType);
    }

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

    if (result == null) {
      if (!m_Host.isEmpty()) {
        m_Session = newSession();
        if (m_Session == null)
          result = "Failed to connect to '" + m_Host + "' as user '" + m_User + "'!";
      }
      else {
        getLogger().warning("No host supplied, not initiating session!");
      }
    }

    return result;
  }

  /**
   * Disconnects the SSH session, if necessary.
   */
  protected void disconnect() {
    if (m_Session != null) {
      if (m_Session.isConnected()) {
        try {
          m_Session.disconnect();
        }
        catch (Exception e) {
          handleException("Failed to disconnect from '" + m_Host + "':", e);
        }
      }
    }
    m_Session = null;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    disconnect();
    super.wrapUp();
  }
}
