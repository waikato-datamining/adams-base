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
 * JTableSupporter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JTable;

/**
 * Interface for components that use tables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface JTableSupporter<T extends JTable> {

  /**
   * Returns the underlying table.
   *
   * @return		the table
   */
  public T getTable();
}
