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
 * LogTMySQL.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.db.mysql;

import adams.db.AbstractDatabaseConnection;
import adams.db.LogT;

/**
 * MySQL implementation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LogTMySQL
  extends LogT {

  private static final long serialVersionUID = 6309576140124918549L;

  /**
   * The constructor.
   *
   * @param dbcon the database context this table is used in
   */
  public LogTMySQL(AbstractDatabaseConnection dbcon) {
    super(dbcon);
  }
}
