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
 * AbstractFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.logging.CustomLoggingLevelObject;

/**
 * Ancestor for file operation classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileOperations
  extends CustomLoggingLevelObject
  implements FileOperations {

  private static final long serialVersionUID = 4856156540114165327L;

  /**
   * Default constructor.
   */
  protected AbstractFileOperations() {
    super();
    initialize();
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
  }
}
