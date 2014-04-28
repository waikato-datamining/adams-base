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
 * RenamingDataProvider.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

/**
 * Interface for data providers that allow the renaming of data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the new name
 */
public interface RenamingDataProvider<T>
  extends DatabaseConnectionProvider {

  /**
   * Renames the data container.
   *
   * @param dbid	the database ID of the data container
   * @param newName	the new name
   * @return		null if successfully renamed, otherwise the error message
   */
  public String rename(int dbid, T newName);
}
