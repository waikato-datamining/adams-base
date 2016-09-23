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
 * TerminalSettingsPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.io.FileUtils;
import adams.core.management.Terminal;
import adams.env.Environment;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

import java.io.File;

/**
 * Panel for configuring the terminal settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6765 $
 */
public class TerminalSettingsPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5325521437739323748L;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    addPropertyType(Terminal.LINUX_EXECUTABLE, PropertyType.STRING);
    addPropertyType(Terminal.LINUX_OPTIONS, PropertyType.STRING);
    addPropertyType(Terminal.MAC_EXECUTABLE, PropertyType.STRING);
    addPropertyType(Terminal.MAC_OPTIONS, PropertyType.STRING);
    addPropertyType(Terminal.WINDOWS_EXECUTABLE, PropertyType.STRING);
    addPropertyType(Terminal.WINDOWS_OPTIONS, PropertyType.STRING);
    setPropertyOrder(new String[]{
      Terminal.LINUX_EXECUTABLE,
      Terminal.LINUX_OPTIONS,
      Terminal.MAC_EXECUTABLE,
      Terminal.MAC_OPTIONS,
      Terminal.WINDOWS_EXECUTABLE,
      Terminal.WINDOWS_OPTIONS,
    });
    setPreferences(Terminal.getProperties());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Terminal";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return false;
  }

  /**
   * Activates the settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    if (Terminal.writeProperties(getPreferences()))
      return null;
    else
      return "Failed to save terminal setup!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().createPropertiesFilename(new File(Terminal.FILENAME).getName());
    return FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().createPropertiesFilename(new File(Terminal.FILENAME).getName());
    if (FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
        return "Failed to remove custom terminal properties: " + props;
    }

    return null;
  }
}
