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
 * MenuItemComparator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.history;

import java.util.Comparator;

/**
 * Comparator for sorting the menu items for the history panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MenuItemComparator
  implements Comparator<AbstractHistoryPopupMenuItem> {

  /**
   * Compares the menu items based on category and title.
   *
   * @param o1		the first menu item
   * @param o2		the second menu item
   * @return		the comparison
   */
  @Override
  public int compare(AbstractHistoryPopupMenuItem o1, AbstractHistoryPopupMenuItem o2) {
    int		result;

    result = o1.getCategory().compareTo(o2.getCategory());
    if (result == 0)
      result = o1.getTitle().compareTo(o2.getTitle());

    return result;
  }
}
