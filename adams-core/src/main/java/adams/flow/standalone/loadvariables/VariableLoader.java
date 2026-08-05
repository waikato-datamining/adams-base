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
 * VariableLoader.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.Variables;
import adams.core.option.OptionHandler;

/**
 * Interface for classes that load variables.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface VariableLoader
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Loads the variables.
   *
   * @param errors	for collecting errors
   * @return		the variables, null if failed to load
   */
  public Variables loadVariables(MessageCollection errors);
}
