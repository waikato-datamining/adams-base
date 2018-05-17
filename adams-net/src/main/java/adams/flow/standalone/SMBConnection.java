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
 * SMBConnection.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.core.net.SMBAuthenticationProvider;
import adams.flow.control.Flow;
import adams.flow.core.OptionalPasswordPrompt;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.gui.dialog.PasswordDialog;
import jcifs.smb.NtlmPasswordAuthentication;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Provides access to a remote host via SMB.
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
 * &nbsp;&nbsp;&nbsp;default: SMBConnection
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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-domain &lt;java.lang.String&gt; (property: domain)
 * &nbsp;&nbsp;&nbsp;The domain name to connect to.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The SMB user to use for connecting.
 * </pre>
 *
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the SMB user to use for connecting.
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
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SMBConnection
  extends AbstractStandalone
  implements OptionalPasswordPrompt, SMBAuthenticationProvider {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the domain. */
  protected String m_Domain;

  /** the SMB user to use. */
  protected String m_User;

  /** the SMB password to use. */
  protected BasePassword m_Password;

  /** the actual SMTP password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** how to perform the stop. */
  protected StopMode m_StopMode;

  /** the SMB authentication. */
  protected transient NtlmPasswordAuthentication m_Session;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Provides access to a remote host via SMB.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "domain", "domain",
      "");

    m_OptionManager.add(
      "user", "user",
      System.getProperty("user.name"), false);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(""), false);

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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;
    String		value;

    result = QuickInfoHelper.toString(this, "domain", (m_Domain.length() == 0 ? "no domain" : m_Domain), "domain: ");
    result += QuickInfoHelper.toString(this, "user", m_User, ", user: ");
    value = QuickInfoHelper.toString(this, "password", m_Password.getValue().replaceAll(".", "*"));
    if (value != null)
      result += ", pw: " + value;

    options = new ArrayList<>();
    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
    }
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the domain to connect to.
   *
   * @param value	the domain name
   */
  public void setDomain(String value) {
    m_Domain = value;
    reset();
  }

  /**
   * Returns the domain to connect to.
   *
   * @return		the domain name
   */
  public String getDomain() {
    return m_Domain;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String domainTipText() {
    return "The domain name to connect to.";
  }

  /**
   * Sets the SMB user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the SMB user name to use.
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
    return "The SMB user to use for connecting.";
  }

  /**
   * Sets the SMB password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the SMB password to use.
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
    return "The password of the SMB user to use for connecting.";
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
   * @return		true if successfully interacted
   */
  public boolean doInteract() {
    boolean		result;
    PasswordDialog	dlg;

    dlg = new PasswordDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dlg.setLocationRelativeTo(getParentComponent());
    ((Flow) getRoot()).registerWindow(dlg, dlg.getTitle());
    dlg.setVisible(true);
    ((Flow) getRoot()).deregisterWindow(dlg);
    result = (dlg.getOption() == PasswordDialog.APPROVE_OPTION);

    if (result)
      m_ActualPassword = dlg.getPassword();

    return result;
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
   * @return		true if successfully interacted
   */
  public boolean doInteractHeadless() {
    boolean		result;
    BasePassword	password;

    result   = false;
    password = ConsoleHelper.enterPassword("Please enter password (" + getName() + "):");
    if (password != null) {
      result           = true;
      m_ActualPassword = password;
    }

    return result;
  }

  /**
   * Returns the SMB authentication.
   *
   * @return		the SMB authentication, null if not connected
   */
  public synchronized NtlmPasswordAuthentication getAuthentication() {
    if (m_Session == null)
      m_Session = newAuthentication();
    return m_Session;
  }

  /**
   * Returns a new SMB authentication.
   *
   * @return		the SMB authentication
   */
  public NtlmPasswordAuthentication newAuthentication() {
    return new NtlmPasswordAuthentication(m_Domain, m_User, m_ActualPassword.getValue());
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

    if (m_Session == null) {
      if (isLoggingEnabled())
        getLogger().info("Starting new session");

      // password
      m_ActualPassword = m_Password;

      if (m_PromptForPassword && (m_Password.getValue().length() == 0)) {
        if (!isHeadless()) {
          if (!doInteract()) {
            if (m_StopFlowIfCanceled) {
              if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
                StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
              else
                StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
              result = getStopMessage();
            }
          }
        }
        else if (supportsHeadlessInteraction()) {
          if (!doInteractHeadless()) {
            if (m_StopFlowIfCanceled) {
              if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
                StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
              else
                StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
              result = getStopMessage();
            }
          }
        }
      }

      if (result == null)
        m_Session = newAuthentication();
    }
    else {
      if (isLoggingEnabled())
        getLogger().info("Re-using current session");
    }

    return result;
  }
}
