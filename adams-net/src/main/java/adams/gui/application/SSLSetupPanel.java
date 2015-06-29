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
 * SSLSetupPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Properties;
import adams.core.net.SSLHelper;
import adams.core.option.OptionUtils;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.X509TrustManager;
import java.awt.BorderLayout;

/**
 * Panel for configuring the system-wide SSL settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SSLSetupPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7937644706618374284L;

  /** the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the hostname verifier. */
  protected GenericObjectEditorPanel m_GOEHostnameVerifier;

  /** the trust manager. */
  protected GenericObjectEditorPanel m_GOETrustManager;

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.CENTER);

    m_GOEHostnameVerifier = new GenericObjectEditorPanel(HostnameVerifier.class, new adams.core.net.hostnameverifier.All(), true);
    m_GOEHostnameVerifier.setPrefix("Hostname _verifier");
    m_PanelParameters.addParameter(m_GOEHostnameVerifier);

    m_GOETrustManager = new GenericObjectEditorPanel(X509TrustManager.class, new adams.core.net.trustmanager.All(), true);
    m_GOETrustManager.setPrefix("_Trust manager");
    m_PanelParameters.addParameter(m_GOETrustManager);
  }

  /**
   * Turns the parameters in the GUI into a properties object.
   *
   * @return		the properties
   */
  protected Properties toProperties() {
    Properties	result;

    result = new Properties();

    result.setProperty(SSLHelper.HOSTNAME_VERIFIER, OptionUtils.getCommandLine(m_GOEHostnameVerifier.getCurrent()));
    result.setProperty(SSLHelper.TRUST_MANAGER, OptionUtils.getCommandLine(m_GOETrustManager.getCurrent()));

    return result;
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "SSL";
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

    result = SSLHelper.writeProperties(toProperties());
    if (result)
      return null;
    else
      return "Failed to save SSL setup to " + SSLHelper.FILENAME + "!";
  }
}
