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
 * RProjectSettingsPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.awt.BorderLayout;

import javax.swing.JSpinner;
import javax.swing.JTextField;

import adams.core.RProjectHelper;
import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractPreferencesPanel;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.ParameterPanel;

/**
 * Panel for configuring the R-Project settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RProjectSettingsPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = 2688871131555552065L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the R executable. */
  protected FileChooserPanel m_FileRExecutable;

  /** the Rserve host. */
  protected JTextField m_TextRserveHost;

  /** the Rserve port. */
  protected JSpinner m_SpinnerRservePort;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.NORTH);

    // R
    m_FileRExecutable = new FileChooserPanel();
    m_FileRExecutable.setPrefix("_R executable");
    m_PanelParameters.addParameter(m_FileRExecutable);
    
    // Rserve
    m_TextRserveHost = new JTextField(15);
    m_PanelParameters.addParameter("Rserve _Host", m_TextRserveHost);

    m_SpinnerRservePort = new JSpinner();
    m_PanelParameters.addParameter("Rserve _Port", m_SpinnerRservePort);

    // display values
    load();
  }

  /**
   * Loads the values from the props file and displays them.
   */
  protected void load() {
    RProjectHelper	helper;

    helper = RProjectHelper.getSingleton();
    helper.reload();

    // R
    m_FileRExecutable.setCurrent(helper.getRExecutable());
    
    // Rserve
    m_TextRserveHost.setText(helper.getRserveHost());
    m_SpinnerRservePort.setValue(helper.getRservePort());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "R-Project";
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
    boolean		result;    
    RProjectHelper	helper;

    helper = RProjectHelper.getSingleton();

    // R
    helper.setRExecutable(new PlaceholderFile(m_FileRExecutable.getCurrent()));
    
    // Rserve
    helper.setRserveHost(m_TextRserveHost.getText());
    helper.setRservePort(((Number) m_SpinnerRservePort.getValue()).intValue());

    result = helper.save();
    
    if (result)
      return null;
    else
      return "Failed to save R-Project setup!";
  }
}
