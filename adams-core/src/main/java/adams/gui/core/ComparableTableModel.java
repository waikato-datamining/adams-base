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
 * ComparableTableModel.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import javax.swing.table.TableModel;

/**
 * Interface for TableModel classes that need to use different values for
 * sorting. E.g., when the underlying object is different from the one being
 * displayed. This interface is used in the <code>SortedTableModel</code> class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see SortableAndSearchableWrapperTableModel
 */
public interface ComparableTableModel
  extends TableModel {

  /**
   * Returns the class type of the column that is used for comparisons.
   *
   * @param columnIndex	the column to get the class for
   * @return		the class for the column
   */
  public Class getComparisonColumnClass(int columnIndex);

  /**
   * Returns the field at the given position.
   *
   * @param row		the row
   * @param column	the column (ignored, since only 1 column)
   * @return		the field
   */
  public Object getComparisonValueAt(int row, int column);
}
