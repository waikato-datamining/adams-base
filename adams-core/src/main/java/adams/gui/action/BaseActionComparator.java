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
 * BaseActionComparator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import java.util.Comparator;

/**
 * Comparator for sorting {@link BaseAction} (and derived classes).
 * If both actions are derived from {@link BaseActionWithGroup} then the
 * group gets taken into account as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseActionComparator
  implements Comparator<BaseAction> {

  /**
   * Compares its two arguments for order.  Returns a negative integer,
   * zero, or a positive integer as the first argument is less than, equal
   * to, or greater than the second.
   * <br><br>
   * Uses the text of the menu items for comparison.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the
   * 	       first argument is less than, equal to, or greater than the
   *	       second.
   */
  public int compare(BaseAction o1, BaseAction o2) {
    int			result;
    BaseActionWithGroup	g1;
    BaseActionWithGroup	g2;

    result = 0;

    if ((o1 instanceof BaseActionWithGroup) && (o2 instanceof BaseActionWithGroup)) {
      g1 = (BaseActionWithGroup) o1;
      g2 = (BaseActionWithGroup) o2;

      if ((g1.getGroup() == null) && (g2.getGroup() == null))
	result = 0;
      else if ((g1.getGroup() == null) && (g2.getGroup() != null))
	result = 1;
      else if ((g1.getGroup() != null) && (g2.getGroup() == null))
	result = -1;
      else
	result = ((BaseActionWithGroup) o1).getGroup().compareTo(((BaseActionWithGroup) o2).getGroup());
    }

    if (result == 0)
      result = o1.getName().compareTo(o2.getName());

    return result;
  }
}
