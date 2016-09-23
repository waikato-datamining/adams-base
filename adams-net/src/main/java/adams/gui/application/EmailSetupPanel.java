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

/**
 * EmailSetupPanel.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.base.BaseText;
import adams.core.io.FileUtils;
import adams.core.net.EmailHelper;
import adams.env.EmailDefinition;
import adams.env.Environment;
import adams.gui.chooser.BaseTextChooserPanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ParameterPanel;

import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

/**
 * Panel for configuring the system-wide Email settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailSetupPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7937644706618374284L;

  /** the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** Whether to enable email support. */
  protected JCheckBox m_CheckBoxEnabled;

  /** the SMTP host. */
  protected JTextField m_TextSmtpServer;

  /** the SMTP port. */
  protected JSpinner m_SpinnerSmtpPort;

  /** the SMTP timeout. */
  protected JSpinner m_SpinnerSmtpTimeout;

  /** Whether the SMTP server requires authentication. */
  protected JCheckBox m_CheckBoxSmtpRequiresAuthentication;

  /** Whether to start TLS. */
  protected JCheckBox m_CheckBoxSmtpStartTLS;

  /** Whether to use SSL. */
  protected JCheckBox m_CheckBoxSmtpUseSSL;

  /** the SMTP user. */
  protected JTextField m_TextSmtpUser;

  /** the SMTP password. */
  protected JPasswordField m_TextSmtpPassword;

  /** Whether to show the password. */
  protected JCheckBox m_CheckBoxShowPassword;

  /** the default FROM address. */
  protected JTextField m_TextDefaultFromAddress;

  /** the default signature. */
  protected BaseTextChooserPanel m_TextDefaultSignature;

  /** the support email addres. */
  protected JTextField m_TextSupportEmailAddress;

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.CENTER);

    m_CheckBoxEnabled = new JCheckBox();
    m_CheckBoxEnabled.setSelected(EmailHelper.isEnabled());
    m_PanelParameters.addParameter("_Enabled", m_CheckBoxEnabled);

    m_TextSmtpServer = new JTextField(20);
    m_TextSmtpServer.setText(EmailHelper.getSmtpServer());
    m_PanelParameters.addParameter("SMTP _Server", m_TextSmtpServer);

    m_SpinnerSmtpPort = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerSmtpPort.getModel()).setMinimum(0);
    ((SpinnerNumberModel) m_SpinnerSmtpPort.getModel()).setMaximum(65530);
    ((SpinnerNumberModel) m_SpinnerSmtpPort.getModel()).setStepSize(1);
    m_SpinnerSmtpPort.setValue(EmailHelper.getSmtpPort());
    m_PanelParameters.addParameter("SMTP _Port", m_SpinnerSmtpPort);

    m_SpinnerSmtpTimeout = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerSmtpTimeout.getModel()).setMinimum(0);
    ((SpinnerNumberModel) m_SpinnerSmtpTimeout.getModel()).setMaximum(1000000);
    ((SpinnerNumberModel) m_SpinnerSmtpTimeout.getModel()).setStepSize(1000);
    m_SpinnerSmtpTimeout.setValue(EmailHelper.getSmtpTimeout());
    m_PanelParameters.addParameter("SMTP T_imeout (msec)", m_SpinnerSmtpTimeout);

    m_CheckBoxSmtpRequiresAuthentication = new JCheckBox();
    m_CheckBoxSmtpRequiresAuthentication.setSelected(EmailHelper.getSmtpRequiresAuthentication());
    m_PanelParameters.addParameter("_Authentication required", m_CheckBoxSmtpRequiresAuthentication);

    m_CheckBoxSmtpStartTLS = new JCheckBox();
    m_CheckBoxSmtpStartTLS.setSelected(EmailHelper.getSmtpStartTLS());
    m_PanelParameters.addParameter("Start _TLS", m_CheckBoxSmtpStartTLS);

    m_CheckBoxSmtpUseSSL = new JCheckBox();
    m_CheckBoxSmtpUseSSL.setSelected(EmailHelper.getSmtpUseSSL());
    m_PanelParameters.addParameter("Use SS_L", m_CheckBoxSmtpUseSSL);

    m_TextSmtpUser = new JTextField(20);
    m_TextSmtpUser.setText(EmailHelper.getSmtpUser());
    m_PanelParameters.addParameter("SMTP _User", m_TextSmtpUser);

    m_TextSmtpPassword = new JPasswordField(20);
    m_TextSmtpPassword.setText(EmailHelper.getSmtpPassword().getValue());
    m_TextSmtpPassword.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelParameters.addParameter("SMTP _Password", m_TextSmtpPassword);

    m_CheckBoxShowPassword = new JCheckBox();
    m_CheckBoxShowPassword.setSelected(false);
    m_CheckBoxShowPassword.addActionListener((ActionEvent e) -> {
      if (m_CheckBoxShowPassword.isSelected())
        m_TextSmtpPassword.setEchoChar((char) 0);
      else
        m_TextSmtpPassword.setEchoChar(Constants.PASSWORD_CHAR);
    });
    m_PanelParameters.addParameter("Sho_w Password", m_CheckBoxShowPassword);

    m_TextDefaultFromAddress = new JTextField(20);
    m_TextDefaultFromAddress.setText(EmailHelper.getDefaultFromAddress());
    m_PanelParameters.addParameter("Default FROM address", m_TextDefaultFromAddress);

    m_TextDefaultSignature = new BaseTextChooserPanel();
    m_TextDefaultSignature.setDialogTitle("Enter signature");
    m_TextDefaultSignature.setDialogSize(new Dimension(600, 400));
    m_TextDefaultSignature.setTextColumns(16);  // TODO longer
    m_TextDefaultSignature.setCurrent(new BaseText(Utils.unbackQuoteChars(EmailHelper.getDefaultSignature())));
    m_PanelParameters.addParameter("Default signature", new BaseScrollPane(m_TextDefaultSignature));

    m_TextSupportEmailAddress = new JTextField(20);
    m_TextSupportEmailAddress.setText(EmailHelper.getSupportEmail());
    m_PanelParameters.addParameter("Suport email address", m_TextSupportEmailAddress);
  }

  /**
   * Turns the parameters in the GUI into a properties object.
   *
   * @return		the properties
   */
  protected Properties toProperties() {
    Properties	result;

    result = new Properties();

    result.setBoolean(EmailHelper.ENABLED, m_CheckBoxEnabled.isSelected());
    result.setProperty(EmailHelper.SMTP_SERVER, m_TextSmtpServer.getText());
    result.setInteger(EmailHelper.SMTP_PORT, ((Number) m_SpinnerSmtpPort.getValue()).intValue());
    result.setInteger(EmailHelper.SMTP_TIMEOUT, ((Number) m_SpinnerSmtpTimeout.getValue()).intValue());
    result.setBoolean(EmailHelper.SMTP_REQUIRES_AUTHENTICATION, m_CheckBoxSmtpRequiresAuthentication.isSelected());
    result.setBoolean(EmailHelper.SMTP_START_TLS, m_CheckBoxSmtpStartTLS.isSelected());
    result.setBoolean(EmailHelper.SMTP_USE_SSL, m_CheckBoxSmtpUseSSL.isSelected());
    result.setProperty(EmailHelper.SMTP_USER, m_TextSmtpUser.getText());
    result.setPassword(EmailHelper.SMTP_PASSWORD, new BasePassword(m_TextSmtpPassword.getText()));
    result.setProperty(EmailHelper.DEFAULT_ADDRESS_FROM, m_TextDefaultFromAddress.getText());
    result.setProperty(EmailHelper.DEFAULT_SIGNATURE, Utils.backQuoteChars(m_TextDefaultSignature.getCurrent().getValue()));
    result.setProperty(EmailHelper.SUPPORT_EMAIL, m_TextSupportEmailAddress.getText());

    return result;
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Email";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return true;
  }
  
  /**
   * Activates the email setup.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    boolean	result;

    result = EmailHelper.writeProperties(toProperties());
    if (result)
      return null;
    else
      return "Failed to save email setup to " + EmailHelper.FILENAME + "!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(EmailDefinition.KEY);
    return (props != null) && FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(EmailDefinition.KEY);
    if ((props != null) && FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
	return "Failed to remove custom email properties: " + props;
    }

    return null;
  }
}
