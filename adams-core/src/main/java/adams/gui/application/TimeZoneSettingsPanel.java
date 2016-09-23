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
 * TimeZoneSettingsPanel.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.management.TimeZoneHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JComboBox;
import java.awt.BorderLayout;

/**
 * Panel for configuring the timezone settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeZoneSettingsPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5325521437739323748L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the combobox with all the timezones. */
  protected JComboBox<String> m_ComboBoxTimeZones;

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

    m_ComboBoxTimeZones = new JComboBox<>(TimeZoneHelper.getIDs());
    m_PanelParameters.addParameter("_Time zone", m_ComboBoxTimeZones);

    // display values
    load();
  }

  /**
   * Loads the values from the props file and displays them.
   */
  protected void load() {
    TimeZoneHelper	helper;

    helper = TimeZoneHelper.getSingleton();
    helper.reload();

    // time zone
    m_ComboBoxTimeZones.setSelectedItem(helper.getTimezone());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Time zone";
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
    TimeZoneHelper	helper;

    helper = TimeZoneHelper.getSingleton();

    // time zone
    if (m_ComboBoxTimeZones.getSelectedIndex() > -1)
      helper.setTimezone(TimeZoneHelper.getIDs()[m_ComboBoxTimeZones.getSelectedIndex()]);
    else
      helper.setTimezone(TimeZoneHelper.DEFAULT_TIMEZONE);

    result = helper.save();
    helper.initializeTimezone();
    
    if (result)
      return null;
    else
      return "Failed to save time zone setup!";
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
    TimeZoneHelper helper;

    helper = TimeZoneHelper.getSingleton();
    helper.setTimezone(TimeZoneHelper.DEFAULT_TIMEZONE);
    result = helper.save();

    if (result)
      return null;
    else
      return "Failed to reset time zone setup!";
  }
}
