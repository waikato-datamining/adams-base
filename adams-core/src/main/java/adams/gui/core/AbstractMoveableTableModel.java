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
 * AbstractMoveableTableModel.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;


/**
 * Abstract table model that allows rows to be moved around.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see MoveableTableModel
 */
public abstract class AbstractMoveableTableModel
  extends AbstractBaseTableModel
  implements MoveableTableModel {
  
  /** for serialization. */
  private static final long serialVersionUID = -6087402226242041322L;

  /**
   * Swaps the two rows.
   * 
   * @param firstIndex	the index of the first row
   * @param secondIndex	the index of the second row
   */
  protected abstract void swap(int firstIndex, int secondIndex);
  
  /**
   * moves the selected items by a certain amount of items in a given direction.
   *
   * @param indices	the indices to move
   * @param moveby      the number of items to move by
   * @param up	   	if true then items are moved up, otherwise down
   * @return		the updated selected indices
   */
  protected int[] moveItems(int[] indices, int moveby, boolean up) {
    int		i;
    int		newIndex;
    boolean	modified;
    
    modified = false;
    
    if (up) {
      for (i = 0; i < indices.length; i++) {
	if (indices[i] == 0)
	  continue;
	newIndex = indices[i] - moveby;
	swap(indices[i], newIndex);
	indices[i] = newIndex;
	modified   = true;
      }
    }
    else {
      for (i = indices.length - 1; i >= 0; i--) {
	if (indices[i] == getRowCount() - 1)
	  continue;
	newIndex = indices[i] + moveby;
	swap(indices[i], newIndex);
	indices[i] = newIndex;
	modified   = true;
      }
    }

    if (modified)
      fireTableDataChanged();
    
    return indices;
  }

  /**
   * moves the selected items up by 1.
   * 
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveUp(int[] indices) {
    return moveItems(indices, 1, true);
  }

  /**
   * moves the selected items down by 1.
   * 
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveDown(int[] indices) {
    return moveItems(indices, 1, false);
  }

  /**
   * moves the selected items to the top.
   * 
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveTop(int[] indices) {
    int		diff;

    if (canMoveUp(indices)) {
      diff    = indices[0];
      indices = moveItems(indices, diff, true);
    }
    
    return indices;
  }

  /**
   * moves the selected items to the end.
   * 
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveBottom(int[] indices) {
    int		diff;

    if (canMoveDown(indices)) {
      diff    = getRowCount() - 1 - indices[indices.length - 1];
      indices = moveItems(indices, diff, false);
    }
    
    return indices;
  }

  /**
   * checks whether the selected items can be moved up.
   * 
   * @param indices	the indices of the rows to move
   * @return		true if the selected items can be moved
   */
  public boolean canMoveUp(int[] indices) {
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
  public boolean canMoveDown(int[] indices) {
    boolean	result;

    result = false;
    
    if (indices.length > 0) {
      if (indices[indices.length - 1] < getRowCount() - 1)
        result = true;
    }

    return result;
  }
}
