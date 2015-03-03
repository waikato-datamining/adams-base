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
 * StrictClassTreeFilter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe.classtree;

/**
 * Interface for filters that allow strict filtering.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface StrictClassTreeFilter {

  /**
   * Sets whether to use strict or relaxed filtering.
   *
   * @param value	if true strict mode is enabled
   */
  public void setStrict(boolean value);

  /**
   * Returns whether strict or relaxed filtering is used.
   *
   * @return		true if strict mode is enabled
   */
  public boolean isStrict();
}
