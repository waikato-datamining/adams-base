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
 * SMBSetupPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Properties;
import adams.core.base.BaseHostname;
import adams.core.io.FileUtils;
import adams.core.net.SMBHelper;
import adams.env.Environment;
import adams.env.SMBDefinition;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.ParameterPanel;

import java.awt.BorderLayout;

/**
 * Panel for configuring the system-wide SMB settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SMBSetupPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7937644706618374284L;

  /** the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the wins host. */
  protected BaseObjectTextField<BaseHostname> m_TextWinsHost;

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.CENTER);

    m_TextWinsHost = new BaseObjectTextField<>(new BaseHostname(SMBHelper.getWinsHost()));
    m_PanelParameters.addParameter("_Wins host", m_TextWinsHost);
  }

  /**
   * Turns the parameters in the GUI into a properties object.
   *
   * @return		the properties
   */
  protected Properties toProperties() {
    Properties	result;

    result = new Properties();

    result.setProperty(SMBHelper.WINS_HOST, m_TextWinsHost.getText());

    return result;
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "SMB";
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

    result = SMBHelper.writeProperties(toProperties());
    if (result)
      return null;
    else
      return "Failed to save SMB setup to " + SMBHelper.FILENAME + "!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(SMBDefinition.KEY);
    return (props != null) && FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(SMBDefinition.KEY);
    if ((props != null) && FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
	return "Failed to remove custom SMB properties: " + props;
    }

    return null;
  }
}
