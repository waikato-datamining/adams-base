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
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.flow.core.OptionalPasswordPrompt;
import adams.gui.dialog.PasswordDialog;
import jcifs.smb.NtlmPasswordAuthentication;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SMBConnection
  extends AbstractStandalone
  implements OptionalPasswordPrompt {

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
   * Returns the SMB session.
   *
   * @return		the SMB session, null if not connected
   */
  public synchronized NtlmPasswordAuthentication getSession() {
    if (m_Session == null)
      m_Session = newSession();
    return m_Session;
  }

  /**
   * Returns a new SMB session.
   *
   * @return		the SMB session, null if not connected
   */
  public NtlmPasswordAuthentication newSession() {
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

    // password
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
      else if (supportsHeadlessInteraction()) {
        if (!doInteractHeadless()) {
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
      m_Session = newSession();

    return result;
  }
}
