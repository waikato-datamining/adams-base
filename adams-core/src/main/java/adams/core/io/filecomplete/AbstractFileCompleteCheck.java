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
 * AbstractFileCompleteCheck.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filecomplete;

import adams.core.option.AbstractOptionHandler;

import java.io.File;

/**
 * Ancestor for classes that check whether a file is complete.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileCompleteCheck
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -8384228126040647401L;

  /**
   * Checks whether the file is complete.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  public abstract boolean isComplete(File file);
}
