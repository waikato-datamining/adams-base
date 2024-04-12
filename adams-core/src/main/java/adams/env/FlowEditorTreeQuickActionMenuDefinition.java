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
 * FlowEditorTreeQuickActionMenuDefinition.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.env;

import adams.gui.flow.FlowEditorPanel;

/**
 * Definition for the FlowEditor props file containing tree quick action menu settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowEditorTreeQuickActionMenuDefinition
  extends AbstractPropertiesDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 288970991741946271L;

  /** the key as constant. */
  public final static String KEY = "flow editor tree quick action menu";

  /**
   * Returns the key this definition is for.
   *
   * @return		the key
   */
  @Override
  public String getKey() {
    return KEY;
  }

  /**
   * Returns the properties file name (no path) this definition is for.
   *
   * @return		the key
   */
  @Override
  public String getFile() {
    return FlowEditorPanel.FILENAME_TREEQUICKACTIONMENU;
  }

  /**
   * Updates the environment object with its definition for the props file
   * (whether to add/replace/etc the values).
   *
   * @param env		the environment object to update
   */
  @Override
  public void update(AbstractEnvironment env) {
    add(env, Project.NAME + "/gui/flow/tree/quickaction", new String[0]);
  }
}
