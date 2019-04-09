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
 * CompareUtils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utility class for comparisons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompareUtils {

  /**
   * Compares two comparable objects. Takes care of null objects.
   * Returns -1, 0 or +1, if o1 less than, equal to or greater than o2.
   * Returns 0 if both objects null, -1 if o1 null but not o2 and +1 if o1 not
   * null but o2.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		the comparison result
   */
  public static int compare(Comparable o1, Comparable o2) {
    if ((o1 != null) && (o2 != null))
      return o1.compareTo(o2);
    else if ((o1 == null) && (o2 == null))
      return 0;
    else if (o1 == null)
      return -1;
    else
      return +1;
  }

  /**
   * Compares two integer arrays.
   *
   * @param a1		the first array
   * @param a2		the second array
   * @return		-1, 0, +1 if a1 is smaller than, equal to, or larger than a2
   */
  public static int compare(int[] a1, int[] a2) {
    int		result;
    int		i;

    result = Integer.compare(a1.length, a2.length);
    if (result == 0) {
      for (i = 0; i < a1.length; i++) {
        result = Integer.compare(a1[i], a2[i]);
        if (result != 0)
          break;
      }
    }

    return result;
  }

  /**
   * Compares two maps. Both, keys and values must be {@link Comparable}.
   *
   * @param m1		the first map
   * @param m2		the second map
   * @return		the comparison result
   */
  public static int compare(Map m1, Map m2) {
    int		result;
    List 	k1;
    List	k2;
    int		i;

    if ((m1 == null) && (m2 == null))
      return 0;
    if (m1 == null)
      return -1;
    if (m2 == null)
      return +1;

    k1 = new ArrayList(m1.keySet());
    k2 = new ArrayList(m2.keySet());
    result = Integer.compare(k1.size(), k2.size());
    if (result == 0) {
      Collections.sort(k1);
      Collections.sort(k2);
      // compare keys
      for (i = 0; i < k1.size(); i++) {
	result = ((Comparable) k1.get(i)).compareTo(k2.get(i));
	if (result != 0)
	  break;
      }
      // compare values
      if (result == 0) {
	for (i = 0; i < k1.size(); i++) {
	  result = ((Comparable) m1.get(k1.get(i))).compareTo(m2.get(k2.get(i)));
	  if (result != 0)
	    break;
	}
      }
    }

    return result;
  }
}
