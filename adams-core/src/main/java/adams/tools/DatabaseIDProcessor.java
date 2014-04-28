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
 * DatabaseIDProcessor.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

/**
 * For tools that can process custom database IDs as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DatabaseIDProcessor {

  /**
   * Sets the database IDs to process.
   *
   * @param value	the IDs
   */
  public void setDatabaseIDs(int[] value);

  /**
   * Returns the database IDs to process.
   *
   * @return		the IDs
   */
  public int[] getDatabaseIDs();

  /**
   * Checks whether custom database IDs have been set.
   *
   * @return		true if custom database IDs will be processed
   */
  public boolean hasDatabaseIDs();
}
