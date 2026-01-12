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
 * ParameterPanelPage.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.gui.core.BaseToggleButton;
import adams.gui.core.ImageManager;
import adams.gui.core.PropertiesParameterPanel;

import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Wizard page that use a {@link PropertiesParameterPanel} for displaying
 * the parameters. Parameters can be set using {@link Properties} object.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ParameterPanelPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;
  
  /** the parameter panel for displaying the parameters. */
  protected PropertiesParameterPanel m_PanelParameter;

  /** the button for toggling the help. */
  protected BaseToggleButton m_ButtonHelp;

  /**
   * Default constructor.
   */
  public ParameterPanelPage() {
    super();
  }
  
  /**
   * Initializes the page with the given page name.
   * 
   * @param pageName	the page name to use
   */
  public ParameterPanelPage(String pageName) {
    this();
    setPageName(pageName);
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelParameter = new PropertiesParameterPanel();
    m_PanelParameter.addChangeListener((ChangeEvent e) -> updateButtons());
    add(m_PanelParameter, BorderLayout.CENTER);

    m_ButtonHelp = new BaseToggleButton(ImageManager.getIcon("help"));
    m_ButtonHelp.addActionListener((ActionEvent e) -> m_PanelParameter.showHelp(m_ButtonHelp.isSelected()));
    m_ButtonHelp.setToolTipText("Display/hide help for parameters");
    m_PanelButtons.add(m_ButtonHelp);
  }
  
  /**
   * Returns the underlying parameter panel.
   * 
   * @return		the parameter panel
   */
  public PropertiesParameterPanel getParameterPanel() {
    return m_PanelParameter;
  }

  /**
   * Updates the wizard's buttons.
   */
  public void updateButtons() {
    super.updateButtons();
    m_ButtonHelp.setVisible(m_PanelParameter.hasAnyHelp());
  }

    /**
     * Sets the parameters from the properties object.
     *
     * @param value	the parameters to set
     */
  public void setProperties(Properties value) {
    m_PanelParameter.setProperties(value);
    updateButtons();
  }
  
  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    return m_PanelParameter.getProperties();
  }
}
