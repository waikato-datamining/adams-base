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
 * AbstractDatabaseConnection.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.db.datatype.AbstractDataTypeSetup;
import adams.db.datatype.DummySetup;
import adams.flow.core.OptionalPasswordPrompt;
import adams.gui.dialog.PasswordDialog;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;

/**
 * Ancestor for standalone actors providing a database connection different
 * from the system-wide one.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseConnection
  extends AbstractStandalone 
  implements OptionalPasswordPrompt {

  /** for serialization. */
  private static final long serialVersionUID = -1726172998200420556L;

  /** the URL to connect to the database. */
  protected String m_URL;

  /** database username. */
  protected String m_User;

  /** database password. */
  protected BasePassword m_Password;

  /** the actual SMTP password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** the data type setup to apply. */
  protected AbstractDataTypeSetup m_DataTypeSetup;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "url", "URL",
	    "jdbc:mysql://somehost:3306/somedatabase", false);

    m_OptionManager.add(
	    "user", "user",
	    "", false);

    m_OptionManager.add(
	    "password", "password",
	    new BasePassword(), false);

    m_OptionManager.add(
	    "prompt-for-password", "promptForPassword",
	    false);

    m_OptionManager.add(
	    "data-type-setup", "dataTypeSetup",
	    new DummySetup());

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
    String	result;

    result = QuickInfoHelper.toString(this, "URL", m_URL);

    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      result += ", prompt for password";
      result += QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow", ", ");
    }

    return result;
  }

  /**
   * Sets the database URL.
   *
   * @param value	the URL
   */
  public void setURL(String value) {
    m_URL = value;
    reset();
  }

  /**
   * Returns the database URL.
   *
   * @return 		the URL
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The JDBC URL of the database to connect to, can contain variables or file placeholders.";
  }
  
  /**
   * Expands variables and placeholders.
   * 
   * @return		the fully resolved URL
   */
  public String getResolvedURL() {
    String	result;
    
    result = m_URL;
    result = getVariables().expand(result);
    result = Placeholders.expandStr(result);
    
    return result;
  }

  /**
   * Sets the database user.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the database user.
   *
   * @return 		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The database user to connect with.";
  }

  /**
   * Sets the database password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the database password.
   *
   * @return 		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password of the database user.";
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
   * Sets the data type setup to apply to the connection.
   *
   * @param value	the setup
   */
  public void setDataTypeSetup(AbstractDataTypeSetup value) {
    m_DataTypeSetup = value;
    reset();
  }

  /**
   * Returns the data type setup to apply to the connection.
   *
   * @return 		the setup
   */
  public AbstractDataTypeSetup getDataTypeSetup() {
    return m_DataTypeSetup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String dataTypeSetupTipText() {
    return "The data type setup to apply to the connection.";
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
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    String				msg;
    adams.db.AbstractDatabaseConnection	conn;

    result = null;
    
    m_ActualPassword = m_Password;
    conn             = null;
    
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

    if (result == null) {
      conn = getConnection();
      msg  = null;
      if (!conn.isConnected() && !conn.getConnectOnStartUp()) {
	try {
	  conn.connect();
	}
	catch (Exception e) {
	  msg = handleException("Failed to connect to database (" + getURL() + "):", e);
	}
      }
      if (!conn.isConnected()) {
	result = "Failed to connect to database (" + getURL() + ")";
	if (msg == null)
	  result += "!";
	else
	  result += ": " + msg;
      }
    }
    
    if (result == null) {
      msg = m_DataTypeSetup.setupDataTypes(conn.getConnection(false));
      if (msg != null)
	result = "Failed to setup data types: " + msg;
    }

    return result;
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  public abstract adams.db.AbstractDatabaseConnection getConnection();
}
