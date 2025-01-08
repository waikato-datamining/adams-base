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
 * OptionalThreadLimiter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

/**
 * Interface for classes that have optional thread limits.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface OptionalThreadLimiter
  extends ThreadLimiter {

  /**
   * Sets whether to limit the number of threads to use.
   *
   * @param value 	true if to limit
   */
  public void setImposeThreadLimits(boolean value);

  /**
   * Returns whether to limit the number of threads to use.
   *
   * @return 		true if to limit
   */
  public boolean getImposeThreadLimits();

}
