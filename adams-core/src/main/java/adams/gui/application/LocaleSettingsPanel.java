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
 * LocaleSettingsPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.awt.BorderLayout;

import javax.swing.JComboBox;

import adams.core.management.LocaleHelper;
import adams.gui.core.ParameterPanel;

/**
 * Panel for configuring the locale settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocaleSettingsPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5325521437739323748L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the combobox with all the locales. */
  protected JComboBox m_ComboBoxLocales;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    // type
    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.NORTH);

    m_ComboBoxLocales = new JComboBox(LocaleHelper.getIDs());
    m_PanelParameters.addParameter("_Locale", m_ComboBoxLocales);

    // display values
    load();
  }

  /**
   * Loads the values from the props file and displays them.
   */
  protected void load() {
    LocaleHelper	helper;

    helper = LocaleHelper.getSingleton();
    helper.reload();

    // locale
    m_ComboBoxLocales.setSelectedItem(helper.getLocale());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Locale";
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
    LocaleHelper	helper;

    helper = LocaleHelper.getSingleton();

    // locale
    if (m_ComboBoxLocales.getSelectedIndex() > -1)
      helper.setLocale(LocaleHelper.getIDs()[m_ComboBoxLocales.getSelectedIndex()]);
    else
      helper.setLocale(LocaleHelper.LOCALE_DEFAULT);

    result = helper.save();
    helper.initializeLocale();
    
    if (result)
      return null;
    else
      return "Failed to save locale setup!";
  }
}
