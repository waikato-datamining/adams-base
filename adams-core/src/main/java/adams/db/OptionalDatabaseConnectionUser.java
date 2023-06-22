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
 * OptionalDatabaseConnectionUser.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.db;

/**
 * Interface for classes that make use of a database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface OptionalDatabaseConnectionUser
  extends DatabaseConnectionUser {

  /**
   * Sets whether to work in offline mode, i.e., not query the database.
   *
   * @param value	if true then offline mode
   */
  public void setOffline(boolean value);

  /**
   * Returns whether to work in offline mode, i.e., not query the database.
   *
   * @return		true if in offline mode
   */
  public boolean getOffline();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offlineTipText();
}
