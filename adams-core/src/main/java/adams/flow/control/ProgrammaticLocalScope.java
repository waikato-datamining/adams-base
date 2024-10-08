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
 * ProgrammaticLocalScope.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Variables;

/**
 * Interface for classes that allow setting local variables/storage.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ProgrammaticLocalScope {

  /**
   * The local variables to use.
   *
   * @param variables	the variables
   */
  public void useLocalVariables(Variables variables);

  /**
   * The local storage to use.
   *
   * @param storage	the storage
   */
  public void useLocalStorage(Storage storage);
}
