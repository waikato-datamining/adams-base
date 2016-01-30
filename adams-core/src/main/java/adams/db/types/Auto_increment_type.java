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
 * Auto_increment_type.java
 * Copyright (C) 2008-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.types;

import adams.db.AbstractDatabaseConnection;
import adams.db.JDBC;

import java.sql.Types;

/**
 * Autoincrement SQL type
 * 
 * @author dale
 * @version $Revision$
 */
public class Auto_increment_type extends SQL_type {

  /**
   * Constructor: it's a BIGINT
   *
   */
  public Auto_increment_type() {
    super(Types.BIGINT);
  }

  /**
   * Return creation String
   */
  public String getCreateType(AbstractDatabaseConnection conn) {
    String create=super.getCreateType(conn);
    if (JDBC.isMySQL(conn))
      return(create+" AUTO_INCREMENT");
    if (JDBC.isPostgreSQL(conn))
      return("BIGSERIAL");
    else
      return create;
  }

}
