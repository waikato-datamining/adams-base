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
 * ProxyPanel.java
 * Copyright (C) 2010-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Constants;
import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.io.FileUtils;
import adams.core.net.ProxyHelper;
import adams.env.Environment;
import adams.env.ProxyDefinition;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextField;
import adams.gui.core.ParameterPanel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.Proxy;

/**
 * Panel for configuring the proxy settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProxySettingsPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -8502296969739181339L;

  /** the panel for the type. */
  protected BasePanel m_PanelType;

  /** the combobox for the proxy type. */
  protected BaseComboBox m_ComboBoxType;

  /** the panel for the http parameters. */
  protected ParameterPanel m_PanelHttpFtp;

  /** the http proxy host. */
  protected BaseTextField m_TextHttpFtpHost;

  /** the http proxy port. */
  protected JSpinner m_SpinnerHttpFtpPort;

  /** whether authentication is necessary. */
  protected BaseCheckBox m_CheckBoxHttpFtpAuthentication;

  /** the http proxy user. */
  protected BaseTextField m_TextHttpFtpUser;

  /** the http proxy password. */
  protected JPasswordField m_TextHttpFtpPassword;

  /** Whether to show the http password. */
  protected BaseCheckBox m_CheckBoxShowHttpFtpPassword;

  /** the hosts that bypass the http proxy . */
  protected BaseTextField m_TextHttpFtpNoProxy;

  /** the panel for the socks parameters. */
  protected ParameterPanel m_PanelSocks;

  /** the socks proxy host. */
  protected BaseTextField m_TextSocksHost;

  /** the socks proxy port. */
  protected JSpinner m_SpinnerSocksPort;

  /** whether authentication is necessary. */
  protected BaseCheckBox m_CheckBoxSocksAuthentication;

  /** the socks proxy user. */
  protected BaseTextField m_TextSocksUser;

  /** the socks proxy password. */
  protected JPasswordField m_TextSocksPassword;

  /** Whether to show the password. */
  protected BaseCheckBox m_CheckBoxShowSocksPassword;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JLabel	label;

    super.initGUI();

    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    setLayout(new BorderLayout());

    // type
    m_PanelType = new BasePanel(new GridLayout(1, 2, 5, 5));
    add(m_PanelType, BorderLayout.NORTH);

    m_ComboBoxType = new BaseComboBox(Proxy.Type.values());
    m_ComboBoxType.addActionListener((ActionEvent e) -> {
      // http/ftp
      m_PanelHttpFtp.setEnabled(m_ComboBoxType.getSelectedItem().equals(Proxy.Type.HTTP));
      boolean sel = m_CheckBoxHttpFtpAuthentication.isSelected();
      m_CheckBoxHttpFtpAuthentication.setSelected(!sel);
      m_CheckBoxHttpFtpAuthentication.setSelected(sel);
      // socks
      m_PanelSocks.setEnabled(m_ComboBoxType.getSelectedItem().equals(Proxy.Type.SOCKS));
      sel = m_CheckBoxSocksAuthentication.isSelected();
      m_CheckBoxSocksAuthentication.setSelected(!sel);
      m_CheckBoxSocksAuthentication.setSelected(sel);
    });
    label = new JLabel("Connection");
    label.setLabelFor(m_ComboBoxType);
    m_PanelType.add(label);
    m_PanelType.add(m_ComboBoxType);

    // http+ftp
    m_PanelHttpFtp = new ParameterPanel();
    m_PanelHttpFtp.setBorder(BorderFactory.createTitledBorder("Http & Ftp"));
    add(m_PanelHttpFtp, BorderLayout.CENTER);

    m_TextHttpFtpHost = new BaseTextField(15);
    m_PanelHttpFtp.addParameter("_Host", m_TextHttpFtpHost);

    m_SpinnerHttpFtpPort = new JSpinner();
    m_PanelHttpFtp.addParameter("Port", m_SpinnerHttpFtpPort);

    m_TextHttpFtpNoProxy = new BaseTextField(15);
    m_PanelHttpFtp.addParameter("No pro_xy for", m_TextHttpFtpNoProxy);

    m_CheckBoxHttpFtpAuthentication = new BaseCheckBox();
    m_CheckBoxHttpFtpAuthentication.addChangeListener((ChangeEvent e) -> {
      m_TextHttpFtpUser.setEnabled(m_CheckBoxHttpFtpAuthentication.isSelected() && m_PanelHttpFtp.isEnabled());
      m_TextHttpFtpPassword.setEnabled(m_CheckBoxHttpFtpAuthentication.isSelected() && m_PanelHttpFtp.isEnabled());
      m_CheckBoxShowHttpFtpPassword.setEnabled(m_CheckBoxHttpFtpAuthentication.isSelected() && m_PanelHttpFtp.isEnabled());
    });
    m_PanelHttpFtp.addParameter("Requires _authentication", m_CheckBoxHttpFtpAuthentication);

    m_TextHttpFtpUser = new BaseTextField(15);
    m_TextHttpFtpUser.setEnabled(false);
    m_PanelHttpFtp.addParameter("User", m_TextHttpFtpUser);

    m_TextHttpFtpPassword = new JPasswordField(15);
    m_TextHttpFtpPassword.setEnabled(false);
    m_TextHttpFtpPassword.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelHttpFtp.addParameter("Password", m_TextHttpFtpPassword);

    m_CheckBoxShowHttpFtpPassword = new BaseCheckBox();
    m_CheckBoxShowHttpFtpPassword.setSelected(false);
    m_CheckBoxShowHttpFtpPassword.addActionListener((ActionEvent e) -> {
      if (m_CheckBoxShowHttpFtpPassword.isSelected())
        m_TextHttpFtpPassword.setEchoChar((char) 0);
      else
        m_TextHttpFtpPassword.setEchoChar(Constants.PASSWORD_CHAR);
    });
    m_PanelHttpFtp.addParameter("Sho_w Password", m_CheckBoxShowHttpFtpPassword);

    // socks
    m_PanelSocks = new ParameterPanel();
    m_PanelSocks.setBorder(BorderFactory.createTitledBorder("Socks"));
    add(m_PanelSocks, BorderLayout.SOUTH);

    m_TextSocksHost = new BaseTextField(15);
    m_PanelSocks.addParameter("_Host", m_TextSocksHost);

    m_SpinnerSocksPort = new JSpinner();
    m_PanelSocks.addParameter("Port", m_SpinnerSocksPort);

    m_CheckBoxSocksAuthentication = new BaseCheckBox();
    m_CheckBoxSocksAuthentication.addChangeListener((ChangeEvent e) -> {
      m_TextSocksUser.setEnabled(m_CheckBoxSocksAuthentication.isSelected() && m_PanelSocks.isEnabled());
      m_TextSocksPassword.setEnabled(m_CheckBoxSocksAuthentication.isSelected() && m_PanelSocks.isEnabled());
      m_CheckBoxShowSocksPassword.setEnabled(m_CheckBoxSocksAuthentication.isSelected() && m_PanelSocks.isEnabled());
    });
    m_PanelSocks.addParameter("Requires _authentication", m_CheckBoxSocksAuthentication);

    m_TextSocksUser = new BaseTextField(15);
    m_TextSocksUser.setEnabled(false);
    m_PanelSocks.addParameter("User", m_TextSocksUser);

    m_TextSocksPassword = new JPasswordField(15);
    m_TextSocksPassword.setEnabled(false);
    m_TextSocksPassword.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelSocks.addParameter("Password", m_TextSocksPassword);

    m_CheckBoxShowSocksPassword = new BaseCheckBox();
    m_CheckBoxShowSocksPassword.setSelected(false);
    m_CheckBoxShowSocksPassword.addActionListener((ActionEvent e) -> {
      if (m_CheckBoxShowSocksPassword.isSelected())
        m_TextSocksPassword.setEchoChar((char) 0);
      else
        m_TextSocksPassword.setEchoChar(Constants.PASSWORD_CHAR);
    });
    m_PanelSocks.addParameter("Sho_w Password", m_CheckBoxShowSocksPassword);

    // display values
    load();
  }

  /**
   * Loads the values from the props file and displays them.
   */
  protected void load() {
    ProxyHelper	proxy;

    proxy = ProxyHelper.getSingleton();
    proxy.reload();

    // type
    m_ComboBoxType.setSelectedItem(proxy.getProxyType());

    // http
    m_TextHttpFtpHost.setText(proxy.getHost(Proxy.Type.HTTP));
    m_SpinnerHttpFtpPort.setValue(proxy.getPort(Proxy.Type.HTTP));
    m_TextHttpFtpNoProxy.setText(Utils.flatten(proxy.getNoProxy(Proxy.Type.HTTP), ", "));
    m_CheckBoxHttpFtpAuthentication.setSelected(proxy.getAuthentication(Proxy.Type.HTTP));
    m_TextHttpFtpUser.setText(proxy.getUser(Proxy.Type.HTTP));
    m_TextHttpFtpPassword.setText(proxy.getPassword(Proxy.Type.HTTP).getValue());

    // socks
    m_TextSocksHost.setText(proxy.getHost(Proxy.Type.SOCKS));
    m_SpinnerSocksPort.setValue(proxy.getPort(Proxy.Type.SOCKS));
    m_CheckBoxSocksAuthentication.setSelected(proxy.getAuthentication(Proxy.Type.SOCKS));
    m_TextSocksUser.setText(proxy.getUser(Proxy.Type.SOCKS));
    m_TextSocksPassword.setText(proxy.getPassword(Proxy.Type.SOCKS).getValue());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Proxy";
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
   * Activates the proxy settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    boolean	result;    
    ProxyHelper	proxy;

    proxy = ProxyHelper.getSingleton();

    // type
    if (m_ComboBoxType.getSelectedIndex() > -1)
      proxy.setProxyType(Proxy.Type.values()[m_ComboBoxType.getSelectedIndex()]);
    else
      proxy.setProxyType(Proxy.Type.DIRECT);

    // http
    proxy.setHost(Proxy.Type.HTTP, m_TextHttpFtpHost.getText());
    proxy.setPort(Proxy.Type.HTTP, ((Number) m_SpinnerHttpFtpPort.getValue()).intValue());
    proxy.setNoProxy(Proxy.Type.HTTP, m_TextHttpFtpNoProxy.getText().replaceAll(" ", "").split(","));
    proxy.setAuthentication(Proxy.Type.HTTP, m_CheckBoxHttpFtpAuthentication.isSelected());
    proxy.setUser(Proxy.Type.HTTP, m_TextHttpFtpUser.getText());
    proxy.setPassword(Proxy.Type.HTTP, new BasePassword(new String(m_TextHttpFtpPassword.getPassword())));

    // socks
    proxy.setHost(Proxy.Type.SOCKS, m_TextSocksHost.getText());
    proxy.setPort(Proxy.Type.SOCKS, ((Number) m_SpinnerSocksPort.getValue()).intValue());
    proxy.setAuthentication(Proxy.Type.SOCKS, m_CheckBoxSocksAuthentication.isSelected());
    proxy.setUser(Proxy.Type.SOCKS, m_TextSocksUser.getText());
    proxy.setPassword(Proxy.Type.SOCKS, new BasePassword(new String(m_TextSocksPassword.getPassword())));

    result = proxy.save();
    proxy.initializeProxy();
    
    if (result)
      return null;
    else
      return "Failed to save proxy setup!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(ProxyDefinition.KEY);
    return (props != null) && FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(ProxyDefinition.KEY);
    if ((props != null) && FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
	return "Failed to remove custom proxy properties: " + props;
    }

    return null;
  }
}
