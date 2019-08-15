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
 * AcceptAllItemFilter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.dotnotationtree;

/**
 * Accepts all items.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AcceptAllItemFilter
  extends AbstractItemFilter {

  /**
   * Performs the actual filtering.
   *
   * @param item	the item to check
   * @return		always true
   */
  @Override
  protected boolean doFilter(String item) {
    return true;
  }

  /**
   * Returns a short representation of the filter.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "Accepts all items";
  }
}
