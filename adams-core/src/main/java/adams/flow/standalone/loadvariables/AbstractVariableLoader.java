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
 * AbstractVariableLoader.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.Variables;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for variable loaders.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractVariableLoader
  extends AbstractOptionHandler
  implements VariableLoader {

  private static final long serialVersionUID = 4441178581271793742L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks whether the variables can be loaded.
   *
   * @param errors	for collecting errors
   */
  protected void check(MessageCollection errors) {
  }

  /**
   * Loads the variables.
   *
   * @param errors	for collecting errors
   * @return		the variables
   */
  protected abstract Variables doLoadVariables(MessageCollection errors);

  /**
   * Loads the variables.
   *
   * @param errors	for collecting errors
   * @return		the variables, null if failed to load
   */
  public Variables loadVariables(MessageCollection errors) {
    Variables		result;

    check(errors);
    if (!errors.isEmpty())
      return null;

    result = doLoadVariables(errors);
    if (errors.isEmpty())
      return result;
    else
      return null;
  }
}
