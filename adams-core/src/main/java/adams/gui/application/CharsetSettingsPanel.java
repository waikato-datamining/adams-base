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
 * CharsetSettingsPanel.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.management.CharsetHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JComboBox;
import java.awt.BorderLayout;

/**
 * Panel for configuring the charset settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CharsetSettingsPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5325521437739323748L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the combobox with all the charsets. */
  protected JComboBox<String> m_ComboBoxCharsets;

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

    m_ComboBoxCharsets = new JComboBox<>(CharsetHelper.getIDs());
    m_PanelParameters.addParameter("_Charset", m_ComboBoxCharsets);

    // display values
    load();
  }

  /**
   * Loads the values from the props file and displays them.
   */
  protected void load() {
    CharsetHelper	helper;

    helper = CharsetHelper.getSingleton();
    helper.reload();

    // charset
    m_ComboBoxCharsets.setSelectedItem(helper.getCharset().toString());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Charset";
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
    CharsetHelper	helper;

    helper = CharsetHelper.getSingleton();

    // charset
    if (m_ComboBoxCharsets.getSelectedIndex() > -1)
      helper.setCharset(CharsetHelper.getIDs()[m_ComboBoxCharsets.getSelectedIndex()]);
    else
      helper.setCharset(CharsetHelper.CHARSET_DEFAULT);

    result = helper.save();
    
    if (result)
      return null;
    else
      return "Failed to save charset setup!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    return true;
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    boolean		result;
    CharsetHelper	helper;

    helper = CharsetHelper.getSingleton();
    helper.setCharset(CharsetHelper.CHARSET_DEFAULT);
    result = helper.save();

    if (result)
      return null;
    else
      return "Failed to reset charset setup!";
  }
}
