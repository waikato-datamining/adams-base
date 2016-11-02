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
 * ListHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core;

import java.util.List;

/**
 * Helper class for lists.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListHelper {

  /**
   * Swaps the two rows.
   *
   * @param firstIndex	the index of the first row
   * @param secondIndex	the index of the second row
   */
  protected static void swap(List list, int firstIndex, int secondIndex) {
    Object	backup;

    backup = list.get(firstIndex);
    list.set(firstIndex, list.get(secondIndex));
    list.set(secondIndex, backup);
  }

  /**
   * moves the selected items by a certain amount of items in a given direction.
   *
   * @param indices	the indices to move
   * @param moveby      the number of items to move by
   * @param up	   	if true then items are moved up, otherwise down
   * @return		the updated selected indices
   */
  protected static int[] moveItems(List list, int[] indices, int moveby, boolean up) {
    int		i;
    int		newIndex;

    if (up) {
      for (i = 0; i < indices.length; i++) {
	if (indices[i] == 0)
	  continue;
	newIndex = indices[i] - moveby;
	swap(list, indices[i], newIndex);
	indices[i] = newIndex;
      }
    }
    else {
      for (i = indices.length - 1; i >= 0; i--) {
	if (indices[i] == list.size() - 1)
	  continue;
	newIndex = indices[i] + moveby;
	swap(list, indices[i], newIndex);
	indices[i] = newIndex;
      }
    }

    return indices;
  }

  /**
   * moves the selected items up by 1.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public static int[] moveUp(List list, int[] indices) {
    return moveItems(list, indices, 1, true);
  }

  /**
   * moves the selected items down by 1.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public static int[] moveDown(List list, int[] indices) {
    return moveItems(list, indices, 1, false);
  }

  /**
   * moves the selected items to the top.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public static int[] moveTop(List list, int[] indices) {
    int		diff;

    if (canMoveUp(list, indices)) {
      diff    = indices[0];
      indices = moveItems(list, indices, diff, true);
    }

    return indices;
  }

  /**
   * moves the selected items to the end.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public static int[] moveBottom(List list, int[] indices) {
    int		diff;

    if (canMoveDown(list, indices)) {
      diff    = list.size() - 1 - indices[indices.length - 1];
      indices = moveItems(list, indices, diff, false);
    }

    return indices;
  }

  /**
   * checks whether the selected items can be moved up.
   *
   * @param indices	the indices of the rows to move
   * @return		true if the selected items can be moved
   */
  public static boolean canMoveUp(List list, int[] indices) {
    boolean	result;

    result = false;

    if (indices.length > 0) {
      if (indices[0] > 0)
        result = true;
    }

    return result;
  }

  /**
   * checks whether the selected items can be moved down.
   *
   * @param indices	the indices of the rows to move
   * @return		true if the selected items can be moved
   */
  public static boolean canMoveDown(List list, int[] indices) {
    boolean	result;

    result = false;

    if (indices.length > 0) {
      if (indices[indices.length - 1] < list.size() - 1)
        result = true;
    }

    return result;
  }
}
