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
 * ErrorProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core;

/**
 * Interface for classes that record error messages.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ErrorProvider {

  /**
   * Returns whether an error was encountered during the last operation.
   *
   * @return		true if an error occurred
   */
  public boolean hasLastError();

  /**
   * Returns the error that occurred during the last operation.
   *
   * @return		the error string, null if none occurred
   */
  public String getLastError();
}
