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
 * FontsPreferencesPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.env.Environment;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Preferences for fonts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FontsPreferencesPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = 3895159356677639564L;

  @Override
  protected void initGUI() {
    super.initGUI();

    addPropertyType("Button", PropertyType.FONT);
    addPropertyType("CheckBox", PropertyType.FONT);
    addPropertyType("CheckBoxMenuItem", PropertyType.FONT);
    addPropertyType("ComboBox", PropertyType.FONT);
    addPropertyType("EditorPane", PropertyType.FONT);
    addPropertyType("FormattedTextField", PropertyType.FONT);
    addPropertyType("Label", PropertyType.FONT);
    addPropertyType("List", PropertyType.FONT);
    addPropertyType("Menu", PropertyType.FONT);
    addPropertyType("MenuBar", PropertyType.FONT);
    addPropertyType("MenuItem", PropertyType.FONT);
    addPropertyType("PasswordField", PropertyType.FONT);
    addPropertyType("PopupMenu", PropertyType.FONT);
    addPropertyType("RadioButton", PropertyType.FONT);
    addPropertyType("RadioButtonMenuItem", PropertyType.FONT);
    addPropertyType("Spinner", PropertyType.FONT);
    addPropertyType("TabbedPane", PropertyType.FONT);
    addPropertyType("TextArea", PropertyType.FONT);
    addPropertyType("TextField", PropertyType.FONT);
    addPropertyType("TextPane", PropertyType.FONT);
    addPropertyType("ToggleButton", PropertyType.FONT);
    addPropertyType("ToolBar", PropertyType.FONT);
    addPropertyType("Tree", PropertyType.FONT);
    setPreferences(adams.gui.core.Fonts.getProperties());
  }

  /**
   * The title of the preferences.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Fonts";
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
    if (getPreferences().save(Environment.getInstance().createPropertiesFilename(adams.gui.core.Fonts.FILENAME)))
      return null;
    else
      return "Failed to save fonts setup!";
  }
}
