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
 * MoveableTableModel.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import javax.swing.table.TableModel;

/**
 * Interface for table models that support moving rows up and down.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MoveableTableModel
  extends TableModel {

  /**
   * moves the selected items up by 1.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveUp(int[] indices);

  /**
   * moves the selected items down by 1.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveDown(int[] indices);

  /**
   * moves the selected items to the top.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveTop(int[] indices);

  /**
   * moves the selected items to the end.
   *
   * @param indices	the indices of the rows to move
   * @return		the updated indices of the selected rows
   */
  public int[] moveBottom(int[] indices);

  /**
   * checks whether the selected items can be moved up.
   *
   * @param indices	the indices of the rows to move
   * @return		true if the selected items can be moved
   */
  public boolean canMoveUp(int[] indices);

  /**
   * checks whether the selected items can be moved down.
   *
   * @param indices	the indices of the rows to move
   * @return		true if the selected items can be moved
   */
  public boolean canMoveDown(int[] indices);
}
