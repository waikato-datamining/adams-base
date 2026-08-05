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
 * Null.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.Variables;

/**
 * Dummy, does nothing.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Null
  extends AbstractVariableLoader {

  private static final long serialVersionUID = -852576582084113917L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does nothing.";
  }

  /**
   * Loads the variables.
   *
   * @param errors 	for collecting errors
   * @return 		the variables
   */
  @Override
  protected Variables doLoadVariables(MessageCollection errors) {
    return new Variables();
  }
}
