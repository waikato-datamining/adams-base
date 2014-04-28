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
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.types;

import java.sql.Types;

/**
 * MySQL Autoincrement SQL type
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
    // TODO Auto-generated constructor stub
  }

  /**
   * Return creation String
   */
  public String getCreateType() {
    String create=super.getCreateType();
    return(create+" AUTO_INCREMENT");
  }

}
